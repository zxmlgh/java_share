package net.dreampo.java_share.structure_algo;

import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 合并有序链表算法测试类
 * 
 * @author richard
 */
public class MergeSortedListsTest {
    
    @Test
    public void testDefaultAscendingMerge() {
        System.out.println("=== 示例1：默认升序合并 ===");
        
        // list1: 1->2->4
        ListNode<Integer> list1 = ListNode.of(1, 2, 4);
        
        // list2: 1->3->4
        ListNode<Integer> list2 = ListNode.of(1, 3, 4);

        list1.printList("链表1");
        list2.printList("链表2");
        
        ListNode<Integer> result = SinglyLinkedListAlgorithms.mergeTwoLists(list1, list2);
        result.printList("合并后（升序）");
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.val);
        assertEquals(1, result.next.val);
        assertEquals(2, result.next.next.val);
        assertEquals(3, result.next.next.next.val);
        assertEquals(4, result.next.next.next.next.val);
        assertEquals(4, result.next.next.next.next.next.val);
        assertNull(result.next.next.next.next.next.next);
    }
    
    @Test
    public void testDescendingMerge() {
        System.out.println("\n=== 示例2：降序合并 ===");
        
        // list3: 5->3->1
        ListNode<Integer> list3 = ListNode.of(5, 3, 1);
        
        // list4: 6->4->2
        ListNode<Integer> list4 = ListNode.of(6, 4, 2);

        list3.printList("链表3");
        list4.printList("链表4");
        
        // 使用降序比较器
        ListNode<Integer> result = SinglyLinkedListAlgorithms.mergeTwoLists(list3, list4,
            (a, b) -> b.val.compareTo(a.val));
        result.printList("合并后（降序）");
        
        // 验证结果
        assertNotNull(result);
        assertEquals(6, result.val);
        assertEquals(5, result.next.val);
        assertEquals(4, result.next.next.val);
        assertEquals(3, result.next.next.next.val);
        assertEquals(2, result.next.next.next.next.val);
        assertEquals(1, result.next.next.next.next.next.val);
        assertNull(result.next.next.next.next.next.next);
    }
    
    @Test
    public void testBoundaryConditions() {
        System.out.println("\n=== 示例3：边界情况测试 ===");
        
        // 测试空链表
        ListNode<Integer> list5 = null;
        ListNode<Integer> list6 = ListNode.of(1);

        ListNode.printList("空链表", list5);
        list6.printList("非空链表");
        
        ListNode<Integer> result = SinglyLinkedListAlgorithms.mergeTwoLists(list5, list6);
        result.printList("合并后");
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.val);
        assertNull(result.next);
        
        // 测试两个空链表
        ListNode<Integer> result2 = SinglyLinkedListAlgorithms.mergeTwoLists(null, null);
        assertNull(result2);
    }
    
    @Test
    public void testStringListMerge() {
        System.out.println("\n=== 示例4：字符串链表合并 ===");
        
        // list7: "apple" -> "dog" -> "zebra"
        ListNode<String> list7 = ListNode.of("apple", "dog", "zebra");
        
        // list8: "bear" -> "cat" -> "elephant"
        ListNode<String> list8 = ListNode.of("bear", "cat", "elephant");

        list7.printList("字符串链表1");
        list8.printList("字符串链表2");
        
        ListNode<String> result = SinglyLinkedListAlgorithms.mergeTwoLists(list7, list8);
        result.printList("合并后（字母序）");
        
        // 验证结果
        assertNotNull(result);
        assertEquals("apple", result.val);
        assertEquals("bear", result.next.val);
        assertEquals("cat", result.next.next.val);
        assertEquals("dog", result.next.next.next.val);
        assertEquals("elephant", result.next.next.next.next.val);
        assertEquals("zebra", result.next.next.next.next.next.val);
        assertNull(result.next.next.next.next.next.next);
    }
    
    @Test
    public void testCustomComparator() {
        System.out.println("\n=== 测试自定义比较器 ===");
        
        // 测试按长度排序的字符串链表
        ListNode<String> list1 = ListNode.of("a", "abc", "abcde");
        ListNode<String> list2 = ListNode.of("bb", "dddd");
        
        list1.printList("链表1（按长度）");
        list2.printList("链表2（按长度）");
        
        // 使用长度比较器
        ListNode<String> result = SinglyLinkedListAlgorithms.mergeTwoLists(list1, list2,
            Comparator.comparingInt(node -> node.val.length()));
        result.printList("合并后（按长度排序）");
        
        // 验证结果
        assertNotNull(result);
        assertEquals("a", result.val);
        assertEquals("bb", result.next.val);
        assertEquals("abc", result.next.next.val);
        assertEquals("dddd", result.next.next.next.val);
        assertEquals("abcde", result.next.next.next.next.val);
    }
}
