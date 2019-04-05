package studio.blacktech.coolqbot.furryblack.module;

public abstract class ModuleLogic extends Module {

	private static int COUNT = 0;

	public abstract boolean doCommandUserMessage(int typeid, int userid, Message message, int messageid, int messagefont) throws Exception;

	public abstract boolean doCommandDiszMessage(int diszid, int userid, Message message, int messageid, int messagefont) throws Exception;

	public abstract boolean doCommandGropMessage(int gropid, int userid, Message message, int messageid, int messagefont) throws Exception;

	public boolean excuteCommandUserMessage(int typeid, int userid, String message, int messageid, int messagefont) throws Exception {
		COUNT++;
		return doCommandUserMessage(typeid, userid, new Message(message), messageid, messagefont);
	}

	public boolean excutCommandDiszMessage(int diszid, int userid, String message, int messageid, int messagefont) throws Exception {
		COUNT++;
		return doCommandDiszMessage(diszid, userid, new Message(message), messageid, messagefont);
	}

	public boolean excuteCommandGropMessage(int gropid, int userid, String message, int messageid, int messagefont) throws Exception {
		COUNT++;
		return doCommandGropMessage(gropid, userid, new Message(message), messageid, messagefont);
	}

}
