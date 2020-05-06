package studio.blacktech.coolqbot.furryblack.modules.Executor;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.annotation.ModuleExecutorComponent;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;


@ModuleExecutorComponent
public class Executor_jrjp extends ModuleExecutor {


	private static final long serialVersionUID = 1L;


	// 见证历史 2020-3-23 BiliBili变更AV为BV


	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	private static final String MODULE_PACKAGENAME = "Executor_JRJP";
	private static final String MODULE_COMMANDNAME = "jrjp";
	private static final String MODULE_DISPLAYNAME = "祭祀";
	private static final String MODULE_DESCRIPTION = "献祭一个成员 召唤一个视频";
	private static final String MODULE_VERSION = "2.0.0";
	private static final String[] MODULE_USAGE = new String[] {
			"/jrjp - 查看今日祭品"
	};
	private static final String[] MODULE_PRIVACY_STORED = new String[] {};
	private static final String[] MODULE_PRIVACY_CACHED = new String[] {
			"群号-QQ号对应表 - 每日UTC+8 00:00 清空",
			"群号-AV号对应表 - 每日UTC+8 00:00 清空"
	};
	private static final String[] MODULE_PRIVACY_OBTAIN = new String[] {
			"获取命令发送人", "被抽到成员的昵称和群昵称"
	};

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================

	private HashMap<Long, Long> AVCODE;
	private HashMap<Long, Long> VICTIM;

	private HashMap<Long, ArrayList<Long>> IGNORES;

	private File USER_IGNORE;

	private Thread thread;

	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================

	public Executor_jrjp() throws Exception {

		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);

	}

	@Override
	public boolean init() throws Exception {

		initAppFolder();
		initConfFolder();

		IGNORES = new HashMap<>();

		AVCODE = new HashMap<>();
		VICTIM = new HashMap<>();


		USER_IGNORE = Paths.get(FOLDER_CONF.getAbsolutePath(), "ignore_user.txt").toFile();

		if (!USER_IGNORE.exists()) USER_IGNORE.createNewFile();


		long gropid;
		long userid;

		String line;
		String[] temp;

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(USER_IGNORE), StandardCharsets.UTF_8));


		while ((line = reader.readLine()) != null) {

			if (line.startsWith("#")) continue;
			if (!line.contains(":")) continue;
			if (line.contains("#")) line = line.substring(0, line.indexOf("#")).trim();
			temp = line.split(":");

			if (temp.length != 2) {
				logger.warn("配置错误", line);
				continue;
			}

			gropid = Long.parseLong(temp[0]);
			userid = Long.parseLong(temp[1]);

			if (!IGNORES.containsKey(gropid)) IGNORES.put(gropid, new ArrayList<>());

			IGNORES.get(gropid).add(userid);

			logger.seek("排除用户", gropid + " > " + userid);
		}

		reader.close();


		ENABLE_USER = false;
		ENABLE_DISZ = false;
		ENABLE_GROP = true;

		return true;
	}


	@Override
	public boolean boot() throws Exception {

		logger.info("启动工作线程");
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

		logger.info("终止工作线程");
		thread.interrupt();
		thread.join();
		logger.info("工作线程已终止");
		return true;

	}

	@Override
	public String[] exec(Message message) throws Exception {

		return new String[] {
				"此模块无可用命令"
		};

	}


	@Override
	public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) {

	}


	@Override
	public void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) {
		AVCODE.remove(gropid);
		VICTIM.remove(gropid);
	}


	@Override
	public boolean doUserMessage(MessageUser message) throws Exception {
		return true;
	}


	@Override
	public boolean doDiszMessage(MessageDisz message) throws Exception {
		return true;
	}


	@Override
	public boolean doGropMessage(MessageGrop message) throws Exception {

		long gropid = message.getGropID();
		long userid = message.getUserID();

		// long victim = VICTIM.get(gropid);
		// long avcode = AVCODE.get(gropid);

		// entry.gropInfo(gropid, userid, entry.getNickname(gropid, victim) + " (" + victim + ") 被作为祭品献祭掉了，召唤出一个神秘视频 https://www.bilibili.com/video/av" + avcode);

		entry.gropInfo(gropid, userid, "有奖征集！\nBiliBili使用新的BV号系统以后，如何随机抽取视频？");

		return true;
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


	@SuppressWarnings("unused")
	class Worker implements Runnable {

		@Override
		@SuppressWarnings("deprecation")
		public void run() {

			if (true) return;

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

						VICTIM.clear();

					}

				} catch (InterruptedException exception) {
					if (entry.isEnable()) {
						long timeserial = System.currentTimeMillis();
						entry.adminInfo("[发生异常] 时间序列号 - " + timeserial + " " + exception.getMessage());
						logger.exception(timeserial, exception);
					} else {
						logger.full("关闭");
					}
				}
			} while (entry.isEnable());
			logger.full("工作线程结束");
		}

	}

}
