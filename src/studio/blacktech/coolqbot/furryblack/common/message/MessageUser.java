package studio.blacktech.coolqbot.furryblack.common.message;

public class MessageUser extends Message {

	private long typeid = 0;
	private long userid = 0;

	public MessageUser(int typeid, long userid, String message, int messageid, int messageFont) {
		super(message, messageid, messageFont);
		this.typeid = typeid;
		this.userid = userid;
	}

	public long typeid() {
		return this.typeid;
	}

	public long userid() {
		return this.userid;
	}
}