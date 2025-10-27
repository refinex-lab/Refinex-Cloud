package cn.refinex.kb.util;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 文档版本对比工具类
 * <p>
 * 基于 java-diff-utils 实现文档内容的差异对比
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DocumentDiffUtils {

    /**
     * 对比两个文档版本的差异
     *
     * @param fromContent 源版本内容
     * @param toContent   目标版本内容
     * @return 差异结果
     */
    public static DiffResult diff(String fromContent, String toContent) {
        // 按行分割内容
        List<String> fromLines = splitLines(fromContent);
        List<String> toLines = splitLines(toContent);

        // 计算差异
        Patch<String> patch = DiffUtils.diff(fromLines, toLines);
        List<AbstractDelta<String>> deltas = patch.getDeltas();

        // 构建差异结果
        DiffResult result = new DiffResult();
        result.setFromLineCount(fromLines.size());
        result.setToLineCount(toLines.size());
        result.setChangeCount(deltas.size());

        List<DiffBlock> diffBlocks = new ArrayList<>();

        for (AbstractDelta<String> delta : deltas) {
            DiffBlock block = new DiffBlock();
            block.setType(delta.getType().name());
            block.setSourcePosition(delta.getSource().getPosition());
            block.setSourceLines(delta.getSource().getLines());
            block.setTargetPosition(delta.getTarget().getPosition());
            block.setTargetLines(delta.getTarget().getLines());

            diffBlocks.add(block);
        }

        result.setDiffBlocks(diffBlocks);
        result.setSummary(generateSummary(deltas));

        return result;
    }

    /**
     * 生成差异摘要
     *
     * @param deltas 差异列表
     * @return 摘要信息
     */
    private static DiffSummary generateSummary(List<AbstractDelta<String>> deltas) {
        int insertions = 0;
        int deletions = 0;
        int changes = 0;

        for (AbstractDelta<String> delta : deltas) {
            switch (delta.getType()) {
                case INSERT -> insertions += delta.getTarget().size();
                case DELETE -> deletions += delta.getSource().size();
                case CHANGE -> {
                    changes++;
                    deletions += delta.getSource().size();
                    insertions += delta.getTarget().size();
                }
                case EQUAL -> {
                    // EQUAL 类型不计入变更
                }
            }
        }

        DiffSummary summary = new DiffSummary();
        summary.setInsertions(insertions);
        summary.setDeletions(deletions);
        summary.setChanges(changes);
        summary.setTotalChanges(deltas.size());

        return summary;
    }

    /**
     * 按行分割文本
     *
     * @param content 文本内容
     * @return 行列表
     */
    private static List<String> splitLines(String content) {
        if (content == null || content.isEmpty()) {
            return List.of();
        }
        return Arrays.asList(content.split("\\r?\\n"));
    }

    /**
     * 生成统一差异格式（类似 Git Diff）
     *
     * @param fromContent 源版本内容
     * @param toContent   目标版本内容
     * @return 统一差异格式字符串
     */
    public static String generateUnifiedDiff(String fromContent, String toContent) {
        return generateUnifiedDiff(fromContent, toContent, "原版本", "新版本");
    }

    /**
     * 生成统一差异格式（类似 Git Diff）
     *
     * @param fromContent 源版本内容
     * @param toContent   目标版本内容
     * @param fromLabel   源版本标签
     * @param toLabel     目标版本标签
     * @return 统一差异格式字符串
     */
    public static String generateUnifiedDiff(String fromContent, String toContent, String fromLabel, String toLabel) {
        List<String> fromLines = splitLines(fromContent);
        List<String> toLines = splitLines(toContent);

        Patch<String> patch = DiffUtils.diff(fromLines, toLines);

        // 生成统一差异格式
        StringBuilder unifiedDiff = new StringBuilder();
        unifiedDiff.append("--- ").append(fromLabel).append("\n");
        unifiedDiff.append("+++ ").append(toLabel).append("\n");

        for (AbstractDelta<String> delta : patch.getDeltas()) {
            int fromStart = delta.getSource().getPosition() + 1;
            int fromCount = delta.getSource().size();
            int toStart = delta.getTarget().getPosition() + 1;
            int toCount = delta.getTarget().size();

            unifiedDiff.append(String.format("@@ -%d,%d +%d,%d @@%n", fromStart, fromCount, toStart, toCount));

            // 删除的行
            for (String line : delta.getSource().getLines()) {
                unifiedDiff.append("-").append(line).append("\n");
            }

            // 添加的行
            for (String line : delta.getTarget().getLines()) {
                unifiedDiff.append("+").append(line).append("\n");
            }
        }

        return unifiedDiff.toString();
    }

    /**
     * 差异结果
     */
    @Setter
    @Getter
    public static class DiffResult {
        /**
         * 源版本行数
         */
        private int fromLineCount;

        /**
         * 目标版本行数
         */
        private int toLineCount;

        /**
         * 变更块数量
         */
        private int changeCount;

        /**
         * 差异块列表
         */
        private List<DiffBlock> diffBlocks;

        /**
         * 差异摘要
         */
        private DiffSummary summary;
    }

    /**
     * 差异块
     */
    @Setter
    @Getter
    public static class DiffBlock {
        /**
         * 变更类型: INSERT, DELETE, CHANGE
         */
        private String type;

        /**
         * 源文档位置
         */
        private int sourcePosition;

        /**
         * 源文档行内容
         */
        private List<String> sourceLines;

        /**
         * 目标文档位置
         */
        private int targetPosition;

        /**
         * 目标文档行内容
         */
        private List<String> targetLines;

    }

    /**
     * 差异摘要
     */
    @Setter
    @Getter
    public static class DiffSummary {

        /**
         * 插入的行数
         */
        private int insertions;

        /**
         * 删除的行数
         */
        private int deletions;

        /**
         * 修改的块数
         */
        private int changes;

        /**
         * 总变更块数
         */
        private int totalChanges;
    }
}

