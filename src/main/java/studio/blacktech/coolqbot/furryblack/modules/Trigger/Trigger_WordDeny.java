package studio.blacktech.coolqbot.furryblack.modules.Trigger;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.regex.Pattern;


import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.LoggerX.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.annotation.ModuleTriggerComponent;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleTrigger;


@ModuleTriggerComponent
public class Trigger_WordDeny extends ModuleTrigger {

	private static final long serialVersionUID = 1L;

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "Trigger_WordDeny";
	private static String MODULE_COMMANDNAME = "worddeny";
	private static String MODULE_DISPLAYNAME = "过滤器";
	private static String MODULE_DESCRIPTION = "正则过滤器";
	private static String MODULE_VERSION = "2.0";
	private static String[] MODULE_USAGE = new String[] {};
	private static String[] MODULE_PRIVACY_STORED = new String[] {
		"按照\"成员-消息\"的层级关系记录违反ELUA的行为"
	};
	private static String[] MODULE_PRIVACY_CACHED = new String[] {};
	private static String[] MODULE_PRIVACY_OBTAIN = new String[] {};

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

	private TreeMap<String, LinkedList<MessageUser>> BLOCK_USER_STORE;
	private TreeMap<String, LinkedList<MessageDisz>> BLOCK_DISZ_STORE;
	private TreeMap<String, LinkedList<MessageGrop>> BLOCK_GROP_STORE;

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

		BLACKLIST = new ArrayList<>(100);

		BLOCK_USER_STORE = new TreeMap<>();
		BLOCK_DISZ_STORE = new TreeMap<>();
		BLOCK_GROP_STORE = new TreeMap<>();

		if (NEW_CONFIG) {

			CONFIG.setProperty("enable_user", "false");
			CONFIG.setProperty("enable_disz", "false");
			CONFIG.setProperty("enable_grop", "false");
			saveConfig();

		} else loadConfig();

		ENABLE_USER = Boolean.parseBoolean(CONFIG.getProperty("enable_user", "false"));
		ENABLE_DISZ = Boolean.parseBoolean(CONFIG.getProperty("enable_disz", "false"));
		ENABLE_GROP = Boolean.parseBoolean(CONFIG.getProperty("enable_grop", "false"));

		FILE_BLACKLIST = Paths.get(FOLDER_CONF.getAbsolutePath(), "blacklist.txt").toFile();
		FILE_DENY_USER = Paths.get(FOLDER_LOGS.getAbsolutePath(), "denied_user_log.txt").toFile();
		FILE_DENY_DISZ = Paths.get(FOLDER_LOGS.getAbsolutePath(), "denied_disz_log.txt").toFile();
		FILE_DENY_GROP = Paths.get(FOLDER_LOGS.getAbsolutePath(), "denied_grop_log.txt").toFile();

		if (!FILE_BLACKLIST.exists()) FILE_BLACKLIST.createNewFile();

		if (!FILE_DENY_USER.exists()) FILE_DENY_USER.createNewFile();

		if (!FILE_DENY_DISZ.exists()) FILE_DENY_DISZ.createNewFile();

		if (!FILE_DENY_GROP.exists()) FILE_DENY_GROP.createNewFile();

		BufferedReader reader = new BufferedReader(
			new InputStreamReader(new FileInputStream(FILE_BLACKLIST), StandardCharsets.UTF_8));

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

		for (String templine : BLACKLIST) {

			BLOCK_USER_STORE.put(templine, new LinkedList<>());
			BLOCK_DISZ_STORE.put(templine, new LinkedList<>());
			BLOCK_GROP_STORE.put(templine, new LinkedList<>());

		}

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
	public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) {}

	@Override
	public void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) {}

	@Override
	public boolean doUserMessage(int typeid, long userid, MessageUser message, int messageid, int messagefont)
		throws Exception {

		for (String temp : BLACKLIST) if (Pattern.matches(temp, message.getRawMessage())) {

			entry.adminInfo("私聊过滤：" + entry.getNickname(userid) + "(" + userid + ")" + message.getRawMessage());
			BLOCK_USER_STORE.get(temp).add(message);
			FileWriter writer = new FileWriter(FILE_DENY_USER, true);
			writer.write(message.toString());
			writer.write("\n\n\n\n");
			writer.flush();
			writer.close();
			return true;

		}
		return false;

	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, MessageDisz message, int messageid, int messagefont)
		throws Exception {

		for (String temp : BLACKLIST) if (Pattern.matches(temp, message.getRawMessage())) {

			entry.adminInfo("组聊过滤：" + diszid + " - " + entry.getNickname(userid) + "(" + userid + ")"
				+ message.getRawMessage());
			BLOCK_DISZ_STORE.get(temp).add(message);
			FileWriter writer = new FileWriter(FILE_DENY_DISZ, true);
			writer.write(message.toString());
			writer.write("\n\n\n\n");
			writer.flush();
			writer.close();
			return true;

		}
		return false;

	}

	@Override
	public boolean doGropMessage(long gropid, long userid, MessageGrop message, int messageid, int messagefont)
		throws Exception {

		for (String temp : BLACKLIST) if (Pattern.matches(temp, message.getRawMessage())) {

			entry.adminInfo("群聊过滤：" + gropid + " - " + entry.getNickname(userid) + "(" + userid + ")"
				+ message.getRawMessage());
			BLOCK_GROP_STORE.get(temp).add(message);
			FileWriter writer = new FileWriter(FILE_DENY_GROP, true);
			writer.write(message.toString());
			writer.write("\n\n\n\n");
			writer.flush();
			writer.close();
			return true;

		}
		return false;

	}

	@Override
	public String[] generateReport(int mode, Message message, Object... parameters) {

		BLOCK_USER = 0;
		BLOCK_DISZ = 0;
		BLOCK_GROP = 0;

		for (String temp : BLOCK_USER_STORE.keySet()) BLOCK_USER = BLOCK_USER + BLOCK_USER_STORE.get(temp).size();

		for (String temp : BLOCK_DISZ_STORE.keySet()) BLOCK_DISZ = BLOCK_DISZ + BLOCK_DISZ_STORE.get(temp).size();

		for (String temp : BLOCK_GROP_STORE.keySet()) BLOCK_GROP = BLOCK_GROP + BLOCK_GROP_STORE.get(temp).size();

		if (BLOCK_USER == 0 && BLOCK_DISZ == 0 && BLOCK_GROP == 0) return null;

		String[] res;
		StringBuilder builder;

		if (mode == 0) {

			builder = new StringBuilder();
			builder.append("拦截私聊：");
			builder.append(BLOCK_USER);
			builder.append("\r\n拦截私聊：");
			builder.append(BLOCK_DISZ);
			builder.append("\r\n拦截私聊：");
			builder.append(BLOCK_GROP);
			res = new String[1];
			res[0] = builder.toString();

		} else {

			res = new String[3];
			builder = new StringBuilder();
			builder.append("拦截私聊：");
			builder.append(BLOCK_USER);

			if (COUNT_USER > 0) {

				LinkedList<MessageUser> blocks;

				for (String temp : BLOCK_USER_STORE.keySet()) {

					blocks = BLOCK_USER_STORE.get(temp);

					if (blocks.size() == 0) continue;
					builder.append("\r\n规则：\"");
					builder.append(temp);
					builder.append("\" - ");
					builder.append(blocks.size());
					builder.append("次");

					for (MessageUser block : blocks) {

						builder.append("\r\n");
						builder.append(LoggerX.datetime(new Date(block.getSendtime())));
						builder.append(" > ");
						builder.append(entry.getNickname(block.userid()));
						builder.append(" (");
						builder.append(block.userid());
						builder.append(") ");
						builder.append(block.getRawMessage());

					}

				}
				res[0] = builder.toString();

			}

			builder = new StringBuilder();
			builder.append("\r\n拦截组聊：");
			builder.append(BLOCK_DISZ);

			if (COUNT_DISZ > 0) {

				LinkedList<MessageDisz> blocks;

				for (String temp : BLOCK_DISZ_STORE.keySet()) {

					blocks = BLOCK_DISZ_STORE.get(temp);

					if (blocks.size() == 0) continue;
					builder.append("\r\n规则：\"");
					builder.append(temp);
					builder.append("\" - ");
					builder.append(blocks.size());
					builder.append("次");

					for (MessageDisz block : blocks) {

						builder.append("\r\n");
						builder.append(LoggerX.datetime(new Date(block.getSendtime())));
						builder.append(" > ");
						builder.append(entry.getNickname(block.userid()));
						builder.append(" (");
						builder.append(block.userid());
						builder.append(" [");
						builder.append(block.diszid());
						builder.append("]) ");
						builder.append(block.getRawMessage());

					}

				}
				res[1] = builder.toString();

			}

			builder = new StringBuilder();
			builder.append("\r\n拦截群聊：");
			builder.append(BLOCK_GROP);

			if (COUNT_GROP > 0) {

				LinkedList<MessageGrop> blocks;

				for (String temp : BLOCK_GROP_STORE.keySet()) {

					blocks = BLOCK_GROP_STORE.get(temp);

					if (blocks.size() == 0) continue;
					builder.append("\r\n规则：\"");
					builder.append(temp);
					builder.append("\" - ");
					builder.append(blocks.size());
					builder.append("次");

					for (MessageGrop block : blocks) {

						builder.append("\r\n");
						builder.append(LoggerX.datetime(new Date(block.getSendtime())));
						builder.append(" > ");
						builder.append(entry.getNickname(block.userid()));
						builder.append(" (");
						builder.append(block.userid());
						builder.append(" [");
						builder.append(block.gropid());
						builder.append("])：");
						builder.append(block.getRawMessage());

					}

				}
				res[2] = builder.toString();

			}

		}
		return res;

	}

}
