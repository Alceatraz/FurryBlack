package studio.blacktech.coolqbot.furryblack.common.message;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Message {

	private final static String REGEX = "\\[CQ:image,file=\\w{32}\\.\\w{3}\\]";

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
	private final String picture[] = {};
	private int section = 0;

	private boolean hasPicture = false;
	private boolean isCommand = false;
	private boolean isSnappic = false;
	private boolean isQQVideo = false;
	private boolean isHongbao = false;

	// ===================================================================================

	public Message(final String message, final int messageid, final int messageFont) {
		this.sendTime = System.currentTimeMillis();
		this.messageId = messageid;
		this.messageFt = messageFont;
		this.rawMessage = message;
	}

	// ===================================================================================

	/***
	 * �Ƿ�Ϊ���� �����п�����Ͳ���
	 *
	 * @return
	 */
	public Message anaylysIsCommand() {
		this.rawLength = this.rawMessage.length();
		if (this.rawLength > 2 && this.rawMessage.charAt(0) == '/') {
			this.isCommand = true;
			this.cmdMessage = this.rawMessage.substring(1);
			this.cmdMessage = this.cmdMessage.trim();
			final int p = this.cmdMessage.indexOf(' ');
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
	 * ��һ���������� �п����в���
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
	 * ��ָ��λ��ƴ�Ӳ���
	 *
	 * @param i
	 * @return
	 */
	public String join(int i) {
		if (this.section == 0) {
			return "";
		} else {
			final StringBuilder builder = new StringBuilder();
			for (; i < this.section; i++) {
				builder.append(" ");
				builder.append(this.segment[i]);
			}
			return builder.toString();
		}
	}

	// ===================================================================================

	/***
	 * ������Ϣ���� 1������ 2����Ƶ 3�����
	 *
	 * �������ͼƬ��ȡ������ͼƬ�����ɴ��ı����䳤��
	 *
	 * "&#91;����&#93;��ʹ���°��ֻ�QQ�鿴���ա�"
	 *
	 * "&#91;QQ���&#93;��ʹ���°��ֻ�QQ���պ����"
	 *
	 * "&#91;��Ƶ&#93;���QQ�ݲ�֧�ֲ鿴��Ƶ��Ƭ�������������°汾��鿴��"
	 *
	 * @return
	 */

	public Message anaylysMessage() {

		if (this.rawMessage.startsWith("&#91;����&#93;")) {
			this.isSnappic = true;
		} else if (this.rawMessage.startsWith("&#91;��Ƶ&#93;")) {
			this.isQQVideo = true;
		} else if (this.rawMessage.startsWith("&#91;QQ���&#93;")) {
			this.isHongbao = true;
		} else {
			// �ƶ����û������ǳ���, ͨ������ͼƬ����Ϣ���ǵ���ͼƬ���������
			final Pattern pattern = Pattern.compile(Message.REGEX);
			final Matcher matcher = pattern.matcher(this.rawMessage);
			final ArrayList<String> temp = new ArrayList<>(1);
			if (matcher.find()) {
				this.hasPicture = true;
				temp.add(matcher.group());
				// ����һ�㶼��false
				while (matcher.find()) {
					temp.add(matcher.group());
				}
				temp.toArray(this.picture);
				this.resMessage = this.rawMessage.replaceAll(Message.REGEX, "");
				this.resLength = this.resMessage.length();
			} else {
				this.resMessage = this.rawMessage;
				this.resLength = this.rawLength;
			}
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

	public boolean hasPicture() {
		return this.hasPicture;
	}

	// ===================================================================================
}
