package net.dreampo.java_share.lru_algo;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 缓存性能对比测试
 * 对比新旧API在实际场景中的表现
 */
public class CacheFactoryPerformanceTest {
    
    private static final int CACHE_SIZE = 1000;
    private static final int DATA_SIZE = 5000;
    private static final int OPERATIONS = 100000;
    private static final Random random = new Random(42); // 固定种子，保证可重复
    
    public static void main(String[] args) {
        System.out.println("=== 缓存性能对比测试 ===\n");
        
        // 场景1：Web应用场景（混合访问模式）
        testWebScenario();
        
        // 场景2：数据库查询场景（有明显热查询）
        testDatabaseScenario();
        
        // 场景3：扫描攻击场景
        testScanAttackScenario();
    }
    
    /**
     * Web应用场景测试
     */
    private static void testWebScenario() {
        System.out.println("🌐 场景1：Web应用（20%热数据，80%随机访问）");
        System.out.println("模拟用户访问网站，部分页面是热门页面\n");
        
        // 旧方式：固定K值
        LRUKCache<String, String> oldCache = LRUKCacheFactory.createFixed(CACHE_SIZE, 2);
        
        // 新方式：自适应K值
        LRUKCache<String, String> newCache = LRUKCacheFactory.createWebCache(CACHE_SIZE);
        
        // 运行测试
        TestResult oldResult = runWebTest(oldCache, "旧API(固定K=2)");
        TestResult newResult = runWebTest(newCache, "新API(自适应K)");
        
        // 对比结果
        compareResults(oldResult, newResult);
        System.out.println();
    }
    
    /**
     * 数据库查询场景测试
     */
    private static void testDatabaseScenario() {
        System.out.println("💾 场景2：数据库查询（10%热查询，有复杂查询）");
        System.out.println("模拟数据库访问，部分查询是高频复杂查询\n");
        
        // 旧方式
        LRUKCache<String, String> oldCache = LRUKCacheFactory.createFixed(CACHE_SIZE, 2);
        
        // 新方式
        LRUKCache<String, String> newCache = LRUKCacheFactory.createDatabaseCache(CACHE_SIZE);
        
        // 运行测试
        TestResult oldResult = runDatabaseTest(oldCache, "旧API(固定K=2)");
        TestResult newResult = runDatabaseTest(newCache, "新API(智能K)");
        
        // 对比结果
        compareResults(oldResult, newResult);
        System.out.println();
    }
    
    /**
     * 扫描攻击场景测试
     */
    private static void testScanAttackScenario() {
        System.out.println("🛡️ 场景3：扫描攻击防护（5%热数据，大量一次性访问）");
        System.out.println("模拟正常访问中突然出现大量扫描请求\n");
        
        // 旧方式
        LRUKCache<String, String> oldCache = LRUKCacheFactory.createFixed(CACHE_SIZE, 3);
        
        // 新方式
        LRUKCache<String, String> newCache = LRUKCacheFactory.createCDNCache(CACHE_SIZE);
        
        // 运行测试
        TestResult oldResult = runScanAttackTest(oldCache, "旧API(固定K=3)");
        TestResult newResult = runScanAttackTest(newCache, "新API(动态K)");
        
        // 对比结果
        compareResults(oldResult, newResult);
    }
    
    /**
     * Web场景测试逻辑
     */
    private static TestResult runWebTest(LRUKCache<String, String> cache, String name) {
        long startTime = System.nanoTime();
        int hits = 0;
        int hotDataHits = 0;
        
        // 预热：建立热数据
        for (int i = 0; i < 200; i++) {
            String key = "hot_page_" + i;
            cache.put(key, "热门页面内容");
            // 多次访问使其成为热数据
            for (int j = 0; j < 5; j++) {
                cache.get(key);
            }
        }
        
        // 混合访问模式
        for (int i = 0; i < OPERATIONS; i++) {
            String key;
            boolean isHotData = false;
            
            if (random.nextDouble() < 0.2) {
                // 20%访问热数据
                key = "hot_page_" + random.nextInt(200);
                isHotData = true;
            } else {
                // 80%随机访问
                key = "page_" + random.nextInt(DATA_SIZE);
            }
            
            // 随机读写
            if (random.nextDouble() < 0.7) {
                // 70%读取
                String value = cache.get(key);
                if (value != null) {
                    hits++;
                    if (isHotData) hotDataHits++;
                }
            } else {
                // 30%写入
                cache.put(key, "页面内容");
            }
        }
        
        long endTime = System.nanoTime();
        double hitRate = (double) hits / (OPERATIONS * 0.7); // 只计算读操作
        double hotDataProtection = (double) hotDataHits / (OPERATIONS * 0.2 * 0.7);
        
        return new TestResult(name, hitRate, hotDataProtection, 
                             TimeUnit.NANOSECONDS.toMillis(endTime - startTime));
    }
    
    /**
     * 数据库场景测试逻辑
     */
    private static TestResult runDatabaseTest(LRUKCache<String, String> cache, String name) {
        long startTime = System.nanoTime();
        int hits = 0;
        int complexQueryHits = 0;
        
        // 建立热查询
        for (int i = 0; i < 100; i++) {
            String key = "hot_query_" + i;
            String value = i < 20 ? generateComplexResult() : "简单查询结果";
            cache.put(key, value);
            // 多次访问
            for (int j = 0; j < 10; j++) {
                cache.get(key);
            }
        }
        
        // 模拟查询
        for (int i = 0; i < OPERATIONS; i++) {
            String key;
            boolean isComplexQuery = false;
            
            if (random.nextDouble() < 0.1) {
                // 10%访问热查询
                key = "hot_query_" + random.nextInt(100);
                isComplexQuery = random.nextInt(100) < 20;
            } else {
                // 90%其他查询
                key = "query_" + random.nextInt(DATA_SIZE);
            }
            
            if (random.nextDouble() < 0.8) {
                // 80%读取
                String value = cache.get(key);
                if (value != null) {
                    hits++;
                    if (isComplexQuery) complexQueryHits++;
                }
            } else {
                // 20%新查询
                cache.put(key, random.nextBoolean() ? generateComplexResult() : "简单结果");
            }
        }
        
        long endTime = System.nanoTime();
        double hitRate = (double) hits / (OPERATIONS * 0.8);
        double complexQueryProtection = complexQueryHits > 0 ? 1.0 : 0.0; // 简化计算
        
        return new TestResult(name, hitRate, complexQueryProtection,
                             TimeUnit.NANOSECONDS.toMillis(endTime - startTime));
    }
    
    /**
     * 扫描攻击测试逻辑
     */
    private static TestResult runScanAttackTest(LRUKCache<String, String> cache, String name) {
        long startTime = System.nanoTime();
        int hits = 0;
        int hotDataSurvived = 0;
        
        // 建立核心热数据
        for (int i = 0; i < 50; i++) {
            String key = "core_data_" + i;
            cache.put(key, "核心数据");
            // 频繁访问
            for (int j = 0; j < 20; j++) {
                cache.get(key);
            }
        }
        
        // 正常访问阶段
        for (int i = 0; i < OPERATIONS / 2; i++) {
            if (random.nextDouble() < 0.05) {
                // 5%访问核心数据
                String key = "core_data_" + random.nextInt(50);
                if (cache.get(key) != null) hits++;
            } else {
                // 正常随机访问
                String key = "data_" + random.nextInt(1000);
                if (random.nextDouble() < 0.7) {
                    if (cache.get(key) != null) hits++;
                } else {
                    cache.put(key, "普通数据");
                }
            }
        }
        
        // 扫描攻击阶段
        for (int i = 0; i < OPERATIONS / 2; i++) {
            // 大量一次性访问
            String scanKey = "scan_" + i;
            cache.put(scanKey, "扫描数据");
            
            // 偶尔检查核心数据是否还在
            if (i % 100 == 0) {
                String coreKey = "core_data_" + random.nextInt(50);
                if (cache.get(coreKey) != null) {
                    hotDataSurvived++;
                }
            }
        }
        
        // 最终检查核心数据保留情况
        int finalCoreDataCount = 0;
        for (int i = 0; i < 50; i++) {
            if (cache.containsKey("core_data_" + i)) {
                finalCoreDataCount++;
            }
        }
        
        long endTime = System.nanoTime();
        double hitRate = (double) hits / (OPERATIONS / 2 * 0.7);
        double coreDataProtection = (double) finalCoreDataCount / 50;
        
        return new TestResult(name, hitRate, coreDataProtection,
                             TimeUnit.NANOSECONDS.toMillis(endTime - startTime));
    }
    
    /**
     * 生成复杂查询结果
     */
    private static String generateComplexResult() {
        return "复杂查询结果".repeat(100); // 模拟大结果集
    }
    
    /**
     * 对比测试结果
     */
    private static void compareResults(TestResult oldResult, TestResult newResult) {
        System.out.println("📊 测试结果对比：");
        System.out.println("─".repeat(60));
        System.out.printf("%-20s %15s %15s%n", "指标", oldResult.name, newResult.name);
        System.out.println("─".repeat(60));
        
        // 命中率对比
        System.out.printf("%-20s %14.1f%% %14.1f%% ", 
                         "总体命中率", oldResult.hitRate * 100, newResult.hitRate * 100);
        printImprovement(oldResult.hitRate, newResult.hitRate);
        
        // 热数据保护率对比
        System.out.printf("%-20s %14.1f%% %14.1f%% ",
                         "关键数据保护率", oldResult.hotDataProtection * 100, newResult.hotDataProtection * 100);
        printImprovement(oldResult.hotDataProtection, newResult.hotDataProtection);
        
        // 运行时间对比
        System.out.printf("%-20s %13dms %13dms ",
                         "运行时间", oldResult.executionTime, newResult.executionTime);
        printTimeImprovement(oldResult.executionTime, newResult.executionTime);
        
        System.out.println("─".repeat(60));
        
        // 总结
        if (newResult.hitRate > oldResult.hitRate && 
            newResult.hotDataProtection > oldResult.hotDataProtection) {
            System.out.println("✅ 新API在命中率和关键数据保护方面均有提升！");
        } else if (newResult.hitRate > oldResult.hitRate || 
                   newResult.hotDataProtection > oldResult.hotDataProtection) {
            System.out.println("✅ 新API在某些方面有所改进。");
        } else {
            System.out.println("⚠️  新API表现与旧API相当。");
        }
        System.out.println();
    }
    
    /**
     * 打印改进幅度
     */
    private static void printImprovement(double oldValue, double newValue) {
        double improvement = ((newValue - oldValue) / oldValue) * 100;
        if (improvement > 0) {
            System.out.printf("(↑%.1f%%)%n", improvement);
        } else if (improvement < 0) {
            System.out.printf("(↓%.1f%%)%n", Math.abs(improvement));
        } else {
            System.out.println("(持平)");
        }
    }
    
    /**
     * 打印时间改进（时间越少越好）
     */
    private static void printTimeImprovement(long oldTime, long newTime) {
        double improvement = ((oldTime - newTime) / (double) oldTime) * 100;
        if (improvement > 0) {
            System.out.printf("(快%.1f%%)%n", improvement);
        } else if (improvement < 0) {
            System.out.printf("(慢%.1f%%)%n", Math.abs(improvement));
        } else {
            System.out.println("(持平)");
        }
    }
    
    /**
     * 测试结果类
     */
    static class TestResult {
        final String name;
        final double hitRate;
        final double hotDataProtection;
        final long executionTime;
        
        TestResult(String name, double hitRate, double hotDataProtection, long executionTime) {
            this.name = name;
            this.hitRate = hitRate;
            this.hotDataProtection = hotDataProtection;
            this.executionTime = executionTime;
        }
    }
}
