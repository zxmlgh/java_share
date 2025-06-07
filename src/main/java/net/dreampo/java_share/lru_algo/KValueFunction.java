package net.dreampo.java_share.lru_algo;

/**
 * K值计算函数接口
 * <p>
 * 业务方可以通过实现此接口来自定义K值计算逻辑，
 * 根据具体的业务场景和数据特征动态调整缓存策略。
 * <p>
 * <b>实现建议：</b>
 * <ul>
 *   <li>考虑数据的访问模式（一次性访问 vs 周期性访问）</li>
 *   <li>根据数据重要性调整K值（重要数据K值更高）</li>
 *   <li>考虑系统资源状况（内存紧张时降低K值）</li>
 *   <li>避免返回过大的K值（建议范围：1-10）</li>
 * </ul>
 *
 * @param <K> 缓存键的类型
 * @param <V> 缓存值的类型
 *
 * @version 1.0
 *
 */
@FunctionalInterface
public interface KValueFunction<K, V> {
    /**
     * 根据访问上下文计算K值
     *
     * @param context 包含当前访问信息的上下文对象
     * @return 计算得出的K值，建议范围1-10
     */
    int apply(AccessContext<K, V> context);
}
