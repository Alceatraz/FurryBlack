package studio.blacktech.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

import com.sobte.cqp.jcq.event.JcqApp;
import com.sobte.cqp.jcq.event.JcqAppAbstract;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.scheduler.Worker_DDNS;

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

	private static boolean ENABLE_BLACKLIST = true;
	private static boolean ENABLE_USERIGNORE = true;
	private static boolean ENABLE_DISZIGNORE = true;
	private static boolean ENABLE_GROPIGNORE = true;

	private static String DDNSAPI_CLIENTUA;
	private static String DDNSAPI_HOSTNAME;
	private static String DDNSAPI_PASSWORD;

	/***
	 * 加载主配置文件 如果不存在将创建默认配置文件
	 *
	 * @return true 如果配置文件存在且正确加载
	 * @throws Exception 任何异常都表示加载失败
	 */
	public static boolean loadConfigure() throws Exception {

		if (entry.DEBUG) {
			ConfigureX.MYSELFID = 3477852529L;
		} else {
			ConfigureX.MYSELFID = JcqApp.CQ.getLoginQQ();
		}

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
				"### CoolQ Setting ##############################################################\r\n" +
				"\r\n" +
				"# CoolQ Admin UID\r\n" +
				"operator=\r\n" +
				"\r\n" +
				"### Ignore list ################################################################\r\n" +
				"\r\n" +
				"# any word contain in this list will ignore\r\n" +
				"enable_blacklist=false\r\n" +
				"\r\n" +
				"# user id in this list will ignore in private message\r\n" +
				"enable_userignore=false\r\n" +
				"\r\n" +
				"# user id and disz id will ignore user in specific discuss\r\n" +
				"enable_diszignore=false\r\n" +
				"\r\n" +
				"# user id and group id will ignore user in specific groupchat\r\n" +
				"enable_gropignore=false\r\n" +
				"\r\n" +
				"### PubYUN API #################################################################\r\n" +
				"\r\n" +
				"# ddns user agent\r\n" +
				"ddnsapi_clientua=BTSCoolQ/1.0\r\n" +
				"\r\n" +
				"# ddns hostname\r\n" +
				"ddnsapi_hostname=\r\n" +
				"\r\n" +
				"# ddns password\r\n" +
				"ddnsapi_password="
			);
			// @formatter:on
			writer.flush();
			writer.close();
		}

		ConfigureX.property.load(new FileInputStream(ConfigureX.FILE_CONFIG));

		ConfigureX.OPERATOR = Long.parseLong(ConfigureX.property.getProperty("operator", "0"));

		ConfigureX.ENABLE_BLACKLIST = Boolean.parseBoolean(ConfigureX.property.getProperty("enable_blacklist", "false"));

		ConfigureX.ENABLE_USERIGNORE = Boolean.parseBoolean(ConfigureX.property.getProperty("enable_userignore", "false"));
		ConfigureX.ENABLE_DISZIGNORE = Boolean.parseBoolean(ConfigureX.property.getProperty("enable_diszignore", "false"));
		ConfigureX.ENABLE_GROPIGNORE = Boolean.parseBoolean(ConfigureX.property.getProperty("enable_gropignore", "false"));

		ConfigureX.DDNSAPI_CLIENTUA = ConfigureX.property.getProperty("ddnsapi_clientua", "BTSCoolQ/1.0");
		ConfigureX.DDNSAPI_HOSTNAME = ConfigureX.property.getProperty("ddnsapi_hostname", "");
		ConfigureX.DDNSAPI_PASSWORD = ConfigureX.property.getProperty("ddnsapi_password", "");

		Worker_DDNS.init(ConfigureX.DDNSAPI_CLIENTUA, ConfigureX.DDNSAPI_HOSTNAME, ConfigureX.DDNSAPI_PASSWORD);

		return true;
	}

	public static long OPERATOR() {
		return ConfigureX.OPERATOR;
	}

	public static long MYSELFID() {
		return ConfigureX.MYSELFID;
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
