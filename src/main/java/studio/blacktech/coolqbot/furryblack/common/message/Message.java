package studio.blacktech.coolqbot.furryblack.common.message;


import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import studio.blacktech.coolqbot.furryblack.common.message.type.MessageType;


public class Message implements Serializable {


	private static final long serialVersionUID = 1L;

	private final static String REGEX_IMAGE = "\\[CQ:image,file=\\w{32}\\.\\w{3}\\]";

	private int messageId = 0;
	private int messageFont = 0;
	private long sendTime = 0;

	private String rawMessage = null;
	private String resMessage = null;

	private String commandName = null;
	private String commandBody = null;

	private int section = 0;
	private String[] segment = null;

	private LinkedList<String> segmentParts;
	private LinkedHashMap<String, String> switchs;

	private int rawLength = 0;
	private int resLength = 0;


	private MessageType type = MessageType.Normal;


	// ===================================================================================

	public Message(String rawMessage, int messageId, int messageFont) {

		this.sendTime = System.currentTimeMillis();
		this.messageId = messageId;
		this.messageFont = messageFont;
		this.rawMessage = rawMessage;

		rawLength = rawMessage.length();

		if (rawMessage.matches("/[a-z]+.*")) this.type = MessageType.Command;

	}

	public Message extractCommand() {

		if (!(type == MessageType.Command)) return this;

		boolean isFeild = false;
		boolean isEscape = false;

		int sliceCount = 0;
		int length = rawLength;

		StringBuilder builder = new StringBuilder();
		LinkedList<String> temp = new LinkedList<>();

		// /admin exec --module=shui eval --mode=sql --command=`SELECT * FROM \`chat_record\` LIMIT 10`
		// FurryBlack 将 `` 作为整段包裹符号 其中的内容将被保留 注：PgSQL并不用 `

		loop: for (int pointer = 1; pointer < length; pointer++) {

			char chat = rawMessage.charAt(pointer);

			switch (chat) {
				case '\\': // 启动对下一个字符的转义
					if (isEscape) {
						// TODO: 如果是 \\ \\\ \\\\ \\\\\\\\\ 怎么办
					} else {
						isEscape = true;
					}
					break;

				case '`':
					if (isEscape) { // 开启了转义 不对feild状态进行操作
						builder.append('`');
					} else {
						isFeild = !isFeild;
					}
					isEscape = false;
					break;

				case ' ':
					if (isFeild) { // feild范围内不按空格进行拆分
						builder.append(chat);
					} else {
						sliceCount = sliceCount + 1;
						temp.add(builder.toString());
						builder.setLength(0);
						builder.append(chat);
					}
					isEscape = false;
					break;

				default:
					builder.append(chat);
					isEscape = false;
					break;
			}
			continue loop;
		}
		temp.add(builder.toString());
		segment = temp.toArray(new String[] {});
		return this;
	}


	// ===================================================================================

	/**
	 * 将消息去掉命令以后 从指定位置拼接
	 *
	 * @param i index位置
	 * @return 拼接后的内容
	 */
	public String join(int i) {
		if (section == 0) {
			return "";
		} else {
			StringBuilder builder = new StringBuilder();
			for (; i < section; i++) {
				builder.append(segment[i] + " ");
			}
			return builder.substring(0, builder.length() - 1);
		}
	}


	// ===================================================================================


	/**
	 * 获取消息ID
	 *
	 * @return id
	 */
	public int getMessageId() {
		return messageId;
	}


	/**
	 * 获取消息字体
	 *
	 * @return fontid
	 */
	public int getMessageFont() {
		return messageFont;
	}


	// ===================================================================================


	/**
	 * 获取消息发送时间 毫秒时间戳
	 *
	 * @return currentTimeMillis
	 */
	public long getSendtime() {
		return sendTime;
	}

	@Override
	public String toString() {
		return Arrays.toString(segment);
	}

}
