package studio.blacktech.coolqbot.furryblack.common.Cipher;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/***
 * 使用标准JavaCipher包装的AES-CBC工具类，
 * 包含三种加密模式：标准加密、使用sha384进行消息验证、使用签名后不初始化的SHA-384进行消息验证。
 *
 * @author Alceatraz Warprays
 *
 */
public class AESCipher {

	private static final Charset UTF_8 = Charset.forName("UTF-8");

	private SecretKeySpec sk;
	private IvParameterSpec iv;
	private Cipher encrypter;
	private Cipher decrypter;
	private BASE64Encoder encoder;
	private BASE64Decoder decoder;
	private MessageDigest digester;

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
	@Deprecated
	public AESCipher(String secretKey) {
		this(generateSecretKeySpec(secretKey), generateIvParameterSpec("0123456789ABCDEF"));
		System.err.println("Warning! Using fix IV is RISK! Only test purpose!");
	}

	/**
	 * 构造方法
	 *
	 * @param secretKeySpec 密钥
	 */
	@Deprecated
	public AESCipher(SecretKeySpec secretKeySpec) {
		this(secretKeySpec, generateIvParameterSpec("0123456789ABCDEF"));
		System.err.println("Warning! Using fix IV is RISK! Only test purpose!");
	}

	/**
	 * 构造方法
	 *
	 * @param secretKey     随机种子，作为密钥生成器的随机数生成器的种子
	 * @param initialVector 初始向量种子，MD5后用于生成初始向量
	 */
	public AESCipher(String secretKey, String initialVector) {
		this(generateSecretKeySpec(secretKey), generateIvParameterSpec(initialVector));
	}

	/**
	 * 构造方法
	 *
	 * @param secretKeySpec 密钥
	 * @param initialVector 初始向量种子，MD5后用于生成初始向量
	 */
	public AESCipher(SecretKeySpec secretKeySpec, String initialVector) {
		this(secretKeySpec, generateIvParameterSpec(initialVector));
	}

	/**
	 * 构造方法
	 *
	 * @param secretKey     随机种子，作为密钥生成器的随机数生成器的种子
	 * @param initialVector 初始向量
	 */
	public AESCipher(String secretKey, IvParameterSpec initialVector) {
		this(generateSecretKeySpec(secretKey), initialVector);
	}

	/**
	 * 构造方法
	 *
	 * @param secretKeySpec 密钥
	 * @param initialVector 初始向量
	 */
	public AESCipher(SecretKeySpec secretKeySpec, IvParameterSpec initialVector) {

		try {

			this.sk = secretKeySpec;
			this.iv = initialVector;

			this.encrypter = Cipher.getInstance("AES/CBC/PKCS5Padding");
			this.decrypter = Cipher.getInstance("AES/CBC/PKCS5Padding");

			this.encrypter.init(Cipher.ENCRYPT_MODE, this.sk, this.iv);
			this.decrypter.init(Cipher.DECRYPT_MODE, this.sk, this.iv);

			this.encoder = new BASE64Encoder();
			this.decoder = new BASE64Decoder();

			this.digester = MessageDigest.getInstance("SHA-384");

		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException exception) {
			exception.printStackTrace();
			// 这些异常不可能发生 - 使用ADoptOpenJDK 8
			// NoSuchAlgorithmException ----------- 不能自定义算法保证绝对合法
			// NoSuchPaddingException ------------- 不能自定义补位保证绝对合法
			// InvalidKeyException ---------------- 密钥由生成器生成保证绝对合法
			// InvalidAlgorithmParameterException - 加密解密模式是写死的保证绝对合法
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
			random.setSeed(secretKey.getBytes(UTF_8));
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
			digest.update(initialVector.getBytes(UTF_8));
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
	 * @throws GeneralSecurityException
	 */
	public String encrypt(String content) {

		try {

			byte[] tmp1 = content.getBytes(UTF_8);
			byte[] tmp2 = this.encrypter.doFinal(tmp1);
			return this.encoder.encode(tmp2);

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
	 */
	public String decrypt(String content) throws IOException {

		try {

			byte[] tmp1 = this.decoder.decodeBuffer(content);
			byte[] tmp2 = this.decrypter.doFinal(tmp1);
			return new String(tmp2, UTF_8);

		} catch (IOException exception) {
			exception.printStackTrace();
			throw exception;

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
	public String safeEncrypt(String content) {

		try {

			byte[] rawMessage = content.getBytes(UTF_8);

			byte[] sizePart = new byte[8];
			byte[] hashPart = new byte[8];

			int rawMessageLength = rawMessage.length;
			byte[] result = new byte[16 + rawMessageLength];

			sizePart = Integer.toHexString(rawMessageLength).getBytes(UTF_8);
			int sizePartLength = sizePart.length;
			System.arraycopy(sizePart, 0, result, 8 - sizePartLength, sizePartLength);

			MessageDigest tempDigester = MessageDigest.getInstance("SHA-384");
			tempDigester.update(rawMessage);
			hashPart = tempDigester.digest();
			System.arraycopy(hashPart, 0, result, 8, 8);

			System.arraycopy(rawMessage, 0, result, 16, rawMessageLength);

			return this.encoder.encode(this.encrypter.doFinal(result));

		} catch (NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException exception) {
			return null;
			// 这些异常不可能发生
			// BadPaddingException ------- 不允许用户自定义算法
			// NoSuchAlgorithmException -- 不允许用户自定义算法
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
	public String safeDecrypt(String content) throws IOException, MessageSizeCheckFailedException, MessageHashCheckFailedException {

		try {

			byte[] rawMessage = this.decrypter.doFinal(this.decoder.decodeBuffer(content));

			byte[] sizePart = new byte[8];
			byte[] hashPart = new byte[8];

			int actualMessageLength = rawMessage.length - 16;

			System.arraycopy(rawMessage, 0, sizePart, 0, 8);
			int claminMessagelength = Integer.valueOf(new String(sizePart).trim(), 16);
			if (claminMessagelength != actualMessageLength) { throw new MessageSizeCheckFailedException(claminMessagelength, actualMessageLength); }

			System.arraycopy(rawMessage, 8, hashPart, 0, 8);

			byte[] mesgPart = new byte[claminMessagelength];
			System.arraycopy(rawMessage, 16, mesgPart, 0, claminMessagelength);
			MessageDigest tempDigester = MessageDigest.getInstance("SHA-384");
			tempDigester.update(mesgPart);
			byte[] digest = tempDigester.digest();

			if (!isSame(hashPart, digest)) { throw new MessageHashCheckFailedException(hashPart, digest); }

			return new String(mesgPart, UTF_8);

		} catch (NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException exception) {
			return null;
			// 这些异常不可能发生
			// BadPaddingException ------- 不允许用户自定义算法
			// NoSuchAlgorithmException -- 不允许用户自定义算法
			// IllegalBlockSizeException - 不允许用户传入byte[]
		}
	}

	public String safeStackEncrypt(String content) {

		try {

			byte[] rawMessage = content.getBytes(UTF_8);

			byte[] sizePart = new byte[8];
			byte[] hashPart = new byte[8];

			int rawMessageLength = rawMessage.length;
			byte[] result = new byte[16 + rawMessageLength];

			sizePart = Integer.toHexString(rawMessageLength).getBytes(UTF_8);
			int sizePartLength = sizePart.length;
			System.arraycopy(sizePart, 0, result, 8 - sizePartLength, sizePartLength);

			this.digester.update(rawMessage);
			hashPart = ((MessageDigest) this.digester.clone()).digest();
			System.arraycopy(hashPart, 0, result, 8, 8);

			System.arraycopy(rawMessage, 0, result, 16, rawMessageLength);

			return this.encoder.encode(this.encrypter.doFinal(result));

		} catch (IllegalBlockSizeException | BadPaddingException | CloneNotSupportedException exception) {
			return null;
			// 这些异常不可能发生
			// BadPaddingException -------- 不允许用户自定义算法
			// IllegalBlockSizeException -- 不允许用户传入byte[]
			// CloneNotSupportedException - MessageDigest是能够克隆的
		}
	}

	public String safeStackDecrypt(String content) throws IOException, MessageSizeCheckFailedException, MessageHashCheckFailedException {
		try {

			byte[] rawMessage = this.decrypter.doFinal(this.decoder.decodeBuffer(content));

			byte[] sizePart = new byte[8];
			byte[] hashPart = new byte[8];

			int actualMessageLength = rawMessage.length - 16;

			System.arraycopy(rawMessage, 0, sizePart, 0, 8);
			int claminMessagelength = Integer.valueOf(new String(sizePart).trim(), 16);
			if (claminMessagelength != actualMessageLength) { throw new MessageSizeCheckFailedException(claminMessagelength, actualMessageLength); }

			System.arraycopy(rawMessage, 8, hashPart, 0, 8);

			byte[] mesgPart = new byte[claminMessagelength];
			System.arraycopy(rawMessage, 16, mesgPart, 0, claminMessagelength);

			this.digester.update(mesgPart);
			byte[] digest = ((MessageDigest) this.digester.clone()).digest();

			if (!isSame(hashPart, digest)) { throw new MessageHashCheckFailedException(hashPart, digest); }

			return new String(mesgPart, UTF_8);

		} catch (IOException exception) {
			exception.printStackTrace();
			throw exception;

		} catch (IllegalBlockSizeException | BadPaddingException | CloneNotSupportedException exception) {
			return null;
			// 这些异常不可能发生
			// BadPaddingException ------- 不允许用户自定义算法
			// NoSuchAlgorithmException -- 不允许用户自定义算法
			// IllegalBlockSizeException - 不允许用户传入byte[]
		}
	}

	private static boolean isSame(byte[] A, byte[] B) {
		// @formatter:off
		return	A[0] == B[0] &&
				A[1] == B[1] &&
				A[2] == B[2] &&
				A[3] == B[3] &&
				A[4] == B[4] &&
				A[5] == B[5] &&
				A[6] == B[6] &&
				A[7] == B[7] ;
		// @formatter:on
	}

	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	public class MessageSizeCheckFailedException extends GeneralSecurityException {
		private static final long serialVersionUID = 0;

		public MessageSizeCheckFailedException(int claimSize, int actualSize) {
			super("Message claim length is " + claimSize + ", But actual length is " + actualSize);
		}
	}

	public class MessageHashCheckFailedException extends GeneralSecurityException {
		private static final long serialVersionUID = 0;

		public MessageHashCheckFailedException(byte[] claimHash, byte[] actualHash) {
			super("Message claim digest is " + Arrays.toString(claimHash) + ", But actual digest is " + Arrays.toString(Arrays.copyOfRange(actualHash, 0, 8)));
		}
	}

}
