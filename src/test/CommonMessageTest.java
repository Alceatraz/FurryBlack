package test;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import studio.blacktech.coolqbot.furryblack.common.message.Message;

public class CommonMessageTest {

	@Test
	void doCommonMessageTest() {
		Message message = new Message("[CQ:at=1234567890] ", 0, 0);
		message.parse();
		assertTrue(message.isPureCQC());
	}

	@Test
	void testURL() {
		String url = "https://gchat.qpic.cn/gchatpic_new/1203813230/805795515-3069017080-6849465D59530EA868AC9E5F5ED893DC/0?vuin=3477852529&term=2";
		System.out.println(url.substring(0, url.indexOf("?")));
	}

	@Test
	void testShui() {

		String temp = "1";

		System.out.println(Arrays.toString(temp.split(",")));
	}
}
