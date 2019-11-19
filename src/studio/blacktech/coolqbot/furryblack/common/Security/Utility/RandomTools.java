package studio.blacktech.coolqbot.furryblack.common.Security.Utility;

import java.security.SecureRandom;

public class RandomTools {

	private static final String RANDOMRANGE = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	public static final String genRandomPort() {
		SecureRandom random = new SecureRandom();
		return String.valueOf(random.nextInt(64512) + 1024);
	}

	public static final String genRandomString() {
		return RandomTools.genRandomString(new SecureRandom());
	}

	public static final String genRandomString(int size) {
		return RandomTools.genRandomString(new SecureRandom(), size);
	}

	public static final String genRandomString(SecureRandom random) {
		return RandomTools.genRandomString(new SecureRandom(), 16);
	}

	public static final String genRandomString(SecureRandom random, int size) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < size; i++) {
			builder.append(RandomTools.RANDOMRANGE.charAt(random.nextInt(62)));
		}
		return builder.toString();
	}
}
