package studio.blacktech.coolqbot.furryblack.module;

/***
 * ��Ϣ�����ʵ�ù�����
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
	 * ���ո�����Ϣ
	 *
	 * @return ����0�ֶ� ������
	 */
	public String prase() {
		this.res = this.raw.trim().substring(2);
		this.messages = this.res.split(" ");
		this.segment = this.messages.length;
		return this.messages[0];
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

	/***
	 * ����//��ͷ�Ĳ������� ֻ��//�Ĳ�������
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
