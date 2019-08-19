package studio.blacktech.coolqbot.furryblack.common;

public class LoggerXDummy extends LoggerX {

	/***
	 * 假的LoggerX 适用于不关心内容但是必须传入LoggerX的情况
	 */
	public LoggerXDummy() {
	}

	public String mini(String message) {
		return message;
	}

	public String rawmini(String message) {
		return message;
	}

	public String info(String message) {
		return message;
	}

	public String rawinfo(String message) {
		return message;
	}

	public String seek(String message) {
		return message;
	}

	public String rawseek(String message) {
		return message;
	}

	public String full(String message) {
		return message;
	}

	public String rawfull(String message) {
		return message;
	}

	public String mini(String name, String message) {
		return message;
	}

	public String info(String name, String message) {
		return message;
	}

	public String seek(String name, String message) {
		return message;
	}

	public String full(String name, String message) {
		return message;
	}

	public long mini(long message) {
		return message;
	}

	public long info(long message) {
		return message;
	}

	public long seek(long message) {
		return message;
	}

	public long full(long message) {
		return message;
	}

	public long mini(String name, long message) {
		return message;
	}

	public long info(String name, long message) {
		return message;
	}

	public long seek(String name, long message) {
		return message;
	}

	public long full(String name, long message) {
		return message;
	}

	public String make(int level) {
		return "LoggerX Dummy";
	}

}
