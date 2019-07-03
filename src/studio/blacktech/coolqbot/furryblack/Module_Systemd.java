package studio.blacktech.coolqbot.furryblack;

import java.util.ArrayList;
import java.util.TreeMap;

import com.sobte.cqp.jcq.entity.IMsg;
import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.common.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.Module;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleListener;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleTrigger;
import studio.blacktech.coolqbot.furryblack.modules.Executor_acon;
import studio.blacktech.coolqbot.furryblack.modules.Executor_admin;
import studio.blacktech.coolqbot.furryblack.modules.Executor_chou;
import studio.blacktech.coolqbot.furryblack.modules.Executor_dice;
import studio.blacktech.coolqbot.furryblack.modules.Executor_echo;
import studio.blacktech.coolqbot.furryblack.modules.Executor_jrjp;
import studio.blacktech.coolqbot.furryblack.modules.Executor_jrrp;
import studio.blacktech.coolqbot.furryblack.modules.Executor_kong;
import studio.blacktech.coolqbot.furryblack.modules.Executor_mine;
import studio.blacktech.coolqbot.furryblack.modules.Executor_roll;
import studio.blacktech.coolqbot.furryblack.modules.Executor_roulette;
import studio.blacktech.coolqbot.furryblack.modules.Executor_zhan;
import studio.blacktech.coolqbot.furryblack.modules.Listener_TopSpeak;
import studio.blacktech.coolqbot.furryblack.modules.Trigger_UserDeny;
import studio.blacktech.coolqbot.furryblack.modules.Trigger_WordDeny;

public class Module_Systemd extends Module {

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	public static String MODULE_PACKAGENAME = "systemd";
	public static String MODULE_DISPLAYNAME = "系统模块";
	public static String MODULE_DESCRIPTION = "系统模块";
	public static String MODULE_VERSION = "22.0";
	public static String[] MODULE_USAGE = new String[] {};
	public static String[] MODULE_PRIVACY_TRIGER = new String[] {};
	public static String[] MODULE_PRIVACY_LISTEN = new String[] {};
	public static String[] MODULE_PRIVACY_STORED = new String[] {};
	public static String[] MODULE_PRIVACY_CACHED = new String[] {};
	public static String[] MODULE_PRIVACY_OBTAIN = new String[] {};

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================

	private SystemdDelegate delegate = new SystemdDelegate();

	private int COUNT_USER_MESSAGE = 0;
	private int COUNT_DISZ_MESSAGE = 0;
	private int COUNT_GROP_MESSAGE = 0;

	private boolean ENABLE_TRIGGER_USER = false;
	private boolean ENABLE_TRIGGER_DISZ = false;
	private boolean ENABLE_TRIGGER_GROP = false;
	private boolean ENABLE_LISENTER_USER = false;
	private boolean ENABLE_LISENTER_DISZ = false;
	private boolean ENABLE_LISENTER_GROP = false;
	private boolean ENABLE_EXECUTOR_USER = false;
	private boolean ENABLE_EXECUTOR_DISZ = false;
	private boolean ENABLE_EXECUTOR_GROP = false;

	private String CONFIG_TRIGGER_USER;
	private String CONFIG_TRIGGER_DISZ;
	private String CONFIG_TRIGGER_GROP;
	private String CONFIG_LISENTER_USER;
	private String CONFIG_LISENTER_DISZ;
	private String CONFIG_LISENTER_GROP;
	private String CONFIG_EXECUTOR_USER;
	private String CONFIG_EXECUTOR_DISZ;
	private String CONFIG_EXECUTOR_GROP;

	private String[] LIST_TRIGGER_USER = {};
	private String[] LIST_TRIGGER_DISZ = {};
	private String[] LIST_TRIGGER_GROP = {};
	private String[] LIST_LISENTER_USER = {};
	private String[] LIST_LISENTER_DISZ = {};
	private String[] LIST_LISENTER_GROP = {};
	private String[] LIST_EXECUTOR_USER = {};
	private String[] LIST_EXECUTOR_DISZ = {};
	private String[] LIST_EXECUTOR_GROP = {};

	private TreeMap<String, Module> MODULE_INSTACE = new TreeMap<>();

	private TreeMap<String, ModuleTrigger> TRIGGER_INSTANCE = new TreeMap<>();
	private ArrayList<ModuleTrigger> TRIGGER_USER = new ArrayList<>(100);
	private ArrayList<ModuleTrigger> TRIGGER_DISZ = new ArrayList<>(100);
	private ArrayList<ModuleTrigger> TRIGGER_GROP = new ArrayList<>(100);

	private TreeMap<String, ModuleListener> LISTENER_INSTANCE = new TreeMap<>();
	private ArrayList<ModuleListener> LISTENER_USER = new ArrayList<>(100);
	private ArrayList<ModuleListener> LISTENER_DISZ = new ArrayList<>(100);
	private ArrayList<ModuleListener> LISTENER_GROP = new ArrayList<>(100);

	private TreeMap<String, ModuleExecutor> EXECUTOR_INSTANCE = new TreeMap<>();
	private TreeMap<String, ModuleExecutor> EXECUTOR_USER = new TreeMap<>();
	private TreeMap<String, ModuleExecutor> EXECUTOR_DISZ = new TreeMap<>();
	private TreeMap<String, ModuleExecutor> EXECUTOR_GROP = new TreeMap<>();

	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================

	public Module_Systemd() throws Exception {
		super(MODULE_DISPLAYNAME, MODULE_PACKAGENAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_TRIGER, MODULE_PRIVACY_LISTEN, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	@Override
	public void init(LoggerX logger) throws Exception {
		if (this.NEW_CONFIG) {
			logger.seek("[Systemd] 配置文件不存在 - 生成默认配置");
			this.CONFIG.setProperty("trigger_user", "none");
			this.CONFIG.setProperty("trigger_disz", "none");
			this.CONFIG.setProperty("trigger_grop", "none");
			this.CONFIG.setProperty("listener_user", "none");
			this.CONFIG.setProperty("listener_disz", "none");
			this.CONFIG.setProperty("listener_grop", "none");
			this.CONFIG.setProperty("executor_user", "none");
			this.CONFIG.setProperty("executor_disz", "none");
			this.CONFIG.setProperty("executor_grop", "none");
			this.saveConfig();
		} else {
			this.loadConfig();
		}

		// =======================================================================================================================
		//
		//
		// =======================================================================================================================
		// 实例化触发器

		this.instantiationTrigger(new Trigger_UserDeny());
		this.instantiationTrigger(new Trigger_WordDeny());

		// =======================================================================================================================
		// 实例化监听器

		this.instantiationListener(new Listener_TopSpeak());

		// =======================================================================================================================
		// 实例化执行器

		this.instantiationExecutor(new Executor_admin());
		this.instantiationExecutor(new Executor_acon());
		this.instantiationExecutor(new Executor_chou());
		this.instantiationExecutor(new Executor_dice());
		this.instantiationExecutor(new Executor_echo());
		this.instantiationExecutor(new Executor_jrjp());
		this.instantiationExecutor(new Executor_jrrp());
		this.instantiationExecutor(new Executor_mine());
		this.instantiationExecutor(new Executor_kong());
		this.instantiationExecutor(new Executor_roll());
		this.instantiationExecutor(new Executor_roulette());
		this.instantiationExecutor(new Executor_zhan());

		// =======================================================================================================================
		//
		//
		// =======================================================================================================================
		// 初始化触发器

		for (final String name : this.TRIGGER_INSTANCE.keySet()) {
			logger.full("[Module] 初始化触发器", name);
			this.TRIGGER_INSTANCE.get(name).init(logger);
		}

		// =======================================================================================================================
		// 初始化监听器

		for (final String name : this.LISTENER_INSTANCE.keySet()) {
			logger.full("[Module] 初始化监听器", name);
			this.LISTENER_INSTANCE.get(name).init(logger);
		}

		// =======================================================================================================================
		// 初始化执行器

		for (final String name : this.EXECUTOR_INSTANCE.keySet()) {
			logger.full("[Module] 初始化执行器", name);
			this.EXECUTOR_INSTANCE.get(name).init(logger);
		}

		// =======================================================================================================================
		//
		//
		// =======================================================================================================================
		// 读取触发器配置

		this.CONFIG_TRIGGER_USER = this.CONFIG.getProperty("trigger_user", "none");
		this.CONFIG_TRIGGER_DISZ = this.CONFIG.getProperty("trigger_disz", "none");
		this.CONFIG_TRIGGER_GROP = this.CONFIG.getProperty("trigger_grop", "none");

		this.LIST_TRIGGER_USER = this.CONFIG_TRIGGER_USER.equals("none") ? new String[0] : this.CONFIG_TRIGGER_USER.split(",");
		this.LIST_TRIGGER_DISZ = this.CONFIG_TRIGGER_DISZ.equals("none") ? new String[0] : this.CONFIG_TRIGGER_DISZ.split(",");
		this.LIST_TRIGGER_GROP = this.CONFIG_TRIGGER_GROP.equals("none") ? new String[0] : this.CONFIG_TRIGGER_GROP.split(",");

		logger.seek("[CONF] 触发器：私聊" + this.CONFIG_TRIGGER_USER);
		logger.seek("[CONF] 触发器：组聊" + this.CONFIG_TRIGGER_DISZ);
		logger.seek("[CONF] 触发器：群聊" + this.CONFIG_TRIGGER_GROP);

		// =======================================================================================================================
		// 读取监听器配置

		this.CONFIG_LISENTER_USER = this.CONFIG.getProperty("listener_user", "none");
		this.CONFIG_LISENTER_DISZ = this.CONFIG.getProperty("listener_disz", "none");
		this.CONFIG_LISENTER_GROP = this.CONFIG.getProperty("listener_grop", "none");

		this.LIST_LISENTER_USER = this.CONFIG_LISENTER_USER.equals("none") ? new String[0] : this.CONFIG_LISENTER_USER.split(",");
		this.LIST_LISENTER_DISZ = this.CONFIG_LISENTER_DISZ.equals("none") ? new String[0] : this.CONFIG_LISENTER_DISZ.split(",");
		this.LIST_LISENTER_GROP = this.CONFIG_LISENTER_GROP.equals("none") ? new String[0] : this.CONFIG_LISENTER_GROP.split(",");

		logger.seek("[CONF] 监听器：私聊" + this.CONFIG_LISENTER_USER);
		logger.seek("[CONF] 监听器：组聊" + this.CONFIG_LISENTER_DISZ);
		logger.seek("[CONF] 监听器：群聊" + this.CONFIG_LISENTER_GROP);

		// =======================================================================================================================
		// 读取执行器配置

		this.CONFIG_EXECUTOR_USER = this.CONFIG.getProperty("executor_user", "none");
		this.CONFIG_EXECUTOR_DISZ = this.CONFIG.getProperty("executor_disz", "none");
		this.CONFIG_EXECUTOR_GROP = this.CONFIG.getProperty("executor_grop", "none");

		this.LIST_EXECUTOR_USER = this.CONFIG_EXECUTOR_USER.equals("none") ? new String[0] : this.CONFIG_EXECUTOR_USER.split(",");
		this.LIST_EXECUTOR_DISZ = this.CONFIG_EXECUTOR_DISZ.equals("none") ? new String[0] : this.CONFIG_EXECUTOR_DISZ.split(",");
		this.LIST_EXECUTOR_GROP = this.CONFIG_EXECUTOR_GROP.equals("none") ? new String[0] : this.CONFIG_EXECUTOR_GROP.split(",");

		logger.seek("[CONF] 执行器：私聊" + this.CONFIG_EXECUTOR_USER);
		logger.seek("[CONF] 执行器：组聊" + this.CONFIG_EXECUTOR_DISZ);
		logger.seek("[CONF] 执行器：群聊" + this.CONFIG_EXECUTOR_GROP);

		// =======================================================================================================================
		//
		//
		// =======================================================================================================================
		// 注册触发器

		for (String name : this.LIST_TRIGGER_USER) {
			this.registerTriggerUser(this.getTrigger(name));
		}

		for (String name : this.LIST_TRIGGER_DISZ) {
			this.registerTriggerDisz(this.getTrigger(name));
		}

		for (String name : this.LIST_TRIGGER_GROP) {
			this.registerTriggerGrop(this.getTrigger(name));
		}

		this.ENABLE_TRIGGER_USER = this.TRIGGER_USER.size() > 0;
		this.ENABLE_TRIGGER_DISZ = this.TRIGGER_DISZ.size() > 0;
		this.ENABLE_TRIGGER_GROP = this.TRIGGER_GROP.size() > 0;

		logger.seek("[Module] 触发器");
		logger.seek("  私聊：", this.ENABLE_TRIGGER_USER ? "启用 - " + this.TRIGGER_USER.size() + "个" : "禁用");
		logger.seek("  组聊：", this.ENABLE_TRIGGER_DISZ ? "启用 - " + this.TRIGGER_DISZ.size() + "个" : "禁用");
		logger.seek("  群聊：", this.ENABLE_TRIGGER_GROP ? "启用 - " + this.TRIGGER_GROP.size() + "个" : "禁用");

		// =======================================================================================================================
		// 注册监听器

		for (String name : this.LIST_LISENTER_USER) {
			this.registerListenerUser(this.getListener(name));
		}

		for (String name : this.LIST_LISENTER_DISZ) {
			this.registerListenerDisz(this.getListener(name));
		}

		for (String name : this.LIST_LISENTER_GROP) {
			this.registerListenerGrop(this.getListener(name));
		}

		this.ENABLE_LISENTER_USER = this.LISTENER_USER.size() > 0;
		this.ENABLE_LISENTER_DISZ = this.LISTENER_DISZ.size() > 0;
		this.ENABLE_LISENTER_GROP = this.LISTENER_GROP.size() > 0;

		logger.seek("[Module] 监听器");
		logger.seek("  私聊：", this.ENABLE_LISENTER_USER ? "启用 - " + this.LISTENER_DISZ.size() + "个" : "禁用");
		logger.seek("  组聊：", this.ENABLE_LISENTER_DISZ ? "启用 - " + this.LISTENER_DISZ.size() + "个" : "禁用");
		logger.seek("  群聊：", this.ENABLE_LISENTER_GROP ? "启用 - " + this.LISTENER_GROP.size() + "个" : "禁用");

		// =======================================================================================================================
		// 注册执行器

		for (String name : this.LIST_EXECUTOR_USER) {
			this.registerExecutorUser(this.getExecutor(name));
		}

		for (String name : this.LIST_EXECUTOR_DISZ) {
			this.registerExecutorDisz(this.getExecutor(name));
		}

		for (String name : this.LIST_EXECUTOR_GROP) {
			this.registerExecutorGrop(this.getExecutor(name));
		}

		this.ENABLE_EXECUTOR_USER = this.EXECUTOR_USER.size() > 0;
		this.ENABLE_EXECUTOR_DISZ = this.EXECUTOR_USER.size() > 0;
		this.ENABLE_EXECUTOR_GROP = this.EXECUTOR_GROP.size() > 0;

		logger.seek("[Module] 执行器");
		logger.seek("  私聊：", this.ENABLE_LISENTER_USER ? "启用 - " + this.LISTENER_DISZ.size() + "个" : "禁用");
		logger.seek("  组聊：", this.ENABLE_LISENTER_DISZ ? "启用 - " + this.LISTENER_DISZ.size() + "个" : "禁用");
		logger.seek("  群聊：", this.ENABLE_LISENTER_GROP ? "启用 - " + this.LISTENER_GROP.size() + "个" : "禁用");

		// =======================================================================================================================
		//
		//
		// =======================================================================================================================
		// 预生成 /list 的信息

		entry.getMessage().genetateList(this.TRIGGER_USER, this.TRIGGER_DISZ, this.TRIGGER_GROP, this.LISTENER_USER, this.LISTENER_DISZ, this.LISTENER_GROP, this.EXECUTOR_USER, this.EXECUTOR_DISZ, this.EXECUTOR_GROP);
	}

	@Override
	public void boot(LoggerX logger) throws Exception {

		// =======================================================================================================================
		// 启动触发器

		for (final String name : this.TRIGGER_INSTANCE.keySet()) {
			logger.full("[Module] 启动触发器", name);
			this.TRIGGER_INSTANCE.get(name).boot(logger);
		}

		// =======================================================================================================================
		// 启动 监听器

		for (final String name : this.LISTENER_INSTANCE.keySet()) {
			logger.full("[Module] 启动监听器", name);
			this.LISTENER_INSTANCE.get(name).boot(logger);
		}

		// =======================================================================================================================
		// 启动执行器

		for (final String name : this.EXECUTOR_INSTANCE.keySet()) {
			logger.full("[Module] 启动执行器", name);
			this.EXECUTOR_INSTANCE.get(name).boot(logger);
		}

	}

	@Override
	public void shut(LoggerX logger) throws Exception {
		for (final String name : this.TRIGGER_INSTANCE.keySet()) {
			this.TRIGGER_INSTANCE.get(name).shut(logger);
		}
		for (final String name : this.LISTENER_INSTANCE.keySet()) {
			this.LISTENER_INSTANCE.get(name).shut(logger);
		}
		for (final String name : this.EXECUTOR_INSTANCE.keySet()) {
			this.EXECUTOR_INSTANCE.get(name).shut(logger);
		}
	}

	@Override
	public void reload(LoggerX logger) throws Exception {
		for (final String name : this.TRIGGER_INSTANCE.keySet()) {
			this.TRIGGER_INSTANCE.get(name).reload(logger);
		}
		for (final String name : this.LISTENER_INSTANCE.keySet()) {
			this.LISTENER_INSTANCE.get(name).reload(logger);
		}
		for (final String name : this.EXECUTOR_INSTANCE.keySet()) {
			this.EXECUTOR_INSTANCE.get(name).reload(logger);
		}
	}

	@Override
	public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) throws Exception {
		LoggerX logger = new LoggerX();

		logger.info(LoggerX.time());
		logger.info(" - [加群] ");
		logger.info(typeid == 1 ? "自主申请" : "邀请加群");
		logger.info("\r\n时间：");
		logger.info(sendtime);
		logger.info("\r\n群号：");
		logger.info(gropid);
		logger.info("\r\n管理：");
		logger.info(JcqApp.CQ.getStrangerInfo(operid).getNick());
		logger.info("(");
		logger.info(operid);
		logger.info(")\r\n成员：");
		logger.info(userid);
		logger.info(JcqApp.CQ.getStrangerInfo(userid).getNick());
		logger.info("(");
		logger.info(userid);
		logger.info(")");

		entry.getMessage().adminInfo(logger.make(1));

		for (final String name : this.TRIGGER_INSTANCE.keySet()) {
			this.TRIGGER_INSTANCE.get(name).groupMemberIncrease(typeid, sendtime, gropid, operid, userid);
		}
		for (final String name : this.LISTENER_INSTANCE.keySet()) {
			this.LISTENER_INSTANCE.get(name).groupMemberIncrease(typeid, sendtime, gropid, operid, userid);
		}
		for (final String name : this.EXECUTOR_INSTANCE.keySet()) {
			this.EXECUTOR_INSTANCE.get(name).groupMemberIncrease(typeid, sendtime, gropid, operid, userid);
		}
	}

	@Override
	public void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) throws Exception {
		LoggerX logger = new LoggerX();

		logger.info(LoggerX.time());
		logger.info(" - [退群] ");
		logger.info(typeid == 1 ? "自主退群" : "管理踢出");
		logger.info("\r\n时间：");
		logger.info(sendtime);
		logger.info("\r\n群号：");
		logger.info(gropid);
		logger.info("\r\n管理：");
		logger.info(JcqApp.CQ.getStrangerInfo(operid).getNick());
		logger.info("(");
		logger.info(operid);
		logger.info(")\r\n成员：");
		logger.info(userid);
		logger.info(JcqApp.CQ.getStrangerInfo(userid).getNick());
		logger.info("(");
		logger.info(userid);
		logger.info(")");

		entry.getMessage().adminInfo(logger.make(1));

		for (final String name : this.TRIGGER_INSTANCE.keySet()) {
			this.TRIGGER_INSTANCE.get(name).groupMemberDecrease(typeid, sendtime, gropid, operid, userid);
		}
		for (final String name : this.LISTENER_INSTANCE.keySet()) {
			this.LISTENER_INSTANCE.get(name).groupMemberDecrease(typeid, sendtime, gropid, operid, userid);
		}
		for (final String name : this.EXECUTOR_INSTANCE.keySet()) {
			this.EXECUTOR_INSTANCE.get(name).groupMemberDecrease(typeid, sendtime, gropid, operid, userid);
		}
	}

	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	protected int doUserMessage(final int typeid, final long userid, final MessageUser message, final int messageid, final int messagefont) throws Exception {

		this.COUNT_USER_MESSAGE++;

		// ===============================================================================================================================

		if (this.ENABLE_TRIGGER_USER) {
			for (final ModuleTrigger temp : this.TRIGGER_USER) {
				if (temp.executeUserMessage(typeid, userid, message, messageid, messagefont)) { return IMsg.MSG_IGNORE; }
			}
		}

		// ===============================================================================================================================

		if (this.ENABLE_LISENTER_USER) {
			for (final ModuleListener temp : this.LISTENER_USER) {
				temp.executeUserMessage(typeid, userid, message, messageid, messagefont);
			}
		}

		// ===============================================================================================================================

		if (message.anaylysIsCommand().isCommand()) {

			switch (message.parseOption().getCommand()) {

			// ===============================================================================================================================
			case "info":
				entry.getMessage().sendInfo(userid);
				break;
			// ===============================================================================================================================
			case "eula":
				entry.getMessage().sendEula(userid);
				break;
			// ===============================================================================================================================
			case "list":
				entry.getMessage().sendListUser(userid);
				break;
			// ===============================================================================================================================
			case "help":
				if (message.getSection() == 0) {
					entry.getMessage().sendHelp(userid);
				} else {
					if (this.EXECUTOR_INSTANCE.containsKey(message.getSegment()[0])) { entry.getMessage().sendHelp(userid, this.EXECUTOR_INSTANCE.get(message.getSegment()[0])); }
				}
				break;
			// ===============================================================================================================================
			default:
				if (this.ENABLE_EXECUTOR_USER && this.EXECUTOR_USER.containsKey(message.getCommand())) {
					this.EXECUTOR_USER.get(message.getCommand()).executeUserMessage(typeid, userid, message, messageid, messagefont);
				} else {
					entry.getMessage().userInfo(userid, "没有此插件");
				}
				break;
			}
			// ===============================================================================================================================

		} else {

			entry.getMessage().userInfo(userid, "未识别的内容，本BOT没有聊天功能，请使用/help查看帮助。");

		}

		return IMsg.MSG_IGNORE;
	}

	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	protected int doDiszMessage(final long diszid, final long userid, final MessageDisz message, final int messageid, final int messagefont) throws Exception {

		this.COUNT_DISZ_MESSAGE++;

		// ===============================================================================================================================

		if (this.ENABLE_TRIGGER_DISZ) {
			for (final ModuleTrigger temp : this.TRIGGER_DISZ) {
				if (temp.executeDiszMessage(diszid, userid, message, messageid, messagefont)) { return IMsg.MSG_IGNORE; }
			}
		}

		// ===============================================================================================================================

		if (this.ENABLE_LISENTER_DISZ) {
			for (final ModuleListener temp : this.LISTENER_DISZ) {
				temp.executeDiszMessage(diszid, userid, message, messageid, messagefont);
			}
		}

		// ===============================================================================================================================

		if (message.anaylysIsCommand().isCommand()) {

			switch (message.parseOption().getCommand()) {
			// ===============================================================================================================================
			case "info":
				entry.getMessage().sendInfo(userid);
				break;
			// ===============================================================================================================================
			case "eula":
				entry.getMessage().sendEula(userid);
				break;
			// ===============================================================================================================================
			case "list":
				entry.getMessage().sendListUser(userid);
				break;
			// ===============================================================================================================================
			case "help":
				if (message.getSection() == 0) {
					entry.getMessage().sendHelp(userid);
				} else {
					if (this.EXECUTOR_INSTANCE.containsKey(message.getSegment()[0])) {
						entry.getMessage().sendHelp(userid, this.EXECUTOR_INSTANCE.get(message.getSegment()[0]));
					} else {
						entry.getMessage().userInfo(userid, "没有此插件");
					}
				}
				break;
			// ===============================================================================================================================
			default:
				if (this.ENABLE_EXECUTOR_DISZ && this.EXECUTOR_DISZ.containsKey(message.getCommand())) {
					this.EXECUTOR_DISZ.get(message.getCommand()).executeDiszMessage(diszid, userid, message, messageid, messagefont);
				} else {
					entry.getMessage().userInfo(userid, "没有此插件");
				}
				break;
			}
			// ===============================================================================================================================
		}
		return IMsg.MSG_IGNORE;
	}

	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	protected int doGropMessage(final long gropid, final long userid, final MessageGrop message, final int messageid, final int messagefont) throws Exception {

		this.COUNT_GROP_MESSAGE++;

		// ===============================================================================================================================

		if (this.ENABLE_TRIGGER_GROP) {
			for (final ModuleTrigger temp : this.TRIGGER_GROP) {
				if (temp.executeGropMessage(gropid, userid, message, messageid, messagefont)) { return IMsg.MSG_IGNORE; }
			}
		}

		// ===============================================================================================================================

		if (this.ENABLE_LISENTER_GROP) {
			for (final ModuleListener temp : this.LISTENER_GROP) {
				temp.executeGropMessage(gropid, userid, message, messageid, messagefont);
			}
		}

		// ===============================================================================================================================

		if (message.anaylysIsCommand().isCommand()) {

			switch (message.parseOption().getCommand()) {
			// ===============================================================================================================================
			case "info":
				entry.getMessage().sendInfo(userid);
				break;
			// ===============================================================================================================================
			case "eula":
				entry.getMessage().sendEula(userid);
				break;
			// ===============================================================================================================================
			case "list":
				entry.getMessage().sendListUser(userid);
				break;
			// ===============================================================================================================================
			case "help":
				if (message.getSection() == 0) {
					entry.getMessage().sendHelp(userid);
				} else {
					if (this.EXECUTOR_INSTANCE.containsKey(message.getSegment()[0])) {
						entry.getMessage().sendHelp(userid, this.EXECUTOR_INSTANCE.get(message.getSegment()[0]));
					} else {
						entry.getMessage().userInfo(userid, "没有此插件");
					}
				}
				break;
			// ===============================================================================================================================
			default:
				if (this.ENABLE_EXECUTOR_GROP && this.EXECUTOR_GROP.containsKey(message.getCommand())) {
					this.EXECUTOR_GROP.get(message.getCommand()).executeGropMessage(gropid, userid, message, messageid, messagefont);
				} else {
					entry.getMessage().userInfo(userid, "没有此插件");
				}
				break;
			}
			// ===============================================================================================================================
		}
		return IMsg.MSG_IGNORE;
	}

	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	public LoggerX reloadTrigger(String name, ModuleTrigger instance) throws Exception {
		LoggerX logger = new LoggerX();

		return logger;
	}

	public ModuleTrigger getTrigger(final String name) {
		if (this.TRIGGER_INSTANCE.containsKey(name)) {
			return this.TRIGGER_INSTANCE.get(name);
		} else {
			return null;
		}
	}

	private void instantiationTrigger(ModuleTrigger instance) {
		this.TRIGGER_INSTANCE.put(instance.MODULE_PACKAGENAME(), instance);
		MODULE_INSTACE.put(instance.MODULE_PACKAGENAME(), instance);
	}

	private void registerTriggerUser(final ModuleTrigger trigger) {
		if (trigger.ENABLE_USER) { this.TRIGGER_USER.add(trigger); }
	}

	private void registerTriggerDisz(final ModuleTrigger trigger) {
		if (trigger.ENABLE_DISZ) { this.TRIGGER_DISZ.add(trigger); }
	}

	private void registerTriggerGrop(final ModuleTrigger trigger) {
		if (trigger.ENABLE_GROP) { this.TRIGGER_GROP.add(trigger); }
	}

	// ==========================================================================================================================

	public ModuleListener getListener(final String name) {
		if (this.LISTENER_INSTANCE.containsKey(name)) {
			return this.LISTENER_INSTANCE.get(name);
		} else {
			return null;
		}
	}

	public void instantiationListener(ModuleListener instance) {
		this.LISTENER_INSTANCE.put(instance.MODULE_PACKAGENAME(), instance);
		MODULE_INSTACE.put(instance.MODULE_PACKAGENAME(), instance);
	}

	public void registerListenerUser(final ModuleListener listener) {
		if (listener.ENABLE_USER) { this.LISTENER_USER.add(listener); }
	}

	public void registerListenerDisz(final ModuleListener listener) {
		if (listener.ENABLE_DISZ) { this.LISTENER_DISZ.add(listener); }
	}

	public void registerListenerGrop(final ModuleListener listener) {
		if (listener.ENABLE_GROP) { this.LISTENER_GROP.add(listener); }
	}

	// ==========================================================================================================================

	public ModuleExecutor getExecutor(final String name) {
		if (this.EXECUTOR_INSTANCE.containsKey(name)) {
			return this.EXECUTOR_INSTANCE.get(name);
		} else {
			return null;
		}
	}

	public void instantiationExecutor(ModuleExecutor instance) {
		this.EXECUTOR_INSTANCE.put(instance.MODULE_PACKAGENAME(), instance);
		MODULE_INSTACE.put(instance.MODULE_PACKAGENAME(), instance);
	}

	public void registerExecutorUser(final ModuleExecutor executor) {
		if (executor.ENABLE_USER) { this.EXECUTOR_USER.put(executor.MODULE_PACKAGENAME(), executor); }
	}

	public void registerExecutorDisz(final ModuleExecutor executor) {
		if (executor.ENABLE_DISZ) { this.EXECUTOR_DISZ.put(executor.MODULE_PACKAGENAME(), executor); }
	}

	public void registerExecutorGrop(final ModuleExecutor executor) {
		if (executor.ENABLE_GROP) { this.EXECUTOR_GROP.put(executor.MODULE_PACKAGENAME(), executor); }
	}

	// ==========================================================================================================================

	@Override
	public String[] generateReport(int mode, final Message message, final Object... parameters) {
		return null;
	}

	// ==========================================================================================================================

	public String doGenerateSystemReport() {

		StringBuilder builder = new StringBuilder();

		// ===============================================================================

		long uptime = System.currentTimeMillis() - entry.BOOTTIME;

		long uptimedd = uptime / 86400000;
		uptime = uptime % 86400000;
		long uptimehh = uptime / 3600000;
		uptime = uptime % 3600000;
		long uptimemm = uptime / 60000;
		uptime = uptime % 60000;
		long uptimess = uptime / 1000;

		final long totalMemory = Runtime.getRuntime().totalMemory() / 1024;
		final long freeMemory = Runtime.getRuntime().freeMemory() / 1024;

		// ===============================================================================

		builder.append(LoggerX.time());
		builder.append(" - 状态简报\r\n\r\n运行时间: ");
		builder.append(uptimedd);
		builder.append(" - ");
		builder.append(uptimehh);
		builder.append(":");
		builder.append(uptimemm);
		builder.append(":");
		builder.append(uptimess);

		// ===============================================================================

		builder.append("\r\n系统内存: ");
		builder.append(totalMemory - freeMemory);
		builder.append("KB /");
		builder.append(totalMemory);
		builder.append("KB");

		// ===============================================================================

		builder.append("\r\n\r\n调用-私聊： ");
		builder.append(this.COUNT_USER_MESSAGE);
		builder.append("次\r\n调用-组聊： ");
		builder.append(this.COUNT_DISZ_MESSAGE);
		builder.append("次\r\n调用-群聊： ");
		builder.append(this.COUNT_GROP_MESSAGE);
		builder.append("次");

		// ===============================================================================

		builder.append("\r\n\r\n触发器: ");
		builder.append(this.TRIGGER_INSTANCE.size());
		builder.append("个");

		for (final String name : this.TRIGGER_INSTANCE.keySet()) {
			final ModuleTrigger temp = this.TRIGGER_INSTANCE.get(name);
			builder.append("\r\n模块 ");
			builder.append(temp.MODULE_PACKAGENAME());
			builder.append(": ");
			builder.append(temp.COUNT);
			builder.append("次");
		}

		// ===============================================================================

		builder.append("\r\n\r\n监听器: ");
		builder.append(this.TRIGGER_INSTANCE.size());
		builder.append("个");

		for (final String name : this.LISTENER_INSTANCE.keySet()) {
			final ModuleListener temp = this.LISTENER_INSTANCE.get(name);
			builder.append("\r\n模块 ");
			builder.append(temp.MODULE_PACKAGENAME());
			builder.append(": ");
			builder.append(temp.COUNT);
			builder.append("次");
		}

		// ===============================================================================

		builder.append("\r\n\r\n执行器: ");
		builder.append(this.TRIGGER_INSTANCE.size());
		builder.append("个");

		for (final String name : this.EXECUTOR_INSTANCE.keySet()) {
			final ModuleExecutor temp = this.EXECUTOR_INSTANCE.get(name);
			builder.append("\r\n模块 ");
			builder.append(temp.MODULE_PACKAGENAME());
			builder.append(": ");
			builder.append(temp.COUNT);
			builder.append("次");
		}

		// ===============================================================================

		return builder.toString();
	}

	public String doGetModuleFullHelp(String name) {
		if (MODULE_INSTACE.containsKey(name)) {
			return MODULE_INSTACE.get(name).MODULE_FULLHELP;
		} else {
			return null;
		}
	}

	public String[] doGenerateModuleReport(String name, int mode, Message message, Object... parameters) throws Exception {
		if (MODULE_INSTACE.containsKey(name)) {
			return MODULE_INSTACE.get(name).generateReport(mode, message, parameters);
		} else {
			return null;
		}
	}

	public SystemdDelegate getDelegate() {
		return this.delegate;
	}

	public class SystemdDelegate {

		public String generateSystemReport() {
			return doGenerateSystemReport();
		}

		public String getModuleFullHelp(String name) {
			return doGetModuleFullHelp(name);
		}

		public String[] generateModuleReport(String name, int mode, Message message, Object... parameters) throws Exception {
			return doGenerateModuleReport(name, mode, message, parameters);
		}

	}

}
