package studio.blacktech.coolqbot.furryblack;

import java.io.FileInputStream;
import java.util.Properties;

import com.sobte.cqp.jcq.entity.CQDebug;
import com.sobte.cqp.jcq.entity.ICQVer;
import com.sobte.cqp.jcq.entity.IMsg;
import com.sobte.cqp.jcq.entity.IRequest;
import com.sobte.cqp.jcq.event.JcqApp;
import com.sobte.cqp.jcq.event.JcqAppAbstract;

import studio.blacktech.common.ConfigureX;
import studio.blacktech.common.LoggerX;
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
import studio.blacktech.coolqbot.furryblack.scheduler.Autobot_DailyDDNS;
import studio.blacktech.coolqbot.furryblack.scheduler.Autobot_DailyTask;

public class entry extends JcqAppAbstract implements ICQVer, IMsg, IRequest {

	public static final String AppID = "studio.blacktech.coolqbot.furryblack.entry";

	public static final String PRODUCT_NAME = "FurryBlack - BOT";
	public static final String PRODUCT_PACKAGENANE = entry.AppID;
	public static final String PRODUCT_VERSION = "1.19.0 2019-03-31 (15:00)";

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

			// 启动异步服务
			entry.THREAD.put("DAILYTASK", new Thread(new Autobot_DailyTask()));
			entry.THREAD.put("DAILYDDNS", new Thread(new Autobot_DailyDDNS()));
			entry.THREAD.get("DAILYTASK").start();
			entry.THREAD.get("DAILYDDNS").start();

			// 发送启动通知信息
			JcqApp.CQ.sendPrivateMsg(ConfigureX.ADMIN_UID(), LoggerX.time() + " [FurryBlack] 已启动");
			JcqAppAbstract.enable = true;

		} catch (final Exception exce) {
			// 初始化一旦出错立刻停机
			JcqApp.CQ.sendPrivateMsg(ConfigureX.ADMIN_UID(), "警告初始化失败！");
			JcqApp.CQ.sendPrivateMsg(ConfigureX.ADMIN_UID(), exce.getStackTrace().toString());
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

	/***
	 * 收到私聊时由JcqSDK调用
	 */
	@Override
	public int privateMsg(final int typeid, final int messageid, final long userid, final String message, final int messagefont) {
		try {
			MessageHandler.doUserMessage(typeid, userid, message, messageid, messagefont);
		} catch (final Exception exce) {
			exce.printStackTrace();
			final StringBuilder builder = new StringBuilder();
			builder.append(LoggerX.time());
			builder.append(" [私聊消息异常] - ");
			builder.append(messageid);
			builder.append("\r\n UID:");
			builder.append(userid);
			builder.append("\r\n MSG:");
			builder.append(message);
			builder.append("\r\n StackTrace\r\n:");
			builder.append(exce.getStackTrace().toString());
			JcqApp.CQ.sendPrivateMsg(ConfigureX.ADMIN_UID(), builder.toString());
		}
		return IMsg.MSG_IGNORE;
	}

	/***
	 * 收到讨论组消息时由JcqSDK调用
	 */
	@Override
	public int discussMsg(final int typeid, final int messageid, final long diszid, final long userid, final String message, final int messagefont) {
		try {
			MessageHandler.doDiszMessage(diszid, userid, message, messageid, messagefont);
		} catch (final Exception exce) {
			exce.printStackTrace();
			final StringBuilder builder = new StringBuilder();
			builder.append(LoggerX.time());
			builder.append(" [讨论组消息异常] - ");
			builder.append(messageid);
			builder.append("\r\n UID:");
			builder.append(userid);
			builder.append("\r\n MSG:");
			builder.append(message);
			builder.append("\r\n StackTrace\r\n:");
			builder.append(exce.getStackTrace().toString());
			JcqApp.CQ.sendPrivateMsg(ConfigureX.ADMIN_UID(), builder.toString());
		}
		return IMsg.MSG_IGNORE;
	}

	@Override
	public int groupMsg(final int typeid, final int messageid, final long gropid, final long userid, final String anonymous, final String message, final int messagefont) {
		try {
			MessageHandler.doGropMessage(gropid, userid, message, messageid, messagefont);
		} catch (final Exception exce) {
			exce.printStackTrace();
			final StringBuilder builder = new StringBuilder();
			builder.append(LoggerX.time());
			builder.append(" [群聊消息异常] - ");
			builder.append(messageid);
			builder.append("\r\n UID:");
			builder.append(userid);
			builder.append("\r\n MSG:");
			builder.append(message);
			builder.append("\r\n StackTrace\r\n:");
			builder.append(exce.getStackTrace().toString());
			JcqApp.CQ.sendPrivateMsg(ConfigureX.ADMIN_UID(), builder.toString());
		}
		return IMsg.MSG_IGNORE;
	}

	// ==============================================================================================================================================================

	/***
	 * 收到添加好友请求时由JcqSDK调用
	 */
	@Override
	public int requestAddFriend(final int type, final int time, final long qqid, final String message, final String flag) {
		JcqApp.CQ.sendPrivateMsg(ConfigureX.ADMIN_UID(), LoggerX.time() + " [FurryBlack] 好友申请 " + qqid + " : " + message);
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
			JcqApp.CQ.sendPrivateMsg(ConfigureX.ADMIN_UID(), LoggerX.time() + " [FurryBlack] 加群申请 - 群号:" + fromGroup + " 申请者:" + fromQQ);
		} else if (subtype == 2) {
			JcqApp.CQ.sendPrivateMsg(ConfigureX.ADMIN_UID(), LoggerX.time() + " [FurryBlack] 加群邀请 - 群号:" + fromGroup + " 邀请者:" + fromQQ);
			// 同意邀请进群
			JcqApp.CQ.setGroupAddRequest(responseFlag, IRequest.REQUEST_GROUP_INVITE, IRequest.REQUEST_ADOPT, null);
		}
		return 0;
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
