package studio.blacktech.coolqbot.furryblack;

import java.io.File;
import java.nio.file.Paths;

import org.meowy.cqp.jcq.entity.CoolQ;
import org.meowy.cqp.jcq.entity.ICQVer;
import org.meowy.cqp.jcq.entity.IMsg;
import org.meowy.cqp.jcq.entity.IRequest;
import org.meowy.cqp.jcq.event.JcqApp;
import org.meowy.cqp.jcq.event.JcqListener;

import studio.blacktech.coolqbot.furryblack.common.LoggerX.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.exception.NotAFolderException;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleListener;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleScheduler;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleTrigger;
import studio.blacktech.coolqbot.furryblack.modules.Systemd;

/**
 * JcqApp的入口类文件 Jcq将会调用约定的生命周期函数 我们不用IoC 我们不用DI 我们只制作高度耦合的专用框架 专用的永远是最好的
 *
 * @author Alceatraz Warprays
 */
public class entry extends JcqApp implements ICQVer, IMsg, IRequest, JcqListener {

	// 绝对不能修改 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

	public final static String AppID = "studio.blacktech.coolqbot.furryblack.entry";

	@Override
	public String appInfo() {
		return ICQVer.CQAPIVER + "," + entry.AppID;
	}

	// 绝对不能修改 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

	public static void main(String[] parameters) {
		System.out.println("This is a JCQ plugin, Not a executable jar file!");
	}

	// ==========================================================================================================================================================
	//
	// 公共恒量
	//
	// ==========================================================================================================================================================

	public final static String VerID = "12.0 2019-10-14 (00:00)";

	public final static long BOOTTIME = System.currentTimeMillis();

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================

	private static boolean DEBUG = true;

	private static File FOLDER_CONF;
	private static File FOLDER_DATA;

	private static LoggerX bootLoggerX;

	private static Systemd SYSTEMD;

	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================

	private static CoolQ CQ;

	private static boolean enable = false;

	private static String appDirectory;

	/**
	 * Jcq 1.3.0 更改了使用方式 南荒喵原话：
	 *
	 * 现在都不提供静态加载的了 不过你可以写个静态变量，然后加载的时候赋值，即可
	 *
	 * 如果说是用的有参构造方法加载的，需要继承JcqApp的
	 *
	 * 还是老的方式的话 那就不用强制继承的，只需要类里提供个CQ变量的
	 *
	 * 嗯 推荐继承JcqApp 不过之后的 JcqAppAbstract 也不会移除 移除的是，无参的构造方式
	 *
	 * @param CQ CQ对象
	 */

	public entry(CoolQ CQ) {
		super(CQ);
		entry.CQ = CQ;
	}

	/**
	 * 生命周期函数：CoolQ启动
	 */
	public int startup() {
		return 0;
	}

	/**
	 * 生命周期函数：JcqApp启动
	 */
	public int enable() {

		bootLoggerX = new LoggerX();
		LoggerX logger = bootLoggerX;

		try {

			logger.info("FurryBlack", "启动", LoggerX.datetime());

			// ==========================================================================================================================

			appDirectory = CQ.getAppDirectory();

			// ==========================================================================================================================

			FOLDER_CONF = Paths.get(appDirectory, "conf").toFile();
			FOLDER_DATA = Paths.get(appDirectory, "data").toFile();

			// ==========================================================================================================================

			logger.full("FurryBlack", "工作目录", appDirectory);
			logger.full("FurryBlack", "配置文件目录", FOLDER_CONF.getPath());
			logger.full("FurryBlack", "数据文件目录", FOLDER_DATA.getPath());

			// ==========================================================================================================================

			if (!FOLDER_CONF.exists()) {
				logger.seek("FurryBlack", "创建目录", FOLDER_CONF.getName());
				FOLDER_CONF.mkdirs();
			}

			if (!FOLDER_DATA.exists()) {
				logger.seek("FurryBlack", "创建目录", FOLDER_DATA.getName());
				FOLDER_DATA.mkdirs();
			}

			// ==========================================================================================================================

			if (!FOLDER_CONF.isDirectory()) { throw new NotAFolderException("配置文件夹被文件占位：" + FOLDER_CONF.getAbsolutePath()); }
			if (!FOLDER_DATA.isDirectory()) { throw new NotAFolderException("配置文件夹被文件占位：" + FOLDER_DATA.getAbsolutePath()); }

			// ==========================================================================================================================

			SYSTEMD = new Systemd();
			SYSTEMD.init(logger);
			SYSTEMD.boot(logger);

			// ==========================================================================================================================

			logger.info("FurryBlack", "完成", LoggerX.datetime());
			logger.info("FurryBlack", "耗时", System.currentTimeMillis() - BOOTTIME + "ms");

			// ==========================================================================================================================

			CQ.logInfo("FurryBlack", logger.make(3));

			SYSTEMD.adminInfo(logger.make(0));

			// ==========================================================================================================================

			enable = true;

			entry.DEBUG = false;

		} catch (Exception exce) {
			enable = false;
			exce.printStackTrace();
			SYSTEMD.adminInfo(logger.make(3));
		}
		return 0;
	}

	/**
	 * 生命周期函数：JcqApp卸载
	 */
	@Override
	public int disable() {
		LoggerX logger = new LoggerX();
		enable = false;
		try {
			logger.mini(LoggerX.datetime());
			logger.mini("[FurryBlack] - 保存");
			SYSTEMD.save(logger);
			logger.mini("[FurryBlack] - 结束");
			SYSTEMD.shut(logger);
			SYSTEMD.adminInfo(logger.make(0));
		} catch (Exception exception) {
			logger.mini(exception.getMessage());
		}
		return 0;
	}

	/**
	 * 生命周期函数：CoolQ关闭
	 */
	@Override
	public int exit() {
		return this.disable();
	}

	// ==========================================================================================================================================================
	//
	// 消息处理函数
	//
	// ==========================================================================================================================================================

	/**
	 * 私聊消息处理方法 不应该在此处修改任何内容
	 */
	@Override
	public int privateMsg(int typeid, int messageid, long userid, String message, int messagefont) {
		try {
			entry.SYSTEMD.doUserMessage(typeid, userid, new MessageUser(typeid, userid, message, messageid, messagefont), messageid, messagefont);
		} catch (Exception exception) {
			exception.printStackTrace();
			StringBuilder builder = new StringBuilder();
			builder.append("[私聊消息异常] - " + LoggerX.time() + "\r\n");
			builder.append("用户ID：" + userid + "\r\n");
			builder.append("消息ID：" + messageid + "\r\n");
			builder.append("消息内容：" + message + "\r\n");
			builder.append("消息转储：" + LoggerX.unicode(message) + "\r\n");
			builder.append("异常原因：" + exception.getCause() + "\r\n");
			builder.append("异常消息：" + exception.getMessage() + "\r\n");
			builder.append("异常栈：" + exception.getClass().getName() + "\r\n");
			for (StackTraceElement temp : exception.getStackTrace()) {
				builder.append("        at " + temp.getClassName() + "(" + temp.getMethodName() + ":" + temp.getLineNumber() + ")\r\n");
			}
			SYSTEMD.adminInfo(builder.toString());
			System.out.println(builder.toString());
		}
		return IMsg.MSG_IGNORE;
	}

	/**
	 * 组聊消息处理方法 不应该在此处修改任何内容
	 */
	@Override
	public int discussMsg(int typeid, int messageid, long diszid, long userid, String message, int messagefont) {
		try {
			entry.SYSTEMD.doDiszMessage(diszid, userid, new MessageDisz(diszid, userid, message, messageid, messagefont), messageid, messagefont);
		} catch (Exception exception) {
			exception.printStackTrace();
			StringBuilder builder = new StringBuilder();
			builder.append("[组聊消息异常] - " + LoggerX.time() + "\r\n");
			builder.append("组聊ID：" + diszid + "\r\n");
			builder.append("用户ID：" + userid + "\r\n");
			builder.append("消息ID：" + messageid + "\r\n");
			builder.append("消息内容：" + message + "\r\n");
			builder.append("消息转储：" + LoggerX.unicode(message) + "\r\n");
			builder.append("异常原因：" + exception.getCause() + "\r\n");
			builder.append("异常消息：" + exception.getMessage() + "\r\n");
			builder.append("异常栈：" + exception.getClass().getName() + "\r\n");
			for (StackTraceElement temp : exception.getStackTrace()) {
				builder.append("        at " + temp.getClassName() + "(" + temp.getMethodName() + ":" + temp.getLineNumber() + ")\r\n");
			}
			SYSTEMD.adminInfo(builder.toString());
			System.out.println(builder.toString());
		}
		return IMsg.MSG_IGNORE;
	}

	/**
	 * 群聊消息处理方法 不应该在此处修改任何内容
	 */
	@Override
	public int groupMsg(int typeid, int messageid, long gropid, long userid, String anonymous, String message, int messagefont) {
		try {
			entry.SYSTEMD.doGropMessage(gropid, userid, new MessageGrop(gropid, userid, message, messageid, messagefont), messageid, messagefont);
		} catch (Exception exception) {
			exception.printStackTrace();
			StringBuilder builder = new StringBuilder();
			builder.append("[群聊消息异常] - " + LoggerX.time() + "\r\n");
			builder.append("群聊ID：" + gropid + "\r\n");
			builder.append("用户ID：" + userid + "\r\n");
			builder.append("消息ID：" + messageid + "\r\n");
			builder.append("消息内容：" + message + "\r\n");
			builder.append("消息转储：" + LoggerX.unicode(message) + "\r\n");
			builder.append("异常原因：" + exception.getCause() + "\r\n");
			builder.append("异常消息：" + exception.getMessage() + "\r\n");
			builder.append("异常栈：" + exception.getClass().getName() + "\r\n");
			for (StackTraceElement temp : exception.getStackTrace()) {
				builder.append("        at " + temp.getClassName() + "(" + temp.getMethodName() + ":" + temp.getLineNumber() + ")\r\n");
			}
			SYSTEMD.adminInfo(builder.toString());
			System.out.println(builder.toString());
		}
		return IMsg.MSG_IGNORE;
	}

	// ==========================================================================================================================================================
	//
	// 群成员变动函数
	//
	// ==========================================================================================================================================================

	/**
	 * 成员加群处理方法 不应该在此处修改任何内容
	 */
	@Override
	public int groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) {
		StringBuilder builder = new StringBuilder();
		builder.append("[成员增加] - " + LoggerX.time() + "\r\n");
		builder.append("类型：" + (typeid == 1 ? "自主申请" : "邀请进群") + "\r\n");
		builder.append("群聊ID：" + gropid + "\r\n");
		builder.append("管理ID：" + operid + "(" + SYSTEMD.getNickname(operid) + ")" + "\r\n");
		builder.append("用户ID：" + userid + "(" + SYSTEMD.getNickname(userid) + ")" + "\r\n");
		try {
			entry.SYSTEMD.groupMemberIncrease(typeid, sendtime, gropid, operid, userid);
		} catch (Exception exception) {
			exception.printStackTrace();
			builder.append("[发生异常]\r\n");
			builder.append("异常原因：" + exception.getCause() + "\r\n");
			builder.append("异常消息：" + exception.getMessage() + "\r\n");
			builder.append("异常栈：" + exception.getClass().getName() + "\r\n");
			for (StackTraceElement temp : exception.getStackTrace()) {
				builder.append("        at " + temp.getClassName() + "(" + temp.getMethodName() + ":" + temp.getLineNumber() + ")\r\n");
			}
		} finally {
			System.out.println(builder.toString());
			SYSTEMD.adminInfo(builder.toString());
		}
		return IMsg.MSG_IGNORE;
	}

	/**
	 * 成员退群处理方法 不应该在此处修改任何内容
	 */
	@Override
	public int groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) {
		StringBuilder builder = new StringBuilder();
		builder.append("[成员减少] - " + LoggerX.time() + "\r\n");
		builder.append("类型：" + (typeid == 1 ? "自主退群" : "管理踢出") + "\r\n");
		builder.append("群聊ID：" + gropid + "\r\n");
		builder.append("管理ID：" + operid + "(" + SYSTEMD.getNickname(operid) + ")" + "\r\n");
		builder.append("用户ID：" + userid + "(" + SYSTEMD.getNickname(userid) + ")" + "\r\n");
		try {
			entry.SYSTEMD.groupMemberDecrease(typeid, sendtime, gropid, operid, userid);
		} catch (Exception exception) {
			exception.printStackTrace();
			builder.append("[发生异常]\r\n");
			builder.append("异常原因：" + exception.getCause() + "\r\n");
			builder.append("异常消息：" + exception.getMessage() + "\r\n");
			builder.append("异常栈：" + exception.getClass().getName() + "\r\n");
			for (StackTraceElement temp : exception.getStackTrace()) {
				builder.append("        at " + temp.getClassName() + "(" + temp.getMethodName() + ":" + temp.getLineNumber() + ")\r\n");
			}
		} finally {
			System.out.println(builder.toString());
			SYSTEMD.adminInfo(builder.toString());
		}
		return IMsg.MSG_IGNORE;
	}

	// ==========================================================================================================================================================
	//
	// 事件函数
	//
	// ==========================================================================================================================================================

	/**
	 * 好友添加成功的处理函数
	 */
	@Override
	public int friendAdd(int typeid, int sendtime, long userid) {
		try {
			Thread.sleep(10000L);
		} catch (InterruptedException exception) {
			exception.printStackTrace();
		}
		SYSTEMD.userInfo(userid, "你好，在下人工智障。为了礼貌和避免打扰，本BOT不接入AI聊天功能也不支持AT。使用即表示同意最终用户许可，可由/eula查看。\r\n发送/help获取通用帮助\r\n发送/list获取可用命令列表\r\n私聊、讨论组、群聊可用的命令有所不同");
		SYSTEMD.sendEula(userid);
		SYSTEMD.sendHelp(userid);
		SYSTEMD.sendListUser(userid);
		SYSTEMD.sendListDisz(userid);
		SYSTEMD.sendListGrop(userid);
		return 0;
	}

	/**
	 * 好友添加请求
	 */
	@Override
	public int requestAddFriend(int typeid, int sendtime, long userid, String message, String flag) {
		StringBuilder builder = new StringBuilder();
		builder.append("[添加好友请求] - " + LoggerX.time() + "\r\n");
		builder.append("用户ID：" + userid + "(" + SYSTEMD.getNickname(userid) + ")\r\n");
		builder.append("请求时间：" + sendtime + "\r\n");
		builder.append("验证消息：" + message + "\r\n");
		entry.SYSTEMD.adminInfo(builder.toString());
		CQ.setFriendAddRequest(flag, IRequest.REQUEST_ADOPT, String.valueOf(userid));
		return 0;
	}

	/**
	 * 群组添加请求
	 */
	@Override
	public int requestAddGroup(int typeid, int sendtime, long gropid, long userid, String message, String flag) {
		StringBuilder builder = new StringBuilder();
		switch (typeid) {
		case 1:
			builder.append("[申请入群] - " + LoggerX.time() + "\r\n");
			builder.append("群聊ID：" + gropid + "\r\n");
			builder.append("用户ID：" + userid + "(" + SYSTEMD.getNickname(userid) + ")\r\n");
			builder.append("请求时间：" + sendtime + "\r\n");
			builder.append("验证消息：" + (message.length() == 0 ? "无" : message));
			break;
		case 2:
			builder.append("[邀请入群] - " + LoggerX.time() + "\r\n");
			builder.append("群聊ID：" + gropid + "\r\n");
			builder.append("用户ID：" + userid + "(" + SYSTEMD.getNickname(userid) + ")\r\n");
			builder.append("请求时间：" + sendtime + "\r\n");
			builder.append("验证消息：" + (message.length() == 0 ? "无" : message));
			CQ.setGroupAddRequest(flag, IRequest.REQUEST_GROUP_INVITE, IRequest.REQUEST_ADOPT, null);
			break;
		}

		SYSTEMD.adminInfo(builder.toString());
		return 0;
	}

	@Override
	public int groupBan(int typeid, int sendtime, long gropid, long operid, long userid, long duration) {
		StringBuilder builder = new StringBuilder();
		builder.append(userid == 0 ? "[全体" : "[");
		builder.append(typeid == 1 ? "解禁]" : "禁言]");
		builder.append(LoggerX.time() + "\r\n");
		builder.append("群聊ID：" + gropid + "\r\n");
		builder.append("管理ID：" + operid + "(" + SYSTEMD.getNickname(operid) + ")\r\n");
		if (userid != 0) { builder.append("用户ID：" + userid + "(" + SYSTEMD.getNickname(userid) + ")\r\n"); }
		if (userid != 1) { builder.append("时间：" + duration); }
		SYSTEMD.adminInfo(builder.toString());
		return 0;
	}

	@Override
	public int groupUpload(int typeid, int sendtime, long gropid, long userid, String file) {
		return 0;
	}

	@Override
	public int groupAdmin(int typeid, int sendtime, long gropid, long userid) {
		return 0;
	}

	// ==========================================================================================================================================================
	//
	// 工具函数
	//
	// ==========================================================================================================================================================

	/**
	 * 获取配置文件根目录
	 *
	 * @return 配置文件根目录
	 */
	public static File FOLDER_CONF() {
		return entry.FOLDER_CONF;
	}

	/**
	 * 获取数据文件根目录
	 *
	 * @return 数据文件根目录
	 */
	public static File FOLDER_DATA() {
		return entry.FOLDER_DATA;
	}

	/**
	 * 获取Systemd对象
	 *
	 * @return Systemd对象
	 */
	public static Systemd getSystemd() {
		return SYSTEMD;
	}

	/**
	 * 切换DEBUG模式
	 *
	 * @return 是否开启DEBUG模式
	 */
	public static boolean switchDEBUG() {
		DEBUG = !DEBUG;
		return DEBUG;
	}

	/**
	 * 获取是否开启DEBUG模式
	 *
	 * @return 是否开启DEBUG模式
	 */
	public static boolean DEBUG() {
		return DEBUG;
	}

	/**
	 * 获取启动日志
	 *
	 * @param level 日志级别
	 * @return 启动日志
	 */
	public static String getBootLogger(int level) {
		return bootLoggerX.make(level);
	}

	public static boolean isEnable() {
		return enable;
	}

	public static void setEnable(boolean mode) {
		enable = mode;
	}

	public static CoolQ getCQ() {
		return CQ;
	}

	public static String getAppDirectory() {
		return appDirectory;
	}

	// ==========================================================================================================================================================
	//
	// 方法传递
	//
	// ==========================================================================================================================================================

	public static boolean isMyself(long userid) {
		return SYSTEMD.isMyself(userid);
	}

	public static boolean isAdmin(long userid) {
		return SYSTEMD.isAdmin(userid);
	}

	public static void adminInfo(String message) {
		SYSTEMD.adminInfo(message);
	}

	public static void adminInfo(String[] message) {
		SYSTEMD.adminInfo(message);
	}

	public static void userInfo(long userid, String message) {
		SYSTEMD.userInfo(userid, message);
	}

	public static void userInfo(long userid, String[] message) {
		SYSTEMD.userInfo(userid, message);
	}

	public static void diszInfo(long diszid, String message) {
		SYSTEMD.diszInfo(diszid, message);
	}

	public static void diszInfo(long diszid, String[] message) {
		SYSTEMD.diszInfo(diszid, message);
	}

	public static void diszInfo(long diszid, long userid, String message) {
		SYSTEMD.diszInfo(diszid, userid, message);
	}

	public static void gropInfo(long gropid, String message) {
		SYSTEMD.gropInfo(gropid, message);
	}

	public static void gropInfo(long gropid, String[] message) {
		SYSTEMD.gropInfo(gropid, message);
	}

	public static void gropInfo(long gropid, long userid, String message) {
		SYSTEMD.gropInfo(gropid, userid, message);
	}

	public static void sendInfo(long userid) {
		SYSTEMD.sendInfo(userid);
	}

	public static void sendEula(long userid) {
		SYSTEMD.sendEula(userid);
	}

	public static void sendHelp(long userid) {
		SYSTEMD.sendHelp(userid);
	}

	public static void sendHelp(long userid, String name) {
		SYSTEMD.sendHelp(userid, name);
	}

	public static void sendListUser(long userid) {
		SYSTEMD.sendListUser(userid);
	}

	public static void sendListDisz(long userid) {
		SYSTEMD.sendListDisz(userid);
	}

	public static void sendListGrop(long userid) {
		SYSTEMD.sendListGrop(userid);
	}

	public static void revokeMessage(long gropid) {
		SYSTEMD.revokeMessage(gropid);
	}

	public static String getNickname(long userid) {
		return SYSTEMD.getNickname(userid);
	}

	public static String getGropnick(long gropid, long userid) {
		return SYSTEMD.getGropnick(gropid, userid);
	}

	public static ModuleScheduler getScheduler(String name) {
		return SYSTEMD.getScheduler(name);
	}

	public static ModuleTrigger getTrigger(String name) {
		return SYSTEMD.getTrigger(name);
	}

	public static ModuleListener getListener(String name) {
		return SYSTEMD.getListener(name);
	}

	public static ModuleExecutor getExecutor(String name) {
		return SYSTEMD.getExecutor(name);
	}

}
