package studio.blacktech.coolqbot.furryblack;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.util.Properties;

import com.sobte.cqp.jcq.entity.CQDebug;
import com.sobte.cqp.jcq.entity.ICQVer;
import com.sobte.cqp.jcq.entity.IMsg;
import com.sobte.cqp.jcq.entity.IRequest;
import com.sobte.cqp.jcq.event.JcqApp;
import com.sobte.cqp.jcq.event.JcqAppAbstract;

import studio.blacktech.coolqbot.furryblack.common.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.Message;
import studio.blacktech.coolqbot.furryblack.modules.Module_Nick;

public class entry extends JcqAppAbstract implements ICQVer, IMsg, IRequest {

	public static final String AppID = "studio.blacktech.coolqbot.furryblack.entry";

	public static boolean INIT_VERBOSE = false;

	public static final String PRODUCT_NAME = "FurryBlack - BOT";
	public static final String PRODUCT_PACKAGENANE = entry.AppID;
	public static final String PRODUCT_VERSION = "4.8.5 2019-06-15 (23:00)";

	private static File FOLDER_CONF;
	private static File FOLDER_DATA;
	private static File FILE_CONFIG;
	private static File FILE_GANNOUNCE;
	private static File FILE_BLACKLIST;
	private static File MODULE_NICK_NICKNAMES;
	private static File FILE_USERIGNORE;
	private static File FILE_DISZIGNORE;
	private static File FILE_GROPIGNORE;
	private static File MODULE_CHOU_USERIGNORE;

	private static long USERID_COOLQ = 0;
	private static long USERID_ADMIN = 0;

	private static Properties config = new Properties();

	public static void main(final String[] args) {
		JcqApp.CQ = new CQDebug();
		final entry demo = new entry();
		demo.startup();
		demo.enable();
		// 自定义测试内容 - 开始
		demo.privateMsg(0, 10005, 12334564L, "123456", 0);
		demo.privateMsg(0, 10005, 12334564L, "//eula", 0);
		demo.privateMsg(0, 10005, 12334564L, "//help", 0);
		demo.privateMsg(0, 10005, 12334564L, "//list", 0);
		demo.privateMsg(0, 10005, 12334564L, "//info", 0);
		demo.privateMsg(0, 10005, 12334564L, "//admin", 0);
		// 自定义测试内容 - 结束
		demo.disable();
		demo.exit();
	}

	@Override
	public String appInfo() {
		return ICQVer.CQAPIVER + "," + entry.AppID;
	}

	@Override
	public int startup() {

		return 0;
	}

	@SuppressWarnings("deprecation")
	@Override
	public int exit() {
		SystemHandler.getSchedulerThread("task").interrupt();
		SystemHandler.getSchedulerThread("ddns").interrupt();
		SystemHandler.getSchedulerThread("task").destroy();
		SystemHandler.getSchedulerThread("ddns").destroy();
		return 0;
	}

	@Override
	public int enable() {

		try {

			// ==========================================================================================================================

			final StringBuilder builder = new StringBuilder();
			builder.append("[FurryBlack]初始化 ");
			builder.append(LoggerX.time());

			// ==========================================================================================================================

			JcqAppAbstract.appDirectory = JcqApp.CQ.getAppDirectory();

			entry.FOLDER_CONF = Paths.get(JcqAppAbstract.appDirectory, "conf").toFile();
			entry.FOLDER_DATA = Paths.get(JcqAppAbstract.appDirectory, "data").toFile();
			entry.FILE_CONFIG = Paths.get(entry.FOLDER_CONF.getAbsolutePath(), "config.properties").toFile();
			entry.FILE_GANNOUNCE = Paths.get(entry.FOLDER_CONF.getAbsolutePath(), "admins.properties").toFile();
			entry.FILE_BLACKLIST = Paths.get(entry.FOLDER_CONF.getAbsolutePath(), "blacklist.txt").toFile();
			entry.FILE_USERIGNORE = Paths.get(entry.FOLDER_CONF.getAbsolutePath(), "ignore_user.txt").toFile();
			entry.FILE_DISZIGNORE = Paths.get(entry.FOLDER_CONF.getAbsolutePath(), "ignore_disz.txt").toFile();
			entry.FILE_GROPIGNORE = Paths.get(entry.FOLDER_CONF.getAbsolutePath(), "ignore_grop.txt").toFile();

			entry.MODULE_NICK_NICKNAMES = Paths.get(entry.FOLDER_CONF.getAbsolutePath(), "module_nick_nicknames.txt").toFile();
			entry.MODULE_CHOU_USERIGNORE = Paths.get(entry.FOLDER_CONF.getAbsolutePath(), "module_chou_userignore.txt").toFile();

			// ==========================================================================================================================

			if (!entry.FOLDER_CONF.exists()) {
				builder.append("\r\n[System] 配置文件夹不存在");
				entry.FOLDER_CONF.mkdirs();
			}

			if (!entry.FOLDER_CONF.isDirectory()) {
				builder.append("\r\n[System] 配置文件夹被文件占位");
				throw new IOException(":" + entry.FOLDER_CONF.getAbsolutePath());
			}

			if (!entry.FOLDER_DATA.exists()) {
				builder.append("\r\n[System] 数据文件夹不存在");
				entry.FOLDER_DATA.mkdirs();
			}

			if (!entry.FOLDER_DATA.isDirectory()) {
				builder.append("\r\n[System] 数据文件夹被文件占位");
				throw new IOException(":" + entry.FOLDER_DATA.getAbsolutePath());
			}

			if (!entry.FILE_BLACKLIST.exists()) {
				builder.append("\r\n[System] 敏感词黑名单文件不存在");
				entry.FILE_BLACKLIST.createNewFile();
			}

			if (!entry.MODULE_NICK_NICKNAMES.exists()) {
				builder.append("\r\n[System] 用户昵称映射表文件不存在");
				entry.MODULE_NICK_NICKNAMES.createNewFile();
			}

			if (!entry.FILE_USERIGNORE.exists()) {
				builder.append("\r\n[System] 私聊黑名单文件不存在");
				entry.FILE_USERIGNORE.createNewFile();
			}

			if (!entry.FILE_DISZIGNORE.exists()) {
				builder.append("\r\n[System] 组聊黑名单文件不存在");
				entry.FILE_DISZIGNORE.createNewFile();
			}

			if (!entry.FILE_GROPIGNORE.exists()) {
				builder.append("\r\n[System] 群聊黑名单文件不存在");
				entry.FILE_GROPIGNORE.createNewFile();
			}

			if (!entry.MODULE_CHOU_USERIGNORE.exists()) {
				builder.append("\r\n[System] 随机抽人黑名单文件不存在");
				entry.MODULE_CHOU_USERIGNORE.createNewFile();
			}

			if (!entry.FILE_CONFIG.exists()) {

				builder.append("\r\n[System] 配置文件不存在");

				entry.FILE_CONFIG.createNewFile();
				final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(entry.FILE_CONFIG), "UTF-8"));

				// @formatter:off
				writer.write(
					"verbose=true\r\n" +
					"operator=\r\n" +
					"enable_nickname_map=true\r\n" +
					"enable_trigger_user=false\r\n" +
					"enable_trigger_disz=false\r\n" +
					"enable_trigger_grop=false\r\n" +
					"enable_listener_user=false\r\n" +
					"enable_listener_disz=false\r\n" +
					"enable_listener_grop=false\r\n" +
					"enable_executor_user=false\r\n" +
					"enable_executor_disz=false\r\n" +
					"enable_executor_grop=false\r\n" +
					"enable_ddnsclient=true\r\n" +
					"ddnsapi_clientua=BTSCoolQ/1.0\r\n" +
					"ddnsapi_hostname=\r\n" +
					"ddnsapi_password="
				);
				// @formatter:on

				writer.flush();
				writer.close();

				builder.append("\r\n[System] 生成新的配置文件，自动退出");

				throw new Exception("配置文件不存在，创建新的配置文件，自动退出");
			}

			if (!entry.FILE_GANNOUNCE.exists()) {

				builder.append("\r\n[System] 组公告配置文件不存在");

				entry.FILE_GANNOUNCE.createNewFile();
				final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(entry.FILE_GANNOUNCE), "UTF-8"));

				// @formatter:off
				writer.write(
					"#管理员等级:管理员号码\r\n" +
					"#1,1002000888\r\n" +
					"#2:1003000888\r\n" +
					"#3:1004000888"
				);
				// @formatter:on

				writer.flush();
				writer.close();

				builder.append("\r\n[System] 生成新的群变动配置文件，自动退出");

				throw new Exception("配置文件不存在，创建新的配置文件，自动退出");
			}

			// ==========================================================================================================================

			builder.append("\r\n[System] 读取配置文件");

			entry.config.load(new FileInputStream(entry.FILE_CONFIG));

			entry.INIT_VERBOSE = Boolean.parseBoolean(entry.config.getProperty("verbose", "true"));

			entry.USERID_COOLQ = JcqApp.CQ.getLoginQQ();

			if (entry.INIT_VERBOSE) {
				builder.append("\r\n[System] 机器人账户为：");
				builder.append(entry.USERID_COOLQ);
			}

			entry.USERID_ADMIN = Long.parseLong(entry.config.getProperty("operator", "0"));

			if (entry.INIT_VERBOSE) {
				builder.append("\r\n[System] 管理员账户为：");
				builder.append(entry.USERID_ADMIN);
			}

			SystemHandler.init(builder, entry.config);
			Module_Nick.init(builder, entry.config);

			builder.append("\r\n[System] 初始化完成");

			JcqApp.CQ.sendPrivateMsg(entry.OPERATOR(), builder.toString());

			JcqAppAbstract.enable = true;

		} catch (final Exception exce) {
			exce.printStackTrace();
			JcqApp.CQ.sendPrivateMsg(entry.OPERATOR(), "警告初始化失败！");
			JcqAppAbstract.enable = false;
		}
		return 0;
	}

	@SuppressWarnings("deprecation")
	@Override
	public int disable() {
		SystemHandler.getSchedulerThread("task").interrupt();
		SystemHandler.getSchedulerThread("ddns").interrupt();
		SystemHandler.getSchedulerThread("task").destroy();
		SystemHandler.getSchedulerThread("ddns").destroy();
		JcqAppAbstract.enable = false;
		return 0;
	}

	// ==============================================================================================================================================================

	@Override
	public int privateMsg(final int typeid, final int messageid, final long userid, final String message, final int messagefont) {
		try {
			SystemHandler.doUserMessage(typeid, userid, new Message(message), messageid, messagefont);
		} catch (final Exception exce) {
			exce.printStackTrace();
			final StringBuilder builder = new StringBuilder();
			builder.append(LoggerX.time());
			builder.append(" [私聊消息异常] - ");
			builder.append(messageid);
			builder.append("\r\n 用户:");
			builder.append(userid);
			builder.append("\r\n 消息:");
			builder.append(message);
			builder.append("\r\n 消息:");
			builder.append(message.length());
			builder.append("\r\n 报错栈\r\n:");
			for (final StackTraceElement stack : exce.getStackTrace()) {
				builder.append("\r\n");
				builder.append(stack.getClassName());
				builder.append(".");
				builder.append(stack.getMethodName());
				builder.append("(");
				builder.append(stack.getClass().getSimpleName());
				builder.append(".");
				builder.append(stack.getMethodName());
				builder.append(":");
				builder.append(stack.getLineNumber());
				builder.append(")");
			}
			JcqApp.CQ.sendPrivateMsg(entry.OPERATOR(), builder.toString());
		}
		return IMsg.MSG_IGNORE;
	}

	@Override
	public int discussMsg(final int typeid, final int messageid, final long diszid, final long userid, final String message, final int messagefont) {
		try {
			SystemHandler.doDiszMessage(diszid, userid, new Message(message), messageid, messagefont);
		} catch (final Exception exce) {
			exce.printStackTrace();
			final StringBuilder builder = new StringBuilder();
			builder.append(LoggerX.time());
			builder.append(" [讨论组消息异常] - ");
			builder.append(messageid);
			builder.append("\r\n 组号:");
			builder.append(diszid);
			builder.append("\r\n 用户:");
			builder.append(userid);
			builder.append("\r\n 消息:");
			builder.append(message);
			builder.append("\r\n 消息:");
			builder.append(message.length());
			builder.append("\r\n 报错栈\r\n:");
			for (final StackTraceElement stack : exce.getStackTrace()) {
				builder.append("\r\n");
				builder.append(stack.getClassName());
				builder.append(".");
				builder.append(stack.getMethodName());
				builder.append("(");
				builder.append(stack.getClass().getSimpleName());
				builder.append(".");
				builder.append(stack.getMethodName());
				builder.append(":");
				builder.append(stack.getLineNumber());
				builder.append(")");
			}
			JcqApp.CQ.sendPrivateMsg(entry.OPERATOR(), builder.toString());
		}
		return IMsg.MSG_IGNORE;
	}

	@Override
	public int groupMsg(final int typeid, final int messageid, final long gropid, final long userid, final String anonymous, final String message, final int messagefont) {
		try {
			SystemHandler.doGropMessage(gropid, userid, new Message(message), messageid, messagefont);
		} catch (final Exception exce) {
			exce.printStackTrace();
			final StringBuilder builder = new StringBuilder();
			builder.append(LoggerX.time());
			builder.append(" [群聊消息异常] - ");
			builder.append(messageid);
			builder.append("\r\n 群号:");
			builder.append(gropid);
			builder.append("\r\n 用户:");
			builder.append(userid);
			builder.append("\r\n 消息:");
			builder.append(message);
			builder.append("\r\n 消息:");
			builder.append(message.length());
			builder.append("\r\n 报错栈:");
			for (final StackTraceElement stack : exce.getStackTrace()) {
				builder.append("\r\n");
				builder.append(stack.getClassName());
				builder.append(".");
				builder.append(stack.getMethodName());
				builder.append("(");
				builder.append(stack.getClass().getSimpleName());
				builder.append(".");
				builder.append(stack.getMethodName());
				builder.append(":");
				builder.append(stack.getLineNumber());
				builder.append(")");
			}
			JcqApp.CQ.sendPrivateMsg(entry.OPERATOR(), builder.toString());
		}
		return IMsg.MSG_IGNORE;
	}

	// ==============================================================================================================================================================

	/***
	 * 添加好友成功时由JcqSDK调用
	 */
	@Override
	public int friendAdd(final int subtype, final int sendTime, final long fromQQ) {
		JcqApp.CQ.sendPrivateMsg(fromQQ, "在下人工智障，如有疑问请发送//help获取帮助。");
		return 0;
	}

	/***
	 * 收到添加好友请求时由JcqSDK调用
	 */
	@Override
	public int requestAddFriend(final int type, final int time, final long qqid, final String message, final String flag) {
		JcqApp.CQ.sendPrivateMsg(entry.OPERATOR(), LoggerX.time() + "[FurryBlack] 好友申请 " + qqid + " : " + message);
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
			JcqApp.CQ.sendPrivateMsg(entry.OPERATOR(), LoggerX.time() + "[FurryBlack] 加群申请 - 群号:" + fromGroup + " 申请者:" + fromQQ);
		} else if (subtype == 2) {
			JcqApp.CQ.sendPrivateMsg(entry.OPERATOR(), LoggerX.time() + "[FurryBlack] 加群邀请 - 群号:" + fromGroup + " 邀请者:" + fromQQ);
			// 同意邀请进群
			JcqApp.CQ.setGroupAddRequest(responseFlag, IRequest.REQUEST_GROUP_INVITE, IRequest.REQUEST_ADOPT, null);
		}
		return 0;
	}

	// ==============================================================================================================================================================

	@Override
	public int groupMemberIncrease(final int subtype, final int sendTime, final long fromGroup, final long fromQQ, final long beingOperateQQ) {
		try {
			SystemHandler.doGroupMemberIncrease(subtype, sendTime, fromGroup, fromQQ, beingOperateQQ);
		} catch (final Exception exce) {
			exce.printStackTrace();
			final StringBuilder builder = new StringBuilder();
			builder.append(LoggerX.time());
			builder.append(" [成员增加异常]");
			for (final StackTraceElement stack : exce.getStackTrace()) {
				builder.append("\r\n");
				builder.append(stack.getClassName());
				builder.append(".");
				builder.append(stack.getMethodName());
				builder.append("(");
				builder.append(stack.getClass().getSimpleName());
				builder.append(".");
				builder.append(stack.getMethodName());
				builder.append(":");
				builder.append(stack.getLineNumber());
				builder.append(")");
			}
			JcqApp.CQ.sendPrivateMsg(entry.OPERATOR(), builder.toString());
		}
		return IMsg.MSG_IGNORE;
	}

	@Override
	public int groupMemberDecrease(final int subtype, final int sendTime, final long fromGroup, final long fromQQ, final long beingOperateQQ) {
		try {
			SystemHandler.doGroupMemberDecrease(subtype, sendTime, fromGroup, fromQQ, beingOperateQQ);
		} catch (final Exception exce) {
			exce.printStackTrace();
			final StringBuilder builder = new StringBuilder();
			builder.append(LoggerX.time());
			builder.append(" [成员减少异常]");
			for (final StackTraceElement stack : exce.getStackTrace()) {
				builder.append("\r\n");
				builder.append(stack.getClassName());
				builder.append(".");
				builder.append(stack.getMethodName());
				builder.append("(");
				builder.append(stack.getClass().getSimpleName());
				builder.append(".");
				builder.append(stack.getMethodName());
				builder.append(":");
				builder.append(stack.getLineNumber());
				builder.append(")");
			}
			JcqApp.CQ.sendPrivateMsg(entry.OPERATOR(), builder.toString());
		}
		return IMsg.MSG_IGNORE;
	}
	// ==============================================================================================================================================================

	public static long OPERATOR() {
		return entry.USERID_ADMIN;
	}

	public static long MYSELFID() {
		return entry.USERID_COOLQ;
	}

	public static File FOLDER_CONF() {
		return entry.FOLDER_CONF;
	}

	public static File FOLDER_DATA() {
		return entry.FOLDER_DATA;
	}

	public static File FILE_GANNOUNCE() {
		return entry.FILE_GANNOUNCE;
	}

	public static File FILE_BLACKLIST() {
		return entry.FILE_BLACKLIST;
	}

	public static File FILE_USERIGNORE() {
		return entry.FILE_USERIGNORE;
	}

	public static File FILE_DISZIGNORE() {
		return entry.FILE_DISZIGNORE;
	}

	public static File FILE_GROPIGNORE() {
		return entry.FILE_GROPIGNORE;
	}

	public static File MODULE_NICK_NICKNAMES() {
		return entry.MODULE_NICK_NICKNAMES;
	}

	public static File MODULE_CHOU_USERIGNORE() {
		return entry.MODULE_CHOU_USERIGNORE;
	}

	// ==============================================================================================================================================================

	@Override
	public int groupAdmin(final int subtype, final int sendTime, final long fromGroup, final long beingOperateQQ) {
		return 0;
	}

	@Override
	public int groupUpload(final int subType, final int sendTime, final long fromGroup, final long fromQQ, final String file) {
		return 0;
	}

}
