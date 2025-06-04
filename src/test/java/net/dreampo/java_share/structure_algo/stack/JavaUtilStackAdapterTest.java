package net.dreampo.java_share.structure_algo.stack;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * JavaUtilStackAdapter的单元测试
 */
@DisplayName("JavaUtilStackAdapter 单元测试")
public class JavaUtilStackAdapterTest {

    private JavaUtilStackAdapter<Integer> stack;

    @BeforeEach
    void setUp() {
        stack = new JavaUtilStackAdapter<>();
    }

    @Nested
    @DisplayName("测试栈的基本操作")
    class BasicOperations {

        @Test
        @DisplayName("测试空栈的基本属性")
        void testEmptyStack() {
            assertTrue(stack.isEmpty());
            assertEquals(0, stack.size());
            assertThrows(IllegalStateException.class, () -> stack.pop());
            assertThrows(IllegalStateException.class, () -> stack.peek());
        }

        @Test
        @DisplayName("测试push操作")
        void testPush() {
            stack.push(1);
            assertFalse(stack.isEmpty());
            assertEquals(1, stack.size());
            assertEquals(1, stack.peek());

            stack.push(2);
            assertEquals(2, stack.size());
            assertEquals(2, stack.peek());
        }

        @Test
        @DisplayName("测试push null元素抛出异常")
        void testPushNull() {
            assertThrows(IllegalArgumentException.class, () -> stack.push(null));
        }

        @Test
        @DisplayName("测试pop操作")
        void testPop() {
            stack.push(1);
            stack.push(2);
            stack.push(3);

            assertEquals(3, stack.pop());
            assertEquals(2, stack.pop());
            assertEquals(1, stack.pop());
            assertTrue(stack.isEmpty());
        }

        @Test
        @DisplayName("测试peek操作")
        void testPeek() {
            stack.push(10);
            stack.push(20);

            // peek不应该移除元素
            assertEquals(20, stack.peek());
            assertEquals(20, stack.peek());
            assertEquals(2, stack.size());
        }

        @Test
        @DisplayName("测试clear操作")
        void testClear() {
            stack.push(1);
            stack.push(2);
            stack.push(3);
            assertEquals(3, stack.size());

            stack.clear();
            assertTrue(stack.isEmpty());
            assertEquals(0, stack.size());
        }
    }

    @Nested
    @DisplayName("测试toList和fromList方法")
    class ListOperations {

        @Test
        @DisplayName("测试toList方法")
        void testToList() {
            stack.push(1);
            stack.push(2);
            stack.push(3);

            List<Integer> list = stack.toList();
            // toList返回的列表：栈底在前，栈顶在后
            assertEquals(Arrays.asList(1, 2, 3), list);

            // 确保返回的是副本
            list.clear();
            assertEquals(3, stack.size());
        }

        @Test
        @DisplayName("测试fromList方法")
        void testFromList() {
            List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
            stack.fromList(list);

            assertEquals(5, stack.size());
            // fromList：列表第一个元素成为栈底，最后一个元素成为栈顶
            assertEquals(5, stack.pop());
            assertEquals(4, stack.pop());
            assertEquals(3, stack.pop());
            assertEquals(2, stack.pop());
            assertEquals(1, stack.pop());
            assertTrue(stack.isEmpty());
        }

        @Test
        @DisplayName("测试fromList方法处理null参数")
        void testFromListWithNull() {
            assertThrows(IllegalArgumentException.class, () -> stack.fromList(null));
        }

        @Test
        @DisplayName("测试toList和fromList的互逆性")
        void testToListFromListConsistency() {
            // 创建原始栈
            stack.push(1);
            stack.push(2);
            stack.push(3);

            // 转换为列表再转回栈
            List<Integer> list = stack.toList();
            JavaUtilStackAdapter<Integer> newStack = new JavaUtilStackAdapter<>();
            newStack.fromList(list);

            // 验证两个栈相同
            assertEquals(stack, newStack);
            assertEquals(stack.size(), newStack.size());
            while (!stack.isEmpty()) {
                assertEquals(stack.pop(), newStack.pop());
            }
        }
    }

    @Nested
    @DisplayName("测试toString、equals和hashCode")
    class ObjectMethods {

        @Test
        @DisplayName("测试toString方法")
        void testToString() {
            assertEquals("[]", stack.toString());

            stack.push(1);
            assertEquals("[1]", stack.toString());

            stack.push(2);
            stack.push(3);
            assertEquals("[3 <- 2 <- 1]", stack.toString());
        }

        @Test
        @DisplayName("测试equals方法")
        void testEquals() {
            JavaUtilStackAdapter<Integer> stack1 = new JavaUtilStackAdapter<>();
            JavaUtilStackAdapter<Integer> stack2 = new JavaUtilStackAdapter<>();

            // 空栈相等
            assertEquals(stack1, stack2);

            // 相同内容相等
            stack1.push(1);
            stack1.push(2);
            stack2.push(1);
            stack2.push(2);
            assertEquals(stack1, stack2);

            // 不同内容不相等
            stack2.push(3);
            assertNotEquals(stack1, stack2);

            // 自反性
            assertEquals(stack1, stack1);

            // null和不同类型
            assertNotEquals(stack1, null);
            assertNotEquals(stack1, "not a stack");
        }

        @Test
        @DisplayName("测试hashCode方法")
        void testHashCode() {
            JavaUtilStackAdapter<Integer> stack1 = new JavaUtilStackAdapter<>();
            JavaUtilStackAdapter<Integer> stack2 = new JavaUtilStackAdapter<>();

            // 空栈hash相同
            assertEquals(stack1.hashCode(), stack2.hashCode());

            // 相同内容hash相同
            stack1.push(1);
            stack1.push(2);
            stack2.push(1);
            stack2.push(2);
            assertEquals(stack1.hashCode(), stack2.hashCode());
        }
    }

    @Nested
    @DisplayName("测试大量数据")
    class PerformanceTests {

        @Test
        @DisplayName("测试大量数据的处理")
        void testLargeData() {
            int n = 10000;

            // 压入大量数据
            for (int i = 0; i < n; i++) {
                stack.push(i);
            }
            assertEquals(n, stack.size());

            // 弹出并验证顺序
            for (int i = n - 1; i >= 0; i--) {
                assertEquals(i, stack.pop());
            }
            assertTrue(stack.isEmpty());
        }
    }
}
