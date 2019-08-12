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
import studio.blacktech.coolqbot.furryblack.common.NotAFolderException;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;

public class entry extends JcqAppAbstract implements ICQVer, IMsg, IRequest {

	// ==========================================================================================================================================================
	//
	// 公共恒量
	//
	// ==========================================================================================================================================================

	// 绝对不能修改 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	public final static String AppID = "studio.blacktech.coolqbot.furryblack.entry";
	// 绝对不能修改 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

	public final static String VerID = "7.5 2019-08-12 (09:30)";

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

	public static void main(String[] args) {
		JcqApp.CQ = new CQDebug();
		entry demo = new entry();
		demo.startup();
		demo.enable();

		demo.privateMsg(1, 1, 1752384244, "/admin", 1);

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

	/***
	 * 启动时调用
	 */
	@Override
	public int startup() {
		return 0;
	}

	/***
	 * 加载时调用
	 */
	@Override
	public int enable() {

		LoggerX logger = new LoggerX();

		try {

			logger.rawmini(LoggerX.datetime());

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

	/***
	 * 卸载时调用
	 */
	@Override
	public int disable() {
		LoggerX logger = new LoggerX();
		try {
			logger.mini(LoggerX.datetime());
			logger.mini("[CORE] 关闭");
			JcqAppAbstract.enable = false;
			DDNSAPI.shut(logger);
			SYSTEMD.shut(logger);
		} catch (Exception exce) {
			exce.printStackTrace();
			JcqAppAbstract.enable = false;
			getMessage().adminInfo(logger.make(3));
		}
		return 0;
	}

	/***
	 * 关闭时调用
	 */
	@Override
	public int exit() {
		LoggerX logger = new LoggerX();
		try {
			logger.mini(LoggerX.datetime());
			logger.mini("[CORE] 关闭");
			JcqAppAbstract.enable = false;
			DDNSAPI.shut(logger);
			SYSTEMD.shut(logger);
		} catch (Exception exce) {
			exce.printStackTrace();
			JcqAppAbstract.enable = false;
			getMessage().adminInfo(logger.make(3));
		}
		return 0;
	}

	// ==========================================================================================================================================================
	//
	// 消息处理函数
	//
	// ==========================================================================================================================================================

	@Override
	public int privateMsg(int typeid, int messageid, long userid, String message, int messagefont) {
		try {
			entry.SYSTEMD.doUserMessage(typeid, userid, new MessageUser(typeid, userid, message, messageid, messagefont), messageid, messagefont);
		} catch (Exception exce) {
			exce.printStackTrace();
			StringBuilder builder = new StringBuilder();
			builder.append(LoggerX.time());
			builder.append(" [私聊消息异常] - ");
			builder.append(messageid);
			builder.append("\r\n 用户:");
			builder.append(userid);
			builder.append("\r\n 消息:");
			builder.append(message);
			builder.append("\r\n 消息:");
			builder.append(message.length());
			builder.append("\r\n 原因:");
			builder.append(exce.getMessage());
			System.out.println(builder.toString());
			getMessage().adminInfo(builder.toString());
		}
		return IMsg.MSG_IGNORE;
	}

	@Override
	public int discussMsg(int typeid, int messageid, long diszid, long userid, String message, int messagefont) {
		try {
			entry.SYSTEMD.doDiszMessage(diszid, userid, new MessageDisz(diszid, userid, message, messageid, messagefont), messageid, messagefont);
		} catch (Exception exce) {
			exce.printStackTrace();
			StringBuilder builder = new StringBuilder();
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
			builder.append("\r\n 原因:");
			builder.append(exce.getMessage());
			System.out.println(builder.toString());
			getMessage().adminInfo(builder.toString());
		}
		return IMsg.MSG_IGNORE;
	}

	@Override
	public int groupMsg(int typeid, int messageid, long gropid, long userid, String anonymous, String message, int messagefont) {
		if (userid == 1000000) {
			entry.getMessage().adminInfo("系统消息 - （" + gropid + "）" + message + " " + anonymous);
		} else {
			try {
				entry.SYSTEMD.doGropMessage(gropid, userid, new MessageGrop(gropid, userid, message, messageid, messagefont), messageid, messagefont);
			} catch (Exception exce) {
				exce.printStackTrace();
				StringBuilder builder = new StringBuilder();
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
				builder.append("\r\n 原因:");
				builder.append(exce.getMessage());
				System.out.println(builder.toString());
				getMessage().adminInfo(builder.toString());
			}
		}
		return IMsg.MSG_IGNORE;
	}

	// ==========================================================================================================================================================
	//
	// 群成员变动函数
	//
	// ==========================================================================================================================================================

	@Override
	public int groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) {
		try {
			entry.SYSTEMD.groupMemberIncrease(typeid, sendtime, gropid, operid, userid);
		} catch (Exception exce) {
			exce.printStackTrace();
			StringBuilder builder = new StringBuilder();
			builder.append(LoggerX.time());
			builder.append(" [成员增加异常]");
			builder.append(sendtime);
			builder.append("\r\n 原因:");
			builder.append(typeid == 1 ? "自主申请" : "邀请进群");
			builder.append("\r\n 群号:");
			builder.append(gropid);
			builder.append("\r\n 管理:");
			builder.append(operid);
			builder.append("\r\n 成员:");
			builder.append(userid);
			builder.append("\r\n 原因:");
			builder.append(exce.getMessage());
			getMessage().adminInfo(builder.toString());
		}
		return IMsg.MSG_IGNORE;
	}

	@Override
	public int groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) {
		try {
			entry.SYSTEMD.groupMemberDecrease(typeid, sendtime, gropid, operid, userid);
		} catch (Exception exce) {
			exce.printStackTrace();
			StringBuilder builder = new StringBuilder();
			builder.append(LoggerX.time());
			builder.append(" [成员减少异常]");
			builder.append(sendtime);
			builder.append("\r\n 原因:");
			builder.append(typeid == 1 ? "自主申请" : "邀请进群");
			builder.append("\r\n 群号:");
			builder.append(gropid);
			builder.append("\r\n 管理:");
			builder.append(operid);
			builder.append("\r\n 成员:");
			builder.append(userid);
			builder.append("\r\n 原因:");
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

	/***
	 * 同意好友邀请
	 */
	@Override
	public int requestAddFriend(int type, int time, long qqid, String message, String flag) {
		JcqApp.CQ.setFriendAddRequest(flag, IRequest.REQUEST_ADOPT, String.valueOf(JcqApp.CQ.getStrangerInfo(qqid).getQqId()));
		getMessage().adminInfo(LoggerX.time() + "[FurryBlack] 好友申请 " + qqid + " : " + message);
		return 0;
	}

	@Override
	public int friendAdd(int typeid, int sendTime, long userid) {
		JcqApp.CQ.sendPrivateMsg(userid, "你好，在下人工智障");
		JcqApp.CQ.sendPrivateMsg(userid, "使用即表示同意最终用户许可，由/eula查看");
		JcqApp.CQ.sendPrivateMsg(userid, "为了礼貌和避免打扰，本BOT不接入聊天功能");
		JcqApp.CQ.sendPrivateMsg(userid, "输入/help获取帮助");
		getMessage().sendHelp(userid);
		getMessage().sendEula(userid);
		return 0;
	}

	@Override
	public int requestAddGroup(int subtype, int sendTime, long fromGroup, long fromQQ, String msg, String responseFlag) {
		// sub type
		// 1 -> 作为管理时收到了用户加入申请
		// 2 -> 收到好友邀请加入某群的邀请
		if (subtype == 1) {
			getMessage().adminInfo(LoggerX.time() + "[FurryBlack] 加群申请 - 群号:" + fromGroup + " 申请者:" + fromQQ);
		} else if (subtype == 2) {
			getMessage().adminInfo(LoggerX.time() + "[FurryBlack] 收到邀请 - 群号:" + fromGroup + " 邀请者:" + fromQQ);
			// 同意邀请进群
			JcqApp.CQ.setGroupAddRequest(responseFlag, IRequest.REQUEST_GROUP_INVITE, IRequest.REQUEST_ADOPT, null);
		}
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

	public static File FOLDER_CONF() {
		return entry.FOLDER_CONF;
	}

	public static File FOLDER_DATA() {
		return entry.FOLDER_DATA;
	}

	public static SystemdDelegate getSystemd() {
		return SYSTEMD.getDelegate();
	}

	public static DDNSapiDelegate getDDNSAPI() {
		return DDNSAPI.getDelegate();
	}

	public static MessageDelegate getMessage() {
		return MESSAGE.getDelegate();
	}

	public static NicknameDelegate getNickmap() {
		return NICKMAP.getDelegate();
	}

	public static boolean switchDEBUG() {
		DEBUG = !DEBUG;
		return DEBUG;
	}

	public static boolean DEBUG() {
		return DEBUG;
	}

}
