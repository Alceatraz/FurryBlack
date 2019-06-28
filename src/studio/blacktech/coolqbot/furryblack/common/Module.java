package studio.blacktech.coolqbot.furryblack.common;

import com.sobte.cqp.jcq.event.JcqApp;

public abstract class Module {

	public int COUNT = 0;
	public String MODULE_PACKAGENAME;
	public String MODULE_DISPLAYNAME;
	public String MODULE_DESCRIPTION;
	public String MODULE_VERSION;
	public String[] MODULE_USAGE;
	public String[] MODULE_PRIVACY_TRIGER;
	public String[] MODULE_PRIVACY_LISTEN;
	public String[] MODULE_PRIVACY_STORED;
	public String[] MODULE_PRIVACY_CACHED;
	public String[] MODULE_PRIVACY_OBTAIN;

	public String MODULE_FULLHELP;

	public abstract void memberExit(final long gropid, final long userid);

	public abstract void memberJoin(final long gropid, final long userid);

	public abstract String[] generateReport(final int logLevel, final int logMode, final int typeid, final long userid, final long diszid, final long gropid, final Message message, final Object... parameters);

	public void genFullHelp() {

		final StringBuilder builder = new StringBuilder();

		builder.append(this.MODULE_PACKAGENAME);
		builder.append(" > ");
		builder.append(this.MODULE_DISPLAYNAME);
		builder.append(" v");
		builder.append(this.MODULE_VERSION);
		builder.append(" - ");
		builder.append(this.MODULE_DESCRIPTION);
		builder.append("\r\n√¸¡Ó”√∑®£∫");

		if (this.MODULE_USAGE.length == 0) {
			builder.append("Œﬁ");
		} else {
			for (final String temp : this.MODULE_USAGE) {
				builder.append("\r\n");
				builder.append(temp);
			}
		}

		builder.append("\r\n“˛ÀΩ…˘√˜£∫");

		builder.append("\r\n¥•∑¢∆˜£∫");
		if (this.MODULE_PRIVACY_TRIGER.length == 0) {
			builder.append("Œﬁ");
		} else {
			builder.append(this.MODULE_PRIVACY_TRIGER.length);
			for (final String temp : this.MODULE_PRIVACY_TRIGER) {
				builder.append("\r\n  ");
				builder.append(temp);
			}
		}

		builder.append("\r\nº‡Ã˝∆˜£∫");
		if (this.MODULE_PRIVACY_LISTEN.length == 0) {
			builder.append("Œﬁ");
		} else {
			builder.append(this.MODULE_PRIVACY_LISTEN.length);
			for (final String temp : this.MODULE_PRIVACY_LISTEN) {
				builder.append("\r\n  ");
				builder.append(temp);
			}
		}

		builder.append("\r\n¥Ê¥¢£∫");
		if (this.MODULE_PRIVACY_STORED.length == 0) {
			builder.append("Œﬁ");
		} else {
			builder.append(this.MODULE_PRIVACY_STORED.length);
			for (final String temp : this.MODULE_PRIVACY_STORED) {
				builder.append("\r\n  ");
				builder.append(temp);
			}
		}

		builder.append("\r\nª∫¥Ê£∫");
		if (this.MODULE_PRIVACY_CACHED.length == 0) {
			builder.append("Œﬁ");
		} else {
			builder.append(this.MODULE_PRIVACY_CACHED.length);
			for (final String temp : this.MODULE_PRIVACY_CACHED) {
				builder.append("\r\n  ");
				builder.append(temp);
			}
		}

		builder.append("\r\nªÒ»°£∫");
		if (this.MODULE_PRIVACY_OBTAIN.length == 0) {
			builder.append("Œﬁ");
		} else {
			builder.append(this.MODULE_PRIVACY_OBTAIN.length);
			for (final String temp : this.MODULE_PRIVACY_OBTAIN) {
				builder.append("\r\n  ");
				builder.append(temp);
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
