package studio.blacktech.coolqbot.furryblack.modules;

import studio.blacktech.coolqbot.furryblack.common.Message;
import studio.blacktech.coolqbot.furryblack.common.Module;
import studio.blacktech.coolqbot.furryblack.common.ModuleExecutor;

public class Executor_dice extends ModuleExecutor {

	public Executor_dice() {

		this.MODULE_DISPLAYNAME = "������";
		this.MODULE_PACKAGENAME = "dice";
		this.MODULE_DESCRIPTION = "����һ�����ӱ���";
		this.MODULE_VERSION = "2.0.3";
		this.MODULE_USAGE = new String[] {
				"//dice - ����һ��ħ������",
				"//dice ���� - Ϊĳ��Ͷ��һö����"
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
	public String[] generateReport(final int logLevel, final int logMode, final int typeid, final long userid, final long diszid, final long gropid, final Message message, final Object... parameters) {
		return null;
	}

	@Override
	public boolean doUserMessage(final int typeid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		Module.userInfo(userid, message.length == 1 ? "[CQ:dice]" : message.join(1) + "[CQ:dice]");
		return true;
	}

	@Override
	public boolean doDiszMessage(final long diszid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		Module.diszInfo(diszid, userid, message.length == 1 ? "[CQ:dice]" : message.join(1) + "[CQ:dice]");
		return true;
	}

	@Override
	public boolean doGropMessage(final long gropid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		Module.gropInfo(gropid, userid, message.length == 1 ? "[CQ:dice]" : message.join(1) + "[CQ:dice]");
		return true;
	}
}
