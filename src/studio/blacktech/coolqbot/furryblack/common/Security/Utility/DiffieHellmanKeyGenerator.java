package studio.blacktech.coolqbot.furryblack.common.Security.Utility;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/***
 * 使用标准JavaCipher包装的Diffie Hellman密钥交换算法，生成标准SecretKeySpec密钥。 *
 *
 * @author Alceatraz Warprays
 *
 */
public class DiffieHellmanKeyGenerator {

	private BASE64Encoder encoder;
	private BASE64Decoder decoder;

	private KeyFactory factory;
	private KeyAgreement agreement;

	private KeyPair keyPair;

	/**
	 * 双方都使用此构造函数
	 */
	public DiffieHellmanKeyGenerator() {

		this.encoder = new BASE64Encoder();
		this.decoder = new BASE64Decoder();

		try {

			this.factory = KeyFactory.getInstance("DH");
			this.agreement = KeyAgreement.getInstance(this.factory.getAlgorithm());

		} catch (NoSuchAlgorithmException exception) {
			// 这些异常不可能发生 - 使用ADoptOpenJDK 8
			// NoSuchAlgorithmException ----------- 不允许用户自定义算法
		}

	}

	/**
	 * 发起方（Alice）使用这个方法初始化
	 *
	 * @return 发起方公钥
	 */
	public String init() {

		try {

			Provider provider = Security.getProvider("SUN");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", provider);
			KeyPairGenerator generator = KeyPairGenerator.getInstance(this.factory.getAlgorithm());
			generator.initialize(4096, random);

			this.keyPair = generator.generateKeyPair();
			this.agreement.init(this.keyPair.getPrivate());

			return this.encoder.encode(this.keyPair.getPublic().getEncoded());

		} catch (NoSuchAlgorithmException | InvalidKeyException exception) {
			// 这些异常不可能发生 - 使用ADoptOpenJDK 8
			// NoSuchAlgorithmException ----------- 不允许用户自定义算法
			// InvalidKeyException ---------------- 密钥由生成器生成
			return null;

		}
	}

	/**
	 * 接收方（Bob）使用这个方法初始化
	 *
	 * @param publicKeyString 发起方的公钥
	 * @return 接收方的公钥
	 * @throws InvalidKeyException 传入了错误的密钥，请确保是init()方法返回的密钥
	 */
	public String init(String publicKeyString) throws InvalidKeyException {

		try {

			byte[] publicKeyByte = this.decoder.decodeBuffer(publicKeyString);

			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyByte);
			DHPublicKey publicKey = (DHPublicKey) this.factory.generatePublic(x509KeySpec);
			DHParameterSpec dhParamSpec = publicKey.getParams();
			KeyPairGenerator generator = KeyPairGenerator.getInstance(this.factory.getAlgorithm());
			generator.initialize(dhParamSpec);

			this.keyPair = generator.generateKeyPair();
			this.agreement.init(this.keyPair.getPrivate());
			this.agreement.doPhase(publicKey, true);

			return this.encoder.encode(this.keyPair.getPublic().getEncoded());

		} catch (IOException | InvalidKeyException exception) {

			throw new InvalidKeyException("传入了不合法的密钥");

		} catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | InvalidKeySpecException exception) {

			// 这些异常不可能发生 (非标准JVM和lib除外，经过测试ADoptOpenJDK不会出现错误)
			// NoSuchAlgorithmException ----------- 不能自定义算法保证绝对合法
			// InvalidKeySpecException ------------ 合法密钥不会生成无效值保证绝对合法
			// InvalidAlgorithmParameterException - 加密解密模式是写死的保证绝对合法
			return null;

		}
	}

	/**
	 * 接收方（Bob）使用此方法生成最终密钥
	 *
	 * @return SecretKeySpec密钥
	 */
	public SecretKeySpec generateFinalKey() {

		byte[] secret = this.agreement.generateSecret();
		return new SecretKeySpec(secret, 0, 16, "AES");

	}

	/**
	 * 发起方（Alice）使用此方法生成最终密钥
	 *
	 * @param publicKeyString 接收方的公钥
	 * @return SecretKeySpec密钥
	 * @throws InvalidKeyException 传入了错误的密钥
	 */
	public SecretKeySpec generateFinalKey(String publicKeyString) throws InvalidKeyException {

		try {

			byte[] publicKeyByte = this.decoder.decodeBuffer(publicKeyString);
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyByte);
			DHPublicKey publicKey = (DHPublicKey) this.factory.generatePublic(x509KeySpec);

			this.agreement.doPhase(publicKey, true);
			byte[] secret = this.agreement.generateSecret();

			return new SecretKeySpec(secret, 0, 16, "AES");

		} catch (IOException | InvalidKeyException exception) {

			throw new InvalidKeyException("传入了不合法的密钥");

		} catch (InvalidKeySpecException exception) {

			// 这些异常不可能发生 (非标准JVM和lib除外，经过测试ADoptOpenJDK不会出现错误)
			// InvalidKeySpecException ------------ 合法密钥不会生成无效值保证绝对合法
			return null;
		}
	}

}
