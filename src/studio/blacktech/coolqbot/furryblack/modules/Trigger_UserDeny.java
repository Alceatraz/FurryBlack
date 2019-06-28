package studio.blacktech.coolqbot.furryblack.modules;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.Message;
import studio.blacktech.coolqbot.furryblack.common.ModuleTrigger;

public class Trigger_UserDeny extends ModuleTrigger {

	private final ArrayList<Long> USER_IGNORE = new ArrayList<>(100);
	private final ArrayList<Long> DISZ_IGNORE = new ArrayList<>(100);
	private final ArrayList<Long> GROP_IGNORE = new ArrayList<>(100);

	private int DENY_USER_COUNT = 0;
	private int DENY_DISZ_COUNT = 0;
	private int DENY_GROP_COUNT = 0;

	public Trigger_UserDeny() {
		this.MODULE_PACKAGENAME = "userdeny";
		this.MODULE_DISPLAYNAME = "过滤器";
		this.MODULE_DESCRIPTION = "用户与群组过滤器";
		this.MODULE_VERSION = "1.0.0";
		this.MODULE_USAGE = new String[] {};
		this.MODULE_PRIVACY_TRIGER = new String[] {
				"获取ID号码 - 用于过滤"
		};
		this.MODULE_PRIVACY_LISTEN = new String[] {};
		this.MODULE_PRIVACY_STORED = new String[] {};
		this.MODULE_PRIVACY_CACHED = new String[] {};
		this.MODULE_PRIVACY_OBTAIN = new String[] {};

		String line;
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(entry.FILE_USERIGNORE()), "UTF-8"));
			while ((line = reader.readLine()) != null) {
				this.USER_IGNORE.add(Long.parseLong(line));
			}
			reader.close();

			reader = new BufferedReader(new InputStreamReader(new FileInputStream(entry.FILE_DISZIGNORE()), "UTF-8"));
			while ((line = reader.readLine()) != null) {
				this.DISZ_IGNORE.add(Long.parseLong(line));
			}
			reader.close();

			reader = new BufferedReader(new InputStreamReader(new FileInputStream(entry.FILE_GROPIGNORE()), "UTF-8"));
			while ((line = reader.readLine()) != null) {
				this.GROP_IGNORE.add(Long.parseLong(line));
			}
			reader.close();

			this.ENABLE_USER = this.USER_IGNORE.size() > 0;
			this.ENABLE_DISZ = this.DISZ_IGNORE.size() > 0;
			this.ENABLE_GROP = this.GROP_IGNORE.size() > 0;

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
		if (this.USER_IGNORE.contains(userid)) {
			this.DENY_USER_COUNT++;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean doDiszMessage(final long diszid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		if (this.DISZ_IGNORE.contains(userid) || this.USER_IGNORE.contains(userid)) {
			this.DENY_DISZ_COUNT++;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean doGropMessage(final long gropid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		if (this.USER_IGNORE.contains(userid) || this.USER_IGNORE.contains(userid)) {
			this.DENY_GROP_COUNT++;
			return true;
		} else {
			return false;
		}
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
