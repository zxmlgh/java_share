package net.dreampo.java_share.structure_algo.stack;

/**
 * 栈实现类型枚举
 * 
 * 定义了系统中可用的栈实现类型，用于在不同场景下选择合适的栈实现。
 * 可用于二叉树遍历、图遍历等需要栈数据结构的算法。
 * 
 * @author Refactored Version
 * @version 1.0
 */
public enum StackType {
    /**
     * 使用 java.util.Stack 的适配器实现
     * 基于 Vector，线程安全但性能相对较低
     */
    JAVA_UTIL,
    
    /**
     * 基于链表的栈实现
     * 动态分配内存，没有容量限制，适合元素数量不确定的场景
     */
    LINKED,
    
    /**
     * 基于数组的栈实现
     * 性能较高，但有初始容量限制，需要动态扩容
     */
    ARRAY
}
