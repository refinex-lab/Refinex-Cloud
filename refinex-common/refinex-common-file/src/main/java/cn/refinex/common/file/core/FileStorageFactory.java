package cn.refinex.common.file.core;

import cn.refinex.common.file.enums.StorageType;
import cn.refinex.common.file.exception.FileErrorCode;
import cn.refinex.common.file.exception.FileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文件存储工厂
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Component
public class FileStorageFactory {

    private final Map<StorageType, FileStorage> storageMap = new ConcurrentHashMap<>();

    /**
     * 构造函数，自动注入所有 FileStorage 实现
     *
     * @param storageList FileStorage 实现列表
     */
    public FileStorageFactory(List<FileStorage> storageList) {
        for (FileStorage storage : storageList) {
            storageMap.put(storage.getStorageType(), storage);
            log.info("注册文件存储实现：{}", storage.getStorageType());
        }
    }

    /**
     * 根据存储类型获取 FileStorage 实现
     *
     * @param storageType 存储类型
     * @return FileStorage 实现
     */
    public FileStorage getStorage(StorageType storageType) {
        FileStorage storage = storageMap.get(storageType);
        if (storage == null) {
            throw new FileException(FileErrorCode.STORAGE_CONFIG_NOT_FOUND, "未找到存储类型对应的实现：" + storageType);
        }
        return storage;
    }

    /**
     * 根据存储类型代码获取 FileStorage 实现
     *
     * @param storageTypeCode 存储类型代码
     * @return FileStorage 实现
     */
    public FileStorage getStorage(String storageTypeCode) {
        StorageType storageType = StorageType.of(storageTypeCode);
        return getStorage(storageType);
    }
}

