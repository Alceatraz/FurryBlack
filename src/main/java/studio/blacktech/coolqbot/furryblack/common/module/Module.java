package studio.blacktech.coolqbot.furryblack.common.module;


import studio.blacktech.coolqbot.furryblack.common.exception.NotAFolderException;
import studio.blacktech.coolqbot.furryblack.common.loggerx.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.entry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.Properties;


public abstract class Module implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String MODULE_PACKAGENAME;
	private final String MODULE_COMMANDNAME;
	private final String MODULE_DISPLAYNAME;
	private final String MODULE_DESCRIPTION;
	private final String MODULE_VERSION;
	private final String[] MODULE_USAGE;
	private final String[] MODULE_PRIVACY_STORED;
	private final String[] MODULE_PRIVACY_CACHED;
	private final String[] MODULE_PRIVACY_OBTAIN;

	public String MODULE_FULLHELP;

	protected Properties CONFIG = new Properties();

	protected File FOLDER_ROOT;
	protected File FOLDER_CONF;
	protected File FOLDER_DATA;
	protected File FOLDER_LOGS;
	protected File FILE_CONFIG;

	protected boolean ENABLE_USER = false;
	protected boolean ENABLE_DISZ = false;
	protected boolean ENABLE_GROP = false;

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

        MODULE_FULLHELP = genFullHelp();

        FOLDER_ROOT = Paths.get(entry.getAppDirectory(), this.MODULE_PACKAGENAME).toFile();
        FOLDER_CONF = Paths.get(entry.getAppDirectory(), this.MODULE_PACKAGENAME, "conf").toFile();
        FOLDER_DATA = Paths.get(entry.getAppDirectory(), this.MODULE_PACKAGENAME, "data").toFile();
        FOLDER_LOGS = Paths.get(entry.getAppDirectory(), this.MODULE_PACKAGENAME, "logs").toFile();
        FILE_CONFIG = Paths.get(entry.getAppDirectory(), this.MODULE_PACKAGENAME, "config.properties").toFile();

        logger = new LoggerX(this);

    }

    // @formatter:on

	public abstract boolean init() throws Exception;

	public abstract boolean boot() throws Exception;

	public abstract boolean save() throws Exception;

	public abstract boolean shut() throws Exception;

	public abstract String[] exec(Message message) throws Exception;

	public abstract String[] generateReport(Message message);


	public void doGroupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) throws Exception {
		if (ENABLE_USER || ENABLE_DISZ || ENABLE_GROP) groupMemberIncrease(typeid, sendtime, gropid, operid, userid);
	}

	public void doGroupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) throws Exception {
		if (ENABLE_USER || ENABLE_DISZ || ENABLE_GROP) groupMemberDecrease(typeid, sendtime, gropid, operid, userid);
	}


	public abstract void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) throws Exception;

	public abstract void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) throws Exception;

	public void initAppFolder() throws Exception {
		if (FOLDER_ROOT.exists()) {
			if (!FOLDER_ROOT.isDirectory()) { throw new NotAFolderException("文件夹被文件占位：" + FOLDER_ROOT.getAbsolutePath()); }
		} else {
			logger.seek("创建目录", FOLDER_ROOT.getAbsolutePath());
			FOLDER_ROOT.mkdirs();
		}
	}

	public void initConfFolder() throws Exception {
		if (FOLDER_CONF.exists()) {
			if (!FOLDER_CONF.isDirectory()) { throw new NotAFolderException("文件夹被文件占位：" + FOLDER_CONF.getAbsolutePath()); }
		} else {
			logger.seek("创建目录", FOLDER_CONF.getAbsolutePath());
			FOLDER_CONF.mkdirs();
		}
	}

	public void initDataFolder() throws Exception {
		if (FOLDER_DATA.exists()) {
			if (!FOLDER_DATA.isDirectory()) { throw new NotAFolderException("文件夹被文件占位：" + FOLDER_DATA.getAbsolutePath()); }
		} else {
			logger.seek("创建目录", FOLDER_DATA.getAbsolutePath());
			FOLDER_DATA.mkdirs();
		}
	}

	public void initLogsFolder() throws Exception {
		if (FOLDER_LOGS.exists()) {
			if (!FOLDER_LOGS.isDirectory()) { throw new NotAFolderException("文件夹被文件占位：" + FOLDER_LOGS.getAbsolutePath()); }
		} else {
			logger.seek("创建目录", FOLDER_LOGS.getName());
			FOLDER_LOGS.mkdirs();
		}
	}

	public void initPropertiesConfigurtion() throws Exception {
		if (!FILE_CONFIG.exists()) {
			logger.seek("创建文件", FILE_CONFIG.getAbsolutePath());
			FILE_CONFIG.createNewFile();
			NEW_CONFIG = true;
		}
	}

	protected void loadConfig() throws Exception {
		CONFIG.load(new FileInputStream(FILE_CONFIG));
	}

	protected void saveConfig() throws Exception {
		CONFIG.store(new FileOutputStream(FILE_CONFIG), null);
	}

	public String MODULE_PACKAGENAME() {
		return MODULE_PACKAGENAME;
	}

	public String MODULE_COMMANDNAME() {
		return MODULE_COMMANDNAME;
	}

	public String MODULE_DISPLAYNAME() {
		return MODULE_DISPLAYNAME;
	}

	public String MODULE_DESCRIPTION() {
		return MODULE_DESCRIPTION;
	}

	public String MODULE_FULLHELP() {
		return MODULE_FULLHELP;
	}

	public String genFullHelp() {

		StringBuilder builder = new StringBuilder();

		builder.append("模块：" + MODULE_PACKAGENAME + "v" + MODULE_VERSION + "\r\n");
		builder.append("名称：" + MODULE_DISPLAYNAME + "\r\n");
		builder.append("用途：" + MODULE_DESCRIPTION + "\r\n");
		builder.append("命令：" + MODULE_COMMANDNAME + "\r\n");
		builder.append("用法：");
		if (MODULE_USAGE.length == 0) {
			builder.append("无");
		} else {
			for (String temp : MODULE_USAGE) {
				builder.append(temp + "\r\n");
			}
		}
		builder.append("隐私声明：" + "\r\n");
		builder.append("存储：" + "\r\n");
		if (MODULE_PRIVACY_STORED.length == 0) {
			builder.append("无" + "\r\n");
		} else {
			builder.append(MODULE_PRIVACY_STORED.length);
			for (String temp : MODULE_PRIVACY_STORED) {
				builder.append(" " + temp + "\r\n");
			}
		}
		builder.append("缓存：" + "\r\n");
		if (MODULE_PRIVACY_CACHED.length == 0) {
			builder.append("无" + "\r\n");
		} else {
			builder.append(MODULE_PRIVACY_CACHED.length);
			for (String temp : MODULE_PRIVACY_CACHED) {
				builder.append(" " + temp + "\r\n");
			}
		}
		builder.append("获取：" + "\r\n");
		if (MODULE_PRIVACY_OBTAIN.length == 0) {
			builder.append("无" + "\r\n");
		} else {
			builder.append(MODULE_PRIVACY_OBTAIN.length);
			for (String temp : MODULE_PRIVACY_OBTAIN) {
				builder.append(" " + temp + "\r\n");
			}
		}
		return builder.toString();

	}

}
