package net.dreampo.java_share.structure_algo.stack;

import java.util.*;

/**
 * Java Util Stack适配器
 * 将 java.util.Stack 适配为 data_structure.stack.Stack 接口
 * 
 * @param <T> 栈中元素的类型
 */
public class JavaUtilStackAdapter<T> implements Stack<T> {
    
    private static final long serialVersionUID = 1L;
    private final java.util.Stack<T> stack = new java.util.Stack<>();
    
    @Override
    public void push(T item) {
        if (item == null) {
            throw new IllegalArgumentException("Element cannot be null");
        }
        stack.push(item);
    }
    
    @Override
    public T pop() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }
        return stack.pop();
    }
    
    @Override
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }
        return stack.peek();
    }
    
    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }
    
    @Override
    public int size() {
        return stack.size();
    }
    
    @Override
    public void clear() {
        stack.clear();
    }
    
    @Override
    public List<T> toList() {
        List<T> result = new ArrayList<>(stack);
        // java.util.Stack 的迭代顺序是从栈底到栈顶，正好符合我们的规范
        return result;
    }
    
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
    
    @Override
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = stack.size() - 1; i >= 0; i--) {
            sb.append(stack.get(i));
            if (i > 0) {
                sb.append(" <- ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        JavaUtilStackAdapter<?> other = (JavaUtilStackAdapter<?>) obj;
        return stack.equals(other.stack);
    }
    
    @Override
    public int hashCode() {
        return stack.hashCode();
    }
}
