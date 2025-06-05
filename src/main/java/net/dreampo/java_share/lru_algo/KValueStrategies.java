package net.dreampo.java_share.lru_algo;

import java.util.function.Function;

/**
 * 预定义的K值计算策略
 * <p>
 * 提供了多种常用的K值计算策略，覆盖了大部分业务场景。
 * 业务方可以直接使用这些策略，也可以通过组合策略实现更复杂的逻辑。
 *
 * @author Claude
 * @version 1.0
 * @since 2025-06-05
 */
public class KValueStrategies {
    
    /**
     * 固定K值策略 - 最简单最稳定
     * <p>
     * <b>适用场景：</b>访问模式稳定、对缓存性能要求一致的业务
     * 
     * @param k 固定的K值
     * @param <K> 缓存键的类型
     * @param <V> 缓存值的类型
     * @return K值计算函数
     */
    public static <K, V> KValueFunction<K, V> fixed(int k) {
        return context -> Math.max(1, Math.min(k, 10));
    }

    /**
     * 默认策略：K=2 - 业界最常用的配置
     * <p>
     * <b>适用场景：</b>通用Web应用、API缓存、数据库查询缓存
     *
     * @param <K> 缓存键的类型
     * @param <V> 缓存值的类型
     * @return K值计算函数
     */
    public static <K, V> KValueFunction<K, V> defaultStrategy() {
        return fixed(2);
    }

    /**
     * 基于访问次数的自适应策略 - 越热门保护越强
     * <p>
     * <b>适用场景：</b>有明显热点数据的系统，如商品推荐、热门文章
     * <p>
     * <b>策略逻辑：</b>
     * <ul>
     *   <li>初次访问(≤1次): K=2</li>
     *   <li>轻度访问(2-3次): K=3</li>
     *   <li>中度访问(4-10次): K=4</li>
     *   <li>重度访问(>10次): K=5</li>
     * </ul>
     *
     * @param <K> 缓存键的类型
     * @param <V> 缓存值的类型
     * @return K值计算函数
     */
    public static <K, V> KValueFunction<K, V> adaptive() {
        return context -> {
            int count = context.getCurrentAccessCount();
            if (count <= 1) return 2;
            if (count <= 3) return 3;
            if (count <= 10) return 4;
            return 5;
        };
    }

    /**
     * 基于缓存利用率的策略 - 容量紧张时更严格保护
     * <p>
     * <b>适用场景：</b>内存受限的环境、需要精确控制缓存大小的系统
     * <p>
     * <b>策略逻辑：</b>
     * <ul>
     *   <li>利用率>90%: K=5 (极严格保护，只保留最热数据)</li>
     *   <li>利用率70-90%: K=4 (严格保护)</li>
     *   <li>利用率50-70%: K=3 (适度保护)</li>
     *   <li>利用率<50%: K=2 (宽松策略)</li>
     * </ul>
     *
     * @param <K> 缓存键的类型
     * @param <V> 缓存值的类型
     * @return K值计算函数
     */
    public static <K, V> KValueFunction<K, V> byUtilization() {
        return context -> {
            double util = context.getCacheUtilization();
            if (util > 0.9) return 5;
            if (util > 0.7) return 4;
            if (util > 0.5) return 3;
            return 2;
        };
    }

    /**
     * 基于数据类型的策略 - 不同类型数据不同保护级别
     * <p>
     * <b>适用场景：</b>多媒体系统、文件缓存、混合数据类型的缓存
     * <p>
     * <b>策略逻辑：</b>
     * <ul>
     *   <li>图片/视频数据: K=4 (大文件，访问有规律)</li>
     *   <li>缓存/会话数据: K=3 (中等重要性)</li>
     *   <li>配置/元数据: K=1 (通常只访问一次)</li>
     *   <li>其他数据: K=2 (默认策略)</li>
     * </ul>
     *
     * @param <K> 缓存键的类型
     * @param <V> 缓存值的类型
     * @return K值计算函数
     */
    public static <K, V> KValueFunction<K, V> byDataType() {
        return context -> {
            V value = context.getValue();
            if (value == null) return 2;
            
            String className = value.getClass().getSimpleName().toLowerCase();
            if (className.contains("image") || className.contains("video") || className.contains("file")) {
                return 4; // 多媒体文件，通常有规律的访问模式
            }
            if (className.contains("cache") || className.contains("session") || className.contains("user")) {
                return 3; // 用户相关数据，中等重要性
            }
            if (className.contains("config") || className.contains("metadata") || className.contains("setting")) {
                return 1; // 配置数据，通常只读取一次
            }
            return 2; // 默认策略
        };
    }

    /**
     * 基于时间段的策略 - 工作时间vs非工作时间
     * <p>
     * <b>适用场景：</b>企业应用、办公系统、有明显时间规律的业务
     * <p>
     * <b>策略逻辑：</b>
     * <ul>
     *   <li>工作时间高峰期(9-11, 13-14, 17-19): K=3</li>
     *   <li>稳定访问期(20-22): K=4</li>
     *   <li>其他时段: K=2</li>
     * </ul>
     *
     * @param <K> 缓存键的类型
     * @param <V> 缓存值的类型
     * @return K值计算函数
     */
    public static <K, V> KValueFunction<K, V> byTimeOfDay() {
        return context -> {
            int hour = java.time.LocalTime.now().getHour();
            // 业务高峰期
            if ((hour >= 9 && hour <= 11) || (hour >= 13 && hour <= 14) || (hour >= 17 && hour <= 19)) {
                return 3;
            }
            // 稳定访问期
            if (hour >= 20 && hour <= 22) {
                return 4;
            }
            return 2; // 其他时段
        };
    }

    /**
     * 智能组合策略 - 利用率和访问模式的加权组合
     * <p>
     * <b>适用场景：</b>复杂的生产环境、需要均衡多种因素的系统
     * <p>
     * 这是一个典型的组合策略，综合考虑缓存利用率（权重60%）和访问频率（权重40%）
     *
     * @param <K> 缓存键的类型
     * @param <V> 缓存值的类型
     * @return K值计算函数
     */
    public static <K, V> KValueFunction<K, V> smart() {
        return weighted(byUtilization(), 0.6, adaptive(), 0.4);
    }

    /**
     * 组合策略：两个策略的加权平均
     * <p>
     * <b>使用方法：</b>
     * <pre>{@code
     * // 白天重视访问频率，晚上重视缓存利用率
     * KValueFunction<String, Object> strategy = KValueStrategies.weighted(
     *     KValueStrategies.adaptive(), 0.7,    // 70%权重给访问频率策略
     *     KValueStrategies.byUtilization(), 0.3 // 30%权重给利用率策略
     * );
     * }</pre>
     * 
     * @param strategy1 第一个策略
     * @param weight1   第一个策略的权重
     * @param strategy2 第二个策略  
     * @param weight2   第二个策略的权重
     * @param <K>       缓存键的类型
     * @param <V>       缓存值的类型
     * @return 组合后的K值计算函数
     */
    public static <K, V> KValueFunction<K, V> weighted(
            KValueFunction<K, V> strategy1, double weight1,
            KValueFunction<K, V> strategy2, double weight2) {
        return context -> {
            int k1 = strategy1.apply(context);
            int k2 = strategy2.apply(context);
            double totalWeight = weight1 + weight2;
            return (int) Math.round((k1 * weight1 + k2 * weight2) / totalWeight);
        };
    }

    /**
     * 条件策略：根据条件选择不同的策略
     * <p>
     * <b>使用示例：</b>
     * <pre>{@code
     * // 工作时间使用严格策略，非工作时间使用宽松策略
     * KValueFunction<String, Object> strategy = KValueStrategies.conditional(
     *     context -> {
     *         int hour = java.time.LocalTime.now().getHour();
     *         return hour >= 9 && hour <= 18; // 工作时间判断
     *     },
     *     KValueStrategies.fixed(4),  // 工作时间：严格保护
     *     KValueStrategies.fixed(2)   // 非工作时间：温和策略
     * );
     * }</pre>
     * 
     * @param condition     条件判断函数
     * @param trueStrategy  条件为true时使用的策略
     * @param falseStrategy 条件为false时使用的策略
     * @param <K>           缓存键的类型
     * @param <V>           缓存值的类型
     * @return 条件策略函数
     */
    public static <K, V> KValueFunction<K, V> conditional(
            Function<AccessContext<K, V>, Boolean> condition,
            KValueFunction<K, V> trueStrategy,
            KValueFunction<K, V> falseStrategy) {
        return context -> condition.apply(context) ? 
            trueStrategy.apply(context) : falseStrategy.apply(context);
    }
}
