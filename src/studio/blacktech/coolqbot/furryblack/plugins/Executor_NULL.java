package studio.blacktech.coolqbot.furryblack.plugins;

import studio.blacktech.coolqbot.furryblack.module.Message;
import studio.blacktech.coolqbot.furryblack.module.ModuleExecutor;

public class Executor_NULL extends ModuleExecutor {

	public Executor_NULL() {

		this.MODULE_DISPLAYNAME = "";
		this.MODULE_PACKAGENAME = "";
		this.MODULE_DESCRIPTION = "";
		this.MODULE_VERSION = "1.0.0";
		this.MODULE_USAGE = new String[] {};
		this.MODULE_PRIVACY_TRIGER = new String[] {};
		this.MODULE_PRIVACY_LISTEN = new String[] {};
		this.MODULE_PRIVACY_STORED = new String[] {};
		this.MODULE_PRIVACY_CACHED = new String[] {};
		this.MODULE_PRIVACY_OBTAIN = new String[] {};
	}

	@Override
	public boolean doUserMessage(int typeid, long userid, Message message, int messageid, int messagefont) throws Exception {

		return true;
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, Message message, int messageid, int messagefont) throws Exception {

		return true;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, Message message, int messageid, int messagefont) throws Exception {

		return true;
	}

	@Override
	public String generateReport(int logLevel, int logMode, Message message, Object[] parameters) {
		return null;
	}

}
