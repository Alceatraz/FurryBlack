package studio.blacktech.coolqbot.furryblack.signal;

import com.sobte.cqp.jcq.entity.Member;
import com.sobte.cqp.jcq.event.JcqApp;

public class Workflow {
	public String[] command;
	public int from;
	public int length;
	public long qqid;
	public long dzid;
	public long gpid;
	public int type_id;
	public int mesg_id;
	public int font_id;
	public String message;
	public String anonmessage;

	/***
	 *
	 * @param mesg_type 11 ���Ժ��� 1 ��������״̬ 2 ����Ⱥ 3 ����������
	 * @param mesg_id
	 * @param user_id
	 * @param message
	 * @param mesg_font
	 */
	public Workflow(final int mesg_type, final int mesg_id, final long user_id, final String message, final int mesg_font) {
		this.from = 1;
		this.type_id = mesg_type;
		this.mesg_id = mesg_id;
		this.font_id = mesg_font;
		this.qqid = user_id;
		this.gpid = 0;
		this.dzid = 0;
		this.message = message.substring(2);
		this.anonmessage = null;
	}

	/***
	 *
	 * @param mesg_type  �̶�Ϊ1
	 * @param mesg_id
	 * @param user_id
	 * @param discuss_id
	 * @param message
	 * @param mesg_font
	 */
	public Workflow(final int mesg_type, final int mesg_id, final long user_id, final long discuss_id, final String message, final int mesg_font) {
		this.from = 2;
		this.type_id = mesg_type;
		this.mesg_id = mesg_id;
		this.font_id = mesg_font;
		this.qqid = user_id;
		this.gpid = 0;
		this.dzid = discuss_id;
		this.message = message.substring(2);
		this.anonmessage = null;
	}

	/***
	 *
	 * @param mesg_type   �̶�Ϊ1
	 * @param mesg_id
	 * @param user_id
	 * @param group_id
	 * @param message
	 * @param anonmessage
	 * @param mesg_font
	 */
	public Workflow(final int mesg_type, final int mesg_id, final long user_id, final long group_id, final String message, final String anonmessage, final int mesg_font) {
		this.from = 3;
		this.type_id = mesg_type;
		this.mesg_id = mesg_id;
		this.font_id = mesg_font;
		this.qqid = user_id;
		this.gpid = group_id;
		this.dzid = 0;
		this.message = message.substring(2);
		this.anonmessage = anonmessage;
	}

	/***
	 * ���ո�����Ϣ
	 *
	 * @return ����0�ֶ� ������
	 */
	public String prase() {
		this.message = this.message.trim();
		this.command = this.message.split(" ");
		this.length = this.command.length;
		return this.command[0];
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
			final StringBuilder bilder = new StringBuilder();
			for (; i < this.length; i++) {
				bilder.append(" ");
				bilder.append(this.command[i]);
			}
			return bilder.toString();
		}
	}

	public Member toMember() {
		return JcqApp.CQ.getGroupMemberInfoV2(this.gpid, this.qqid);
	}

	@Override
	public String toString() {
		return this.from + " " + this.gpid + " " + this.dzid + " " + this.qqid + " " + this.message;
	}
}
