package net.dreampo.java_share.examples.lru_algo;

import net.dreampo.java_share.lru_algo.LRUKCacheFactory;
import net.dreampo.java_share.lru_algo.LRUKCache;

/**
 * 缓存工厂使用示例
 * 展示新的统一工厂类如何简化缓存的创建和使用
 */
public class LRUKCacheFactoryDemo {
    
    public static void main(String[] args) {
        System.out.println("=== 新缓存工厂使用示例 ===\n");
        
        // 示例1：Web应用场景
        demonstrateWebCache();
        
        // 示例2：数据库查询场景
        demonstrateDatabaseCache();
        
        // 示例3：对比新旧API
        compareOldAndNewAPI();
        
        // 打印使用指南
        System.out.println();
        LRUKCacheFactory.printUsageGuide();
    }
    
    /**
     * Web应用缓存示例
     */
    private static void demonstrateWebCache() {
        System.out.println("📱 示例1：Web应用缓存");
        System.out.println("场景：电商网站的商品信息缓存\n");
        
        // 一行代码创建针对Web优化的缓存
        LRUKCache<String, String> cache = LRUKCacheFactory.createWebCache(100);
        
        // 模拟用户浏览行为
        System.out.println("用户浏览商品...");
        cache.put("product:001", "iPhone 15");
        cache.put("product:002", "MacBook Pro");
        cache.put("product:003", "AirPods");
        
        // 热门商品被多次查看
        cache.get("product:001"); // 第2次
        cache.get("product:001"); // 第3次
        System.out.println("iPhone被访问3次，K值自动调整为: " + cache.getCurrentK("product:001"));
        
        // 继续访问
        for (int i = 0; i < 5; i++) {
            cache.get("product:001");
        }
        System.out.println("iPhone被访问8次，K值自动调整为: " + cache.getCurrentK("product:001"));
        
        System.out.println("✅ Web缓存根据访问频率自动调整保护级别！\n");
        System.out.println("" + "=".repeat(50) + "\n");
    }
    
    /**
     * 数据库查询缓存示例
     */
    private static void demonstrateDatabaseCache() {
        System.out.println("🗄️ 示例2：数据库查询缓存");
        System.out.println("场景：复杂查询结果的智能缓存\n");
        
        // 创建数据库缓存
        LRUKCache<String, QueryResult> cache = LRUKCacheFactory.createDatabaseCache(50);
        
        // 模拟不同复杂度的查询
        cache.put("simple_query", new QueryResult("用户名", 10));
        cache.put("complex_query", new QueryResult("月度销售报表", 5000));
        
        System.out.println("简单查询初始K值: " + cache.getCurrentK("simple_query"));
        System.out.println("复杂查询初始K值: " + cache.getCurrentK("complex_query"));
        
        // 访问查询
        cache.get("complex_query");
        cache.get("complex_query");
        
        System.out.println("\n复杂查询被访问3次后K值: " + cache.getCurrentK("complex_query"));
        System.out.println("✅ 数据库缓存根据查询复杂度提供差异化保护！\n");
        System.out.println("" + "=".repeat(50) + "\n");
    }
    
    /**
     * 对比新旧API
     */
    private static void compareOldAndNewAPI() {
        System.out.println("🔄 示例3：新旧API对比");
        
        System.out.println("\n❌ 旧方式（功能分离）：");
        System.out.println("// 方式1：使用固定K值");
        System.out.println("LRUKCache<String, String> cache1 = CacheFactory.createFixed(100, 2);");
        System.out.println("// 结果：K=2，无法根据访问模式调整");
        
        System.out.println("\n// 方式2：使用KValueStrategies（需要手动组合）");
        System.out.println("KValueFunction<String, String> strategy = KValueStrategies.adaptive();");
        System.out.println("LRUKCache<String, String> cache2 = new LRUKCache<>(100, strategy);");
        System.out.println("// 结果：灵活但使用复杂");
        
        System.out.println("\n✅ 新方式（统一简洁）：");
        System.out.println("// 一行代码，自动选择最佳策略");
        System.out.println("LRUKCache<String, String> cache = CacheFactory.createWebCache(100);");
        System.out.println("// 结果：自适应K值(2-5)，根据访问模式自动优化");
        
        System.out.println("\n🎯 优势：");
        System.out.println("1. API更简洁，一个工厂类搞定所有场景");
        System.out.println("2. 默认配置更智能，自带动态优化");
        System.out.println("3. 场景化命名，选择更直观");
        System.out.println("4. 保留灵活性，支持自定义策略");
    }
    
    /**
     * 模拟查询结果
     */
    static class QueryResult {
        private final String data;
        private final int size;
        
        public QueryResult(String data, int size) {
            this.data = data;
            this.size = size;
        }
        
        @Override
        public String toString() {
            // 模拟大结果集
            return data.repeat(Math.max(1, size / data.length()));
        }
    }
}
