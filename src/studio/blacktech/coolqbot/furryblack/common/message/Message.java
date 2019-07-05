package studio.blacktech.coolqbot.furryblack.common.message;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	private boolean isPureCQC = false;

	private TreeMap<String, String> switchs;

	// ===================================================================================

	public Message(String message, int messageid, int messageFont) {
		this.sendTime = System.currentTimeMillis();
		this.messageId = messageid;
		this.messageFt = messageFont;
		this.rawMessage = message;
	}

	// ===================================================================================

	/***
	 * 是否为命令 是则切开命令和参数
	 *
	 * @return
	 */
	public Message anaylysIsCommand() {
		this.rawLength = this.rawMessage.length();
		if (this.rawMessage.charAt(0) == '/' && this.rawLength > 2) {
			this.isCommand = true;
			this.cmdMessage = this.rawMessage.substring(1);
			this.cmdMessage = this.cmdMessage.trim();
			int p = this.cmdMessage.indexOf(' ');
			if (p < 0) {
				this.command = this.cmdMessage;
			} else {
				this.command = this.cmdMessage.substring(0, p);
				this.options = this.cmdMessage.substring(p + 1);
			}
		}
		return this;
	}

	/***
	 * 进一步处理命令 切开所有参数
	 *
	 * @return
	 */
	public Message parseOption() {
		if (this.isCommand) {
			this.options.replaceAll("\\s+", " ");
			this.segment = this.options.split(" ");
			this.section = this.segment.length;
		}
		return this;
	}

	/***
	 * 进一步处理命令 读取所有开关
	 *
	 * @return
	 */
	public Message parseFlags() {
		if (this.isCommand) {
			if (this.section > 0) {
				this.switchs = new TreeMap<>();
				String[] flag;
				for (String temp : this.segment) {
					if (temp.startsWith("--") && temp.indexOf("=") > 0) {
						temp = temp.substring(2);
						flag = temp.split("=");
						this.switchs.put(flag[0], flag[1]);
					}
				}
			}

		}
		return this;
	}

	// ===================================================================================

	/***
	 * 从指定位置拼接参数
	 *
	 * @param i
	 * @return
	 */
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

	public Message anaylysMessage() {

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
			if (this.resLength == 0) { this.isPureCQC = true; }
		}
		return this;
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

	// ===================================================================================

	public String getFlag(String name) {
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

	public boolean isPureCQC() {
		return this.isPureCQC;
	}

	public boolean hasPicture() {
		return this.hasPicture;
	}

	// ===================================================================================

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Message ID:");
		builder.append(this.messageId);
		builder.append("\nMessage Font:");
		builder.append(this.messageFt);
		builder.append("\nSendTime:");
		builder.append(this.sendTime);
		builder.append("\nRAW-CONTENT:");
		builder.append(this.rawMessage);
		builder.append("\nRAW-LENGTH:");
		builder.append(this.rawLength);
		builder.append("\nRES-CONTENT:");
		builder.append(this.resMessage);
		builder.append("\nRES-LENGTH:");
		builder.append(this.resLength);
		builder.append("\nisCommand:");
		builder.append(this.isCommand ? "True" : "False");
		builder.append("\nisSnappic:");
		builder.append(this.isSnappic ? "True" : "False");
		builder.append("\nisQQVideo:");
		builder.append(this.isQQVideo ? "True" : "False");
		builder.append("\nisHongbao:");
		builder.append(this.isHongbao ? "True" : "False");
		builder.append("\nhasPicture:");
		builder.append(this.hasPicture ? "True" : "False");
		builder.append("\nPicture:");
		for (String temp : this.picture) {
			builder.append("\n");
			builder.append(temp);
		}
		if (this.isCommand) {
			builder.append("\ncmdMessage:");
			builder.append(this.cmdMessage);
			builder.append("\ncommand:");
			builder.append(this.command);
			builder.append("\noptions:");
			builder.append(this.options);
			builder.append("\nsection");
			builder.append(this.section);
			builder.append("\nsegment:");
			for (String temp : this.segment) {
				builder.append("\n");
				builder.append(temp);
			}
			builder.append("\nFlags:");
			for (String name : this.switchs.keySet()) {
				builder.append("\n");
				builder.append(name);
				builder.append(" ");
				builder.append(this.switchs.get(name));
			}
		}

		return builder.toString();
	}
}
