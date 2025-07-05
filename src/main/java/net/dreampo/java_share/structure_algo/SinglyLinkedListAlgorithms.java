package net.dreampo.java_share.structure_algo;

import net.dreampo.java_share.structure_algo.helper.ParLinkedList;

import java.util.Comparator;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * @author richard.dreampo  // 默认使用系统用户名
 * @date 2025/7/4    // 自动填充日期
 */
public class SinglyLinkedListAlgorithms {

    /**
     * 将链表按照给定的判断条件进行分隔（核心方法）
     * <p>
     * 算法基本思路：
     * 使用"双指针分离"策略，通过一次遍历将链表节点根据判断条件分配到两个独立的链表中。
     * 这种方法避免了创建新节点，仅通过调整节点间的指针连接来实现分隔。
     * <p>
     * 算法详细步骤：
     * 1. 初始化阶段：
     * - 创建两个虚拟头节点（dummy nodes），分别作为两个结果链表的起始点
     * - 创建两个尾指针，用于高效地在链表尾部添加新节点
     * <p>
     * 2. 遍历分配阶段：
     * - 对原链表的每个节点，调用predicate判断函数
     * - 如果predicate返回true，将节点添加到第一个链表
     * - 如果predicate返回false，将节点添加到第二个链表
     * - 断开当前节点与原链表的连接，避免形成环
     * <p>
     * 3. 返回结果阶段：
     * - 跳过虚拟头节点，返回两个真实的链表头
     * <p>
     * 算法优势：
     * 1. 高效性：时间复杂度O(n)，只需一次遍历，每个节点的处理时间为O(1)
     * 2. 节省空间：空间复杂度O(1)，原地操作，不创建新节点
     * 3. 稳定性：保持原链表中节点的相对顺序不变
     * 4. 灵活性：支持任意复杂的判断逻辑，不限于简单的数值比较
     * 5. 可扩展性：判断条件可以是状态相关的，支持动态判断
     * 6. 类型安全：使用泛型，支持任意类型的节点值
     * <p>
     * 算法局限：
     * 1. 破坏原链表：原链表结构被破坏，如需保留原链表需要先复制
     * 2. 单向链表限制：只适用于单向链表，双向链表需要额外处理prev指针
     * 3. 不支持并发：算法不是线程安全的，并发环境需要外部同步
     * 4. predicate异常：如果predicate抛出异常，可能导致链表处于不一致状态
     * 5. 无法回滚：一旦开始执行，无法撤销已完成的部分操作
     *
     * @param <T>       节点值的类型
     * @param head      原链表的头节点
     * @param predicate 判断条件，返回true的节点放入第一个链表，返回false的放入第二个链表
     * @return 包含两个分隔后链表的结果对象
     * @throws NullPointerException 如果predicate为null
     */
    public static <T> ParLinkedList<T> partitionByPredicate(ListNode<T> head, Predicate<T> predicate) {
        if (predicate == null) {
            throw new NullPointerException("Predicate cannot be null");
        }

        // 边界条件：空链表返回两个空链表
        if (head == null) {
            return new ParLinkedList<>(null, null);
        }

        /**
         * 创建两个虚拟头节点，避免处理头节点的特殊情况
         * 存储满足条件的节点 firstDummy
         * 存储不满足条件的节点 secondDummy
         */
        ListNode<T> firstDummy = new ListNode<>(null);
        ListNode<T> secondDummy = new ListNode<>(null);

        // 维护两个尾指针，用于O(1)时间在尾部添加节点
        ListNode<T> firstTail = firstDummy;
        ListNode<T> secondTail = secondDummy;

        // 遍历原链表
        ListNode<T> current = head;
        while (current != null) {
            // 保存下一个节点的引用，因为当前节点的next会被修改
            ListNode<T> nextNode = current.next;

            // 根据判断条件决定节点的去向
            if (predicate.test(current.val)) {
                // 满足条件，添加到第一个链表
                firstTail.next = current;
                firstTail = current;
            } else {
                // 不满足条件，添加到第二个链表
                secondTail.next = current;
                secondTail = current;
            }

            // 断开当前节点与原链表的连接，防止形成环
            current.next = null;

            // 移动到下一个节点
            current = nextNode;
        }

        // 返回两个独立的链表（跳过虚拟头节点）
        return new ParLinkedList<>(firstDummy.next, secondDummy.next);
    }

    /**
     * 合并两个有序链表（支持自定义比较器）
     *
     * 算法基本思路：
     * 1. 使用双指针技术，分别指向两个链表的当前节点
     * 2. 比较两个指针所指节点的值，选择较小（或根据比较器规则）的节点加入结果链表
     * 3. 移动被选中节点所在链表的指针
     * 4. 重复步骤2-3，直到其中一个链表遍历完毕
     * 5. 将剩余链表的所有节点直接连接到结果链表的尾部
     *
     * 算法步骤：
     * 1. 创建一个虚拟头节点（dummy node）作为结果链表的起始
     * 2. 创建一个current指针，遍历结束时，是链表的尾部
     * 3. 当两个链表都不为空时：
     *    - 使用比较器比较两个链表头节点的值
     *    - 将较小值的节点连接到tail后面
     *    - 移动被选中链表的指针到下一个节点
     *    - 移动tail指针到新加入的节点
     * 4. 如果list1还有剩余节点，将其全部连接到tail后面
     * 5. 如果list2还有剩余节点，将其全部连接到tail后面
     * 6. 返回dummy.next作为合并后链表的头节点
     *
     * 算法优势：
     * 1. 时间复杂度O(n1 + n2)：每个节点只被访问一次，n1和n2分别是两个链表的长度
     * 2. 空间复杂度O(1)：只使用了常数个额外变量，不创建新节点，仅重新连接现有节点
     * 3. 支持自定义比较器，可以灵活处理不同的排序需求（升序、降序、自定义规则）
     * 4. 保持了原链表中节点的相对顺序，是稳定的合并算法
     * 5. 代码简洁，易于理解和维护
     *
     * 算法局限：
     * 1. 会破坏原有两个链表的结构，合并后原链表不再可用
     * 2. 只适用于已经排序的链表，对于无序链表需要先排序
     * 3. 当链表很长时，递归实现可能导致栈溢出（本实现使用迭代避免了这个问题）
     * 4. 比较器的性能会直接影响算法的整体性能
     * 5. 对于包含重复元素的链表，无法去重（如需去重需要额外处理）
     *
     * @param <T> 节点值的类型
     * @param list1 第一个有序链表
     * @param list2 第二个有序链表
     * @param comparator 比较器，用于定义节点的排序规则
     * @return 合并后的有序链表
     */
    public static  <T> ListNode<T> mergeTwoLists(ListNode<T> list1, ListNode<T> list2, Comparator<ListNode<T>> comparator) {
        // 边界条件处理：如果其中一个链表为空，直接返回另一个链表
        if (list1 == null) {
            return list2;
        }
        if (list2 == null) {
            return list1;
        }

        // 创建虚拟头节点，简化边界条件的处理
        ListNode<T> dummy = new ListNode<>(null);
        ListNode<T> current = dummy;

        // 当两个链表都还有节点时，进行合并
        while (list1 != null && list2 != null) {
            // 使用比较器比较两个节点
            // 如果compare结果 <= 0，说明list1的当前节点应该先加入结果链表
            if (comparator.compare(list1, list2) <= 0) {
                // 将list1的当前节点连接到结果链表
                current.next = list1;
                // list1指针后移
                list1 = list1.next;
            } else {
                // 将list2的当前节点连接到结果链表
                current.next = list2;
                // list2指针后移
                list2 = list2.next;
            }
            current = current.next;
        }

        // 处理剩余节点：将未遍历完的链表直接连接到结果链表尾部
        // 这里只会执行其中一个分支，因为至少有一个链表已经遍历完毕
        if (list1 != null) {
            current.next = list1;
        }
        if (list2 != null) {
            current.next = list2;
        }

        // 返回真正的头节点（跳过虚拟头节点）
        return dummy.next;
    }

    /**
     * 合并两个有序链表（泛型版本，使用自然顺序）
     *
     * @param <T> 节点值的类型，必须实现Comparable接口
     * @param list1 第一个有序链表
     * @param list2 第二个有序链表
     * @return 合并后的有序链表
     */
    public static  <T extends Comparable<T>> ListNode<T> mergeTwoLists(ListNode<T> list1, ListNode<T> list2) {
        return SinglyLinkedListAlgorithms.mergeTwoLists(list1, list2, Comparator.comparing(node -> node.val));
    }

    /**
     * 方法一：头插法实现链表每K个节点一组反转
     *
     * 算法核心思想：
     * 头插法的本质是将反转组内的每个节点依次"拔出"并"插入"到该组的最前面，
     * 同时保持组的第一个节点（反转后的尾节点）位置相对稳定。
     *
     * 关键变量说明：
     * - pre: 遍历链表时，之前那个节点的引用。它在反转组内外都有更新：
     *        在反转组内：pre节点始终不变，但next指针会更新成当前反转组的头节点。
     *        在反转组外：pre会随着遍历前进，指向当前节点之前的那个。
     *        反转完成后：pre更新为该组的尾节点，保证链表遍历的连续性。
     *
     * - current: 遍历指针，指向当前待处理的节点。
     *            进入反转区域时，先将其作为groupTail（该组反转后的尾节点）保存，
     *            然后current继续前移处理后续节点。
     *
     * - groupTail: 当前反转组的第一个节点，反转后将成为该组的尾节点。
     *              在反转过程中，它的next指针不断更新以维持链表的连续性。
     *
     * 算法执行流程：
     * 1. 创建dummy节点作为虚拟头节点，简化边界处理
     * 2. 遍历链表，使用shouldEnterReverse判断是否进入新的反转组
     * 3. 进入反转组后：
     *    a. 保存当前节点为groupTail（反转后的尾节点）
     *    b. 使用头插法将后续节点逐个插入到pre之后：
     *       - 暂存当前节点的后继
     *       - 更新groupTail的next指向当前节点的后继（维持链表连续性）
     *       - 将当前节点插入到pre和原pre.next之间
     *       - 更新pre.next指向当前节点
     *    c. 继续处理直到满足shouldExitReverse条件
     * 4. 反转完成后，更新pre为groupTail，继续处理下一组
     *
     * 时间复杂度：O(n)，每个节点只访问一次
     * 空间复杂度：O(1)，只使用常数个额外变量
     *
     * 算法优势：
     * - 实现简洁，逻辑清晰
     * - 支持灵活的分组条件（通过lambda表达式）
     * - 处理各种边界情况（空链表、单节点、不足K个等）
     *
     * @param <T> 节点值的类型
     * @param head 链表头节点
     * @param shouldEnterReverse 判断是否进入反转区域的函数，参数为(位置, 节点值)
     * @param shouldExitReverse 判断是否离开反转区域的函数，参数为(位置, 节点值)
     * @return 反转后的链表头节点
     */
    public static  <T> ListNode<T> reverseKGroupByHeadInsertion(
            ListNode<T> head,
            BiPredicate<Integer, T> shouldEnterReverse,
            BiPredicate<Integer, T> shouldExitReverse) {

        if (head == null || head.next == null) return head;

        ListNode<T> dummy = new ListNode<>(null);
        dummy.next = head;

        ListNode<T> pre = dummy;
        ListNode<T> current = head;
        int position = 1;

        while (current != null) {
            if (shouldEnterReverse.test(position, current.val)) {
                /**
                 * 进入反转区域，current指向的节点是反转组的第一个节点。
                 * 操作语义：groupTail，记录反转组的起始节点。
                 * 业务语义：反转过程中，groupTail本身位置不变（相当于固定锚点），
                 *          但它的next指针会动态更新，确保反转组与链表右侧部分始终连续。
                 *          反转完成后，它成为该组的尾节点。
                 */
                ListNode<T> groupTail = current;

                /**
                 * 操作语义：current前移到下一个节点，position递增。
                 * 业务语义：从反转组第二个节点，开始把后继指向前面节点。
                 */
                current = current.next;
                position++;

                // 持续反转直到满足退出条件
                while (current != null && !shouldExitReverse.test(position, current.val)) {
                    /**
                     * 操作语义：暂存current的下一个节点。
                     * 业务语义：在修改current.next之前保存原始后继，确保遍历不中断。
                     */
                    ListNode<T> next = current.next;

                    /**
                     * 操作语义：更新groupTail的next指向current的后继节点。
                     * 业务语义：维持反转组尾部与链表未反转部分的连续性，
                     *          确保反转过程中链表不会断裂。
                     */
                    groupTail.next = next;

                    /**
                     * 头插法核心操作：
                     * 操作语义1：current.next指向pre.next（上一轮的组头节点）。
                     * 业务语义1：将当前节点的后继指向反转组的当前头节点，实现反转。
                     *
                     * 操作语义2：pre.next指向current。
                     * 业务语义2：将当前节点插入到pre之后，使其成为反转组的新头节点。
                     */
                    current.next = pre.next;
                    pre.next = current;

                    /**
                     * 操作语义：current移动到next，position递增。
                     * 业务语义：前进到下一个待处理节点，继续反转或退出反转。
                     */
                    current = next;
                    position++;
                }

                /**
                 * 操作语义：pre更新为groupTail。
                 * 业务语义：反转完成后，pre指向该组的尾节点，
                 *          为下一组反转做准备，保证链表遍历的连续性。
                 */
                pre = groupTail;

            } else {
                /**
                 * 非反转区域的处理：
                 * 操作语义：pre和current都前移一位，position递增。
                 * 业务语义：在非反转区域，pre和current同步前进，
                 *          保持正常的链表遍历状态。
                 */
                pre = current;
                current = current.next;
                position++;
            }
        }

        return dummy.next;
    }

    /**
     * 方法二：三变量法实现链表每K个节点一组反转（优化版）
     *
     * 算法核心思想：
     * 使用prev、curr、next三个指针协调工作，通过改变节点的next指向来实现反转。
     * 优化版不再使用scout指针预先探测，而是边遍历边反转，真正达到O(n)复杂度。
     *
     * 算法步骤：
     * 1. 遍历链表，当遇到需要反转的起始位置时开始反转
     * 2. 使用三指针法（groupHead, current, next）进行局部反转
     * 3. 当遇到退出条件时，结束当前组的反转并连接链表
     * 4. 继续处理下一组
     *
     * 关键优化：
     * - 移除了scout指针的预探测，避免重复遍历
     * - 边遍历边反转，每个节点只访问一次
     * - 使用三个指针完成反转操作
     *
     * 时间复杂度：O(n)，每个节点只访问一次
     * 空间复杂度：O(1)，只使用常数个额外变量
     *
     * 算法优势：
     * - 真正的O(n)复杂度，没有重复遍历
     * - 经典的三指针反转方法，可靠性高
     * - 支持动态的分组条件
     *
     * @param <T> 节点值的类型
     * @param head 链表头节点
     * @param shouldEnterReverse 判断是否进入反转区域的函数
     * @param shouldExitReverse 判断是否离开反转区域的函数
     * @return 反转后的链表头节点
     */
    public static  <T> ListNode<T> reverseKGroupByThreePointers(
            ListNode<T> head,
            BiPredicate<Integer, T> shouldEnterReverse,
            BiPredicate<Integer, T> shouldExitReverse) {

        if (head == null || head.next == null) {
            return head;
        }

        ListNode<T> dummy = new ListNode<>(null);
        dummy.next = head;

        ListNode<T> prev = dummy; // 遍历的游标-->当前节点，前的那个
        ListNode<T> current = head;// 遍历的游标-->当前节点
        int position = 1;

        while (current != null) {
            if (shouldEnterReverse.test(position, current.val)) {
                // 记录组的起始节点（反转后会成为组尾）
                ListNode<T> groupTail = current;
                // 先移动到下一个节点，避免第一个节点被重复判断
                ListNode<T> next = current.next;
                // 开始反转过程
                ListNode<T> groupHead = current;
                current = next;
                position++;

                // 继续反转当前组的剩余节点
                while (current != null && !shouldExitReverse.test(position, current.val)) {
                    next = current.next;
                    current.next = groupHead;  // 反转指向上个循环的头
                    groupHead = current;       // 更新反转后的头-前进
                    current = next;       // current前进
                    position++;
                }

                /**
                 * 反转后、组的头尾，接入整体的链表中
                 */
                // 1. 反转后、组的头部，接入大链表、左边的节点中
                prev.next = groupHead;
                // 2. 反转后、组的尾部,接入大链表、右边的节点中
                groupTail.next = next;

                // 更新大链表为当前组的尾部
                prev = groupTail;
            } else {
                // 非反转区域直接推进
                prev = current;
                current = current.next;
                position++;
            }
        }

        return dummy.next;
    }

    /**
     * 便捷方法：按固定大小K反转链表
     *
     * @param <T> 节点值的类型
     * @param head 链表头节点
     * @param k 每组的大小
     * @return 反转后的链表头节点
     */
    public static <T> ListNode<T> reverseKGroup(ListNode<T> head, int k) {
        if (k <= 1) return head;

        return SinglyLinkedListAlgorithms.reverseKGroupByHeadInsertion(
                head,
                (pos, val) -> (pos - 1) % k == 0,       // 每K个节点开始新组
                (pos, val) -> (pos - 1) % k == 0  // 下一组的开始位置
        );
    }

    /**
     * 全局反转：反转整个链表
     *
     * @param <T> 节点值的类型
     * @param head 链表头节点
     * @return 反转后的链表头节点
     */
    public static <T> ListNode<T> reverseAll(ListNode<T> head) {
        return SinglyLinkedListAlgorithms.reverseKGroupByHeadInsertion(
                head,
                (pos, val) -> pos == 1,     // 从第一个节点开始
                (pos, val) -> false          // 永不退出，直到链表结束
        );
    }

    /**
     * 条件反转：根据节点值的条件进行分组反转（简化版）
     *
     * @param <T> 节点值的类型（必须实现Comparable接口）
     * @param head 链表头节点
     * @param threshold 阈值
     * @param reverseGreaterOrEqual true表示反转>=阈值的连续节点，false表示反转<阈值的
     * @return 反转后的链表头节点
     */
    public static <T extends Comparable<T>> ListNode<T> reverseByCondition(
            ListNode<T> head,
            T threshold,
            boolean reverseGreaterOrEqual) {

        if (head == null) return null;

        ListNode<T> dummy = new ListNode<>(null);
        dummy.next = head;

        ListNode<T> prevGroupTail = dummy;
        ListNode<T> current = head;

        while (current != null) {
            // 检查当前节点是否满足条件
            boolean meetsCondition = reverseGreaterOrEqual ?
                    current.val.compareTo(threshold) >= 0 :
                    current.val.compareTo(threshold) < 0;

            if (meetsCondition) {
                // 找到满足条件的连续节点组
                ListNode<T> groupFirst = current;
                ListNode<T> prev = current;
                current = current.next;

                // 收集所有满足条件的连续节点
                while (current != null) {
                    boolean currentMeets = reverseGreaterOrEqual ?
                            current.val.compareTo(threshold) >= 0 :
                            current.val.compareTo(threshold) < 0;

                    if (!currentMeets) break;

                    // 头插法
                    ListNode<T> next = current.next;
                    prev.next = next;
                    current.next = prevGroupTail.next;
                    prevGroupTail.next = current;
                    current = next;
                }

                prevGroupTail = groupFirst;
            } else {
                prevGroupTail = current;
                current = current.next;
            }
        }

        return dummy.next;
    }



    /**
     * 检测链表环的入口节点
     *
     * 算法核心思想：
     * 1. 第一阶段：使用快慢指针检测是否存在环，并找到相遇点
     * 2. 第二阶段：根据数学推导，将一个指针移回头节点，两个指针同速前进，
     *    再次相遇的位置就是环的入口节点
     *
     * 数学证明：
     * 设链表起点到环入口的距离为a，环入口到相遇点的距离为b，环的长度为c。
     * - 慢指针走过的距离：a + b
     * - 快指针走过的距离：a + b + k*c（k为快指针在环内转的圈数）
     *
     * 由于快指针速度是慢指针的2倍：
     * 2(a + b) = a + b + k*c
     * 化简得：a + b = k*c
     * 进一步：a = k*c - b = (k-1)*c + (c-b)
     *
     * 这意味着：从头节点到环入口的距离a，等于从相遇点继续前进到环入口的距离(c-b)，
     * 再加上(k-1)圈的环长。因此，两个指针分别从头节点和相遇点同速前进，
     * 必然在环入口相遇。
     *
     * 时间复杂度：O(n)，最多遍历链表两次
     * 空间复杂度：O(1)，只使用常数个额外指针
     *
     * @param <T> 节点值的类型
     * @param head 链表头节点
     * @return 环的入口节点，如果不存在环则返回null
     */
    public static <T> ListNode<T> detectCycle(ListNode<T> head) {
        if (head == null || head.next == null) {
            return null;
        }

        // 第一阶段：快慢指针检测环
        ListNode<T> slow = head;
        ListNode<T> fast = head;

        // 快指针每次走两步，慢指针每次走一步
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;

            // 快慢指针相遇，说明存在环
            if (slow == fast) {
                // 第二阶段：寻找环的入口
                slow = head;

                // 两个指针同速前进，会在环的入口节点相遇
                while (slow != fast) {
                    slow = slow.next;
                    fast = fast.next;
                }

                return slow;
            }
        }

        // 快指针到达末尾，不存在环
        return null;
    }

    /**
     * 检测链表是否存在环
     *
     * 算法核心思想：
     * 使用Floyd的龟兔赛跑算法，设置两个指针：
     * - 慢指针（slow）：每次移动一步
     * - 快指针（fast）：每次移动两步
     *
     * 如果链表存在环，快指针最终会追上慢指针；
     * 如果不存在环，快指针会先到达链表末尾（null）。
     *
     * 时间复杂度：O(n)，其中n是链表的节点数
     * 空间复杂度：O(1)，只使用两个额外指针
     *
     * @param <T> 节点值的类型
     * @param head 链表头节点
     * @return 如果存在环返回true，否则返回false
     */
    public static <T> boolean hasCycle(ListNode<T> head) {
        if (head == null || head.next == null) {
            return false;
        }

        /**
         * 操作语义：初始化快慢指针，slow指向第一个节点，fast指向第二个节点。
         * 业务语义：设置不同的起始位置，确保在环内能够相遇。
         *          如果都从head开始，第一次判断就会相等，产生误判。
         */
        ListNode<T> slow = head;
        ListNode<T> fast = head.next;

        /**
         * 操作语义：当快慢指针不相等时，继续移动。
         * 业务语义：在环内，快指针会追上慢指针；无环时，快指针会到达末尾。
         */
        while (slow != fast) {
            /**
             * 操作语义：检查fast和fast.next是否为null。
             * 业务语义：如果快指针到达链表末尾，说明不存在环。
             */
            if (fast == null || fast.next == null) {
                return false;
            }

            /**
             * 操作语义：slow前进一步，fast前进两步。
             * 业务语义：快指针以2倍速追赶慢指针，保证在有环时一定能相遇。
             */
            slow = slow.next;
            fast = fast.next.next;
        }

        /**
         * 操作语义：循环结束，slow == fast。
         * 业务语义：快慢指针相遇，证明存在环。
         */
        return true;
    }

    /**
     * 获取环的长度
     *
     * 算法思想：
     * 找到环后，让一个指针在环内走一圈，计算环的节点数。
     *
     * @param <T> 节点值的类型
     * @param head 链表头节点
     * @return 环的长度，如果不存在环则返回0
     */
    public static <T> int getCycleLength(ListNode<T> head) {
        ListNode<T> cycleEntry = head.detectCycle();
        if (cycleEntry == null) {
            return 0;
        }

        /**
         * 操作语义：从环入口开始，遍历一圈计算长度。
         * 业务语义：通过计数得到环的实际大小。
         */
        int length = 1;
        ListNode<T> current = cycleEntry.next;
        while (current != cycleEntry) {
            length++;
            current = current.next;
        }

        return length;
    }
}
