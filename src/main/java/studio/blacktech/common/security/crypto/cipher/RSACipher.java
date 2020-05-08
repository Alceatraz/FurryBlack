package studio.blacktech.common.security.crypto.cipher;


import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


/**
 * 使用标准JavaCipher包装的RSA工具类，
 * 包含三种加密模式：标准加密、使用SHA-384进行消息验证、使用签名后不初始化的SHA-384进行消息验证。
 * 带消息验证的数据帧为：
 * 00 00 00 00 , 00 00 00 00 - 00 00 00 00 , 00 00 00 00 - 原文
 * 前8位 原始消息getBytes(UTF-8)后数组的长度 int → hexString → getBytes(UTF-8)
 * 后8位 SHA-384的前8位
 * 之后为原始数据getBytes(UTF-8)数据帧经过AES加密和Base64编码，成为密文。
 *
 * @author BTS - Alceatraz Warprays alceatraz@blacktech.studio
 */

public class RSACipher {


	private RSAPublicKey publicKey;
	private RSAPrivateKey privateKey;

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
	 * 构造方法
	 *
	 * @param secretKey 随机种子，作为密钥生成器的随机数生成器的种子
	 * @param keyLength 密钥长度，至少为512
	 */
	public RSACipher(String secretKey, int keyLength) {
		this(Objects.requireNonNull(RSACipher.generateKeyPair(secretKey, keyLength)));
	}


	/**
	 * 构造方法
	 *
	 * @param keyPair 密钥对
	 */
	public RSACipher(KeyPair keyPair) {
		this((RSAPublicKey) keyPair.getPublic(), (RSAPrivateKey) keyPair.getPrivate());
	}


	/**
	 * 构造方法
	 *
	 * @param publicKey  Base64编码的X509格式公钥
	 * @param privateKey Base64编码的PKCS8格式私钥
	 * @throws InvalidPublicKeyException  公钥格式错误
	 * @throws InvalidPrivateKeyException 私钥格式错误
	 */
	public RSACipher(String publicKey, String privateKey) throws InvalidPublicKeyException, InvalidPrivateKeyException {
		this(RSACipher.getRSAPublicKeyFromString(publicKey), RSACipher.getRSAPrivateKeyFromString(privateKey));
	}


	/**
	 * 构造方法
	 *
	 * @param publicKey RSA公钥
	 */
	public RSACipher(RSAPublicKey publicKey) {
		try {
			this.publicKey = publicKey;
			this.encrypter = Cipher.getInstance("RSA");
			this.encrypter.init(Cipher.ENCRYPT_MODE, this.publicKey);
			this.staticDigester = MessageDigest.getInstance("SHA-384");
			this.oneoffDigester = MessageDigest.getInstance("SHA-384");
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException exception) {
			// 这些异常不可能发生
			// InvalidKeyException ---------------- 由密钥生成器生成，输入密钥错误已经在上一级构造方法抛出
			// NoSuchPaddingException ------------- 不允许用户自定义算法
			// NoSuchAlgorithmException ----------- 不允许用户自定义算法
		}
	}


	/**
	 * 构造方法
	 *
	 * @param privateKey RSA公钥
	 */
	public RSACipher(RSAPrivateKey privateKey) {
		try {
			this.privateKey = privateKey;
			this.decrypter = Cipher.getInstance("RSA");
			this.decrypter.init(Cipher.DECRYPT_MODE, this.privateKey);
			this.staticDigester = MessageDigest.getInstance("SHA-384");
			this.oneoffDigester = MessageDigest.getInstance("SHA-384");
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException exception) {
			// 这些异常不可能发生
			// InvalidKeyException ---------------- 由密钥生成器生成，输入密钥错误已经在上一级构造方法抛出
			// NoSuchPaddingException ------------- 不允许用户自定义算法
			// NoSuchAlgorithmException ----------- 不允许用户自定义算法
		}
	}


	/**
	 * 构造方法
	 *
	 * @param publicKey  RSA公钥
	 * @param privateKey RSA私钥
	 */
	public RSACipher(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
		try {
			this.publicKey = publicKey;
			this.privateKey = privateKey;
			this.encrypter = Cipher.getInstance("RSA");
			this.decrypter = Cipher.getInstance("RSA");
			this.encrypter.init(Cipher.ENCRYPT_MODE, this.publicKey);
			this.decrypter.init(Cipher.DECRYPT_MODE, this.privateKey);
			this.staticDigester = MessageDigest.getInstance("SHA-384");
			this.oneoffDigester = MessageDigest.getInstance("SHA-384");
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException exception) {
			// 这些异常不可能发生
			// InvalidKeyException ---------------- 由密钥生成器生成，输入密钥错误已经在上一级构造方法抛出
			// NoSuchPaddingException ------------- 不允许用户自定义算法
			// NoSuchAlgorithmException ----------- 不允许用户自定义算法
		}
	}

	/**
	 * 静态构造方法
	 *
	 * @param publicKey RSA公钥
	 */
	public static RSACipher getEncryptInstance(String publicKey) throws InvalidPublicKeyException {
		return new RSACipher(RSACipher.getRSAPublicKeyFromString(publicKey));
	}


	/**
	 * 静态构造方法
	 *
	 * @param privateKey RSA私钥
	 */
	public static RSACipher getDecryptInstance(String privateKey) throws InvalidPrivateKeyException {
		return new RSACipher(RSACipher.getRSAPrivateKeyFromString(privateKey));
	}


	/**
	 * 静态构造方法
	 *
	 * @param publicKey RSA公钥
	 */
	public static RSACipher getEncryptInstance(RSAPublicKey publicKey) throws InvalidPublicKeyException {
		return new RSACipher(publicKey);
	}


	/**
	 * 静态构造方法
	 *
	 * @param privateKey RSA私钥
	 */
	public static RSACipher getDecryptInstance(RSAPrivateKey privateKey) throws InvalidPrivateKeyException {
		return new RSACipher(privateKey);
	}


	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================


	private static RSAPublicKey getRSAPublicKeyFromString(String publicKey) throws InvalidPublicKeyException {
		try {
			KeyFactory factory = KeyFactory.getInstance("RSA");
			byte[] publicKeyString = decoder.decode(publicKey);
			return (RSAPublicKey) factory.generatePublic(new X509EncodedKeySpec(publicKeyString));
		} catch (InvalidKeySpecException exception) {
			throw new InvalidPublicKeyException("Invalidate publickey, make sure is formated as X509 and encode with BASE64.");
		} catch (NoSuchAlgorithmException exception) {
			return null;
			// 这些异常不可能发生
			// NoSuchAlgorithmException ----------- 不允许用户自定义算法
		}
	}


	private static RSAPrivateKey getRSAPrivateKeyFromString(String privateKey) throws InvalidPrivateKeyException {
		try {
			KeyFactory factory = KeyFactory.getInstance("RSA");
			byte[] privateKeyString = decoder.decode(privateKey);
			return (RSAPrivateKey) factory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyString));
		} catch (InvalidKeySpecException exception) {
			throw new InvalidPrivateKeyException("Invalidate publickey, make sure is formated as PKCS8 and encode with BASE64.");
		} catch (NoSuchAlgorithmException exception) {
			return null;
			// 这些异常不可能发生
			// NoSuchAlgorithmException ----------- 不允许用户自定义算法
		}
	}


	private static KeyPair generateKeyPair(String randomSeed, int keyLength) {
		try {
			Provider provider = Security.getProvider("SUN");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", provider);
			random.setSeed(randomSeed.getBytes(StandardCharsets.UTF_8));
			KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(keyLength, random);
			return generator.generateKeyPair();
		} catch (NoSuchAlgorithmException exception) {
			return null;
			// 这些异常不可能发生
			// NoSuchAlgorithmException ----------- 不允许用户自定义算法
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
			byte[] tmp2 = this.encrypter.doFinal(tmp1);
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
	 */
	public String decrypt(String content) {
		try {
			byte[] tmp1 = decoder.decode(content);
			byte[] tmp2 = this.decrypter.doFinal(tmp1);
			return new String(tmp2, StandardCharsets.UTF_8);
		} catch (IllegalBlockSizeException | BadPaddingException exception) {
			exception.printStackTrace();
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
			this.oneoffDigester.update(rawMessage);
			hashPart = this.oneoffDigester.digest();
			System.arraycopy(hashPart, 0, result, 8, 8);
			System.arraycopy(rawMessage, 0, result, 16, rawMessageLength);
			byte[] tmp1 = this.encrypter.doFinal(result);
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
			byte[] rawMessage = this.decrypter.doFinal(decoder.decode(content));
			byte[] sizePart = new byte[8];
			byte[] hashPart = new byte[8];
			int actualMessageLength = rawMessage.length - 16;
			System.arraycopy(rawMessage, 0, sizePart, 0, 8);
			int claimMessageLength = Integer.valueOf(new String(sizePart).trim(), 16);
			if (claimMessageLength != actualMessageLength) throw new MessageSizeCheckFailedException(claimMessageLength, actualMessageLength);
			System.arraycopy(rawMessage, 8, hashPart, 0, 8);
			byte[] messagePart = new byte[claimMessageLength];
			System.arraycopy(rawMessage, 16, messagePart, 0, claimMessageLength);
			this.oneoffDigester.update(messagePart);
			byte[] digest = this.oneoffDigester.digest();
			if (!isDigestValidate(hashPart, digest)) throw new MessageHashCheckFailedException(hashPart, digest);
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
			this.staticDigester.update(rawMessage);
			hashPart = ((MessageDigest) this.staticDigester.clone()).digest();
			System.arraycopy(hashPart, 0, result, 8, 8);
			System.arraycopy(rawMessage, 0, result, 16, rawMessageLength);
			byte[] tmp1 = this.encrypter.doFinal(result);
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
			byte[] rawMessage = this.decrypter.doFinal(decoder.decode(content));
			byte[] sizePart = new byte[8];
			byte[] hashPart = new byte[8];
			int actualMessageLength = rawMessage.length - 16;
			System.arraycopy(rawMessage, 0, sizePart, 0, 8);
			int claimMessageLength = Integer.valueOf(new String(sizePart).trim(), 16);
			if (claimMessageLength != actualMessageLength) throw new MessageSizeCheckFailedException(claimMessageLength, actualMessageLength);
			System.arraycopy(rawMessage, 8, hashPart, 0, 8);
			byte[] messagePart = new byte[claimMessageLength];
			System.arraycopy(rawMessage, 16, messagePart, 0, claimMessageLength);
			this.staticDigester.update(messagePart);
			byte[] digest = ((MessageDigest) this.staticDigester.clone()).digest();
			if (!isDigestValidate(hashPart, digest)) throw new MessageHashCheckFailedException(hashPart, digest);
			return new String(messagePart, StandardCharsets.UTF_8);
		} catch (IllegalBlockSizeException | BadPaddingException | CloneNotSupportedException exception) {
			return null;
			// 这些异常不可能发生
			// BadPaddingException -------- 不允许用户自定义算法
			// IllegalBlockSizeException -- 不允许用户传入byte[]
			// CloneNotSupportedException - MessageDigest是能够克隆的
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


	public String getEncodedPublicKey() {
		return new String(encoder.encode(this.publicKey.getEncoded()), StandardCharsets.UTF_8);
	}

	public String getEncodedPrivateKey() {
		return new String(encoder.encode(this.privateKey.getEncoded()), StandardCharsets.UTF_8);
	}


	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================


	/**
	 * 静态工具方法 生成密钥对
	 * 
	 * @param secretKey
	 * @param keyLength
	 * @return
	 */
	public static KeyPair generateRSAKeyPair(String secretKey, int keyLength) {
		return generateKeyPair(secretKey, keyLength);
	}


	public static RSAPublicKey toRSAPublicKey(KeyPair keyPair) {
		return (RSAPublicKey) keyPair.getPublic();
	}


	public static RSAPrivateKey toRSAPrivateKey(KeyPair keyPair) {
		return (RSAPrivateKey) keyPair.getPrivate();
	}


	public static String toEncodedRSAPublicKey(KeyPair keyPair) {
		return new String(encoder.encode(toRSAPublicKey(keyPair).getEncoded()), StandardCharsets.UTF_8);
	}


	public static String toEncodedRSAPrivateKey(KeyPair keyPair) {
		return new String(encoder.encode(toRSAPrivateKey(keyPair).getEncoded()), StandardCharsets.UTF_8);
	}


	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================


	public static class MessageSizeCheckFailedException extends GeneralSecurityException {

		private static final long serialVersionUID = 1L;

		public MessageSizeCheckFailedException(int claimSize, int actualSize) {
			super("Message length dismatch, Claim " + claimSize + ", But actual " + actualSize);
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


	public static class InvalidPublicKeyException extends InvalidKeyException {

		private static final long serialVersionUID = 1L;

		public InvalidPublicKeyException(String message) {
			super(message);
		}
	}


	public static class InvalidPrivateKeyException extends InvalidKeyException {

		private static final long serialVersionUID = 1L;

		public InvalidPrivateKeyException(String message) {
			super(message);
		}
	}


}
