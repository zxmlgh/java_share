package net.dreampo.java_share.structure_algo.stack;

/**
 * 栈工厂类，用于根据 StackType 创建相应的栈实例
 * 
 * 该工厂类提供了统一的创建栈实例的方法，避免了在多个类中重复相同的创建逻辑
 * 
 * @author jdk_helper
 * @version 1.0
 */
public class StackFactory {
    
    /**
     * 私有构造函数，防止实例化
     */
    private StackFactory() {
        throw new UnsupportedOperationException("工具类不应该被实例化");
    }
    
    /**
     * 根据指定的栈类型创建栈实例
     * 
     * @param <T> 栈元素类型，必须实现 Serializable 接口
     * @param stackType 栈实现类型
     * @return 对应类型的栈实例
     * @throws IllegalArgumentException 如果 stackType 为 null
     */
    @SuppressWarnings("unchecked")
    public static <T> Stack<T> createStack(StackType stackType) {
        if (stackType == null) {
            throw new IllegalArgumentException("栈类型不能为 null");
        }
        
        return switch (stackType) {
            case JAVA_UTIL -> new JavaUtilStackAdapter<T>();
            case LINKED -> new LinkedStack<T>();
            case ARRAY -> new ArrayStack<T>();
        };
    }
    
    /**
     * 创建默认类型（链表）的栈实例
     * 
     * @param <T> 栈元素类型，必须实现 Serializable 接口
     * @return 链表栈实例
     */
    @SuppressWarnings("unchecked")
    public static <T> Stack<T> createDefaultStack() {
        return createStack(StackType.LINKED);
    }

}
