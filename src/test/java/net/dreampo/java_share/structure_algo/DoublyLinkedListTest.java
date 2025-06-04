package net.dreampo.java_share.structure_algo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DoublyLinkedList的单元测试
 */
@DisplayName("双向链表测试")
class DoublyLinkedListTest {

    private DoublyLinkedList<Integer> intList;
    private DoublyLinkedList<String> stringList;

    @BeforeEach
    void setUp() {
        intList = new DoublyLinkedList<>();
        stringList = new DoublyLinkedList<>();
    }

    @Nested
    @DisplayName("基本操作测试")
    class BasicOperationsTest {

        @Test
        @DisplayName("新建链表应该为空")
        void testNewListIsEmpty() {
            assertTrue(intList.isEmpty());
            assertEquals(0, intList.size());
        }

        @Test
        @DisplayName("添加节点到头部")
        void testAddToHead() {
            DoublyLinkedList<Integer>.Node node1 = intList.addFirst(10);
            DoublyLinkedList<Integer>.Node node2 = intList.addFirst(20);
            DoublyLinkedList<Integer>.Node node3 = intList.addFirst(30);

            assertEquals(3, intList.size());
            assertFalse(intList.isEmpty());
            /*assertNotNull(node1);
            assertNotNull(node2);
            assertNotNull(node3);
            assertEquals(10, node1.getData());
            assertEquals(20, node2.getData());
            assertEquals(30, node3.getData());*/
            intList.print();
        }

        @Test
        @DisplayName("创建节点")
        void testCreateNode() {
            DoublyLinkedList<String>.Node node = stringList.createNode("test");
            assertNotNull(node);
            assertEquals("test", node.getData());
            assertEquals(0, stringList.size()); // 创建节点不会自动添加到链表
        }

        @Test
        @DisplayName("删除节点")
        void testRemoveNode() {
            DoublyLinkedList<Integer>.Node node1 = intList.addFirst(10);
            DoublyLinkedList<Integer>.Node node2 = intList.addFirst(20);
            DoublyLinkedList<Integer>.Node node3 = intList.addFirst(30);

            intList.removeNode(node2);
            assertEquals(2, intList.size());

            intList.removeNode(node1);
            assertEquals(1, intList.size());

            intList.removeNode(node3);
            assertEquals(0, intList.size());
            assertTrue(intList.isEmpty());
        }
    }

    @Nested
    @DisplayName("链表操作测试")
    class ListOperationsTest {

        @Test
        @DisplayName("移动节点到头部")
        void testMoveToHead() {
            DoublyLinkedList<Integer>.Node node1 = intList.addFirst(10);
            DoublyLinkedList<Integer>.Node node2 = intList.addFirst(20);
            DoublyLinkedList<Integer>.Node node3 = intList.addFirst(30);
            DoublyLinkedList<Integer>.Node node4 = intList.addFirst(40);

            String node1OptDesc = STR."intList.moveToHead(\{node1.getData()})";
            intList.print(STR."\{node1OptDesc}操作前\n\t链表的数据是：");
            // 移动node1（最后添加的）到头部
            intList.moveToHead(node1);
            intList.print(STR."\{node1OptDesc}操作后\n\t链表的数据是：");
            assertEquals(4, intList.size()); // 大小不变

            // 验证node1确实在头部（通过删除尾部来验证）
            DoublyLinkedList<Integer>.Node tail1 = intList.removeTail();
            String removeOpeDesc =STR."remove the node of\{tail1.getData()}";
            assertEquals(20, tail1.getData()); // node2变成了尾部
            intList.print(STR."\{removeOpeDesc}，操作后\n\t链表的数据是：");

            DoublyLinkedList<Integer>.Node tail2 = intList.removeTail();
            removeOpeDesc =STR."remove the node of \{tail2.getData()}";
            assertEquals(30, tail2.getData()); // node3是倒数第二个
            assertEquals(2, intList.size());
            intList.print(STR."\{removeOpeDesc}，操作后\n\t链表的数据是：");

            DoublyLinkedList<Integer>.Node head = intList.removeHead();
            removeOpeDesc = STR."remove the head of \{head.getData()}";
            intList.print(STR."\{removeOpeDesc}，操作后\n\t链表的数据是：");
        }

        @Test
        @DisplayName("删除尾部节点")
        void testRemoveTail() {
            intList.addFirst(10);
            intList.addFirst(20);
            intList.addFirst(30);

            DoublyLinkedList<Integer>.Node tail1 = intList.removeTail();
            assertEquals(10, tail1.getData());
            assertEquals(2, intList.size());

            DoublyLinkedList<Integer>.Node tail2 = intList.removeTail();
            assertEquals(20, tail2.getData());
            assertEquals(1, intList.size());

            DoublyLinkedList<Integer>.Node tail3 = intList.removeTail();
            assertEquals(30, tail3.getData());
            assertEquals(0, intList.size());
            assertTrue(intList.isEmpty());
        }

        @Test
        @DisplayName("从空链表删除尾部返回null")
        void testRemoveTailFromEmptyList() {
            assertNull(intList.removeTail());
        }

        @Test
        @DisplayName("清空链表")
        void testClear() {
            intList.addFirst(10);
            intList.addFirst(20);
            intList.addFirst(30);

            assertEquals(3, intList.size());
            assertFalse(intList.isEmpty());

            intList.clear();

            assertEquals(0, intList.size());
            assertTrue(intList.isEmpty());
            assertNull(intList.removeTail());
        }
    }

    @Nested
    @DisplayName("不同类型数据测试")
    class DifferentTypesTest {

        @Test
        @DisplayName("存储字符串")
        void testStringList() {
            stringList.addFirst("first");
            stringList.addFirst("second");
            stringList.addFirst("third");

            assertEquals(3, stringList.size());

            DoublyLinkedList<String>.Node tail = stringList.removeTail();
            assertEquals("first", tail.getData());
        }

        @Test
        @DisplayName("存储自定义对象")
        void testCustomObjectList() {
            class Person {
                String name;
                int age;

                Person(String name, int age) {
                    this.name = name;
                    this.age = age;
                }
            }

            DoublyLinkedList<Person> personList = new DoublyLinkedList<>();

            Person p1 = new Person("Alice", 25);
            Person p2 = new Person("Bob", 30);
            Person p3 = new Person("Charlie", 35);

            personList.addFirst(p1);
            personList.addFirst(p2);
            personList.addFirst(p3);

            assertEquals(3, personList.size());

            DoublyLinkedList<Person>.Node tailNode = personList.removeTail();
            Person tailPerson = tailNode.getData();
            assertEquals("Alice", tailPerson.name);
            assertEquals(25, tailPerson.age);
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class EdgeCaseTest {

        @Test
        @DisplayName("删除null节点")
        void testRemoveNullNode() {
            intList.addFirst(10);
            int sizeBefore = intList.size();

            intList.removeNode(null);
            assertEquals(sizeBefore, intList.size()); // 大小不变
        }

        @Test
        @DisplayName("节点数据的获取和设置")
        void testNodeDataGetterSetter() {
            DoublyLinkedList<String>.Node node = stringList.createNode("initial");
            assertEquals("initial", node.getData());

            node.setData("updated");
            assertEquals("updated", node.getData());
        }

        @Test
        @DisplayName("大量数据操作")
        void testLargeDataSet() {
            // 添加1000个元素
            for (int i = 0; i < 1000; i++) {
                intList.addFirst(i);
            }

            assertEquals(1000, intList.size());

            // 删除500个元素
            for (int i = 0; i < 500; i++) {
                intList.removeTail();
            }

            assertEquals(500, intList.size());

            // 清空
            intList.clear();
            assertTrue(intList.isEmpty());
        }
    }
}