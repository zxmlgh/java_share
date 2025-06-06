package net.dreampo.java_share.lru_algo;

import java.time.LocalTime;

/**
 * 统一的缓存工厂类 - 融合了静态配置和动态策略
 * <p>
 * 提供了面向业务场景的缓存创建方法，每个方法都针对特定场景优化。
 * 既支持简单的固定K值配置，也支持复杂的动态策略。
 * 
 * @author Assistant
 * @version 2.0
 * @since 2025-06-06
 */
public class LRUKCacheFactory {
    
    // ==================== 预定义场景（简单易用） ====================
    
    /**
     * 创建Web应用缓存
     * <p>
     * <b>场景特点：</b>
     * <ul>
     *   <li>访问模式多样，有热点数据也有随机访问</li>
     *   <li>需要平衡命中率和内存使用</li>
     *   <li>响应时间敏感</li>
     * </ul>
     * <p>
     * <b>策略：</b>根据访问频率自适应调整K值（2-5）
     */
    public static <K, V> LRUKCache<K, V> createWebCache(int capacity) {
        return new LRUKCache<>(capacity, Strategies.webOptimized());
    }
    
    /**
     * 创建数据库查询缓存
     * <p>
     * <b>场景特点：</b>
     * <ul>
     *   <li>查询成本高，缓存价值大</li>
     *   <li>通常有明显的热查询</li>
     *   <li>需要避免慢查询风暴</li>
     * </ul>
     * <p>
     * <b>策略：</b>组合访问频率和查询成本，复杂查询获得更多保护
     */
    public static <K, V> LRUKCache<K, V> createDatabaseCache(int capacity) {
        return new LRUKCache<>(capacity, Strategies.databaseOptimized());
    }
    
    /**
     * 创建文件系统缓存
     * <p>
     * <b>场景特点：</b>
     * <ul>
     *   <li>I/O成本极高</li>
     *   <li>文件大小差异大</li>
     *   <li>访问模式相对稳定</li>
     * </ul>
     * <p>
     * <b>策略：</b>基于文件类型和大小动态调整，大文件获得更多保护
     */
    public static <K, V> LRUKCache<K, V> createFileCache(int capacity) {
        return new LRUKCache<>(capacity, Strategies.fileSystemOptimized());
    }
    
    /**
     * 创建CDN边缘缓存
     * <p>
     * <b>场景特点：</b>
     * <ul>
     *   <li>面对大量随机访问</li>
     *   <li>需要极强的扫描抵抗性</li>
     *   <li>热内容价值极高</li>
     * </ul>
     * <p>
     * <b>策略：</b>高K值基础上根据内容类型和访问模式微调
     */
    public static <K, V> LRUKCache<K, V> createCDNCache(int capacity) {
        return new LRUKCache<>(capacity, Strategies.cdnOptimized());
    }
    
    /**
     * 创建会话缓存
     * <p>
     * <b>场景特点：</b>
     * <ul>
     *   <li>生命周期明确</li>
     *   <li>访问模式可预测</li>
     *   <li>活跃会话需要保护</li>
     * </ul>
     * <p>
     * <b>策略：</b>根据会话活跃度和时间动态调整
     */
    public static <K, V> LRUKCache<K, V> createSessionCache(int capacity) {
        return new LRUKCache<>(capacity, Strategies.sessionOptimized());
    }
    
    /**
     * 创建API限流缓存
     * <p>
     * <b>场景特点：</b>
     * <ul>
     *   <li>需要识别恶意访问</li>
     *   <li>正常用户需要保护</li>
     *   <li>访问频率是关键指标</li>
     * </ul>
     * <p>
     * <b>策略：</b>基于访问频率和时间窗口的智能策略
     */
    public static <K, V> LRUKCache<K, V> createRateLimitCache(int capacity) {
        return new LRUKCache<>(capacity, Strategies.rateLimitOptimized());
    }
    
    // ==================== 高级配置（灵活定制） ====================
    
    /**
     * 创建自适应缓存
     * <p>
     * 根据系统运行状态自动调整策略，适合不确定访问模式的场景
     */
    public static <K, V> LRUKCache<K, V> createAdaptive(int capacity) {
        return new LRUKCache<>(capacity, Strategies.smartAdaptive());
    }
    
    /**
     * 创建时间感知缓存
     * <p>
     * 根据业务时间（工作时间、高峰期等）调整策略
     */
    public static <K, V> LRUKCache<K, V> createTimeAware(int capacity) {
        return new LRUKCache<>(capacity, Strategies.timeAware());
    }
    
    /**
     * 创建自定义缓存
     * <p>
     * 使用业务方提供的策略函数
     */
    public static <K, V> LRUKCache<K, V> createCustom(int capacity, 
                                                      KValueFunction<K, V> strategy) {
        return new LRUKCache<>(capacity, strategy);
    }
    
    /**
     * 创建固定K值缓存（向后兼容）
     */
    public static <K, V> LRUKCache<K, V> createFixed(int capacity, int k) {
        return new LRUKCache<>(capacity, Strategies.fixed(k));
    }
    
    // ==================== 策略定义（内部实现） ====================
    
    /**
     * 内部策略类，封装所有预定义策略
     */
    private static class Strategies {
        
        /**
         * Web应用优化策略
         * - 访问1-2次：K=2（快速识别）
         * - 访问3-5次：K=3（温和保护）
         * - 访问6-10次：K=4（重点保护）
         * - 访问>10次：K=5（核心数据）
         */
        static <K, V> KValueFunction<K, V> webOptimized() {
            return context -> {
                int count = context.getCurrentAccessCount();
                if (count <= 2) return 2;
                if (count <= 5) return 3;
                if (count <= 10) return 4;
                return 5;
            };
        }
        
        /**
         * 数据库查询优化策略
         * 结合访问频率和查询复杂度（通过值类型推断）
         */
        static <K, V> KValueFunction<K, V> databaseOptimized() {
            return context -> {
                int baseK = 2;
                int count = context.getCurrentAccessCount();
                
                // 基于访问频率
                if (count > 5) baseK++;
                if (count > 10) baseK++;
                
                // 基于查询复杂度（简化判断）
                V value = context.getValue();
                if (value != null) {
                    String valueStr = value.toString();
                    // 大结果集需要更多保护
                    if (valueStr.length() > 1000) baseK++;
                }
                
                return Math.min(baseK, 5);
            };
        }
        
        /**
         * 文件系统优化策略
         * 基于文件类型和访问模式
         */
        static <K, V> KValueFunction<K, V> fileSystemOptimized() {
            return context -> {
                V value = context.getValue();
                if (value == null) return 3;
                
                String className = value.getClass().getSimpleName().toLowerCase();
                
                // 多媒体文件：高成本，需要更多保护
                if (className.contains("image") || className.contains("video")) {
                    return context.getCurrentAccessCount() > 2 ? 5 : 4;
                }
                
                // 文档文件：中等成本
                if (className.contains("document") || className.contains("pdf")) {
                    return 3;
                }
                
                // 配置文件：通常只读一次
                if (className.contains("config") || className.contains("properties")) {
                    return 1;
                }
                
                // 默认策略
                return 3;
            };
        }
        
        /**
         * CDN优化策略
         * 极强的扫描抵抗性 + 内容类型感知
         */
        static <K, V> KValueFunction<K, V> cdnOptimized() {
            return context -> {
                // 基础K值就很高，抵抗扫描
                int k = 4;
                
                // 热内容额外保护
                if (context.getCurrentAccessCount() > 10) {
                    k = 5;
                }
                
                // 高负载时更严格
                if (context.getCacheUtilization() > 0.8) {
                    k = Math.min(k + 1, 6);
                }
                
                return k;
            };
        }
        
        /**
         * 会话缓存优化策略
         * 活跃会话获得更多保护
         */
        static <K, V> KValueFunction<K, V> sessionOptimized() {
            return context -> {
                long now = System.currentTimeMillis();
                long lastAccess = context.getTimestamp();
                long timeDiff = now - lastAccess;
                
                // 刚访问过的活跃会话
                if (timeDiff < 60_000) { // 1分钟内
                    return 2;
                }
                
                // 相对活跃的会话
                if (timeDiff < 300_000) { // 5分钟内
                    return 3;
                }
                
                // 不活跃的会话，提高淘汰门槛
                return 4;
            };
        }
        
        /**
         * API限流优化策略
         * 识别和保护正常用户，限制异常访问
         */
        static <K, V> KValueFunction<K, V> rateLimitOptimized() {
            return context -> {
                int count = context.getCurrentAccessCount();
                
                // 正常访问频率
                if (count <= 10) return 2;
                
                // 频繁访问，可能是重度用户
                if (count <= 50) return 3;
                
                // 异常高频，可能是爬虫或攻击
                return 5; // 需要更多证明才能占用缓存
            };
        }
        
        /**
         * 智能自适应策略
         * 综合考虑多个因素
         */
        static <K, V> KValueFunction<K, V> smartAdaptive() {
            return context -> {
                double utilization = context.getCacheUtilization();
                int accessCount = context.getCurrentAccessCount();
                
                // 基础策略：根据访问次数
                int baseK = 2;
                if (accessCount > 3) baseK = 3;
                if (accessCount > 10) baseK = 4;
                
                // 根据缓存压力调整
                if (utilization > 0.9) {
                    // 高压力，提高门槛
                    baseK = Math.min(baseK + 2, 5);
                } else if (utilization > 0.7) {
                    // 中等压力，适度提高
                    baseK = Math.min(baseK + 1, 5);
                }
                
                return baseK;
            };
        }
        
        /**
         * 时间感知策略
         * 根据业务时间动态调整
         */
        static <K, V> KValueFunction<K, V> timeAware() {
            return context -> {
                LocalTime now = LocalTime.now();
                int hour = now.getHour();
                
                // 业务高峰期（9-11, 14-17）
                if ((hour >= 9 && hour <= 11) || (hour >= 14 && hour <= 17)) {
                    // 高峰期更宽松，快速建立缓存
                    return context.getCurrentAccessCount() > 5 ? 3 : 2;
                }
                
                // 晚高峰（19-22）
                if (hour >= 19 && hour <= 22) {
                    // 用户活跃期，平衡策略
                    return 3;
                }
                
                // 其他时间
                return 2;
            };
        }
        
        /**
         * 固定K值策略
         */
        static <K, V> KValueFunction<K, V> fixed(int k) {
            return context -> k;
        }
    }
    
    // ==================== 使用建议 ====================
    
    /**
     * 打印使用建议
     */
    public static void printUsageGuide() {
        System.out.println("=== 缓存工厂使用指南 ===\n");
        
        System.out.println("🎯 场景选择建议：");
        System.out.println("• Web应用 → createWebCache(): 自适应策略，平衡性能");
        System.out.println("• 数据库 → createDatabaseCache(): 保护复杂查询");
        System.out.println("• 文件系统 → createFileCache(): 基于文件类型优化");
        System.out.println("• CDN → createCDNCache(): 极强扫描抵抗性");
        System.out.println("• 会话管理 → createSessionCache(): 活跃度感知");
        System.out.println("• API限流 → createRateLimitCache(): 异常识别");
        
        System.out.println("\n📊 容量规划：");
        System.out.println("• 小型应用: 100-1000");
        System.out.println("• 中型应用: 1000-10000");
        System.out.println("• 大型应用: 10000+");
        
        System.out.println("\n💡 高级用法：");
        System.out.println("• 不确定场景 → createAdaptive(): 智能自适应");
        System.out.println("• 时间规律明显 → createTimeAware(): 时间感知");
        System.out.println("• 特殊需求 → createCustom(): 自定义策略");
    }
}
