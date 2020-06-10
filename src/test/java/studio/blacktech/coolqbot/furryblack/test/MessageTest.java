package studio.blacktech.coolqbot.furryblack.test;


import org.junit.jupiter.api.Test;
import studio.blacktech.coolqbot.furryblack.common.message.Message;


public class MessageTest {


	@Test
	void test() {

		String[] sample = {
				"/admin",
				"/admin exec",
				"/admin exec --module",
				"/admin exec --module=shui",
				"/admin exec --module=shui create",
				"/admin exec --module=shui create `SELECT * FROM chat_record LIMIT 10`"
		};

		for (String temp : sample) System.out.println(new Message(temp));

	}


}
