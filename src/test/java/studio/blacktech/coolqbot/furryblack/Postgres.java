package studio.blacktech.coolqbot.furryblack;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import org.junit.jupiter.api.Test;

import studio.blacktech.coolqbot.furryblack.common.exception.InitializationException;


public class Postgres {

	Connection connection;

	String JDBC_HOSTNAME = "jdbc:postgresql://192.168.1.10:5432/furryblack";
	String JDBC_USERNAME = "furryblack";
	String JDBC_PASSWORD = "furryblack";

	@Test
	void test() throws Exception {

		Class.forName("org.postgresql.Driver");
		connection = DriverManager.getConnection(JDBC_HOSTNAME, JDBC_USERNAME, JDBC_PASSWORD);

		if (connection == null) throw new InitializationException("数据库连接失败");

		System.out.println("数据库已连接");

		// ===============================

		Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

		statement.execute("SELECT * FROM chat_record LIMIT 10");

		ResultSet resultSet = statement.getResultSet();

		ResultSetMetaData resultSetMeta = resultSet.getMetaData();

		int columnSize = resultSetMeta.getColumnCount();

		System.out.println("结果宽度 " + columnSize);
		System.out.println("列名显示宽度 " + resultSetMeta.getColumnName(1));

		while (resultSet.next()) {
			for (int i = 1; i <= columnSize; i++) {
				String col0 = resultSet.getString(i);
				System.out.println(col0);
			}
		}
	}
}
