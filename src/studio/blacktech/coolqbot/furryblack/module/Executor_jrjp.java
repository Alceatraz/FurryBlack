package studio.blacktech.coolqbot.furryblack.module;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;

import com.sobte.cqp.jcq.entity.Member;
import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.signal.Workflow;

public class Executor_jrjp extends FunctionExecutor {

	private static HashMap<Long, Integer> jrav = new HashMap<Long, Integer>();
	private static HashMap<Long, Member> jrjp = new HashMap<Long, Member>();

	public static void flush() {
		Executor_jrjp.jrjp.clear();
		Executor_jrjp.jrav.clear();
	}

	public Executor_jrjp() {
		this.MODULE_NAME = "���ռ�Ʒ";
		this.MODULE_HELP = "//jrjp ���ռ�Ʒ";
		this.MODULE_COMMAND = "jrjp";
		this.MODULE_VERSION = "1.10.1";
		this.MODULE_DESCRIPTION = "���ռ�Ʒ";
		this.MODULE_PRIVACY = "�洢 : ��\r\n���� : 2\r\n1: Ⱥ��-QQ�Ŷ�Ӧ�� - ÿ��UTC+8 00:00 ���\r\n2: Ⱥ��-AV�Ŷ�Ӧ�� - ÿ��UTC+8 00:00 ���\r\n��ȡ : 2\r\n1: �����������@\r\n2: ���鵽��Ա���ǳƺ�Ⱥ�ǳ�";
	}

	@Override
	public void executor(final Workflow flow) {
		this.counter++;
		if (flow.from < 3) {
			FunctionExecutor.priWarn(flow, "���ռ�Ʒ��֧��Ⱥ");
			return;
		}
		if (!Executor_jrjp.jrjp.containsKey(flow.gpid)) {
			final SecureRandom random = new SecureRandom();
			final List<Member> members = JcqApp.CQ.getGroupMemberList(flow.gpid);
			Executor_jrjp.jrjp.put(flow.gpid, members.get(random.nextInt(members.size() + 1)));
			Executor_jrjp.jrav.put(flow.gpid, random.nextInt(50000000));
		}
		final String nickname = Executor_jrjp.jrjp.get(flow.gpid).getCard();
		if (nickname.length() == 0) {
			Executor_jrjp.jrjp.get(flow.gpid).getNick();
		}
		FunctionExecutor.grpInfo(flow, nickname + " (" + Executor_jrjp.jrjp.get(flow.gpid).getQqId() + ") ����Ϊ��Ʒ�׼����ˣ��ٻ���һ��������Ƶ https://www.bilibili.com/video/av" + Executor_jrjp.jrav.get(flow.gpid));
	}

	@Override
	public String genReport() {
		return null;
	}
}
