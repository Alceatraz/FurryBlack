package studio.blacktech.coolqbot.furryblack;

import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.utility.Message;

public abstract class JavaExtension {

	private static int COUNT = 0;

	private static String MODULE_FULLHELP = null;

	private final String MODULE_DISPLAYNAME = null;
	private final String MODULE_COMMANDNAME = null;
	private final String MODULE_VERSION = null;
	private final String MODULE_DESCRIPTION = null;
	private final String[] MODULE_USAGE = null;
	private final String[] MODULE_PRIVACY_LISTEN = null;
	private final String[] MODULE_PRIVACY_EVENTS = null;
	private final String[] MODULE_PRIVACY_STORED = null;
	private final String[] MODULE_PRIVACY_CACHED = null;
	private final String[] MODULE_PRIVACY_OBTAIN = null;

	public abstract boolean doUserMessage(int typeid, int userid, Message message, int messageid, int messagefont) throws Exception;

	public abstract boolean doDiszMessage(int diszid, int userid, Message message, int messageid, int messagefont) throws Exception;

	public abstract boolean doGropMessage(int gropid, int userid, Message message, int messageid, int messagefont) throws Exception;

	public boolean excuteUserMessage(int typeid, int userid, String message, int messageid, int messagefont) throws Exception {
		COUNT++;
		return doUserMessage(typeid, userid, new Message(message), messageid, messagefont);
	}

	public boolean excuteDiszMessage(int diszid, int userid, String message, int messageid, int messagefont) throws Exception {
		COUNT++;
		return doDiszMessage(diszid, userid, new Message(message), messageid, messagefont);
	}

	public boolean excuteGropMessage(int gropid, int userid, String message, int messageid, int messagefont) throws Exception {
		COUNT++;
		return doGropMessage(gropid, userid, new Message(message), messageid, messagefont);
	}

	public abstract String getReport();

	public static int getCount() {
		return COUNT;
	}

	public static String getHelp() {
		return MODULE_FULLHELP;
	}

	public void genFullHelp() {
		final StringBuilder builder = new StringBuilder();
		builder.append(this.MODULE_COMMANDNAME);
		builder.append(" > ");
		builder.append(this.MODULE_DISPLAYNAME);
		builder.append(" v");
		builder.append(this.MODULE_VERSION);
		builder.append(" - ");
		builder.append(this.MODULE_DESCRIPTION);
		builder.append("\r\n");
		if (MODULE_USAGE.length == 0) {
			builder.append("√¸¡Ó”√∑®: Œﬁ");
			builder.append("\r\n");
		} else {
			builder.append("√¸¡Ó”√∑®");
			builder.append("\r\n");
			for (final String temp : this.MODULE_USAGE) {
				builder.append(temp);
				builder.append("\r\n");
			}
		}
		builder.append("“˛ÀΩ…˘√˜\r\n");
		builder.append("º‡Ã˝:");
		builder.append(MODULE_PRIVACY_LISTEN.length);
		builder.append("\r\n");
		if (MODULE_PRIVACY_LISTEN.length != 0) {
			for (final String temp : this.MODULE_PRIVACY_LISTEN) {
				builder.append(temp);
				builder.append("\r\n");
			}
		}
		builder.append(" ¬º˛:");
		builder.append(MODULE_PRIVACY_EVENTS.length);
		builder.append("\r\n");
		if (MODULE_PRIVACY_EVENTS.length != 0) {
			for (final String temp : this.MODULE_PRIVACY_EVENTS) {
				builder.append(temp);
				builder.append("\r\n");
			}
		}
		builder.append("¥Ê¥¢:");
		builder.append(MODULE_PRIVACY_STORED.length);
		builder.append("\r\n");
		if (MODULE_PRIVACY_STORED.length != 0) {
			for (final String temp : this.MODULE_PRIVACY_STORED) {
				builder.append(temp);
				builder.append("\r\n");
			}
		}
		builder.append("ª∫¥Ê:");
		builder.append(MODULE_PRIVACY_CACHED.length);
		builder.append("\r\n");
		if (MODULE_PRIVACY_CACHED.length != 0) {
			for (final String temp : this.MODULE_PRIVACY_CACHED) {
				builder.append(temp);
				builder.append("\r\n");
			}
		}
		builder.append("ªÒ»°:");
		builder.append(MODULE_PRIVACY_OBTAIN.length);
		builder.append("\r\n");
		if (MODULE_PRIVACY_OBTAIN.length != 0) {
			for (final String temp : this.MODULE_PRIVACY_OBTAIN) {
				builder.append(temp);
				builder.append("\r\n");
			}
		}
		MODULE_FULLHELP = builder.toString();
	}

	public static void userInfo(final long userid, final String message) {
		JcqApp.CQ.sendPrivateMsg(userid, message);
	}

	public static void userWarn(final long userid, final String message) {
		JcqApp.CQ.sendPrivateMsg(userid, "√¸¡Ó¥ÌŒÛ:" + message);
	}

	public static void diszInfo(long diszid, final String message) {
		JcqApp.CQ.sendDiscussMsg(diszid, message);
	}

	public static void diszWarn(long diszid, final String message) {
		JcqApp.CQ.sendDiscussMsg(diszid, "√¸¡Ó¥ÌŒÛ: " + message);
	}

	public static void diszInfo(long diszid, long userid, final String message) {
		JcqApp.CQ.sendDiscussMsg(diszid, "[CQ:at,qq=" + userid + "] " + message);
	}

	public static void diszWarn(long diszid, long userid, final String message) {
		JcqApp.CQ.sendDiscussMsg(diszid, "[CQ:at,qq=" + userid + "] √¸¡Ó¥ÌŒÛ: " + message);
	}

	public static void gropInfo(long gropid, final String message) {
		JcqApp.CQ.sendGroupMsg(gropid, message);
	}

	public static void gropWarn(long gropid, final String message) {
		JcqApp.CQ.sendGroupMsg(gropid, "√¸¡Ó¥ÌŒÛ: " + message);
	}

	public static void gropInfo(long gropid, long userid, final String message) {
		JcqApp.CQ.sendGroupMsg(gropid, "[CQ:at,qq=" + userid + "] " + message);
	}

	public static void gropWarn(long gropid, long userid, final String message) {
		JcqApp.CQ.sendGroupMsg(gropid, "[CQ:at,qq=" + userid + "] √¸¡Ó¥ÌŒÛ: " + message);
	}
}
