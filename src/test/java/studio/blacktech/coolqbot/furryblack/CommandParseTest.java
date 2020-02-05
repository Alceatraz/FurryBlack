package studio.blacktech.coolqbot.furryblack;


import org.junit.jupiter.api.Test;

import studio.blacktech.coolqbot.furryblack.common.message.Message;


public class CommandParseTest {

	String[] sample = {

			"/admim",
			"/admin report",
			"/admin  report",
			"/admin  report  123    123      42423",
			"/admin  report  `123    123      42423`",
			"/zhan 啊 是吗",
			"/zhan `啊 是吗`",
			"/zhan `啊 \\`是吗`",
			"/admin exec --no-trim",
			"/admin exec --module=shui report",
			"/admin exec --module=shui dump --group=1234567890",
			"/admin exec --module=shui eval `123 123 123`",
			"/admin exec --module=shui eval --sql=`SELECT * FROM \\`chat_record\\``\r\n",
			"/admin exec --module=shui eval --mode=sql --command=`SELECT * FROM \\`chat_record\\` LIMIT 10`",
			"/admin report --module=shui:20 --regex=`\\\"张 三|李 四\\\"`",
			"/admin exec --module=shui eval --sql=`SELECT \r\n" +
					"	temp_table_4.\"count\",\r\n" +
					"	temp_table_4.code,\r\n" +
					"	chat_picture.file_url\r\n" +
					"FROM (\r\n" +
					"	SELECT \r\n" +
					"		counts as \"count\",result AS code\r\n" +
					"	FROM (\r\n" +
					"		SELECT \r\n" +
					"			*,count(*) AS counts\r\n" +
					"		FROM (\r\n" +
					"			SELECT \r\n" +
					"				match[1] AS result\r\n" +
					"			FROM (\r\n" +
					"				SELECT \r\n" +
					"					chat_history.message \r\n" +
					"				FROM \r\n" +
					"					chat_history\r\n" +
					"				WHERE\r\n" +
					"					chat_history.grop_id = 805795515 AND\r\n" +
					"					chat_history.message_time BETWEEN 1577808000000 AND 1580400000000\r\n" +
					"			) AS temp_table_1\r\n" +
					"			CROSS JOIN LATERAL \r\n" +
					"				regexp_matches(temp_table_1.message, '\\[CQ:image,file=\\w{32}\\.\\w{3,4}\\]','g') \r\n" +
					"				AS match\r\n" +
					"		) AS temp_table_2\r\n" +
					"		GROUP BY 1\r\n" +
					"		ORDER BY 2 DESC\r\n" +
					"	) AS temp_table_3\r\n" +
					"	WHERE\r\n" +
					"		temp_table_3.counts > 1\r\n" +
					") AS temp_table_4\r\n" +
					"LEFT JOIN \r\n" +
					"	chat_picture\r\n" +
					"ON\r\n" +
					"	temp_table_4.code = chat_picture.image_code`"
	};

	@Test
	void test() {
		for (String temp : sample) {
			System.out.println(new Message(temp, 0, 0).extractCommand());
		}
	}

}
