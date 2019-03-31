package studio.blacktech.coolqbot.furryblack.signal;

import com.sobte.cqp.jcq.entity.Member;
import com.sobte.cqp.jcq.event.JcqApp;

public class Workflow {
	public String anonmessage;
	public String[] command;
	public long dsid;
	public int font;
	public int from;
	public long gpid;
	public int length;
	public String message;
	public long qqid;
	public int ssid;
	public int type;

	public Workflow(final int mesg_type, final int mesg_id, final long user_id, final long discuss_id, final String message, final int mesg_font) {
		this.from = 2;
		this.type = mesg_type;
		this.ssid = mesg_id;
		this.font = mesg_font;
		this.qqid = user_id;
		this.gpid = 0;
		this.dsid = discuss_id;
		this.message = message.substring(2);
		this.anonmessage = null;
	}

	public Workflow(final int mesg_type, final int mesg_id, final long user_id, final long group_id, final String message, final String anonmessage, final int mesg_font) {
		this.from = 3;
		this.type = mesg_type;
		this.ssid = mesg_id;
		this.font = mesg_font;
		this.qqid = user_id;
		this.gpid = group_id;
		this.dsid = 0;
		this.message = message.substring(2);
		this.anonmessage = anonmessage;
	}

	public Workflow(final int mesg_type, final int mesg_id, final long user_id, final String message, final int mesg_font) {
		this.from = 1;
		this.type = mesg_type;
		this.ssid = mesg_id;
		this.font = mesg_font;
		this.qqid = user_id;
		this.gpid = 0;
		this.dsid = 0;
		this.message = message.substring(2);
		this.anonmessage = null;
	}

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

	public String prase() {
		this.message = this.message.trim();
		this.command = this.message.split(" ");
		this.length = this.command.length;
		return this.command[0];
	}

	public Member toMember() {
		return JcqApp.CQ.getGroupMemberInfoV2(this.gpid, this.qqid);
	}

	@Override
	public String toString() {
		return this.from + " " + this.gpid + " " + this.dsid + " " + this.qqid + " " + this.message;
	}
}
