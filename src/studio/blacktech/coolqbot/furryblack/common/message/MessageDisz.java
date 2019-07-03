package studio.blacktech.coolqbot.furryblack.common.message;

public class MessageDisz extends Message {

	private long diszid = 0;
	private long userid = 0;

	public MessageDisz(final long diszid, final long userid, final String message, final int messageid, final int messageFont) {
		super(message, messageid, messageFont);
		this.diszid = diszid;
		this.userid = userid;
	}

	public long diszid() {
		return this.diszid;
	}

	public long userid() {
		return this.userid;
	}
}
