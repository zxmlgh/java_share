package net.dreampo.java_share.examples.slide_win;

import java.util.*;

import static java.lang.System.out;

/**
 * 滑动窗口算法教学演示
 * 
 * 通过对比展示三种实现方式，帮助理解滑动窗口算法的优化过程
 * 
 * @author dreampo
 */
public class SlidingFixedWindowTeachingDemo {
    
    /**
     * 算法结果类，包含结果数组和循环次数
     */
    static class AlgorithmResult {
        int[] result;
        int loopCount;
        
        AlgorithmResult(int[] result, int loopCount) {
            this.result = result;
            this.loopCount = loopCount;
        }
    }
    
    /**
     * 方法1：暴力解法 - O(n*k)
     * 最直观但效率最低的实现
     * 
     * 添加了循环计数器来统计实际执行的循环次数
     */
    public static int[] maxSlidingWindowBruteForce(int[] nums, int k) {
        return maxSlidingWindowBruteForce(nums, k, true);
    }
    
    /**
     * 方法1：暴力解法（带打印控制）
     */
    public static int[] maxSlidingWindowBruteForce(int[] nums, int k, boolean printStats) {
        if (nums == null || nums.length == 0 || k <= 0) {
            return new int[0];
        }
        
        int n = nums.length;
        int[] result = new int[n - k + 1];
        int loopCount = 0; //统计循环次数
        
        // 对每个窗口位置
        for (int i = 0; i <= n - k; i++) {
            loopCount++; // 外层循环计数
            int max = nums[i];
            // 遍历窗口内的所有元素找最大值
            for (int j = i + 1; j < i + k; j++) {
                loopCount++; // 内层循环计数
                max = Math.max(max, nums[j]);
            }
            result[i] = max;
        }
        
        // 根据参数决定是否打印
        if (printStats) {
            out.printf("暴力解法循环次数: %d (数组长度:%d, 窗口大小:%d)\n", 
                       loopCount, n, k);
        }
        
        return result;
    }
    
    /**
     * 获取暴力解法的循环次数（不打印）
     */
    public static AlgorithmResult maxSlidingWindowBruteForceWithCount(int[] nums, int k) {
        if (nums == null || nums.length == 0 || k <= 0) {
            return new AlgorithmResult(new int[0], 0);
        }
        
        int n = nums.length;
        int[] result = new int[n - k + 1];
        int loopCounter = 0;
        
        // 对每个窗口位置
        for (int i = 0; i <= n - k; i++) {
            loopCounter++; // 外层循环计数
            int max = nums[i];
            // 遍历窗口内的所有元素找最大值
            for (int j = i + 1; j < i + k; j++) {
                loopCounter++; // 内层循环计数
                max = Math.max(max, nums[j]);
            }
            result[i] = max;
        }
        
        return new AlgorithmResult(result, loopCounter);
    }
    
    /**
     * 方法2：优先队列解法 - O(n*logk)
     * 使用堆来维护窗口内的最大值
     * 
     * 添加了循环计数器来统计实际执行的循环次数
     */
    public static int[] maxSlidingWindowHeap(int[] nums, int k) {
        if (nums == null || nums.length == 0 || k <= 0) {
            return new int[0];
        }
        
        int n = nums.length;
        int[] result = new int[n - k + 1];
        int loopCount = 0;
        
        // 最大堆：存储 (值, 索引) 对
        PriorityQueue<int[]> maxHeap = new PriorityQueue<>((a, b) -> b[0] - a[0]);
        
        // 初始化第一个窗口
        for (int i = 0; i < k; i++) {
            loopCount++; // 初始化循环计数
            maxHeap.offer(new int[]{nums[i], i});
        }
        result[0] = maxHeap.peek()[0];
        
        // 滑动窗口
        for (int i = k; i < n; i++) {
            loopCount++; // 主循环计数
            // 加入新元素
            maxHeap.offer(new int[]{nums[i], i});
            
            // 移除超出窗口范围的元素
            while (!maxHeap.isEmpty() && maxHeap.peek()[1] < i - k + 1) {
                loopCount++; // while循环计数
                maxHeap.poll();
            }
            
            result[i - k + 1] = maxHeap.peek()[0];
        }
        
        // 打印循环次数统计
        out.printf("优先队列循环次数: %d (数组长度:%d, 窗口大小:%d)\n", 
                   loopCount, n, k);
        
        return result;
    }
    
    /**
     * 方法3：双端队列解法 - O(n) 【最优解】
     * 使用单调递减队列维护窗口最大值
     * 
     * 添加了循环计数器来统计实际执行的循环次数
     */
    public static int[] maxSlidingWindowDeque(int[] nums, int k) {
        if (nums == null || nums.length == 0 || k <= 0) {
            return new int[0];
        }
        
        int n = nums.length;
        int[] result = new int[n - k + 1];
        int loopCount = 0;//统计循环次数
        
        // 双端队列存储索引，保证对应的值单调递减
        Deque<Integer> deque = new ArrayDeque<>();
        
        for (int i = 0; i < n; i++) {
            loopCount++; // 主循环计数
            
            // 步骤1：移除超出窗口的元素
            while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
                loopCount++; // 移出窗口循环计数
                deque.pollFirst();
            }
            
            // 步骤2：维护单调性 - 移除所有比当前元素小的元素
            while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
                loopCount++; // 极值和单调循环计数
                deque.pollLast();
            }
            
            // 步骤3：把当前元素 添加到窗口中
            deque.offerLast(i);
            
            // 步骤4：记录窗口最大值
            if (i >= k - 1) {
                result[i - k + 1] = nums[deque.peekFirst()];
            }
        }
        
        // 打印循环次数统计
        out.printf("双端队列循环次数: %d (数组长度:%d, 窗口大小:%d)\n", 
                   loopCount, n, k);
        
        return result;
    }
    
    /**
     * 可视化演示滑动窗口过程
     */
    public static void visualizeSliding(int[] nums, int k) {
        out.println("\n滑动窗口过程可视化：");
        out.println("数组: " + Arrays.toString(nums));
        out.println("窗口大小: " + k);
        out.println();
        
        Deque<Integer> deque = new ArrayDeque<>();
        
        for (int i = 0; i < nums.length; i++) {
            // 移除过期元素
            while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
                deque.pollFirst();
            }
            
            // 维护单调性
            while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
                deque.pollLast();
            }
            
            deque.offerLast(i);
            
            // 打印当前状态
            if (i >= k - 1) {
                out.printf("窗口[%d-%d]: ", i - k + 1, i);
                
                // 显示窗口内容
                out.print("[");
                for (int j = i - k + 1; j <= i; j++) {
                    if (j > i - k + 1) out.print(", ");
                    if (j == deque.peekFirst()) {
                        out.printf("*%d*", nums[j]); // 标记最大值
                    } else {
                        out.print(nums[j]);
                    }
                }
                out.print("] ");
                
                // 显示队列状态
                out.print("队列: [");
                boolean first = true;
                for (int idx : deque) {
                    if (!first) out.print(", ");
                    out.printf("%d(索引%d)", nums[idx], idx);
                    first = false;
                }
                out.println("]");
            }
        }
    }
    
    /**
     * 性能测试
     */
    public static void performanceTest(int arraySize, int windowSize) {
        // 生成随机数组
        Random rand = new Random(42);
        int[] nums = new int[arraySize];
        for (int i = 0; i < arraySize; i++) {
            nums[i] = rand.nextInt(100);
        }
        
        out.printf("\n性能测试 - 数组大小: %d, 窗口大小: %d\n", 
                         arraySize, windowSize);
        out.println("=" .repeat(50));
        
        // 存储测试结果
        Map<String, Long> results = new LinkedHashMap<>();
        

        long start = System.nanoTime();
        maxSlidingWindowBruteForce(nums, windowSize);
        long bruteTime = System.nanoTime() - start;
        results.put("暴力解法", bruteTime);

        // 测试堆解法
        start = System.nanoTime();
        maxSlidingWindowHeap(nums, windowSize);
        long heapTime = System.nanoTime() - start;
        results.put("优先队列", heapTime);
        
        // 测试双端队列解法
        start = System.nanoTime();
        maxSlidingWindowDeque(nums, windowSize);
        long dequeTime = System.nanoTime() - start;
        results.put("双端队列", dequeTime);
        
        // 找出最优算法
        String bestMethod = "";
        long bestTime = Long.MAX_VALUE;
        for (Map.Entry<String, Long> entry : results.entrySet()) {
            if (entry.getValue() < bestTime) {
                bestTime = entry.getValue();
                bestMethod = entry.getKey();
            }
        }
        
        // 输出结果
        for (Map.Entry<String, Long> entry : results.entrySet()) {
            String method = entry.getKey();
            long time = entry.getValue();
            out.printf("%s: %.2f ms", method, time / 1_000_000.0);
            if (method.equals(bestMethod)) {
                out.print(" (最优)");
            }
            out.println();
        }
        
        // 计算性能提升
        if (results.size() >= 2) {
            List<Long> times = new ArrayList<>(results.values());
            Collections.sort(times);
            if (times.size() >= 2 && times.get(0) > 0) {
                out.printf("最优算法相比次优算法性能提升: %.1fx\n", 
                                 (double)times.get(1) / times.get(0));
            }
        }
    }

    /**
     * 循环次数对比测试
     */
    public static void loopCountComparison() {
        out.println("\n\n3. 循环次数对比测试");
        out.println("=" .repeat(80));
        
        // 测试不同规模
        int[][] testCases = {
            {10, 3},     // 小规模
            {100, 10},   // 中规模
            {1000, 50},  // 大规模
            {5000, 100}  // 超大规模
        };
        
        for (int[] testCase : testCases) {
            int arraySize = testCase[0];
            int windowSize = testCase[1];
            
            // 生成测试数据
            int[] nums = new int[arraySize];
            Random rand = new Random(42);
            for (int i = 0; i < arraySize; i++) {
                nums[i] = rand.nextInt(100);
            }
            
            out.printf("\n数组大小: %d, 窗口大小: %d\n", arraySize, windowSize);
            out.println("-".repeat(60));
            
            // 保存原始输出流
            java.io.PrintStream originalOut = System.out;
            
            // 创建一个ByteArrayOutputStream来捕获输出
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.PrintStream ps = new java.io.PrintStream(baos);
            
            // 重定向输出
            System.setOut(ps);
            
            // 执行算法
            maxSlidingWindowBruteForce(nums, windowSize);
            String bruteOutput = baos.toString();
            baos.reset();
            
            maxSlidingWindowHeap(nums, windowSize);
            String heapOutput = baos.toString();
            baos.reset();
            
            maxSlidingWindowDeque(nums, windowSize);
            String dequeOutput = baos.toString();
            
            // 恢复输出流
            System.setOut(originalOut);
            
            // 提取循环次数
            int bruteLoops = extractLoopCount(bruteOutput);
            int heapLoops = extractLoopCount(heapOutput);
            int dequeLoops = extractLoopCount(dequeOutput);
            
            // 显示结果
            out.printf("暴力解法: %,d 次循环\n", bruteLoops);
            out.printf("优先队列: %,d 次循环 (相比暴力减少 %.1f%%)\n", 
                      heapLoops, (1.0 - (double)heapLoops/bruteLoops) * 100);
            out.printf("双端队列: %,d 次循环 (相比暴力减少 %.1f%%)\n", 
                      dequeLoops, (1.0 - (double)dequeLoops/bruteLoops) * 100);
            
            // 理论值对比
            int windowCount = arraySize - windowSize + 1;
            int theoryBrute = windowCount + windowCount * (windowSize - 1);
            out.printf("理论值 - 暴力: %,d (实际/理论 = %.2f)\n", 
                      theoryBrute, (double)bruteLoops/theoryBrute);
        }
    }
    
    /**
     * 从输出字符串中提取循环次数
     */
    private static int extractLoopCount(String output) {
        // 匹配 "循环次数: 数字" 的模式
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("循环次数: (\\d+)");
        java.util.regex.Matcher matcher = pattern.matcher(output);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return -1;
    }

    /**
     * 演示如何使用循环计数功能
     */
    public static void demonstrateLoopCounting() {
        out.println("\n\n4. 循环计数功能演示");
        out.println("=" .repeat(50));
        
        int[] testArray = {1, 3, -1, -3, 5, 3, 6, 7};
        int k = 3;
        
        out.println("测试数组: " + Arrays.toString(testArray));
        out.println("窗口大小: " + k);
        out.println();
        
        // 方式1：使用返回AlgorithmResult的方法
        AlgorithmResult result = maxSlidingWindowBruteForceWithCount(testArray, k);
        out.println("方式1 - 使用AlgorithmResult:");
        out.printf("  结果: %s\n", Arrays.toString(result.result));
        out.printf("  循环次数: %d\n", result.loopCount);
        
        // 方式2：使用打印参数控制
        out.println("\n方式2 - 使用打印参数控制:");
        out.println("  打印开启:");
        maxSlidingWindowBruteForce(testArray, k, true);
        
        out.println("  打印关闭:");
        int[] silentResult = maxSlidingWindowBruteForce(testArray, k, false);
        out.println("  结果: " + Arrays.toString(silentResult) + " (无循环次数输出)");
        
        // 循环次数公式验证
        int n = testArray.length;
        int windowCount = n - k + 1;
        int expectedLoops = windowCount + windowCount * (k - 1);
        out.println("\n循环次数公式验证:");
        out.printf("  窗口数量: %d\n", windowCount);
        out.printf("  每个窗口内部循环: %d\n", k - 1);
        out.printf("  理论循环次数: %d + %d × %d = %d\n", 
                   windowCount, windowCount, k - 1, expectedLoops);
        out.printf("  实际循环次数: %d\n", result.loopCount);
        out.printf("  验证: %s\n", expectedLoops == result.loopCount ? "✓ 正确" : "✗ 错误");
    }

    /**
     * 主函数
     */
    public static void main(String[] args) {
        out.println("=== 滑动窗口算法完整教学演示 ===");
        // 1. 小规模演示
//        int[] demo = {1, 3, -1, -3, 5, 3, 6, 7};
        int[] 过去每天销量 = {300, 100, 400, 200, 500, 900, 200};
        int 连续天数 = 3;
        
        out.println("\n1. 算法演示");
        out.println("输入数组: " + Arrays.toString(过去每天销量));
        out.println("窗口大小: " + 连续天数);
        
        out.println("\n执行三种算法并统计循环次数：");
        out.println("-".repeat(50));
        
        int[] result1 = maxSlidingWindowBruteForce(过去每天销量, 连续天数);
        int[] result2 = maxSlidingWindowHeap(过去每天销量, 连续天数);
        int[] result3 = maxSlidingWindowDeque(过去每天销量, 连续天数);
        
        out.println("\n三种方法的结果（应该相同）：");
        out.println("暴力解法: " + Arrays.toString(result1));
        out.println("优先队列: " + Arrays.toString(result2));
        out.println("双端队列: " + Arrays.toString(result3));
        
        // 理论循环次数分析
        int n = 过去每天销量.length;
        int windowCount = n - 连续天数 + 1;
        int bruteForceTheory = windowCount * 连续天数;
        out.println("\n理论循环次数分析：");
        out.printf("- 暴力解法理论值: O(n*k) = %d × %d = %d\n", 
                   windowCount, 连续天数, bruteForceTheory);
        out.println("- 优先队列理论值: O(n*log k)");
        out.println("- 双端队列理论值: O(n)");
        
        // 2. 可视化过程
        visualizeSliding(过去每天销量, 连续天数);
        
        // 3. 性能对比
        out.println("\n\n2. 性能对比测试");
        performanceTest(1000, 50);
        performanceTest(10000, 100);
        performanceTest(100000, 500);
        
        // 4. 循环次数对比
        loopCountComparison();
        
        // 5. 循环计数功能演示
        demonstrateLoopCounting();

    }
}
