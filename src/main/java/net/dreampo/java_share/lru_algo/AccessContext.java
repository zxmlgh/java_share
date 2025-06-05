package net.dreampo.java_share.lru_algo;

/**
 * 访问上下文 - 提供K值计算所需的全部信息
 * <p>
 * 这个类封装了计算K值时可能需要的所有信息，
 * 包括当前访问的键值对、历史访问情况、缓存状态等。
 * 业务方可以根据这些信息制定个性化的缓存策略。
 *
 * @param <K> 缓存键的类型
 * @param <V> 缓存值的类型
 * @author Claude
 * @version 1.0
 * @since 2025-06-05
 */
public class AccessContext<K, V> {
    private final K key;                    // 当前访问的键
    private final V value;                  // 当前访问的值
    private final int currentAccessCount;   // 当前访问次数
    private final long timestamp;           // 当前时间戳
    private final int cacheSize;           // 当前缓存大小
    private final int capacity;            // 缓存容量
    private final double cacheUtilization; // 缓存利用率

    /**
     * 构造访问上下文
     *
     * @param key                当前访问的键
     * @param value              当前访问的值
     * @param currentAccessCount 当前访问次数
     * @param timestamp          当前时间戳
     * @param cacheSize          当前缓存大小
     * @param capacity           缓存容量
     */
    public AccessContext(K key, V value, int currentAccessCount, long timestamp,
                         int cacheSize, int capacity) {
        this.key = key;
        this.value = value;
        this.currentAccessCount = currentAccessCount;
        this.timestamp = timestamp;
        this.cacheSize = cacheSize;
        this.capacity = capacity;
        this.cacheUtilization = capacity > 0 ? (double) cacheSize / capacity : 0;
    }

    /**
     * 获取当前访问的键
     *
     * @return 当前访问的键
     */
    public K getKey() { 
        return key; 
    }
    
    /**
     * 获取当前访问的值
     *
     * @return 当前访问的值
     */
    public V getValue() { 
        return value; 
    }
    
    /**
     * 获取当前的访问次数
     *
     * @return 当前的访问次数
     */
    public int getCurrentAccessCount() { 
        return currentAccessCount; 
    }
    
    /**
     * 获取当前时间戳
     *
     * @return 当前时间戳
     */
    public long getTimestamp() { 
        return timestamp; 
    }
    
    /**
     * 获取当前缓存中的元素数量
     *
     * @return 当前缓存中的元素数量
     */
    public int getCacheSize() { 
        return cacheSize; 
    }
    
    /**
     * 获取缓存的最大容量
     *
     * @return 缓存的最大容量
     */
    public int getCapacity() { 
        return capacity; 
    }
    
    /**
     * 获取缓存利用率（0.0-1.0）
     *
     * @return 缓存利用率
     */
    public double getCacheUtilization() { 
        return cacheUtilization; 
    }
    
    /**
     * 判断当前是否是缓存容量紧张时期
     *
     * @return 利用率超过80%时返回true
     */
    public boolean isCapacityTight() { 
        return cacheUtilization > 0.8; 
    }
    
    /**
     * 判断当前数据是否为热点数据
     *
     * @return 访问次数超过5次时返回true
     */
    public boolean isHotData() { 
        return currentAccessCount > 5; 
    }
}
