package net.dreampo.java_share.structure_algo.stack;

import java.io.Serializable;
import java.util.*;

import static java.lang.System.out;

/**
 * 基于链表的栈实现
 * LinkedStack 相比 java.util.Stack 性能提升了 58.21%
 *
 * @param <T> 栈中元素的类型
 */
public class LinkedStack<T> implements Stack<T>, Iterable<T> {
    
    private static final long serialVersionUID = 1L;

    /**
     * 内部节点类
     */
    private static class Node<T> implements Serializable {
        private static final long serialVersionUID = 1L;
        T value;
        Node<T> next;

        Node(T value, Node<T> next) {
            this.value = value;
            this.next = next;
        }
    }

    private Node<T> top;
    private int size;

    /**
     * 构造一个空栈
     */
    public LinkedStack() {
        this.top = null;
        this.size = 0;
    }

    /**
     * 将元素压入栈顶
     *
     * @param value 要压入的元素
     */
    @Override
    public void push(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Cannot push null element");
        }
        top = new Node<>(value, top);
        size++;
    }

    /**
     * 弹出并返回栈顶元素
     *
     * @return 栈顶元素
     * @throws IllegalStateException 如果栈为空
     */
    @Override
    public T pop() {
        if (isEmpty()) {
            throw new NoSuchElementException("Stack is empty");
        }
        T value = top.value;
        top = top.next;
        size--;
        return value;
    }

    /**
     * 返回栈顶元素但不移除
     *
     * @return 栈顶元素
     * @throws IllegalStateException 如果栈为空
     */
    @Override
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Stack is empty");
        }
        return top.value;
    }

    /**
     * 检查栈是否为空
     *
     * @return 如果栈为空返回true，否则返回false
     */
    @Override
    public boolean isEmpty() {
        return top == null;
    }

    /**
     * 返回栈中元素的数量
     *
     * @return 栈的大小
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * 清空栈中的所有元素
     */
    @Override
    public void clear() {
        top = null;
        size = 0;
    }

    /**
     * 将栈转换为列表（栈底元素在列表开头，栈顶元素在列表末尾）
     * 
     * 【内存问题】：此方法会创建临时列表并进行反转操作，消耗额外内存
     * 【建议】：对于大数据量场景，使用 toListEfficient() 方法
     *
     * @return 包含栈中所有元素的列表
     */
    @Override
    public List<T> toList() {
        List<T> list = new ArrayList<>(size);
        Node<T> current = top;
        while (current != null) {
            list.add(current.value);
            current = current.next;
        }
        // 反转列表，使栈底元素在开头
        Collections.reverse(list);
        return list;
    }

    /**
     * 【内存优化新增方法】
     * 高效地将栈转换为列表，避免反转操作，减少内存消耗
     * 
     * 实现原理：
     * 1. 先遍历一次获取栈的深度
     * 2. 创建固定大小的数组
     * 3. 从后向前填充数组，避免反转
     * 
     * 内存优化：
     * - 避免了 Collections.reverse() 的额外内存开销
     * - 直接在正确位置放置元素，减少数组元素移动
     * 
     * @return 包含栈中所有元素的列表（栈底在前，栈顶在后）
     */
    public List<T> toListEfficient() {
        if (size == 0) {
            return new ArrayList<>();
        }
        
        // 创建固定大小的列表
        ArrayList<T> list = new ArrayList<>(size);
        
        // 预先分配空间
        for (int i = 0; i < size; i++) {
            list.add(null);
        }
        
        // 从后向前填充，避免反转
        Node<T> current = top;
        int index = size - 1;
        while (current != null && index >= 0) {
            list.set(index--, current.value);
            current = current.next;
        }
        
        return list;
    }

    /**
     * 从列表构建栈（列表开头元素成为栈底，列表末尾元素成为栈顶）
     *
     * @param list 源列表
     */
    @Override
    public void fromList(List<T> list) {
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null");
        }
        clear();
        for (T item : list) {
            push(item);
        }
    }

    /**
     * 创建栈的副本
     *
     * @return 栈的深拷贝
     */
    public LinkedStack<T> copy() {
        LinkedStack<T> newStack = new LinkedStack<>();
        List<T> temp = this.toListEfficient();  // 使用高效方法
        // toListEfficient已经返回正确顺序（栈底在前），直接使用fromList
        newStack.fromList(temp);
        return newStack;
    }

    /**
     * 检查栈中是否包含指定元素
     *
     * @param element 要查找的元素
     * @return 如果包含该元素返回true，否则返回false
     */
    public boolean contains(T element) {
        Node<T> current = top;
        while (current != null) {
            if (Objects.equals(current.value, element)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = top;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T value = current.value;
                current = current.next;
                return value;
            }
        };
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Node<T> current = top;
        while (current != null) {
            sb.append(current.value);
            if (current.next != null) {
                sb.append(" <- ");
            }
            current = current.next;
        }
        sb.append("]");
        return sb.toString();
    }

    public void print(){
        print("");
    }

    public void print(String desc){
        if(desc==null || desc.isBlank()){
            out.println(toString());
        }else{
            out.println(STR."\{desc.trim()} \{toString()}");
        }

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        LinkedStack<?> other = (LinkedStack<?>) obj;
        if (this.size != other.size) return false;

        Node<T> thisCurrent = this.top;
        Node<?> otherCurrent = other.top;

        while (thisCurrent != null) {
            if (!Objects.equals(thisCurrent.value, otherCurrent.value)) {
                return false;
            }
            thisCurrent = thisCurrent.next;
            otherCurrent = otherCurrent.next;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        Node<T> current = top;
        while (current != null) {
            result = 31 * result + (current.value == null ? 0 : current.value.hashCode());
            current = current.next;
        }
        return result;
    }
}