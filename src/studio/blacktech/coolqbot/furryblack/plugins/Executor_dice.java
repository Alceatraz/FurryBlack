package studio.blacktech.coolqbot.furryblack.plugins;

import studio.blacktech.coolqbot.furryblack.module.Message;
import studio.blacktech.coolqbot.furryblack.module.Module;
import studio.blacktech.coolqbot.furryblack.module.ModuleExecutor;

@SuppressWarnings("unused")
public class Executor_dice extends ModuleExecutor {

	private String MODULE_DISPLAYNAME = "������";
	private String MODULE_PACKAGENAME = "dice";
	private String MODULE_DESCRIPTION = "����һ�����ӱ���";
	private String MODULE_VERSION = "2.0.3";
	private String[] MODULE_USAGE = {
			"//dice", "//dice Ͷ������"
	};
	private String[] MODULE_PRIVACY_LISTEN = {};
	private String[] MODULE_PRIVACY_EVENTS = {};
	private String[] MODULE_PRIVACY_STORED = {};
	private String[] MODULE_PRIVACY_CACHED = {};
	private String[] MODULE_PRIVACY_OBTAIN = {
			"��ȡ�������"
	};

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
