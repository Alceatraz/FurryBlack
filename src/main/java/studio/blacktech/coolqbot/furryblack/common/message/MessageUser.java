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

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("============================================\n");
		builder.append("类型ID: " + this.typeid + "\n");
		builder.append("用户ID: " + this.userid + "\n");
		builder.append(super.toString());
		return builder.toString();
	}

	public Message toMessage() {
		return this;
	}
}