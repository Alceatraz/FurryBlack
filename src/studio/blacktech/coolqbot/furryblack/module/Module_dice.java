package studio.blacktech.coolqbot.furryblack.module;

import studio.blacktech.coolqbot.furryblack.signal.Workflow;

public class Module_dice extends FunctionModuel {

	public Module_dice() {
		this.MODULE_NAME = "������";
		this.MODULE_HELP = "//dice Ͷ��һö����";
		this.MODULE_COMMAND = "dice";
		this.MODULE_VERSION = "1.0.2";
		this.MODULE_DESCRIPTION = "����һ�����ӱ���";
		this.MODULE_PRIVACY = "�洢 : ��\r\n���� : ��\r\n��ȡ : 1\r\n1: �����������@";
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
