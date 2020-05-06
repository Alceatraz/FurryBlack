package studio.blacktech.coolqbot.furryblack.modules.Trigger;


import studio.blacktech.coolqbot.furryblack.common.LoggerX.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.annotation.ModuleTriggerComponent;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleTrigger;
import studio.blacktech.coolqbot.furryblack.entry;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;


@ModuleTriggerComponent
public class Trigger_WordDeny extends ModuleTrigger {

	private static final long serialVersionUID = 1L;

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	private static final String MODULE_PACKAGENAME = "Trigger_WordDeny";
	private static final String MODULE_COMMANDNAME = "worddeny";
	private static final String MODULE_DISPLAYNAME = "过滤器";
	private static final String MODULE_DESCRIPTION = "正则过滤器";
	private static final String MODULE_VERSION = "2.0";
	private static final String[] MODULE_USAGE = new String[] {};
	private static final String[] MODULE_PRIVACY_STORED = new String[] {
			"按照\"成员-消息\"的层级关系记录违反ELUA的行为"
	};
	private static final String[] MODULE_PRIVACY_CACHED = new String[] {};
	private static final String[] MODULE_PRIVACY_OBTAIN = new String[] {};

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================

	private ArrayList<String> BLACKLIST;

	private File FILE_BLACKLIST;
	private File FILE_DENY_USER;
	private File FILE_DENY_DISZ;
	private File FILE_DENY_GROP;

	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================

	public Trigger_WordDeny() throws Exception {
		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}


	@Override
	public boolean init() throws Exception {

		initAppFolder();
		initConfFolder();
		initLogsFolder();

		BLACKLIST = new ArrayList<>();

		if (NEW_CONFIG) {

			CONFIG.setProperty("enable_user", "false");
			CONFIG.setProperty("enable_disz", "false");
			CONFIG.setProperty("enable_grop", "false");

			saveConfig();

		} else {

			loadConfig();

		}

		ENABLE_USER = Boolean.parseBoolean(CONFIG.getProperty("enable_user", "false"));
		ENABLE_DISZ = Boolean.parseBoolean(CONFIG.getProperty("enable_disz", "false"));
		ENABLE_GROP = Boolean.parseBoolean(CONFIG.getProperty("enable_grop", "false"));

		FILE_BLACKLIST = Paths.get(FOLDER_CONF.getAbsolutePath(), "blacklist.txt").toFile();

		FILE_DENY_USER = Paths.get(FOLDER_LOGS.getAbsolutePath(), "denied_user_log.txt").toFile();
		FILE_DENY_DISZ = Paths.get(FOLDER_LOGS.getAbsolutePath(), "denied_disz_log.txt").toFile();
		FILE_DENY_GROP = Paths.get(FOLDER_LOGS.getAbsolutePath(), "denied_grop_log.txt").toFile();

		if (!FILE_BLACKLIST.exists()) { FILE_BLACKLIST.createNewFile(); }
		if (!FILE_DENY_USER.exists()) { FILE_DENY_USER.createNewFile(); }
		if (!FILE_DENY_DISZ.exists()) { FILE_DENY_DISZ.createNewFile(); }
		if (!FILE_DENY_GROP.exists()) { FILE_DENY_GROP.createNewFile(); }

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_BLACKLIST), StandardCharsets.UTF_8));

		String line;

		while ((line = reader.readLine()) != null) {
			if (line.startsWith("#")) continue;
			if (line.contains("#")) line = line.substring(0, line.indexOf("#"));
			BLACKLIST.add(line.trim());
			logger.seek("过滤规则", line);
		}

		reader.close();

		boolean temp = BLACKLIST.size() > 0;

		ENABLE_USER = ENABLE_USER && temp;
		ENABLE_DISZ = ENABLE_DISZ && temp;
		ENABLE_GROP = ENABLE_GROP && temp;
		return true;
	}


	@Override
	public boolean boot() throws Exception {
		return true;
	}


	@Override
	public boolean save() throws Exception {
		return true;
	}


	@Override
	public boolean shut() throws Exception {
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

		for (String temp : BLACKLIST) {

			if (message.getMessage().matches(temp)) {

				FileWriter writer = new FileWriter(FILE_DENY_USER);
				writer.append("[" + LoggerX.datetime(message.getSendtime()) + "] " + entry.getNickname(userid) + "(" + userid + ") " + message.getMessage() + "\n");
				writer.flush();
				writer.close();

				return true;
			}
		}
		return false;
	}

	@Override
	public boolean doDiszMessage(MessageDisz message) throws Exception {

		long diszid = message.getDiszID();
		long userid = message.getUserID();

		for (String temp : BLACKLIST) {

			if (message.getMessage().matches(temp)) {

				FileWriter writer = new FileWriter(FILE_DENY_DISZ, true);
				writer.append("[" + LoggerX.datetime(message.getSendtime()) + "][" + diszid + "]" + entry.getNickname(userid) + "(" + userid + ") " + message.getMessage() + "\n");
				writer.flush();
				writer.close();
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean doGropMessage(MessageGrop message) throws Exception {

		long gropid = message.getGropID();
		long userid = message.getUserID();

		for (String temp : BLACKLIST) {

			if (message.getMessage().matches(temp)) {

				FileWriter writer = new FileWriter(FILE_DENY_DISZ, true);
				writer.append("[" + LoggerX.datetime(message.getSendtime()) + "][" + gropid + "]" + entry.getNickname(userid) + "(" + userid + ") " + message.getMessage() + "\n");
				writer.flush();
				writer.close();
				return true;
			}
		}
		return false;
	}

	@Override
	public String[] generateReport(Message message) {
		return new String[0];
	}
}
