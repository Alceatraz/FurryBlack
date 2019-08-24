package studio.blacktech.coolqbot.furryblack;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import com.sobte.cqp.jcq.event.JcqApp;
import com.sobte.cqp.jcq.event.JcqAppAbstract;

import studio.blacktech.coolqbot.furryblack.common.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.module.Module;

public class Module_Dynamic extends Module {

	private static final long serialVersionUID = 1L;

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "core_ddnsclient";
	private static String MODULE_COMMANDNAME = "ddns";
	private static String MODULE_DISPLAYNAME = "动态域名";
	private static String MODULE_DESCRIPTION = "动态域名客户端";
	private static String MODULE_VERSION = "1.0";
	private static String[] MODULE_USAGE = new String[] {};
	private static String[] MODULE_PRIVACY_TRIGER = new String[] {};
	private static String[] MODULE_PRIVACY_LISTEN = new String[] {};
	private static String[] MODULE_PRIVACY_STORED = new String[] {};
	private static String[] MODULE_PRIVACY_CACHED = new String[] {};
	private static String[] MODULE_PRIVACY_OBTAIN = new String[] {};

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

	public Module_Dynamic() throws Exception {
		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_TRIGER, MODULE_PRIVACY_LISTEN, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	@Override
	public void init(LoggerX logger) throws Exception {

		this.initConfFolder();
		this.initCofigurtion();

		if (this.NEW_CONFIG) {
			logger.seek("[Dynamic] 配置文件不存在 - 生成默认配置");
			this.CONFIG.setProperty("enable_ddnsclient", "false");
			this.CONFIG.setProperty("ddnsapi_getaddress", "");
			this.CONFIG.setProperty("ddnsapi_setaddress", "");
			this.CONFIG.setProperty("ddnsapi_clientua", "BTSCoolQ/1.0");
			this.CONFIG.setProperty("ddnsapi_hostname", "");
			this.CONFIG.setProperty("ddnsapi_password", "");
			this.saveConfig();
		} else {
			this.loadConfig();
		}

		this.ENABLE = Boolean.parseBoolean(this.CONFIG.getProperty("enable_ddnsclient", "false"));

		logger.seek("[Dynamic] 开关", this.ENABLE ? "启用" : "禁用");

		if (this.ENABLE) {

			this.API_GETADDRESS = this.CONFIG.getProperty("ddnsapi_getaddress", "");
			this.API_SETADDRESS = this.CONFIG.getProperty("ddnsapi_setaddress", "");
			this.CLIENTUA = this.CONFIG.getProperty("ddnsapi_clientua", "BTSCoolQ/1.0");
			this.HOSTNAME = this.CONFIG.getProperty("ddnsapi_hostname", "");
			this.PASSWORD = this.CONFIG.getProperty("ddnsapi_password", "");

			logger.seek("[Dynamic] 获取", this.API_GETADDRESS);
			logger.seek("[Dynamic] 刷新", this.API_SETADDRESS);
			logger.seek("[Dynamic] 标识", this.CLIENTUA);
			logger.seek("[Dynamic] 域名", this.HOSTNAME);
			logger.seek("[Dynamic] 密码", this.PASSWORD.substring(6, 12));

			String response = this.delegate.updateDDNSIP();

			if (response == null) {
				response = this.delegate.getIPAddress();
				if (response == null) {
					logger.mini("[Dynamic] 设置地址失败", "需要手动介入");
				} else {
					this.ADDRESS = response;
					response = this.delegate.setDDNSAddress(this.ADDRESS);
					logger.seek("[Dynamic] 设置地址成功", response);
				}
			} else {
				this.ADDRESS = response.split(" ")[1];
				logger.seek("[Dynamic] 刷新地址成功", response);
			}
		}
	}

	@Override
	public void boot(LoggerX logger) throws Exception {
		if (this.ENABLE) {
			logger.info("[Dynamic] 启动工作线程");
			this.thread = new Thread(new WorkerProcerss());
			this.thread.start();
		}
	}

	@Override
	public void shut(LoggerX logger) throws Exception {
		if (this.ENABLE) {
			logger.info("[Dynamic] 终止工作线程");
			this.thread.interrupt();
			this.thread.join();
		}
	}

	@Override
	public void save(LoggerX logger) throws Exception {
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
			entry.getMessage().adminInfo("[Dynamic] 获取异常" + exception.getMessage());
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
			entry.getMessage().adminInfo("[Dynamic] 获取异常" + exception.getMessage());
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
			entry.getMessage().adminInfo("[Dynamic] 获取异常" + exception.getMessage());
			return null;
		}
	}

	public DDNSapiDelegate getDelegate() {
		return this.delegate;
	}

	public class DDNSapiDelegate {

		public String getIPAddress() {
			return Module_Dynamic.this.doGetIPAddress();
		}

		public String updateDDNSIP() {
			return Module_Dynamic.this.doUpdateDDNSIP();
		}

		public String setDDNSAddress(String address) {
			return Module_Dynamic.this.doSetDDNSAddress(address);
		}

	}

	@SuppressWarnings("deprecation")
	class WorkerProcerss implements Runnable {

		@Override
		public void run() {
			long time;
			Date date;
			do {
				try {
					// =======================================================
					while (true) {
						date = new Date();
						time = 600L;
						time = time - date.getSeconds();
						time = time - date.getMinutes() % 10 * 60;
						if (time < 60) { time = time + 600; }
						time = time * 1000;
						time = time - 5;
						JcqApp.CQ.logInfo("FurryBlackWorker", "[Module_Dynamic] 休眠：" + time);
						Thread.sleep(time);
						// =======================================================
						JcqApp.CQ.logInfo("FurryBlackWorker", "[Module_Dynamic] 执行");
						String response = Module_Dynamic.this.delegate.updateDDNSIP();
						if (response == null) {
							entry.getMessage().adminInfo("[Dynamic] 更新失败：更新新地址失败");
						} else {
							response = response.split(" ")[1];
							if (!Module_Dynamic.this.ADDRESS.equals(response)) {
								entry.getMessage().adminInfo("[DDNS] 检测到地址变更： " + LoggerX.time() + "\r\n旧地址：" + Module_Dynamic.this.ADDRESS + "\r\n新地址：" + response);
								Module_Dynamic.this.ADDRESS = response;
							}
						}
						JcqApp.CQ.logInfo("FurryBlackWorker", "[Module_Dynamic] 结果：" + response);
						// =======================================================
					}
				} catch (InterruptedException exception) {
					if (JcqAppAbstract.enable) {
						JcqApp.CQ.logWarning("FurryBlackWorker", "[Module_DDynamic] 异常");
					} else {
						JcqApp.CQ.logInfo("FurryBlackWorker", "[Module_Dynamic] 关闭");
					}
				}
			} while (JcqAppAbstract.enable);
		}
	}
}
