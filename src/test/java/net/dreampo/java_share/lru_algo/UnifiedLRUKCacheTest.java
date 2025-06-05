package net.dreampo.java_share.lru_algo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * UnifiedLRUKCache的测试类
 * 验证修复后的统一LRU-K缓存的功能正确性
 */
public class UnifiedLRUKCacheTest {

    private UnifiedLRUKCache<String, Integer> cache;

    @BeforeEach
    void setUp() {
        cache = UnifiedLRUKCache.createDefault(3); // 容量为3的默认缓存（K=2）
    }

    @Test
    void testBasicOperations() {
        // 测试基本的put和get操作
        cache.put("key1", 1);
        cache.put("key2", 2);
        cache.put("key3", 3);

        assertEquals(Integer.valueOf(1), cache.get("key1"));
        assertEquals(Integer.valueOf(2), cache.get("key2"));
        assertEquals(Integer.valueOf(3), cache.get("key3"));
        assertEquals(3, cache.size());
    }

    @Test
    void testCapacityLimit() {
        // 测试容量限制和淘汰机制
        cache.put("key1", 1);
        cache.put("key2", 2);
        cache.put("key3", 3);
        
        // 添加第4个元素应该触发淘汰
        cache.put("key4", 4);
        
        assertEquals(3, cache.size());
        assertFalse(cache.containsKey("key1")); // key1应该被淘汰
        assertTrue(cache.containsKey("key4"));
    }

    @Test
    void testLRUKBehavior() {
        // 测试LRU-K行为（K=2）
        cache.put("key1", 1);
        cache.put("key2", 2);
        cache.put("key3", 3);

        // key1访问1次，应该在历史队列
        assertTrue(cache.isInHistoryQueue("key1"));
        assertEquals(1, cache.getAccessCount("key1"));

        // 再次访问key1，应该晋升到缓存队列
        cache.get("key1");
        assertEquals(2, cache.getAccessCount("key1"));
        assertTrue(cache.isInCacheQueue("key1"));
    }

    @Test
    void testRemove() {
        cache.put("key1", 1);
        cache.put("key2", 2);

        Integer removed = cache.remove("key1");
        assertEquals(Integer.valueOf(1), removed);
        assertFalse(cache.containsKey("key1"));
        assertEquals(1, cache.size());
    }

    @Test
    void testClear() {
        cache.put("key1", 1);
        cache.put("key2", 2);
        cache.put("key3", 3);

        cache.clear();
        assertEquals(0, cache.size());
        assertTrue(cache.isEmpty());
    }

    @Test
    void testStatistics() {
        cache.put("key1", 1);
        cache.put("key2", 2);
        
        cache.get("key1"); // 命中
        cache.get("key1"); // 命中
        cache.get("key3"); // 未命中

        assertTrue(cache.getHitRate() > 0);
        assertTrue(cache.getUtilization() > 0);
        
        String stats = cache.getStatistics();
        assertNotNull(stats);
        assertTrue(stats.contains("缓存统计信息"));
    }

    @Test
    void testFactoryMethods() {
        // 测试不同的工厂方法
        UnifiedLRUKCache<String, String> defaultCache = UnifiedLRUKCache.createDefault(10);
        assertNotNull(defaultCache);
        assertEquals(10, defaultCache.capacity());

        UnifiedLRUKCache<String, String> adaptiveCache = UnifiedLRUKCache.createAdaptive(10);
        assertNotNull(adaptiveCache);

        UnifiedLRUKCache<String, String> smartCache = UnifiedLRUKCache.createSmart(10);
        assertNotNull(smartCache);

        UnifiedLRUKCache<String, String> timeBasedCache = UnifiedLRUKCache.createTimeBased(10);
        assertNotNull(timeBasedCache);
    }

    @Test
    void testCustomKValueStrategy() {
        // 测试自定义K值策略
        UnifiedLRUKCache<String, String> customCache = UnifiedLRUKCache.create(5, context -> {
            // 根据键的长度决定K值
            String key = context.getKey();
            return key != null && key.length() > 3 ? 3 : 2;
        });

        customCache.put("abc", "short");
        customCache.put("abcde", "long");

        // 短键的K值应该是2
        assertEquals(2, customCache.getCurrentK("abc"));
        
        // 长键的K值应该是3
        assertEquals(3, customCache.getCurrentK("abcde"));
    }

    @Test
    void testErrorHandling() {
        // 测试错误处理
        assertThrows(IllegalArgumentException.class, () -> {
            new UnifiedLRUKCache<String, String>(0); // 容量不能为0
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new UnifiedLRUKCache<String, String>(10, null); // K函数不能为null
        });

        assertThrows(IllegalArgumentException.class, () -> {
            cache.put(null, 1); // 键不能为null
        });
    }

    @Test
    void testQueueTransition() {
        // 测试队列之间的数据迁移
        cache.put("key1", 1);
        
        // 初始状态：在历史队列
        assertTrue(cache.isInHistoryQueue("key1"));
        assertFalse(cache.isInCacheQueue("key1"));
        assertEquals(1, cache.historyQueueSize());
        assertEquals(0, cache.cacheQueueSize());

        // 第二次访问：应该移动到缓存队列
        cache.get("key1");
        
        assertFalse(cache.isInHistoryQueue("key1"));
        assertTrue(cache.isInCacheQueue("key1"));
        assertEquals(0, cache.historyQueueSize());
        assertEquals(1, cache.cacheQueueSize());
    }
}
