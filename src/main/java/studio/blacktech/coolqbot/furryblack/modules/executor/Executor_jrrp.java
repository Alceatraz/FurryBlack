package studio.blacktech.coolqbot.furryblack.modules.executor;


import studio.blacktech.common.security.RandomTool;
import studio.blacktech.coolqbot.furryblack.common.annotation.ModuleExecutorComponent;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;
import studio.blacktech.coolqbot.furryblack.entry;

import java.util.Date;
import java.util.HashMap;


@ModuleExecutorComponent
public class Executor_jrrp extends ModuleExecutor {

	private static final long serialVersionUID = 1L;

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	private static final String MODULE_PACKAGENAME = "Executor_JRRP";
	private static final String MODULE_COMMANDNAME = "jrrp";
	private static final String MODULE_DISPLAYNAME = "今日运气";
	private static final String MODULE_DESCRIPTION = "查看今天的运气值";
	private static final String MODULE_VERSION = "1.3.0";
	private static final String[] MODULE_USAGE = new String[] {
			"/jrrp - 查看今日运气"
	};
	private static final String[] MODULE_PRIVACY_STORED = new String[] {};
	private static final String[] MODULE_PRIVACY_CACHED = new String[] {
			"用户与运气对应表 - 每日UTC+8 00:00 清空"
	};
	private static final String[] MODULE_PRIVACY_OBTAIN = new String[] {
			"获取命令发送人"
	};

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================


	private HashMap<Long, Integer> JRRP;

	private Thread thread;


	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================


	public Executor_jrrp() throws Exception {
		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}


	@Override
	public boolean init() {

		JRRP = new HashMap<>();
		ENABLE_USER = true;
		ENABLE_DISZ = true;
		ENABLE_GROP = true;
		return true;

	}

	@Override
	public boolean boot() {

		logger.info("启动工作线程");
		thread = new Thread(new Worker());
		thread.start();
		return true;

	}


	@Override
	public boolean save() {

		return true;

	}


	@Override
	public boolean shut() throws Exception {
		logger.info("终止工作线程");
		thread.interrupt();
		thread.join();
		logger.info("工作线程已终止");
		return true;
	}

	@Override
	public String[] exec(Message message) {
		return new String[] {
				"此模块无可用命令"
		};
	}


	@Override
	public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) {

	}

	@Override
	public void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) {

	}


	@Override
	public boolean doUserMessage(MessageUser message) {

		long userid = message.getUserID();


		entry.userInfo(userid, "今天的运气是 " + getUserJRRP(userid) + "% !!!");

		return true;
	}


	@Override
	public boolean doDiszMessage(MessageDisz message) {

		long diszid = message.getDiszID();
		long userid = message.getUserID();

		entry.diszInfo(diszid, userid, "今天的运气是 " + getUserJRRP(userid) + "% !!!");

		return true;
	}


	@Override
	public boolean doGropMessage(MessageGrop message) {

		long gropid = message.getGropID();
		long userid = message.getUserID();

		entry.gropInfo(gropid, userid, "今天的运气是 " + getUserJRRP(userid) + "% !!!");

		return true;
	}


	private int getUserJRRP(long userid) {
		if (!JRRP.containsKey(userid)) { JRRP.put(userid, RandomTool.nextInt(100)); }
		return JRRP.get(userid);
	}


	@Override
	public String[] generateReport(Message message) {
		return new String[0];
	}


	class Worker implements Runnable {


		@Override
		public void run() {

			long time;
			Date date;

			do {

				try {
					while (true) {
						date = new Date();
						time = 86400L;
						time = time - date.getSeconds();
						time = time - date.getMinutes() * 60;
						time = time - date.getHours() * 3600;
						time = time * 1000;
						Thread.sleep(time);
						JRRP.clear();
					}
				} catch (Exception exception) {
					if (entry.isEnable()) {
						long timeserial = System.currentTimeMillis();
						entry.adminInfo("[发生异常] 时间序列号 - " + timeserial + " " + exception.getMessage());
						Executor_jrrp.this.logger.exception(exception);
					} else {
						Executor_jrrp.this.logger.full("关闭");
					}
				}
			} while (entry.isEnable());

			logger.full("工作线程结束");
		}
	}
}
