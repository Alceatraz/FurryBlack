package studio.blacktech.coolqbot.furryblack.common.Cipher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import java.util.Arrays;

import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 使用标准JavaCipher包装的Diiffie Hellman密钥协商工具类
 *
 * @author netuser
 *
 */
public class DiffieHellmanX {

	private BASE64Encoder encoder;
	private BASE64Decoder decoder;

	private KeyFactory factory;
	private KeyAgreement agreement;

	private KeyPair keyPair;

	/**
	 * 双方都使用此构造函数
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

			// 这些异常不可能发生 (非标准JVM和lib除外，经过测试ADoptOpenJDK不会出现错误)
			// NoSuchAlgorithmException ----------- 不能自定义算法保证绝对合法
			// InvalidKeyException ---------------- 密钥由生成器生成保证绝对合法
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
	 * @throws GeneralSecurityException 传入了错误的密钥，请确保是init(String
	 *                                  publicKeyString)方法返回的密钥
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

	@Test
	void doDiffieHellmanTest() throws IOException, GeneralSecurityException {

		// 预约的内容作为了初始向量
		String key = "THIS IS THE MESSAGE APPOINTMENT BY ALICE AND BOB";

		// A先发起请求
		// 生成公钥对
		// 获取A的公钥
		DiffieHellmanX A = new DiffieHellmanX();
		String publicKeyA = A.init();

		// B接受请求
		// 将A的公钥发送给B
		// B根据A的公钥生成自己的密钥对
		// B根据A的公钥和B的私钥生成最终密钥
		DiffieHellmanX B = new DiffieHellmanX();
		String publicKeyB = B.init(publicKeyA);
		SecretKeySpec keyB = B.generateFinalKey();

		// 将B的公钥发送给A
		// A根据B的公钥和A的私钥生成最终密钥
		SecretKeySpec keyA = A.generateFinalKey(publicKeyB);

		// AB两个密钥是一样的
		assertTrue(Arrays.equals(keyA.getEncoded(), keyB.getEncoded()));

		// SecretKeySpec 可以直接用于AES
		AESCipher cipherA = new AESCipher(keyA, key);
		AESCipher cipherB = new AESCipher(keyB, key);

		String raw = "Hello, World!";
		String tmp = cipherA.encrypt(raw);
		String res = cipherB.decrypt(tmp);

		assertEquals(raw, res);

		System.out.println(tmp);

	}

}
