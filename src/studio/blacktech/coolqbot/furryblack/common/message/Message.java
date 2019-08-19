package studio.blacktech.coolqbot.furryblack.common.message;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import studio.blacktech.coolqbot.furryblack.common.LoggerX;

public class Message {

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
	private String segment[] = {};
	private String picture[] = {};
	private int section = 0;

	private boolean hasPicture = false;
	private boolean isCommand = false;
	private boolean isSnappic = false;
	private boolean isQQVideo = false;
	private boolean isHongbao = false;
	private boolean isPureCQS = false;

	private LinkedList<String> segmentParts;
	private TreeMap<String, String> switchs;

	// ===================================================================================

	public Message(String message, int messageid, int messageFont) {

		this.sendTime = System.currentTimeMillis();

		this.messageId = messageid;
		this.messageFt = messageFont;
		this.rawMessage = message;
	}

	// ===================================================================================

	public Message anaylys() {

		// 居然因为这么一条鬼消息出BUG了 -> /招手[CQ:at,qq=XXXXXXXX]

		if (this.rawMessage.charAt(0) != '/') { return this; }
		this.rawLength = this.rawMessage.length();
		if (this.rawLength == 1) { return this; }
		if (this.rawMessage.matches("/[a-z]+.*")) { this.isCommand = true; }
		return this;
	}

	public Message parseCommand() {

		// 如果不是/开头则还未统计消息长度
		if (!this.isCommand) { this.rawLength = this.rawMessage.length(); }

		// 去掉 /
		// 去掉首尾多余空格
		// 合并所有连续空格
		this.cmdMessage = this.rawMessage.substring(1);
		this.cmdMessage = this.cmdMessage.trim();
		this.cmdMessage = this.cmdMessage.replaceAll("\\s+", " ");

		int indexOfSpace = this.cmdMessage.indexOf(' ');

		// 是否空命令
		if (indexOfSpace < 0) {
			this.command = this.cmdMessage;
		} else {
			// 切开
			// 命令
			// 参数
			this.command = this.cmdMessage.substring(0, indexOfSpace);
			this.options = this.cmdMessage.substring(indexOfSpace + 1);

			String[] flag;
			this.switchs = new TreeMap<>();
			this.segmentParts = new LinkedList<>();

			// 提取所有 --XX=XXXX 形式的开关
			// 提取所有其他内容为参数
			for (String temp : this.options.split(" ")) {
				if (temp.startsWith("--") && temp.indexOf("=") > 0) {
					temp = temp.substring(2);
					flag = temp.split("=");
					this.switchs.put(flag[0], flag[1]);
				} else {
					this.segmentParts.add(temp);
				}
			}

			this.segment = new String[this.segmentParts.size()];
			this.segmentParts.toArray(this.segment);
			this.section = this.segment.length;
		}
		return this;
	}

	// ===================================================================================

	/***
	 * 分析消息内容 1：闪照 2：视频 3：红包
	 *
	 * 如果包含图片则取出所有图片并生成纯文本及其长度
	 *
	 * "&#91;闪照&#93;请使用新版手机QQ查看闪照。"
	 *
	 * "&#91;QQ红包&#93;请使用新版手机QQ查收红包。"
	 *
	 * "&#91;视频&#93;你的QQ暂不支持查看视频短片，请升级到最新版本后查看。"
	 *
	 * @return
	 */

	private final static String REGEX_IMAGE = "\\[CQ:image,file=\\w{32}\\.\\w{3}\\]";
	private final static String REGEX_CCODE = "\\[CQ:.+\\]";

	public Message parseMessage() {

		// 如果不是/开头则还未统计消息长度
		if (!this.isCommand) { this.rawLength = this.rawMessage.length(); }

		if (this.rawMessage.startsWith("&#91;闪照&#93;")) {
			this.isSnappic = true;
		} else if (this.rawMessage.startsWith("&#91;视频&#93;")) {
			this.isQQVideo = true;
		} else if (this.rawMessage.startsWith("&#91;QQ红包&#93;")) {
			this.isHongbao = true;
		} else {
			// 移动端用户数量非常大, 通常包含图片的消息都是单张图片（表情包）
			Pattern pattern = Pattern.compile(Message.REGEX_IMAGE);
			Matcher matcher = pattern.matcher(this.rawMessage);
			ArrayList<String> temp = new ArrayList<>(1);
			if (matcher.find()) {
				this.hasPicture = true;
				temp.add(matcher.group());
				// 这里一般都是false
				while (matcher.find()) {
					temp.add(matcher.group());
				}
				this.picture = new String[temp.size()];
				temp.toArray(this.picture);
			}
			this.resMessage = this.rawMessage.replaceAll(Message.REGEX_CCODE, "");
			this.resLength = this.resMessage.length();
			if (this.resLength == 0) { this.isPureCQS = true; }
		}
		return this;
	}

	// ===================================================================================

	public String join(int i) {
		if (this.section == 0) {
			return "";
		} else {
			StringBuilder builder = new StringBuilder();
			for (; i < this.section; i++) {
				builder.append(" ");
				builder.append(this.segment[i]);
			}
			return builder.toString();
		}
	}

	// ===================================================================================

	public int getMessageId() {
		return this.messageId;
	}

	public int getMessageFont() {
		return this.messageFt;
	}

	public long getSendtime() {
		return this.sendTime;
	}

	public String getRawMessage() {
		return this.rawMessage;
	}

	public int getRawLength() {
		return this.rawLength;
	}

	public String getResMessage() {
		return this.resMessage;
	}

	public int getResLength() {
		return this.resLength;
	}

	public String[] getPicture() {
		return this.picture;
	}

	// ===================================================================================

	public String getCommand() {
		return this.command;
	}

	public String getCmdMessage() {
		return this.cmdMessage;
	}

	public String getOptions() {
		return this.options;
	}

	public int getSection() {
		return this.section;
	}

	public String[] getSegment() {
		return this.segment;
	}

	public String getSwitch(String name) {
		return this.switchs.get(name);
	}

	// ===================================================================================

	public boolean isCommand() {
		return this.isCommand;
	}

	public boolean isHongbao() {
		return this.isHongbao;
	}

	public boolean isQQVideo() {
		return this.isQQVideo;
	}

	public boolean isSnappic() {
		return this.isSnappic;
	}

	public boolean isPureCQS() {
		return this.isPureCQS;
	}

	public boolean hasPicture() {
		return this.hasPicture;
	}

	// ===================================================================================

	public String toString() {

		StringBuilder builder = new StringBuilder();

		builder.append("Message ID: ");
		builder.append(this.messageId);
		builder.append("\nMessage Font: ");
		builder.append(this.messageFt);

		builder.append("\nSendTime: ");
		builder.append(LoggerX.datetime(new Date(this.sendTime)));
		builder.append("(");
		builder.append(this.sendTime);
		builder.append(")");

		builder.append("\nRAW-CONTENT: ");
		builder.append(this.rawMessage);
		builder.append("\nRAW-LENGTH: ");
		builder.append(this.rawLength);

		builder.append("\nRES-CONTENT: ");
		builder.append(this.resMessage);
		builder.append("\nRES-LENGTH: ");
		builder.append(this.resLength);

		builder.append("\nisCommand: ");
		builder.append(this.isCommand ? "True" : "False");
		builder.append("\nisSnappic: ");
		builder.append(this.isSnappic ? "True" : "False");
		builder.append("\nisQQVideo: ");
		builder.append(this.isQQVideo ? "True" : "False");
		builder.append("\nisHongbao: ");
		builder.append(this.isHongbao ? "True" : "False");

		builder.append("\nhasPicture: ");
		builder.append(this.hasPicture ? "True" : "False");

		if (this.hasPicture) {
			builder.append("\nPicture: ");
			for (String temp : this.picture) {
				builder.append("\n");
				builder.append(temp);
			}
		}

		if (this.isCommand) {

			builder.append("\ncmdMessage: ");
			builder.append(this.cmdMessage);

			builder.append("\ncommand: ");
			builder.append(this.command);

			builder.append("\noptions: ");
			builder.append(this.options);

			builder.append("\nsection: ");
			builder.append(this.section);

			if (this.section > 0) {
				builder.append("\nsegment: ");
				for (String temp : this.segment) {
					builder.append("\n");
					builder.append(temp);
				}
			}

			if (this.switchs != null) {
				builder.append("\nFlags:");
				for (String name : this.switchs.keySet()) {
					builder.append("\n");
					builder.append(name);
					builder.append(" ");
					builder.append(this.switchs.get(name));
				}
			}
		}
		return builder.toString();
	}
}
