package net.dreampo.java_share.structure_algo.stack;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * StackType 枚举测试类
 */
public class StackTypeTest {

    @Test
    public void testStackTypeValues() {
        // 测试所有枚举值
        StackType[] types = StackType.values();
        assertEquals(3, types.length);
        
        // 验证每个枚举值
        assertEquals(StackType.JAVA_UTIL, StackType.valueOf("JAVA_UTIL"));
        assertEquals(StackType.LINKED, StackType.valueOf("LINKED"));
        assertEquals(StackType.ARRAY, StackType.valueOf("ARRAY"));
    }

    @Test
    public void testStackTypeOrdinal() {
        // 测试枚举的序号
        assertEquals(0, StackType.JAVA_UTIL.ordinal());
        assertEquals(1, StackType.LINKED.ordinal());
        assertEquals(2, StackType.ARRAY.ordinal());
    }

    @Test
    public void testStackTypeName() {
        // 测试枚举的名称
        assertEquals("JAVA_UTIL", StackType.JAVA_UTIL.name());
        assertEquals("LINKED", StackType.LINKED.name());
        assertEquals("ARRAY", StackType.ARRAY.name());
    }

    @Test
    public void testStackCreation() {
        // 测试使用不同的 StackType 创建栈实例
        for (StackType type : StackType.values()) {
            Stack<Integer> stack = createStack(type);
            assertNotNull(stack);
            
            // 测试基本操作
            assertTrue(stack.isEmpty());
            stack.push(1);
            assertFalse(stack.isEmpty());
            assertEquals(1, stack.pop());
            assertTrue(stack.isEmpty());
        }
    }

    /**
     * 辅助方法：根据 StackType 创建相应的栈实例
     */
    private Stack<Integer> createStack(StackType type) {
        return switch (type) {
            case JAVA_UTIL -> new JavaUtilStackAdapter<Integer>();
            case LINKED -> new LinkedStack<Integer>();
            case ARRAY -> new ArrayStack<Integer>();
        };
    }
}
