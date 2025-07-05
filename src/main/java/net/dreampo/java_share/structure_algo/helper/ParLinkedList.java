package net.dreampo.java_share.structure_algo.helper;

import net.dreampo.java_share.structure_algo.ListNode;

/**
 * @author richard.dreampo
 */
public class ParLinkedList<T> {
    private final ListNode<T> firstList;   // 满足条件的链表（predicate返回true）
    private final ListNode<T> secondList;  // 不满足条件的链表（predicate返回false）

    public ParLinkedList(ListNode<T> firstList, ListNode<T> secondList) {
        this.firstList = firstList;
        this.secondList = secondList;
    }

    /**
     * 获取满足条件的链表
     *
     * @return 所有使predicate返回true的节点组成的链表
     */
    public ListNode<T> getFirstList() {
        return firstList;
    }

    /**
     * 获取不满足条件的链表
     *
     * @return 所有使predicate返回false的节点组成的链表
     */
    public ListNode<T> getSecondList() {
        return secondList;
    }

    /**
     * 将两个链表合并成一个链表
     * 满足条件的节点在前，不满足条件的节点在后
     *
     * @return 合并后的链表头节点
     */
    public ListNode<T> getMergedList() {
        if (firstList == null) {
            return secondList;
        }

        // 找到firstList的尾节点
        ListNode<T> tail = firstList;
        while (tail.next != null) {
            tail = tail.next;
        }

        // 连接两个链表
        tail.next = secondList;
        return firstList;
    }
}
