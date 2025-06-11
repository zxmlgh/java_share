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
     * 方法1：暴力解法 - O(n*k)
     * 最直观但效率最低的实现
     */
    public static int[] maxSlidingWindowBruteForce(int[] nums, int k) {
        if (nums == null || nums.length == 0 || k <= 0) {
            return new int[0];
        }
        
        int n = nums.length;
        int[] result = new int[n - k + 1];
        
        // 对每个窗口位置
        for (int i = 0; i <= n - k; i++) {
            int max = nums[i];
            // 遍历窗口内的所有元素找最大值
            for (int j = i + 1; j < i + k; j++) {
                max = Math.max(max, nums[j]);
            }
            result[i] = max;
        }
        
        return result;
    }
    
    /**
     * 方法2：优先队列解法 - O(n*logk)
     * 使用堆来维护窗口内的最大值
     */
    public static int[] maxSlidingWindowHeap(int[] nums, int k) {
        if (nums == null || nums.length == 0 || k <= 0) {
            return new int[0];
        }
        
        int n = nums.length;
        int[] result = new int[n - k + 1];
        
        // 最大堆：存储 (值, 索引) 对
        PriorityQueue<int[]> maxHeap = new PriorityQueue<>((a, b) -> b[0] - a[0]);
        
        // 初始化第一个窗口
        for (int i = 0; i < k; i++) {
            maxHeap.offer(new int[]{nums[i], i});
        }
        result[0] = maxHeap.peek()[0];
        
        // 滑动窗口
        for (int i = k; i < n; i++) {
            // 加入新元素
            maxHeap.offer(new int[]{nums[i], i});
            
            // 移除超出窗口范围的元素
            while (!maxHeap.isEmpty() && maxHeap.peek()[1] < i - k + 1) {
                maxHeap.poll();
            }
            
            result[i - k + 1] = maxHeap.peek()[0];
        }
        
        return result;
    }
    
    /**
     * 方法3：双端队列解法 - O(n) 【最优解】
     * 使用单调递减队列维护窗口最大值
     */
    public static int[] maxSlidingWindowDeque(int[] nums, int k) {
        if (nums == null || nums.length == 0 || k <= 0) {
            return new int[0];
        }
        
        int n = nums.length;
        int[] result = new int[n - k + 1];
        
        // 双端队列存储索引，保证对应的值单调递减
        Deque<Integer> deque = new ArrayDeque<>();
        
        for (int i = 0; i < n; i++) {
            // 步骤1：移除超出窗口的元素
            while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
                deque.pollFirst();
            }
            
            // 步骤2：维护单调性 - 移除所有比当前元素小的元素
            while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
                deque.pollLast();
            }
            
            // 步骤3：把当前元素 添加到窗口中
            deque.offerLast(i);
            
            // 步骤4：记录窗口最大值
            if (i >= k - 1) {
                result[i - k + 1] = nums[deque.peekFirst()];
            }
        }
        
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
        
        int[] result1 = maxSlidingWindowBruteForce(过去每天销量, 连续天数);
        int[] result2 = maxSlidingWindowHeap(过去每天销量, 连续天数);
        int[] result3 = maxSlidingWindowDeque(过去每天销量, 连续天数);
        
        out.println("\n三种方法的结果（应该相同）：");
        out.println("暴力解法: " + Arrays.toString(result1));
        out.println("优先队列: " + Arrays.toString(result2));
        out.println("双端队列: " + Arrays.toString(result3));
        
        // 2. 可视化过程
        visualizeSliding(过去每天销量, 连续天数);
        
        // 3. 性能对比
        out.println("\n\n2. 性能对比测试");
        performanceTest(1000, 50);
        performanceTest(10000, 100);
        performanceTest(100000, 500);

    }
}
