package studio.blacktech.coolqbot.furryblack.plugins;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import studio.blacktech.coolqbot.furryblack.module.Message;
import studio.blacktech.coolqbot.furryblack.module.Module;
import studio.blacktech.coolqbot.furryblack.module.ModuleExecutor;

@SuppressWarnings("unused")
public class Executor_jrrp extends ModuleExecutor {
	private static HashMap<Long, Integer> jrrp = new HashMap<Long, Integer>();

	private String MODULE_DISPLAYNAME = "��������";
	private String MODULE_PACKAGENAME = "jrrp";
	private String MODULE_DESCRIPTION = "��������";
	private String MODULE_VERSION = "2.6.4";
	private String[] MODULE_USAGE = {
			"//jrrp"
	};
	private String[] MODULE_PRIVACY_LISTEN = {};
	private String[] MODULE_PRIVACY_EVENTS = {};
	private String[] MODULE_PRIVACY_STORED = {};
	private String[] MODULE_PRIVACY_CACHED = {
			"�û���������Ӧ�� - ÿ��UTC+8 00:00 ���"
	};
	private String[] MODULE_PRIVACY_OBTAIN = {
			"��ȡ�������"
	};

	@Override
	public boolean doUserMessage(int typeid, long userid, Message message, int messageid, int messagefont) throws Exception {
		if (!Executor_jrrp.jrrp.containsKey(userid)) {
			SecureRandom random = new SecureRandom();
			Executor_jrrp.jrrp.put(userid, random.nextInt(100));
		}
		Module.userInfo(userid, "�����������" + Executor_jrrp.jrrp.get(userid) + "%!!!");
		return true;
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, Message message, int messageid, int messagefont) throws Exception {
		if (!Executor_jrrp.jrrp.containsKey(userid)) {
			SecureRandom random = new SecureRandom();
			Executor_jrrp.jrrp.put(userid, random.nextInt(100));
		}
		Module.diszInfo(diszid, userid, "�����������" + Executor_jrrp.jrrp.get(userid) + "%!!!");
		return true;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, Message message, int messageid, int messagefont) throws Exception {
		if (!Executor_jrrp.jrrp.containsKey(userid)) {
			SecureRandom random = new SecureRandom();
			Executor_jrrp.jrrp.put(userid, random.nextInt(100));
		}
		Module.gropInfo(gropid, userid, "�����������" + Executor_jrrp.jrrp.get(userid) + "%!!!");
		return true;
	}

	public static void flush() {
		Executor_jrrp.jrrp.clear();
	}

	@Override
	public String getReport() {
		if (this.COUNT == 0) {
			return null;
		}
		TreeMap<Integer, Integer> frequency = new TreeMap<Integer, Integer>();
		for (long temp : Executor_jrrp.jrrp.keySet()) {
			int luck = Executor_jrrp.jrrp.get(temp);
			frequency.put(luck, frequency.containsKey(luck) ? frequency.get(luck) + 1 : 1);
		}
		int size = Executor_jrrp.jrrp.size();
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