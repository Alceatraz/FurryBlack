package studio.blacktech.coolqbot.furryblack.module;

import java.security.SecureRandom;

import studio.blacktech.coolqbot.furryblack.signal.Workflow;

public class Executor_roll extends FunctionExecutor {

	private static int mode_1 = 0;
	private static int mode_2 = 0;
	private static int mode_3 = 0;

	private static int mode_fucked = 0;
	private static int mode_fucker = 0;

	public Executor_roll() {
		this.MODULE_NAME = "�����";
		this.MODULE_HELP = "//roll ���ѡ��һ����� -> 1\r\n//roll 20 ���ѡ��һ������ -> 17\r\n//roll 10 20 ���ѡ��һ������ -> 13";
		this.MODULE_COMMAND = "roll";
		this.MODULE_VERSION = "1.1.3";
		this.MODULE_DESCRIPTION = "���ѡ��һ������";
		this.MODULE_PRIVACY = "�洢 : ��\r\n���� : ��\r\n��ȡ : 1\r\n1: �����������@";
	}

	@Override
	public void executor(final Workflow flow) {
		this.counter++;
		String res = null;
		final SecureRandom random = new SecureRandom();
		switch (flow.length) {
		case 1:
			if (random.nextBoolean()) {
				Executor_roll.mode_fucker++;
				res = "1";
			} else {
				Executor_roll.mode_fucked++;
				res = "0";
			}
			Executor_roll.mode_1++;
			break;
		case 2:
			int range = 100;
			try {
				range = Integer.parseInt(flow.command[1]);
				res = Integer.toString(random.nextInt(range));
				Executor_roll.mode_2++;
			} catch (final Exception exce) {
				res = flow.command[1] + "��";
				if (random.nextBoolean()) {
					Executor_roll.mode_fucker++;
					res = res + "1";
				} else {
					Executor_roll.mode_fucked++;
					res = res + "0";
				}
				Executor_roll.mode_1++;
			}
			break;
		case 3:
			int min = 100;
			int max = 200;
			try {
				min = Integer.parseInt(flow.command[1]);
				max = Integer.parseInt(flow.command[2]);
			} catch (final Exception exce) {
				FunctionExecutor.priWarn(flow, "������������������");
			}
			int temp = random.nextInt(max);
			if (temp < min) {
				temp = ((temp / max) * (max - min)) + min;
			}
			res = Integer.toString(temp);
			Executor_roll.mode_3++;
			break;
		}

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
		final StringBuilder builder = new StringBuilder();
		builder.append("ģʽ1 - ���: ");
		builder.append(Executor_roll.mode_1);
		builder.append(" (");
		builder.append(Executor_roll.mode_fucker);
		builder.append("/");
		builder.append(Executor_roll.mode_fucked);
		builder.append(")\r\nģʽ2 - ����: ");
		builder.append(Executor_roll.mode_2);
		builder.append("\r\nģʽ3 - ˫��: ");
		builder.append(Executor_roll.mode_3);
		return builder.toString();
	}
}
