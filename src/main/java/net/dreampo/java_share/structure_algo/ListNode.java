package net.dreampo.java_share.structure_algo;

import static java.lang.System.out;

/**
 * 链表节点类（泛型版本）
 * 用于构建单向链表的基本数据结构
 * 
 * @param <T> 节点存储的数据类型
 */
public class ListNode<T> {
    //节点存储的值
    public T val;
    
    //指向下一个节点的引用
    public ListNode<T> next;
    
    public ListNode(T val) {
        this.val = val;
        this.next = null;
    }
    
    public ListNode(T val, ListNode<T> next) {
        this.val = val;
        this.next = next;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        ListNode<T> current = this;
        
        // 检测是否有环
        ListNode<T> cycleEntry = this.detectCycle();
        
        if (cycleEntry == null) {
            // 无环链表，正常遍历
            while (current != null) {
                sb.append(current.val);
                if (current.next != null) {
                    sb.append(" -> ");
                }
                current = current.next;
            }
        } else {
            // 有环链表，需要特殊处理
            // 找到尾节点（环中指向环入口的节点）
            ListNode<T> tail = cycleEntry;
            while (tail.next != cycleEntry) {
                tail = tail.next;
            }
            
            // 打印从头到环入口的部分
            while (current != cycleEntry) {
                sb.append(current.val).append(" -> ");
                current = current.next;
            }
            
            // 标记环入口
            sb.append(current.val).append("(环入口)");
            current = current.next;
            
            // 打印环中的所有节点，直到到达尾节点
            while (current != cycleEntry) {
                sb.append(" -> ").append(current.val);
                if (current == tail) {
                    sb.append("(尾节点)");
                }
                current = current.next;
            }
            
            // 显示尾节点指向环入口
            sb.append(" -> [尾节点指向环入口]");
        }
        
        return sb.toString();
    }
    
    /**
     * 静态工厂方法：使用可变参数创建链表
     * <p>
     * 该方法提供了一种便捷的方式来创建链表，避免了手动设置每个节点的next指针。
     * <p>
     * 使用示例：
     * - ListNode<Integer> list = ListNode.of(1, 2, 3, 4, 5);
     * - ListNode<String> strList = ListNode.of("hello", "world");
     * - ListNode<Integer> emptyList = ListNode.of(); // 返回null
     *
     * @param <T>    节点值的类型
     * @param values 用于创建链表的值，按顺序连接
     * @return 链表的头节点，如果没有提供值则返回null
     */
    @SafeVarargs
    public static <T> ListNode<T> of(T... values) {
        if (values == null || values.length == 0) {
            return null;
        }
        
        ListNode<T> head = new ListNode<>(values[0]);
        ListNode<T> current = head;
        
        for (int i = 1; i < values.length; i++) {
            current.next = new ListNode<>(values[i]);
            current = current.next;
        }
        
        return head;
    }

    /**
     * 辅助方法：打印链表
     */
    public void printList(String desc) {
        ListNode.printList(desc,this);
    }

    public static <T> void printList(String desc,ListNode<T> listNode) {
        out.print(STR."\{desc}: ");
        
        if (listNode == null) {
            out.println("null");
            return;
        }

        // 检测环的入口节点
        ListNode<T> cycleEntry = SinglyLinkedListAlgorithms.detectCycle(listNode);
        
        if (cycleEntry == null) {
            // 无环链表，使用原有的toString方法
            out.println(listNode.toString());
        } else {
            // 有环链表，需要特殊处理
            ListNode<T> current = listNode;
            
            // 打印从头到环入口的部分
            while (current != cycleEntry) {
                out.print(current.val);
                out.print(" -> ");
                current = current.next;
            }
            
            // 标记环入口并打印完整的一圈环
            out.print(STR."\{current.val}(环入口)");
            current = current.next;
            
            // 打印环中的所有节点，直到回到环入口
            while (current != cycleEntry) {
                out.print(" -> ");
                out.print(current.val);
                current = current.next;
            }
            
            // 用特殊标记表示回到环入口，形成闭环
            out.println(STR." -> ↩ \{cycleEntry.val}(环入口)");
        }
    }


    public ListNode<T> detectCycle() {
        return SinglyLinkedListAlgorithms.detectCycle(this);
    }




    public boolean hasCycle(){
        return SinglyLinkedListAlgorithms.hasCycle(this);
    }



    public int getCycleLength(){
        return SinglyLinkedListAlgorithms.getCycleLength(this);
    }


    /**
     * 重写equals方法，用于比较两个链表是否相等
     * @param obj 要比较的对象
     * @return 如果两个链表的所有节点值都相等则返回true
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ListNode<?> that = (ListNode<?>) obj;
        ListNode<T> current1 = this;
        ListNode<?> current2 = that;

        while (current1 != null && current2 != null) {
            if (!current1.val.equals(current2.val)) {
                return false;
            }
            current1 = current1.next;
            current2 = current2.next;
        }

        return current1 == null && current2 == null;
    }

    /**
     * 重写hashCode方法
     * @return 链表的哈希码
     */
    @Override
    public int hashCode() {
        int result = 0;
        ListNode<T> current = this;
        while (current != null) {
            result = 31 * result + (current.val != null ? current.val.hashCode() : 0);
            current = current.next;
        }
        return result;
    }
}
