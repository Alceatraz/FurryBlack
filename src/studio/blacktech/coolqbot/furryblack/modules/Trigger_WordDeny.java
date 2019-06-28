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

	private final ArrayList<String> BLACKLIST = new ArrayList<>(100);

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
			final boolean temp = this.BLACKLIST.size() > 0;
			this.ENABLE_USER = temp;
			this.ENABLE_DISZ = temp;
			this.ENABLE_GROP = temp;
		} catch (final Exception exce) {
			exce.printStackTrace();
		}
	}

	@Override
	public void memberExit(long gropid, long userid) {
	}

	@Override
	public void memberJoin(long gropid, long userid) {
	}

	@Override
	public boolean doUserMessage(final int typeid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		for (final String temp : this.BLACKLIST) {
			if (Pattern.matches(temp, message.rawMessage())) {
				this.DENY_USER_COUNT++;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean doDiszMessage(final long diszid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		for (final String temp : this.BLACKLIST) {
			if (Pattern.matches(temp, message.rawMessage())) {
				this.DENY_DISZ_COUNT++;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean doGropMessage(final long gropid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		for (final String temp : this.BLACKLIST) {
			if (Pattern.matches(temp, message.rawMessage())) {
				this.DENY_GROP_COUNT++;
				return true;
			}
		}
		return false;
	}

	@Override
	public String[] generateReport(final int logLevel, final int logMode, final int typeid, final long userid, final long diszid, final long gropid, final Message message, final Object... parameters) {
		final StringBuilder builder = new StringBuilder();
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
