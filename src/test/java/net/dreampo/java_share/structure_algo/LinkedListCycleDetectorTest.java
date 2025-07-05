package net.dreampo.java_share.structure_algo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 链表环检测算法测试类
 * 
 * @author richard
 */
public class LinkedListCycleDetectorTest {
    
    /**
     * 辅助方法：创建带环的测试链表
     * 
     * @param values 节点值数组
     * @param cyclePos 环的连接位置（-1表示无环）
     * @return 构建的链表头节点
     */
    private ListNode<Integer> createListWithCycle(int[] values, int cyclePos) {
        if (values.length == 0) return null;

        ListNode<Integer> dummy = new ListNode<>(0);
        ListNode<Integer> current = dummy;
        ListNode<Integer> cycleNode = null;

        for (int i = 0; i < values.length; i++) {
            //为遍历的节点创建后继节点
            current.next = new ListNode<>(values[i]);
            //后继节点作为下轮遍历的节点对象
            current = current.next;
            if (i == cyclePos) {
                //创建环的入口节点
                cycleNode = current;
            }
        }

        // 创建环:尾节点，指向环入口节点
        if (cyclePos >= 0 && cyclePos < values.length) {
            current.next = cycleNode;
        }

        return dummy.next;
    }
    
    @Test
    public void testNoCycle() {
        System.out.println("测试1：无环链表");
        ListNode<Integer> list = createListWithCycle(new int[]{1, 2, 3, 4, 5}, -1);
        list.printList("链表结构");
        
        assertFalse(list.hasCycle());
        assertNull(list.detectCycle());
    }
    
    @Test
    public void testWholeCycle() {
        System.out.println("测试2：整个链表是环（环入口是头节点）");
        ListNode<Integer> list = createListWithCycle(new int[]{1, 2, 3, 4, 5}, 0);
        list.printList("链表结构");
        
        assertTrue(list.hasCycle());
        ListNode<Integer> entry = list.detectCycle();
        assertNotNull(entry);
        assertEquals(1, entry.val);
        assertEquals(5, list.getCycleLength());
    }
    
    @Test
    public void testMiddleCycle() {
        System.out.println("测试3：环在链表中间（位置2）");
        ListNode<Integer> list = createListWithCycle(new int[]{1, 2, 3, 4, 5, 6}, 2);
        list.printList("链表结构");
        
        assertTrue(list.hasCycle());
        ListNode<Integer> entry = list.detectCycle();
        assertNotNull(entry);
        assertEquals(3, entry.val);
        assertEquals(4, list.getCycleLength());
    }
    
    @Test
    public void testSelfCycle() {
        System.out.println("测试4：链表末尾自环");
        ListNode<Integer> list = createListWithCycle(new int[]{1, 2, 3, 4, 5}, 4);
        list.printList("链表结构");
        
        assertTrue(list.hasCycle());
        ListNode<Integer> entry = list.detectCycle();
        assertNotNull(entry);
        assertEquals(5, entry.val);
        assertEquals(1, list.getCycleLength());
    }
    
    @Test
    public void testSingleNodeNoCycle() {
        System.out.println("测试5：单节点无环");
        ListNode<Integer> list = createListWithCycle(new int[]{1}, -1);
        list.printList("链表结构");
        
        assertFalse(list.hasCycle());
        assertNull(list.detectCycle());
    }
    
    @Test
    public void testSingleNodeSelfCycle() {
        System.out.println("测试6：单节点自环");
        ListNode<Integer> list = createListWithCycle(new int[]{1}, 0);
        list.printList("链表结构");
        
        assertTrue(list.hasCycle());
        ListNode<Integer> entry = list.detectCycle();
        assertNotNull(entry);
        assertEquals(1, entry.val);
        assertEquals(1, list.getCycleLength());
    }
    
    @Test
    public void testEmptyList() {
        System.out.println("测试7：空链表");
        ListNode<Integer> list = null;
        
        assertFalse(SinglyLinkedListAlgorithms.hasCycle(list));
        assertNull(SinglyLinkedListAlgorithms.detectCycle(list));
    }
    
    @Test
    public void testComplexScenario() {
        System.out.println("测试8：复杂场景（100个节点，环从位置30开始）");
        int[] largeArray = new int[100];
        for (int i = 0; i < 100; i++) {
            largeArray[i] = i + 1;
        }
        ListNode<Integer> list = createListWithCycle(largeArray, 30);
        
        System.out.println("链表总节点数: 100");
        assertTrue(list.hasCycle());
        
        ListNode<Integer> entry = list.detectCycle();
        assertNotNull(entry);
        assertEquals(31, entry.val);
        assertEquals(70, list.getCycleLength());
        System.out.println("环入口节点值: " + entry.val);
        System.out.println("环的长度: " + list.getCycleLength());
        System.out.println("非环部分长度: 31");
    }
    
    @Test
    public void testPerformance() {
        System.out.println("测试9：性能测试（10000个节点）");
        int[] hugeArray = new int[10000];
        for (int i = 0; i < 10000; i++) {
            hugeArray[i] = i + 1;
        }
        
        // 无环情况
        ListNode<Integer> hugeList1 = createListWithCycle(hugeArray, -1);
        long start1 = System.nanoTime();
        boolean hasCycle1 = hugeList1.hasCycle();
        long end1 = System.nanoTime();
        System.out.println("无环检测耗时: " + (end1 - start1) / 1000000.0 + " ms");
        assertFalse(hasCycle1);
        
        // 有环情况
        ListNode<Integer> hugeList2 = createListWithCycle(hugeArray, 5000);
        long start2 = System.nanoTime();
        ListNode<Integer> entry = hugeList2.detectCycle();
        long end2 = System.nanoTime();
        System.out.println("有环检测耗时: " + (end2 - start2) / 1000000.0 + " ms");
        assertNotNull(entry);
        assertEquals(5001, entry.val);
        System.out.println("环入口位置: " + entry.val);
    }
}
