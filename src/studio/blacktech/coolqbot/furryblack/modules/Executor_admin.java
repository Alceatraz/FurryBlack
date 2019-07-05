package studio.blacktech.coolqbot.furryblack.modules;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;

public class Executor_admin extends ModuleExecutor {

	// ==========================================================================================================================================================
	//
	// ģ���������
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "executor_admin";
	private static String MODULE_COMMANDNAME = "admin";
	private static String MODULE_DISPLAYNAME = "shell";
	private static String MODULE_DESCRIPTION = "����Աshell";
	private static String MODULE_VERSION = "1.0";
	private static String[] MODULE_USAGE = new String[] {};
	private static String[] MODULE_PRIVACY_TRIGER = new String[] {};
	private static String[] MODULE_PRIVACY_LISTEN = new String[] {};
	private static String[] MODULE_PRIVACY_STORED = new String[] {};
	private static String[] MODULE_PRIVACY_CACHED = new String[] {};
	private static String[] MODULE_PRIVACY_OBTAIN = new String[] {};

	// ==========================================================================================================================================================
	//
	// ��Ա����
	//
	// ==========================================================================================================================================================

	public Executor_admin() throws Exception {
		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_TRIGER, MODULE_PRIVACY_LISTEN, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	@Override
	public void init(LoggerX logger) throws Exception {
		this.ENABLE_USER = true;
		this.ENABLE_DISZ = false;
		this.ENABLE_GROP = true;
	}

	@Override
	public void boot(LoggerX logger) throws Exception {

	}

	@Override
	public void shut(LoggerX logger) throws Exception {
	}

	@Override
	public void reload(LoggerX logger) throws Exception {
	}

	@Override
	public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) {
	}

	@Override
	public void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) {
	}

	@Override
	public boolean doUserMessage(int typeid, long userid, MessageUser message, int messageid, int messagefont) throws Exception {
		if (!entry.getMessage().isAdmin(userid)) { return false; }
		if (message.getSection() == 0) {
			entry.getSystemd().sendSystemsReport(0, 0);
			entry.getSystemd().sendModulesReport(0, 0);
			return true;
		} else {
			message.parseOption();
			message.parseFlags();
			switch (message.getSegment()[0]) {
			case "report":
				entry.getSystemd().sendModuleReport(message);
				return true;
			}
			return false;
		}
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, MessageDisz message, int messageid, int messagefont) throws Exception {
		return false;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, MessageGrop message, int messageid, int messagefont) throws Exception {
		if (!entry.getMessage().isAdmin(userid)) { return false; }

		if (message.getSection() == 0) {
			entry.getSystemd().sendSystemsReport(0, 0);
			entry.getSystemd().sendModulesReport(0, 0);
			return true;
		} else {
			message.parseOption();
			message.parseFlags();
			switch (message.getSegment()[0]) {
			case "say":
				entry.getMessage().gropInfo(gropid, message.join(1));
				return true;
			case "message":
				switch (message.getSegment()[1]) {
				case "revoke":
					entry.getMessage().revokeMessage(gropid);
					System.out.print(message);
					break;
				}
				return true;
			case "system":
				return true;
			case "ddns":
				return true;
			case "nick":
				return true;
			case "mode":
				return true;

			case "report":
				entry.getSystemd().sendModuleReport(message);
				return true;
			}
			return false;
		}
	}

	@Override
	public String[] generateReport(int mode, Message message, Object... parameters) {
		return null;
	}
}