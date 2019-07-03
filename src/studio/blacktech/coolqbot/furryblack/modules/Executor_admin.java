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
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "admin";
	private static String MODULE_DISPLAYNAME = "Sakurga";
	private static String MODULE_DESCRIPTION = "??rZVxx";
	private static String MODULE_VERSION = "Lv:80";
	private static String[] MODULE_USAGE = new String[] {
			"~~?C???r Pz>\\???",
			"~~\\T+??r Pz>]]??",
			"~~\\C???r Fy:????",
			".?-\\C??r Pt=??!#",
			"~.ZT\\C?r Rr>:!??"
	};
	private static String[] MODULE_PRIVACY_TRIGER = new String[] {};
	private static String[] MODULE_PRIVACY_LISTEN = new String[] {};
	private static String[] MODULE_PRIVACY_STORED = new String[] {};
	private static String[] MODULE_PRIVACY_CACHED = new String[] {};
	private static String[] MODULE_PRIVACY_OBTAIN = new String[] {};

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================

	public Executor_admin() throws Exception {
		super(MODULE_DISPLAYNAME, MODULE_PACKAGENAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_TRIGER, MODULE_PRIVACY_LISTEN, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	@Override
	public void init(LoggerX logger) throws Exception {

	}

	@Override
	public void boot(LoggerX logger) throws Exception {
		this.ENABLE_USER = true;
		this.ENABLE_DISZ = false;
		this.ENABLE_GROP = true;
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

		if (entry.getMessage().isAdmin(userid)) { return false; }

		if (message.getSection() == 0) {

			entry.getMessage().adminInfo(entry.getSystemd().generateSystemReport());
//			entry.getRouter().adminInfo(entry.getSystemd().generateModuleReport());

			return true;

		} else {

			switch (message.parseOption().getSegment()[0]) {

			case "ddns":

				return doDDNS(typeid, userid, message, messageid, messagefont);

			// 获取地址
			case "getddns":
				entry.getMessage().adminInfo(entry.getDDNSAPI().getIPAddress());
				break;

			// 设置地址 强制更新 手动设置
			case "setddns":
				if (message.getSection() == 0) {
					entry.getMessage().adminInfo(entry.getDDNSAPI().updateDDNSIP());
				} else {
					entry.getMessage().adminInfo(entry.getDDNSAPI().setDDNSAddress(message.getSegment()[0]));
				}
				break;

			}
		}
		return true;
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, MessageDisz message, int messageid, int messagefont) throws Exception {
		if (entry.getMessage().isAdmin(userid)) { return false; }
		return true;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, MessageGrop message, int messageid, int messagefont) throws Exception {
		if (entry.getMessage().isAdmin(userid)) { return false; }
		return true;
	}

	@Override
	public String[] generateReport(int mode, final Message message, final Object... parameters) {
		return null;
	}

	private boolean doDDNS(int typeid, long userid, MessageUser message, int messageid, int messagefont) {

		
		
		
	}

}
