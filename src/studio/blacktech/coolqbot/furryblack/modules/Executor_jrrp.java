package studio.blacktech.coolqbot.furryblack.modules;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import studio.blacktech.coolqbot.furryblack.common.Message;
import studio.blacktech.coolqbot.furryblack.common.Module;
import studio.blacktech.coolqbot.furryblack.common.ModuleExecutor;

public class Executor_jrrp extends ModuleExecutor {

	private final HashMap<Long, Integer> jrrp = new HashMap<>();

	public Executor_jrrp() {
		this.MODULE_DISPLAYNAME = "今日运气";
		this.MODULE_PACKAGENAME = "jrrp";
		this.MODULE_DESCRIPTION = "今日运气";
		this.MODULE_VERSION = "2.6.4";
		this.MODULE_USAGE = new String[] {
				"//jrrp - 随机抽取一个成员和一个视频"
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
	public void memberExit(long gropid, long userid) {
	}

	@Override
	public void memberJoin(long gropid, long userid) {
	}

	@Override
	public boolean doUserMessage(final int typeid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		if (!this.jrrp.containsKey(userid)) {
			final SecureRandom random = new SecureRandom();
			this.jrrp.put(userid, random.nextInt(100));
		}
		Module.userInfo(userid, "今天的运气是" + this.jrrp.get(userid) + "%!!!");
		return true;
	}

	@Override
	public boolean doDiszMessage(final long diszid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		if (!this.jrrp.containsKey(userid)) {
			final SecureRandom random = new SecureRandom();
			this.jrrp.put(userid, random.nextInt(100));
		}
		Module.diszInfo(diszid, userid, "今天的运气是" + this.jrrp.get(userid) + "%!!!");
		return true;
	}

	@Override
	public boolean doGropMessage(final long gropid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		if (!this.jrrp.containsKey(userid)) {
			final SecureRandom random = new SecureRandom();
			this.jrrp.put(userid, random.nextInt(100));
		}
		Module.gropInfo(gropid, userid, "今天的运气是" + this.jrrp.get(userid) + "%!!!");
		return true;
	}

	public void flush() {
		this.jrrp.clear();
	}

	@Override
	public String[] generateReport(final int logLevel, final int logMode, final int typeid, final long userid, final long diszid, final long gropid, final Message message, final Object... parameters) {
		if (this.COUNT == 0) { return null; }
		final TreeMap<Integer, Integer> frequency = new TreeMap<>();
		for (final long temp : this.jrrp.keySet()) {
			final int luck = this.jrrp.get(temp);
			frequency.put(luck, frequency.containsKey(luck) ? frequency.get(luck) + 1 : 1);
		}
		final int size = this.jrrp.size();
		final StringBuilder builder = new StringBuilder();
		builder.append("共生成了 ");
		builder.append(size);
		builder.append("次");
		for (final Entry<Integer, Integer> temp : frequency.entrySet()) {
			builder.append("\r\n");
			builder.append(temp.getKey());
			builder.append(" : ");
			builder.append((temp.getValue() * 100) / size);
			builder.append("%");
		}
		String res[] = new String[1];
		res[0] = builder.toString();
		return res;
	}
}
