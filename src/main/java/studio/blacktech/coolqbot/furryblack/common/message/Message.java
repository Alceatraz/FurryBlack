package studio.blacktech.coolqbot.furryblack.common.message;


import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import studio.blacktech.coolqbot.furryblack.common.message.type.MessageType;


public class Message implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Pattern pattern = Pattern.compile("^/[a-z]+"); // 贪婪模式 直到空格为止 作为命令名

	private int messageId = 0;
	private int messageFont = 0;
	private long sendTime = 0;


	private String message = null;
	private String content = null;

	private String commandName = null;
	private String commandBody = null;

	private int section = 0;
	private String[] parameter = null;

	private LinkedHashMap<String, String> switchs;

	private MessageType type = MessageType.Normal;

	private String[] at;
	private String[] rps;
	private String[] dice;
	private String[] image;
	private String[] face;
	private String[] sface;
	private String[] bface;
	private String[] emoji;

	// ===================================================================================


	public Message(String rawMessage) {
		this(rawMessage, 0, 0);
	}


	public Message(String rawMessage, int messageId, int messageFont) {

		sendTime = System.currentTimeMillis();
		this.messageId = messageId;
		this.messageFont = messageFont;
		message = rawMessage;

		Matcher matcher = pattern.matcher(message);

		if (matcher.find()) {

			commandName = matcher.group().substring(1);
			type = MessageType.Command;

			if (message.indexOf(' ') < 0) {
				section = 0;
				return;
			}

			commandBody = message.substring(commandName.length() + 1);

			boolean isFeild = false;
			boolean isEscape = false;

			StringBuilder builder = new StringBuilder();
			LinkedList<String> temp = new LinkedList<>();

			int length = commandBody.length();

			/*
			 * /admin exec --module=shui eval --mode=sql --command=`SELECT * FROM \`chat_record\` LIMIT 10`
			 * 将`作为包裹符号其中的空格不进行拆分 对\进行查询\`是转义
			 * 注：PgSQL并不用 `
			 */

			loop: for (int pointer = 1; pointer < length; pointer++) {

				char chat = commandBody.charAt(pointer);

				switch (chat) {

				case '\\': // 启动对下一个字符的转义
					if (isEscape) {
						builder.append("\\" + chat);
						isEscape = false;
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
						if (builder.length() == 0) continue;
						temp.add(builder.toString());
						builder.setLength(0);
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

			LinkedList<String> result = new LinkedList<>();

			for (String slice : temp) {

				if (slice.startsWith("--")) {

					if (switchs == null) switchs = new LinkedHashMap<>();

					slice = slice.substring(2);
					int index = slice.indexOf("=");

					if (index > 0) {
						switchs.put(slice.substring(0, index), slice.substring(index + 1)); // --XXXX=XXXX 开关参数
					} else {
						switchs.put(slice, null); // --XXXX 开关
					}
				} else {
					result.add(slice); // 提取所有其他内容为参数
				}
			}

			parameter = result.toArray(new String[] {});
			section = parameter.length;

		} else if (message.startsWith("&#91;闪照&#93;")) {
			type = MessageType.SnapShot;
		} else if (message.startsWith("&#91;视频&#93;")) {
			type = MessageType.TapVideo;
		} else if (message.startsWith("&#91;QQ红包&#93;")) {
			type = MessageType.Envelope;
		} else if (message.startsWith("&#91;涂鸦&#93;")) {
			type = MessageType.Scrawls;
		} else if (message.startsWith("&#91;礼物&#93;")) {
			type = MessageType.Present;
		} else if (message.startsWith("&#91;开启一起听歌&#93;")) {
			type = MessageType.SyncMusic;
		} else {

			if (!message.contains("[CQ:")) return;

			at = extract("(\\[CQ:at,qq=)(\\d+?)(])", 2); // 提取所有 @
			rps = extract("(\\[CQ:rps,type=)(\\d)(])", 2); // 提取所有猜拳
			dice = extract("(\\[dice,type=)(\\d)(])", 2); // 提取所有骰子
			face = extract("(\\[CQ:face,id=)(\\d+?)(])", 2); // 提取所有表情
			sface = extract("\\[CQ:sface,p=\\d+?,id=\\w{32}]"); // 提取所有小表情
			bface = extract("\\[CQ:bface,p=\\d+?,id=\\w{32}]"); // 提取所有大表情（原创表情s）
			emoji = extract("(\\[CQ:emoji,id=)(\\d+?)(])", 2); // 提取所有emoji
			image = extract("\\[CQ:image,file=\\w{32}\\.\\w{3}\\]"); // 提取所有图片

			// 剔除掉所有CQ码 合并多个空格
			content = message.replaceAll("\\[CQ:.+?\\]", "");
			content = content.replaceAll("\\s+", " ");

			// 最终剩下一堆空格\s按照纯码处理
			if (content.length() == 0 || content.matches("\\s+")) type = MessageType.PureCode;
		}
	}


	private String[] extract(String regex) {

		LinkedList<String> temp = new LinkedList<>();

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(message);

		while (matcher.find()) temp.add(matcher.group());

		return temp.toArray(new String[] {});
	}


	private String[] extract(String regex, int group) {

		LinkedList<String> temp = new LinkedList<>();

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(message);

		while (matcher.find()) temp.add(matcher.group(group));

		return temp.toArray(new String[] {});
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
				builder.append(parameter[i] + " ");
			}
			return builder.substring(0, builder.length() - 1);
		}
	}


	// ===================================================================================


	public long getSendtime() {
		return sendTime;
	}


	public int getMessageID() {
		return messageId;
	}


	public int getMessageFont() {
		return messageFont;
	}


	public String getMessage() {
		return message;
	}

	public String getContent() {
		return content;
	}

	public MessageType getType() {
		return type;
	}

	// ===================================================================================


	public boolean isCommand() {
		return type == MessageType.Command;
	}


	public String getCommandName() {
		return commandName;
	}


	public String getCommandBody() {
		return commandBody;
	}


	public int getParameterSection() {
		return section;
	}


	public String[] getParameterSegment() {
		if (parameter == null) return null;
		return parameter;
	}


	public String getParameterSegment(int i) {
		if (parameter == null) return null;
		return parameter[i];
	}


	public boolean hasSwitch(String name) {
		if (switchs == null) return false;
		return switchs.containsKey(name);
	}

	public String getSwitch(String name) {
		return switchs.get(name);
	}


	// ===================================================================================


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (isCommand()) {
			builder.append(type.getName()).append("|").append(section).append("=").append(message).append("\n");
			for (int i = 0; i < section; i++) builder.append(i).append(":").append(parameter[i]).append("\n");
			if (switchs != null) switchs.forEach((key, value) -> builder.append(key).append("=").append(value).append("\n"));
			builder.setLength(builder.length() - 1);
		} else {
			builder.append(type.getName()).append(" - ").append(message);
		}
		return builder.toString();
	}


	// ===================================================================================


	public String[] getAt() {
		return at;
	}


	public String[] getRps() {
		return rps;
	}


	public String[] getDice() {
		return dice;
	}


	public String[] getImage() {
		return image;
	}

	public String[] getFace() {
		return face;
	}


	public String[] getSface() {
		return sface;
	}


	public String[] getBface() {
		return bface;
	}


	public String[] getEmoji() {
		return emoji;
	}


	public boolean hasAt() {
		return at != null;
	}


	public boolean hasRps() {
		return rps != null;
	}


	public boolean hasDice() {
		return dice != null;
	}


	public boolean hasImage() {
		return image != null;
	}


	public boolean hasFace() {
		return face != null;
	}


	public boolean hasBface() {
		return bface != null;
	}


	public boolean hasSface() {
		return sface != null;
	}


	public boolean hasEmoji() {
		return emoji != null;
	}
}
