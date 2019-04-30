package studio.blacktech.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class LoggerX {

	private final HashMap<Integer, Long> clock = new HashMap<Integer, Long>();
	private final HashMap<String, Long> clockS = new HashMap<String, Long>();

	public static String time() {
		final SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formater.format(new Date());
	}

	public static String time(final Date date) {
		final SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formater.format(date);
	}

	public static String time(String formate) {
		final SimpleDateFormat formater = new SimpleDateFormat(formate);
		return formater.format(new Date());
	}

	public static String time(String formate, Date date) {
		final SimpleDateFormat formater = new SimpleDateFormat(formate);
		return formater.format(date);
	}

	public long clock(final int name, final boolean isReset) {
		final long time = System.nanoTime();

		if (this.clock.containsKey(name)) {
			if (isReset) {
				this.clock.put(name, time);
			}
			return time - this.clock.get(name);
		} else {
			this.clock.put(name, time);
		}
		return 0;
	}

	public long clock(final String name, final boolean isReset) {
		final long time = System.nanoTime();
		if (this.clockS.containsKey(name)) {
			if (isReset) {
				this.clockS.put(name, time);
			}
			return time - this.clockS.get(name);
		} else {
			this.clockS.put(name, time);
		}
		return 0;
	}
}
