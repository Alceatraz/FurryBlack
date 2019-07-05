package studio.blacktech.coolqbot.furryblack;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.util.Date;

import com.sobte.cqp.jcq.event.JcqApp;
import com.sobte.cqp.jcq.event.JcqAppAbstract;

import studio.blacktech.coolqbot.furryblack.common.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.module.Module;

public class Module_DDNS extends Module {

	// ==========================================================================================================================================================
	//
	// ģ���������
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "core_ddnsclient";
	private static String MODULE_COMMANDNAME = "ddns";
	private static String MODULE_DISPLAYNAME = "��̬����";
	private static String MODULE_DESCRIPTION = "��̬�����ͻ���";
	private static String MODULE_VERSION = "1.0";
	private static String[] MODULE_USAGE = new String[] {};
	private static String[] MODULE_PRIVACY_TRIGER = new String[] {};
	private static String[] MODULE_PRIVACY_LISTEN = new String[] {};
	private static String[] MODULE_PRIVACY_STORED = new String[] {};
	private static String[] MODULE_PRIVACY_CACHED = new String[] {};
	private static String[] MODULE_PRIVACY_OBTAIN = new String[] {};

	// ==========================================================================================================================================================
	//
	// ��Ա����
	//
	// ==========================================================================================================================================================

	private boolean ENABLE = true;

	private String API_GETADDRESS;
	private String API_SETADDRESS;

	private String CLIENTUA;
	private String HOSTNAME;
	private String PASSWORD;

	private String ADDRESS = null;

	private Thread thread;
	private DDNSapiDelegate delegate = new DDNSapiDelegate();

	public Module_DDNS() throws Exception {
		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_TRIGER, MODULE_PRIVACY_LISTEN, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	@Override
	public void init(LoggerX logger) throws Exception {
		if (this.NEW_CONFIG) {
			logger.seek("[DDNS] �����ļ������� - ����Ĭ������");
			this.CONFIG.setProperty("enable_ddnsclient", "false");
			this.CONFIG.setProperty("ddnsapi_getaddress", "");
			this.CONFIG.setProperty("ddnsapi_setaddress", "");
			this.CONFIG.setProperty("ddnsapi_clientua", "BTSCoolQ/1.0");
			this.CONFIG.setProperty("ddnsapi_hostname", "");
			this.CONFIG.setProperty("ddnsapi_password", "");
			this.saveConfig();
		}
	}

	@Override
	public void boot(LoggerX logger) throws Exception {

		this.loadConfig();

		this.ENABLE = Boolean.parseBoolean(this.CONFIG.getProperty("enable_ddnsclient", "false"));

		if (!this.ENABLE) { return; }

		this.API_GETADDRESS = this.CONFIG.getProperty("ddnsapi_getaddress", "");
		this.API_SETADDRESS = this.CONFIG.getProperty("ddnsapi_setaddress", "");
		this.CLIENTUA = this.CONFIG.getProperty("ddnsapi_clientua", "BTSCoolQ/1.0");
		this.HOSTNAME = this.CONFIG.getProperty("ddnsapi_hostname", "");
		this.PASSWORD = this.CONFIG.getProperty("ddnsapi_password", "");

		logger.seek("[DDNS] ����", this.ENABLE ? "����" : "����");
		logger.seek("[DDNS] ��ȡ��ַ", this.API_GETADDRESS);
		logger.seek("[DDNS] ��������", this.API_SETADDRESS);
		logger.seek("[DDNS] ��ʾ", this.CLIENTUA);
		logger.seek("[DDNS] ����", this.HOSTNAME);
		logger.seek("[DDNS] ����", this.PASSWORD);

		String response = this.delegate.updateDDNSIP();

		if (response == null) {
			response = this.delegate.getIPAddress();
			if (response == null) {
				logger.mini("[DDNS] ���õ�ַʧ��", "��Ҫ�ֶ�����");
			} else {
				this.ADDRESS = response;
				response = this.delegate.setDDNSAddress(this.ADDRESS);
				logger.mini("[DDNS] ���õ�ַ�ɹ�", response);
			}
		} else {
			this.ADDRESS = response.split(" ")[1];
			logger.mini("[DDNS] ˢ�µ�ַ�ɹ�", response);
		}

		this.thread = new Thread(new WorkerProcerss());
		this.thread.start();
	}

	@Override
	public void shut(LoggerX logger) throws Exception {
		if (!this.ENABLE) { return; }
		this.thread.interrupt();
	}

	@Override
	public void reload(LoggerX logger) throws Exception {
	}

	@Override
	public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) {
	}

	@Override
	public void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) {
	}

	// ==========================================================================================================================================================
	//
	// ���ߺ���
	//
	// ==========================================================================================================================================================

	@Override
	public String[] generateReport(int mode, Message message, Object... parameters) {
		return null;
	}

	public String doGetIPAddress() {
		try {
			URL url = new URL(this.API_GETADDRESS);
			URLConnection connection = url.openConnection();
			connection.setReadTimeout(5000);
			connection.setConnectTimeout(5000);
			connection.setRequestProperty("User-Agent", this.CLIENTUA);
			connection.connect();
			connection.getContent();
			byte[] buffer = new byte[32];
			InputStream rx = connection.getInputStream();
			rx.read(buffer);
			return new String(buffer, "UTF-8").trim();
		} catch (IOException exception) {
			exception.printStackTrace();
			entry.getMessage().adminInfo("[DDNS]��ȡ�쳣" + exception.getMessage());
			return null;
		}
	}

	public String doUpdateDDNSIP() {
		try {
			URL url = new URL(this.API_SETADDRESS + "?hostname=" + this.HOSTNAME);
			URLConnection connection = url.openConnection();
			connection.setReadTimeout(5000);
			connection.setConnectTimeout(5000);
			connection.setRequestProperty("User-Agent", this.CLIENTUA);
			connection.setRequestProperty("Authorization", this.PASSWORD);
			connection.connect();
			connection.getContent();
			byte[] buffer = new byte[32];
			InputStream rx = connection.getInputStream();
			rx.read(buffer);
			return new String(buffer, "UTF-8").trim();
		} catch (IOException exception) {
			exception.printStackTrace();
			entry.getMessage().adminInfo("[DDNS]��ȡ�쳣" + exception.getMessage());
			return null;
		}
	}

	public String doSetDDNSAddress(String address) {
		try {
			this.ADDRESS = address;
			URL url = new URL(this.API_SETADDRESS + "?hostname=" + this.HOSTNAME + "&myip=" + address);
			URLConnection connection = url.openConnection();
			connection.setReadTimeout(5000);
			connection.setConnectTimeout(5000);
			connection.setRequestProperty("User-Agent", this.CLIENTUA);
			connection.setRequestProperty("Authorization", this.PASSWORD);
			connection.connect();
			connection.getContent();
			byte[] buffer = new byte[32];
			InputStream rx = connection.getInputStream();
			rx.read(buffer);
			return new String(buffer, "UTF-8").trim();
		} catch (IOException exception) {
			exception.printStackTrace();
			entry.getMessage().adminInfo("[DDNS]��ȡ�쳣" + exception.getMessage());
			return null;
		}
	}

	public DDNSapiDelegate getDelegate() {
		return this.delegate;
	}

	public class DDNSapiDelegate {

		public String getIPAddress() {
			return Module_DDNS.this.doGetIPAddress();
		}

		public String updateDDNSIP() {
			return Module_DDNS.this.doUpdateDDNSIP();
		}

		public String setDDNSAddress(String address) {
			return Module_DDNS.this.doSetDDNSAddress(address);
		}

	}

	@SuppressWarnings("deprecation")
	class WorkerProcerss implements Runnable {

		@Override
		public void run() {
			JcqApp.CQ.logInfo("FurryBlack", "DDNS - Worker ������");
			long time;
			Date date;
			while (JcqAppAbstract.enable) {
				try {
					// =======================================================
					date = new Date();
					time = 605L;
					time = time - date.getSeconds();
					time = time - date.getMinutes() % 10 * 60;
					if (time < 60) { time = time + 600; }
					time = time * 1000;
					time = time - 5;
					Thread.sleep(time);
					// =======================================================
					String response = Module_DDNS.this.delegate.updateDDNSIP();
					if (response == null) {
						entry.getMessage().adminInfo("[DDNS] ����ʧ�ܣ������µ�ַʧ��");
					} else {
						response = response.split(" ")[1];
						if (!Module_DDNS.this.ADDRESS.equals(response)) {
							entry.getMessage().adminInfo("[DDNS] ��⵽��ַ����� " + LoggerX.time() + "\r\n�ɵ�ַ��" + Module_DDNS.this.ADDRESS + "\r\n�µ�ַ��" + response);
							Module_DDNS.this.ADDRESS = response;
						}
					}
					// =======================================================
					SecureRandom random = new SecureRandom();
					Thread.sleep(random.nextInt(60000));
					// =======================================================
				} catch (InterruptedException exception) {

				}
			}
		}
	}
}
