package studio.blacktech.coolqbot.furryblack.module;

import studio.blacktech.coolqbot.furryblack.signal.Workflow;

public class Executor_kong extends FunctionExecutor {

	public Executor_kong() {
		this.MODULE_NAME = "变臭";
		this.MODULE_HELP = "//kong 需要变臭的原句";
		this.MODULE_COMMAND = "kong";
		this.MODULE_VERSION = "1.0.0";
		this.MODULE_DESCRIPTION = "给 文 字 加 空 格";
		this.MODULE_PRIVACY = "存储 : 无\r\n缓存 : 无\r\n获取 : 1\r\n1: 命令发送人用于@";
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
