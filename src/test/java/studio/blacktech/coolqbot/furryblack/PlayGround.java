package studio.blacktech.coolqbot.furryblack;


import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.jupiter.api.Test;


public class PlayGround {

	private String JDBC_HOSTNAME = "jdbc:postgresql://192.168.1.10:5432/furryblack";
	private String JDBC_USERNAME = "furryblack";
	private String JDBC_PASSWORD = "furryblack";

	@Test
	void test() throws Exception {

		Class.forName("org.postgresql.Driver");

		Connection connection = DriverManager.getConnection(JDBC_HOSTNAME, JDBC_USERNAME, JDBC_PASSWORD);

		connection.getClientInfo().forEach((key, value) -> System.out.println(key + " " + value));


	}

}
