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
	private static String MODULE_VERSION = "11.0";
	private static String[] MODULE_USAGE = new String[] {};
	private static String[] MODULE_PRIVACY_STORED = new String[] {};
	private static String[] MODULE_PRIVACY_CACHED = new String[] {};
	private static String[] MODULE_PRIVACY_OBTAIN = new String[] {};

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================

	public final static String MESSAGE_bootlog = "Logger Level\r\n0 mini - 必须介入的消息\r\n1 info - 需要知晓的消息\r\n2 seek - 自动执行的消息\r\n3 full - 所有消息";
	public final static String MESSAGE_initlvl = "init 0 - 切换起停\r\ninit 1 - 初始化\r\ninit 2 - 启动\r\ninit 3 - 保存\r\ninit 4 - 丢弃关闭\r\ninit 5 - 保存关闭\r\ninit 6 - 保存重启";

	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================

	public Executor_admin() throws Exception {

		// @formatter:off

		super(
				MODULE_PACKAGENAME,
				MODULE_COMMANDNAME,
				MODULE_DISPLAYNAME,
				MODULE_DESCRIPTION,
				MODULE_VERSION,
				MODULE_USAGE,
				MODULE_PRIVACY_STORED,
				MODULE_PRIVACY_CACHED,
				MODULE_PRIVACY_OBTAIN
				);

		// @formatter:on

	}

	@Override
	public LoggerX init(LoggerX logger) throws Exception {

		this.ENABLE_USER = true;
		this.ENABLE_DISZ = true;
		this.ENABLE_GROP = true;

		return logger;
	}

	@Override
	public LoggerX boot(LoggerX logger) throws Exception {
		return logger;
	}

	@Override
	public LoggerX shut(LoggerX logger) throws Exception {
		return logger;
	}

	@Override
	public LoggerX save(LoggerX logger) throws Exception {
		return logger;
	}

	@Override
	public LoggerX exec(LoggerX logger, Message message) throws Exception {
		return logger;
	}

	@Override
	public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) {
	}

	@Override
	public void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) {
	}

	@Override
	public boolean doUserMessage(int typeid, long userid, MessageUser message, int messageid, int messagefont) throws Exception {

		if (entry.isAdmin(userid)) {

			if (message.getSection() == 0) {
				entry.adminInfo(entry.getSystemd().generateReport(0, message, null, null));
			} else {
				switch (message.getSegment(0)) {
				case "report":
					entry.adminInfo(entry.getSystemd().reportSpecifiedModule(0, message, null, null));
					break;
				case "reportall":
					entry.adminInfo(entry.getSystemd().reportAllModules(0, message, null, null));
					break;
				case "exec":
					entry.adminInfo(entry.getSystemd().exec(new LoggerX(), message).make(3));
					break;
				case "debug":
					entry.adminInfo(entry.switchDEBUG() ? "DEBUG → Enable" : "DEBUG → Disable");
					break;
				}
			}

			return true;

		} else {

			entry.userInfo(userid, "你不是我的Master");
			return false;

		}
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, MessageDisz message, int messageid, int messagefont) throws Exception {
		if (entry.isAdmin(userid)) {
			if (message.getSection() == 0) {
				entry.adminInfo(entry.getSystemd().generateReport(0, message, null, null));
			} else {
			}
			return true;
		} else {
			entry.diszInfo(diszid, "你不是我的Master");
			return false;
		}
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, MessageGrop message, int messageid, int messagefont) throws Exception {
		if (entry.isAdmin(userid)) {
			if (message.getSection() == 0) {
				entry.adminInfo(entry.getSystemd().generateReport(0, message, null, null));
			} else {
				switch (message.getSegment(0)) {
				case "report":
					entry.gropInfo(gropid, entry.getSystemd().generateReport(10, message, null, null));
					break;
				case "debug":
					entry.gropInfo(gropid, entry.switchDEBUG() ? "DEBUG → Enable" : "DEBUG → Disable");
					break;
				}
			}
			return true;
		} else {
			entry.gropInfo(gropid, "你不是我的Master");
			return false;
		}
	}

	// ==========================================================================================================================================================
	//
	// 工具函数
	//
	// ==========================================================================================================================================================

	@Override
	public String[] generateReport(int mode, Message message, Object... parameters) {
		return new String[0];
	}

}
