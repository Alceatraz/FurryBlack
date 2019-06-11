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

		this.MODULE_DISPLAYNAME = "动态域名";
		this.MODULE_PACKAGENAME = "ddns";
		this.MODULE_DESCRIPTION = "动态域名";
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
			initBuilder.append("\r\n[DDNS] 客户端：");
			initBuilder.append(this.ENABLE ? "启用" : "禁用");
			initBuilder.append("\r\n[DDNS] 标识：");
			initBuilder.append(this.CLIENTUA);
			initBuilder.append("\r\n[DDNS] 域名：");
			initBuilder.append(this.HOSTNAME);
			initBuilder.append("\r\n[DDNS] 密码：");
			initBuilder.append(this.PASSWORD);
		}

		String temp = this.getIPAddress();
		if (temp == null) {
			initBuilder.append("\r\n[DDNS] 地址获取失败");
			temp = this.updateDDNSIPAddress();
			if (temp == null) {
				initBuilder.append("\r\n[DDNS] 更新地址失败，需要手动介入");
			} else {
				initBuilder.append("\r\b[DDNS] 更新地址成功： ");
				initBuilder.append(temp);
			}
		} else {
			initBuilder.append("\r\n[DDNS] 地址获取成功： ");
			initBuilder.append(temp);
			this.ADDRESS = temp;
			temp = this.setDDNSIPAddress(temp);
			if (temp == null) {
				initBuilder.append("\r\n[DDNS] 设置地址失败");
			} else {
				initBuilder.append("\r\n[DDNS] 设置地址成功： ");
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
					if (temp == null) { Module.userInfo(entry.OPERATOR(), "[DDNS] 地址更新失败，需要手动介入！"); }
				} else {
					if (!temp.equals(this.ADDRESS)) { Module.userInfo(entry.OPERATOR(), "[DDNS] 检测到地址变更\r\n旧地址：" + this.ADDRESS + "\r\n新地址：" + temp + "\r\n设置新地址：" + this.setDDNSIPAddress(temp)); }
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
