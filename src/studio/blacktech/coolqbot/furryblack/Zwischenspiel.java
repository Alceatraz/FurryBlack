package studio.blacktech.coolqbot.furryblack;

import java.util.Date;

import com.sobte.cqp.jcq.entity.Group;
import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.common.LoggerX;
import studio.blacktech.coolqbot.furryblack.signal.Workflow;

public class Zwischenspiel {

	public static void parseAdmin(final Workflow flow) {

		final Date date = new Date();

		if (flow.length < 2) {
			JcqApp.CQ.sendPrivateMsg(entry.OPERATOR, Zwischenspiel.genReport(date));
			return;
		}

		switch (flow.command[1]) {

		case "getddns":
			JcqApp.CQ.sendPrivateMsg(entry.OPERATOR, Zwischenspiel.ddnsGetIP());
			break;

		case "setddns":
			if (flow.length < 3) {
				JcqApp.CQ.sendPrivateMsg(entry.OPERATOR, Zwischenspiel.ddnsSetIP());
			} else {
				JcqApp.CQ.sendPrivateMsg(entry.OPERATOR, Zwischenspiel.ddnsSetIP(flow.command[2]));
			}
			break;

		case "getdate":
			JcqApp.CQ.sendPrivateMsg(entry.OPERATOR, Zwischenspiel.getDate(date));
			break;

		case "say":
			for (final Group temp : JcqApp.CQ.getGroupList()) {
				JcqApp.CQ.sendGroupMsg(temp.getId(), "开发者广播(所有群) : " + flow.join(1));
			}
			break;
		}
	}

	public static String genReport(final Date date) {
		final StringBuilder builder = new StringBuilder();
		builder.append(LoggerX.time(date));
		builder.append(" - 状态简报");
		String report;
		for (final String temp : entry.Executor.keySet()) {
			builder.append("\r\n\r\n模块 ");
			builder.append(temp);
			builder.append(": ");
			builder.append(entry.Executor.get(temp).counter);
			builder.append(" 次调用\r\n");
			report = entry.Executor.get(temp).genReport();
			builder.append(report == null ? "无可用数据" : report);
		}
		return builder.toString();
	}

	@SuppressWarnings("deprecation")
	public static String getDate(final Date date) {
		final StringBuilder builder = new StringBuilder();
		builder.append("纳秒戳: " + System.nanoTime());
		builder.append("\r\n毫秒戳: " + date.getTime());
		builder.append("\r\n时区: " + date.getTimezoneOffset());
		builder.append("\r\n年: " + date.getYear());
		builder.append("\r\n月: " + date.getMonth());
		builder.append("\r\n日: " + date.getDate());
		builder.append("\r\n时: " + date.getHours());
		builder.append("\r\n分: " + date.getMinutes());
		builder.append("\r\n秒: " + date.getSeconds());
		return builder.toString();
	}

}
