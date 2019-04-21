package studio.blacktech.coolqbot.furryblack.plugins;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;

import com.sobte.cqp.jcq.entity.Member;
import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.module.Message;
import studio.blacktech.coolqbot.furryblack.module.Module;
import studio.blacktech.coolqbot.furryblack.module.ModuleExecutor;

public class Executor_jrjp extends ModuleExecutor {

	private HashMap<Long, Integer> jrav = new HashMap<Long, Integer>();
	private HashMap<Long, Member> jrjp = new HashMap<Long, Member>();

	public Executor_jrjp() {
		this.MODULE_DISPLAYNAME = "���ռ�Ʒ";
		this.MODULE_PACKAGENAME = "jrjp";
		this.MODULE_DESCRIPTION = "���ռ�Ʒ";
		this.MODULE_VERSION = "2.10.2";
		this.MODULE_USAGE = new String[] {
				"//jrjp - ������ɽ�����Ʒ"
		};
		this.MODULE_PRIVACY_TRIGER = new String[] {};
		this.MODULE_PRIVACY_LISTEN = new String[] {};
		this.MODULE_PRIVACY_STORED = new String[] {};
		this.MODULE_PRIVACY_CACHED = new String[] {
				"Ⱥ��-QQ�Ŷ�Ӧ�� - ÿ��UTC+8 00:00 ���", "Ⱥ��-AV�Ŷ�Ӧ�� - ÿ��UTC+8 00:00 ���"
		};
		this.MODULE_PRIVACY_OBTAIN = new String[] {
				"��ȡ�������", "���鵽��Ա���ǳƺ�Ⱥ�ǳ�"
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
		if (!this.jrjp.containsKey(gropid)) {
			SecureRandom random = new SecureRandom();
			List<Member> members = JcqApp.CQ.getGroupMemberList(gropid);
			this.jrjp.put(gropid, members.get(random.nextInt(members.size() + 1)));
			this.jrav.put(gropid, random.nextInt(50000000));
		}
		Module.gropInfo(gropid, userid, this.jrjp.get(gropid).getNick() + " (" + userid + ") ����Ϊ��Ʒ�׼����ˣ��ٻ���һ��������Ƶ https://www.bilibili.com/video/av" + this.jrav.get(gropid));
		return true;
	}

	public void flush() {
		this.jrjp.clear();
		this.jrav.clear();
	}

	@Override
	public String generateReport(boolean fullreport, int loglevel, Object[] parameters) {
		return null;
	}

}
