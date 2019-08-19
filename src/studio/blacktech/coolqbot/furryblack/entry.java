package studio.blacktech.coolqbot.furryblack;

import java.io.File;
import java.nio.file.Paths;

import com.sobte.cqp.jcq.entity.CQDebug;
import com.sobte.cqp.jcq.entity.ICQVer;
import com.sobte.cqp.jcq.entity.IMsg;
import com.sobte.cqp.jcq.entity.IRequest;
import com.sobte.cqp.jcq.event.JcqApp;
import com.sobte.cqp.jcq.event.JcqAppAbstract;

import studio.blacktech.coolqbot.furryblack.Module_DDNS.DDNSapiDelegate;
import studio.blacktech.coolqbot.furryblack.Module_Message.MessageDelegate;
import studio.blacktech.coolqbot.furryblack.Module_Nickmap.NicknameDelegate;
import studio.blacktech.coolqbot.furryblack.Module_Systemd.SystemdDelegate;
import studio.blacktech.coolqbot.furryblack.common.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.LoggerXDummy;
import studio.blacktech.coolqbot.furryblack.common.NotAFolderException;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;

/**
 * JcqApp的入口类文件
 *
 * Jcq将会调用约定的生命周期函数
 *
 * @author Alceatraz Warprays
 *
 */
public class entry extends JcqAppAbstract implements ICQVer, IMsg, IRequest {

	// ==========================================================================================================================================================
	//
	// 公共恒量
	//
	// ==========================================================================================================================================================

	// 绝对不能修改 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	public final static String AppID = "studio.blacktech.coolqbot.furryblack.entry";
	// 绝对不能修改 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

	public final static String VerID = "7.11 2019-08-19 (20:30)";

	public final static long BOOTTIME = System.currentTimeMillis();

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================

	private static boolean DEBUG = false;

	private static File FOLDER_CONF;
	private static File FOLDER_DATA;

	private static Module_Systemd SYSTEMD;
	private static Module_Nickmap NICKMAP;
	private static Module_Message MESSAGE;
	private static Module_DDNS DDNSAPI;

	/**
	 * JcqDebug 模式从此处执行
	 *
	 * @param args 启动参数 不应传入任何参数
	 */
	public static void main(String[] args) {
		JcqApp.CQ = new CQDebug();
		entry demo = new entry();
		demo.startup();
		demo.enable();
		demo.disable();
		demo.exit();

	}

	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================

	// 绝对不能修改 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	@Override
	public String appInfo() {
		return ICQVer.CQAPIVER + "," + entry.AppID;
	}
	// 绝对不能修改 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

	/**
	 * 生命周期函数：CoolQ启动
	 */
	@Override
	public int startup() {
		return 0;
	}

	/**
	 * 生命周期函数：JcqApp启动
	 */
	@Override
	public int enable() {

		LoggerX logger = new LoggerX();

		try {

			logger.rawmini("[CORE] - " + LoggerX.datetime());

			// ==========================================================================================================================

			JcqAppAbstract.enable = true;
			JcqAppAbstract.appDirectory = JcqApp.CQ.getAppDirectory();

			// ==========================================================================================================================

			entry.FOLDER_CONF = Paths.get(JcqAppAbstract.appDirectory, "conf").toFile();
			entry.FOLDER_DATA = Paths.get(JcqAppAbstract.appDirectory, "data").toFile();

			// ==========================================================================================================================

			logger.full("[CORE] 工作目录", appDirectory);
			logger.full("[CORE] 配置文件目录", FOLDER_CONF.getPath());
			logger.full("[CORE] 数据文件目录", FOLDER_DATA.getPath());

			// ==========================================================================================================================

			if (!entry.FOLDER_CONF.exists()) {
				logger.seek("[CORE] 创建目录", FOLDER_CONF.getName());
				entry.FOLDER_CONF.mkdirs();
			}

			if (!entry.FOLDER_DATA.exists()) {
				logger.seek("[CORE] 创建目录", FOLDER_DATA.getName());
				entry.FOLDER_DATA.mkdirs();
			}

			// ==========================================================================================================================

			if (!entry.FOLDER_CONF.isDirectory()) { throw new NotAFolderException("配置文件夹被文件占位：" + entry.FOLDER_CONF.getAbsolutePath()); }
			if (!entry.FOLDER_DATA.isDirectory()) { throw new NotAFolderException("配置文件夹被文件占位：" + entry.FOLDER_DATA.getAbsolutePath()); }

			// ==========================================================================================================================

			entry.DDNSAPI = new Module_DDNS();
			entry.MESSAGE = new Module_Message();
			entry.NICKMAP = new Module_Nickmap();
			entry.SYSTEMD = new Module_Systemd();

			DDNSAPI.init(logger);
			MESSAGE.init(logger);
			NICKMAP.init(logger);
			SYSTEMD.init(logger);

			DDNSAPI.boot(logger);
			MESSAGE.boot(logger);
			NICKMAP.boot(logger);
			SYSTEMD.boot(logger);

			// ==========================================================================================================================

			logger.mini("[CORE] 启动", "耗时" + (System.currentTimeMillis() - BOOTTIME) + "ms");

			// ==========================================================================================================================

			JcqApp.CQ.logInfo("FurryBlack", logger.make(3));
			getMessage().adminInfo(logger.make(0));

			// ==========================================================================================================================

		} catch (Exception exce) {
			exce.printStackTrace();
			JcqAppAbstract.enable = false;
			getMessage().adminInfo(logger.make(3));
		}
		return 0;
	}

	/**
	 * 生命周期函数：JcqApp卸载
	 */
	@Override
	public int disable() {
		LoggerXDummy logger = new LoggerXDummy();
		if (JcqAppAbstract.enable) {
			JcqAppAbstract.enable = false;
			try {
				logger.mini(LoggerX.datetime());
				logger.mini("[CORE] 关闭");
				DDNSAPI.shut(logger);
				SYSTEMD.shut(logger);
			} catch (Exception exce) {
				exce.printStackTrace();
			}
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
		} catch (Exception exce) {
			exce.printStackTrace();
			StringBuilder builder = new StringBuilder();
			builder.append("[私聊消息异常]");
			builder.append(LoggerX.time());
			builder.append("\r\n序号：");
			builder.append(messageid);
			builder.append("\r\n用户：");
			builder.append(userid);
			builder.append("\r\n消息:");
			builder.append(message);
			builder.append("\r\n长度：");
			builder.append(message.length());
			builder.append("\r\n原因：");
			builder.append(exce.getMessage());
			System.out.println(builder.toString());
			getMessage().adminInfo(builder.toString());
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
		} catch (Exception exce) {
			exce.printStackTrace();
			StringBuilder builder = new StringBuilder();
			builder.append("[组聊消息异常]");
			builder.append(LoggerX.time());
			builder.append("\r\n序号：");
			builder.append(messageid);
			builder.append("\r\n组号：");
			builder.append(diszid);
			builder.append("\r\n用户：");
			builder.append(userid);
			builder.append("\r\n消息:");
			builder.append(message);
			builder.append("\r\n长度：");
			builder.append(message.length());
			builder.append("\r\n原因：");
			builder.append(exce.getMessage());
			System.out.println(builder.toString());
			getMessage().adminInfo(builder.toString());
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
		} catch (Exception exce) {
			exce.printStackTrace();
			StringBuilder builder = new StringBuilder();
			builder.append("[群聊消息异常]");
			builder.append(LoggerX.time());
			builder.append("\r\n序号：");
			builder.append(messageid);
			builder.append("\r\n群号：");
			builder.append(gropid);
			builder.append("\r\n用户：");
			builder.append(userid);
			builder.append("\r\n消息:");
			builder.append(message);
			builder.append("\r\n长度：");
			builder.append(message.length());
			builder.append("\r\n原因：");
			builder.append(exce.getMessage());
			System.out.println(builder.toString());
			getMessage().adminInfo(builder.toString());
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
		try {
			entry.SYSTEMD.groupMemberIncrease(typeid, sendtime, gropid, operid, userid);
		} catch (Exception exce) {
			exce.printStackTrace();
			StringBuilder builder = new StringBuilder();
			builder.append("[成员增加异常]");
			builder.append(LoggerX.time());
			builder.append("\r\n时间:");
			builder.append(sendtime);
			builder.append("\r\n类型:");
			builder.append(typeid == 1 ? "自主申请" : "邀请进群");
			builder.append("\r\n群号:");
			builder.append(gropid);
			builder.append("\r\n管理:");
			builder.append(operid);
			builder.append("\r\n成员:");
			builder.append(userid);
			builder.append("\r\n原因:");
			builder.append(exce.getMessage());
			getMessage().adminInfo(builder.toString());
		}
		return IMsg.MSG_IGNORE;
	}

	/**
	 * 成员退群处理方法 不应该在此处修改任何内容
	 */
	@Override
	public int groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) {
		try {
			entry.SYSTEMD.groupMemberDecrease(typeid, sendtime, gropid, operid, userid);
		} catch (Exception exce) {
			exce.printStackTrace();
			StringBuilder builder = new StringBuilder();
			builder.append("[成员减少异常]");
			builder.append(LoggerX.time());
			builder.append("\r\n时间:");
			builder.append(sendtime);
			builder.append("\r\n类型:");
			builder.append(typeid == 1 ? "自主退群" : "管理踢出");
			builder.append("\r\n群号:");
			builder.append(gropid);
			builder.append("\r\n管理:");
			builder.append(operid);
			builder.append("\r\n成员:");
			builder.append(userid);
			builder.append("\r\n原因:");
			builder.append(exce.getMessage());
			getMessage().adminInfo(builder.toString());
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
	public int friendAdd(int typeid, int sendTime, long userid) {
		getMessage().userInfo(userid, "你好，在下人工智障。为了礼貌和避免打扰，本BOT不接入聊天功能也不支持@。使用即表示同意最终用户许可。\r\n输入/help获取通用帮助\r\n输入/list获取可用命令列表\r\n私聊、讨论组、群聊可用的命令有所不同");
		getMessage().sendEula(userid);
		getMessage().sendHelp(userid);
		getMessage().sendListUser(userid);
		getMessage().sendListDisz(userid);
		getMessage().sendListGrop(userid);
		return 0;
	}

	/**
	 * 好友添加请求
	 */
	@Override
	public int requestAddFriend(int typeid, int sendtime, long userid, String message, String flag) {

		StringBuilder builder = new StringBuilder();
		builder.append("[添加好友请求]");
		builder.append(LoggerX.time());
		builder.append("\r\n时间:");
		builder.append(sendtime);
		builder.append("\r\n用户:");
		builder.append(entry.getNickmap().getNickname(userid));
		builder.append("(");
		builder.append(userid);
		builder.append(")\r\n消息:");
		builder.append(message);
		getMessage().adminInfo(builder.toString());

		JcqApp.CQ.setFriendAddRequest(flag, IRequest.REQUEST_ADOPT, String.valueOf(userid));

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
			builder.append("[申请入群]");
			builder.append(LoggerX.time());
			builder.append("\r\n时间:");
			builder.append(sendtime);
			builder.append("\r\n群号:");
			builder.append(gropid);
			builder.append("\r\n用户:");
			builder.append(entry.getNickmap().getNickname(userid));
			builder.append("(");
			builder.append(userid);
			builder.append(")\r\n消息:");
			builder.append(message);
			break;

		case 2:
			builder.append("[邀请加群]");
			builder.append(LoggerX.time());
			builder.append("\r\n时间:");
			builder.append(sendtime);
			builder.append("\r\n群号:");
			builder.append(gropid);
			builder.append("\r\n用户:");
			builder.append(entry.getNickmap().getNickname(userid));
			builder.append("(");
			builder.append(userid);
			builder.append(")\r\n消息:");
			builder.append(message);

			JcqApp.CQ.setGroupAddRequest(flag, IRequest.REQUEST_GROUP_INVITE, IRequest.REQUEST_ADOPT, null);

			break;
		}

		getMessage().adminInfo(builder.toString());
		return 0;
	}

	@Override
	public int groupUpload(int subType, int sendTime, long fromGroup, long fromQQ, String file) {
		return 0;
	}

	@Override
	public int groupAdmin(int subtype, int sendTime, long fromGroup, long beingOperateQQ) {
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
	 * 获取Systemd的代理对象
	 *
	 * @return Systemd的代理对象
	 */
	public static SystemdDelegate getSystemd() {
		return SYSTEMD.getDelegate();
	}

	/**
	 * 获取DDNSApi的代理对象
	 *
	 * @return DDNSApi的代理对象
	 */
	public static DDNSapiDelegate getDDNSAPI() {
		return DDNSAPI.getDelegate();
	}

	/**
	 * 获取DDNSApi的代理对象
	 *
	 * @return DDNSApi的代理对象
	 */
	public static MessageDelegate getMessage() {
		return MESSAGE.getDelegate();
	}

	/**
	 * 获取DDNSApi的代理对象
	 *
	 * @return DDNSApi的代理对象
	 */
	public static NicknameDelegate getNickmap() {
		return NICKMAP.getDelegate();
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

}
