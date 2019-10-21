package test.Cipher;

import static org.junit.Assert.fail;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class CommonTest {

	private static final Charset UTF_8 = Charset.forName("UTF-8");

	// 00 00 00 00 , 00 00 00 00 - 00 00 00 00 , 00 00 00 00
	// message length 8bit
	// message digest 8bit

	@Test
	void dataDigest() throws Exception {
		String content = "这是比较长的消息 this is message !! 1234567890 ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		// ===================================================================================================
		// 生成

		MessageDigest digesterA = MessageDigest.getInstance("SHA-384");

		byte[] tmp1A = content.getBytes(UTF_8);

		int tmp1LengthA = tmp1A.length;

		byte[] resA = new byte[16 + tmp1LengthA];

		byte[] tmp2A = Integer.toHexString(tmp1LengthA).getBytes(UTF_8);

		digesterA.update(tmp1A);
		byte[] tmp3A = digesterA.digest();

		System.arraycopy(tmp2A, 0, resA, 8 - tmp2A.length, tmp2A.length);
		System.arraycopy(tmp3A, 0, resA, 8, 8);
		System.arraycopy(tmp1A, 0, resA, 16, tmp1LengthA);

		System.out.println(tmp1LengthA);
		System.out.println(Arrays.toString(tmp2A));
		System.out.println(Arrays.toString(resA));

		// ===================================================================================================
		// 验证

		MessageDigest digesterB = MessageDigest.getInstance("SHA-384");

		int rawLengthB = resA.length;

		byte[] tmp1B = new byte[8];
		byte[] tmp2B = new byte[8];

		System.arraycopy(resA, 0, tmp1B, 0, 8);
		System.arraycopy(resA, 8, tmp2B, 0, 8);

		String lengthB = new String(tmp1B).trim();
		int mesglengthB = Integer.valueOf(lengthB, 16);

		if (mesglengthB + 16 != rawLengthB) { fail(); }

		byte[] tmp3B = new byte[mesglengthB];
		System.arraycopy(resA, 16, tmp3B, 0, mesglengthB);

		digesterB.update(tmp3B);
		byte[] digestB = digesterB.digest();

		if (tmp2B[0] != digestB[0] || tmp2B[1] != digestB[1] || tmp2B[2] != digestB[2] || tmp2B[3] != digestB[3] || tmp2B[4] != digestB[4] || tmp2B[5] != digestB[5] || tmp2B[6] != digestB[6] || tmp2B[7] != digestB[7]

		) { fail(); }

		for (int i = 0; i < tmp1LengthA; i++) {
			if (tmp1A[i] != tmp3B[i]) { fail(); }
		}
	}

}
