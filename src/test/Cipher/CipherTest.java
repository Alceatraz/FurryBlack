package test.Cipher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;

import studio.blacktech.coolqbot.furryblack.common.Security.Cipher.AESCipher;
import studio.blacktech.coolqbot.furryblack.common.Security.Cipher.RSACipher;
import studio.blacktech.coolqbot.furryblack.common.Security.Utility.DiffieHellmanKeyGenerator;
import studio.blacktech.coolqbot.furryblack.common.Security.Utility.TimeBaseVerification;

@SuppressWarnings("all")
public class CipherTest {

	@Test
	void doAESCipherTest() throws GeneralSecurityException, IOException {

		String tmp;
		String res;

		String raw = "Hello, World!";

		// 实例化 Cipher
		AESCipher cipher = new AESCipher("0123456789", "0123456789");

		// 普通加密模式
		tmp = cipher.encrypt(raw);
		res = cipher.decrypt(tmp);
		assertEquals(raw, res);

		// 增加hash验证的加密模式
		tmp = cipher.encryptHash(raw);
		res = cipher.decryptHash(tmp);
		assertEquals(raw, res);

		// PhaseHash，签名过程使用克隆体，即不会reset STATE

		AESCipher cipherAlice = new AESCipher("0123456789", "0123456789");
		AESCipher cipherBob = new AESCipher("0123456789", "0123456789");
		AESCipher cipherMallory = new AESCipher("0123456789", "0123456789");

		String aliceMessage1 = "I Love You";
		String aliceMessage2 = "May you marry me?";
		String malloryMessage = "I Hate You";

		// 第一次发送消息， Digest使用 aliceMessage1 进行更新
		String encryptByAlice1 = cipherAlice.encraptPhaseHash(aliceMessage1);
		String decryptByBob1 = cipherBob.decryptPhaseHash(encryptByAlice1);
		assertEquals(aliceMessage1, decryptByBob1);

		// 第二次发送消息， Digest使用 aliceMessage2 进行更新
		String encryptByAlice2 = cipherAlice.encraptPhaseHash(aliceMessage2);
		String decryptByBob2 = cipherBob.decryptPhaseHash(encryptByAlice2);
		assertEquals(aliceMessage2, decryptByBob2);

		// Mallory的Digest是初始状态，没有经过aliceMessage1 aliceMessage2更新，所以Bob进行完整性检验时将会失败
		String encryptByMallory = cipherMallory.encraptPhaseHash(malloryMessage);

		try {
			// 这里会抛出hash不一致的异常
			String decryptByBob3 = cipherBob.decryptPhaseHash(encryptByMallory);
			assertEquals(cipherMallory, decryptByBob3);

		} catch (Exception exception) {
			System.err.println(exception.getMessage());
		}

	}

	@Test
	void doRSACipherTest() throws GeneralSecurityException, IOException {

		String tmp;
		String res;

		String raw = "Hello, World!";

		// 实例化 Cipher
		RSACipher cipher = new RSACipher("0123456789", 512);

		// 普通加密模式
		tmp = cipher.encrypt(raw);
		res = cipher.decrypt(tmp);
		assertEquals(raw, res);

		// 增加hash验证的加密模式
		tmp = cipher.encryptHash(raw);
		res = cipher.decryptHash(tmp);
		assertEquals(raw, res);

		String publicKey = cipher.getEncodedPublicKey();
		String privateKey = cipher.getEncodedPrivateKey();

		RSACipher cipherFromStore = new RSACipher(publicKey, privateKey);

		// 从存储中读取密钥生成的Cipher
		tmp = cipher.encrypt(raw);
		res = cipherFromStore.decrypt(tmp);
		assertEquals(raw, res);

		// PhaseHash，签名过程使用克隆体，即不会reset STATE

		RSACipher cipherAlice = new RSACipher("0123456789", 512);
		RSACipher cipherBob = new RSACipher("0123456789", 512);
		RSACipher cipherMallory = new RSACipher("0123456789", 512);

		String aliceMessage1 = "I Love You";
		String aliceMessage2 = "May you marry me?";
		String malloryMessage = "I Hate You";

		// 第一次发送消息， Digest使用 aliceMessage1 进行更新
		String encryptByAlice1 = cipherAlice.encraptPhaseHash(aliceMessage1);
		String decryptByBob1 = cipherBob.decryptPhaseHash(encryptByAlice1);
		assertEquals(aliceMessage1, decryptByBob1);

		// 第二次发送消息， Digest使用 aliceMessage2 进行更新
		String encryptByAlice2 = cipherAlice.encraptPhaseHash(aliceMessage2);
		String decryptByBob2 = cipherBob.decryptPhaseHash(encryptByAlice2);
		assertEquals(aliceMessage2, decryptByBob2);

		// Mallory的Digest是初始状态，没有经过aliceMessage1 aliceMessage2更新，所以Bob进行完整性检验时将会失败
		String encryptByMallory = cipherMallory.encraptPhaseHash(malloryMessage);

		try {

			// 这里会抛出hash不一致的异常
			String decryptByBob3 = cipherBob.decryptPhaseHash(encryptByMallory);
			assertEquals(cipherMallory, decryptByBob3);

		} catch (Exception exception) {
			System.err.println(exception.getMessage());
		}

	}

	@Test
	void doDiffieHellmanTest() throws IOException, GeneralSecurityException {

		// 预约的内容作为了初始向量
		String key = "THIS IS THE MESSAGE APPOINTMENT BY ALICE AND BOB";

		// A先发起请求
		// 生成公钥对
		// 获取A的公钥
		DiffieHellmanKeyGenerator A = new DiffieHellmanKeyGenerator();
		String publicKeyA = A.init();

		// B接受请求
		// 将A的公钥发送给B
		// B根据A的公钥生成自己的密钥对
		// B根据A的公钥和B的私钥生成最终密钥
		DiffieHellmanKeyGenerator B = new DiffieHellmanKeyGenerator();
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

	}

	@Test
	void doTimeBaseVerificationTest() throws Exception {

		String key = "Hello, World!";

		// 双方实例化对象
		TimeBaseVerification dongle01 = new TimeBaseVerification(key);
		TimeBaseVerification dongle02 = new TimeBaseVerification(key);

		// A生成挑战 发送给B
		String challenge = dongle01.generateChallenge();

		// B根据挑战生成应答 发送给A
		String response = dongle02.generateResponse(challenge);

		// A对应答进行检验
		boolean result = dongle01.verifyResponse(response);

		assertTrue(result);

	}
}
