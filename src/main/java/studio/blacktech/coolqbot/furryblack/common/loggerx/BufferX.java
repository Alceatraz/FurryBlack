package studio.blacktech.coolqbot.furryblack.common.loggerx;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;


public class BufferX {


	private StringBuilder builder;
	private final LinkedList<StringBuilder> builders = new LinkedList<>();


	public BufferX() {
		warp();
	}


	public void warp() {
		StringBuilder temp = new StringBuilder();
		builders.add(temp);
		builder = temp;
	}


	public String[] make() {
		String[] message = new String[builders.size()];
		int length = builders.size();
		for (int i = 0; i < length; i++) {
			message[i] = builders.get(i).toString();
		}
		return message;
	}


	public void merge(String... message) {
		warp();
		for (String temp : message) {
			builders.add(new StringBuilder(temp));
		}
	}


	public void merge(BufferX... buffers) {
		for (BufferX buffer : buffers) {
			for (StringBuilder temp : buffer.dump()) {
				builders.add(temp);
			}
		}
	}


	public LinkedList<StringBuilder> dump() {
		return builders;
	}


	// ==================================================================================================
	//
	//
	// ==================================================================================================


	public void exception(Exception exception) {
		builder.append("[" + LoggerX.time() + "][EXCEPTION] 发生异常" + "\r\n");
		builder.append("异常原因：" + exception.getCause() + "\r\n");
		builder.append("异常消息：" + exception.getMessage() + "\r\n");
		builder.append("异常调用：" + exception.getClass().getName() + "\r\n");
		for (StackTraceElement temp : exception.getStackTrace()) {
			builder.append("    at " + temp.getClassName() + "(" + temp.getMethodName() + ":" + temp.getLineNumber() + ")\r\n");
		}
		builder.setLength(builder.length() - 1);
	}


	// ==================================================================================================
	//
	//
	// ==================================================================================================


	public String warn(String message) {
		String temp = "[" + LoggerX.time() + "][WARN]" + message + "\r\n";
		builder.append(temp);
		return this.info(message);
	}


	public String info(String message) {
		String temp = "[" + LoggerX.time() + "][INFO]" + message + "\r\n";
		builder.append(temp);
		return this.seek(message);
	}


	public String seek(String message) {
		String temp = "[" + LoggerX.time() + "][SEEK]" + message + "\r\n";
		builder.append(temp);
		return this.full(message);
	}


	public String full(String message) {
		String temp = "[" + LoggerX.time() + "][FULL]" + message + "\r\n";
		builder.append(temp);
		return message;
	}


	// ==================================================================================================
	//
	//
	// ==================================================================================================


	public String warn(String category, String message) {
		String temp = "[" + LoggerX.time() + "][WARN][" + category + "]" + message + "\r\n";
		builder.append(temp);
		return this.info(category, message);
	}


	public String info(String category, String message) {
		String temp = "[" + LoggerX.time() + "][INFO][" + category + "]" + message + "\r\n";
		builder.append(temp);
		return this.seek(category, message);
	}


	public String seek(String category, String message) {
		String temp = "[" + LoggerX.time() + "][SEEK][" + category + "]" + message + "\r\n";
		builder.append(temp);
		return this.full(category, message);
	}


	public String full(String category, String message) {
		String temp = "[" + LoggerX.time() + "][FULL][" + category + "]" + message + "\r\n";
		builder.append(temp);
		return message;
	}


	// ==================================================================================================
	//
	//
	// ==================================================================================================


	public String[] warn(String category, String... message) {
		for (String line : message) {
			String temp = "[" + LoggerX.time() + "][WARN]" + category + ": " + line + "\r\n";
			builder.append(temp);
		}
		return message;
	}


	public String[] info(String category, String... message) {
		for (String line : message) {
			String temp = "[" + LoggerX.time() + "][INFO]" + category + ": " + line + "\r\n";
			builder.append(temp);
		}
		return message;
	}


	public String[] seek(String category, String... message) {
		for (String line : message) {
			String temp = "[" + LoggerX.time() + "][SEEK]" + category + ": " + line + "\r\n";
			builder.append(temp);
		}
		return message;
	}


	public String[] full(String category, String... message) {
		for (String line : message) {
			String temp = "[" + LoggerX.time() + "][FULL]" + category + ": " + line + "\r\n";
			builder.append(temp);
		}
		return message;
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


	// ================================================================

}
