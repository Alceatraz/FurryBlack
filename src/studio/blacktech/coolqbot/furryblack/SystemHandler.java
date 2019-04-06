package studio.blacktech.coolqbot.furryblack;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import com.sobte.cqp.jcq.entity.Group;
import com.sobte.cqp.jcq.entity.IMsg;
import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.common.ConfigureX;
import studio.blacktech.common.LoggerX;
import studio.blacktech.common.exception.ReInitializationException;
import studio.blacktech.coolqbot.furryblack.module.Message;
import studio.blacktech.coolqbot.furryblack.module.Module;
import studio.blacktech.coolqbot.furryblack.module.ModuleExecutor;
import studio.blacktech.coolqbot.furryblack.module.ModuleListener;
import studio.blacktech.coolqbot.furryblack.module.ModuleTrigger;
import studio.blacktech.coolqbot.furryblack.plugins.Executor_chou;
import studio.blacktech.coolqbot.furryblack.plugins.Executor_dice;
import studio.blacktech.coolqbot.furryblack.plugins.Executor_echo;
import studio.blacktech.coolqbot.furryblack.plugins.Executor_gamb;
import studio.blacktech.coolqbot.furryblack.plugins.Executor_jrjp;
import studio.blacktech.coolqbot.furryblack.plugins.Executor_jrrp;
import studio.blacktech.coolqbot.furryblack.plugins.Executor_kong;
import studio.blacktech.coolqbot.furryblack.plugins.Executor_roll;
import studio.blacktech.coolqbot.furryblack.plugins.Executor_zhan;
import studio.blacktech.coolqbot.furryblack.plugins.Listener_WaterCount;
import studio.blacktech.coolqbot.furryblack.scheduler.Scheduler_DDNS;
import studio.blacktech.coolqbot.furryblack.scheduler.Scheduler_Task;

@SuppressWarnings({
		"deprecation", "unused"
})
public class SystemHandler extends Module {

	private static int COUNT_USER_MESSAGE = 0;
	private static int COUNT_DISZ_MESSAGE = 0;
	private static int COUNT_GROP_MESSAGE = 0;

	// @formatter:off
	private static String MESSAGE_LIST_USER = "";
	private static String MESSAGE_LIST_DISZ = "";
	private static String MESSAGE_LIST_GROP = "";
	private static String MESSAGE_HELP =
			"FurryBlack - 一个小动物形象的人工智障\r\n" +
			"\r\n" +
			"使用即表明同意最终用户许可协议\r\n" +
			"\r\n" +
			"无需添加好友也可使用\r\n" +
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
			"私聊模式 - 用户消息 → 监听器 → 触发器 → 过滤器(//开头的消息) → 命令插件\r\n" +
			"群聊模式 - 用户消息 → 监听器 → 触发器 → 过滤器(//开头的消息) → 命令插件\r\n" +
			"\r\n" +
			"隐私声明(模块级隐私):\r\n" +
			"每个模块都有属于自己的隐私级别，可由//help <name>查看\r\n" +
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
			"\r\n" +
			"版权信息(虚拟形象): 版权属于FPDG,授权使用\r\n" +
			"https://twitter.com/flappydoggy/status/877582553762283520\r\n" +
			"https://twitter.com/flappydoggy/status/875026125038080000\r\n" +
			"".replaceAll("REPLACE_VERSION", entry.PRODUCT_VERSION);
	private static String MESSAGE_EULA =
			"FurryBlack - 一个小动物形象的人工智障\r\n" +
			"\r\n" +
			"最终用户许可协议（以下简称EULA）：\r\n" +
			"\r\n" +
			"甲方：Blacktechstudio（以下简称BTS）\r\n" +
			"乙方：阁下\r\n" +
			"\r\n" +
			"1：除//help //eula //info(无参数)之外，使用本人格任何功能即表示乙方同意本EULA；\r\n" +
			"2：甲方不对乙方的任何行为明示或者默示的任何赞许或反对；\r\n" +
			"3：乙方如违反本EULA，甲方有权利取消乙方的使用权；\r\n" +
			"4：乙方不得以任何形式散播违法违宪或者煽动仇恨等不良信息；\r\n" +
			"5：乙方不得在未授权的情况下将本人格以任何形式用于任何形式的商业用途；\r\n" +
			"6：甲方以及所有涉及的开发维护保障人员不承担由于乙方使用导致的任何损失；\r\n" +
			"7：甲方以及所有涉及的开发维护保障人员不承担由于程序或机组故障引起的任何损失；\r\n" +
			"\r\n" +
			"\r\n" +
			"BTS，2019-02-22 敬上";
	// @formatter:on

	private static boolean INITIALIZATIONLOCK = false;

	private static boolean ENABLE_TRIGGER_USER = false;
	private static boolean ENABLE_TRIGGER_DISZ = false;
	private static boolean ENABLE_TRIGGER_GROP = false;

	private static boolean ENABLE_LISENTER_USER = false;
	private static boolean ENABLE_LISENTER_DISZ = false;
	private static boolean ENABLE_LISENTER_GROP = false;

	private static boolean ENABLE_BLACKLIST = false;

	private static boolean ENABLE_USER_IGNORE = false;
	private static boolean ENABLE_DISZ_IGNORE = false;
	private static boolean ENABLE_GROP_IGNORE = false;

	private static HashMap<String, Thread> SCHEDULER = new HashMap<String, Thread>();

	private static ArrayList<CharSequence> BLACKLIST = new ArrayList<CharSequence>(100);

	private static ArrayList<Long> USER_IGNORE = new ArrayList<Long>(100);
	private static ArrayList<Long> DISZ_IGNORE = new ArrayList<Long>(100);
	private static ArrayList<Long> GROP_IGNORE = new ArrayList<Long>(100);

	private static HashSet<ModuleTrigger> TRIGGER_INSTANCE = new HashSet<ModuleTrigger>();
	private static HashSet<ModuleListener> LISTENER_INSTANCE = new HashSet<ModuleListener>();
	private static HashSet<ModuleExecutor> EXECUTOR_INSTANCE = new HashSet<ModuleExecutor>();

	private static ArrayList<ModuleTrigger> TRIGGER_USER = new ArrayList<ModuleTrigger>(100);
	private static ArrayList<ModuleTrigger> TRIGGER_DISZ = new ArrayList<ModuleTrigger>(100);
	private static ArrayList<ModuleTrigger> TRIGGER_GROP = new ArrayList<ModuleTrigger>(100);

	private static ArrayList<ModuleListener> LISTENER_USER = new ArrayList<ModuleListener>(100);
	private static ArrayList<ModuleListener> LISTENER_DISZ = new ArrayList<ModuleListener>(100);
	private static ArrayList<ModuleListener> LISTENER_GROP = new ArrayList<ModuleListener>(100);

	private static TreeMap<String, ModuleExecutor> EXECUTOR_USER = new TreeMap<String, ModuleExecutor>();
	private static TreeMap<String, ModuleExecutor> EXECUTOR_DISZ = new TreeMap<String, ModuleExecutor>();
	private static TreeMap<String, ModuleExecutor> EXECUTOR_GROP = new TreeMap<String, ModuleExecutor>();

	protected static boolean init(StringBuilder initbuilder) throws ReInitializationException, NumberFormatException, IOException {

		if (SystemHandler.INITIALIZATIONLOCK) {
			throw new ReInitializationException();
		}

		SystemHandler.INITIALIZATIONLOCK = true;

		SystemHandler.ENABLE_TRIGGER_USER = ConfigureX.ENABLE_TRIGGER_USER();
		SystemHandler.ENABLE_TRIGGER_DISZ = ConfigureX.ENABLE_TRIGGER_DISZ();
		SystemHandler.ENABLE_TRIGGER_GROP = ConfigureX.ENABLE_TRIGGER_GROP();

		SystemHandler.ENABLE_LISENTER_USER = ConfigureX.ENABLE_LISENTER_USER();
		SystemHandler.ENABLE_LISENTER_DISZ = ConfigureX.ENABLE_LISENTER_DISZ();
		SystemHandler.ENABLE_LISENTER_GROP = ConfigureX.ENABLE_LISENTER_GROP();

		SystemHandler.ENABLE_BLACKLIST = ConfigureX.ENABLE_BLACKLIST();

		SystemHandler.ENABLE_USER_IGNORE = ConfigureX.ENABLE_USER_IGNORE();
		SystemHandler.ENABLE_DISZ_IGNORE = ConfigureX.ENABLE_DISZ_IGNORE();
		SystemHandler.ENABLE_GROP_IGNORE = ConfigureX.ENABLE_GROP_IGNORE();

		String line;
		BufferedReader reader;
		StringBuilder builder;

		reader = new BufferedReader(new InputStreamReader(new FileInputStream(ConfigureX.FILE_BLACKLIST), "UTF-8"));
		while ((line = reader.readLine()) != null) {
			SystemHandler.BLACKLIST.add(line);
		}
		reader.close();

		reader = new BufferedReader(new InputStreamReader(new FileInputStream(ConfigureX.FILE_USERIGNORE), "UTF-8"));
		while ((line = reader.readLine()) != null) {
			SystemHandler.USER_IGNORE.add(Long.parseLong(line));
		}
		reader.close();

		reader = new BufferedReader(new InputStreamReader(new FileInputStream(ConfigureX.FILE_DISZIGNORE), "UTF-8"));
		while ((line = reader.readLine()) != null) {
			SystemHandler.DISZ_IGNORE.add(Long.parseLong(line));
		}
		reader.close();

		reader = new BufferedReader(new InputStreamReader(new FileInputStream(ConfigureX.FILE_GROPIGNORE), "UTF-8"));
		while ((line = reader.readLine()) != null) {
			SystemHandler.GROP_IGNORE.add(Long.parseLong(line));
		}
		reader.close();

		ModuleExecutor chou = new Executor_chou();
		ModuleExecutor dice = new Executor_dice();
		ModuleExecutor echo = new Executor_echo();
		ModuleExecutor gamb = new Executor_gamb();
		ModuleExecutor jrjp = new Executor_jrjp();
		ModuleExecutor jrrp = new Executor_jrrp();
		ModuleExecutor kong = new Executor_kong();
		ModuleExecutor roll = new Executor_roll();
		ModuleExecutor zhan = new Executor_zhan();

		ModuleListener watercount = new Listener_WaterCount();

		SystemHandler.registerGropListener(watercount);

		SystemHandler.registerUserExecutor(dice);
		SystemHandler.registerUserExecutor(echo);
		SystemHandler.registerUserExecutor(jrrp);
		SystemHandler.registerUserExecutor(kong);
		SystemHandler.registerUserExecutor(roll);
		SystemHandler.registerUserExecutor(zhan);

		SystemHandler.registerDiszExecutor(dice);
		SystemHandler.registerDiszExecutor(echo);
		SystemHandler.registerDiszExecutor(jrrp);
		SystemHandler.registerDiszExecutor(jrjp);
		SystemHandler.registerDiszExecutor(roll);
		SystemHandler.registerDiszExecutor(zhan);

		SystemHandler.registerGropExecutor(chou);
		SystemHandler.registerGropExecutor(dice);
		SystemHandler.registerGropExecutor(echo);
		SystemHandler.registerGropExecutor(gamb);
		SystemHandler.registerGropExecutor(jrjp);
		SystemHandler.registerGropExecutor(jrrp);
		SystemHandler.registerGropExecutor(kong);
		SystemHandler.registerGropExecutor(roll);
		SystemHandler.registerGropExecutor(zhan);

		builder = new StringBuilder();
		builder.append("已经安装的插件: ");
		builder.append(SystemHandler.EXECUTOR_USER.size());
		for (final String temp : SystemHandler.EXECUTOR_USER.keySet()) {
			final ModuleExecutor module = SystemHandler.EXECUTOR_USER.get(temp);
			module.genFullHelp();
			builder.append("\r\n//");
			builder.append(module.MODULE_PACKAGENAME);
			builder.append(" > ");
			builder.append(module.MODULE_DISPLAYNAME);
			builder.append(" : ");
			builder.append(module.MODULE_FULLHELP);
		}
		builder.append("已经安装的触发器: ");
		for (final ModuleTrigger temp : SystemHandler.TRIGGER_USER) {
			builder.append("\r\n");
			builder.append(temp.MODULE_PACKAGENAME);
			builder.append(" > ");
			builder.append(temp.MODULE_DISPLAYNAME);
			builder.append(" : ");
			builder.append(temp.MODULE_FULLHELP);
		}
		builder.append("已经安装的监听器: ");
		for (final ModuleListener temp : SystemHandler.LISTENER_USER) {
			builder.append("\r\n");
			builder.append(temp.MODULE_PACKAGENAME);
			builder.append(" > ");
			builder.append(temp.MODULE_DISPLAYNAME);
			builder.append(" : ");
			builder.append(temp.MODULE_FULLHELP);
		}
		SystemHandler.MESSAGE_LIST_USER = builder.toString();

		builder = new StringBuilder();
		builder.append("已经安装的插件：");
		builder.append(SystemHandler.EXECUTOR_DISZ.size());
		for (final String temp : SystemHandler.EXECUTOR_DISZ.keySet()) {
			final ModuleExecutor module = SystemHandler.EXECUTOR_DISZ.get(temp);
			module.genFullHelp();
			builder.append("\r\n//");
			builder.append(module.MODULE_PACKAGENAME);
			builder.append(" > ");
			builder.append(module.MODULE_DISPLAYNAME);
			builder.append(" : ");
			builder.append(module.MODULE_FULLHELP);
		}
		builder.append("已经安装的触发器：");
		for (final ModuleTrigger temp : SystemHandler.TRIGGER_DISZ) {
			builder.append("\r\n");
			builder.append(temp.MODULE_PACKAGENAME);
			builder.append(" > ");
			builder.append(temp.MODULE_DISPLAYNAME);
			builder.append(" : ");
			builder.append(temp.MODULE_FULLHELP);
		}
		builder.append("已经安装的监听器: ");
		for (final ModuleListener temp : SystemHandler.LISTENER_GROP) {
			builder.append("\r\n");
			builder.append(temp.MODULE_PACKAGENAME);
			builder.append(" > ");
			builder.append(temp.MODULE_DISPLAYNAME);
			builder.append(" : ");
			builder.append(temp.MODULE_FULLHELP);
		}
		SystemHandler.MESSAGE_LIST_DISZ = builder.toString();

		builder = new StringBuilder();
		builder.append("已经安装的插件：");
		builder.append(SystemHandler.EXECUTOR_GROP.size());
		for (final String temp : SystemHandler.EXECUTOR_GROP.keySet()) {
			final ModuleExecutor module = SystemHandler.EXECUTOR_GROP.get(temp);
			module.genFullHelp();
			builder.append("\r\n//");
			builder.append(module.MODULE_PACKAGENAME);
			builder.append(" > ");
			builder.append(module.MODULE_DISPLAYNAME);
			builder.append(" : ");
			builder.append(module.MODULE_FULLHELP);
		}
		builder.append("已经安装的触发器：");
		for (final ModuleTrigger temp : SystemHandler.TRIGGER_GROP) {
			builder.append("\r\n");
			builder.append(temp.MODULE_PACKAGENAME);
			builder.append(" > ");
			builder.append(temp.MODULE_DISPLAYNAME);
			builder.append(" : ");
			builder.append(temp.MODULE_FULLHELP);
		}
		builder.append("已经安装的监听器：");
		for (final ModuleListener temp : SystemHandler.LISTENER_GROP) {
			builder.append("\r\n");
			builder.append(temp.MODULE_PACKAGENAME);
			builder.append(" > ");
			builder.append(temp.MODULE_DISPLAYNAME);
			builder.append(" : ");
			builder.append(temp.MODULE_FULLHELP);
		}
		SystemHandler.MESSAGE_LIST_GROP = builder.toString();

		SystemHandler.SCHEDULER.put("TASK", new Thread(new Scheduler_Task()));
		SystemHandler.SCHEDULER.put("DDNS", new Thread(new Scheduler_DDNS()));

		SystemHandler.SCHEDULER.get("TASK").start();
		SystemHandler.SCHEDULER.get("DDNS").start();

		initbuilder.append("触发器-用户：");
		initbuilder.append(SystemHandler.ENABLE_TRIGGER_USER);
		initbuilder.append("\r\n");

		initbuilder.append("触发器-组聊：");
		initbuilder.append(SystemHandler.ENABLE_TRIGGER_DISZ);
		initbuilder.append("\r\n");

		initbuilder.append("触发器-群聊：");
		initbuilder.append(SystemHandler.ENABLE_LISENTER_USER);
		initbuilder.append("\r\n");

		initbuilder.append("监听器-用户：");
		initbuilder.append(SystemHandler.ENABLE_TRIGGER_USER);
		initbuilder.append("\r\n");

		initbuilder.append("监听器-组聊：");
		initbuilder.append(SystemHandler.ENABLE_LISENTER_DISZ);
		initbuilder.append("\r\n");

		initbuilder.append("触发器-群聊：");
		initbuilder.append(SystemHandler.ENABLE_LISENTER_GROP);
		initbuilder.append("\r\n");

		initbuilder.append("黑名单-关键词：");
		initbuilder.append(SystemHandler.ENABLE_LISENTER_GROP);
		for (CharSequence temp : SystemHandler.BLACKLIST) {
			initbuilder.append("\r\n");
			initbuilder.append(temp);
		}
		initbuilder.append("\r\n");

		initbuilder.append("黑名单-用户：");
		initbuilder.append(SystemHandler.ENABLE_USER_IGNORE);
		for (Long temp : SystemHandler.USER_IGNORE) {
			initbuilder.append("\r\n");
			initbuilder.append(temp);
		}
		initbuilder.append("\r\n");

		initbuilder.append("黑名单-组聊：");
		initbuilder.append(SystemHandler.ENABLE_DISZ_IGNORE);
		for (Long temp : SystemHandler.DISZ_IGNORE) {
			initbuilder.append("\r\n");
			initbuilder.append(temp);
		}
		initbuilder.append("\r\n");

		initbuilder.append("黑名单-群聊：");
		initbuilder.append(SystemHandler.ENABLE_GROP_IGNORE);
		for (Long temp : SystemHandler.GROP_IGNORE) {
			initbuilder.append("\r\n");
			initbuilder.append(temp);
		}

		return true;
	}

	protected static int doUserMessage(final int typeid, final long userid, final String message, final int messageid, final int messagefont) throws Exception {
		SystemHandler.COUNT_USER_MESSAGE++;
		// 是否启用用户黑名单
		if (SystemHandler.ENABLE_USER_IGNORE && SystemHandler.USER_IGNORE.contains(userid)) {
			return IMsg.MSG_IGNORE;
		}
		// 是否启用监听器
		if (SystemHandler.ENABLE_LISENTER_USER) {
			for (final ModuleListener temp : SystemHandler.LISTENER_USER) {
				temp.excuteUserMessage(typeid, userid, new Message(message), messageid, messagefont);
			}
		}
		// 是否启用触发器
		if (SystemHandler.ENABLE_TRIGGER_USER) {
			boolean intercept = false;
			for (final ModuleTrigger temp : SystemHandler.TRIGGER_USER) {
				intercept = intercept || temp.excuteUserMessage(typeid, userid, new Message(message), messageid, messagefont);
			}
			if (intercept) {
				return IMsg.MSG_IGNORE;
			}
		}
		// 是否启用包含词过滤
		if (SystemHandler.ENABLE_BLACKLIST) {
			for (final CharSequence temp : SystemHandler.BLACKLIST) {
				if (message.contains(temp)) {
					return IMsg.MSG_IGNORE;
				}
			}
		}
		// 如果是命令
		if (message.startsWith("//") && (message.length() > 2)) {
			final Message command = new Message(message);
			switch (command.prase()) {
			case "info":
				JcqApp.CQ.sendPrivateMsg(userid, SystemHandler.MESSAGE_INFO);
				break;
			case "eula":
				JcqApp.CQ.sendPrivateMsg(userid, SystemHandler.MESSAGE_EULA);
				break;
			case "list":
				JcqApp.CQ.sendPrivateMsg(userid, SystemHandler.MESSAGE_LIST_USER);
				break;
			case "help":
				if (command.length == 1) {
					JcqApp.CQ.sendPrivateMsg(userid, SystemHandler.MESSAGE_HELP);
				} else if (command.length > 1) {
					if (SystemHandler.EXECUTOR_USER.containsKey(command.cmd[1])) {
						Module.userInfo(userid, SystemHandler.EXECUTOR_USER.get(command.cmd[1]).MODULE_FULLHELP);
					} else {
						Module.userInfo(userid, "没有此插件\r\n" + SystemHandler.MESSAGE_LIST_USER);
					}
				}
				break;
			case "admin":
				if (userid == ConfigureX.OPERATOR()) {
					final Date date = new Date();
					if (command.length < 2) {
						String temp = SystemHandler.genReport();
						int length = temp.length();
						if (length > 3000) {
							int times = length / 3000;
							int remin = length % 3000;
							for (int i = 0; i < times; i++) {
								Module.userInfo(ConfigureX.OPERATOR(), temp.substring(i * 3000, (i + 1) * 3000));
							}
							Module.userInfo(ConfigureX.OPERATOR(), temp.substring(times * 3000));
						} else {
							Module.userInfo(ConfigureX.OPERATOR(), temp);
						}
						return IMsg.MSG_IGNORE;
					}
					switch (command.cmd[1])

					{

					case "getddns":
						Module.userInfo(ConfigureX.OPERATOR(), Scheduler_DDNS.getIPAddress());
						return IMsg.MSG_IGNORE;
					case "setddns":
						if (command.length < 3) {
							Module.userInfo(ConfigureX.OPERATOR(), Scheduler_DDNS.updateDDNSIPAddress());
						} else {
							Module.userInfo(ConfigureX.OPERATOR(), Scheduler_DDNS.setDDNSIPAddress(command.cmd[2]));
						}
						return IMsg.MSG_IGNORE;
					case "getdate":
						StringBuilder builder = new StringBuilder();
						builder.append("纳秒戳: " + System.nanoTime());
						builder.append("\r\n毫秒戳: " + date.getTime());
						builder.append("\r\n时区: " + date.getTimezoneOffset());
						builder.append("\r\n年: " + date.getYear());
						builder.append("\r\n月: " + date.getMonth());
						builder.append("\r\n日: " + date.getDate());
						builder.append("\r\n时: " + date.getHours());
						builder.append("\r\n分: " + date.getMinutes());
						builder.append("\r\n秒: " + date.getSeconds());
						Module.userInfo(ConfigureX.OPERATOR(), builder.toString());
						break;
					case "say":
						for (Group temp : JcqApp.CQ.getGroupList()) {
							JcqApp.CQ.sendGroupMsg(temp.getId(), "开发者广播(所有群) : " + command.join(1));
						}
						return IMsg.MSG_IGNORE;
					case "shui":
						for (ModuleListener temp : SystemHandler.LISTENER_INSTANCE) {
							Module.userInfo(ConfigureX.OPERATOR(), temp.generateReport());
						}
						return IMsg.MSG_IGNORE;
					}

				}
				break;
			default:
				if (SystemHandler.EXECUTOR_USER.containsKey(command.cmd[0])) {
					try {
						SystemHandler.EXECUTOR_USER.get(command.cmd[0]).excuteUserMessage(typeid, userid, command, messageid, messagefont);
					} catch (final Exception exception) {
						exception.printStackTrace();
					}
				} else {
					Module.userInfo(userid, "没有此插件\r\n" + SystemHandler.MESSAGE_LIST_USER);
				}
			}
			return IMsg.MSG_IGNORE;
		}
		JcqApp.CQ.sendPrivateMsg(userid, "请使用//help查看帮助");
		return IMsg.MSG_IGNORE;

	}

	protected static int doDiszMessage(final long diszid, final long userid, final String message, final int messageid, final int messagefont) throws Exception {
		SystemHandler.COUNT_USER_MESSAGE++;
		// 是否启用用户黑名单
		if (SystemHandler.ENABLE_DISZ_IGNORE && SystemHandler.DISZ_IGNORE.contains(userid)) {
			return IMsg.MSG_IGNORE;
		}
		// 是否启用监听器
		if (SystemHandler.ENABLE_LISENTER_DISZ) {
			for (final ModuleListener temp : SystemHandler.LISTENER_DISZ) {
				temp.excutDiszMessage(diszid, userid, new Message(message), messageid, messagefont);
			}
		}
		// 是否启用触发器
		if (SystemHandler.ENABLE_TRIGGER_DISZ) {
			boolean intercept = false;
			for (final ModuleTrigger temp : SystemHandler.TRIGGER_DISZ) {
				intercept = intercept || temp.excutDiszMessage(diszid, userid, new Message(message), messageid, messagefont);
			}
			if (intercept) {
				return IMsg.MSG_IGNORE;
			}
		}
		// 是否启用包含词过滤
		if (SystemHandler.ENABLE_BLACKLIST) {
			for (final CharSequence temp : SystemHandler.BLACKLIST) {
				if (message.contains(temp)) {
					return IMsg.MSG_IGNORE;
				}
			}
		}
		// 如果是命令
		if (message.startsWith("//") && (message.length() > 2)) {
			final Message command = new Message(message);
			switch (command.prase()) {
			case "info":
				JcqApp.CQ.sendPrivateMsg(userid, SystemHandler.MESSAGE_INFO);
				break;
			case "eula":
				JcqApp.CQ.sendPrivateMsg(userid, SystemHandler.MESSAGE_EULA);
				break;
			case "list":
				JcqApp.CQ.sendPrivateMsg(userid, SystemHandler.MESSAGE_LIST_DISZ);
				break;
			case "help":
				if (command.length == 1) {
					JcqApp.CQ.sendPrivateMsg(userid, SystemHandler.MESSAGE_HELP);
				} else if (command.length > 1) {
					if (SystemHandler.EXECUTOR_DISZ.containsKey(command.cmd[1])) {
						Module.userInfo(userid, SystemHandler.EXECUTOR_DISZ.get(command.cmd[1]).MODULE_FULLHELP);
					} else {
						Module.userInfo(userid, "没有此插件\r\n" + SystemHandler.MESSAGE_LIST_DISZ);
					}
				}
				break;
			case "admin":
				if (userid == ConfigureX.OPERATOR()) {
					Zwischenspiel.doDiszAdmin(command);
				}
				break;
			default:
				if (SystemHandler.EXECUTOR_DISZ.containsKey(command.cmd[0])) {
					try {
						SystemHandler.EXECUTOR_DISZ.get(command.cmd[0]).excutDiszMessage(diszid, userid, command, messageid, messagefont);
					} catch (final Exception exception) {
						exception.printStackTrace();
					}
				} else {
					Module.userInfo(userid, "没有此插件\r\n" + SystemHandler.MESSAGE_LIST_DISZ);
				}
			}
			return IMsg.MSG_IGNORE;
		}
		return IMsg.MSG_IGNORE;
	}

	protected static int doGropMessage(final long gropid, final long userid, final String message, final int messageid, final int messagefont) throws Exception {
		SystemHandler.COUNT_GROP_MESSAGE++;
		// 是否启用用户黑名单
		if (SystemHandler.ENABLE_GROP_IGNORE && SystemHandler.GROP_IGNORE.contains(userid)) {
			return IMsg.MSG_IGNORE;
		}
		// 是否启用监听器
		if (SystemHandler.ENABLE_LISENTER_GROP) {
			for (final ModuleListener temp : SystemHandler.LISTENER_GROP) {
				temp.excuteGropMessage(gropid, userid, new Message(message), messageid, messagefont);
			}
		}
		// 是否启用触发器
		if (SystemHandler.ENABLE_TRIGGER_GROP) {
			boolean intercept = false;
			for (final ModuleTrigger temp : SystemHandler.TRIGGER_GROP) {
				intercept = intercept || temp.excuteGropMessage(gropid, userid, new Message(message), messageid, messagefont);
			}
			if (intercept) {
				return IMsg.MSG_IGNORE;
			}
		}
		// 是否启用包含词过滤
		if (SystemHandler.ENABLE_BLACKLIST) {
			for (final CharSequence temp : SystemHandler.BLACKLIST) {
				if (message.contains(temp)) {
					return IMsg.MSG_IGNORE;
				}
			}
		}
		// 如果是命令
		if (message.startsWith("//") && (message.length() > 2)) {
			final Message command = new Message(message);
			switch (command.prase()) {
			case "info":
				JcqApp.CQ.sendPrivateMsg(userid, SystemHandler.MESSAGE_INFO);
				break;
			case "eula":
				JcqApp.CQ.sendPrivateMsg(userid, SystemHandler.MESSAGE_EULA);
				break;
			case "list":
				JcqApp.CQ.sendPrivateMsg(userid, SystemHandler.MESSAGE_LIST_GROP);
				break;
			case "help":
				if (command.length == 1) {
					JcqApp.CQ.sendPrivateMsg(userid, SystemHandler.MESSAGE_HELP);
				} else if (command.length > 1) {
					if (SystemHandler.EXECUTOR_USER.containsKey(command.cmd[1])) {
						Module.userInfo(userid, SystemHandler.EXECUTOR_USER.get(command.cmd[1]).MODULE_FULLHELP);
					} else {
						Module.userInfo(userid, "没有此插件\r\n" + SystemHandler.MESSAGE_LIST_GROP);
					}
				}
				break;
			case "admin":
				if (userid == ConfigureX.OPERATOR()) {
					Zwischenspiel.doGropAdmin(command);
				}
				break;
			default:
				if (SystemHandler.EXECUTOR_GROP.containsKey(command.cmd[0])) {
					try {
						SystemHandler.EXECUTOR_GROP.get(command.cmd[0]).excuteGropMessage(gropid, userid, command, messageid, messagefont);
					} catch (final Exception exception) {
						exception.printStackTrace();
					}
				} else {
					Module.userInfo(userid, "没有此插件\r\n" + SystemHandler.MESSAGE_LIST_GROP);
				}
			}
			return IMsg.MSG_IGNORE;
		}
		return IMsg.MSG_IGNORE;
	}

	@Override
	public String generateReport() {
		return SystemHandler.genReport();
	}

	public static String genReport() {
		String report;
		StringBuilder builder = new StringBuilder();
		builder.append(LoggerX.time());
		builder.append(" - 状态简报");
		builder.append("\r\n");
		builder.append("\r\n");

		builder.append("系统状态：");
		builder.append("\r\n");
		builder.append("调用-私聊： ");
		builder.append(SystemHandler.COUNT_USER_MESSAGE);
		builder.append("次");
		builder.append("\r\n");
		builder.append("调用-组聊： ");
		builder.append(SystemHandler.COUNT_DISZ_MESSAGE);
		builder.append("次");
		builder.append("\r\n");
		builder.append("调用-群聊： ");
		builder.append("次");
		builder.append(SystemHandler.COUNT_GROP_MESSAGE);
		builder.append("\r\n");
		builder.append("\r\n");

		builder.append("触发器状态: ");
		builder.append(SystemHandler.TRIGGER_INSTANCE.size());
		builder.append("个");
		for (final ModuleTrigger temp : SystemHandler.TRIGGER_INSTANCE) {
			builder.append("\r\n");
			builder.append("模块 ");
			builder.append(temp.MODULE_PACKAGENAME);
			builder.append(": ");
			builder.append(temp.COUNT);
			builder.append("次");
			report = temp.generateReport();
			if (report != null) {
				builder.append("\r\n");
				builder.append(report);
			}
		}
		builder.append("\r\n");
		builder.append("\r\n");

		builder.append("监听器状态:");
		builder.append(SystemHandler.LISTENER_INSTANCE.size());
		builder.append("个");
		for (final ModuleListener temp : SystemHandler.LISTENER_INSTANCE) {
			builder.append("\r\n");
			builder.append("模块 ");
			builder.append(temp.MODULE_PACKAGENAME);
			builder.append(": ");
			builder.append(temp.COUNT);
			builder.append("次");
			report = temp.generateReport();
			if (report != null) {
				builder.append("\r\n");
				builder.append(report);
			}
		}
		builder.append("\r\n");
		builder.append("\r\n");

		builder.append("执行器状态: ");
		builder.append(SystemHandler.EXECUTOR_INSTANCE.size());
		builder.append("个");
		for (final ModuleExecutor temp : SystemHandler.EXECUTOR_INSTANCE) {
			builder.append("\r\n");
			builder.append("模块 ");
			builder.append(temp.MODULE_PACKAGENAME);
			builder.append(": ");
			builder.append(temp.COUNT);
			builder.append("次");
			report = temp.generateReport();
			if (report != null) {
				builder.append("\r\n");
				builder.append(report);
			}
		}

		return builder.toString();
	}

	private static void registerUserTrigger(ModuleTrigger trigger) {
		SystemHandler.TRIGGER_USER.add(trigger);
		SystemHandler.TRIGGER_INSTANCE.add(trigger);
	}

	private static void registerDiszTrigger(ModuleTrigger trigger) {
		SystemHandler.TRIGGER_DISZ.add(trigger);
		SystemHandler.TRIGGER_INSTANCE.add(trigger);
	}

	private static void registerGropTrigger(ModuleTrigger trigger) {
		SystemHandler.TRIGGER_GROP.add(trigger);
		SystemHandler.TRIGGER_INSTANCE.add(trigger);
	}

	private static void registerUserListener(ModuleListener listener) {
		SystemHandler.LISTENER_USER.add(listener);
		SystemHandler.LISTENER_INSTANCE.add(listener);
	}

	private static void registerDiszListener(ModuleListener listener) {
		SystemHandler.LISTENER_DISZ.add(listener);
		SystemHandler.LISTENER_INSTANCE.add(listener);
	}

	private static void registerGropListener(ModuleListener listener) {
		SystemHandler.LISTENER_GROP.add(listener);
		SystemHandler.LISTENER_INSTANCE.add(listener);
	}

	private static void registerUserExecutor(ModuleExecutor executor) {
		SystemHandler.EXECUTOR_USER.put(executor.MODULE_PACKAGENAME, executor);
		SystemHandler.EXECUTOR_INSTANCE.add(executor);

	}

	private static void registerDiszExecutor(ModuleExecutor executor) {
		SystemHandler.EXECUTOR_DISZ.put(executor.MODULE_PACKAGENAME, executor);
		SystemHandler.EXECUTOR_INSTANCE.add(executor);
	}

	private static void registerGropExecutor(ModuleExecutor executor) {
		SystemHandler.EXECUTOR_GROP.put(executor.MODULE_PACKAGENAME, executor);
		SystemHandler.EXECUTOR_INSTANCE.add(executor);
	}

}
