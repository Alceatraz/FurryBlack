package studio.blacktech.coolqbot.furryblack.scheduler;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import studio.blacktech.common.ConfigureX;
import studio.blacktech.common.exception.ReInitializationException;
import studio.blacktech.coolqbot.furryblack.module.Module;
import studio.blacktech.coolqbot.furryblack.module.ModuleScheduler;

@SuppressWarnings("deprecation")
public class Scheduler_DDNS extends ModuleScheduler {

	private static boolean INITIALIZATIONLOCK = false;

	private static final String API_GETADDRESS = "http://ip.3322.net/";
	private static final String API_SETADDRESS = "http://members.3322.net/dyndns/update";

	private static boolean ENABLE;
	private static String CLIENTUA;
	private static String HOSTNAME;
	private static String PASSWORD;

	private static String ADDRESS = null;
	private static String RESPONCE = null;

	private static final Thread WORKER = new Thread(() -> {

		Scheduler_DDNS.RESPONCE = Scheduler_DDNS.getIPAddress();
		if (Scheduler_DDNS.RESPONCE == null) {
			Module.userInfo(ConfigureX.OPERATOR(), "[DDNS] 地址更新失败");
		} else if (!Scheduler_DDNS.RESPONCE.equals(Scheduler_DDNS.ADDRESS)) {
			Scheduler_DDNS.RESPONCE = Scheduler_DDNS.setDDNSIPAddress(Scheduler_DDNS.RESPONCE);
			if (Scheduler_DDNS.RESPONCE == null) {
				Module.userInfo(ConfigureX.OPERATOR(), "[DDNS] 域名更新失败");
			} else {
				String[] temp = Scheduler_DDNS.RESPONCE.split(" ");
				Module.userInfo(ConfigureX.OPERATOR(), "[DDNS] 地址变更 " + temp[0] + "\r\n旧地址： " + Scheduler_DDNS.ADDRESS + "新地址： " + temp[1]);
				Scheduler_DDNS.ADDRESS = Scheduler_DDNS.RESPONCE;
			}
		}
	});

	@Override
	public void run() {
		try {
			if (Scheduler_DDNS.ENABLE) {
				Thread.sleep(3000);
				Scheduler_DDNS.ADDRESS = Scheduler_DDNS.getIPAddress();
				if (Scheduler_DDNS.ADDRESS == null) {
					Module.userInfo(ConfigureX.OPERATOR(), "[DDNS] 地址初始化失败");
					Scheduler_DDNS.RESPONCE = Scheduler_DDNS.updateDDNSIPAddress();
					Module.userInfo(ConfigureX.OPERATOR(), "[DDNS] 强制更新响应： " + Scheduler_DDNS.RESPONCE);
				} else {
					Module.userInfo(ConfigureX.OPERATOR(), "[DDNS] 地址初始化成功: " + Scheduler_DDNS.ADDRESS);
					Scheduler_DDNS.RESPONCE = Scheduler_DDNS.setDDNSIPAddress(Scheduler_DDNS.ADDRESS);
					Module.userInfo(ConfigureX.OPERATOR(), "[DDNS] 域名更新响应： " + Scheduler_DDNS.RESPONCE);
				}
				Date date = new Date();
				int time = 3605;
				time = time - date.getSeconds();
				time = time - (date.getMinutes() * 60);
				if (time < 60) {
					time = time + 3600;
				}
				Thread.sleep(time * 1000);
				while (true) {
					Scheduler_DDNS.WORKER.start();
					Thread.sleep(3600000L);
				}
			}
		} catch (InterruptedException exce) {
			exce.printStackTrace();
		}
	}

	public static void init(boolean enable, final String clientua, final String hostname, final String password) throws ReInitializationException {
		if (Scheduler_DDNS.INITIALIZATIONLOCK) {
			throw new ReInitializationException();
		}
		Scheduler_DDNS.INITIALIZATIONLOCK = true;
		Scheduler_DDNS.ENABLE = enable;
		Scheduler_DDNS.CLIENTUA = clientua;
		Scheduler_DDNS.HOSTNAME = hostname;
		Scheduler_DDNS.PASSWORD = password;
	}

	public static String getIPAddress() {
		try {
			final URL url = new URL(Scheduler_DDNS.API_GETADDRESS);
			final URLConnection connection = url.openConnection();
			connection.setReadTimeout(5000);
			connection.setConnectTimeout(2000);
			connection.setRequestProperty("User-Agent", Scheduler_DDNS.CLIENTUA);
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

	public static String updateDDNSIPAddress() {
		try {
			final URL url = new URL(Scheduler_DDNS.API_SETADDRESS + "?hostname=" + Scheduler_DDNS.HOSTNAME);
			final URLConnection connection = url.openConnection();
			connection.setReadTimeout(5000);
			connection.setConnectTimeout(2000);
			connection.setRequestProperty("User-Agent", Scheduler_DDNS.CLIENTUA);
			connection.setRequestProperty("Authorization", Scheduler_DDNS.PASSWORD);
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

	public static String setDDNSIPAddress(final String address) {
		try {
			Scheduler_DDNS.ADDRESS = address;
			final URL url = new URL(Scheduler_DDNS.API_SETADDRESS + "?hostname=" + Scheduler_DDNS.HOSTNAME + "&myip=" + address);
			final URLConnection connection = url.openConnection();
			connection.setReadTimeout(5000);
			connection.setConnectTimeout(2000);
			connection.setRequestProperty("User-Agent", Scheduler_DDNS.CLIENTUA);
			connection.setRequestProperty("Authorization", Scheduler_DDNS.PASSWORD);
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
	public String generateReport() {
		return null;
	}
}
