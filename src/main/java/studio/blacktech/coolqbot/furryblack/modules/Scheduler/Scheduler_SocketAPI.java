package studio.blacktech.coolqbot.furryblack.modules.Scheduler;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

import org.meowy.cqp.jcq.entity.Group;
import org.meowy.cqp.jcq.entity.Member;
import org.meowy.cqp.jcq.entity.QQInfo;

import studio.blacktech.common.security.Base64;
import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.annotation.ModuleSchedulerComponent;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleScheduler;


@ModuleSchedulerComponent
public class Scheduler_SocketAPI extends ModuleScheduler {

	private static final long serialVersionUID = 1L;

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "Scheduler_SocketAPI";
	private static String MODULE_COMMANDNAME = "socketapi";
	private static String MODULE_DISPLAYNAME = "网络接口";
	private static String MODULE_DESCRIPTION = "网络接口";
	private static String MODULE_VERSION = "1.0.0";
	private static String[] MODULE_USAGE = new String[] {};
	public static String[] MODULE_PRIVACY_STORED = new String[] {};
	public static String[] MODULE_PRIVACY_CACHED = new String[] {};
	public static String[] MODULE_PRIVACY_OBTAIN = new String[] {};

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================


	private int port;

	private Thread thread;

	private Worker worker;

	private HashMap<String, String> secret;


	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================

	public Scheduler_SocketAPI() throws Exception {
		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	@Override
	public boolean init() throws Exception {

		initAppFolder();
		initConfFolder();
		initLogsFolder();

		initPropertiesConfigurtion();

		if (NEW_CONFIG) {

			CONFIG.setProperty("enable", "true");
			CONFIG.setProperty("socket.port", "57781");
			saveConfig();

		} else {
			loadConfig();
		}

		ENABLE = Boolean.parseBoolean(CONFIG.getProperty("enable"));

		if (!ENABLE) return true;

		port = Integer.parseInt(CONFIG.getProperty("socket.port"));

		secret = new HashMap<>();

		secret.put("192.168.1.10", "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF");

		return true;

	}

	@Override
	public boolean boot() throws Exception {
		if (ENABLE) {
			logger.info("启动工作线程");
			worker = new Worker();
			thread = new Thread(worker);
			thread.start();
		}
		return true;
	}

	@Override
	public boolean save() throws Exception {
		return true;
	}

	@Override
	public boolean shut() throws Exception {
		if (ENABLE) {
			logger.info("终止工作线程");
			worker.stop();
			thread.interrupt();
			thread.join();
			logger.info("工作线程已终止");
		}
		return true;
	}

	@Override
	public String[] exec(Message message) throws Exception {
		return new String[] {
				"此模块无可用命令"
		};
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
	public String[] generateReport(Message message) {
		return new String[0];
	}

	class Worker implements Runnable {

		ServerSocket server = null;


		@Override
		public void run() {

			int count = 0;

			loop: do {

				logger.full("接口线程启动 第" + count++ + "次");


				try {
					server = new ServerSocket(port);
				} catch (IOException exception) {
					exception.printStackTrace();
					entry.adminInfo("端口监听失败 " + exception.toString());
					break loop;
				}

				try {

					do {

						System.out.println("API-等待连接");

						Socket socket = server.accept();

						System.out.println("API-收到连接");

						String temp;

						DataInputStream read = new DataInputStream(socket.getInputStream());
						DataOutputStream send = new DataOutputStream(socket.getOutputStream());

						temp = read.readUTF();

						System.out.println("API-读取消息");

						String command = temp.substring(0, 2);
						String content = temp.substring(2);

						System.out.println("API-命令 " + command);
						System.out.println("API-内容 " + content);

						switch (command) {

						// Heart Beat , Ping
						case "00":
							send.writeUTF("00");
							break;

						// User Info
						case "11":
							send.writeUTF(getUserInfo(Long.parseLong(content)));
							break;

						// Group Info
						case "21":
							send.writeUTF(getGroupInfo(Long.parseLong(content)));
							break;

						// Groups Info
						case "22":
							send.writeUTF(getGroupInfo(Long.parseLong(content)));
							break;

						// List Group User
						case "32":
							send.writeUTF(listGroupMembers(Long.parseLong(content)));
							break;
						}

						send.flush();

						System.out.println("API-发送完成");

						socket.close();

						System.out.println("API-连接关闭");

					} while (true);

				} catch (IOException exception) {

					if (entry.isEnable()) {
						entry.adminInfo("接口线程异常 " + exception.toString());
						logger.exception(exception);
						exception.printStackTrace();
					} else {
						logger.full("接口线程关闭");
					}

				}

				try {
					server.close();
				} catch (IOException exception) {
					exception.printStackTrace();
					entry.adminInfo("端口关闭失败 " + exception.toString());
					break loop;
				}

			} while (entry.isEnable());

			logger.full("接口线程结束");
		}

		public void stop() {
			try {
				server.close();
			} catch (IOException exception) {
				exception.printStackTrace();
				entry.adminInfo("端口关闭失败 " + exception.toString());
			}
		}

		private QQInfo getUser(long userid) {
			return entry.getCQ().getStrangerInfo(userid);
		}


		private String getUserInfo(long userid) {
			QQInfo user = getUser(userid);
			return "{\"id\":" + userid + ",\"name\":\"" + Base64.encode(user.getNick()) + "\"}";
		}


		private Group getGroup(long gropid) {
			return entry.getCQ().getGroupInfo(gropid);
		}


		private String getGroupInfo(long gropid) {
			Group group = getGroup(gropid);
			if (group == null) return "{\"id\":" + gropid + ",\"name\":\"已解散\"}";
			return "{\"id\":" + gropid + ",\"name\":\"" + group.getName().replaceAll("\"", "\\\"") + "\",\"size\":" + group.getCount() + ",\"capacity\":" + group.getCountMax() + "}";
		}


		private List<Member> listMember(long gropid) {
			return entry.getCQ().getGroupMemberList(gropid);
		}

		private String listGroupMembers(long gropid) {
			List<Member> members = listMember(gropid);
			StringBuilder builder = new StringBuilder();
			builder.append("[");
			for (Member member : members) {
				if (entry.isMyself(member.getQQId())) continue;
				builder.append("{\"id\":").append(member.getQQId()).append(",");
				if (member.getCard() == "") {
					builder.append("\"name\":\"").append(member.getCard().replaceAll("\"", "\\\"")).append("\"},");
				} else {
					builder.append("\"name\":\"").append(member.getNick().replaceAll("\"", "\\\"")).append("\"},");
				}
			}
			builder.setLength(builder.length() - 1);
			builder.append("]");
			return builder.toString();
		}

	}
}
