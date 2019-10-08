package studio.blacktech.coolqbot.furryblack.modules;

import java.util.ArrayList;
import java.util.TreeMap;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.LoggerX.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.Module;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleListener;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleScheduler;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleTrigger;
import studio.blacktech.coolqbot.furryblack.modules.Module_Message.MessageDelegate;
import studio.blacktech.coolqbot.furryblack.modules.Module_Nickmap.NicknameDelegate;
import studio.blacktech.coolqbot.furryblack.modules.Executor.Executor_acon;
import studio.blacktech.coolqbot.furryblack.modules.Executor.Executor_admin;
import studio.blacktech.coolqbot.furryblack.modules.Executor.Executor_chou;
import studio.blacktech.coolqbot.furryblack.modules.Executor.Executor_dice;
import studio.blacktech.coolqbot.furryblack.modules.Executor.Executor_echo;
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

public class Module_Systemd extends Module {

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
	private static String MODULE_VERSION = "26.0";
	private static String[] MODULE_USAGE = new String[] {};
	private static String[] MODULE_PRIVACY_STORED = new String[] {};
	private static String[] MODULE_PRIVACY_CACHED = new String[] {};
	private static String[] MODULE_PRIVACY_OBTAIN = new String[] {};

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================

	private SystemdDelegate delegate = new SystemdDelegate();

	private Module_Nickmap NICKMAP;
	private Module_Message MESSAGE;

	private int COUNT_USER_MESSAGE = 0;
	private int COUNT_DISZ_MESSAGE = 0;
	private int COUNT_GROP_MESSAGE = 0;

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

	private TreeMap<String, Module> RAWMODULE_INSTANCE;

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

	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================

	public Module_Systemd() throws Exception {
		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	@Override
	public void init(LoggerX logger) throws Exception {

		this.initConfFolder();
		this.initCofigurtion();

		this.RAWMODULE_INSTANCE = new TreeMap<>();

		this.SCHEDULER_INSTANCE = new TreeMap<>();
		this.SCHEDULER_ENABLED = new ArrayList<>(100);

		this.TRIGGER_INSTANCE = new TreeMap<>();
		this.TRIGGER_USER = new ArrayList<>(100);
		this.TRIGGER_DISZ = new ArrayList<>(100);
		this.TRIGGER_GROP = new ArrayList<>(100);

		this.LISTENER_INSTANCE = new TreeMap<>();
		this.LISTENER_USER = new ArrayList<>(100);
		this.LISTENER_DISZ = new ArrayList<>(100);
		this.LISTENER_GROP = new ArrayList<>(100);

		this.EXECUTOR_INSTANCE = new TreeMap<>();
		this.EXECUTOR_USER = new TreeMap<>();
		this.EXECUTOR_DISZ = new TreeMap<>();
		this.EXECUTOR_GROP = new TreeMap<>();

		if (this.NEW_CONFIG) {
			logger.seek(MODULE_PACKAGENAME, "配置文件不存在", "生成默认配置");
			this.CONFIG.setProperty("scheduler", "none");
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

		logger.full(MODULE_PACKAGENAME, "实例化模块");

		// =======================================================================================================================
		// 实例化始祖模块

		this.NICKMAP = new Module_Nickmap();
		this.MESSAGE = new Module_Message();

		this.instantiationRawModule(this.NICKMAP);
		this.instantiationRawModule(this.MESSAGE);

		// =======================================================================================================================
		// 实例化定时器

		this.instantiationScheduler(new Scheduler_Dynamic());

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
		this.instantiationExecutor(new Executor_kong());
		this.instantiationExecutor(new Executor_roll());
		this.instantiationExecutor(new Executor_roulette());
		this.instantiationExecutor(new Executor_time());
		this.instantiationExecutor(new Executor_zhan());

		// =======================================================================================================================
		//
		//
		// =======================================================================================================================

		logger.full(MODULE_PACKAGENAME, "初始化模块");

		this.NICKMAP.init(logger);
		this.MESSAGE.init(logger);

		// =======================================================================================================================
		// 初始化定时器
		for (String name : this.SCHEDULER_INSTANCE.keySet()) {
			logger.full(MODULE_PACKAGENAME, "初始化定时器", name);
			this.SCHEDULER_INSTANCE.get(name).init(logger);
		}

		// =======================================================================================================================
		// 初始化触发器

		for (String name : this.TRIGGER_INSTANCE.keySet()) {
			logger.full(MODULE_PACKAGENAME, "初始化触发器", name);
			this.TRIGGER_INSTANCE.get(name).init(logger);
		}

		// =======================================================================================================================
		// 初始化监听器

		for (String name : this.LISTENER_INSTANCE.keySet()) {
			logger.full(MODULE_PACKAGENAME, "初始化监听器", name);
			this.LISTENER_INSTANCE.get(name).init(logger);
		}

		// =======================================================================================================================
		// 初始化执行器

		for (String name : this.EXECUTOR_INSTANCE.keySet()) {
			logger.full(MODULE_PACKAGENAME, "初始化执行器", name);
			this.EXECUTOR_INSTANCE.get(name).init(logger);
		}

		// =======================================================================================================================
		//
		//
		// =======================================================================================================================

		logger.full(MODULE_PACKAGENAME, "读取模块配置列表");

		// =======================================================================================================================
		// 读取定时器配置

		this.CONFIG_SCHEDULER = this.CONFIG.getProperty("scheduler", "none");

		logger.seek(MODULE_PACKAGENAME, "定时器配置 全局", this.CONFIG_SCHEDULER);

		this.LIST_SCHEDULER = this.CONFIG_SCHEDULER.equals("none") ? new String[0] : this.CONFIG_SCHEDULER.split(",");

		// =======================================================================================================================
		// 读取触发器配置

		this.CONFIG_TRIGGER_USER = this.CONFIG.getProperty("trigger_user", "none");
		this.CONFIG_TRIGGER_DISZ = this.CONFIG.getProperty("trigger_disz", "none");
		this.CONFIG_TRIGGER_GROP = this.CONFIG.getProperty("trigger_grop", "none");

		logger.seek(MODULE_PACKAGENAME, "触发器配置 私聊", this.CONFIG_TRIGGER_USER);
		logger.seek(MODULE_PACKAGENAME, "触发器配置 组聊", this.CONFIG_TRIGGER_DISZ);
		logger.seek(MODULE_PACKAGENAME, "触发器配置 群聊", this.CONFIG_TRIGGER_GROP);

		this.LIST_TRIGGER_USER = this.CONFIG_TRIGGER_USER.equals("none") ? new String[0] : this.CONFIG_TRIGGER_USER.split(",");
		this.LIST_TRIGGER_DISZ = this.CONFIG_TRIGGER_DISZ.equals("none") ? new String[0] : this.CONFIG_TRIGGER_DISZ.split(",");
		this.LIST_TRIGGER_GROP = this.CONFIG_TRIGGER_GROP.equals("none") ? new String[0] : this.CONFIG_TRIGGER_GROP.split(",");

		// =======================================================================================================================
		// 读取监听器配置

		this.CONFIG_LISENTER_USER = this.CONFIG.getProperty("listener_user", "none");
		this.CONFIG_LISENTER_DISZ = this.CONFIG.getProperty("listener_disz", "none");
		this.CONFIG_LISENTER_GROP = this.CONFIG.getProperty("listener_grop", "none");

		logger.seek(MODULE_PACKAGENAME, "监听器配置 私聊", this.CONFIG_LISENTER_USER);
		logger.seek(MODULE_PACKAGENAME, "监听器配置 组聊", this.CONFIG_LISENTER_DISZ);
		logger.seek(MODULE_PACKAGENAME, "监听器配置 群聊", this.CONFIG_LISENTER_GROP);

		this.LIST_LISENTER_USER = this.CONFIG_LISENTER_USER.equals("none") ? new String[0] : this.CONFIG_LISENTER_USER.split(",");
		this.LIST_LISENTER_DISZ = this.CONFIG_LISENTER_DISZ.equals("none") ? new String[0] : this.CONFIG_LISENTER_DISZ.split(",");
		this.LIST_LISENTER_GROP = this.CONFIG_LISENTER_GROP.equals("none") ? new String[0] : this.CONFIG_LISENTER_GROP.split(",");

		// =======================================================================================================================
		// 读取执行器配置

		this.CONFIG_EXECUTOR_USER = this.CONFIG.getProperty("executor_user", "none");
		this.CONFIG_EXECUTOR_DISZ = this.CONFIG.getProperty("executor_disz", "none");
		this.CONFIG_EXECUTOR_GROP = this.CONFIG.getProperty("executor_grop", "none");

		logger.seek(MODULE_PACKAGENAME, "执行器配置 私聊", this.CONFIG_EXECUTOR_USER);
		logger.seek(MODULE_PACKAGENAME, "执行器配置 组聊", this.CONFIG_EXECUTOR_DISZ);
		logger.seek(MODULE_PACKAGENAME, "执行器配置 群聊", this.CONFIG_EXECUTOR_GROP);

		this.LIST_EXECUTOR_USER = this.CONFIG_EXECUTOR_USER.equals("none") ? new String[0] : this.CONFIG_EXECUTOR_USER.split(",");
		this.LIST_EXECUTOR_DISZ = this.CONFIG_EXECUTOR_DISZ.equals("none") ? new String[0] : this.CONFIG_EXECUTOR_DISZ.split(",");
		this.LIST_EXECUTOR_GROP = this.CONFIG_EXECUTOR_GROP.equals("none") ? new String[0] : this.CONFIG_EXECUTOR_GROP.split(",");

		// =======================================================================================================================
		//
		//
		// =======================================================================================================================

		logger.full(MODULE_PACKAGENAME, "注册模块");

		// =======================================================================================================================
		// 注册定时器

		for (String name : this.LIST_SCHEDULER) {
			if (this.SCHEDULER_INSTANCE.containsKey(name)) {
				ModuleScheduler instance = this.SCHEDULER_INSTANCE.get(name);
				if (instance.ENABLE()) {
					logger.full(MODULE_PACKAGENAME, "注册定时器 全局", instance.MODULE_PACKAGENAME());
					this.SCHEDULER_ENABLED.add(instance);
				}
			} else {
				logger.mini(MODULE_PACKAGENAME, "配置错误", "定时器不存在 " + name);
			}
		}

		this.ENABLE_SCHEDULER = this.SCHEDULER_ENABLED.size() > 0;

		// =======================================================================================================================
		// 注册触发器

		for (String name : this.LIST_TRIGGER_USER) {
			if (this.TRIGGER_INSTANCE.containsKey(name)) {
				ModuleTrigger instance = this.TRIGGER_INSTANCE.get(name);
				if (instance.ENABLE_USER()) {
					logger.full(MODULE_PACKAGENAME, "注册触发器 私聊", instance.MODULE_PACKAGENAME());
					this.TRIGGER_USER.add(instance);
				}
			} else {
				logger.mini(MODULE_PACKAGENAME, "配置错误", "私聊触发器不存在 " + name);
			}
		}

		for (String name : this.LIST_TRIGGER_DISZ) {
			if (this.TRIGGER_INSTANCE.containsKey(name)) {
				ModuleTrigger instance = this.TRIGGER_INSTANCE.get(name);
				if (instance.ENABLE_DISZ()) {
					logger.full(MODULE_PACKAGENAME, "注册触发器 组聊", instance.MODULE_PACKAGENAME());
					this.TRIGGER_DISZ.add(instance);
				}
			} else {
				logger.mini(MODULE_PACKAGENAME, "配置错误", "组聊触发器不存在 " + name);
			}
		}

		for (String name : this.LIST_TRIGGER_GROP) {
			if (this.TRIGGER_INSTANCE.containsKey(name)) {
				ModuleTrigger instance = this.TRIGGER_INSTANCE.get(name);
				if (instance.ENABLE_GROP()) {
					logger.full(MODULE_PACKAGENAME, "注册触发器 群聊", instance.MODULE_PACKAGENAME());
					this.TRIGGER_GROP.add(instance);
				}
			} else {
				logger.mini(MODULE_PACKAGENAME, "配置错误", "群聊触发器不存在 " + name);
			}
		}

		this.ENABLE_TRIGGER_USER = this.TRIGGER_USER.size() > 0;
		this.ENABLE_TRIGGER_DISZ = this.TRIGGER_DISZ.size() > 0;
		this.ENABLE_TRIGGER_GROP = this.TRIGGER_GROP.size() > 0;

		// =======================================================================================================================
		// 注册监听器

		for (String name : this.LIST_LISENTER_USER) {
			if (this.LISTENER_INSTANCE.containsKey(name)) {
				ModuleListener instance = this.LISTENER_INSTANCE.get(name);
				if (instance.ENABLE_USER()) {
					logger.full(MODULE_PACKAGENAME, "注册监听器 私聊", instance.MODULE_PACKAGENAME());
					this.LISTENER_USER.add(instance);
				}
			} else {
				logger.mini(MODULE_PACKAGENAME, "配置错误", "私聊监听器不存在 " + name);
			}
		}

		for (String name : this.LIST_LISENTER_DISZ) {
			if (this.LISTENER_INSTANCE.containsKey(name)) {
				ModuleListener instance = this.LISTENER_INSTANCE.get(name);
				if (instance.ENABLE_DISZ()) {
					logger.full(MODULE_PACKAGENAME, "注册监听器 组聊", instance.MODULE_PACKAGENAME());
					this.LISTENER_DISZ.add(instance);
				}
			} else {
				logger.mini(MODULE_PACKAGENAME, "配置错误", "组聊监听器不存在 " + name);
			}
		}

		for (String name : this.LIST_LISENTER_GROP) {
			if (this.LISTENER_INSTANCE.containsKey(name)) {
				ModuleListener instance = this.LISTENER_INSTANCE.get(name);
				if (instance.ENABLE_GROP()) {
					logger.full(MODULE_PACKAGENAME, "注册监听器 群聊", instance.MODULE_PACKAGENAME());
					this.LISTENER_GROP.add(instance);
				}
			} else {
				logger.mini(MODULE_PACKAGENAME, "配置错误", "群聊监听器不存在 " + name);
			}
		}

		this.ENABLE_LISENTER_USER = this.LISTENER_USER.size() > 0;
		this.ENABLE_LISENTER_DISZ = this.LISTENER_DISZ.size() > 0;
		this.ENABLE_LISENTER_GROP = this.LISTENER_GROP.size() > 0;

		// =======================================================================================================================
		// 注册执行器

		for (String name : this.LIST_EXECUTOR_USER) {
			if (this.EXECUTOR_INSTANCE.containsKey(name)) {
				ModuleExecutor instance = this.EXECUTOR_INSTANCE.get(name);
				if (instance.ENABLE_USER()) {
					logger.full(MODULE_PACKAGENAME, "注册执行器 私聊", instance.MODULE_PACKAGENAME());
					this.EXECUTOR_USER.put(instance.MODULE_COMMANDNAME(), instance);
				}
			} else {
				logger.mini(MODULE_PACKAGENAME, "配置错误", "组聊执行器不存在 " + name);
			}
		}

		for (String name : this.LIST_EXECUTOR_DISZ) {
			if (this.EXECUTOR_INSTANCE.containsKey(name)) {
				ModuleExecutor instance = this.EXECUTOR_INSTANCE.get(name);
				if (instance.ENABLE_DISZ()) {
					logger.full(MODULE_PACKAGENAME, "注册执行器 组聊", instance.MODULE_PACKAGENAME());
					this.EXECUTOR_DISZ.put(instance.MODULE_COMMANDNAME(), instance);
				}
			} else {
				logger.mini(MODULE_PACKAGENAME, "配置错误", "组聊执行器不存在 " + name);
			}
		}

		for (String name : this.LIST_EXECUTOR_GROP) {
			if (this.EXECUTOR_INSTANCE.containsKey(name)) {
				ModuleExecutor instance = this.EXECUTOR_INSTANCE.get(name);
				if (instance.ENABLE_GROP()) {
					logger.full(MODULE_PACKAGENAME, "注册执行器 群聊", instance.MODULE_PACKAGENAME());
					this.EXECUTOR_GROP.put(instance.MODULE_COMMANDNAME(), instance);
				}
			} else {
				logger.mini(MODULE_PACKAGENAME, "配置错误", "组聊执行器不存在 " + name);
			}
		}

		this.ENABLE_EXECUTOR_USER = this.EXECUTOR_USER.size() > 0;
		this.ENABLE_EXECUTOR_DISZ = this.EXECUTOR_DISZ.size() > 0;
		this.ENABLE_EXECUTOR_GROP = this.EXECUTOR_GROP.size() > 0;

		// =======================================================================================================================

		logger.info(MODULE_PACKAGENAME, "模块状态");

		logger.info("计时器 - 全局", this.ENABLE_SCHEDULER ? "启用 - " + this.SCHEDULER_ENABLED.size() + "个" : "禁用");

		logger.info("触发器 - 私聊", this.ENABLE_TRIGGER_USER ? "启用 - " + this.TRIGGER_USER.size() + "个" : "禁用");
		logger.info("触发器 - 组聊", this.ENABLE_TRIGGER_DISZ ? "启用 - " + this.TRIGGER_DISZ.size() + "个" : "禁用");
		logger.info("触发器 - 群聊", this.ENABLE_TRIGGER_GROP ? "启用 - " + this.TRIGGER_GROP.size() + "个" : "禁用");

		logger.info("监听器 - 私聊", this.ENABLE_LISENTER_USER ? "启用 - " + this.LISTENER_DISZ.size() + "个" : "禁用");
		logger.info("监听器 - 组聊", this.ENABLE_LISENTER_DISZ ? "启用 - " + this.LISTENER_DISZ.size() + "个" : "禁用");
		logger.info("监听器 - 群聊", this.ENABLE_LISENTER_GROP ? "启用 - " + this.LISTENER_GROP.size() + "个" : "禁用");

		logger.info("执行器 - 私聊", this.ENABLE_EXECUTOR_USER ? "启用 - " + this.EXECUTOR_USER.size() + "个" : "禁用");
		logger.info("执行器 - 组聊", this.ENABLE_EXECUTOR_DISZ ? "启用 - " + this.EXECUTOR_DISZ.size() + "个" : "禁用");
		logger.info("执行器 - 群聊", this.ENABLE_EXECUTOR_GROP ? "启用 - " + this.EXECUTOR_GROP.size() + "个" : "禁用");

		// =======================================================================================================================
		//
		//
		// =======================================================================================================================
		// 预生成 /list 的信息

		// @formatter:off
		entry.getMessage().genetateList(
				this.TRIGGER_USER,
				this.TRIGGER_DISZ,
				this.TRIGGER_GROP,
				this.LISTENER_USER,
				this.LISTENER_DISZ,
				this.LISTENER_GROP,
				this.EXECUTOR_USER,
				this.EXECUTOR_DISZ,
				this.EXECUTOR_GROP
		);
		// @formatter:on
	}

	@Override
	public void boot(LoggerX logger) throws Exception {

//		this.NICKMAP.boot(logger);
//		this.MESSAGE.boot(logger);

		// =======================================================================================================================
		// 启动定时器

		for (ModuleScheduler instance : this.SCHEDULER_ENABLED) {
			logger.full(MODULE_PACKAGENAME, "启动定时器 ", instance.MODULE_PACKAGENAME());
			instance.boot(logger);
		}

		// =======================================================================================================================
		// 启动触发器

		for (String name : this.TRIGGER_INSTANCE.keySet()) {
			logger.full(MODULE_PACKAGENAME, "启动触发器", name);
			ModuleTrigger instance = this.TRIGGER_INSTANCE.get(name);
			if (instance.ENABLE_USER() || instance.ENABLE_DISZ() || instance.ENABLE_GROP()) { instance.boot(logger); }
		}

		// =======================================================================================================================
		// 启动 监听器

		for (String name : this.LISTENER_INSTANCE.keySet()) {
			logger.full(MODULE_PACKAGENAME, "启动监听器", name);
			ModuleListener instance = this.LISTENER_INSTANCE.get(name);
			if (instance.ENABLE_USER() || instance.ENABLE_DISZ() || instance.ENABLE_GROP()) { instance.boot(logger); }
		}

		// =======================================================================================================================
		// 启动执行器

		for (String name : this.EXECUTOR_INSTANCE.keySet()) {
			logger.full(MODULE_PACKAGENAME, "启动执行器", name);
			ModuleExecutor instance = this.EXECUTOR_INSTANCE.get(name);
			if (instance.ENABLE_USER() || instance.ENABLE_DISZ() || instance.ENABLE_GROP()) { instance.boot(logger); }
		}

	}

	@Override
	public void save(LoggerX logger) throws Exception {
		for (String name : this.SCHEDULER_INSTANCE.keySet()) {
			logger.full(MODULE_PACKAGENAME, "保存定时器", name);
			this.SCHEDULER_INSTANCE.get(name).save(logger);
		}
		for (String name : this.TRIGGER_INSTANCE.keySet()) {
			logger.full(MODULE_PACKAGENAME, "保存触发器", name);
			this.TRIGGER_INSTANCE.get(name).save(logger);
		}
		for (String name : this.LISTENER_INSTANCE.keySet()) {
			logger.full(MODULE_PACKAGENAME, "保存监听器", name);
			this.LISTENER_INSTANCE.get(name).save(logger);
		}
		for (String name : this.EXECUTOR_INSTANCE.keySet()) {
			logger.full(MODULE_PACKAGENAME, "保存执行器", name);
			this.EXECUTOR_INSTANCE.get(name).save(logger);
		}
	}

	@Override
	public void shut(LoggerX logger) throws Exception {
		for (String name : this.SCHEDULER_INSTANCE.keySet()) {
			logger.full(MODULE_PACKAGENAME, "关闭定时器", name);
			this.SCHEDULER_INSTANCE.get(name).shut(logger);
		}
		for (String name : this.TRIGGER_INSTANCE.keySet()) {
			logger.full(MODULE_PACKAGENAME, "关闭触发器", name);
			this.TRIGGER_INSTANCE.get(name).shut(logger);
		}
		for (String name : this.LISTENER_INSTANCE.keySet()) {
			logger.full(MODULE_PACKAGENAME, "关闭监听器", name);
			this.LISTENER_INSTANCE.get(name).shut(logger);
		}
		for (String name : this.EXECUTOR_INSTANCE.keySet()) {
			logger.full(MODULE_PACKAGENAME, "关闭执行器", name);
			this.EXECUTOR_INSTANCE.get(name).shut(logger);
		}
	}

	@Override
	public void reload(LoggerX logger) throws Exception {
		for (String name : this.TRIGGER_INSTANCE.keySet()) {
			this.TRIGGER_INSTANCE.get(name).reload(logger);
		}
		for (String name : this.LISTENER_INSTANCE.keySet()) {
			this.LISTENER_INSTANCE.get(name).reload(logger);
		}
		for (String name : this.EXECUTOR_INSTANCE.keySet()) {
			this.EXECUTOR_INSTANCE.get(name).reload(logger);
		}
	}

	@Override
	public void exec(LoggerX logger, Message message) throws Exception {
	}

	@Override
	public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) throws Exception {

		StringBuilder builder = new StringBuilder();

		builder.append("[加群] - " + LoggerX.datetime() + "\r\n");
		builder.append("群号：" + gropid + "\r\n");
		if (typeid == 1) {
			builder.append("自主申请\r\n");
			builder.append("管理：" + entry.getNickmap().getNickname(operid) + "(" + operid + ")" + "\r\n");
		} else {
			builder.append("邀请加群\r\n");
		}
		builder.append("成员：" + entry.getNickmap().getNickname(userid) + "(" + userid + ")" + "\r\n");

		entry.getMessage().adminInfo(builder.toString());

		for (String name : this.TRIGGER_INSTANCE.keySet()) {
			this.TRIGGER_INSTANCE.get(name).groupMemberIncrease(typeid, sendtime, gropid, operid, userid);
		}
		for (String name : this.LISTENER_INSTANCE.keySet()) {
			this.LISTENER_INSTANCE.get(name).groupMemberIncrease(typeid, sendtime, gropid, operid, userid);
		}
		for (String name : this.EXECUTOR_INSTANCE.keySet()) {
			this.EXECUTOR_INSTANCE.get(name).groupMemberIncrease(typeid, sendtime, gropid, operid, userid);
		}
	}

	@Override
	public void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) throws Exception {

		StringBuilder builder = new StringBuilder();

		builder.append("[退群] - " + LoggerX.datetime() + "\r\n");
		builder.append("群号：" + gropid + "\r\n");
		if (typeid == 1) {
			builder.append("自主退群\r\n");
		} else {
			builder.append("管理踢出\r\n");
			builder.append("管理：" + entry.getNickmap().getNickname(operid) + "(" + operid + ")" + "\r\n");
		}
		builder.append("成员：" + entry.getNickmap().getNickname(userid) + "(" + userid + ")" + "\r\n");

		entry.getMessage().adminInfo(builder.toString());

		for (String name : this.TRIGGER_INSTANCE.keySet()) {
			this.TRIGGER_INSTANCE.get(name).groupMemberDecrease(typeid, sendtime, gropid, operid, userid);
		}
		for (String name : this.LISTENER_INSTANCE.keySet()) {
			this.LISTENER_INSTANCE.get(name).groupMemberDecrease(typeid, sendtime, gropid, operid, userid);
		}
		for (String name : this.EXECUTOR_INSTANCE.keySet()) {
			this.EXECUTOR_INSTANCE.get(name).groupMemberDecrease(typeid, sendtime, gropid, operid, userid);
		}
	}
	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	private void instantiationRawModule(Module instance) {
		this.RAWMODULE_INSTANCE.put(instance.MODULE_COMMANDNAME(), instance);
	}

	private void instantiationScheduler(ModuleScheduler instance) {
		this.SCHEDULER_INSTANCE.put(instance.MODULE_COMMANDNAME(), instance);
	}

	private void instantiationTrigger(ModuleTrigger instance) {
		this.TRIGGER_INSTANCE.put(instance.MODULE_COMMANDNAME(), instance);
	}

	public void instantiationListener(ModuleListener instance) {
		this.LISTENER_INSTANCE.put(instance.MODULE_COMMANDNAME(), instance);
	}

	private void instantiationExecutor(ModuleExecutor instance) {
		this.EXECUTOR_INSTANCE.put(instance.MODULE_COMMANDNAME(), instance);
	}

	public ModuleTrigger getTrigger(String name) {
		return this.TRIGGER_INSTANCE.get(name);
	}

	public ModuleListener getListener(String name) {
		return this.LISTENER_INSTANCE.get(name);
	}

	public ModuleExecutor getExecutor(String name) {
		return this.EXECUTOR_INSTANCE.get(name);
	}

	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	public void doUserMessage(int typeid, long userid, MessageUser message, int messageid, int messagefont) throws Exception {

		this.COUNT_USER_MESSAGE++;

		// ===============================================================================================================================

		// >>>>> DEBUG

		if (entry.DEBUG()) {
			if (message.parse().isCommand()) {
				entry.getCQ().logDebug("FurryBlack", message.toString());
			} else {
				entry.getCQ().logDebug("FurryBlack", message.toString());
			}
		}

		// >>>>> DEBUG

		// ===============================================================================================================================

		if (this.ENABLE_TRIGGER_USER) {
			for (ModuleTrigger temp : this.TRIGGER_USER) {
				if (temp.executeUserMessage(typeid, userid, message, messageid, messagefont)) { return; }
			}
		}

		// ===============================================================================================================================

		if (this.ENABLE_LISENTER_USER) {
			for (ModuleListener temp : this.LISTENER_USER) {
				temp.executeUserMessage(typeid, userid, message, messageid, messagefont);
			}
		}

		// ===============================================================================================================================

		if (message.parse().isCommand()) {

			switch (message.getCommand()) {

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
						entry.getMessage().sendHelp(userid);
					}
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

	}

	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	public void doDiszMessage(long diszid, long userid, MessageDisz message, int messageid, int messagefont) throws Exception {

		this.COUNT_DISZ_MESSAGE++;

		// ===============================================================================================================================

		// >>>>> DEBUG

		if (entry.DEBUG()) {
			if (message.parse().isCommand()) {
				entry.getCQ().logDebug("FurryBlack", message.toString());
			} else {
				entry.getCQ().logDebug("FurryBlack", message.toString());
			}
		}

		// >>>>> DEBUG

		// ===============================================================================================================================

		if (this.ENABLE_TRIGGER_DISZ) {
			for (ModuleTrigger temp : this.TRIGGER_DISZ) {
				if (temp.executeDiszMessage(diszid, userid, message, messageid, messagefont)) { return; }
			}
		}

		// ===============================================================================================================================

		if (this.ENABLE_LISENTER_DISZ) {
			for (ModuleListener temp : this.LISTENER_DISZ) {
				temp.executeDiszMessage(diszid, userid, message, messageid, messagefont);
			}
		}

		// ===============================================================================================================================

		if (message.parse().isCommand()) {

			switch (message.getCommand()) {
			// ===============================================================================================================================
			case "info":
				entry.getMessage().diszInfo(diszid, userid, "已发送至私聊，如未收到请允许临时会话或添加好友");
				entry.getMessage().sendInfo(userid);
				break;
			// ===============================================================================================================================
			case "eula":
				entry.getMessage().diszInfo(diszid, userid, "已发送至私聊，如未收到请允许临时会话或添加好友");
				entry.getMessage().sendEula(userid);
				break;
			// ===============================================================================================================================
			case "list":
				entry.getMessage().diszInfo(diszid, userid, "已发送至私聊，如未收到请允许临时会话或添加好友");
				entry.getMessage().sendListDisz(userid);
				break;
			// ===============================================================================================================================
			case "help":
				if (message.getSection() == 0) {
					entry.getMessage().sendHelp(userid);
				} else {
					if (this.EXECUTOR_INSTANCE.containsKey(message.getSegment()[0])) {
						entry.getMessage().sendHelp(userid, this.EXECUTOR_INSTANCE.get(message.getSegment()[0]));
					} else {
						entry.getMessage().gropInfo(diszid, userid, "没有此插件，可用插件已发送至私聊，如未收到请允许临时会话或添加好友");
						entry.getMessage().sendListDisz(userid);
					}
				}
				break;
			// ===============================================================================================================================
			default:
				if (this.ENABLE_EXECUTOR_DISZ && this.EXECUTOR_DISZ.containsKey(message.getCommand())) {
					this.EXECUTOR_DISZ.get(message.getCommand()).executeDiszMessage(diszid, userid, message, messageid, messagefont);
				} else {
					entry.getMessage().gropInfo(diszid, userid, "没有此插件，可用插件已发送至私聊，如未收到请允许临时会话或添加好友");
					entry.getMessage().sendListDisz(userid);
				}
				break;
			}

			// ===============================================================================================================================
		}
	}

	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	public void doGropMessage(long gropid, long userid, MessageGrop message, int messageid, int messagefont) throws Exception {

		this.COUNT_GROP_MESSAGE++;

		// ===============================================================================================================================

		// >>>>> DEBUG

		if (entry.DEBUG()) {
			if (message.parse().isCommand()) {
				entry.getCQ().logDebug("FurryBlack", message.toString());
			} else {
				entry.getCQ().logDebug("FurryBlack", message.toString());
			}
		}

		// >>>>> DEBUG

		// ===============================================================================================================================

		if (this.ENABLE_TRIGGER_GROP) {
			for (ModuleTrigger temp : this.TRIGGER_GROP) {
				if (temp.executeGropMessage(gropid, userid, message, messageid, messagefont)) { return; }
			}
		}

		// ===============================================================================================================================

		if (this.ENABLE_LISENTER_GROP) {
			for (ModuleListener temp : this.LISTENER_GROP) {
				temp.executeGropMessage(gropid, userid, message, messageid, messagefont);
			}
		}

		// ===============================================================================================================================

		if (message.parse().isCommand()) {

			switch (message.getCommand()) {
			// ===============================================================================================================================
			case "info":
				entry.getMessage().gropInfo(gropid, userid, "已发送至私聊，如未收到请允许临时会话或添加好友");
				entry.getMessage().sendInfo(userid);
				break;
			// ===============================================================================================================================
			case "eula":
				entry.getMessage().gropInfo(gropid, userid, "已发送至私聊，如未收到请允许临时会话或添加好友");
				entry.getMessage().sendEula(userid);
				break;
			// ===============================================================================================================================
			case "list":
				entry.getMessage().gropInfo(gropid, userid, "已发送至私聊，如未收到请允许临时会话或添加好友");
				entry.getMessage().sendListGrop(userid);
				break;
			// ===============================================================================================================================
			case "help":
				if (message.getSection() == 0) {
					entry.getMessage().sendHelp(userid);
				} else {
					if (this.EXECUTOR_INSTANCE.containsKey(message.getSegment()[0])) {
						entry.getMessage().sendHelp(userid, this.EXECUTOR_INSTANCE.get(message.getSegment()[0]));
					} else {
						entry.getMessage().gropInfo(gropid, userid, "没有此插件，可用插件已发送至私聊，如未收到请允许临时会话或添加好友");
						entry.getMessage().sendListGrop(userid);
					}
				}
				break;
			// ===============================================================================================================================
			default:
				if (this.ENABLE_EXECUTOR_GROP && this.EXECUTOR_GROP.containsKey(message.getCommand())) {
					this.EXECUTOR_GROP.get(message.getCommand()).executeGropMessage(gropid, userid, message, messageid, messagefont);
				} else {
					entry.getMessage().gropInfo(gropid, userid, "没有此插件，可用插件已发送至私聊，如未收到请允许临时会话或添加好友");
					entry.getMessage().sendListGrop(userid);
				}
				break;
			}

			// ===============================================================================================================================
		}
	}

	// ==========================================================================================================================

	@Override
	public String[] generateReport(int mode, Message message, Object... parameters) {
		return null;
	}

	// ========================================================================================================================
	// ========================================================================================================================

	public SystemdDelegate getDelegate() {
		return this.delegate;
	}

	// ========================================================================================================================
	// ========================================================================================================================

	private String doGenerateMiniSystemsReport() {

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

		long totalMemory = Runtime.getRuntime().totalMemory() / 1024;
		long freeMemory = Runtime.getRuntime().freeMemory() / 1024;

		// ========================================================================================================================

		builder.append(LoggerX.datetime() + " - 状态简报" + "\r\n");
		builder.append("运行时间：" + uptimedd + " - " + uptimehh + ":" + uptimemm + ":" + uptimess + "\r\n");

		// ===============================================================================

		builder.append("系统内存：" + (totalMemory - freeMemory) + "KB/" + totalMemory + "KB" + "\r\n");

		// ===============================================================================

		builder.append("私聊：" + this.COUNT_USER_MESSAGE + "次" + "\r\n");
		builder.append("组聊：" + this.COUNT_DISZ_MESSAGE + "次" + "\r\n");
		builder.append("群聊：" + this.COUNT_GROP_MESSAGE + "次" + "\r\n");

		// ===============================================================================

		builder.append("定时器：" + this.SCHEDULER_ENABLED.size() + "个" + "\r\n");

		for (ModuleScheduler instance : this.SCHEDULER_ENABLED) {
			if (instance.ENABLE()) { builder.append(instance.MODULE_PACKAGENAME() + "：" + instance.COUNT() + "\r\n"); }
		}

		// ===============================================================================

		builder.append("触发器：" + this.TRIGGER_USER.size() + "/" + this.TRIGGER_DISZ.size() + "/" + this.TRIGGER_GROP.size() + "个" + "\r\n");

		for (String temp : this.TRIGGER_INSTANCE.keySet()) {
			ModuleTrigger instance = this.TRIGGER_INSTANCE.get(temp);
			if (instance.ENABLE_USER() || instance.ENABLE_DISZ() || instance.ENABLE_GROP()) { builder.append(instance.MODULE_PACKAGENAME() + "：" + instance.BLOCK_USER() + "/" + instance.BLOCK_DISZ() + "/" + instance.BLOCK_GROP() + "\r\n"); }
		}

		// ===============================================================================

		builder.append("监听器：" + this.LISTENER_USER.size() + "/" + this.LISTENER_DISZ.size() + "/" + this.LISTENER_GROP.size() + "个" + "\r\n");

		for (String temp : this.LISTENER_INSTANCE.keySet()) {
			ModuleListener instance = this.LISTENER_INSTANCE.get(temp);
			if (instance.ENABLE_USER() || instance.ENABLE_DISZ() || instance.ENABLE_GROP()) { builder.append(instance.MODULE_PACKAGENAME() + "：" + instance.COUNT_USER() + "/" + instance.COUNT_DISZ() + "/" + instance.COUNT_GROP() + "\r\n"); }
		}

		// ===============================================================================

		builder.append("执行器：" + this.EXECUTOR_USER.size() + "/" + this.EXECUTOR_DISZ.size() + "/" + this.EXECUTOR_GROP.size() + "个" + "\r\n");

		for (String temp : this.EXECUTOR_INSTANCE.keySet()) {
			ModuleExecutor instance = this.EXECUTOR_INSTANCE.get(temp);
			if (instance.ENABLE_USER() || instance.ENABLE_DISZ() || instance.ENABLE_GROP()) { builder.append(instance.MODULE_PACKAGENAME() + "：" + instance.COUNT_USER() + "/" + instance.COUNT_DISZ() + "/" + instance.COUNT_GROP() + "\r\n"); }
		}

		return builder.substring(0, builder.length() - 2).toString();
	}

	// ===============================================================================

	private String doGenerateModuleFullHelp(String name) {
		if (this.EXECUTOR_INSTANCE.containsKey(name)) {
			return this.EXECUTOR_INSTANCE.get(name).MODULE_FULLHELP;
		} else {
			return null;
		}
	}

	// ===============================================================================

	private String[] doGenerateModuleReport(String name, int mode, Message message) {
		if (this.TRIGGER_INSTANCE.containsKey(name)) {
			return this.TRIGGER_INSTANCE.get(name).generateReport(mode, message, null, null);
		} else if (this.LISTENER_INSTANCE.containsKey(name)) {
			return this.LISTENER_INSTANCE.get(name).generateReport(mode, message, null, null);
		} else if (this.EXECUTOR_INSTANCE.containsKey(name)) {
			return this.EXECUTOR_INSTANCE.get(name).generateReport(mode, message, null, null);
		} else {
			return new String[] {
					"模块不存在"
			};
		}
	}

	// ========================================================================================================================
	// ========================================================================================================================
	// ========================================================================================================================

	/***
	 * 生成并发送系统报告
	 *
	 * @param logdest 发送的目的 0：管理员 1：私聊 2： 3：群聊
	 * @param destid  目的地
	 */
	public void doSendSystemsReport(int logdest, long destid) {
		switch (logdest) {
		case 0:
			entry.getMessage().adminInfo(this.doGenerateMiniSystemsReport());
			break;
		case 1:
			entry.getMessage().userInfo(destid, this.doGenerateMiniSystemsReport());
			break;
		case 2:
			entry.getMessage().diszInfo(destid, this.doGenerateMiniSystemsReport());
			break;
		case 3:
			entry.getMessage().gropInfo(destid, this.doGenerateMiniSystemsReport());
		}
	}

	public void doSendModuleFullHelp(int logdest, long destid, String name) {
		switch (logdest) {
		case 0:
			entry.getMessage().adminInfo(this.doGenerateModuleFullHelp(name));
			break;
		case 1:
			entry.getMessage().userInfo(destid, this.doGenerateModuleFullHelp(name));
			break;
		case 2:
			entry.getMessage().diszInfo(destid, this.doGenerateModuleFullHelp(name));
			break;
		case 3:
			entry.getMessage().gropInfo(destid, this.doGenerateModuleFullHelp(name));
		}
	}

	public void doSendModuleReport(Message message) {
		String[] target = message.getSwitch("target").split(":");
		String[] module = message.getSwitch("module").split(":");

		String[] report = new String[2];
		if (target == null) {
			report[0] = "参数错误 --target 为空";
		} else if (module == null) {
			report[1] = "参数错误 --module 为空";
		} else {
			report = this.doGenerateModuleReport(module[0], Integer.parseInt(module[1]), message);
		}

		switch (target[0]) {
		case "1":
			entry.getMessage().userInfo(Long.parseLong(target[1]), report);
			break;
		case "2":
			entry.getMessage().diszInfo(Long.parseLong(target[1]), report);
			break;
		case "3":
			entry.getMessage().gropInfo(Long.parseLong(target[1]), report);
			break;
		}
	}

	public LoggerX doInit(String level) throws Exception {
		LoggerX logger = new LoggerX();
		logger.info(LoggerX.datetime());
		switch (level) {
		case "0":
			logger.info("init 0：切换启停");
			if (entry.isEnable()) {
				entry.setEnable(false);
				logger.info("切换至停机");
			} else {
				entry.setEnable(true);
				logger.info("切换至运行");
			}
			break;
		case "1":
			logger.info("init 1：初始化");
			Module_Systemd.this.init(logger);
			break;
		case "2":
			logger.info("init 2：启动");
			Module_Systemd.this.boot(logger);
			break;
		case "3":
			logger.info("init 3：保存");
			Module_Systemd.this.save(logger);
			break;
		case "4":
			logger.info("init 4：关闭丢弃");
			Module_Systemd.this.shut(logger);
			break;
		case "5":
			logger.info("init 5：保存关闭");
			Module_Systemd.this.save(logger);
			Module_Systemd.this.shut(logger);
			break;
		case "6":
			logger.info("init 6：保存重启");
			Module_Systemd.this.save(logger);
			Module_Systemd.this.shut(logger);
			Module_Systemd.this.boot(logger);
			break;
		}
		return logger;
	}

	public LoggerX doExec(Message message) throws Exception {

		LoggerX logger = new LoggerX();
		logger.info(LoggerX.datetime());

		String module = message.getSwitch("module");

		if (module == null) {
			logger.info("参数错误 --module 为空");
			return logger;
		}

		// /admin exec --mode=eval System.out.println("EVAL");

		if (module.equals("eval")) {
			logger.info("eval", message.getOptions());
		} else if (this.RAWMODULE_INSTANCE.containsKey(module)) {
			logger.info("核心模块", module);
			this.RAWMODULE_INSTANCE.get(module).exec(logger, message);
		} else if (this.SCHEDULER_INSTANCE.containsKey(module)) {
			logger.info("定时器", module);
			this.SCHEDULER_INSTANCE.get(module).exec(logger, message);
		} else if (this.TRIGGER_INSTANCE.containsKey(module)) {
			logger.info("触发器", module);
			this.TRIGGER_INSTANCE.get(module).exec(logger, message);
		} else if (this.LISTENER_INSTANCE.containsKey(module)) {
			logger.info("监听器", module);
			this.LISTENER_INSTANCE.get(module).exec(logger, message);
		} else if (this.EXECUTOR_INSTANCE.containsKey(module)) {
			logger.info("执行器", module);
			this.EXECUTOR_INSTANCE.get(module).exec(logger, message);
		} else {
			logger.mini("模块不存在");
		}

		return logger;
	}

// ========================================================================================================================
// ========================================================================================================================
// ========================================================================================================================

	public MessageDelegate getMESSAGE() {
		return this.MESSAGE.getDelegate();
	}

	public NicknameDelegate getNICKMAP() {
		return this.NICKMAP.getDelegate();
	}

	// ==========================================================================================================================================================
	//
	// 代理对象
	//
	// ==========================================================================================================================================================

	public class SystemdDelegate {

		private TestDelegate testDelegate = new TestDelegate();

		public void sendModuleFullHelp(int logdest, long destid, String name) {
			Module_Systemd.this.doSendModuleFullHelp(logdest, destid, name);
		}

		public void sendSystemsReport(int logdest, long destid) {
			Module_Systemd.this.doSendSystemsReport(logdest, destid);
		}

		public void sendModuleReport(Message message) {
			Module_Systemd.this.doSendModuleReport(message);
		}

		public LoggerX exec(Message message) throws Exception {
			return Module_Systemd.this.doExec(message);
		}

		public LoggerX init(String string) throws Exception {
			return Module_Systemd.this.doInit(string);
		}

		// ========================================================================================================================

		public TestDelegate test() {
			return this.testDelegate;
		}

		public class TestDelegate {
		}

	}

}
