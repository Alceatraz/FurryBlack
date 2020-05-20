package studio.blacktech.common.security.crypto.cipher;


import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * 使用标准JavaCipher包装的AES-128 CBC分组模式工具类
 * 包含三种加密模式：标准加密、使用SHA-384进行消息验证、使用签名后不初始化的SHA-384进行消息验证。
 * 带消息验证的数据帧为：
 * 00 00 00 00 , 00 00 00 00 - 00 00 00 00 , 00 00 00 00 - 原文
 * 前8位 原始消息getBytes(UTF-8)后数组的长度 int → hexString → getBytes(UTF-8)
 * 后8位 SHA-384的前8位
 * 之后为原始数据getBytes(UTF-8)数据帧经过AES加密和Base64编码，成为密文。
 *
 * @author BTS - Alceatraz Warprays alceatraz@blacktech.studio
 */

public class AESCipher {


	private Cipher encrypter;
	private Cipher decrypter;

	private MessageDigest staticDigester;
	private MessageDigest oneoffDigester;

	// 不使用CodecTool是因为保证即使只复制一个java文件也能独立运行
	private static final Encoder encoder = Base64.getEncoder();
	private static final Decoder decoder = Base64.getDecoder();


	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================


	/**
	 * 构造方法 严重警告：固定的初始向量会导致严重的安全问题
	 *
	 * @param secretKey 随机种子，作为密钥生成器的随机数生成器的种子
	 */
	@Deprecated
	public AESCipher(String secretKey) {
		this(AESCipher.generateSecretKeySpec(secretKey), AESCipher.generateIvParameterSpec("0123456789ABCDEF"));
		System.err.println("Warning! Using fix IV is RISK! Only test purpose!");
	}


	/**
	 * 构造方法 严重警告：固定的初始向量会导致严重的安全问题
	 *
	 * @param secretKeySpec 密钥
	 */
	@Deprecated
	public AESCipher(SecretKeySpec secretKeySpec) {
		this(secretKeySpec, AESCipher.generateIvParameterSpec("0123456789ABCDEF"));
		System.err.println("Warning! Using fix IV is RISK! Only test purpose!");
	}


	/**
	 * 构造方法
	 *
	 * @param secretKey     随机种子，作为密钥生成器的随机数生成器的种子
	 * @param initialVector 初始向量种子，MD5后用于生成初始向量
	 */
	public AESCipher(String secretKey, String initialVector) {
		this(AESCipher.generateSecretKeySpec(secretKey), AESCipher.generateIvParameterSpec(initialVector));
	}


	/**
	 * 构造方法
	 *
	 * @param secretKeySpec 密钥
	 * @param initialVector 初始向量种子，MD5后用于生成初始向量
	 */
	public AESCipher(SecretKeySpec secretKeySpec, String initialVector) {
		this(secretKeySpec, AESCipher.generateIvParameterSpec(initialVector));
	}


	/**
	 * 构造方法
	 *
	 * @param secretKey     随机种子，作为密钥生成器的随机数生成器的种子
	 * @param initialVector 初始向量
	 */
	public AESCipher(String secretKey, IvParameterSpec initialVector) {
		this(AESCipher.generateSecretKeySpec(secretKey), initialVector);
	}


	/**
	 * 构造方法
	 *
	 * @param secretKeySpec 密钥
	 * @param initialVector 初始向量
	 */
	public AESCipher(SecretKeySpec secretKeySpec, IvParameterSpec initialVector) {
		try {
			encrypter = Cipher.getInstance("AES/CBC/PKCS5Padding");
			decrypter = Cipher.getInstance("AES/CBC/PKCS5Padding");
			encrypter.init(Cipher.ENCRYPT_MODE, secretKeySpec, initialVector);
			decrypter.init(Cipher.DECRYPT_MODE, secretKeySpec, initialVector);
			staticDigester = MessageDigest.getInstance("SHA-384");
			oneoffDigester = MessageDigest.getInstance("SHA-384");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException exception) {
			exception.printStackTrace();
			// 这些异常不可能发生
			// NoSuchAlgorithmException ----------- 不允许用户自定义算法
			// NoSuchPaddingException ------------- 不允许用户自定义算法
			// InvalidKeyException ---------------- 密钥由生成器生成
			// InvalidAlgorithmParameterException - 不允许用户自定义算法
		}
	}


	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================


	/**
	 * 生成密钥
	 *
	 * @param secretKey 密钥种子，作为密钥生成器的随机数生成器的种子
	 * @return 密钥
	 */
	private static SecretKeySpec generateSecretKeySpec(String secretKey) {
		try {
			Provider provider = Security.getProvider("SUN");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", provider);
			random.setSeed(secretKey.getBytes(StandardCharsets.UTF_8));
			KeyGenerator generator = KeyGenerator.getInstance("AES");
			generator.init(128, random);
			SecretKey skey = generator.generateKey();
			return new SecretKeySpec(skey.getEncoded(), "AES");
		} catch (NoSuchAlgorithmException exception) {
			return null;
		}
	}


	/**
	 * 生成初始向量
	 *
	 * @param initialVector 初始向量种子，MD5后用于生成初始向量
	 * @return 初始向量
	 */
	private static IvParameterSpec generateIvParameterSpec(String initialVector) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(initialVector.getBytes(StandardCharsets.UTF_8));
			return new IvParameterSpec(digest.digest());
		} catch (NoSuchAlgorithmException exception) {
			return null;
		}
	}


	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================


	/**
	 * 加密模式1
	 *
	 * @param content 原文
	 * @return 密文
	 */
	public String encrypt(String content) {
		try {
			byte[] tmp1 = content.getBytes(StandardCharsets.UTF_8);
			byte[] tmp2 = encrypter.doFinal(tmp1);
			byte[] tmp3 = encoder.encode(tmp2);
			return new String(tmp3, StandardCharsets.UTF_8);
		} catch (IllegalBlockSizeException | BadPaddingException exception) {
			return null;
			// 这些异常不可能发生
			// BadPaddingException ------- 不允许用户自定义算法
			// IllegalBlockSizeException - 不允许用户传入byte[]
		}
	}


	/**
	 * 解密模式1
	 *
	 * @param content 密文
	 * @return 原文
	 */
	public String decrypt(String content) {
		try {
			byte[] tmp1 = decoder.decode(content);
			byte[] tmp2 = decrypter.doFinal(tmp1);
			return new String(tmp2, StandardCharsets.UTF_8);
		} catch (IllegalBlockSizeException | BadPaddingException exception) {
			return null;
			// 这些异常不可能发生
			// BadPaddingException ------- 不允许用户自定义算法
			// IllegalBlockSizeException - 不允许用户传入byte[]
		}
	}


	// ==========================================================================================================================================================


	/**
	 * 加密模式2
	 *
	 * @param content 原文
	 * @return 密文
	 */
	public String encryptHash(String content) {
		try {
			byte[] rawMessage = content.getBytes(StandardCharsets.UTF_8);
			byte[] sizePart;
			byte[] hashPart;
			int rawMessageLength = rawMessage.length;
			byte[] result = new byte[16 + rawMessageLength];
			sizePart = Integer.toHexString(rawMessageLength).getBytes(StandardCharsets.UTF_8);
			int sizePartLength = sizePart.length;
			System.arraycopy(sizePart, 0, result, 8 - sizePartLength, sizePartLength);
			oneoffDigester.update(rawMessage);
			hashPart = oneoffDigester.digest();
			System.arraycopy(hashPart, 0, result, 8, 8);
			System.arraycopy(rawMessage, 0, result, 16, rawMessageLength);
			byte[] tmp1 = encrypter.doFinal(result);
			byte[] tmp2 = encoder.encode(tmp1);
			return new String(tmp2, StandardCharsets.UTF_8);
		} catch (IllegalBlockSizeException | BadPaddingException exception) {
			return null;
			// 这些异常不可能发生
			// BadPaddingException ------- 不允许用户自定义算法
			// IllegalBlockSizeException - 不允许用户传入byte[]
		}
	}


	/**
	 * 解密模式2
	 *
	 * @param content 密文
	 * @return 原文
	 * @throws MessageSizeCheckFailedException 消息长度验证不通过
	 * @throws MessageHashCheckFailedException 消息哈希验证不通过
	 */
	public String decryptHash(String content) throws MessageSizeCheckFailedException, MessageHashCheckFailedException {
		try {
			byte[] rawMessage = decrypter.doFinal(decoder.decode(content));
			byte[] sizePart = new byte[8];
			byte[] hashPart = new byte[8];
			int actualMessageLength = rawMessage.length - 16;
			System.arraycopy(rawMessage, 0, sizePart, 0, 8);
			int claimMessageLength = Integer.valueOf(new String(sizePart).trim(), 16);
			if (claimMessageLength != actualMessageLength) throw new MessageSizeCheckFailedException(claimMessageLength, actualMessageLength);
			System.arraycopy(rawMessage, 8, hashPart, 0, 8);
			byte[] messagePart = new byte[claimMessageLength];
			System.arraycopy(rawMessage, 16, messagePart, 0, claimMessageLength);
			oneoffDigester.update(messagePart);
			byte[] digest = oneoffDigester.digest();
			if (!AESCipher.isDigestValidate(hashPart, digest)) throw new MessageHashCheckFailedException(hashPart, digest);
			return new String(messagePart, StandardCharsets.UTF_8);
		} catch (IllegalBlockSizeException | BadPaddingException exception) {
			return null;
			// 这些异常不可能发生
			// BadPaddingException ------- 不允许用户自定义算法
			// IllegalBlockSizeException - 不允许用户传入byte[]
		}
	}


	// ==========================================================================================================================================================


	/**
	 * 加密模式3
	 *
	 * @param content 原文
	 * @return 密文
	 */
	public String encryptPhaseHash(String content) {
		try {
			byte[] rawMessage = content.getBytes(StandardCharsets.UTF_8);
			byte[] sizePart;
			byte[] hashPart;
			int rawMessageLength = rawMessage.length;
			byte[] result = new byte[16 + rawMessageLength];
			sizePart = Integer.toHexString(rawMessageLength).getBytes(StandardCharsets.UTF_8);
			int sizePartLength = sizePart.length;
			System.arraycopy(sizePart, 0, result, 8 - sizePartLength, sizePartLength);
			staticDigester.update(rawMessage);
			hashPart = ((MessageDigest) staticDigester.clone()).digest();
			System.arraycopy(hashPart, 0, result, 8, 8);
			System.arraycopy(rawMessage, 0, result, 16, rawMessageLength);
			byte[] tmp1 = encrypter.doFinal(result);
			byte[] tmp2 = encoder.encode(tmp1);
			return new String(tmp2, StandardCharsets.UTF_8);
		} catch (IllegalBlockSizeException | BadPaddingException | CloneNotSupportedException exception) {
			return null;
			// 这些异常不可能发生
			// BadPaddingException -------- 不允许用户自定义算法
			// IllegalBlockSizeException -- 不允许用户传入byte[]
			// CloneNotSupportedException - MessageDigest是能够克隆的
		}
	}


	/**
	 * 解密模式3
	 *
	 * @param content 密文
	 * @return 原文
	 * @throws MessageSizeCheckFailedException 消息长度验证不通过
	 * @throws MessageHashCheckFailedException 消息哈希验证不通过
	 */
	public String decryptPhaseHash(String content) throws MessageSizeCheckFailedException, MessageHashCheckFailedException {
		try {
			byte[] rawMessage = decrypter.doFinal(decoder.decode(content));
			byte[] sizePart = new byte[8];
			byte[] hashPart = new byte[8];
			int actualMessageLength = rawMessage.length - 16;
			System.arraycopy(rawMessage, 0, sizePart, 0, 8);
			int claimMessageLength = Integer.valueOf(new String(sizePart).trim(), 16);
			if (claimMessageLength != actualMessageLength) throw new MessageSizeCheckFailedException(claimMessageLength, actualMessageLength);
			System.arraycopy(rawMessage, 8, hashPart, 0, 8);
			byte[] messagePart = new byte[claimMessageLength];
			System.arraycopy(rawMessage, 16, messagePart, 0, claimMessageLength);
			staticDigester.update(messagePart);
			byte[] digest = ((MessageDigest) staticDigester.clone()).digest();
			if (!AESCipher.isDigestValidate(hashPart, digest)) throw new MessageHashCheckFailedException(hashPart, digest);
			return new String(messagePart, StandardCharsets.UTF_8);
		} catch (IllegalBlockSizeException | BadPaddingException | CloneNotSupportedException exception) {
			return null;
			// 这些异常不可能发生
			// BadPaddingException ------- 不允许用户自定义算法
			// NoSuchAlgorithmException -- 不允许用户自定义算法
			// IllegalBlockSizeException - 不允许用户传入byte[]
		}
	}


	// ==========================================================================================================================================================


	private static boolean isDigestValidate(byte[] A, byte[] B) {
		byte[] tempA = new byte[8];
		byte[] tempB = new byte[8];
		System.arraycopy(A, 0, tempA, 0, 8);
		System.arraycopy(B, 0, tempB, 0, 8);
		return Arrays.equals(tempA, tempB);
	}


	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================


	public static class MessageSizeCheckFailedException extends GeneralSecurityException {

		private static final long serialVersionUID = 1L;

		public MessageSizeCheckFailedException(int claimSize, int actualSize) {
			super("Message length dismatch , Claim " + claimSize + ", But actual " + actualSize);
		}
	}


	public static class MessageHashCheckFailedException extends GeneralSecurityException {

		private static final long serialVersionUID = 1L;

		public MessageHashCheckFailedException(byte[] claimHash, byte[] actualHash) {
			super("Message digest dismatch, Claim " + unstanderHashConvert(claimHash) + ", But actual " + unstanderHashConvert(Arrays.copyOfRange(actualHash, 0, 8)));
		}

		private static String unstanderHashConvert(byte[] raw) {
			StringBuilder builder = new StringBuilder();
			for (byte dig : raw) builder.append(Integer.toHexString(dig & 0xFF));
			return builder.toString().toUpperCase();
		}
	}


}
