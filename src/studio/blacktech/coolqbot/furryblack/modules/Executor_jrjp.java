package studio.blacktech.coolqbot.furryblack.modules;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;

import com.sobte.cqp.jcq.entity.Member;
import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.common.Message;
import studio.blacktech.coolqbot.furryblack.common.Module;
import studio.blacktech.coolqbot.furryblack.common.ModuleExecutor;

public class Executor_jrjp extends ModuleExecutor {

	private final HashMap<Long, Integer> jrav = new HashMap<>();
	private final HashMap<Long, Member> jrjp = new HashMap<>();

	public Executor_jrjp() {
		this.MODULE_DISPLAYNAME = "今日祭品";
		this.MODULE_PACKAGENAME = "jrjp";
		this.MODULE_DESCRIPTION = "今日祭品";
		this.MODULE_VERSION = "2.10.2";
		this.MODULE_USAGE = new String[] {
				"//jrjp - 随机生成今日人品"
		};
		this.MODULE_PRIVACY_TRIGER = new String[] {};
		this.MODULE_PRIVACY_LISTEN = new String[] {};
		this.MODULE_PRIVACY_STORED = new String[] {};
		this.MODULE_PRIVACY_CACHED = new String[] {
				"群号-QQ号对应表 - 每日UTC+8 00:00 清空",
				"群号-AV号对应表 - 每日UTC+8 00:00 清空"
		};
		this.MODULE_PRIVACY_OBTAIN = new String[] {
				"获取命令发送人",
				"被抽到成员的昵称和群昵称"
		};
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
		if (!this.jrjp.containsKey(gropid)) {
			final SecureRandom random = new SecureRandom();
			final List<Member> members = JcqApp.CQ.getGroupMemberList(gropid);
			this.jrjp.put(gropid, members.get(random.nextInt(members.size() + 1)));
			this.jrav.put(gropid, random.nextInt(50000000));
		}
		final Long sacrificeid = this.jrjp.get(gropid).getQqId();
		Module.gropInfo(gropid, userid, Module_Nick.getNickname(sacrificeid) + " (" + sacrificeid + ") 被作为祭品献祭掉了，召唤出一个神秘视频 https://www.bilibili.com/video/av" + this.jrav.get(gropid));
		return true;
	}

	public void flush() {
		this.jrjp.clear();
		this.jrav.clear();
	}

}
