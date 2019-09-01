package studio.blacktech.coolqbot.furryblack.common.message;

public class MessageUser extends Message {

	private static final long serialVersionUID = 1L;

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

	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("Type ID: ");
		builder.append(this.typeid);
		builder.append("\nUser ID: ");
		builder.append(this.userid);
		builder.append("\n");
		builder.append(super.toString());
		return builder.toString();
	}

	public Message toMessage() {
		return this;
	}
}