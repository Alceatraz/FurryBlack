package studio.blacktech.coolqbot.furryblack.modules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.meowy.cqp.jcq.entity.Group;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.LoggerX.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.module.Module;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleListener;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleTrigger;

public class Module_Message extends Module {

	private static final long serialVersionUID = 1L;

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "Core_Message";
	private static String MODULE_COMMANDNAME = "message";
	private static String MODULE_DISPLAYNAME = "消息广播";
	private static String MODULE_DESCRIPTION = "负责发送所有消息";
	private static String MODULE_VERSION = "8.0";
	private static String[] MODULE_USAGE = new String[] {};
	private static String[] MODULE_PRIVACY_STORED = new String[] {};
	private static String[] MODULE_PRIVACY_CACHED = new String[] {};
	private static String[] MODULE_PRIVACY_OBTAIN = new String[] {};

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================

	private File FILE_MESSAGE_HELP;
	private File FILE_MESSAGE_INFO;
	private File FILE_MESSAGE_EULA;

	private File FILE_SILENCE_GROP;

	private String MESSAGE_HELP = "";
	private String MESSAGE_INFO = "";
	private String MESSAGE_EULA = "";

	private String MESSAGE_LIST_USER = "";
	private String MESSAGE_LIST_DISZ = "";
	private String MESSAGE_LIST_GROP = "";

	private MessageDelegate delegate = new MessageDelegate();

	private HashSet<Long> SILENCE_GROP;
	private TreeMap<Long, LinkedList<Integer>> MESSAGE_HISTORY_GROP;

	private long USERID_CQBOT = 0;
	private long USERID_ADMIN = 0;

	private boolean GEN_LOCK = false;

	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================

	public Module_Message() throws Exception {
		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	@SuppressWarnings("resource")
	@Override
	public void init(LoggerX logger) throws Exception {

		this.initConfFolder();
		this.initCofigurtion();

		this.SILENCE_GROP = new HashSet<>();
		this.MESSAGE_HISTORY_GROP = new TreeMap<>();

		if (this.NEW_CONFIG) {
			logger.seek(MODULE_PACKAGENAME, "配置文件不存在", "生成默认配置");
			this.CONFIG.setProperty("logger_level", "0");
			this.CONFIG.setProperty("userid_admin", "0");
			this.saveConfig();
		} else {
			this.loadConfig();
		}

		this.FILE_MESSAGE_HELP = Paths.get(this.FOLDER_CONF.getAbsolutePath(), "message_help.txt").toFile();
		this.FILE_MESSAGE_INFO = Paths.get(this.FOLDER_CONF.getAbsolutePath(), "message_info.txt").toFile();
		this.FILE_MESSAGE_EULA = Paths.get(this.FOLDER_CONF.getAbsolutePath(), "message_eula.txt").toFile();
		this.FILE_SILENCE_GROP = Paths.get(this.FOLDER_CONF.getAbsolutePath(), "silence_grop.txt").toFile();

		if (!this.FILE_MESSAGE_HELP.exists()) { this.FILE_MESSAGE_HELP.createNewFile(); }
		if (!this.FILE_MESSAGE_INFO.exists()) { this.FILE_MESSAGE_INFO.createNewFile(); }
		if (!this.FILE_MESSAGE_EULA.exists()) { this.FILE_MESSAGE_EULA.createNewFile(); }
		if (!this.FILE_SILENCE_GROP.exists()) { this.FILE_SILENCE_GROP.createNewFile(); }

		BufferedReader readerHelp = new BufferedReader(new InputStreamReader(new FileInputStream(this.FILE_MESSAGE_HELP), StandardCharsets.UTF_8));
		BufferedReader readerInfo = new BufferedReader(new InputStreamReader(new FileInputStream(this.FILE_MESSAGE_INFO), StandardCharsets.UTF_8));
		BufferedReader readerEula = new BufferedReader(new InputStreamReader(new FileInputStream(this.FILE_MESSAGE_EULA), StandardCharsets.UTF_8));
		BufferedReader readerSKIP = new BufferedReader(new InputStreamReader(new FileInputStream(this.FILE_SILENCE_GROP), StandardCharsets.UTF_8));

		String line;

		while ((line = readerHelp.readLine()) != null) {
			this.MESSAGE_HELP = this.MESSAGE_HELP + line + "\r\n";
		}

		while ((line = readerInfo.readLine()) != null) {
			this.MESSAGE_INFO = this.MESSAGE_INFO + line + "\r\n";
		}

		while ((line = readerEula.readLine()) != null) {
			this.MESSAGE_EULA = this.MESSAGE_EULA + line + "\r\n";
		}

		readerHelp.close();
		readerInfo.close();
		readerEula.close();

		this.MESSAGE_HELP = this.MESSAGE_HELP.replaceAll("REPLACE_VERSION", entry.VerID);
		this.MESSAGE_INFO = this.MESSAGE_INFO.replaceAll("REPLACE_VERSION", entry.VerID);
		this.MESSAGE_EULA = this.MESSAGE_EULA.replaceAll("REPLACE_VERSION", entry.VerID);

		this.USERID_CQBOT = entry.getCQ().getLoginQQ();
		this.USERID_ADMIN = Long.parseLong(this.CONFIG.getProperty("userid_admin", "0"));

		if (this.USERID_ADMIN == 0) { throw new Exception("管理员账号配置错误"); }

		logger.seek(MODULE_PACKAGENAME, "机器人账号", this.USERID_CQBOT);
		logger.seek(MODULE_PACKAGENAME, "管理员账号", this.USERID_ADMIN);

		List<Group> groups = entry.getCQ().getGroupList();
		for (Group group : groups) {
			this.MESSAGE_HISTORY_GROP.put(group.getId(), new LinkedList<>());
		}

		while ((line = readerSKIP.readLine()) != null) {
			if (line.startsWith("#")) { continue; }
			this.SILENCE_GROP.add(Long.valueOf(line));
			logger.seek(MODULE_PACKAGENAME, "关闭发言", line);
		}

		readerSKIP.close();
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
	public String[] generateReport(int mode, Message message, Object... parameters) {
		return null;
	}

	private void doAdminInfo(String message) {
		entry.getCQ().sendPrivateMsg(this.USERID_ADMIN, message);
	}

	private void doAdminInfo(String[] message) {
		for (String temp : message) {
			entry.getCQ().sendPrivateMsg(this.USERID_ADMIN, temp);
		}
	}

	private void doUserInfo(long userid, String message) {
		entry.getCQ().sendPrivateMsg(userid, message);
	}

	private void doUserInfo(long userid, String[] message) {
		for (String temp : message) {
			entry.getCQ().sendPrivateMsg(userid, temp);
		}
	}

	private void doDiszInfo(long diszid, String message) {
		entry.getCQ().sendDiscussMsg(diszid, message);
	}

	private void doDiszInfo(long diszid, String[] message) {
		for (String temp : message) {
			entry.getCQ().sendDiscussMsg(diszid, temp);
		}
	}

	private void doDiszInfo(long diszid, long userid, String message) {
		entry.getCQ().sendDiscussMsg(diszid, "[CQ:at,qq=" + userid + "] " + message);
	}

	private void doGropInfo(long gropid, String message) {
		if (this.SILENCE_GROP.contains(gropid)) { return; }
		this.MESSAGE_HISTORY_GROP.get(gropid).add(entry.getCQ().sendGroupMsg(gropid, message));
	}

	private void doGropInfo(long gropid, String[] message) {
		if (this.SILENCE_GROP.contains(gropid)) { return; }
		for (String temp : message) {
			this.MESSAGE_HISTORY_GROP.get(gropid).add(entry.getCQ().sendGroupMsg(gropid, temp));
		}
	}

	private void doGropInfo(long gropid, long userid, String message) {
		if (this.SILENCE_GROP.contains(gropid)) { return; }
		this.MESSAGE_HISTORY_GROP.get(gropid).add(entry.getCQ().sendGroupMsg(gropid, "[CQ:at,qq=" + userid + "] " + message));
	}

	private void doSendInfo(long userid) {
		entry.getCQ().sendPrivateMsg(userid, this.MESSAGE_INFO);
	}

	private void doSendEula(long userid) {
		entry.getCQ().sendPrivateMsg(userid, this.MESSAGE_EULA);
	}

	private void doSendHelp(long userid) {
		entry.getCQ().sendPrivateMsg(userid, this.MESSAGE_HELP);
	}

	private void doSendHelp(long userid, ModuleTrigger module) {
		entry.getCQ().sendPrivateMsg(userid, module.MODULE_FULLHELP());
	}

	private void doSendHelp(long userid, ModuleListener module) {
		entry.getCQ().sendPrivateMsg(userid, module.MODULE_FULLHELP());
	}

	private void doSendHelp(long userid, ModuleExecutor module) {
		entry.getCQ().sendPrivateMsg(userid, module.MODULE_FULLHELP());
	}

	private void doSendListUser(long userid) {
		entry.getCQ().sendPrivateMsg(userid, this.MESSAGE_LIST_USER);
	}

	private void doSendListDisz(long userid) {
		entry.getCQ().sendPrivateMsg(userid, this.MESSAGE_LIST_DISZ);
	}

	private void doSendListGrop(long userid) {
		entry.getCQ().sendPrivateMsg(userid, this.MESSAGE_LIST_GROP);
	}

	private boolean doIsMyself(long userid) {
		return this.USERID_CQBOT == userid;
	}

	private boolean doIsAdmin(long userid) {
		return this.USERID_ADMIN == userid;
	}

	private void doRevokeMessage(long gropid) {
		entry.getCQ().deleteMsg(this.MESSAGE_HISTORY_GROP.get(gropid).pollLast());
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

		StringBuilder builder;

		// =========================================================
		// =========================================================
		// =========================================================

		builder = new StringBuilder();

		builder.append("=================\r\n私聊启用的模块\r\n=================\r\n启用的触发器： ");

		if (TRIGGER_USER.size() == 0) {
			builder.append("无");
		} else {
			builder.append(TRIGGER_USER.size());
			for (ModuleTrigger temp : TRIGGER_USER) {
				builder.append("\r\n");
				builder.append(temp.MODULE_COMMANDNAME());
				builder.append(" > ");
				builder.append(temp.MODULE_DISPLAYNAME());
				builder.append("：");
				builder.append(temp.MODULE_DESCRIPTION());
			}
		}

		builder.append("\r\n=================");
		builder.append("\r\n启用的监听器： ");

		if (LISTENER_USER.size() == 0) {
			builder.append("无");
		} else {
			builder.append(LISTENER_USER.size());
			for (ModuleListener temp : LISTENER_USER) {
				builder.append("\r\n");
				builder.append(temp.MODULE_COMMANDNAME());
				builder.append(" > ");
				builder.append(temp.MODULE_DISPLAYNAME());
				builder.append("：");
				builder.append(temp.MODULE_DESCRIPTION());
			}
		}

		builder.append("\r\n=================");
		builder.append("\r\n可用的执行器： ");

		if (EXECUTOR_USER.size() == 0) {
			builder.append("无");
		} else {
			builder.append(EXECUTOR_USER.size());
			for (String temp : EXECUTOR_USER.keySet()) {
				ModuleExecutor module = EXECUTOR_USER.get(temp);
				module.genFullHelp();
				builder.append("\r\n");
				builder.append(module.MODULE_COMMANDNAME());
				builder.append(" > ");
				builder.append(module.MODULE_DISPLAYNAME());
				builder.append("：");
				builder.append(module.MODULE_DESCRIPTION());
			}
		}

		builder.append("\r\n=================");

		this.MESSAGE_LIST_USER = builder.toString();

		// =========================================================
		// =========================================================
		// =========================================================

		builder = new StringBuilder();

		builder.append("=================\r\n组聊启用的模块\r\n=================\r\n启用的触发器： ");

		if (TRIGGER_DISZ.size() == 0) {
			builder.append("无");
		} else {
			builder.append(TRIGGER_DISZ.size());
			for (ModuleTrigger temp : TRIGGER_DISZ) {
				builder.append("\r\n");
				builder.append(temp.MODULE_COMMANDNAME());
				builder.append(" > ");
				builder.append(temp.MODULE_DISPLAYNAME());
				builder.append("：");
				builder.append(temp.MODULE_DESCRIPTION());
			}
		}

		builder.append("\r\n=================");
		builder.append("\r\n启用的监听器： ");

		if (LISTENER_DISZ.size() == 0) {
			builder.append("无");
		} else {
			builder.append(LISTENER_DISZ.size());
			for (ModuleListener temp : LISTENER_DISZ) {
				builder.append("\r\n");
				builder.append(temp.MODULE_COMMANDNAME());
				builder.append(" > ");
				builder.append(temp.MODULE_DISPLAYNAME());
				builder.append("：");
				builder.append(temp.MODULE_DESCRIPTION());
			}
		}

		builder.append("\r\n=================");
		builder.append("\r\n可用的执行器： ");

		if (EXECUTOR_DISZ.size() == 0) {
			builder.append("无");
		} else {
			builder.append(EXECUTOR_DISZ.size());
			for (String temp : EXECUTOR_DISZ.keySet()) {
				ModuleExecutor module = EXECUTOR_DISZ.get(temp);
				module.genFullHelp();
				builder.append("\r\n");
				builder.append(module.MODULE_COMMANDNAME());
				builder.append(" > ");
				builder.append(module.MODULE_DISPLAYNAME());
				builder.append("：");
				builder.append(module.MODULE_DESCRIPTION());
			}
		}

		builder.append("\r\n=================");

		this.MESSAGE_LIST_DISZ = builder.toString();

		// =========================================================
		// =========================================================
		// =========================================================

		builder = new StringBuilder();

		builder.append("=================\r\n群聊启用的模块\r\n=================\r\n启用的触发器： ");

		if (TRIGGER_GROP.size() == 0) {
			builder.append("无");
		} else {
			builder.append(TRIGGER_GROP.size());
			for (ModuleTrigger temp : TRIGGER_GROP) {
				builder.append("\r\n");
				builder.append(temp.MODULE_COMMANDNAME());
				builder.append(" > ");
				builder.append(temp.MODULE_DISPLAYNAME());
				builder.append("：");
				builder.append(temp.MODULE_DESCRIPTION());
			}
		}

		builder.append("\r\n=================");
		builder.append("\r\n启用的监听器： ");

		if (LISTENER_GROP.size() == 0) {
			builder.append("无");
		} else {
			builder.append(LISTENER_GROP.size());
			for (ModuleListener temp : LISTENER_USER) {
				builder.append("\r\n");
				builder.append(temp.MODULE_COMMANDNAME());
				builder.append(" > ");
				builder.append(temp.MODULE_DISPLAYNAME());
				builder.append("：");
				builder.append(temp.MODULE_DESCRIPTION());
			}
		}

		builder.append("\r\n=================");
		builder.append("\r\n可用的执行器： ");

		if (EXECUTOR_GROP.size() == 0) {
			builder.append("无");
		} else {
			builder.append(EXECUTOR_GROP.size());
			for (String temp : EXECUTOR_GROP.keySet()) {
				ModuleExecutor module = EXECUTOR_GROP.get(temp);
				module.genFullHelp();
				builder.append("\r\n");
				builder.append(module.MODULE_COMMANDNAME());
				builder.append(" > ");
				builder.append(module.MODULE_DISPLAYNAME());
				builder.append("：");
				builder.append(module.MODULE_DESCRIPTION());
			}
		}

		builder.append("\r\n=================");

		this.MESSAGE_LIST_GROP = builder.toString();

	}

	public MessageDelegate getDelegate() {
		return this.delegate;
	}

	public class MessageDelegate {

		public void adminInfo(String message) {
			Module_Message.this.doAdminInfo(message);
		}

		public void adminInfo(String[] message) {
			Module_Message.this.doAdminInfo(message);
		}

		public void userInfo(long userid, String message) {
			Module_Message.this.doUserInfo(userid, message);
		}

		public void userInfo(long userid, String[] message) {
			Module_Message.this.doUserInfo(userid, message);
		}

		public void diszInfo(long diszid, String message) {
			Module_Message.this.doDiszInfo(diszid, message);
		}

		public void diszInfo(long diszid, String[] message) {
			Module_Message.this.doDiszInfo(diszid, message);
		}

		public void diszInfo(long diszid, long userid, String message) {
			Module_Message.this.doDiszInfo(diszid, userid, message);
		}

		public void gropInfo(long gropid, String message) {
			Module_Message.this.doGropInfo(gropid, message);
		}

		public void gropInfo(long gropid, String[] message) {
			Module_Message.this.doGropInfo(gropid, message);
		}

		public void gropInfo(long gropid, long userid, String message) {
			Module_Message.this.doGropInfo(gropid, userid, message);
		}

		public void sendInfo(long userid) {
			Module_Message.this.doSendInfo(userid);
		}

		public void sendEula(long userid) {
			Module_Message.this.doSendEula(userid);
		}

		public void sendHelp(long userid) {
			Module_Message.this.doSendHelp(userid);
		}

		public void sendHelp(long userid, ModuleTrigger module) {
			Module_Message.this.doSendHelp(userid, module);
		}

		public void sendHelp(long userid, ModuleListener module) {
			Module_Message.this.doSendHelp(userid, module);
		}

		public void sendHelp(long userid, ModuleExecutor module) {
			Module_Message.this.doSendHelp(userid, module);
		}

		public void sendListUser(long userid) {
			Module_Message.this.doSendListUser(userid);
		}

		public void sendListDisz(long userid) {
			Module_Message.this.doSendListDisz(userid);
		}

		public void sendListGrop(long userid) {
			Module_Message.this.doSendListGrop(userid);
		}

		public boolean isMyself(long userid) {
			return Module_Message.this.doIsMyself(userid);
		}

		public boolean isAdmin(long userid) {
			return Module_Message.this.doIsAdmin(userid);
		}

		public void revokeMessage(long gropid) {
			Module_Message.this.doRevokeMessage(gropid);
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
			Module_Message.this.doGenetateList(TRIGGER_USER, TRIGGER_DISZ, TRIGGER_GROP, LISTENER_USER, LISTENER_DISZ, LISTENER_GROP, EXECUTOR_USER, EXECUTOR_DISZ, EXECUTOR_GROP);
		}

	}
}
