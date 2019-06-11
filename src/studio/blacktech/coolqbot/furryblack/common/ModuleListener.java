package studio.blacktech.coolqbot.furryblack.common;

public abstract class ModuleListener extends Module {

	public boolean ENABLE_USER = true;
	public boolean ENABLE_DISZ = true;
	public boolean ENABLE_GROP = true;

	public abstract boolean doUserMessage(int typeid, long userid, Message message, int messageid, int messagefont) throws Exception;

	public abstract boolean doDiszMessage(long diszid, long userid, Message message, int messageid, int messagefont) throws Exception;

	public abstract boolean doGropMessage(long gropid, long userid, Message message, int messageid, int messagefont) throws Exception;

	public boolean executeUserMessage(final int typeid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		this.COUNT++;
		return this.doUserMessage(typeid, userid, message, messageid, messagefont);
	}

	public boolean executeDiszMessage(final long diszid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		this.COUNT++;
		return this.doDiszMessage(diszid, userid, message, messageid, messagefont);
	}

	public boolean executeGropMessage(final long gropid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		this.COUNT++;
		return this.doGropMessage(gropid, userid, message, messageid, messagefont);
	}

}
