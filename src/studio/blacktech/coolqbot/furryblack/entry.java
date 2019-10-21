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
 * 整个BOT的核心，JcqApp的入口类文件 Jcq将会调用约定的生命周期函数
 *
 * 我们不用IoC 我们不用DI 我们只制作高度耦合的专用框架 专用的永远是最好的
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

	/**
	 * Java通常的入口方法，JCQ并不使用这个方法，JCQ使用以下生命周期函数
	 *
	 * startup, enable, disable, exit
	 *
	 * @param parameters 命令行参数
	 */
	public static void main(String[] parameters) {
		System.out.println("This is a JCQ plugin, Not a executable jar file!");
	}

	// ==========================================================================================================================================================
	//
	// 公共恒量
	//
	// ==========================================================================================================================================================

	// 版本ID
	public final static String VerID = "12.0 2019-10-14 (00:00)";

	// 启动时间戳
	public final static long BOOTTIME = System.currentTimeMillis();

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================

	// 是否启用DEBUG的开关 启动过程为true 启动完成时改为false 此设计有助于debug和启动异常排查
	private static boolean DEBUG = true;

	// conf/ 对象的持有
	private static File FOLDER_CONF;
	// data/ 对象的持有
	private static File FOLDER_DATA;
	// 启动日志
	private static LoggerX bootLoggerX;
	// systemd 对象的持有
	private static Systemd SYSTEMD;

	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================

	// CQ对象的持有
	private static CoolQ CQ;
	// 继承自JCQ JcqAbstract包含这个对象
	private static boolean enable = false;
	// 继承自JCQ JcqAbstract包含这个对象
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

		try {

			bootLoggerX.info("FurryBlack", "启动", LoggerX.datetime());

			// ==========================================================================================================================
			// 获取APP存储目录 继承自JCQ JcqAbstract包含这个对象

			appDirectory = CQ.getAppDirectory();

			// ==========================================================================================================================
			// 实例化 conf/ 与 data/ 对象

			FOLDER_CONF = Paths.get(appDirectory, "conf").toFile();
			FOLDER_DATA = Paths.get(appDirectory, "data").toFile();

			// ==========================================================================================================================

			bootLoggerX.full("FurryBlack", "工作目录", appDirectory);
			bootLoggerX.full("FurryBlack", "配置文件目录", FOLDER_CONF.getPath());
			bootLoggerX.full("FurryBlack", "数据文件目录", FOLDER_DATA.getPath());

			// ==========================================================================================================================
			// 初始化文件夹

			if (!FOLDER_CONF.exists()) {
				bootLoggerX.seek("FurryBlack", "创建目录", FOLDER_CONF.getName());
				FOLDER_CONF.mkdirs();
			}

			if (!FOLDER_DATA.exists()) {
				bootLoggerX.seek("FurryBlack", "创建目录", FOLDER_DATA.getName());
				FOLDER_DATA.mkdirs();
			}

			if (!FOLDER_CONF.isDirectory()) { throw new NotAFolderException("配置文件夹被文件占位：" + FOLDER_CONF.getAbsolutePath()); }
			if (!FOLDER_DATA.isDirectory()) { throw new NotAFolderException("配置文件夹被文件占位：" + FOLDER_DATA.getAbsolutePath()); }

			// ==========================================================================================================================
			// 初始化Systemd

			SYSTEMD = new Systemd();
			SYSTEMD.init(bootLoggerX);
			SYSTEMD.boot(bootLoggerX);

			// ==========================================================================================================================

			bootLoggerX.info("FurryBlack", "完成", LoggerX.datetime());
			bootLoggerX.info("FurryBlack", "耗时", System.currentTimeMillis() - BOOTTIME + "ms");

			// ==========================================================================================================================

			CQ.logInfo("FurryBlack", bootLoggerX.make(3));

			SYSTEMD.adminInfo(bootLoggerX.make(0));

			// ==========================================================================================================================
			// 启动完成 关闭debug
			// 启动完成 启动Jcq的开关
			entry.DEBUG = false;
			enable = true;

		} catch (Exception exce) {
			exce.printStackTrace();
			enable = false;
			SYSTEMD.adminInfo(bootLoggerX.make(3));
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
	 * 好友添加成功的处理函数 不应该在此处修改任何内容
	 *
	 * 发送欢迎信息 实际工作中，这个东西会提示无法发送消息 貌似是 这个事件触发时 还未建立好友关系 但是添加延时依然无效 原因未知
	 *
	 */
	@Override
	public int friendAdd(int typeid, int sendtime, long userid) {
		new Thread(() -> this.sendFriendAddMessage(userid)).start();
		return 0;
	}

	private void sendFriendAddMessage(long userid) {
		SYSTEMD.userInfo(userid, "你好，在下人工智障。为了礼貌和避免打扰，本BOT不接入AI聊天功能也不支持AT。使用即表示同意最终用户许可，可由/eula查看。\r\n发送/help获取通用帮助\r\n发送/list获取可用命令列表\r\n私聊、讨论组、群聊可用的命令有所不同");
		SYSTEMD.sendEula(userid);
		SYSTEMD.sendHelp(userid);
		SYSTEMD.sendListUser(userid);
		SYSTEMD.sendListDisz(userid);
		SYSTEMD.sendListGrop(userid);
	}

	/**
	 * 好友添加请求 不应该在此处修改任何内容
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
	 * 群组添加请求 不应该在此处修改任何内容
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

	/**
	 * 禁言事件 不应该在此处修改任何内容
	 *
	 */
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

	/**
	 * 文件上传事件 不应该在此处修改任何内容
	 */
	@Override
	public int groupUpload(int typeid, int sendtime, long gropid, long userid, String file) {
		return 0;
	}

	/**
	 * 管理员变动事件 不应该在此处修改任何内容
	 */
	@Override
	public int groupAdmin(int typeid, int sendtime, long gropid, long userid) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		builder.append(typeid == 1 ? "解除" : "任命");
		builder.append("管理] - " + LoggerX.time() + "\r\n");
		builder.append("群聊ID：" + gropid + "\r\n");
		builder.append("用户ID：" + userid + "(" + SYSTEMD.getNickname(userid) + ")\r\n");
		SYSTEMD.adminInfo(builder.toString());
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

	/**
	 * 获取开关状态
	 *
	 * @return 开 / 关
	 */
	public static boolean isEnable() {
		return enable;
	}

	/**
	 * 设置开关
	 *
	 * @param mode 开 / 关
	 */
	public static void setEnable(boolean mode) {
		enable = mode;
	}

	/**
	 * 获取CQ对象 1.3.0开始CQ不再是静态调用
	 *
	 * @return CQ对象
	 */
	public static CoolQ getCQ() {
		return CQ;
	}

	/**
	 * 获取存储目录的路径
	 *
	 * @return 存储路径
	 */
	public static String getAppDirectory() {
		return appDirectory;
	}

	// ==========================================================================================================================================================
	//
	// 方法传递
	//
	// ==========================================================================================================================================================

	/**
	 * 判断一个ID是否为BOT自身
	 *
	 * @param userid 用户ID
	 * @return 是 / 否
	 */
	public static boolean isMyself(long userid) {
		return SYSTEMD.isMyself(userid);
	}

	/**
	 * 判断一个ID是否为管理员（JCQ设置的管理，并非qq群管理）
	 *
	 * @param userid 用户ID
	 * @return 是 / 否
	 */
	public static boolean isAdmin(long userid) {
		return SYSTEMD.isAdmin(userid);
	}

	/**
	 * 给管理员发消息
	 *
	 * @param message 消息
	 */
	public static void adminInfo(String message) {
		SYSTEMD.adminInfo(message);
	}

	/**
	 * 给管理员发消息
	 *
	 * @param message 消息
	 */
	public static void adminInfo(String[] message) {
		SYSTEMD.adminInfo(message);
	}

	/**
	 * 给用户发私聊
	 *
	 * @param userid  用户ID
	 * @param message 消息
	 */
	public static void userInfo(long userid, String message) {
		SYSTEMD.userInfo(userid, message);
	}

	/**
	 * 给用户发私聊
	 *
	 * @param userid  用户ID
	 * @param message 消息
	 */
	public static void userInfo(long userid, String[] message) {
		SYSTEMD.userInfo(userid, message);
	}

	/**
	 * 在讨论组发消息
	 *
	 * @param diszid  讨论组ID
	 * @param message 消息
	 */
	public static void diszInfo(long diszid, String message) {
		SYSTEMD.diszInfo(diszid, message);
	}

	/**
	 * 在讨论组发消息
	 *
	 * @param diszid  讨论组ID
	 * @param message 消息
	 */
	public static void diszInfo(long diszid, String[] message) {
		SYSTEMD.diszInfo(diszid, message);
	}

	/**
	 * 在讨论组发消息 并at某人
	 *
	 * @param diszid  讨论组ID
	 * @param userid  用户ID
	 * @param message 消息
	 */
	public static void diszInfo(long diszid, long userid, String message) {
		SYSTEMD.diszInfo(diszid, userid, message);
	}

	/**
	 * 在群聊发消息
	 *
	 * @param gropid  群组ID
	 * @param message 消息
	 */
	public static void gropInfo(long gropid, String message) {
		SYSTEMD.gropInfo(gropid, message);
	}

	/**
	 * 在群聊发消息
	 *
	 * @param gropid  群组ID
	 * @param message 消息
	 */
	public static void gropInfo(long gropid, String[] message) {
		SYSTEMD.gropInfo(gropid, message);
	}

	/**
	 * 在群聊发消息 并at某人
	 *
	 * @param gropid  群组ID
	 * @param userid  用户ID
	 * @param message 消息
	 */
	public static void gropInfo(long gropid, long userid, String message) {
		SYSTEMD.gropInfo(gropid, userid, message);
	}

	/**
	 * 私聊某人 /info
	 *
	 * @param userid 用户ID
	 */
	public static void sendInfo(long userid) {
		SYSTEMD.sendInfo(userid);
	}

	/**
	 * 私聊某人 /eula
	 *
	 * @param userid 用户ID
	 */
	public static void sendEula(long userid) {
		SYSTEMD.sendEula(userid);
	}

	/**
	 * 私聊某人 /help
	 *
	 * @param userid 用户ID
	 */
	public static void sendHelp(long userid) {
		SYSTEMD.sendHelp(userid);
	}

	/**
	 * 私聊某人 /help $name
	 *
	 * @param userid 用户ID
	 * @param name   模块名
	 */
	public static void sendHelp(long userid, String name) {
		SYSTEMD.sendHelp(userid, name);
	}

	/**
	 * 私聊某人 私聊模式/list
	 *
	 * @param userid 用户ID
	 */
	public static void sendListUser(long userid) {
		SYSTEMD.sendListUser(userid);
	}

	/**
	 * 私聊某人 讨论组模式/list
	 *
	 * @param userid 用户ID
	 */
	public static void sendListDisz(long userid) {
		SYSTEMD.sendListDisz(userid);
	}

	/**
	 * 私聊某人 群组模式/list
	 *
	 * @param userid 用户ID
	 */
	public static void sendListGrop(long userid) {
		SYSTEMD.sendListGrop(userid);
	}

	/**
	 * 撤回上一条消息 需要CoolQ Pro版本
	 *
	 * @param gropid 群组ID
	 */
	public static void revokeMessage(long gropid) {
		SYSTEMD.revokeMessage(gropid);
	}

	/**
	 * 从昵称对应表查找昵称
	 *
	 * @param userid 用户ID
	 * @return 昵称
	 */
	public static String getNickname(long userid) {
		return SYSTEMD.getNickname(userid);
	}

	/**
	 * 从昵称对应表查找昵称
	 *
	 * @param gropid 群组ID
	 * @param userid 用户ID
	 * @return 昵称
	 */
	public static String getGropnick(long gropid, long userid) {
		return SYSTEMD.getGropnick(gropid, userid);
	}

	/**
	 * 获取定时器
	 *
	 * @param name 名称
	 * @return 实例
	 */
	public static ModuleScheduler getScheduler(String name) {
		return SYSTEMD.getScheduler(name);
	}

	/**
	 * 获取触发器
	 *
	 * @param name 名称
	 * @return 实例
	 */
	public static ModuleTrigger getTrigger(String name) {
		return SYSTEMD.getTrigger(name);
	}

	/**
	 * 获取监听器
	 *
	 * @param name 名称
	 * @return 实例
	 */
	public static ModuleListener getListener(String name) {
		return SYSTEMD.getListener(name);
	}

	/**
	 * 获取执行器
	 *
	 * @param name 名称
	 * @return 实例
	 */
	public static ModuleExecutor getExecutor(String name) {
		return SYSTEMD.getExecutor(name);
	}

}
