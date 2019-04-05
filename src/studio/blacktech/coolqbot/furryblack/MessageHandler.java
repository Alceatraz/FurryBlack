package studio.blacktech.coolqbot.furryblack;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import com.sobte.cqp.jcq.entity.IMsg;
import com.sobte.cqp.jcq.event.JcqApp;
import com.sobte.cqp.jcq.event.JcqAppAbstract;

import studio.blacktech.common.ConfigureX;
import studio.blacktech.common.exception.ReInitializationException;
import studio.blacktech.coolqbot.furryblack.module.Message;
import studio.blacktech.coolqbot.furryblack.module.Module;
import studio.blacktech.coolqbot.furryblack.module.ModuleExecutor;
import studio.blacktech.coolqbot.furryblack.module.ModuleListener;
import studio.blacktech.coolqbot.furryblack.module.ModuleTrigger;

public class MessageHandler extends Module {

	private static int COUNT_USER_MESSAGE = 0;
	private static int COUNT_DISZ_MESSAGE = 0;
	private static int COUNT_GROP_MESSAGE = 0;

	// @formatter:off
	private static String MESSAGE_LIST_USER = "";
	private static String MESSAGE_LIST_DISZ = "";
	private static String MESSAGE_LIST_GROP = "";
	private static String MESSAGE_HELP = "FurryBlack - 一个小动物形象的人工智障\r\n\r\n使用即表明同意最终用户许可协议\r\n\r\n无需添加好友也可使用\r\n添加好友将自动同意\r\n邀请至群将自动同意\r\n\r\n命令均以双斜线//开头\r\n//eula - 查看用户使用协议\r\n//info - 版本版权信息\r\n//list - 列出功能模块\r\n//helpall - 列出所有模块的帮助\r\n//help <命令> - 显示指定模块帮助";
	private static String MESSAGE_INFO = "FurryBlack - 一个小动物形象的人工智障\r\n\r\n使用即表明同意最终用户许可协议(//eula查看)\r\n\r\n版本信息:\r\nREPLACE_VERSION\r\n\r\n隐私声明(框架级隐私):\r\n私聊模式 - 仅解析//开头的消息, 其他消息将视为//help处理\r\n群聊模式 - 仅解析//开头的消息, 其他消息将被无视且不记录\r\n\r\n隐私声明(模块级隐私):\r\n每个模块都有属于自己的隐私级别，可由//help <name>查看\r\n存储 - 指将含有用户信息的数据存储到持久化/数据库/文件中\r\n缓存 - 指将含有用户信息的数据存储到内存数据结构/容器中\r\n获取 - 指将含有用户信息的数据提取并用于内存仅用于单次处理\r\n\r\n版权信息(逻辑部分): 版权属于BlackTechStudio\r\n由 Team BTSNUVO 开发\r\n由 Team BTSNODE 运营\r\n\r\n版权信息(虚拟形象): 版权属于FPDG,授权使用\r\nhttps://twitter.com/flappydoggy/status/877582553762283520\r\nhttps://twitter.com/flappydoggy/status/875026125038080000".replaceAll("REPLACE_VERSION", entry.PRODUCT_VERSION);
	private static String MESSAGE_EULA = "FurryBlack - 一个小动物形象的人工智障\r\n\r\n最终用户许可协议（以下简称EULA）：\r\n\r\n甲方：Blacktechstudio（以下简称BTS）\r\n乙方：阁下\r\n\r\n1：除//help //eula //info(无参数)之外，使用本人格任何功能即表示乙方同意本EULA；\r\n2：乙方如违反本EULA，甲方有权利取消乙方的使用权；\r\n3：乙方不得以任何形式散播违法信息，此情况下甲方有权力将乙方信息提供给公安机关；\r\n4：乙方不得在未授权的情况下将本人格以任何形式用于任何形式的商业用途；\r\n5：甲方以及所有涉及的开发维护保障人员不承担由于乙方使用导致的任何损失；\r\n6：甲方以及所有涉及的开发维护保障人员不承担由于程序或机组故障引起的任何损失；\r\n7：商业用途，业务合作请联系我们的BD邮箱 alceatraz@blacktech.studio。\r\n\r\nBTS，2019-02-22 敬上";
	// @formatter:on

	private static boolean INITIALIZATIONLOCK = false;

	private static boolean ENABLE_BLACKLIST = false;

	private static boolean ENABLE_USER_IGNORE = false;
	private static boolean ENABLE_DISZ_IGNORE = false;
	private static boolean ENABLE_GROP_IGNORE = false;

	private static boolean ENABLE_TRIGGER_USER = false;
	private static boolean ENABLE_TRIGGER_DISZ = false;
	private static boolean ENABLE_TRIGGER_GROP = false;

	private static boolean ENABLE_LISENTER_USER = false;
	private static boolean ENABLE_LISENTER_DISZ = false;
	private static boolean ENABLE_LISENTER_GROP = false;

	private static ArrayList<CharSequence> BLACKLIST = new ArrayList<CharSequence>(100);

	private static ArrayList<Long> USER_IGNORE = new ArrayList<Long>(100);
	private static ArrayList<Long> DISZ_IGNORE = new ArrayList<Long>(100);
	private static ArrayList<Long> GROP_IGNORE = new ArrayList<Long>(100);

	private static ArrayList<ModuleTrigger> TRIGGER_USER = new ArrayList<ModuleTrigger>(100);
	private static ArrayList<ModuleTrigger> TRIGGER_DISZ = new ArrayList<ModuleTrigger>(100);
	private static ArrayList<ModuleTrigger> TRIGGER_GROP = new ArrayList<ModuleTrigger>(100);

	private static ArrayList<ModuleListener> LISENTER_USER = new ArrayList<ModuleListener>(100);
	private static ArrayList<ModuleListener> LISENTER_DISZ = new ArrayList<ModuleListener>(100);
	private static ArrayList<ModuleListener> LISENTER_GROP = new ArrayList<ModuleListener>(100);

	private static HashMap<String, ModuleExecutor> EXECUTOR_USER = new HashMap<String, ModuleExecutor>(100);
	private static HashMap<String, ModuleExecutor> EXECUTOR_DISZ = new HashMap<String, ModuleExecutor>(100);
	private static HashMap<String, ModuleExecutor> EXECUTOR_GROP = new HashMap<String, ModuleExecutor>(100);

	protected boolean init() throws ReInitializationException, NumberFormatException, IOException {

		if (MessageHandler.INITIALIZATIONLOCK) {
			throw new ReInitializationException();
		}
		MessageHandler.INITIALIZATIONLOCK = true;

		String line;
		BufferedReader reader;
		StringBuilder builder;

		reader = new BufferedReader(new FileReader(Paths.get(JcqAppAbstract.appDirectory, "USER_IGNORE.txt").toFile()));
		while ((line = reader.readLine()) != null) {
			MessageHandler.USER_IGNORE.add(Long.parseLong(line));
		}
		reader.close();

		reader = new BufferedReader(new FileReader(Paths.get(JcqAppAbstract.appDirectory, "DISC_IGNORE.txt").toFile()));
		while ((line = reader.readLine()) != null) {
			MessageHandler.DISZ_IGNORE.add(Long.parseLong(line));
		}
		reader.close();

		reader = new BufferedReader(new FileReader(Paths.get(JcqAppAbstract.appDirectory, "GROP_IGNORE.txt").toFile()));
		while ((line = reader.readLine()) != null) {
			MessageHandler.GROP_IGNORE.add(Long.parseLong(line));
		}
		reader.close();

		reader = new BufferedReader(new FileReader(Paths.get(JcqAppAbstract.appDirectory, "BLACKLIST.txt").toFile()));
		while ((line = reader.readLine()) != null) {
			MessageHandler.BLACKLIST.add(line);
		}
		reader.close();

		// 预生成list的内容
		builder = new StringBuilder("已经安装的插件 - 私聊可用: ");
		builder.append(MessageHandler.EXECUTOR_USER.size());
		for (final String temp : MessageHandler.EXECUTOR_USER.keySet()) {
			final ModuleExecutor module = MessageHandler.EXECUTOR_USER.get(temp);
			module.genFullHelp();
			builder.append("\r\n//");
			builder.append(module.MODULE_PACKAGENAME);
			builder.append(" > ");
			builder.append(module.MODULE_DISPLAYNAME);
			builder.append(" : ");
			builder.append(module.MODULE_DESCRIPTION);
		}
		MessageHandler.MESSAGE_LIST_USER = builder.toString();

		builder = new StringBuilder("已经安装的插件 - 讨论组可用: ");
		builder.append(MessageHandler.EXECUTOR_USER.size());
		for (final String temp : MessageHandler.EXECUTOR_USER.keySet()) {
			final ModuleExecutor module = MessageHandler.EXECUTOR_USER.get(temp);
			module.genFullHelp();
			builder.append("\r\n//");
			builder.append(module.MODULE_PACKAGENAME);
			builder.append(" > ");
			builder.append(module.MODULE_DISPLAYNAME);
			builder.append(" : ");
			builder.append(module.MODULE_DESCRIPTION);
		}
		MessageHandler.MESSAGE_LIST_DISZ = builder.toString();

		builder = new StringBuilder("已经安装的插件 - 群聊可用: ");
		builder.append(MessageHandler.EXECUTOR_USER.size());
		for (final String temp : MessageHandler.EXECUTOR_USER.keySet()) {
			final ModuleExecutor module = MessageHandler.EXECUTOR_USER.get(temp);
			module.genFullHelp();
			builder.append("\r\n//");
			builder.append(module.MODULE_PACKAGENAME);
			builder.append(" > ");
			builder.append(module.MODULE_DISPLAYNAME);
			builder.append(" : ");
			builder.append(module.MODULE_DESCRIPTION);
		}
		MessageHandler.MESSAGE_LIST_GROP = builder.toString();

		return true;
	}

	protected static int doUserMessage(final int typeid, final long userid, final String message, final int messageid, final int messagefont) throws Exception {
		MessageHandler.COUNT_USER_MESSAGE++;
		// 是否启用用户黑名单
		if (MessageHandler.ENABLE_USER_IGNORE && MessageHandler.USER_IGNORE.contains(userid)) {
			return IMsg.MSG_IGNORE;
		}
		// 是否启用监听器
		if (MessageHandler.ENABLE_LISENTER_USER) {
			for (final ModuleListener temp : MessageHandler.LISENTER_USER) {
				temp.doUserMessage(typeid, userid, new Message(message), messageid, messagefont);
			}
		}
		// 是否启用触发器
		if (MessageHandler.ENABLE_TRIGGER_USER) {
			boolean intercept = false;
			for (final ModuleTrigger temp : MessageHandler.TRIGGER_USER) {
				intercept = intercept || temp.doUserMessage(typeid, userid, new Message(message), messageid, messagefont);
			}
			if (intercept) {
				return IMsg.MSG_IGNORE;
			}
		}
		// 是否启用包含词过滤
		if (MessageHandler.ENABLE_BLACKLIST) {
			for (final CharSequence temp : MessageHandler.BLACKLIST) {
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
				JcqApp.CQ.sendPrivateMsg(userid, MessageHandler.MESSAGE_INFO);
				break;
			case "eula":
				JcqApp.CQ.sendPrivateMsg(userid, MessageHandler.MESSAGE_EULA);
				break;
			case "list":
				JcqApp.CQ.sendPrivateMsg(userid, MessageHandler.MESSAGE_LIST_USER);
				break;
			case "help":
				if (command.length == 1) {
					JcqApp.CQ.sendPrivateMsg(userid, MessageHandler.MESSAGE_HELP);
				} else if (command.length > 1) {
					if (MessageHandler.EXECUTOR_USER.containsKey(command.cmd[1])) {
						Module.userInfo(userid, MessageHandler.EXECUTOR_USER.get(command.cmd[1]).MODULE_FULLHELP);
					} else {
						Module.userInfo(userid, "没有此插件\r\n" + MessageHandler.MESSAGE_LIST_USER);
					}
				}
				break;
			case "admin":
				if (userid == ConfigureX.OPERATOR()) {
//					Zwischenspiel.parseAdmin(command);
				}
				break;
			default:
				if (MessageHandler.EXECUTOR_USER.containsKey(command.cmd[0])) {
					try {
						MessageHandler.EXECUTOR_USER.get(command.cmd[0]).doUserMessage(typeid, userid, command, messageid, messagefont);
					} catch (final Exception exception) {
						exception.printStackTrace();
					}
				} else {
					Module.userInfo(userid, "没有此插件\r\n" + MessageHandler.MESSAGE_LIST_USER);
				}
			}
			return IMsg.MSG_IGNORE;
		}
		JcqApp.CQ.sendPrivateMsg(userid, "请使用//help查看帮助");
		return IMsg.MSG_IGNORE;
	}

	protected static int doDiszMessage(final long diszid, final long userid, final String message, final int messageid, final int messagefont) throws Exception {
		MessageHandler.COUNT_USER_MESSAGE++;
		// 是否启用用户黑名单
		if (MessageHandler.ENABLE_DISZ_IGNORE && MessageHandler.DISZ_IGNORE.contains(userid)) {
			return IMsg.MSG_IGNORE;
		}
		// 是否启用监听器
		if (MessageHandler.ENABLE_LISENTER_DISZ) {
			for (final ModuleListener temp : MessageHandler.LISENTER_DISZ) {
				temp.doDiszMessage(diszid, userid, new Message(message), messageid, messagefont);
			}
		}
		// 是否启用触发器
		if (MessageHandler.ENABLE_TRIGGER_DISZ) {
			boolean intercept = false;
			for (final ModuleTrigger temp : MessageHandler.TRIGGER_DISZ) {
				intercept = intercept || temp.doDiszMessage(diszid, userid, new Message(message), messageid, messagefont);
			}
			if (intercept) {
				return IMsg.MSG_IGNORE;
			}
		}
		// 是否启用包含词过滤
		if (MessageHandler.ENABLE_BLACKLIST) {
			for (final CharSequence temp : MessageHandler.BLACKLIST) {
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
				JcqApp.CQ.sendPrivateMsg(userid, MessageHandler.MESSAGE_INFO);
				break;
			case "eula":
				JcqApp.CQ.sendPrivateMsg(userid, MessageHandler.MESSAGE_EULA);
				break;
			case "list":
				JcqApp.CQ.sendPrivateMsg(userid, MessageHandler.MESSAGE_LIST_DISZ);
				break;
			case "help":
				if (command.length == 1) {
					JcqApp.CQ.sendPrivateMsg(userid, MessageHandler.MESSAGE_HELP);
				} else if (command.length > 1) {
					if (MessageHandler.EXECUTOR_DISZ.containsKey(command.cmd[1])) {
						Module.userInfo(userid, MessageHandler.EXECUTOR_DISZ.get(command.cmd[1]).MODULE_FULLHELP);
					} else {
						Module.userInfo(userid, "没有此插件\r\n" + MessageHandler.MESSAGE_LIST_DISZ);
					}
				}
				break;
			case "admin":
				if (userid == ConfigureX.OPERATOR()) {
//					Zwischenspiel.parseAdmin(command);
				}
				break;
			default:
				if (MessageHandler.EXECUTOR_DISZ.containsKey(command.cmd[0])) {
					try {
						MessageHandler.EXECUTOR_DISZ.get(command.cmd[0]).doDiszMessage(diszid, userid, command, messageid, messagefont);
					} catch (final Exception exception) {
						exception.printStackTrace();
					}
				} else {
					Module.userInfo(userid, "没有此插件\r\n" + MessageHandler.MESSAGE_LIST_DISZ);
				}
			}
			return IMsg.MSG_IGNORE;
		}
		JcqApp.CQ.sendPrivateMsg(userid, "请使用//help查看帮助");
		return IMsg.MSG_IGNORE;
	}

	protected static int doGropMessage(final long gropid, final long userid, final String message, final int messageid, final int messagefont) throws Exception {
		MessageHandler.COUNT_GROP_MESSAGE++;
		// 是否启用用户黑名单
		if (MessageHandler.ENABLE_GROP_IGNORE && MessageHandler.GROP_IGNORE.contains(userid)) {
			return IMsg.MSG_IGNORE;
		}
		// 是否启用监听器
		if (MessageHandler.ENABLE_LISENTER_GROP) {
			for (final ModuleListener temp : MessageHandler.LISENTER_GROP) {
				temp.doGropMessage(gropid, userid, new Message(message), messageid, messagefont);
			}
		}
		// 是否启用触发器
		if (MessageHandler.ENABLE_TRIGGER_GROP) {
			boolean intercept = false;
			for (final ModuleTrigger temp : MessageHandler.TRIGGER_GROP) {
				intercept = intercept || temp.doGropMessage(gropid, userid, new Message(message), messageid, messagefont);
			}
			if (intercept) {
				return IMsg.MSG_IGNORE;
			}
		}
		// 是否启用包含词过滤
		if (MessageHandler.ENABLE_BLACKLIST) {
			for (final CharSequence temp : MessageHandler.BLACKLIST) {
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
				JcqApp.CQ.sendPrivateMsg(userid, MessageHandler.MESSAGE_INFO);
				break;
			case "eula":
				JcqApp.CQ.sendPrivateMsg(userid, MessageHandler.MESSAGE_EULA);
				break;
			case "list":
				JcqApp.CQ.sendPrivateMsg(userid, MessageHandler.MESSAGE_LIST_GROP);
				break;
			case "help":
				if (command.length == 1) {
					JcqApp.CQ.sendPrivateMsg(userid, MessageHandler.MESSAGE_HELP);
				} else if (command.length > 1) {
					if (MessageHandler.EXECUTOR_USER.containsKey(command.cmd[1])) {
						Module.userInfo(userid, MessageHandler.EXECUTOR_USER.get(command.cmd[1]).MODULE_FULLHELP);
					} else {
						Module.userInfo(userid, "没有此插件\r\n" + MessageHandler.MESSAGE_LIST_GROP);
					}
				}
				break;
			case "admin":
				if (userid == ConfigureX.OPERATOR()) {
//					Zwischenspiel.parseAdmin(command);
				}
				break;
			default:
				if (MessageHandler.EXECUTOR_GROP.containsKey(command.cmd[0])) {
					try {
						MessageHandler.EXECUTOR_GROP.get(command.cmd[0]).doGropMessage(gropid, userid, command, messageid, messagefont);
					} catch (final Exception exception) {
						exception.printStackTrace();
					}
				} else {
					Module.userInfo(userid, "没有此插件\r\n" + MessageHandler.MESSAGE_LIST_GROP);
				}
			}
			return IMsg.MSG_IGNORE;
		}
		JcqApp.CQ.sendPrivateMsg(userid, "请使用//help查看帮助");
		return IMsg.MSG_IGNORE;
	}

	@Override
	public String getReport() {
		return "USER: " + MessageHandler.COUNT_USER_MESSAGE + "\r\nDISZ: " + MessageHandler.COUNT_DISZ_MESSAGE + "\r\nGROP: " + MessageHandler.COUNT_GROP_MESSAGE;
	}
}
