package studio.blacktech.coolqbot.furryblack.modules;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Properties;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.Module;
import studio.blacktech.coolqbot.furryblack.common.ModuleScheduler;

@SuppressWarnings("deprecation")
public class Scheduler_DDNS extends ModuleScheduler {

	private static final String API_GETADDRESS = "http://ip.3322.net/";
	private static final String API_SETADDRESS = "http://members.3322.net/dyndns/update";

	private static boolean INITIALIZATIONLOCK = false;

	private boolean ENABLE = true;
	private String CLIENTUA;
	private String HOSTNAME;
	private String PASSWORD;

	private String ADDRESS = null;

	public Scheduler_DDNS(StringBuilder initBuilder, Properties config) {
		if (Scheduler_DDNS.INITIALIZATIONLOCK) { return; }
		Scheduler_DDNS.INITIALIZATIONLOCK = true;

		this.MODULE_DISPLAYNAME = "��̬����";
		this.MODULE_PACKAGENAME = "ddns";
		this.MODULE_DESCRIPTION = "��̬����";
		this.MODULE_VERSION = "2.0.8";
		this.MODULE_USAGE = new String[] {};
		this.MODULE_PRIVACY_TRIGER = new String[] {};
		this.MODULE_PRIVACY_LISTEN = new String[] {};
		this.MODULE_PRIVACY_STORED = new String[] {};
		this.MODULE_PRIVACY_CACHED = new String[] {};
		this.MODULE_PRIVACY_OBTAIN = new String[] {};

		this.ENABLE = Boolean.parseBoolean(config.getProperty("enable_ddnsclient", "false"));
		this.CLIENTUA = config.getProperty("ddnsapi_clientua", "BTSCoolQ/1.0");
		this.HOSTNAME = config.getProperty("ddnsapi_hostname", "");
		this.PASSWORD = config.getProperty("ddnsapi_password", "");

		if (entry.INIT_VERBOSE) {
			initBuilder.append("\r\n[DDNS] �ͻ��ˣ�");
			initBuilder.append(this.ENABLE ? "����" : "����");
			initBuilder.append("\r\n[DDNS] ��ʶ��");
			initBuilder.append(this.CLIENTUA);
			initBuilder.append("\r\n[DDNS] ������");
			initBuilder.append(this.HOSTNAME);
			initBuilder.append("\r\n[DDNS] ���룺");
			initBuilder.append(this.PASSWORD);
		}

		String temp = this.getIPAddress();
		if (temp == null) {
			initBuilder.append("\r\n[DDNS] ��ַ��ȡʧ��");
			temp = this.updateDDNSIPAddress();
			if (temp == null) {
				initBuilder.append("\r\n[DDNS] ���µ�ַʧ�ܣ���Ҫ�ֶ�����");
			} else {
				initBuilder.append("\r\b[DDNS] ���µ�ַ�ɹ��� ");
				initBuilder.append(temp);
			}
		} else {
			initBuilder.append("\r\n[DDNS] ��ַ��ȡ�ɹ��� ");
			initBuilder.append(temp);
			this.ADDRESS = temp;
			temp = this.setDDNSIPAddress(temp);
			if (temp == null) {
				initBuilder.append("\r\n[DDNS] ���õ�ַʧ��");
			} else {
				initBuilder.append("\r\n[DDNS] ���õ�ַ�ɹ��� ");
				initBuilder.append(temp);
			}
		}
	}

	@Override
	public void run() {
		if (this.ENABLE) {
			while (true) {
				try {
					this.doLoop();
				} catch (InterruptedException exce) {
					exce.printStackTrace();
					return;
				}
			}
		}
	}

	private void doLoop() throws InterruptedException {
		Date date = new Date();

		int time = 605;

		time = time - date.getSeconds();
		time = time - (date.getMinutes() * 5);

		if (time < 60) { time = time + 600; }

		Thread.sleep(time * 1000);

		while (true) {
			// ####### =====================================================
			new Thread(() -> {
				String temp = this.getIPAddress();
				if (temp == null) {
					temp = this.updateDDNSIPAddress();
					if (temp == null) { Module.userInfo(entry.OPERATOR(), "[DDNS] ��ַ����ʧ�ܣ���Ҫ�ֶ����룡"); }
				} else {
					if (!temp.equals(this.ADDRESS)) { Module.userInfo(entry.OPERATOR(), "[DDNS] ��⵽��ַ���\r\n�ɵ�ַ��" + this.ADDRESS + "\r\n�µ�ַ��" + temp + "\r\n�����µ�ַ��" + this.setDDNSIPAddress(temp)); }
				}
				// ####### =====================================================
			}).start();
			Thread.sleep(600000L);
		}
	}

	public String getIPAddress() {
		try {
			final URL url = new URL(Scheduler_DDNS.API_GETADDRESS);
			final URLConnection connection = url.openConnection();
			connection.setReadTimeout(5000);
			connection.setConnectTimeout(2000);
			connection.setRequestProperty("User-Agent", this.CLIENTUA);
			connection.connect();
			connection.getContent();
			final byte[] buffer = new byte[32];
			final InputStream rx = connection.getInputStream();
			rx.read(buffer);
			return new String(buffer, "UTF-8").trim();
		} catch (Exception exce) {
			exce.printStackTrace();
			return null;
		}
	}

	public String updateDDNSIPAddress() {
		try {
			final URL url = new URL(Scheduler_DDNS.API_SETADDRESS + "?hostname=" + this.HOSTNAME);
			final URLConnection connection = url.openConnection();
			connection.setReadTimeout(5000);
			connection.setConnectTimeout(2000);
			connection.setRequestProperty("User-Agent", this.CLIENTUA);
			connection.setRequestProperty("Authorization", this.PASSWORD);
			connection.connect();
			connection.getContent();
			final byte[] buffer = new byte[32];
			final InputStream rx = connection.getInputStream();
			rx.read(buffer);
			return new String(buffer, "UTF-8").trim();
		} catch (Exception exce) {
			exce.printStackTrace();
			return null;
		}
	}

	public String setDDNSIPAddress(final String address) {
		try {
			this.ADDRESS = address;
			final URL url = new URL(Scheduler_DDNS.API_SETADDRESS + "?hostname=" + this.HOSTNAME + "&myip=" + address);
			final URLConnection connection = url.openConnection();
			connection.setReadTimeout(5000);
			connection.setConnectTimeout(2000);
			connection.setRequestProperty("User-Agent", this.CLIENTUA);
			connection.setRequestProperty("Authorization", this.PASSWORD);
			connection.connect();
			connection.getContent();
			final byte[] buffer = new byte[32];
			final InputStream rx = connection.getInputStream();
			rx.read(buffer);
			return new String(buffer, "UTF-8").trim();
		} catch (Exception exce) {
			exce.printStackTrace();
			return null;
		}
	}

}
