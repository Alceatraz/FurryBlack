package studio.blacktech.coolqbot.furryblack.plugins;

import studio.blacktech.coolqbot.furryblack.SystemHandler;
import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.module.Message;
import studio.blacktech.coolqbot.furryblack.module.Module;
import studio.blacktech.coolqbot.furryblack.module.ModuleExecutor;
import studio.blacktech.coolqbot.furryblack.scheduler.Scheduler_DDNS;

public class Executor_admin extends ModuleExecutor {

	public Executor_admin() {

		this.MODULE_DISPLAYNAME = "����Ա��̨";
		this.MODULE_PACKAGENAME = "admin";
		this.MODULE_DESCRIPTION = "����Ա��̨";
		this.MODULE_VERSION = "1.0.0";
		this.MODULE_USAGE = new String[] {};
		this.MODULE_PRIVACY_TRIGER = new String[] {};
		this.MODULE_PRIVACY_LISTEN = new String[] {};
		this.MODULE_PRIVACY_STORED = new String[] {};
		this.MODULE_PRIVACY_CACHED = new String[] {};
		this.MODULE_PRIVACY_OBTAIN = new String[] {};
	}

	@Override
	public boolean doUserMessage(int typeid, long userid, Message message, int messageid, int messagefont) throws Exception {
		if (userid != entry.OPERATOR()) {
			return false;
		}
		if (message.segment < 2) {
			String temp = SystemHandler.genReport(false, 0, null);
			Module.userInfo(entry.OPERATOR(), temp);
			return true;
		} else {
			switch (message.messages[1]) {
			// ��ȡ��ַ
			case "getddns":
				Module.userInfo(entry.OPERATOR(), ((Scheduler_DDNS) SystemHandler.getScheduler("ddns")).getIPAddress());
				break;
			// ���õ�ַ ǿ�Ƹ��� �ֶ�����
			case "setddns":
				if (message.segment == 2) {
					Module.userInfo(entry.OPERATOR(), ((Scheduler_DDNS) SystemHandler.getScheduler("ddns")).updateDDNSIPAddress());
				} else {
					Module.userInfo(entry.OPERATOR(), ((Scheduler_DDNS) SystemHandler.getScheduler("ddns")).setDDNSIPAddress(message.messages[2]));
				}
				break;
			//
			case "shui":
				if (message.segment == 2) {
					Module.userInfo(entry.OPERATOR(), SystemHandler.getListener("shui").generateReport(true, 1, null));
				} else {
					Module.userInfo(entry.OPERATOR(), SystemHandler.getListener("shui").generateReport(true, 2, null));
				}
				break;
			}
		}
		return true;
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, Message message, int messageid, int messagefont) throws Exception {
		if (userid == entry.OPERATOR()) {

		}
		return true;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, Message message, int messageid, int messagefont) throws Exception {
		if (userid != entry.OPERATOR()) {
			return false;
		}
		if (message.segment < 2) {
			String temp = SystemHandler.genReport(false, 0, null);
			Module.userInfo(entry.OPERATOR(), temp);
			return true;
		} else {
			switch (message.messages[1]) {
			case "shui":
				Module.gropInfo(gropid, SystemHandler.getListener("shui").generateReport(true, 3, new Object[] {
						gropid
				}));
				break;
			}
		}
		return true;
	}

	@Override
	public String generateReport(boolean fullreport, int loglevel, Object[] parameters) {
		return null;
	}
}
