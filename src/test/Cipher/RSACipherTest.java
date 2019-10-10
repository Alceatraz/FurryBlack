package test.Cipher;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import studio.blacktech.coolqbot.furryblack.common.Cipher.RSACipher;

class RSACipherTest {

	@Test
	void doRSACipherTest() throws Exception {
		String raw = "Hello, World!";
		String tmp;
		String res;
		RSACipher cipher = new RSACipher("54g254g254g4yh54yh54ytrt4", 2048);
		tmp = cipher.encrypt(raw);
		res = cipher.decrypt(tmp);
		assertEquals("和原文一致", raw, res);
	}

}
