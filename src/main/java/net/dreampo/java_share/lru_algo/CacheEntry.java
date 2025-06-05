package net.dreampo.java_share.lru_algo;

import net.dreampo.java_share.structure_algo.DoublyLinkedList;

/**
 * 缓存项内部类
 * <p>
 * 封装了每个缓存项的完整信息，包括键值对、访问统计、队列位置等。
 * 这个类原本是UnifiedLRUKCache的私有内部类，现在被重构为包私有类。
 *
 * @param <K> 缓存键的类型
 * @param <V> 缓存值的类型
 * @author Claude
 * @version 1.0
 * @since 2025-06-05
 */
class CacheEntry<K, V> {
    K key;                                                    // 缓存键
    V value;                                                 // 缓存值
    int accessCount;                                         // 访问次数
    int currentK;                                           // 当前使用的K值
    long lastAccessTime;                                    // 最后访问时间
    DoublyLinkedList<CacheEntry<K, V>>.Node historyNode;   // 在历史队列中的节点引用
    DoublyLinkedList<CacheEntry<K, V>>.Node cacheNode;     // 在缓存队列中的节点引用

    /**
     * 创建缓存项
     *
     * @param key              缓存键
     * @param value            缓存值
     * @param kValueFunction   K值计算函数
     * @param cacheSize        当前缓存大小
     * @param capacity         缓存容量
     */
    public CacheEntry(K key, V value, KValueFunction<K, V> kValueFunction, 
                     int cacheSize, int capacity) {
        this.key = key;
        this.value = value;
        this.accessCount = 0;
        this.currentK = calculateK(key, value, 0, kValueFunction, cacheSize, capacity);
        this.lastAccessTime = System.currentTimeMillis();
        this.historyNode = null;
        this.cacheNode = null;
    }

    /**
     * 检查此项是否有资格进入高优先级缓存队列
     *
     * @return 如果访问次数达到K值则返回true
     */
    public boolean isEligibleForCache() {
        return accessCount >= currentK;
    }

    /**
     * 检查此项是否在历史队列中
     *
     * @return 如果在历史队列中则返回true
     */
    public boolean isInHistoryQueue() { 
        return historyNode != null; 
    }

    /**
     * 检查此项是否在缓存队列中
     *
     * @return 如果在缓存队列中则返回true
     */
    public boolean isInCacheQueue() { 
        return cacheNode != null; 
    }

    /**
     * 更新访问信息
     *
     * @param kValueFunction K值计算函数
     * @param cacheSize      当前缓存大小
     * @param capacity       缓存容量
     */
    public void updateAccess(KValueFunction<K, V> kValueFunction, int cacheSize, int capacity) {
        this.accessCount++;
        this.currentK = calculateK(key, value, accessCount, kValueFunction, cacheSize, capacity);
        this.lastAccessTime = System.currentTimeMillis();
    }

    /**
     * 计算K值的辅助方法
     */
    private int calculateK(K key, V value, int currentAccessCount, 
                          KValueFunction<K, V> kFunction, int cacheSize, int capacity) {
        AccessContext<K, V> context = new AccessContext<>(
            key, value, currentAccessCount, System.currentTimeMillis(),
            cacheSize, capacity
        );
        
        int k = kFunction.apply(context);
        // 确保K值在合理范围内
        return Math.max(1, Math.min(k, 10));
    }
}
