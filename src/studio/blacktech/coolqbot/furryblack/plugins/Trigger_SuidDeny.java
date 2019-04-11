package studio.blacktech.coolqbot.furryblack.plugins;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.module.Message;
import studio.blacktech.coolqbot.furryblack.module.ModuleTrigger;

public class Trigger_SuidDeny extends ModuleTrigger {

	private ArrayList<Long> USER_IGNORE = new ArrayList<Long>(100);
	private ArrayList<Long> DISZ_IGNORE = new ArrayList<Long>(100);
	private ArrayList<Long> GROP_IGNORE = new ArrayList<Long>(100);

	private int DENY_USER_COUNT = 0;
	private int DENY_DISZ_COUNT = 0;
	private int DENY_GROP_COUNT = 0;

	public Trigger_SuidDeny() {
		this.MODULE_DISPLAYNAME = "过滤器";
		this.MODULE_PACKAGENAME = "userdeny";
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
		} catch (Exception exce) {
			exce.printStackTrace();
		}
	}

	@Override
	public boolean doUserMessage(int typeid, long userid, Message message, int messageid, int messagefont) throws Exception {
		if (this.USER_IGNORE.contains(userid)) {
			this.DENY_USER_COUNT++;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, Message message, int messageid, int messagefont) throws Exception {
		if (this.USER_IGNORE.contains(userid)) {
			this.DENY_DISZ_COUNT++;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, Message message, int messageid, int messagefont) throws Exception {
		if (this.USER_IGNORE.contains(userid)) {
			this.DENY_GROP_COUNT++;
			return true;
		} else {
			return false;
		}
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
