package studio.blacktech.coolqbot.furryblack.plugins;

import java.security.SecureRandom;
import java.util.List;

import com.sobte.cqp.jcq.entity.Member;
import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.common.ConfigureX;
import studio.blacktech.coolqbot.furryblack.module.Message;
import studio.blacktech.coolqbot.furryblack.module.Module;
import studio.blacktech.coolqbot.furryblack.module.ModuleExecutor;

@SuppressWarnings("unused")
public class Executor_chou extends ModuleExecutor {

	private final String MODULE_DISPLAYNAME = "随机抽人";
	private final String MODULE_PACKAGENAME = "chou";
	private final String MODULE_DESCRIPTION = "从群随机抽取一个人";
	private final String MODULE_VERSION = "2.4.0";
	private final String[] MODULE_USAGE = {
			"//chou ", "//chou 理由"
	};
	private final String[] MODULE_PRIVACY_LISTEN = {};
	private final String[] MODULE_PRIVACY_EVENTS = {};
	private final String[] MODULE_PRIVACY_STORED = {};
	private final String[] MODULE_PRIVACY_CACHED = {};
	private final String[] MODULE_PRIVACY_OBTAIN = {
			"获取命令发送人", "获取群成员列表"
	};

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
		final List<Member> members = JcqApp.CQ.getGroupMemberList(gropid);
		Member member;
		final int size = members.size();
		long uid = 0;
		do {
			member = members.get(Executor_chou.random(size));
			uid = member.getQqId();
		} while ((uid == ConfigureX.MYSELFID()) || (uid == userid));
		String nickname = member.getCard();
		if (nickname.length() == 0) {
			nickname = member.getNick();
		}
		message.prase();
		if (message.length == 1) {
			Module.gropInfo(gropid, userid, "随机抽到 " + nickname + "(" + member.getQqId() + ")");
		} else {
			Module.gropInfo(gropid, userid, "随机抽到 " + nickname + "(" + member.getQqId() + ") : " + message.join(1));
		}
		return true;
	}

	private static int random(final int size) {
		final SecureRandom random = new SecureRandom();
		return random.nextInt(size);
	}

	@Override
	public String getReport() {
		return null;
	}

}
