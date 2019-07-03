package studio.blacktech.coolqbot.furryblack.common.module;

import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;

public abstract class ModuleListener extends Module {

	// @formatter:off
	public ModuleListener(
			String MODULE_PACKAGENAME,
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
			MODULE_DISPLAYNAME,
			MODULE_PACKAGENAME,
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

	public boolean ENABLE_USER = false;
	public boolean ENABLE_DISZ = false;
	public boolean ENABLE_GROP = false;

	public abstract boolean doUserMessage(int typeid, long userid, MessageUser message, int messageid, int messagefont) throws Exception;

	public abstract boolean doDiszMessage(long diszid, long userid, MessageDisz message, int messageid, int messagefont) throws Exception;

	public abstract boolean doGropMessage(long gropid, long userid, MessageGrop message, int messageid, int messagefont) throws Exception;

	public boolean executeUserMessage(final int typeid, final long userid, final MessageUser message, final int messageid, final int messagefont) throws Exception {
		this.COUNT++;
		return this.doUserMessage(typeid, userid, message, messageid, messagefont);
	}

	public boolean executeDiszMessage(final long diszid, final long userid, final MessageDisz message, final int messageid, final int messagefont) throws Exception {
		this.COUNT++;
		return this.doDiszMessage(diszid, userid, message, messageid, messagefont);
	}

	public boolean executeGropMessage(final long gropid, final long userid, final MessageGrop message, final int messageid, final int messagefont) throws Exception {
		this.COUNT++;
		return this.doGropMessage(gropid, userid, message, messageid, messagefont);
	}

}
