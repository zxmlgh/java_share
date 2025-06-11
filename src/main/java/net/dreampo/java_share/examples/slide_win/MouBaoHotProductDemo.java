package net.dreampo.java_share.examples.slide_win;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.System.out;

/**
 * 某宝热门商品分析演示 - 基于时间的滑动窗口版本
 * <p>
 * 演示如何使用基于时间的滑动窗口分析不同品类下的热门商品
 *
 * @author dreampo
 */
public class MouBaoHotProductDemo {

    /**
     * 商品事件 - 包含品类信息
     *
     * @param category  品类
     * @param productId 商品ID
     * @param time      事件时间
     * @param score     热度分数
     */
    record ProductEvent(String category, String productId, LocalDateTime time, int score) {

        @Override
        public String toString() {
            return String.format("Event[%s,%s,%s,%d]", category, productId,
                    time.format(DateTimeFormatter.ofPattern("HH:mm:ss")), score);
        }
    }

    /**
     * 窗口分析结果
     *
     * @param categoryTopProducts 品类 -> Top商品列表
     * @param eventsInWindow      窗口内事件总数（用于调试）
     */
    record WindowResult(LocalDateTime windowStart, LocalDateTime windowEnd,
                        Map<String, List<ProductScore>> categoryTopProducts, int eventsInWindow) {
    }

    /**
     * 商品得分
     */
    record ProductScore(String productId, int score) {
    }

    /**
     * 滑动窗口状态维护类
     */
    static class SlidingWindowState {
        private final Map<String, Integer> productScores = new HashMap<>();
        private final Deque<ProductEvent> eventsInWindow = new LinkedList<>();
        private int totalEvents = 0;

        void addEvent(ProductEvent event) {
            eventsInWindow.addLast(event);
            productScores.merge(event.productId, event.score, Integer::sum);
            totalEvents++;
        }

        void removeEvent(ProductEvent event) {
            eventsInWindow.removeFirst();
            int currentScore = productScores.get(event.productId);
            if (currentScore - event.score <= 0) {
                productScores.remove(event.productId);
            } else {
                productScores.put(event.productId, currentScore - event.score);
            }
            totalEvents--;
        }

        List<ProductScore> getTopProducts(int topN) {
            return productScores.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(topN)
                    .map(e -> new ProductScore(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());
        }

        int getTotalEvents() {
            return totalEvents;
        }
    }

    /**
     * 基于时间的滑动窗口分析（优化版本）
     *
     * @param eventsByCategory     按品类分组的事件列表（每个品类内按时间排序）
     * @param windowSizeMinutes    窗口大小（分钟）
     * @param slideIntervalMinutes 滑动间隔（分钟）
     * @param topN                 每个品类的Top N商品
     */
    public static List<WindowResult> analyzeWithSlidingWindow(
            Map<String, List<ProductEvent>> eventsByCategory,
            int windowSizeMinutes, int slideIntervalMinutes, int topN) {

        List<WindowResult> results = new ArrayList<>();

        // 找出所有事件的时间范围
        LocalDateTime minTime = null;
        LocalDateTime maxTime = null;

        for (List<ProductEvent> events : eventsByCategory.values()) {
            if (!events.isEmpty()) {
                LocalDateTime categoryMinTime = events.get(0).time;
                LocalDateTime categoryMaxTime = events.get(events.size() - 1).time;

                if (minTime == null || categoryMinTime.isBefore(minTime)) {
                    minTime = categoryMinTime;
                }
                if (maxTime == null || categoryMaxTime.isAfter(maxTime)) {
                    maxTime = categoryMaxTime;
                }
            }
        }

        if (minTime == null || maxTime == null) {
            return results;
        }

        // 为每个品类维护滑动窗口状态
        Map<String, SlidingWindowState> categoryWindowStates = new HashMap<>();
        Map<String, Integer> categoryEventIndices = new HashMap<>(); // 每个品类当前处理到的事件索引

        for (String category : eventsByCategory.keySet()) {
            categoryWindowStates.put(category, new SlidingWindowState());
            categoryEventIndices.put(category, 0);
        }

        // 滑动窗口处理
        LocalDateTime windowStart = minTime;
        int windowCount = 0;
        long addOperations = 0;
        long removeOperations = 0;

        while (windowStart.isBefore(maxTime) || windowStart.isEqual(maxTime)) {
            LocalDateTime windowEnd = windowStart.plusMinutes(windowSizeMinutes);

            // 处理每个品类
            for (Map.Entry<String, List<ProductEvent>> entry : eventsByCategory.entrySet()) {
                String category = entry.getKey();
                List<ProductEvent> categoryEvents = entry.getValue();
                SlidingWindowState windowState = categoryWindowStates.get(category);
                int currentIndex = categoryEventIndices.get(category);

                // 移除超出窗口的事件
                while (!windowState.eventsInWindow.isEmpty()) {
                    ProductEvent firstEvent = windowState.eventsInWindow.peekFirst();
                    if (firstEvent.time.isBefore(windowStart)) {
                        windowState.removeEvent(firstEvent);
                        removeOperations++;
                    } else {
                        break;
                    }
                }

                // 添加新进入窗口的事件
                while (currentIndex < categoryEvents.size()) {
                    ProductEvent event = categoryEvents.get(currentIndex);
                    if (event.time.isBefore(windowEnd)) {
                        if (!event.time.isBefore(windowStart)) {
                            windowState.addEvent(event);
                            addOperations++;
                        }
                        currentIndex++;
                    } else {
                        break;
                    }
                }

                categoryEventIndices.put(category, currentIndex);
            }

            // 收集当前窗口的结果
            Map<String, List<ProductScore>> categoryTopProducts = new HashMap<>();
            int totalEventsInWindow = 0;

            for (Map.Entry<String, SlidingWindowState> entry : categoryWindowStates.entrySet()) {
                String category = entry.getKey();
                SlidingWindowState windowState = entry.getValue();
                List<ProductScore> topProducts = windowState.getTopProducts(topN);
                if (!topProducts.isEmpty()) {
                    categoryTopProducts.put(category, topProducts);
                }
                totalEventsInWindow += windowState.getTotalEvents();
            }

            results.add(new WindowResult(windowStart, windowEnd, categoryTopProducts, totalEventsInWindow));

            // 滑动到下一个窗口
            windowStart = windowStart.plusMinutes(slideIntervalMinutes);
            windowCount++;
        }

        // 输出性能统计
        out.println("\n⚡ 滑动窗口性能统计：");
        out.println("-".repeat(50));
        out.printf("总窗口数：%d\n", windowCount);
        out.printf("添加操作次数：%d\n", addOperations);
        out.printf("移除操作次数：%d\n", removeOperations);
        out.printf("总操作次数：%d\n", addOperations + removeOperations);

        // 计算总事件数
        long totalEvents = eventsByCategory.values().stream()
                .mapToLong(List::size)
                .sum();
        out.printf("总事件数：%d\n", totalEvents);

        // 如果使用朴素方法，每个窗口都要处理所有事件
        long naiveOperations = windowCount * totalEvents;
        out.printf("朴素方法操作次数：%d\n", naiveOperations);

        // 性能提升比例
        double improvement = (double) (naiveOperations - (addOperations + removeOperations)) / naiveOperations * 100;
        out.printf("性能提升：%.1f%%\n", improvement);

        return results;
    }

    /**
     * 生成模拟数据 - 返回按品类分组且时间排序的数据
     */
    public static Map<String, List<ProductEvent>> generateData() {
        Map<String, List<ProductEvent>> eventsByCategory = new HashMap<>();
        LocalDateTime baseTime = LocalDateTime.now().minusMinutes(180); // 3小时前开始

        // 定义品类和商品
        Map<String, String[]> categoryProducts = new HashMap<>();
        categoryProducts.put("手机数码", new String[]{
                "iPhone 15", "小米14", "华为Mate60", "三星S24", "OPPO Find"});
        categoryProducts.put("美妆护肤", new String[]{
                "SK-II神仙水", "雅诗兰黛", "兰蔻小黑瓶", "资生堂", "科颜氏"});
        categoryProducts.put("家用电器", new String[]{
                "戴森吹风机", "美的空调", "格力电扇", "小米净化器", "飞利浦剃须刀"});
        categoryProducts.put("玩具游戏", new String[]{
                "乐高积木", "Switch游戏机", "PS5", "泡泡玛特", "万代高达"});

        // 为每个品类生成事件
        Random random = new Random(42);

        for (Map.Entry<String, String[]> entry : categoryProducts.entrySet()) {
            String category = entry.getKey();
            String[] products = entry.getValue();
            List<ProductEvent> categoryEvents = new ArrayList<>();

            // 模拟180分钟数据
            for (int minute = 0; minute < 180; minute++) {
                LocalDateTime currentTime = baseTime.plusMinutes(minute);

                // 每个品类每分钟生成2-8个事件
                int eventCount = 2 + random.nextInt(7);
                for (int i = 0; i < eventCount; i++) {
                    // 选择商品（前2个商品更热门）
                    int productIndex;
                    if (random.nextDouble() < 0.6) {
                        productIndex = random.nextInt(2);  // 60%概率选择前2个商品
                    } else {
                        productIndex = 2 + random.nextInt(products.length - 2);
                    }

                    // 热度分在10-100之间
                    int score = 10 + random.nextInt(91) ;

                    // 特定时段某些品类会更热
                    if (minute >= 60 && minute <= 120) {
                        if (category.equals("手机数码") || category.equals("美妆护肤")) {
                            score *= 1.5;  // 高峰期加成
                        }
                    }

                    // 为确保时间精确性，添加秒和毫秒
                    LocalDateTime eventTime = currentTime.plusSeconds(random.nextInt(60));
                    categoryEvents.add(new ProductEvent(category, products[productIndex], eventTime, score));
                }
            }

            // 确保按时间排序
            categoryEvents.sort(Comparator.comparing(e -> e.time));
            eventsByCategory.put(category, categoryEvents);
        }

        return eventsByCategory;
    }

    /**
     * 可视化展示结果
     */
    private static void visualizeResults(List<WindowResult> results) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        out.println("\n🔥 基于时间窗口的品类热门商品分析：");
        out.println("=".repeat(100));

        // 只展示部分窗口结果
        int[] windowsToShow = {0, 1, results.size() / 2, results.size() - 2, results.size() - 1};

        for (int idx : windowsToShow) {
            if (idx < 0 || idx >= results.size()) continue;

            WindowResult result = results.get(idx);
            out.printf("\n⏰ 窗口 #%d [%s - %s] (事件数: %d):\n",
                    idx + 1,
                    result.windowStart.format(formatter),
                    result.windowEnd.format(formatter),
                    result.eventsInWindow);
            out.println("-".repeat(80));

            // 按品类名称排序以保证输出稳定
            TreeMap<String, List<ProductScore>> sortedCategories =
                    new TreeMap<>(result.categoryTopProducts);

            for (Map.Entry<String, List<ProductScore>> entry : sortedCategories.entrySet()) {
                String category = entry.getKey();
                List<ProductScore> topProducts = entry.getValue();

                out.printf("  📦 %s Top5: ", category);
                for (int j = 0; j < Math.min(5, topProducts.size()); j++) {
                    ProductScore ps = topProducts.get(j);
                    String medal = j == 0 ? "🥇" : j == 1 ? "🥈" : j == 2 ? "🥉" : "  ";
                    out.printf("%s%s(热度%d) ", medal, ps.productId, ps.score);
                }
                out.println();
            }
        }

        // 统计整体情况
        out.println("\n📊 整体统计：");
        out.println("-".repeat(50));
        out.printf("总窗口数：%d\n", results.size());

        // 统计每个品类的持续热门商品
        Map<String, Map<String, Integer>> categoryProductAppearances = new HashMap<>();
        for (WindowResult result : results) {
            for (Map.Entry<String, List<ProductScore>> entry : result.categoryTopProducts.entrySet()) {
                String category = entry.getKey();
                List<ProductScore> topProducts = entry.getValue();

                Map<String, Integer> productCounts = categoryProductAppearances
                        .computeIfAbsent(category, k -> new HashMap<>());

                // 只统计前3名
                for (int i = 0; i < Math.min(3, topProducts.size()); i++) {
                    String productId = topProducts.get(i).productId;
                    productCounts.merge(productId, 1, Integer::sum);
                }
            }
        }

        out.println("\n🏆 各品类持续热门商品（出现在前3名的次数）：");
        TreeMap<String, Map<String, Integer>> sortedAppearances = new TreeMap<>(categoryProductAppearances);

        for (Map.Entry<String, Map<String, Integer>> categoryEntry : sortedAppearances.entrySet()) {
            String category = categoryEntry.getKey();
            out.printf("\n%s:\n", category);

            categoryEntry.getValue().entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(3)
                    .forEach(e -> out.printf("  - %s: %d次\n", e.getKey(), e.getValue()));
        }
    }

    /**
     * 主函数
     */
    public static void main(String[] args) {
        out.println("=== 某宝热门商品基于时间的滑动窗口分析（优化版） ===\n");

        // 生成数据
        Map<String, List<ProductEvent>> eventsByCategory = generateData();

        // 统计数据信息
        int totalEvents = eventsByCategory.values().stream()
                .mapToInt(List::size)
                .sum();
        out.printf("生成事件数据：共 %d 个品类，%d 个事件\n",
                eventsByCategory.size(), totalEvents);

        for (Map.Entry<String, List<ProductEvent>> entry : eventsByCategory.entrySet()) {
            out.printf("  - %s: %d 个事件\n", entry.getKey(), entry.getValue().size());
        }

        // 设置参数
        int windowSizeMinutes = 60;      // 60分钟窗口
        int slideIntervalMinutes = 15;   // 15分钟滑动
        int topN = 5;                    // 每个品类Top 5商品

        out.printf("\n窗口大小：%d分钟 | 滑动间隔：%d分钟 | Top N：%d\n",
                windowSizeMinutes, slideIntervalMinutes, topN);

        // 执行分析
        long startTime = System.currentTimeMillis();
        List<WindowResult> results = analyzeWithSlidingWindow(
                eventsByCategory, windowSizeMinutes, slideIntervalMinutes, topN);
        long endTime = System.currentTimeMillis();

        out.printf("\n⏱️ 分析耗时：%d ms\n", endTime - startTime);

        // 展示结果
        visualizeResults(results);

        // 算法说明
        out.println("\n💡 优化算法的关键改进：");
        out.println("1. ✅ 数据按品类分组，每个品类内按时间排序");
        out.println("2. ✅ 使用真正的滑动窗口，维护窗口状态");
        out.println("3. ✅ 窗口滑动时只处理移出和移入的事件（增量计算）");
        out.println("4. ✅ 避免重复扫描，大幅提升性能");
        out.println("5. ✅ 空间复杂度优化，只维护当前窗口内的数据");

        // 业务应用
        out.println("\n🎯 实际应用场景：");
        out.println("• 实时热榜：毫秒级更新各品类热销榜单");
        out.println("• 流式处理：适用于Flink/Spark Streaming等框架");
        out.println("• 大数据场景：处理海量事件流时性能优势明显");
        out.println("• 资源优化：降低CPU和内存消耗");
    }
}
