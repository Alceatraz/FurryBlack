package studio.blacktech.coolqbot.furryblack.modules.Trigger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.TreeMap;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.LoggerX.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleTrigger;

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
	private static String[] MODULE_PRIVACY_STORED = new String[] { "按照\"群-成员\"的层级关系手动配置被阻止的用户" };
	private static String[] MODULE_PRIVACY_CACHED = new String[] {};
	private static String[] MODULE_PRIVACY_OBTAIN = new String[] {};

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================

	private HashSet<Long> USER_IGNORE;
	private HashSet<Long> DISZ_IGNORE;
	private HashSet<Long> GROP_IGNORE;
	private TreeMap<Long, HashSet<Long>> DISZ_IGNORE_ONE;
	private TreeMap<Long, HashSet<Long>> GROP_IGNORE_ONE;

	private TreeMap<Long, Integer> DENY_USER_COUNT;
	private TreeMap<Long, TreeMap<Long, Integer>> DENY_DISZ_COUNT;
	private TreeMap<Long, TreeMap<Long, Integer>> DENY_GROP_COUNT;

	private File FILE_USERIGNORE;
	private File FILE_DISZIGNORE;
	private File FILE_GROPIGNORE;

	private File FILE_DENY_USER;
	private File FILE_DENY_DISZ;
	private File FILE_DENY_GROP;

	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================

	public Trigger_UserDeny() throws Exception {

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

		this.initAppFolder(logger);
		this.initConfFolder(logger);
		this.initLogsFolder(logger);

		this.USER_IGNORE = new HashSet<>(100);
		this.DISZ_IGNORE = new HashSet<>();
		this.GROP_IGNORE = new HashSet<>();
		this.DISZ_IGNORE_ONE = new TreeMap<>();
		this.GROP_IGNORE_ONE = new TreeMap<>();
		this.DENY_USER_COUNT = new TreeMap<>();
		this.DENY_DISZ_COUNT = new TreeMap<>();
		this.DENY_GROP_COUNT = new TreeMap<>();

		if (this.NEW_CONFIG) {
			this.CONFIG.setProperty("enable_user", "false");
			this.CONFIG.setProperty("enable_disz", "false");
			this.CONFIG.setProperty("enable_grop", "false");
			this.saveConfig();
		} else {
			this.loadConfig();
		}

		this.FILE_USERIGNORE = Paths.get(this.FOLDER_CONF.getAbsolutePath(), "ignore_user.txt").toFile();
		this.FILE_DISZIGNORE = Paths.get(this.FOLDER_CONF.getAbsolutePath(), "ignore_disz.txt").toFile();
		this.FILE_GROPIGNORE = Paths.get(this.FOLDER_CONF.getAbsolutePath(), "ignore_grop.txt").toFile();

		this.FILE_DENY_USER = Paths.get(this.FOLDER_LOGS.getAbsolutePath(), "ignore_user_log.txt").toFile();
		this.FILE_DENY_DISZ = Paths.get(this.FOLDER_LOGS.getAbsolutePath(), "ignore_disz_log.txt").toFile();
		this.FILE_DENY_GROP = Paths.get(this.FOLDER_LOGS.getAbsolutePath(), "ignore_grop_log.txt").toFile();

		if (!this.FILE_USERIGNORE.exists()) { this.FILE_USERIGNORE.createNewFile(); }
		if (!this.FILE_DISZIGNORE.exists()) { this.FILE_DISZIGNORE.createNewFile(); }
		if (!this.FILE_GROPIGNORE.exists()) { this.FILE_GROPIGNORE.createNewFile(); }

		if (!this.FILE_DENY_USER.exists()) { this.FILE_DENY_USER.createNewFile(); }
		if (!this.FILE_DENY_DISZ.exists()) { this.FILE_DENY_DISZ.createNewFile(); }
		if (!this.FILE_DENY_GROP.exists()) { this.FILE_DENY_GROP.createNewFile(); }

		this.ENABLE_USER = Boolean.parseBoolean(this.CONFIG.getProperty("enable_user", "false"));
		this.ENABLE_DISZ = Boolean.parseBoolean(this.CONFIG.getProperty("enable_disz", "false"));
		this.ENABLE_GROP = Boolean.parseBoolean(this.CONFIG.getProperty("enable_grop", "false"));

		BufferedReader readerUser = new BufferedReader(new InputStreamReader(new FileInputStream(this.FILE_USERIGNORE), StandardCharsets.UTF_8));
		BufferedReader readerDisz = new BufferedReader(new InputStreamReader(new FileInputStream(this.FILE_DISZIGNORE), StandardCharsets.UTF_8));
		BufferedReader readerGrop = new BufferedReader(new InputStreamReader(new FileInputStream(this.FILE_GROPIGNORE), StandardCharsets.UTF_8));

		long userid;
		long diszid;
		long gropid;
		String line;
		String[] temp;

		while ((line = readerUser.readLine()) != null) {
			if (line.startsWith("#")) { continue; }
			if (line.contains("#")) { line = line.substring(0, line.indexOf("#")).trim(); }
			this.USER_IGNORE.add(Long.parseLong(line));
			logger.seek(Trigger_UserDeny.MODULE_PACKAGENAME, "禁止私聊用户", line);
		}

		while ((line = readerDisz.readLine()) != null) {
			if (line.startsWith("#")) { continue; }
			if (!line.contains(":")) { continue; }
			if (line.contains("#")) { line = line.substring(0, line.indexOf("#")).trim(); }
			temp = line.split(":");
			diszid = Long.parseLong(temp[0]);
			userid = Long.parseLong(temp[1]);
			if (userid == 0) {
				this.DISZ_IGNORE.add(diszid);
			} else {
				if (!this.DISZ_IGNORE_ONE.containsKey(diszid)) { HashSet<Long> tempSet = new HashSet<>(); this.DISZ_IGNORE_ONE.put(diszid, tempSet); }
				this.DISZ_IGNORE_ONE.get(diszid).add(userid);
			}
			logger.seek(Trigger_UserDeny.MODULE_PACKAGENAME, "禁止组聊用户", line);
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
				this.GROP_IGNORE.add(gropid);
			} else {
				if (!this.GROP_IGNORE_ONE.containsKey(gropid)) { HashSet<Long> tempSet = new HashSet<>(); this.GROP_IGNORE_ONE.put(gropid, tempSet); }
				this.GROP_IGNORE_ONE.get(gropid).add(userid);
			}
			logger.seek(Trigger_UserDeny.MODULE_PACKAGENAME, "禁止群聊用户", line);
		}

		readerUser.close();
		readerDisz.close();
		readerGrop.close();

		this.ENABLE_USER = this.ENABLE_USER && (this.USER_IGNORE.size() > 0);
		this.ENABLE_DISZ = this.ENABLE_DISZ && ((this.DISZ_IGNORE.size() + this.DISZ_IGNORE_ONE.size()) > 0);
		this.ENABLE_GROP = this.ENABLE_GROP && ((this.GROP_IGNORE.size() + this.GROP_IGNORE_ONE.size()) > 0);

		for (Long tempuserid : this.USER_IGNORE) {
			this.DENY_USER_COUNT.put(tempuserid, 0);
		}

		for (Long tempdiszid : this.DISZ_IGNORE_ONE.keySet()) {
			TreeMap<Long, Integer> tempcount = new TreeMap<>();
			HashSet<Long> tempdisz = this.DISZ_IGNORE_ONE.get(tempdiszid);
			for (Long tempuserid : tempdisz) {
				tempcount.put(tempuserid, 0);
			}
			this.DENY_DISZ_COUNT.put(tempdiszid, tempcount);
		}

		for (Long tempgropid : this.GROP_IGNORE_ONE.keySet()) {
			TreeMap<Long, Integer> tempcount = new TreeMap<>();
			HashSet<Long> tempgrop = this.GROP_IGNORE_ONE.get(tempgropid);
			for (Long tempuserid : tempgrop) {
				tempcount.put(tempuserid, 0);
			}
			this.DENY_GROP_COUNT.put(tempgropid, tempcount);
		}

		return logger;

	}

	@Override
	public LoggerX boot(LoggerX logger) throws Exception {
		return logger;
	}

	@Override
	public LoggerX save(LoggerX logger) throws Exception {
		return logger;
	}

	@Override
	public LoggerX shut(LoggerX logger) throws Exception {
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

		if (this.USER_IGNORE.contains(userid)) {

			this.DENY_USER_COUNT.put(userid, this.DENY_USER_COUNT.get(userid) + 1);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.FILE_DENY_USER, true), StandardCharsets.UTF_8));
			writer.write(message.toString());
			writer.write("\r\n\r\n\r\n\r\n");
			writer.flush();
			writer.close();

			return true;

		} else {

			return false;

		}
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, MessageDisz message, int messageid, int messagefont) throws Exception {

		if (this.USER_IGNORE.contains(userid)) {

			this.DENY_USER_COUNT.put(userid, this.DENY_USER_COUNT.get(userid) + 1);

		} else if (this.DISZ_IGNORE.contains(diszid) && this.DISZ_IGNORE_ONE.get(diszid).contains(userid)) {

			TreeMap<Long, Integer> temp = this.DENY_DISZ_COUNT.get(diszid);
			temp.put(userid, temp.get(userid) + 1);

		} else {

			return false;

		}

		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.FILE_DENY_DISZ, true), StandardCharsets.UTF_8));
		writer.write(message.toString());
		writer.write("\r\n\r\n\r\n\r\n");
		writer.flush();
		writer.close();

		return true;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, MessageGrop message, int messageid, int messagefont) throws Exception {

		if (this.USER_IGNORE.contains(userid)) {

			this.DENY_USER_COUNT.put(userid, this.DENY_USER_COUNT.get(userid) + 1);

		} else if (this.GROP_IGNORE.contains(gropid) && this.GROP_IGNORE_ONE.get(gropid).contains(userid)) {

			TreeMap<Long, Integer> temp = this.DENY_GROP_COUNT.get(gropid);
			temp.put(userid, temp.get(userid) + 1);

		} else {

			return false;

		}

		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.FILE_DENY_GROP, true), StandardCharsets.UTF_8));
		writer.write(message.toString());
		writer.write("\r\n\r\n\r\n\r\n");
		writer.flush();
		writer.close();

		return true;
	}

	@Override
	public String[] generateReport(int mode, Message message, Object... parameters) {

		this.COUNT_USER = 0;
		this.COUNT_DISZ = 0;
		this.COUNT_GROP = 0;

		for (long userid : this.DENY_USER_COUNT.keySet()) {
			this.COUNT_USER = this.COUNT_USER + this.DENY_USER_COUNT.get(userid);
		}
		for (long diszid : this.DENY_DISZ_COUNT.keySet()) {
			TreeMap<Long, Integer> disz = this.DENY_DISZ_COUNT.get(diszid);
			for (long userid : disz.keySet()) {
				this.COUNT_DISZ = this.COUNT_DISZ + disz.get(userid);
			}
		}
		for (long gropid : this.DENY_GROP_COUNT.keySet()) {
			TreeMap<Long, Integer> grop = this.DENY_GROP_COUNT.get(gropid);
			for (long userid : grop.keySet()) {
				this.COUNT_GROP = this.COUNT_GROP + grop.get(userid);
			}
		}

		if ((this.COUNT_USER == 0) && (this.COUNT_DISZ == 0) && (this.COUNT_GROP == 0)) { return null; }

		StringBuilder builder = new StringBuilder();

		if (this.COUNT_USER == 0) {
			builder.append("拦截私聊：0");
		} else {
			builder.append("拦截私聊：");
			builder.append(this.COUNT_USER);
			for (long userid : this.DENY_USER_COUNT.keySet()) {
				builder.append("\r\n");
				builder.append(entry.getNickname(userid));
				builder.append(" (");
				builder.append(userid);
				builder.append(") ");
				builder.append(this.DENY_USER_COUNT.get(userid));
			}
		}

		if (this.COUNT_DISZ == 0) {
			builder.append("\r\n拦截组聊：0");
		} else {
			builder.append("\r\n拦截组聊：");
			builder.append(this.COUNT_DISZ);
			builder.append("\r\n");
			for (long diszid : this.DENY_DISZ_COUNT.keySet()) {
				TreeMap<Long, Integer> disz = this.DENY_DISZ_COUNT.get(diszid);
				builder.append("组号：");
				builder.append(diszid);
				for (long userid : disz.keySet()) {
					builder.append("\r\n");
					builder.append(entry.getNickname(userid));
					builder.append(" (");
					builder.append(userid);
					builder.append(") ");
					builder.append(disz.get(userid));
				}
			}
		}

		if (this.COUNT_GROP == 0) {
			builder.append("\r\n拦截群聊：0");
		} else {
			builder.append("\r\n拦截群聊：");
			builder.append(this.COUNT_GROP);
			builder.append("\r\n");
			for (long gropid : this.DENY_GROP_COUNT.keySet()) {
				TreeMap<Long, Integer> grop = this.DENY_GROP_COUNT.get(gropid);
				builder.append(" 群号：");
				builder.append(gropid);
				for (long userid : grop.keySet()) {
					builder.append("\r\n");
					builder.append(entry.getNickname(userid));
					builder.append(" (");
					builder.append(userid);
					builder.append(") ");
					builder.append(grop.get(userid));
				}
			}
		}
		String[] res = new String[] { builder.toString() };
		return res;
	}

}
