package studio.blacktech.coolqbot.furryblack.scheduler;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import studio.blacktech.common.exception.ReInitializationException;
import studio.blacktech.coolqbot.furryblack.entry;

public class Worker_DDNS {

	private static boolean INITIALIZATIONLOCK = false;

	private static final String API_GETADDRESS = "http://ip.3322.net/";
	private static final String API_SETADDRESS = "http://members.3322.net/dyndns/update";

	private static String CLIENTUA;
	private static String HOSTNAME;
	private static String PASSWORD;

	private static String ADDRESS;

	public static void init(String clientua, String hostname, String password) throws ReInitializationException {
		if (INITIALIZATIONLOCK) {
			throw new ReInitializationException();
		}
		INITIALIZATIONLOCK = true;
		CLIENTUA = clientua;
		HOSTNAME = hostname;
		PASSWORD = password;
	}

	private static String getIPAddress() throws IOException {
		URL url = new URL(API_GETADDRESS);
		URLConnection connection = url.openConnection();
		connection.setRequestProperty("User-Agent", CLIENTUA);
		connection.connect();
		connection.getContent();

		byte[] buffer = new byte[1024];
		InputStream rx = connection.getInputStream();
		rx.read(buffer);
		return new String(buffer, "UTF-8").trim();

	}

	private static String updateDDNSIPAddress() throws IOException {
		URL url = new URL(API_SETADDRESS + "?hostname=" + HOSTNAME);
		URLConnection connection = url.openConnection();
		connection.setRequestProperty("Authorization", PASSWORD);
		connection.setRequestProperty("User-Agent", CLIENTUA);
		connection.connect();
		connection.getContent();

		byte[] buffer = new byte[1024];
		InputStream rx = connection.getInputStream();
		rx.read(buffer);
		return new String(buffer, "UTF-8").trim();
	}

	private static String setDDNSIPAddress(final String address) throws IOException {
		URL url = new URL(API_SETADDRESS + "?hostname=" + HOSTNAME + "&myip=" + address);
		URLConnection connection = url.openConnection();
		connection.setRequestProperty("Authorization", PASSWORD);
		connection.setRequestProperty("User-Agent", CLIENTUA);
		connection.connect();
		connection.getContent();

		byte[] buffer = new byte[1024];
		InputStream rx = connection.getInputStream();
		rx.read(buffer);
		return new String(buffer, "UTF-8").trim();
	}
}
