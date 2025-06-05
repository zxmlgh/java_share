package net.dreampo.java_share.lru_algo;

/**
 * LRU-K缓存工厂类
 * 
 * 提供针对不同业务场景优化的LRU-K缓存配置，
 * 封装了业界最佳实践和经验参数
 */
public class LRUKCacheFactory {

    /**
     * 创建数据库缓存
     * 适用场景：数据库查询结果缓存
     * 特点：K=2，温和的保护策略，避免一次性查询冲掉热数据
     * 
     * @param capacity 缓存容量
     * @return 配置好的LRU-K缓存实例
     */
    public static <K, V> LRUKCache<K, V> createDatabaseCache(int capacity) {
        return new LRUKCache<>(capacity, 2);
    }

    /**
     * 创建Web缓存
     * 适用场景：Web页面、API响应缓存
     * 特点：K=2，平衡性能和命中率
     * 
     * @param capacity 缓存容量
     * @return 配置好的LRU-K缓存实例
     */
    public static <K, V> LRUKCache<K, V> createWebCache(int capacity) {
        return new LRUKCache<>(capacity, 2);
    }

    /**
     * 创建文件系统缓存
     * 适用场景：文件内容、元数据缓存
     * 特点：K=3，较严格的保护，因为文件I/O成本高
     * 
     * @param capacity 缓存容量
     * @return 配置好的LRU-K缓存实例
     */
    public static <K, V> LRUKCache<K, V> createFileSystemCache(int capacity) {
        return new LRUKCache<>(capacity, 3);
    }

    /**
     * 创建CDN缓存
     * 适用场景：内容分发网络，需要抵抗大量一次性访问
     * 特点：K=3，强扫描抵抗性
     * 
     * @param capacity 缓存容量
     * @return 配置好的LRU-K缓存实例
     */
    public static <K, V> LRUKCache<K, V> createCDNCache(int capacity) {
        return new LRUKCache<>(capacity, 3);
    }

    /**
     * 创建应用级缓存
     * 适用场景：应用内部对象缓存，计算结果缓存
     * 特点：K=2，通用配置
     * 
     * @param capacity 缓存容量
     * @return 配置好的LRU-K缓存实例
     */
    public static <K, V> LRUKCache<K, V> createApplicationCache(int capacity) {
        return new LRUKCache<>(capacity, 2);
    }

    /**
     * 创建会话缓存
     * 适用场景：用户会话数据缓存
     * 特点：K=2，适中的保护策略
     * 
     * @param capacity 缓存容量
     * @return 配置好的LRU-K缓存实例
     */
    public static <K, V> LRUKCache<K, V> createSessionCache(int capacity) {
        return new LRUKCache<>(capacity, 2);
    }

    /**
     * 创建高频访问缓存
     * 适用场景：预期会有大量重复访问的热数据
     * 特点：K=4，严格的热数据识别
     * 
     * @param capacity 缓存容量
     * @return 配置好的LRU-K缓存实例
     */
    public static <K, V> LRUKCache<K, V> createHighFrequencyCache(int capacity) {
        return new LRUKCache<>(capacity, 4);
    }

    /**
     * 创建抗扫描缓存
     * 适用场景：面对大量随机访问攻击的系统
     * 特点：K=5，极强的扫描抵抗能力
     * 
     * @param capacity 缓存容量
     * @return 配置好的LRU-K缓存实例
     */
    public static <K, V> LRUKCache<K, V> createScanResistantCache(int capacity) {
        return new LRUKCache<>(capacity, 5);
    }

    /**
     * 创建自定义缓存
     * 适用场景：需要特殊K值的场景
     * 
     * @param capacity 缓存容量
     * @param k 自定义K值
     * @return 配置好的LRU-K缓存实例
     */
    public static <K, V> LRUKCache<K, V> createCustomCache(int capacity, int k) {
        return new LRUKCache<>(capacity, k);
    }

    /**
     * 缓存配置推荐
     */
    public static class Recommendations {
        
        /**
         * 根据业务场景推荐K值
         * 
         * @param scenario 业务场景描述
         * @return 推荐的K值和说明
         */
        public static String recommendK(String scenario) {
            return switch (scenario.toLowerCase()) {
                case "database", "数据库" -> 
                    "推荐K=2: 数据库查询通常有重复性，K=2可以有效识别热查询";
                    
                case "web", "网页" -> 
                    "推荐K=2: Web访问模式多样，K=2提供良好的平衡";
                    
                case "file", "文件" -> 
                    "推荐K=3: 文件访问成本高，需要更严格的热数据识别";
                    
                case "cdn", "内容分发" -> 
                    "推荐K=3: CDN面对大量随机访问，需要较强的扫描抵抗性";
                    
                case "high-frequency", "高频" -> 
                    "推荐K=4: 高频场景下，K=4可以更准确识别真正的热数据";
                    
                case "scan-resistant", "抗扫描" -> 
                    "推荐K=5: 面对攻击性扫描，K=5提供最强保护";
                    
                default -> 
                    "推荐K=2: 通用场景下，K=2是最常用的配置";
            };
        }

        /**
         * 获取配置说明
         */
        public static void printConfigurationGuide() {
            System.out.println("=== LRU-K 配置指南 ===");
            System.out.println();
            System.out.println("K值选择原则：");
            System.out.println("• K=1: 等同于传统LRU，无扫描抵抗性");
            System.out.println("• K=2: 业界最常用配置，适用于大多数场景");
            System.out.println("• K=3: 较强的扫描抵抗性，适用于I/O密集型场景");
            System.out.println("• K=4-5: 极强的扫描抵抗性，适用于特殊安全场景");
            System.out.println("• K>5: 通常不推荐，会导致缓存效率下降");
            System.out.println();
            System.out.println("容量规划建议：");
            System.out.println("• 小型应用: 100-1000");
            System.out.println("• 中型应用: 1000-10000");
            System.out.println("• 大型应用: 10000+");
            System.out.println();
            System.out.println("性能特性：");
            System.out.println("• 时间复杂度: O(1) for get/put operations");
            System.out.println("• 空间复杂度: O(capacity)");
            System.out.println("• 内存开销: 约为传统LRU的1.5-2倍");
        }
    }

    /**
     * 缓存性能监控辅助类
     */
    public static class Monitor {
        
        /**
         * 计算缓存效率指标
         */
        public static void printCacheStats(LRUKCache<?, ?> cache) {
            System.out.println("=== 缓存统计信息 ===");
            System.out.printf("总容量: %d%n", cache.capacity());
            System.out.printf("当前大小: %d (使用率: %.1f%%)%n", 
                             cache.size(), 
                             (double) cache.size() / cache.capacity() * 100);
            System.out.printf("历史队列: %d%n", cache.historyQueueSize());
            System.out.printf("缓存队列: %d%n", cache.cacheQueueSize());
            System.out.printf("K值: %d%n", cache.getK());
            
            double protectionRatio = cache.size() > 0 ? 
                (double) cache.cacheQueueSize() / cache.size() * 100 : 0;
            System.out.printf("高优先级保护率: %.1f%% %s%n", 
                             protectionRatio,
                             protectionRatio > 50 ? "(良好)" : protectionRatio > 30 ? "(一般)" : "(较低)");
        }
        
        /**
         * 检查缓存健康状况
         */
        public static String healthCheck(LRUKCache<?, ?> cache) {
            double usageRatio = (double) cache.size() / cache.capacity();
            double protectionRatio = cache.size() > 0 ? 
                (double) cache.cacheQueueSize() / cache.size() : 0;
            
            if (usageRatio < 0.5) {
                return "✅ 健康 - 缓存使用率较低，性能良好";
            } else if (usageRatio < 0.8) {
                if (protectionRatio > 0.3) {
                    return "✅ 健康 - 缓存使用率适中，热数据保护良好";
                } else {
                    return "⚠️ 注意 - 缓存使用率适中，但热数据保护率较低";
                }
            } else {
                if (protectionRatio > 0.4) {
                    return "⚠️ 注意 - 缓存使用率较高，建议监控性能";
                } else {
                    return "❌ 警告 - 缓存使用率高且热数据保护率低，建议优化";
                }
            }
        }
    }
}
