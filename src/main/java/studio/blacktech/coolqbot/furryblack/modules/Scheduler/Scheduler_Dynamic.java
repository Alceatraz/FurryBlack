package studio.blacktech.coolqbot.furryblack.modules.Scheduler;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.regex.Pattern;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.annotation.ModuleSchedulerComponent;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleScheduler;


@ModuleSchedulerComponent
public class Scheduler_Dynamic extends ModuleScheduler {

	private static final long serialVersionUID = 1L;
	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================
	private static String MODULE_PACKAGENAME = "Scheduler_Dynamic";
	private static String MODULE_COMMANDNAME = "dynamic";
	private static String MODULE_DISPLAYNAME = "动态域名";
	private static String MODULE_DESCRIPTION = "动态域名";
	private static String MODULE_VERSION = "4.0";
	private static String[] MODULE_USAGE = new String[] {};
	public static String[] MODULE_PRIVACY_STORED = new String[] {};
	public static String[] MODULE_PRIVACY_CACHED = new String[] {};
	public static String[] MODULE_PRIVACY_OBTAIN = new String[] {};
	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================
	private String API_GETADDRESS;
	private String API_SETADDRESS;
	private String CLIENTUA;
	private String HOSTNAME;
	private String PASSWORD;
	private Thread thread;
	private int COUNT_GETIP = 0;
	private int COUNT_SETIP = 0;
	private int COUNT_FRESH = 0;
	private int COUNT_GETIP_FAILED = 0;
	private int COUNT_SETIP_FAILED = 0;
	private int COUNT_FRESH_FAILED = 0;
	private int COUNT_CHANGE = 0;
	private int COUNT_FAILED = 0;
	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================


	public Scheduler_Dynamic() throws Exception {

		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);

	}


	@Override
	public boolean init() throws Exception {

		initAppFolder();
		initPropertiesConfigurtion();
		if (NEW_CONFIG) {
			logger.seek("配置文件不存在 - 生成默认配置");
			CONFIG.setProperty("enable", "false");
			CONFIG.setProperty("getaddress", "");
			CONFIG.setProperty("setaddress", "");
			CONFIG.setProperty("clientua", "BTSCoolQ/1.0");
			CONFIG.setProperty("hostname", "");
			CONFIG.setProperty("password", "");
			saveConfig();
		} else {
			loadConfig();
		}
		ENABLE = Boolean.parseBoolean(CONFIG.getProperty("enable", "false"));
		logger.seek("开关", ENABLE ? "启用" : "禁用");
		if (!ENABLE) { return false; }
		API_GETADDRESS = CONFIG.getProperty("getaddress", "");
		API_SETADDRESS = CONFIG.getProperty("setaddress", "");
		CLIENTUA = CONFIG.getProperty("clientua", "BTSCoolQ/1.0");
		HOSTNAME = CONFIG.getProperty("hostname", "");
		PASSWORD = CONFIG.getProperty("password", "");
		logger.seek("获取", API_GETADDRESS);
		logger.seek("刷新", API_SETADDRESS);
		logger.seek("标识", CLIENTUA);
		logger.seek("域名", HOSTNAME);
		logger.seek("密码", PASSWORD.substring(6, 12));
		return true;

	}


	@Override
	public boolean boot() throws Exception {

		if (!ENABLE) { return false; }
		logger.info("启动工作线程");
		thread = new Thread(new Worker());
		thread.start();
		return true;

	}


	@Override
	public boolean save() throws Exception {

		return true;

	}


	@Override
	public boolean shut() throws Exception {

		if (!ENABLE) { return false; }
		logger.info("终止工作线程");
		thread.interrupt();
		thread.join();
		logger.info("工作线程已终止");
		return true;

	}


	@Override
	public String[] exec(Message message) throws Exception {

		if (message.getSection() < 2) {
			return new String[] {
				"参数不足"
			};
		}
		String command = message.getSegment(1);
		switch (command) {
		case "get":
			return new String[] {
				getAddress()
			};

		case "set":
			if (message.getSection() == 2) {
				return new String[] {
					this.setAddress()
				};
			} else {
				return new String[] {
					this.setAddress(message.getSegment(2))
				};
			}
		default:
			return new String[] {
				"此模块无此命令 - " + message.getSegment(1)
			};
		}

	}


	@Override
	public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) throws Exception {

	}


	@Override
	public void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) throws Exception {

	}
	// ==========================================================================================================================================================
	//
	// 工具函数
	//
	// ==========================================================================================================================================================


	@Override
	public String[] generateReport(int mode, Message message, Object... parameters) {

		StringBuilder builder = new StringBuilder();
		builder.append("获取地址：");
		builder.append(COUNT_GETIP);
		builder.append("/");
		builder.append(COUNT_GETIP_FAILED);
		builder.append("\r\n设置地址：");
		builder.append(COUNT_SETIP);
		builder.append("/");
		builder.append(COUNT_SETIP_FAILED);
		builder.append("\r\n更新地址：");
		builder.append(COUNT_FRESH);
		builder.append("/");
		builder.append(COUNT_FRESH_FAILED);
		builder.append("\r\n地址变更：");
		builder.append(COUNT_CHANGE);
		builder.append("\r\n访问失败：");
		builder.append(COUNT_FAILED);
		String[] res = new String[] {
			builder.toString()
		};
		return res;

	}


	@SuppressWarnings("deprecation")
	class Worker implements Runnable {

		@Override
		public void run() {

			long time;
			Date date;
			String address;
			String respons;
			int failcount = 0;
			do {
				try {
					while (true) {
						date = new Date();
						time = 300L;
						time = time - date.getSeconds();
						time = time - ((date.getMinutes() % 10) * 60);
						if (time < 60) { time = time + 300; }
						time = time * 1000;
						if (entry.DEBUG()) { Scheduler_Dynamic.this.logger.full("工作线程休眠：" + time); }
						Thread.sleep(time);
						// =======================================================
						Scheduler_Dynamic.this.COUNT++;
						// =======================================================
						if (entry.DEBUG()) { Scheduler_Dynamic.this.logger.full("工作线程执行"); }
						respons = Scheduler_Dynamic.this.setAddress();
						// 直接更新地址
						if (respons == null) {
							// 失败的话 执行备用逻辑
							address = getAddress();
							// 获取IP地址
							if (address == null) {
								// 失败的话 增加失败计数
								failcount++;
								COUNT_FAILED++;
							} else // 成功的话
									// 利用正则判断是否是正常的ip地址
								if (Pattern.matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}", address)) {
									// 成功的话 设置地址
									respons = Scheduler_Dynamic.this.setAddress(address);
									// 是否设置成功
									if (respons == null) {
										// 失败的话 增加失败计数
										failcount++;
										COUNT_FAILED++;
									} else {
										// 成功的话 重置失败计数
										failcount = 0;
										if (respons.startsWith("good")) { COUNT_CHANGE++; }
									}
								} else {
									// 不是正常地址 增加失败计数
									failcount++;
									COUNT_FAILED++;
								}
						} else {
							// 成功的话 重置失败计数
							failcount = 0;
							// 如果发生改变API返回内容为 good 123.123.123.123
							if (respons.startsWith("good")) { COUNT_CHANGE++; }
						}
						if (failcount > 6) {
							failcount = 0;
							entry.adminInfo("[DDNS] 警告 更新失败\r\n需要手动介入\r\n已连续失败六次");
						}
						if (entry.DEBUG()) { Scheduler_Dynamic.this.logger.full("结果 " + respons); }
					}
				} catch (InterruptedException exception) {
					if (entry.isEnable()) {
						long timeserial = System.currentTimeMillis();
						entry.adminInfo("[发生异常] 时间序列号 - " + timeserial + " " + exception.getMessage());
						Scheduler_Dynamic.this.logger.exception(timeserial, exception);
					} else {
						Scheduler_Dynamic.this.logger.full("关闭");
					}
				}
			} while (entry.isEnable());

		}


	}


	public String getAddress() {

		try {
			URL url = new URL(API_GETADDRESS);
			URLConnection connection = url.openConnection();
			connection.setReadTimeout(5000);
			connection.setConnectTimeout(5000);
			connection.setRequestProperty("User-Agent", CLIENTUA);
			connection.connect();
			connection.getContent();
			byte[] buffer = new byte[32];
			InputStream rx = connection.getInputStream();
			rx.read(buffer);
			COUNT_GETIP++;
			return new String(buffer, StandardCharsets.UTF_8).trim();
		} catch (IOException exception) {
			exception.printStackTrace();
			entry.adminInfo(Scheduler_Dynamic.MODULE_PACKAGENAME + " 获取异常 " + exception.getMessage());
			COUNT_GETIP_FAILED++;
			return null;
		}

	}


	public String setAddress() {

		try {
			URL url = new URL(API_SETADDRESS + "?hostname=" + HOSTNAME);
			URLConnection connection = url.openConnection();
			connection.setReadTimeout(5000);
			connection.setConnectTimeout(5000);
			connection.setRequestProperty("User-Agent", CLIENTUA);
			connection.setRequestProperty("Authorization", PASSWORD);
			connection.connect();
			connection.getContent();
			byte[] buffer = new byte[32];
			InputStream rx = connection.getInputStream();
			rx.read(buffer);
			COUNT_SETIP++;
			return new String(buffer, StandardCharsets.UTF_8).trim();
		} catch (IOException exception) {
			COUNT_SETIP_FAILED++;
			exception.printStackTrace();
			entry.adminInfo(Scheduler_Dynamic.MODULE_PACKAGENAME + " 获取异常" + exception.getMessage());
			return null;
		}

	}


	public String setAddress(String address) {

		try {
			URL url = new URL(API_SETADDRESS + "?hostname=" + HOSTNAME + "&myip=" + address);
			URLConnection connection = url.openConnection();
			connection.setReadTimeout(5000);
			connection.setConnectTimeout(5000);
			connection.setRequestProperty("User-Agent", CLIENTUA);
			connection.setRequestProperty("Authorization", PASSWORD);
			connection.connect();
			connection.getContent();
			byte[] buffer = new byte[32];
			InputStream rx = connection.getInputStream();
			rx.read(buffer);
			COUNT_FRESH++;
			return new String(buffer, StandardCharsets.UTF_8).trim();
		} catch (IOException exception) {
			COUNT_FRESH_FAILED++;
			exception.printStackTrace();
			entry.adminInfo(Scheduler_Dynamic.MODULE_PACKAGENAME + " 获取异常" + exception.getMessage());
			return null;
		}

	}


}
