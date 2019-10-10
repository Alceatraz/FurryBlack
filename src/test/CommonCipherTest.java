package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import studio.blacktech.coolqbot.furryblack.common.Cipher.AESCipher;
import studio.blacktech.coolqbot.furryblack.common.Cipher.TimeBaseVerification;

public class CommonCipherTest {

	@Test
	void AESCipherTest() throws Exception {
		String raw = "Hello, World!";
		String tmp;
		String res;
		AESCipher cipher = new AESCipher("3967398574956723487596234875623784567823465", "1");
		tmp = cipher.encryptAES(raw);
		res = cipher.decryptAES(tmp);
		assertEquals("和原文一致", raw, res);
	}

	@Test
	void TimebaseVerificationTest() throws Exception {
		String key = "Hello, World!";
		TimeBaseVerification dongle01 = new TimeBaseVerification(key);
		TimeBaseVerification dongle02 = new TimeBaseVerification(key);
		String challenge = dongle01.generateChallenge();
		String response = dongle02.generateResponse(challenge);
		boolean result = dongle01.verifyResponse(response);
		assertTrue(result);
	}

}
