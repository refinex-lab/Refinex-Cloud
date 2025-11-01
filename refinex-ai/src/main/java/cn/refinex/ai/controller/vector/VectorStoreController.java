package cn.refinex.ai.controller.vector;

import cn.refinex.ai.controller.vector.dto.request.AddDocumentsRequest;
import cn.refinex.ai.controller.vector.vo.DocumentVO;
import cn.refinex.ai.service.VectorStoreService;
import cn.refinex.common.apilog.core.annotation.LogOperation;
import cn.refinex.common.apilog.core.enums.OperateTypeEnum;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.enums.HttpStatusCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 向量存储控制器
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/vector-store")
@RequiredArgsConstructor
@Tag(name = "向量存储管理", description = "向量存储的增删查改操作")
@ConditionalOnProperty(prefix = "refinex.ai.vector-store", name = "enabled", havingValue = "true")
public class VectorStoreController {

    private final VectorStoreService vectorStoreService;

    @PostMapping("/documents")
    @LogOperation(operateDesc = "添加文档到向量存储", operationType = OperateTypeEnum.CREATE)
    @Operation(summary = "添加文档到向量存储", description = "将文档添加到指定模型的向量存储中")
    public ApiResult<Void> addDocuments(@Valid @RequestBody AddDocumentsRequest request) {
        List<Document> documents = request.getDocuments().stream()
                .map(doc -> Document.builder()
                        .id(doc.getId())
                        .text(doc.getContent())
                        .metadata(doc.getMetadata())
                        .build())
                .toList();

        vectorStoreService.addDocuments(request.getModelCode(), documents);
        return ApiResult.success(HttpStatusCode.NO_CONTENT, null);
    }

    @GetMapping("/search")
    @Operation(summary = "相似性搜索", description = "在向量存储中搜索与查询文本相似的文档")
    public ApiResult<List<DocumentVO>> similaritySearch(
            @Parameter(description = "嵌入模型编码") @RequestParam @NotBlank String modelCode,
            @Parameter(description = "查询文本") @RequestParam @NotBlank String query,
            @Parameter(description = "返回结果数量") @RequestParam(defaultValue = "5") @Min(1) int topK,
            @Parameter(description = "相似度阈值(0-1)") @RequestParam(required = false) Double similarityThreshold,
            @Parameter(description = "过滤表达式") @RequestParam(required = false) String filterExpression) {

        List<Document> results = vectorStoreService.similaritySearch(
                modelCode, query, topK, similarityThreshold, filterExpression);

        List<DocumentVO> vos = results.stream()
                .map(doc -> {
                    DocumentVO vo = new DocumentVO();
                    vo.setId(doc.getId());
                    vo.setContent(doc.getText());
                    vo.setMetadata(doc.getMetadata());
                    // 获取相似度分数（如果有）
                    if (doc.getMetadata().containsKey("distance")) {
                        vo.setScore((Double) doc.getMetadata().get("distance"));
                    }
                    return vo;
                })
                .toList();

        return ApiResult.success(vos);
    }

    @DeleteMapping("/documents/{documentId}")
    @LogOperation(operateDesc = "删除文档", operationType = OperateTypeEnum.DELETE)
    @Operation(summary = "删除文档", description = "从向量存储中删除指定文档")
    public ApiResult<Void> deleteDocument(
            @Parameter(description = "模型编码") @RequestParam @NotBlank String modelCode,
            @Parameter(description = "文档ID") @PathVariable @NotBlank String documentId) {

        vectorStoreService.deleteDocument(modelCode, documentId);
        return ApiResult.success(HttpStatusCode.NO_CONTENT, null);
    }

    @DeleteMapping("/documents")
    @LogOperation(operateDesc = "批量删除文档", operationType = OperateTypeEnum.DELETE)
    @Operation(summary = "批量删除文档", description = "从向量存储中批量删除文档")
    public ApiResult<Void> deleteDocuments(
            @Parameter(description = "模型编码") @RequestParam @NotBlank String modelCode,
            @Parameter(description = "文档ID列表") @RequestBody @NotBlank List<String> documentIds) {

        vectorStoreService.deleteDocuments(modelCode, documentIds);
        return ApiResult.success(HttpStatusCode.NO_CONTENT, null);
    }

    @DeleteMapping("/documents/filter")
    @LogOperation(operateDesc = "根据过滤表达式删除文档", operationType = OperateTypeEnum.DELETE)
    @Operation(summary = "根据过滤表达式删除文档", description = "根据过滤表达式从向量存储中删除文档")
    public ApiResult<Void> deleteByFilter(
            @Parameter(description = "模型编码") @RequestParam @NotBlank String modelCode,
            @Parameter(description = "过滤表达式") @RequestParam @NotBlank String filterExpression) {

        vectorStoreService.deleteByFilter(modelCode, filterExpression);
        return ApiResult.success(HttpStatusCode.NO_CONTENT, null);
    }

    @PostMapping("/persist")
    @LogOperation(operateDesc = "持久化向量存储", operationType = OperateTypeEnum.UPDATE)
    @Operation(summary = "持久化向量存储", description = "将向量存储数据持久化到磁盘（仅适用于SimpleVectorStore）")
    public ApiResult<Void> persistVectorStore(@Parameter(description = "模型编码") @RequestParam @NotBlank String modelCode) {
        vectorStoreService.persistVectorStore(modelCode);
        return ApiResult.success(HttpStatusCode.NO_CONTENT, null);
    }

}

