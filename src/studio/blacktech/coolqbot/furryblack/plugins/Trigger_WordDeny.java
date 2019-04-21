package studio.blacktech.coolqbot.furryblack.plugins;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.module.Message;
import studio.blacktech.coolqbot.furryblack.module.ModuleTrigger;

public class Trigger_WordDeny extends ModuleTrigger {

	private ArrayList<CharSequence> BLACKLIST = new ArrayList<CharSequence>(100);

	private int DENY_USER_COUNT = 0;
	private int DENY_DISZ_COUNT = 0;
	private int DENY_GROP_COUNT = 0;

	public Trigger_WordDeny() {
		this.MODULE_DISPLAYNAME = "过滤器";
		this.MODULE_PACKAGENAME = "worddeny";
		this.MODULE_DESCRIPTION = "正则过滤器";
		this.MODULE_VERSION = "1.0.0";
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
		} catch (Exception exce) {
			exce.printStackTrace();
		}
	}

	@Override
	public boolean doUserMessage(int typeid, long userid, Message message, int messageid, int messagefont) throws Exception {
		for (final CharSequence temp : this.BLACKLIST) {
			if (message.rawMessage.contains(temp)) {
				this.DENY_USER_COUNT++;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, Message message, int messageid, int messagefont) throws Exception {
		for (final CharSequence temp : this.BLACKLIST) {
			if (message.rawMessage.contains(temp)) {
				this.DENY_DISZ_COUNT++;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, Message message, int messageid, int messagefont) throws Exception {
		for (final CharSequence temp : this.BLACKLIST) {
			if (message.rawMessage.contains(temp)) {
				this.DENY_GROP_COUNT++;
				return true;
			}
		}
		return false;
	}

	@Override
	public String generateReport(boolean fullreport, int loglevel, Object[] parameters) {
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
