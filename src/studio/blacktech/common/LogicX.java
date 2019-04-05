package studio.blacktech.common;

import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.core.Zwischenspiel;
import studio.blacktech.coolqbot.furryblack.signal.Workflow;

public class LogicX {

	public static void executor(final Workflow flow) {

		switch (flow.prase()) {

		case "info":
			// info [xxxx] ����
			JcqApp.CQ.sendPrivateMsg(flow.qqid, entry.MESSAGE_INFO);
			break;

		case "eula":
			// eula [xxxx] �����û�Э��
			JcqApp.CQ.sendPrivateMsg(flow.qqid, entry.MESSAGE_EULA);
			break;

		case "list":
			// list [xxxx] �г�����ģ��
			JcqApp.CQ.sendPrivateMsg(flow.qqid, entry.getMESSAGE_LIST());
			break;

		case "helpall":
			// helpall [xxxx] ���а���
			JcqApp.CQ.sendPrivateMsg(flow.qqid, entry.getMESSAGE_HELPALL());
			break;

		case "admin":
			if (flow.qqid == entry.OPERATOR) {
				Zwischenspiel.parseAdmin(flow);
			}
			break;

		case "help":
			if (flow.length == 1) {
				// help ����
				JcqApp.CQ.sendPrivateMsg(flow.qqid, entry.MESSAGE_HELP);
			} else if (flow.length > 1) {
				// help xxxx [xxxx] ģ�����
				if (entry.Executor.containsKey(flow.command[1])) {
					JcqApp.CQ.sendPrivateMsg(flow.qqid, entry.Executor.get(flow.command[1]).MODULE_FULLHELP);
				} else {
					JcqApp.CQ.sendPrivateMsg(flow.qqid, "û�д˲��");
				}

			}
			break;

		default:
			// xxxx [xxxx] ��������ִ��
			if (entry.Executor.containsKey(flow.command[0])) {
				try {
					entry.Executor.get(flow.command[0]).executor(flow);
				} catch (final Exception exception) {
					exception.printStackTrace();
				}
			} else {
				JcqApp.CQ.sendPrivateMsg(flow.qqid, "û�д˲��");
			}
		}
	}

	public static void listener(final Workflow flow) {

	}
}
