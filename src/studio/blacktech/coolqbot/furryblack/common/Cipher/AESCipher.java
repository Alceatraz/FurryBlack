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

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class AESCipher {

	private static final Charset UTF_8 = Charset.forName("UTF-8");

	private SecretKeySpec sk;
	private IvParameterSpec iv;
	private Cipher encrypter;
	private Cipher decrypter;
	private BASE64Encoder encoder;
	private BASE64Decoder decoder;

	/**
	 * AES + BASE64 工具类
	 *
	 * @param secretKey     密钥种子，任意长度，不做处理
	 * @param initialVector 初始向量，任意长度，MD5处理
	 */
	public AESCipher(String secretKey, String initialVector) {
		try {
			Provider provider = Security.getProvider("SUN");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", provider);
			random.setSeed(secretKey.getBytes(UTF_8));
			KeyGenerator generator = KeyGenerator.getInstance("AES");
			generator.init(128, random);
			SecretKey skey = generator.generateKey();
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(initialVector.getBytes(UTF_8));
			this.sk = new SecretKeySpec(skey.getEncoded(), "AES");
			this.iv = new IvParameterSpec(digest.digest());
			this.encrypter = Cipher.getInstance("AES/CBC/PKCS5Padding");
			this.decrypter = Cipher.getInstance("AES/CBC/PKCS5Padding");
			this.encrypter.init(Cipher.ENCRYPT_MODE, this.sk, this.iv);
			this.decrypter.init(Cipher.DECRYPT_MODE, this.sk, this.iv);
			this.encoder = new BASE64Encoder();
			this.decoder = new BASE64Decoder();
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException exception) {
			// 这些异常不可能发生 (非标准JVM和lib除外，经过测试ADoptOpenJDK不会出现错误)
			// NoSuchAlgorithmException ----------- 不能自定义算法保证绝对合法
			// NoSuchPaddingException ------------- 不能自定义补位保证绝对合法
			// InvalidKeyException ---------------- 密钥由生成器生成保证绝对合法
			// InvalidAlgorithmParameterException - 加密解密模式是写死的保证绝对合法
		}
	}

	/**
	 * 加密
	 *
	 * @param content 需要加密的内容
	 * @return 加密后的内容
	 * @throws GeneralSecurityException Cipher产生
	 *
	 */
	public String encrypt(String content) throws GeneralSecurityException {
		byte[] tmp1 = content.getBytes(UTF_8);
		byte[] tmp2 = this.encrypter.doFinal(tmp1);
		return this.encoder.encode(tmp2);
	}

	/**
	 * 解密
	 *
	 * @param content 需要解密的内容
	 * @return 解密后的内容
	 * @throws IOException              SUN Base64库产生
	 * @throws GeneralSecurityException Cipher产生
	 *
	 */
	public String decrypt(String content) throws GeneralSecurityException, IOException {
		byte[] tmp1 = this.decoder.decodeBuffer(content);
		byte[] tmp2 = this.decrypter.doFinal(tmp1);
		return new String(tmp2, UTF_8);
	}

	/**
	 * 加密 unsafe指不会抛出异常，返回null
	 *
	 * @param content 需要加密的内容
	 * @return 加密后的内容 发生异常则为null
	 *
	 */
	public String unsafeEncrypt(String content) {
		try {
			byte[] tmp1 = content.getBytes(UTF_8);
			byte[] tmp2 = this.encrypter.doFinal(tmp1);
			return this.encoder.encode(tmp2);
		} catch (Exception exception) {
			return null;
		}
	}

	/**
	 * 解密 unsafe指不会抛出异常，返回null
	 *
	 * @param content 需要解密的内容
	 * @return 解密后的内容 发生异常则为null
	 *
	 */
	public String unsafeDecrypt(String content) {
		try {
			byte[] tmp1 = this.decoder.decodeBuffer(content);
			byte[] tmp2 = this.decrypter.doFinal(tmp1);
			return new String(tmp2, UTF_8);
		} catch (Exception exception) {
			return null;
		}
	}
}
