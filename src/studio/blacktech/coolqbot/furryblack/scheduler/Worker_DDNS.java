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
public class Worker_DDNS extends ModuleScheduler {

	private static boolean INITIALIZATIONLOCK = false;

	private static final String API_GETADDRESS = "http://ip.3322.net/";
	private static final String API_SETADDRESS = "http://members.3322.net/dyndns/update";

	private static String CLIENTUA;
	private static String HOSTNAME;
	private static String PASSWORD;

	private static String ADDRESS = null;
	private static String RESPONCE = null;

	private static final Thread WORKER = new Thread(() -> {
		Worker_DDNS.RESPONCE = Worker_DDNS.getIPAddress();

		if (Worker_DDNS.RESPONCE == null) {
			Module.userInfo(ConfigureX.OPERATOR(), "[DDNS] 地址获取失败");
		} else if (!Worker_DDNS.RESPONCE.equals(Worker_DDNS.ADDRESS)) {
			Worker_DDNS.RESPONCE = Worker_DDNS.setDDNSIPAddress(Worker_DDNS.RESPONCE);
			if (Worker_DDNS.RESPONCE == null) {
				Module.userInfo(ConfigureX.OPERATOR(), "[DDNS] 域名更新失败");
			} else {
				String[] temp = Worker_DDNS.RESPONCE.split(" ");
				Module.userInfo(ConfigureX.OPERATOR(), "[DDNS] 地址变更 " + temp[0] + "\r\n旧地址： " + Worker_DDNS.ADDRESS + "新地址： " + temp[1]);
				Worker_DDNS.ADDRESS = Worker_DDNS.RESPONCE;
			}
		}
	});

	@Override
	public void run() {

		Worker_DDNS.ADDRESS = Worker_DDNS.getIPAddress();
		Module.userInfo(ConfigureX.OPERATOR(), "[DDNS] 成功 " + Worker_DDNS.ADDRESS);
		if (Worker_DDNS.ADDRESS == null) {
			Worker_DDNS.RESPONCE = Worker_DDNS.updateDDNSIPAddress();
			Module.userInfo(ConfigureX.OPERATOR(), "[DDNS] 重试 " + Worker_DDNS.RESPONCE);
		} else {
			Worker_DDNS.RESPONCE = Worker_DDNS.setDDNSIPAddress(Worker_DDNS.ADDRESS);
			Module.userInfo(ConfigureX.OPERATOR(), "[DDNS] 重试 " + Worker_DDNS.RESPONCE);
		}

		Date date = new Date();
		int time = 3605;
		time = time - date.getSeconds();
		time = time - (date.getMinutes() * 60);

		if (time < 60) {
			time = time + 3600;
		}

		try {
			Thread.sleep(time * 1000);
		} catch (InterruptedException exce) {
			exce.printStackTrace();
		}

		while (true) {
			Worker_DDNS.WORKER.start();
			try {
				Thread.sleep(3600000L);
			} catch (InterruptedException exce) {
				exce.printStackTrace();
			}
		}
	}

	public static void init(final String clientua, final String hostname, final String password) throws ReInitializationException {
		if (Worker_DDNS.INITIALIZATIONLOCK) {
			throw new ReInitializationException();
		}
		Worker_DDNS.INITIALIZATIONLOCK = true;
		Worker_DDNS.CLIENTUA = clientua;
		Worker_DDNS.HOSTNAME = hostname;
		Worker_DDNS.PASSWORD = password;
	}

	public static String getIPAddress() {
		try {
			final URL url = new URL(Worker_DDNS.API_GETADDRESS);
			final URLConnection connection = url.openConnection();
			connection.setRequestProperty("User-Agent", Worker_DDNS.CLIENTUA);
			connection.connect();
			connection.getContent();
			final byte[] buffer = new byte[1024];
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
			final URL url = new URL(Worker_DDNS.API_SETADDRESS + "?hostname=" + Worker_DDNS.HOSTNAME);
			final URLConnection connection = url.openConnection();
			connection.setRequestProperty("Authorization", Worker_DDNS.PASSWORD);
			connection.setRequestProperty("User-Agent", Worker_DDNS.CLIENTUA);
			connection.connect();
			connection.getContent();
			final byte[] buffer = new byte[1024];
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
			final URL url = new URL(Worker_DDNS.API_SETADDRESS + "?hostname=" + Worker_DDNS.HOSTNAME + "&myip=" + address);
			final URLConnection connection = url.openConnection();
			connection.setRequestProperty("Authorization", Worker_DDNS.PASSWORD);
			connection.setRequestProperty("User-Agent", Worker_DDNS.CLIENTUA);
			connection.connect();
			connection.getContent();
			final byte[] buffer = new byte[1024];
			final InputStream rx = connection.getInputStream();
			rx.read(buffer);
			return new String(buffer, "UTF-8").trim();
		} catch (Exception exce) {
			exce.printStackTrace();
			return null;
		}
	}

	@Override
	public String getReport() {
		return null;
	}
}
