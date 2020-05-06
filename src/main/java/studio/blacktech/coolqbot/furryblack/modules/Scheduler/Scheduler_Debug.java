package studio.blacktech.coolqbot.furryblack.modules.Scheduler;


import java.net.ServerSocket;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.annotation.ModuleSchedulerComponent;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleScheduler;


@ModuleSchedulerComponent
public class Scheduler_Debug extends ModuleScheduler {

	private static final long serialVersionUID = 1L;

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	private static final String MODULE_PACKAGENAME = "Scheduler_DEBUG";
	private static final String MODULE_COMMANDNAME = "debug";
	private static final String MODULE_DISPLAYNAME = "调试接口";
	private static final String MODULE_DESCRIPTION = "调试接口";
	private static final String MODULE_VERSION = "1.0.0";
	private static final String[] MODULE_USAGE = new String[] {};
	public static String[] MODULE_PRIVACY_STORED = new String[] {};
	public static String[] MODULE_PRIVACY_CACHED = new String[] {};
	public static String[] MODULE_PRIVACY_OBTAIN = new String[] {};

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================


	private Thread thread;


	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================


	public Scheduler_Debug() throws Exception {
		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}


	@Override
	public boolean init() throws Exception {
		return true;
	}


	@Override
	public boolean boot() throws Exception {
		System.out.println("DEBUG模块 - boot阶段 " + entry.listGroups().size());
		thread = new Thread(new Worker());
		thread.start();

		return true;
	}

	@Override
	public boolean save() throws Exception {
		return true;
	}

	@Override
	public boolean shut() throws Exception {
		return true;
	}

	@Override
	public String[] exec(Message message) throws Exception {
		return new String[] {
				"此模块无可用命令"
		};
	}

	@Override
	public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) throws Exception {

	}

	@Override
	public void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) throws Exception {

	}

	// ==========================================================================================================================================================
	//
	// 工具函数
	//
	// ==========================================================================================================================================================

	@Override
	public String[] generateReport(Message message) {
		return new String[0];
	}

	class Worker implements Runnable {

		ServerSocket server = null;


		@Override
		public void run() {

			do {

				System.out.println("DEBUG模块 - 线程阶段 " + entry.listGroups().size());

				try {
					Thread.sleep(5000L);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} while (true);

		}


	}
}
