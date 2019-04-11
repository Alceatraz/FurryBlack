package studio.blacktech.coolqbot.furryblack.module;

/***
 * 消息处理的实用工具类
 *
 */
public class Message {

	public String raw;
	public String res;
	public int segment;
	public int length;
	public String[] messages;
	public Long time;

	public Message(final String raw) {
		this.raw = raw;
	}

	/***
	 * 按空格拆解消息
	 *
	 * @return 返回0字段 即命令
	 */
	public String prase() {
		this.res = this.raw.trim().substring(2);
		this.messages = this.res.split(" ");
		this.segment = this.messages.length;
		return this.messages[0];
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

	/***
	 * 不是//开头的不是命令 只有//的不是命令
	 *
	 * @return
	 */
	public boolean isCommand() {
		return (this.raw.startsWith("//") && (this.raw.length() > 2));
	}

	public void calculateLength() {
		this.length = this.raw.length();
	}

}
