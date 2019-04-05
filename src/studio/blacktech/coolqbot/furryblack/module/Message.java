package studio.blacktech.coolqbot.furryblack.module;

/***
 * 消息处理的实用工具类
 * 
 */
public class Message {

	public String raw;
	public String res;
	public int length;
	public String[] cmd;

	public Message(String raw) {
		this.raw = raw;
	}

	/***
	 * 按空格拆解消息
	 *
	 * @return 返回0字段 即命令
	 */
	public String prase() {
		this.res = this.raw.trim().substring(2);
		this.cmd = this.res.split(" ");
		this.length = this.cmd.length;
		return this.cmd[0];
	}

	/***
	 * 从指定开始位置用空格拼接
	 *
	 * @param i 从第几位置开始
	 * @return 拼接后的信息
	 */
	public String join(int i) {
		if (this.length == 1) {
			return "";
		} else {
			final StringBuilder builder = new StringBuilder();
			for (; i < this.length; i++) {
				builder.append(" ");
				builder.append(this.cmd[i]);
			}
			return builder.toString();
		}
	}

}
