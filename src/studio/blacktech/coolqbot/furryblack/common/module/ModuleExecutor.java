package studio.blacktech.coolqbot.furryblack.common.module;

import studio.blacktech.coolqbot.furryblack.common.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;

public abstract class ModuleExecutor extends Module {

	// @formatter:off
	public ModuleExecutor(
			String MODULE_PACKAGENAME,
			String MODULE_COMMANDNAME,
			String MODULE_DISPLAYNAME,
			String MODULE_DESCRIPTION,
			String MODULE_VERSION,
			String[] MODULE_USAGE,
			String[] MODULE_PRIVACY_TRIGER,
			String[] MODULE_PRIVACY_LISTEN,
			String[] MODULE_PRIVACY_STORED,
			String[] MODULE_PRIVACY_CACHED,
			String[] MODULE_PRIVACY_OBTAIN
	) throws Exception {
		super(
			MODULE_PACKAGENAME,
			MODULE_COMMANDNAME,
			MODULE_DISPLAYNAME,
			MODULE_DESCRIPTION,
			MODULE_VERSION,
			MODULE_USAGE,
			MODULE_PRIVACY_TRIGER,
			MODULE_PRIVACY_LISTEN,
			MODULE_PRIVACY_STORED,
			MODULE_PRIVACY_CACHED,
			MODULE_PRIVACY_OBTAIN
		);
	}
	// @formatter:on

	public int COUNT_USER = 0;
	public int COUNT_DISZ = 0;
	public int COUNT_GROP = 0;

	public boolean ENABLE_USER = false;
	public boolean ENABLE_DISZ = false;
	public boolean ENABLE_GROP = false;

	public abstract void init(LoggerX logger) throws Exception;

	public abstract void boot(LoggerX logger) throws Exception;

	public abstract void shut(LoggerX logger) throws Exception;

	public abstract void reload(LoggerX logger) throws Exception;

	public abstract boolean doUserMessage(int typeid, long userid, MessageUser message, int messageid, int messagefont) throws Exception;

	public abstract boolean doDiszMessage(long diszid, long userid, MessageDisz message, int messageid, int messagefont) throws Exception;

	public abstract boolean doGropMessage(long gropid, long userid, MessageGrop message, int messageid, int messagefont) throws Exception;

	public boolean executeUserMessage(int typeid, long userid, MessageUser message, int messageid, int messagefont) throws Exception {
		this.COUNT_USER++;
		return this.doUserMessage(typeid, userid, message, messageid, messagefont);
	}

	public boolean executeDiszMessage(long diszid, long userid, MessageDisz message, int messageid, int messagefont) throws Exception {
		this.COUNT_DISZ++;
		return this.doDiszMessage(diszid, userid, message, messageid, messagefont);
	}

	public boolean executeGropMessage(long gropid, long userid, MessageGrop message, int messageid, int messagefont) throws Exception {
		this.COUNT_GROP++;
		return this.doGropMessage(gropid, userid, message, messageid, messagefont);
	}

}