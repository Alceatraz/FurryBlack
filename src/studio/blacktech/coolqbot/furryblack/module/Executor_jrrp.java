package studio.blacktech.coolqbot.furryblack.module;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import studio.blacktech.coolqbot.furryblack.signal.Workflow;

public class Executor_jrrp extends FunctionExecutor {
	private static HashMap<Long, Integer> jrrp = new HashMap<Long, Integer>();

	public static void flush() {
		Executor_jrrp.jrrp.clear();
	}

	public Executor_jrrp() {
		this.MODULE_NAME = "��������";
		this.MODULE_HELP = "//jrrp ��������";
		this.MODULE_COMMAND = "jrrp";
		this.MODULE_VERSION = "1.6.3";
		this.MODULE_DESCRIPTION = "��������";
		this.MODULE_PRIVACY = "�洢 : ��\r\n���� : 1\r\n1: QQ��-����ֵ��Ӧ�� - ÿ��UTC+8 00:00 ���\r\n��ȡ : 1\r\n1: �����������@";
	}

	@Override
	public void executor(final Workflow flow) {
		this.counter++;
		if (!Executor_jrrp.jrrp.containsKey(flow.qqid)) {
			final SecureRandom random = new SecureRandom();
			Executor_jrrp.jrrp.put(flow.qqid, random.nextInt(100));
		}
		final String res = "�����������" + Executor_jrrp.jrrp.get(flow.qqid) + "%!!!";
		switch (flow.from) {
		case 1:
			FunctionExecutor.priInfo(flow, res);
			break;
		case 2:
			FunctionExecutor.disInfo(flow, res);
			break;
		case 3:
			FunctionExecutor.grpInfo(flow, res);
			break;
		}
	}

	@Override
	public String genReport() {
		if (this.counter == 0) {
			return null;
		}
		final TreeMap<Integer, Integer> frequency = new TreeMap<Integer, Integer>();
		for (final long temp : Executor_jrrp.jrrp.keySet()) {
			final int luck = Executor_jrrp.jrrp.get(temp);
			frequency.put(luck, frequency.containsKey(luck) ? frequency.get(luck) + 1 : 1);
		}
		final int size = Executor_jrrp.jrrp.size();
		final StringBuilder builder = new StringBuilder();
		builder.append("�������� ");
		builder.append(size);
		builder.append("��");
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
