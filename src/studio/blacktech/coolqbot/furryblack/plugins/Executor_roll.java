package studio.blacktech.coolqbot.furryblack.plugins;

import java.security.SecureRandom;

import studio.blacktech.coolqbot.furryblack.module.Message;
import studio.blacktech.coolqbot.furryblack.module.Module;
import studio.blacktech.coolqbot.furryblack.module.ModuleExecutor;

@SuppressWarnings("unused")
public class Executor_roll extends ModuleExecutor {

	private static int mode_1 = 0;
	private static int mode_2 = 0;
	private static int mode_3 = 0;

	private static int mode_fucked = 0;
	private static int mode_fucker = 0;

	private String MODULE_DISPLAYNAME = "随机数";
	private String MODULE_PACKAGENAME = "roll";
	private String MODULE_DESCRIPTION = "生成随机数或者真假";
	private String MODULE_VERSION = "2.1.4";
	private String[] MODULE_USAGE = {
			"//roll", "//roll 数字", "//roll 数字 数字"
	};
	private String[] MODULE_PRIVACY_LISTEN = {};
	private String[] MODULE_PRIVACY_EVENTS = {};
	private String[] MODULE_PRIVACY_STORED = {};
	private String[] MODULE_PRIVACY_CACHED = {};
	private String[] MODULE_PRIVACY_OBTAIN = {
			"获取命令发送人"
	};

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
		switch (message.length) {
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
				range = Integer.parseInt(message.cmd[1]);
				res = Integer.toString(random.nextInt(range));
				Executor_roll.mode_2++;
			} catch (final Exception exce) {
				res = message.cmd[1] + "是";
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
				min = Integer.parseInt(message.cmd[1]);
				max = Integer.parseInt(message.cmd[2]);
			} catch (final Exception exce) {
				return "参数必须是罗马数字";
			}
			int temp = random.nextInt(max);
			if (temp < min) {
				temp = ((temp / max) * (max - min)) + min;
			}
			res = Integer.toString(temp);
			Executor_roll.mode_3++;
			break;
		}
		return res;
	}

	@Override
	public String getReport() {
		if (this.COUNT == 0) {
			return null;
		}
		final StringBuilder builder = new StringBuilder();
		builder.append("模式1 - 真假: ");
		builder.append(Executor_roll.mode_1);
		builder.append(" (");
		builder.append(Executor_roll.mode_fucker);
		builder.append("/");
		builder.append(Executor_roll.mode_fucked);
		builder.append(")\r\n模式2 - 单限: ");
		builder.append(Executor_roll.mode_2);
		builder.append("\r\n模式3 - 双限: ");
		builder.append(Executor_roll.mode_3);
		return builder.toString();
	}

}
