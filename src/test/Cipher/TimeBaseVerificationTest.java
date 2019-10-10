package test.Cipher;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import studio.blacktech.coolqbot.furryblack.common.Cipher.TimeBaseVerification;

public class TimeBaseVerificationTest {

	@Test
	void doTimeBaseVerificationTest() throws Exception {
		String key = "Hello, World!";
		TimeBaseVerification dongle01 = new TimeBaseVerification(key);
		TimeBaseVerification dongle02 = new TimeBaseVerification(key);
		String challenge = dongle01.generateChallenge();
		String response = dongle02.generateResponse(challenge);
		boolean result = dongle01.verifyResponse(response);
		assertTrue(result);
	}

}
