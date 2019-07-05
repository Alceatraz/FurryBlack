package studio.blacktech.coolqbot.furryblack.modules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.TreeMap;

import studio.blacktech.coolqbot.furryblack.common.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleTrigger;

public class Trigger_UserDeny extends ModuleTrigger {

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "trigger_userdeny";
	private static String MODULE_COMMANDNAME = "userdeny";
	private static String MODULE_DISPLAYNAME = "过滤器";
	private static String MODULE_DESCRIPTION = "用户过滤器";
	private static String MODULE_VERSION = "1.0";
	private static String[] MODULE_USAGE = new String[] {};
	private static String[] MODULE_PRIVACY_TRIGER = new String[] {
			"获取ID号码 - 用于过滤"
	};
	private static String[] MODULE_PRIVACY_LISTEN = new String[] {};
	private static String[] MODULE_PRIVACY_STORED = new String[] {};
	private static String[] MODULE_PRIVACY_CACHED = new String[] {};
	private static String[] MODULE_PRIVACY_OBTAIN = new String[] {

	};

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================

	private ArrayList<Long> USER_IGNORE = new ArrayList<>(100);
	private TreeMap<Long, ArrayList<Long>> DISZ_IGNORE = new TreeMap<>();
	private TreeMap<Long, ArrayList<Long>> GROP_IGNORE = new TreeMap<>();

	private int DENY_USER_COUNT = 0;
	private int DENY_DISZ_COUNT = 0;
	private int DENY_GROP_COUNT = 0;

	private File FILE_USERIGNORE;
	private File FILE_DISZIGNORE;
	private File FILE_GROPIGNORE;

	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================

	public Trigger_UserDeny() throws Exception {
		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_TRIGER, MODULE_PRIVACY_LISTEN, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	@Override
	public void init(LoggerX logger) throws Exception {

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

		if (!this.FILE_USERIGNORE.exists()) { this.FILE_USERIGNORE.createNewFile(); }
		if (!this.FILE_DISZIGNORE.exists()) { this.FILE_DISZIGNORE.createNewFile(); }
		if (!this.FILE_GROPIGNORE.exists()) { this.FILE_GROPIGNORE.createNewFile(); }

		this.ENABLE_USER = Boolean.parseBoolean(this.CONFIG.getProperty("enable_user", "false"));
		this.ENABLE_DISZ = Boolean.parseBoolean(this.CONFIG.getProperty("enable_disz", "false"));
		this.ENABLE_GROP = Boolean.parseBoolean(this.CONFIG.getProperty("enable_grop", "false"));

		long userid;
		long diszid;
		long gropid;

		BufferedReader readerUser = new BufferedReader(new InputStreamReader(new FileInputStream(this.FILE_USERIGNORE), "UTF-8"));
		BufferedReader readerDisz = new BufferedReader(new InputStreamReader(new FileInputStream(this.FILE_DISZIGNORE), "UTF-8"));
		BufferedReader readerGrop = new BufferedReader(new InputStreamReader(new FileInputStream(this.FILE_GROPIGNORE), "UTF-8"));

		String line;
		String temp[];

		while ((line = readerUser.readLine()) != null) {
			if (line.startsWith("#")) { continue; }
			if (line.indexOf(":") < 0) { continue; }
			this.USER_IGNORE.add(Long.parseLong(line));
		}

		while ((line = readerDisz.readLine()) != null) {
			if (line.startsWith("#")) { continue; }
			if (line.indexOf(":") < 0) { continue; }
			temp = line.split(":");
			diszid = Long.parseLong(temp[0]);
			userid = Long.parseLong(temp[1]);
			if (!this.DISZ_IGNORE.containsKey(diszid)) { this.DISZ_IGNORE.put(diszid, new ArrayList<Long>()); }
			this.DISZ_IGNORE.get(diszid).add(userid);
		}

		while ((line = readerGrop.readLine()) != null) {
			if (line.startsWith("#")) { continue; }
			if (line.indexOf(":") < 0) { continue; }
			temp = line.split(":");
			gropid = Long.parseLong(temp[0]);
			userid = Long.parseLong(temp[1]);
			if (!this.GROP_IGNORE.containsKey(gropid)) { this.GROP_IGNORE.put(gropid, new ArrayList<Long>()); }
			this.GROP_IGNORE.get(gropid).add(userid);
		}

		readerUser.close();
		readerDisz.close();
		readerGrop.close();

		this.ENABLE_USER = this.ENABLE_USER && this.USER_IGNORE.size() > 0;
		this.ENABLE_DISZ = this.ENABLE_DISZ && this.DISZ_IGNORE.size() > 0;
		this.ENABLE_GROP = this.ENABLE_GROP && this.GROP_IGNORE.size() > 0;

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
	public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) {
	}

	@Override
	public void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) {
	}

	@Override
	public boolean doUserMessage(int typeid, long userid, MessageUser message, int messageid, int messagefont) throws Exception {
		if (this.USER_IGNORE.contains(userid)) {
			this.DENY_USER_COUNT++;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, MessageDisz message, int messageid, int messagefont) throws Exception {
		if (this.DISZ_IGNORE.containsKey(diszid) && this.DISZ_IGNORE.get(diszid).contains(userid)) {
			this.DENY_DISZ_COUNT++;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, MessageGrop message, int messageid, int messagefont) throws Exception {
		if (this.GROP_IGNORE.containsKey(gropid) && this.GROP_IGNORE.get(gropid).contains(userid)) {
			this.DENY_GROP_COUNT++;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String[] generateReport(int mode, Message message, Object... parameters) {
		StringBuilder builder = new StringBuilder();
		builder.append("拦截私聊：");
		builder.append(this.DENY_USER_COUNT);
		builder.append("\r\n拦截组聊：");
		builder.append(this.DENY_DISZ_COUNT);
		builder.append("\r\n拦截群聊：");
		builder.append(this.DENY_GROP_COUNT);
		String res[] = new String[1];
		res[0] = builder.toString();
		return res;
	}

}
