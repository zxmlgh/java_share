package net.dreampo.java_share.structure_algo;

import net.dreampo.java_share.structure_algo.helper.ParLinkedList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 链表分隔算法测试类
 * 
 * @author richard
 */
public class PartitionListTest {
    
    @Test
    public void testLambdaExpression() {
        System.out.println("=== 演示新的 partitionByPredicate 方法 ===");
        System.out.println("\n示例1：使用 lambda 表达式（val < 3）");
        
        ListNode<Integer> head = ListNode.of(1, 4, 3, 2, 5, 2);
        head.printList("原始链表");
        
        ParLinkedList<Integer> result = SinglyLinkedListAlgorithms.partitionByPredicate(head, val -> val < 3);
        result.getFirstList().printList("满足条件的链表（< 3）");
        result.getSecondList().printList("不满足条件的链表（>= 3）");
        
        // 验证结果
        assertNotNull(result.getFirstList());
        assertNotNull(result.getSecondList());
        
        // 验证第一个链表（< 3）
        ListNode<Integer> first = result.getFirstList();
        assertEquals(1, first.val);
        assertEquals(2, first.next.val);
        assertEquals(2, first.next.next.val);
        assertNull(first.next.next.next);
        
        // 验证第二个链表（>= 3）
        ListNode<Integer> second = result.getSecondList();
        assertEquals(4, second.val);
        assertEquals(3, second.next.val);
        assertEquals(5, second.next.next.val);
        assertNull(second.next.next.next);
    }
    
    @Test
    public void testMethodReference() {
        System.out.println("\n示例2：使用方法引用（长度 <= 3）");
        
        ListNode<String> head = ListNode.of("dog", "cat", "elephant", "bear", "ant");
        head.printList("原始链表");
        
        // 使用方法引用：字符串长度 <= 3
        ParLinkedList<String> result = SinglyLinkedListAlgorithms.partitionByPredicate(
                head,
                str -> str.length() <= 3
        );
        result.getFirstList().printList("短字符串（长度 <= 3）");
        result.getSecondList().printList("长字符串（长度 > 3）");
        
        // 验证结果
        ListNode<String> first = result.getFirstList();
        assertEquals("dog", first.val);
        assertEquals("cat", first.next.val);
        assertEquals("ant", first.next.next.val);
        assertNull(first.next.next.next);
        
        ListNode<String> second = result.getSecondList();
        assertEquals("elephant", second.val);
        assertEquals("bear", second.next.val);
        assertNull(second.next.next);
    }
    
    @Test
    public void testComplexCondition() {
        System.out.println("\n示例3：使用复杂条件（偶数或大于10）");
        
        ListNode<Integer> head = ListNode.of(1, 2, 15, 7, 8, 12);
        head.printList("原始链表");
        
        // 复杂条件：偶数或大于10的数
        ParLinkedList<Integer> result = SinglyLinkedListAlgorithms.partitionByPredicate(
                head,
                val -> val % 2 == 0 || val > 10
        );
        result.getFirstList().printList("偶数或大于10的数");
        result.getSecondList().printList("奇数且小于等于10的数");
        
        // 验证结果
        ListNode<Integer> first = result.getFirstList();
        assertEquals(2, first.val);
        assertEquals(15, first.next.val);
        assertEquals(8, first.next.next.val);
        assertEquals(12, first.next.next.next.val);
        assertNull(first.next.next.next.next);
        
        ListNode<Integer> second = result.getSecondList();
        assertEquals(1, second.val);
        assertEquals(7, second.next.val);
        assertNull(second.next.next);
    }
    
    @Test
    public void testTradingSystemSimulation() {
        System.out.println("\n示例4：模拟交易系统买价队列（价格 >= 100）");
        
        class Order {
            double price;
            String id;

            Order(String id, double price) {
                this.id = id;
                this.price = price;
            }

            @Override
            public String toString() {
                return id + ":" + price;
            }
        }

        ListNode<Order> orderList = ListNode.of(
            new Order("A", 98.5), 
            new Order("B", 101.0), 
            new Order("C", 99.5), 
            new Order("D", 102.5), 
            new Order("E", 100.0)
        );

        orderList.printList("买价队列");
        
        // 按价格分隔：>= 100 的订单
        ParLinkedList<Order> orderResult = SinglyLinkedListAlgorithms.partitionByPredicate(
                orderList,
                order -> order.price >= 100.0
        );
        orderResult.getFirstList().printList("高价订单（>= 100）");
        orderResult.getSecondList().printList("低价订单（< 100）");
        
        // 验证结果
        ListNode<Order> highPrice = orderResult.getFirstList();
        assertEquals("B", highPrice.val.id);
        assertEquals(101.0, highPrice.val.price);
        assertEquals("D", highPrice.next.val.id);
        assertEquals(102.5, highPrice.next.val.price);
        assertEquals("E", highPrice.next.next.val.id);
        assertEquals(100.0, highPrice.next.next.val.price);
        
        ListNode<Order> lowPrice = orderResult.getSecondList();
        assertEquals("A", lowPrice.val.id);
        assertEquals(98.5, lowPrice.val.price);
        assertEquals("C", lowPrice.next.val.id);
        assertEquals(99.5, lowPrice.next.val.price);
    }
    
    @Test
    public void testBackwardCompatibility() {
        System.out.println("\n示例5：测试向后兼容的 partitionToTwo 方法");
        
        ListNode<Integer> head = ListNode.of(2, 1, 3);
        head.printList("原始链表");
        
        ParLinkedList<Integer> result = SinglyLinkedListAlgorithms.partitionByPredicate(head, val -> val < 2);
        result.getFirstList().printList("小于2的链表");
        result.getSecondList().printList("大于等于2的链表");
        
        // 验证结果
        ListNode<Integer> first = result.getFirstList();
        assertEquals(1, first.val);
        assertNull(first.next);
        
        ListNode<Integer> second = result.getSecondList();
        assertEquals(2, second.val);
        assertEquals(3, second.next.val);
        assertNull(second.next.next);
    }
    
    @Test
    public void testBoundaryConditions() {
        System.out.println("\n边界条件测试");
        
        // 空链表
        ParLinkedList<Integer> result1 = SinglyLinkedListAlgorithms.partitionByPredicate(null, val -> val < 5);
        assertNull(result1.getFirstList());
        assertNull(result1.getSecondList());
        
        // 单节点链表
        ListNode<Integer> single = ListNode.of(5);
        ParLinkedList<Integer> result2 = SinglyLinkedListAlgorithms.partitionByPredicate(single, val -> val < 5);
        assertNull(result2.getFirstList());
        assertEquals(5, result2.getSecondList().val);
        
        // 所有元素都满足条件
        ListNode<Integer> allMatch = ListNode.of(1, 2, 3);
        ParLinkedList<Integer> result3 = SinglyLinkedListAlgorithms.partitionByPredicate(allMatch, val -> val < 10);
        assertNotNull(result3.getFirstList());
        assertNull(result3.getSecondList());
        
        // 所有元素都不满足条件
        ListNode<Integer> noneMatch = ListNode.of(10, 20, 30);
        ParLinkedList<Integer> result4 = SinglyLinkedListAlgorithms.partitionByPredicate(noneMatch, val -> val < 5);
        assertNull(result4.getFirstList());
        assertNotNull(result4.getSecondList());
    }
}
