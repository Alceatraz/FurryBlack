package studio.blacktech.coolqbot.furryblack;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.TreeMap;

import com.sobte.cqp.jcq.entity.IMsg;
import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.common.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.Message;
import studio.blacktech.coolqbot.furryblack.common.Module;
import studio.blacktech.coolqbot.furryblack.common.ModuleExecutor;
import studio.blacktech.coolqbot.furryblack.common.ModuleListener;
import studio.blacktech.coolqbot.furryblack.common.ModuleScheduler;
import studio.blacktech.coolqbot.furryblack.common.ModuleTrigger;
import studio.blacktech.coolqbot.furryblack.common.NickNameMap;
import studio.blacktech.coolqbot.furryblack.common.ReInitializationException;
import studio.blacktech.coolqbot.furryblack.modules.Executor_acon;
import studio.blacktech.coolqbot.furryblack.modules.Executor_admin;
import studio.blacktech.coolqbot.furryblack.modules.Executor_chou;
import studio.blacktech.coolqbot.furryblack.modules.Executor_dice;
import studio.blacktech.coolqbot.furryblack.modules.Executor_echo;
import studio.blacktech.coolqbot.furryblack.modules.Executor_gamb;
import studio.blacktech.coolqbot.furryblack.modules.Executor_jrjp;
import studio.blacktech.coolqbot.furryblack.modules.Executor_jrrp;
import studio.blacktech.coolqbot.furryblack.modules.Executor_kong;
import studio.blacktech.coolqbot.furryblack.modules.Executor_roll;
import studio.blacktech.coolqbot.furryblack.modules.Executor_zhan;
import studio.blacktech.coolqbot.furryblack.modules.Listener_TopSpeak;
import studio.blacktech.coolqbot.furryblack.modules.Scheduler_DDNS;
import studio.blacktech.coolqbot.furryblack.modules.Scheduler_TASK;
import studio.blacktech.coolqbot.furryblack.modules.Trigger_UserDeny;
import studio.blacktech.coolqbot.furryblack.modules.Trigger_WordDeny;

public class SystemHandler extends Module {

	private static final long BOOTTIME = System.currentTimeMillis();

	private static boolean INITIALIZATIONLOCK = false;

	private static int COUNT_USER_MESSAGE = 0;
	private static int COUNT_DISZ_MESSAGE = 0;
	private static int COUNT_GROP_MESSAGE = 0;

	private static String MESSAGE_LIST_USER = "";
	private static String MESSAGE_LIST_DISZ = "";
	private static String MESSAGE_LIST_GROP = "";

	// @formatter:off

	private static String MESSAGE_HELP =
			"FurryBlack - 一个小动物形象的人工智障\r\n" +
			"\r\n" +
			"使用即表明同意最终用户许可协议\r\n" +
			"\r\n" + "无需添加好友也可使用\r\n" +
			"添加好友将自动同意\r\n" +
			"邀请至群将自动同意\r\n" +
			"\r\n" +
			"命令均以双斜线//开头\r\n" +
			"//eula - 查看用户使用协议\r\n" +
			"//info - 版本版权信息\r\n" +
			"//list - 列出功能模块\r\n" +
			"//help <命令> - 显示指定模块帮助";
	private static String MESSAGE_INFO =
			"FurryBlack - 一个小动物形象的人工智障\r\n" +
			"\r\n" +
			"使用即表明同意最终用户许可协议(//eula查看)\r\n" +
			"\r\n" +
			"版本信息: REPLACE_VERSION\r\n" +
			"\r\n" +
			"隐私声明(框架级隐私):\r\n" +
			"框架执行流程的三种可能：\r\n" +
			"用户消息 → 触发器（拦截）→ 结束" +
			"用户消息 → 触发器（不拦截）→ 以//开头（是）→ 执行器 → 结束" +
			"用户消息 → 触发器（不拦截）→ 以//开头（否）→ 监听器 → 结束" +
			"\r\n" +
			"触发器 可决定是否将消息拦截，用于过滤消息" +
			"监听器 可监听所有消息，用于扩展执行器的功能" +
			"执行器 命令模块，用于实现各种互动功能" +
			"\r\n" +
			"隐私声明(模块级隐私):\r\n" +
			"每个模块都有属于自己的隐私级别，可由//help <name>查看\r\n"	+
			"触发 - 指可直接获取用户输入信息并且可以在解析命令前拦截但不能修改任何内容的插件\r\n" +
			"监听 - 指可直接获取用户输入信息但不能拦截消息也不可修改任何内容的的插件\r\n" +
			"普通 - 如未注明触发或者监听则为仅在//开头的信息被唤起且只唤起对应PACKNAME的插件\r\n" +
			"\r\n" +
			"存储 - 指将含有用户信息的数据存储到持久化/数据库/文件中\r\n" +
			"缓存 - 指将含有用户信息的数据存储到内存数据结构/容器中，在特定情况下释放\r\n" +
			"获取 - 指将含有用户信息的数据提取并用于内存仅用于单次处理，处理完成后立即释放\r\n" +
			"\r\n" +
			"版权信息(逻辑部分): 版权属于BlackTechStudio\r\n" +
			"由 Team BTSNUVO 开发\r\n" +
			"由 Team BTSNODE 运营\r\n" +
			"项目地址 https://git.blacktech.studio/blacktechstudio/furryblack\r\n" +
			"\r\n" +
			"版权信息(虚拟形象): 版权属于FPDG,授权使用\r\n" +
			"https://twitter.com/flappydoggy/status/877582553762283520\r\n" +
			"https://twitter.com/flappydoggy/status/875026125038080000" ;
	private static String MESSAGE_EULA =
			"FurryBlack - 一个小动物形象的人工智障\r\n" +
			"\r\n" +
			"最终用户许可协议（以下简称EULA）：\r\n" +
			"\r\n" +
			"甲方：Blacktechstudio（以下简称BTS）\r\n" +
			"乙方：阁下\r\n" +
			"\r\n" +
			"1：除//help //eula //info之外，使用本人格任何功能即表示乙方同意本EULA；\r\n" +
			"2：甲方不对乙方的任何行为明示或者默示的任何赞许或反对；\r\n" +
			"3：乙方如违反本EULA，甲方有权利取消乙方的使用权；\r\n" +
			"4：乙方不得以任何形式散播任与法律或道德冲违背的不良信息；\r\n" +
			"5：乙方不得在未授权的情况下将本人格以任何形式用于任何形式的商业用途；\r\n"	+
			"6：甲方以及所有涉及的开发维护保障人员不承担由于乙方使用导致的任何损失；\r\n" +
			"7：甲方以及所有涉及的开发维护保障人员不承担由于程序或机组故障引起的任何损失；\r\n" +
			"最终解释权归BTS所有" +
			"\r\n" +
			"\r\n" +
			"BTS，2019-02-22 敬上";

	// @formatter:on

	private static Executor_acon executor_acon;
	private static Executor_chou executor_chou;
	private static Executor_dice executor_dice;
	private static Executor_echo executor_echo;
	private static Executor_gamb executor_gamb;
	private static Executor_jrjp executor_jrjp;
	private static Executor_jrrp executor_jrrp;
	private static Executor_kong executor_kong;
	private static Executor_roll executor_roll;
	private static Executor_zhan executor_zhan;
	private static Executor_admin executor_admin;
	private static Listener_TopSpeak listener_topspeak;
	private static Trigger_UserDeny trigger_userdeny;
	private static Trigger_WordDeny trigger_worddeny;
	private static Scheduler_TASK scheduler_task;
	private static Scheduler_DDNS scheduler_ddns;

	private static boolean ENABLE_LISENTER_USER = false;
	private static boolean ENABLE_LISENTER_DISZ = false;
	private static boolean ENABLE_LISENTER_GROP = false;

	private static boolean ENABLE_TRIGGER_USER = false;
	private static boolean ENABLE_TRIGGER_DISZ = false;
	private static boolean ENABLE_TRIGGER_GROP = false;

	private static boolean ENABLE_EXECUTOR_USER = false;
	private static boolean ENABLE_EXECUTOR_DISZ = false;
	private static boolean ENABLE_EXECUTOR_GROP = false;

	private static TreeMap<String, ModuleListener> LISTENER_INSTANCE = new TreeMap<>();
	private static TreeMap<String, ModuleTrigger> TRIGGER_INSTANCE = new TreeMap<>();
	private static TreeMap<String, ModuleExecutor> EXECUTOR_INSTANCE = new TreeMap<>();

	private static ArrayList<ModuleTrigger> TRIGGER_USER = new ArrayList<>(100);
	private static ArrayList<ModuleTrigger> TRIGGER_DISZ = new ArrayList<>(100);
	private static ArrayList<ModuleTrigger> TRIGGER_GROP = new ArrayList<>(100);

	private static ArrayList<ModuleListener> LISTENER_USER = new ArrayList<>(100);
	private static ArrayList<ModuleListener> LISTENER_DISZ = new ArrayList<>(100);
	private static ArrayList<ModuleListener> LISTENER_GROP = new ArrayList<>(100);

	private static TreeMap<String, ModuleExecutor> EXECUTOR_USER = new TreeMap<>();
	private static TreeMap<String, ModuleExecutor> EXECUTOR_DISZ = new TreeMap<>();
	private static TreeMap<String, ModuleExecutor> EXECUTOR_GROP = new TreeMap<>();

	private static HashMap<String, ModuleScheduler> SCHEDULER = new HashMap<>();
	private static HashMap<String, Thread> SCHEDULER_INSTANCE = new HashMap<>();

//	private static HashMap<Long, HashSet<Long>> GANNOUNCE = new HashMap<>();

	protected static boolean init(StringBuilder initBuilder, Properties config) throws ReInitializationException, NumberFormatException, IOException {

		if (SystemHandler.INITIALIZATIONLOCK) { throw new ReInitializationException(); }

		SystemHandler.INITIALIZATIONLOCK = true;

		// ==========================================================================================================================

		SystemHandler.MESSAGE_INFO = SystemHandler.MESSAGE_INFO.replaceAll("REPLACE_VERSION", entry.PRODUCT_VERSION);

		// ==========================================================================================================================

		if (entry.INIT_VERBOSE) { initBuilder.append("\r\n[System] 读取配置"); }

		SystemHandler.ENABLE_LISENTER_USER = Boolean.parseBoolean(config.getProperty("enable_listener_user", "false"));
		SystemHandler.ENABLE_LISENTER_DISZ = Boolean.parseBoolean(config.getProperty("enable_listener_disz", "false"));
		SystemHandler.ENABLE_LISENTER_GROP = Boolean.parseBoolean(config.getProperty("enable_listener_grop", "false"));

		SystemHandler.ENABLE_TRIGGER_USER = Boolean.parseBoolean(config.getProperty("enable_trigger_user", "false"));
		SystemHandler.ENABLE_TRIGGER_DISZ = Boolean.parseBoolean(config.getProperty("enable_trigger_disz", "false"));
		SystemHandler.ENABLE_TRIGGER_GROP = Boolean.parseBoolean(config.getProperty("enable_trigger_grop", "false"));

		SystemHandler.ENABLE_EXECUTOR_USER = Boolean.parseBoolean(config.getProperty("enable_executor_user", "false"));
		SystemHandler.ENABLE_EXECUTOR_DISZ = Boolean.parseBoolean(config.getProperty("enable_executor_disz", "false"));
		SystemHandler.ENABLE_EXECUTOR_GROP = Boolean.parseBoolean(config.getProperty("enable_executor_grop", "false"));

		// ==========================================================================================================================

		if (entry.INIT_VERBOSE) { initBuilder.append("\r\n[System] 读取组公告配置"); }

		BufferedReader reader = new BufferedReader(new FileReader(entry.FILE_GANNOUNCE()));

		String line;

		while ((line = reader.readLine()) != null) {
			if (line.charAt(0) == '#') {
				continue;
			} else {

			}
		}
		reader.close();

		// ==========================================================================================================================

		if (entry.INIT_VERBOSE) { initBuilder.append("\r\n[Module] 实例化监听器"); }
		SystemHandler.listener_topspeak = new Listener_TopSpeak();

		// =====================================================================

		if (entry.INIT_VERBOSE) {
			initBuilder.append("\r\n[Module] 注册私聊监听器");
//		SystemHandler.registerUserListener(SystemHandler.listener_topspeak);
		}

		// =====================================================================

		if (entry.INIT_VERBOSE) {
			initBuilder.append("\r\n[Module] 注册组聊监听器");
//		SystemHandler.registerDiszListener(SystemHandler.listener_topspeak);
		}

		// =====================================================================

		if (entry.INIT_VERBOSE) { initBuilder.append("\r\n[Module] 注册群聊监听器"); }
		SystemHandler.registerGropListener(SystemHandler.listener_topspeak);

		// =====================================================================

		if (entry.INIT_VERBOSE) { initBuilder.append("\r\n[Module] 统计监听器"); }
		SystemHandler.ENABLE_LISENTER_USER = SystemHandler.ENABLE_LISENTER_USER && (SystemHandler.LISTENER_USER.size() > 0);
		SystemHandler.ENABLE_LISENTER_DISZ = SystemHandler.ENABLE_LISENTER_DISZ && (SystemHandler.LISTENER_DISZ.size() > 0);
		SystemHandler.ENABLE_LISENTER_GROP = SystemHandler.ENABLE_LISENTER_GROP && (SystemHandler.LISTENER_GROP.size() > 0);

		// =====================================================================

		initBuilder.append("\r\n[Module] 监听器：");
		initBuilder.append("\r\n  私聊：");
		initBuilder.append(SystemHandler.ENABLE_LISENTER_USER ? "启用 - " + SystemHandler.LISTENER_USER.size() + "个" : "禁用");
		initBuilder.append("\r\n  组聊：");
		initBuilder.append(SystemHandler.ENABLE_LISENTER_DISZ ? "启用 - " + SystemHandler.LISTENER_DISZ.size() + "个" : "禁用");
		initBuilder.append("\r\n  群聊：");
		initBuilder.append(SystemHandler.ENABLE_LISENTER_GROP ? "启用 - " + SystemHandler.LISTENER_GROP.size() + "个" : "禁用");

		// ==========================================================================================================================

		if (entry.INIT_VERBOSE) { initBuilder.append("\r\n[Module] 实例化触发器"); }
		SystemHandler.trigger_userdeny = new Trigger_UserDeny();
		SystemHandler.trigger_worddeny = new Trigger_WordDeny();

		// =====================================================================

		if (entry.INIT_VERBOSE) { initBuilder.append("\r\n[Module] 注册私聊触发器"); }
		SystemHandler.registerUserTrigger(SystemHandler.trigger_userdeny);
		SystemHandler.registerUserTrigger(SystemHandler.trigger_worddeny);

		// =====================================================================

		if (entry.INIT_VERBOSE) { initBuilder.append("\r\n[Module] 注册组聊触发器"); }
		SystemHandler.registerDiszTrigger(SystemHandler.trigger_userdeny);
		SystemHandler.registerDiszTrigger(SystemHandler.trigger_worddeny);

		// =====================================================================

		if (entry.INIT_VERBOSE) { initBuilder.append("\r\n[Module] 注册群聊触发器"); }
		SystemHandler.registerGropTrigger(SystemHandler.trigger_userdeny);
		SystemHandler.registerGropTrigger(SystemHandler.trigger_worddeny);

		// =====================================================================

		if (entry.INIT_VERBOSE) { initBuilder.append("\r\n[Module] 统计触发器"); }
		SystemHandler.ENABLE_TRIGGER_USER = SystemHandler.TRIGGER_USER.size() > 0;
		SystemHandler.ENABLE_TRIGGER_DISZ = SystemHandler.TRIGGER_DISZ.size() > 0;
		SystemHandler.ENABLE_TRIGGER_GROP = SystemHandler.TRIGGER_GROP.size() > 0;

		// =====================================================================

		initBuilder.append("\r\n[Module] 触发器：");
		initBuilder.append("\r\n  私聊：");
		initBuilder.append(SystemHandler.ENABLE_TRIGGER_USER ? "启用 - " + SystemHandler.TRIGGER_USER.size() + "个" : "禁用");
		initBuilder.append("\r\n  组聊：");
		initBuilder.append(SystemHandler.ENABLE_TRIGGER_DISZ ? "启用 - " + SystemHandler.TRIGGER_DISZ.size() + "个" : "禁用");
		initBuilder.append("\r\n  群聊：");
		initBuilder.append(SystemHandler.ENABLE_TRIGGER_GROP ? "启用 - " + SystemHandler.TRIGGER_GROP.size() + "个" : "禁用");

		// ==========================================================================================================================

		if (entry.INIT_VERBOSE) { initBuilder.append("\r\n[Module] 实例化执行器"); }
		SystemHandler.executor_acon = new Executor_acon();
		SystemHandler.executor_chou = new Executor_chou();
		SystemHandler.executor_dice = new Executor_dice();
		SystemHandler.executor_echo = new Executor_echo();
		SystemHandler.executor_gamb = new Executor_gamb();
		SystemHandler.executor_jrjp = new Executor_jrjp();
		SystemHandler.executor_jrrp = new Executor_jrrp();
		SystemHandler.executor_kong = new Executor_kong();
		SystemHandler.executor_roll = new Executor_roll();
		SystemHandler.executor_zhan = new Executor_zhan();
		SystemHandler.executor_admin = new Executor_admin();

		// =======================================================================

		if (entry.INIT_VERBOSE) { initBuilder.append("\r\n[Module] 注册私聊执行器"); }
		SystemHandler.registerUserExecutor(SystemHandler.executor_dice);
		SystemHandler.registerUserExecutor(SystemHandler.executor_echo);
		SystemHandler.registerUserExecutor(SystemHandler.executor_jrrp);
		SystemHandler.registerUserExecutor(SystemHandler.executor_kong);
		SystemHandler.registerUserExecutor(SystemHandler.executor_roll);
		SystemHandler.registerUserExecutor(SystemHandler.executor_zhan);
		SystemHandler.registerUserExecutor(SystemHandler.executor_admin);

		// =======================================================================

		if (entry.INIT_VERBOSE) { initBuilder.append("\r\n[Module] 注册组聊执行器"); }
		SystemHandler.registerDiszExecutor(SystemHandler.executor_dice);
		SystemHandler.registerDiszExecutor(SystemHandler.executor_echo);
		SystemHandler.registerDiszExecutor(SystemHandler.executor_jrrp);
		SystemHandler.registerDiszExecutor(SystemHandler.executor_jrjp);
		SystemHandler.registerDiszExecutor(SystemHandler.executor_roll);
		SystemHandler.registerDiszExecutor(SystemHandler.executor_zhan);

		// ======================================================================

		if (entry.INIT_VERBOSE) { initBuilder.append("\r\n[Module] 注册群聊执行器"); }
		SystemHandler.registerGropExecutor(SystemHandler.executor_acon);
		SystemHandler.registerGropExecutor(SystemHandler.executor_chou);
		SystemHandler.registerGropExecutor(SystemHandler.executor_dice);
		SystemHandler.registerGropExecutor(SystemHandler.executor_echo);
		SystemHandler.registerGropExecutor(SystemHandler.executor_gamb);
		SystemHandler.registerGropExecutor(SystemHandler.executor_jrjp);
		SystemHandler.registerGropExecutor(SystemHandler.executor_jrrp);
		SystemHandler.registerGropExecutor(SystemHandler.executor_kong);
		SystemHandler.registerGropExecutor(SystemHandler.executor_roll);
		SystemHandler.registerGropExecutor(SystemHandler.executor_zhan);
		SystemHandler.registerGropExecutor(SystemHandler.executor_admin);

		// =====================================================================

		if (entry.INIT_VERBOSE) { initBuilder.append("\r\n[Module] 统计执行器"); }
		SystemHandler.ENABLE_EXECUTOR_USER = SystemHandler.EXECUTOR_USER.size() > 0;
		SystemHandler.ENABLE_EXECUTOR_DISZ = SystemHandler.EXECUTOR_DISZ.size() > 0;
		SystemHandler.ENABLE_EXECUTOR_GROP = SystemHandler.EXECUTOR_GROP.size() > 0;

		// =====================================================================

		initBuilder.append("\r\n[Module] 执行器：");
		initBuilder.append("\r\n  私聊：");
		initBuilder.append(SystemHandler.ENABLE_EXECUTOR_USER ? "启用 - " + SystemHandler.EXECUTOR_USER.size() + "个" : "禁用");
		initBuilder.append("\r\n  组聊：");
		initBuilder.append(SystemHandler.ENABLE_EXECUTOR_DISZ ? "启用 - " + SystemHandler.EXECUTOR_DISZ.size() + "个" : "禁用");
		initBuilder.append("\r\n  群聊：");
		initBuilder.append(SystemHandler.ENABLE_EXECUTOR_GROP ? "启用 - " + SystemHandler.EXECUTOR_GROP.size() + "个" : "禁用");

		// ==========================================================================================================================

		StringBuilder preBuilder = new StringBuilder();

		preBuilder.append("已经安装的执行器: ");
		preBuilder.append(SystemHandler.EXECUTOR_USER.size());
		for (final String temp : SystemHandler.EXECUTOR_USER.keySet()) {
			final ModuleExecutor module = SystemHandler.EXECUTOR_USER.get(temp);
			module.genFullHelp();
			preBuilder.append("\r\n");
			preBuilder.append(module.MODULE_PACKAGENAME);
			preBuilder.append(" > ");
			preBuilder.append(module.MODULE_DISPLAYNAME);
			preBuilder.append(" : ");
			preBuilder.append(module.MODULE_DESCRIPTION);
		}
		preBuilder.append("\r\n已经安装的触发器: ");
		preBuilder.append(SystemHandler.TRIGGER_USER.size());
		for (final ModuleTrigger temp : SystemHandler.TRIGGER_USER) {
			preBuilder.append("\r\n");
			preBuilder.append(temp.MODULE_PACKAGENAME);
			preBuilder.append(" > ");
			preBuilder.append(temp.MODULE_DISPLAYNAME);
			preBuilder.append(" : ");
			preBuilder.append(temp.MODULE_DESCRIPTION);
		}
		preBuilder.append("\r\n已经安装的监听器: ");
		preBuilder.append(SystemHandler.LISTENER_USER.size());
		for (final ModuleListener temp : SystemHandler.LISTENER_USER) {
			preBuilder.append("\r\n");
			preBuilder.append(temp.MODULE_PACKAGENAME);
			preBuilder.append(" > ");
			preBuilder.append(temp.MODULE_DISPLAYNAME);
			preBuilder.append(" : ");
			preBuilder.append(temp.MODULE_DESCRIPTION);
		}
		SystemHandler.MESSAGE_LIST_USER = preBuilder.toString();
		if (entry.INIT_VERBOSE) {
			initBuilder.append("\r\n[Message] list 私聊\r\n");
			initBuilder.append(SystemHandler.MESSAGE_LIST_USER);
		}

		// ==========================================================================================================================

		preBuilder = new StringBuilder();
		preBuilder.append("已经安装的执行器：");
		preBuilder.append(SystemHandler.EXECUTOR_DISZ.size());
		for (final String temp : SystemHandler.EXECUTOR_DISZ.keySet()) {
			final ModuleExecutor module = SystemHandler.EXECUTOR_DISZ.get(temp);
			module.genFullHelp();
			preBuilder.append("\r\n");
			preBuilder.append(module.MODULE_PACKAGENAME);
			preBuilder.append(" > ");
			preBuilder.append(module.MODULE_DISPLAYNAME);
			preBuilder.append(" : ");
			preBuilder.append(module.MODULE_DESCRIPTION);
		}
		preBuilder.append("\r\n已经安装的触发器：");
		preBuilder.append(SystemHandler.TRIGGER_DISZ.size());
		for (final ModuleTrigger temp : SystemHandler.TRIGGER_DISZ) {
			preBuilder.append("\r\n");
			preBuilder.append(temp.MODULE_PACKAGENAME);
			preBuilder.append(" > ");
			preBuilder.append(temp.MODULE_DISPLAYNAME);
			preBuilder.append(" : ");
			preBuilder.append(temp.MODULE_DESCRIPTION);
		}
		preBuilder.append("\r\n已经安装的监听器: ");
		preBuilder.append(SystemHandler.LISTENER_DISZ.size());
		for (final ModuleListener temp : SystemHandler.LISTENER_DISZ) {
			preBuilder.append("\r\n");
			preBuilder.append(temp.MODULE_PACKAGENAME);
			preBuilder.append(" > ");
			preBuilder.append(temp.MODULE_DISPLAYNAME);
			preBuilder.append(" : ");
			preBuilder.append(temp.MODULE_DESCRIPTION);
		}
		SystemHandler.MESSAGE_LIST_DISZ = preBuilder.toString();
		if (entry.INIT_VERBOSE) {
			initBuilder.append("\r\n[Message] list 组聊\r\n");
			initBuilder.append(SystemHandler.MESSAGE_LIST_DISZ);
		}

		// ==========================================================================================================================

		preBuilder = new StringBuilder();
		preBuilder.append("已经安装的执行器：");
		preBuilder.append(SystemHandler.EXECUTOR_GROP.size());
		for (final String temp : SystemHandler.EXECUTOR_GROP.keySet()) {
			final ModuleExecutor module = SystemHandler.EXECUTOR_GROP.get(temp);
			module.genFullHelp();
			preBuilder.append("\r\n");
			preBuilder.append(module.MODULE_PACKAGENAME);
			preBuilder.append(" > ");
			preBuilder.append(module.MODULE_DISPLAYNAME);
			preBuilder.append(" : ");
			preBuilder.append(module.MODULE_DESCRIPTION);
		}
		preBuilder.append("\r\n已经安装的触发器：");
		preBuilder.append(SystemHandler.TRIGGER_GROP.size());
		for (final ModuleTrigger temp : SystemHandler.TRIGGER_GROP) {
			preBuilder.append("\r\n");
			preBuilder.append(temp.MODULE_PACKAGENAME);
			preBuilder.append(" > ");
			preBuilder.append(temp.MODULE_DISPLAYNAME);
			preBuilder.append(" : ");
			preBuilder.append(temp.MODULE_DESCRIPTION);
		}
		preBuilder.append("\r\n已经安装的监听器：");
		preBuilder.append(SystemHandler.LISTENER_GROP.size());
		for (final ModuleListener temp : SystemHandler.LISTENER_GROP) {
			preBuilder.append("\r\n");
			preBuilder.append(temp.MODULE_PACKAGENAME);
			preBuilder.append(" > ");
			preBuilder.append(temp.MODULE_DISPLAYNAME);
			preBuilder.append(" : ");
			preBuilder.append(temp.MODULE_DESCRIPTION);
		}
		SystemHandler.MESSAGE_LIST_GROP = preBuilder.toString();
		if (entry.INIT_VERBOSE) {
			initBuilder.append("\r\n[Message] list 群聊\r\n");
			initBuilder.append(SystemHandler.MESSAGE_LIST_GROP);
		}

		// ==========================================================================================================================

		if (entry.INIT_VERBOSE) { initBuilder.append("\r\n[Module] 实例化计划任务"); }

		SystemHandler.scheduler_task = new Scheduler_TASK(initBuilder, config);
		SystemHandler.scheduler_ddns = new Scheduler_DDNS(initBuilder, config);

		if (entry.INIT_VERBOSE) { initBuilder.append("\r\n[Module] 注册计划任务"); }

		SystemHandler.registerSchedular(SystemHandler.scheduler_task);
		SystemHandler.registerSchedular(SystemHandler.scheduler_ddns);

		if (entry.INIT_VERBOSE) { initBuilder.append("\r\n[Module] 启动计划任务"); }

		SystemHandler.getSchedulerThread("task").start();
		SystemHandler.getSchedulerThread("ddns").start();

		// ==========================================================================================================================

		return true;
	}

	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	public static void doGroupMemberIncrease(int subtype, int sendTime, long fromGroup, long fromQQ, long beingOperateQQ) {
		JcqApp.CQ.sendPrivateMsg(entry.OPERATOR(), LoggerX.time() + " - [加群] " + ((subtype == 1) ? "自主申请" : "管理邀请") + "\r\n群号：" + fromGroup + "\r\n管理：" + fromQQ + "\r\n成员：" + beingOperateQQ);
		SystemHandler.listener_topspeak.memberJoin(fromGroup, beingOperateQQ);
		NickNameMap.addMember(fromQQ);
	}

	public static void doGroupMemberDecrease(int subtype, int sendTime, long fromGroup, long fromQQ, long beingOperateQQ) {
		JcqApp.CQ.sendPrivateMsg(entry.OPERATOR(), LoggerX.time() + " - [退群] " + (subtype == 1 ? "自主退群" : "管理踢出") + "\r\n群号：" + fromGroup + "\r\n管理：" + fromQQ + "\r\n成员：" + beingOperateQQ);
		SystemHandler.listener_topspeak.memberExit(fromGroup, beingOperateQQ);
	}

	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	protected static int doUserMessage(final int typeid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		SystemHandler.COUNT_USER_MESSAGE++;
		if (SystemHandler.ENABLE_TRIGGER_USER) {
			boolean INTERCEPT = false;
			for (final ModuleTrigger temp : SystemHandler.TRIGGER_USER) {
				INTERCEPT = temp.executeUserMessage(typeid, userid, message, messageid, messagefont) || INTERCEPT;
			}
			if (INTERCEPT) { return IMsg.MSG_IGNORE; }
		}
		if (SystemHandler.ENABLE_LISENTER_USER) {
			for (final ModuleListener temp : SystemHandler.LISTENER_USER) {
				temp.executeUserMessage(typeid, userid, message, messageid, messagefont);
			}
		}
		if (message.isCommand) {
			switch (message.messages[0]) {
			case "info":
				Module.userInfo(userid, SystemHandler.MESSAGE_INFO);
				break;
			case "eula":
				Module.userInfo(userid, SystemHandler.MESSAGE_EULA);
				break;
			case "list":
				Module.userInfo(userid, SystemHandler.MESSAGE_LIST_USER);
				break;
			case "help":
				if (message.segment == 2) {
					if (SystemHandler.EXECUTOR_USER.containsKey(message.messages[1])) {
						Module.userInfo(userid, SystemHandler.EXECUTOR_USER.get(message.messages[1]).MODULE_FULLHELP);
					} else {
						Module.userInfo(userid, "没有此插件");
					}
				} else {
					Module.userInfo(userid, SystemHandler.MESSAGE_HELP);
				}
				break;
			default:
				if (SystemHandler.ENABLE_EXECUTOR_USER && SystemHandler.EXECUTOR_USER.containsKey(message.messages[0])) {
					SystemHandler.EXECUTOR_USER.get(message.messages[0]).executeUserMessage(typeid, userid, message, messageid, messagefont);
				} else {
					Module.userInfo(userid, "没有此插件");
				}
				break;
			}
		} else {
			Module.userInfo(userid, "未识别的内容，本BOT没有聊天功能，请使用//help查看帮助。");
		}
		return IMsg.MSG_IGNORE;
	}

	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	protected static int doDiszMessage(final long diszid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		SystemHandler.COUNT_DISZ_MESSAGE++;
		if (SystemHandler.ENABLE_TRIGGER_DISZ) {
			boolean INTERCEPT = false;
			for (final ModuleTrigger temp : SystemHandler.TRIGGER_DISZ) {
				INTERCEPT = temp.executeDiszMessage(diszid, userid, message, messageid, messagefont) || INTERCEPT;
			}
			if (INTERCEPT) { return IMsg.MSG_IGNORE; }
		}

		if (SystemHandler.ENABLE_LISENTER_DISZ) {
			for (final ModuleListener temp : SystemHandler.LISTENER_DISZ) {
				temp.executeDiszMessage(diszid, userid, message, messageid, messagefont);
			}
		}

		if (message.isCommand) {
			switch (message.messages[0]) {
			case "list":
				Module.diszInfo(diszid, userid, "已发送至私聊，如未收到请允许临时会话或添加好友");
				Module.userInfo(userid, SystemHandler.MESSAGE_LIST_DISZ);
				break;
			case "help":
				Module.diszInfo(diszid, userid, "已发送至私聊，如未收到请允许临时会话或添加好友");
				if (message.segment == 2) {
					if (SystemHandler.EXECUTOR_DISZ.containsKey(message.messages[1])) {
						Module.userInfo(userid, SystemHandler.EXECUTOR_DISZ.get(message.messages[1]).MODULE_FULLHELP);
					} else {
						Module.userInfo(userid, "没有此插件");
					}
				} else {
					Module.userInfo(userid, SystemHandler.MESSAGE_HELP);
				}
				break;
			default:
				if (SystemHandler.ENABLE_EXECUTOR_DISZ && SystemHandler.EXECUTOR_DISZ.containsKey(message.messages[0])) {
					SystemHandler.EXECUTOR_DISZ.get(message.messages[0]).executeDiszMessage(diszid, userid, message, messageid, messagefont);
				} else {
					Module.userInfo(userid, "没有此插件");
				}
				break;
			}
		}
		return IMsg.MSG_IGNORE;
	}

	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	protected static int doGropMessage(final long gropid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		SystemHandler.COUNT_GROP_MESSAGE++;
		if (SystemHandler.ENABLE_TRIGGER_GROP) {
			boolean INTERCEPT = false;
			for (final ModuleTrigger temp : SystemHandler.TRIGGER_GROP) {
				INTERCEPT = temp.executeGropMessage(gropid, userid, message, messageid, messagefont) || INTERCEPT;
			}
			if (INTERCEPT) { return IMsg.MSG_IGNORE; }
		}
		if (SystemHandler.ENABLE_LISENTER_GROP) {
			for (final ModuleListener temp : SystemHandler.LISTENER_GROP) {
				temp.executeGropMessage(gropid, userid, message, messageid, messagefont);
			}
		}
		if (message.isCommand) {
			switch (message.messages[0]) {
			case "list":
				Module.gropInfo(gropid, userid, "已发送至私聊，如未收到请允许临时会话或添加好友。");
				Module.userInfo(userid, SystemHandler.MESSAGE_LIST_GROP);
				break;
			case "help":
				if (message.segment == 2) {
					if (SystemHandler.EXECUTOR_GROP.containsKey(message.messages[1])) {
						Module.userInfo(userid, SystemHandler.EXECUTOR_GROP.get(message.messages[1]).MODULE_FULLHELP);
					} else {
						Module.userInfo(userid, "没有此插件");
					}
				} else {
					Module.userInfo(userid, SystemHandler.MESSAGE_HELP);
				}
				break;
			default:
				if (SystemHandler.ENABLE_EXECUTOR_GROP && SystemHandler.EXECUTOR_GROP.containsKey(message.messages[0])) {
					SystemHandler.EXECUTOR_GROP.get(message.messages[0]).executeGropMessage(gropid, userid, message, messageid, messagefont);
				} else {
					Module.userInfo(userid, "没有此插件");
				}
				break;
			}
		}
		return IMsg.MSG_IGNORE;
	}

	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	public static String generateFullReport(int logLevel, int logMode, int typeid, long userid, long diszid, long gropid, Message message, Object[] parameters) {

		String report;
		StringBuilder builder = new StringBuilder();

		long uptime = System.currentTimeMillis() - SystemHandler.BOOTTIME;
		long uptimedd = uptime / 86400000;
		uptime = uptime % 86400000;
		long uptimehh = uptime / 3600000;
		uptime = uptime % 3600000;
		long uptimemm = uptime / 60000;
		uptime = uptime % 60000;
		long uptimess = uptime / 1000;

		long totalMemory = Runtime.getRuntime().totalMemory() / 1024;
		long freeMemory = Runtime.getRuntime().freeMemory() / 1024;

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
		builder.append("KB\r\n");

		// ===============================================================================

		builder.append("\r\n调用-私聊： ");
		builder.append(SystemHandler.COUNT_USER_MESSAGE);
		builder.append("次\r\n调用-组聊： ");
		builder.append(SystemHandler.COUNT_DISZ_MESSAGE);
		builder.append("次\r\n调用-群聊： ");
		builder.append(SystemHandler.COUNT_GROP_MESSAGE);

		// ===============================================================================

		builder.append("次\r\n\r\n触发器状态: ");
		builder.append(SystemHandler.TRIGGER_INSTANCE.size());
		builder.append("个");

		for (final String name : SystemHandler.TRIGGER_INSTANCE.keySet()) {
			ModuleTrigger temp = SystemHandler.TRIGGER_INSTANCE.get(name);
			builder.append("\r\n模块 ");
			builder.append(temp.MODULE_PACKAGENAME);
			builder.append(": ");
			builder.append(temp.COUNT);
			builder.append("次");
			report = temp.generateReport(0, 0, 0, 0, 0, 0, null, null);
			if (report != null) {
				builder.append("\r\n");
				builder.append(report);
			}
		}

		// ===============================================================================

		builder.append("\r\n\r\n监听器状态:");
		builder.append(SystemHandler.LISTENER_INSTANCE.size());
		builder.append("个");

		for (final String name : SystemHandler.LISTENER_INSTANCE.keySet()) {
			ModuleListener temp = SystemHandler.LISTENER_INSTANCE.get(name);
			builder.append("\r\n模块 ");
			builder.append(temp.MODULE_PACKAGENAME);
			builder.append(": ");
			builder.append(temp.COUNT);
			builder.append("次");
			report = temp.generateReport(0, 0, 0, 0, 0, 0, null, null);
			if (report != null) {
				builder.append("\r\n");
				builder.append(report);
			}
		}

		// ===============================================================================

		builder.append("\r\n\r\n执行器状态: ");
		builder.append(SystemHandler.EXECUTOR_INSTANCE.size());
		builder.append("个");

		for (final String name : SystemHandler.EXECUTOR_INSTANCE.keySet()) {
			ModuleExecutor temp = SystemHandler.EXECUTOR_INSTANCE.get(name);
			builder.append("\r\n模块 ");
			builder.append(temp.MODULE_PACKAGENAME);
			builder.append(": ");
			builder.append(temp.COUNT);
			builder.append("次");
			report = temp.generateReport(0, 0, 0, 0, 0, 0, null, null);
			if (report != null) {
				builder.append("\r\n");
				builder.append(report);
			}
		}

		// ===============================================================================

		return builder.toString();
	}

	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	public static void registerUserTrigger(ModuleTrigger trigger) {
		if (!trigger.ENABLE_USER) { return; }
		SystemHandler.TRIGGER_USER.add(trigger);
		SystemHandler.TRIGGER_INSTANCE.put(trigger.MODULE_PACKAGENAME, trigger);

	}

	public static void registerDiszTrigger(ModuleTrigger trigger) {
		if (!trigger.ENABLE_DISZ) { return; }
		SystemHandler.TRIGGER_DISZ.add(trigger);
		SystemHandler.TRIGGER_INSTANCE.put(trigger.MODULE_PACKAGENAME, trigger);
	}

	private static void registerGropTrigger(ModuleTrigger trigger) {
		if (!trigger.ENABLE_GROP) { return; }
		SystemHandler.TRIGGER_GROP.add(trigger);
		SystemHandler.TRIGGER_INSTANCE.put(trigger.MODULE_PACKAGENAME, trigger);
	}

	// ==========================================================================================================================

	public static void registerUserListener(ModuleListener listener) {
		if (!listener.ENABLE_USER) { return; }
		SystemHandler.LISTENER_USER.add(listener);
		SystemHandler.LISTENER_INSTANCE.put(listener.MODULE_PACKAGENAME, listener);
	}

	public static void registerDiszListener(ModuleListener listener) {
		if (!listener.ENABLE_DISZ) { return; }
		SystemHandler.LISTENER_DISZ.add(listener);
		SystemHandler.LISTENER_INSTANCE.put(listener.MODULE_PACKAGENAME, listener);
	}

	public static void registerGropListener(ModuleListener listener) {
		if (!listener.ENABLE_GROP) { return; }
		SystemHandler.LISTENER_GROP.add(listener);
		SystemHandler.LISTENER_INSTANCE.put(listener.MODULE_PACKAGENAME, listener);
	}

	// ==========================================================================================================================

	public static void registerUserExecutor(ModuleExecutor executor) {
		SystemHandler.EXECUTOR_USER.put(executor.MODULE_PACKAGENAME, executor);
		SystemHandler.EXECUTOR_INSTANCE.put(executor.MODULE_PACKAGENAME, executor);
	}

	public static void registerDiszExecutor(ModuleExecutor executor) {
		SystemHandler.EXECUTOR_DISZ.put(executor.MODULE_PACKAGENAME, executor);
		SystemHandler.EXECUTOR_INSTANCE.put(executor.MODULE_PACKAGENAME, executor);
	}

	public static void registerGropExecutor(ModuleExecutor executor) {
		SystemHandler.EXECUTOR_GROP.put(executor.MODULE_PACKAGENAME, executor);
		SystemHandler.EXECUTOR_INSTANCE.put(executor.MODULE_PACKAGENAME, executor);
	}

	// ==========================================================================================================================

	public static void registerSchedular(ModuleScheduler schulder) {
		SystemHandler.SCHEDULER.put(schulder.MODULE_PACKAGENAME, schulder);
		SystemHandler.SCHEDULER_INSTANCE.put(schulder.MODULE_PACKAGENAME, new Thread(schulder));
	}

	// ==========================================================================================================================

	public static ModuleTrigger getTrigger(String name) {
		if (SystemHandler.TRIGGER_INSTANCE.containsKey(name)) {
			return SystemHandler.TRIGGER_INSTANCE.get(name);
		} else {
			return null;
		}
	}

	public static ModuleListener getListener(String name) {
		if (SystemHandler.LISTENER_INSTANCE.containsKey(name)) {
			return SystemHandler.LISTENER_INSTANCE.get(name);
		} else {
			return null;
		}
	}

	public static ModuleExecutor getExecutor(String name) {
		if (SystemHandler.EXECUTOR_INSTANCE.containsKey(name)) {
			return SystemHandler.EXECUTOR_INSTANCE.get(name);
		} else {
			return null;
		}
	}

	public static ModuleScheduler getScheduler(String name) {
		if (SystemHandler.SCHEDULER.containsKey(name)) {
			return SystemHandler.SCHEDULER.get(name);
		} else {
			return null;
		}
	}

	public static Thread getSchedulerThread(String name) {
		if (SystemHandler.SCHEDULER_INSTANCE.containsKey(name)) {
			return SystemHandler.SCHEDULER_INSTANCE.get(name);
		} else {
			return null;
		}
	}

	// ==========================================================================================================================

}
