package studio.blacktech.coolqbot.furryblack.modules.Executor;


import java.security.SecureRandom;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.annotation.ModuleExecutorComponent;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;


@ModuleExecutorComponent
public class Executor_roll extends ModuleExecutor {

	private static final long serialVersionUID = 1L;

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "Executor_Roll";
	private static String MODULE_COMMANDNAME = "roll";
	private static String MODULE_DISPLAYNAME = "生成随机数";
	private static String MODULE_DESCRIPTION = "生成随机数";
	private static String MODULE_VERSION = "1.1.0";
	private static String[] MODULE_USAGE = new String[] {
			"/roll - 抽取真假", "/roll 数字 - 从零到给定数字任选一个数字", "/roll 数字 数字 - 从给定两个数字中间抽取一个"
	};
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
		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	@Override
	public boolean init() throws Exception {
		ENABLE_USER = true;
		ENABLE_DISZ = true;
		ENABLE_GROP = true;
		return true;
	}


	@Override
	public boolean boot() throws Exception {
		return true;
	}


	@Override
	public boolean save() throws Exception {
		return true;
	}


	@Override
	public boolean shut() throws Exception {
		return true;
	}


	@Override
	public String[] exec(Message message) throws Exception {
		return new String[] {
				"此模块无可用命令"
		};
	}


	@Override
	public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) {

	}

	@Override
	public void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) {

	}

	@Override
	public boolean doUserMessage(MessageUser message) throws Exception {
		long userid = message.getUserID();
		entry.userInfo(userid, roll(message));
		return true;

	}

	@Override
	public boolean doDiszMessage(MessageDisz message) throws Exception {
		long diszid = message.getDiszID();
		long userid = message.getUserID();
		entry.diszInfo(diszid, userid, roll(message));
		return true;

	}

	@Override
	public boolean doGropMessage(MessageGrop message) throws Exception {
		long gropid = message.getGropID();
		long userid = message.getUserID();
		entry.gropInfo(gropid, userid, roll(message));
		return true;

	}

	private String roll(Message message) {

		String res = null;
		SecureRandom random = new SecureRandom();

		switch (message.getParameterSection()) {

		// ============================================================

		case 0:
			if (random.nextBoolean()) {
				mode_fucker++;
				res = " [CQ:emoji,id=10000049]";
			} else {
				mode_fucked++;
				res = " [CQ:emoji,id=10000048]";
			}
			mode_1++;
			break;

		// ============================================================

		case 1:
			int range = 100;
			try {
				range = Integer.parseInt(message.getParameterSegment(0));
				res = Integer.toString(random.nextInt(range));
				mode_2++;
			} catch (Exception exce) {
				res = message.getParameterSection() + " 是 ";
				if (random.nextBoolean()) {
					mode_fucker++;
					res = res + "[CQ:emoji,id=10000049]";
				} else {
					mode_fucked++;
					res = res + "[CQ:emoji,id=10000048]";
				}
				mode_1++;
			}
			break;

		// ============================================================

		case 2:
			int min = 100;
			int max = 200;
			try {
				min = Integer.parseInt(message.getParameterSegment(0));
				max = Integer.parseInt(message.getParameterSegment(1));
			} catch (Exception exce) {
				return "参数必须是罗马数字";
			}
			int temp = random.nextInt(max);
			if (temp < min) { temp = temp / max * (max - min) + min; }
			res = Integer.toString(temp);
			mode_3++;
			break;
		}


		return res;

	}

	@Override
	public String[] generateReport(Message message) {

		if (COUNT_USER + COUNT_DISZ + COUNT_GROP == 0) { return null; }
		StringBuilder builder = new StringBuilder();
		builder.append("模式1 - 真假: ");
		builder.append(mode_1);
		builder.append(" (");
		builder.append(mode_fucker);
		builder.append("/");
		builder.append(mode_fucked);
		builder.append(")\r\n模式2 - 单限: ");
		builder.append(mode_2);
		builder.append("\r\n模式3 - 双限: ");
		builder.append(mode_3);
		String[] res = new String[] {
				builder.toString()
		};
		return res;

	}

}
