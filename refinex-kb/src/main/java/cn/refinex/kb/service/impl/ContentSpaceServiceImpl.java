package cn.refinex.kb.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.refinex.common.constants.SystemStatusConstants;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.exception.BusinessException;
import cn.refinex.common.exception.SystemException;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.properties.RefinexBizProperties;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.common.utils.security.CryptoUtils;
import cn.refinex.kb.client.PlatformUserServiceClient;
import cn.refinex.kb.controller.space.dto.request.ContentSpaceCreateRequestDTO;
import cn.refinex.kb.controller.space.dto.request.ContentSpacePublishRequestDTO;
import cn.refinex.kb.controller.space.dto.request.ContentSpaceQueryRequestDTO;
import cn.refinex.kb.controller.space.dto.request.ContentSpaceUpdateRequestDTO;
import cn.refinex.kb.controller.space.dto.response.ContentSpaceDetailResponseDTO;
import cn.refinex.kb.controller.space.dto.response.ContentSpaceResponseDTO;
import cn.refinex.kb.entity.ContentSpace;
import cn.refinex.kb.enums.AccessType;
import cn.refinex.kb.enums.SpaceType;
import cn.refinex.kb.repository.ContentSpaceRepository;
import cn.refinex.kb.service.ContentSpaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 内容空间服务实现
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContentSpaceServiceImpl implements ContentSpaceService {

    private final ContentSpaceRepository spaceRepository;
    private final PlatformUserServiceClient platformUserServiceClient;
    private final RefinexBizProperties bizProperties;

    /**
     * 创建内容空间
     *
     * @param request    创建请求
     * @param operatorId 操作人ID
     * @return 空间ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ContentSpaceCreateRequestDTO request, Long operatorId) {
        // 参数校验
        validateCreateRequest(request);

        // 生成空间编码
        String spaceCode = generateSpaceCode();

        // 构建实体
        ContentSpace space = BeanConverter.toBean(request, ContentSpace.class);
        space.setSpaceCode(spaceCode);
        space.setOwnerId(operatorId);
        space.setIsPublished(0);
        space.setViewCount(0L);
        space.setCreateBy(operatorId);
        space.setCreateTime(LocalDateTime.now());
        space.setUpdateBy(operatorId);
        space.setUpdateTime(LocalDateTime.now());
        space.setDeleted(0);
        space.setVersion(0);
        space.setStatus(SystemStatusConstants.NORMAL_VALUE);

        // 处理访问密码加密
        if (Objects.equals(request.getAccessType(), AccessType.PASSWORD_PROTECTED.getCode())) {
            if (request.getAccessPassword() == null || request.getAccessPassword().isBlank()) {
                throw new BusinessException("密码访问类型必须设置访问密码");
            }
            space.setAccessPassword(BCrypt.hashpw(request.getAccessPassword()));
        }

        // 插入数据库
        return spaceRepository.insert(space);
    }

    /**
     * 更新内容空间
     *
     * @param spaceId    空间ID
     * @param request    更新请求
     * @param operatorId 操作人ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(Long spaceId, ContentSpaceUpdateRequestDTO request, Long operatorId) {
        // 查询空间
        ContentSpace existingSpace = spaceRepository.selectById(spaceId);
        if (existingSpace == null) {
            throw new BusinessException("空间不存在");
        }

        // 权限校验：只有拥有者可以更新
        if (!existingSpace.getOwnerId().equals(operatorId)) {
            throw new BusinessException("无权限更新此空间");
        }

        // 更新字段
        if (request.getSpaceName() != null) {
            existingSpace.setSpaceName(request.getSpaceName());
        }
        if (request.getSpaceDesc() != null) {
            existingSpace.setSpaceDesc(request.getSpaceDesc());
        }
        if (request.getCoverImage() != null) {
            existingSpace.setCoverImage(request.getCoverImage());
        }
        if (request.getSpaceType() != null) {
            if (!SpaceType.isValid(request.getSpaceType())) {
                throw new BusinessException("无效的空间类型");
            }
            existingSpace.setSpaceType(request.getSpaceType());
        }
        if (request.getAccessType() != null) {
            if (!AccessType.isValid(request.getAccessType())) {
                throw new BusinessException("无效的访问类型");
            }
            existingSpace.setAccessType(request.getAccessType());

            // 处理密码
            if (Objects.equals(request.getAccessType(), AccessType.PASSWORD_PROTECTED.getCode())) {
                if (request.getAccessPassword() != null && !request.getAccessPassword().isBlank()) {
                    existingSpace.setAccessPassword(BCrypt.hashpw(request.getAccessPassword()));
                }
            } else {
                existingSpace.setAccessPassword(null);
            }
        }
        if (request.getSort() != null) {
            existingSpace.setSort(request.getSort());
        }
        if (request.getStatus() != null) {
            existingSpace.setStatus(request.getStatus());
        }
        if (request.getRemark() != null) {
            existingSpace.setRemark(request.getRemark());
        }

        existingSpace.setUpdateBy(operatorId);
        existingSpace.setUpdateTime(LocalDateTime.now());
        existingSpace.setVersion(request.getVersion());

        // 执行更新
        int rows = spaceRepository.update(existingSpace);
        if (rows == 0) {
            throw new BusinessException("更新失败，可能是版本冲突或空间不存在");
        }

        return true;
    }

    /**
     * 发布/取消发布空间
     *
     * @param spaceId    空间ID
     * @param request    发布请求
     * @param operatorId 操作人ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publish(Long spaceId, ContentSpacePublishRequestDTO request, Long operatorId) {
        // 查询空间
        ContentSpace existingSpace = spaceRepository.selectById(spaceId);
        if (existingSpace == null) {
            throw new BusinessException("空间不存在");
        }

        // 权限校验：只有拥有者可以发布
        if (!existingSpace.getOwnerId().equals(operatorId)) {
            throw new BusinessException("无权限发布此空间");
        }

        // 发布时间
        LocalDateTime publishTime = request.getIsPublished() == 1 ? LocalDateTime.now() : null;

        // 执行更新
        int rows = spaceRepository.updatePublishStatus(spaceId, request.getIsPublished(), publishTime, operatorId, request.getVersion());
        if (rows == 0) {
            throw new BusinessException("发布失败，可能是版本冲突或空间不存在");
        }

        return true;
    }

    /**
     * 删除内容空间
     *
     * @param spaceId    空间ID
     * @param operatorId 操作人ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long spaceId, Long operatorId) {
        // 查询空间
        ContentSpace existingSpace = spaceRepository.selectById(spaceId);
        if (existingSpace == null) {
            throw new BusinessException("空间不存在");
        }

        // 权限校验：只有拥有者可以删除
        if (!existingSpace.getOwnerId().equals(operatorId)) {
            throw new BusinessException("无权限删除此空间");
        }

        // 检查空间下是否有文档
        long documentCount = spaceRepository.countDocumentsBySpaceId(spaceId);
        if (documentCount > 0) {
            throw new BusinessException("空间下存在文档，无法删除");
        }

        // 执行删除
        int rows = spaceRepository.deleteById(spaceId, operatorId);
        if (rows == 0) {
            throw new BusinessException("删除失败");
        }
    }

    /**
     * 根据ID获取空间详情
     *
     * @param spaceId 空间ID
     * @return 空间详情
     */
    @Override
    public ContentSpaceDetailResponseDTO getDetail(Long spaceId) {
        ContentSpace space = spaceRepository.selectById(spaceId);
        if (space == null) {
            throw new BusinessException("空间不存在");
        }

        return buildDetailResponse(space);
    }

    /**
     * 根据空间编码获取空间详情
     *
     * @param spaceCode 空间编码
     * @return 空间详情
     */
    @Override
    public ContentSpaceDetailResponseDTO getDetailByCode(String spaceCode) {
        ContentSpace space = spaceRepository.selectBySpaceCode(spaceCode);
        if (space == null) {
            throw new BusinessException("空间不存在");
        }

        return buildDetailResponse(space);
    }

    /**
     * 根据拥有者ID获取空间列表
     *
     * @param ownerId 拥有者ID
     * @return 空间列表
     */
    @Override
    public List<ContentSpaceResponseDTO> listByOwnerId(Long ownerId) {
        List<ContentSpace> spaces = spaceRepository.selectByOwnerId(ownerId);
        if (CollectionUtils.isEmpty(spaces)) {
            return Collections.emptyList();
        }

        // 提取拥有者ID列表
        List<Long> ownerIds = spaces.stream()
                .map(ContentSpace::getOwnerId)
                .toList();

        Map<String, Object> usernameMap = buildUsernameMap(ownerIds);
        return spaces.stream()
                .map(space -> buildResponse(space, usernameMap))
                .toList();
    }

    /**
     * 分页查询空间列表
     *
     * @param queryDTO    查询条件
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    @Override
    public PageResult<ContentSpaceResponseDTO> page(ContentSpaceQueryRequestDTO queryDTO, PageRequest pageRequest) {
        PageResult<ContentSpace> pageResult = spaceRepository.selectPage(queryDTO, pageRequest);
        if (CollectionUtils.isEmpty(pageResult.getRecords())) {
            return PageResult.empty(pageRequest.getPageNum(), pageRequest.getPageSize());
        }

        // 提取拥有者ID列表
        List<Long> ownerIds = pageResult.getRecords().stream()
                .map(ContentSpace::getOwnerId)
                .toList();

        Map<String, Object> usernameMap = buildUsernameMap(ownerIds);
        List<ContentSpaceResponseDTO> dtoList = pageResult.getRecords().stream()
                .map(space -> buildResponse(space, usernameMap))
                .toList();

        return new PageResult<>(dtoList, pageResult.getTotal(), pageResult.getPageNum(), pageResult.getPageSize());
    }

    /**
     * 构建用户名映射表
     *
     * @param ownerIds 拥有者ID列表
     * @return 用户名映射表
     */
    private Map<String, Object> buildUsernameMap(List<Long> ownerIds) {
        ApiResult<Map<String, Object>> usernameMapResult = platformUserServiceClient.getUsernameMap(ownerIds);
        if (!usernameMapResult.isSuccess()) {
            log.error("批量获取用户信息失败，响应结果：{}", usernameMapResult);
            throw new SystemException("平台服务暂时不可用，请稍后重试");
        }

        return usernameMapResult.data();
    }

    /**
     * 单个获取用户信息
     *
     * @param ownerId 拥有者ID
     * @return 用户名
     */
    private String getSingleUsername(Long ownerId) {
        ApiResult<String> usernameResult = platformUserServiceClient.getUsernameByUserId(ownerId);
        if (!usernameResult.isSuccess()) {
            log.error("单个获取用户信息失败，响应结果：{}", usernameResult);
            throw new SystemException("平台服务暂时不可用，请稍后重试");
        }

        return usernameResult.data();
    }

    /**
     * 增加浏览次数
     *
     * @param spaceId 空间ID
     */
    @Override
    public void incrementViewCount(Long spaceId) {
        spaceRepository.incrementViewCount(spaceId);
    }

    /**
     * 校验空间访问权限
     * <p>
     * 注意：此方法用于检查用户是否有权限访问空间，会根据访问类型进行判断。
     * 对于密码保护类型的空间：
     * - 必须提供正确的密码才能访问（即使是拥有者也必须输入密码）
     * - 密码必须使用 RSA + AES 混合加密后传输
     * - 这样的设计可以防止密码泄露后无法撤销访问权限
     * </p>
     *
     * @param spaceId  空间ID
     * @param userId   用户ID
     * @param password 访问密码（RSA + AES 混合加密，格式：encryptedKey|encryptedData）
     * @return 是否有权限访问
     */
    @Override
    public boolean validateAccess(Long spaceId, Long userId, String password) {
        ContentSpace space = spaceRepository.selectById(spaceId);
        if (space == null) {
            return false;
        }

        // 获取访问类型
        Integer accessType = space.getAccessType();

        // 【优先】密码访问：必须先验证密码（即使是拥有者也要验证）
        if (AccessType.PASSWORD_PROTECTED.getCode().equals(accessType)) {
            // 验证空间是否配置了密码
            if (space.getAccessPassword() == null || space.getAccessPassword().isBlank()) {
                log.warn("空间 [{}] 设置了密码访问，但数据库中未存储密码哈希值", spaceId);
                return false;
            }
            
            // 必须提供密码才能访问（包括拥有者）
            if (password == null || password.isBlank()) {
                log.debug("空间 [{}] 需要密码访问，但未提供密码", spaceId);
                return false;
            }
            
            // 解密密码（使用 RSA + AES 混合解密）
            String plainPassword = decryptPassword(password);
            
            // 验证密码是否正确
            boolean passwordValid = BCrypt.checkpw(plainPassword, space.getAccessPassword());
            if (!passwordValid) {
                log.debug("空间 [{}] 密码验证失败", spaceId);
            }
            return passwordValid;
        }

        // 未发布的空间只有拥有者可以访问
        if (space.getIsPublished() == 0) {
            return space.getOwnerId().equals(userId);
        }

        // 公开空间：所有人都可以访问
        if (AccessType.PUBLIC.getCode().equals(accessType)) {
            return true;
        }

        // 私有空间：只有拥有者可以访问
        if (AccessType.PRIVATE.getCode().equals(accessType)) {
            return space.getOwnerId().equals(userId);
        }

        // 未知的访问类型，拒绝访问
        log.warn("空间 [{}] 包含未知的访问类型: {}", spaceId, accessType);
        return false;
    }

    // ==================== 私有方法 ====================

    /**
     * 解密密码（使用 RSA + AES 混合解密）
     * <p>
     * 密码格式：encryptedKey|encryptedData
     * - encryptedKey: RSA 加密的 AES 密钥（Base64）
     * - encryptedData: AES-GCM 加密的密码数据（Base64）
     * </p>
     *
     * @param encryptedPassword RSA + AES 混合加密的密码
     * @return 解密后的明文密码
     * @throws BusinessException 如果密码格式错误或解密失败
     */
    private String decryptPassword(String encryptedPassword) {
        if (!StringUtils.hasText(encryptedPassword)) {
            throw new BusinessException("密码不能为空");
        }

        try {
            // 获取 RSA 私钥
            String base64PrivateKey = bizProperties.getRsaPrivateKey();
            if (!StringUtils.hasText(base64PrivateKey)) {
                log.error("RSA 私钥未配置（refinex.security.rsa.private-key），密码解密功能不可用");
                throw new BusinessException("系统配置错误，密码解密功能不可用");
            }

            PrivateKey privateKey = CryptoUtils.loadRsaPrivateKeyFromBase64(base64PrivateKey);
            if (privateKey == null) {
                log.error("RSA 私钥加载失败，无法解密密码");
                throw new BusinessException("系统配置错误，密码解密功能不可用");
            }

            // 解析加密数据（格式：encryptedKey|encryptedData）
            String[] parts = encryptedPassword.split("\\|");
            if (parts.length != 2) {
                log.warn("密码格式错误，期望格式：encryptedKey|encryptedData，实际：{}", encryptedPassword);
                throw new BusinessException("密码格式错误");
            }

            String encryptedKey = parts[0];
            String encryptedData = parts[1];

            // 使用混合解密
            String plainPassword = CryptoUtils.hybridDecrypt(encryptedKey, encryptedData, privateKey);
            if (!StringUtils.hasText(plainPassword)) {
                throw new BusinessException("解密后的密码为空");
            }
            
            return plainPassword;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("密码解密失败：{}", e.getMessage(), e);
            throw new BusinessException("密码解密失败，请检查密码格式");
        }
    }

    /**
     * 生成空间编码
     * <p>
     * 格式：SPACE_YYYYMMDDHHMMSS_RANDOM8
     *
     * @return 空间编码
     */
    private String generateSpaceCode() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomPart = IdUtil.randomUUID().substring(0, 8).toUpperCase();
        return "SPACE_" + timestamp + "_" + randomPart;
    }

    /**
     * 校验创建请求
     * <p>
     * 校验空间类型和访问类型是否有效
     *
     * @param request 创建请求DTO
     */
    private void validateCreateRequest(ContentSpaceCreateRequestDTO request) {
        // 校验空间类型
        if (!SpaceType.isValid(request.getSpaceType())) {
            throw new BusinessException("无效的空间类型");
        }

        // 校验访问类型
        if (!AccessType.isValid(request.getAccessType())) {
            throw new BusinessException("无效的访问类型");
        }

        // 密码访问必须设置密码
        if (Objects.equals(request.getAccessType(), AccessType.PASSWORD_PROTECTED.getCode())
                && (request.getAccessPassword() == null || request.getAccessPassword().isBlank())) {
            throw new BusinessException("密码访问类型必须设置访问密码");
        }

    }

    /**
     * 构建响应DTO
     *
     * @param space       内容空间实体
     * @param usernameMap 用户ID到用户名的映射
     * @return 内容空间响应DTO
     */
    private ContentSpaceResponseDTO buildResponse(ContentSpace space, Map<String, Object> usernameMap) {
        ContentSpaceResponseDTO dto = BeanConverter.toBean(space, ContentSpaceResponseDTO.class);
        dto.setSpaceTypeDesc(SpaceType.getDescription(space.getSpaceType()));
        dto.setAccessTypeDesc(AccessType.getDescription(space.getAccessType()));
        dto.setOwnerName(Convert.toStr(usernameMap.get(space.getOwnerId().toString()), "UNKNOWN"));

        return dto;
    }

    /**
     * 构建详情响应DTO
     *
     * @param space 内容空间实体
     * @return 内容空间详情响应DTO
     */
    private ContentSpaceDetailResponseDTO buildDetailResponse(ContentSpace space) {
        ContentSpaceDetailResponseDTO dto = BeanConverter.toBean(space, ContentSpaceDetailResponseDTO.class);
        dto.setSpaceTypeDesc(SpaceType.getDescription(space.getSpaceType()));
        dto.setAccessTypeDesc(AccessType.getDescription(space.getAccessType()));

        // 统计信息
        dto.setDocumentCount(spaceRepository.countDocumentsBySpaceId(space.getId()));
        dto.setDirectoryCount(spaceRepository.countDirectoriesBySpaceId(space.getId()));

        // 填充拥有者用户名（需要调用用户服务）
        dto.setOwnerName(getSingleUsername(space.getOwnerId()));

        return dto;
    }
}

