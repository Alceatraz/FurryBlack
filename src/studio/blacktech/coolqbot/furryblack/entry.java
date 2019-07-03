package studio.blacktech.coolqbot.furryblack;

import java.io.File;
import java.nio.file.Paths;

import com.sobte.cqp.jcq.entity.CQDebug;
import com.sobte.cqp.jcq.entity.ICQVer;
import com.sobte.cqp.jcq.entity.IMsg;
import com.sobte.cqp.jcq.entity.IRequest;
import com.sobte.cqp.jcq.event.JcqApp;
import com.sobte.cqp.jcq.event.JcqAppAbstract;

import studio.blacktech.coolqbot.furryblack.Module_DDNSAPI.DDNSapiDelegate;
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
	public static final String AppID = "studio.blacktech.coolqbot.furryblack.entry";
	// 绝对不能修改 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

	public static final String VerID = "7.0 2019-07-02 (00:00)";

	public static final long BOOTTIME = System.currentTimeMillis();

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================

	private static File FOLDER_CONF;
	private static File FOLDER_DATA;
	private static File FILE_CONFIG;
	private static File FOLDER_MODULE_CONF;
	private static File FOLDER_MODULE_DATA;

	private static Module_Systemd SYSTEMD;
	private static Module_Nickmap NICKMAP;
	private static Module_Message MESSAGE;
	private static Module_DDNSAPI DDNSAPI;

	public static void main(final String[] args) {
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

			logger.mini("[CORE] 启动", LoggerX.datetime());

			// ==========================================================================================================================

			JcqAppAbstract.enable = true;
			JcqAppAbstract.appDirectory = JcqApp.CQ.getAppDirectory();

			// ==========================================================================================================================

			entry.FOLDER_CONF = Paths.get(JcqAppAbstract.appDirectory, "conf").toFile();
			entry.FOLDER_DATA = Paths.get(JcqAppAbstract.appDirectory, "data").toFile();
			entry.FILE_CONFIG = Paths.get(entry.FOLDER_CONF.getAbsolutePath(), "config.properties").toFile();
			entry.FOLDER_MODULE_CONF = Paths.get(entry.FOLDER_CONF.getAbsolutePath(), "module").toFile();
			entry.FOLDER_MODULE_DATA = Paths.get(entry.FOLDER_DATA.getAbsolutePath(), "module").toFile();

			// ==========================================================================================================================

			logger.full("[CORE] 工作目录", appDirectory);
			logger.full("[CORE] 配置文件目录", FOLDER_CONF.getPath());
			logger.full("[CORE] 数据文件目录", FOLDER_DATA.getPath());
			logger.full("[CORE] 配置文件", FILE_CONFIG.getPath());
			logger.full("[CORE] 模块配置文件目录", FOLDER_MODULE_CONF.getPath());
			logger.full("[CORE] 模块数据文件目录", FOLDER_MODULE_DATA.getPath());

			// ==========================================================================================================================

			if (!entry.FOLDER_CONF.exists()) {
				logger.seek("[CORE] 创建目录", FOLDER_CONF.getName());
				entry.FOLDER_CONF.mkdirs();
			}

			if (!entry.FOLDER_DATA.exists()) {
				logger.seek("[CORE] 创建目录", FOLDER_DATA.getName());
				entry.FOLDER_DATA.mkdirs();
			}

			if (!entry.FOLDER_MODULE_CONF.exists()) {
				logger.seek("[CORE] 创建目录", FOLDER_MODULE_CONF.getName());
				entry.FOLDER_MODULE_CONF.mkdirs();
			}

			if (!entry.FOLDER_MODULE_DATA.exists()) {
				logger.seek("[CORE] 创建目录", FOLDER_MODULE_DATA.getName());
				entry.FOLDER_MODULE_DATA.mkdirs();
			}


			// ==========================================================================================================================

			if (!entry.FOLDER_CONF.isDirectory()) { throw new NotAFolderException("配置文件夹被文件占位：" + entry.FOLDER_CONF.getAbsolutePath()); }
			if (!entry.FOLDER_DATA.isDirectory()) { throw new NotAFolderException("配置文件夹被文件占位：" + entry.FOLDER_DATA.getAbsolutePath()); }

			if (!entry.FOLDER_MODULE_CONF.isDirectory()) { throw new NotAFolderException("配置文件夹被文件占位：" + entry.FOLDER_MODULE_CONF.getAbsolutePath()); }
			if (!entry.FOLDER_MODULE_DATA.isDirectory()) { throw new NotAFolderException("配置文件夹被文件占位：" + entry.FOLDER_MODULE_DATA.getAbsolutePath()); }

			// ==========================================================================================================================

			entry.MESSAGE = new Module_Message();
			entry.NICKMAP = new Module_Nickmap();
			entry.DDNSAPI = new Module_DDNSAPI();
			entry.SYSTEMD = new Module_Systemd();

			MESSAGE.init(logger);
			NICKMAP.init(logger);
			DDNSAPI.init(logger);
			SYSTEMD.init(logger);

			MESSAGE.boot(logger);
			NICKMAP.boot(logger);
			DDNSAPI.boot(logger);
			SYSTEMD.boot(logger);

			// ==========================================================================================================================

			logger.mini("[CORE] 启动完成", "耗时" + (System.currentTimeMillis() - BOOTTIME) + "ms");

			// ==========================================================================================================================

			System.out.println(logger.make(3));

			// ==========================================================================================================================

			getMessage().adminInfo(logger.make(0));

		} catch (final Exception exce) {
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
		JcqAppAbstract.enable = false;
		LoggerX logger = new LoggerX();
		try {
			DDNSAPI.shut(logger);
			SYSTEMD.shut(logger);
		} catch (Exception exception) {
			exception.printStackTrace();
			getMessage().adminInfo(logger.make(3));
		}
		return 0;
	}

	/***
	 * 关闭时调用
	 */
	@Override
	public int exit() {
		JcqAppAbstract.enable = false;
		LoggerX logger = new LoggerX();
		try {
			DDNSAPI.shut(logger);
			SYSTEMD.shut(logger);
		} catch (Exception exception) {
			exception.printStackTrace();
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
	public int privateMsg(final int typeid, final int messageid, final long userid, final String message, final int messagefont) {
		try {
			entry.SYSTEMD.doUserMessage(typeid, userid, new MessageUser(typeid, userid, message, messageid, messagefont), messageid, messagefont);
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
			builder.append("\r\n 原因:");
			builder.append(exce.getMessage());
			getMessage().adminInfo(builder.toString());
		}
		return IMsg.MSG_IGNORE;
	}

	@Override
	public int discussMsg(final int typeid, final int messageid, final long diszid, final long userid, final String message, final int messagefont) {
		try {
			entry.SYSTEMD.doDiszMessage(diszid, userid, new MessageDisz(diszid, userid, message, messageid, messagefont), messageid, messagefont);
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
			builder.append("\r\n 原因:");
			builder.append(exce.getMessage());
			getMessage().adminInfo(builder.toString());
		}
		return IMsg.MSG_IGNORE;
	}

	@Override
	public int groupMsg(final int typeid, final int messageid, final long gropid, final long userid, final String anonymous, final String message, final int messagefont) {
		try {
			entry.SYSTEMD.doGropMessage(gropid, userid, new MessageGrop(gropid, userid, message, messageid, messagefont), messageid, messagefont);
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
			builder.append("\r\n 原因:");
			builder.append(exce.getMessage());
			getMessage().adminInfo(builder.toString());
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
		} catch (final Exception exce) {
			exce.printStackTrace();
			final StringBuilder builder = new StringBuilder();
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
		} catch (final Exception exce) {
			exce.printStackTrace();
			final StringBuilder builder = new StringBuilder();
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
	public int requestAddFriend(final int type, final int time, final long qqid, final String message, final String flag) {
		JcqApp.CQ.setFriendAddRequest(flag, IRequest.REQUEST_ADOPT, String.valueOf(JcqApp.CQ.getStrangerInfo(qqid).getQqId()));
		getMessage().adminInfo(LoggerX.time() + "[FurryBlack] 好友申请 " + qqid + " : " + message);
		return 0;
	}

	@Override
	public int friendAdd(final int typeid, final int sendTime, final long userid) {
		JcqApp.CQ.sendPrivateMsg(userid, "你好，在下人工智障");
		getMessage().sendHelp(userid);
		getMessage().sendEula(userid);
		return 0;
	}

	@Override
	public int requestAddGroup(final int subtype, final int sendTime, final long fromGroup, final long fromQQ, final String msg, final String responseFlag) {
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
	public int groupUpload(final int subType, final int sendTime, final long fromGroup, final long fromQQ, final String file) {
		return 0;
	}

	@Override
	public int groupAdmin(final int subtype, final int sendTime, final long fromGroup, final long beingOperateQQ) {
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

	public static File FOLDER_MODULE_CONF() {
		return entry.FOLDER_MODULE_CONF;
	}

	public static File FOLDER_MODULE_DATA() {
		return entry.FOLDER_MODULE_DATA;
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

}
