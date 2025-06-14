package net.dreampo.java_share.examples.slide_win;

import java.util.HashMap;
import java.util.Map;

/**
 * 经典的动态滑动窗口解决方案集合
 */
public class DynamicSlidingWindowTeachingDemo {

    /**
     * 给定一个字符串s，请找出其中不含有重复字符的最长子串的长度。
     * 
     * @param s 输入字符串
     * @return 最长无重复字符子串的长度
     */
    public int lengthOfLongestSubstrWithoutRepeating(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        
        // 窗口内字符及其出现位置的映射
        Map<Character, Integer> charIndexMap = new HashMap<>();
        int maxLength = 0;
        int left = 0; // 窗口左边界
        
        // 步骤1：右指针不断向右扩展窗口
        for (int right = 0; right < s.length(); right++) {
            char currentChar = s.charAt(right);
            
            // 步骤2：如果当前字符已存在于窗口中，需要收缩窗口
            if (charIndexMap.containsKey(currentChar)) {
                // 将左边界移动到重复字符的下一个位置
                // 注意：需要取max，避免左指针回退
                left = Math.max(left, charIndexMap.get(currentChar) + 1);
            }
            
            // 步骤3：更新字符的最新位置
            charIndexMap.put(currentChar, right);
            
            // 步骤4：更新最大长度（当前窗口大小 = right - left + 1）
            maxLength = Math.max(maxLength, right - left + 1);
        }
        
        // 返回结果：最长无重复字符子串的长度
        return maxLength;
    }

    /**
     * 给定字符串s和t，返回s中涵盖t所有字符的最小子串。
     * 
     * @param s 源字符串
     * @param t 目标字符串
     * @return 最小覆盖子串，如果不存在则返回空字符串
     */
    public String minWindowSubstring(String s, String t) {
        if (s == null || t == null || s.length() < t.length()) {
            return "";
        }
        
        // 目标字符串t中各字符的计数
        Map<Character, Integer> targetCount = new HashMap<>();
        for (char c : t.toCharArray()) {
            targetCount.put(c, targetCount.getOrDefault(c, 0) + 1);
        }
        
        // 窗口内各字符的计数
        Map<Character, Integer> windowCount = new HashMap<>();
        
        // 记录满足条件的字符个数
        int valid = 0;
        int required = targetCount.size();
        
        // 记录最小窗口的信息：长度，左边界，右边界
        int[] minWindow = {Integer.MAX_VALUE, 0, 0};
        
        int left = 0;
        
        // 步骤1：右指针向右扩展窗口
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            windowCount.put(c, windowCount.getOrDefault(c, 0) + 1);
            
            // 步骤2：检查当前字符是否满足目标要求
            if (targetCount.containsKey(c) && 
                windowCount.get(c).intValue() == targetCount.get(c).intValue()) {
                valid++;
            }
            
            // 步骤3：当窗口包含了t的所有字符时，尝试收缩窗口
            while (valid == required && left <= right) {
                // 更新最小窗口
                if (right - left + 1 < minWindow[0]) {
                    minWindow[0] = right - left + 1;
                    minWindow[1] = left;
                    minWindow[2] = right;
                }
                
                // 步骤4：收缩左边界
                char leftChar = s.charAt(left);
                windowCount.put(leftChar, windowCount.get(leftChar) - 1);
                
                // 检查收缩后是否仍满足条件
                if (targetCount.containsKey(leftChar) && 
                    windowCount.get(leftChar) < targetCount.get(leftChar)) {
                    valid--;
                }
                
                left++;
            }
        }
        
        // 返回结果：最小覆盖子串
        return minWindow[0] == Integer.MAX_VALUE ? "" : 
               s.substring(minWindow[1], minWindow[2] + 1);
    }

    /**
     * 找出数组中满足和≥target的最短连续子数组长度。
     * 
     * @param target 目标和
     * @param nums 正整数数组
     * @return 最短子数组长度，如果不存在则返回0
     */
    public int minSubArrayLen(int target, int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        
        int minLength = Integer.MAX_VALUE;
        int windowSum = 0; // 窗口内元素和
        int left = 0;
        
        // 步骤1：右指针向右扩展窗口
        for (int right = 0; right < nums.length; right++) {
            // 将右边界元素加入窗口
            windowSum += nums[right];
            
            // 步骤2：当窗口和≥target时，尝试收缩窗口
            while (windowSum >= target) {
                // 更新最小长度
                minLength = Math.min(minLength, right - left + 1);
                
                // 步骤3：收缩左边界，移除左边元素
                windowSum -= nums[left];
                left++;
            }
        }
        
        // 返回结果：最短子数组长度，如果不存在返回0
        return minLength == Integer.MAX_VALUE ? 0 : minLength;
    }

    /**
     * 农场有一排果树，你有两个篮子，每个篮子只能装一种水果。求最多能收集的水果数。
     * 
     * @param fruits 果树数组，fruits[i]表示第i棵树的水果类型
     * @return 最多能收集的水果数
     */
    public int totalFruit(int[] fruits) {
        if (fruits == null || fruits.length == 0) {
            return 0;
        }
        
        // 篮子（窗口）中水果类型及其数量
        Map<Integer, Integer> basket = new HashMap<>();
        int maxFruits = 0;
        int left = 0;
        
        // 步骤1：右指针向右扩展窗口（采摘水果）
        for (int right = 0; right < fruits.length; right++) {
            // 将当前水果放入篮子
            basket.put(fruits[right], basket.getOrDefault(fruits[right], 0) + 1);
            
            // 步骤2：当篮子中水果种类超过2种时，需要收缩窗口
            while (basket.size() > 2) {
                // 移除左边的水果
                int leftFruit = fruits[left];
                basket.put(leftFruit, basket.get(leftFruit) - 1);
                
                // 如果该种水果数量为0，从篮子中完全移除
                if (basket.get(leftFruit) == 0) {
                    basket.remove(leftFruit);
                }
                
                left++;
            }
            
            // 步骤3：更新最大水果数（当前窗口大小）
            maxFruits = Math.max(maxFruits, right - left + 1);
        }
        
        // 返回结果：最多能收集的水果数
        return maxFruits;
    }
    
    /**
     * 测试方法
     */
    public static void main(String[] args) {
        DynamicSlidingWindowTeachingDemo demo = new DynamicSlidingWindowTeachingDemo();
        
        // 测试1：无重复字符的最长子串
        System.out.println("测试1 - 无重复字符的最长子串：");
        System.out.println("输入: \"abcbbcbb\"");
        System.out.println("输出: " + demo.lengthOfLongestSubstrWithoutRepeating("abcbbcbb"));
        System.out.println("解释: 最长子串是 \"abc\"，长度为3\n");
        
        // 测试2：最小覆盖子串
        System.out.println("测试2 - 最小覆盖子串：");
        System.out.println("输入: s = \"ADOBECODEBANC\", t = \"ABC\"");
        System.out.println("输出: " + demo.minWindowSubstring("ADOBECODEBANC", "ABC"));
        System.out.println("解释: 最小覆盖子串是 \"BANC\"\n");
        
        // 测试3：长度最小的子数组
        System.out.println("测试3 - 长度最小的子数组：");
        System.out.println("输入: target = 7, nums = [2,3,1,2,4,3]");
        System.out.println("输出: " + demo.minSubArrayLen(7, new int[]{2,3,1,2,4,3}));
        System.out.println("解释: 子数组 [4,3] 是该条件下的长度最小的子数组\n");
        
        // 测试4：水果成篮
        System.out.println("测试4 - 水果成篮：");
        System.out.println("输入: fruits = [1,2,1]");
        System.out.println("输出: " + demo.totalFruit(new int[]{1,2,1}));
        System.out.println("解释: 可以收集全部3个水果");
        
        System.out.println("\n输入: fruits = [0,1,2,2]");
        System.out.println("输出: " + demo.totalFruit(new int[]{0,1,2,2}));
        System.out.println("解释: 可以收集 [1,2,2] 这3个水果");
    }
}
