package studio.blacktech.coolqbot.furryblack;

import java.util.Date;

import com.sobte.cqp.jcq.entity.Group;
import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.common.ConfigureX;
import studio.blacktech.coolqbot.furryblack.module.Message;
import studio.blacktech.coolqbot.furryblack.module.Module;
import studio.blacktech.coolqbot.furryblack.scheduler.Worker_DDNS;

@SuppressWarnings("deprecation")
public class Zwischenspiel extends Module {

	public static void doUserAdmin(Message message) {
		final Date date = new Date();

		if (message.length < 2) {
			userInfo(ConfigureX.OPERATOR(), MessageHandler.genReport());
			return;
		}
		switch (message.cmd[1]) {

		case "getddns":
			userInfo(ConfigureX.OPERATOR(), Worker_DDNS.getIPAddress());
			break;
		case "setddns":
			if (message.length < 3) {
				userInfo(ConfigureX.OPERATOR(), Worker_DDNS.updateDDNSIPAddress());
			} else {
				userInfo(ConfigureX.OPERATOR(), Worker_DDNS.setDDNSIPAddress(message.cmd[2]));
			}
			break;
		case "getdate":
			StringBuilder builder = new StringBuilder();
			builder.append("�����: " + System.nanoTime());
			builder.append("\r\n�����: " + date.getTime());
			builder.append("\r\nʱ��: " + date.getTimezoneOffset());
			builder.append("\r\n��: " + date.getYear());
			builder.append("\r\n��: " + date.getMonth());
			builder.append("\r\n��: " + date.getDate());
			builder.append("\r\nʱ: " + date.getHours());
			builder.append("\r\n��: " + date.getMinutes());
			builder.append("\r\n��: " + date.getSeconds());
			userInfo(ConfigureX.OPERATOR(), builder.toString());
			break;
		case "say":
			for (final Group temp : JcqApp.CQ.getGroupList()) {
				JcqApp.CQ.sendGroupMsg(temp.getId(), "�����߹㲥(����Ⱥ) : " + message.join(1));
			}
			break;
		}
	}

	public static void doGropAdmin(Message command) {

	}

	public static void doDiszAdmin(Message command) {

	}

	@Override
	public String getReport() {
		return null;
	}

}
