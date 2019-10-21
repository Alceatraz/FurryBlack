package test.Cipher;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.junit.jupiter.api.Test;

import studio.blacktech.coolqbot.furryblack.common.Cipher.RSACipher;

class RSACipherTest {

	@Test
	void useCase01() throws GeneralSecurityException, IOException {

		String raw = "Hello, World!";

		RSACipher cipher = new RSACipher("0123456789", 512);

		String publicKey = cipher.getPublicKeyBase64();
		String privateKey = cipher.getPrivateKeyBase64();

		System.out.println(publicKey);
		System.out.println(privateKey);

		String tmp = cipher.encrypt(raw);
		String res = cipher.decrypt(tmp);

		assertEquals(raw, res);
	}

	@Test
	void useCase02() throws Exception {

		String raw = "Hello, World!";

		RSACipher cipher = new RSACipher("0123456789", 512);

		String publicKey = cipher.getPublicKeyBase64();
		String privateKey = cipher.getPrivateKeyBase64();

		System.out.println(publicKey);
		System.out.println(privateKey);

		RSACipher cipherFromFile = new RSACipher(publicKey, privateKey);

		String tmp = cipherFromFile.encrypt(raw);
		String res = cipherFromFile.decrypt(tmp);

		assertEquals(raw, res);

	}

}
