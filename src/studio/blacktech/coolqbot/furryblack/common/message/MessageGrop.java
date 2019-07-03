package studio.blacktech.coolqbot.furryblack.common.message;

public class MessageGrop extends Message {

	private long gropid = 0;
	private long userid = 0;

	public MessageGrop(final long gropid, final long userid, final String message, final int messageid, final int messageFont) {
		super(message, messageid, messageFont);
		this.gropid = gropid;
		this.userid = userid;
	}

	public long gropid() {
		return this.gropid;
	}

	public long userid() {
		return this.userid;
	}
}
