package net.dreampo.java_share.lru_algo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

/**
 * LRU-K缓存测试类
 * 验证LRU-K算法的核心特性：数据需要被访问K次后才获得高优先级保护
 */
public class LRUKCacheTest {

    private LRUKCache<String, Integer> cache;

    @BeforeEach
    void setUp() {
        // 创建容量为3，K=2的LRU-K缓存
        cache = new LRUKCache<>(3, 2);
    }

    @Test
    @DisplayName("基础操作测试：put和get")
    void testBasicOperations() {
        // 测试put和get基础功能
        cache.put("key1", 1);
        cache.put("key2", 2);
        
        assertThat(cache.get("key1")).isEqualTo(1);
        assertThat(cache.get("key2")).isEqualTo(2);
        assertThat(cache.get("nonexistent")).isNull();
        
        assertThat(cache.size()).isEqualTo(2);
        assertThat(cache.containsKey("key1")).isTrue();
        assertThat(cache.containsKey("nonexistent")).isFalse();
    }

    @Test
    @DisplayName("访问计数功能测试")
    void testAccessCounting() {
        cache.put("key1", 1);
        
        // 初次put后访问计数应为1
        assertThat(cache.getAccessCount("key1")).isEqualTo(1);
        assertThat(cache.isInHistoryQueue("key1")).isTrue();
        assertThat(cache.isInCacheQueue("key1")).isFalse();
        
        // 第二次访问
        cache.get("key1");
        assertThat(cache.getAccessCount("key1")).isEqualTo(2);
        assertThat(cache.isInHistoryQueue("key1")).isFalse();
        assertThat(cache.isInCacheQueue("key1")).isTrue();
        
        // 第三次访问
        cache.get("key1");
        assertThat(cache.getAccessCount("key1")).isEqualTo(3);
        assertThat(cache.isInCacheQueue("key1")).isTrue();
    }

    @Test
    @DisplayName("LRU-K核心特性：优先淘汰未达到K次访问的数据")
    void testLRUKEvictionPriority() {
        // 添加3个项目，达到容量上限
        cache.put("A", 1);  // 访问1次，在历史队列
        cache.put("B", 2);  // 访问1次，在历史队列
        cache.put("C", 3);  // 访问1次，在历史队列
        
        // 验证都在历史队列中
        assertThat(cache.isInHistoryQueue("A")).isTrue();
        assertThat(cache.isInHistoryQueue("B")).isTrue();
        assertThat(cache.isInHistoryQueue("C")).isTrue();
        
        // 让B达到K次访问，提升到缓存队列
        cache.get("B"); // B现在访问了2次，进入缓存队列
        assertThat(cache.isInCacheQueue("B")).isTrue();
        assertThat(cache.isInHistoryQueue("B")).isFalse();
        
        // 添加第4个项目，应该淘汰历史队列中最久未访问的A
        cache.put("D", 4);
        
        assertThat(cache.containsKey("A")).isFalse(); // A被淘汰（在历史队列中最久未访问）
        assertThat(cache.containsKey("B")).isTrue();  // B保留（在缓存队列中，有高优先级保护）
        assertThat(cache.containsKey("C")).isTrue();  // C保留
        assertThat(cache.containsKey("D")).isTrue();  // D新添加
    }

    @Test
    @DisplayName("历史队列为空时从缓存队列淘汰")
    void testEvictionWhenHistoryQueueEmpty() {
        // 添加3个项目并都让它们达到K次访问
        cache.put("A", 1);
        cache.get("A"); // A: 2次访问，进入缓存队列
        
        cache.put("B", 2);
        cache.get("B"); // B: 2次访问，进入缓存队列
        
        cache.put("C", 3);
        cache.get("C"); // C: 2次访问，进入缓存队列
        
        // 验证历史队列为空，缓存队列满
        assertThat(cache.historyQueueSize()).isEqualTo(0);
        assertThat(cache.cacheQueueSize()).isEqualTo(3);
        
        // 再次访问B，使其成为最近使用的
        cache.get("B");
        
        // 添加新项目，应该从缓存队列淘汰最久未使用的A
        cache.put("D", 4);
        
        assertThat(cache.containsKey("A")).isFalse(); // A被淘汰
        assertThat(cache.containsKey("B")).isTrue();  // B保留（最近访问）
        assertThat(cache.containsKey("C")).isTrue();  // C保留
        assertThat(cache.containsKey("D")).isTrue();  // D新添加
    }

    @Test
    @DisplayName("与传统LRU对比：避免一次性访问获得最高保留权")
    void testComparisonWithTraditionalLRU() {
        // 设置场景：有一些经常访问的热数据
        cache.put("hot1", 1);
        cache.get("hot1"); // hot1达到K次，进入缓存队列
        
        cache.put("hot2", 2);
        cache.get("hot2"); // hot2达到K次，进入缓存队列
        
        // 现在来了一个一次性访问的数据
        cache.put("oneTime", 999);
        
        // 验证一次性数据在历史队列中，没有获得高优先级
        assertThat(cache.isInHistoryQueue("oneTime")).isTrue();
        assertThat(cache.isInCacheQueue("oneTime")).isFalse();
        
        // 继续添加数据，一次性数据应该被优先淘汰
        cache.put("another", 888);
        
        // 在传统LRU中，oneTime可能因为是最近访问而被保留
        // 但在LRU-K中，热数据得到保护，一次性数据被淘汰
        assertThat(cache.containsKey("hot1")).isTrue();  // 热数据保留
        assertThat(cache.containsKey("hot2")).isTrue();  // 热数据保留
        assertThat(cache.containsKey("oneTime")).isFalse(); // 一次性数据被淘汰
    }

    @Test
    @DisplayName("更新已存在键的值")
    void testUpdateExistingKey() {
        cache.put("key1", 1);
        cache.put("key1", 100); // 更新值
        
        // 检查访问计数（在get之前）
        assertThat(cache.getAccessCount("key1")).isEqualTo(2); // put时1次，再次put时又1次
        assertThat(cache.isInCacheQueue("key1")).isTrue(); // 达到K次，进入缓存队列
        
        // 验证值正确性
        assertThat(cache.get("key1")).isEqualTo(100);
    }

    @Test
    @DisplayName("删除操作测试")
    void testRemove() {
        cache.put("key1", 1);
        cache.get("key1"); // 让其进入缓存队列
        
        assertThat(cache.isInCacheQueue("key1")).isTrue();
        
        Integer removed = cache.remove("key1");
        assertThat(removed).isEqualTo(1);
        assertThat(cache.containsKey("key1")).isFalse();
        assertThat(cache.size()).isEqualTo(0);
        
        // 删除不存在的键
        assertThat(cache.remove("nonexistent")).isNull();
    }

    @Test
    @DisplayName("清空缓存测试")
    void testClear() {
        cache.put("key1", 1);
        cache.put("key2", 2);
        cache.get("key1"); // 让key1进入缓存队列
        
        assertThat(cache.size()).isEqualTo(2);
        assertThat(cache.historyQueueSize()).isEqualTo(1);
        assertThat(cache.cacheQueueSize()).isEqualTo(1);
        
        cache.clear();
        
        assertThat(cache.size()).isEqualTo(0);
        assertThat(cache.historyQueueSize()).isEqualTo(0);
        assertThat(cache.cacheQueueSize()).isEqualTo(0);
        assertThat(cache.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("队列状态统计测试")
    void testQueueStatistics() {
        // 添加一些只访问1次的数据
        cache.put("hist1", 1);
        cache.put("hist2", 2);
        
        // 添加一些访问K次的数据
        cache.put("cache1", 3);
        cache.get("cache1");
        
        assertThat(cache.historyQueueSize()).isEqualTo(2);
        assertThat(cache.cacheQueueSize()).isEqualTo(1);
        assertThat(cache.size()).isEqualTo(3);
        assertThat(cache.capacity()).isEqualTo(3);
        assertThat(cache.getK()).isEqualTo(2);
    }

    @Test
    @DisplayName("边界条件测试：K=1时行为类似传统LRU")
    void testKEqualsOne() {
        LRUKCache<String, Integer> k1Cache = new LRUKCache<>(2, 1);
        
        k1Cache.put("A", 1);
        assertThat(k1Cache.isInCacheQueue("A")).isTrue(); // K=1时，首次访问就进入缓存队列
        
        k1Cache.put("B", 2);
        k1Cache.put("C", 3); // 应该淘汰A
        
        assertThat(k1Cache.containsKey("A")).isFalse();
        assertThat(k1Cache.containsKey("B")).isTrue();
        assertThat(k1Cache.containsKey("C")).isTrue();
    }

    @Test
    @DisplayName("大K值测试：需要多次访问才能获得保护")
    void testLargeKValue() {
        LRUKCache<String, Integer> bigKCache = new LRUKCache<>(3, 5);
        
        bigKCache.put("A", 1);
        bigKCache.get("A"); // 2次
        bigKCache.get("A"); // 3次
        bigKCache.get("A"); // 4次
        
        assertThat(bigKCache.isInHistoryQueue("A")).isTrue(); // 还没达到5次
        
        bigKCache.get("A"); // 第5次
        assertThat(bigKCache.isInCacheQueue("A")).isTrue(); // 现在进入缓存队列
    }

    @Test
    @DisplayName("构造函数参数验证")
    void testConstructorValidation() {
        assertThatThrownBy(() -> new LRUKCache<>(0, 2))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("缓存容量必须大于0");
            
        assertThatThrownBy(() -> new LRUKCache<>(5, 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("K值必须大于0");
    }

    @Test
    @DisplayName("toString方法测试")
    void testToString() {
        String str = cache.toString();
        assertThat(str).contains("capacity=3");
        assertThat(str).contains("k=2");
        assertThat(str).contains("size=0");
        assertThat(str).contains("historyQueue=0");
        assertThat(str).contains("cacheQueue=0");
    }
}
