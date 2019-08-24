
package studio.blacktech.coolqbot.furryblack.modules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.sobte.cqp.jcq.entity.Group;
import com.sobte.cqp.jcq.entity.Member;
import com.sobte.cqp.jcq.event.JcqApp;
import com.sobte.cqp.jcq.event.JcqAppAbstract;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;

public class Executor_jrjp extends ModuleExecutor {

	private static final long serialVersionUID = 1L;

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "executor_jrjp";
	private static String MODULE_COMMANDNAME = "jrjp";
	private static String MODULE_DISPLAYNAME = "祭祀";
	private static String MODULE_DESCRIPTION = "献祭一个成员 召唤一个视频";
	private static String MODULE_VERSION = "1.1";
	private static String[] MODULE_USAGE = new String[] {
			"/jrjp - 查看今日祭品"
	};
	private static String[] MODULE_PRIVACY_TRIGER = new String[] {};
	private static String[] MODULE_PRIVACY_LISTEN = new String[] {};
	private static String[] MODULE_PRIVACY_STORED = new String[] {};
	private static String[] MODULE_PRIVACY_CACHED = new String[] {
			"群号-QQ号对应表 - 每日UTC+8 00:00 清空",
			"群号-AV号对应表 - 每日UTC+8 00:00 清空"
	};
	private static String[] MODULE_PRIVACY_OBTAIN = new String[] {
			"获取命令发送人",
			"被抽到成员的昵称和群昵称"
	};

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================

	private HashMap<Long, Long> AVCODE;
	private HashMap<Long, Long> VICTIM;

	private HashMap<Long, ArrayList<Long>> MEMBERS;
	private HashMap<Long, ArrayList<Long>> IGNORES;

	private File USER_IGNORE;

	private Thread thread;

	private SecureRandom random = new SecureRandom();
	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================

	public Executor_jrjp() throws Exception {
		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_TRIGER, MODULE_PRIVACY_LISTEN, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	@Override
	public void init(LoggerX logger) throws Exception {

		this.initConfFolder();

		this.AVCODE = new HashMap<>();
		this.VICTIM = new HashMap<>();
		this.MEMBERS = new HashMap<>();
		this.IGNORES = new HashMap<>();

		this.USER_IGNORE = Paths.get(this.FOLDER_CONF.getAbsolutePath(), "ignore_user.txt").toFile();

		if (!this.USER_IGNORE.exists()) { this.USER_IGNORE.createNewFile(); }

		List<Group> groups = JcqApp.CQ.getGroupList();

		for (Group group : groups) {
			this.MEMBERS.put(group.getId(), new ArrayList<Long>());
			this.IGNORES.put(group.getId(), new ArrayList<Long>());
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.USER_IGNORE), "UTF-8"));
		String line;
		String temp[];
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("#")) { continue; }
			if (line.indexOf(":") < 0) { continue; }
			logger.seek(this.MODULE_PACKAGENAME(), "排除用户 " + line);
			temp = line.split(":");
			long gropid = Long.parseLong(temp[0]);
			long userid = Long.parseLong(temp[1]);
			this.IGNORES.get(gropid).add(userid);
		}
		reader.close();

		for (Group group : groups) {
			ArrayList<Long> tempMembers = this.MEMBERS.get(group.getId());
			ArrayList<Long> tempIgnores = this.IGNORES.get(group.getId());
			for (Member member : JcqApp.CQ.getGroupMemberList(group.getId())) {
				if (entry.getMessage().isMyself(member.getQqId())) { continue; }
				if (tempIgnores.contains(member.getQqId())) { continue; }
				tempMembers.add(member.getQqId());
			}
			Executor_jrjp.this.VICTIM.put(group.getId(), tempMembers.get(this.random.nextInt(tempMembers.size())));
			Executor_jrjp.this.AVCODE.put(group.getId(), (long) this.random.nextInt(60000000));
		}

		this.ENABLE_USER = false;
		this.ENABLE_DISZ = false;
		this.ENABLE_GROP = true;
	}

	@Override
	public void boot(LoggerX logger) throws Exception {
		logger.info(this.MODULE_PACKAGENAME(), "启动工作线程");
		this.thread = new Thread(new Worker());
		this.thread.start();
	}

	@Override
	public void shut(LoggerX logger) throws Exception {
		logger.info(this.MODULE_PACKAGENAME(), "终止工作线程");
		this.thread.interrupt();
		this.thread.join();
	}

	@Override
	public void save(LoggerX logger) throws Exception {
	}

	@Override
	public void reload(LoggerX logger) throws Exception {
	}

	@Override
	public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) {
	}

	@Override
	public void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) {
	}

	@Override
	public boolean doUserMessage(int typeid, long userid, MessageUser message, int messageid, int messagefont) throws Exception {
		return true;
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, MessageDisz message, int messageid, int messagefont) throws Exception {
		return true;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, MessageGrop message, int messageid, int messagefont) throws Exception {
		long victim = this.VICTIM.get(gropid);
		entry.getMessage().gropInfo(gropid, entry.getNickmap().getNickname(victim) + " (" + victim + ") 被作为祭品献祭掉了，召唤出一个神秘视频 https://www.bilibili.com/video/av" + this.AVCODE.get(gropid));
		return true;
	}
	// ==========================================================================================================================================================
	//
	// 工具函数
	//
	// ==========================================================================================================================================================

	@Override
	public String[] generateReport(int mode, Message message, Object... parameters) {
		return null;
	}

	@SuppressWarnings("deprecation")
	class Worker implements Runnable {
		@Override
		public void run() {
			long time;
			Date date;
			do {
				try {
					// =======================================================
					while (true) {
						date = new Date();
						time = 86400L;
						time = time - date.getSeconds();
						time = time - date.getMinutes() * 60;
						time = time - date.getHours() * 3600;
						time = time * 1000;
						time = time - 5;
						JcqApp.CQ.logInfo("FurryBlackWorker", "[Executor_JRJP] 休眠：" + time);
						Thread.sleep(time);
						// =======================================================
						JcqApp.CQ.logInfo("FurryBlackWorker", "[Executor_JRJP] 执行");
						Executor_jrjp.this.AVCODE.clear();
						Executor_jrjp.this.VICTIM.clear();
						ArrayList<Long> temp;
						long victim;
						long avcode;
						StringBuilder builder = new StringBuilder();
						builder.append("[计划任务] Executor_jrjp 定时刷新");
						for (Long group : Executor_jrjp.this.MEMBERS.keySet()) {
							temp = Executor_jrjp.this.MEMBERS.get(group);
							victim = temp.get(Executor_jrjp.this.random.nextInt(temp.size()));
							avcode = Executor_jrjp.this.random.nextInt(60000000);
							Executor_jrjp.this.VICTIM.put(group, victim);
							Executor_jrjp.this.AVCODE.put(group, avcode);
							builder.append("\r\n" + group + " - " + " AV" + avcode);
						}
						JcqApp.CQ.logInfo("FurryBlackWorker", "[Executor_JRJP] 结果" + builder.toString());
					}
				} catch (InterruptedException exception) {
					if (JcqAppAbstract.enable) {
						JcqApp.CQ.logWarning("FurryBlackWorker", "[Executor_JRJP] 异常");
					} else {
						JcqApp.CQ.logInfo("FurryBlackWorker", "[Executor_JRJP] 关闭");
					}
				}
			} while (JcqAppAbstract.enable);
		}
	}
}
