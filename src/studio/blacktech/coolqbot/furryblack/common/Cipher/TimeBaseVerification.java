package studio.blacktech.coolqbot.furryblack.common.Cipher;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import studio.blacktech.coolqbot.furryblack.common.Utility.RandomTools;

public class TimeBaseVerification {

	private static final Charset UTF_8 = Charset.forName("UTF-8");

	private AESCipher cipher;
	private String challenge;
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
		this.challenge = this.makeChallenge(raw);
		this.response = this.makeResponse(this.challenge);
		String res = this.cipher.encrypt(this.challenge);
		return res;
	}

	public String generateResponse(String challenge) {
		try {
			this.challenge = this.cipher.decrypt(challenge);
			this.response = this.makeResponse(this.challenge);
			return this.cipher.encrypt(this.response);
		} catch (IOException exception) {
			return null;
		}
	}

	public boolean verifyResponse(String raw) {
		try {
			return this.cipher.decrypt(raw).equals(this.response);
		} catch (IOException exception) {
			return false;
		}
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

	public String getChallenge() {
		return this.challenge;
	}

	public String getResponse() {
		return this.response;
	}
}
