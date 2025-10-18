package cn.refinex.common.utils.security;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.Base64;
import java.util.Scanner;

/**
 * {@link CryptoUtils} 加密工具类交互式测试程序
 *
 * @author Refinex
 * @since 1.0.0
 */
public class CryptoUtilsTest {

    private static final Scanner scanner = new Scanner(System.in);
    private static String jasyptPassword = null;
    private static byte[] aesKey = null;
    private static KeyPair sm2KeyPair = null;

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("CryptoUtils 加密工具测试程序");
        System.out.println("=".repeat(60));
        System.out.println();

        boolean running = true;
        while (running) {
            try {
                displayMainMenu();
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        handleJasyptOperations();
                        break;
                    case "2":
                        handleAesGcmOperations();
                        break;
                    case "3":
                        handleAesCbcOperations();
                        break;
                    case "4":
                        handleHashOperations();
                        break;
                    case "5":
                        handleSm2Operations();
                        break;
                    case "6":
                        handleKeyGenerationOperations();
                        break;
                    case "7":
                        handleEncodingOperations();
                        break;
                    case "0":
                    case "exit":
                    case "quit":
                        running = false;
                        System.out.println("\n感谢使用，程序已退出。");
                        break;
                    default:
                        System.out.println("\n无效的选择，请重新输入。\n");
                }
            } catch (Exception e) {
                System.err.println("\n操作失败：" + e.getMessage());
                e.printStackTrace();
                System.out.println();
            }
        }

        scanner.close();
    }

    private static void displayMainMenu() {
        System.out.println("请选择功能分类：");
        System.out.println("  [1] Jasypt 加密解密");
        System.out.println("  [2] AES-GCM 加密解密");
        System.out.println("  [3] AES-CBC 加密解密");
        System.out.println("  [4] 哈希与 HMAC 计算");
        System.out.println("  [5] SM2 加密解密");
        System.out.println("  [6] 密钥生成工具");
        System.out.println("  [7] 编码转换工具");
        System.out.println("  [0] 退出程序");
        System.out.print("\n请输入选项编号：");
    }

    // ==================== Jasypt 操作 ====================

    private static void handleJasyptOperations() {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("Jasypt 加密解密操作");
        System.out.println("-".repeat(60));

        if (jasyptPassword == null) {
            System.out.print("请输入 Jasypt 加密密码（将用于后续操作）：");
            jasyptPassword = scanner.nextLine().trim();
            if (jasyptPassword.isEmpty()) {
                System.out.println("密码不能为空，操作取消。\n");
                return;
            }
        }

        System.out.println("\n当前使用的密码：" + jasyptPassword);
        System.out.println("  [1] 加密文本");
        System.out.println("  [2] 解密文本");
        System.out.println("  [3] 更换密码");
        System.out.println("  [0] 返回主菜单");
        System.out.print("请选择操作：");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                System.out.print("请输入待加密的明文：");
                String plainText = scanner.nextLine();
                String encrypted = CryptoUtils.jasyptEncrypt(plainText, jasyptPassword);
                System.out.println("加密结果：" + encrypted);
                break;
            case "2":
                System.out.print("请输入待解密的密文：");
                String cipherText = scanner.nextLine();
                String decrypted = CryptoUtils.jasyptDecrypt(cipherText, jasyptPassword);
                System.out.println("解密结果：" + decrypted);
                break;
            case "3":
                jasyptPassword = null;
                System.out.println("密码已清除，下次操作时将重新输入。");
                break;
            case "0":
                break;
            default:
                System.out.println("无效的选择。");
        }
        System.out.println();
    }

    // ==================== AES-GCM 操作 ====================

    private static void handleAesGcmOperations() {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("AES-GCM 加密解密操作");
        System.out.println("-".repeat(60));

        if (aesKey == null) {
            System.out.println("当前没有可用的 AES 密钥。");
            System.out.println("  [1] 生成新的 AES-256 密钥");
            System.out.println("  [2] 输入现有密钥（Base64 格式）");
            System.out.println("  [0] 返回主菜单");
            System.out.print("请选择操作：");

            String choice = scanner.nextLine().trim();
            if ("1".equals(choice)) {
                aesKey = CryptoUtils.generateAes256Key();
                System.out.println("已生成新密钥（Base64）：" + Base64.getEncoder().encodeToString(aesKey));
            } else if ("2".equals(choice)) {
                System.out.print("请输入 Base64 编码的密钥：");
                String keyBase64 = scanner.nextLine().trim();
                aesKey = Base64.getDecoder().decode(keyBase64);
                if (aesKey.length != 32) {
                    System.out.println("密钥长度必须为 32 字节，当前长度：" + aesKey.length);
                    aesKey = null;
                    return;
                }
                System.out.println("密钥已加载。");
            } else {
                return;
            }
            System.out.println();
        }

        System.out.println("当前密钥（Base64）：" + Base64.getEncoder().encodeToString(aesKey));
        System.out.println("  [1] 加密文本");
        System.out.println("  [2] 解密文本");
        System.out.println("  [3] 加密文本（带附加认证数据 AAD）");
        System.out.println("  [4] 解密文本（带附加认证数据 AAD）");
        System.out.println("  [5] 清除当前密钥");
        System.out.println("  [0] 返回主菜单");
        System.out.print("请选择操作：");

        String choice = scanner.nextLine().trim();
        try {
            switch (choice) {
                case "1":
                    System.out.print("请输入待加密的明文：");
                    String plainText = scanner.nextLine();
                    String encrypted = CryptoUtils.aesGcmEncryptToBase64(
                            plainText.getBytes(StandardCharsets.UTF_8), aesKey, null);
                    System.out.println("加密结果（Base64）：" + encrypted);
                    break;
                case "2":
                    System.out.print("请输入待解密的密文（Base64）：");
                    String cipherText = scanner.nextLine().trim();
                    byte[] decrypted = CryptoUtils.aesGcmDecryptFromBase64(cipherText, aesKey, null);
                    System.out.println("解密结果：" + new String(decrypted, StandardCharsets.UTF_8));
                    break;
                case "3":
                    System.out.print("请输入待加密的明文：");
                    String plainText2 = scanner.nextLine();
                    System.out.print("请输入附加认证数据（AAD）：");
                    String aadStr = scanner.nextLine();
                    byte[] aad = aadStr.getBytes(StandardCharsets.UTF_8);
                    String encrypted2 = CryptoUtils.aesGcmEncryptToBase64(
                            plainText2.getBytes(StandardCharsets.UTF_8), aesKey, aad);
                    System.out.println("加密结果（Base64）：" + encrypted2);
                    break;
                case "4":
                    System.out.print("请输入待解密的密文（Base64）：");
                    String cipherText2 = scanner.nextLine().trim();
                    System.out.print("请输入附加认证数据（AAD）：");
                    String aadStr2 = scanner.nextLine();
                    byte[] aad2 = aadStr2.getBytes(StandardCharsets.UTF_8);
                    byte[] decrypted2 = CryptoUtils.aesGcmDecryptFromBase64(cipherText2, aesKey, aad2);
                    System.out.println("解密结果：" + new String(decrypted2, StandardCharsets.UTF_8));
                    break;
                case "5":
                    aesKey = null;
                    System.out.println("密钥已清除。");
                    break;
                case "0":
                    break;
                default:
                    System.out.println("无效的选择。");
            }
        } catch (Exception e) {
            System.err.println("操作失败：" + e.getMessage());
        }
        System.out.println();
    }

    // ==================== AES-CBC 操作 ====================

    private static void handleAesCbcOperations() {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("AES-CBC 加密解密操作");
        System.out.println("-".repeat(60));

        if (aesKey == null) {
            System.out.println("当前没有可用的 AES 密钥。");
            System.out.println("  [1] 生成新的 AES-256 密钥");
            System.out.println("  [2] 输入现有密钥（Base64 格式）");
            System.out.println("  [0] 返回主菜单");
            System.out.print("请选择操作：");

            String choice = scanner.nextLine().trim();
            if ("1".equals(choice)) {
                aesKey = CryptoUtils.generateAes256Key();
                System.out.println("已生成新密钥（Base64）：" + Base64.getEncoder().encodeToString(aesKey));
            } else if ("2".equals(choice)) {
                System.out.print("请输入 Base64 编码的密钥：");
                String keyBase64 = scanner.nextLine().trim();
                aesKey = Base64.getDecoder().decode(keyBase64);
                System.out.println("密钥已加载。");
            } else {
                return;
            }
            System.out.println();
        }

        System.out.println("当前密钥（Base64）：" + Base64.getEncoder().encodeToString(aesKey));
        System.out.println("  [1] 加密文本");
        System.out.println("  [2] 解密文本");
        System.out.println("  [3] 清除当前密钥");
        System.out.println("  [0] 返回主菜单");
        System.out.print("请选择操作：");

        String choice = scanner.nextLine().trim();
        try {
            switch (choice) {
                case "1":
                    System.out.print("请输入待加密的明文：");
                    String plainText = scanner.nextLine();
                    String encrypted = CryptoUtils.aesCbcEncryptToBase64(
                            plainText.getBytes(StandardCharsets.UTF_8), aesKey);
                    System.out.println("加密结果（Base64）：" + encrypted);
                    break;
                case "2":
                    System.out.print("请输入待解密的密文（Base64）：");
                    String cipherText = scanner.nextLine().trim();
                    byte[] decrypted = CryptoUtils.aesCbcDecryptFromBase64(cipherText, aesKey);
                    System.out.println("解密结果：" + new String(decrypted, StandardCharsets.UTF_8));
                    break;
                case "3":
                    aesKey = null;
                    System.out.println("密钥已清除。");
                    break;
                case "0":
                    break;
                default:
                    System.out.println("无效的选择。");
            }
        } catch (Exception e) {
            System.err.println("操作失败：" + e.getMessage());
        }
        System.out.println();
    }

    // ==================== 哈希与 HMAC 操作 ====================

    private static void handleHashOperations() {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("哈希与 HMAC 计算");
        System.out.println("-".repeat(60));

        System.out.println("  [1] 计算 MD5 哈希");
        System.out.println("  [2] 计算 SHA-256 哈希");
        System.out.println("  [3] 计算 HMAC-SHA256");
        System.out.println("  [0] 返回主菜单");
        System.out.print("请选择操作：");

        String choice = scanner.nextLine().trim();
        try {
            switch (choice) {
                case "1":
                    System.out.print("请输入待计算的文本：");
                    String input1 = scanner.nextLine();
                    String md5 = CryptoUtils.md5Hex(input1);
                    System.out.println("MD5 结果（Hex）：" + md5);
                    break;
                case "2":
                    System.out.print("请输入待计算的文本：");
                    String input2 = scanner.nextLine();
                    String sha256 = CryptoUtils.sha256Hex(input2);
                    System.out.println("SHA-256 结果（Hex）：" + sha256);
                    break;
                case "3":
                    System.out.print("请输入 HMAC 密钥（文本）：");
                    String keyStr = scanner.nextLine();
                    byte[] key = keyStr.getBytes(StandardCharsets.UTF_8);
                    System.out.print("请输入待计算的数据：");
                    String data = scanner.nextLine();
                    String hmac = CryptoUtils.hmacSha256Hex(key, data);
                    System.out.println("HMAC-SHA256 结果（Hex）：" + hmac);
                    break;
                case "0":
                    break;
                default:
                    System.out.println("无效的选择。");
            }
        } catch (Exception e) {
            System.err.println("操作失败：" + e.getMessage());
        }
        System.out.println();
    }

    // ==================== SM2 操作 ====================

    private static void handleSm2Operations() {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("SM2 加密解密操作");
        System.out.println("-".repeat(60));

        if (sm2KeyPair == null) {
            System.out.println("当前没有可用的 SM2 密钥对。");
            System.out.println("  [1] 生成新的 SM2 密钥对");
            System.out.println("  [0] 返回主菜单");
            System.out.print("请选择操作：");

            String choice = scanner.nextLine().trim();
            if ("1".equals(choice)) {
                try {
                    sm2KeyPair = CryptoUtils.generateSm2KeyPair();
                    System.out.println("已生成新的 SM2 密钥对。");
                    System.out.println("公钥（Base64）：" + Base64.getEncoder().encodeToString(sm2KeyPair.getPublic().getEncoded()));
                    System.out.println("私钥（Base64）：" + Base64.getEncoder().encodeToString(sm2KeyPair.getPrivate().getEncoded()));
                } catch (Exception e) {
                    System.err.println("密钥生成失败：" + e.getMessage());
                    return;
                }
            } else {
                return;
            }
            System.out.println();
        }

        System.out.println("  [1] 使用公钥加密文本");
        System.out.println("  [2] 使用私钥解密文本");
        System.out.println("  [3] 显示当前密钥对");
        System.out.println("  [4] 清除当前密钥对");
        System.out.println("  [0] 返回主菜单");
        System.out.print("请选择操作：");

        String choice = scanner.nextLine().trim();
        try {
            switch (choice) {
                case "1":
                    System.out.print("请输入待加密的明文：");
                    String plainText = scanner.nextLine();
                    String encrypted = CryptoUtils.sm2EncryptToHex(
                            plainText.getBytes(StandardCharsets.UTF_8), sm2KeyPair.getPublic());
                    System.out.println("加密结果（Hex）：" + encrypted);
                    break;
                case "2":
                    System.out.print("请输入待解密的密文（Hex）：");
                    String cipherText = scanner.nextLine().trim();
                    byte[] decrypted = CryptoUtils.sm2DecryptFromHex(cipherText, sm2KeyPair.getPrivate());
                    System.out.println("解密结果：" + new String(decrypted, StandardCharsets.UTF_8));
                    break;
                case "3":
                    System.out.println("公钥（Base64）：" + Base64.getEncoder().encodeToString(sm2KeyPair.getPublic().getEncoded()));
                    System.out.println("私钥（Base64）：" + Base64.getEncoder().encodeToString(sm2KeyPair.getPrivate().getEncoded()));
                    break;
                case "4":
                    sm2KeyPair = null;
                    System.out.println("密钥对已清除。");
                    break;
                case "0":
                    break;
                default:
                    System.out.println("无效的选择。");
            }
        } catch (Exception e) {
            System.err.println("操作失败：" + e.getMessage());
        }
        System.out.println();
    }

    // ==================== 密钥生成工具 ====================

    private static void handleKeyGenerationOperations() {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("密钥生成工具");
        System.out.println("-".repeat(60));

        System.out.println("  [1] 生成 AES-256 密钥（Base64 格式）");
        System.out.println("  [2] 生成 AES-256 密钥（原始字节，Hex 格式）");
        System.out.println("  [3] 生成 SM2 密钥对");
        System.out.println("  [0] 返回主菜单");
        System.out.print("请选择操作：");

        String choice = scanner.nextLine().trim();
        try {
            switch (choice) {
                case "1":
                    String keyBase64 = CryptoUtils.generateAes256KeyBase64();
                    System.out.println("生成的 AES-256 密钥（Base64）：");
                    System.out.println(keyBase64);
                    System.out.println("\n提示：此密钥可用于配置文件中的 db-sensitive-data-key 配置项。");
                    break;
                case "2":
                    byte[] keyBytes = CryptoUtils.generateAes256Key();
                    System.out.println("生成的 AES-256 密钥（Hex）：");
                    System.out.println(CryptoUtils.toHex(keyBytes));
                    System.out.println("生成的 AES-256 密钥（Base64）：");
                    System.out.println(Base64.getEncoder().encodeToString(keyBytes));
                    break;
                case "3":
                    KeyPair kp = CryptoUtils.generateSm2KeyPair();
                    System.out.println("生成的 SM2 密钥对：");
                    System.out.println("公钥（Base64）：");
                    System.out.println(Base64.getEncoder().encodeToString(kp.getPublic().getEncoded()));
                    System.out.println("\n私钥（Base64）：");
                    System.out.println(Base64.getEncoder().encodeToString(kp.getPrivate().getEncoded()));
                    break;
                case "0":
                    break;
                default:
                    System.out.println("无效的选择。");
            }
        } catch (Exception e) {
            System.err.println("操作失败：" + e.getMessage());
        }
        System.out.println();
    }

    // ==================== 编码转换工具 ====================

    private static void handleEncodingOperations() {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("编码转换工具");
        System.out.println("-".repeat(60));

        System.out.println("  [1] 文本转 Hex 编码");
        System.out.println("  [2] Hex 编码转文本");
        System.out.println("  [3] 文本转 Base64 编码");
        System.out.println("  [4] Base64 编码转文本");
        System.out.println("  [5] Hex 转 Base64");
        System.out.println("  [6] Base64 转 Hex");
        System.out.println("  [0] 返回主菜单");
        System.out.print("请选择操作：");

        String choice = scanner.nextLine().trim();
        try {
            switch (choice) {
                case "1":
                    System.out.print("请输入文本：");
                    String text1 = scanner.nextLine();
                    String hex1 = CryptoUtils.toHex(text1.getBytes(StandardCharsets.UTF_8));
                    System.out.println("Hex 编码：" + hex1);
                    break;
                case "2":
                    System.out.print("请输入 Hex 编码：");
                    String hex2 = scanner.nextLine().trim();
                    byte[] bytes2 = org.bouncycastle.util.encoders.Hex.decode(hex2);
                    System.out.println("文本：" + new String(bytes2, StandardCharsets.UTF_8));
                    break;
                case "3":
                    System.out.print("请输入文本：");
                    String text3 = scanner.nextLine();
                    String base64_3 = CryptoUtils.toBase64(text3.getBytes(StandardCharsets.UTF_8));
                    System.out.println("Base64 编码：" + base64_3);
                    break;
                case "4":
                    System.out.print("请输入 Base64 编码：");
                    String base64_4 = scanner.nextLine().trim();
                    byte[] bytes4 = CryptoUtils.fromBase64(base64_4);
                    System.out.println("文本：" + new String(bytes4, StandardCharsets.UTF_8));
                    break;
                case "5":
                    System.out.print("请输入 Hex 编码：");
                    String hex5 = scanner.nextLine().trim();
                    byte[] bytes5 = org.bouncycastle.util.encoders.Hex.decode(hex5);
                    String base64_5 = CryptoUtils.toBase64(bytes5);
                    System.out.println("Base64 编码：" + base64_5);
                    break;
                case "6":
                    System.out.print("请输入 Base64 编码：");
                    String base64_6 = scanner.nextLine().trim();
                    byte[] bytes6 = CryptoUtils.fromBase64(base64_6);
                    String hex6 = CryptoUtils.toHex(bytes6);
                    System.out.println("Hex 编码：" + hex6);
                    break;
                case "0":
                    break;
                default:
                    System.out.println("无效的选择。");
            }
        } catch (Exception e) {
            System.err.println("操作失败：" + e.getMessage());
        }
        System.out.println();
    }
}
