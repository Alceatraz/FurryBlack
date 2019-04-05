package studio.blacktech.coolqbot.furryblack;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.TreeMap;

import com.sobte.cqp.jcq.entity.CQDebug;
import com.sobte.cqp.jcq.entity.ICQVer;
import com.sobte.cqp.jcq.entity.IMsg;
import com.sobte.cqp.jcq.entity.IRequest;
import com.sobte.cqp.jcq.event.JcqApp;
import com.sobte.cqp.jcq.event.JcqAppAbstract;

import studio.blacktech.common.LoggerX;
import studio.blacktech.common.LogicX;
import studio.blacktech.coolqbot.furryblack.core.EventHandler;
import studio.blacktech.coolqbot.furryblack.module.Executor_chou;
import studio.blacktech.coolqbot.furryblack.module.Executor_dice;
import studio.blacktech.coolqbot.furryblack.module.Executor_echo;
import studio.blacktech.coolqbot.furryblack.module.Executor_jrjp;
import studio.blacktech.coolqbot.furryblack.module.Executor_jrrp;
import studio.blacktech.coolqbot.furryblack.module.Executor_kong;
import studio.blacktech.coolqbot.furryblack.module.Executor_mine;
import studio.blacktech.coolqbot.furryblack.module.Executor_nsfw;
import studio.blacktech.coolqbot.furryblack.module.Executor_roll;
import studio.blacktech.coolqbot.furryblack.module.Executor_roulette;
import studio.blacktech.coolqbot.furryblack.module.Executor_zhan;
import studio.blacktech.coolqbot.furryblack.module.FunctionExecutor;
import studio.blacktech.coolqbot.furryblack.module.FunctionListener;
import studio.blacktech.coolqbot.furryblack.scheduler.Autobot_DailyDDNS;
import studio.blacktech.coolqbot.furryblack.scheduler.Autobot_DailyTask;
import studio.blacktech.coolqbot.furryblack.signal.Workflow;

public class entry extends JcqAppAbstract implements ICQVer, IMsg, IRequest {

	public static final String AppID = "studio.blacktech.coolqbot.furryblack.entry";

	public static final String PRODUCT_NAME = "FurryBlack - BOT";
	public static final String PRODUCT_PACKAGENANE = entry.AppID;
	public static final String PRODUCT_VERSION = "1.19.0 2019-03-31 (15:00)";

	private static String MESSAGE_MODULES = "";
	private static String MESSAGE_HELPALL = "";


	// @formatter:off

	public static final String MESSAGE_HELP = "FurryBlack - 一个小动物形象的人工智障\r\n\r\n使用即表明同意最终用户许可协议\r\n\r\n无需添加好友也可使用\r\n添加好友将自动同意\r\n邀请至群将自动同意\r\n\r\n命令均以双斜线//开头\r\n//eula - 查看用户使用协议\r\n//info - 版本版权信息\r\n//list - 列出功能模块\r\n//helpall - 列出所有模块的帮助\r\n//help <命令> - 显示指定模块帮助";
	public static final String MESSAGE_INFO = "FurryBlack - 一个小动物形象的人工智障\r\n\r\n使用即表明同意最终用户许可协议(//eula查看)\r\n\r\n版本信息:\r\nREPLACE_VERSION\r\n\r\n隐私声明(框架级隐私):\r\n私聊模式 - 仅解析//开头的消息, 其他消息将视为//help处理\r\n群聊模式 - 仅解析//开头的消息, 其他消息将被无视且不记录\r\n\r\n隐私声明(模块级隐私):\r\n每个模块都有属于自己的隐私级别，可由//help <name>查看\r\n存储 - 指将含有用户信息的数据存储到持久化/数据库/文件中\r\n缓存 - 指将含有用户信息的数据存储到内存数据结构/容器中\r\n获取 - 指将含有用户信息的数据提取并用于内存仅用于单次处理\r\n\r\n版权信息(逻辑部分): 版权属于BlackTechStudio\r\n由 Team BTSNUVO 开发\r\n由 Team BTSNODE 运营\r\n\r\n版权信息(虚拟形象): 版权属于FPDG,授权使用\r\nhttps://twitter.com/flappydoggy/status/877582553762283520\r\nhttps://twitter.com/flappydoggy/status/875026125038080000".replaceAll("REPLACE_VERSION", entry.PRODUCT_VERSION);
	public static final String MESSAGE_EULA = "FurryBlack - 一个小动物形象的人工智障\r\n\r\n最终用户许可协议（以下简称EULA）：\r\n\r\n甲方：Blacktechstudio（以下简称BTS）\r\n乙方：阁下\r\n\r\n1：除//help //eula //info(无参数)之外，使用本人格任何功能即表示乙方同意本EULA；\r\n2：乙方如违反本EULA，甲方有权利取消乙方的使用权；\r\n3：乙方不得以任何形式散播违法信息，此情况下甲方有权力将乙方信息提供给公安机关；\r\n4：乙方不得在未授权的情况下将本人格以任何形式用于任何形式的商业用途；\r\n5：甲方以及所有涉及的开发维护保障人员不承担由于乙方使用导致的任何损失；\r\n6：甲方以及所有涉及的开发维护保障人员不承担由于程序或机组故障引起的任何损失；\r\n7：商业用途，业务合作请联系我们的BD邮箱 alceatraz@blacktech.studio。\r\n\r\nBTS，2019-02-22 敬上";

	// @formatter:on

	/***
	 * 此main并非实际执行入口 JcqSDK调用的初始化函数为 startup() enable() disable() exit()
	 * main仅用于JcqDebug模式
	 *
	 * @param args 启动参数
	 */
	public static void main(final String[] args) {
		// 固定写法
		JcqApp.CQ = new CQDebug();
		final entry demo = new entry();
		demo.startup();
		demo.enable();
		// 自定义测试内容 - 开始
		demo.privateMsg(0, 10005, 12334564L, "123", 0);
		demo.privateMsg(0, 10005, 12334564L, "//eula", 0);
		demo.privateMsg(0, 10005, 12334564L, "//help", 0);
		demo.privateMsg(0, 10005, 12334564L, "//list", 0);
		demo.privateMsg(0, 10005, 12334564L, "//info", 0);
		demo.privateMsg(0, 10005, 12334564L, "//admin", 0);
		demo.privateMsg(0, 10005, 12334564L, "//helpall", 0);
		// 自定义测试内容 - 结束
		demo.disable();
		demo.exit();
	}

	/***
	 * Jcq强制要求函数 勿动
	 */
	@Override
	public String appInfo() {
		return ICQVer.CQAPIVER + "," + entry.AppID;
	}

	/***
	 * CoolQ-Jcq启动时调用
	 */
	@Override
	public int startup() {
		return 0;
	}

	/***
	 * CoolQ-Jcq退出时调用
	 */
	@Override
	public int exit() {
		System.exit(0);
		return 0;
	}

	/***
	 * JcqApp加载时调用-即插件初始化
	 */
	@Override
	public int enable() {
		try {
			// 获取数据目录
			JcqAppAbstract.appDirectory = JcqApp.CQ.getAppDirectory();

			// 加载配置
			final Properties property = new Properties();
			property.load(new FileInputStream(JcqAppAbstract.appDirectory + "/config.properties"));
			entry.APIURL_BASE = property.getProperty("APIURL_BASE");
			entry.APIKEY_DDNS = property.getProperty("APIKEY_DDNS");
			if ((entry.APIURL_BASE == null) || (entry.APIKEY_DDNS == null)) {
				throw new Exception("DDNS-API加载失败");
			}

			// 加载屏蔽列表

			// 加载模块
			entry.register(new Executor_chou());
			entry.register(new Executor_dice());
			entry.register(new Executor_echo());
			entry.register(new Executor_jrjp());
			entry.register(new Executor_jrrp());
			entry.register(new Executor_kong());
			entry.register(new Executor_mine());
			entry.register(new Executor_nsfw());
			entry.register(new Executor_roll());
			entry.register(new Executor_roulette());
			entry.register(new Executor_zhan());

			// 预生成//list的内容 - 开始
			StringBuilder builder = new StringBuilder("已经安装的插件: ");
			builder.append(entry.Executor.size());
			for (final String temp : entry.Executor.keySet()) {
				final FunctionExecutor module = entry.Executor.get(temp);
				builder.append("\r\n//");
				builder.append(module.MODULE_COMMAND);
				builder.append(" > ");
				builder.append(module.MODULE_NAME);
				builder.append(" : ");
				builder.append(module.MODULE_DESCRIPTION);
				module.genHelpinfo();
			}
			entry.MESSAGE_MODULES = builder.toString();
			// 预生成//list的内容 - 结束

			// 预生成//helpall的内容 - 开始
			builder = new StringBuilder("已经安装的插件: ");
			builder.append(entry.Executor.size());
			for (final String temp : entry.Executor.keySet()) {
				builder.append("\r\n\r\n");
				builder.append(entry.Executor.get(temp).MODULE_FULLHELP);
			}
			entry.MESSAGE_HELPALL = builder.toString();
			// 预生成//helpall的内容 - 结束

			// 启动异步服务
			entry.THREAD.put("DAILYTASK", new Thread(new Autobot_DailyTask()));
			entry.THREAD.put("DAILYDDNS", new Thread(new Autobot_DailyDDNS()));
			entry.THREAD.get("DAILYTASK").start();
			entry.THREAD.get("DAILYDDNS").start();

			// 发送启动通知信息
			JcqApp.CQ.sendPrivateMsg(entry.OPERATOR, LoggerX.time() + " [FurryBlack] 已启动");
			JcqAppAbstract.enable = true;

		} catch (final Exception exce) {
			// 初始化一旦出错立刻停机
			JcqApp.CQ.sendPrivateMsg(entry.OPERATOR, "警告初始化失败！");
			JcqApp.CQ.sendPrivateMsg(entry.OPERATOR, exce.getStackTrace().toString());
			System.exit(1);
		}
		return 0;
	}

	/***
	 * JcqApp卸载时调用-即插件关闭
	 */
	@Override
	public int disable() {
		JcqAppAbstract.enable = false;
		return 0;
	}

	// ==============================================================================================================================================================

	private static boolean ENABLE_LISTENER_USER = false;
	private static boolean ENABLE_LISTENER_DISC = false;
	private static boolean ENABLE_LISTENER_GROP = false;

	private static HashMap<Long, FunctionListener> LISTENER_USER = new HashMap<Long, FunctionListener>(100);
	private static HashMap<Long, HashMap<Long, FunctionListener>> LISTENER_DISC = new HashMap<Long, HashMap<Long, FunctionListener>>(100);
	private static HashMap<Long, HashMap<Long, FunctionListener>> LISTENER_GROP = new HashMap<Long, HashMap<Long, FunctionListener>>(100);

	public static void registerListenerPriv(final Long qqid) {
		entry.ENABLE_LISTENER_USER = true;
	}

	public static void registerListenerDisc(final Long qqid, final Long dzid) {
		entry.ENABLE_LISTENER_DISC = true;
	}

	public static void registerListenerGrop(final Long qqid, final Long gpid) {
		entry.ENABLE_LISTENER_GROP = true;
	}

	public static void revokeListenerPriv(final Long qqid) {
		if (entry.LISTENER_USER.size() == 0) {
			entry.ENABLE_LISTENER_USER = false;
		}
	}

	public static void revokeListenerDisc(final Long qqid, final Long dzid) {
		if (entry.LISTENER_DISC.size() == 0) {
			entry.ENABLE_LISTENER_DISC = false;
		}
	}

	public static void revokeListenerGrop(final Long qqid, final Long gpid) {
		if (entry.LISTENER_GROP.size() == 0) {
			entry.ENABLE_LISTENER_GROP = false;
		}
	}

	// ==============================================================================================================================================================

	/***
	 * 收到私聊时由JcqSDK调用
	 */
	@Override
	public int privateMsg(final int mesg_type, final int mesg_id, final long user_id, final String message, final int mesg_font) {

		HandlerFilter
	}

	/***
	 * 收到讨论组消息时由JcqSDK调用
	 */
	@Override
	public int discussMsg(final int mesg_type, final int mesg_id, final long discuss_id, final long user_id, final String message, final int mesg_font) {
//		if (USER_IGNORE.contains(user_id) || DISC_IGNORE.contains(discuss_id)) {
//			return IMsg.MSG_IGNORE;
//		}
		if (message.startsWith("//") && (message.length() > 2)) {
			LogicX.executor(new Workflow(mesg_type, mesg_id, user_id, discuss_id, message, mesg_font));
//		} else if (ENABLE_LISTENER_GROP && LISTENER_GROP.containsKey(discuss_id) && LISTENER_GROP.get(discuss_id).containsKey(user_id)) {
//			LogicX.listener(new Workflow(mesg_type, mesg_id, user_id, discuss_id, message, mesg_font));
		}
		return IMsg.MSG_IGNORE;
	}

	/***
	 * 收到群消息时由JcqSDK调用
	 */
	@Override
	public int groupMsg(final int mesg_type, final int mesg_id, final long group_id, final long user_id, final String anonmessage, final String message, final int mesg_font) {
		EventHandler.doGroupMessage(mesg_type, mesg_id, group_id, user_id, anonmessage, message, mesg_font);
		return IMsg.MSG_IGNORE;
	}

	// ==============================================================================================================================================================

	/***
	 * 收到添加好友请求时由JcqSDK调用
	 */
	@Override
	public int requestAddFriend(final int type, final int time, final long qqid, final String message, final String flag) {
		JcqApp.CQ.sendPrivateMsg(entry.OPERATOR, LoggerX.time() + " [FurryBlack] 好友申请 " + qqid + " : " + message);
		// 同意好友添加
		JcqApp.CQ.setFriendAddRequest(flag, IRequest.REQUEST_ADOPT, null);
		return 0;
	}

	/***
	 * 收到添加群请求时由JcqSDK调用
	 */
	@Override
	public int requestAddGroup(final int subtype, final int sendTime, final long fromGroup, final long fromQQ, final String msg, final String responseFlag) {
		// sub type
		// 1 -> 作为管理时收到了用户加入申请
		// 2 -> 收到好友邀请加入某群的邀请
		if (subtype == 1) {
			JcqApp.CQ.sendPrivateMsg(entry.OPERATOR, LoggerX.time() + " [FurryBlack] 加群申请 - 群号:" + fromGroup + " 申请者:" + fromQQ);
		} else if (subtype == 2) {
			JcqApp.CQ.sendPrivateMsg(entry.OPERATOR, LoggerX.time() + " [FurryBlack] 加群邀请 - 群号:" + fromGroup + " 邀请者:" + fromQQ);
			// 同意邀请进群
			JcqApp.CQ.setGroupAddRequest(responseFlag, IRequest.REQUEST_GROUP_INVITE, IRequest.REQUEST_ADOPT, null);
		}
		return 0;
	}

	// ==============================================================================================================================================================

	private static void register(final FunctionExecutor module) {

	}

	public static String getAPIKEY_DDNS() {
		return entry.APIKEY_DDNS;
	}

	public static String getAPIURL_BASE() {
		return entry.APIURL_BASE;
	}

	public static String getMESSAGE_LIST() {
		return entry.MESSAGE_MODULES;
	}

	public static String getMESSAGE_HELPALL() {
		return entry.MESSAGE_HELPALL;
	}

	// ==============================================================================================================================================================
	// 未注册 未使用

	@Override
	public int friendAdd(final int subtype, final int sendTime, final long fromQQ) {
		return 0;
	}

	@Override
	public int groupAdmin(final int subtype, final int sendTime, final long fromGroup, final long beingOperateQQ) {
		return 0;
	}

	@Override
	public int groupUpload(final int subType, final int sendTime, final long fromGroup, final long fromQQ, final String file) {
		return 0;
	}

	@Override
	public int groupMemberDecrease(final int subtype, final int sendTime, final long fromGroup, final long fromQQ, final long beingOperateQQ) {
		return 0;
	}

	@Override
	public int groupMemberIncrease(final int subtype, final int sendTime, final long fromGroup, final long fromQQ, final long beingOperateQQ) {
		return 0;
	}

}
