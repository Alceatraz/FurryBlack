package studio.blacktech.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

import com.sobte.cqp.jcq.event.JcqApp;
import com.sobte.cqp.jcq.event.JcqAppAbstract;

import studio.blacktech.coolqbot.furryblack.scheduler.Scheduler_DDNS;

public class ConfigureX {

	public static final File FOLDER_CONF = Paths.get(JcqAppAbstract.appDirectory, "conf").toFile();
	public static final File FILE_CONFIG = Paths.get(ConfigureX.FOLDER_CONF.getAbsolutePath(), "config.properties").toFile();
	public static final File FILE_BLACKLIST = Paths.get(ConfigureX.FOLDER_CONF.getAbsolutePath(), "blacklist.txt").toFile();
	public static final File FILE_USERIGNORE = Paths.get(ConfigureX.FOLDER_CONF.getAbsolutePath(), "user_ignore.txt").toFile();
	public static final File FILE_DISZIGNORE = Paths.get(ConfigureX.FOLDER_CONF.getAbsolutePath(), "disz_ignore.txt").toFile();
	public static final File FILE_GROPIGNORE = Paths.get(ConfigureX.FOLDER_CONF.getAbsolutePath(), "grop_ignore.txt").toFile();

	private static Properties property = new Properties();

	private static long OPERATOR = 0;
	private static long MYSELFID = 0;

	private static boolean ENABLE_TRIGGER_USER = false;
	private static boolean ENABLE_TRIGGER_DISZ = false;
	private static boolean ENABLE_TRIGGER_GROP = false;

	private static boolean ENABLE_LISENTER_USER = false;
	private static boolean ENABLE_LISENTER_DISZ = false;
	private static boolean ENABLE_LISENTER_GROP = false;

	private static boolean ENABLE_BLACKLIST = false;

	private static boolean ENABLE_USERIGNORE = false;
	private static boolean ENABLE_DISZIGNORE = false;
	private static boolean ENABLE_GROPIGNORE = false;

	private static boolean ENABLE_DDNSCLIENT = false;
	private static String DDNSAPI_CLIENTUA;
	private static String DDNSAPI_HOSTNAME;
	private static String DDNSAPI_PASSWORD;

	/***
	 * �����������ļ� ��������ڽ�����Ĭ�������ļ�
	 *
	 * @return true ��������ļ���������ȷ����
	 * @throws Exception �κ��쳣����ʾ����ʧ��
	 */
	public static boolean loadConfigure() throws Exception {

//		if (entry.DEBUG) {
//			ConfigureX.MYSELFID = 3477852529L;
//		} else {
		ConfigureX.MYSELFID = JcqApp.CQ.getLoginQQ();
//		}

		if (!ConfigureX.FOLDER_CONF.exists()) {
			ConfigureX.FOLDER_CONF.mkdirs();
		}

		if (!ConfigureX.FOLDER_CONF.isDirectory()) {
			throw new IOException("Main configure folder path is a file:" + ConfigureX.FOLDER_CONF.getAbsolutePath());
		}

		if (!ConfigureX.FILE_BLACKLIST.exists()) {
			ConfigureX.FILE_BLACKLIST.createNewFile();
		}

		if (!ConfigureX.FILE_USERIGNORE.exists()) {
			ConfigureX.FILE_USERIGNORE.createNewFile();
		}

		if (!ConfigureX.FILE_DISZIGNORE.exists()) {
			ConfigureX.FILE_DISZIGNORE.createNewFile();
		}

		if (!ConfigureX.FILE_GROPIGNORE.exists()) {
			ConfigureX.FILE_GROPIGNORE.createNewFile();
		}

		if (!ConfigureX.FILE_CONFIG.exists()) {
			ConfigureX.FILE_CONFIG.createNewFile();
			final FileWriter writer = new FileWriter(ConfigureX.FILE_CONFIG);
			// @formatter:off
			writer.write(
				"operator=\r\n" +
				"enable_trigger_user=false\r\n" +
				"enable_trigger_disz=false\r\n" +
				"enable_trigger_grop=false\r\n" +
				"enable_listener_user=false\r\n" +
				"enable_listener_disz=false\r\n" +
				"enable_listener_grop=false\r\n" +
				"enable_blacklist=false\r\n" +
				"enable_userignore=false\r\n" +
				"enable_diszignore=false\r\n" +
				"enable_gropignore=false\r\n" +
				"enable_ddnsclient=false\r\n" +
				"ddnsapi_clientua=BTSCoolQ/1.0\r\n" +
				"ddnsapi_hostname=\r\n" +
				"ddnsapi_password="
			);
			// @formatter:on
			writer.flush();
			writer.close();
			throw new Exception("������������Ҫ��д�����ļ�");
		}

		ConfigureX.property.load(new FileInputStream(ConfigureX.FILE_CONFIG));
		ConfigureX.OPERATOR = Long.parseLong(ConfigureX.property.getProperty("operator", "0"));

		ConfigureX.ENABLE_TRIGGER_USER = Boolean.parseBoolean(ConfigureX.property.getProperty("enable_trigger_user", "false"));
		ConfigureX.ENABLE_TRIGGER_DISZ = Boolean.parseBoolean(ConfigureX.property.getProperty("enable_trigger_disz", "false"));
		ConfigureX.ENABLE_TRIGGER_GROP = Boolean.parseBoolean(ConfigureX.property.getProperty("enable_trigger_grop", "false"));
		ConfigureX.ENABLE_LISENTER_USER = Boolean.parseBoolean(ConfigureX.property.getProperty("enable_listener_user", "false"));
		ConfigureX.ENABLE_LISENTER_DISZ = Boolean.parseBoolean(ConfigureX.property.getProperty("enable_listener_disz", "false"));
		ConfigureX.ENABLE_LISENTER_GROP = Boolean.parseBoolean(ConfigureX.property.getProperty("enable_listener_grop", "false"));
		ConfigureX.ENABLE_BLACKLIST = Boolean.parseBoolean(ConfigureX.property.getProperty("enable_blacklist", "false"));
		ConfigureX.ENABLE_USERIGNORE = Boolean.parseBoolean(ConfigureX.property.getProperty("enable_userignore", "false"));
		ConfigureX.ENABLE_DISZIGNORE = Boolean.parseBoolean(ConfigureX.property.getProperty("enable_diszignore", "false"));
		ConfigureX.ENABLE_GROPIGNORE = Boolean.parseBoolean(ConfigureX.property.getProperty("enable_gropignore", "false"));
		ConfigureX.ENABLE_DDNSCLIENT = Boolean.parseBoolean(ConfigureX.property.getProperty("enable_ddnsclient", "false"));
		ConfigureX.DDNSAPI_CLIENTUA = ConfigureX.property.getProperty("ddnsapi_clientua", "BTSCoolQ/1.0");
		ConfigureX.DDNSAPI_HOSTNAME = ConfigureX.property.getProperty("ddnsapi_hostname", "");
		ConfigureX.DDNSAPI_PASSWORD = ConfigureX.property.getProperty("ddnsapi_password", "");

		Scheduler_DDNS.init(ConfigureX.ENABLE_DDNSCLIENT, ConfigureX.DDNSAPI_CLIENTUA, ConfigureX.DDNSAPI_HOSTNAME, ConfigureX.DDNSAPI_PASSWORD);

		return true;
	}

	public static long OPERATOR() {
		return ConfigureX.OPERATOR;
	}

	public static long MYSELFID() {
		return ConfigureX.MYSELFID;
	}

	public static boolean ENABLE_TRIGGER_USER() {
		return ConfigureX.ENABLE_TRIGGER_USER;
	}

	public static boolean ENABLE_TRIGGER_DISZ() {
		return ConfigureX.ENABLE_TRIGGER_DISZ;
	}

	public static boolean ENABLE_TRIGGER_GROP() {
		return ConfigureX.ENABLE_TRIGGER_GROP;
	}

	public static boolean ENABLE_LISENTER_USER() {
		return ConfigureX.ENABLE_LISENTER_USER;
	}

	public static boolean ENABLE_LISENTER_DISZ() {
		return ConfigureX.ENABLE_LISENTER_DISZ;
	}

	public static boolean ENABLE_LISENTER_GROP() {
		return ConfigureX.ENABLE_LISENTER_GROP;
	}

	public static boolean ENABLE_BLACKLIST() {
		return ConfigureX.ENABLE_BLACKLIST;
	}

	public static boolean ENABLE_USERIGNORE() {
		return ConfigureX.ENABLE_USERIGNORE;
	}

	public static boolean ENABLE_DISZIGNORE() {
		return ConfigureX.ENABLE_DISZIGNORE;
	}

	public static boolean ENABLE_GROPIGNORE() {
		return ConfigureX.ENABLE_GROPIGNORE;
	}

}
