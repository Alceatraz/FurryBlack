package studio.blacktech.coolqbot.furryblack.common.module;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.Properties;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.LoggerX.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.exception.InitializationException;
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

	protected File FOLDER_CONF;
	protected File FOLDER_DATA;
	protected File FILE_CONFIG;

	protected boolean NEW_CONFIG = false;

	private boolean init_conf_folder = false;

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

	}
	// @formatter:on

	public void initConfFolder() throws Exception {
		this.FOLDER_CONF = Paths.get(entry.FOLDER_CONF().getAbsolutePath(), this.MODULE_PACKAGENAME).toFile();
		if (!this.FOLDER_CONF.exists()) { this.FOLDER_CONF.mkdirs(); }
		if (!this.FOLDER_CONF.isDirectory()) { throw new NotAFolderException("配置文件夹被文件占位：" + this.FOLDER_CONF.getAbsolutePath()); }
		this.init_conf_folder = true;
	}

	public void initDataFolder() throws Exception {
		this.FOLDER_DATA = Paths.get(entry.FOLDER_DATA().getAbsolutePath(), this.MODULE_PACKAGENAME).toFile();
		if (!this.FOLDER_DATA.exists()) { this.FOLDER_DATA.mkdirs(); }
		if (!this.FOLDER_DATA.isDirectory()) { throw new NotAFolderException("配置文件夹被文件占位：" + this.FOLDER_DATA.getAbsolutePath()); }
	}

	public void initCofigurtion() throws Exception {
		if (this.init_conf_folder) {
			this.FILE_CONFIG = Paths.get(this.FOLDER_CONF.getAbsolutePath(), "config.properties").toFile();
			if (!this.FILE_CONFIG.exists()) {
				this.FILE_CONFIG.createNewFile();
				this.NEW_CONFIG = true;
			}
		} else {
			throw new InitializationException("还未对配置目录初始化");
		}
	}

	public abstract void init(LoggerX logger) throws Exception;

	public abstract void boot(LoggerX logger) throws Exception;

	public abstract void shut(LoggerX logger) throws Exception;

	public abstract void save(LoggerX logger) throws Exception;

	public abstract void reload(LoggerX logger) throws Exception;

	public abstract void exec(LoggerX logger, Message message) throws Exception;

	public abstract void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) throws Exception;

	public abstract void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) throws Exception;

	public abstract String[] generateReport(int mode, Message message, Object... parameters);

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
