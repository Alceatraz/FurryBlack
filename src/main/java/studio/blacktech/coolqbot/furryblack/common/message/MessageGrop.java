package studio.blacktech.coolqbot.furryblack.common.message;


public class MessageGrop extends Message {

	private static final long serialVersionUID = 1L;

	private long gropid = 0;
	private long userid = 0;


	public MessageGrop(long gropid, long userid, String message, int messageid, int messageFont) {

		super(message, messageid, messageFont);
		this.gropid = gropid;
		this.userid = userid;

	}

	public long gropid() {

		return gropid;

	}

	public long userid() {

		return userid;

	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("============================================\n");
		builder.append("群聊ID: " + gropid + "\n");
		builder.append("用户ID: " + userid + "\n");
		builder.append(super.toString());
		return builder.toString();

	}

	public Message toMessage() {

		return this;

	}

}
