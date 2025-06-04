package net.dreampo.java_share.structure_algo;


import static java.lang.System.out;

/**
 * 通用双向链表实现
 * @param <T> 存储的数据类型
 */
public class DoublyLinkedList<T> {
    // 双向链表节点
    public class Node {
        T data;
        Node prev;
        Node next;

        public Node(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }

    private Node head, tail;  // 虚拟头尾节点
    private int size;         // 链表大小

    public DoublyLinkedList() {
        // 初始化虚拟头尾节点（简化边界处理）
        head = new Node(null);
        tail = new Node(null);
        head.next = tail;
        tail.prev = head;
        size = 0;
    }

    /**
     * 在链表头部添加节点
     */
    public void addToHead(Node node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
        size++;
    }

    /**
     * 在链表尾部添加节点
     */
    public void addToTail(Node node) {
        node.next = tail;
        node.prev = tail.prev;
        tail.prev.next = node;
        tail.prev = node;
        size++;
    }

    /**
     * 提供数据入队操作（添加到尾部）
     */
    public void offer(T data) {
        Node newNode = new Node(data);
        addToTail(newNode);
    }

    /**
     * 在链表头部添加数据
     */
    public Node addFirst(T data) {
        Node node = new Node(data);
        addToHead(node);
        return node;
    }

    /**
     * 删除指定节点
     */
    public void removeNode(Node node) {
        if (node == null || node == head || node == tail) return;

        node.prev.next = node.next;
        node.next.prev = node.prev;
        size--;
    }

    /**
     * 将节点移动到链表头部
     */
    public void moveToHead(Node node) {
        removeNode(node);
        addToHead(node);
    }

    /**
     * 删除并返回链表尾部节点
     */
    public Node removeTail() {
        if (isEmpty()) return null;

        Node res = tail.prev;
        removeNode(res);
        return res;
    }

    /**
     * 删除并返回链表尾部节点
     */
    public Node removeHead() {
        if (isEmpty()) return null;

        Node res = head.next;
        removeNode(res);
        return res;
    }

    /**
     * 获取链表大小
     */
    public int size() {
        return size;
    }

    /**
     * 判断链表是否为空
     */
    public boolean isEmpty() {
        return head.next == tail;
    }

    /**
     * 创建新节点
     */
    public Node createNode(T data) {
        return new Node(data);
    }

    /**
     * 清空链表
     */
    public void clear() {
        head.next = tail;
        tail.prev = head;
        size = 0;
    }

    /**
     * 以字符串形式显示双向链表的内容
     * 格式: size n: head<-->data1<-->data2<-->...<-->tail
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("size ").append(size).append(": ");

        // 从头节点开始
        sb.append("head");

        // 遍历所有实际节点
        Node current = head.next;
        while (current != tail) {
            sb.append("<-->");
            sb.append(current.data);
            current = current.next;
        }

        // 添加尾节点
        sb.append("<-->tail");

        return sb.toString();
    }

    /**
     * 打印双向链表内容（便于调试）
     */
    public void print(String tag) {
        if(tag==null||tag.isBlank())
            System.out.println(toString());
        else{
            tag =tag.trim();
            out.println(STR."\{tag}\{toString()}");
        }
    }

    /**
     * 打印双向链表内容（便于调试）
     */
    public void print() {
        this.print("");
    }
}
