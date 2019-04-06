package studio.blacktech.coolqbot.furryblack.module;

import com.sobte.cqp.jcq.event.JcqApp;

public abstract class Module {
	public int COUNT = 0;
	public String MODULE_DISPLAYNAME;
	public String MODULE_PACKAGENAME;
	public String MODULE_DESCRIPTION;
	public String MODULE_VERSION;
	public String[] MODULE_USAGE;
	public String[] MODULE_PRIVACY_TRIGER;
	public String[] MODULE_PRIVACY_LISTEN;
	public String[] MODULE_PRIVACY_STORED;
	public String[] MODULE_PRIVACY_CACHED;
	public String[] MODULE_PRIVACY_OBTAIN;

	public String MODULE_FULLHELP;

	public abstract String generateReport();

	public void genFullHelp() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.MODULE_PACKAGENAME);
		builder.append(" > ");
		builder.append(this.MODULE_DISPLAYNAME);
		builder.append(" v");
		builder.append(this.MODULE_VERSION);
		builder.append(" - ");
		builder.append(this.MODULE_DESCRIPTION);
		builder.append("\r\n");
		if (this.MODULE_USAGE.length == 0) {
			builder.append("√¸¡Ó”√∑®: Œﬁ");
			builder.append("\r\n");
		} else {
			builder.append("√¸¡Ó”√∑®");
			builder.append("\r\n");
			for (String temp : this.MODULE_USAGE) {
				builder.append(temp);
				builder.append("\r\n");
			}
		}
		builder.append("“˛ÀΩ…˘√˜\r\n");
		builder.append("¥•∑¢:");
		builder.append(this.MODULE_PRIVACY_TRIGER.length);
		builder.append("\r\n");
		if (this.MODULE_PRIVACY_TRIGER.length != 0) {
			for (String temp : this.MODULE_PRIVACY_TRIGER) {
				builder.append(temp);
				builder.append("\r\n");
			}
		}
		builder.append("º‡Ã˝:");
		builder.append(this.MODULE_PRIVACY_LISTEN.length);
		builder.append("\r\n");
		if (this.MODULE_PRIVACY_LISTEN.length != 0) {
			for (String temp : this.MODULE_PRIVACY_LISTEN) {
				builder.append(temp);
				builder.append("\r\n");
			}
		}

		builder.append("¥Ê¥¢:");
		builder.append(this.MODULE_PRIVACY_STORED.length);
		builder.append("\r\n");
		if (this.MODULE_PRIVACY_STORED.length != 0) {
			for (String temp : this.MODULE_PRIVACY_STORED) {
				builder.append(temp);
				builder.append("\r\n");
			}
		}
		builder.append("ª∫¥Ê:");
		builder.append(this.MODULE_PRIVACY_CACHED.length);
		builder.append("\r\n");
		if (this.MODULE_PRIVACY_CACHED.length != 0) {
			for (String temp : this.MODULE_PRIVACY_CACHED) {
				builder.append(temp);
				builder.append("\r\n");
			}
		}
		builder.append("ªÒ»°:");
		builder.append(this.MODULE_PRIVACY_OBTAIN.length);
		builder.append("\r\n");
		if (this.MODULE_PRIVACY_OBTAIN.length != 0) {
			for (String temp : this.MODULE_PRIVACY_OBTAIN) {
				builder.append(temp);
				builder.append("\r\n");
			}
		}
		this.MODULE_FULLHELP = builder.toString();
	}

	public static void userInfo(final long userid, final String message) {
		JcqApp.CQ.sendPrivateMsg(userid, message);
	}

	public static void userWarn(final long userid, final String message) {
		JcqApp.CQ.sendPrivateMsg(userid, "√¸¡Ó¥ÌŒÛ:" + message);
	}

	public static void diszInfo(final long diszid, final String message) {
		JcqApp.CQ.sendDiscussMsg(diszid, message);
	}

	public static void diszWarn(final long diszid, final String message) {
		JcqApp.CQ.sendDiscussMsg(diszid, "√¸¡Ó¥ÌŒÛ: " + message);
	}

	public static void diszInfo(final long diszid, final long userid, final String message) {
		JcqApp.CQ.sendDiscussMsg(diszid, "[CQ:at,qq=" + userid + "] " + message);
	}

	public static void diszWarn(final long diszid, final long userid, final String message) {
		JcqApp.CQ.sendDiscussMsg(diszid, "[CQ:at,qq=" + userid + "] √¸¡Ó¥ÌŒÛ: " + message);
	}

	public static void gropInfo(final long gropid, final String message) {
		JcqApp.CQ.sendGroupMsg(gropid, message);
	}

	public static void gropWarn(final long gropid, final String message) {
		JcqApp.CQ.sendGroupMsg(gropid, "√¸¡Ó¥ÌŒÛ: " + message);
	}

	public static void gropInfo(final long gropid, final long userid, final String message) {
		JcqApp.CQ.sendGroupMsg(gropid, "[CQ:at,qq=" + userid + "] " + message);
	}

	public static void gropWarn(final long gropid, final long userid, final String message) {
		JcqApp.CQ.sendGroupMsg(gropid, "[CQ:at,qq=" + userid + "] √¸¡Ó¥ÌŒÛ: " + message);
	}
}
