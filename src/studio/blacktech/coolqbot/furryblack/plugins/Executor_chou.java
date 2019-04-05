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

	private String MODULE_DISPLAYNAME = "�������";
	private String MODULE_PACKAGENAME = "chou";
	private String MODULE_DESCRIPTION = "��Ⱥ�����ȡһ����";
	private String MODULE_VERSION = "2.4.0";
	private String[] MODULE_USAGE = {
			"//chou ", "//chou ����"
	};
	private String[] MODULE_PRIVACY_LISTEN = {};
	private String[] MODULE_PRIVACY_EVENTS = {};
	private String[] MODULE_PRIVACY_STORED = {};
	private String[] MODULE_PRIVACY_CACHED = {};
	private String[] MODULE_PRIVACY_OBTAIN = {
			"��ȡ�������", "��ȡȺ��Ա�б�"
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
		List<Member> members = JcqApp.CQ.getGroupMemberList(gropid);
		Member member;
		int size = members.size();
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
			Module.gropInfo(gropid, userid, "����鵽 " + nickname + "(" + member.getQqId() + ")");
		} else {
			Module.gropInfo(gropid, userid, "����鵽 " + nickname + "(" + member.getQqId() + ") : " + message.join(1));
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
