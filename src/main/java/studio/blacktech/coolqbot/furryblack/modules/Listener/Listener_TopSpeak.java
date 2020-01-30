package studio.blacktech.coolqbot.furryblack.modules.Listener;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.meowy.cqp.jcq.entity.CQImage;
import org.meowy.cqp.jcq.entity.Group;
import org.meowy.cqp.jcq.entity.Member;
import org.meowy.cqp.jcq.message.CQCode;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.LoggerX.LoggerX;
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
	private static String MODULE_DISPLAYNAME = "水群统计";
	private static String MODULE_DESCRIPTION = "水群统计";
	private static String MODULE_VERSION = "33.1";
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

	private ArrayList<Long> GROUP_REPORT;

//	private HashMap<Long, GroupStatus> GROUP_STATUS;
	public HashMap<Long, GroupStatus> GROUP_STATUS;

	private Thread thread;

	private File CONFIG_ENABLE_REPORT;
	private File GROUP_STATUS_STORAGE;

	private boolean downloding = false;
	private Thread downloadTask;

	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================

	public Listener_TopSpeak() throws Exception {
		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean init() throws Exception {

		initAppFolder();
		initConfFolder();
		initLogsFolder();
		initDataFolder();

		GROUP_REPORT = new ArrayList<>();

		GROUP_STATUS_STORAGE = Paths.get(FOLDER_DATA.getAbsolutePath(), "shui").toFile();
		CONFIG_ENABLE_REPORT = Paths.get(FOLDER_CONF.getAbsolutePath(), "daily_report.txt").toFile();

		if (!CONFIG_ENABLE_REPORT.exists() && !CONFIG_ENABLE_REPORT.createNewFile()) { throw new InitializationException("无法创建文件" + CONFIG_ENABLE_REPORT.getName()); }


		// =================================================================
		// 读取每日自动汇报配置文件

		String line;
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(CONFIG_ENABLE_REPORT), StandardCharsets.UTF_8));

		while ((line = reader.readLine()) != null) {
			if (line.startsWith("#")) { continue; }
			if (line.contains("#")) { line = line.substring(0, line.indexOf("#")).trim(); }
			GROUP_REPORT.add(Long.parseLong(line));
			logger.seek("启用每日汇报", line);
		}

		reader.close();


		// =================================================================
		// 读取存档文件

		HashSet<Long> groupIdStorage = new HashSet<>();

		if (GROUP_STATUS_STORAGE.exists()) {

			logger.seek("读取存档");
			ObjectInputStream loader = new ObjectInputStream(new FileInputStream(GROUP_STATUS_STORAGE));
			GROUP_STATUS = (HashMap<Long, GroupStatus>) loader.readObject();
			loader.close();
			logger.seek("读取完成", GROUP_STATUS.size() == 0 ? "空" : "包含" + GROUP_STATUS.size() + "个群");

			for (long gropid : GROUP_STATUS.keySet()) {
				groupIdStorage.add(gropid); // 这个用于移除已经不存在的群的存档
				long time = GROUP_STATUS.get(gropid).initdt;
				logger.seek("群 " + gropid, LoggerX.datetime(new Date(time)) + "(" + time + ")");
			}

			File GROUP_STATUS_LEGACY = Paths.get(FOLDER_DATA.getAbsolutePath(), LoggerX.formatTime("yyyy_MM_dd_HH_mm_ss") + ".old").toFile();
			GROUP_STATUS_STORAGE.renameTo(GROUP_STATUS_LEGACY);
			GROUP_STATUS_STORAGE.delete();

		} else {
			logger.seek("存档不存在");
			GROUP_STATUS = new HashMap<>();
		}


		// =================================================================
		// 检查内存存档状态

		List<Group> groups = entry.getCQ().getGroupList();

		HashSet<Long> groupIdCoverge = new HashSet<>();

		for (Group group : groups) {

			long gropid = group.getId();

			groupIdCoverge.add(gropid); // 这个用于移除已经不存在的群的存档

			if (GROUP_STATUS.containsKey(gropid)) {
				GroupStatus groupStatus = GROUP_STATUS.get(gropid);
				List<Member> members = entry.getCQ().getGroupMemberList(gropid);
				for (Member member : members) {
					long userid = member.getQQId();
					if (entry.isMyself(userid)) GROUP_STATUS.get(gropid).USER_STATUS.remove(userid); // 移除自身存档
					if (!groupStatus.USER_STATUS.containsKey(userid)) {
						groupStatus.USER_STATUS.put(userid, new UserStatus(userid));
						logger.seek("新建成员", gropid + " > " + entry.getNickname(userid) + "(" + userid + ")");
					}
				}
			} else {
				GROUP_STATUS.put(group.getId(), new GroupStatus(group.getId()));
				logger.seek("新建群", group.getName() + "(" + group.getId() + ")");
			}
		}


		// 这个用于移除已经不存在的群的存档 这么写很傻逼
		// 但是在迭代的时候如果同时对其进行修改就会抛出java.util.ConcurrentModificationException异常
		// 所以不能直接GROUP_STATUS.keySet()
		for (Long tempGroupid : groupIdStorage) {
			if (!groupIdCoverge.contains(tempGroupid)) {
				logger.seek("移除多余存档 " + tempGroupid);
				GROUP_STATUS.remove(tempGroupid);
			}
		}

		GROUP_STATUS.forEach((key, value) -> value.parse());

		ENABLE_USER = false;
		ENABLE_DISZ = false;
		ENABLE_GROP = true;

		return true;

	}

	@Override
	public boolean boot() throws Exception {

		logger.info("启动工作线程");
		thread = new Thread(new Worker());
		thread.start();
		return true;

	}

	@Override
	public boolean save() throws Exception {

		logger.info("数据开始保存");
		saveData(GROUP_STATUS_STORAGE);
		logger.info("数据保存完成");
		return true;

	}

	@Override
	public boolean shut() throws Exception {

		logger.info("终止工作线程");
		thread.interrupt();
		thread.join();
		logger.info("工作线程已终止");
		return true;

	}

	@Override
	public String[] exec(Message message) throws Exception {


		long a = System.nanoTime();


		if (message.getSection() < 1) {
			return new String[] {
					"参数不足"
			};
		}

		String command = message.getSegment()[1];


		switch (command) {

			// ================================================================================================
			// 下载所有消息的图片

			case "download":

				if (downloding) return new String[] {
						"后台任务已开始"
				};

				downloding = true;

				downloadTask = new Thread(() -> {

					int total = 0;
					int download = 0;
					int failed = 0;

					for (Entry<Long, GroupStatus> statusEntry : GROUP_STATUS.entrySet()) {
						int[] temp = statusEntry.getValue().download();
						total = total + temp[0];
						download = download + temp[1];
						failed = failed + temp[2];
					}

					entry.adminInfo("下载任务已完成\r\n耗时：" + (System.nanoTime() - a) / 1000000 + "ms\r\n共计：" + total + "\r\n下载：" + download + "\r\n失败：" + failed);

				});

				downloadTask.start();

				return new String[] {
						"后台任务已开始"
				};


			case "stop":

				downloadTask.interrupt();

				return new String[] {
						"后台任务已中断"
				};

			// ================================================================================================
			// 重设所有消息的解析结果 并重新解析

			case "parse":

				GROUP_STATUS.forEach((key, value) -> value.parse());

				return new String[] {
						"耗时 " + (System.nanoTime() - a) / 1000000 + "ms"
				};


			// ================================================================================================

			case "list":

				StringBuilder builder = new StringBuilder();

				if (message.getSection() == 2) {

					builder.append("包含 " + GROUP_STATUS.size() + "个群\r\n");

					for (Entry<Long, GroupStatus> groupStatusStorageEntry : GROUP_STATUS.entrySet()) {
						long gropid = groupStatusStorageEntry.getKey();
						GroupStatus groupStatus = groupStatusStorageEntry.getValue();
						long initTime = groupStatus.initdt;
						builder.append("群：" + gropid + "[" + LoggerX.datetime(initTime) + "] " + groupStatus.USER_STATUS.size() + "个\r\n");
					}

					builder.setLength(builder.length() - 2);

				} else if (message.getSection() == 3) {

					long gropid = Long.parseLong(message.getSegment(2));

					GroupStatus groupStatus = GROUP_STATUS.get(gropid);

					for (Entry<Long, UserStatus> userStatusEntry : groupStatus.USER_STATUS.entrySet()) {
						long userid = userStatusEntry.getKey();
						UserStatus userStatus = userStatusEntry.getValue();
						builder.append(entry.getGropnick(gropid, userid) + "(" + userid + ") " + userStatus.MESSAGES.size() + "句\r\n");
					}
				} else {
					builder.append("命令错误");
				}

				return new String[] {
						builder.toString()
				};


			// ================================================================================================

			case "delete":

				if (message.getSection() == 3) {
					long gropid = Long.parseLong(message.getSegment(2));
					GROUP_STATUS.remove(gropid);
					return new String[] {
							"群号：" + gropid + "已删除"
					};
				} else if (message.getSection() == 4) {
					long gropid = Long.parseLong(message.getSegment(2));
					long userid = Long.parseLong(message.getSegment(3));
					GROUP_STATUS.get(gropid).USER_STATUS.remove(userid);
					return new String[] {
							"群号：" + gropid + " 用户：" + userid + "已删除"
					};
				}

				return new String[] {
						"错误：需要群号或者群+用户"
				};


			// ================================================================================================

			case "save":
				File DAILY_BACKUP = Paths.get(FOLDER_DATA.getAbsolutePath(), LoggerX.formatTime("yyyy_MM_dd_HH_mm_ss") + ".bak").toFile();
				saveData(DAILY_BACKUP);
				return new String[] {
						"耗时 " + (System.nanoTime() - a) / 1000000 + "ms 存存档 " + DAILY_BACKUP.getName()
				};


			// ================================================================================================

			case "dump":
				if (!message.hasSwitch("gropid")) {
					return new String[] {
							"参数错误 --gropid 为空"
					};
				}

				if (message.hasSwitch("parse")) GROUP_STATUS.forEach((key, value) -> value.parse());

				long gropid = Long.parseLong(message.getSwitch("gropid"));

				File dumpFolder = Paths.get(FOLDER_LOGS.getAbsolutePath(), LoggerX.formatTime("yyyy_MM_dd_HH_mm_ss") + "-dump").toFile();

				dumpFolder.mkdirs();

				// =====================================================================================
				// 写入trim的全局排行榜

				File dumpFile = Paths.get(dumpFolder.getAbsolutePath(), "global_rank.txt").toFile();
				dumpFile.createNewFile();

				FileWriter fileWriter = new FileWriter(dumpFile);

				String[] result = generateMemberRank(gropid, -1, -1, -1, message.hasSwitch("no-trim") ? false : true); // 是否修剪掉只出现一次的内容

				for (String part : result) {
					fileWriter.append(part);
					fileWriter.append("\n");
				}

				fileWriter.flush();
				fileWriter.close();


				TreeMap<Long, String> allMessageByTime = new TreeMap<>();


				// =====================================================================================
				// 按照用户写入发言记录


				GroupStatus groupStatus = GROUP_STATUS.get(gropid);

				String record = "存档创建时间：" + LoggerX.formatTime("yyyy-MM-dd HH:mm:ss", new Date(groupStatus.initdt)) + "(" + groupStatus.initdt + ")\n\n\n\n";

				for (Entry<Long, UserStatus> userStatusEntry : groupStatus.USER_STATUS.entrySet()) {

					Long userid = userStatusEntry.getKey();
					UserStatus userStatus = userStatusEntry.getValue();

					dumpFile = Paths.get(dumpFolder.getAbsolutePath(), "user_" + userid + "_" + entry.getGropnick(gropid, userid) + ".txt").toFile();
					dumpFile.createNewFile();

					fileWriter = new FileWriter(dumpFile);

					fileWriter.write(record);
					fileWriter.write("用户：" + entry.getGropnick(gropid, userid) + "(" + userid + ")\n");
					fileWriter.write("发言条数：" + (userStatus.USER_SENTENCE.size() + userStatus.USER_PURECCODE) + "\n");
					fileWriter.write("发言字数：" + userStatus.USER_CHARACTER + "\n");
					fileWriter.write("命令次数：" + userStatus.USER_COMMANDS.size() + "\n");
					fileWriter.write("发言图数：" + userStatus.USER_PICTURES.size() + "\n");
					fileWriter.write("涂鸦个数：" + userStatus.USER_SCRAWLS + "\n");
					fileWriter.write("礼物个数：" + userStatus.USER_PRESENT + "\n");
					fileWriter.write("红包个数：" + userStatus.USER_ENVELOPE + "\n");
					fileWriter.write("视频个数：" + userStatus.USER_TAPVIDEO + "\n");
					fileWriter.write("闪照图数：" + userStatus.USER_SNAPSHOT + "\n");
					fileWriter.write("听歌次数：" + userStatus.USER_SYNCMUSIC + "\n");

					for (MessageGrop element : userStatus.MESSAGES) {
						String sendTime = "[" + LoggerX.datetime(element.getSendtime()) + "]";
						fileWriter.write(sendTime + element.getRawMessage() + "\n");
						String userInfo = "{" + entry.getGropnick(gropid, userid) + "(" + userid + ")}";
						allMessageByTime.put(element.getSendtime(), sendTime + userInfo + element.getRawMessage() + "\n");
					}

					fileWriter.flush();
					fileWriter.close();
				}

				dumpFile = Paths.get(dumpFolder.getAbsolutePath(), "timeline.txt").toFile();
				dumpFile.createNewFile();

				fileWriter = new FileWriter(dumpFile);

				fileWriter.write(record);
				fileWriter.write("总消息数：" + groupStatus.GROP_MESSAGES + "\n");
				fileWriter.write("发言条数：" + (groupStatus.GROP_SENTENCE.size() + groupStatus.GROP_PURECCODE) + "\n");
				fileWriter.write("发言字数：" + groupStatus.GROP_CHARACTER + "\n");
				fileWriter.write("命令次数：" + groupStatus.GROP_COMMANDS.size() + "\n");
				fileWriter.write("发言图数：" + groupStatus.GROP_PICTURES.size() + "\n");
				fileWriter.write("涂鸦个数：" + groupStatus.GROP_SCRAWLS + "\n");
				fileWriter.write("礼物个数：" + groupStatus.GROP_PRESENT + "\n");
				fileWriter.write("红包个数：" + groupStatus.GROP_ENVELOPE + "\n");
				fileWriter.write("视频个数：" + groupStatus.GROP_TAPVIDEO + "\n");
				fileWriter.write("闪照图数：" + groupStatus.GROP_SNAPSHOT + "\n");
				fileWriter.write("听歌次数：" + groupStatus.GROP_SYNCMUSIC + "\n");

				for (Entry<Long, String> allMessageByTimeEntry : allMessageByTime.entrySet()) fileWriter.write(allMessageByTimeEntry.getValue());

				fileWriter.flush();
				fileWriter.close();

				return new String[] {
						"耗时 " + (System.nanoTime() - a) / 1000000 + "ms 保存至" + dumpFolder.getName()
				};


			// ================================================================================================

			default:
				return new String[] {
						"参数不足 save/dump"
				};
		}

	}

	@Override
	public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) {
		if (entry.isMyself(userid)) {
			GROUP_STATUS.put(gropid, new GroupStatus(gropid));
		} else {
			GROUP_STATUS.get(gropid).USER_STATUS.put(userid, new UserStatus(userid));
		}
	}

	@Override
	public void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) {
		if (entry.isMyself(userid)) {
			GROUP_STATUS.remove(gropid);
		} else {
			GROUP_STATUS.get(gropid).USER_STATUS.remove(userid);
		}
	}


	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	@Override
	public boolean doUserMessage(int typeid, long userid, MessageUser message, int messageid, int messagefont) throws Exception {
		return false;
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, MessageDisz message, int messageid, int messagefont) throws Exception {
		return false;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, MessageGrop message, int messageid, int messagefont) throws Exception {
		GROUP_STATUS.get(gropid).say(userid, message);
		return true;
	}

	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	@Override
	public String[] generateReport(int mode, Message message, Object... parameters) {

		if (!message.hasSwitch("gropid")) {
			return new String[] {
					"参数错误 --gropid 为空"
			};
		}

		long gropid = Long.parseLong(message.getSwitch("gropid"));

		int limitRank = 10;
		int limitRepeat = 10;
		int limitPicture = 3;

		if (message.hasSwitch("limit")) {

			String[] limits = message.getSwitch("limit").split(",");
			switch (limits.length) {
				case 3:
					limitPicture = Integer.parseInt(limits[2]);
				case 2:
					limitRepeat = Integer.parseInt(limits[1]);
				case 1:
					limitRank = Integer.parseInt(limits[0]);
			}
		}

		boolean trim = message.hasSwitch("no-trim") ? false : true;

		switch (mode) {

			case 10:
				String[] result = generateMemberRank(gropid, limitRank, limitRepeat, limitPicture, trim);
				if (message.hasSwitch("dump")) {
					File dumpFile = Paths.get(FOLDER_LOGS.getAbsolutePath(), LoggerX.formatTime("yyyy_MM_dd_HH_mm_ss") + "_rank.txt").toFile();
					try {
						dumpFile.createNewFile();
						FileWriter writer = new FileWriter(dumpFile, true);
						for (String part : result) {
							writer.append(part);
							writer.append("\n");
						}
						writer.flush();
						writer.close();
					} catch (Exception exception) {
						System.out.println("shui写入文件失败");
						exception.printStackTrace();
						throw new NullPointerException("日志写入失败 " + exception.getLocalizedMessage());
					}
					return new String[] {
							"已将结果保存至文件 " + dumpFile.getName()
					};
				} else {
					return result;
				}

			case 20:

				if (message.hasSwitch("match")) {
					String match = message.getSwitch("match");
					if (match.length() == 0) {
						return new String[] {
								"参数错误 --match 为空"
						};
					}
					return generateMemberCountGrep(gropid, match, limitRank);

				} else if (message.hasSwitch("regex")) {

					String regex = message.getSwitch("regex");
					if (regex.length() == 0) {
						return new String[] {
								"参数错误 --regex 为空"
						};
					}
					return generateMemberRegexGrep(gropid, regex, limitRank);

				} else {
					return new String[] {
							"参数错误 模式为空 需要 --match 或 --regex"
					};
				}

		}

		return new String[] {
				"无可用消息"
		};

	}

	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	/**
	 * 这个函数写了好几百行 而且我不打算告诉你这个的运行逻辑
	 *
	 * @param gropid       群号
	 * @param limitRank    排名 limit
	 * @param limitRepeat  复读 limit
	 * @param limitPicture 图片 limit
	 * @param trim         是否裁剪 无重复的发言和图片
	 * @return 报告
	 */
	private String[] generateMemberRank(long gropid, int limitRank, int limitRepeat, int limitPicture, boolean trim) {

		StringBuilder builder;
		LinkedList<String> report = new LinkedList<>();

		GroupStatus groupStatus = GROUP_STATUS.get(gropid).sum();

		// ===========================================================

		builder = new StringBuilder();

		// ===========================================================
		//
		// 第一部分 基础统计数据
		//
		// ===========================================================

		String valueRank = limitRank > 0 ? String.valueOf(limitRank) : limitRank == 0 ? "关" : "无限";
		String valueRepeat = limitRepeat > 0 ? String.valueOf(limitRepeat) : limitRepeat == 0 ? "关" : "无限";
		String valuePicture = limitPicture > 0 ? String.valueOf(limitPicture) : limitPicture == 0 ? "关" : "无限";

		builder.append("（1/4）水群统计 " + valueRank + "/" + valueRepeat + "/" + valuePicture + "\r\n");
		builder.append("自" + LoggerX.formatTime("yyyy-MM-dd HH", new Date(groupStatus.initdt)) + ":00 以来" + "\r\n");
		builder.append("总消息数：" + groupStatus.GROP_MESSAGES + "\r\n");
		builder.append("发言条数：" + (groupStatus.GROP_SENTENCE.size() + groupStatus.GROP_PURECCODE) + "\r\n");
		builder.append("发言字数：" + groupStatus.GROP_CHARACTER + "\r\n");
		builder.append("命令次数：" + groupStatus.GROP_COMMANDS.size() + "\r\n");
		builder.append("发言图数：" + groupStatus.GROP_PICTURES.size() + "\r\n");
		builder.append("涂鸦个数：" + groupStatus.GROP_SCRAWLS + "\r\n");
		builder.append("礼物个数：" + groupStatus.GROP_PRESENT + "\r\n");
		builder.append("红包个数：" + groupStatus.GROP_ENVELOPE + "\r\n");
		builder.append("视频个数：" + groupStatus.GROP_TAPVIDEO + "\r\n");
		builder.append("闪照图数：" + groupStatus.GROP_SNAPSHOT + "\r\n");
		builder.append("听歌次数：" + groupStatus.GROP_SYNCMUSIC);

		report.add(builder.toString());


		// ===========================================================
		//
		// 第二部分 成员按照发言条数排序
		//
		// ===========================================================


		if (limitRank != 0) { // limit = 0 表示关闭


			// step 1 历遍所有消息 统计重复次数

			TreeMap<Integer, HashSet<Long>> allMemberRank = new TreeMap<>((a, b) -> b - a);

			for (Entry<Long, UserStatus> groupStatusEntry : groupStatus.USER_STATUS.entrySet()) {

				UserStatus userStatus = groupStatusEntry.getValue();

				if (userStatus.MESSAGES.size() == 0) continue; // 过滤掉没说话的

				long userid = groupStatusEntry.getKey();

				int userCharacter = userStatus.USER_SENTENCE.size() + userStatus.USER_PURECCODE;

				if (!allMemberRank.containsKey(userCharacter)) allMemberRank.put(userCharacter, new HashSet<>()); // 没有则新建

				allMemberRank.get(userCharacter).add(userid);
			}


			// step 2 输出报告


			if (allMemberRank.size() > 0) {

				builder = new StringBuilder();
				builder.append("（2/4）成员排行：" + "\r\n");

				int order = 1;
				int limit = 1;
				int slice = 0;

				loop: for (Entry<Integer, HashSet<Long>> allMemberRankEntry : allMemberRank.entrySet()) {

					HashSet<Long> membersWithSameRank = allMemberRankEntry.getValue();

					for (long userid : membersWithSameRank) {

						if (limitRank != -1 && limit++ > limitRank) break loop; // limit = -1 表示无限

						if (slice++ == 10) {
							slice = 0;
							report.add(builder.substring(0, builder.length() - 2));
							builder = new StringBuilder();
						}

						UserStatus userStatus = groupStatus.USER_STATUS.get(userid);

						builder.append("No." + order + " - " + entry.getGropnick(gropid, userid) + "(" + userid + ") " + (userStatus.USER_SENTENCE.size() + userStatus.USER_PURECCODE) + "句/" + userStatus.USER_CHARACTER + "字");

						if (userStatus.USER_PICTURES.size() > 0) builder.append("/" + userStatus.USER_PICTURES.size() + "图");
						if (userStatus.USER_SNAPSHOT > 0) builder.append("/" + userStatus.USER_SNAPSHOT + "闪");
						if (userStatus.USER_TAPVIDEO > 0) builder.append("/" + userStatus.USER_TAPVIDEO + "片");
						if (userStatus.USER_ENVELOPE > 0) builder.append("/" + userStatus.USER_ENVELOPE + "包");
						if (userStatus.USER_SCRAWLS > 0) builder.append("/" + userStatus.USER_SCRAWLS + "画");
						if (userStatus.USER_PRESENT > 0) builder.append("/" + userStatus.USER_PRESENT + "礼");
						if (userStatus.USER_SYNCMUSIC > 0) builder.append("/" + userStatus.USER_SYNCMUSIC + "听");

						builder.append("\r\n");
					}
					order = order + membersWithSameRank.size();
				}
				report.add(builder.substring(0, builder.length() - 2));
			}
		}


		// ===========================================================
		//
		// 第三部分 整句重复频度排序
		//
		// ===========================================================


		if (limitRepeat != 0) { // limit = 0 表示关闭


			// step 1 历遍所有消息 统计重复次数

			HashMap<String, Integer> allMessageRankTemp = new HashMap<>();

			for (String messageContent : groupStatus.GROP_SENTENCE) {

				messageContent = messageContent.trim();

				// @formatter:off

				// 合并常见消息

				if (messageContent.length() == 0) continue;
				else if (messageContent.matches("\\s+")) continue;
				else if (messageContent.equals("¿")) messageContent = "？";
				else if (messageContent.equals("?")) messageContent = "？";
				else if (messageContent.equals("??")) messageContent = "？";
				else if (messageContent.equals("???")) messageContent = "？";
				else if (messageContent.equals("????")) messageContent = "？";
				else if (messageContent.equals("wky")) messageContent = "我可以";
				else if (messageContent.equals("whl")) messageContent = "我好了";
				else if (messageContent.equals("hso")) messageContent = "好骚哦";
				else if (messageContent.equals("tql")) messageContent = "太强了";
				else if (messageContent.equals("tfl")) messageContent = "太富了";
				else if (messageContent.equals("tcl")) messageContent = "太草了";
				else if (messageContent.equals("ghs")) messageContent = "搞黄色";
				else if (messageContent.equals("gkd")) messageContent = "搞快点";
				else if (messageContent.equals("草")) messageContent = "草";
				else if (messageContent.equals("操")) messageContent = "草";
				else if (messageContent.equals("艹")) messageContent = "草";
				else if (messageContent.equals("好色哦")) messageContent = "好骚哦";

				// @formatter:on

				if (allMessageRankTemp.containsKey(messageContent)) {
					allMessageRankTemp.put(messageContent, allMessageRankTemp.get(messageContent) + 1);
				} else {
					allMessageRankTemp.put(messageContent, 1);
				}
			}


			// step 2 历遍重复次数表 按照次数排序

			TreeMap<Integer, HashSet<String>> allMessageRank = new TreeMap<>((a, b) -> b - a);

			for (Entry<String, Integer> allMessageRankTempEntry : allMessageRankTemp.entrySet()) {

				String sentence = allMessageRankTempEntry.getKey();
				int sentenceRepeatCount = allMessageRankTempEntry.getValue();

				if (!allMessageRank.containsKey(sentenceRepeatCount)) allMessageRank.put(sentenceRepeatCount, new HashSet<>());

				allMessageRank.get(sentenceRepeatCount).add(sentence);
			}


			if (trim) allMessageRank.remove(1);


			// step 3 输出报告

			if (allMessageRank.size() > 0) {

				builder = new StringBuilder();
				builder.append("（3/4）整句排行：" + "\r\n");

				int order = 1;
				int limit = 1;
				int slice = 0;

				loop: for (Entry<Integer, HashSet<String>> allMessageRankEntry : allMessageRank.entrySet()) {

					int messageRepeatCount = allMessageRankEntry.getKey();

					HashSet<String> messageRepeat = allMessageRankEntry.getValue();

					for (String sentence : messageRepeat) {

						if (limitRepeat != -1 && limit++ > limitRepeat) break loop; // limit = -1 表示无限

						if (slice++ == 10) {
							slice = 0;
							report.add(builder.substring(0, builder.length() - 2));
							builder = new StringBuilder();
						}

						builder.append("No." + order + " - " + messageRepeatCount + "次：" + sentence + "\r\n");
					}
					order = order + messageRepeat.size();
				}
				report.add(builder.substring(0, builder.length() - 2));
			}
		}


		// ===========================================================
		//
		// 第四部分 图片重复频度排序
		//
		// ===========================================================


		if (limitPicture != 0) { // limit = 0 表示关闭


			// step 1 历遍所有消息 统计重复次数

			HashMap<String, Integer> allPictureRankTemp = new HashMap<>();

			for (String messageContent : groupStatus.GROP_PICTURES) {
				if (allPictureRankTemp.containsKey(messageContent)) {
					allPictureRankTemp.put(messageContent, allPictureRankTemp.get(messageContent) + 1);
				} else {
					allPictureRankTemp.put(messageContent, 1);
				}
			}


			// step 2 历遍重复次数表 按照次数排序

			TreeMap<Integer, HashSet<String>> allPictureRank = new TreeMap<>((a, b) -> b - a);

			for (String raw : allPictureRankTemp.keySet()) {

				int tempCount = allPictureRankTemp.get(raw);

				if (!allPictureRank.containsKey(tempCount)) allPictureRank.put(tempCount, new HashSet<>());

				allPictureRank.get(tempCount).add(raw);
			}

			if (trim) allPictureRank.remove(1);


			// step 3 输出报告

			if (allPictureRank.size() > 0) {

				int order = 1;
				int limit = 1;

				loop: for (Entry<Integer, HashSet<String>> allPictureRankEntry : allPictureRank.entrySet()) {

					int pictureRepeatCount = allPictureRankEntry.getKey();
					HashSet<String> pictures = allPictureRankEntry.getValue();

					for (String pictureCode : pictures) {

						if (limitRepeat != -1 && limit++ > limitPicture) break loop; // limit = -1 表示无限

						String imageURL = CQCode.getInstance().getCQImage(pictureCode).getUrl();
						report.add("No." + order + " - " + pictureRepeatCount + "次 " + imageURL);
					}
				}
			}
		}

		// ===========================================================

		return report.toArray(new String[report.size()]);

	}


	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================


	private String[] generateMemberCountGrep(long gropid, String content, int limitRank) {

		int totalMatchCount = 0;

		StringBuilder builder = new StringBuilder();
		LinkedList<String> report = new LinkedList<>();

		GroupStatus groupStatus = GROUP_STATUS.get(gropid).sum();

		builder.append("自" + LoggerX.formatTime("yyyy-MM-dd HH", new Date(groupStatus.initdt)) + ":00 以来，在\r\n");
		builder.append(groupStatus.GROP_MESSAGES + "条消息中(共" + groupStatus.GROP_CHARACTER + "字)\r\n");
		builder.append("之中 " + content);

		int position;
		int contextLength = content.length();

		// step 1 在所有消息中搜索

		TreeMap<Integer, HashSet<Long>> memberCountRank = new TreeMap<>((a, b) -> b - a);

		for (Long userid : groupStatus.USER_STATUS.keySet()) {

			UserStatus userStatus = groupStatus.USER_STATUS.get(userid);

			int userMatchCount = 0;

			for (String message : userStatus.USER_SENTENCE) {
				while ((position = message.indexOf(content)) > 0) {
					message = message.substring(position + contextLength);
					userMatchCount++;
					totalMatchCount++;
				}
			}

			if (userMatchCount == 0) continue;
			if (!memberCountRank.containsKey(userMatchCount)) memberCountRank.put(userMatchCount, new HashSet<>());
			memberCountRank.get(userMatchCount).add(userid);

		}

		// step 2 生成报告

		builder.append(" 出现了：" + totalMatchCount + "次");

		report.add(builder.toString());
		builder = new StringBuilder();

		if (memberCountRank.size() > 0) {

			int order = 1;
			int limit = 0;
			int slice = 0;


			loop: for (Entry<Integer, HashSet<Long>> memberCountRankEntry : memberCountRank.entrySet()) {

				int userMatchCount = memberCountRankEntry.getKey();
				HashSet<Long> tempSet = memberCountRankEntry.getValue();


				for (long userid : tempSet) {
					builder.append("No." + order + " - " + entry.getGropnick(gropid, userid) + "(" + userid + ") " + userMatchCount + "次\r\n");

					if (++limit > limitRank) break loop;

					if (++slice == 50) {
						report.add(builder.substring(0, builder.length() - 2));
						builder = new StringBuilder();
						slice = 0;
					}
				}
				order = order + tempSet.size();
			}
			report.add(builder.substring(0, builder.length() - 2));
		}
		return report.toArray(new String[report.size()]);
	}


	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================


	private String[] generateMemberRegexGrep(long gropid, String content, int limitRank) {

		int totalMatchCount = 0;

		StringBuilder builder = new StringBuilder();
		LinkedList<String> report = new LinkedList<>();

		GroupStatus groupStatus = GROUP_STATUS.get(gropid).sum();

		builder.append("自" + LoggerX.formatTime("yyyy-MM-dd HH", new Date(groupStatus.initdt)) + ":00 以来，在\r\n");
		builder.append(groupStatus.GROP_MESSAGES + "条消息中(共" + groupStatus.GROP_CHARACTER + "字)\r\n");
		builder.append("之中 " + content);

		// step 1 在所有消息中搜索

		TreeMap<Integer, HashSet<Long>> memberCountRank = new TreeMap<>((a, b) -> b - a);

		for (Long userid : groupStatus.USER_STATUS.keySet()) {

			UserStatus userStatus = groupStatus.USER_STATUS.get(userid);

			int userMatchCount = 0;

			Pattern pattern = Pattern.compile(content);

			for (String message : userStatus.USER_SENTENCE) {
				Matcher matcher = pattern.matcher(message);
				while (matcher.find()) {
					userMatchCount++;
					totalMatchCount++;
				}
			}

			if (userMatchCount == 0) continue;
			if (!memberCountRank.containsKey(userMatchCount)) memberCountRank.put(userMatchCount, new HashSet<>());
			memberCountRank.get(userMatchCount).add(userid);

		}

		// step 2 生成报告

		builder.append(" 出现了：" + totalMatchCount + "次");

		report.add(builder.toString());
		builder = new StringBuilder();

		if (memberCountRank.size() > 0) {

			int order = 1;
			int limit = 0;
			int slice = 0;

			loop: for (Entry<Integer, HashSet<Long>> memberCountRankEntry : memberCountRank.entrySet()) {

				int userMatchCount = memberCountRankEntry.getKey();
				HashSet<Long> tempSet = memberCountRankEntry.getValue();

				for (long userid : tempSet) {
					builder.append("No." + order + " - " + entry.getGropnick(gropid, userid) + "(" + userid + ") " + userMatchCount + "次\r\n");

					if (++limit > limitRank) break loop;

					if (++slice == 50) {
						report.add(builder.substring(0, builder.length() - 2));
						builder = new StringBuilder();
						slice = 0;
					}
				}
				order = order + tempSet.size();
			}
			report.add(builder.substring(0, builder.length() - 2));
		}
		return report.toArray(new String[report.size()]);
	}


	private void saveData(File file) {

		try {

			if (file.exists()) file.delete();

			FileOutputStream stream = new FileOutputStream(file);
			ObjectOutputStream saver = new ObjectOutputStream(stream);

			GROUP_STATUS.forEach((key, value) -> value.clean()); // 清理掉所有不需要保存的数据

			saver.writeObject(GROUP_STATUS);

			saver.flush();
			saver.close();

			stream.flush();
			stream.close();

		} catch (Exception exception) {
			logger.exception(exception);
		}
	}


	@SuppressWarnings("deprecation")
	class Worker implements Runnable {
		@Override
		public void run() {
			long time;
			Date date;
			do {
				try {
					while (true) {
						date = new Date();
						time = 86400L;
						time = time - date.getSeconds();
						time = time - date.getMinutes() * 60;
						time = time - date.getHours() * 3600;
						time = time * 1000;
						Thread.sleep(time);
						File DAILY_BACKUP = Paths.get(Listener_TopSpeak.this.FOLDER_DATA.getAbsolutePath(), LoggerX.formatTime("yyyy_MM_dd_HH_mm_ss") + ".bak").toFile();
						saveData(DAILY_BACKUP);
						Listener_TopSpeak.this.logger.seek("定时备份", DAILY_BACKUP.getAbsolutePath());
						for (long temp : GROUP_REPORT) entry.gropInfo(temp, generateMemberRank(temp, 10, 10, 3, true));
					}
				} catch (Exception exception) {
					if (entry.isEnable()) {
						long timeserial = System.currentTimeMillis();
						entry.adminInfo("[每日任务发生异常] 时间序列号 - " + timeserial + " " + exception.getMessage());
						Listener_TopSpeak.this.logger.exception(timeserial, "每日任务发生异常", exception);
					} else {
						Listener_TopSpeak.this.logger.full("关闭");
					}
				}
			} while (entry.isEnable());
		}
	}
}


class GroupStatus implements Serializable {

	private static final long serialVersionUID = 1L;

	public long gropid = 0;
	public long initdt = 0;

	public HashMap<Long, UserStatus> USER_STATUS = new HashMap<>();

	public LinkedList<String> GROP_SENTENCE;
	public LinkedList<String> GROP_COMMANDS;
	public LinkedList<String> GROP_PICTURES;

	public int GROP_MESSAGES = 0;
	public int GROP_CHARACTER = 0;
	public int GROP_PURECCODE = 0;
	public int GROP_SCRAWLS = 0;
	public int GROP_PRESENT = 0;
	public int GROP_ENVELOPE = 0;
	public int GROP_TAPVIDEO = 0;
	public int GROP_SNAPSHOT = 0;
	public int GROP_SYNCMUSIC = 0;


	public GroupStatus(long gropid) {

		initdt = System.currentTimeMillis();
		this.gropid = gropid;

		entry.getCQ().logDebug("创新的组存档", initdt + " " + gropid);

		for (Member member : entry.getCQ().getGroupMemberList(gropid)) USER_STATUS.put(member.getQQId(), new UserStatus(member.getQQId()));
	}


	public void clean() {
		USER_STATUS.forEach((key, value) -> value.clean());
		GROP_SENTENCE = null;
		GROP_COMMANDS = null;
		GROP_PICTURES = null;
	}


	public void parse() {
		USER_STATUS.forEach((key, value) -> value.parse());
	}


	public int[] download() {
		int total = 0;
		int download = 0;
		int failed = 0;
		for (Entry<Long, UserStatus> userStatusEntry : USER_STATUS.entrySet()) {
			int[] temp = userStatusEntry.getValue().download();
			total = total + temp[0];
			download = download + temp[1];
			failed = failed + temp[2];
		}
		System.out.println("群聊完成下载 " + gropid + " " + total + " - " + download + "/" + failed);
		return new int[] {
				total, download, failed
		};
	}


	public void say(long userid, MessageGrop message) {
		USER_STATUS.get(userid).say(message);
	}


	public GroupStatus sum() {

		GROP_SENTENCE = new LinkedList<>();
		GROP_COMMANDS = new LinkedList<>();
		GROP_PICTURES = new LinkedList<>();

		GROP_MESSAGES = 0;
		GROP_CHARACTER = 0;
		GROP_PURECCODE = 0;
		GROP_SCRAWLS = 0;
		GROP_PRESENT = 0;
		GROP_ENVELOPE = 0;
		GROP_TAPVIDEO = 0;
		GROP_SNAPSHOT = 0;
		GROP_SYNCMUSIC = 0;

		for (long userid : USER_STATUS.keySet()) {

			UserStatus userStauts = USER_STATUS.get(userid).sum();

			GROP_SENTENCE.addAll(userStauts.USER_SENTENCE);
			GROP_COMMANDS.addAll(userStauts.USER_COMMANDS);
			GROP_PICTURES.addAll(userStauts.USER_PICTURES);

			GROP_MESSAGES = GROP_MESSAGES + userStauts.MESSAGES.size();

			GROP_CHARACTER = GROP_CHARACTER + userStauts.USER_CHARACTER;
			GROP_PURECCODE = GROP_PURECCODE + userStauts.USER_PURECCODE;

			GROP_SCRAWLS = GROP_SCRAWLS + userStauts.USER_SCRAWLS;
			GROP_PRESENT = GROP_PRESENT + userStauts.USER_PRESENT;
			GROP_ENVELOPE = GROP_ENVELOPE + userStauts.USER_ENVELOPE;
			GROP_TAPVIDEO = GROP_TAPVIDEO + userStauts.USER_TAPVIDEO;
			GROP_SNAPSHOT = GROP_SNAPSHOT + userStauts.USER_SNAPSHOT;
			GROP_SYNCMUSIC = GROP_SYNCMUSIC + userStauts.USER_SYNCMUSIC;

		}

		return this;
	}

}


class UserStatus implements Serializable {

	private static final long serialVersionUID = 1L;

	public long userid = 0;

	public LinkedList<MessageGrop> MESSAGES = new LinkedList<>();

	public LinkedList<String> USER_COMMANDS;
	public LinkedList<String> USER_SENTENCE;
	public LinkedList<String> USER_PICTURES;

	public int USER_CHARACTER = 0;
	public int USER_PURECCODE = 0;
	public int USER_SCRAWLS = 0;
	public int USER_PRESENT = 0;
	public int USER_ENVELOPE = 0;
	public int USER_TAPVIDEO = 0;
	public int USER_SNAPSHOT = 0;
	public int USER_SYNCMUSIC = 0;


	public UserStatus(long userid) {
		this.userid = userid;
	}


	public void clean() {
		MESSAGES.forEach(MessageGrop::clean);
		USER_COMMANDS = null;
		USER_SENTENCE = null;
		USER_PICTURES = null;
	}


	public void parse() {
		MESSAGES.forEach(MessageGrop::parse);
	}


	public int[] download() {

		int total = 0;
		int download = 0;
		int failed = 0;

		for (MessageGrop message : MESSAGES) {

			if (!message.hasPicture()) continue;

			int length = message.getPictureLength();

			total = total + length;

			for (int i = 0; i < length; i++) {
				String code = message.getPicture(i);
				CQImage image = CQCode.getInstance().getCQImage(code);
				File file = Paths.get(entry.getPictureStorePath(), code.substring(16, 51)).toFile();
				if (file.exists()) continue;
				try {
					image.download(file);
					download++;
				} catch (IOException exception) {
					failed++;
				}
			}
		}

		System.out.println("  成员 " + userid + " " + total + " - " + download + "/" + failed);

		return new int[] {
				total, download, failed
		};
	}

	public void say(MessageGrop message) {
		MESSAGES.add(message);
	}

	public UserStatus sum() {

		USER_SENTENCE = new LinkedList<>();
		USER_COMMANDS = new LinkedList<>();
		USER_PICTURES = new LinkedList<>();

		USER_CHARACTER = 0;
		USER_PURECCODE = 0;
		USER_SCRAWLS = 0;
		USER_PRESENT = 0;
		USER_ENVELOPE = 0;
		USER_TAPVIDEO = 0;
		USER_SNAPSHOT = 0;
		USER_SYNCMUSIC = 0;

		for (MessageGrop temp : MESSAGES) {

			if (temp == null) continue;

			if (temp.getType() == null) temp.parse();

			switch (temp.getType()) {

				// 垃圾消息

				case Scrawls:
					USER_SCRAWLS++;
					continue;

				case Present:
					USER_PRESENT++;
					continue;

				case Envelope:
					USER_ENVELOPE++;
					continue;

				case TapVideo:
					USER_TAPVIDEO++;
					continue;

				case SnapShot:
					USER_SNAPSHOT++;
					continue;

				case SyncMusic:
					USER_SYNCMUSIC++;
					continue;

				// 正常消息

				case Command:
					USER_COMMANDS.add(temp.getCmdMessage());
					continue;

				case PureCode:
					USER_PURECCODE++;
					USER_CHARACTER++;
					continue;

				case Normal:
					// if (temp.getResLength() == 0) continue;
					if (temp.hasPicture()) for (String image : temp.getPicture()) USER_PICTURES.add(image);
					USER_SENTENCE.add(temp.getResMessage());
					USER_CHARACTER = USER_CHARACTER + temp.getResLength();
					continue;
			}
		}
		return this;
	}

}
