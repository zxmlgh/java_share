package net.dreampo.java_share.structure_algo.stack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ArrayStack 单元测试")
public class ArrayStackTest {

    private ArrayStack<Integer> stack;

    @BeforeEach
    void setUp() {
        stack = new ArrayStack<>();
    }

    @Nested
    @DisplayName("测试栈的基本操作")
    class BasicOperations {

        @Test
        @DisplayName("测试压入元素")
        void testPush() {
            stack.push(1);
            assertEquals(1, stack.size());
            assertEquals(1, stack.peek());

            stack.push(2);
            assertEquals(2, stack.size());
            assertEquals(2, stack.peek());
        }

        @Test
        @DisplayName("测试弹出元素")
        void testPop() {
            stack.push(1);
            assertEquals(1, stack.pop());
            assertTrue(stack.isEmpty());

            stack.push(2);
            stack.push(3);
            assertEquals(3, stack.pop());
            assertEquals(2, stack.pop());
        }

        @Test
        @DisplayName("测试查看栈顶元素")
        void testPeek() {
            stack.push(1);
            assertEquals(1, stack.peek());
            assertEquals(1, stack.size());

            stack.push(2);
            assertEquals(2, stack.peek());
        }

        @Test
        @DisplayName("测试检查栈是否为空")
        void testIsEmpty() {
            assertTrue(stack.isEmpty());
            stack.push(1);
            assertFalse(stack.isEmpty());
        }

        @Test
        @DisplayName("测试获取栈的大小")
        void testSize() {
            assertEquals(0, stack.size());
            stack.push(1);
            assertEquals(1, stack.size());

            stack.push(2);
            assertEquals(2, stack.size());
        }

        @Test
        @DisplayName("测试清空栈")
        void testClear() {
            stack.push(1);
            stack.clear();
            assertTrue(stack.isEmpty());
        }
    }

    @Nested
    @DisplayName("测试栈的高级操作")
    class AdvancedOperations {

        @Test
        @DisplayName("测试将栈转换为列表")
        void testToList() {
            stack.push(1);
            stack.push(2);
            List<Integer> list = stack.toList();
            // toList返回的列表：栈底在前，栈顶在后
            assertEquals(List.of(1, 2), list);
        }

        @Test
        @DisplayName("测试从列表构建栈")
        void testFromList() {
            List<Integer> list = List.of(1, 2);
            stack.fromList(list);
            stack.print();
            // fromList：列表第一个元素成为栈底，最后一个元素成为栈顶
            assertEquals(2, stack.peek());
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
        @DisplayName("测试创建栈的副本")
        void testCopy() {
            stack.push(1);
            stack.push(2);
            ArrayStack<Integer> copy = stack.copy();
            assertEquals(stack, copy);
        }

        @Test
        @DisplayName("测试栈是否包含指定元素")
        void testContains() {
            stack.push(1);
            assertTrue(stack.contains(1));
            assertFalse(stack.contains(2));
        }
    }

    @Nested
    @DisplayName("测试栈的异常情况")
    class ExceptionHandling {

        @Test
        @DisplayName("测试弹出空栈时抛出异常")
        void testPopEmptyStack() {
            assertThrows(NoSuchElementException.class, stack::pop);
        }

        @Test
        @DisplayName("测试查看空栈顶元素时抛出异常")
        void testPeekEmptyStack() {
            assertThrows(NoSuchElementException.class, stack::peek);
        }
    }

    @Nested
    @DisplayName("测试栈的字符串表示和打印功能")
    class StringRepresentation {

        @Test
        @DisplayName("测试栈的字符串表示")
        void testToString() {
            assertEquals("[]", stack.toString());
            stack.push(1);
            assertEquals("[1]", stack.toString());
            stack.push(2);
            assertEquals("[2 <- 1]", stack.toString());
        }

        @Test
        @DisplayName("测试栈的打印功能")
        void testPrint() {
            stack.push(1);
            stack.print("Stack contents: ");
        }
    }

    @Nested
    @DisplayName("测试栈的相等性和哈希值")
    class EqualityAndHashCode {

        @Test
        @DisplayName("测试栈的相等性")
        void testEquals() {
            ArrayStack<Integer> stack1 = new ArrayStack<>();
            stack1.push(1);
            stack.push(1);
            assertEquals(stack, stack1);

            ArrayStack<Integer> stack2 = new ArrayStack<>();
            stack2.push(2);
            assertNotEquals(stack, stack2);
        }

        @Test
        @DisplayName("测试栈的哈希值")
        void testHashCode() {
            ArrayStack<Integer> stack1 = new ArrayStack<>();
            stack1.push(1);
            stack.push(1);
            assertEquals(stack.hashCode(), stack1.hashCode());

            ArrayStack<Integer> stack2 = new ArrayStack<>();
            stack2.push(2);
            assertNotEquals(stack.hashCode(), stack2.hashCode());
        }
    }
}

