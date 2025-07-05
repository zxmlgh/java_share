package net.dreampo.java_share.structure_algo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 链表每K个节点一组反转算法测试类
 * 
 * @author richard
 */
public class ReverseKGroupTest {
    
    @Test
    public void testBasicKGroupReversal() {
        System.out.println("测试1：固定大小K=3反转（头插法）");
        ListNode<Integer> list = ListNode.of(1, 2, 3, 4, 5, 6, 7, 8);
        list.printList("原始链表");
        
        ListNode<Integer> result = SinglyLinkedListAlgorithms.reverseKGroup(list, 3);
        result.printList("反转结果");
        System.out.println("说明：[1,2,3]→[3,2,1], [4,5,6]→[6,5,4], [7,8]不足K个保持原样\n");
        
        // 验证结果
        assertNotNull(result);
        assertEquals(3, result.val);
        assertEquals(2, result.next.val);
        assertEquals(1, result.next.next.val);
        assertEquals(6, result.next.next.next.val);
        assertEquals(5, result.next.next.next.next.val);
        assertEquals(4, result.next.next.next.next.next.val);
        assertEquals(8, result.next.next.next.next.next.next.val);
        assertEquals(7, result.next.next.next.next.next.next.next.val);
        assertNull(result.next.next.next.next.next.next.next.next);
    }
    
    @Test
    public void testThreePointersMethod() {
        System.out.println("测试2：固定大小K=3反转（三变量法）");
        ListNode<Integer> list = ListNode.of(1, 2, 3, 4, 5, 6, 7, 8);
        list.printList("原始链表");
        
        ListNode<Integer> result = SinglyLinkedListAlgorithms.reverseKGroupByThreePointers(
            list,
            (pos, val) -> (pos - 1) % 3 == 0,
            (pos, val) -> (pos - 1) % 3 == 0 && pos > 1
        );
        result.printList("反转结果");
        System.out.println("说明：使用三变量法实现相同功能\n");
        
        // 验证结果与头插法相同
        assertNotNull(result);
        assertEquals(3, result.val);
        assertEquals(2, result.next.val);
        assertEquals(1, result.next.next.val);
        assertEquals(6, result.next.next.next.val);
        assertEquals(5, result.next.next.next.next.val);
        assertEquals(4, result.next.next.next.next.next.val);
        assertEquals(8, result.next.next.next.next.next.next.val);
        assertEquals(7, result.next.next.next.next.next.next.next.val);
    }
    
    @Test
    public void testGlobalReversal() {
        System.out.println("测试3：全局反转");
        ListNode<Integer> list = ListNode.of(1, 2, 3, 4, 5);
        list.printList("原始链表");
        
        ListNode<Integer> result = SinglyLinkedListAlgorithms.reverseAll(list);
        result.printList("反转结果");
        System.out.println("说明：整个链表作为一组进行反转\n");
        
        // 验证结果
        assertNotNull(result);
        assertEquals(5, result.val);
        assertEquals(4, result.next.val);
        assertEquals(3, result.next.next.val);
        assertEquals(2, result.next.next.next.val);
        assertEquals(1, result.next.next.next.next.val);
        assertNull(result.next.next.next.next.next);
    }
    
    @Test
    public void testConditionalReversal() {
        System.out.println("测试4：条件反转（值>=5的连续节点）");
        ListNode<Integer> list = ListNode.of(1, 2, 6, 7, 8, 3, 4, 9, 10, 2);
        list.printList("原始链表");
        
        ListNode<Integer> result = SinglyLinkedListAlgorithms.reverseByCondition(list, 5, true);
        result.printList("反转结果");
        System.out.println("说明：[6,7,8]和[9,10]满足>=5条件，分别被反转\n");
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.val);
        assertEquals(2, result.next.val);
        assertEquals(8, result.next.next.val);
        assertEquals(7, result.next.next.next.val);
        assertEquals(6, result.next.next.next.next.val);
        assertEquals(3, result.next.next.next.next.next.val);
        assertEquals(4, result.next.next.next.next.next.next.val);
        assertEquals(10, result.next.next.next.next.next.next.next.val);
        assertEquals(9, result.next.next.next.next.next.next.next.next.val);
        assertEquals(2, result.next.next.next.next.next.next.next.next.next.val);
    }
    
    @Test
    public void testCustomPositionReversal() {
        System.out.println("测试5：自定义规则（反转位置3-5）");
        ListNode<Integer> list = ListNode.of(1, 2, 3, 4, 5, 6, 7);
        list.printList("原始链表");
        
        ListNode<Integer> result = SinglyLinkedListAlgorithms.reverseKGroupByThreePointers(
            list,
            (pos, val) -> pos == 3,  // 从位置3开始
            (pos, val) -> pos > 5    // 位置大于5时退出
        );
        result.printList("反转结果");
        System.out.println("说明：位置3-5的节点[3,4,5]被反转为[5,4,3]\n");
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.val);
        assertEquals(2, result.next.val);
        assertEquals(5, result.next.next.val);
        assertEquals(4, result.next.next.next.val);
        assertEquals(3, result.next.next.next.next.val);
        assertEquals(6, result.next.next.next.next.next.val);
        assertEquals(7, result.next.next.next.next.next.next.val);
    }
    
    @Test
    public void testBoundaryConditions() {
        System.out.println("测试6：边界情况测试");
        
        // 单节点
        ListNode<Integer> single = ListNode.of(1);
        single.printList("单节点");
        ListNode<Integer> result1 = SinglyLinkedListAlgorithms.reverseKGroup(single, 3);
        result1.printList("反转后");
        assertEquals(1, result1.val);
        assertNull(result1.next);
        
        // K=1（不反转）
        ListNode<Integer> noReverse = ListNode.of(1, 2, 3, 4);
        noReverse.printList("K=1链表");
        ListNode<Integer> result2 = SinglyLinkedListAlgorithms.reverseKGroup(noReverse, 1);
        result2.printList("反转后");
        assertEquals(1, result2.val);
        assertEquals(2, result2.next.val);
        assertEquals(3, result2.next.next.val);
        assertEquals(4, result2.next.next.next.val);
        
        // K大于链表长度
        ListNode<Integer> smallList = ListNode.of(1, 2, 3);
        smallList.printList("K>长度");
        ListNode<Integer> result3 = SinglyLinkedListAlgorithms.reverseKGroup(smallList, 5);
        result3.printList("K=5反转");
        assertEquals(3, result3.val);
        assertEquals(2, result3.next.val);
        assertEquals(1, result3.next.next.val);
        System.out.println("说明：K大于链表长度时，不足一组，保持原样\n");
    }
    
    @Test
    public void testAlgorithmCorrectness() {
        System.out.println("测试7：算法正确性验证");
        
        // K=2反转
        ListNode<Integer> test1 = ListNode.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        test1.printList("原始链表");
        ListNode<Integer> k2Result = SinglyLinkedListAlgorithms.reverseKGroup(test1, 2);
        k2Result.printList("K=2反转");
        System.out.println("期望结果：2->1->4->3->6->5->8->7->10->9");
        
        // 验证K=2的结果
        assertEquals(2, k2Result.val);
        assertEquals(1, k2Result.next.val);
        assertEquals(4, k2Result.next.next.val);
        assertEquals(3, k2Result.next.next.next.val);
        assertEquals(6, k2Result.next.next.next.next.val);
        assertEquals(5, k2Result.next.next.next.next.next.val);
        assertEquals(8, k2Result.next.next.next.next.next.next.val);
        assertEquals(7, k2Result.next.next.next.next.next.next.next.val);
        assertEquals(10, k2Result.next.next.next.next.next.next.next.next.val);
        assertEquals(9, k2Result.next.next.next.next.next.next.next.next.next.val);
        
        // K=4反转
        ListNode<Integer> test2 = ListNode.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        ListNode<Integer> k4Result = SinglyLinkedListAlgorithms.reverseKGroup(test2, 4);
        k4Result.printList("K=4反转");
        System.out.println("期望结果：4->3->2->1->8->7->6->5->9->10\n");
        
        // 验证K=4的结果
        assertEquals(4, k4Result.val);
        assertEquals(3, k4Result.next.val);
        assertEquals(2, k4Result.next.next.val);
        assertEquals(1, k4Result.next.next.next.val);
        assertEquals(8, k4Result.next.next.next.next.val);
        assertEquals(7, k4Result.next.next.next.next.next.val);
        assertEquals(6, k4Result.next.next.next.next.next.next.val);
        assertEquals(5, k4Result.next.next.next.next.next.next.next.val);
        assertEquals(10, k4Result.next.next.next.next.next.next.next.next.val);
        assertEquals(9, k4Result.next.next.next.next.next.next.next.next.next.val);
    }
    
    @Test
    public void testPerformance() {

        Integer[] largeArray = new Integer[100000];
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = i + 1;
        }
        System.out.println(STR."测试8：性能测试（\{largeArray.length}个节点，K=100）");

        // 测试头插法
        ListNode<Integer> largeList1 = ListNode.of(largeArray);
        long start1 = System.nanoTime();
        ListNode<Integer> result1 = SinglyLinkedListAlgorithms.reverseKGroup(largeList1, 100);
        long end1 = System.nanoTime();
        System.out.println("头插法耗时: " + (end1 - start1) / 1000000.0 + " ms");
        
        // 验证第一组是否正确反转
        assertEquals(100, result1.val);
        assertEquals(99, result1.next.val);
        assertEquals(98, result1.next.next.val);

        // 测试三变量法
        ListNode<Integer> largeList2 = ListNode.of(largeArray);
        long start2 = System.nanoTime();
        ListNode<Integer> result2 = SinglyLinkedListAlgorithms.reverseKGroupByThreePointers(
            largeList2,
            (pos, val) -> (pos - 1) % 100 == 0,
            (pos, val) -> (pos - 1) % 100 == 0 && pos > 1
        );
        long end2 = System.nanoTime();
        System.out.println("三变量法耗时: " + (end2 - start2) / 1000000.0 + " ms");
        
        // 验证第一组是否正确反转
        assertEquals(100, result2.val);
        assertEquals(99, result2.next.val);
        assertEquals(98, result2.next.next.val);
        
        System.out.println("\n所有测试完成！");
    }
    
    @Test
    public void testNullAndEmpty() {
        System.out.println("测试9：空链表和null测试");
        
        // null链表
        ListNode<Integer> nullList = null;
        ListNode<Integer> result1 = SinglyLinkedListAlgorithms.reverseKGroup(nullList, 3);
        assertNull(result1);
        
        // 空链表
        ListNode<Integer> emptyList = ListNode.of(new Integer[]{});
        ListNode<Integer> result2 = SinglyLinkedListAlgorithms.reverseKGroup(emptyList, 3);
        assertNull(result2);
    }
    
    @Test
    public void testConditionalReversalVariations() {
        System.out.println("测试10：条件反转的其他变体");
        
        // 反转小于阈值的连续节点
        ListNode<Integer> list = ListNode.of(8, 9, 2, 3, 1, 7, 6, 4, 5, 10);
        list.printList("原始链表");
        
        ListNode<Integer> result = SinglyLinkedListAlgorithms.reverseByCondition(list, 5, false);
        result.printList("反转结果（<5的连续节点）");
        System.out.println("说明：[2,3,1]和[4]满足<5条件，分别被反转\n");
        
        // 验证结果
        assertNotNull(result);
        assertEquals(8, result.val);
        assertEquals(9, result.next.val);
        assertEquals(1, result.next.next.val);
        assertEquals(3, result.next.next.next.val);
        assertEquals(2, result.next.next.next.next.val);
        assertEquals(7, result.next.next.next.next.next.val);
        assertEquals(6, result.next.next.next.next.next.next.val);
        assertEquals(4, result.next.next.next.next.next.next.next.val);
        assertEquals(5, result.next.next.next.next.next.next.next.next.val);
        assertEquals(10, result.next.next.next.next.next.next.next.next.next.val);
    }
}
