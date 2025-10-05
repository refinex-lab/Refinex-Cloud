package cn.refinex.common.file.service;

import cn.refinex.common.file.domain.entity.FileInfo;
import cn.refinex.common.file.repository.FileInfoRepository;
import cn.refinex.common.utils.algorithm.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文件去重服务, 基于 MD5 实现秒传功能。
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileDeduplicationService {

    private final FileInfoRepository fileInfoRepository;
    private final SnowflakeIdGenerator idGenerator;

    /**
     * 检查文件是否已存在（基于 MD5）
     *
     * @param fileMd5 文件 MD5
     * @return 已存在的文件信息，不存在返回 null
     */
    public FileInfo checkFileExists(String fileMd5) {
        List<FileInfo> existingFiles = fileInfoRepository.findByFileMd5(fileMd5);
        if (existingFiles.isEmpty()) {
            log.debug("文件不存在，需要上传，md5={}", fileMd5);
            return null;
        }

        // 返回第一个未删除的文件
        FileInfo existingFile = existingFiles.get(0);
        log.info("文件已存在，可秒传，md5={}, fileGuid={}", fileMd5, existingFile.getFileGuid());
        return existingFile;
    }

    /**
     * 复制文件元数据（秒传）
     *
     * @param sourceFile 源文件
     * @param uploaderId 上传者 ID
     * @param bizType    业务类型
     * @param bizId      业务 ID
     * @return 新文件信息
     */
    public FileInfo copyFileMetadata(FileInfo sourceFile, Long uploaderId, String bizType, Long bizId) {
        if (sourceFile == null) {
            log.error("源文件不存在，无法复制元数据");
            return null;
        }

        // 1. 复制文件元数据
        FileInfo newFile = new FileInfo();
        newFile.setId(idGenerator.nextId());
        newFile.setFileGuid(String.valueOf(newFile.getId()));
        newFile.setFileName(sourceFile.getFileName());
        newFile.setOriginalName(sourceFile.getOriginalName());
        newFile.setFileExtension(sourceFile.getFileExtension());
        newFile.setFileSize(sourceFile.getFileSize());
        newFile.setFileType(sourceFile.getFileType());
        newFile.setFileMd5(sourceFile.getFileMd5());
        newFile.setStorageStrategy(sourceFile.getStorageStrategy());
        newFile.setStorageConfigId(sourceFile.getStorageConfigId());
        newFile.setStoragePath(sourceFile.getStoragePath());
        newFile.setBucketName(sourceFile.getBucketName());
        newFile.setThumbnailFileId(sourceFile.getThumbnailFileId());
        newFile.setUploaderId(uploaderId);
        newFile.setBizType(bizType);
        newFile.setBizId(bizId);
        newFile.setRefCount(1);
        newFile.setDeleted(0);
        newFile.setCreateTime(LocalDateTime.now());
        newFile.setUpdateTime(LocalDateTime.now());

        // 2. 插入新文件记录
        fileInfoRepository.insert(newFile);

        // 3. 增加源文件的引用计数
        fileInfoRepository.updateRefCount(sourceFile.getId(), 1);

        log.info("文件元数据已复制（秒传），sourceFileId={}, newFileGuid={}", sourceFile.getId(), newFile.getFileGuid());
        return newFile;
    }
}

