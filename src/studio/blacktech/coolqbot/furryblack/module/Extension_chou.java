package studio.blacktech.coolqbot.furryblack.module;

import java.security.SecureRandom;
import java.util.List;

import com.sobte.cqp.jcq.entity.Member;
import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.JavaExtension;
import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.utility.Message;

@SuppressWarnings("unused")
public class Extension_chou extends JavaExtension {

	//@formatter:off
	private final String MODULE_DISPLAYNAME = "随机抽人";
	private final String MODULE_COMMANDNAME = "chou";
	private final String MODULE_VERSION = "2.4.0";
	private final String MODULE_DESCRIPTION = "从群随机抽取一个人";
	private final String[] MODULE_USAGE = {"//chou ","//chou 理由"};
	private final String[] MODULE_PRIVACY_LISTEN = {};
	private final String[] MODULE_PRIVACY_EVENTS = {};
	private final String[] MODULE_PRIVACY_STORED = {};
	private final String[] MODULE_PRIVACY_CACHED = {};
	private final String[] MODULE_PRIVACY_OBTAIN = {"获取命令发送人","获取群成员列表"};
	//@formatter:on	

	@Override
	public boolean doUserMessage(int typeid, int userid, Message message, int messageid, int messagefont) throws Exception {
		userWarn(userid, "此功能不支持私聊");
		return true;
	}

	@Override
	public boolean doDiszMessage(int diszid, int userid, Message message, int messageid, int messagefont) throws Exception {
		diszInfo(diszid, userid, "此功能不支持讨论组");
		return true;
	}

	@Override
	public boolean doGropMessage(int gropid, int userid, Message message, int messageid, int messagefont) throws Exception {
		List<Member> members = JcqApp.CQ.getGroupMemberList(gropid);
		Member member;
		int size = members.size();
		long uid = 0;
		do {
			member = members.get(random(size));
			uid = member.getQqId();
		} while (uid == entry.SELFQQID || uid == userid);
		String nickname = member.getCard();
		if (nickname.length() == 0) {
			nickname = member.getNick();
		}
		message.prase();
		if (message.length == 1) {
			gropInfo(gropid, userid, "随机抽到 " + nickname + "(" + member.getQqId() + ")");
		} else {
			gropInfo(gropid, userid, "随机抽到 " + nickname + "(" + member.getQqId() + ") : " + message.join(1));
		}
		return true;
	}

	private static int random(int size) {
		SecureRandom random = new SecureRandom();
		return random.nextInt(size);
	}

	@Override
	public String getReport() {
		return null;
	}
}
