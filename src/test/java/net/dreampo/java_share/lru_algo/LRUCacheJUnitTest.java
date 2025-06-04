package net.dreampo.java_share.lru_algo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LRUCache的单元测试
 */
@DisplayName("LRU缓存测试")
public class LRUCacheJUnitTest {

    private LRUCache<Integer, String> cache;
    private LRUCache<String, Integer> stringCache;

    @BeforeEach
    void setUp() {
        cache = new LRUCache<>(3);
        stringCache = new LRUCache<>(2);
    }

    @Nested
    @DisplayName("基本功能测试")
    class BasicFunctionalityTest {

        @Test
        @DisplayName("新建缓存应该为空")
        void testNewCacheIsEmpty() {
            assertTrue(cache.isEmpty());
            assertEquals(0, cache.size());
            assertEquals(3, cache.capacity());
        }

        @Test
        @DisplayName("基本的put和get操作")
        void testBasicPutAndGet() {
            cache.put(1, "One");
            cache.put(2, "Two");
            cache.put(3, "Three");

            assertEquals("One", cache.get(1));
            assertEquals("Two", cache.get(2));
            assertEquals("Three", cache.get(3));
            assertEquals(3, cache.size());
            assertFalse(cache.isEmpty());
        }

        @Test
        @DisplayName("获取不存在的键返回null")
        void testGetNonExistentKey() {
            cache.put(1, "One");
            assertNull(cache.get(2));
            assertNull(cache.get(999));
        }

        @Test
        @DisplayName("更新已存在的键")
        void testUpdateExistingKey() {
            cache.put(1, "One");
            cache.put(2, "Two");

            assertEquals("One", cache.get(1));

            cache.put(1, "One Updated");
            assertEquals("One Updated", cache.get(1));
            assertEquals(2, cache.size()); // 大小不变
        }
    }

    @Nested
    @DisplayName("LRU淘汰策略测试")
    class LRUEvictionTest {

        @Test
        @DisplayName("超出容量时淘汰最久未使用的元素")
        void testLRUEvictionPolicy() {
            // 添加3个元素（达到容量）
            cache.put(1, "One");
            cache.put(2, "Two");
            cache.put(3, "Three");

            // 访问key=2，使其成为最近使用
            assertEquals("Two", cache.get(2));

            // 添加第4个元素，应该淘汰key=1（最久未使用）
            cache.put(4, "Four");

            assertNull(cache.get(1)); // key=1已被淘汰
            assertEquals("Two", cache.get(2));
            assertEquals("Three", cache.get(3));
            assertEquals("Four", cache.get(4));
            assertEquals(3, cache.size());
        }

        @Test
        @DisplayName("访问顺序影响淘汰策略")
        void testAccessOrderAffectsEviction() {
            cache.put(1, "One");
            cache.put(2, "Two");
            cache.put(3, "Three");

            // 按顺序访问所有元素
            cache.get(1);
            cache.get(2);
            cache.get(3);

            // 添加新元素，应该淘汰key=1（最早访问的）
            cache.put(4, "Four");

            assertNull(cache.get(1));
            assertNotNull(cache.get(2));
            assertNotNull(cache.get(3));
            assertNotNull(cache.get(4));
        }

        @Test
        @DisplayName("更新操作也算作访问")
        void testUpdateCountsAsAccess() {
            cache.put(1, "One");
            cache.put(2, "Two");
            cache.put(3, "Three");

            // 更新key=1
            cache.put(1, "One Updated");

            // 添加新元素，应该淘汰key=2（现在是最久未使用的）
            cache.put(4, "Four");

            assertNotNull(cache.get(1)); // key=1因为刚更新过，不会被淘汰
            assertNull(cache.get(2));    // key=2被淘汰
            assertNotNull(cache.get(3));
            assertNotNull(cache.get(4));
        }
    }

    @Nested
    @DisplayName("附加功能测试")
    class AdditionalFeaturesTest {

        @Test
        @DisplayName("containsKey方法测试")
        void testContainsKey() {
            assertFalse(cache.containsKey(1));

            cache.put(1, "One");
            assertTrue(cache.containsKey(1));

            cache.put(2, "Two");
            cache.put(3, "Three");
            cache.put(4, "Four"); // 淘汰key=1

            assertFalse(cache.containsKey(1));
            assertTrue(cache.containsKey(4));
        }

        @Test
        @DisplayName("remove方法测试")
        void testRemove() {
            cache.put(1, "One");
            cache.put(2, "Two");
            cache.put(3, "Three");

            assertEquals("Two", cache.remove(2));
            assertEquals(2, cache.size());
            assertFalse(cache.containsKey(2));
            assertNull(cache.get(2));

            // 删除不存在的键
            assertNull(cache.remove(999));
            assertEquals(2, cache.size());
        }

        @Test
        @DisplayName("clear方法测试")
        void testClear() {
            cache.put(1, "One");
            cache.put(2, "Two");
            cache.put(3, "Three");

            assertEquals(3, cache.size());
            assertFalse(cache.isEmpty());

            cache.clear();

            assertEquals(0, cache.size());
            assertTrue(cache.isEmpty());
            assertNull(cache.get(1));
            assertNull(cache.get(2));
            assertNull(cache.get(3));
        }
    }

    @Nested
    @DisplayName("不同类型缓存测试")
    class DifferentTypeCacheTest {

        @Test
        @DisplayName("字符串键整数值缓存")
        void testStringKeyIntegerValueCache() {
            stringCache.put("apple", 100);
            stringCache.put("banana", 200);

            assertEquals(100, stringCache.get("apple"));
            assertEquals(200, stringCache.get("banana"));

            // 访问apple
            stringCache.get("apple");

            // 添加第三个，应该淘汰banana
            stringCache.put("orange", 300);

            assertEquals(100, stringCache.get("apple"));
            assertNull(stringCache.get("banana"));
            assertEquals(300, stringCache.get("orange"));
        }

        @Test
        @DisplayName("自定义对象作为值")
        void testCustomObjectAsValue() {
            class User {
                String name;
                int age;

                User(String name, int age) {
                    this.name = name;
                    this.age = age;
                }
            }

            LRUCache<String, User> userCache = new LRUCache<>(2);

            User user1 = new User("Alice", 25);
            User user2 = new User("Bob", 30);
            User user3 = new User("Charlie", 35);

            userCache.put("u1", user1);
            userCache.put("u2", user2);

            assertEquals("Alice", userCache.get("u1").name);
            assertEquals(30, userCache.get("u2").age);

            userCache.put("u3", user3);

            assertNull(userCache.get("u1")); // u1被淘汰
            assertNotNull(userCache.get("u2"));
            assertNotNull(userCache.get("u3"));
        }
    }

    @Nested
    @DisplayName("边界条件和特殊情况测试")
    class EdgeCaseTest {

        @Test
        @DisplayName("容量为1的缓存")
        void testCacheWithCapacityOne() {
            LRUCache<Integer, String> singleCache = new LRUCache<>(1);

            singleCache.put(1, "One");
            assertEquals("One", singleCache.get(1));

            singleCache.put(2, "Two");
            assertNull(singleCache.get(1)); // 1被淘汰
            assertEquals("Two", singleCache.get(2));
        }

        @ParameterizedTest
        @ValueSource(ints = {10, 100, 1000})
        @DisplayName("不同容量的缓存测试")
        void testDifferentCapacities(int capacity) {
            LRUCache<Integer, Integer> testCache = new LRUCache<>(capacity);

            // 填满缓存
            for (int i = 0; i < capacity; i++) {
                testCache.put(i, i * 10);
            }

            assertEquals(capacity, testCache.size());

            // 添加一个新元素，验证最旧的被淘汰
            testCache.put(capacity, capacity * 10);
            assertEquals(capacity, testCache.size());
            assertNull(testCache.get(0)); // 第一个元素被淘汰
            assertNotNull(testCache.get(capacity)); // 新元素存在
        }

        @Test
        @DisplayName("连续的put和get操作")
        void testContinuousPutAndGet() {
            // 模拟实际使用场景
            for (int i = 0; i < 10; i++) {
                cache.put(i, "Value" + i);

                // 随机访问一些已存在的键
                if (i > 2) {
                    cache.get(i - 2);
                }
            }

            assertEquals(3, cache.size()); // 容量限制

            // 最近的3个应该还在缓存中
            assertNotNull(cache.get(9));
            assertNotNull(cache.get(8));
            assertNotNull(cache.get(7));
        }

        @Test
        @DisplayName("null值处理")
        void testNullValues() {
            cache.put(1, null);
            assertTrue(cache.containsKey(1));
            assertNull(cache.get(1));
            assertEquals(1, cache.size());

            cache.put(1, "NotNull");
            assertEquals("NotNull", cache.get(1));
        }
    }

    @Nested
    @DisplayName("性能相关测试")
    class PerformanceTest {

        @Test
        @DisplayName("大量操作性能测试")
        void testLargeNumberOfOperations() {
            LRUCache<Integer, String> largeCache = new LRUCache<>(1000);

            long startTime = System.currentTimeMillis();

            // 执行10000次操作
            for (int i = 0; i < 10000; i++) {
                largeCache.put(i, "Value" + i);

                // 随机访问
                if (i % 3 == 0 && i > 0) {
                    largeCache.get(i / 2);
                }
            }

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // 验证基本功能正常
            assertEquals(1000, largeCache.size());
            assertNotNull(largeCache.get(9999)); // 最近添加的应该存在

            // 性能应该在合理范围内（具体时间取决于机器）
            assertTrue(duration < 1000, "操作耗时过长: " + duration + "ms");
        }
    }

}
