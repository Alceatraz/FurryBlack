package studio.blacktech.coolqbot.furryblack.modules;

import java.security.SecureRandom;

import studio.blacktech.coolqbot.furryblack.common.Message;
import studio.blacktech.coolqbot.furryblack.common.Module;
import studio.blacktech.coolqbot.furryblack.common.ModuleExecutor;

public class Executor_roll extends ModuleExecutor {

	private int mode_1 = 0;
	private int mode_2 = 0;
	private int mode_3 = 0;

	private int mode_fucked = 0;
	private int mode_fucker = 0;

	public Executor_roll() {
		this.MODULE_PACKAGENAME = "roll";
		this.MODULE_DISPLAYNAME = "�����";
		this.MODULE_DESCRIPTION = "����������������";
		this.MODULE_VERSION = "2.1.4";
		this.MODULE_USAGE = new String[] {
				"//roll - ��ȡ���",
				"//roll ���� - ���㵽����������ѡһ������",
				"//roll ���� ���� - �Ӹ������������м��ȡһ��"
		};
		this.MODULE_PRIVACY_TRIGER = new String[] {};
		this.MODULE_PRIVACY_LISTEN = new String[] {};
		this.MODULE_PRIVACY_STORED = new String[] {};
		this.MODULE_PRIVACY_CACHED = new String[] {};
		this.MODULE_PRIVACY_OBTAIN = new String[] {
				"��ȡ�������"
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
		Module.userInfo(userid, this.roll(message));
		return true;
	}

	@Override
	public boolean doDiszMessage(final long diszid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		Module.diszInfo(diszid, userid, this.roll(message));
		return true;
	}

	@Override
	public boolean doGropMessage(final long gropid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		Module.gropInfo(gropid, userid, this.roll(message));
		return true;
	}

	public String roll(final Message message) {
		String res = null;
		final SecureRandom random = new SecureRandom();
		switch (message.segment) {
		case 1:
			if (random.nextBoolean()) {
				this.mode_fucker++;
				res = "1";
			} else {
				this.mode_fucked++;
				res = "0";
			}
			this.mode_1++;
			break;
		case 2:
			int range = 100;
			try {
				range = Integer.parseInt(message.messages[1]);
				res = Integer.toString(random.nextInt(range));
				this.mode_2++;
			} catch (final Exception exce) {
				res = message.messages[1] + "��";
				if (random.nextBoolean()) {
					this.mode_fucker++;
					res = res + "1";
				} else {
					this.mode_fucked++;
					res = res + "0";
				}
				this.mode_1++;
			}
			break;
		case 3:
			int min = 100;
			int max = 200;
			try {
				min = Integer.parseInt(message.messages[1]);
				max = Integer.parseInt(message.messages[2]);
			} catch (final Exception exce) {
				return "������������������";
			}
			int temp = random.nextInt(max);
			if (temp < min) { temp = ((temp / max) * (max - min)) + min; }
			res = Integer.toString(temp);
			this.mode_3++;
			break;
		}
		return res;
	}

	@Override
	public String[] generateReport(final int logLevel, final int logMode, final int typeid, final long userid, final long diszid, final long gropid, final Message message, final Object... parameters) {
		if (this.COUNT == 0) { return null; }
		final StringBuilder builder = new StringBuilder();
		builder.append("ģʽ1 - ���: ");
		builder.append(this.mode_1);
		builder.append(" (");
		builder.append(this.mode_fucker);
		builder.append("/");
		builder.append(this.mode_fucked);
		builder.append(")\r\nģʽ2 - ����: ");
		builder.append(this.mode_2);
		builder.append("\r\nģʽ3 - ˫��: ");
		builder.append(this.mode_3);
		String res[] = new String[1];
		res[0] = builder.toString();
		return res;
	}

}
