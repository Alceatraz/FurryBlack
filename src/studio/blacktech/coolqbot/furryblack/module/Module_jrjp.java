package studio.blacktech.coolqbot.furryblack.module;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;

import com.sobte.cqp.jcq.entity.Member;
import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.signal.Workflow;

public class Module_jrjp extends FunctionModuel {

	private static HashMap<Long, Integer> jrav = new HashMap<Long, Integer>();
	private static HashMap<Long, Member> jrjp = new HashMap<Long, Member>();

	public static void flush() {
		Module_jrjp.jrjp.clear();
		Module_jrjp.jrav.clear();
	}

	public Module_jrjp() {
		this.MODULE_NAME = "今日祭品";
		this.MODULE_HELP = "//jrjp 今日祭品";
		this.MODULE_COMMAND = "jrjp";
		this.MODULE_VERSION = "1.10.1";
		this.MODULE_DESCRIPTION = "今日祭品";
		this.MODULE_PRIVACY = "存储 : 无\r\n缓存 : 2\r\n1: 群号-QQ号对应表 - 每日UTC+8 00:00 清空\r\n2: 群号-AV号对应表 - 每日UTC+8 00:00 清空\r\n获取 : 2\r\n1: 命令发送人用于@\r\n2: 被抽到成员的昵称和群昵称";
	}

	@Override
	public void excute(final Workflow flow) {
		this.counter++;
		if (flow.from < 3) {
			FunctionModuel.priWarn(flow, "今日祭品仅支持群");
			return;
		}
		if (!Module_jrjp.jrjp.containsKey(flow.gpid)) {
			final SecureRandom random = new SecureRandom();
			final List<Member> members = JcqApp.CQ.getGroupMemberList(flow.gpid);
			Module_jrjp.jrjp.put(flow.gpid, members.get(random.nextInt(members.size() + 1)));
			Module_jrjp.jrav.put(flow.gpid, random.nextInt(50000000));
		}
		final String nickname = Module_jrjp.jrjp.get(flow.gpid).getCard();
		if (nickname.length() == 0) {
			Module_jrjp.jrjp.get(flow.gpid).getNick();
		}
		FunctionModuel.grpInfo(flow, nickname + " (" + Module_jrjp.jrjp.get(flow.gpid).getQqId() + ") 被作为祭品献祭掉了，召唤出一个神秘视频 https://www.bilibili.com/video/av" + Module_jrjp.jrav.get(flow.gpid));
	}

	@Override
	public String genReport() {
		return null;
	}
}
