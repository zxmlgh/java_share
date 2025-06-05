package net.dreampo.java_share.lru_algo;

/**
 * LRU-K算法演示类
 * 
 * 展示LRU-K相比传统LRU的优势：
 * 1. 避免"扫描抵抗性"问题 - 大量一次性访问不会冲掉热数据
 * 2. 提供更精确的热度判断 - 只有频繁访问的数据才获得长期保护
 * 3. 在实际场景中的应用效果
 */
public class LRUKDemo {

    public static void main(String[] args) {
        System.out.println("=== LRU-K算法演示 ===\n");
        
        // 演示1：基础使用
        demonstrateBasicUsage();
        
        // 演示2：对比传统LRU的"扫描抵抗性"
        demonstrateScanResistance();
        
        // 演示3：热数据保护机制
        demonstrateHotDataProtection();
        
        // 演示4：不同K值的影响
        demonstrateDifferentKValues();
        
        // 演示5：实际业务场景模拟
        demonstrateRealWorldScenario();
    }

    /**
     * 演示1：LRU-K的基础使用
     */
    private static void demonstrateBasicUsage() {
        System.out.println("📚 演示1：LRU-K基础使用");
        System.out.println("创建容量为4，K=2的缓存...\n");
        
        LRUKCache<String, String> cache = new LRUKCache<>(4, 2);
        
        // 添加数据
        cache.put("doc1", "Java基础教程");
        cache.put("doc2", "Spring框架指南");
        cache.put("doc3", "数据库优化");
        
        System.out.println("添加3个文档后：");
        printCacheStatus(cache);
        
        // 访问数据，观察队列变化
        System.out.println("\n📖 用户访问doc1（第2次访问）:");
        cache.get("doc1");
        printCacheStatus(cache);
        
        System.out.println("\n📖 用户访问doc2（第2次访问）:");
        cache.get("doc2");
        printCacheStatus(cache);
        
        System.out.println("✅ 可以看到，doc1和doc2达到K=2次访问后，被提升到高优先级缓存队列\n");
        System.out.println("" + "=".repeat(60) + "\n");
    }

    /**
     * 演示2：扫描抵抗性 - LRU-K vs 传统LRU
     */
    private static void demonstrateScanResistance() {
        System.out.println("🛡️ 演示2：扫描抵抗性对比");
        System.out.println("模拟场景：有热数据正在被频繁访问，突然来了大量一次性扫描请求\n");
        
        LRUKCache<String, String> lruKCache = new LRUKCache<>(3, 2);
        LRUCache<String, String> lruCache = new LRUCache<>(3);
        
        // 建立热数据
        System.out.println("🔥 建立热数据：");
        lruKCache.put("hotData1", "热点数据1");
        lruKCache.get("hotData1"); // 达到K次，进入高优先级队列
        lruKCache.put("hotData2", "热点数据2"); 
        lruKCache.get("hotData2"); // 达到K次，进入高优先级队列
        
        lruCache.put("hotData1", "热点数据1");
        lruCache.put("hotData2", "热点数据2");
        
        System.out.println("LRU-K缓存状态：");
        printCacheStatus(lruKCache);
        System.out.println("传统LRU缓存大小：" + lruCache.size());
        
        // 模拟扫描攻击
        System.out.println("\n💥 扫描攻击：大量一次性访问");
        lruKCache.put("scan1", "扫描数据1");
        lruCache.put("scan1", "扫描数据1");
        
        System.out.println("\n添加scan1后：");
        System.out.println("LRU-K: 热数据保留=" + lruKCache.containsKey("hotData1") + 
                         ", " + lruKCache.containsKey("hotData2"));
        System.out.println("传统LRU: 热数据保留=" + lruCache.containsKey("hotData1") + 
                         ", " + lruCache.containsKey("hotData2"));
        
        // 继续扫描
        lruKCache.put("scan2", "扫描数据2");
        lruCache.put("scan2", "扫描数据2");
        
        System.out.println("\n添加scan2后：");
        System.out.println("LRU-K: 热数据保留=" + lruKCache.containsKey("hotData1") + 
                         ", " + lruKCache.containsKey("hotData2"));
        System.out.println("传统LRU: 热数据保留=" + lruCache.containsKey("hotData1") + 
                         ", " + lruCache.containsKey("hotData2"));
        
        System.out.println("\n✅ LRU-K成功保护了热数据，而传统LRU的热数据被扫描数据冲掉了！\n");
        System.out.println("" + "=".repeat(60) + "\n");
    }

    /**
     * 演示3：热数据保护机制
     */
    private static void demonstrateHotDataProtection() {
        System.out.println("🔥 演示3：热数据保护机制");
        System.out.println("展示只有被证明是热数据的内容才会获得长期保护\n");
        
        LRUKCache<String, String> cache = new LRUKCache<>(4, 3); // K=3，需要3次访问才能获得保护
        
        // 添加各种数据
        cache.put("user123", "用户信息");      // 1次访问
        cache.put("product456", "商品详情");   // 1次访问
        cache.put("config", "系统配置");       // 1次访问
        
        System.out.println("初始状态，所有数据都在历史队列：");
        printCacheStatus(cache);
        
        // 模拟config被频繁访问（成为热数据）
        System.out.println("\n🔄 config被频繁访问：");
        cache.get("config"); // 2次
        cache.get("config"); // 3次，达到K次
        
        printCacheStatus(cache);
        
        // 添加更多数据，观察淘汰行为
        System.out.println("\n➕ 添加新数据，观察淘汰策略：");
        cache.put("temp1", "临时数据1");
        cache.put("temp2", "临时数据2");
        
        System.out.println("添加2个临时数据后：");
        printCacheStatus(cache);
        System.out.println("config(热数据)保留：" + cache.containsKey("config"));
        System.out.println("user123保留：" + cache.containsKey("user123"));
        System.out.println("product456保留：" + cache.containsKey("product456"));
        
        System.out.println("\n✅ 只有被证明是热数据的config获得了长期保护！\n");
        System.out.println("" + "=".repeat(60) + "\n");
    }

    /**
     * 演示4：不同K值的影响
     */
    private static void demonstrateDifferentKValues() {
        System.out.println("⚙️ 演示4：不同K值的影响");
        System.out.println("K值决定了获得高优先级保护的门槛\n");
        
        // K=1：类似传统LRU
        System.out.println("🎯 K=1（类似传统LRU）：");
        LRUKCache<String, String> k1Cache = new LRUKCache<>(3, 1);
        k1Cache.put("data", "测试数据");
        System.out.println("首次访问后进入缓存队列：" + k1Cache.isInCacheQueue("data"));
        
        // K=2：温和的保护
        System.out.println("\n🎯 K=2（温和保护）：");
        LRUKCache<String, String> k2Cache = new LRUKCache<>(3, 2);
        k2Cache.put("data", "测试数据");
        System.out.println("首次访问后在历史队列：" + k2Cache.isInHistoryQueue("data"));
        k2Cache.get("data");
        System.out.println("二次访问后进入缓存队列：" + k2Cache.isInCacheQueue("data"));
        
        // K=5：严格的保护
        System.out.println("\n🎯 K=5（严格保护）：");
        LRUKCache<String, String> k5Cache = new LRUKCache<>(3, 5);
        k5Cache.put("data", "测试数据");
        for (int i = 1; i < 5; i++) {
            k5Cache.get("data");
            System.out.printf("第%d次访问后在历史队列：%s%n", i + 1, k5Cache.isInHistoryQueue("data"));
        }
        k5Cache.get("data"); // 第5次
        System.out.println("第5次访问后进入缓存队列：" + k5Cache.isInCacheQueue("data"));
        
        System.out.println("\n✅ K值越大，获得保护的门槛越高，越能过滤掉非热数据！\n");
        System.out.println("" + "=".repeat(60) + "\n");
    }

    /**
     * 演示5：实际业务场景模拟
     */
    private static void demonstrateRealWorldScenario() {
        System.out.println("🌍 演示5：实际业务场景 - 在线文档系统");
        System.out.println("场景：用户访问文档，有些是常用文档，有些是偶尔查看\n");
        
        LRUKCache<String, String> docCache = new LRUKCache<>(5, 3); // 容量5，需要3次访问才算热文档
        
        // 模拟真实的文档访问模式
        System.out.println("📋 模拟文档访问序列：");
        
        // 第一波：用户正常工作，访问常用文档
        System.out.println("\n第一阶段：正常工作访问");
        docCache.put("API文档", "接口说明");
        docCache.put("开发规范", "代码规范");
        docCache.put("需求文档", "产品需求");
        
        // 重复访问API文档（这是热文档）
        docCache.get("API文档");
        docCache.get("API文档"); // 达到3次，成为热文档
        
        // 偶尔访问开发规范
        docCache.get("开发规范");
        
        System.out.println("当前缓存状态：");
        printCacheStatus(docCache);
        
        // 第二波：新人入职，大量查看各种文档（扫描式访问）
        System.out.println("\n第二阶段：新人入职扫描访问");
        docCache.put("历史项目A", "旧项目文档");
        docCache.put("历史项目B", "旧项目文档");
        docCache.put("工具说明", "开发工具");
        
        System.out.println("大量新文档访问后：");
        printCacheStatus(docCache);
        
        // 检查热文档是否被保护
        System.out.println("\n🔍 热文档保护检查：");
        System.out.println("API文档(热文档)保留：" + docCache.containsKey("API文档"));
        System.out.println("开发规范保留：" + docCache.containsKey("开发规范"));
        System.out.println("需求文档保留：" + docCache.containsKey("需求文档"));
        
        // 继续正常工作
        System.out.println("\n第三阶段：继续正常工作");
        if (docCache.containsKey("API文档")) {
            docCache.get("API文档"); // 继续访问热文档
            System.out.println("✅ 热文档API文档依然可用，工作效率没有受到影响！");
        } else {
            System.out.println("❌ 热文档被冲掉了，需要重新加载，影响工作效率");
        }
        
        System.out.println("\n🎯 业务价值：");
        System.out.println("- LRU-K保护了真正的热文档，避免被临时访问冲掉");
        System.out.println("- 提高了系统响应速度，减少了重新加载的开销");
        System.out.println("- 更符合实际使用模式，提升了用户体验");
        
        System.out.println("\n" + "=".repeat(60));
    }

    /**
     * 打印缓存状态的辅助方法
     */
    private static void printCacheStatus(LRUKCache<?, ?> cache) {
        System.out.printf("📊 缓存状态：总大小=%d, 历史队列=%d, 缓存队列=%d%n", 
                         cache.size(), cache.historyQueueSize(), cache.cacheQueueSize());
    }
}
