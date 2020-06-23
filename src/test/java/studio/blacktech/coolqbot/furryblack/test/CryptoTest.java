package studio.blacktech.coolqbot.furryblack.test;


import org.junit.jupiter.api.Test;
import studio.blacktech.common.security.HashTool;
import studio.blacktech.common.security.RandomTool;


public class CryptoTest {


	@Test
	void testHash() {

		HashTool.md5("1234");
		RandomTool.nextInt(10);

	}


}
