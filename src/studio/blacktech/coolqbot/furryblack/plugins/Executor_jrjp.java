package studio.blacktech.coolqbot.furryblack.plugins;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;

import com.sobte.cqp.jcq.entity.Member;
import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.module.Message;
import studio.blacktech.coolqbot.furryblack.module.Module;
import studio.blacktech.coolqbot.furryblack.module.ModuleExecutor;

@SuppressWarnings("unused")
public class Executor_jrjp extends ModuleExecutor {

	private static HashMap<Long, Integer> jrav = new HashMap<Long, Integer>();
	private static HashMap<Long, Member> jrjp = new HashMap<Long, Member>();

	private String MODULE_DISPLAYNAME = "今日祭品";
	private String MODULE_PACKAGENAME = "jrjp";
	private String MODULE_DESCRIPTION = "今日祭品";
	private String MODULE_VERSION = "2.10.2";
	private String[] MODULE_USAGE = {
			"//jrjp"
	};
	private String[] MODULE_PRIVACY_LISTEN = {};
	private String[] MODULE_PRIVACY_EVENTS = {};
	private String[] MODULE_PRIVACY_STORED = {};
	private String[] MODULE_PRIVACY_CACHED = {
			"群号-QQ号对应表 - 每日UTC+8 00:00 清空", "群号-AV号对应表 - 每日UTC+8 00:00 清空"
	};
	private String[] MODULE_PRIVACY_OBTAIN = {
			"获取命令发送人", "被抽到成员的昵称和群昵称"
	};

	@Override
	public boolean doUserMessage(int typeid, long userid, Message message, int messageid, int messagefont) throws Exception {
		return false;
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, Message message, int messageid, int messagefont) throws Exception {
		return false;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, Message message, int messageid, int messagefont) throws Exception {
		if (!Executor_jrjp.jrjp.containsKey(gropid)) {
			SecureRandom random = new SecureRandom();
			List<Member> members = JcqApp.CQ.getGroupMemberList(gropid);
			Executor_jrjp.jrjp.put(gropid, members.get(random.nextInt(members.size() + 1)));
			Executor_jrjp.jrav.put(gropid, random.nextInt(50000000));
		}
		String nickname = Executor_jrjp.jrjp.get(gropid).getCard();
		if (nickname.length() == 0) {
			nickname = Executor_jrjp.jrjp.get(gropid).getNick();
		}
		Module.gropInfo(gropid, userid, nickname + " (" + userid + ") 被作为祭品献祭掉了，召唤出一个神秘视频 https://www.bilibili.com/video/av" + Executor_jrjp.jrav.get(gropid));
		return true;
	}

	public static void flush() {
		Executor_jrjp.jrjp.clear();
		Executor_jrjp.jrav.clear();
	}

	@Override
	public String getReport() {
		return null;
	}

}
