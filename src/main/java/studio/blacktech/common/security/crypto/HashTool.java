package studio.blacktech.common.security.crypto;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class HashTool {

	private static MessageDigest md5Digest;
	private static MessageDigest sha256Digest;
	private static MessageDigest sha384Digest;
	private static MessageDigest sha512Digest;

	static {
		try {
			md5Digest = MessageDigest.getInstance("MD5");
			sha256Digest = MessageDigest.getInstance("SHA-256");
			sha384Digest = MessageDigest.getInstance("SHA-384");
			sha512Digest = MessageDigest.getInstance("SHA-512");
		} catch (NoSuchAlgorithmException execption) {
			execption.printStackTrace();
		}
	}


	// ==========================================================================================


	/**
	 * md5
	 */
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


	// ==========================================================================================

	/**
	 * sha256
	 */
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

		return sha256(message.getBytes());
	}

	public static String SHA256(String message) {

		return SHA256(message.getBytes());

	}


	// ==========================================================================================


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

		return sha384(message.getBytes());
	}

	public static String SHA384(String message) {

		return SHA384(message.getBytes());

	}


	// ==========================================================================================

	/**
	 * sha512
	 */
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

		return sha512(message.getBytes());
	}

	public static String SHA512(String message) {

		return SHA512(message.getBytes());

	}

}
