package studio.blacktech.coolqbot.furryblack.plugins;

import java.security.SecureRandom;
import java.util.List;

import com.sobte.cqp.jcq.entity.Member;
import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.common.ConfigureX;
import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.module.Message;
import studio.blacktech.coolqbot.furryblack.module.ModuleLogic;

@SuppressWarnings("unused")
public class Command_chou extends ModuleLogic {

	//@formatter:off
	private final String MODULE_DISPLAYNAME = "�������";
	private final String MODULE_PACKAGENAME = "chou";
	private final String MODULE_VERSION = "2.4.0";
	private final String MODULE_DESCRIPTION = "��Ⱥ�����ȡһ����";
	private final String[] MODULE_USAGE = {"//chou ","//chou ����"};
	private final String[] MODULE_PRIVACY_LISTEN = {};
	private final String[] MODULE_PRIVACY_EVENTS = {};
	private final String[] MODULE_PRIVACY_STORED = {};
	private final String[] MODULE_PRIVACY_CACHED = {};
	private final String[] MODULE_PRIVACY_OBTAIN = {"��ȡ�������","��ȡȺ��Ա�б�"};
	//@formatter:on	

	@Override
	public boolean doCommandUserMessage(int typeid, int userid, Message message, int messageid, int messagefont) throws Exception {
		userWarn(userid, "�˹��ܲ�֧��˽��");
		return true;
	}

	@Override
	public boolean doCommandDiszMessage(int diszid, int userid, Message message, int messageid, int messagefont) throws Exception {
		diszInfo(diszid, userid, "�˹��ܲ�֧��������");
		return true;
	}

	@Override
	public boolean doCommandGropMessage(int gropid, int userid, Message message, int messageid, int messagefont) throws Exception {
		List<Member> members = JcqApp.CQ.getGroupMemberList(gropid);
		Member member;
		int size = members.size();
		long uid = 0;
		do {
			member = members.get(random(size));
			uid = member.getQqId();
		} while (uid == ConfigureX.SELFUSERID() || uid == userid);
		String nickname = member.getCard();
		if (nickname.length() == 0) {
			nickname = member.getNick();
		}
		message.prase();
		if (message.length == 1) {
			gropInfo(gropid, userid, "����鵽 " + nickname + "(" + member.getQqId() + ")");
		} else {
			gropInfo(gropid, userid, "����鵽 " + nickname + "(" + member.getQqId() + ") : " + message.join(1));
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
