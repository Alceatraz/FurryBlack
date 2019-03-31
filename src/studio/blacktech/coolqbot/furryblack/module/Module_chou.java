package studio.blacktech.coolqbot.furryblack.module;

import java.security.SecureRandom;
import java.util.List;

import com.sobte.cqp.jcq.entity.Member;
import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.signal.Workflow;

public class Module_chou extends FunctionModuel {

	public Module_chou() {
		this.MODULE_NAME = "���ѡ��";
		this.MODULE_HELP = "//chou �ӱ�Ⱥ���ѡ��һ����";
		this.MODULE_COMMAND = "chou";
		this.MODULE_VERSION = "1.3.0";
		this.MODULE_DESCRIPTION = "���ѡ��һ�����˵ĺ���";
		this.MODULE_PRIVACY = "�洢 : ��\r\n���� : ��\r\n��ȡ : 2\r\n1: �����������@\r\n2: ��ȡȺ��Ա�б�";
	}

	@Override
	public void excute(final Workflow flow) {
		this.counter++;
		if (flow.from < 3) {
			FunctionModuel.priWarn(flow, "���ѡ�˽�֧��Ⱥ");
			return;
		}
		int i;
		Member theone;
		final SecureRandom random = new SecureRandom();
		final List<Member> members = JcqApp.CQ.getGroupMemberList(flow.gpid);
		do {
			i = random.nextInt(members.size());
			theone = members.get(i);
			if (theone.getQqId() == flow.qqid) {
				members.remove(i);
			} else if (theone.getQqId() == entry.SELFQQID) {
				members.remove(i);
			} else {
				break;
			}
		} while (true);

		String nickname = theone.getCard();
		if (nickname.length() == 0) {
			nickname = theone.getNick();
		}
		if (flow.length == 1) {
			FunctionModuel.grpInfo(flow, "����鵽 " + nickname + "(" + theone.getQqId() + ")");
		} else {
			FunctionModuel.grpInfo(flow, "����鵽 " + nickname + "(" + theone.getQqId() + ") : " + flow.join(1));
		}
	}

	@Override
	public String genReport() {
		return null;
	}
}
