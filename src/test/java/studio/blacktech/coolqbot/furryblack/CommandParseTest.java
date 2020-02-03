package studio.blacktech.coolqbot.furryblack;


import org.junit.jupiter.api.Test;

import studio.blacktech.coolqbot.furryblack.common.message.Message;


public class CommandParseTest {

	String[] sample = {

			"/admin report --module=shui:20 --regex=`\\\"张 三|李 四\\\"`",
			"/admin exec --module=shui report",
			"/admin exec --module=shui dump --group=1234567890",
			"/admin exec --module=shui eval `123 123 123`",
			"/admin exec --module=shui eval --mode=sql --command=`SELECT * FROM \\`chat_record\\` LIMIT 10`",
			"/admim",
			"/admin report",
			"/zhan 啊 是吗",
			"/admin exec --module=shui eval \r\n"
	};


	@Test
	void test() {
		for (String temp : sample) System.out.println(new Message(temp, 0, 0).extractCommand());
	}


}
