package studio.blacktech.coolqbot.furryblack.modules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sobte.cqp.jcq.entity.Group;
import com.sobte.cqp.jcq.entity.Member;
import com.sobte.cqp.jcq.entity.QQInfo;
import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;

public class Executor_chou extends ModuleExecutor {

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "chou";
	private static String MODULE_DISPLAYNAME = "随机抽人";
	private static String MODULE_DESCRIPTION = "从当前群随机选择一个成员";
	private static String MODULE_VERSION = "5.0";
	private static String[] MODULE_USAGE = new String[] {
			"/chou - 随机抽一个人",
			"/chou 理由 - 以某个理由抽一个人"
	};
	private static String[] MODULE_PRIVACY_TRIGER = new String[] {};
	private static String[] MODULE_PRIVACY_LISTEN = new String[] {};
	private static String[] MODULE_PRIVACY_STORED = new String[] {};
	private static String[] MODULE_PRIVACY_CACHED = new String[] {};
	private static String[] MODULE_PRIVACY_OBTAIN = new String[] {
			"获取命令发送人",
			"获取群成员列表"
	};

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================

	private HashMap<Long, ArrayList<Long>> MEMBERS;
	private HashMap<Long, ArrayList<Long>> IGNORES;

	private File FILE_IGNORE_USER;

	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================

	public Executor_chou() throws Exception {
		super(MODULE_DISPLAYNAME, MODULE_PACKAGENAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_TRIGER, MODULE_PRIVACY_LISTEN, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	@Override
	public void init(LoggerX logger) throws Exception {

		this.MEMBERS = new HashMap<>();
		this.IGNORES = new HashMap<>();

		if (this.NEW_CONFIG) {
			this.CONFIG.setProperty("config1", "none");
			this.CONFIG.setProperty("config2", "none");
			this.CONFIG.setProperty("config3", "none");
			this.CONFIG.setProperty("config4", "none");
			this.saveConfig();
		} else {
			this.loadConfig();
		}

		this.FILE_IGNORE_USER = Paths.get(this.FOLDER_CONF.getAbsolutePath(), "ignore_user.txt").toFile();

		if (!this.FILE_IGNORE_USER.exists()) { this.FILE_IGNORE_USER.createNewFile(); }

		List<Group> groups = JcqApp.CQ.getGroupList();

		for (final Group group : groups) {
			this.MEMBERS.put(group.getId(), new ArrayList<Long>());
			this.IGNORES.put(group.getId(), new ArrayList<Long>());
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.FILE_IGNORE_USER), "UTF-8"));

		String line;
		String temp[];

		long gropid;
		long userid;

		while ((line = reader.readLine()) != null) {
			if (line.startsWith("#")) { continue; }
			if (line.indexOf(":") < 0) { continue; }
			temp = line.split(":");
			gropid = Long.parseLong(temp[0]);
			userid = Long.parseLong(temp[1]);
			this.IGNORES.get(gropid).add(userid);
		}

		reader.close();

		ArrayList<Long> tempMembers;
		ArrayList<Long> tempIgnores;

		for (final Group group : groups) {

			tempMembers = this.MEMBERS.get(group.getId());
			tempIgnores = this.IGNORES.get(group.getId());

			for (final Member member : JcqApp.CQ.getGroupMemberList(group.getId())) {
				if (entry.getMessage().isMyself(member.getQqId())) { continue; }
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
	}

	@Override
	public void shut(LoggerX logger) throws Exception {
	}

	@Override
	public void reload(LoggerX logger) throws Exception {
	}

	@Override
	public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) {
		ArrayList<Long> tempMembers = this.MEMBERS.get(gropid);
		ArrayList<Long> tempIgnores = this.IGNORES.get(gropid);
		if (tempIgnores.contains(userid)) {
			return;
		} else {
			tempMembers.add(userid);
		}
	}

	@Override
	public void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) {
		ArrayList<Long> tempMembers = this.MEMBERS.get(gropid);
		if (tempMembers.contains(userid)) { tempMembers.remove(tempMembers.indexOf(userid)); }
	}

	// ==========================================================================================================================================================
	//
	// 工作函数
	//
	// ==========================================================================================================================================================

	@Override
	public boolean doUserMessage(final int typeid, final long userid, final MessageUser message, final int messageid, final int messagefont) throws Exception {
		return true;
	}

	@Override
	public boolean doDiszMessage(final long diszid, final long userid, final MessageDisz message, final int messageid, final int messagefont) throws Exception {
		return true;
	}

	@Override
	public boolean doGropMessage(final long gropid, final long userid, final MessageGrop message, final int messageid, final int messagefont) throws Exception {
		SecureRandom random = new SecureRandom();
		ArrayList<Long> members = this.MEMBERS.get(gropid);
		int size = members.size();
		if (size < 3) {
			entry.getMessage().gropInfo(gropid, userid, "至少需要三个成员");
		} else {
			long chouid = 0;
			do {
				chouid = members.get(random.nextInt(size));
			} while (chouid == userid);
			QQInfo member = JcqApp.CQ.getStrangerInfo(chouid);
			if (message.getSection() == 1) {
				entry.getMessage().gropInfo(gropid, userid, "随机抽到 " + entry.getNickmap().getNickname(member.getQqId()) + "(" + chouid + ")");
			} else {
				entry.getMessage().gropInfo(gropid, userid, "随机抽到 " + entry.getNickmap().getNickname(member.getQqId()) + "(" + chouid + ")： " + message.getOptions());
			}
		}
		return true;
	}

	// ==========================================================================================================================================================
	//
	// 工具函数
	//
	// ==========================================================================================================================================================

	@Override
	public String[] generateReport(int mode, final Message message, final Object... parameters) {
		return null;
	}

}
