package studio.blacktech.coolqbot.furryblack.module;

public abstract class ModuleExecutor extends Module {

	public abstract boolean doUserMessage(int typeid, long userid, Message message, int messageid, int messagefont) throws Exception;

	public abstract boolean doDiszMessage(long diszid, long userid, Message message, int messageid, int messagefont) throws Exception;

	public abstract boolean doGropMessage(long gropid, long userid, Message message, int messageid, int messagefont) throws Exception;

	public boolean excuteUserMessage(final int typeid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		this.COUNT++;
		return this.doUserMessage(typeid, userid, message, messageid, messagefont);
	}

	public boolean executeDiszMessage(final long diszid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		this.COUNT++;
		return this.doDiszMessage(diszid, userid, message, messageid, messagefont);
	}

	public boolean excuteGropMessage(final long gropid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		this.COUNT++;
		return this.doGropMessage(gropid, userid, message, messageid, messagefont);
	}

}
