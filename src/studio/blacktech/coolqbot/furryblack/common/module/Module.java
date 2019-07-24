package studio.blacktech.coolqbot.furryblack.common.module;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.Properties;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.NotAFolderException;
import studio.blacktech.coolqbot.furryblack.common.message.Message;

public abstract class Module {

	private String MODULE_PACKAGENAME;
	private String MODULE_COMMANDNAME;
	private String MODULE_DISPLAYNAME;
	private String MODULE_DESCRIPTION;
	private String MODULE_VERSION;
	private String[] MODULE_USAGE;
	private String[] MODULE_PRIVACY_TRIGER;
	private String[] MODULE_PRIVACY_LISTEN;
	private String[] MODULE_PRIVACY_STORED;
	private String[] MODULE_PRIVACY_CACHED;
	private String[] MODULE_PRIVACY_OBTAIN;

	public String MODULE_FULLHELP;

	protected Properties CONFIG = new Properties();

	protected File FOLDER_CONF;
	protected File FOLDER_DATA;
	protected File FILE_CONFIG;

	protected boolean NEW_CONFIG = false;

	public Module(String MODULE_PACKAGENAME, String MODULE_COMMANDNAME, String MODULE_DISPLAYNAME, String MODULE_DESCRIPTION, String MODULE_VERSION, String[] MODULE_USAGE, String[] MODULE_PRIVACY_TRIGER, String[] MODULE_PRIVACY_LISTEN, String[] MODULE_PRIVACY_STORED, String[] MODULE_PRIVACY_CACHED, String[] MODULE_PRIVACY_OBTAIN) throws Exception {

		this.MODULE_PACKAGENAME = MODULE_PACKAGENAME;
		this.MODULE_COMMANDNAME = MODULE_COMMANDNAME;
		this.MODULE_DISPLAYNAME = MODULE_DISPLAYNAME;
		this.MODULE_DESCRIPTION = MODULE_DESCRIPTION;
		this.MODULE_VERSION = MODULE_VERSION;
		this.MODULE_USAGE = MODULE_USAGE;
		this.MODULE_PRIVACY_TRIGER = MODULE_PRIVACY_TRIGER;
		this.MODULE_PRIVACY_LISTEN = MODULE_PRIVACY_LISTEN;
		this.MODULE_PRIVACY_STORED = MODULE_PRIVACY_STORED;
		this.MODULE_PRIVACY_CACHED = MODULE_PRIVACY_CACHED;
		this.MODULE_PRIVACY_OBTAIN = MODULE_PRIVACY_OBTAIN;

		this.MODULE_FULLHELP = this.genFullHelp();

		this.FOLDER_CONF = Paths.get(entry.FOLDER_CONF().getAbsolutePath(), this.MODULE_PACKAGENAME).toFile();
		this.FOLDER_DATA = Paths.get(entry.FOLDER_DATA().getAbsolutePath(), this.MODULE_PACKAGENAME).toFile();

		this.FILE_CONFIG = Paths.get(this.FOLDER_CONF.getAbsolutePath(), "config.properties").toFile();

		if (!this.FOLDER_CONF.exists()) { this.FOLDER_CONF.mkdirs(); }
		if (!this.FOLDER_DATA.exists()) { this.FOLDER_DATA.mkdirs(); }

		if (!this.FOLDER_CONF.isDirectory()) { throw new NotAFolderException("配置文件夹被文件占位：" + this.FOLDER_CONF.getAbsolutePath()); }
		if (!this.FOLDER_DATA.isDirectory()) { throw new NotAFolderException("配置文件夹被文件占位：" + this.FOLDER_DATA.getAbsolutePath()); }

		if (!this.FILE_CONFIG.exists()) {
			this.FILE_CONFIG.createNewFile();
			this.NEW_CONFIG = true;
		}
	}

	public abstract void init(LoggerX logger) throws Exception;

	public abstract void boot(LoggerX logger) throws Exception;

	public abstract void shut(LoggerX logger) throws Exception;

	public abstract void reload(LoggerX logger) throws Exception;

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
		builder.append("模块：");
		builder.append(this.MODULE_PACKAGENAME);
		builder.append("\r\n");
		builder.append(this.MODULE_COMMANDNAME);
		builder.append(" > ");
		builder.append(this.MODULE_DISPLAYNAME);
		builder.append(" v");
		builder.append(this.MODULE_VERSION);
		builder.append("\r\b");
		builder.append(this.MODULE_DESCRIPTION);
		builder.append("\r\n命令用法：");
		if (this.MODULE_USAGE.length == 0) {
			builder.append("无");
		} else {
			for (String temp : this.MODULE_USAGE) {
				builder.append("\r\n");
				builder.append(temp);
			}
		}
		builder.append("\r\n隐私声明：");
		builder.append("\r\n触发器：");
		if (this.MODULE_PRIVACY_TRIGER.length == 0) {
			builder.append("无");
		} else {
			builder.append(this.MODULE_PRIVACY_TRIGER.length);
			for (String temp : this.MODULE_PRIVACY_TRIGER) {
				builder.append("\r\n  ");
				builder.append(temp);
			}
		}
		builder.append("\r\n监听器：");
		if (this.MODULE_PRIVACY_LISTEN.length == 0) {
			builder.append("无");
		} else {
			builder.append(this.MODULE_PRIVACY_LISTEN.length);
			for (String temp : this.MODULE_PRIVACY_LISTEN) {
				builder.append("\r\n  ");
				builder.append(temp);
			}
		}
		builder.append("\r\n存储：");
		if (this.MODULE_PRIVACY_STORED.length == 0) {
			builder.append("无");
		} else {
			builder.append(this.MODULE_PRIVACY_STORED.length);
			for (String temp : this.MODULE_PRIVACY_STORED) {
				builder.append("\r\n  ");
				builder.append(temp);
			}
		}
		builder.append("\r\n缓存：");
		if (this.MODULE_PRIVACY_CACHED.length == 0) {
			builder.append("无");
		} else {
			builder.append(this.MODULE_PRIVACY_CACHED.length);
			for (String temp : this.MODULE_PRIVACY_CACHED) {
				builder.append("\r\n  ");
				builder.append(temp);
			}
		}
		builder.append("\r\n获取：");
		if (this.MODULE_PRIVACY_OBTAIN.length == 0) {
			builder.append("无");
		} else {
			builder.append(this.MODULE_PRIVACY_OBTAIN.length);
			for (String temp : this.MODULE_PRIVACY_OBTAIN) {
				builder.append("\r\n  ");
				builder.append(temp);
			}
		}
		return builder.toString();
	}

}
