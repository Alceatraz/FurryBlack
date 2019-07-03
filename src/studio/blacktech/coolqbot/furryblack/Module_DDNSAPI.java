package studio.blacktech.coolqbot.furryblack;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.util.Date;

import javax.naming.ConfigurationException;

import com.sobte.cqp.jcq.event.JcqAppAbstract;

import studio.blacktech.coolqbot.furryblack.common.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.module.Module;

public class Module_DDNSAPI extends Module {

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	public static String MODULE_PACKAGENAME = "ddnsapi";
	public static String MODULE_DISPLAYNAME = "动态域名";
	public static String MODULE_DESCRIPTION = "动态域名";
	public static String MODULE_VERSION = "1.0";
	public static String[] MODULE_USAGE = new String[] {};
	public static String[] MODULE_PRIVACY_TRIGER = new String[] {};
	public static String[] MODULE_PRIVACY_LISTEN = new String[] {};
	public static String[] MODULE_PRIVACY_STORED = new String[] {};
	public static String[] MODULE_PRIVACY_CACHED = new String[] {};
	public static String[] MODULE_PRIVACY_OBTAIN = new String[] {};

	// ==========================================================================================================================================================
	//
	// 成员变量
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

	public Module_DDNSAPI() throws Exception {
		super(MODULE_PACKAGENAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_TRIGER, MODULE_PRIVACY_LISTEN, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	@Override
	public void init(LoggerX logger) throws Exception {
		if (this.NEW_CONFIG) {
			logger.seek("[DDNSAPI] 配置文件不存在 - 生成默认配置");
			this.CONFIG.setProperty("enable_ddnsclient", "false");
			this.CONFIG.setProperty("ddnsapi_getaddress", "");
			this.CONFIG.setProperty("ddnsapi_setaddress", "");
			this.CONFIG.setProperty("ddnsapi_clientua", "BTSCoolQ/1.0");
			this.CONFIG.setProperty("ddnsapi_hostname", "");
			this.CONFIG.setProperty("ddnsapi_password", "");
			this.saveConfig();
			throw new ConfigurationException("[DDNSAPI] 生成配置文件 自动停机");
		}
	}

	@Override
	public void boot(LoggerX logger) throws Exception {

		this.loadConfig();

		this.ENABLE = Boolean.parseBoolean(this.CONFIG.getProperty("enable_ddnsclient", "false"));
		this.API_GETADDRESS = this.CONFIG.getProperty("ddnsapi_getaddress", "");
		this.API_SETADDRESS = this.CONFIG.getProperty("ddnsapi_setaddress", "");
		this.CLIENTUA = this.CONFIG.getProperty("ddnsapi_clientua", "BTSCoolQ/1.0");
		this.HOSTNAME = this.CONFIG.getProperty("ddnsapi_hostname", "");
		this.PASSWORD = this.CONFIG.getProperty("ddnsapi_password", "");

		logger.seek("[DDNS] 开关", this.ENABLE ? "启用" : "禁用");
		logger.seek("[DDNS] 获取地址", this.API_GETADDRESS);
		logger.seek("[DDNS] 设置域名", this.API_SETADDRESS);
		logger.seek("[DDNS] 表示", this.CLIENTUA);
		logger.seek("[DDNS] 域名", this.HOSTNAME);
		logger.seek("[DDNS] 密码", this.PASSWORD);

		String response = this.delegate.updateDDNSIP();

		if (response == null) {
			response = this.delegate.getIPAddress();
			if (response == null) {
				logger.mini("[DDNS] 设置地址失败", "需要手动介入");
			} else {
				this.ADDRESS = response;
				response = this.delegate.setDDNSAddress(this.ADDRESS);
				logger.mini("[DDNS] 设置地址成功", response);
			}
		} else {
			this.ADDRESS = response.split(" ")[1];
			logger.mini("[DDNS] 刷新地址成功", response);
		}

		this.thread = new Thread(new WorkerProcerss());
		this.thread.start();
	}

	@Override
	public void shut(LoggerX logger) throws Exception {
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
	// 工具函数
	//
	// ==========================================================================================================================================================

	@Override
	public String[] generateReport(int mode, final Message message, final Object... parameters) {
		return null;
	}

	public String doGetIPAddress() {
		try {
			final URL url = new URL(this.API_GETADDRESS);
			final URLConnection connection = url.openConnection();
			connection.setReadTimeout(5000);
			connection.setConnectTimeout(5000);
			connection.setRequestProperty("User-Agent", this.CLIENTUA);
			connection.connect();
			connection.getContent();
			final byte[] buffer = new byte[32];
			final InputStream rx = connection.getInputStream();
			rx.read(buffer);
			return new String(buffer, "UTF-8").trim();
		} catch (IOException exception) {
			exception.printStackTrace();
			entry.getMessage().adminInfo("[DDNS]获取异常" + exception.getMessage());
			return null;
		}
	}

	public String doUpdateDDNSIP() {
		try {
			final URL url = new URL(this.API_SETADDRESS + "?hostname=" + this.HOSTNAME);
			final URLConnection connection = url.openConnection();
			connection.setReadTimeout(5000);
			connection.setConnectTimeout(5000);
			connection.setRequestProperty("User-Agent", this.CLIENTUA);
			connection.setRequestProperty("Authorization", this.PASSWORD);
			connection.connect();
			connection.getContent();
			final byte[] buffer = new byte[32];
			final InputStream rx = connection.getInputStream();
			rx.read(buffer);
			return new String(buffer, "UTF-8").trim();
		} catch (IOException exception) {
			exception.printStackTrace();
			entry.getMessage().adminInfo("[DDNS]获取异常" + exception.getMessage());
			return null;
		}
	}

	public String doSetDDNSAddress(final String address) {
		try {
			this.ADDRESS = address;
			final URL url = new URL(this.API_SETADDRESS + "?hostname=" + this.HOSTNAME + "&myip=" + address);
			final URLConnection connection = url.openConnection();
			connection.setReadTimeout(5000);
			connection.setConnectTimeout(5000);
			connection.setRequestProperty("User-Agent", this.CLIENTUA);
			connection.setRequestProperty("Authorization", this.PASSWORD);
			connection.connect();
			connection.getContent();
			final byte[] buffer = new byte[32];
			final InputStream rx = connection.getInputStream();
			rx.read(buffer);
			return new String(buffer, "UTF-8").trim();
		} catch (IOException exception) {
			exception.printStackTrace();
			entry.getMessage().adminInfo("[DDNS]获取异常" + exception.getMessage());
			return null;
		}
	}

	public DDNSapiDelegate getDelegate() {
		return this.delegate;
	}

	public class DDNSapiDelegate {

		public String getIPAddress() {
			return doGetIPAddress();
		}

		public String updateDDNSIP() {
			return doUpdateDDNSIP();
		}

		public String setDDNSAddress(final String address) {
			return doSetDDNSAddress(address);
		}

	}

	@SuppressWarnings("deprecation")
	class WorkerProcerss implements Runnable {

		@Override
		public void run() {

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
					String response = delegate.updateDDNSIP();
					if (response == null) {
						entry.getMessage().adminInfo("[DDNS] 更新失败：更新新地址失败");
					} else {
						response = response.split(" ")[1];
						if (!Module_DDNSAPI.this.ADDRESS.equals(response)) {
							entry.getMessage().adminInfo("[DDNS] 检测到地址变更： " + LoggerX.time() + "\r\n旧地址：" + Module_DDNSAPI.this.ADDRESS + "\r\n新地址：" + response);
							Module_DDNSAPI.this.ADDRESS = response;
						}
					}
					// =======================================================
					final SecureRandom random = new SecureRandom();
					Thread.sleep(random.nextInt(60000));
					// =======================================================
				} catch (InterruptedException exception) {
				}
			}
		}
	}
}
