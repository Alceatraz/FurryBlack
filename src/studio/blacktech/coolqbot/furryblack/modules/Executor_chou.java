package studio.blacktech.coolqbot.furryblack.modules;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sobte.cqp.jcq.entity.Group;
import com.sobte.cqp.jcq.entity.Member;
import com.sobte.cqp.jcq.entity.QQInfo;
import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.Message;
import studio.blacktech.coolqbot.furryblack.common.Module;
import studio.blacktech.coolqbot.furryblack.common.ModuleExecutor;

public class Executor_chou extends ModuleExecutor {

	private final HashMap<Long, ArrayList<Long>> MEMBERS = new HashMap<>();
	private final HashMap<Long, ArrayList<Long>> IGNORES = new HashMap<>();

	public Executor_chou() {
		this.MODULE_PACKAGENAME = "chou";
		this.MODULE_DISPLAYNAME = "随机抽人";
		this.MODULE_DESCRIPTION = "从群随机抽取一个人";
		this.MODULE_VERSION = "2.4.1";
		this.MODULE_USAGE = new String[] {
				"//chou - 随机抽一个人",
				"//chou 理由 - 以某个理由抽一个人"
		};
		this.MODULE_PRIVACY_TRIGER = new String[] {};
		this.MODULE_PRIVACY_LISTEN = new String[] {};
		this.MODULE_PRIVACY_STORED = new String[] {};
		this.MODULE_PRIVACY_CACHED = new String[] {};
		this.MODULE_PRIVACY_OBTAIN = new String[] {
				"获取命令发送人",
				"获取群成员列表"
		};
		final List<Group> groups = JcqApp.CQ.getGroupList();
		for (final Group group : groups) {
			this.MEMBERS.put(group.getId(), new ArrayList<Long>());
			this.IGNORES.put(group.getId(), new ArrayList<Long>());
		}
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(entry.MODULE_CHOU_USERIGNORE()), "UTF-8"));) {
			String line;
			String temp[];
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("#")) { continue; }
				if (line.indexOf(":") < 0) { continue; }
				temp = line.split(":");
				final long gropid = Long.parseLong(temp[0]);
				final long userid = Long.parseLong(temp[1]);
				this.IGNORES.get(gropid).add(userid);
			}
			reader.close();
		} catch (final Exception exce) {
			exce.printStackTrace();
		}
		for (final Group group : groups) {
			final ArrayList<Long> tempMembers = this.MEMBERS.get(group.getId());
			final ArrayList<Long> tempIgnores = this.IGNORES.get(group.getId());
			for (final Member member : JcqApp.CQ.getGroupMemberList(group.getId())) {
				if (entry.MYSELFID() == member.getQqId()) { continue; }
				if (tempIgnores.contains(member.getQqId())) { continue; }
				tempMembers.add(member.getQqId());
			}
		}
	}

	@Override
	public void memberExit(long gropid, long userid) {
	}

	@Override
	public void memberJoin(long gropid, long userid) {
	}

	@Override
	public String[] generateReport(final int logLevel, final int logMode, final int typeid, final long userid, final long diszid, final long gropid, final Message message, final Object... parameters) {
		return null;
	}

	@Override
	public boolean doUserMessage(final int typeid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		return false;
	}

	@Override
	public boolean doDiszMessage(final long diszid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		return false;
	}

	@Override
	public boolean doGropMessage(final long gropid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		final SecureRandom random = new SecureRandom();
		final ArrayList<Long> members = this.MEMBERS.get(gropid);
		final int size = members.size();
		if (size < 3) {
			Module.gropInfo(gropid, userid, "至少需要三个成员");
		} else {
			long chouid = 0;
			do {
				chouid = members.get(random.nextInt(size));
			} while ((chouid == entry.MYSELFID()) || (chouid == userid));
			final QQInfo member = JcqApp.CQ.getStrangerInfo(chouid);
			if (message.segment == 1) {
				Module.gropInfo(gropid, userid, "随机抽到 " + Module_Nick.getNickname(member.getQqId()) + "(" + chouid + ")");
			} else {
				Module.gropInfo(gropid, userid, "随机抽到 " + Module_Nick.getNickname(member.getQqId()) + "(" + chouid + ")： " + message.join(1));
			}
		}
		return true;
	}

}
