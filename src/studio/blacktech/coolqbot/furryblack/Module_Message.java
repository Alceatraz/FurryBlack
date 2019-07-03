package studio.blacktech.coolqbot.furryblack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.TreeMap;

import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.common.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.module.Module;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleListener;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleTrigger;

public class Module_Message extends Module {

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	public static String MODULE_PACKAGENAME = "message";
	public static String MODULE_DISPLAYNAME = "消息路由";
	public static String MODULE_DESCRIPTION = "消息路由";
	public static String MODULE_VERSION = "1.0";
	public static String[] MODULE_USAGE = new String[] {};
	public static String[] MODULE_PRIVACY_TRIGER = new String[] {};
	public static String[] MODULE_PRIVACY_LISTEN = new String[] {};
	public static String[] MODULE_PRIVACY_STORED = new String[] {};
	public static String[] MODULE_PRIVACY_CACHED = new String[] {};
	public static String[] MODULE_PRIVACY_OBTAIN = new String[] {};

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================

	private File FILE_MESSAGE_HELP;
	private File FILE_MESSAGE_INFO;
	private File FILE_MESSAGE_EULA;

	private String MESSAGE_HELP;
	private String MESSAGE_INFO;
	private String MESSAGE_EULA;

	private String MESSAGE_LIST_USER;
	private String MESSAGE_LIST_DISZ;
	private String MESSAGE_LIST_GROP;

	private MessageDelegate delegate = new MessageDelegate();

	private long USERID_CQBOT = 0;
	private long USERID_ADMIN = 0;

	private boolean GEN_LOCK = false;

	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================

	public Module_Message() throws Exception {
		super(MODULE_PACKAGENAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_TRIGER, MODULE_PRIVACY_LISTEN, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	@Override
	public void init(LoggerX logger) throws Exception {

		if (this.NEW_CONFIG) {
			logger.seek("[Message] 配置文件不存在 - 生成默认配置");
			this.CONFIG.setProperty("logger_level", "0");
			this.CONFIG.setProperty("userid_admin", "0");
			this.saveConfig();
			throw new Exception("[Message] 生成配置文件 自动停机");
		} else {
			this.loadConfig();
		}

		this.FILE_MESSAGE_HELP = Paths.get(this.FOLDER_CONF.getAbsolutePath(), "message_help.txt").toFile();
		this.FILE_MESSAGE_INFO = Paths.get(this.FOLDER_CONF.getAbsolutePath(), "message_info.txt").toFile();
		this.FILE_MESSAGE_EULA = Paths.get(this.FOLDER_CONF.getAbsolutePath(), "message_eula.txt").toFile();

		if (!this.FILE_MESSAGE_HELP.exists()) { this.FILE_MESSAGE_HELP.createNewFile(); }
		if (!this.FILE_MESSAGE_INFO.exists()) { this.FILE_MESSAGE_INFO.createNewFile(); }
		if (!this.FILE_MESSAGE_EULA.exists()) { this.FILE_MESSAGE_EULA.createNewFile(); }

		BufferedReader readerHelp = new BufferedReader(new InputStreamReader(new FileInputStream(this.FILE_MESSAGE_HELP), "UTF-8"));
		BufferedReader readerInfo = new BufferedReader(new InputStreamReader(new FileInputStream(this.FILE_MESSAGE_INFO), "UTF-8"));
		BufferedReader readerEula = new BufferedReader(new InputStreamReader(new FileInputStream(this.FILE_MESSAGE_EULA), "UTF-8"));

		String line;

		while ((line = readerHelp.readLine()) != null) {
			this.MESSAGE_HELP = this.MESSAGE_HELP + line;
		}

		while ((line = readerHelp.readLine()) != null) {
			this.MESSAGE_INFO = this.MESSAGE_INFO + line;
		}

		while ((line = readerHelp.readLine()) != null) {
			this.MESSAGE_EULA = this.MESSAGE_EULA + line;
		}

		readerHelp.close();
		readerInfo.close();
		readerEula.close();

		this.MESSAGE_HELP.replace("REPLACE_VERSION", entry.VerID);
		this.MESSAGE_INFO.replace("REPLACE_VERSION", entry.VerID);
		this.MESSAGE_EULA.replace("REPLACE_VERSION", entry.VerID);

		this.USERID_CQBOT = JcqApp.CQ.getLoginQQ();
		this.USERID_ADMIN = Long.parseLong(this.CONFIG.getProperty("userid_admin", "0"));

		if (this.USERID_ADMIN == 0) { throw new Exception("管理员账号配置错误"); }

		logger.full("[CORE] 机器人账号：", this.USERID_CQBOT);
		logger.full("[CORE] 管理员账号：", this.USERID_ADMIN);

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
	public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) throws Exception {
	}

	@Override
	public void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) throws Exception {
	}

	// ==========================================================================================================================================================
	//
	// 工具函数
	//
	// ==========================================================================================================================================================

	@Override
	public String[] generateReport(int mode, Message message, Object... parameters) throws Exception {
		return null;
	}

	public void doAdminInfo(final String message) {
		JcqApp.CQ.sendPrivateMsg(this.USERID_ADMIN, message);
	}

	public void doUserInfo(final long userid, final String message) {
		JcqApp.CQ.sendPrivateMsg(userid, message);
	}

	public void doDiszInfo(final long diszid, final String message) {
		JcqApp.CQ.sendDiscussMsg(diszid, message);
	}

	public void doDiszInfo(final long diszid, final long userid, final String message) {
		JcqApp.CQ.sendDiscussMsg(diszid, "[CQ:at,qq=" + userid + "] " + message);
	}

	public void doGropInfo(final long gropid, final String message) {
		JcqApp.CQ.sendGroupMsg(gropid, message);
	}

	public void doGropInfo(final long gropid, final long userid, final String message) {
		JcqApp.CQ.sendGroupMsg(gropid, "[CQ:at,qq=" + userid + "] " + message);
	}

	public void doSendInfo(final long userid) {
		JcqApp.CQ.sendPrivateMsg(userid, this.MESSAGE_INFO);
	}

	public void doSendEula(final long userid) {
		JcqApp.CQ.sendPrivateMsg(userid, this.MESSAGE_EULA);
	}

	public void doSendHelp(final long userid) {
		JcqApp.CQ.sendPrivateMsg(userid, this.MESSAGE_HELP);
	}

	public void doSendHelp(final long userid, ModuleTrigger module) {
		JcqApp.CQ.sendPrivateMsg(userid, module.MODULE_FULLHELP());
	}

	public void doSendHelp(final long userid, ModuleListener module) {
		JcqApp.CQ.sendPrivateMsg(userid, module.MODULE_FULLHELP());
	}

	public void doSendHelp(final long userid, ModuleExecutor module) {
		JcqApp.CQ.sendPrivateMsg(userid, module.MODULE_FULLHELP());
	}

	public void doSendListUser(final long userid) {
		JcqApp.CQ.sendPrivateMsg(userid, this.MESSAGE_LIST_USER);
	}

	public void doSendListDisz(final long userid) {
		JcqApp.CQ.sendPrivateMsg(userid, this.MESSAGE_LIST_DISZ);
	}

	public void doSendListGrop(final long userid) {
		JcqApp.CQ.sendPrivateMsg(userid, this.MESSAGE_LIST_GROP);
	}

	public boolean doIsMyself(long userid) {
		return this.USERID_CQBOT == userid;
	}

	public boolean doIsAdmin(long userid) {
		return this.USERID_ADMIN == userid;
	}

	public void doGenetateList(
	// @formatter:off
		ArrayList<ModuleTrigger> TRIGGER_USER,
		ArrayList<ModuleTrigger> TRIGGER_DISZ,
		ArrayList<ModuleTrigger> TRIGGER_GROP,
		ArrayList<ModuleListener> LISTENER_USER,
		ArrayList<ModuleListener> LISTENER_DISZ,
		ArrayList<ModuleListener> LISTENER_GROP,
		TreeMap<String, ModuleExecutor> EXECUTOR_USER,
		TreeMap<String, ModuleExecutor> EXECUTOR_DISZ,
		TreeMap<String, ModuleExecutor> EXECUTOR_GROP
	// @formatter:on
	) {
		if (this.GEN_LOCK) { return; }

		StringBuilder preBuilder = new StringBuilder();
		preBuilder.append("\r\n已经安装的触发器： ");
		preBuilder.append(TRIGGER_USER.size());
		for (final ModuleTrigger temp : TRIGGER_USER) {
			preBuilder.append("\r\n");
			preBuilder.append(temp.MODULE_PACKAGENAME());
			preBuilder.append(" > ");
			preBuilder.append(temp.MODULE_DISPLAYNAME());
			preBuilder.append(" : ");
			preBuilder.append(temp.MODULE_DESCRIPTION());
		}
		preBuilder.append("\r\n已经安装的监听器： ");
		preBuilder.append(LISTENER_USER.size());
		for (final ModuleListener temp : LISTENER_USER) {
			preBuilder.append("\r\n");
			preBuilder.append(temp.MODULE_PACKAGENAME());
			preBuilder.append(" > ");
			preBuilder.append(temp.MODULE_DISPLAYNAME());
			preBuilder.append(" : ");
			preBuilder.append(temp.MODULE_DESCRIPTION());
		}
		preBuilder.append("已经安装的执行器： ");
		preBuilder.append(EXECUTOR_USER.size());
		for (final String temp : EXECUTOR_USER.keySet()) {
			final ModuleExecutor module = EXECUTOR_USER.get(temp);
			module.genFullHelp();
			preBuilder.append("\r\n");
			preBuilder.append(module.MODULE_PACKAGENAME());
			preBuilder.append(" > ");
			preBuilder.append(module.MODULE_DISPLAYNAME());
			preBuilder.append(" : ");
			preBuilder.append(module.MODULE_DESCRIPTION());
		}
		this.MESSAGE_LIST_USER = preBuilder.toString();
		preBuilder = new StringBuilder();
		preBuilder.append("\r\n已经安装的触发器： ");
		preBuilder.append(TRIGGER_DISZ.size());
		for (final ModuleTrigger temp : TRIGGER_DISZ) {
			preBuilder.append("\r\n");
			preBuilder.append(temp.MODULE_PACKAGENAME());
			preBuilder.append(" > ");
			preBuilder.append(temp.MODULE_DISPLAYNAME());
			preBuilder.append(" : ");
			preBuilder.append(temp.MODULE_DESCRIPTION());
		}
		preBuilder.append("\r\n已经安装的监听器： ");
		preBuilder.append(LISTENER_DISZ.size());
		for (final ModuleListener temp : LISTENER_DISZ) {
			preBuilder.append("\r\n");
			preBuilder.append(temp.MODULE_PACKAGENAME());
			preBuilder.append(" > ");
			preBuilder.append(temp.MODULE_DISPLAYNAME());
			preBuilder.append(" : ");
			preBuilder.append(temp.MODULE_DESCRIPTION());
		}
		preBuilder.append("已经安装的执行器： ");
		preBuilder.append(EXECUTOR_DISZ.size());
		for (final String temp : EXECUTOR_DISZ.keySet()) {
			final ModuleExecutor module = EXECUTOR_DISZ.get(temp);
			module.genFullHelp();
			preBuilder.append("\r\n");
			preBuilder.append(module.MODULE_PACKAGENAME());
			preBuilder.append(" > ");
			preBuilder.append(module.MODULE_DISPLAYNAME());
			preBuilder.append(" : ");
			preBuilder.append(module.MODULE_DESCRIPTION());
		}
		this.MESSAGE_LIST_DISZ = preBuilder.toString();
		preBuilder = new StringBuilder();
		preBuilder.append("\r\n已经安装的触发器： ");
		preBuilder.append(TRIGGER_GROP.size());
		for (final ModuleTrigger temp : TRIGGER_GROP) {
			preBuilder.append("\r\n");
			preBuilder.append(temp.MODULE_PACKAGENAME());
			preBuilder.append(" > ");
			preBuilder.append(temp.MODULE_DISPLAYNAME());
			preBuilder.append(" : ");
			preBuilder.append(temp.MODULE_DESCRIPTION());
		}
		preBuilder.append("\r\n已经安装的监听器： ");
		preBuilder.append(LISTENER_GROP.size());
		for (final ModuleListener temp : LISTENER_GROP) {
			preBuilder.append("\r\n");
			preBuilder.append(temp.MODULE_PACKAGENAME());
			preBuilder.append(" > ");
			preBuilder.append(temp.MODULE_DISPLAYNAME());
			preBuilder.append(" : ");
			preBuilder.append(temp.MODULE_DESCRIPTION());
		}
		preBuilder.append("已经安装的执行器： ");
		preBuilder.append(EXECUTOR_GROP.size());
		for (final String temp : EXECUTOR_GROP.keySet()) {
			final ModuleExecutor module = EXECUTOR_GROP.get(temp);
			module.genFullHelp();
			preBuilder.append("\r\n");
			preBuilder.append(module.MODULE_PACKAGENAME());
			preBuilder.append(" > ");
			preBuilder.append(module.MODULE_DISPLAYNAME());
			preBuilder.append(" : ");
			preBuilder.append(module.MODULE_DESCRIPTION());
		}
		this.MESSAGE_LIST_GROP = preBuilder.toString();
	}

	public MessageDelegate getDelegate() {
		return this.delegate;
	}

	public class MessageDelegate {

		public void adminInfo(final String message) {
			doAdminInfo(message);
		}

		public void userInfo(long userid, String message) {
			doUserInfo(userid, message);
		}

		public void diszInfo(long diszid, String message) {
			doDiszInfo(diszid, message);
		}

		public void diszInfo(long diszid, long userid, String message) {
			doDiszInfo(diszid, userid, message);
		}

		public void gropInfo(long gropid, String message) {
			doGropInfo(gropid, message);
		}

		public void gropInfo(long gropid, long userid, String message) {
			doGropInfo(gropid, userid, message);
		}

		public void sendInfo(final long userid) {
			doSendInfo(userid);
		}

		public void sendEula(final long userid) {
			doSendEula(userid);
		}

		public void sendHelp(final long userid) {
			doSendHelp(userid);
		}

		public void sendHelp(long userid, ModuleTrigger module) {
			doSendHelp(userid, module);
		}

		public void sendHelp(long userid, ModuleListener module) {
			doSendHelp(userid, module);
		}

		public void sendHelp(long userid, ModuleExecutor module) {
			doSendHelp(userid, module);
		}

		public void sendListUser(final long userid) {
			doSendListUser(userid);
		}

		public void sendListDisz(final long userid) {
			doSendListDisz(userid);
		}

		public void sendListGrop(final long userid) {
			doSendListGrop(userid);
		}

		public boolean isMyself(long userid) {
			return doIsMyself(userid);
		}

		public boolean isAdmin(long userid) {
			return doIsAdmin(userid);
		}

		public void genetateList(
		// 	@formatter:off
			ArrayList<ModuleTrigger> TRIGGER_USER,
			ArrayList<ModuleTrigger> TRIGGER_DISZ,
			ArrayList<ModuleTrigger> TRIGGER_GROP,
			ArrayList<ModuleListener> LISTENER_USER,
			ArrayList<ModuleListener> LISTENER_DISZ,
			ArrayList<ModuleListener> LISTENER_GROP,
			TreeMap<String, ModuleExecutor> EXECUTOR_USER,
			TreeMap<String, ModuleExecutor> EXECUTOR_DISZ,
			TreeMap<String, ModuleExecutor> EXECUTOR_GROP
			// @formatter:on
		) {
			doGenetateList(TRIGGER_USER, TRIGGER_DISZ, TRIGGER_GROP, LISTENER_USER, LISTENER_DISZ, LISTENER_GROP, EXECUTOR_USER, EXECUTOR_DISZ, EXECUTOR_GROP);
		}
	}
}
