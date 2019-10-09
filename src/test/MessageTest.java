package test;

import org.junit.jupiter.api.Test;

import studio.blacktech.coolqbot.furryblack.common.message.Message;

public class MessageTest {

	@Test
	void test01() {
		Message message01 = new Message("[CQ:at=1234567890] ", 0, 0);
		System.out.println(message01.parse());
	}
}
