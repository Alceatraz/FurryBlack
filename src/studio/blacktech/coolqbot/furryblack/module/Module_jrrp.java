package studio.blacktech.coolqbot.furryblack.module;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import studio.blacktech.coolqbot.furryblack.signal.Workflow;

public class Module_jrrp extends FunctionModuel {
	private static HashMap<Long, Integer> jrrp = new HashMap<Long, Integer>();

	public static void flush() {
		Module_jrrp.jrrp.clear();
	}

	public Module_jrrp() {
		this.MODULE_NAME = "今日运气";
		this.MODULE_HELP = "//jrrp 今日运气";
		this.MODULE_COMMAND = "jrrp";
		this.MODULE_VERSION = "1.6.3";
		this.MODULE_DESCRIPTION = "今日运气";
		this.MODULE_PRIVACY = "存储 : 无\r\n缓存 : 1\r\n1: QQ号-运气值对应表 - 每日UTC+8 00:00 清空\r\n获取 : 1\r\n1: 命令发送人用于@";
	}

	@Override
	public void excute(final Workflow flow) {
		this.counter++;
		if (!Module_jrrp.jrrp.containsKey(flow.qqid)) {
			final SecureRandom random = new SecureRandom();
			Module_jrrp.jrrp.put(flow.qqid, random.nextInt(100));
		}
		final String res = "今天的运气是" + Module_jrrp.jrrp.get(flow.qqid) + "%!!!";
		switch (flow.from) {
		case 1:
			FunctionModuel.priInfo(flow, res);
			break;
		case 2:
			FunctionModuel.disInfo(flow, res);
			break;
		case 3:
			FunctionModuel.grpInfo(flow, res);
			break;
		}
	}

	@Override
	public String genReport() {
		if (this.counter == 0) {
			return null;
		}
		final TreeMap<Integer, Integer> frequency = new TreeMap<Integer, Integer>();
		for (final long temp : Module_jrrp.jrrp.keySet()) {
			final int luck = Module_jrrp.jrrp.get(temp);
			frequency.put(luck, frequency.containsKey(luck) ? frequency.get(luck) + 1 : 1);
		}
		final int size = Module_jrrp.jrrp.size();
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
		return builder.toString();
	}
}
