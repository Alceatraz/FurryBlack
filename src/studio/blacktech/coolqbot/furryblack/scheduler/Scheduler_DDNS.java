package studio.blacktech.coolqbot.furryblack.scheduler;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Properties;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.module.Module;
import studio.blacktech.coolqbot.furryblack.module.ModuleScheduler;

@SuppressWarnings("deprecation")
public class Scheduler_DDNS extends ModuleScheduler {

	private static final String API_GETADDRESS = "http://ip.3322.net/";
	private static final String API_SETADDRESS = "http://members.3322.net/dyndns/update";

	private boolean INITIALIZATIONLOCK = false;

	private boolean ENABLE = true;
	private String CLIENTUA;
	private String HOSTNAME;
	private String PASSWORD;

	private String ADDRESS = null;

	public Scheduler_DDNS(StringBuilder initBuilder, Properties config) {
		if (this.INITIALIZATIONLOCK) {
			return;
		}
		this.INITIALIZATIONLOCK = true;

		this.ENABLE = Boolean.parseBoolean(config.getProperty("enable_ddnsclient", "false"));
		this.CLIENTUA = config.getProperty("ddnsapi_clientua", "BTSCoolQ/1.0");
		this.HOSTNAME = config.getProperty("ddnsapi_hostname", "");
		this.PASSWORD = config.getProperty("ddnsapi_password", "");

		String temp = this.getIPAddress();
		if (temp == null) {
			initBuilder.append("[DDNS] ��ַ��ȡʧ��\r\n");
			temp = this.updateDDNSIPAddress();
			if (temp == null) {
				initBuilder.append("[DDNS] ���µ�ַʧ��\r\n");
				Module.userInfo(entry.OPERATOR(), "[DDNS] ��Ҫ�ֶ����룡");
			} else {
				initBuilder.append("[DDNS] ���µ�ַ�ɹ���");
				initBuilder.append(temp);
				initBuilder.append("\r\n");
			}
		} else {
			initBuilder.append("[DDNS] ��ַ��ȡ�ɹ���");
			initBuilder.append(temp);
			initBuilder.append("\r\n");
			this.ADDRESS = temp;
			temp = this.setDDNSIPAddress(temp);
			if (temp == null) {
				initBuilder.append("[DDNS] ���õ�ַʧ��\r\n");
			} else {
				initBuilder.append("[DDNS] ���õ�ַ�ɹ���");
				initBuilder.append(temp);
				initBuilder.append("\r\n");
			}
		}
	}

	@Override
	public void run() {
		if (this.ENABLE) {
			while (true) {
				this.doLoop();
			}
		}
	}

	private void doLoop() {
		try {
			Date date = new Date();
			int time = 3605;
			time = time - date.getSeconds();
			time = time - (date.getMinutes() * 60);
			if (time < 60) {
				time = time + 3600;
			}
			Thread.sleep(time * 1000);
			while (true) {
				// ####### =====================================================
				new Thread(() -> {
					String temp = this.getIPAddress();
					if (temp == null) {
						temp = this.updateDDNSIPAddress();
						if (temp == null) {
							Module.userInfo(entry.OPERATOR(), "[DDNS] ��ַ����ʧ�ܣ���Ҫ�ֶ����룡");
						}
					} else {
						if (!temp.equals(this.ADDRESS)) {
							Module.userInfo(entry.OPERATOR(), "[DDNS] ��⵽��ַ���\r\n�ɵ�ַ��" + this.ADDRESS + "\r\n�µ�ַ��" + temp + "\r\n�����µ�ַ��" + this.setDDNSIPAddress(temp));
						}
					}
					// ####### =====================================================
				}).start();
				Thread.sleep(3600000L);
			}
		} catch (InterruptedException exce) {
			exce.printStackTrace();
			Module.userInfo(entry.OPERATOR(), "[DDNS] �����쳣�������ؼƻ�����");
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

	@Override
	public String generateReport(boolean fullreport, int loglevel, Object[] parameters) {
		return null;
	}

}
