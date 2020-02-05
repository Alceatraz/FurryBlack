package studio.blacktech.coolqbot.furryblack.common.message;


public class MessageDisz extends Message {

	private static final long serialVersionUID = 1L;
	private long diszid = 0;
	private long userid = 0;

	public MessageDisz(long diszid, long userid, String message, int messageid, int messageFont) {

		super(message, messageid, messageFont);
		this.diszid = diszid;
		this.userid = userid;

	}

	public long getDiszID() {
		return diszid;
	}

	public long getUserID() {
		return userid;
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(diszid + " ");
		builder.append(userid + " ");
		builder.append(super.toString());
		return builder.toString();

	}

	public Message toMessage() {

		return this;

	}

}
