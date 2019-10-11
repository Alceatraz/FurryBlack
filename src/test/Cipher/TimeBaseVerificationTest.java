package test.Cipher;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import studio.blacktech.coolqbot.furryblack.common.Cipher.TimeBaseVerification;
import studio.blacktech.coolqbot.furryblack.common.LoggerX.LoggerX;

public class TimeBaseVerificationTest {

	@Test
	void doTimeBaseVerificationTest() throws Exception {

		for (int i = 0; i < 100; i++) {

			String key = "Hello, World!";
			TimeBaseVerification dongle01 = new TimeBaseVerification(key);
			TimeBaseVerification dongle02 = new TimeBaseVerification(key);

			String challenge = dongle01.generateChallenge();
			String response = dongle02.generateResponse(challenge);
			boolean result = dongle01.verifyResponse(response);

			System.out.println(dongle01.getChallenge());
			System.out.println(dongle01.getResponse());
			System.out.println(dongle02.getChallenge());
			System.out.println(dongle02.getResponse());

			System.out.println(LoggerX.unicode(dongle01.getResponse()));
			System.out.println(LoggerX.unicode(dongle02.getResponse()));

			assertTrue(result);
		}
	}

}
