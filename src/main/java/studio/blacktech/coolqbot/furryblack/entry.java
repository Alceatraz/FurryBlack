package studio.blacktech.coolqbot.furryblack;

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

import java.io.File;
import java.nio.file.Paths;

/**
 * 整个BOT的核心，JcqApp的入口类文件 JCQ将会调用约定的生命周期函数
 * <p>
 * 我们不用IoC 我们不用DI 我们只制作高度耦合的专用框架 专用的永远是最好的
 * <p>
 * 拒绝反射地狱 拒绝注解噩梦 拒绝配置 拒绝混乱的slf4j 直观 简单 见名知意
 *
 * @author Alceatraz Warprays
 */
public class entry extends JcqApp implements ICQVer, IMsg, IRequest, JcqListener {

	// ==========================================================================================================================================================
	//
	// 宇宙恒量
	//
	// ==========================================================================================================================================================

	// 绝对不能修改 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	// 绝对不能修改 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	// 绝对不能修改 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

	public final static String AppID = "studio.blacktech.coolqbot.furryblack.entry";

	@Override public String appInfo() {
		return ICQVer.CQAPIVER + "," + AppID;
	}

	// 绝对不能修改 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
	// 绝对不能修改 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
	// 绝对不能修改 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

	/**
	 * Java通常的入口方法，JCQ并不使用这个方法，JCQ使用以下生命周期函数
	 * <p>
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
	public final static String VerID = "14.0 2019-12-08 (20:00)";

	// 启动时间戳
	public final static long BOOTTIME = System.currentTimeMillis();

	// 是否启用DEBUG的开关 启动过程为true 启动完成时改为false 此设计有助于debug和启动时异常排查
	private static boolean DEBUG = true;

	// 日志
	private static LoggerX logger;

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================

	// Systemd 对象的持有
	private static Systemd SYSTEMD;

	// CQ对象的持有
	private static CoolQ CQ;

	// 继承自JCQ, JcqAbstract包含这个对象
	private static boolean enable = false;

	// 继承自JCQ, JcqAbstract包含这个对象
	private static String appDirectory;

	private File FOLDER_ROOT;
	//	private File FOLDER_CONF;
	//	private File FOLDER_DATA;
	private File FOLDER_LOGS;
	private File FILE_LOGGER;

	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================

	/**
	 * JCQ 1.3.0 更改了使用方式 南荒喵原话：
	 * <p>
	 * 现在都不提供静态加载的了 不过你可以写个静态变量，然后加载的时候赋值，即可
	 * <p>
	 * 如果说是用的有参构造方法加载的，需要继承JcqApp的
	 * <p>
	 * 还是老的方式的话 那就不用强制继承的，只需要类里提供个CQ变量的
	 * <p>
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
	@Override public int startup() {
		return 0;
	}

	/**
	 * 生命周期函数：JcqApp启动
	 */
	@Override public int enable() {

		try {

			// ==========================================================================================================================
			// 获取APP存储目录
			// 继承自JCQ, JcqAbstract包含这个对象

			appDirectory = CQ.getAppDirectory();

			// ==========================================================================================================================
			// 实例化 data/ logs/ 对象

			FOLDER_ROOT = Paths.get(appDirectory, "Core_Entry").toFile();
			//			this.FOLDER_CONF = Paths.get(appDirectory, "Core_Entry", "conf").toFile();
			//			this.FOLDER_DATA = Paths.get(appDirectory, "Core_Entry", "data").toFile();
			FOLDER_LOGS = Paths.get(appDirectory, "Core_Entry", "logs").toFile();
			FILE_LOGGER = Paths.get(appDirectory, "Core_Entry", "logs", LoggerX.formatTime("yyyy_MM_dd_HH_mm_ss") + ".txt").toFile();

			FOLDER_ROOT = Paths.get(getAppDirectory(), "Core_Entry").toFile();
			//			this.FOLDER_CONF = Paths.get(getAppDirectory(), "Core_Entry", "conf").toFile();
			//			this.FOLDER_DATA = Paths.get(getAppDirectory(), "Core_Entry", "data").toFile();
			FOLDER_LOGS = Paths.get(getAppDirectory(), "Core_Entry", "logs").toFile();

			// ==========================================================================================================================
			// 初始化文件夹

			if (!FOLDER_ROOT.exists()) {
				FOLDER_ROOT.mkdirs();
			}
			//			if (!this.FOLDER_CONF.exists()) this.FOLDER_CONF.mkdirs();
			//			if (!this.FOLDER_DATA.exists()) this.FOLDER_DATA.mkdirs();
			if (!FOLDER_LOGS.exists()) {
				FOLDER_LOGS.mkdirs();
			}

			if (!FOLDER_ROOT.isDirectory()) {
				throw new NotAFolderException("文件夹被文件占位：" + FOLDER_ROOT.getAbsolutePath());
			}
			//			if (!this.FOLDER_CONF.isDirectory()) { throw new NotAFolderException("文件夹被文件占位：" + this
			//			.FOLDER_CONF.getAbsolutePath()); }
			//			if (!this.FOLDER_DATA.isDirectory()) { throw new NotAFolderException("文件夹被文件占位：" + this
			//			.FOLDER_DATA.getAbsolutePath()); }
			if (!FOLDER_LOGS.isDirectory()) {
				throw new NotAFolderException("文件夹被文件占位：" + FOLDER_LOGS.getAbsolutePath());
			}

			// ==========================================================================================================================
			// 初始化 日志 系统

			// 初始化日志系统
			// 旧的LoggerX拥有一个先行系统，能够将文件创建前的日志写入缓存，等待写入，这里为了避免每次都判断，提升性能，去掉了这个功能

			logger = new LoggerX("Core_Entry");

			logger.info("启动", LoggerX.datetime());

			// ==========================================================================================================================
			// 实例化 系统

			SYSTEMD = new Systemd();

			// ==========================================================================================================================
			// 初始化 系统

			SYSTEMD.init();
			SYSTEMD.boot();

			// ==========================================================================================================================

			logger.info("完成", LoggerX.datetime());
			logger.info("耗时", (System.currentTimeMillis() - BOOTTIME) + "ms");

			// ==========================================================================================================================
			// 启动完成 关闭debug
			// 启动完成 启动Jcq的开关

			DEBUG = false;
			enable = true;

			SYSTEMD.adminInfo("启动完成 " + LoggerX.datetime());

		} catch (Exception exception) {

			enable = false;

			exception.printStackTrace();

			logger.exception(exception);
			SYSTEMD.adminInfo("启动异常 " + exception.getMessage());

		}

		return 0;
	}

	/**
	 * 生命周期函数：JcqApp卸载
	 */
	@Override public int disable() {
		return 0;
	}

	/**
	 * 生命周期函数：CoolQ关闭
	 */
	@Override public int exit() {

		logger.info("系统关闭");

		enable = false;

		try {

			SYSTEMD.save();
			SYSTEMD.shut();

		} catch (Exception exception) {
			logger.exception(exception);
		}

		return 0;
	}

	// ==========================================================================================================================================================
	//
	// 消息处理函数
	//
	// ==========================================================================================================================================================

	/**
	 * 私聊消息处理方法 不应该在此处修改任何内容
	 */
	@Override public int privateMsg(int typeid, int messageid, long userid, String message, int messagefont) {

		try {

			SYSTEMD.doUserMessage(typeid, userid, new MessageUser(typeid, userid, message, messageid, messagefont), messageid, messagefont);

		} catch (Exception exception) {

			long time = System.currentTimeMillis();

			SYSTEMD.adminInfo("[私聊消息异常] 时间序列号 - " + time + "\r\n" + exception.getMessage());

			logger.info("时间序列号", time);
			logger.info("时间", LoggerX.datetime(time));
			logger.info("用户ID", userid);
			logger.info("消息ID", messageid);
			logger.info("消息内容", message);
			logger.info("消息转储", LoggerX.unicode(message));

			logger.exception(exception);

		}

		return IMsg.MSG_IGNORE;
	}

	/**
	 * 组聊消息处理方法 不应该在此处修改任何内容
	 */
	@Override public int discussMsg(int typeid, int messageid, long diszid, long userid, String message, int messagefont) {

		try {

			SYSTEMD.doDiszMessage(diszid, userid, new MessageDisz(diszid, userid, message, messageid, messagefont), messageid, messagefont);

		} catch (Exception exception) {

			long time = System.currentTimeMillis();

			SYSTEMD.adminInfo("[私聊消息异常] 序列号 - " + time);

			logger.info("序列号", time);
			logger.info("时间", LoggerX.datetime(time));
			logger.info("组聊ID", diszid);
			logger.info("用户ID", userid);
			logger.info("消息ID", messageid);
			logger.info("消息内容", message);
			logger.info("消息转储", LoggerX.unicode(message));

			logger.exception(exception);

		}

		return IMsg.MSG_IGNORE;
	}

	/**
	 * 群聊消息处理方法 不应该在此处修改任何内容
	 */
	@Override public int groupMsg(int typeid, int messageid, long gropid, long userid, String anonymous, String message, int messagefont) {

		try {

			SYSTEMD.doGropMessage(gropid, userid, new MessageGrop(gropid, userid, message, messageid, messagefont), messageid, messagefont);

		} catch (Exception exception) {

			long time = System.currentTimeMillis();

			SYSTEMD.adminInfo("[私聊消息异常] 序列号 - " + time + "\r\n" + exception.getMessage());

			logger.info("序列号", time);
			logger.info("时间", LoggerX.datetime(time));
			logger.info("群聊ID", gropid);
			logger.info("用户ID", userid);
			logger.info("消息ID", messageid);
			logger.info("消息内容", message);
			logger.info("消息转储", LoggerX.unicode(message));

			logger.exception(exception);

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
	@Override public int groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) {

		long time = System.currentTimeMillis();

		StringBuilder builder = new StringBuilder();

		builder.append("[成员增加] - 时间序列号" + time + "\r\n");
		builder.append("类型ID：" + typeid + " " + (typeid == 1 ? "自主申请" : "邀请进群") + "\r\n");
		builder.append("群聊ID：" + gropid + "\r\n");
		builder.append("管理ID：" + SYSTEMD.getNickname(operid) + "(" + operid + ")" + "\r\n");
		builder.append("用户ID：" + SYSTEMD.getNickname(userid) + "(" + userid + ")");

		logger.raw("成员增加", builder.toString());

		try {

			SYSTEMD.groupMemberIncrease(typeid, sendtime, gropid, operid, userid);

		} catch (Exception exception) {

			time = System.currentTimeMillis();

			builder.append("\r\n[成员增加异常] 时间序列号 - " + time + "\r\n" + exception.getMessage());

			logger.exception(time, exception);

		} finally {

			SYSTEMD.adminInfo(builder.toString());

		}

		return IMsg.MSG_IGNORE;

	}

	/**
	 * 成员退群处理方法 不应该在此处修改任何内容
	 */
	@Override public int groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) {

		long time = System.currentTimeMillis();

		StringBuilder builder = new StringBuilder();

		builder.append("[成员减少] - 时间序列号" + time + "\r\n");
		builder.append("类型ID：" + typeid + " " + (typeid == 1 ? "自主退群" : "管理踢出") + "\r\n");
		builder.append("群聊ID：" + gropid + "\r\n");
		builder.append("管理ID：" + SYSTEMD.getNickname(operid) + "(" + operid + ")" + "\r\n");
		builder.append("用户ID：" + SYSTEMD.getNickname(userid) + "(" + userid + ")");

		logger.raw("成员减少", builder.toString());

		try {

			SYSTEMD.groupMemberDecrease(typeid, sendtime, gropid, operid, userid);

		} catch (Exception exception) {

			time = System.currentTimeMillis();

			builder.append("\r\n[成员减少异常] 时间序列号 - " + time + "\r\n" + exception.getMessage());

			logger.exception(time, exception);

		} finally {

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
	 */
	@Override public int friendAdd(int typeid, int sendtime, long userid) {
		new Thread(() -> sendFriendAddMessage(userid)).start();
		return 0;
	}

	private void sendFriendAddMessage(long userid) {

		try {
			Thread.sleep(10000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		SYSTEMD.userInfo(userid, "你好，在下人工智障。为了礼貌和避免打扰，本BOT不接入AI聊天功能也不支持AT。使用即表示同意最终用户许可，可由/eula查看。\r\n发送/help获取通用帮助\r" + "\n发送/list获取可用命令列表\r\n私聊、讨论组、群聊可用的命令有所不同");
		SYSTEMD.sendEula(userid);
		SYSTEMD.sendHelp(userid);
	}

	/**
	 * 好友添加请求 不应该在此处修改任何内容
	 */
	@Override public int requestAddFriend(int typeid, int sendtime, long userid, String message, String flag) {

		long time = System.currentTimeMillis();

		StringBuilder builder = new StringBuilder();

		builder.append("[好友请求] 事件序列号 - " + time + "\r\n");
		builder.append("用户：" + SYSTEMD.getNickname(userid) + "(" + userid + ")\r\n");
		builder.append("请求时间：" + sendtime + "\r\n");
		builder.append("验证消息：" + message);

		logger.raw("好友请求", builder.toString());

		SYSTEMD.adminInfo(builder.toString());

		CQ.setFriendAddRequest(flag, IRequest.REQUEST_ADOPT, String.valueOf(userid));

		return 0;
	}

	/**
	 * 群组添加请求 不应该在此处修改任何内容
	 */
	@Override public int requestAddGroup(int typeid, int sendtime, long gropid, long userid, String message, String flag) {

		long time = System.currentTimeMillis();

		StringBuilder builder = new StringBuilder();

		builder.append((typeid == 1 ? "申请入群" : "邀请入群") + "时间序列号 - " + time + "\r\n");
		builder.append("类型ID：" + typeid + " " + (typeid == 1 ? "申请入群" : "邀请入群") + "\r\n");
		builder.append("群聊ID：" + gropid + "\r\n");
		builder.append("用户ID：" + SYSTEMD.getNickname(userid) + "(" + userid + ")\r\n");
		builder.append("请求时间：" + sendtime + "\r\n");
		builder.append("验证消息：" + (message.length() == 0 ? "无" : message));

		logger.raw("群邀请", builder.toString());

		SYSTEMD.adminInfo(builder.toString());

		if (typeid == 2) CQ.setGroupAddRequest(flag, IRequest.REQUEST_GROUP_INVITE, IRequest.REQUEST_ADOPT, null);

		return 0;
	}

	/**
	 * 禁言事件 不应该在此处修改任何内容
	 */
	@Override public int groupBan(int typeid, int sendtime, long gropid, long operid, long userid, long duration) {

		String type = (userid == 0 ? "全体" : "") + (typeid == 1 ? "解禁" : "禁言");

		StringBuilder builder = new StringBuilder();

		builder.append(LoggerX.time() + "\r\n");

		builder.append("群聊ID：" + gropid + "\r\n");
		builder.append("管理ID：" + operid + "(" + SYSTEMD.getNickname(operid) + ")\r\n");

		if (userid != 0) {
			builder.append("用户ID：" + SYSTEMD.getNickname(userid) + "(" + userid + ")\r\n");
		}
		if (typeid != 1) {

			long ss = duration;
			long dd = ss / 86400;
			ss = ss % 86400;
			long hh = ss / 3600;
			ss = ss % 3600;
			long mm = ss / 60;
			ss = ss % 60;

			builder.append("时间：" + duration + " (" + dd + " - " + hh + ":" + mm + ":" + ss + ")");
		}

		logger.raw(type, builder.toString());

		builder.insert(0, "[" + type + "]\r\n");

		SYSTEMD.adminInfo(builder.toString());

		return 0;
	}

	/**
	 * 文件上传事件 不应该在此处修改任何内容
	 */
	@Override public int groupUpload(int typeid, int sendtime, long gropid, long userid, String file) {
		return 0;
	}

	/**
	 * 管理员变动事件 不应该在此处修改任何内容
	 */
	@Override public int groupAdmin(int typeid, int sendtime, long gropid, long userid) {

		String type = typeid == 1 ? "解除" : "任命";

		StringBuilder builder = new StringBuilder();

		builder.append("群聊ID：" + gropid + "\r\n");
		builder.append("用户ID：" + SYSTEMD.getNickname(userid) + "(" + userid + ")");

		logger.raw(type, builder.toString());

		builder.insert(0, "[" + type + "] - " + LoggerX.time() + "\r\n");

		SYSTEMD.adminInfo(builder.toString());

		return 0;
	}

	// ==========================================================================================================================================================
	//
	// 工具函数
	//
	// ==========================================================================================================================================================

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

	// ==========================================================================================================================================================
	//
	// 方法传递 消息
	//
	// ==========================================================================================================================================================

	/**
	 * 获取Systemd对象
	 *
	 * @return Systemd对象
	 */
	public static Systemd getSystemd() {
		return SYSTEMD;
	}

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

	// ==========================================================================================================================================================
	//
	// 方法传递
	//
	// ==========================================================================================================================================================

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

	// ==========================================================================================================================================================
	//
	// 方法传递
	//
	// ==========================================================================================================================================================

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
		return SYSTEMD.getGropNick(gropid, userid);
	}

	// ==========================================================================================================================================================
	//
	// 方法传递
	//
	// ==========================================================================================================================================================

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