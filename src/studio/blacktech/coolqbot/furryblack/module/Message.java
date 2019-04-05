package studio.blacktech.coolqbot.furryblack.module;

/***
 * ��Ϣ�����ʵ�ù�����
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
	 * ���ո�����Ϣ
	 *
	 * @return ����0�ֶ� ������
	 */
	public String prase() {
		this.res = this.raw.trim().substring(2);
		this.cmd = this.res.split(" ");
		this.length = this.cmd.length;
		return this.cmd[0];
	}

	/***
	 * ��ָ����ʼλ���ÿո�ƴ��
	 *
	 * @param i �ӵڼ�λ�ÿ�ʼ
	 * @return ƴ�Ӻ����Ϣ
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
