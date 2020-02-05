package studio.blacktech.coolqbot.furryblack.modules.Listener;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.LinkedList;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.annotation.ModuleListenerComponent;
import studio.blacktech.coolqbot.furryblack.common.exception.InitializationException;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleListener;


@ModuleListenerComponent
public class Listener_TopSpeak extends ModuleListener {

	private static final long serialVersionUID = 1L;

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "Listener_TopSpeak";
	private static String MODULE_COMMANDNAME = "shui";
	private static String MODULE_DISPLAYNAME = "水群分析";
	private static String MODULE_DESCRIPTION = "水群分析";
	private static String MODULE_VERSION = "34.0";
	private static String[] MODULE_USAGE = new String[] {};
	private static String[] MODULE_PRIVACY_STORED = new String[] {
			"按照\"群-成员-消息\"的层级关系保存所有聊天内容"
	};
	private static String[] MODULE_PRIVACY_CACHED = new String[] {};
	private static String[] MODULE_PRIVACY_OBTAIN = new String[] {};

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================

	private File CONFIG_ENABLE_RECORD;
	private File CONFIG_ENABLE_REPORT;

	private ArrayList<Long> GROUP_RECORD;
	private ArrayList<Long> GROUP_REPORT;

	private Thread thread;

	private boolean JDBC_ENABLE;
	private String JDBC_HOSTNAME;
	private String JDBC_USERNAME;
	private String JDBC_PASSWORD;

	private Object queueLock;
	private LinkedList<MessageGrop> queue;

	private Connection connection;


	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================

	public Listener_TopSpeak() throws Exception {
		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	@Override
	public boolean init() throws Exception {

		initAppFolder();
		initConfFolder();
		initLogsFolder();
		initDataFolder();
		initPropertiesConfigurtion();

		queueLock = new Object();
		queue = new LinkedList<>();

		if (NEW_CONFIG) {
			logger.seek("配置文件不存在 - 生成默认配置");
			CONFIG.setProperty("postgresql.enable", "false");
			CONFIG.setProperty("postgresql.hostname", "jdbc:postgresql://localhost:5432/furryblack");
			CONFIG.setProperty("postgresql.username", "furryblack");
			CONFIG.setProperty("postgresql.password", "furryblack");
			saveConfig();
		} else {
			loadConfig();
		}

		JDBC_ENABLE = Boolean.parseBoolean(CONFIG.getProperty("postgresql.enable"));

		if (!JDBC_ENABLE) {
			ENABLE_USER = false;
			ENABLE_DISZ = false;
			ENABLE_GROP = false;
			return true;
		}

		JDBC_HOSTNAME = CONFIG.getProperty("postgresql.hostname");
		JDBC_USERNAME = CONFIG.getProperty("postgresql.username");
		JDBC_PASSWORD = CONFIG.getProperty("postgresql.password");

		logger.seek("数据库", JDBC_HOSTNAME);
		logger.seek("帐号", JDBC_USERNAME);
		logger.seek("密码", JDBC_PASSWORD);

		// =================================================================
		// 测试数据库

		Class.forName("org.postgresql.Driver");
		connection = DriverManager.getConnection(JDBC_HOSTNAME, JDBC_USERNAME, JDBC_PASSWORD);
		if (connection == null) throw new InitializationException("数据库连接失败");

		// =================================================================
		// 初始化内存

		GROUP_RECORD = new ArrayList<>();
		GROUP_REPORT = new ArrayList<>();

		CONFIG_ENABLE_RECORD = Paths.get(FOLDER_CONF.getAbsolutePath(), "enable_record.txt").toFile();
		CONFIG_ENABLE_REPORT = Paths.get(FOLDER_CONF.getAbsolutePath(), "enable_report.txt").toFile();

		if (!CONFIG_ENABLE_RECORD.exists() && !CONFIG_ENABLE_RECORD.createNewFile()) throw new InitializationException("无法创建文件" + CONFIG_ENABLE_RECORD.getName());
		if (!CONFIG_ENABLE_REPORT.exists() && !CONFIG_ENABLE_REPORT.createNewFile()) throw new InitializationException("无法创建文件" + CONFIG_ENABLE_REPORT.getName());

		// =================================================================
		// 读取配置文件

		String line;

		BufferedReader recordReader = new BufferedReader(new InputStreamReader(new FileInputStream(CONFIG_ENABLE_RECORD), StandardCharsets.UTF_8));
		BufferedReader reportReader = new BufferedReader(new InputStreamReader(new FileInputStream(CONFIG_ENABLE_REPORT), StandardCharsets.UTF_8));

		while ((line = recordReader.readLine()) != null) {
			if (line.startsWith("#")) continue;
			if (line.contains("#")) line = line.substring(0, line.indexOf("#")).trim();
			GROUP_RECORD.add(Long.parseLong(line));
			logger.seek("记录群聊", line);
		}

		recordReader.close();


		while ((line = reportReader.readLine()) != null) {
			if (line.startsWith("#")) continue;
			if (line.contains("#")) line = line.substring(0, line.indexOf("#")).trim();
			GROUP_REPORT.add(Long.parseLong(line));
			logger.seek("每日汇报", line);
		}

		reportReader.close();


		ENABLE_USER = false;
		ENABLE_DISZ = false;
		ENABLE_GROP = true;

		return true;

	}


	@Override
	public boolean boot() throws Exception {
		logger.info("启动工作线程");
		thread = new Thread(new WriterWorker(queue, queueLock, connection));
		thread.start();
		return true;
	}


	@Override
	public boolean save() throws Exception {
		return true;
	}


	@Override
	public boolean shut() throws Exception {
		logger.info("关闭数据库连接");
		connection.close();
		logger.info("关闭工作线程");
		thread.interrupt();
		thread.join();
		return true;
	}


	@Override
	public String[] exec(Message message) throws Exception {
		return new String[] {
				"无可用消息"
		};
	}

	@Override
	public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) {

	}

	@Override
	public void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) {

	}


	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	@Override
	public boolean doUserMessage(MessageUser message) throws Exception {
		return false;
	}

	@Override
	public boolean doDiszMessage(MessageDisz message) throws Exception {
		return false;
	}

	@Override
	public boolean doGropMessage(MessageGrop message) throws Exception {
		if (GROUP_RECORD.contains(message.getGropID())) {
			queue.add(message);
			if (queue.size() > 10) queueLock.notifyAll();
		}
		return true;
	}


	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================


	@Override
	public String[] generateReport(Message message) {
		return new String[] {
				"无可用消息"
		};
	}


	class WriterWorker implements Runnable {

		private Object queueLock;
		private LinkedList<MessageGrop> queue;
		private Connection connection;
		private PreparedStatement insertStatement;

		public WriterWorker(LinkedList<MessageGrop> queue, Object queueLock, Connection connection) {
			this.queue = queue;
			this.queueLock = queueLock;
			this.connection = connection;
		}

		@Override
		public void run() {
			do {
				try {
					insertStatement = connection.prepareStatement("INSERT INTO chat_record VALUES (?,?,?,?,?,?,?)");
					while (true) {
						System.out.println("开始等待");
						queueLock.wait(5000);
						System.out.println("等待超时");
						for (MessageGrop message : queue) {
							System.out.println("写入" + message);
							insertStatement.setLong(1, message.getMessageID());
							insertStatement.setLong(2, message.getMessageFont());
							insertStatement.setLong(3, message.getSendtime());
							insertStatement.setLong(4, message.getGropID());
							insertStatement.setLong(5, message.getUserID());
							insertStatement.setBoolean(6, message.isCommand());
							insertStatement.setString(7, message.getMessage());
							insertStatement.execute();
						}
					}
				} catch (Exception exception) {
					if (entry.isEnable()) {
						long timeserial = System.currentTimeMillis();
						entry.adminInfo("[辅助线程发生异常] 时间序列号 - " + timeserial + " " + exception.getMessage());
						Listener_TopSpeak.this.logger.exception(timeserial, "辅助线程发生异常", exception);
					} else {
						Listener_TopSpeak.this.logger.full("关闭");
					}
				}
			} while (entry.isEnable());
		}
	}
}
