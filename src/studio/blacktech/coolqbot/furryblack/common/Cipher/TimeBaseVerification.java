package studio.blacktech.coolqbot.furryblack.common.Cipher;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import studio.blacktech.coolqbot.furryblack.common.Utility.RandomTools;

public class TimeBaseVerification {

	private static final Charset UTF_8 = Charset.forName("UTF-8");

	private AESCipher cipher;
	private String response;

	public TimeBaseVerification(String key) {
		long current = System.currentTimeMillis();
		Date date = new Date(current);
		int ss = Integer.parseInt(new SimpleDateFormat("ss").format(date));
		if (55 < ss) { date = new Date(current + 60000); }
		long timebase = (date.getTime() / 1000 - ss) * 1000;
		this.cipher = new AESCipher(key, Long.toString(timebase));
	}

	public String generateChallenge() {
		String raw = RandomTools.genRandomString(64);
		String tmp = this.makeChallenge(raw);
		this.response = this.makeResponse(tmp);
		String res = this.cipher.unsafeEncrypt(tmp);
		return res;
	}

	public String generateResponse(String challenge) {
		String raw = this.cipher.unsafeDecrypt(challenge);
		String tmp = this.makeResponse(raw);
		String res = this.cipher.unsafeEncrypt(tmp);
		return res;
	}

	public boolean verifyResponse(String raw) {
		return this.cipher.unsafeDecrypt(raw).equals(this.response);
	}

	private String makeChallenge(String code) {
		byte[] sha;
		StringBuilder builder = new StringBuilder();
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-512");
			digest.update(code.getBytes(UTF_8));
			sha = digest.digest();
		} catch (NoSuchAlgorithmException exception) {
			exception.printStackTrace();
			return null;
		}
		for (int i = 0; i < 32; i++) {
			builder.append(Integer.toHexString(sha[i] & 0x000000FF | 0xFFFFFF00).substring(6));
		}
		return builder.toString();
	}

	private String makeResponse(String code) {
		byte[] sha;
		StringBuilder builder = new StringBuilder();
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-512");
			digest.update(code.getBytes(UTF_8));
			sha = digest.digest();
		} catch (NoSuchAlgorithmException exception) {
			exception.printStackTrace();
			return null;
		}
		int hbit;
		int lbit;
		String hex;
		for (int i = 0; i < 32; i++) {
			hbit = sha[2 * i] + 0x80;
			lbit = sha[2 * i + 1] + 0x80;
			hex = Integer.toString(hbit) + Integer.toString(lbit);
			builder.append(Character.toChars(Integer.valueOf(hex) & 0xFFFF));
		}
		return builder.toString();
	}
}
