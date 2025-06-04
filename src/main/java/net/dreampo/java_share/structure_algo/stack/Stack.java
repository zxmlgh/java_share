package net.dreampo.java_share.structure_algo.stack;

import java.io.Serializable;

/**
 * 栈的公共接口
 * 
 * @param <E> 栈中元素的类型
 */
public interface Stack<E> extends Serializable {
    
    /**
     * 将元素压入栈顶
     * 
     * @param element 要压入的元素
     * @throws IllegalStateException 如果栈已满（对于固定大小的栈）
     */
    void push(E element);
    
    /**
     * 从栈顶弹出元素
     * 
     * @return 栈顶元素
     * @throws java.util.NoSuchElementException 如果栈为空
     */
    E pop();
    
    /**
     * 查看栈顶元素但不移除
     * 
     * @return 栈顶元素
     * @throws java.util.NoSuchElementException 如果栈为空
     */
    E peek();
    
    /**
     * 检查栈是否为空
     * 
     * @return 如果栈为空返回 true，否则返回 false
     */
    boolean isEmpty();
    
    /**
     * 获取栈中元素的数量
     * 
     * @return 栈中元素的数量
     */
    int size();
    
    /**
     * 清空栈中所有元素
     */
    void clear();
    
    /**
     * 将栈中的元素转换为列表
     * 列表中的元素顺序为：第一个元素是栈底，最后一个元素是栈顶
     * 
     * @return 包含栈中所有元素的列表
     */
    java.util.List<E> toList();
    
    /**
     * 从列表中初始化栈
     * 列表中的第一个元素将成为栈底，最后一个元素将成为栈顶
     * 
     * @param list 用于初始化栈的列表
     * @throws IllegalArgumentException 如果列表为 null
     */
    void fromList(java.util.List<E> list);
}
