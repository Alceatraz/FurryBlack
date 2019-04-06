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

	public Executor_jrrp() {
		this.MODULE_DISPLAYNAME = "今日运气";
		this.MODULE_PACKAGENAME = "jrrp";
		this.MODULE_DESCRIPTION = "今日运气";
		this.MODULE_VERSION = "2.6.4";
		this.MODULE_USAGE = new String[] {
				"//jrrp"
		};
		this.MODULE_PRIVACY_TRIGER = new String[] {};
		this.MODULE_PRIVACY_LISTEN = new String[] {};
		this.MODULE_PRIVACY_STORED = new String[] {};
		this.MODULE_PRIVACY_CACHED = new String[] {
				"用户与运气对应表 - 每日UTC+8 00:00 清空"
		};
		this.MODULE_PRIVACY_OBTAIN = new String[] {
				"获取命令发送人"
		};
	}

	@Override
	public boolean doUserMessage(int typeid, long userid, Message message, int messageid, int messagefont) throws Exception {
		if (!Executor_jrrp.jrrp.containsKey(userid)) {
			SecureRandom random = new SecureRandom();
			Executor_jrrp.jrrp.put(userid, random.nextInt(100));
		}
		Module.userInfo(userid, "今天的运气是" + Executor_jrrp.jrrp.get(userid) + "%!!!");
		return true;
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, Message message, int messageid, int messagefont) throws Exception {
		if (!Executor_jrrp.jrrp.containsKey(userid)) {
			SecureRandom random = new SecureRandom();
			Executor_jrrp.jrrp.put(userid, random.nextInt(100));
		}
		Module.diszInfo(diszid, userid, "今天的运气是" + Executor_jrrp.jrrp.get(userid) + "%!!!");
		return true;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, Message message, int messageid, int messagefont) throws Exception {
		if (!Executor_jrrp.jrrp.containsKey(userid)) {
			SecureRandom random = new SecureRandom();
			Executor_jrrp.jrrp.put(userid, random.nextInt(100));
		}
		Module.gropInfo(gropid, userid, "今天的运气是" + Executor_jrrp.jrrp.get(userid) + "%!!!");
		return true;
	}

	public static void flush() {
		Executor_jrrp.jrrp.clear();
	}

	@Override
	public String generateReport() {
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
		builder.append("共生成了 ");
		builder.append(size);
		builder.append("次");
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
