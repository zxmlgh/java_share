package net.dreampo.java_share.lru_algo;

import net.dreampo.java_share.structure_algo.DoublyLinkedList;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 统一的LRU-K缓存实现
 * <p>
 * 这是一个完全重新设计和实现的LRU-K缓存，融合了固定K值和动态K值的所有优势。
 * 专为业务场景设计，提供灵活的配置选项和强大的监控能力。
 * <p>
 * <b>核心特性：</b>
 * <ul>
 *   <li>支持固定K值和动态K值计算策略</li>
 *   <li>提供多种预定义的K值策略，适应不同业务场景</li>
 *   <li>完整的缓存状态监控和调试信息</li>
 *   <li>优雅的函数式编程API</li>
 *   <li>高性能的双队列LRU-K算法实现</li>
 *   <li>线程安全的访问计数器</li>
 * </ul>
 * <p>
 * <b>业务使用场景：</b>
 * <ul>
 *   <li><b>Web应用缓存：</b>页面数据、用户session、API响应缓存</li>
 *   <li><b>数据库查询缓存：</b>根据查询复杂度动态调整K值</li>
 *   <li><b>图片/视频缓存：</b>根据文件大小和访问模式优化缓存策略</li>
 *   <li><b>配置数据缓存：</b>系统配置、元数据等低频但重要的数据</li>
 *   <li><b>实时计算结果缓存：</b>计算密集型操作的结果缓存</li>
 * </ul>
 * <p>
 * <b>使用示例：</b>
 * <pre>{@code
 * // 创建默认缓存 (K=2，适合大多数Web应用)
 * UnifiedLRUKCache<String, Object> webCache = UnifiedLRUKCache.createDefault(1000);
 * 
 * // 创建智能自适应缓存（根据系统负载和访问模式自动调整）
 * UnifiedLRUKCache<String, Object> smartCache = UnifiedLRUKCache.createSmart(500);
 * 
 * // 创建基于业务时间的缓存（工作时间vs非工作时间不同策略）
 * UnifiedLRUKCache<String, Object> timeBasedCache = UnifiedLRUKCache.createTimeBased(800);
 * 
 * // 自定义K值策略（例如：根据数据重要性动态调整）
 * UnifiedLRUKCache<String, Data> customCache = UnifiedLRUKCache.create(1000, context -> {
 *     Data data = context.getValue();
 *     if (data != null && data.isImportant()) return 5; // 重要数据需要更多保护
 *     if (context.getCurrentAccessCount() > 10) return 4; // 热点数据
 *     return 2; // 普通数据
 * });
 * }</pre>
 *
 * @param <K> 缓存键的类型
 * @param <V> 缓存值的类型
 * @version 1.0
 * @since 2025-06-05
 */
public class UnifiedLRUKCache<K, V> {

    // ==================== 核心字段 ====================
    
    /** 主缓存：键到缓存项的映射 */
    private final HashMap<K, CacheEntry<K, V>> cache;
    
    /** 历史队列：访问次数少于K次的数据项，按访问时间排序 */
    private final DoublyLinkedList<CacheEntry<K, V>> historyQueue;
    
    /** 缓存队列：访问次数达到K次的数据项，按LRU策略排序 */
    private final DoublyLinkedList<CacheEntry<K, V>> cacheQueue;
    
    /** 缓存总容量 */
    private final int capacity;
    
    /** K值计算函数 */
    private final KValueFunction<K, V> kFunction;
    
    /** 是否在晋升时严格执行容量检查 */
    private final boolean strictCapacityOnPromotion;
    
    /** 统计信息：总访问次数 */
    private final AtomicLong totalAccesses = new AtomicLong(0);
    
    /** 统计信息：缓存命中次数 */
    private final AtomicLong hits = new AtomicLong(0);
    
    /** 统计信息：缓存未命中次数 */
    private final AtomicLong misses = new AtomicLong(0);

    // ==================== 构造函数 ====================

    /**
     * 默认构造函数：使用K=2的固定策略
     * <p>
     * <b>推荐场景：</b>通用Web应用、API缓存、简单的数据缓存
     * 
     * @param capacity 缓存容量，必须大于0
     */
    public UnifiedLRUKCache(int capacity) {
        this(capacity, KValueStrategies.<K,V>defaultStrategy());
    }

    /**
     * 函数式构造函数：使用自定义K值计算策略
     * <p>
     * <b>推荐场景：</b>有特殊业务逻辑的系统、需要动态调整缓存策略的应用
     * 
     * @param capacity  缓存容量，必须大于0
     * @param kFunction K值计算函数，不能为null
     */
    public UnifiedLRUKCache(int capacity, KValueFunction<K, V> kFunction) {
        this(capacity, kFunction, false);
    }

    /**
     * 完整构造函数：支持所有配置选项
     * 
     * @param capacity                   缓存容量，必须大于0
     * @param kFunction                  K值计算函数，不能为null
     * @param strictCapacityOnPromotion  是否在晋升时严格执行容量检查
     */
    public UnifiedLRUKCache(int capacity, KValueFunction<K, V> kFunction, 
                           boolean strictCapacityOnPromotion) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("缓存容量必须大于0，当前值: " + capacity);
        }
        if (kFunction == null) {
            throw new IllegalArgumentException("K值计算函数不能为null");
        }

        this.capacity = capacity;
        this.kFunction = kFunction;
        this.strictCapacityOnPromotion = strictCapacityOnPromotion;
        this.cache = new HashMap<>(capacity * 4 / 3 + 1); // 优化HashMap初始容量
        this.historyQueue = new DoublyLinkedList<>();
        this.cacheQueue = new DoublyLinkedList<>();
    }

    /**
     * 兼容性构造函数：支持固定K值
     * <p>
     * <b>推荐场景：</b>从其他LRU实现迁移、简单固定策略需求
     * 
     * @param capacity 缓存容量
     * @param fixedK   固定的K值
     */
    public UnifiedLRUKCache(int capacity, int fixedK) {
        this(capacity, KValueStrategies.<K,V>fixed(fixedK));
    }

    // ==================== 工厂方法：便捷创建常用配置 ====================

    /**
     * 创建默认缓存 (K=2)
     * <p>
     * <b>适用场景：</b>通用Web应用、API响应缓存、数据库查询缓存
     * 
     * @param capacity 缓存容量
     * @param <K>      缓存键的类型
     * @param <V>      缓存值的类型
     * @return 默认配置的缓存实例
     */
    public static <K, V> UnifiedLRUKCache<K, V> createDefault(int capacity) {
        KValueFunction<K, V> strategy = KValueStrategies.<K,V>defaultStrategy();
        return new UnifiedLRUKCache<K, V>(capacity, strategy);
    }

    /**
     * 创建自适应缓存：根据访问频率动态调整K值
     * <p>
     * <b>适用场景：</b>有明显热点数据的系统、用户行为分析、推荐系统
     * 
     * @param capacity 缓存容量
     * @param <K>      缓存键的类型
     * @param <V>      缓存值的类型
     * @return 自适应缓存实例
     */
    public static <K, V> UnifiedLRUKCache<K, V> createAdaptive(int capacity) {
        KValueFunction<K, V> strategy = KValueStrategies.<K,V>adaptive();
        return new UnifiedLRUKCache<K, V>(capacity, strategy);
    }

    /**
     * 创建智能缓存：综合考虑利用率和访问模式
     * <p>
     * <b>适用场景：</b>复杂生产环境、资源敏感的系统、需要均衡性能的应用
     * 
     * @param capacity 缓存容量
     * @param <K>      缓存键的类型
     * @param <V>      缓存值的类型
     * @return 智能缓存实例
     */
    public static <K, V> UnifiedLRUKCache<K, V> createSmart(int capacity) {
        KValueFunction<K, V> strategy = KValueStrategies.<K,V>smart();
        return new UnifiedLRUKCache<K, V>(capacity, strategy);
    }

    /**
     * 创建基于时间的缓存：工作时间和非工作时间不同策略
     * <p>
     * <b>适用场景：</b>企业应用、办公系统、有明显时间访问规律的业务
     * 
     * @param capacity 缓存容量
     * @param <K>      缓存键的类型
     * @param <V>      缓存值的类型
     * @return 时间相关的缓存实例
     */
    public static <K, V> UnifiedLRUKCache<K, V> createTimeBased(int capacity) {
        KValueFunction<K, V> strategy = KValueStrategies.<K,V>conditional(
            context -> {
                int hour = java.time.LocalTime.now().getHour();
                return hour >= 9 && hour <= 18; // 工作时间
            },
            KValueStrategies.<K,V>fixed(4), // 工作时间：严格保护
            KValueStrategies.<K,V>fixed(2)  // 非工作时间：温和策略
        );
        return new UnifiedLRUKCache<K, V>(capacity, strategy);
    }

    /**
     * 创建自定义缓存：使用用户提供的策略函数
     * <p>
     * <b>适用场景：</b>有特殊业务逻辑的系统、需要精细控制的缓存策略
     * 
     * @param capacity       缓存容量
     * @param customStrategy 自定义K值计算策略
     * @param <K>            缓存键的类型
     * @param <V>            缓存值的类型
     * @return 自定义配置的缓存实例
     */
    public static <K, V> UnifiedLRUKCache<K, V> create(int capacity, 
                                                       KValueFunction<K, V> customStrategy) {
        return new UnifiedLRUKCache<K, V>(capacity, customStrategy);
    }

    // ==================== 核心缓存操作方法 ====================

    /**
     * 获取缓存值
     * <p>
     * 每次访问都会更新访问统计信息，可能触发队列间的数据迁移。
     * 当访问次数达到K值时，数据会从历史队列晋升到缓存队列。
     * <p>
     * <b>性能特点：</b>O(1)时间复杂度，线程安全的统计更新
     * 
     * @param key 要查找的键
     * @return 对应的值，如果不存在则返回null
     */
    public V get(K key) {
        totalAccesses.incrementAndGet();
        
        CacheEntry<K, V> entry = cache.get(key);
        if (entry == null) {
            misses.incrementAndGet();
            return null;
        }

        hits.incrementAndGet();
        updateAccessInfo(entry);
        return entry.value;
    }

    /**
     * 设置缓存值
     * <p>
     * 如果键已存在则更新值和访问信息；如果是新键则创建新的缓存项。
     * 当缓存容量超限时，会按照LRU-K策略自动淘汰数据。
     * <p>
     * <b>淘汰优先级：</b>历史队列尾部 > 缓存队列尾部
     * <p>
     * <b>性能特点：</b>O(1)时间复杂度，可能触发容量检查和数据淘汰
     * 
     * @param key   键，不能为null
     * @param value 值，可以为null
     */
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("缓存键不能为null");
        }
        
        CacheEntry<K, V> entry = cache.get(key);

        if (entry != null) {
            // 更新已存在的缓存项
            entry.value = value;
            entry.updateAccess(kFunction, cache.size(), capacity);

            if (entry.isInHistoryQueue()) {
                // 在历史队列中，更新位置
                historyQueue.moveToHead(entry.historyNode);
                
                // 检查是否达到晋升条件
                if (entry.isEligibleForCache()) {
                    promoteToCache(entry);
                }
            } else if (entry.isInCacheQueue()) {
                // 在缓存队列中，更新LRU位置
                cacheQueue.moveToHead(entry.cacheNode);
            }
        } else {
            // 添加新的缓存项
            if (size() >= capacity) {
                evictItem();
            }

            entry = new CacheEntry<>(key, value, kFunction, cache.size(), capacity);
            cache.put(key, entry);
            // 新创建的entry在构造函数中accessCount已经是0，现在更新为1
            entry.updateAccess(kFunction, cache.size(), capacity);

            if (entry.isEligibleForCache()) {
                // 直接进入缓存队列（例如K=1的情况）
                entry.cacheNode = cacheQueue.createNode(entry);
                cacheQueue.addToHead(entry.cacheNode);
            } else {
                // 进入历史队列
                entry.historyNode = historyQueue.createNode(entry);
                historyQueue.addToHead(entry.historyNode);
            }
        }
    }

    /**
     * 删除指定键的缓存项
     * <p>
     * 完全移除缓存项，包括从相应队列和主缓存中删除。
     * 
     * @param key 要删除的键
     * @return 被删除的值，如果键不存在则返回null
     */
    public V remove(K key) {
        CacheEntry<K, V> entry = cache.get(key);
        if (entry == null) {
            return null;
        }

        V value = entry.value;
        removeEntry(entry);
        return value;
    }

    /**
     * 检查是否包含指定键
     * <p>
     * <b>注意：</b>此方法不会更新访问统计信息
     * 
     * @param key 要检查的键
     * @return 如果包含则返回true
     */
    public boolean containsKey(K key) {
        return cache.containsKey(key);
    }

    /**
     * 获取当前缓存大小
     * 
     * @return 缓存中的项目总数量
     */
    public int size() {
        return cache.size();
    }

    /**
     * 获取缓存容量
     * 
     * @return 缓存的最大容量
     */
    public int capacity() {
        return capacity;
    }

    /**
     * 判断缓存是否为空
     * 
     * @return 如果缓存为空则返回true
     */
    public boolean isEmpty() {
        return cache.isEmpty();
    }

    /**
     * 清空缓存
     * <p>
     * 清除所有缓存项和统计信息，重置缓存到初始状态。
     */
    public void clear() {
        cache.clear();
        historyQueue.clear();
        cacheQueue.clear();
        // 注意：不重置统计计数器，保留历史统计信息
    }

    // ==================== 监控和调试方法 ====================

    /**
     * 获取指定键当前的K值
     * <p>
     * 用于调试和监控，了解当前缓存策略对特定数据的处理。
     * 
     * @param key 要查询的键
     * @return 当前K值，如果键不存在则返回根据当前上下文计算的K值
     */
    public int getCurrentK(K key) {
        CacheEntry<K, V> entry = cache.get(key);
        return entry != null ? entry.currentK : calculateK(key, null, 0);
    }

    /**
     * 获取指定键的访问次数
     * 
     * @param key 要查询的键
     * @return 访问次数，如果键不存在则返回-1
     */
    public int getAccessCount(K key) {
        CacheEntry<K, V> entry = cache.get(key);
        return entry != null ? entry.accessCount : -1;
    }

    /**
     * 检查指定键是否在缓存队列中（高优先级队列）
     * 
     * @param key 要检查的键
     * @return 如果在缓存队列中则返回true
     */
    public boolean isInCacheQueue(K key) {
        CacheEntry<K, V> entry = cache.get(key);
        return entry != null && entry.isInCacheQueue();
    }

    /**
     * 检查指定键是否在历史队列中（低优先级队列）
     * 
     * @param key 要检查的键
     * @return 如果在历史队列中则返回true
     */
    public boolean isInHistoryQueue(K key) {
        CacheEntry<K, V> entry = cache.get(key);
        return entry != null && entry.isInHistoryQueue();
    }

    /**
     * 获取历史队列大小
     * 
     * @return 历史队列中的项目数量
     */
    public int historyQueueSize() {
        return historyQueue.size();
    }

    /**
     * 获取缓存队列大小
     * 
     * @return 缓存队列中的项目数量
     */
    public int cacheQueueSize() {
        return cacheQueue.size();
    }

    /**
     * 获取缓存命中率
     * <p>
     * <b>计算公式：</b>命中次数 / 总访问次数
     * 
     * @return 命中率（0.0-1.0），如果没有访问记录则返回0.0
     */
    public double getHitRate() {
        long total = totalAccesses.get();
        return total > 0 ? (double) hits.get() / total : 0.0;
    }

    /**
     * 获取缓存利用率
     * 
     * @return 当前缓存利用率（0.0-1.0）
     */
    public double getUtilization() {
        return (double) size() / capacity;
    }

    /**
     * 获取详细的缓存统计信息
     * <p>
     * 包含命中率、利用率、队列分布等关键指标，用于性能监控和调优。
     * 
     * @return 格式化的统计信息字符串
     */
    public String getStatistics() {
        long total = totalAccesses.get();
        long hitCount = hits.get();
        long missCount = misses.get();
        
        return String.format(
            "缓存统计信息:\n" +
            "  容量: %d/%d (%.1f%%)\n" +
            "  访问统计: 总计=%d, 命中=%d, 未命中=%d\n" +
            "  命中率: %.2f%%\n" +
            "  队列分布: 历史队列=%d, 缓存队列=%d\n" +
            "  队列比例: 历史队列=%.1f%%, 缓存队列=%.1f%%",
            size(), capacity, getUtilization() * 100,
            total, hitCount, missCount,
            getHitRate() * 100,
            historyQueueSize(), cacheQueueSize(),
            size() > 0 ? (double) historyQueueSize() / size() * 100 : 0,
            size() > 0 ? (double) cacheQueueSize() / size() * 100 : 0
        );
    }


    // ==================== 内部辅助方法 ====================

    /**
     * 计算K值的核心方法
     * <p>
     * 构建访问上下文并调用K值计算函数，确保返回值在合理范围内。
     */
    private int calculateK(K key, V value, int currentAccessCount) {
        AccessContext<K, V> context = new AccessContext<>(
            key, value, currentAccessCount, System.currentTimeMillis(),
            cache.size(), capacity
        );
        
        int k = kFunction.apply(context);
        // 确保K值在合理范围内
        return Math.max(1, Math.min(k, 10));
    }

    /**
     * 更新访问信息的核心逻辑
     * <p>
     * 这是LRU-K算法的关键部分，处理访问计数更新、K值重新计算、队列迁移等。
     */
    private void updateAccessInfo(CacheEntry<K, V> entry) {
        entry.updateAccess(kFunction, cache.size(), capacity);

        if (entry.isInHistoryQueue()) {
            // 在历史队列中，更新位置
            historyQueue.moveToHead(entry.historyNode);
            
            // 检查是否达到晋升条件
            if (entry.isEligibleForCache()) {
                promoteToCache(entry);
            }
        } else if (entry.isInCacheQueue()) {
            // 在缓存队列中，更新LRU位置
            cacheQueue.moveToHead(entry.cacheNode);
        }
    }

    /**
     * 将数据项从历史队列晋升到缓存队列
     * <p>
     * 这是LRU-K算法的核心操作之一，当数据达到K次访问时触发。
     */
    private void promoteToCache(CacheEntry<K, V> entry) {
        // 如果启用严格容量检查，在晋升前确保有空间
        if (strictCapacityOnPromotion && size() >= capacity) {
            evictItem();
        }

        // 从历史队列移除
        if (entry.historyNode != null) {
            historyQueue.removeNode(entry.historyNode);
            entry.historyNode = null;
        }

        // 添加到缓存队列头部
        entry.cacheNode = cacheQueue.createNode(entry);
        cacheQueue.addToHead(entry.cacheNode);
    }

    /**
     * 按LRU-K策略淘汰数据项
     * <p>
     * <b>淘汰优先级：</b>
     * <ol>
     *   <li>历史队列尾部（访问次数少于K次的最久未访问项）</li>
     *   <li>缓存队列尾部（访问次数≥K次的最久未访问项）</li>
     * </ol>
     */
    private void evictItem() {
        CacheEntry<K, V> entryToEvict = null;

        // 优先从历史队列淘汰（这些是访问次数少于K的项目）
        if (!historyQueue.isEmpty()) {
            DoublyLinkedList<CacheEntry<K, V>>.Node tailNode = historyQueue.removeTail();
            if (tailNode != null) {
                entryToEvict = tailNode.getData();
                entryToEvict.historyNode = null;
            }
        }
        // 历史队列为空时，从缓存队列淘汰最久未使用的
        else if (!cacheQueue.isEmpty()) {
            DoublyLinkedList<CacheEntry<K, V>>.Node tailNode = cacheQueue.removeTail();
            if (tailNode != null) {
                entryToEvict = tailNode.getData();
                entryToEvict.cacheNode = null;
            }
        }

        // 从主缓存中移除
        if (entryToEvict != null) {
            cache.remove(entryToEvict.key);
        }
    }

    /**
     * 完全移除缓存项
     * <p>
     * 从相应队列和主缓存中彻底删除指定项。
     */
    private void removeEntry(CacheEntry<K, V> entry) {
        // 从相应队列中移除
        if (entry.isInHistoryQueue()) {
            historyQueue.removeNode(entry.historyNode);
            entry.historyNode = null;
        } else if (entry.isInCacheQueue()) {
            cacheQueue.removeNode(entry.cacheNode);
            entry.cacheNode = null;
        }

        // 从主缓存中移除
        cache.remove(entry.key);
    }

    @Override
    public String toString() {
        return String.format(
            "UnifiedLRUKCache{容量=%d, 当前大小=%d, 历史队列=%d, 缓存队列=%d, 命中率=%.2f%%}",
            capacity, size(), historyQueueSize(), cacheQueueSize(), getHitRate() * 100
        );
    }
}
