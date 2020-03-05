package studio.blacktech.coolqbot.furryblack.modules.Executor;


import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.annotation.ModuleExecutorComponent;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;


@ModuleExecutorComponent
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
	private static String MODULE_VERSION = "1.3.0";
	private static String[] MODULE_USAGE = new String[] {};
	private static String[] MODULE_PRIVACY_STORED = new String[] {};
	private static String[] MODULE_PRIVACY_CACHED = new String[] {};
	private static String[] MODULE_PRIVACY_OBTAIN = new String[] {};

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================


	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================


	public Executor_admin() throws Exception {
		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}


	@Override
	public boolean init() throws Exception {
		ENABLE_USER = true;
		ENABLE_DISZ = true;
		ENABLE_GROP = true;
		return true;
	}

	@Override
	public boolean boot() throws Exception {
		return true;
	}

	@Override
	public boolean shut() throws Exception {
		return true;
	}

	@Override
	public boolean save() throws Exception {
		return true;
	}


	@Override
	public String[] exec(Message message) throws Exception {
		return new String[] {
				"此模块无可用命令"
		};
	}


	@Override
	public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) {

	}


	@Override
	public void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) {

	}


	@Override
	public boolean doUserMessage(MessageUser message) throws Exception {

		long userid = message.getUserID();

		if (!entry.isAdmin(userid)) {
			entry.userInfo(userid, "你不是我的Master");
			return false;
		}

		if (message.getParameterSection() == 0) {
			entry.adminInfo(entry.getSystemd().generateReport(message));
			return true;
		}


		switch (message.getParameterSegment(0)) {


		case "report":
			entry.adminInfo(entry.getSystemd().reportSpecifiedModule(message));
			break;

		case "reportall":
			entry.adminInfo(entry.getSystemd().reportAllModules(message));
			break;

		case "exec":
			entry.adminInfo(entry.getSystemd().exec(message));
			break;

		case "debug":
			entry.adminInfo(entry.switchDEBUG() ? "DEBUG → Enable" : "DEBUG → Disable");
			break;

		case "friend":
			if (message.getParameterSection() < 2) {
				entry.adminInfo("Exit code → 1 缺少参数");
				return false;
			}
			switch (message.getParameterSegment(1)) {
			case "accept":
				entry.adminInfo("Exit code → " + entry.getCQ().setFriendAddRequest(message.getParameterSegment(2), 1));
				break;

			case "refuse":
				entry.adminInfo("Exit code → " + entry.getCQ().setFriendAddRequest(message.getParameterSegment(2), 2));
				break;
			}
			break;

		case "group":
			if (message.getParameterSection() < 2) {
				entry.adminInfo("Exit code → 1 缺少参数");
				return false;
			}
			switch (message.getParameterSegment(1)) {
			case "accept":
				entry.adminInfo("Exit code → " + entry.getCQ().setGroupAddRequest(message.getParameterSegment(2), 2, 1, null));
				break;

			case "refuse":
				entry.adminInfo("Exit code → " + entry.getCQ().setGroupAddRequest(message.getParameterSegment(2), 2, 2, null));
				break;

			case "leave":
				entry.adminInfo("Exit code → " + entry.getCQ().setGroupLeave(Long.parseLong(message.getParameterSegment(2)), message.getParameterSegment(3) == "true" ? true : false));
				break;
			}
			break;
		}
		return true;

	}


	@Override
	public boolean doDiszMessage(MessageDisz message) throws Exception {

		long diszid = message.getDiszID();
		long userid = message.getUserID();

		if (!entry.isAdmin(userid)) {
			entry.diszInfo(diszid, "你不是我的Master");
			return false;
		}

		return true;

	}


	@Override
	public boolean doGropMessage(MessageGrop message) throws Exception {

		long gropid = message.getGropID();
		long userid = message.getUserID();

		if (!entry.isAdmin(userid)) {
			entry.gropInfo(gropid, "你不是我的Master");
			return false;
		}

		if (message.getParameterSection() == 0) {
			entry.adminInfo(entry.getSystemd().generateReport(message));
			return true;
		}

		switch (message.getParameterSegment(0)) {
		case "report":
			entry.gropInfo(gropid, entry.getSystemd().reportSpecifiedModule(message));
			break;

		case "reportall":
			entry.gropInfo(gropid, entry.getSystemd().reportAllModules(message));
			break;

		case "exec":
			entry.gropInfo(gropid, entry.getSystemd().exec(message));
			break;
		}

		return true;

	}


	// ==========================================================================================================================================================
	//
	// 工具函数
	//
	// ==========================================================================================================================================================


	@Override
	public String[] generateReport(Message message) {
		return new String[0];
	}

}
