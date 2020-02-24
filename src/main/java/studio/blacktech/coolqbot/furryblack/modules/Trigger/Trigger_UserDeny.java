package studio.blacktech.coolqbot.furryblack.modules.Trigger;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.TreeMap;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.LoggerX.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.annotation.ModuleTriggerComponent;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleTrigger;


@ModuleTriggerComponent
public class Trigger_UserDeny extends ModuleTrigger {

	private static final long serialVersionUID = 1L;

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "Trigger_UserDeny";
	private static String MODULE_COMMANDNAME = "userdeny";
	private static String MODULE_DISPLAYNAME = "过滤器";
	private static String MODULE_DESCRIPTION = "用户过滤器";
	private static String MODULE_VERSION = "2.0";
	private static String[] MODULE_USAGE = new String[] {};
	private static String[] MODULE_PRIVACY_STORED = new String[] {
			"按照\"群-成员\"的层级关系手动配置被阻止的用户"
	};
	private static String[] MODULE_PRIVACY_CACHED = new String[] {};
	private static String[] MODULE_PRIVACY_OBTAIN = new String[] {};

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================

	private HashSet<Long> GLOBAL_USER_IGNORE;
	private HashSet<Long> GLOBAL_DISZ_IGNORE;
	private HashSet<Long> GLOBAL_GROP_IGNORE;

	private TreeMap<Long, HashSet<Long>> DISZ_MEMBER_IGNORE;
	private TreeMap<Long, HashSet<Long>> GROP_MEMBER_IGNORE;

	private File FILE_USERIGNORE;
	private File FILE_DISZIGNORE;
	private File FILE_GROPIGNORE;

	private File FILE_DENY_USER_LOGGER;
	private File FILE_DENY_DISZ_LOGGER;
	private File FILE_DENY_GROP_LOGGER;

	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================

	public Trigger_UserDeny() throws Exception {

		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);

	}

	@Override
	public boolean init() throws Exception {

		initAppFolder();
		initConfFolder();
		initLogsFolder();

		GLOBAL_USER_IGNORE = new HashSet<>();
		GLOBAL_DISZ_IGNORE = new HashSet<>();
		GLOBAL_GROP_IGNORE = new HashSet<>();

		DISZ_MEMBER_IGNORE = new TreeMap<>();
		GROP_MEMBER_IGNORE = new TreeMap<>();

		if (NEW_CONFIG) {

			CONFIG.setProperty("enable_user", "false");
			CONFIG.setProperty("enable_disz", "false");
			CONFIG.setProperty("enable_grop", "false");
			saveConfig();

		} else {

			loadConfig();

		}

		FILE_USERIGNORE = Paths.get(FOLDER_CONF.getAbsolutePath(), "ignore_user.txt").toFile();
		FILE_DISZIGNORE = Paths.get(FOLDER_CONF.getAbsolutePath(), "ignore_disz.txt").toFile();
		FILE_GROPIGNORE = Paths.get(FOLDER_CONF.getAbsolutePath(), "ignore_grop.txt").toFile();

		FILE_DENY_USER_LOGGER = Paths.get(FOLDER_LOGS.getAbsolutePath(), "ignore_user_log.txt").toFile();
		FILE_DENY_DISZ_LOGGER = Paths.get(FOLDER_LOGS.getAbsolutePath(), "ignore_disz_log.txt").toFile();
		FILE_DENY_GROP_LOGGER = Paths.get(FOLDER_LOGS.getAbsolutePath(), "ignore_grop_log.txt").toFile();

		if (!FILE_USERIGNORE.exists()) { FILE_USERIGNORE.createNewFile(); }
		if (!FILE_DISZIGNORE.exists()) { FILE_DISZIGNORE.createNewFile(); }
		if (!FILE_GROPIGNORE.exists()) { FILE_GROPIGNORE.createNewFile(); }

		if (!FILE_DENY_USER_LOGGER.exists()) { FILE_DENY_USER_LOGGER.createNewFile(); }
		if (!FILE_DENY_DISZ_LOGGER.exists()) { FILE_DENY_DISZ_LOGGER.createNewFile(); }
		if (!FILE_DENY_GROP_LOGGER.exists()) { FILE_DENY_GROP_LOGGER.createNewFile(); }

		ENABLE_USER = Boolean.parseBoolean(CONFIG.getProperty("enable_user", "false"));
		ENABLE_DISZ = Boolean.parseBoolean(CONFIG.getProperty("enable_disz", "false"));
		ENABLE_GROP = Boolean.parseBoolean(CONFIG.getProperty("enable_grop", "false"));

		BufferedReader readerUser = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_USERIGNORE), StandardCharsets.UTF_8));
		BufferedReader readerDisz = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_DISZIGNORE), StandardCharsets.UTF_8));
		BufferedReader readerGrop = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_GROPIGNORE), StandardCharsets.UTF_8));

		long userid;
		long diszid;
		long gropid;

		String line;
		String[] temp;

		while ((line = readerUser.readLine()) != null) {

			if (line.startsWith("#")) { continue; }
			if (line.contains("#")) { line = line.substring(0, line.indexOf("#")).trim(); }

			GLOBAL_USER_IGNORE.add(Long.parseLong(line));
			logger.seek("全局屏蔽", line);
		}

		while ((line = readerDisz.readLine()) != null) {

			if (line.startsWith("#")) { continue; }
			if (!line.contains(":")) { continue; }
			if (line.contains("#")) { line = line.substring(0, line.indexOf("#")).trim(); }

			temp = line.split(":");

			diszid = Long.parseLong(temp[0]);
			userid = Long.parseLong(temp[1]);

			if (userid == 0) {
				GLOBAL_DISZ_IGNORE.add(diszid);
			} else {
				if (!DISZ_MEMBER_IGNORE.containsKey(diszid)) {
					HashSet<Long> tempSet = new HashSet<>();
					DISZ_MEMBER_IGNORE.put(diszid, tempSet);
				}
				DISZ_MEMBER_IGNORE.get(diszid).add(userid);
			}

			logger.seek("指定组聊", line);
		}

		while ((line = readerGrop.readLine()) != null) {

			if (line.startsWith("#")) { continue; }
			if (!line.contains(":")) { continue; }
			if (line.contains("#")) { line = line.substring(0, line.indexOf("#")).trim(); }

			temp = line.split(":");
			temp = line.split(":");

			gropid = Long.parseLong(temp[0]);
			userid = Long.parseLong(temp[1]);

			if (userid == 0) {
				GLOBAL_GROP_IGNORE.add(gropid);
			} else {
				if (!GROP_MEMBER_IGNORE.containsKey(gropid)) {
					HashSet<Long> tempSet = new HashSet<>();
					GROP_MEMBER_IGNORE.put(gropid, tempSet);
				}
				GROP_MEMBER_IGNORE.get(gropid).add(userid);
			}

			logger.seek("指定群聊", line);

		}
		readerUser.close();
		readerDisz.close();
		readerGrop.close();

		ENABLE_USER = ENABLE_USER && GLOBAL_USER_IGNORE.size() > 0;
		ENABLE_DISZ = ENABLE_USER || ENABLE_DISZ && GLOBAL_DISZ_IGNORE.size() + DISZ_MEMBER_IGNORE.size() > 0;
		ENABLE_GROP = ENABLE_USER || ENABLE_GROP && GLOBAL_GROP_IGNORE.size() + GROP_MEMBER_IGNORE.size() > 0;

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

		if (GLOBAL_USER_IGNORE.contains(userid)) {

			FileWriter writer = new FileWriter(FILE_DENY_USER_LOGGER);
			writer.append("[" + LoggerX.datetime() + "] 某人" + entry.getNickname(userid) + "(" + userid + ") " + message.getMessage() + "\n");
			writer.flush();
			writer.close();

		} else {
			return false;
		}

		return true;

	}

	@Override
	public boolean doDiszMessage(MessageDisz message) throws Exception {

		long diszid = message.getDiszID();
		long userid = message.getUserID();

		if (GLOBAL_USER_IGNORE.contains(userid)) {

			FileWriter writer = new FileWriter(FILE_DENY_USER_LOGGER);
			writer.append("[" + LoggerX.datetime() + "] 某人" + entry.getNickname(userid) + "(" + userid + ") " + message.getMessage() + "\n");
			writer.flush();
			writer.close();

		} else if (GLOBAL_DISZ_IGNORE.contains(diszid)) {

			FileWriter writer = new FileWriter(FILE_DENY_GROP_LOGGER);
			writer.append("[" + LoggerX.datetime() + "] 整个组 " + diszid + " - " + entry.getNickname(userid) + "(" + userid + ") " + message.getMessage() + "\n");
			writer.flush();
			writer.close();

		} else if (DISZ_MEMBER_IGNORE.containsKey(diszid) && DISZ_MEMBER_IGNORE.get(diszid).contains(userid)) {

			FileWriter writer = new FileWriter(FILE_DENY_GROP_LOGGER);
			writer.append("[" + LoggerX.datetime() + "] 组成员 " + diszid + " - " + entry.getNickname(userid) + "(" + userid + ") " + message.getMessage() + "\n");
			writer.flush();
			writer.close();

		} else {
			return false;
		}

		return true;


	}

	@Override
	public boolean doGropMessage(MessageGrop message) throws Exception {

		long gropid = message.getGropID();
		long userid = message.getUserID();

		if (GLOBAL_USER_IGNORE.contains(userid)) {

			FileWriter writer = new FileWriter(FILE_DENY_USER_LOGGER);
			writer.append("[" + LoggerX.datetime() + "] 指定人" + entry.getNickname(userid) + "(" + userid + ") " + message.getMessage() + "\n");
			writer.flush();
			writer.close();

		} else if (GLOBAL_GROP_IGNORE.contains(gropid)) {

			FileWriter writer = new FileWriter(FILE_DENY_GROP_LOGGER);
			writer.append("[" + LoggerX.datetime() + "] 整个群 " + gropid + " - " + entry.getNickname(userid) + "(" + userid + ") " + message.getMessage() + "\n");
			writer.flush();
			writer.close();

		} else if (GROP_MEMBER_IGNORE.containsKey(gropid) && GROP_MEMBER_IGNORE.get(gropid).contains(userid)) {

			FileWriter writer = new FileWriter(FILE_DENY_GROP_LOGGER);
			writer.append("[" + LoggerX.datetime() + "] 群成员 " + gropid + " - " + entry.getNickname(userid) + "(" + userid + ") " + message.getMessage() + "\n");
			writer.flush();
			writer.close();

		} else {
			return false;
		}
		return true;
	}

	@Override
	public String[] generateReport(Message message) {
		return new String[0];
	}

}
