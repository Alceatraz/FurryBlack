package studio.blacktech.coolqbot.furryblack.scheduler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import studio.blacktech.common.exception.ReInitializationException;

public class Worker_DDNS {

	private static boolean INITIALIZATIONLOCK = false;

	private static final String API_GETADDRESS = "http://ip.3322.net/";
	private static final String API_SETADDRESS = "http://members.3322.net/dyndns/update";

	private static String CLIENTUA;
	private static String HOSTNAME;
	private static String PASSWORD;

	private static String ADDRESS;

	public static void init(final String clientua, final String hostname, final String password) throws ReInitializationException {
		if (Worker_DDNS.INITIALIZATIONLOCK) {
			throw new ReInitializationException();
		}
		Worker_DDNS.INITIALIZATIONLOCK = true;
		Worker_DDNS.CLIENTUA = clientua;
		Worker_DDNS.HOSTNAME = hostname;
		Worker_DDNS.PASSWORD = password;
	}

	private static String getIPAddress() throws IOException {
		final URL url = new URL(Worker_DDNS.API_GETADDRESS);
		final URLConnection connection = url.openConnection();
		connection.setRequestProperty("User-Agent", Worker_DDNS.CLIENTUA);
		connection.connect();
		connection.getContent();

		final byte[] buffer = new byte[1024];
		final InputStream rx = connection.getInputStream();
		rx.read(buffer);
		return new String(buffer, "UTF-8").trim();

	}

	private static String updateDDNSIPAddress() throws IOException {
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
	}

	private static String setDDNSIPAddress(final String address) throws IOException {
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
	}
}
