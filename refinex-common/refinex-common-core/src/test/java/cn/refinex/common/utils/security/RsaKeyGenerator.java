package cn.refinex.common.utils.security;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.util.Base64;

/**
 * RSA 密钥生成工具
 * <p>
 * 运行此类生成 RSA 密钥对，用于配置文件中
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
public class RsaKeyGenerator {

    public static void main(String[] args) throws GeneralSecurityException {
        System.out.println("=".repeat(80));
        System.out.println("RSA 密钥对生成工具（2048位）");
        System.out.println("=".repeat(80));

        // 生成 2048 位 RSA 密钥对
        final KeyPair keyPair = CryptoUtils.generateRsaKeyPair(2048);

        // 公钥（Base64 编码，X.509 格式）
        final String publicKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());

        // 私钥（Base64 编码，PKCS#8 格式）
        final String privateKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

        System.out.println("\n【RSA 公钥】（X.509 格式，Base64 编码）");
        System.out.println("请将此公钥配置到前端，用于加密敏感数据：");
        System.out.println("-".repeat(80));
        System.out.println(publicKeyBase64);
        System.out.println("-".repeat(80));

        System.out.println("\n【RSA 私钥】（PKCS#8 格式，Base64 编码）");
        System.out.println("请将此私钥配置到后端 Nacos 配置中心（注意安全保管）：");
        System.out.println("-".repeat(80));
        System.out.println(privateKeyBase64);
        System.out.println("-".repeat(80));

        System.out.println("\n【配置示例】");
        System.out.println("后端配置（Nacos - refinex-common.yml）：");
        System.out.println("refinex:");
        System.out.println("  security:");
        System.out.println("    rsa:");
        System.out.println("      private-key: " + privateKeyBase64);
        System.out.println();
        System.out.println("前端配置（.env）：");
        System.out.println("RSA_PUBLIC_KEY=" + publicKeyBase64);

        System.out.println("\n" + "=".repeat(80));
        System.out.println("密钥生成完成！请妥善保管私钥，不要泄露！");
        System.out.println("=".repeat(80));

        // 测试加解密
        System.out.println("\n【测试加解密】");
        testEncryptDecrypt(keyPair);
    }

    private static void testEncryptDecrypt(KeyPair keyPair) {
        try {
            final String testMessage = "Hello, RSA!";
            System.out.println("原始消息: " + testMessage);

            // 使用公钥加密
            final String encrypted = CryptoUtils.rsaEncryptToBase64(testMessage.getBytes(), keyPair.getPublic());
            System.out.println("加密后: " + encrypted);

            // 使用私钥解密
            final byte[] decrypted = CryptoUtils.rsaDecryptFromBase64(encrypted, keyPair.getPrivate());
            final String decryptedMessage = new String(decrypted);
            System.out.println("解密后: " + decryptedMessage);

            if (testMessage.equals(decryptedMessage)) {
                System.out.println("✓ 加解密测试成功！");
            } else {
                System.err.println("✗ 加解密测试失败！");
            }
        } catch (Exception e) {
            System.err.println("✗ 加解密测试失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
}

