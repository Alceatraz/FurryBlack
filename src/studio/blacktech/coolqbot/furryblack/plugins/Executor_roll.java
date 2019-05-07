package studio.blacktech.coolqbot.furryblack.plugins;

import java.security.SecureRandom;

import studio.blacktech.coolqbot.furryblack.module.Message;
import studio.blacktech.coolqbot.furryblack.module.Module;
import studio.blacktech.coolqbot.furryblack.module.ModuleExecutor;

public class Executor_roll extends ModuleExecutor {

	private int mode_1 = 0;
	private int mode_2 = 0;
	private int mode_3 = 0;

	private int mode_fucked = 0;
	private int mode_fucker = 0;

	public Executor_roll() {
		this.MODULE_DISPLAYNAME = "随机数";
		this.MODULE_PACKAGENAME = "roll";
		this.MODULE_DESCRIPTION = "生成随机数或者真假";
		this.MODULE_VERSION = "2.1.4";
		this.MODULE_USAGE = new String[] {
				"//roll - 抽取真假", "//roll 数字 - 从零到给定数字任选一个数字", "//roll 数字 数字 - 从给定两个数字中间抽取一个"
		};
		this.MODULE_PRIVACY_TRIGER = new String[] {};
		this.MODULE_PRIVACY_LISTEN = new String[] {};
		this.MODULE_PRIVACY_STORED = new String[] {};
		this.MODULE_PRIVACY_CACHED = new String[] {};
		this.MODULE_PRIVACY_OBTAIN = new String[] {
				"获取命令发送人"
		};
	}

	@Override
	public boolean doUserMessage(int typeid, long userid, Message message, int messageid, int messagefont) throws Exception {
		Module.userInfo(userid, this.roll(message));
		return true;
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, Message message, int messageid, int messagefont) throws Exception {
		Module.diszInfo(diszid, userid, this.roll(message));
		return true;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, Message message, int messageid, int messagefont) throws Exception {
		Module.gropInfo(gropid, userid, this.roll(message));
		return true;
	}

	public String roll(Message message) {
		String res = null;
		SecureRandom random = new SecureRandom();
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
				res = message.messages[1] + "是";
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
				return "参数必须是罗马数字";
			}
			int temp = random.nextInt(max);
			if (temp < min) {
				temp = ((temp / max) * (max - min)) + min;
			}
			res = Integer.toString(temp);
			this.mode_3++;
			break;
		}
		return res;
	}

	@Override
	public String generateReport(int logLevel, int logMode, Message message, Object[] parameters) {
		if (this.COUNT == 0) {
			return null;
		}
		final StringBuilder builder = new StringBuilder();
		builder.append("模式1 - 真假: ");
		builder.append(this.mode_1);
		builder.append(" (");
		builder.append(this.mode_fucker);
		builder.append("/");
		builder.append(this.mode_fucked);
		builder.append(")\r\n模式2 - 单限: ");
		builder.append(this.mode_2);
		builder.append("\r\n模式3 - 双限: ");
		builder.append(this.mode_3);
		return builder.toString();
	}

}
