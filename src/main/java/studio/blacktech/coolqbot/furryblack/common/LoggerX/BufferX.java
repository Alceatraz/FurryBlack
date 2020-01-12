package studio.blacktech.coolqbot.furryblack.common.LoggerX;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;


public class BufferX {

	private StringBuilder builder;
	private LinkedList<StringBuilder> builders = new LinkedList<>();

	public BufferX() {

		this.warp();

	}

	public void warp() {

		StringBuilder temp = new StringBuilder();
		this.builders.add(temp);
		this.builder = temp;

	}

	public String[] make() {

		String[] message = new String[this.builders.size()];
		int length = this.builders.size();

		for (int i = 0; i < length; i++) {

			message[i] = this.builders.get(i).toString();

		}
		return message;

	}

	/**
	 * OMG . this is too shit , I will figure out a better way
	 *
	 * @param message
	 */

	public void merge(String... message) {

		this.warp();

		for (String temp : message) {

			this.builders.add(new StringBuilder(temp));

		}

	}

	public void merge(BufferX... buffers) {

		for (BufferX buffer : buffers) {

			for (StringBuilder temp : buffer.dump()) {

				this.builders.add(temp);

			}

		}

	}

	public LinkedList<StringBuilder> dump() {

		return this.builders;

	}

	// ==================================================================================================
	//
	//
	// ==================================================================================================

	public void exception(Exception exception) {

		this.builder.append("[" + LoggerX.time() + "][EXCEPTION] 发生异常" + "\r\n");

		this.builder.append("异常原因：" + exception.getCause() + "\r\n");
		this.builder.append("异常消息：" + exception.getMessage() + "\r\n");
		this.builder.append("异常调用：" + exception.getClass().getName() + "\r\n");

		for (StackTraceElement temp : exception.getStackTrace()) {

			this.builder.append("    at " + temp.getClassName() + "(" + temp.getMethodName() + ":"
					+ temp.getLineNumber() + ")\r\n");

		}

		this.builder.setLength(this.builder.length() - 1);

	}

	// ==================================================================================================
	//
	//
	// ==================================================================================================

	public String warn(String message) {

		String temp = "[" + LoggerX.time() + "][WARN]" + message + "\r\n";
		this.builder.append(temp);
		return this.info(message);

	}

	public String info(String message) {

		String temp = "[" + LoggerX.time() + "][INFO]" + message + "\r\n";
		this.builder.append(temp);
		return this.seek(message);

	}

	public String seek(String message) {

		String temp = "[" + LoggerX.time() + "][SEEK]" + message + "\r\n";
		this.builder.append(temp);
		return this.full(message);

	}

	public String full(String message) {

		String temp = "[" + LoggerX.time() + "][FULL]" + message + "\r\n";
		this.builder.append(temp);
		return message;

	}

	// ==================================================================================================
	//
	//
	// ==================================================================================================

	public String warn(String category, String message) {

		String temp = "[" + LoggerX.time() + "][WARN][" + category + "]" + message + "\r\n";
		this.builder.append(temp);
		return this.info(category, message);

	}

	public String info(String category, String message) {

		String temp = "[" + LoggerX.time() + "][INFO][" + category + "]" + message + "\r\n";
		this.builder.append(temp);
		return this.seek(category, message);

	}

	public String seek(String category, String message) {

		String temp = "[" + LoggerX.time() + "][SEEK][" + category + "]" + message + "\r\n";
		this.builder.append(temp);
		return this.full(category, message);

	}

	public String full(String category, String message) {

		String temp = "[" + LoggerX.time() + "][FULL][" + category + "]" + message + "\r\n";
		this.builder.append(temp);
		return message;

	}

	// ==================================================================================================
	//
	//
	// ==================================================================================================

	public String[] warn(String catgory, String... message) {

		for (String line : message) {

			String temp = "[" + LoggerX.time() + "][WARN]" + catgory + ": " + line + "\r\n";
			this.builder.append(temp);

		}
		return message;

	}

	public String[] info(String catgory, String... message) {

		for (String line : message) {

			String temp = "[" + LoggerX.time() + "][INFO]" + catgory + ": " + line + "\r\n";
			this.builder.append(temp);

		}
		return message;

	}

	public String[] seek(String catgory, String... message) {

		for (String line : message) {

			String temp = "[" + LoggerX.time() + "][SEEK]" + catgory + ": " + line + "\r\n";
			this.builder.append(temp);

		}
		return message;

	}

	public String[] full(String catgory, String... message) {

		for (String line : message) {

			String temp = "[" + LoggerX.time() + "][FULL]" + catgory + ": " + line + "\r\n";
			this.builder.append(temp);

		}
		return message;

	}

	// ==================================================================================================
	//
	//
	// ==================================================================================================

	private final static SimpleDateFormat formater_date = new SimpleDateFormat("yyyy-MM-dd");
	private final static SimpleDateFormat formater_time = new SimpleDateFormat("HH:mm:ss");
	private final static SimpleDateFormat formater_full = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	// ================================================================

	public static String date() {

		return BufferX.formater_date.format(new Date());

	}

	public static String date(Date date) {

		return BufferX.formater_date.format(date);

	}

	public static String date(long timestamp) {

		return BufferX.formater_date.format(new Date(timestamp));

	}

	public static String time() {

		return BufferX.formater_time.format(new Date());

	}

	public static String time(Date date) {

		return BufferX.formater_time.format(date);

	}

	public static String time(long timestamp) {

		return BufferX.formater_time.format(new Date(timestamp));

	}

	public static String datetime() {

		return BufferX.formater_full.format(new Date());

	}

	public static String datetime(Date date) {

		return BufferX.formater_full.format(date);

	}

	public static String datetime(long timestamp) {

		return BufferX.formater_full.format(new Date(timestamp));

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
