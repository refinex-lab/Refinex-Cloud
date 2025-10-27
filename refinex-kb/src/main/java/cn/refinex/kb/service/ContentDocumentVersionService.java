package cn.refinex.kb.service;

import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.kb.controller.document.dto.response.ContentDocumentVersionDetailResponseDTO;
import cn.refinex.kb.controller.document.dto.response.ContentDocumentVersionResponseDTO;

/**
 * 文档版本服务接口
 *
 * @author Refinex
 * @since 1.0.0
 */
public interface ContentDocumentVersionService {

    /**
     * 分页查询文档版本历史
     *
     * @param documentId  文档ID
     * @param pageRequest 分页参数
     * @param operatorId  操作人ID（用于权限验证）
     * @return 分页结果
     */
    PageResult<ContentDocumentVersionResponseDTO> page(Long documentId, PageRequest pageRequest, Long operatorId);

    /**
     * 查询指定版本详情
     *
     * @param documentId    文档ID
     * @param versionNumber 版本号
     * @param operatorId    操作人ID（用于权限验证）
     * @return 版本详情
     */
    ContentDocumentVersionDetailResponseDTO getVersion(Long documentId, Integer versionNumber, Long operatorId);

    /**
     * 恢复到指定版本
     * <p>
     * 将指定版本的内容恢复为当前版本，并创建新的版本记录
     * </p>
     *
     * @param documentId    文档ID
     * @param versionNumber 要恢复的版本号
     * @param operatorId    操作人ID
     * @return 新版本号
     */
    Integer restoreVersion(Long documentId, Integer versionNumber, Long operatorId);

    /**
     * 清理文档的旧版本
     * <p>
     * 保留最近的 N 个版本，删除更早的版本
     * </p>
     *
     * @param documentId 文档ID
     * @param keepCount  保留的版本数量
     * @param operatorId 操作人ID
     * @return 删除的版本数量
     */
    int cleanOldVersions(Long documentId, int keepCount, Long operatorId);

    /**
     * 删除指定版本
     * <p>
     * 注意：不能删除当前版本
     * </p>
     *
     * @param documentId    文档ID
     * @param versionNumber 版本号
     * @param operatorId    操作人ID
     * @return 是否成功
     */
    boolean deleteVersion(Long documentId, Integer versionNumber, Long operatorId);

    /**
     * 对比两个版本的差异
     *
     * @param documentId  文档ID
     * @param fromVersion 起始版本号
     * @param toVersion   目标版本号
     * @param operatorId  操作人ID（用于权限验证）
     * @return 差异结果
     */
    java.util.Map<String, Object> compareVersions(Long documentId, Integer fromVersion, Integer toVersion, Long operatorId);
}

