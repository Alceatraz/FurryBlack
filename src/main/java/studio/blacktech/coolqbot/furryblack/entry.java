package studio.blacktech.coolqbot.furryblack;


import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.jar.JarFile;

import org.meowy.cqp.jcq.entity.CoolQ;
import org.meowy.cqp.jcq.entity.Friend;
import org.meowy.cqp.jcq.entity.Group;
import org.meowy.cqp.jcq.entity.ICQVer;
import org.meowy.cqp.jcq.entity.IMsg;
import org.meowy.cqp.jcq.entity.IRequest;
import org.meowy.cqp.jcq.entity.Member;
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

	@Override
	public String appInfo() {
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
	public final static String VerID = "2.0.0";

	// 启动时间戳
	public final static long BOOTTIME = System.currentTimeMillis();

	// 自身Jar文件
	private static JarFile JAR_INSTANCE;

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

	private static String pictureStorePath;

	private static File FOLDER_ROOT;
	private static File FOLDER_CONF;
	private static File FOLDER_DATA;
	private static File FOLDER_PICS;
	private static File FOLDER_LOGS;
	private static File FILE_LOGGER;


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
	@Override
	public int startup() {
		CQ.logInfo("FurryBlack", "启动");
		return 0;
	}

	/**
	 * 生命周期函数：JcqApp启动
	 */
	@Override
	public int enable() {

		CQ.logInfo("FurryBlack", "启用");

		try {

			// ==========================================================================================================================
			// 获取APP存储目录
			// 继承自JCQ, JcqAbstract包含这个对象

			appDirectory = CQ.getAppDirectory();


			// ==========================================================================================================================
			// 实例化 data/ logs/ 对象

			FOLDER_ROOT = Paths.get(appDirectory, "Core_Entry").toFile();


			FOLDER_CONF = Paths.get(appDirectory, "Core_Entry", "conf").toFile();


			FOLDER_DATA = Paths.get(appDirectory, "Core_Entry", "data").toFile();
			FOLDER_PICS = Paths.get(appDirectory, "Core_Entry", "data", "picture").toFile();


			FOLDER_LOGS = Paths.get(appDirectory, "Core_Entry", "logs").toFile();
			FILE_LOGGER = Paths.get(appDirectory, "Core_Entry", "logs", LoggerX.formatTime("yyyy_MM_dd_HH_mm_ss") + ".txt").toFile();


			pictureStorePath = FOLDER_PICS.getAbsolutePath();


			// ==========================================================================================================================
			// 初始化文件夹

			if (!FOLDER_ROOT.exists()) FOLDER_ROOT.mkdirs();
			if (!FOLDER_CONF.exists()) FOLDER_CONF.mkdirs();
			if (!FOLDER_DATA.exists()) FOLDER_DATA.mkdirs();
			if (!FOLDER_PICS.exists()) FOLDER_PICS.mkdirs();
			if (!FOLDER_LOGS.exists()) FOLDER_LOGS.mkdirs();

			if (!FOLDER_ROOT.isDirectory()) throw new NotAFolderException("文件夹被文件占位：" + FOLDER_ROOT.getAbsolutePath());
			if (!FOLDER_CONF.isDirectory()) throw new NotAFolderException("文件夹被文件占位：" + FOLDER_CONF.getAbsolutePath());
			if (!FOLDER_DATA.isDirectory()) throw new NotAFolderException("文件夹被文件占位：" + FOLDER_DATA.getAbsolutePath());
			if (!FOLDER_PICS.isDirectory()) throw new NotAFolderException("文件夹被文件占位：" + FOLDER_PICS.getAbsolutePath());
			if (!FOLDER_LOGS.isDirectory()) throw new NotAFolderException("文件夹被文件占位：" + FOLDER_LOGS.getAbsolutePath());


			// ==========================================================================================================================
			// 初始化 日志 系统

			LoggerX.init(FILE_LOGGER);

			// 初始化日志系统
			// 旧的LoggerX拥有一个先行系统，能够将文件创建前的日志写入缓存，等待写入，这里为了避免每次都判断，提升性能，去掉了这个功能

			logger = new LoggerX("Core_Entry");
			logger.info("启动", LoggerX.datetime());


			// ==========================================================================================================================
			// 获取APP本身的Jar

			String workingDir = System.getProperty("user.dir");
			String workingJar = AppID + ".jar";
			File jarFile = Paths.get(workingDir, "data", "app", "org.meowy.cqp.jcq", "app", workingJar).toFile();

			// 别忘了你在JCQ的JVM里 你直接获取的user.dir是JCQ的PWD

			logger.full("加载", jarFile.getAbsolutePath());
			JAR_INSTANCE = new JarFile(jarFile);


			// ==========================================================================================================================
			// 实例化 系统

			SYSTEMD = new Systemd();


			// ==========================================================================================================================
			// 初始化 系统

			SYSTEMD.init();


			// ==========================================================================================================================
			// 启动 系统

			SYSTEMD.boot();


			// ==========================================================================================================================

			String bootCoast = System.currentTimeMillis() - BOOTTIME + "ms";

			logger.info("完成", LoggerX.datetime());
			logger.info("耗时", bootCoast);


			// ==========================================================================================================================
			// 启动完成 关闭debug
			// 启动完成 启动Jcq的开关

			DEBUG = false;
			enable = true;

			SYSTEMD.adminInfo("启动完成 耗时：" + bootCoast);

		} catch (Exception exception) {

			enable = false;

			exception.printStackTrace();

			logger.exception(exception);
			SYSTEMD.adminInfo("启动异常 " + exception.toString());

		}

		return 0;

	}


	/**
	 * 生命周期函数：JcqApp卸载
	 */
	@Override
	public int disable() {
		return 0;
	}


	/**
	 * 生命周期函数：CoolQ关闭
	 */
	@Override
	public int exit() {
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
	@Override
	public int privateMsg(int typeid, int messageid, long userid, String message, int messagefont) {

		try {
			SYSTEMD.doUserMessage(new MessageUser(typeid, userid, message, messageid, messagefont));
		} catch (Exception exception) {
			long time = System.currentTimeMillis();
			SYSTEMD.adminInfo("[私聊消息异常] 异常序号 - " + time + "\r\n" + userid + "：" + message + "\r\n" + exception.toString());
			logger.warn("私聊消息异常", userid + "：" + message);
			logger.exception(exception);
		}
		return IMsg.MSG_IGNORE;

	}


	/**
	 * 组聊消息处理方法 不应该在此处修改任何内容
	 */
	@Override
	public int discussMsg(int typeid, int messageid, long diszid, long userid, String message, int messagefont) {

		try {
			SYSTEMD.doDiszMessage(new MessageDisz(diszid, userid, message, messageid, messagefont));
		} catch (Exception exception) {
			long time = System.currentTimeMillis();
			SYSTEMD.adminInfo("[组聊消息异常] 异常序号 - " + time + "\r\n" + diszid + "-" + userid + "：" + message + "\r\n" + exception.toString());
			logger.warn("组聊消息异常", diszid + "-" + userid + "：" + message);
			logger.exception(exception);
		}
		return IMsg.MSG_IGNORE;

	}


	/**
	 * 群聊消息处理方法 不应该在此处修改任何内容
	 */
	@Override
	public int groupMsg(int typeid, int messageid, long gropid, long userid, String anonymous, String message, int messagefont) {

		try {
			SYSTEMD.doGropMessage(new MessageGrop(gropid, userid, message, messageid, messagefont));
		} catch (Exception exception) {
			long time = System.currentTimeMillis();
			SYSTEMD.adminInfo("[群聊消息异常] 异常序号 - " + time + "\r\n" + gropid + "-" + userid + "：" + message + "\r\n" + exception.toString());
			logger.warn("群聊消息异常", gropid + "-" + userid + "：" + message);
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
	@Override
	public int groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) {

		String typeName = typeid == 1 ? "主动申请" : "受邀加群";
		String operNick = SYSTEMD.getNickname(gropid, operid) + "(" + operid + ")";
		String userNick = SYSTEMD.getNickname(gropid, userid) + "(" + userid + ")";

		try {

			SYSTEMD.groupMemberIncrease(typeid, sendtime, gropid, operid, userid);

			logger.info("成员增加", "类型：" + typeName + " 群聊：" + gropid + " 管理：" + operNick + " 用户：" + userNick);
			SYSTEMD.adminInfo("[成员增加] " + LoggerX.datetime() + "\r\n类型：" + typeName + "\r\n群聊：" + gropid + "\r\n管理：" + operNick + "\r\n用户：" + userNick);

		} catch (Exception exception) {

			long time = System.currentTimeMillis();

			logger.exception(time, "成员增加异常", exception);
			SYSTEMD.adminInfo("[成员增加异常] - 异常序号" + time + " 原因：\r\n" + exception.toString() + "类型：" + typeName + " 群聊：" + gropid + " 管理：" + operid + " 用户：" + userid);
		}

		return IMsg.MSG_IGNORE;

	}

	/**
	 * 成员退群处理方法 不应该在此处修改任何内容
	 */
	@Override
	public int groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) {

		String userNick = SYSTEMD.getNickname(gropid, userid) + "(" + userid + ")";

		try {

			SYSTEMD.groupMemberDecrease(typeid, sendtime, gropid, operid, userid);

			if (typeid == 1) {
				logger.info("成员减少", "类型：主动离开 群聊：" + gropid + " 用户：" + userNick);
				SYSTEMD.adminInfo("[成员减少] " + LoggerX.datetime() + "\r\n类型：主动离开\r\n群聊：" + gropid + "\r\n用户：" + userNick);
			} else {
				String operNick = SYSTEMD.getNickname(operid) + "(" + operid + ")";
				logger.info("成员减少", "类型：管理踢出 群聊：" + gropid + " 管理：" + operNick + " 用户：" + userNick);
				SYSTEMD.adminInfo("[成员减少] " + LoggerX.datetime() + "\r\n类型：管理踢出\r\n群聊：" + gropid + "\r\n管理：" + operNick + "\r\n用户：" + userNick);
			}

		} catch (Exception exception) {

			long time = System.currentTimeMillis();

			logger.exception(time, "成员减少异常", exception);
			if (typeid == 1) {
				SYSTEMD.adminInfo("[成员减少异常] - 异常序号" + time + " 原因：\r\n" + exception.toString() + "\r\n类型：主动离开\r\n群聊：" + gropid + "\r\n用户：" + userid);
			} else {
				SYSTEMD.adminInfo("[成员减少异常] - 异常序号" + time + " 原因：\r\n" + exception.toString() + "\r\n类型：管理踢出\r\n群聊：" + gropid + "\r\n管理：" + operid + "\r\n用户：" + userid);
			}

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
	@Override
	public int friendAdd(int typeid, int sendtime, long userid) {

		new Thread(() -> sendFriendAddMessage(userid)).start();
		return 0;

	}

	private void sendFriendAddMessage(long userid) {

		try {
			Thread.sleep(10000L);
		} catch (InterruptedException ignore) {

		}

		SYSTEMD.userInfo(userid, "你好，在下人工智障。为了礼貌和避免打扰，本BOT不接入AI聊天功能也不支持AT。只有BTS官方运行的FurryBlackBot才是“白熊”，由其他人使用本SDK开发运营的帐号与BTS无关。使用即表示同意最终用户许可，可由/eula查看。\r\n发送/help获取通用帮助\r\n发送/list获取可用命令列表\r\n私聊、讨论组、群聊可用的命令有所不同");

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

		String nick = SYSTEMD.getNickname(userid);
		logger.full("添加好友", "时间" + sendtime + " 用户：" + nick + "(" + userid + ")" + " 信息：" + message + " 标志：" + flag);
		SYSTEMD.adminInfo("[收到好友请求] - " + sendtime + "\r\n用户: " + nick + "(" + userid + ")" + "\r\n信息：" + message + "\r\n/admin friend accept " + flag + "\r\n/admin friend refuse " + flag);
		return 0;

	}

	/**
	 * 群组添加请求 不应该在此处修改任何内容
	 */
	@Override
	public int requestAddGroup(int typeid, int sendtime, long gropid, long userid, String message, String flag) {

		String nick = SYSTEMD.getNickname(gropid, userid);

		if (typeid == 1) {
			logger.full("收到加群申请", "时间" + sendtime + " 群聊：" + gropid + " 用户: " + nick + "(" + userid + ")" + " 信息：" + message + " 标志：" + flag);
			SYSTEMD.adminInfo("[收到加群申请] - " + sendtime + "\r\n群聊：" + gropid + "\r\n用户: " + nick + "(" + userid + ")" + "\r\n信息：" + message + "\r\n标志：" + flag);
		} else {
			logger.full("收到入群邀请", "时间" + sendtime + " 群聊：" + gropid + " 用户: " + nick + "(" + userid + ")" + " 信息：" + message + " 标志：" + flag);
			SYSTEMD.adminInfo("[收到入群邀请] - " + sendtime + "\r\n群聊：" + gropid + "\r\n用户: " + nick + "(" + userid + ")" + "\r\n信息：" + message + "\r\n/admin group accept " + flag + "\r\n/admin group refuse " + flag);
		}

		return 0;

	}

	/**
	 * 禁言事件 不应该在此处修改任何内容 交叉逻辑我改了四次 都没有更好的办法
	 */
	@Override
	public int groupBan(int typeid, int sendtime, long gropid, long operid, long userid, long duration) {

		String operNick = SYSTEMD.getNickname(gropid, operid) + "(" + operid + ")";

		if (userid == 0) {

			if (typeid == 2) {
				String time = duration + " (" + LoggerX.duration(duration) + ")";
				logger.full("全体禁言", sendtime + "群聊：" + gropid + " 管理：" + operNick + " 时间：" + time);
				SYSTEMD.adminInfo("[全体禁言] - " + sendtime + "\r\n" + "群聊：" + gropid + "\r\n管理：" + operNick + "\r\n时间：" + time);
			} else {
				logger.full("全体解禁", sendtime + "群聊：" + gropid + " 管理：" + operNick);
				SYSTEMD.adminInfo("[全体解禁] - " + sendtime + "\r\n" + "群聊：" + gropid + "\r\n管理：" + operNick);
			}

		} else {

			String userNick = SYSTEMD.getNickname(userid) + "(" + userid + ")";

			if (typeid == 2) {
				String time = duration + " (" + LoggerX.duration(duration) + ")";
				if (isMyself(userid)) {
					CQ.setGroupLeave(gropid, false);
					logger.full("白熊被禁", sendtime + "群聊：" + gropid + " 管理：" + operNick + " 用户：" + userNick + " 时间：" + time + " 已自动退群");
					SYSTEMD.adminInfo("[白熊被禁] - " + sendtime + "\r\n" + "群聊：" + gropid + "\r\n管理：" + operNick + "\r\n用户：" + userNick + "\r\n时间：" + time + "\r\n已自动退群");
				} else {
					logger.full("成员禁言", sendtime + "群聊：" + gropid + " 管理：" + operNick + " 用户：" + userNick + " 时间：" + time);
					SYSTEMD.adminInfo("[成员禁言] - " + sendtime + "\r\n" + "群聊：" + gropid + "\r\n管理：" + operNick + "\r\n用户：" + userNick + "\r\n时间：" + time);
				}
			} else {
				logger.full("成员解禁", sendtime + "群聊：" + gropid + " 管理：" + operNick + " 用户：" + userNick);
				SYSTEMD.adminInfo("[成员解禁] - " + sendtime + "\r\n" + "群聊：" + gropid + "\r\n管理：" + operNick + "\r\n用户：" + userNick);
			}
		}

		return 0;

	}

	/**
	 * 管理员变动事件 不应该在此处修改任何内容
	 */
	@Override
	public int groupAdmin(int typeid, int sendtime, long gropid, long userid) {

		String type = typeid == 1 ? "解除" : "任命";
		String nick = SYSTEMD.getNickname(gropid, userid);
		logger.full("管理员变动", "群" + gropid + " " + type + " " + nick + "(" + userid + ")");
		SYSTEMD.adminInfo("[管理员变动] - " + sendtime + "\r\n群聊：" + gropid + "\r\n类型：" + type + "\r\n用户：" + nick + "(" + userid + ")");
		return 0;

	}

	/**
	 * 文件上传事件 不应该在此处修改任何内容
	 */
	@Override
	public int groupUpload(int typeid, int sendtime, long gropid, long userid, String file) {
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
	 * 获取自身JarFile
	 *
	 * @return JarFile对象
	 */
	public static JarFile getJar() {
		return JAR_INSTANCE;
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
	 * 获取图片存储路径
	 *
	 * @return 图片存储路径
	 */
	public static File getPictureStore() {
		return FOLDER_PICS;
	}


	/**
	 * 获取图片存储路径
	 *
	 * @return 图片存储路径
	 */
	public static String getPictureStorePath() {
		return pictureStorePath;
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
		return Systemd.isMyself(userid);
	}

	/**
	 * 判断一个ID是否为管理员（JCQ设置的管理，并非qq群管理）
	 *
	 * @param userid 用户ID
	 * @return 是 / 否
	 */
	public static boolean isAdmin(long userid) {
		return Systemd.isAdmin(userid);
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
	 * 无视群Mute 在群聊发消息 并at某人
	 *
	 * @param gropid  群组ID
	 * @param userid  用户ID
	 * @param message 消息
	 */
	public static void gropInfoForce(long gropid, long userid, String message) {
		SYSTEMD.gropInfoForce(gropid, userid, message);
	}


	/**
	 * 无视群Mute 在群聊发消息
	 *
	 * @param gropid  群组ID
	 * @param message 消息
	 */
	public static void gropInfoForce(long gropid, String message) {
		SYSTEMD.gropInfoForce(gropid, message);
	}


	/**
	 * 无视群Mute 在群聊发消息
	 *
	 * @param gropid  群组ID
	 * @param message 消息
	 */
	public static void gropInfoForce(long gropid, String... message) {
		SYSTEMD.gropInfoForce(gropid, message);
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
	 * 对照表 → 群昵称 → 用户昵称
	 *
	 * @param gropid 群组ID
	 * @param userid 用户ID
	 * @return 昵称
	 */
	public static String getNickname(long gropid, long userid) {
		return SYSTEMD.getNickname(gropid, userid);
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


	// ==========================================================================================================================================================
	//
	// 方法传递
	//
	// ==========================================================================================================================================================


	/**
	 * 在 CQ.enable()阶段 此方法会返回0长度数组
	 */
	public static List<Friend> listFriends() {
		return CQ.getFriendList();
	}


	/**
	 * 在 CQ.enable()阶段 此方法会返回0长度数组
	 */
	public static List<Group> listGroups() {
		return CQ.getGroupList();
	}


	/**
	 * 在 CQ.enable()阶段 此方法会返回0长度数组
	 */
	public static List<Member> listMembers(long gropid) {
		return CQ.getGroupMemberList(gropid);
	}

}
