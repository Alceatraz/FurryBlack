package studio.blacktech.coolqbot.furryblack.modules;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.Message;
import studio.blacktech.coolqbot.furryblack.common.ModuleTrigger;

public class Trigger_WordDeny extends ModuleTrigger {

	public boolean ENABLE = false;

	private ArrayList<String> BLACKLIST = new ArrayList<>(100);

	private int DENY_USER_COUNT = 0;
	private int DENY_DISZ_COUNT = 0;
	private int DENY_GROP_COUNT = 0;

	public Trigger_WordDeny() {
		this.MODULE_PACKAGENAME = "worddeny";
		this.MODULE_DISPLAYNAME = "过滤器";
		this.MODULE_DESCRIPTION = "正则过滤器";
		this.MODULE_VERSION = "2.1.0";
		this.MODULE_USAGE = new String[] {};
		this.MODULE_PRIVACY_TRIGER = new String[] {
				"获取消息内容 - 用于正则判断"
		};
		this.MODULE_PRIVACY_LISTEN = new String[] {};
		this.MODULE_PRIVACY_STORED = new String[] {};
		this.MODULE_PRIVACY_CACHED = new String[] {};
		this.MODULE_PRIVACY_OBTAIN = new String[] {};

		String line;
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(entry.FILE_BLACKLIST()), "UTF-8"));
			while ((line = reader.readLine()) != null) {
				this.BLACKLIST.add(line);
			}
			reader.close();
			boolean temp = this.BLACKLIST.size() > 0;
			this.ENABLE_USER = temp;
			this.ENABLE_DISZ = temp;
			this.ENABLE_GROP = temp;
		} catch (Exception exce) {
			exce.printStackTrace();
		}
	}

	@Override
	public boolean doUserMessage(int typeid, long userid, Message message, int messageid, int messagefont) throws Exception {
		for (final String temp : this.BLACKLIST) {
			if (Pattern.matches(temp, message.rawMessage())) {
				this.DENY_USER_COUNT++;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, Message message, int messageid, int messagefont) throws Exception {
		for (final String temp : this.BLACKLIST) {
			if (Pattern.matches(temp, message.rawMessage())) {
				this.DENY_DISZ_COUNT++;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, Message message, int messageid, int messagefont) throws Exception {
		for (final String temp : this.BLACKLIST) {
			if (Pattern.matches(temp, message.rawMessage())) {
				this.DENY_GROP_COUNT++;
				return true;
			}
		}
		return false;
	}

	@Override
	public String generateReport(int logLevel, int logMode, int typeid, long userid, long diszid, long gropid, Message message, Object[] parameters) {
		StringBuilder builder = new StringBuilder();
		builder.append("拦截私聊：");
		builder.append(this.DENY_USER_COUNT);
		builder.append("\r\n拦截组聊：");
		builder.append(this.DENY_DISZ_COUNT);
		builder.append("\r\n拦截群聊：");
		builder.append(this.DENY_GROP_COUNT);
		return builder.toString();
	}
}
