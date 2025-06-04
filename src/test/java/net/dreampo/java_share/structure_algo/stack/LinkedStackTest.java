package net.dreampo.java_share.structure_algo.stack;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * LinkedStack的单元测试
 */
public class LinkedStackTest {

    private LinkedStack<Integer> stack;

    @BeforeEach
    public void setUp() {
        stack = new LinkedStack<>();
    }

    @Test
    @DisplayName("测试空栈的基本属性")
    public void testEmptyStack() {
        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
        assertThrows(NoSuchElementException.class, () -> stack.pop());
        assertThrows(NoSuchElementException.class, () -> stack.peek());
    }

    @Test
    @DisplayName("测试push和pop操作")
    public void testPushAndPop() {
        // 测试单个元素
        stack.push(1);
        assertFalse(stack.isEmpty());
        assertEquals(1, stack.size());
        assertEquals(1, stack.peek());
        assertEquals(1, stack.pop());
        assertTrue(stack.isEmpty());

        // 测试多个元素（LIFO顺序）
        stack.push(1);
        stack.push(2);
        stack.push(3);
        assertEquals(3, stack.size());
        assertEquals(3, stack.pop());
        assertEquals(2, stack.pop());
        assertEquals(1, stack.pop());
        assertTrue(stack.isEmpty());
    }

    @Test
    @DisplayName("测试peek操作")
    public void testPeek() {
        stack.push(10);
        stack.push(20);

        // peek不应该移除元素
        assertEquals(20, stack.peek());
        assertEquals(20, stack.peek());
        assertEquals(2, stack.size());

        stack.pop();
        assertEquals(10, stack.peek());
        assertEquals(1, stack.size());
    }

    @Test
    @DisplayName("测试clear操作")
    public void testClear() {
        stack.push(1);
        stack.push(2);
        stack.push(3);
        assertEquals(3, stack.size());
        stack.print("清空前，栈的内容是");
        stack.clear();
        stack.print("清空后，栈的内容是");
        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
        assertThrows(NoSuchElementException.class, () -> stack.pop());
    }

    @Test
    @DisplayName("测试toList方法")
    public void testToList() {
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
    public void testFromList() {
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
    public void testFromListWithNull() {
        assertThrows(IllegalArgumentException.class, () -> stack.fromList(null));
    }

    @Test
    @DisplayName("测试copy方法")
    public void testCopy() {
        stack.push(1);
        stack.push(2);
        stack.push(3);

        LinkedStack<Integer> copy = stack.copy();

        // 验证副本内容相同
        assertEquals(stack.size(), copy.size());
        assertEquals(stack.peek(), copy.peek());

        // 验证是深拷贝
        stack.pop();
        assertEquals(2, stack.size());
        assertEquals(3, copy.size());
        assertEquals(2, stack.peek());
        assertEquals(3, copy.peek());
    }

    @Test
    @DisplayName("测试contains方法")
    public void testContains() {
        stack.push(1);
        stack.push(2);
        stack.push(3);

        assertTrue(stack.contains(1));
        assertTrue(stack.contains(2));
        assertTrue(stack.contains(3));
        assertFalse(stack.contains(4));
        assertFalse(stack.contains(0));

        // LinkedStack不允许null元素，所以不测试null值
        // 验证push null会抛出异常
        assertThrows(IllegalArgumentException.class, () -> stack.push(null));
    }

    @Test
    @DisplayName("测试迭代器")
    public void testIterator() {
        stack.push(1);
        stack.push(2);
        stack.push(3);

        List<Integer> result = new ArrayList<>();
        for (Integer value : stack) {
            result.add(value);
        }

        assertEquals(Arrays.asList(3, 2, 1), result);

        // 测试迭代器异常
        Iterator<Integer> iter = stack.iterator();
        assertTrue(iter.hasNext());
        iter.next();
        iter.next();
        iter.next();
        assertFalse(iter.hasNext());
        assertThrows(NoSuchElementException.class, iter::next);
    }

    @Test
    @DisplayName("测试toString方法")
    public void testToString() {
        assertEquals("[]", stack.toString());

        stack.push(1);
        assertEquals("[1]", stack.toString());

        stack.push(2);
        stack.push(3);
        assertEquals("[3 <- 2 <- 1]", stack.toString());
    }

    @Test
    @DisplayName("测试equals和hashCode方法")
    public void testEqualsAndHashCode() {
        LinkedStack<Integer> stack1 = new LinkedStack<>();
        LinkedStack<Integer> stack2 = new LinkedStack<>();

        // 空栈相等
        assertEquals(stack1, stack2);
        assertEquals(stack1.hashCode(), stack2.hashCode());

        // 相同内容相等
        stack1.push(1);
        stack1.push(2);
        stack2.push(1);
        stack2.push(2);
        assertEquals(stack1, stack2);
        assertEquals(stack1.hashCode(), stack2.hashCode());

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
    @DisplayName("测试大量数据")
    public void testLargeData() {
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

    @Test
    @DisplayName("测试null元素的处理")
    public void testNullElements() {
        LinkedStack<String> stringStack = new LinkedStack<>();

        // LinkedStack不允许null元素
        assertThrows(IllegalArgumentException.class, () -> stringStack.push(null));
        
        // 验证正常元素仍然可以正常工作
        stringStack.push("a");
        stringStack.push("b");
        
        assertEquals(2, stringStack.size());
        assertEquals("b", stringStack.pop());
        assertEquals("a", stringStack.pop());
        assertTrue(stringStack.isEmpty());
    }

    @Test
    @DisplayName("测试线程安全性（单线程）")
    public void testConcurrentModification() {
        stack.push(1);
        stack.push(2);
        stack.push(3);

        // 在迭代过程中修改栈应该不会抛出ConcurrentModificationException
        // 因为我们的实现不追踪修改
        List<Integer> collected = new ArrayList<>();
        for (Integer value : stack) {
            collected.add(value);
            if (value == 2) {
                stack.push(4); // 这不会影响当前迭代
            }
        }

        assertEquals(Arrays.asList(3, 2, 1), collected);
        assertEquals(4, stack.size());
    }
}
