package studio.blacktech.coolqbot.furryblack.plugins;

import studio.blacktech.coolqbot.furryblack.module.Message;
import studio.blacktech.coolqbot.furryblack.module.Module;
import studio.blacktech.coolqbot.furryblack.module.ModuleExecutor;

public class Executor_dice extends ModuleExecutor {

	public Executor_dice() {

		this.MODULE_DISPLAYNAME = "������";
		this.MODULE_PACKAGENAME = "dice";
		this.MODULE_DESCRIPTION = "����һ�����ӱ���";
		this.MODULE_VERSION = "2.0.3";
		this.MODULE_USAGE = new String[] {
				"//dice", "//dice Ͷ������"
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
	public boolean doUserMessage(int typeid, long userid, Message message, int messageid, int messagefont) throws Exception {
		Module.userInfo(userid, message.length == 1 ? "[CQ:dice]" : message.join(1) + "[CQ:dice]");
		return true;
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, Message message, int messageid, int messagefont) throws Exception {
		Module.diszInfo(diszid, userid, message.length == 1 ? "[CQ:dice]" : message.join(1) + "[CQ:dice]");
		return true;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, Message message, int messageid, int messagefont) throws Exception {
		Module.gropInfo(gropid, userid, message.length == 1 ? "[CQ:dice]" : message.join(1) + "[CQ:dice]");
		return true;
	}

	@Override
	public String getReport() {
		return null;
	}
}
