package studio.blacktech.coolqbot.furryblack.plugins;

import studio.blacktech.coolqbot.furryblack.module.Message;
import studio.blacktech.coolqbot.furryblack.module.Module;
import studio.blacktech.coolqbot.furryblack.module.ModuleExecutor;

public class Executor_echo extends ModuleExecutor {

	public Executor_echo() {
		this.MODULE_DISPLAYNAME = "����";
		this.MODULE_PACKAGENAME = "echo";
		this.MODULE_DESCRIPTION = "��������";
		this.MODULE_VERSION = "2.0.1";
		this.MODULE_USAGE = new String[] {
				"//echo ���� - stdin>stdout"
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
		Module.userInfo(userid, message.length == 1 ? "echo" : message.join(1));
		return true;
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, Message message, int messageid, int messagefont) throws Exception {
		Module.diszInfo(diszid, userid, message.length == 1 ? "echo" : message.join(1));
		return true;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, Message message, int messageid, int messagefont) throws Exception {
		Module.gropInfo(gropid, userid, message.length == 1 ? "echo" : message.join(1));
		return true;
	}

	@Override
	public String generateReport(boolean fullreport, int loglevel, Object[] parameters) {
		return null;
	}
}
