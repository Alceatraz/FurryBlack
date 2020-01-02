package studio.blacktech.coolqbot.furryblack.modules.Executor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.meowy.cqp.jcq.entity.Group;
import org.meowy.cqp.jcq.entity.Member;
import org.meowy.cqp.jcq.entity.QQInfo;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.annotation.ModuleExecutorCompment;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;

@ModuleExecutorCompment(name = "Executor_chou")
public class Executor_chou extends ModuleExecutor {

	private static final long serialVersionUID = 1L;

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "Executor_Chou";
	private static String MODULE_COMMANDNAME = "chou";
	private static String MODULE_DISPLAYNAME = "随机抽人";
	private static String MODULE_DESCRIPTION = "从当前群随机选择一个成员";
	private static String MODULE_VERSION = "6.3";
	private static String[] MODULE_USAGE = new String[] {
			"/chou - 随机抽一个人",
			"/chou 理由 - 以某个理由抽一个人"
	};

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
	public boolean init() throws Exception {

		this.initAppFolder();
		this.initConfFolder();

		this.MEMBERS = new HashMap<>();
		this.IGNORES = new HashMap<>();

		this.FILE_IGNORE_USER = Paths.get(this.FOLDER_CONF.getAbsolutePath(), "ignore_user.txt").toFile();

		if (!this.FILE_IGNORE_USER.exists()) { this.FILE_IGNORE_USER.createNewFile(); }

		List<Group> groups = entry.getCQ().getGroupList();

		for (Group group : groups) {
			this.MEMBERS.put(group.getId(), new ArrayList<Long>());
			this.IGNORES.put(group.getId(), new ArrayList<Long>());
		}

		long gropid;
		long userid;

		String line;
		String[] temp;

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.FILE_IGNORE_USER), StandardCharsets.UTF_8));

		while ((line = reader.readLine()) != null) {

			if (line.startsWith("#")) { continue; }
			if (!line.contains(":")) { continue; }
			if (line.contains("#")) { line = line.substring(0, line.indexOf("#")).trim(); }

			temp = line.split(":");

			if (temp.length != 2) {
				this.logger.warn("配置无效", line);
				continue;
			}

			gropid = Long.parseLong(temp[0]);
			userid = Long.parseLong(temp[1]);

			if (this.IGNORES.containsKey(gropid)) {
				this.IGNORES.get(gropid).add(userid);
				this.logger.seek("排除用户", gropid + " > " + userid);
			} else {
				this.logger.seek("排除用户", "群不存在 " + gropid);
			}

		}

		reader.close();

		ArrayList<Long> tempMembers;
		ArrayList<Long> tempIgnores;

		for (Group group : groups) {

			tempMembers = this.MEMBERS.get(group.getId());
			tempIgnores = this.IGNORES.get(group.getId());

			for (Member member : entry.getCQ().getGroupMemberList(group.getId())) {
				if (entry.isMyself(member.getQQId())) { continue; }
				if (tempIgnores.contains(member.getQQId())) { continue; }
				tempMembers.add(member.getQQId());
			}
		}

		this.ENABLE_USER = false;
		this.ENABLE_DISZ = false;
		this.ENABLE_GROP = true;

		return true;

	}

	@Override
	public boolean boot() throws Exception {
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
	public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) {
		ArrayList<Long> tempMembers = new ArrayList<>();
		if (this.IGNORES.containsKey(gropid)) {
			ArrayList<Long> tempIgnores = this.IGNORES.get(gropid);
			for (Member tempUserid : entry.getCQ().getGroupMemberList(gropid)) {
				if (!tempIgnores.contains(tempUserid.getQQId())) { tempMembers.add(tempUserid.getQQId()); }
			}
		} else {
			for (Member tempUserid : entry.getCQ().getGroupMemberList(gropid)) {
				tempMembers.add(tempUserid.getQQId());
			}
		}
		this.MEMBERS.put(gropid, tempMembers);
	}

	@Override
	public void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) {
		ArrayList<Long> tempMembers = this.MEMBERS.get(gropid);
		tempMembers.remove(userid);
	}

	// ==========================================================================================================================================================
	//
	// 工作函数
	//
	// ==========================================================================================================================================================

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
		SecureRandom random = new SecureRandom();
		ArrayList<Long> members = this.MEMBERS.get(gropid);
		int size = members.size();
		if (size < 3) {
			entry.gropInfo(gropid, userid, "至少需要三个成员");
		} else {
			long chouid = 0;
			do {
				chouid = members.get(random.nextInt(size));
			} while (chouid == userid);
			QQInfo member = entry.getCQ().getStrangerInfo(chouid);
			if (message.getSection() == 1) {
				entry.gropInfo(gropid, userid, "随机抽到 " + entry.getGropnick(gropid, member.getQQId()) + "(" + chouid + ")");
			} else {
				entry.gropInfo(gropid, userid, "随机抽到 " + entry.getGropnick(gropid, member.getQQId()) + "(" + chouid + ")： " + message.getOptions());
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
	public String[] generateReport(int mode, Message message, Object... parameters) {
		return new String[0];
	}
}
