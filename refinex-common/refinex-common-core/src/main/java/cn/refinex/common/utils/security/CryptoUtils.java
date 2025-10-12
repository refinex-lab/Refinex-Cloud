package cn.refinex.common.utils.security;

import cn.refinex.common.exception.SystemException;
import cn.refinex.common.exception.code.ResultCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;
import java.util.Base64;

/**
 * 加密工具类
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CryptoUtils {

    private static final String AES_TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String AES_CBC_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String BC_PROVIDER = BouncyCastleProvider.PROVIDER_NAME;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // AES-GCM 参数
    // 96 bits 推荐长度
    private static final int GCM_IV_LENGTH = 12;
    // bits
    private static final int GCM_TAG_LENGTH = 128;

    static {
        // 注册 BouncyCastle 提供者（幂等）
        if (Security.getProvider(BC_PROVIDER) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    // ========================= Jasypt ========================= //
    /**
     * Jasypt 加密器实例
     */
    private static StringEncryptor stringEncryptor;

    /**
     * 初始化 Jasypt 加密器
     *
     * @param password 加密密码
     * @return StringEncryptor 实例
     */
    public static StringEncryptor initJasyptEncryptor(String password) {
        if (stringEncryptor == null) {
            StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();

            EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
            config.setPassword(password);
            config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
            config.setKeyObtentionIterations("1000");
            config.setPoolSize("1");
            config.setProviderName("SunJCE");
            config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
            config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
            config.setStringOutputType("base64");

            encryptor.setConfig(config);
            stringEncryptor = encryptor;
        }
        return stringEncryptor;
    }

    /**
     * Jasypt 加密
     *
     * @param plainText 明文
     * @param password  加密密码
     * @return 加密后的密文
     */
    public static String jasyptEncrypt(String plainText, String password) {
        if (!StringUtils.hasText(plainText)) {
            return plainText;
        }
        try {
            StringEncryptor encryptor = initJasyptEncryptor(password);
            return encryptor.encrypt(plainText);
        } catch (Exception e) {
            log.error("Jasypt 加密失败", e);
            throw new SystemException(ResultCode.INTERNAL_ERROR.getCode(), "Jasypt 加密失败", e);
        }
    }

    /**
     * Jasypt 解密
     *
     * @param encryptedText 密文
     * @param password      解密密码
     * @return 解密后的明文
     */
    public static String jasyptDecrypt(String encryptedText, String password) {
        if (!StringUtils.hasText(encryptedText)) {
            return encryptedText;
        }
        try {
            StringEncryptor encryptor = initJasyptEncryptor(password);
            return encryptor.decrypt(encryptedText);
        } catch (Exception e) {
            log.error("Jasypt 解密失败", e);
            throw new SystemException(ResultCode.INTERNAL_ERROR.getCode(), "Jasypt 解密失败", e);
        }
    }

    // ========================= AES GCM ========================= //

    /**
     * 生成 AES-256 随机密钥（返回 raw bytes）
     *
     * @return 32 字节密钥
     */
    public static byte[] generateAes256Key() {
        final byte[] key = new byte[32];
        SECURE_RANDOM.nextBytes(key);
        return key;
    }

    /**
     * 使用 AES/GCM/NoPadding 加密（返回 Base64(iv + ciphertext)）
     *
     * @param plainBytes 明文
     * @param key        32 字节 AES-256 密钥
     * @param aad        附加认证数据（可为空）
     * @return Base64 编码字符串
     * @throws GeneralSecurityException 若加密失败
     */
    public static String aesGcmEncryptToBase64(final byte[] plainBytes, final byte[] key, final byte[] aad) throws GeneralSecurityException {
        Assert.notNull(plainBytes, "plainBytes 不能为空");
        Assert.notNull(key, "key 不能为空");
        if (key.length != 32) {
            throw new IllegalArgumentException("AES-256 key must be 32 bytes");
        }

        final byte[] iv = new byte[GCM_IV_LENGTH];
        SECURE_RANDOM.nextBytes(iv);

        final SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        final GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, spec, SECURE_RANDOM);

        if (aad != null && aad.length > 0) {
            cipher.updateAAD(aad);
        }
        final byte[] cipherText = cipher.doFinal(plainBytes);

        // 返回 iv + cipherText（Base64）
        final byte[] out = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, out, 0, iv.length);
        System.arraycopy(cipherText, 0, out, iv.length, cipherText.length);
        return Base64.getEncoder().encodeToString(out);
    }

    /**
     * 解密 AES/GCM/NoPadding（输入 Base64(iv + ciphertext)）
     *
     * @param base64IvAndCiphertext Base64(iv + ciphertext)
     * @param key                   32 字节 AES-256 密钥
     * @param aad                   附加认证数据（需与加密时一致）
     * @return 明文字节数组
     * @throws GeneralSecurityException 若解密失败（包括认证失败）
     */
    public static byte[] aesGcmDecryptFromBase64(final String base64IvAndCiphertext, final byte[] key, final byte[] aad) throws GeneralSecurityException {
        Assert.hasText(base64IvAndCiphertext, "输入不能为空");
        Assert.notNull(key, "key 不能为空");
        if (key.length != 32) {
            throw new IllegalArgumentException("AES-256 key must be 32 bytes");
        }

        final byte[] all = Base64.getDecoder().decode(base64IvAndCiphertext);
        if (all.length < GCM_IV_LENGTH) {
            throw new IllegalArgumentException("输入数据长度不合法");
        }
        final byte[] iv = Arrays.copyOfRange(all, 0, GCM_IV_LENGTH);
        final byte[] ciphertext = Arrays.copyOfRange(all, GCM_IV_LENGTH, all.length);

        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        final SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        final GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, spec);
        if (aad != null && aad.length > 0) {
            cipher.updateAAD(aad);
        }
        return cipher.doFinal(ciphertext);
    }

    // ========================= AES CBC (兼容模式) ========================= //

    /**
     * AES-CBC-PKCS5 加密，返回 Base64( iv + ciphertext )
     *
     * @param plainBytes 明文
     * @param key        16/24/32 字节 AES 密钥
     */
    public static String aesCbcEncryptToBase64(final byte[] plainBytes, final byte[] key) throws GeneralSecurityException {
        Assert.notNull(plainBytes, "plainBytes 不能为空");
        Assert.notNull(key, "key 不能为空");
        final byte[] iv = new byte[16];
        SECURE_RANDOM.nextBytes(iv);

        final SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        final Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv), SECURE_RANDOM);
        final byte[] encrypted = cipher.doFinal(plainBytes);

        final byte[] out = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, out, 0, iv.length);
        System.arraycopy(encrypted, 0, out, iv.length, encrypted.length);
        return Base64.getEncoder().encodeToString(out);
    }

    /**
     * AES-CBC-PKCS5 解密，输入 Base64(iv + ciphertext)
     *
     * @param base64IvAndCiphertext Base64(iv + ciphertext)
     * @param key                   16/24/32 字节 AES 密钥
     * @return 明文字节数组
     * @throws GeneralSecurityException 若解密失败
     */
    public static byte[] aesCbcDecryptFromBase64(final String base64IvAndCiphertext, final byte[] key) throws GeneralSecurityException {
        Assert.hasText(base64IvAndCiphertext, "输入不能为空");
        Assert.notNull(key, "key 不能为空");
        final byte[] all = Base64.getDecoder().decode(base64IvAndCiphertext);
        if (all.length < 16) {
            throw new IllegalArgumentException("输入长度不合法");
        }
        final byte[] iv = Arrays.copyOfRange(all, 0, 16);
        final byte[] cipherText = Arrays.copyOfRange(all, 16, all.length);

        final Cipher cipher = Cipher.getInstance(AES_CBC_TRANSFORMATION);
        final SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
        return cipher.doFinal(cipherText);
    }

    // ========================= Hash / HMAC ========================= //

    /**
     * 计算 MD5（Hex 小写）
     *
     * @param input 待计算数据
     * @return MD5 Hex 表示（小写）
     */
    public static String md5Hex(final String input) {
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            final byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return Hex.toHexString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("不支持 MD5 算法", e);
        }
    }

    /**
     * 计算 SHA-256 Hex 表示
     *
     * @param input 待计算数据
     * @return SHA-256 Hex 表示
     * @throws NoSuchAlgorithmException 若不支持 SHA-256 算法
     */
    public static String sha256Hex(final String input) throws NoSuchAlgorithmException {
        final MessageDigest md = MessageDigest.getInstance("SHA-256");
        final byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
        return Hex.toHexString(digest);
    }

    /**
     * 计算 HMAC-SHA256（返回 Hex）
     *
     * @param key  HMAC 密钥
     * @param data 待计算数据
     * @return HMAC-SHA256 Hex 表示
     * @throws NoSuchAlgorithmException 若不支持 HmacSHA256 算法
     * @throws InvalidKeyException      若密钥无效
     */
    public static String hmacSha256Hex(final byte[] key, final String data) throws NoSuchAlgorithmException, InvalidKeyException {
        final Mac mac = Mac.getInstance("HmacSHA256");
        final SecretKeySpec keySpec = new SecretKeySpec(key, "HmacSHA256");
        mac.init(keySpec);
        final byte[] digest = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Hex.toHexString(digest);
    }

    // ========================= SM2 (BouncyCastle) ========================= //

    /**
     * 生成 SM2 密钥对（BouncyCastle）
     *
     * @return KeyPair（公钥/私钥均为 java.security.interfaces 下的实现）
     * @throws GeneralSecurityException 若生成失败
     */
    public static KeyPair generateSm2KeyPair() throws GeneralSecurityException {
        final KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", BC_PROVIDER);
        final ECGenParameterSpec ecSpec = new ECGenParameterSpec("SM2");
        kpg.initialize(ecSpec, SECURE_RANDOM);
        return kpg.generateKeyPair();
    }

    /**
     * 使用 SM2Engine（BouncyCastle）执行 SM2 加密（返回 Hex 表示）
     *
     * @param plain 明文字节
     * @param pub   公钥（java.security.PublicKey）
     */
    public static String sm2EncryptToHex(final byte[] plain, final PublicKey pub) throws InvalidKeyException, InvalidCipherTextException {
        Assert.notNull(plain, "plain 不能为空");
        Assert.notNull(pub, "pub 不能为空");

        // 转换为 BC 格式参数
        final org.bouncycastle.jce.interfaces.ECPublicKey bcPub = (org.bouncycastle.jce.interfaces.ECPublicKey) pub;
        final ECPublicKeyParameters pubParams = (ECPublicKeyParameters) ECUtil.generatePublicKeyParameter(bcPub);

        // 默认 C1C3C2
        final SM2Engine engine = new SM2Engine();
        engine.init(true, new ParametersWithRandom(pubParams, SECURE_RANDOM));
        final byte[] cipher = engine.processBlock(plain, 0, plain.length);
        return Hex.toHexString(cipher);
    }

    /**
     * SM2 解密（输入 Hex 表示的密文）
     *
     * @param hexCipher Hex 密文
     * @param priv      私钥（java.security.PrivateKey）
     */
    public static byte[] sm2DecryptFromHex(final String hexCipher, final PrivateKey priv) throws InvalidKeyException, InvalidCipherTextException {
        Assert.hasText(hexCipher, "hexCipher 不能为空");
        Assert.notNull(priv, "priv 不能为空");

        final org.bouncycastle.jce.interfaces.ECPrivateKey bcPriv = (org.bouncycastle.jce.interfaces.ECPrivateKey) priv;
        final ECPrivateKeyParameters privParams = (ECPrivateKeyParameters) ECUtil.generatePrivateKeyParameter(bcPriv);

        final SM2Engine engine = new SM2Engine();
        engine.init(false, privParams);
        final byte[] input = Hex.decode(hexCipher);
        return engine.processBlock(input, 0, input.length);
    }

    // ========================= 辅助方法 ========================= //

    /**
     * 将字节数组 hex 编码（小写）
     *
     * @param data 待编码的字节数组
     * @return Hex 编码字符串（小写）
     */
    public static String toHex(final byte[] data) {
        return Hex.toHexString(data);
    }

    /**
     * Base64 编码
     *
     * @param data 待编码的字节数组
     * @return Base64 编码字符串
     */
    public static String toBase64(final byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * Base64 解码
     *
     * @param base64 Base64 编码字符串
     * @return 解码后的字节数组
     */
    public static byte[] fromBase64(final String base64) {
        return Base64.getDecoder().decode(base64);
    }
}
