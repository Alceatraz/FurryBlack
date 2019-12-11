package studio.blacktech.security.Cipher;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;

/***
 * 使用标准JavaCipher包装的AES-128 CBC分组模式工具类，
 *
 * 包含三种加密模式：标准加密、使用HA-384进行消息验证、使用签名后不初始化的SHA-384进行消息验证。
 *
 * 带消息验证的数据帧为：
 *
 * 00 00 00 00 , 00 00 00 00 - 00 00 00 00 , 00 00 00 00 - XXXX
 *
 * 前8位 原始消息getBytes(UTF-8)后数组的长度 int → hexString → getBytes(UTF-8)
 *
 * 后8位 SHA-384的前8位
 *
 * 之后为原始数据getBytes(UTF-8)
 *
 * 数据帧经过AES加密和Base64编码，成为密文。
 *
 * @author Alceatraz Warprays
 *
 */
public class AESCipher {

	private SecretKeySpec sk;
	private IvParameterSpec iv;
	private Cipher encrypter;
	private Cipher decrypter;
	private Base64.Encoder encoder;
	private Base64.Decoder decoder;
	private MessageDigest staticDigester;
	private MessageDigest oneoffDigester;

	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	/**
	 * 构造方法
	 *
	 * @param secretKey 随机种子，作为密钥生成器的随机数生成器的种子
	 */
	@Deprecated public AESCipher(String secretKey) {
		this(AESCipher.generateSecretKeySpec(secretKey), AESCipher.generateIvParameterSpec("0123456789ABCDEF"));
		System.err.println("Warning! Using fix IV is RISK! Only test purpose!");
	}

	/**
	 * 构造方法
	 *
	 * @param secretKeySpec 密钥
	 */
	@Deprecated public AESCipher(SecretKeySpec secretKeySpec) {
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

			sk = secretKeySpec;
			iv = initialVector;

			encrypter = Cipher.getInstance("AES/CBC/PKCS5Padding");
			decrypter = Cipher.getInstance("AES/CBC/PKCS5Padding");

			encrypter.init(Cipher.ENCRYPT_MODE, sk, iv);
			decrypter.init(Cipher.DECRYPT_MODE, sk, iv);

			encoder = Base64.getEncoder();
			decoder = Base64.getDecoder();

			staticDigester = MessageDigest.getInstance("SHA-384");
			oneoffDigester = MessageDigest.getInstance("SHA-384");

		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException exception) {
			exception.printStackTrace();
			// 这些异常不可能发生 - 使用ADoptOpenJDK 8
			// NoSuchAlgorithmException ----------- 不允许用户自定义算法
			// NoSuchPaddingException ------------- 不允许用户自定义算法
			// InvalidKeyException ---------------- 密钥由生成器生成
			// InvalidAlgorithmParameterException - 不允许用户自定义算法
		}
	}

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
			exception.printStackTrace();
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
	 * @throws IOException 输入错误的内容
	 */
	public String decrypt(String content) {

		try {

			byte[] tmp1 = decoder.decode(content);
			byte[] tmp2 = decrypter.doFinal(tmp1);
			return new String(tmp2, StandardCharsets.UTF_8);

		} catch (IllegalBlockSizeException | BadPaddingException exception) {
			exception.printStackTrace();
			return null;
			// 这些异常不可能发生
			// BadPaddingException ------- 不允许用户自定义算法
			// IllegalBlockSizeException - 不允许用户传入byte[]
		}
	}

	/**
	 * 加密模式2
	 *
	 * @param content 原文
	 * @return 密文
	 */
	public String encryptHash(String content) {

		try {

			byte[] rawMessage = content.getBytes(StandardCharsets.UTF_8);

			byte[] sizePart = new byte[8];
			byte[] hashPart = new byte[8];

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
	 * @throws IOException                     输入了错误的内容
	 * @throws MessageSizeCheckFailedException 消息长度验证不通过
	 * @throws MessageHashCheckFailedException 消息哈希验证不通过
	 */
	public String decryptHash(String content) throws IOException, MessageSizeCheckFailedException, MessageHashCheckFailedException {

		try {

			byte[] rawMessage = decrypter.doFinal(decoder.decode(content));

			byte[] sizePart = new byte[8];
			byte[] hashPart = new byte[8];

			int actualMessageLength = rawMessage.length - 16;

			System.arraycopy(rawMessage, 0, sizePart, 0, 8);
			int claminMessagelength = Integer.valueOf(new String(sizePart).trim(), 16);
			if (claminMessagelength != actualMessageLength) {
				throw new MessageSizeCheckFailedException(claminMessagelength, actualMessageLength);
			}

			System.arraycopy(rawMessage, 8, hashPart, 0, 8);

			byte[] mesgPart = new byte[claminMessagelength];
			System.arraycopy(rawMessage, 16, mesgPart, 0, claminMessagelength);
			oneoffDigester.update(mesgPart);
			byte[] digest = oneoffDigester.digest();

			if (!AESCipher.isSame(hashPart, digest)) { throw new MessageHashCheckFailedException(hashPart, digest); }

			return new String(mesgPart, StandardCharsets.UTF_8);

		} catch (IllegalBlockSizeException | BadPaddingException exception) {
			return null;
			// 这些异常不可能发生
			// BadPaddingException ------- 不允许用户自定义算法
			// IllegalBlockSizeException - 不允许用户传入byte[]
		}
	}

	public String encraptPhaseHash(String content) {

		try {

			byte[] rawMessage = content.getBytes(StandardCharsets.UTF_8);

			byte[] sizePart = new byte[8];
			byte[] hashPart = new byte[8];

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

	public String decryptPhaseHash(String content) throws IOException, MessageSizeCheckFailedException, MessageHashCheckFailedException {
		try {

			byte[] rawMessage = decrypter.doFinal(decoder.decode(content));

			byte[] sizePart = new byte[8];
			byte[] hashPart = new byte[8];

			int actualMessageLength = rawMessage.length - 16;

			System.arraycopy(rawMessage, 0, sizePart, 0, 8);
			int claminMessagelength = Integer.valueOf(new String(sizePart).trim(), 16);
			if (claminMessagelength != actualMessageLength) {
				throw new MessageSizeCheckFailedException(claminMessagelength, actualMessageLength);
			}

			System.arraycopy(rawMessage, 8, hashPart, 0, 8);

			byte[] mesgPart = new byte[claminMessagelength];
			System.arraycopy(rawMessage, 16, mesgPart, 0, claminMessagelength);

			staticDigester.update(mesgPart);
			byte[] digest = ((MessageDigest) staticDigester.clone()).digest();

			if (!AESCipher.isSame(hashPart, digest)) { throw new MessageHashCheckFailedException(hashPart, digest); }

			return new String(mesgPart, StandardCharsets.UTF_8);

		} catch (IllegalBlockSizeException | BadPaddingException | CloneNotSupportedException exception) {
			return null;
			// 这些异常不可能发生
			// BadPaddingException ------- 不允许用户自定义算法
			// NoSuchAlgorithmException -- 不允许用户自定义算法
			// IllegalBlockSizeException - 不允许用户传入byte[]
		}
	}

	private static boolean isSame(byte[] A, byte[] B) {
		// 只发送前8位，所以只比较前8位，java没有数组截取 "python[0:7]" 所以只能写的这么蠢
		// @formatter:off
		return	(A[0] == B[0]) && (A[1] == B[1]) && (A[2] == B[2]) && (A[3] == B[3]) &&
				(A[4] == B[4]) && (A[5] == B[5]) && (A[6] == B[6]) && (A[7] == B[7]) ;
		// @formatter:on
	}

	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	public static class MessageSizeCheckFailedException extends GeneralSecurityException {
		private static final long serialVersionUID = 0;

		public MessageSizeCheckFailedException(int claimSize, int actualSize) {
			super("Message claim length is " + claimSize + ", But actual length is " + actualSize);
		}
	}

	public static class MessageHashCheckFailedException extends GeneralSecurityException {
		private static final long serialVersionUID = 0;

		public MessageHashCheckFailedException(byte[] claimHash, byte[] actualHash) {
			super("Message claim digest is " + Arrays.toString(claimHash) + ", But actual digest is " + Arrays.toString(Arrays.copyOfRange(actualHash, 0, 8)));
		}
	}
}
