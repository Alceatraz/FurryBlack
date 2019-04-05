package studio.blacktech.coolqbot.furryblack.module;

public abstract class ModuleListener extends Module {

	public abstract boolean doUserMessage(int typeid, long userid, Message message, int messageid, int messagefont) throws Exception;

	public abstract boolean doDiszMessage(long diszid, long userid, Message message, int messageid, int messagefont) throws Exception;

	public abstract boolean doGropMessage(long gropid, long userid, Message message, int messageid, int messagefont) throws Exception;

}
