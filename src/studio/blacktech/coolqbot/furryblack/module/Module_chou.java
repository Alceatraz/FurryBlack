package studio.blacktech.coolqbot.furryblack.module;

import java.security.SecureRandom;
import java.util.List;

import com.sobte.cqp.jcq.entity.Member;
import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.signal.Workflow;

public class Module_chou extends FunctionModuel {

	public Module_chou() {
		this.MODULE_NAME = "随机选人";
		this.MODULE_HELP = "//chou 从本群随机选择一个人";
		this.MODULE_COMMAND = "chou";
		this.MODULE_VERSION = "1.3.0";
		this.MODULE_DESCRIPTION = "随机选择一个幸运的孩子";
		this.MODULE_PRIVACY = "存储 : 无\r\n缓存 : 无\r\n获取 : 2\r\n1: 命令发送人用于@\r\n2: 获取群成员列表";
	}

	@Override
	public void excute(final Workflow flow) {
		this.counter++;
		if (flow.from < 3) {
			FunctionModuel.priWarn(flow, "随机选人仅支持群");
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
			FunctionModuel.grpInfo(flow, "随机抽到 " + nickname + "(" + theone.getQqId() + ")");
		} else {
			FunctionModuel.grpInfo(flow, "随机抽到 " + nickname + "(" + theone.getQqId() + ") : " + flow.join(1));
		}
	}

	@Override
	public String genReport() {
		return null;
	}
}
