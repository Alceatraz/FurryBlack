package studio.blacktech.coolqbot.furryblack.modules;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;

public class Executor_dice extends ModuleExecutor {

	// ==========================================================================================================================================================
	//
	// ģ���������
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "executor_dice";
	private static String MODULE_COMMANDNAME = "dice";
	private static String MODULE_DISPLAYNAME = "������";
	private static String MODULE_DESCRIPTION = "����һ�����ӵ�ħ������";
	private static String MODULE_VERSION = "1.0";
	private static String[] MODULE_USAGE = new String[] {
			"/dice - ����һ��ħ������",
			"/dice ���� - Ϊĳ��Ͷ��һö����"
	};
	private static String[] MODULE_PRIVACY_TRIGER = new String[] {};
	private static String[] MODULE_PRIVACY_LISTEN = new String[] {};
	private static String[] MODULE_PRIVACY_STORED = new String[] {};
	private static String[] MODULE_PRIVACY_CACHED = new String[] {};
	private static String[] MODULE_PRIVACY_OBTAIN = new String[] {
			"��ȡ�������"
	};

	// ==========================================================================================================================================================
	//
	// ��Ա����
	//
	// ==========================================================================================================================================================

	// ==========================================================================================================================================================
	//
	// �������ں���
	//
	// ==========================================================================================================================================================

	public Executor_dice() throws Exception {
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
		entry.getMessage().userInfo(userid, message.getSection() == 1 ? "[CQ:dice]" : message.getOptions() + "[CQ:dice]");
		return true;
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, MessageDisz message, int messageid, int messagefont) throws Exception {
		entry.getMessage().diszInfo(diszid, userid, message.getSection() == 1 ? "[CQ:dice]" : message.getOptions() + "[CQ:dice]");
		return true;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, MessageGrop message, int messageid, int messagefont) throws Exception {
		entry.getMessage().gropInfo(gropid, userid, message.getSection() == 1 ? "[CQ:dice]" : message.getOptions() + "[CQ:dice]");
		return true;
	}

	// ==========================================================================================================================================================
	//
	// ���ߺ���
	//
	// ==========================================================================================================================================================

	@Override
	public String[] generateReport(int mode, Message message, Object... parameters) {
		return null;
	}

}