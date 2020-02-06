package studio.blacktech.coolqbot.furryblack;


import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import studio.blacktech.coolqbot.furryblack.common.message.Message;


public class PlayGround {


	Pattern pattern = Pattern.compile("^/[a-z]+");

	String[] sample = {
			"/admin",
			"/admin exec",
			"/admin exec --module=shui",
			"/admin exec --module=shui create `SELECT * FROM chat_record LIMIT 10`"
	};

	@Test
	void test() {
		for (String temp : sample) System.out.println(new Message(temp));
	}

}
