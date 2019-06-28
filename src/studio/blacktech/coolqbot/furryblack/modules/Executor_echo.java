package studio.blacktech.coolqbot.furryblack.modules;

import studio.blacktech.coolqbot.furryblack.common.Message;
import studio.blacktech.coolqbot.furryblack.common.Module;
import studio.blacktech.coolqbot.furryblack.common.ModuleExecutor;

public class Executor_echo extends ModuleExecutor {

	public Executor_echo() {
		this.MODULE_PACKAGENAME = "echo";
		this.MODULE_DISPLAYNAME = "回显";
		this.MODULE_DESCRIPTION = "回显内容";
		this.MODULE_VERSION = "2.0.1";
		this.MODULE_USAGE = new String[] {
				"//echo 内容 - stdin>stdout"
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
		Module.userInfo(userid, message.length == 1 ? "echo" : message.join(1));
		return true;
	}

	@Override
	public boolean doDiszMessage(final long diszid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		Module.diszInfo(diszid, userid, message.length == 1 ? "echo" : message.join(1));
		return true;
	}

	@Override
	public boolean doGropMessage(final long gropid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		Module.gropInfo(gropid, userid, message.length == 1 ? "echo" : message.join(1));
		return true;
	}

}
