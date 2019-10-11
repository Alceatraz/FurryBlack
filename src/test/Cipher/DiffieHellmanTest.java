package test.Cipher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;

import studio.blacktech.coolqbot.furryblack.common.Cipher.AESCipher;
import studio.blacktech.coolqbot.furryblack.common.Cipher.DiffieHellmanX;

public class DiffieHellmanTest {

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
