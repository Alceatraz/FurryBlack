package studio.blacktech.coolqbot.furryblack.plugins;

import java.security.SecureRandom;
import java.util.List;

import com.sobte.cqp.jcq.entity.Member;
import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.common.ConfigureX;
import studio.blacktech.coolqbot.furryblack.module.Message;
import studio.blacktech.coolqbot.furryblack.module.Module;
import studio.blacktech.coolqbot.furryblack.module.ModuleExecutor;

public class Executor_chou extends ModuleExecutor {

	public Executor_chou() {
		this.MODULE_DISPLAYNAME = "�������";
		this.MODULE_PACKAGENAME = "chou";
		this.MODULE_DESCRIPTION = "��Ⱥ�����ȡһ����";
		this.MODULE_VERSION = "2.4.0";
		this.MODULE_USAGE = new String[] {
				"//chou ", "//chou ����"
		};
		this.MODULE_PRIVACY_TRIGER = new String[] {};
		this.MODULE_PRIVACY_LISTEN = new String[] {};
		this.MODULE_PRIVACY_STORED = new String[] {};
		this.MODULE_PRIVACY_CACHED = new String[] {};
		this.MODULE_PRIVACY_OBTAIN = new String[] {
				"��ȡ�������", "��ȡȺ��Ա�б�"
		};
	}

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
		long uid = 0;
		SecureRandom random = new SecureRandom();
		Member member;
		List<Member> members = JcqApp.CQ.getGroupMemberList(gropid);
		int size = members.size();
		do {
			member = members.get(random.nextInt(size));
			uid = member.getQqId();
		} while ((uid == ConfigureX.MYSELFID()) || (uid == userid));
		message.prase();
		if (message.length == 1) {
			Module.gropInfo(gropid, userid, "����鵽 " + (member.getCard().length() == 0 ? member.getCard() : member.getNick()) + "(" + member.getQqId() + ")");
		} else {
			Module.gropInfo(gropid, userid, "����鵽 " + (member.getCard().length() == 0 ? member.getCard() : member.getNick()) + "(" + member.getQqId() + ") : " + message.join(1));
		}
		return true;
	}

	@Override
	public String generateReport() {
		return null;
	}

}
