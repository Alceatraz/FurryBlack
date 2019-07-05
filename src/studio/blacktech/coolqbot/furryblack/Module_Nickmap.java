package studio.blacktech.coolqbot.furryblack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.TreeMap;

import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.common.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.module.Module;

public class Module_Nickmap extends Module {

	// ==========================================================================================================================================================
	//
	// ģ���������
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "core_nicknmap";
	private static String MODULE_COMMANDNAME = "ddns";
	private static String MODULE_DISPLAYNAME = "�ǳ�ӳ��";
	private static String MODULE_DESCRIPTION = "�����ӵ��ǳ�ӳ��Ϊ���Ѽ�ļ�̳ƺ�";
	private static String MODULE_VERSION = "1.0";
	private static String[] MODULE_USAGE = new String[] {};
	private static String[] MODULE_PRIVACY_TRIGER = new String[] {};
	private static String[] MODULE_PRIVACY_LISTEN = new String[] {};
	private static String[] MODULE_PRIVACY_STORED = new String[] {};
	private static String[] MODULE_PRIVACY_CACHED = new String[] {};
	private static String[] MODULE_PRIVACY_OBTAIN = new String[] {};

	// ==========================================================================================================================================================
	//
	// ��Ա����
	//
	// ==========================================================================================================================================================

	private static TreeMap<Long, String> NICKNAME = new TreeMap<>();

	private boolean ENABLE_REPLACE = false;

	private File FILE_NICKNAME;

	private NicknameDelegate delegate = new NicknameDelegate();

	// ==========================================================================================================================================================
	//
	// �������ں���
	//
	// ==========================================================================================================================================================

	public Module_Nickmap() throws Exception {
		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_TRIGER, MODULE_PRIVACY_LISTEN, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	@Override
	public void init(LoggerX logger) throws Exception {
		if (this.NEW_CONFIG) {
			logger.seek("[Nickmap] �����ļ������� - ����Ĭ������");
			this.CONFIG.setProperty("enable_nickname_replace", "false");
			this.saveConfig();
		} else {
			this.loadConfig();

		}
	}

	@Override
	public void boot(LoggerX logger) throws Exception {

		this.FILE_NICKNAME = Paths.get(this.FOLDER_CONF.getAbsolutePath(), "user_nickname.txt").toFile();
		if (!this.FILE_NICKNAME.exists()) { this.FILE_NICKNAME.createNewFile(); }

		this.ENABLE_REPLACE = Boolean.parseBoolean(this.CONFIG.getProperty("enable_nickname_replace", "false"));

		String line;
		String temp[];
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.FILE_NICKNAME), "UTF-8"));
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("#")) { continue; }
			if (line.indexOf(":") < 0) { continue; }
			temp = line.split(":");
			NICKNAME.put(Long.parseLong(temp[0]), temp[1]);
		}
		reader.close();

		logger.seek("[Nickmap] �ǳ��滻��", this.ENABLE_REPLACE ? "����" : "����");

	}

	@Override
	public void shut(LoggerX logger) throws Exception {
	}

	@Override
	public void reload(LoggerX logger) throws Exception {
	}

	@Override
	public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) throws Exception {
	}

	@Override
	public void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) throws Exception {
	}

	// ==========================================================================================================================================================
	//
	// ���ߺ���
	//
	// ==========================================================================================================================================================

	@Override
	public String[] generateReport(int mode, Message message, Object... parameters) {
		return null;
	}

	public String getNickname(long userid) {
		if (this.ENABLE_REPLACE && NICKNAME.containsKey(userid)) {
			return NICKNAME.get(userid);
		} else {
			return JcqApp.CQ.getStrangerInfo(userid).getNick();
		}
	}

	public NicknameDelegate getDelegate() {
		return this.delegate;
	}

	public class NicknameDelegate {
		public String getNickname(long userid) {
			return JcqApp.CQ.getStrangerInfo(userid).getNick();
		}
	}
}
