package test;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import studio.blacktech.coolqbot.furryblack.common.message.Message;

public class CommonMessageTest {

	@Test
	void MessagetoStringTest() {
		Message message = new Message("[CQ:at=1234567890] ", 0, 0);
		message.parse();
		assertTrue(message.isPureCQC());
	}
}
