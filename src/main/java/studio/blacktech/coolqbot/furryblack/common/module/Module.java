package studio.blacktech.coolqbot.furryblack.common.module;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.Properties;


import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.LoggerX.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.exception.NotAFolderException;
import studio.blacktech.coolqbot.furryblack.common.message.Message;


public abstract class Module implements Serializable {

	private static final long serialVersionUID = 1L;

	private String MODULE_PACKAGENAME;
	private String MODULE_COMMANDNAME;
	private String MODULE_DISPLAYNAME;
	private String MODULE_DESCRIPTION;
	private String MODULE_VERSION;
	private String[] MODULE_USAGE;
	private String[] MODULE_PRIVACY_STORED;
	private String[] MODULE_PRIVACY_CACHED;
	private String[] MODULE_PRIVACY_OBTAIN;

	public String MODULE_FULLHELP;

	protected Properties CONFIG = new Properties();

	protected File FOLDER_ROOT;

	protected File FOLDER_CONF;
	protected File FOLDER_DATA;
	protected File FOLDER_LOGS;

	protected File FILE_CONFIG;

	protected boolean NEW_CONFIG = false;

	protected LoggerX logger;

	// @formatter:off

	public Module(
			String MODULE_PACKAGENAME,
			String MODULE_COMMANDNAME,
			String MODULE_DISPLAYNAME,
			String MODULE_DESCRIPTION,
			String MODULE_VERSION,
			String[] MODULE_USAGE,
			String[] MODULE_PRIVACY_STORED,
			String[] MODULE_PRIVACY_CACHED,
			String[] MODULE_PRIVACY_OBTAIN
			) throws Exception {

		this.MODULE_PACKAGENAME = MODULE_PACKAGENAME;
		this.MODULE_COMMANDNAME = MODULE_COMMANDNAME;
		this.MODULE_DISPLAYNAME = MODULE_DISPLAYNAME;
		this.MODULE_DESCRIPTION = MODULE_DESCRIPTION;
		this.MODULE_VERSION = MODULE_VERSION;
		this.MODULE_USAGE = MODULE_USAGE;
		this.MODULE_PRIVACY_STORED = MODULE_PRIVACY_STORED;
		this.MODULE_PRIVACY_CACHED = MODULE_PRIVACY_CACHED;
		this.MODULE_PRIVACY_OBTAIN = MODULE_PRIVACY_OBTAIN;

		this.MODULE_FULLHELP = this.genFullHelp();

		this.FOLDER_ROOT = Paths.get(entry.getAppDirectory(), this.MODULE_PACKAGENAME).toFile();
		this.FOLDER_CONF =  Paths.get(entry.getAppDirectory(), this.MODULE_PACKAGENAME , "conf").toFile();
		this.FOLDER_DATA =  Paths.get(entry.getAppDirectory(), this.MODULE_PACKAGENAME , "data").toFile();
		this.FOLDER_LOGS =  Paths.get(entry.getAppDirectory(), this.MODULE_PACKAGENAME , "logs").toFile();
		this.FILE_CONFIG =  Paths.get(entry.getAppDirectory(), this.MODULE_PACKAGENAME , "config.properties").toFile();

		this.logger = new LoggerX(this);

	}

	// @formatter:on

	public abstract boolean init() throws Exception;

	public abstract boolean boot() throws Exception;

	public abstract boolean save() throws Exception;

	public abstract boolean shut() throws Exception;

	public abstract String[] exec(Message message) throws Exception;

	public abstract String[] generateReport(int mode, Message message, Object... parameters);

	public abstract void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid)
			throws Exception;

	public abstract void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid)
			throws Exception;

	public void initAppFolder() throws Exception {

		if (this.FOLDER_ROOT.exists()) {

			if (!this.FOLDER_ROOT.isDirectory()) {

				throw new NotAFolderException("文件夹被文件占位：" + this.FOLDER_ROOT.getAbsolutePath());

			}

		} else {

			this.logger.seek("创建目录", this.FOLDER_ROOT.getAbsolutePath());
			this.FOLDER_ROOT.mkdirs();

		}

	}

	public void initConfFolder() throws Exception {

		if (this.FOLDER_CONF.exists()) {

			if (!this.FOLDER_CONF.isDirectory()) {

				throw new NotAFolderException("文件夹被文件占位：" + this.FOLDER_CONF.getAbsolutePath());

			}

		} else {

			this.logger.seek("创建目录", this.FOLDER_CONF.getAbsolutePath());
			this.FOLDER_CONF.mkdirs();

		}

	}

	public void initDataFolder() throws Exception {

		if (this.FOLDER_DATA.exists()) {

			if (!this.FOLDER_DATA.isDirectory()) {

				throw new NotAFolderException("文件夹被文件占位：" + this.FOLDER_DATA.getAbsolutePath());

			}

		} else {

			this.logger.seek("创建目录", this.FOLDER_DATA.getAbsolutePath());
			this.FOLDER_DATA.mkdirs();

		}

	}

	public void initLogsFolder() throws Exception {

		if (this.FOLDER_LOGS.exists()) {

			if (!this.FOLDER_LOGS.isDirectory()) {

				throw new NotAFolderException("文件夹被文件占位：" + this.FOLDER_LOGS.getAbsolutePath());

			}

		} else {

			this.logger.seek("创建目录", this.FOLDER_LOGS.getName());
			this.FOLDER_LOGS.mkdirs();

		}

	}

	public void initPropertiesConfigurtion() throws Exception {

		if (!this.FILE_CONFIG.exists()) {

			this.logger.seek("创建文件", this.FILE_CONFIG.getAbsolutePath());
			this.FILE_CONFIG.createNewFile();
			this.NEW_CONFIG = true;

		}

	}

	protected void loadConfig() throws Exception {

		this.CONFIG.load(new FileInputStream(this.FILE_CONFIG));

	}

	protected void saveConfig() throws Exception {

		this.CONFIG.store(new FileOutputStream(this.FILE_CONFIG), null);

	}

	public String MODULE_PACKAGENAME() {

		return this.MODULE_PACKAGENAME;

	}

	public String MODULE_COMMANDNAME() {

		return this.MODULE_COMMANDNAME;

	}

	public String MODULE_DISPLAYNAME() {

		return this.MODULE_DISPLAYNAME;

	}

	public String MODULE_DESCRIPTION() {

		return this.MODULE_DESCRIPTION;

	}

	public String MODULE_FULLHELP() {

		return this.MODULE_FULLHELP;

	}

	public String genFullHelp() {

		StringBuilder builder = new StringBuilder();
		builder.append("模块：" + this.MODULE_PACKAGENAME + "v" + this.MODULE_VERSION + "\r\n");
		builder.append("名称：" + this.MODULE_DISPLAYNAME + "\r\n");
		builder.append("用途：" + this.MODULE_DESCRIPTION + "\r\n");
		builder.append("命令：" + this.MODULE_COMMANDNAME + "\r\n");
		builder.append("用法：");

		if (this.MODULE_USAGE.length == 0) {

			builder.append("无");

		} else {

			for (String temp : this.MODULE_USAGE) {

				builder.append(temp + "\r\n");

			}

		}

		builder.append("隐私声明：" + "\r\n");

		builder.append("存储：" + "\r\n");

		if (this.MODULE_PRIVACY_STORED.length == 0) {

			builder.append("无" + "\r\n");

		} else {

			builder.append(this.MODULE_PRIVACY_STORED.length);

			for (String temp : this.MODULE_PRIVACY_STORED) {

				builder.append(" " + temp + "\r\n");

			}

		}
		builder.append("缓存：" + "\r\n");

		if (this.MODULE_PRIVACY_CACHED.length == 0) {

			builder.append("无" + "\r\n");

		} else {

			builder.append(this.MODULE_PRIVACY_CACHED.length);

			for (String temp : this.MODULE_PRIVACY_CACHED) {

				builder.append(" " + temp + "\r\n");

			}

		}
		builder.append("获取：" + "\r\n");

		if (this.MODULE_PRIVACY_OBTAIN.length == 0) {

			builder.append("无" + "\r\n");

		} else {

			builder.append(this.MODULE_PRIVACY_OBTAIN.length);

			for (String temp : this.MODULE_PRIVACY_OBTAIN) {

				builder.append(" " + temp + "\r\n");

			}

		}
		return builder.toString();

	}

}
