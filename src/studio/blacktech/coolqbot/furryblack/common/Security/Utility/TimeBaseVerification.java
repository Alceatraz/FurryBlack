package studio.blacktech.coolqbot.furryblack.common.Security.Utility;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TimeBaseVerification {

	public TimeBaseVerification(String key) {

		try {

			MessageDigest digest = MessageDigest.getInstance("SHA-512");
			digest.update(key.getBytes(StandardCharsets.UTF_8));

		} catch (NoSuchAlgorithmException exception) {

		}

	}
}
