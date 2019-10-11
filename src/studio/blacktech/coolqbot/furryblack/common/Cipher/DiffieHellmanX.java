package studio.blacktech.coolqbot.furryblack.common.Cipher;

import java.io.IOException;
import java.security.GeneralSecurityException;
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

public class DiffieHellmanX {

	private BASE64Encoder encoder;
	private BASE64Decoder decoder;

	private KeyFactory factory;
	private KeyAgreement agreement;

	private KeyPair keyPair;

	/**
	 * Diffi
	 */
	public DiffieHellmanX() {
		this.encoder = new BASE64Encoder();
		this.decoder = new BASE64Decoder();
		try {
			this.factory = KeyFactory.getInstance("DH");
			this.agreement = KeyAgreement.getInstance(this.factory.getAlgorithm());
		} catch (NoSuchAlgorithmException exception) {
		}
	}

	public String init() throws GeneralSecurityException {
		try {
			Provider provider = Security.getProvider("SUN");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", provider);
			KeyPairGenerator generator = KeyPairGenerator.getInstance(this.factory.getAlgorithm());
			generator.initialize(4096, random);
			this.keyPair = generator.generateKeyPair();
			this.agreement.init(this.keyPair.getPrivate());
			return encoder.encode(this.keyPair.getPublic().getEncoded());
		} catch (NoSuchAlgorithmException | InvalidKeyException exception) {
			// 这些异常不可能发生 (非标准JVM和lib除外，经过测试ADoptOpenJDK不会出现错误)
			// NoSuchAlgorithmException ----------- 不能自定义算法保证绝对合法
			// InvalidKeyException ---------------- 密钥由生成器生成保证绝对合法
			throw exception;
		}
	}

	public String init(String publicKeyString) throws GeneralSecurityException {
		try {
			byte[] publicKeyByte = decoder.decodeBuffer(publicKeyString);
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyByte);
			DHPublicKey publicKey = (DHPublicKey) factory.generatePublic(x509KeySpec);
			DHParameterSpec dhParamSpec = publicKey.getParams();
			KeyPairGenerator generator = KeyPairGenerator.getInstance(factory.getAlgorithm());
			generator.initialize(dhParamSpec);
			this.keyPair = generator.generateKeyPair();
			this.agreement.init(keyPair.getPrivate());
			this.agreement.doPhase(publicKey, true);
			return encoder.encode(this.keyPair.getPublic().getEncoded());
		} catch (IOException | InvalidKeyException exception) {
			throw new InvalidKeyException("传入了不合法的密钥");
		} catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | InvalidKeySpecException exception) {
			// 这些异常不可能发生 (非标准JVM和lib除外，经过测试ADoptOpenJDK不会出现错误)
			// NoSuchAlgorithmException ----------- 不能自定义算法保证绝对合法
			// InvalidKeySpecException ------------ 合法密钥不会生成无效值保证绝对合法
			// InvalidAlgorithmParameterException - 加密解密模式是写死的保证绝对合法
			throw exception;
		}
	}

	public SecretKeySpec generateFinalKey() {
		byte[] secret = this.agreement.generateSecret();
		return new SecretKeySpec(secret, 0, 16, "AES");
	}

	public SecretKeySpec generateFinalKey(String publicKeyString) throws GeneralSecurityException {
		try {
			byte[] publicKeyByte = decoder.decodeBuffer(publicKeyString);
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyByte);
			DHPublicKey publicKey = (DHPublicKey) factory.generatePublic(x509KeySpec);
			this.agreement.doPhase(publicKey, true);
			byte[] secret = this.agreement.generateSecret();
			return new SecretKeySpec(secret, 0, 16, "AES");
		} catch (IOException | InvalidKeyException exception) {
			throw new InvalidKeyException("传入了不合法的密钥");
		} catch (InvalidKeySpecException exception) {
			// 这些异常不可能发生 (非标准JVM和lib除外，经过测试ADoptOpenJDK不会出现错误)
			// InvalidKeySpecException ------------ 合法密钥不会生成无效值保证绝对合法
			throw exception;
		}
	}
}
