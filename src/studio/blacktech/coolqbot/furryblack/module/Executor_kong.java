package studio.blacktech.coolqbot.furryblack.module;

import studio.blacktech.coolqbot.furryblack.signal.Workflow;

public class Executor_kong extends FunctionExecutor {

	public Executor_kong() {
		this.MODULE_NAME = "���";
		this.MODULE_HELP = "//kong ��Ҫ�����ԭ��";
		this.MODULE_COMMAND = "kong";
		this.MODULE_VERSION = "1.0.0";
		this.MODULE_DESCRIPTION = "�� �� �� �� �� ��";
		this.MODULE_PRIVACY = "�洢 : ��\r\n���� : ��\r\n��ȡ : 1\r\n1: �����������@";
	}

	@Override
	public void executor(final Workflow flow) {
		this.counter++;

		if (flow.length == 1) {
			return;
		}
		String raw;
		raw = flow.join(1);
		raw = raw.replaceAll(" ", "");
		raw = raw.trim();
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < raw.length(); i++) {
			builder.append(raw.charAt(i));
			builder.append(" ");
		}
		builder.setLength(builder.length() - 1);
		switch (flow.from) {
		case 1:
			FunctionExecutor.priInfo(flow, builder.toString());
			break;
		case 2:
			FunctionExecutor.disInfo(flow, builder.toString());
			break;
		case 3:
			FunctionExecutor.grpInfo(flow, builder.toString());
			break;
		}
	}

	@Override
	public String genReport() {
		return null;
	}
}
