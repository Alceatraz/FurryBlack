package studio.blacktech.coolqbot.furryblack.modules;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import studio.blacktech.coolqbot.furryblack.common.Message;
import studio.blacktech.coolqbot.furryblack.common.Module;
import studio.blacktech.coolqbot.furryblack.common.ModuleExecutor;

public class Executor_jrrp extends ModuleExecutor {

	private HashMap<Long, Integer> jrrp = new HashMap<Long, Integer>();

	public Executor_jrrp() {
		this.MODULE_DISPLAYNAME = "��������";
		this.MODULE_PACKAGENAME = "jrrp";
		this.MODULE_DESCRIPTION = "��������";
		this.MODULE_VERSION = "2.6.4";
		this.MODULE_USAGE = new String[] {
				"//jrrp - �����ȡһ����Ա��һ����Ƶ"
		};
		this.MODULE_PRIVACY_TRIGER = new String[] {};
		this.MODULE_PRIVACY_LISTEN = new String[] {};
		this.MODULE_PRIVACY_STORED = new String[] {};
		this.MODULE_PRIVACY_CACHED = new String[] {
				"�û���������Ӧ�� - ÿ��UTC+8 00:00 ���"
		};
		this.MODULE_PRIVACY_OBTAIN = new String[] {
				"��ȡ�������"
		};
	}

	@Override
	public boolean doUserMessage(int typeid, long userid, Message message, int messageid, int messagefont) throws Exception {
		if (!this.jrrp.containsKey(userid)) {
			SecureRandom random = new SecureRandom();
			this.jrrp.put(userid, random.nextInt(100));
		}
		Module.userInfo(userid, "�����������" + this.jrrp.get(userid) + "%!!!");
		return true;
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, Message message, int messageid, int messagefont) throws Exception {
		if (!this.jrrp.containsKey(userid)) {
			SecureRandom random = new SecureRandom();
			this.jrrp.put(userid, random.nextInt(100));
		}
		Module.diszInfo(diszid, userid, "�����������" + this.jrrp.get(userid) + "%!!!");
		return true;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, Message message, int messageid, int messagefont) throws Exception {
		if (!this.jrrp.containsKey(userid)) {
			SecureRandom random = new SecureRandom();
			this.jrrp.put(userid, random.nextInt(100));
		}
		Module.gropInfo(gropid, userid, "�����������" + this.jrrp.get(userid) + "%!!!");
		return true;
	}

	public void flush() {
		this.jrrp.clear();
	}

	@Override
	public String generateReport(int logLevel, int logMode, Message message, Object[] parameters) {
		if (this.COUNT == 0) {
			return null;
		}
		TreeMap<Integer, Integer> frequency = new TreeMap<Integer, Integer>();
		for (long temp : this.jrrp.keySet()) {
			int luck = this.jrrp.get(temp);
			frequency.put(luck, frequency.containsKey(luck) ? frequency.get(luck) + 1 : 1);
		}
		int size = this.jrrp.size();
		StringBuilder builder = new StringBuilder();
		builder.append("�������� ");
		builder.append(size);
		builder.append("��");
		for (Entry<Integer, Integer> temp : frequency.entrySet()) {
			builder.append("\r\n");
			builder.append(temp.getKey());
			builder.append(" : ");
			builder.append((temp.getValue() * 100) / size);
			builder.append("%");
		}
		return builder.toString();
	}
}
