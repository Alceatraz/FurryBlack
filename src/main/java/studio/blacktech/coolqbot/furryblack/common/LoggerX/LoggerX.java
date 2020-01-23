package studio.blacktech.coolqbot.furryblack.common.LoggerX;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;

import studio.blacktech.coolqbot.furryblack.common.exception.CantReinitializationException;
import studio.blacktech.coolqbot.furryblack.common.exception.InitializationException;


/**
 * 唯一根模式
 *
 * @author AW
 */
public class LoggerX {

	private final static SimpleDateFormat formater_time = new SimpleDateFormat("HH:mm:ss");
	private final static SimpleDateFormat formater_date = new SimpleDateFormat("yyyy-MM-dd");
	private final static SimpleDateFormat formater_full = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static boolean INIT_LOCK = false;
	private static File FILE_LOGGER;
	private String name;

	public LoggerX(String name) {

		this.name = name;

	}

	public LoggerX(Object thisInstance) {

		name = thisInstance.getClass().getSimpleName();

	}

	/**
	 * 必须传入绝对路径
	 */
	public static void init(File file) throws InitializationException {

		if (INIT_LOCK) { throw new CantReinitializationException(); }
		INIT_LOCK = true;
		FILE_LOGGER = file;

	}
	// ==================================================================================================
	//
	//
	// ==================================================================================================

	public void exception(Exception exception) {

		StringBuilder builder = new StringBuilder();
		builder.append("异常原因：" + exception.getCause());
		builder.append("异常消息：" + exception.getMessage());
		builder.append("异常调用：" + exception.getClass().getName());
		for (StackTraceElement temp : exception.getStackTrace()) {
			builder.append("    at " + temp.getClassName() + "(" + temp.getMethodName() + ":" + temp.getLineNumber()
					+ ")\r\n");
		}
		builder.setLength(builder.length() - 1);
		String temp = "[" + LoggerX.datetime() + "][EXCEPTION][" + name + "] 发生异常\r\n" + builder.toString();
		LoggerX.PRINT(temp);
		LoggerX.WRITE(temp);

	}

	public void exception(long timestamp, Exception exception) {

		StringBuilder builder = new StringBuilder();
		builder.append("异常原因：" + exception.getCause());
		builder.append("异常消息：" + exception.getMessage());
		builder.append("异常调用：" + exception.getClass().getName());
		for (StackTraceElement temp : exception.getStackTrace()) {
			builder.append("    at " + temp.getClassName() + "(" + temp.getMethodName() + ":" + temp.getLineNumber()
					+ ")\r\n");
		}
		builder.setLength(builder.length() - 1);
		String temp = "[" + LoggerX.datetime() + "][EXCEPTION][" + name + "] 发生异常\r\n时间序列号: " + timestamp
				+ builder.toString();
		LoggerX.PRINT(temp);
		LoggerX.WRITE(temp);

	}

	public void exception(long timestamp, String catgory, Exception exception) {

		StringBuilder builder = new StringBuilder();
		builder.append("异常原因：" + exception.getCause());
		builder.append("异常消息：" + exception.getMessage());
		builder.append("异常调用：" + exception.getClass().getName());
		for (StackTraceElement temp : exception.getStackTrace()) {
			builder.append("    at " + temp.getClassName() + "(" + temp.getMethodName() + ":" + temp.getLineNumber()
					+ ")\r\n");
		}
		builder.setLength(builder.length() - 1);
		String temp = "[" + LoggerX.datetime() + "][EXCEPTION][" + name + "] " + catgory + "\r\n时间序列号: " + timestamp
				+ builder.toString();
		LoggerX.PRINT(temp);
		LoggerX.WRITE(temp);

	}
	// ==================================================================================================
	//
	//
	// ==================================================================================================

	public String raw(String catgory, String message) {

		String temp = "[" + LoggerX.datetime() + "][RAW-MESSAGE][" + name + "] " + catgory + "\r\n" + message
				+ "\r\n[RAW-EOF]\r\n";
		LoggerX.PRINT(temp);
		LoggerX.WRITE(temp);
		return message;

	}
	// ==================================================================================================
	//
	//
	// ==================================================================================================

	public String warn(String message) {

		String temp = "[" + LoggerX.datetime() + "][WARN][" + name + "]" + message;
		LoggerX.PRINT(temp);
		LoggerX.WRITE(temp);
		return message;

	}

	public String info(String message) {

		String temp = "[" + LoggerX.datetime() + "][INFO][" + name + "]" + message;
		LoggerX.PRINT(temp);
		LoggerX.WRITE(temp);
		return message;

	}

	public String seek(String message) {

		String temp = "[" + LoggerX.datetime() + "][SEEK][" + name + "]" + message;
		LoggerX.PRINT(temp);
		LoggerX.WRITE(temp);
		return message;

	}

	public String full(String message) {

		String temp = "[" + LoggerX.datetime() + "][FULL][" + name + "]" + message;
		LoggerX.PRINT(temp);
		LoggerX.WRITE(temp);
		return message;

	}
	// ==================================================================================================
	//
	//
	// ==================================================================================================

	public String warn(String catgory, String message) {

		String temp = "[" + LoggerX.datetime() + "][WARN][" + name + "]" + catgory + ": " + message;
		LoggerX.PRINT(temp);
		LoggerX.WRITE(temp);
		return message;

	}

	public String info(String catgory, String message) {

		String temp = "[" + LoggerX.datetime() + "][INFO][" + name + "]" + catgory + ": " + message;
		LoggerX.PRINT(temp);
		LoggerX.WRITE(temp);
		return message;

	}

	public String seek(String catgory, String message) {

		String temp = "[" + LoggerX.datetime() + "][SEEK][" + name + "]" + catgory + ": " + message;
		LoggerX.PRINT(temp);
		LoggerX.WRITE(temp);
		return message;

	}

	public String full(String catgory, String message) {

		String temp = "[" + LoggerX.datetime() + "][FULL][" + name + "]" + catgory + ": " + message;
		LoggerX.PRINT(temp);
		LoggerX.WRITE(temp);
		return message;

	}
	// ==================================================================================================
	//
	//
	// ==================================================================================================

	public long warn(String catgory, long message) {

		String temp = "[" + LoggerX.datetime() + "][WARN][" + name + "]" + catgory + ": " + message;
		LoggerX.PRINT(temp);
		LoggerX.WRITE(temp);
		return message;

	}

	public long info(String catgory, long message) {

		String temp = "[" + LoggerX.datetime() + "][INFO][" + name + "]" + catgory + ": " + message;
		LoggerX.PRINT(temp);
		LoggerX.WRITE(temp);
		return message;

	}

	public long seek(String catgory, long message) {

		String temp = "[" + LoggerX.datetime() + "][SEEK][" + name + "]" + catgory + ": " + message;
		LoggerX.PRINT(temp);
		LoggerX.WRITE(temp);
		return message;

	}

	public long full(String catgory, long message) {

		String temp = "[" + LoggerX.datetime() + "][FULL][" + name + "]" + catgory + ": " + message;
		LoggerX.PRINT(temp);
		LoggerX.WRITE(temp);
		return message;

	}
	// ==================================================================================================
	//
	//
	// ==================================================================================================

	public String[] warn(String catgory, String... message) {

		for (String line : message) {
			String temp = "[" + LoggerX.datetime() + "][WARN][" + name + "]" + catgory + ": " + line;
			LoggerX.PRINT(temp);
			LoggerX.WRITE(temp);
		}
		return message;

	}

	public String[] info(String catgory, String... message) {

		for (String line : message) {
			String temp = "[" + LoggerX.datetime() + "][INFO][" + name + "]" + catgory + ": " + line;
			LoggerX.PRINT(temp);
			LoggerX.WRITE(temp);
		}
		return message;

	}

	public String[] seek(String catgory, String... message) {

		for (String line : message) {
			String temp = "[" + LoggerX.datetime() + "][SEEK][" + name + "]" + catgory + ": " + line;
			LoggerX.PRINT(temp);
			LoggerX.WRITE(temp);
		}
		return message;

	}

	public String[] full(String catgory, String... message) {

		for (String line : message) {
			String temp = "[" + LoggerX.datetime() + "][FULL][" + name + "]" + catgory + ": " + line;
			LoggerX.PRINT(temp);
			LoggerX.WRITE(temp);
		}
		return message;

	}
	// ==================================================================================================
	//
	//
	// ==================================================================================================

	private static void PRINT(String message) {

		System.out.println(message);

	}

	private static void WRITE(String message) {

		try {
			FileWriter writer = new FileWriter(FILE_LOGGER, true);
			writer.append(message);
			writer.append("\r\n");
			writer.flush();
			writer.close();
		} catch (IOException exception) {
			System.err.println(exception.getMessage());
		}

	}
	// ==================================================================================================
	//
	//
	// ==================================================================================================

	public static String unicode(String raw) {

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < raw.length(); i++) {
			builder.append("\\u");
			builder.append(String.format("%1$4s", Integer.toHexString(raw.charAt(i) & 0xffff)).replace(" ", "0"));
		}
		return builder.toString();

	}

	public static String[] unicodeid(String raw) {

		LinkedList<String> tmp = new LinkedList<>();
		for (int i = 0; i < raw.length(); i++) {
			tmp.add(Integer.toHexString(raw.charAt(i) & 0xffff));
		}
		String[] res = new String[tmp.size()];
		tmp.toArray(res);
		return res;

	}
	// ==================================================================================================
	//
	//
	// ==================================================================================================

	public static String date() {

		return formater_date.format(new Date());

	}

	public static String date(Date date) {

		return formater_date.format(date);

	}

	public static String date(long timestamp) {

		return formater_date.format(new Date(timestamp));

	}

	public static String time() {

		return formater_time.format(new Date());

	}

	public static String time(Date date) {

		return formater_time.format(date);

	}

	public static String time(long timestamp) {

		return formater_time.format(new Date(timestamp));

	}

	public static String datetime() {

		return formater_full.format(new Date());

	}

	public static String datetime(Date date) {

		return formater_full.format(date);

	}

	public static String datetime(long timestamp) {

		return formater_full.format(new Date(timestamp));

	}
	// ================================================================

	public static String formatTime(String format) {

		return new SimpleDateFormat(format).format(new Date());

	}

	public static String formatTime(String format, Date date) {

		return new SimpleDateFormat(format).format(date);

	}

	public static String formatTime(String format, long timestamp) {

		return new SimpleDateFormat(format).format(new Date(timestamp));

	}

	public static String formatTime(String format, TimeZone timezone) {

		SimpleDateFormat formater = new SimpleDateFormat(format);
		formater.setTimeZone(timezone);
		return formater.format(new Date());

	}

	public static String formatTime(String format, TimeZone timezone, Date date) {

		SimpleDateFormat formater = new SimpleDateFormat(format);
		formater.setTimeZone(timezone);
		return formater.format(date);

	}

	public static String formatTime(String format, TimeZone timezone, long timestamp) {

		SimpleDateFormat formater = new SimpleDateFormat(format);
		formater.setTimeZone(timezone);
		return formater.format(new Date(timestamp));

	}
	// ================================================================

}
