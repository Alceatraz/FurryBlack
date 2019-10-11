package studio.blacktech.coolqbot.furryblack.common.Cipher;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class RSACipher {

	private static final Charset UTF_8 = Charset.forName("UTF-8");

	private BASE64Encoder encoder;
	private BASE64Decoder decoder;

	private RSAPublicKey publicKey;
	private RSAPrivateKey privateKey;

	private Cipher encrypter;
	private Cipher decrypter;

	public RSACipher() {
		this.encoder = new BASE64Encoder();
		this.decoder = new BASE64Decoder();
	}

	/**
	 * 通过密钥长度和随机种子生成新的密钥
	 *
	 * @param secretKey 随机种子
	 * @param keyLength 密钥长度
	 */
	public RSACipher(String secretKey, int keyLength) {
		this();
		try {
			Provider provider = Security.getProvider("SUN");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", provider);
			random.setSeed(secretKey.getBytes(UTF_8));
			KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(keyLength, random);
			KeyPair keyPair = generator.generateKeyPair();
			this.publicKey = (RSAPublicKey) keyPair.getPublic();
			this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
			this.encrypter = Cipher.getInstance("RSA");
			this.decrypter = Cipher.getInstance("RSA");
			this.encrypter.init(Cipher.ENCRYPT_MODE, this.publicKey);
			this.decrypter.init(Cipher.DECRYPT_MODE, this.privateKey);
		} catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException exception) {
			// 这些异常不可能发生 (非标准JVM和lib除外，经过测试ADoptOpenJDK不会出现错误)
			// NoSuchAlgorithmException ----------- 不能自定义算法保证绝对合法
		}
	}

	/**
	 * 输入X509编码的密钥
	 *
	 * @param publicKey  x509公钥 Base64
	 * @param privateKey x509私钥 Base64
	 * @throws IOException             传入错误密钥将会产生异常 - 应经过BASE64编码
	 * @throws InvalidKeySpecException 传入错误密钥将会产生异常 - 不是X509密钥
	 */
	public RSACipher(String publicKey, String privateKey) throws InvalidKeySpecException, IOException {
		this();
		try {
			byte[] publicKeyString = decoder.decodeBuffer(publicKey);
			byte[] privateKeyString = decoder.decodeBuffer(privateKey);
			KeyFactory factory = KeyFactory.getInstance("RSA");
			this.publicKey = (RSAPublicKey) factory.generatePublic(new X509EncodedKeySpec(publicKeyString));
			this.privateKey = (RSAPrivateKey) factory.generatePrivate(new X509EncodedKeySpec(privateKeyString));
			this.encrypter = Cipher.getInstance("RSA");
			this.decrypter = Cipher.getInstance("RSA");
			this.encrypter.init(Cipher.ENCRYPT_MODE, this.publicKey);
			this.decrypter.init(Cipher.DECRYPT_MODE, this.privateKey);
		} catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException exception) {
			// 这些异常不可能发生 (非标准JVM和lib除外，经过测试ADoptOpenJDK不会出现错误)
			// NoSuchAlgorithmException ----------- 不能自定义算法保证绝对合法
			// NoSuchPaddingException ------------- 不能自定义补位保证绝对合法
			// InvalidKeyException ---------------- 密钥由生成器生成保证绝对合法
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

	/***
	 * 获取公钥 Base64
	 * 
	 * @return x509
	 */
	public String getPublicKeyBase64() {
		return encoder.encode(this.publicKey.getEncoded());
	}

	/***
	 * 获取私钥 Base64
	 * 
	 * @return x509
	 */
	public String getPrivateKeyBase64() {
		return encoder.encode(this.privateKey.getEncoded());
	}
}
