package studio.blacktech.coolqbot.furryblack.modules;

import java.security.SecureRandom;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;

public class Executor_roll extends ModuleExecutor {

	private static final long serialVersionUID = 1L;

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "executor_roll";
	private static String MODULE_COMMANDNAME = "roll";
	private static String MODULE_DISPLAYNAME = "生成随机数";
	private static String MODULE_DESCRIPTION = "生成随机数";
	private static String MODULE_VERSION = "1.1";
	private static String[] MODULE_USAGE = new String[] {
			"/roll - 抽取真假",
			"/roll 数字 - 从零到给定数字任选一个数字",
			"/roll 数字 数字 - 从给定两个数字中间抽取一个"
	};
	private static String[] MODULE_PRIVACY_TRIGER = new String[] {};
	private static String[] MODULE_PRIVACY_LISTEN = new String[] {};
	private static String[] MODULE_PRIVACY_STORED = new String[] {};
	private static String[] MODULE_PRIVACY_CACHED = new String[] {};
	private static String[] MODULE_PRIVACY_OBTAIN = new String[] {
			"获取命令发送人"
	};

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================

	private int mode_1 = 0;
	private int mode_2 = 0;
	private int mode_3 = 0;

	private int mode_fucked = 0;
	private int mode_fucker = 0;

	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================

	public Executor_roll() throws Exception {
		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_TRIGER, MODULE_PRIVACY_LISTEN, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	@Override
	public void init(LoggerX logger) throws Exception {
		this.ENABLE_USER = true;
		this.ENABLE_DISZ = true;
		this.ENABLE_GROP = true;
	}

	@Override
	public void boot(LoggerX logger) throws Exception {
	}

	@Override
	public void shut(LoggerX logger) throws Exception {
	}

	@Override
	public void save(LoggerX logger) throws Exception {
	}

	@Override
	public void reload(LoggerX logger) throws Exception {
	}

	@Override
	public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) {
	}

	@Override
	public void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) {
	}

	@Override
	public boolean doUserMessage(int typeid, long userid, MessageUser message, int messageid, int messagefont) throws Exception {
		entry.getMessage().userInfo(userid, this.roll(message));
		return true;
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, MessageDisz message, int messageid, int messagefont) throws Exception {
		entry.getMessage().diszInfo(diszid, userid, this.roll(message));
		return true;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, MessageGrop message, int messageid, int messagefont) throws Exception {
		entry.getMessage().gropInfo(gropid, userid, this.roll(message));
		return true;
	}

	public String roll(Message message) {

		String res = null;
		SecureRandom random = new SecureRandom();

		switch (message.getSection()) {

		// ============================================================

		case 0:
			if (random.nextBoolean()) {
				this.mode_fucker++;
				res = "1";
			} else {
				this.mode_fucked++;
				res = "0";
			}
			this.mode_1++;
			break;

		// ============================================================

		case 1:
			int range = 100;
			try {
				range = Integer.parseInt(message.getSegment()[0]);
				res = Integer.toString(random.nextInt(range));
				this.mode_2++;
			} catch (Exception exce) {
				res = message.getSegment()[0] + "是";
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

		// ============================================================

		case 2:
			int min = 100;
			int max = 200;
			try {
				min = Integer.parseInt(message.getSegment()[0]);
				max = Integer.parseInt(message.getSegment()[1]);
			} catch (Exception exce) {
				return "参数必须是罗马数字";
			}
			int temp = random.nextInt(max);
			if (temp < min) { temp = temp / max * (max - min) + min; }
			res = Integer.toString(temp);
			this.mode_3++;
			break;
		}

		// ============================================================

		return res;
	}

	@Override
	public String[] generateReport(int mode, Message message, Object... parameters) {
		if (this.COUNT_USER + this.COUNT_DISZ + this.COUNT_GROP == 0) { return null; }
		StringBuilder builder = new StringBuilder();
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
		String res[] = new String[1];
		res[0] = builder.toString();
		return res;
	}

}
