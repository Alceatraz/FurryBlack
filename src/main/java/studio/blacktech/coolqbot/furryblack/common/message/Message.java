package studio.blacktech.coolqbot.furryblack.common.message;


import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Message implements Serializable {

	private static final long serialVersionUID = 1L;

	private int messageId = 0;
	private int messageFont = 0;
	private long sendTime = 0;

	private String message = null;

	private String commandName = null;
	private String commandBody = null;

	private int section = 0;
	private String[] parameter = null;

	private LinkedHashMap<String, String> switchs;

	private boolean isCommand = false;


	// ===================================================================================


	public Message(String rawMessage) {
		this(rawMessage, 0, 0);
	}


	public Message(String rawMessage, int messageId, int messageFont) {

		sendTime = System.currentTimeMillis();
		this.messageId = messageId;
		this.messageFont = messageFont;
		message = rawMessage;

		Pattern pattern = Pattern.compile("/[a-z]+"); // 贪婪模式 直到空格为止 作为命令名
		Matcher matcher = pattern.matcher(message);

		if (!matcher.find()) return;

		commandName = matcher.group().substring(1);
		isCommand = true;

		if (message.indexOf(' ') < 0) {
			section = 0;
			return;
		}

		commandBody = message.substring(commandName.length() + 2);

		boolean isFeild = false;
		boolean isEscape = false;

		StringBuilder builder = new StringBuilder();
		LinkedList<String> temp = new LinkedList<>();

		int length = message.length();

		/*
		 * /admin exec --module=shui eval --mode=sql --command=`SELECT * FROM \`chat_record\` LIMIT 10`
		 * 将`作为包裹符号其中的空格不进行拆分 对\进行查询\`是转义
		 * 注：PgSQL并不用 `
		 */

		loop: for (int pointer = 1; pointer < length; pointer++) {

			char chat = message.charAt(pointer);

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


	// ===================================================================================


	public boolean isCommand() {
		return isCommand;
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
		return parameter;
	}


	public String getParameterSegment(int i) {
		return parameter[i];
	}


	public boolean hasSwitch(String name) {
		return switchs.containsKey(name);
	}

	public String getSwitch(String name) {
		return switchs.get(name);
	}


	// ===================================================================================


	@Override
	public String toString() {
		return message;
	}

}
