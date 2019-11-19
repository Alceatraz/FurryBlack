package studio.blacktech.coolqbot.furryblack.modules.Executor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.meowy.cqp.jcq.entity.Group;
import org.meowy.cqp.jcq.entity.Member;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.LoggerX.LoggerX;
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

	private static String MODULE_PACKAGENAME = "Executor_JRJP";
	private static String MODULE_COMMANDNAME = "jrjp";
	private static String MODULE_DISPLAYNAME = "祭祀";
	private static String MODULE_DESCRIPTION = "献祭一个成员 召唤一个视频";
	private static String MODULE_VERSION = "1.2";
	private static String[] MODULE_USAGE = new String[] {
			"/jrjp - 查看今日祭品"
	};
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

		// @formatter:off

		super(
				MODULE_PACKAGENAME,
				MODULE_COMMANDNAME,
				MODULE_DISPLAYNAME,
				MODULE_DESCRIPTION,
				MODULE_VERSION,
				MODULE_USAGE,
				MODULE_PRIVACY_STORED,
				MODULE_PRIVACY_CACHED,
				MODULE_PRIVACY_OBTAIN
				);

		// @formatter:on

	}

	@Override
	public LoggerX init(LoggerX logger) throws Exception {

		this.initAppFolder(logger);
		this.initConfFolder(logger);

		this.AVCODE = new HashMap<>();
		this.VICTIM = new HashMap<>();
		this.MEMBERS = new HashMap<>();
		this.IGNORES = new HashMap<>();

		this.USER_IGNORE = Paths.get(this.FOLDER_CONF.getAbsolutePath(), "ignore_user.txt").toFile();

		if (!this.USER_IGNORE.exists()) { this.USER_IGNORE.createNewFile(); }

		List<Group> groups = entry.getCQ().getGroupList();

		for (Group group : groups) {
			this.MEMBERS.put(group.getId(), new ArrayList<Long>());
			this.IGNORES.put(group.getId(), new ArrayList<Long>());
		}

		long gropid;
		long userid;

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.USER_IGNORE), StandardCharsets.UTF_8));

		String line;
		String[] temp;

		while ((line = reader.readLine()) != null) {

			if (line.startsWith("#")) { continue; }
			if (!line.contains(":")) { continue; }
			if (line.contains("#")) { line = line.substring(0, line.indexOf("#")).trim(); }

			temp = line.split(":");

			if (temp.length != 2) {
				logger.mini(Executor_jrjp.MODULE_PACKAGENAME, "配置错误", line);
				continue;
			}

			gropid = Long.parseLong(temp[0]);
			userid = Long.parseLong(temp[1]);

			if (this.IGNORES.containsKey(gropid)) {
				this.IGNORES.get(gropid).add(userid);
				logger.seek(Executor_jrjp.MODULE_PACKAGENAME, "排除用户", gropid + " > " + userid);
			} else {
				logger.seek(Executor_jrjp.MODULE_PACKAGENAME, "排除用户", "群不存在 " + gropid);
			}

		}

		reader.close();

		for (Group group : groups) {

			ArrayList<Long> tempMembers = this.MEMBERS.get(group.getId());
			ArrayList<Long> tempIgnores = this.IGNORES.get(group.getId());

			for (Member member : entry.getCQ().getGroupMemberList(group.getId())) {

				if (entry.isMyself(member.getQQId())) { continue; }
				if (tempIgnores.contains(member.getQQId())) { continue; }

				tempMembers.add(member.getQQId());
			}

			Executor_jrjp.this.VICTIM.put(group.getId(), tempMembers.get(this.random.nextInt(tempMembers.size())));
			Executor_jrjp.this.AVCODE.put(group.getId(), (long) this.random.nextInt(70000000));

		}

		this.ENABLE_USER = false;
		this.ENABLE_DISZ = false;
		this.ENABLE_GROP = true;

		return logger;
	}

	@Override
	public LoggerX boot(LoggerX logger) throws Exception {

		logger.info(Executor_jrjp.MODULE_PACKAGENAME, "启动工作线程");
		this.thread = new Thread(new Worker());
		this.thread.start();

		return logger;
	}

	@Override
	public LoggerX save(LoggerX logger) throws Exception {
		return logger;
	}

	@Override
	public LoggerX shut(LoggerX logger) throws Exception {

		logger.info(Executor_jrjp.MODULE_PACKAGENAME, "终止工作线程");
		this.thread.interrupt();
		this.thread.join();

		return logger;

	}

	@Override
	public LoggerX exec(LoggerX logger, Message message) throws Exception {
		return logger;
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
		entry.gropInfo(gropid, entry.getGropnick(gropid, victim) + " (" + victim + ") 被作为祭品献祭掉了，召唤出一个神秘视频 https://www.bilibili.com/video/av" + this.AVCODE.get(gropid));
		return true;

	}
	// ==========================================================================================================================================================
	//
	// 工具函数
	//
	// ==========================================================================================================================================================

	@Override
	public String[] generateReport(int mode, Message message, Object... parameters) {
		return new String[0];
	}

	@SuppressWarnings("deprecation")
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
						time = time - (date.getMinutes() * 60);
						time = time - (date.getHours() * 3600);
						time = time * 1000;
						Thread.sleep(time);

						Executor_jrjp.this.AVCODE.clear();
						Executor_jrjp.this.VICTIM.clear();

						ArrayList<Long> temp;

						long victim;
						long avcode;

						StringBuilder builder = new StringBuilder();

						for (Long group : Executor_jrjp.this.MEMBERS.keySet()) {

							temp = Executor_jrjp.this.MEMBERS.get(group);
							victim = temp.get(Executor_jrjp.this.random.nextInt(temp.size()));
							avcode = Executor_jrjp.this.random.nextInt(60000000);

							Executor_jrjp.this.VICTIM.put(group, victim);
							Executor_jrjp.this.AVCODE.put(group, avcode);

							builder.append(group + " - " + " AV" + avcode + "\r\n");

						}
					}

				} catch (InterruptedException exception) {

					if (entry.isEnable()) {
						entry.getCQ().logWarning(Executor_jrjp.MODULE_PACKAGENAME, "异常");
					} else {
						entry.getCQ().logInfo(Executor_jrjp.MODULE_PACKAGENAME, "关闭");
					}
				}

			} while (entry.isEnable());
		}
	}
}
