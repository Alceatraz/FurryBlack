package studio.blacktech.coolqbot.furryblack.modules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Pattern;

import studio.blacktech.coolqbot.furryblack.common.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleTrigger;

public class Trigger_WordDeny extends ModuleTrigger {

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "trigger_worddeny";
	private static String MODULE_COMMANDNAME = "worddeny";
	private static String MODULE_DISPLAYNAME = "过滤器";
	private static String MODULE_DESCRIPTION = "正则过滤器";
	private static String MODULE_VERSION = "1.0";
	private static String[] MODULE_USAGE = new String[] {};
	private static String[] MODULE_PRIVACY_TRIGER = new String[] {
			"获取消息内容 - 用于过滤"
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

	private ArrayList<String> BLACKLIST;

	private int DENY_USER_COUNT = 0;
	private int DENY_DISZ_COUNT = 0;
	private int DENY_GROP_COUNT = 0;

	private File FILE_BLACKLIST;

	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================

	public Trigger_WordDeny() throws Exception {
		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_TRIGER, MODULE_PRIVACY_LISTEN, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	@Override
	public void init(LoggerX logger) throws Exception {

		this.initConfFolder();
		this.initCofigurtion();

		this.BLACKLIST = new ArrayList<>(100);

		if (this.NEW_CONFIG) {
			this.CONFIG.setProperty("enable_user", "false");
			this.CONFIG.setProperty("enable_disz", "false");
			this.CONFIG.setProperty("enable_grop", "false");
			this.saveConfig();
		} else {
			this.loadConfig();
		}

		this.ENABLE_USER = Boolean.parseBoolean(this.CONFIG.getProperty("enable_user", "false"));
		this.ENABLE_DISZ = Boolean.parseBoolean(this.CONFIG.getProperty("enable_disz", "false"));
		this.ENABLE_GROP = Boolean.parseBoolean(this.CONFIG.getProperty("enable_grop", "false"));

		this.FILE_BLACKLIST = Paths.get(this.FOLDER_CONF.getAbsolutePath(), "blacklist.txt").toFile();

		if (!this.FILE_BLACKLIST.exists()) { this.FILE_BLACKLIST.createNewFile(); }

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.FILE_BLACKLIST), "UTF-8"));
		String line;
		while ((line = reader.readLine()) != null) {
			this.BLACKLIST.add(line);
		}
		reader.close();

		boolean temp = this.BLACKLIST.size() > 0;

		this.ENABLE_USER = this.ENABLE_USER && temp;
		this.ENABLE_DISZ = this.ENABLE_DISZ && temp;
		this.ENABLE_GROP = this.ENABLE_GROP && temp;
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
		for (String temp : this.BLACKLIST) {
			if (Pattern.matches(temp, message.getRawMessage())) {
				this.DENY_USER_COUNT++;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, MessageDisz message, int messageid, int messagefont) throws Exception {
		for (String temp : this.BLACKLIST) {
			if (Pattern.matches(temp, message.getRawMessage())) {
				this.DENY_DISZ_COUNT++;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, MessageGrop message, int messageid, int messagefont) throws Exception {
		for (String temp : this.BLACKLIST) {
			if (Pattern.matches(temp, message.getRawMessage())) {
				this.DENY_GROP_COUNT++;
				return true;
			}
		}
		return false;
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
