package studio.blacktech.coolqbot.furryblack.plugins;

import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.SystemHandler;
import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.module.Message;
import studio.blacktech.coolqbot.furryblack.module.Module;
import studio.blacktech.coolqbot.furryblack.module.ModuleExecutor;
import studio.blacktech.coolqbot.furryblack.module.ModuleListener;
import studio.blacktech.coolqbot.furryblack.scheduler.Scheduler_DDNS;

public class Executor_admin extends ModuleExecutor {

	public Executor_admin() {

		this.MODULE_DISPLAYNAME = "管理员后台";
		this.MODULE_PACKAGENAME = "admin";
		this.MODULE_DESCRIPTION = "管理员后台";
		this.MODULE_VERSION = "3.1.7";
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
			Module.userInfo(userid, "");
			return false;
		}
		if (message.segment < 2) {
			String temp = SystemHandler.generateFullReport(0, 0, message, null);
			Module.userInfo(entry.OPERATOR(), temp);
			return true;
		} else {
			switch (message.messages[1]) {
			case "getcsrf":
				Module.userInfo(entry.OPERATOR(), "JQCCOOKIE: " + JcqApp.CQ.getCookies());
				Module.userInfo(entry.OPERATOR(), "CSRFTOKEN: " + JcqApp.CQ.getCsrfToken());
				break;

			// 获取地址
			case "getddns":
				Module.userInfo(entry.OPERATOR(), ((Scheduler_DDNS) SystemHandler.getScheduler("ddns")).getIPAddress());
				break;
			// 设置地址 强制更新 手动设置
			case "setddns":
				if (message.segment == 2) {
					Module.userInfo(entry.OPERATOR(), ((Scheduler_DDNS) SystemHandler.getScheduler("ddns")).updateDDNSIPAddress());
				} else {
					Module.userInfo(entry.OPERATOR(), ((Scheduler_DDNS) SystemHandler.getScheduler("ddns")).setDDNSIPAddress(message.messages[2]));
				}
				break;
			//
			case "shui":
				ModuleListener instance = SystemHandler.getListener("shui");
				switch (message.messages[2]) {
				case "dump":
					Module.userInfo(entry.OPERATOR(), instance.generateReport(100, 0, message, null));
					break;
				case "rank":
					// admin shui rank 123123123
					if (message.segment == 4) {
						String temp = message.messages[3];
						long gropid = Long.parseLong(temp);
						Module.userInfo(entry.OPERATOR(), instance.generateReport(1, 0, message, new Object[] {
								gropid
						}));
					} else {
						Module.userInfo(entry.OPERATOR(), instance.generateReport(0, 0, message, null));
					}
					break;
				}
				break;
			default:
				Module.userInfo(entry.OPERATOR(), "找不到命令");
				break;
			}
		}
		return true;

	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, Message message, int messageid, int messagefont) throws Exception {
		if (userid != entry.OPERATOR()) {
			Module.diszInfo(diszid, userid, "你不是我的管理员");
			return false;
		}
		return true;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, Message message, int messageid, int messagefont) throws Exception {
		if (userid != entry.OPERATOR()) {
//			Module.gropInfo(gropid, userid, "你不是我的管理员");
			return false;
		}
		if (message.segment < 2) {
			Module.gropInfo(gropid, SystemHandler.generateFullReport(0, 0, message, null));
			return true;
		} else {
			switch (message.messages[1]) {
			case "shui":
				ModuleListener instance = SystemHandler.getListener("shui");
				switch (message.messages[2]) {
				case "dump":
					Module.userInfo(entry.OPERATOR(), instance.generateReport(100, 0, message, null));
					break;
				case "rank":
					if (message.segment == 4) {
						Module.gropInfo(gropid,instance.generateReport(1, 0, message, new Object[] {
								gropid
						}));
					} else {
						Module.gropInfo(gropid,instance.generateReport(0, 0, message, null));
					}
					break;
				}
			}
		}
		return true;
	}

	@Override
	public String generateReport(int logLevel, int logMode, Message message, Object[] parameters) {
		return null;
	}
}
