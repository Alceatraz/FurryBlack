package studio.blacktech.coolqbot.furryblack.common.loggerx;


import studio.blacktech.coolqbot.furryblack.common.exception.CantReinitializationException;
import studio.blacktech.coolqbot.furryblack.common.exception.InitializationException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;


public class LoggerX {


	private static boolean INIT_LOCK = false;
	private static File FILE_LOGGER;
	private final String name;


	public LoggerX(String name) {
		this.name = name;
	}


	public LoggerX(Object thisInstance) {
		name = thisInstance.getClass().getSimpleName();
	}


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
		builder.append("异常原因：" + exception.getCause() + "\r\n");
		builder.append("异常消息：" + exception.getMessage() + "\r\n");
		builder.append("异常调用：" + exception.getClass().getName() + "\r\n");
		for (StackTraceElement temp : exception.getStackTrace()) builder.append("    at " + temp.getClassName() + "(" + temp.getMethodName() + ":" + temp.getLineNumber() + ")\r\n");
		builder.setLength(builder.length() - 1);
		String temp = "[" + LoggerX.datetime() + "][EXCEPTION][" + name + "] 发生异常\r\n" + builder.toString();
		LoggerX.PRINT(temp);
		LoggerX.WRITE(temp);
	}


	public void exception(String category, Exception exception) {
		StringBuilder builder = new StringBuilder();
		builder.append("异常原因：" + exception.getCause() + "\r\n");
		builder.append("异常消息：" + exception.getMessage() + "\r\n");
		builder.append("异常调用：" + exception.getClass().getName() + "\r\n");
		for (StackTraceElement temp : exception.getStackTrace()) builder.append("    at " + temp.getClassName() + "(" + temp.getMethodName() + ":" + temp.getLineNumber() + ")\r\n");
		builder.setLength(builder.length() - 1);
		String temp = "[" + LoggerX.datetime() + "][EXCEPTION][" + name + "] " + category + "\r\n" + builder.toString();
		LoggerX.PRINT(temp);
		LoggerX.WRITE(temp);
	}


	public void exception(long timestamp, Exception exception) {
		StringBuilder builder = new StringBuilder();
		builder.append("异常原因：" + exception.getCause() + "\r\n");
		builder.append("异常消息：" + exception.getMessage() + "\r\n");
		builder.append("异常调用：" + exception.getClass().getName() + "\r\n");
		for (StackTraceElement temp : exception.getStackTrace()) builder.append("    at " + temp.getClassName() + "(" + temp.getMethodName() + ":" + temp.getLineNumber() + ")\r\n");
		builder.setLength(builder.length() - 1);
		String temp = "[" + LoggerX.datetime() + "][EXCEPTION][" + name + "] 发生异常\r\n时间序号: " + timestamp + builder.toString();
		LoggerX.PRINT(temp);
		LoggerX.WRITE(temp);
	}


	public void exception(long timestamp, String category, Exception exception) {
		StringBuilder builder = new StringBuilder();
		builder.append("异常原因：" + exception.getCause() + "\r\n");
		builder.append("异常消息：" + exception.getMessage() + "\r\n");
		builder.append("异常调用：" + exception.getClass().getName() + "\r\n");
		for (StackTraceElement temp : exception.getStackTrace()) builder.append("    at " + temp.getClassName() + "(" + temp.getMethodName() + ":" + temp.getLineNumber() + ")\r\n");
		builder.setLength(builder.length() - 1);
		String temp = "[" + LoggerX.datetime() + "][EXCEPTION][" + name + "] " + category + "\r\n时间序号: " + timestamp + builder.toString();
		LoggerX.PRINT(temp);
		LoggerX.WRITE(temp);
	}


	// ==================================================================================================
	//
	//
	// ==================================================================================================


	public String raw(String category, String message) {
		String temp = "[" + LoggerX.datetime() + "][RAW-MESSAGE][" + name + "] " + category + "\r\n" + message + "\r\n[RAW-EOF]\r\n";
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


	public String warn(String category, String message) {
		String temp = "[" + LoggerX.datetime() + "][WARN][" + name + "]" + category + ": " + message;
		LoggerX.PRINT(temp);
		LoggerX.WRITE(temp);
		return message;
	}


	public String info(String category, String message) {
		String temp = "[" + LoggerX.datetime() + "][INFO][" + name + "]" + category + ": " + message;
		LoggerX.PRINT(temp);
		LoggerX.WRITE(temp);
		return message;
	}


	public String seek(String category, String message) {
		String temp = "[" + LoggerX.datetime() + "][SEEK][" + name + "]" + category + ": " + message;
		LoggerX.PRINT(temp);
		LoggerX.WRITE(temp);
		return message;
	}


	public String full(String category, String message) {
		String temp = "[" + LoggerX.datetime() + "][FULL][" + name + "]" + category + ": " + message;
		LoggerX.PRINT(temp);
		LoggerX.WRITE(temp);
		return message;
	}


	// ==================================================================================================
	//
	//
	// ==================================================================================================


	public long warn(String category, long message) {
		String temp = "[" + LoggerX.datetime() + "][WARN][" + name + "]" + category + ": " + message;
		LoggerX.PRINT(temp);
		LoggerX.WRITE(temp);
		return message;
	}


	public long info(String category, long message) {
		String temp = "[" + LoggerX.datetime() + "][INFO][" + name + "]" + category + ": " + message;
		LoggerX.PRINT(temp);
		LoggerX.WRITE(temp);
		return message;
	}


	public long seek(String category, long message) {
		String temp = "[" + LoggerX.datetime() + "][SEEK][" + name + "]" + category + ": " + message;
		LoggerX.PRINT(temp);
		LoggerX.WRITE(temp);
		return message;
	}


	public long full(String category, long message) {
		String temp = "[" + LoggerX.datetime() + "][FULL][" + name + "]" + category + ": " + message;
		LoggerX.PRINT(temp);
		LoggerX.WRITE(temp);
		return message;
	}


	// ==================================================================================================
	//
	//
	// ==================================================================================================


	public String[] warn(String category, String... message) {
		for (String line : message) {
			String temp = "[" + LoggerX.datetime() + "][WARN][" + name + "]" + category + ": " + line;
			LoggerX.PRINT(temp);
			LoggerX.WRITE(temp);
		}
		return message;
	}


	public String[] info(String category, String... message) {
		for (String line : message) {
			String temp = "[" + LoggerX.datetime() + "][INFO][" + name + "]" + category + ": " + line;
			LoggerX.PRINT(temp);
			LoggerX.WRITE(temp);
		}
		return message;
	}

	public String[] seek(String category, String... message) {

		for (String line : message) {
			String temp = "[" + LoggerX.datetime() + "][SEEK][" + name + "]" + category + ": " + line;
			LoggerX.PRINT(temp);
			LoggerX.WRITE(temp);
		}
		return message;
	}


	public String[] full(String category, String... message) {
		for (String line : message) {
			String temp = "[" + LoggerX.datetime() + "][FULL][" + name + "]" + category + ": " + line;
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
		try (FileWriter writer = new FileWriter(FILE_LOGGER, true)) {
			writer.append(message);
			writer.append("\r\n");
			writer.flush();
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
		for (int i = 0; i < raw.length(); i++) tmp.add(Integer.toHexString(raw.charAt(i) & 0xffff));

		String[] res = new String[tmp.size()];
		tmp.toArray(res);
		return res;
	}


	// ==================================================================================================
	//
	//
	// ==================================================================================================


	public static String date() {
		return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	}


	public static String date(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}


	public static String date(long timestamp) {
		return new SimpleDateFormat("yyyy-MM-dd").format(new Date(timestamp));
	}


	public static String time() {
		return new SimpleDateFormat("HH:mm:ss").format(new Date());
	}


	public static String time(Date date) {
		return new SimpleDateFormat("HH:mm:ss").format(date);
	}


	public static String time(long timestamp) {
		return new SimpleDateFormat("HH:mm:ss").format(new Date(timestamp));
	}


	public static String datetime() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}


	public static String datetime(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}


	public static String datetime(long timestamp) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timestamp));
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
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		formatter.setTimeZone(timezone);
		return formatter.format(new Date());
	}


	public static String formatTime(String format, TimeZone timezone, Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		formatter.setTimeZone(timezone);
		return formatter.format(date);
	}


	public static String formatTime(String format, TimeZone timezone, long timestamp) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		formatter.setTimeZone(timezone);
		return formatter.format(new Date(timestamp));
	}


	public static String duration(long time) {
		long ss = time;
		long dd = ss / 86400;
		ss = ss % 86400;
		long hh = ss / 3600;
		ss = ss % 3600;
		long mm = ss / 60;
		ss = ss % 60;
		return dd + " - " + String.format("%02d", hh) + ":" + String.format("%02d", mm) + ":" + String.format("%02d", ss);
	}


	public static String durationMille(long time) {
		long ms = time;
		long dd = ms / 86400000;
		ms = ms % 86400000;
		long hh = ms / 3600000;
		ms = ms % 3600000;
		long mm = ms / 60000;
		ms = ms % 60000;
		long ss = ms / 1000;
		ms = ms % 1000;
		return dd + " - " + hh + ":" + mm + ":" + ss + ":" + String.format("%04d", ms);
	}


	// ================================================================

}
