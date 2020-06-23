package studio.blacktech.coolqbot.furryblack.modules.listener;


import org.meowy.cqp.jcq.entity.CQImage;
import org.meowy.cqp.jcq.entity.Friend;
import org.meowy.cqp.jcq.entity.Group;
import org.meowy.cqp.jcq.entity.Member;
import org.meowy.cqp.jcq.message.CQCode;
import studio.blacktech.coolqbot.furryblack.common.annotation.ModuleListenerComponent;
import studio.blacktech.coolqbot.furryblack.common.exception.InitializationException;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleListener;
import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.modules.trigger.Trigger_UserDeny;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


@ModuleListenerComponent
public class Listener_TopSpeak extends ModuleListener {

	private static final long serialVersionUID = 1L;

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	private static final String MODULE_PACKAGENAME = "Listener_TopSpeak";
	private static final String MODULE_COMMANDNAME = "shui";
	private static final String MODULE_DISPLAYNAME = "水群分析";
	private static final String MODULE_DESCRIPTION = "水群分析";
	private static final String MODULE_VERSION = "2.2.1";
	private static final String[] MODULE_USAGE = new String[] {
			"重启辅助线程 /admin exec --module=shui reload",
			"执行命令 暂不打印 /admin exec --module=shui execute `SQL`",
			"执行命令 直接保存 /admin exec --module=shui execute `SQL` --save",
			"执行命令 直接打印 /admin exec --module=shui execute `SQL` --show",
			"执行命令 有限打印 /admin exec --module=shui execute `SQL` --show --limit=XXX",
			"打印结果 全部打印 /admin exec --module=shui show",
			"打印结果 有限打印 /admin exec --module=shui show --limit=XXX",
			"保存结果 /admin exec --module=shui save"
	};
	private static final String[] MODULE_PRIVACY_STORED = new String[] {
			"按照\"群-成员-消息\"的层级关系保存所有聊天内容"
	};
	private static final String[] MODULE_PRIVACY_CACHED = new String[] {};
	private static final String[] MODULE_PRIVACY_OBTAIN = new String[] {};

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

	private String JDBC_HOSTNAME;
	private String JDBC_USERNAME;
	private String JDBC_PASSWORD;


	private Connection connection;


	private long FLUSH_DURATION = 60;
	private long FLUSH_SENTENSE = 10;

	private boolean ENABLE_GROUP_RECORD_LIST = false;
	private boolean ENABLE_GROUP_REPORT_LIST = false;


	private BlockingQueue<MessageGrop> queue;

	private final Object lock = new Object();


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


		queue = new LinkedBlockingQueue<>();

		if (NEW_CONFIG) {
			logger.seek("配置文件不存在 - 生成默认配置");
			CONFIG.setProperty("postgresql.enable", "false");
			CONFIG.setProperty("postgresql.hostname", "jdbc:postgresql://localhost:5432/furryblack");
			CONFIG.setProperty("postgresql.username", "furryblack");
			CONFIG.setProperty("postgresql.password", "furryblack");
			CONFIG.setProperty("list.record", "false");
			CONFIG.setProperty("list.report", "false");
			CONFIG.setProperty("flush.duration", "60");
			CONFIG.setProperty("flush.sentense", "10");
			saveConfig();
		} else {
			loadConfig();
		}

		ENABLE_GROP = Boolean.parseBoolean(CONFIG.getProperty("postgresql.enable"));

		if (!ENABLE_GROP) return true;

		JDBC_HOSTNAME = CONFIG.getProperty("postgresql.hostname");
		JDBC_USERNAME = CONFIG.getProperty("postgresql.username");
		JDBC_PASSWORD = CONFIG.getProperty("postgresql.password");


		logger.seek("数据库", JDBC_HOSTNAME);
		logger.seek("帐号", JDBC_USERNAME);
		logger.seek("密码", JDBC_PASSWORD);

		FLUSH_DURATION = Long.parseLong(CONFIG.getProperty("flush.duration"));
		FLUSH_SENTENSE = Long.parseLong(CONFIG.getProperty("flush.sentense"));

		ENABLE_GROUP_RECORD_LIST = Boolean.parseBoolean(CONFIG.getProperty("list.record"));
		ENABLE_GROUP_REPORT_LIST = Boolean.parseBoolean(CONFIG.getProperty("list.report"));


		// =================================================================
		// 测试数据库

		Class.forName("org.postgresql.Driver");
		connection = DriverManager.getConnection(JDBC_HOSTNAME, JDBC_USERNAME, JDBC_PASSWORD);


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


		if (ENABLE_GROUP_RECORD_LIST) {
			String line;
			try (BufferedReader recordReader = new BufferedReader(new InputStreamReader(new FileInputStream(CONFIG_ENABLE_RECORD), StandardCharsets.UTF_8))) {
				while ((line = recordReader.readLine()) != null) {
					if (line.startsWith("#")) continue;
					if (line.contains("#")) line = line.substring(0, line.indexOf("#")).trim();
					GROUP_RECORD.add(Long.parseLong(line));
					logger.seek("记录群聊", line);
				}
			} catch (Exception exception) {
				return false;
			}
		} else {
			logger.seek("记录群聊", "已在所有群开启");
		}


		if (ENABLE_GROUP_REPORT_LIST) {
			String line;
			try (BufferedReader reportReader = new BufferedReader(new InputStreamReader(new FileInputStream(CONFIG_ENABLE_REPORT), StandardCharsets.UTF_8))) {
				while ((line = reportReader.readLine()) != null) {
					if (line.startsWith("#")) continue;
					if (line.contains("#")) line = line.substring(0, line.indexOf("#")).trim();
					GROUP_REPORT.add(Long.parseLong(line));
					logger.seek("每日汇报", line);
				}
			} catch (Exception exception) {
				return false;
			}
		} else {
			logger.seek("每日汇报", "已在所有群开启");
		}


		new Thread(() -> {
			try {
				updateSchemaInfo();
				logger.info("启动更新群与成员", "已完成");
			} catch (SQLException exception) {
				logger.exception("更新群与成员异常", exception);
			}
		}).start();


		ENABLE_USER = false;
		ENABLE_DISZ = false;
		ENABLE_GROP = true;


		return true;
	}


	@Override
	public boolean boot() {
		logger.info("启动工作线程");
		thread = new Thread(new Worker(queue, lock, connection));
		thread.start();
		return true;
	}


	@Override
	public boolean save() {
		return true;
	}


	@Override
	public boolean shut() throws Exception {
		logger.info("关闭工作线程");
		thread.interrupt();
		thread.join();
		logger.info("关闭数据库连接");
		connection.close();
		return true;
	}


	@Override
	public String[] exec(Message message) throws Exception {

		if (message.getParameterSection() < 1) return MODULE_USAGE;

		switch (message.getParameterSegment(1)) {


		case "reload":
			thread.interrupt();
			thread.join();
			connection.close();
			connection = DriverManager.getConnection(JDBC_HOSTNAME, JDBC_USERNAME, JDBC_PASSWORD);
			thread = new Thread(new Worker(queue, lock, connection));
			thread.start();
			return new String[] {
					"工作线程与数据库连接重启完成"
			};

		case "update":
			updateSchemaInfo();
			return new String[] {
					"用户和群成员已更新"
			};

		case "execute":
			if (message.getParameterSection() < 3) return new String[] {
					"execute: 需要语句"
			};

			boolean explain = message.hasSwitch("explain");
			boolean analyze = message.hasSwitch("analyze");

			String prefix = null;
			if (explain) {
				prefix = "EXPLAIN ";
			}
			if (analyze) {
				prefix = "EXPLAIN ANALYZE ";
			}
			String command = (prefix == null ? "" : prefix) + message.getParameterSegment(2);


			try {

				boolean show = message.hasSwitch("show");

				Statement statement = show ?
						connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY) :
						connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

				boolean isResultSet = statement.execute(command);

				int limit = message.hasSwitch("limit") ? Integer.parseInt(message.getSwitch("limit")) : Integer.MAX_VALUE;


				if (isResultSet) {
					ResultSet resultSet = statement.getResultSet();
					ResultSetMetaData metaData = resultSet.getMetaData();
					int columnSize = metaData.getColumnCount();
					StringBuilder builder = new StringBuilder();

					if (show) {
						builder.append("命令执行完成 结果集为\r\n");
						for (int i = 1; i <= columnSize; i++) builder.append(metaData.getColumnName(i)).append("|");
						builder.setLength(builder.length() - 1);
						builder.append("\r\n");
						int count = 0;
						while (resultSet.next() && count++ < limit) {
							builder.append("|").append(count).append("|");
							for (int i = 1; i <= columnSize; i++) builder.append(resultSet.getString(i)).append("|");
							builder.append("\r\n");
						}
						resultSet.close();
						builder.setLength(builder.length() - 2);
						return new String[] {
								builder.toString()
						};
					} else {
						resultSet.last();
						builder.append("命令执行完成 结果共").append(resultSet.getRow()).append("行");
						resultSet.close();
						return new String[] {
								builder.toString()
						};
					}
				} else {
					return new String[] {
							"命令执行完成 无结果集 " + statement.getUpdateCount()
					};
				}


			} catch (Exception exception) {
				long serial = logger.exception(exception);
				return new String[] {
						"命令执行失败 异常日志索引" + serial
				};
			}
		}

		return new String[] {
				"无可用消息"
		};
	}

	@Override
	public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) {
		if (entry.isMyself(userid)) {
			new Thread(() -> {
				try {
					insertGroupInfo(gropid);
				} catch (SQLException exception) {
					logger.exception("更新群与成员异常", exception);
				}
			}).start();
		} else {
			new Thread(() -> {
				try {
					insertMemberInfo(gropid, userid);
				} catch (SQLException exception) {
					logger.exception("更新群与成员异常", exception);
				}
			}).start();

		}
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
	public boolean doUserMessage(MessageUser message) {
		return false;
	}


	@Override
	public boolean doDiszMessage(MessageDisz message) {
		return false;
	}


	@Override
	public boolean doGropMessage(MessageGrop message) throws Exception {

		if (ENABLE_GROUP_RECORD_LIST && !GROUP_RECORD.contains(message.getGropID())) return true;

		queue.put(message);

		if (queue.size() > FLUSH_SENTENSE) synchronized (lock) {
			lock.notifyAll();
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


	public void insertGroupInfo(long gropid) throws SQLException {

		try (
				PreparedStatement gropInfoStatement = connection.prepareStatement("INSERT INTO grop_info VALUES (?,?)");
				PreparedStatement userCardStatement = connection.prepareStatement("INSERT INTO user_card VALUES (?,?,?)")
		) {

			Group group = entry.getCQ().getGroupInfo(gropid);

			gropInfoStatement.setLong(1, gropid);
			gropInfoStatement.setString(2, group.getName());

			gropInfoStatement.execute();
			gropInfoStatement.close();


			Trigger_UserDeny userDenyInstance = (Trigger_UserDeny) entry.getTrigger("userdeny");


			for (Member member : entry.getCQ().getGroupMemberList(gropid)) {

				long userid = member.getQQId();

				if (entry.isMyself(userid)) continue;
				if (userDenyInstance.isUserIgnore(userid) > 0) continue;
				if (userDenyInstance.isGropUserIgnore(gropid, userid) > 0) continue;

				String card = entry.getNickname(gropid, userid);

				userCardStatement.setLong(1, gropid);
				userCardStatement.setLong(2, userid);
				userCardStatement.setString(3, card);
				userCardStatement.execute();
			}
		}
	}


	public void insertMemberInfo(Long gropid, long userid) throws SQLException {
		try (PreparedStatement userCardStatement = connection.prepareStatement("INSERT INTO user_card VALUES (?,?,?)")) {
			userCardStatement.setLong(1, gropid);
			userCardStatement.setLong(2, userid);
			userCardStatement.setString(3, entry.getNickname(gropid, userid));
			userCardStatement.execute();
		}
	}


	public void updateSchemaInfo() throws SQLException {

		try (Statement statement = connection.createStatement()) {
			statement.execute("TRUNCATE TABLE grop_info");
			statement.execute("TRUNCATE TABLE user_nick");
			statement.execute("TRUNCATE TABLE user_card");
		}

		try (PreparedStatement userNickStatement = connection.prepareStatement("INSERT INTO user_nick VALUES (?,?)")) {
			for (Friend friend : entry.getCQ().getFriendList()) {
				if (entry.isMyself(friend.getQQId())) continue;
				userNickStatement.setLong(1, friend.getQQId());
				userNickStatement.setString(2, friend.getNick());
				userNickStatement.execute();
			}
		}

		logger.info("写入群组列表");

		try (
				PreparedStatement gropInfoStatement = connection.prepareStatement("INSERT INTO grop_info VALUES (?,?)");
				PreparedStatement userCardStatement = connection.prepareStatement("INSERT INTO user_card VALUES (?,?,?)")
		) {
			for (Group group : entry.getCQ().getGroupList()) {

				long gropid = group.getId();
				String name = group.getName();

				gropInfoStatement.setLong(1, gropid);
				gropInfoStatement.setString(2, name);
				gropInfoStatement.execute();

				Trigger_UserDeny userDenyInstance = (Trigger_UserDeny) entry.getTrigger("userdeny");

				for (Member member : entry.getCQ().getGroupMemberList(gropid)) {

					long userid = member.getQQId();

					if (entry.isMyself(userid)) continue;
					if (userDenyInstance.isUserIgnore(userid) > 0) continue;
					if (userDenyInstance.isGropUserIgnore(gropid, userid) > 0) continue;

					String card = entry.getNickname(gropid, userid);

					userCardStatement.setLong(1, gropid);
					userCardStatement.setLong(2, userid);
					userCardStatement.setString(3, card);
					userCardStatement.execute();
				}
			}
		}
	}


	class Worker implements Runnable {

		private final Object lock;

		private final BlockingQueue<MessageGrop> queue;

		private final Connection connection;

		private PreparedStatement chat_record_Statement;
		private PreparedStatement record_at_Statement;
		private PreparedStatement record_rps_Statement;
		private PreparedStatement record_dice_Statement;
		private PreparedStatement record_image_Statement;
		private PreparedStatement record_face_Statement;
		private PreparedStatement record_sface_Statement;
		private PreparedStatement record_bface_Statement;
		private PreparedStatement record_emoji_Statement;

		private int count = 0;

		public Worker(BlockingQueue<MessageGrop> queue, Object queueLock, Connection connection) {
			this.queue = queue;
			lock = queueLock;
			this.connection = connection;
		}


		@Override
		public void run() {


			do {

				logger.full("写入线程启动 第" + count++ + "次");

				try {

					chat_record_Statement = connection.prepareStatement("INSERT INTO chat_record VALUES (?,?,?,?,?,?,?,?)");
					record_at_Statement = connection.prepareStatement("INSERT INTO record_at VALUES (?,?)");
					record_rps_Statement = connection.prepareStatement("INSERT INTO record_rps VALUES (?,?)");
					record_dice_Statement = connection.prepareStatement("INSERT INTO record_dice VALUES (?,?)");
					record_face_Statement = connection.prepareStatement("INSERT INTO record_face VALUES (?,?)");
					record_emoji_Statement = connection.prepareStatement("INSERT INTO record_emoji VALUES (?,?)");
					record_sface_Statement = connection.prepareStatement("INSERT INTO record_sface VALUES (?,?)");
					record_bface_Statement = connection.prepareStatement("INSERT INTO record_bface VALUES (?,?)");
					record_image_Statement = connection.prepareStatement("INSERT INTO record_image VALUES (?,?,?)");

					while (true) {

						synchronized (lock) {
							lock.wait(FLUSH_DURATION * 1000);
						}

						flush();

					}

				} catch (Exception exception) {
					if (entry.isEnable()) {
						entry.adminInfo("写入线程发生异常 " + exception.toString());
						logger.exception(exception);
						exception.printStackTrace();
					} else {
						logger.full("写入线程关闭");
						try {
							flush();
						} catch (Exception ignored) {

						}
					}
				}

			} while (entry.isEnable());
			logger.full("工作线程结束");
		}


		private void flush() throws InterruptedException, SQLException, IOException {

			while (queue.size() > 0) {

				MessageGrop message = queue.take();

				long message_id = message.getMessageID();

				chat_record_Statement.setLong(1, message_id);
				chat_record_Statement.setLong(2, message.getMessageFont());
				chat_record_Statement.setTimestamp(3, new Timestamp(message.getSendtime()));
				chat_record_Statement.setLong(4, message.getGropID());
				chat_record_Statement.setLong(5, message.getUserID());
				chat_record_Statement.setInt(6, message.getType().getID());
				chat_record_Statement.setString(7, message.getMessage());
				chat_record_Statement.setString(8, message.getContent());

				chat_record_Statement.execute();

				if (message.hasAt()) {
					for (String temp : message.getAt()) {
						record_at_Statement.setLong(1, message_id);
						record_at_Statement.setLong(2, Long.parseLong(temp));
						record_at_Statement.execute();
					}
				}

				if (message.hasRps()) {
					for (String temp : message.getRps()) {
						record_rps_Statement.setLong(1, message_id);
						record_rps_Statement.setLong(2, Long.parseLong(temp));
						record_rps_Statement.execute();
					}
				}

				if (message.hasDice()) {
					for (String temp : message.getDice()) {
						record_dice_Statement.setLong(1, message_id);
						record_dice_Statement.setLong(2, Long.parseLong(temp));
						record_dice_Statement.execute();
					}
				}

				if (message.hasEmoji()) {
					for (String temp : message.getEmoji()) {
						record_emoji_Statement.setLong(1, message_id);
						record_emoji_Statement.setLong(2, Long.parseLong(temp));
						record_emoji_Statement.execute();
					}
				}

				if (message.hasFace()) {
					for (String temp : message.getFace()) {
						record_face_Statement.setLong(1, message_id);
						record_face_Statement.setLong(2, Long.parseLong(temp));
						record_face_Statement.execute();
					}
				}

				if (message.hasSface()) {
					for (String temp : message.getSface()) {
						record_sface_Statement.setLong(1, message_id);
						record_sface_Statement.setString(2, temp);
						record_sface_Statement.execute();
					}
				}

				if (message.hasBface()) {
					for (String temp : message.getBface()) {
						record_bface_Statement.setLong(1, message_id);
						record_bface_Statement.setString(2, temp);
						record_bface_Statement.execute();
					}
				}

				if (message.hasImage()) {

					for (String temp : message.getImage()) {

						CQImage image = CQCode.getInstance().getCQImage(temp);

						// Jcq BUG -> CQCode.getInstance().getCQImage() 使用的是已废弃的CQImage构造方法 getName()会返回null
						// String filename = image.getName();

						String filename = temp.substring(15, 51);
						// entry.getCQ().getImage(file);

						File file = Paths.get(entry.getPictureStorePath(), filename).toFile();

						record_image_Statement.setLong(1, message_id);
						record_image_Statement.setString(2, temp);
						record_image_Statement.setString(3, image.getUrl());
						record_image_Statement.execute();

						try {
							if (!file.exists()) image.download(file);
						} catch (Exception exception) {
							entry.getCQ().logError("CoolQ BUG", "图片下载失败 " + image.getUrl());
							// CoolQ BUG 不管什么SDK都会发生这个错误
							// Received fatal alert - bad_record_mac
						}
					}
				}
			}
		}
	}
}



