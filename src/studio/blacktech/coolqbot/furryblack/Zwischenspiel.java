package studio.blacktech.coolqbot.furryblack;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import com.sobte.cqp.jcq.entity.Group;
import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.signal.Workflow;
import studio.blacktech.coolqbot.furryblack.utility.LoggerX;

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
				JcqApp.CQ.sendGroupMsg(temp.getId(), "�����߹㲥(����Ⱥ) : " + flow.join(1));
			}
			break;
		}
	}

	public static String genReport(final Date date) {
		final StringBuilder builder = new StringBuilder();
		builder.append(LoggerX.time(date));
		builder.append(" - ״̬��");
		String report;
		for (final String temp : entry.Executor.keySet()) {
			builder.append("\r\n\r\nģ�� ");
			builder.append(temp);
			builder.append(": ");
			builder.append(entry.Executor.get(temp).counter);
			builder.append(" �ε���\r\n");
			report = entry.Executor.get(temp).genReport();
			builder.append(report == null ? "�޿�������" : report);
		}
		return builder.toString();
	}

	@SuppressWarnings("deprecation")
	public static String getDate(final Date date) {
		final StringBuilder builder = new StringBuilder();
		builder.append("�����: " + System.nanoTime());
		builder.append("\r\n�����: " + date.getTime());
		builder.append("\r\nʱ��: " + date.getTimezoneOffset());
		builder.append("\r\n��: " + date.getYear());
		builder.append("\r\n��: " + date.getMonth());
		builder.append("\r\n��: " + date.getDate());
		builder.append("\r\nʱ: " + date.getHours());
		builder.append("\r\n��: " + date.getMinutes());
		builder.append("\r\n��: " + date.getSeconds());
		return builder.toString();
	}

	/***
	 * !!!!!!!!!!!!!!!!!
	 *
	 * !!��������ʹ�ô˷���
	 *
	 * !!!!!!!!!!!!!!!!!!
	 *
	 *
	 * PubYUN-API: ��ȡIP��ַ
	 *
	 * @return
	 */
	public static String ddnsGetIP() {
		try {
			final URL url = new URL("http://ip.3322.net/");
			final URLConnection connection = url.openConnection();
			connection.setRequestProperty("User-Agent", "BTS CoolQ/1.0");
			connection.connect();
			connection.getContent();
			final InputStream rx = connection.getInputStream();
			final byte[] buffer = new byte[1024];
			rx.read(buffer);
			final String temp = new String(buffer, "UTF-8");
			return temp.trim();
		} catch (final IOException exception) {
			exception.printStackTrace();
			return null;
		}
	}

	/***
	 * !!!!!!!!!!!!!!!!!
	 *
	 * !!��������ʹ�ô˷���
	 *
	 * !!!!!!!!!!!!!!!!!!
	 *
	 *
	 * PubYUN-API: ǿ������IP��ַ
	 *
	 * @return
	 */
	public static String ddnsSetIP() {
		try {
			final URL url = new URL(entry.getAPIURL_BASE());
			final URLConnection connection = url.openConnection();
			connection.setRequestProperty("Authorization", entry.getAPIKEY_DDNS());
			connection.setRequestProperty("User-Agent", "BTS CoolQ/1.0");
			connection.connect();
			final InputStream rx = connection.getInputStream();
			final byte[] buffer = new byte[1024];
			rx.read(buffer);
			String temp = new String(buffer, "UTF-8");
			temp = temp.trim();
			return temp;
		} catch (final IOException exception) {
			exception.printStackTrace();
			return null;
		}
	}

	/***
	 * !!!!!!!!!!!!!!!!!
	 *
	 * !!��������ʹ�ô˷���
	 *
	 * !!!!!!!!!!!!!!!!!!
	 *
	 *
	 * PubYUN-API: ��������IP��ַ
	 *
	 * @return
	 */
	public static String ddnsSetIP(final String address) {
		try {
			final URL url = new URL(entry.getAPIURL_BASE() + "&myip=" + address);
			final URLConnection connection = url.openConnection();
			connection.setRequestProperty("Authorization", entry.getAPIKEY_DDNS());
			connection.setRequestProperty("User-Agent", "BTS CoolQ/1.0");
			connection.connect();
			final InputStream rx = connection.getInputStream();
			final byte[] buffer = new byte[1024];
			rx.read(buffer);
			String temp = new String(buffer, "UTF-8");
			temp = temp.trim();
			return temp;
		} catch (final IOException exception) {
			exception.printStackTrace();
			return null;
		}
	}

}
