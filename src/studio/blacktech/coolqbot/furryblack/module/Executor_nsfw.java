package studio.blacktech.coolqbot.furryblack.module;

import studio.blacktech.coolqbot.furryblack.signal.Workflow;

public class Executor_nsfw extends FunctionExecutor {

	public Executor_nsfw() {
		this.MODULE_NAME = "ˢ������";
		this.MODULE_HELP = "//nsfw";
		this.MODULE_COMMAND = "nsfw";
		this.MODULE_VERSION = "1.0.0";
		this.MODULE_DESCRIPTION = "������������һ���Ե�ˢ������";
		this.MODULE_PRIVACY = "��Ȩ : 1\r\n1: ����Ⱥ�û������¼���������//���ˣ�����������\r\n�洢 : ��\r\n���� : ��\r\n��ȡ : 1\r\n1: �����������@";

	}

	@Override
	public void executor(final Workflow flow) {
		this.counter++;
		final String res = "NSFW !\r\n++++++\r\n| 01 |\r\n| 02 |\r\n| 03 |\r\n| 04 |\r\n| 05 |\r\n| 06 |\r\n| 07 |\r\n| 08 |\r\n| 09 |\r\n| 10 |\r\n| 11 |\r\n| 12 |\r\n| 13 |\r\n| 14 |\r\n| 15 |\r\n| 16 |\r\n| 17 |\r\n| 18 |\r\n| 19 |\r\n| 20 |\r\n| 21 |\r\n| 22 |\r\n| 23 |\r\n| 24 |\r\n| 25 |\r\n| 26 |\r\n| 27 |\r\n| 28 |\r\n| 29 |\r\n| 30 |\r\n| 41 |\r\n| 42 |\r\n| 43 |\r\n| 44 |\r\n| 45 |\r\n| 46 |\r\n| 47 |\r\n| 48 |\r\n| 49 |\r\n| 40 |\r\n| 41 |\r\n| 42 |\r\n| 43 |\r\n| 44 |\r\n| 45 |\r\n| 46 |\r\n| 47 |\r\n| 48 |\r\n| 49 |\r\n| 50 |\r\n| 51 |\r\n| 52 |\r\n| 53 |\r\n| 54 |\r\n| 55 |\r\n| 56 |\r\n| 57 |\r\n| 58 |\r\n| 59 |\r\n| 60 |\r\n++++++\r\nNSFW !";
		switch (flow.from) {
		case 1:
			FunctionExecutor.priInfo(flow, res);
			break;
		case 2:
			FunctionExecutor.disAnno(flow.dzid, res);
			break;
		case 3:
			FunctionExecutor.grpAnno(flow.gpid, res);
			break;
		}
	}

	@Override
	public String genReport() {
		return null;
	}
}
