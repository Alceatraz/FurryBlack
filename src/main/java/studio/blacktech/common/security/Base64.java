package studio.blacktech.common.security;


import java.util.Base64.Decoder;
import java.util.Base64.Encoder;


public class Base64 {


	private static final Encoder encoder = java.util.Base64.getEncoder();
	private static final Decoder decoder = java.util.Base64.getDecoder();

	public static String encode(String raw) {
		return new String(encoder.encode(raw.getBytes()));
	}

	public static String decode(String raw) {
		return new String(decoder.decode(raw.getBytes()));
	}
}
