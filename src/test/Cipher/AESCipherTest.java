package test.Cipher;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.junit.jupiter.api.Test;

import studio.blacktech.coolqbot.furryblack.common.Cipher.AESCipher;
import studio.blacktech.coolqbot.furryblack.common.Cipher.AESCipher.MessageHashCheckFailedException;
import studio.blacktech.coolqbot.furryblack.common.Cipher.AESCipher.MessageSizeCheckFailedException;

public class AESCipherTest {

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
		tmp = cipher.safeEncrypt(raw);
		res = cipher.safeDecrypt(tmp);
		assertEquals(raw, res);

	}

	@Test
	void doAESP2PHashTest() {

		try {
			// 点对点Hash，签名过程使用克隆体，即不会reset STATE

			AESCipher cipherAlice = new AESCipher("0123456789", "0123456789");
			AESCipher cipherBob = new AESCipher("0123456789", "0123456789");
			AESCipher cipherMallory = new AESCipher("0123456789", "0123456789");

			String aliceMessage1 = "I Love You";
			String aliceMessage2 = "May you marry me?";
			String malloryMessage = "I Hate You";

			// 第一次发送消息， Digest使用 aliceMessage1 进行更新
			String encryptByAlice1 = cipherAlice.safeStackEncrypt(aliceMessage1);
			String decryptByBob1 = cipherBob.safeStackDecrypt(encryptByAlice1);

			System.out.println("Alice send message: " + aliceMessage1);
			System.out.println("Bob accept message: " + decryptByBob1);

			// 第二次发送消息， Digest使用 aliceMessage2 进行更新
			String encryptByAlice2 = cipherAlice.safeStackEncrypt(aliceMessage2);
			String decryptByBob2 = cipherBob.safeStackDecrypt(encryptByAlice2);

			System.out.println("Alice send message: " + aliceMessage2);
			System.out.println("Bob accept message: " + decryptByBob2);

			// Mallory的Digest是初始状态，没有经过aliceMessage1 aliceMessage2更新，所以Bob进行完整性检验时将会失败
			String encryptByMallory = cipherMallory.safeStackEncrypt(malloryMessage);
			String decryptByBob3 = cipherBob.safeStackDecrypt(encryptByMallory);
			// ↑ 这里会抛出异常

			decryptByBob3.length();

		} catch (MessageSizeCheckFailedException | MessageHashCheckFailedException | IOException exception) {
			exception.printStackTrace();
		}
	}
}
