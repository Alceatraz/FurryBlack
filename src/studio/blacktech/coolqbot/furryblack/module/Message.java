package studio.blacktech.coolqbot.furryblack.module;

/***
 * ��Ϣ�����ʵ�ù�����
 *
 */
public class Message {

	public String rawMessage;
	public String trimedMessage;
	public String[] messages;
	public Long sendTime;
	public int length;
	public int segment;
	public boolean isCommand = false;

	public Message(final String raw) {
		this.sendTime = System.currentTimeMillis();
		this.rawMessage = raw;
		this.length = this.rawMessage.length();
		// ��isCommand��������ʵ�ʲ��� ��������
		this.isCommand = (this.length > 2) && (this.rawMessage.startsWith("//"));
		if (this.isCommand) {
			this.trimedMessage = this.rawMessage.trim().substring(2);
			this.messages = this.trimedMessage.split(" ");
			this.segment = this.messages.length;
		}
	}

	/***
	 * ��ָ����ʼλ���ÿո�ƴ��
	 *
	 * @param i �ӵڼ�λ�ÿ�ʼ
	 * @return ƴ�Ӻ����Ϣ
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
}
