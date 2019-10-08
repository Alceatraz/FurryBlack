package studio.blacktech.coolqbot.furryblack.modules.Executor;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.LoggerX.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;

public class Executor_admin extends ModuleExecutor {

	private static final long serialVersionUID = 1L;

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "Executor_Admin";
	private static String MODULE_COMMANDNAME = "admin";
	private static String MODULE_DISPLAYNAME = "管理工具";
	private static String MODULE_DESCRIPTION = "管理员控制台";
	private static String MODULE_VERSION = "1.0";
	private static String[] MODULE_USAGE = new String[] {};
	private static String[] MODULE_PRIVACY_STORED = new String[] {};
	private static String[] MODULE_PRIVACY_CACHED = new String[] {};
	private static String[] MODULE_PRIVACY_OBTAIN = new String[] {};

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================

	public Executor_admin() throws Exception {
		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	@Override
	public void init(LoggerX logger) throws Exception {
		this.ENABLE_USER = true;
		this.ENABLE_DISZ = true;
		this.ENABLE_GROP = true;
	}

	@Override
	public void boot(LoggerX logger) throws Exception {

	}

	@Override
	public void shut(LoggerX logger) throws Exception {
	}

	@Override
	public void save(LoggerX logger) throws Exception {
	}

	@Override
	public void reload(LoggerX logger) throws Exception {
	}

	@Override
	public void exec(LoggerX logger, Message message) throws Exception {
	}

	@Override
	public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) {
	}

	@Override
	public void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) {
	}

	private final String MESSAGE_bootlog = "Logger Level\r\n0 mini - 必须介入的消息\r\n1 info - 需要知晓的消息\r\n2 seek - 自动执行的消息\r\n3 full - 所有消息";
	private final String MESSAGE_init = "init 0 - 切换起停\r\ninit 1 - 初始化\r\ninit 2 - 启动\r\ninit 3 - 保存\r\ninit 4 - 丢弃关闭\r\ninit 5 - 保存关闭\r\ninit 6 - 保存重启";

	@Override
	public boolean doUserMessage(int typeid, long userid, MessageUser message, int messageid, int messagefont) throws Exception {
		if (!entry.getMessage().isAdmin(userid)) { return false; }

		if (message.getSection() == 0) {
			entry.getSystemd().sendSystemsReport(0, 0);
			return true;
		} else {
			switch (message.getSegment()[0]) {
			case "bootlog":
				if (message.getSection() == 2) {
					entry.getMessage().adminInfo(entry.getBootLogger(Integer.parseInt(message.getSegment(1))));
				} else {
					entry.getMessage().adminInfo(this.MESSAGE_bootlog);
				}
				return true;
			case "exec":
				entry.getMessage().adminInfo(entry.getSystemd().exec(message.toMessage()).make(3));
				return true;
			case "init":
				if (message.getSection() == 2) {
					entry.getMessage().adminInfo(entry.getSystemd().init(message.getSegment(1)).make(3));
				} else {
					entry.getMessage().adminInfo(this.MESSAGE_init);
				}
				return true;
			case "debug":
				String temp = entry.switchDEBUG() ? "ENABLE" : "DISABLE";
				entry.getMessage().adminInfo("DEBUG → " + temp);
				return true;
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
			entry.getSystemd().sendSystemsReport(3, gropid);
			return true;
		} else {

			switch (message.getSegment()[0]) {

			case "bootlog":
				if (message.getSection() == 2) {
					entry.getMessage().gropInfo(gropid, entry.getBootLogger(Integer.parseInt(message.getSegment(1))));
				} else {
					entry.getMessage().gropInfo(gropid, this.MESSAGE_bootlog);
				}
				return true;
			case "init":
				if (message.getSection() == 2) {
					entry.getMessage().gropInfo(gropid, entry.getSystemd().init(message.getSegment(1)).make(3));
				} else {
					entry.getMessage().gropInfo(gropid, this.MESSAGE_init);
				}
				return true;
			case "debug":
				String temp = entry.switchDEBUG() ? "ENABLE" : "DISABLE";
				entry.getMessage().gropInfo(gropid, "DEBUG → " + temp);
				return true;
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
