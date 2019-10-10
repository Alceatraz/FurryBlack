package test.Cipher;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import studio.blacktech.coolqbot.furryblack.common.Cipher.AESCipher;

public class AESCipherTest {

	@Test
	void doAESCipherTest() throws Exception {
		String raw = "Hello, World!";
		String tmp;
		String res;
		AESCipher cipher = new AESCipher("4gv24g24g24g24", "1");
		tmp = cipher.encrypt(raw);
		res = cipher.decrypt(tmp);
		assertEquals("和原文一致", raw, res);
	}
}
