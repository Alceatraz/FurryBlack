package studio.blacktech.coolqbot.furryblack.common;

/***
 * 消息处理的实用工具类
 *
 */
public class Message {

	private String rawMessage;
	public String trimedMessage;
	public String[] messages;
	public Long sendTime;

	public int length;
	public int segment;
	public boolean isCommand = false;
	public boolean isPicture = false;

	public Message(final String raw) {
		this.sendTime = System.currentTimeMillis();
		this.rawMessage = raw;
		this.length = this.rawMessage.length();
		// 此isCommand方法经过实际测试 性能最优
		this.isCommand = (this.length > 2) && (this.rawMessage.startsWith("//"));
		if (this.isCommand) {
			this.trimedMessage = this.rawMessage.trim().substring(2);
			this.messages = this.trimedMessage.split(" ");
			this.segment = this.messages.length;
		} else {
			this.isPicture = (this.length == 52) && (this.rawMessage.startsWith("[CQ:image,file="));
		}
	}

	/***
	 * 从指定开始位置用空格拼接
	 *
	 * @param i 从第几位置开始
	 * @return 拼接后的信息
	 */
	public String join(int i) {
		if (this.segment == 1) {
			return "";
		} else {
			final StringBuilder builder = new StringBuilder();
			for (; i < this.segment; i++) {
				builder.append(" ");
				builder.append(this.messages[i]);
			}
			return builder.toString();
		}
	}

	@Override
	public String toString() {
		return this.rawMessage;
	}

	public String rawMessage() {
		return this.rawMessage;
	}
}
