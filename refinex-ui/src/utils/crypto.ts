/**
 * 前后端加密工具类
 *
 * 使用 RSA + AES-GCM 混合加密方案：
 * 1. 生成随机的 AES-256 密钥
 * 2. 使用 AES-GCM 加密实际数据
 * 3. 使用 RSA 公钥加密 AES 密钥
 * 4. 返回加密的 AES 密钥和加密的数据
 *
 * @author Refinex
 * @since 1.0.0
 */

/**
 * 将 ArrayBuffer 转换为 Base64 字符串
 */
function arrayBufferToBase64(buffer: ArrayBuffer): string {
  const bytes = new Uint8Array(buffer);
  let binary = '';
  for (let i = 0; i < bytes.byteLength; i++) {
    binary += String.fromCharCode(bytes[i]);
  }
  return window.btoa(binary);
}

/**
 * 将 Base64 字符串转换为 ArrayBuffer
 */
function base64ToArrayBuffer(base64: string): ArrayBuffer {
  const binary = window.atob(base64);
  const bytes = new Uint8Array(binary.length);
  for (let i = 0; i < binary.length; i++) {
    bytes[i] = binary.charCodeAt(i);
  }
  return bytes.buffer;
}

/**
 * 从 Base64 编码的公钥字符串导入 RSA 公钥
 *
 * @param base64PublicKey Base64 编码的 X.509 格式公钥
 * @returns CryptoKey 对象
 */
async function importRsaPublicKey(base64PublicKey: string): Promise<CryptoKey> {
  const keyData = base64ToArrayBuffer(base64PublicKey);
  return await window.crypto.subtle.importKey(
    'spki', // X.509 格式
    keyData,
    {
      name: 'RSA-OAEP',
      hash: 'SHA-256',
    },
    true,
    ['encrypt']
  );
}

/**
 * 生成随机的 AES-256 密钥
 *
 * @returns CryptoKey 对象
 */
async function generateAesKey(): Promise<CryptoKey> {
  return await window.crypto.subtle.generateKey(
    {
      name: 'AES-GCM',
      length: 256, // AES-256
    },
    true, // 可导出
    ['encrypt', 'decrypt']
  );
}

/**
 * 使用 AES-GCM 加密数据
 *
 * @param plaintext 明文字符串
 * @param aesKey AES 密钥
 * @returns Base64 编码的密文（包含 IV）
 */
async function aesGcmEncrypt(plaintext: string, aesKey: CryptoKey): Promise<string> {
  // 生成 12 字节随机 IV（推荐长度）
  const iv = window.crypto.getRandomValues(new Uint8Array(12));

  // 将字符串转换为 Uint8Array
  const encoder = new TextEncoder();
  const data = encoder.encode(plaintext);

  // 加密
  const encrypted = await window.crypto.subtle.encrypt(
    {
      name: 'AES-GCM',
      iv: iv,
      tagLength: 128, // 128 bits 认证标签
    },
    aesKey,
    data
  );

  // 合并 IV 和密文：[IV(12 bytes) + Ciphertext]
  const result = new Uint8Array(iv.length + encrypted.byteLength);
  result.set(iv, 0);
  result.set(new Uint8Array(encrypted), iv.length);

  return arrayBufferToBase64(result.buffer);
}

/**
 * 使用 RSA 公钥加密 AES 密钥
 *
 * @param aesKey AES 密钥
 * @param rsaPublicKey RSA 公钥
 * @returns Base64 编码的加密密钥
 */
async function rsaEncryptAesKey(aesKey: CryptoKey, rsaPublicKey: CryptoKey): Promise<string> {
  // 导出 AES 密钥为原始字节
  const keyData = await window.crypto.subtle.exportKey('raw', aesKey);

  // 使用 RSA 公钥加密（必须指定与后端一致的 hash 算法）
  const encrypted = await window.crypto.subtle.encrypt(
    {
      name: 'RSA-OAEP',
      // 注意：这里不需要指定 hash，因为它在 importKey 时已经指定了
    },
    rsaPublicKey,
    keyData
  );

  return arrayBufferToBase64(encrypted);
}

/**
 * 混合加密结果
 */
export interface HybridEncryptResult {
  /** RSA 加密的 AES 密钥（Base64） */
  encryptedKey: string;
  /** AES-GCM 加密的数据（Base64，包含 IV） */
  encryptedData: string;
}

/**
 * 使用 RSA + AES-GCM 混合加密方案加密数据
 *
 * @param plaintext 明文字符串
 * @param rsaPublicKeyBase64 Base64 编码的 RSA 公钥（X.509 格式）
 * @returns 混合加密结果
 */
export async function hybridEncrypt(
  plaintext: string,
  rsaPublicKeyBase64: string
): Promise<HybridEncryptResult> {
  // 1. 导入 RSA 公钥
  const rsaPublicKey = await importRsaPublicKey(rsaPublicKeyBase64);

  // 2. 生成随机 AES-256 密钥
  const aesKey = await generateAesKey();

  // 3. 使用 AES-GCM 加密数据
  const encryptedData = await aesGcmEncrypt(plaintext, aesKey);

  // 4. 使用 RSA 公钥加密 AES 密钥
  const encryptedKey = await rsaEncryptAesKey(aesKey, rsaPublicKey);

  return {
    encryptedKey,
    encryptedData,
  };
}

/**
 * 加密密码（用于登录等场景）
 *
 * @param password 明文密码
 * @param rsaPublicKeyBase64 Base64 编码的 RSA 公钥
 * @returns 混合加密结果
 */
export async function encryptPassword(
  password: string,
  rsaPublicKeyBase64: string
): Promise<HybridEncryptResult> {
  return hybridEncrypt(password, rsaPublicKeyBase64);
}

/**
 * 获取 RSA 公钥
 *
 * 配置方式：
 * 1. 在 config/config.ts 中通过 define 配置（当前方案）
 * 2. 生产环境通过环境变量 RSA_PUBLIC_KEY 注入
 * 3. 启动时：RSA_PUBLIC_KEY=xxx npm run start
 * 4. 构建时：RSA_PUBLIC_KEY=xxx npm run build
 *
 * @returns RSA 公钥 Base64 字符串
 */
export function getRsaPublicKey(): string {
  // 从环境变量读取（在 config.ts 中通过 define 配置）
  const envKey = process.env.RSA_PUBLIC_KEY;
  if (!envKey) {
    console.warn('未配置 RSA_PUBLIC_KEY，请在 config/config.ts 或环境变量中配置');
  }
  return envKey || '';
}

