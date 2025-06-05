package net.dreampo.java_share.lru_algo;

import net.dreampo.java_share.structure_algo.DoublyLinkedList;

import java.util.HashMap;

/**
 * LRU-K缓存实现
 * <p>
 * LRU-K算法要求数据被访问K次后才有资格获得高优先级保护，避免"一次性访问的数据立即获得最高保留权"的问题。
 * 只有证明自己被频繁需要的数据才获得长期驻留权。
 * <p>
 * 核心机制：
 * 1. History Queue: 维护访问次数少于K次的数据项，按访问时间排序
 * 2. Cache Queue: 维护访问次数达到K次的数据项，按LRU策略排序
 * 3. 淘汰策略: 优先从History Queue淘汰，只有当History Queue为空时才从Cache Queue淘汰
 *
 * @param <K> 键类型
 * @param <V> 值类型
 */
public class LRUKCache<K, V> {

    private final HashMap<K, CacheEntry> cache;  // 键到缓存项的映射
    private final DoublyLinkedList<CacheEntry> historyQueue; // 历史队列：访问次数 < K 的数据
    private final DoublyLinkedList<CacheEntry> cacheQueue;   // 缓存队列：访问次数 >= K 的数据
    private final int capacity; // 总容量
    private final int k;  // K值：进入高优先级队列所需的最小访问次数
    private final boolean strictCapacityOnPromotion;  // 是否在晋升时严格执行容量检查

    /**
     * 缓存项，封装键值对及其访问信息
     */
    private class CacheEntry {
        K key;
        V value;
        int accessCount; // 访问次数
        DoublyLinkedList<CacheEntry>.Node historyNode; // 在历史队列中的节点引用
        DoublyLinkedList<CacheEntry>.Node cacheNode;  // 在缓存队列中的节点引用

        public CacheEntry(K key, V value) {
            this.key = key;
            this.value = value;
            this.accessCount = 0;
            this.historyNode = null;
            this.cacheNode = null;
        }

        /**
         * 检查此项是否有资格进入高优先级缓存队列
         */
        public boolean isEligibleForCache() {
            return accessCount >= k;
        }

        /**
         * 检查此项是否在历史队列中
         */
        public boolean isInHistoryQueue() {
            return historyNode != null;
        }

        /**
         * 检查此项是否在缓存队列中
         */
        public boolean isInCacheQueue() {
            return cacheNode != null;
        }
    }

    /**
     * 构造LRU-K缓存（默认不在晋升时执行容量检查）
     *
     * @param capacity 缓存总容量
     * @param k        进入高优先级保护所需的最小访问次数
     */
    public LRUKCache(int capacity, int k) {
        this(capacity, k, false);
    }

    /**
     * 构造LRU-K缓存
     *
     * @param capacity                   缓存总容量
     * @param k                          进入高优先级保护所需的最小访问次数
     * @param strictCapacityOnPromotion  是否在从历史队列晋升到缓存队列时严格执行容量检查
     */
    public LRUKCache(int capacity, int k, boolean strictCapacityOnPromotion) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("缓存容量必须大于0");
        }
        if (k <= 0) {
            throw new IllegalArgumentException("K值必须大于0");
        }

        this.capacity = capacity;
        this.k = k;
        this.strictCapacityOnPromotion = strictCapacityOnPromotion;
        this.cache = new HashMap<>();
        this.historyQueue = new DoublyLinkedList<>();
        this.cacheQueue = new DoublyLinkedList<>();
    }

    /**
     * 获取缓存值
     * 每次访问都会增加访问计数，当达到K次时会提升到高优先级队列
     *
     * @param key 键
     * @return 对应的值，如果不存在则返回null
     */
    public V get(K key) {
        CacheEntry entry = cache.get(key);
        if (entry == null) {
            return null;
        }

        // 更新访问信息
        updateAccessInfo(entry);
        return entry.value;
    }

    /**
     * 设置缓存值
     * 如果键已存在则更新值，否则添加新项
     * 超出容量时按LRU-K策略淘汰数据
     *
     * @param key   键
     * @param value 值
     */
    public void put(K key, V value) {
        CacheEntry entry = cache.get(key);

        if (entry != null) {
            // 更新已存在的项
            entry.value = value;
            // 手动实现访问逻辑，避免double counting
            entry.accessCount++;

            if (entry.isInHistoryQueue()) {
                // 在历史队列中，更新位置
                historyQueue.moveToHead(entry.historyNode);

                // 检查是否达到K次访问，需要提升到缓存队列
                if (entry.isEligibleForCache()) {
                    promoteToCache(entry);
                }
            } else if (entry.isInCacheQueue()) {
                // 在缓存队列中，更新LRU位置
                cacheQueue.moveToHead(entry.cacheNode);
            }
        } else {
            // 添加新项前检查是否需要淘汰
            if (size() >= capacity) {
                /**
                 * 最大超量：理论上可达到 capacity * (K-1)/K + capacity = capacity * (2K-1)/K
                 * K=2时最多1.5倍capacity，K=5时最多1.8倍capacity
                 */
                evictItem();
            }

            // 创建新项
            entry = new CacheEntry(key, value);
            cache.put(key, entry);

            // 新项访问计数设为1并进入相应队列
            entry.accessCount = 1;

            if (entry.isEligibleForCache()) {
                // K=1时直接进入缓存队列
                entry.cacheNode = cacheQueue.createNode(entry);
                cacheQueue.addToHead(entry.cacheNode);
            } else {
                // 否则进入历史队列
                entry.historyNode = historyQueue.createNode(entry);
                historyQueue.addToHead(entry.historyNode);
            }
        }
    }

    /**
     * 删除指定键的缓存项
     *
     * @param key 要删除的键
     * @return 被删除的值，如果键不存在则返回null
     */
    public V remove(K key) {
        CacheEntry entry = cache.get(key);
        if (entry == null) {
            return null;
        }

        V value = entry.value;
        removeEntry(entry);
        return value;
    }

    /**
     * 检查是否包含指定键
     *
     * @param key 键
     * @return 如果包含则返回true
     */
    public boolean containsKey(K key) {
        return cache.containsKey(key);
    }

    /**
     * 获取当前缓存大小
     *
     * @return 缓存中的项目数量
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
     * 获取K值
     *
     * @return 进入高优先级保护所需的最小访问次数
     */
    public int getK() {
        return k;
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
     * 清空缓存
     */
    public void clear() {
        cache.clear();
        historyQueue.clear();
        cacheQueue.clear();
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
     * 更新访问信息
     * 这是LRU-K算法的核心逻辑，处理访问计数和队列迁移
     */
    private void updateAccessInfo(CacheEntry entry) {
        entry.accessCount++;

        if (entry.isInHistoryQueue()) {
            // 在历史队列中，更新位置
            historyQueue.moveToHead(entry.historyNode);

            // 检查是否达到K次访问，需要提升到缓存队列
            if (entry.isEligibleForCache()) {
                promoteToCache(entry);
            }
        } else if (entry.isInCacheQueue()) {
            // 在缓存队列中，更新LRU位置
            cacheQueue.moveToHead(entry.cacheNode);
        }
    }

    /**
     * 将项目从历史队列提升到缓存队列
     */
    private void promoteToCache(CacheEntry entry) {
        // 如果启用了严格容量检查，在晋升前确保有空间
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
     * 优先级：历史队列尾部 > 缓存队列尾部
     */
    private void evictItem() {
        CacheEntry entryToEvict = null;

        // 优先从历史队列淘汰（这些是访问次数少于K的项目）
        if (!historyQueue.isEmpty()) {
            DoublyLinkedList<CacheEntry>.Node tailNode = historyQueue.removeTail();
            if (tailNode != null) {
                entryToEvict = tailNode.getData();
                entryToEvict.historyNode = null;
            }
        }
        // 历史队列为空时，从缓存队列淘汰最久未使用的
        else if (!cacheQueue.isEmpty()) {
            DoublyLinkedList<CacheEntry>.Node tailNode = cacheQueue.removeTail();
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
     * 完全移除一个缓存项
     */
    private void removeEntry(CacheEntry entry) {
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

    /**
     * 获取指定键的访问次数（用于调试和测试）
     *
     * @param key 键
     * @return 访问次数，如果键不存在则返回-1
     */
    public int getAccessCount(K key) {
        CacheEntry entry = cache.get(key);
        return entry != null ? entry.accessCount : -1;
    }

    /**
     * 检查指定键是否在缓存队列中（用于调试和测试）
     *
     * @param key 键
     * @return 如果在缓存队列中则返回true
     */
    public boolean isInCacheQueue(K key) {
        CacheEntry entry = cache.get(key);
        return entry != null && entry.isInCacheQueue();
    }

    /**
     * 检查指定键是否在历史队列中（用于调试和测试）
     *
     * @param key 键
     * @return 如果在历史队列中则返回true
     */
    public boolean isInHistoryQueue(K key) {
        CacheEntry entry = cache.get(key);
        return entry != null && entry.isInHistoryQueue();
    }

    @Override
    public String toString() {
        return String.format("LRUKCache{capacity=%d, k=%d, size=%d, historyQueue=%d, cacheQueue=%d}",
                capacity, k, size(), historyQueueSize(), cacheQueueSize());
    }
}
