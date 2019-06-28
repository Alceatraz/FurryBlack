package studio.blacktech.coolqbot.furryblack.modules;

import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.SystemHandler;
import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.Message;
import studio.blacktech.coolqbot.furryblack.common.Module;
import studio.blacktech.coolqbot.furryblack.common.ModuleExecutor;
import studio.blacktech.coolqbot.furryblack.common.ModuleListener;

public class Executor_admin extends ModuleExecutor {

	public Executor_admin() {

		this.MODULE_PACKAGENAME = "admin";
		this.MODULE_DISPLAYNAME = "Sakurga";
		this.MODULE_DESCRIPTION = "Lv:80";
		this.MODULE_VERSION = "??rZVxx";
		this.MODULE_USAGE = new String[] {};
		this.MODULE_PRIVACY_TRIGER = new String[] {
				"~~?C???r Pz>\\???"
		};
		this.MODULE_PRIVACY_LISTEN = new String[] {
				"~ZT\\C?r Rr>:!??"
		};
		this.MODULE_PRIVACY_STORED = new String[] {
				"~~\\T+??r Pz>]]??"
		};
		this.MODULE_PRIVACY_CACHED = new String[] {
				"~~\\C???r Fy:????"
		};
		this.MODULE_PRIVACY_OBTAIN = new String[] {
				".?-\\C??r Pt=??!#"
		};
	}

	@Override
	public void memberExit(long gropid, long userid) {
	}

	@Override
	public void memberJoin(long gropid, long userid) {
	}

	@Override
	public String[] generateReport(final int logLevel, final int logMode, final int typeid, final long userid, final long diszid, final long gropid, final Message message, final Object... parameters) {
		return null;
	}

	@Override
	public boolean doUserMessage(final int typeid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		if (userid != entry.OPERATOR()) {
			Module.userInfo(userid, "");
			return false;
		}
		if (message.segment < 2) {
			final String temp = SystemHandler.generateFullReport(0, 0, 0, 0, 0, 0, message, null);
			Module.userInfo(entry.OPERATOR(), temp);
			return true;
		} else {
			switch (message.messages[1]) {
			// 获取Cookie和Token
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
			case "say":
				if (message.segment == 2) {
					Module.userInfo(entry.OPERATOR(), "//admin say <GroupID> XXX XXX XXX");
				} else {
					Module.gropInfo(Long.parseLong(message.messages[3]), message.join(4));
				}
				break;
			//
			case "gc":
				System.gc();
				break;
			//
			case "nick":
				switch (message.messages[2]) {
				case "load":
					Module_Nick.loadMember();
					break;
				case "dump":
					Module_Nick.dumpMember();
					break;
				case "add":
					Module_Nick.addMember(message.messages[3], message.messages[4]);
					break;
				}
				break;
			//
			case "shui":
				final ModuleListener instance = SystemHandler.getListener("shui");
				switch (message.messages[2]) {
				case "dump":
					instance.generateReport(100, 0, 0, 0, 0, 0, message, null);
					break;
				case "rank":
					if (message.segment == 4) {
						final String temp = message.messages[3];
						instance.generateReport(1, 0, 0, 0, 0, Long.parseLong(temp), message, null);
					} else {
						instance.generateReport(0, 0, 0, 0, 0, 0, message, null);
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
	public boolean doDiszMessage(final long diszid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		if (userid != entry.OPERATOR()) {
			Module.diszInfo(diszid, userid, "你不是我的管理员");
			return false;
		}
		return true;
	}

	@Override
	public boolean doGropMessage(final long gropid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		if (userid != entry.OPERATOR()) { return false; }
		if (message.segment < 2) {
			Module.gropInfo(gropid, SystemHandler.generateFullReport(0, 0, 0, 0, 0, 0, message, null));
			return true;
		} else {
			switch (message.messages[1]) {
			case "shui":
				final ModuleListener instance = SystemHandler.getListener("shui");
				instance.generateReport(1, 1, 0, 0, 0, gropid, message, null);
			}
		}
		return true;
	}

}
