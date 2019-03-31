package studio.blacktech.coolqbot.furryblack.module;

import studio.blacktech.coolqbot.furryblack.signal.Workflow;

public class Module_dice extends FunctionModuel {

	public Module_dice() {
		this.MODULE_NAME = "扔骰子";
		this.MODULE_HELP = "//dice 投掷一枚骰子";
		this.MODULE_COMMAND = "dice";
		this.MODULE_VERSION = "1.0.2";
		this.MODULE_DESCRIPTION = "发送一个骰子表情";
		this.MODULE_PRIVACY = "存储 : 无\r\n缓存 : 无\r\n获取 : 1\r\n1: 命令发送人用于@";
	}

	@Override
	public void excute(final Workflow flow) {
		this.counter++;
		final String res = flow.length == 1 ? "[CQ:dice]" : flow.join(1) + "[CQ:dice]";
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
		return null;
	}
}
