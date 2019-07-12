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

	// ==========================================================================================================================================================
	//
	// ģ���������
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "executor_jrjp";
	private static String MODULE_COMMANDNAME = "jrjp";
	private static String MODULE_DISPLAYNAME = "����";
	private static String MODULE_DESCRIPTION = "�׼�һ����Ա �ٻ�һ����Ƶ";
	private static String MODULE_VERSION = "1.0";
	private static String[] MODULE_USAGE = new String[] {
			"/jrjp - �鿴���ռ�Ʒ"
	};
	private static String[] MODULE_PRIVACY_TRIGER = new String[] {};
	private static String[] MODULE_PRIVACY_LISTEN = new String[] {};
	private static String[] MODULE_PRIVACY_STORED = new String[] {};
	private static String[] MODULE_PRIVACY_CACHED = new String[] {
			"Ⱥ��-QQ�Ŷ�Ӧ�� - ÿ��UTC+8 00:00 ���",
			"Ⱥ��-AV�Ŷ�Ӧ�� - ÿ��UTC+8 00:00 ���"
	};
	private static String[] MODULE_PRIVACY_OBTAIN = new String[] {
			"��ȡ�������",
			"���鵽��Ա���ǳƺ�Ⱥ�ǳ�"
	};

	// ==========================================================================================================================================================
	//
	// ��Ա����
	//
	// ==========================================================================================================================================================

	private HashMap<Long, Long> AVCODE = new HashMap<>();
	private HashMap<Long, Long> VICTIM = new HashMap<>();

	private HashMap<Long, ArrayList<Long>> MEMBERS = new HashMap<>();
	private HashMap<Long, ArrayList<Long>> IGNORES = new HashMap<>();

	private File USER_IGNORE;

	private Thread flush;

	// ==========================================================================================================================================================
	//
	// �������ں���
	//
	// ==========================================================================================================================================================

	public Executor_jrjp() throws Exception {
		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_TRIGER, MODULE_PRIVACY_LISTEN, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	@Override
	public void init(LoggerX logger) throws Exception {

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
				if (entry.getMessage().isAdmin(member.getQqId())) { continue; }
				if (tempIgnores.contains(member.getQqId())) { continue; }
				tempMembers.add(member.getQqId());
			}
		}

		this.ENABLE_USER = false;
		this.ENABLE_DISZ = false;
		this.ENABLE_GROP = true;
	}

	@Override
	public void boot(LoggerX logger) throws Exception {
		this.flush = new Thread(new Worker());
		this.flush.start();
	}

	@Override
	public void shut(LoggerX logger) throws Exception {
		this.flush.interrupt();
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
		entry.getMessage().gropInfo(gropid, entry.getNickmap().getNickname(victim) + " (" + victim + ") ����Ϊ��Ʒ�׼����ˣ��ٻ���һ��������Ƶ https://www.bilibili.com/video/av" + this.AVCODE.get(gropid));
		return true;
	}
	// ==========================================================================================================================================================
	//
	// ���ߺ���
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
			JcqApp.CQ.logInfo("FurryBlack", "jrjp - Worker ������");
			while (JcqAppAbstract.enable) {
				try {
					long time;
					Date date;
					while (true) {
						time = 86400L;
						date = new Date();
						time = time - date.getSeconds();
						time = time - date.getMinutes() * 65;
						time = time - date.getHours() * 3600;
						time = time * 1000;
						time = time - 5;
						Thread.sleep(time);
						Executor_jrjp.this.AVCODE.clear();
						Executor_jrjp.this.VICTIM.clear();
						ArrayList<Long> temp;
						SecureRandom random = new SecureRandom();
						for (long gropid : Executor_jrjp.this.MEMBERS.keySet()) {
							temp = Executor_jrjp.this.MEMBERS.get(gropid);
							Executor_jrjp.this.AVCODE.put(gropid, (long) random.nextInt(60000000));
							Executor_jrjp.this.VICTIM.put(gropid, temp.get(random.nextInt(temp.size())));
						}
						Thread.sleep(random.nextInt(3600000));
					}
				} catch (InterruptedException exception) {
				}
			}
		}
	}
}
