package studio.blacktech.coolqbot.furryblack;

import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.signal.Workflow;

public class ExectorX {

	public static void shell(final Workflow flow) {

		switch (flow.prase()) {

		case "info":
			// info [xxxx] 关于
			JcqApp.CQ.sendPrivateMsg(flow.qqid, entry.MESSAGE_INFO);
			break;

		case "eula":
			// eula [xxxx] 最终用户协议
			JcqApp.CQ.sendPrivateMsg(flow.qqid, entry.MESSAGE_EULA);
			break;

		case "list":
			// list [xxxx] 列出所有模块
			JcqApp.CQ.sendPrivateMsg(flow.qqid, entry.getMESSAGE_LIST());
			break;

		case "helpall":
			// helpall [xxxx] 所有帮助
			JcqApp.CQ.sendPrivateMsg(flow.qqid, entry.getMESSAGE_HELPALL());
			break;

		case "admin":
			if (flow.qqid == entry.OPERATOR) {
				Zwischenspiel.parseAdmin(flow);
			}
			break;

		case "help":
			if (flow.length == 1) {
				// help 帮助
				JcqApp.CQ.sendPrivateMsg(flow.qqid, entry.MESSAGE_HELP);
			} else if (flow.length > 1) {
				// help xxxx [xxxx] 模块帮助
				if (entry.MODULES.containsKey(flow.command[1])) {
					JcqApp.CQ.sendPrivateMsg(flow.qqid, entry.MODULES.get(flow.command[1]).MODULE_FULLHELP);
				} else {
					JcqApp.CQ.sendPrivateMsg(flow.qqid, "没有此插件");
				}

			}
			break;

		default:
			// xxxx [xxxx] 具体命令执行
			if (entry.MODULES.containsKey(flow.command[0])) {
				try {
					entry.MODULES.get(flow.command[0]).excute(flow);
				} catch (final Exception exception) {
					exception.printStackTrace();
				}
			} else {
				JcqApp.CQ.sendPrivateMsg(flow.qqid, "没有此插件");
			}
		}
	}
}
