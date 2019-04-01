package studio.blacktech.coolqbot.furryblack.module;

import studio.blacktech.coolqbot.furryblack.signal.Workflow;

public class Executor_echo extends FunctionExecutor {

	public Executor_echo() {
		this.MODULE_NAME = "回显";
		this.MODULE_HELP = "//echo 任意内容";
		this.MODULE_COMMAND = "echo";
		this.MODULE_VERSION = "1.0.0";
		this.MODULE_DESCRIPTION = "回显任何内容";
		this.MODULE_PRIVACY = "存储 : 无\r\n缓存 : 无\r\n获取 : 1\r\n1: 命令发送人用于@";
	}

	@Override
	public void executor(final Workflow flow) {
		this.counter++;

		final String res = flow.length == 1 ? "echo" : flow.join(1);

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
		return null;
	}
}
