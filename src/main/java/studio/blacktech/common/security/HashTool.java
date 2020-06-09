package studio.blacktech.common.security;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * HASH工具类
 * 密码学中md5和MD5是不一样的 注意严谨性 即使这么命名会破坏JAVA命名规范
 *
 * @author BTS - Alceatraz Warprays alceatraz@blacktech.studio
 */


public class HashTool {


	private static MessageDigest md5Digest;
	private static MessageDigest sha256Digest;
	private static MessageDigest sha384Digest;
	private static MessageDigest sha512Digest;

	private HashTool() {
		throw new IllegalStateException("Static utility class");
	}


	// ==========================================================================================================================================================


	static {
		try {
			md5Digest = MessageDigest.getInstance("MD5");
			sha256Digest = MessageDigest.getInstance("SHA-256");
			sha384Digest = MessageDigest.getInstance("SHA-384");
			sha512Digest = MessageDigest.getInstance("SHA-512");
		} catch (NoSuchAlgorithmException ignore) {
			// Ignore
		}
	}


	// ==========================================================================================================================================================


	public static String md5(byte[] message) {
		byte[] digested = md5Digest.digest(message);
		StringBuilder builder = new StringBuilder();
		for (byte element : digested) {
			builder.append(Integer.toHexString(0x000000FF & element | 0xFFFFFF00).substring(6));
		}
		return builder.toString();
	}


	public static String MD5(byte[] message) {
		return md5(message).toUpperCase();
	}


	// ==========================================================================================================================================================


	public static String sha256(byte[] message) {
		byte[] digested = sha256Digest.digest(message);
		String temp;
		StringBuilder builder = new StringBuilder();
		for (byte element : digested) {
			temp = Integer.toHexString(element & 0xFF);
			builder.append(temp.length() == 2 ? temp : "0" + temp);
		}
		return builder.toString();
	}


	public static String SHA256(byte[] message) {
		return sha256(message).toUpperCase();
	}


	public static String sha256(String message) {
		return sha256(message.getBytes(StandardCharsets.UTF_8));
	}


	public static String SHA256(String message) {
		return SHA256(message.getBytes(StandardCharsets.UTF_8));
	}


	// ==========================================================================================================================================================


	public static String sha384(byte[] message) {
		byte[] digested = sha384Digest.digest(message);
		String temp;
		StringBuilder builder = new StringBuilder();
		for (byte element : digested) {
			temp = Integer.toHexString(element & 0xFF);
			builder.append(temp.length() == 2 ? temp : "0" + temp);
		}
		return builder.toString();
	}


	public static String SHA384(byte[] message) {
		return sha384(message).toUpperCase();
	}


	public static String sha384(String message) {
		return sha384(message.getBytes(StandardCharsets.UTF_8));
	}


	public static String SHA384(String message) {
		return SHA384(message.getBytes(StandardCharsets.UTF_8));
	}


	// ==========================================================================================================================================================


	public static String sha512(byte[] message) {
		byte[] digested = sha512Digest.digest(message);
		String temp;
		StringBuilder builder = new StringBuilder();
		for (byte element : digested) {
			temp = Integer.toHexString(element & 0xFF);
			builder.append(temp.length() == 2 ? temp : "0" + temp);
		}
		return builder.toString();
	}


	public static String SHA512(byte[] message) {
		return sha512(message).toUpperCase();
	}


	public static String sha512(String message) {
		return sha512(message.getBytes(StandardCharsets.UTF_8));
	}


	public static String SHA512(String message) {
		return SHA512(message.getBytes(StandardCharsets.UTF_8));
	}


}
