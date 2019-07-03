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
	// ģ���������
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "worddeny";
	private static String MODULE_DISPLAYNAME = "������";
	private static String MODULE_DESCRIPTION = "���������";
	private static String MODULE_VERSION = "1.0";
	private static String[] MODULE_USAGE = new String[] {};
	private static String[] MODULE_PRIVACY_TRIGER = new String[] {
			"��ȡ��Ϣ���� - ���ڹ���"
	};
	private static String[] MODULE_PRIVACY_LISTEN = new String[] {};
	private static String[] MODULE_PRIVACY_STORED = new String[] {};
	private static String[] MODULE_PRIVACY_CACHED = new String[] {};
	private static String[] MODULE_PRIVACY_OBTAIN = new String[] {

	};

	// ==========================================================================================================================================================
	//
	// ��Ա����
	//
	// ==========================================================================================================================================================

	private ArrayList<String> BLACKLIST = new ArrayList<>(100);

	private int DENY_USER_COUNT = 0;
	private int DENY_DISZ_COUNT = 0;
	private int DENY_GROP_COUNT = 0;

	private File FILE_BLACKLIST;

	// ==========================================================================================================================================================
	//
	// �������ں���
	//
	// ==========================================================================================================================================================

	public Trigger_WordDeny() throws Exception {
		super(MODULE_DISPLAYNAME, MODULE_PACKAGENAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_TRIGER, MODULE_PRIVACY_LISTEN, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
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

		this.ENABLE_USER = Boolean.parseBoolean(this.CONFIG.getProperty("enable_user", "false"));
		this.ENABLE_DISZ = Boolean.parseBoolean(this.CONFIG.getProperty("enable_disz", "false"));
		this.ENABLE_GROP = Boolean.parseBoolean(this.CONFIG.getProperty("enable_grop", "false"));

		this.FILE_BLACKLIST = Paths.get(this.FOLDER_CONF.getAbsolutePath(), "blacklist.txt").toFile();

		if (!this.FILE_BLACKLIST.exists()) { this.FILE_BLACKLIST.createNewFile(); }

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.FILE_BLACKLIST), "UTF-8"));) {
			String line;
			while ((line = reader.readLine()) != null) {
				this.BLACKLIST.add(line);
			}
			reader.close();
		} catch (final Exception exce) {
			exce.printStackTrace();
		}

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
	public boolean doUserMessage(final int typeid, final long userid, final MessageUser message, final int messageid, final int messagefont) throws Exception {
		for (final String temp : this.BLACKLIST) {
			if (Pattern.matches(temp, message.getRawMessage())) {
				this.DENY_USER_COUNT++;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean doDiszMessage(final long diszid, final long userid, final MessageDisz message, final int messageid, final int messagefont) throws Exception {
		for (final String temp : this.BLACKLIST) {
			if (Pattern.matches(temp, message.getRawMessage())) {
				this.DENY_DISZ_COUNT++;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean doGropMessage(final long gropid, final long userid, final MessageGrop message, final int messageid, final int messagefont) throws Exception {
		for (final String temp : this.BLACKLIST) {
			if (Pattern.matches(temp, message.getRawMessage())) {
				this.DENY_GROP_COUNT++;
				return true;
			}
		}
		return false;
	}

	@Override
	public String[] generateReport(int mode, final Message message, final Object... parameters) {
		final StringBuilder builder = new StringBuilder();
		builder.append("����˽�ģ�");
		builder.append(this.DENY_USER_COUNT);
		builder.append("\r\n�������ģ�");
		builder.append(this.DENY_DISZ_COUNT);
		builder.append("\r\n����Ⱥ�ģ�");
		builder.append(this.DENY_GROP_COUNT);
		final String res[] = new String[1];
		res[0] = builder.toString();
		return res;
	}

}
