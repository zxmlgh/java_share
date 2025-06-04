package net.dreampo.java_share.lru_algo;

import net.dreampo.java_share.structure_algo.DoublyLinkedList;

import java.util.HashMap;

/**
 * LRU缓存实现
 * 使用双向链表维护访问顺序，HashMap提供O(1)查找
 * @param <K> 键类型
 * @param <V> 值类型
 */
public class LRUCache<K, V> {

    private HashMap<K, DoublyLinkedList<CacheEntry>.Node> cache;  // 哈希表存储键到节点的映射
    private DoublyLinkedList<CacheEntry> linkedList;              // 双向链表维护访问顺序
    private int capacity;                                          // 缓存容量

    // 缓存项，封装键值对
    private class CacheEntry {
        K key;
        V value;

        public CacheEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.linkedList = new DoublyLinkedList<>();
    }

    /**
     * 获取缓存值
     * 如果存在，将其移动到链表头部（表示最近使用）
     */
    public V get(K key) {
        DoublyLinkedList<CacheEntry>.Node node = cache.get(key);
        if (node == null) return null;

        // 将访问节点移动到链表头部（表示最近使用）
        linkedList.moveToHead(node);
        return node.getData().value;
    }

    /**
     * 设置缓存值
     * 如果已存在则更新，否则添加新节点
     * 超出容量时删除最久未使用的节点
     */
    public void put(K key, V value) {
        DoublyLinkedList<CacheEntry>.Node node = cache.get(key);

        if (node == null) { // 新节点
            CacheEntry entry = new CacheEntry(key, value);
            node = linkedList.createNode(entry);
            cache.put(key, node);
            linkedList.addToHead(node);  // 添加到链表头部

            if (linkedList.size() > capacity) { // 容量超出，删除尾部节点
                DoublyLinkedList<CacheEntry>.Node removed
                        = linkedList.removeTail();
                if (removed != null) {
                    cache.remove(removed.getData().key);
                }
            }
        } else { // 已存在节点
            node.getData().value = value;
            linkedList.moveToHead(node); // 更新访问时间
        }
    }

    /**
     * 删除指定键的缓存
     */
    public V remove(K key) {
        DoublyLinkedList<CacheEntry>.Node node = cache.get(key);
        if (node == null) return null;

        V value = node.getData().value;
        linkedList.removeNode(node);
        cache.remove(key);
        return value;
    }

    /**
     * 检查是否包含指定键
     */
    public boolean containsKey(K key) {
        return cache.containsKey(key);
    }

    /**
     * 获取当前缓存大小
     */
    public int size() {
        return linkedList.size();
    }

    /**
     * 获取缓存容量
     */
    public int capacity() {
        return capacity;
    }

    /**
     * 清空缓存
     */
    public void clear() {
        cache.clear();
        linkedList.clear();
    }

    /**
     * 判断缓存是否为空
     */
    public boolean isEmpty() {
        return linkedList.isEmpty();
    }
}
