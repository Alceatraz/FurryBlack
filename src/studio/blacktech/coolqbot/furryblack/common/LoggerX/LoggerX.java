package studio.blacktech.coolqbot.furryblack.common.LoggerX;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;

public class LoggerX {

	private StringBuilder builder_mini;
	private StringBuilder builder_info;
	private StringBuilder builder_seek;
	private StringBuilder builder_full;

//	private static StringBuilder builder_mini = new StringBuilder();
//	private static StringBuilder builder_info = new StringBuilder();
//	private static StringBuilder builder_seek = new StringBuilder();
//	private static StringBuilder builder_full = new StringBuilder();

	public LoggerX() {

		this.builder_mini = new StringBuilder();
		this.builder_info = new StringBuilder();
		this.builder_seek = new StringBuilder();
		this.builder_full = new StringBuilder();

		this.builder_mini.append(datetime());
		this.builder_info.append(datetime());
		this.builder_seek.append(datetime());
		this.builder_full.append(datetime());

		this.builder_mini.append("\r\n");
		this.builder_info.append("\r\n");
		this.builder_seek.append("\r\n");
		this.builder_full.append("\r\n");
	}

	public String make(int level) {
		switch (level) {
		case 0:
			return this.builder_mini.toString().substring(0, this.builder_mini.length() - 2);
		case 1:
			return this.builder_info.toString().substring(0, this.builder_info.length() - 2);
		case 2:
			return this.builder_seek.toString().substring(0, this.builder_seek.length() - 2);
		default:
			return this.builder_full.toString().substring(0, this.builder_full.length() - 2);
		}
	}

	// ==================================================================================================
	//
	//
	// ==================================================================================================

	// =====================================
	//
	// [11:22:33] 消息内容
	//
	// =====================================

	public String mini(String message) {
		this.builder_mini.append("[" + time() + "]" + message + "\r\n");
		return this.info(message);
	}

	public String info(String message) {
		this.builder_info.append("[" + time() + "]" + message + "\r\n");
		return this.seek(message);
	}

	public String seek(String message) {
		this.builder_seek.append("[" + time() + "]" + message + "\r\n");
		return this.full(message);
	}

	public String full(String message) {
		this.builder_full.append("[" + time() + "]" + message + "\r\n");
		return message;
	}

	// =====================================
	//
	// [11:22:33] 消息内容：消息内容
	//
	// =====================================

	public String mini(String category, String message) {
		this.builder_mini.append("[" + time() + "][" + category + "] " + message + "\r\n");
		return this.info(category, message);
	}

	public String info(String category, String message) {
		this.builder_info.append("[" + time() + "][" + category + "] " + message + "\r\n");
		return this.seek(category, message);
	}

	public String seek(String category, String message) {
		this.builder_seek.append("[" + time() + "][" + category + "] " + message + "\r\n");
		return this.full(category, message);
	}

	public String full(String category, String message) {
		this.builder_full.append("[" + time() + "][" + category + "] " + message + "\r\n");
		return message;
	}

	// =====================================
	//
	// [11:22:33][CORE] 消息内容：消息内容
	//
	// =====================================

	public String mini(String packname, String key, String value) {
		this.builder_mini.append("[" + time() + "][" + packname + "]" + key + "：" + value + "\r\n");
		return this.info(packname, key, value);
	}

	public String info(String packname, String key, String value) {
		this.builder_info.append("[" + time() + "][" + packname + "]" + key + "：" + value + "\r\n");
		return this.seek(packname, key, value);
	}

	public String seek(String packname, String key, String value) {
		this.builder_seek.append("[" + time() + "][" + packname + "]" + key + "：" + value + "\r\n");
		return this.full(packname, key, value);
	}

	public String full(String packname, String key, String value) {
		this.builder_full.append("[" + time() + "][" + packname + "]" + key + "：" + value + "\r\n");
		return value;
	}

	// ========================================================

	public int mini(String packname, String key, int value) {
		this.builder_mini.append("[" + time() + "][" + packname + "]" + key + "：" + value + "\r\n");
		return this.info(packname, key, value);
	}

	public int info(String packname, String key, int value) {
		this.builder_mini.append("[" + time() + "][" + packname + "]" + key + "：" + value + "\r\n");
		return this.seek(packname, key, value);
	}

	public int seek(String packname, String key, int value) {
		this.builder_seek.append("[" + time() + "][" + packname + "]" + key + "：" + value + "\r\n");
		return this.full(packname, key, value);
	}

	public int full(String packname, String key, int value) {
		this.builder_full.append("[" + time() + "][" + packname + "]" + key + "：" + value + "\r\n");
		return value;
	}

	// ========================================================

	public long mini(String packname, String key, long value) {
		this.builder_mini.append("[" + time() + "][" + packname + "]" + key + "：" + value + "\r\n");
		return this.info(packname, key, value);
	}

	public long info(String packname, String key, long value) {
		this.builder_info.append("[" + time() + "][" + packname + "]" + key + "：" + value + "\r\n");
		return this.seek(packname, key, value);
	}

	public long seek(String packname, String key, long value) {
		this.builder_seek.append("[" + time() + "][" + packname + "]" + key + "：" + value + "\r\n");
		return this.full(packname, key, value);
	}

	public long full(String packname, String key, long value) {
		this.builder_full.append("[" + time() + "][" + packname + "]" + key + "：" + value + "\r\n");
		return value;
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

	private static SimpleDateFormat formater_date = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat formater_time = new SimpleDateFormat("HH:mm:ss");
	private static SimpleDateFormat formater_full = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	// ================================================================

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
