package studio.blacktech.coolqbot.furryblack.common.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import studio.blacktech.coolqbot.furryblack.common.LoggerX.LoggerX;

public class Message implements Serializable {

	private final static String REGEX_IMAGE = "\\[CQ:image,file=\\w{32}\\.\\w{3}\\]";

	private static final long serialVersionUID = 1L;

	private int messageId = 0;
	private int messageFt = 0;

	private long sendTime = 0;

	private String rawMessage = "";
	private String resMessage = "";
	private int rawLength = 0;
	private int resLength = 0;

	private String cmdMessage = "";
	private String command = "";
	private String options = "";
	private String[] segment = {};
	private String[] picture = {};
	private int section = 0;

	private boolean hasPicture = false;
	private boolean isCommand = false;
	private boolean isSnappic = false;
	private boolean isQQVideo = false;
	private boolean isHongbao = false;
	private boolean isPureCQC = false;

	private boolean parsed = false;

	private LinkedList<String> segmentParts;
	private TreeMap<String, String> switchs;

	// ===================================================================================

	public Message(String message, int messageid, int messageFont) {

		sendTime = System.currentTimeMillis();

		messageId = messageid;
		messageFt = messageFont;
		rawMessage = message;
	}

	// ===================================================================================

	public Message parse() {

		if (parsed) { return this; }

		parsed = true;

		rawLength = rawMessage.length();

		if (rawMessage.matches("/[a-z]+.*")) {

			// 居然因为这么一条鬼消息出BUG了 -> /招手[CQ:at,qq=XXXXXXXX]

			isCommand = true;

			// 去掉 /
			// 去掉首尾多余空格
			// 合并所有连续空格

			cmdMessage = rawMessage.substring(1);
			cmdMessage = cmdMessage.trim();
			cmdMessage = cmdMessage.replaceAll("\\s+", " ");

			int indexOfSpace = cmdMessage.indexOf(' ');

			// 是否无参数命令
			if (indexOfSpace < 0) {
				command = cmdMessage;
			} else {
				// 切开
				// 命令
				// 参数
				command = cmdMessage.substring(0, indexOfSpace);
				options = cmdMessage.substring(indexOfSpace + 1);

				String[] flag;
				switchs = new TreeMap<>();
				segmentParts = new LinkedList<>();

				// 提取所有 --XX=XXXX 形式的开关
				// 提取所有其他内容为参数
				for (String temp : options.split(" ")) {
					if (temp.startsWith("--") && (temp.indexOf("=") > 0)) {
						temp = temp.substring(2);
						flag = temp.split("=");
						switchs.put(flag[0], flag[1]);
					} else {
						segmentParts.add(temp);
					}
				}

				segment = new String[segmentParts.size()];
				segmentParts.toArray(segment);
				section = segment.length;
			}

		} else if (rawMessage.startsWith("&#91;闪照&#93;")) {
			isSnappic = true;
		} else if (rawMessage.startsWith("&#91;视频&#93;")) {
			isQQVideo = true;
		} else if (rawMessage.startsWith("&#91;QQ红包&#93;")) {
			isHongbao = true;
		} else {
			// 如果是普通消息

			// 提取所有图片
			Pattern pattern = Pattern.compile(Message.REGEX_IMAGE);
			Matcher matcher = pattern.matcher(rawMessage);
			ArrayList<String> temp = new ArrayList<>(1);
			if (matcher.find()) {
				hasPicture = true;
				do {
					temp.add(matcher.group());
				} while (matcher.find());
				picture = new String[temp.size()];
				temp.toArray(picture);
			}

			// 删除所有CQ码
			// 删除所有空白字符
			resMessage = rawMessage.replaceAll("\\[CQ:.+\\]", "").trim();
			resMessage = resMessage.replaceAll("\\s+", "").trim();

			resLength = resMessage.length();

			// 删除所有空白字符以后长度为0 则不视为正常消息
			// 比如@时会在最后自动加一个空格
			// [CQ:at=1234567890]□
			// 多次连续at会产生多个空格，不应用 ==" " 判断

			if (resLength == 0) { isPureCQC = true; }
		}

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

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();

		builder.append("============================================\n");
		builder.append("时间戳：" + LoggerX.datetime(new Date(sendTime)) + "(" + sendTime + ")" + "\n");
		builder.append("消息ID：" + messageId + "\n");
		builder.append("字体ID：" + messageFt + "\n");

		builder.append("============================================\n");
		builder.append("原始内容：" + rawMessage + "\n");
		builder.append("原始长度：" + rawLength + "\n");
		builder.append("原始编码：");
		for (int i = 0; i < rawLength; i++) {
			builder.append("\\u");
			builder.append(Integer.toHexString(rawMessage.charAt(i) & 0xffff));
		}
		builder.append("\n");

		builder.append("============================================\n");
		builder.append("是否命令：" + (isCommand ? "True" : "False") + "\n");

		if (isCommand) {

			builder.append("============================================\n");
			builder.append("命令内容：" + cmdMessage + "\n");
			builder.append("命令名字：" + command + "\n");
			builder.append("命令参数：" + options + "\n");
			builder.append("参数长度：" + section + "\n");

			if (section > 0) {
				builder.append("============================================\n");
				builder.append("参数内容: \n");
				for (String temp : segment) {
					builder.append(temp + "\n");
				}
			}

			if (switchs != null) {
				builder.append("============================================\n");
				builder.append("参数开关：\n");
				for (String name : switchs.keySet()) {
					builder.append(name + " - " + switchs.get(name) + "\n");
				}
			}

		} else {

			builder.append("============================================\n");
			builder.append("是否闪照：" + (isSnappic ? "True" : "False") + "\n");
			builder.append("是否视频：" + (isQQVideo ? "True" : "False") + "\n");
			builder.append("是否红包：" + (isHongbao ? "True" : "False") + "\n");
			builder.append("是否纯码：" + (isPureCQC ? "True" : "False") + "\n");

			builder.append("============================================\n");
			builder.append("包含图片：" + (hasPicture ? "True" : "False") + "\n");
			if (hasPicture) {
				builder.append("图片ID：\n");
				for (String temp : picture) {
					builder.append(temp + "\n");
				}
			}

			builder.append("============================================\n");
			builder.append("最终长度: " + resLength + "\n");
			if (resLength == 0) {
				builder.append("最终内容：" + "无" + "\n");
				builder.append("最终编码：" + "无" + "\n");
			} else {
				builder.append("最终内容：" + resMessage + "\n");
				builder.append("最终编码：");
				for (int i = 0; i < resLength; i++) {
					builder.append("\\u");
					builder.append(Integer.toHexString(resMessage.charAt(i) & 0xffff));
				}
				builder.append("\n");
			}
		}

		builder.append("============================================");

		return builder.toString();
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
		return messageFt;
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

	/**
	 * 获取消息发送时间 Date对象
	 *
	 * @return Date(currentTimeMillis)
	 */
	public Date getSendDate() {
		return new Date(sendTime);
	}

	// ===================================================================================

	/**
	 * 获取原始消息
	 *
	 * @return 原始消息
	 */
	public String getRawMessage() {
		return rawMessage;
	}

	/**
	 * 获取原始消息长度
	 *
	 * @return 原始消息长度
	 */
	public int getRawLength() {
		return rawLength;
	}

	// ===================================================================================

	/**
	 * 获取命令内容 即去掉/ 如果不是命令则为null 执行器以外的地方不应执行这个函数
	 *
	 * @return 去掉/的内容
	 */
	public String getCmdMessage() {
		return cmdMessage;
	}

	/**
	 * 获取命令 即去掉/以空格切分的[0] 如果不是命令则为null 执行器以外的地方不应执行这个函数
	 *
	 * @return 获取命令头
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * 获取参数 即去掉/以后按空格切分的[1:] 如果不是命令则为null 执行器以外的地方不应执行这个函数
	 *
	 * @return 获取所有参数
	 */
	public String getOptions() {
		return options;
	}

	/**
	 * 获取参数长度 即去掉/以后按空格切分的[1:]的元素数量 如果不是命令则为0 执行器以外的地方不应执行这个函数
	 *
	 * @return 获取参数长度
	 */
	public int getSection() {
		return section;
	}

	/**
	 * 获取所有的参数 即去掉/以后按空格切分的[1:]的元素 如果不是命令则为null 执行器以外的地方不应执行这个函数
	 *
	 * @return 参数
	 */
	public String[] getSegment() {
		return segment;
	}

	/***
	 * 获取参数 即去掉/以后按空格切分的index+1, 如果不是命令则为null 执行器以外的地方不应执行这个函数
	 *
	 * @param index 索引号
	 * @return 获取指定顺序参数
	 */
	public String getSegment(int index) {
		return segment[index];
	}

	/**
	 * 获取开关的值 --name=value 形式的参数为开关 如果不是命令或不存在此参数则为null 执行器以外的地方不应执行这个函数
	 *
	 * @param name 名称
	 * @return 值
	 */
	public String getSwitch(String name) {
		return switchs.get(name);
	}

	/**
	 * 是否包含指定名字的开关
	 *
	 * @param name 名称
	 * @return 值
	 */
	public boolean hasSwitch(String name) {
		return switchs.containsKey(name);
	}

	// ===================================================================================

	/**
	 * 消息是否为命令
	 *
	 * @return 是否
	 */
	public boolean isCommand() {
		return isCommand;
	}

	/**
	 * 消息是否为红包
	 *
	 * @return 是否
	 */
	public boolean isHongbao() {
		return isHongbao;
	}

	/**
	 * 消息是否为短视频
	 *
	 * @return 是否
	 */
	public boolean isQQVideo() {
		return isQQVideo;
	}

	/**
	 * 消息是否为闪照
	 *
	 * @return 是否
	 */
	public boolean isSnappic() {
		return isSnappic;
	}

	/**
	 * 消息是否为纯CQ码
	 *
	 * @return 是否
	 */
	public boolean isPureCQC() {
		return isPureCQC;
	}

	/**
	 * 消息是否包含图片
	 *
	 * @return 是否
	 */
	public boolean hasPicture() {
		return hasPicture;
	}

	/**
	 * 获取消息中的所有图片
	 *
	 * @return CQImage码
	 */
	public String[] getPicture() {
		return picture;
	}

	// ===================================================================================

	/**
	 * 获取分析后的消息
	 *
	 * @return 消息
	 */
	public String getResMessage() {
		return resMessage;

	}

	/**
	 * 获取分析后的消息长度
	 *
	 * @return 长度
	 */
	public int getResLength() {
		return resLength;
	}

}
