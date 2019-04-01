package studio.blacktech.coolqbot.furryblack.module;

import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.signal.Workflow;

public abstract class FunctionExecutor {

	public int counter = 0;
	public String MODULE_NAME = null;
	public String MODULE_COMMAND = null;
	public String MODULE_VERSION = null;
	public String MODULE_DESCRIPTION = null;
	public String MODULE_HELP = null;
	public String MODULE_FULLHELP = null;
	public String MODULE_PRIVACY = null;

	public abstract void executor(Workflow flow) throws Exception;

	public void genHelpinfo() {
		this.MODULE_FULLHELP = this.MODULE_COMMAND + " > " + this.MODULE_NAME + " v" + this.MODULE_VERSION + " - " + this.MODULE_DESCRIPTION + "\r\n√¸¡Ó”√∑®:\r\n" + this.MODULE_HELP + "\r\n“˛ÀΩ…˘√˜:\r\n" + this.MODULE_PRIVACY;
	}

	public static void priInfo(final Workflow flow, final String message) {
		JcqApp.CQ.sendPrivateMsg(flow.qqid, message);
	}

	public static void priWarn(final Workflow flow, final String message) {
		JcqApp.CQ.sendPrivateMsg(flow.qqid, "√¸¡Ó¥ÌŒÛ:" + message);
	}

	public static void disAnno(final long dzid, final String message) {
		JcqApp.CQ.sendDiscussMsg(dzid, message);
	}

	public static void disInfo(final Workflow flow, final String message) {
		JcqApp.CQ.sendDiscussMsg(flow.dzid, "[CQ:at,qq=" + flow.qqid + "] " + message);
	}

	public static void disWarn(final Workflow flow, final String message) {
		JcqApp.CQ.sendDiscussMsg(flow.dzid, "[CQ:at,qq=" + flow.qqid + "] √¸¡Ó¥ÌŒÛ: " + message);
	}

	public static void grpAnno(final long gpid, final String message) {
		JcqApp.CQ.sendGroupMsg(gpid, message);
	}

	public static void grpInfo(final Workflow flow, final String message) {
		JcqApp.CQ.sendGroupMsg(flow.gpid, "[CQ:at,qq=" + flow.qqid + "] " + message);
	}

	public static void grpWarn(final Workflow flow, final String message) {
		JcqApp.CQ.sendGroupMsg(flow.gpid, "[CQ:at,qq=" + flow.qqid + "] √¸¡Ó¥ÌŒÛ: " + message);
	}

	public abstract String genReport();

}
