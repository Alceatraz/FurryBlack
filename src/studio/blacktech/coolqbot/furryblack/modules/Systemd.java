package studio.blacktech.coolqbot.furryblack.modules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;

import org.meowy.cqp.jcq.entity.Group;
import org.meowy.cqp.jcq.entity.Member;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.LoggerX.BufferX;
import studio.blacktech.coolqbot.furryblack.common.LoggerX.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.exception.CantReinitializationException;
import studio.blacktech.coolqbot.furryblack.common.exception.InitializationException;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.Module;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleListener;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleScheduler;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleTrigger;
import studio.blacktech.coolqbot.furryblack.modules.Executor.Executor_acon;
import studio.blacktech.coolqbot.furryblack.modules.Executor.Executor_admin;
import studio.blacktech.coolqbot.furryblack.modules.Executor.Executor_chou;
import studio.blacktech.coolqbot.furryblack.modules.Executor.Executor_dice;
import studio.blacktech.coolqbot.furryblack.modules.Executor.Executor_echo;
import studio.blacktech.coolqbot.furryblack.modules.Executor.Executor_food;
import studio.blacktech.coolqbot.furryblack.modules.Executor.Executor_jrjp;
import studio.blacktech.coolqbot.furryblack.modules.Executor.Executor_jrrp;
import studio.blacktech.coolqbot.furryblack.modules.Executor.Executor_kong;
import studio.blacktech.coolqbot.furryblack.modules.Executor.Executor_roll;
import studio.blacktech.coolqbot.furryblack.modules.Executor.Executor_roulette;
import studio.blacktech.coolqbot.furryblack.modules.Executor.Executor_time;
import studio.blacktech.coolqbot.furryblack.modules.Executor.Executor_zhan;
import studio.blacktech.coolqbot.furryblack.modules.Listener.Listener_TopSpeak;
import studio.blacktech.coolqbot.furryblack.modules.Scheduler.Scheduler_Dynamic;
import studio.blacktech.coolqbot.furryblack.modules.Trigger.Trigger_UserDeny;
import studio.blacktech.coolqbot.furryblack.modules.Trigger.Trigger_WordDeny;

/**
 * FurryBlack的核心路由
 *
 * 持有所有模块的实例
 *
 * 管理所有模块的生命周期
 *
 * 响应所有事件
 *
 * 接受所有消息
 *
 * 发送所有消息
 *
 * 昵称表管理
 *
 * Systemd本身也继承自Module始祖模块，包含一般方法。
 *
 * @author Alceatraz Warprays
 *
 */
public class Systemd extends Module {

	private static final long serialVersionUID = 1L;

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "Core_Systemd";
	private static String MODULE_COMMANDNAME = "systemd";
	private static String MODULE_DISPLAYNAME = "核心模块";
	private static String MODULE_DESCRIPTION = "管理所有功能模块并路由所有消息";
	private static String MODULE_VERSION = "28.0";
	private static String[] MODULE_USAGE = new String[] {};
	private static String[] MODULE_PRIVACY_STORED = new String[] {};
	private static String[] MODULE_PRIVACY_CACHED = new String[] {};
	private static String[] MODULE_PRIVACY_OBTAIN = new String[] {};

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================

	private int COUNT_USER_MESSAGE = 0;
	private int COUNT_DISZ_MESSAGE = 0;
	private int COUNT_GROP_MESSAGE = 0;

	private long USERID_CQBOT = 0;
	private long USERID_ADMIN = 0;

	private boolean ENABLE_SCHEDULER = false;
	private boolean ENABLE_TRIGGER_USER = false;
	private boolean ENABLE_TRIGGER_DISZ = false;
	private boolean ENABLE_TRIGGER_GROP = false;
	private boolean ENABLE_LISENTER_USER = false;
	private boolean ENABLE_LISENTER_DISZ = false;
	private boolean ENABLE_LISENTER_GROP = false;
	private boolean ENABLE_EXECUTOR_USER = false;
	private boolean ENABLE_EXECUTOR_DISZ = false;
	private boolean ENABLE_EXECUTOR_GROP = false;

	private String CONFIG_SCHEDULER;
	private String CONFIG_TRIGGER_USER;
	private String CONFIG_TRIGGER_DISZ;
	private String CONFIG_TRIGGER_GROP;
	private String CONFIG_LISENTER_USER;
	private String CONFIG_LISENTER_DISZ;
	private String CONFIG_LISENTER_GROP;
	private String CONFIG_EXECUTOR_USER;
	private String CONFIG_EXECUTOR_DISZ;
	private String CONFIG_EXECUTOR_GROP;

	private String[] LIST_SCHEDULER = {};
	private String[] LIST_TRIGGER_USER = {};
	private String[] LIST_TRIGGER_DISZ = {};
	private String[] LIST_TRIGGER_GROP = {};
	private String[] LIST_LISENTER_USER = {};
	private String[] LIST_LISENTER_DISZ = {};
	private String[] LIST_LISENTER_GROP = {};
	private String[] LIST_EXECUTOR_USER = {};
	private String[] LIST_EXECUTOR_DISZ = {};
	private String[] LIST_EXECUTOR_GROP = {};

	private TreeMap<String, ModuleScheduler> SCHEDULER_INSTANCE;
	private ArrayList<ModuleScheduler> SCHEDULER_ENABLED;

	private TreeMap<String, ModuleTrigger> TRIGGER_INSTANCE;
	private ArrayList<ModuleTrigger> TRIGGER_USER;
	private ArrayList<ModuleTrigger> TRIGGER_DISZ;
	private ArrayList<ModuleTrigger> TRIGGER_GROP;

	private TreeMap<String, ModuleListener> LISTENER_INSTANCE;
	private ArrayList<ModuleListener> LISTENER_USER;
	private ArrayList<ModuleListener> LISTENER_DISZ;
	private ArrayList<ModuleListener> LISTENER_GROP;

	private TreeMap<String, ModuleExecutor> EXECUTOR_INSTANCE;
	private TreeMap<String, ModuleExecutor> EXECUTOR_USER;
	private TreeMap<String, ModuleExecutor> EXECUTOR_DISZ;
	private TreeMap<String, ModuleExecutor> EXECUTOR_GROP;

	private File FILE_NICKNAME_MAP;
	private File FILE_MESSAGE_HELP;
	private File FILE_MESSAGE_INFO;
	private File FILE_MESSAGE_EULA;
	private File FILE_SILENCE_GROP;

	private File FILE_MEMBERCHANGE;

	private String MESSAGE_HELP = "";
	private String MESSAGE_INFO = "";
	private String MESSAGE_EULA = "";

	private String MESSAGE_LIST_USER = "";
	private String MESSAGE_LIST_DISZ = "";
	private String MESSAGE_LIST_GROP = "";

	private TreeMap<Long, TreeMap<Long, String>> NICKNAME_MAP;

	private HashSet<Long> MESSAGE_MUTE;

	private boolean LOCK_INIT = false;

	// ==========================================================================================================================================================
	//
	// 构造函数
	//
	// ==========================================================================================================================================================

	public Systemd() throws Exception {

		// @formatter:off

		super(
				MODULE_PACKAGENAME,
				MODULE_COMMANDNAME,
				MODULE_DISPLAYNAME,
				MODULE_DESCRIPTION,
				MODULE_VERSION,
				MODULE_USAGE,
				MODULE_PRIVACY_STORED,
				MODULE_PRIVACY_CACHED,
				MODULE_PRIVACY_OBTAIN
				);

		// @formatter:on

	}

	// ==========================================================================================================================================================
	//
	// 模块声明周期管理
	//
	// ==========================================================================================================================================================

	/**
	 * 你永远不应该执行这个方法
	 *
	 * @return
	 */
	@Override
	public boolean init() throws Exception {

		if (LOCK_INIT) { throw new CantReinitializationException(); }
		LOCK_INIT = true;

		// =======================================================================================================================
		// 初始化目录
		// =======================================================================================================================

		initAppFolder();
		initConfFolder();
		initLogsFolder();

		// =======================================================================================================================
		// 初始化内存结构
		// =======================================================================================================================

		SCHEDULER_INSTANCE = new TreeMap<>();
		SCHEDULER_ENABLED = new ArrayList<>();

		TRIGGER_INSTANCE = new TreeMap<>();
		TRIGGER_USER = new ArrayList<>();
		TRIGGER_DISZ = new ArrayList<>();
		TRIGGER_GROP = new ArrayList<>();

		LISTENER_INSTANCE = new TreeMap<>();
		LISTENER_USER = new ArrayList<>();
		LISTENER_DISZ = new ArrayList<>();
		LISTENER_GROP = new ArrayList<>();

		EXECUTOR_INSTANCE = new TreeMap<>();
		EXECUTOR_USER = new TreeMap<>();
		EXECUTOR_DISZ = new TreeMap<>();
		EXECUTOR_GROP = new TreeMap<>();

		MESSAGE_MUTE = new HashSet<>();

		NICKNAME_MAP = new TreeMap<>();

		// =======================================================================================================================
		// 读取配置文件
		// =======================================================================================================================

		if (NEW_CONFIG) {
			logger.seek("配置文件不存在", "生成默认配置");
			CONFIG.setProperty("logger_level", "0");
			CONFIG.setProperty("userid_admin", "0");
			CONFIG.setProperty("scheduler", "none");
			CONFIG.setProperty("trigger_user", "none");
			CONFIG.setProperty("trigger_disz", "none");
			CONFIG.setProperty("trigger_grop", "none");
			CONFIG.setProperty("listener_user", "none");
			CONFIG.setProperty("listener_disz", "none");
			CONFIG.setProperty("listener_grop", "none");
			CONFIG.setProperty("executor_user", "none");
			CONFIG.setProperty("executor_disz", "none");
			CONFIG.setProperty("executor_grop", "none");
			saveConfig();
		} else {
			loadConfig();
		}

		USERID_CQBOT = entry.getCQ().getLoginQQ();
		USERID_ADMIN = Long.parseLong(CONFIG.getProperty("userid_admin", "0"));

		if (USERID_ADMIN == 0) { throw new InitializationException("管理员账号配置错误"); }

		logger.seek("机器人账号", USERID_CQBOT);
		logger.seek("管理员账号", USERID_ADMIN);

		for (Group group : entry.getCQ().getGroupList()) {
			NICKNAME_MAP.put(group.getId(), new TreeMap<>());
		}

		// =======================================================================================================================
		// 读取独立配置文件
		// =======================================================================================================================

		FILE_MESSAGE_HELP = Paths.get(FOLDER_CONF.getAbsolutePath(), "message_help.txt").toFile();
		FILE_MESSAGE_INFO = Paths.get(FOLDER_CONF.getAbsolutePath(), "message_info.txt").toFile();
		FILE_MESSAGE_EULA = Paths.get(FOLDER_CONF.getAbsolutePath(), "message_eula.txt").toFile();
		FILE_SILENCE_GROP = Paths.get(FOLDER_CONF.getAbsolutePath(), "mute_grop.txt").toFile();
		FILE_NICKNAME_MAP = Paths.get(FOLDER_CONF.getAbsolutePath(), "nickmap_grop.txt").toFile();
		FILE_MEMBERCHANGE = Paths.get(FOLDER_LOGS.getAbsolutePath(), "member_change.txt").toFile();

		if (!FILE_MESSAGE_HELP.exists()) { if (!FILE_MESSAGE_HELP.createNewFile()) { throw new InitializationException("无法创建文件" + FILE_SILENCE_GROP.getName()); } }
		if (!FILE_MESSAGE_INFO.exists()) { if (!FILE_MESSAGE_INFO.createNewFile()) { throw new InitializationException("无法创建文件" + FILE_SILENCE_GROP.getName()); } }
		if (!FILE_MESSAGE_EULA.exists()) { if (!FILE_MESSAGE_EULA.createNewFile()) { throw new InitializationException("无法创建文件" + FILE_SILENCE_GROP.getName()); } }
		if (!FILE_SILENCE_GROP.exists()) { if (!FILE_SILENCE_GROP.createNewFile()) { throw new InitializationException("无法创建文件" + FILE_SILENCE_GROP.getName()); } }
		if (!FILE_NICKNAME_MAP.exists()) { if (!FILE_NICKNAME_MAP.createNewFile()) { throw new InitializationException("无法创建文件" + FILE_SILENCE_GROP.getName()); } }
		if (!FILE_MEMBERCHANGE.exists()) { if (!FILE_MEMBERCHANGE.createNewFile()) { throw new InitializationException("无法创建文件" + FILE_SILENCE_GROP.getName()); } }

		long gropid;
		long userid;
		String line;
		String[] temp;

		BufferedReader readerHelp = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_MESSAGE_HELP), StandardCharsets.UTF_8));
		BufferedReader readerInfo = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_MESSAGE_INFO), StandardCharsets.UTF_8));
		BufferedReader readerEula = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_MESSAGE_EULA), StandardCharsets.UTF_8));
		BufferedReader readerMute = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_SILENCE_GROP), StandardCharsets.UTF_8));
		BufferedReader readerNick = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_NICKNAME_MAP), StandardCharsets.UTF_8));

		// =======================================================================================
		// 读取 help info eula 文本

		StringBuilder builder = new StringBuilder();

		logger.full("读取HELP模板消息");

		while ((line = readerHelp.readLine()) != null) {
			builder.append(line + "\r\n");
		}

		builder.setLength(builder.length() - 2);
		MESSAGE_HELP = builder.toString();

		builder = new StringBuilder();

		logger.full("读取INFO模板消息");

		while ((line = readerInfo.readLine()) != null) {
			builder.append(line + "\r\n");
			MESSAGE_INFO = MESSAGE_INFO + line + "\r\n";
		}

		builder.setLength(builder.length() - 2);
		MESSAGE_INFO = builder.toString();
		builder = new StringBuilder();

		logger.full("读取EULA模板消息");

		while ((line = readerEula.readLine()) != null) {
			builder.append(line + "\r\n");
		}

		builder.setLength(builder.length() - 2);
		MESSAGE_EULA = builder.toString();

		MESSAGE_HELP = MESSAGE_HELP.replaceAll("REPLACE_VERSION", entry.VerID);
		MESSAGE_INFO = MESSAGE_INFO.replaceAll("REPLACE_VERSION", entry.VerID);
		MESSAGE_EULA = MESSAGE_EULA.replaceAll("REPLACE_VERSION", entry.VerID);

		String hashInfo = logger.seek("INFO散列值", Systemd.sha1(MESSAGE_INFO));
		String hashEula = logger.seek("EULA散列值", Systemd.sha1(MESSAGE_EULA));

		MESSAGE_INFO = MESSAGE_INFO + "\n=======================\nSHA-1: " + hashInfo;
		MESSAGE_EULA = MESSAGE_EULA + "\n=======================\nSHA-1: " + hashEula;

		// =======================================================================================
		// 读取 静音的群

		logger.full("读取静音群列表");

		while ((line = readerMute.readLine()) != null) {

			if (line.startsWith("#")) { continue; }
			if (line.contains("#")) { line = line.substring(0, line.indexOf("#")).trim(); }

			MESSAGE_MUTE.add(Long.parseLong(line));

			logger.seek("关闭发言", line);
		}

		// =======================================================================================
		// 读取 群昵称对应表

		logger.full("读取群昵称表");

		while ((line = readerNick.readLine()) != null) {

			if (line.startsWith("#")) { continue; }
			if (!line.contains(":")) { continue; }
			if (line.contains("#")) { line = line.substring(0, line.indexOf("#")).trim(); }

			temp = line.split(":");

			if (temp.length != 3) {
				logger.seek("配置无效", line);
				continue;
			}

			gropid = Long.parseLong(temp[0]);
			userid = Long.parseLong(temp[1]);

			if (!NICKNAME_MAP.containsKey(gropid)) { NICKNAME_MAP.put(gropid, new TreeMap<Long, String>()); }

			NICKNAME_MAP.get(gropid).put(userid, temp[2]);

		}

		readerHelp.close();
		readerInfo.close();
		readerEula.close();
		readerMute.close();
		readerNick.close();

		logger.seek("群昵称表", NICKNAME_MAP.size() + "个");

		for (long nickmap : NICKNAME_MAP.keySet()) {
			logger.seek("群" + nickmap, NICKNAME_MAP.get(nickmap).size() + "个");
		}

		// =======================================================================================================================
		// 实例化模块
		// =======================================================================================================================

		logger.full("实例化模块");

		// =======================================================================================================================
		// 实例化辅助模块

		instantiationScheduler(new Scheduler_Dynamic());

		// =======================================================================================================================
		// 实例化触发器

		instantiationTrigger(new Trigger_UserDeny());
		instantiationTrigger(new Trigger_WordDeny());

		// =======================================================================================================================
		// 实例化监听器

		instantiationListener(new Listener_TopSpeak());

		// =======================================================================================================================
		// 实例化执行器

		instantiationExecutor(new Executor_admin());
		instantiationExecutor(new Executor_acon());
		instantiationExecutor(new Executor_chou());
		instantiationExecutor(new Executor_dice());
		instantiationExecutor(new Executor_echo());
		instantiationExecutor(new Executor_food());
		instantiationExecutor(new Executor_jrjp());
		instantiationExecutor(new Executor_jrrp());
		instantiationExecutor(new Executor_kong());
		instantiationExecutor(new Executor_roll());
		instantiationExecutor(new Executor_roulette());
		instantiationExecutor(new Executor_time());
		instantiationExecutor(new Executor_zhan());

		// =======================================================================================================================
		// 初始化模块
		// =======================================================================================================================

		logger.full("初始化模块");

		// =======================================================================================================================
		// 初始化定时器
		for (String name : SCHEDULER_INSTANCE.keySet()) {
			logger.full("初始化定时器", name);
			SCHEDULER_INSTANCE.get(name).init();
		}

		// =======================================================================================================================
		// 初始化触发器

		for (String name : TRIGGER_INSTANCE.keySet()) {
			logger.full("初始化触发器", name);
			TRIGGER_INSTANCE.get(name).init();
		}

		// =======================================================================================================================
		// 初始化监听器

		for (String name : LISTENER_INSTANCE.keySet()) {
			logger.full("初始化监听器", name);
			LISTENER_INSTANCE.get(name).init();
		}

		// =======================================================================================================================
		// 初始化执行器

		for (String name : EXECUTOR_INSTANCE.keySet()) {
			logger.full("初始化执行器", name);
			EXECUTOR_INSTANCE.get(name).init();
		}

		// =======================================================================================================================
		// 读取模块配置
		// =======================================================================================================================

		logger.full("读取模块配置列表");

		// =======================================================================================================================
		// 读取定时器配置

		CONFIG_SCHEDULER = CONFIG.getProperty("scheduler", "none");

		logger.seek("定时器配置 全局", CONFIG_SCHEDULER);

		LIST_SCHEDULER = CONFIG_SCHEDULER.equals("none") ? new String[0] : CONFIG_SCHEDULER.split(",");

		// =======================================================================================================================
		// 读取触发器配置

		CONFIG_TRIGGER_USER = CONFIG.getProperty("trigger_user", "none");
		CONFIG_TRIGGER_DISZ = CONFIG.getProperty("trigger_disz", "none");
		CONFIG_TRIGGER_GROP = CONFIG.getProperty("trigger_grop", "none");

		logger.seek("触发器配置 私聊", CONFIG_TRIGGER_USER);
		logger.seek("触发器配置 组聊", CONFIG_TRIGGER_DISZ);
		logger.seek("触发器配置 群聊", CONFIG_TRIGGER_GROP);

		LIST_TRIGGER_USER = CONFIG_TRIGGER_USER.equals("none") ? new String[0] : CONFIG_TRIGGER_USER.split(",");
		LIST_TRIGGER_DISZ = CONFIG_TRIGGER_DISZ.equals("none") ? new String[0] : CONFIG_TRIGGER_DISZ.split(",");
		LIST_TRIGGER_GROP = CONFIG_TRIGGER_GROP.equals("none") ? new String[0] : CONFIG_TRIGGER_GROP.split(",");

		// =======================================================================================================================
		// 读取监听器配置

		CONFIG_LISENTER_USER = CONFIG.getProperty("listener_user", "none");
		CONFIG_LISENTER_DISZ = CONFIG.getProperty("listener_disz", "none");
		CONFIG_LISENTER_GROP = CONFIG.getProperty("listener_grop", "none");

		logger.seek("监听器配置 私聊", CONFIG_LISENTER_USER);
		logger.seek("监听器配置 组聊", CONFIG_LISENTER_DISZ);
		logger.seek("监听器配置 群聊", CONFIG_LISENTER_GROP);

		LIST_LISENTER_USER = CONFIG_LISENTER_USER.equals("none") ? new String[0] : CONFIG_LISENTER_USER.split(",");
		LIST_LISENTER_DISZ = CONFIG_LISENTER_DISZ.equals("none") ? new String[0] : CONFIG_LISENTER_DISZ.split(",");
		LIST_LISENTER_GROP = CONFIG_LISENTER_GROP.equals("none") ? new String[0] : CONFIG_LISENTER_GROP.split(",");

		// =======================================================================================================================
		// 读取执行器配置

		CONFIG_EXECUTOR_USER = CONFIG.getProperty("executor_user", "none");
		CONFIG_EXECUTOR_DISZ = CONFIG.getProperty("executor_disz", "none");
		CONFIG_EXECUTOR_GROP = CONFIG.getProperty("executor_grop", "none");

		logger.seek("执行器配置 私聊", CONFIG_EXECUTOR_USER);
		logger.seek("执行器配置 组聊", CONFIG_EXECUTOR_DISZ);
		logger.seek("执行器配置 群聊", CONFIG_EXECUTOR_GROP);

		LIST_EXECUTOR_USER = CONFIG_EXECUTOR_USER.equals("none") ? new String[0] : CONFIG_EXECUTOR_USER.split(",");
		LIST_EXECUTOR_DISZ = CONFIG_EXECUTOR_DISZ.equals("none") ? new String[0] : CONFIG_EXECUTOR_DISZ.split(",");
		LIST_EXECUTOR_GROP = CONFIG_EXECUTOR_GROP.equals("none") ? new String[0] : CONFIG_EXECUTOR_GROP.split(",");

		// =======================================================================================================================
		// 注册模块
		// =======================================================================================================================

		logger.full("注册模块");

		// =======================================================================================================================
		// 注册定时器

		for (String name : LIST_SCHEDULER) {
			if (SCHEDULER_INSTANCE.containsKey(name)) {
				ModuleScheduler instance = SCHEDULER_INSTANCE.get(name);
				if (instance.ENABLE()) {
					logger.full("注册定时器 全局", instance.MODULE_PACKAGENAME());
					SCHEDULER_ENABLED.add(instance);
				}
			} else {
				logger.warn("配置错误", "定时器不存在 " + name);
			}
		}

		ENABLE_SCHEDULER = SCHEDULER_ENABLED.size() > 0;

		// =======================================================================================================================
		// 注册触发器

		for (String name : LIST_TRIGGER_USER) {
			if (TRIGGER_INSTANCE.containsKey(name)) {
				ModuleTrigger instance = TRIGGER_INSTANCE.get(name);
				if (instance.ENABLE_USER()) {
					logger.full("注册触发器 私聊", instance.MODULE_PACKAGENAME());
					TRIGGER_USER.add(instance);
				}
			} else {
				logger.warn("配置错误", "私聊触发器不存在 " + name);
			}
		}

		for (String name : LIST_TRIGGER_DISZ) {
			if (TRIGGER_INSTANCE.containsKey(name)) {
				ModuleTrigger instance = TRIGGER_INSTANCE.get(name);
				if (instance.ENABLE_DISZ()) {
					logger.full("注册触发器 组聊", instance.MODULE_PACKAGENAME());
					TRIGGER_DISZ.add(instance);
				}
			} else {
				logger.warn("配置错误", "组聊触发器不存在 " + name);
			}
		}

		for (String name : LIST_TRIGGER_GROP) {
			if (TRIGGER_INSTANCE.containsKey(name)) {
				ModuleTrigger instance = TRIGGER_INSTANCE.get(name);
				if (instance.ENABLE_GROP()) {
					logger.full("注册触发器 群聊", instance.MODULE_PACKAGENAME());
					TRIGGER_GROP.add(instance);
				}
			} else {
				logger.warn("配置错误", "群聊触发器不存在 " + name);
			}
		}

		ENABLE_TRIGGER_USER = TRIGGER_USER.size() > 0;
		ENABLE_TRIGGER_DISZ = TRIGGER_DISZ.size() > 0;
		ENABLE_TRIGGER_GROP = TRIGGER_GROP.size() > 0;

		// =======================================================================================================================
		// 注册监听器

		for (String name : LIST_LISENTER_USER) {
			if (LISTENER_INSTANCE.containsKey(name)) {
				ModuleListener instance = LISTENER_INSTANCE.get(name);
				if (instance.ENABLE_USER()) {
					logger.full("注册监听器 私聊", instance.MODULE_PACKAGENAME());
					LISTENER_USER.add(instance);
				}
			} else {
				logger.warn("配置错误", "私聊监听器不存在 " + name);
			}
		}

		for (String name : LIST_LISENTER_DISZ) {
			if (LISTENER_INSTANCE.containsKey(name)) {
				ModuleListener instance = LISTENER_INSTANCE.get(name);
				if (instance.ENABLE_DISZ()) {
					logger.full("注册监听器 组聊", instance.MODULE_PACKAGENAME());
					LISTENER_DISZ.add(instance);
				}
			} else {
				logger.warn("配置错误", "组聊监听器不存在 " + name);
			}
		}

		for (String name : LIST_LISENTER_GROP) {
			if (LISTENER_INSTANCE.containsKey(name)) {
				ModuleListener instance = LISTENER_INSTANCE.get(name);
				if (instance.ENABLE_GROP()) {
					logger.full("注册监听器 群聊", instance.MODULE_PACKAGENAME());
					LISTENER_GROP.add(instance);
				}
			} else {
				logger.warn("配置错误", "群聊监听器不存在 " + name);
			}
		}

		ENABLE_LISENTER_USER = LISTENER_USER.size() > 0;
		ENABLE_LISENTER_DISZ = LISTENER_DISZ.size() > 0;
		ENABLE_LISENTER_GROP = LISTENER_GROP.size() > 0;

		// =======================================================================================================================
		// 注册执行器

		for (String name : LIST_EXECUTOR_USER) {
			if (EXECUTOR_INSTANCE.containsKey(name)) {
				ModuleExecutor instance = EXECUTOR_INSTANCE.get(name);
				if (instance.ENABLE_USER()) {
					logger.full("注册执行器 私聊", instance.MODULE_PACKAGENAME());
					EXECUTOR_USER.put(instance.MODULE_COMMANDNAME(), instance);
				}
			} else {
				logger.warn("配置错误", "私聊执行器不存在 " + name);
			}
		}

		for (String name : LIST_EXECUTOR_DISZ) {
			if (EXECUTOR_INSTANCE.containsKey(name)) {
				ModuleExecutor instance = EXECUTOR_INSTANCE.get(name);
				if (instance.ENABLE_DISZ()) {
					logger.full("注册执行器 组聊", instance.MODULE_PACKAGENAME());
					EXECUTOR_DISZ.put(instance.MODULE_COMMANDNAME(), instance);
				}
			} else {
				logger.warn("配置错误", "组聊执行器不存在 " + name);
			}
		}

		for (String name : LIST_EXECUTOR_GROP) {
			if (EXECUTOR_INSTANCE.containsKey(name)) {
				ModuleExecutor instance = EXECUTOR_INSTANCE.get(name);
				if (instance.ENABLE_GROP()) {
					logger.full("注册执行器 群聊", instance.MODULE_PACKAGENAME());
					EXECUTOR_GROP.put(instance.MODULE_COMMANDNAME(), instance);
				}
			} else {
				logger.warn("配置错误", "群聊执行器不存在 " + name);
			}
		}

		ENABLE_EXECUTOR_USER = EXECUTOR_USER.size() > 0;
		ENABLE_EXECUTOR_DISZ = EXECUTOR_DISZ.size() > 0;
		ENABLE_EXECUTOR_GROP = EXECUTOR_GROP.size() > 0;

		// =======================================================================================================================
		// 统计模块状态
		// =======================================================================================================================

		logger.info("模块状态");

		logger.info("计时器 全局", ENABLE_SCHEDULER ? "启用 " + SCHEDULER_ENABLED.size() + "个" : "禁用");

		logger.info("触发器 私聊", ENABLE_TRIGGER_USER ? "启用 " + TRIGGER_USER.size() + "个" : "禁用");
		logger.info("触发器 组聊", ENABLE_TRIGGER_DISZ ? "启用 " + TRIGGER_DISZ.size() + "个" : "禁用");
		logger.info("触发器 群聊", ENABLE_TRIGGER_GROP ? "启用 " + TRIGGER_GROP.size() + "个" : "禁用");

		logger.info("监听器 私聊", ENABLE_LISENTER_USER ? "启用 " + LISTENER_DISZ.size() + "个" : "禁用");
		logger.info("监听器 组聊", ENABLE_LISENTER_DISZ ? "启用 " + LISTENER_DISZ.size() + "个" : "禁用");
		logger.info("监听器 群聊", ENABLE_LISENTER_GROP ? "启用 " + LISTENER_GROP.size() + "个" : "禁用");

		logger.info("执行器 私聊", ENABLE_EXECUTOR_USER ? "启用 " + EXECUTOR_USER.size() + "个" : "禁用");
		logger.info("执行器 组聊", ENABLE_EXECUTOR_DISZ ? "启用 " + EXECUTOR_DISZ.size() + "个" : "禁用");
		logger.info("执行器 群聊", ENABLE_EXECUTOR_GROP ? "启用 " + EXECUTOR_GROP.size() + "个" : "禁用");

		// =======================================================================================================================
		// 预生成 list 的信息

		MESSAGE_LIST_USER = generateListMessage("私聊", TRIGGER_USER, LISTENER_USER, EXECUTOR_USER);
		MESSAGE_LIST_DISZ = generateListMessage("组聊", TRIGGER_DISZ, LISTENER_DISZ, EXECUTOR_DISZ);
		MESSAGE_LIST_GROP = generateListMessage("群聊", TRIGGER_GROP, LISTENER_GROP, EXECUTOR_GROP);

		return true;

	}

	/**
	 * 你永远不应该执行这个方法
	 */
	@Override
	public boolean boot() throws Exception {

		// =======================================================================================================================
		// 启动定时器

		for (ModuleScheduler instance : SCHEDULER_ENABLED) {
			logger.full("启动定时器 ", instance.MODULE_PACKAGENAME());
			instance.boot();
		}

		// =======================================================================================================================
		// 启动触发器

		for (String name : TRIGGER_INSTANCE.keySet()) {
			logger.full("启动触发器", name);
			ModuleTrigger instance = TRIGGER_INSTANCE.get(name);
			if (instance.ENABLE_USER() || instance.ENABLE_DISZ() || instance.ENABLE_GROP()) { instance.boot(); }
		}

		// =======================================================================================================================
		// 启动 监听器

		for (String name : LISTENER_INSTANCE.keySet()) {
			logger.full("启动监听器", name);
			ModuleListener instance = LISTENER_INSTANCE.get(name);
			if (instance.ENABLE_USER() || instance.ENABLE_DISZ() || instance.ENABLE_GROP()) { instance.boot(); }
		}

		// =======================================================================================================================
		// 启动执行器

		for (String name : EXECUTOR_INSTANCE.keySet()) {
			logger.full("启动执行器", name);
			ModuleExecutor instance = EXECUTOR_INSTANCE.get(name);
			if (instance.ENABLE_USER() || instance.ENABLE_DISZ() || instance.ENABLE_GROP()) { instance.boot(); }
		}

		return true;
	}

	/**
	 * 你永远不应该执行这个方法
	 */
	@Override
	public boolean save() throws Exception {

		// =======================================================================================================================
		// 保存定时器

		for (String name : SCHEDULER_INSTANCE.keySet()) {
			logger.full("保存定时器", name);
			SCHEDULER_INSTANCE.get(name).save();
		}

		// =======================================================================================================================
		// 保存触发器

		for (String name : TRIGGER_INSTANCE.keySet()) {
			logger.full("保存触发器", name);
			TRIGGER_INSTANCE.get(name).save();
		}

		// =======================================================================================================================
		// 保存监听器

		for (String name : LISTENER_INSTANCE.keySet()) {
			logger.full("保存监听器", name);
			LISTENER_INSTANCE.get(name).save();
		}

		// =======================================================================================================================
		// 保存执行器

		for (String name : EXECUTOR_INSTANCE.keySet()) {
			logger.full("保存执行器", name);
			EXECUTOR_INSTANCE.get(name).save();
		}

		return true;
	}

	/**
	 * 你永远不应该执行这个方法
	 */
	@Override
	public boolean shut() throws Exception {

		// =======================================================================================================================
		// 关闭定时器

		for (String name : SCHEDULER_INSTANCE.keySet()) {
			logger.full("关闭定时器", name);
			SCHEDULER_INSTANCE.get(name).shut();
		}

		// =======================================================================================================================
		// 关闭触发器

		for (String name : TRIGGER_INSTANCE.keySet()) {
			logger.full("关闭触发器", name);
			TRIGGER_INSTANCE.get(name).shut();
		}

		// =======================================================================================================================
		// 关闭监听器

		for (String name : LISTENER_INSTANCE.keySet()) {
			logger.full("关闭监听器", name);
			LISTENER_INSTANCE.get(name).shut();
		}

		// =======================================================================================================================
		// 关闭执行器

		for (String name : EXECUTOR_INSTANCE.keySet()) {
			logger.full("关闭执行器", name);
			EXECUTOR_INSTANCE.get(name).shut();
		}

		return true;
	}

	/**
	 * 你永远不应该执行这个方法
	 */
	@Override
	public String[] exec(Message message) throws Exception {

		String[] report = null;

		BufferX builder = new BufferX();

		String module = message.getSwitch("module");

		if (module == null) {
			return new String[] {
					"参数错误 --module 为空"
			};
		}

		builder.info("指定模块", module);

		if (module.equals("systemd")) {

			builder.info("指定功能", message.getSegment(1));
			builder.info("指定命令", message.getSegment(2));

			if (message.getSection() < 2) {
				return new String[] {
						"参数错误 缺少行为参数 /admin exec --module=$NAME $ACTION"
				};
			}

			switch (message.getSegment(1)) {

			case "nickmap":

				switch (message.getSegment(2)) {

				case "dump":

					int i = 0;

					File allUserDump = Paths.get(FOLDER_LOGS.getAbsolutePath(), "nickdump_" + LoggerX.formatTime("yyyy_MM_dd_HH_mm_ss") + ".txt").toFile();
					allUserDump.createNewFile();
					FileWriter dumper = new FileWriter(allUserDump, true);

					for (Group group : entry.getCQ().getGroupList()) {
						dumper.append("#===========================\n#" + group.getName() + "(" + group.getId() + ")\n");
						for (Member memebr : entry.getCQ().getGroupMemberList(group.getId())) {
							if (isMyself(memebr.getQQId())) { continue; }
							dumper.append(group.getId() + ":" + memebr.getQQId() + ":" + getGropNick(group.getId(), memebr.getQQId()) + "\n");
							i++;
						}
					}

					dumper.flush();
					dumper.close();

					return new String[] {
							"[" + MODULE_PACKAGENAME + "] " + "昵称转储" + i + "个"
					};

				case "save":

					FILE_NICKNAME_MAP.delete();
					FILE_NICKNAME_MAP.createNewFile();

					FileWriter saver = new FileWriter(FILE_NICKNAME_MAP, true);

					for (Group group : entry.getCQ().getGroupList()) {

						saver.append("#===========================\n#" + group.getName() + "(" + group.getId() + ")\n");

						for (Member memebr : entry.getCQ().getGroupMemberList(group.getId())) {
							if (isMyself(memebr.getQQId())) { continue; }
							saver.append(group.getId() + ":" + memebr.getQQId() + ":" + getGropNick(group.getId(), memebr.getQQId()) + "\n");
						}
					}

					saver.flush();
					saver.close();

					return new String[] {
							"[" + MODULE_PACKAGENAME + "] " + "昵称保存"
					};

				case "load":

					NICKNAME_MAP.clear();

					long gropid;
					long userid;
					String line;
					String[] temp;

					BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_NICKNAME_MAP), StandardCharsets.UTF_8));

					while ((line = reader.readLine()) != null) {

						if (line.startsWith("#")) { continue; }
						if (!line.contains(":")) { continue; }
						if (line.contains("#")) { line = line.substring(0, line.indexOf("#")).trim(); }

						temp = line.split(":");

						if (temp.length != 3) {
							builder.seek(MODULE_PACKAGENAME, "此行配置无效", line);
							continue;
						}

						gropid = Long.parseLong(temp[0]);
						userid = Long.parseLong(temp[1]);

						if (!NICKNAME_MAP.containsKey(gropid)) { NICKNAME_MAP.put(gropid, new TreeMap<Long, String>()); }

						NICKNAME_MAP.get(gropid).put(userid, temp[2]);

						reader.close();

					}

					break;
				}

				break;

			default:

				return new String[] {
						"参数错误 无此子功能"
				};

			}

		} else if (SCHEDULER_INSTANCE.containsKey(module)) {

			ModuleScheduler instance = SCHEDULER_INSTANCE.get(module);
			builder.info("查找到定时器", instance.MODULE_PACKAGENAME());
			report = instance.exec(message);

		} else if (TRIGGER_INSTANCE.containsKey(module)) {

			ModuleTrigger instance = TRIGGER_INSTANCE.get(module);
			builder.info("查找到触发器", instance.MODULE_PACKAGENAME());
			report = instance.exec(message);

		} else if (LISTENER_INSTANCE.containsKey(module)) {

			ModuleListener instance = LISTENER_INSTANCE.get(module);
			builder.info("查找到监听器", instance.MODULE_PACKAGENAME());
			report = instance.exec(message);

		} else if (EXECUTOR_INSTANCE.containsKey(module)) {

			ModuleExecutor instance = EXECUTOR_INSTANCE.get(module);
			builder.info("查找到执行器", instance.MODULE_PACKAGENAME());
			report = instance.exec(message);

		} else {

			return new String[] {
					"无此名称的模块"
			};

		}

		if ((report == null) || (report.length == 0)) {
			builder.info("模块没有返回任何消息");
			return builder.make();
		} else {
			return report;
		}

	}

	// ==========================================================================================================================================================
	//
	// 事件处理
	//
	// ==========================================================================================================================================================

	/**
	 * 你永远不应该执行这个方法
	 */
	@Override
	public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) throws Exception {

		FileWriter writer = new FileWriter(FILE_MEMBERCHANGE, true);

		if (isMyself(userid)) {

			writer.append("# Bot Join Group " + LoggerX.datetime() + "\n");
			for (Member memeber : entry.getCQ().getGroupMemberList(gropid)) {
				writer.append(gropid + ":" + memeber.getQQId() + ":" + entry.getCQ().getStrangerInfo(memeber.getQQId()).getNick() + "\n");
			}

		} else {

			writer.append("# Member Increase " + LoggerX.datetime() + "\n" + gropid + ":" + userid + ":" + entry.getCQ().getStrangerInfo(userid).getNick() + "\n\n");
		}

		writer.flush();
		writer.close();

		for (String name : TRIGGER_INSTANCE.keySet()) {
			TRIGGER_INSTANCE.get(name).groupMemberIncrease(typeid, sendtime, gropid, operid, userid);
		}

		for (String name : LISTENER_INSTANCE.keySet()) {
			LISTENER_INSTANCE.get(name).groupMemberIncrease(typeid, sendtime, gropid, operid, userid);
		}

		for (String name : EXECUTOR_INSTANCE.keySet()) {
			EXECUTOR_INSTANCE.get(name).groupMemberIncrease(typeid, sendtime, gropid, operid, userid);
		}
	}

	/**
	 * 你永远不应该执行这个方法
	 */
	@Override
	public void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) throws Exception {

		for (String name : TRIGGER_INSTANCE.keySet()) {
			TRIGGER_INSTANCE.get(name).groupMemberDecrease(typeid, sendtime, gropid, operid, userid);
		}

		for (String name : LISTENER_INSTANCE.keySet()) {
			LISTENER_INSTANCE.get(name).groupMemberDecrease(typeid, sendtime, gropid, operid, userid);
		}

		for (String name : EXECUTOR_INSTANCE.keySet()) {
			EXECUTOR_INSTANCE.get(name).groupMemberDecrease(typeid, sendtime, gropid, operid, userid);
		}

	}

	// ==========================================================================================================================================================
	//
	// 消息处理
	//
	// ==========================================================================================================================================================

	/**
	 * 你永远不应该执行这个方法
	 *
	 * @param typeid      你永远不应该执行这个方法
	 * @param userid      你永远不应该执行这个方法
	 * @param message     你永远不应该执行这个方法
	 * @param messageid   你永远不应该执行这个方法
	 * @param messagefont 你永远不应该执行这个方法
	 * @throws Exception 你永远不应该执行这个方法
	 */
	public void doUserMessage(int typeid, long userid, MessageUser message, int messageid, int messagefont) throws Exception {

		COUNT_USER_MESSAGE++;

		message.parse();

		if (entry.DEBUG()) { System.out.println(message.toString()); }

		if (ENABLE_TRIGGER_USER) {
			for (ModuleTrigger temp : TRIGGER_USER) {
				if (temp.executeUserMessage(typeid, userid, message, messageid, messagefont)) { return; }
			}
		}

		if (ENABLE_LISENTER_USER) {
			for (ModuleListener temp : LISTENER_USER) {
				temp.executeUserMessage(typeid, userid, message, messageid, messagefont);
			}
		}

		if (message.isCommand()) {

			switch (message.getCommand()) {

			case "info":
				sendInfo(userid);
				break;

			case "eula":
				sendEula(userid);
				break;

			case "list":
				sendListUser(userid);
				break;

			case "help":
				if (message.getSection() == 0) {
					this.sendHelp(userid);
				} else {
					this.sendHelp(userid, message.getSegment()[0]);
				}
				break;

			default:
				if (ENABLE_EXECUTOR_USER && EXECUTOR_USER.containsKey(message.getCommand())) {
					EXECUTOR_USER.get(message.getCommand()).executeUserMessage(typeid, userid, message, messageid, messagefont);
				} else {
					this.userInfo(userid, "没有此插件，可用插件如下");
					sendListUser(userid);
				}
				break;

			}
		} else {
			this.userInfo(userid, "未识别的内容，本BOT没有聊天功能，请使用/help查看帮助。");
		}
	}

	/**
	 * 你永远不应该执行这个方法
	 *
	 * @param diszid      你永远不应该执行这个方法
	 * @param userid      你永远不应该执行这个方法
	 * @param message     你永远不应该执行这个方法
	 * @param messageid   你永远不应该执行这个方法
	 * @param messagefont 你永远不应该执行这个方法
	 * @throws Exception 你永远不应该执行这个方法
	 */
	public void doDiszMessage(long diszid, long userid, MessageDisz message, int messageid, int messagefont) throws Exception {

		COUNT_DISZ_MESSAGE++;

		message.parse();

		if (entry.DEBUG()) { System.out.println(message.toString()); }

		if (ENABLE_TRIGGER_DISZ) {
			for (ModuleTrigger temp : TRIGGER_DISZ) {
				if (temp.executeDiszMessage(diszid, userid, message, messageid, messagefont)) { return; }
			}
		}

		if (ENABLE_LISENTER_DISZ) {
			for (ModuleListener temp : LISTENER_DISZ) {
				temp.executeDiszMessage(diszid, userid, message, messageid, messagefont);
			}
		}

		if (message.isCommand()) {

			switch (message.getCommand()) {

			case "info":
				this.diszInfo(diszid, userid, "已发送至私聊，如未收到请允许临时会话或添加好友");
				sendInfo(userid);
				break;

			case "eula":
				this.diszInfo(diszid, userid, "已发送至私聊，如未收到请允许临时会话或添加好友");
				sendEula(userid);
				break;

			case "list":
				this.diszInfo(diszid, userid, "已发送至私聊，如未收到请允许临时会话或添加好友");
				sendListDisz(userid);
				break;

			case "help":
				if (message.getSection() == 0) {
					this.sendHelp(userid);
				} else {
					this.diszInfo(diszid, userid, "已发送至私聊，如未收到请允许临时会话或添加好友");
					this.sendHelp(userid, message.getSegment()[0]);
				}
				break;

			default:
				if (ENABLE_EXECUTOR_DISZ && EXECUTOR_DISZ.containsKey(message.getCommand())) {
					EXECUTOR_DISZ.get(message.getCommand()).executeDiszMessage(diszid, userid, message, messageid, messagefont);
				} else {
					this.diszInfo(diszid, userid, "没有此插件，可用插件已发送至私聊，如未收到请允许临时会话或添加好友");
					sendListDisz(userid);
				}
				break;

			}
		}
	}

	/**
	 * 你永远不应该执行这个方法
	 *
	 * @param gropid      你永远不应该执行这个方法
	 * @param userid      你永远不应该执行这个方法
	 * @param message     你永远不应该执行这个方法
	 * @param messageid   你永远不应该执行这个方法
	 * @param messagefont 你永远不应该执行这个方法
	 * @throws Exception 你永远不应该执行这个方法
	 */
	public void doGropMessage(long gropid, long userid, MessageGrop message, int messageid, int messagefont) throws Exception {

		COUNT_GROP_MESSAGE++;

		message.parse();

		if (entry.DEBUG()) { System.out.println(message.toString()); }

		if (ENABLE_TRIGGER_GROP) {
			for (ModuleTrigger temp : TRIGGER_GROP) {
				if (temp.executeGropMessage(gropid, userid, message, messageid, messagefont)) { return; }
			}
		}

		if (ENABLE_LISENTER_GROP) {
			for (ModuleListener temp : LISTENER_GROP) {
				temp.executeGropMessage(gropid, userid, message, messageid, messagefont);
			}
		}

		if (message.isCommand()) {

			switch (message.getCommand()) {

			case "info":
				this.gropInfo(gropid, userid, "已发送至私聊，如未收到请允许临时会话或添加好友");
				sendInfo(userid);
				break;

			case "eula":
				this.gropInfo(gropid, userid, "已发送至私聊，如未收到请允许临时会话或添加好友");
				sendEula(userid);
				break;

			case "list":
				this.gropInfo(gropid, userid, "已发送至私聊，如未收到请允许临时会话或添加好友");
				sendListGrop(userid);
				break;

			case "help":
				if (message.getSection() == 0) {
					this.sendHelp(userid);
				} else {
					this.gropInfo(gropid, userid, "已发送至私聊，如未收到请允许临时会话或添加好友");
					this.sendHelp(userid, message.getSegment()[0]);
				}
				break;

			default:
				if (ENABLE_EXECUTOR_GROP && EXECUTOR_GROP.containsKey(message.getCommand())) {
					EXECUTOR_GROP.get(message.getCommand()).executeGropMessage(gropid, userid, message, messageid, messagefont);
				} else {
					this.gropInfo(gropid, userid, "没有此插件，可用插件已发送至私聊，如未收到请允许临时会话或添加好友");
					sendListGrop(userid);
				}
				break;

			}
		}
	}

	// ==========================================================================================================================================================
	//
	// 辅助管理
	//
	// ==========================================================================================================================================================

	private void instantiationScheduler(ModuleScheduler instance) {
		SCHEDULER_INSTANCE.put(instance.MODULE_COMMANDNAME(), instance);
	}

	private void instantiationTrigger(ModuleTrigger instance) {
		TRIGGER_INSTANCE.put(instance.MODULE_COMMANDNAME(), instance);
	}

	private void instantiationListener(ModuleListener instance) {
		LISTENER_INSTANCE.put(instance.MODULE_COMMANDNAME(), instance);
	}

	private void instantiationExecutor(ModuleExecutor instance) {
		EXECUTOR_INSTANCE.put(instance.MODULE_COMMANDNAME(), instance);
	}

	// ==========================================================================================================================================================
	//
	// 工具函数
	//
	// ==========================================================================================================================================================

	/**
	 * 生成报告的方法 你永远不应该执行这个方法
	 */
	@Override
	public String[] generateReport(int mode, Message message, Object... parameters) {

		StringBuilder builder = new StringBuilder();

		long uptime = System.currentTimeMillis() - entry.BOOTTIME;

		long uptimedd = uptime / 86400000;
		uptime = uptime % 86400000;

		long uptimehh = uptime / 3600000;
		uptime = uptime % 3600000;

		long uptimemm = uptime / 60000;
		uptime = uptime % 60000;

		long uptimess = uptime / 1000;

		long totalMemory = Runtime.getRuntime().totalMemory() / 1024;
		long freeMemory = Runtime.getRuntime().freeMemory() / 1024;

		builder.append(LoggerX.datetime() + " - 状态简报" + "\r\n");

		builder.append("运行时间：" + uptimedd + " - " + uptimehh + ":" + uptimemm + ":" + uptimess + "\r\n");
		builder.append("系统内存：" + (totalMemory - freeMemory) + "KB/" + totalMemory + "KB" + "\r\n");

		builder.append("私聊：" + COUNT_USER_MESSAGE + "次" + "\r\n");
		builder.append("组聊：" + COUNT_DISZ_MESSAGE + "次" + "\r\n");
		builder.append("群聊：" + COUNT_GROP_MESSAGE + "次" + "\r\n");

		builder.append("定时器：" + SCHEDULER_ENABLED.size() + "个" + "\r\n");

		for (ModuleScheduler instance : SCHEDULER_ENABLED) {
			if (instance.ENABLE()) { builder.append(instance.MODULE_PACKAGENAME() + "：" + instance.COUNT() + "\r\n"); }
		}

		builder.append("触发器：" + TRIGGER_USER.size() + "/" + TRIGGER_DISZ.size() + "/" + TRIGGER_GROP.size() + "个" + "\r\n");

		for (String temp : TRIGGER_INSTANCE.keySet()) {

			ModuleTrigger instance = TRIGGER_INSTANCE.get(temp);

			if (instance.ENABLE_USER() || instance.ENABLE_DISZ() || instance.ENABLE_GROP()) { continue; }

			builder.append(instance.MODULE_PACKAGENAME() + "：");
			builder.append(instance.ENABLE_USER() ? instance.BLOCK_USER() : "关");
			builder.append("/");
			builder.append(instance.ENABLE_DISZ() ? instance.BLOCK_DISZ() : "关");
			builder.append("/");
			builder.append(instance.ENABLE_GROP() ? instance.BLOCK_GROP() : "关");

			builder.append("\r\n");
		}

		builder.append("监听器：" + LISTENER_USER.size() + "/" + LISTENER_DISZ.size() + "/" + LISTENER_GROP.size() + "个" + "\r\n");

		for (String temp : LISTENER_INSTANCE.keySet()) {

			ModuleListener instance = LISTENER_INSTANCE.get(temp);

			if (instance.ENABLE_USER() || instance.ENABLE_DISZ() || instance.ENABLE_GROP()) { continue; }

			builder.append(instance.MODULE_PACKAGENAME() + "：");
			builder.append(instance.ENABLE_USER() ? instance.COUNT_USER() : "关");
			builder.append("/");
			builder.append(instance.ENABLE_DISZ() ? instance.COUNT_DISZ() : "关");
			builder.append("/");
			builder.append(instance.ENABLE_GROP() ? instance.COUNT_GROP() : "关");

			builder.append("\r\n");
		}

		builder.append("执行器：" + EXECUTOR_USER.size() + "/" + EXECUTOR_DISZ.size() + "/" + EXECUTOR_GROP.size() + "个" + "\r\n");

		for (String temp : EXECUTOR_INSTANCE.keySet()) {

			ModuleExecutor instance = EXECUTOR_INSTANCE.get(temp);

			if (instance.ENABLE_USER() || instance.ENABLE_DISZ() || instance.ENABLE_GROP()) { continue; }

			builder.append(instance.MODULE_PACKAGENAME() + "：");
			builder.append(instance.ENABLE_USER() ? instance.COUNT_USER() : "关");
			builder.append("/");
			builder.append(instance.ENABLE_DISZ() ? instance.COUNT_DISZ() : "关");
			builder.append("/");
			builder.append(instance.ENABLE_GROP() ? instance.COUNT_GROP() : "关");

			builder.append("\r\n");
		}

		return new String[] {
				builder.substring(0, builder.length() - 2).toString()
		};
	}

	public String[] reportAllModules(int mode, Message message, Object... parameters) {

		StringBuilder builder01 = new StringBuilder();
		StringBuilder builder02 = new StringBuilder();
		StringBuilder builder03 = new StringBuilder();
		StringBuilder builder04 = new StringBuilder();

		// part 1 定时器

		builder01.append("定时器：" + SCHEDULER_ENABLED.size() + "个" + "\r\n");

		for (ModuleScheduler instance : SCHEDULER_ENABLED) {

			String[] temp = instance.generateReport(0, message, null, null);

			if (temp == null) { builder01.append(instance.MODULE_PACKAGENAME() + "：" + instance.COUNT() + "无\r\n"); }

		}

		// part 2 触发器

		builder02.append("触发器：" + TRIGGER_USER.size() + "/" + TRIGGER_DISZ.size() + "/" + TRIGGER_GROP.size() + "个" + "\r\n");

		for (String temp : TRIGGER_INSTANCE.keySet()) {

			ModuleTrigger instance = TRIGGER_INSTANCE.get(temp);

			if (!instance.ENABLE_USER() || !instance.ENABLE_DISZ() || !instance.ENABLE_GROP()) { continue; }

			builder02.append(instance.MODULE_PACKAGENAME() + "：");
			builder02.append(instance.ENABLE_USER() ? instance.BLOCK_USER() : "关");
			builder02.append("/");
			builder02.append(instance.ENABLE_DISZ() ? instance.BLOCK_DISZ() : "关");
			builder02.append("/");
			builder02.append(instance.ENABLE_GROP() ? instance.BLOCK_GROP() : "关");
			builder02.append("\r\n");

			String[] tempReport = instance.generateReport(0, message, null, null);

			if (tempReport == null) { continue; }

			for (String part : tempReport) {
				builder02.append(part);
				builder02.append("\r\n");
			}

			builder02.append("\r\n");
		}

		// part 3 监听器

		builder03.append("监听器：" + LISTENER_USER.size() + "/" + LISTENER_DISZ.size() + "/" + LISTENER_GROP.size() + "个" + "\r\n");

		for (String temp : LISTENER_INSTANCE.keySet()) {

			ModuleListener instance = LISTENER_INSTANCE.get(temp);

			if (!instance.ENABLE_USER() || !instance.ENABLE_DISZ() || !instance.ENABLE_GROP()) { continue; }

			builder03.append(instance.MODULE_PACKAGENAME() + "：");
			builder03.append(instance.ENABLE_USER() ? instance.COUNT_USER() : "关");
			builder03.append("/");
			builder03.append(instance.ENABLE_DISZ() ? instance.COUNT_DISZ() : "关");
			builder03.append("/");
			builder03.append(instance.ENABLE_GROP() ? instance.COUNT_GROP() : "关");
			builder03.append("\r\n");

			String[] tempReport = instance.generateReport(0, message, null, null);

			if (tempReport == null) { continue; }

			for (String part : tempReport) {
				builder03.append(part);
				builder03.append("\r\n");
			}

			builder03.append("\r\n");
		}

		// part 4 执行器

		builder04.append("执行器：" + EXECUTOR_USER.size() + "/" + EXECUTOR_DISZ.size() + "/" + EXECUTOR_GROP.size() + "个" + "\r\n");

		for (String temp : EXECUTOR_INSTANCE.keySet()) {

			ModuleExecutor instance = EXECUTOR_INSTANCE.get(temp);

			if (!instance.ENABLE_USER() || !instance.ENABLE_DISZ() || !instance.ENABLE_GROP()) { continue; }

			builder04.append(instance.MODULE_PACKAGENAME() + "：");
			builder04.append(instance.ENABLE_USER() ? instance.COUNT_USER() : "关");
			builder04.append("/");
			builder04.append(instance.ENABLE_DISZ() ? instance.COUNT_DISZ() : "关");
			builder04.append("/");
			builder04.append(instance.ENABLE_GROP() ? instance.COUNT_GROP() : "关");
			builder04.append("\r\n");

			String[] tempReport = instance.generateReport(0, message, null, null);

			if (tempReport == null) { continue; }

			for (String part : tempReport) {
				builder04.append(part);
				builder04.append("\r\n");
			}

			builder04.append("\r\n");
		}

		return new String[] {
				builder01.substring(0, builder01.length() - 2).toString(),
				builder02.substring(0, builder02.length() - 2).toString(),
				builder03.substring(0, builder03.length() - 2).toString(),
				builder04.substring(0, builder04.length() - 2).toString(),
		};
	}

	public String[] reportSpecifiedModule(int mode, Message message, Object... parameters) {

		String[] report = new String[1];

		String module = message.getSwitch("module");

		if (module.contains(":")) {

			String temp[] = module.split(":");
			String name = temp[0];
			int submode = Integer.parseInt(temp[1]);

			if (name.equals("systemd")) {

				report = generateReport(0, message, null, null); // /admin report --module=systemd:$

			} else if (TRIGGER_INSTANCE.containsKey(name)) {

				report = TRIGGER_INSTANCE.get(name).generateReport(submode, message, null, null); // /admin report --module=userdeny:0

			} else if (LISTENER_INSTANCE.containsKey(name)) {

				report = LISTENER_INSTANCE.get(name).generateReport(submode, message, null, null); // /admin report --module=shui:20 --group=1234567890

			} else if (EXECUTOR_INSTANCE.containsKey(name)) {

				report = EXECUTOR_INSTANCE.get(name).generateReport(submode, message, null, null); // /admin report --module=acon --group=1234567890

			} else {

				report[0] = "模块不存在";

			}

		} else {
			report[0] = "module参数错误：" + module;
		}

		return report;

	}

	/**
	 * 你永远不应该执行这个方法
	 *
	 * @param level 你永远不应该执行这个方法
	 * @return 你永远不应该执行这个方法
	 * @throws Exception 你永远不应该执行这个方法
	 */
	public String[] doInit(int level) throws Exception {

		BufferX builder = new BufferX();

		builder.info(LoggerX.datetime());

		switch (level) {

		case 0:
			builder.info("init 0：切换启停");
			if (entry.isEnable()) {
				entry.setEnable(false);
				builder.info("切换至停机");
			} else {
				entry.setEnable(true);
				builder.info("切换至运行");
			}
			break;

		case 1:
			builder.info("init 1：初始化");
			Systemd.this.init();
			break;

		case 2:
			builder.info("init 2：启动");
			Systemd.this.boot();
			break;

		case 3:
			builder.info("init 3：保存");
			Systemd.this.save();
			break;

		case 4:
			builder.info("init 4：关闭丢弃");
			Systemd.this.shut();
			break;

		case 5:
			builder.info("init 5：保存关闭");
			Systemd.this.save();
			Systemd.this.shut();
			break;

		case 6:
			builder.info("init 6：保存重启");
			Systemd.this.save();
			Systemd.this.shut();
			Systemd.this.boot();
			break;

		}

		return builder.make();

	}

	/**
	 * 生成list命令发送的内容 你永远不应该执行这个方法
	 *
	 * @param flagname  标题名字
	 * @param triggers  触发器列表
	 * @param listeners 监听器列表
	 * @param executors 执行器列表
	 * @return 生成好的/list
	 */
	private String generateListMessage(String flagname, ArrayList<ModuleTrigger> triggers, ArrayList<ModuleListener> listeners, TreeMap<String, ModuleExecutor> executors) {

		StringBuilder builder = new StringBuilder();

		builder.append("=================\r\n" + flagname + "启用的模块\r\n=================\r\n启用的触发器：");

		if (triggers.size() == 0) {
			builder.append("无\r\n");
		} else {
			builder.append(triggers.size() + "\r\n");
			for (ModuleTrigger module : triggers) {
				builder.append(module.MODULE_COMMANDNAME() + " > " + module.MODULE_DISPLAYNAME() + "：" + module.MODULE_DESCRIPTION() + "\r\n");
			}
		}

		builder.append("=================\r\n启用的监听器：");

		if (listeners.size() == 0) {
			builder.append("无\r\n");
		} else {
			builder.append(listeners.size() + "\r\n");
			for (ModuleListener module : listeners) {
				builder.append(module.MODULE_COMMANDNAME() + " > " + module.MODULE_DISPLAYNAME() + "：" + module.MODULE_DESCRIPTION() + "\r\n");
			}
		}

		builder.append("\r\n=================\r\n可用的执行器： ");

		if (executors.size() == 0) {
			builder.append("无\r\n");
		} else {
			builder.append(executors.size() + "\r\n");
			for (String temp : executors.keySet()) {
				ModuleExecutor module = executors.get(temp);
				module.genFullHelp();
				builder.append(module.MODULE_COMMANDNAME() + " > " + module.MODULE_DISPLAYNAME() + "：" + module.MODULE_DESCRIPTION() + "\r\n");
			}
		}

		builder.append("=================");

		return builder.toString();

	}

	public static String sha1(String message) {

		try {

			StringBuffer hexString = new StringBuffer();
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
			byte[] hashBytes = messageDigest.digest(message.getBytes(StandardCharsets.UTF_8));

			for (byte hashByte : hashBytes) {
				int value = (hashByte) & 0xff;
				if (value < 16) { hexString.append("0"); }
				hexString.append(Integer.toHexString(value));
			}

			return hexString.toString();

		} catch (NoSuchAlgorithmException exception) {
			// wtf, is there any java runtime not support SHA-1?
			return null;
		}

	}

	// ==========================================================================================================================================================
	//
	// 消息发送
	//
	// ==========================================================================================================================================================

	/**
	 * 判断一个ID是否为BOT自身 entry对此方法进行了转发请勿在此执行
	 *
	 * @param userid 用户ID
	 * @return 是 / 否
	 */
	public boolean isMyself(long userid) {
		return USERID_CQBOT == userid;
	}

	/**
	 * 判断一个ID是否为管理员（JCQ设置的管理，并非QQ群管理） entry对此方法进行了转发请勿在此执行
	 *
	 * @param userid 用户ID
	 * @return 是 / 否
	 */
	public boolean isAdmin(long userid) {
		return USERID_ADMIN == userid;
	}

	/**
	 * 给管理员发消息 entry对此方法进行了转发请勿在此执行
	 *
	 * @param message 消息
	 */
	public void adminInfo(String message) {
		entry.getCQ().sendPrivateMsg(USERID_ADMIN, message);
	}

	/**
	 * 给管理员发消息 entry对此方法进行了转发请勿在此执行
	 *
	 * @param message 消息
	 */
	public void adminInfo(String... message) {
		for (String temp : message) {
			entry.getCQ().sendPrivateMsg(USERID_ADMIN, temp);
		}
	}

	/**
	 * 给用户发私聊 entry对此方法进行了转发请勿在此执行
	 *
	 * @param userid  用户ID
	 * @param message 消息
	 */
	public void userInfo(long userid, String message) {
		entry.getCQ().sendPrivateMsg(userid, message);
	}

	/**
	 * 给用户发私聊 entry对此方法进行了转发请勿在此执行
	 *
	 * @param userid  用户ID
	 * @param message 消息
	 */
	public void userInfo(long userid, String... message) {
		for (String temp : message) {
			entry.getCQ().sendPrivateMsg(userid, temp);
		}
	}

	/**
	 * 在讨论组发消息 entry对此方法进行了转发请勿在此执行
	 *
	 * @param diszid  讨论组ID
	 * @param message 消息
	 */
	public void diszInfo(long diszid, String message) {
		entry.getCQ().sendDiscussMsg(diszid, message);
	}

	/**
	 * 在讨论组发消息 entry对此方法进行了转发请勿在此执行
	 *
	 * @param diszid  讨论组ID
	 * @param message 消息
	 */
	public void diszInfo(long diszid, String... message) {
		for (String temp : message) {
			entry.getCQ().sendDiscussMsg(diszid, temp);
		}
	}

	/**
	 * 在讨论组发消息 并at某人 entry对此方法进行了转发请勿在此执行
	 *
	 * @param diszid  讨论组ID
	 * @param userid  用户ID
	 * @param message 消息
	 */
	public void diszInfo(long diszid, long userid, String message) {
		entry.getCQ().sendDiscussMsg(diszid, "[CQ:at,qq=" + userid + "] " + message);
	}

	/**
	 * 在群聊发消息 entry对此方法进行了转发请勿在此执行
	 *
	 * @param gropid  群组ID
	 * @param message 消息
	 */
	public void gropInfo(long gropid, String message) {
		if (MESSAGE_MUTE.contains(gropid)) {
			System.out.println("关闭发言 " + gropid + "：" + message);
		} else {
			entry.getCQ().sendGroupMsg(gropid, message);
		}
	}

	/**
	 * 在群聊发消息 entry对此方法进行了转发请勿在此执行
	 *
	 * @param gropid  群组ID
	 * @param message 消息
	 */
	public void gropInfo(long gropid, String... message) {
		if (MESSAGE_MUTE.contains(gropid)) {
			System.out.println("关闭发言 " + gropid + "：" + message);
		} else {
			for (String temp : message) {
				entry.getCQ().sendGroupMsg(gropid, temp);
			}
		}
	}

	/**
	 * 在群聊发消息 并at某人 entry对此方法进行了转发请勿在此执行
	 *
	 * @param gropid  群组ID
	 * @param userid  用户ID
	 * @param message 消息
	 */
	public void gropInfo(long gropid, long userid, String message) {
		if (MESSAGE_MUTE.contains(gropid)) {
			System.out.println("关闭发言 " + gropid + "：" + message);
		} else {
			entry.getCQ().sendGroupMsg(gropid, "[CQ:at,qq=" + userid + "] " + message);
		}
	}

	/**
	 * 私聊某人 /info entry对此方法进行了转发请勿在此执行
	 *
	 * @param userid 用户ID
	 */
	public void sendInfo(long userid) {
		entry.getCQ().sendPrivateMsg(userid, MESSAGE_INFO);
	}

	/**
	 * 私聊某人 /eula entry对此方法进行了转发请勿在此执行
	 *
	 * @param userid 用户ID
	 */
	public void sendEula(long userid) {
		entry.getCQ().sendPrivateMsg(userid, MESSAGE_EULA);
	}

	/**
	 * 私聊某人 /help entry对此方法进行了转发请勿在此执行
	 *
	 * @param userid 用户ID
	 */
	public void sendHelp(long userid) {
		entry.getCQ().sendPrivateMsg(userid, MESSAGE_HELP);
	}

	/**
	 * 私聊某人 /help $name entry对此方法进行了转发请勿在此执行
	 *
	 * @param userid 用户ID
	 * @param name   模块名
	 */
	public void sendHelp(long userid, String name) {
		if (EXECUTOR_INSTANCE.containsKey(name)) {
			entry.getCQ().sendPrivateMsg(userid, EXECUTOR_INSTANCE.get(name).MODULE_FULLHELP);
		} else {
			entry.getCQ().sendPrivateMsg(userid, "不存在名叫" + name + "的模块。请使用/list查询。");
		}
	}

	/**
	 * 私聊某人 私聊模式/list entry对此方法进行了转发请勿在此执行
	 *
	 * @param userid 用户ID
	 */
	public void sendListUser(long userid) {
		entry.getCQ().sendPrivateMsg(userid, MESSAGE_LIST_USER);
	}

	/**
	 * 私聊某人 讨论组模式/list entry对此方法进行了转发请勿在此执行
	 *
	 * @param userid 用户ID
	 */
	public void sendListDisz(long userid) {
		entry.getCQ().sendPrivateMsg(userid, MESSAGE_LIST_DISZ);
	}

	/**
	 * 私聊某人 群组模式/list entry对此方法进行了转发请勿在此执行
	 *
	 * @param userid 用户ID
	 */
	public void sendListGrop(long userid) {
		entry.getCQ().sendPrivateMsg(userid, MESSAGE_LIST_GROP);
	}

	// ==========================================================================================================================================================
	//
	// 昵称映射
	//
	// ==========================================================================================================================================================

	/**
	 * 从昵称对应表查找昵称 entry对此方法进行了转发请勿在此执行
	 *
	 * @param userid 用户ID
	 * @return 昵称
	 */
	public String getNickname(long userid) {
		return entry.getCQ().getStrangerInfo(userid).getNick();
	}

	/**
	 * 从昵称对应表查找昵称 entry对此方法进行了转发请勿在此执行
	 *
	 * @param gropid 群组ID
	 * @param userid 用户ID
	 * @return 昵称
	 */
	public String getGropNick(long gropid, long userid) {
		if (NICKNAME_MAP.containsKey(gropid)) {
			TreeMap<Long, String> temp = NICKNAME_MAP.get(gropid);
			if (temp.containsKey(userid)) { return temp.get(userid); }
		}
		return entry.getCQ().getStrangerInfo(userid).getNick();
	}

	// ==========================================================================================================================================================
	//
	// 对象持有
	//
	// ==========================================================================================================================================================

	/**
	 * 获取定时器 entry对此方法进行了转发请勿在此执行
	 *
	 * @param name 名称
	 * @return 实例
	 */
	public ModuleScheduler getScheduler(String name) {
		return SCHEDULER_INSTANCE.get(name);
	}

	/**
	 * 获取触发器 entry对此方法进行了转发请勿在此执行
	 *
	 * @param name 名称
	 * @return 实例
	 */
	public ModuleTrigger getTrigger(String name) {
		return TRIGGER_INSTANCE.get(name);
	}

	/**
	 * 获取监听器 entry对此方法进行了转发请勿在此执行
	 *
	 * @param name 名称
	 * @return 实例
	 */
	public ModuleListener getListener(String name) {
		return LISTENER_INSTANCE.get(name);
	}

	/**
	 * 获取执行器 entry对此方法进行了转发请勿在此执行
	 *
	 * @param name 名称
	 * @return 实例
	 */
	public ModuleExecutor getExecutor(String name) {
		return EXECUTOR_INSTANCE.get(name);
	}
}
