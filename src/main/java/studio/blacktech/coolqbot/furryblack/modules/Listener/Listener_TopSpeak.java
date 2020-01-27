package studio.blacktech.coolqbot.furryblack.modules.Listener;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
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
	private static String MODULE_VERSION = "32.0";
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

	private HashMap<Long, GroupStatus> GROUP_STATUS;

	private Thread thread;

	private File CONFIG_ENABLE_REPORT;
	private File GROUP_STATUS_STORAGE;

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
					if (!groupStatus.USER_STATUS.containsKey(member.getQQId())) {
						groupStatus.USER_STATUS.put(member.getQQId(), new UserStatus(member.getQQId()));
						logger.seek("新建成员", group.getId() + " > " + entry.getNickname(member.getQQId()) + "(" + member.getQQId() + ")");
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

		if (message.getSection() < 1) {
			return new String[] {
					"参数不足"
			};
		}

		String command = message.getSegment()[1];

		switch (command) {

			case "save":
				File DAILY_BACKUP = Paths.get(FOLDER_DATA.getAbsolutePath(), LoggerX.formatTime("yyyy_MM_dd_HH_mm_ss") + ".bak").toFile();
				saveData(DAILY_BACKUP);
				return new String[] {
						"保存存档 - " + DAILY_BACKUP.getName()
				};

			case "dump":

				if (!message.hasSwitch("gropid")) {
					return new String[] {
							"参数错误 --gropid 为空"
					};
				}

				boolean C = false;
				boolean P = false;
				boolean R = false;

				if (message.hasSwitch("filter")) {

					String raw = message.getSwitch("filter");

					if (raw == null || raw.length() != 3) {
						return new String[] {
								"BinMask位置 命令 纯码 原始消息"
						};
					}

					C = raw.charAt(0) == '1' ? true : false;
					P = raw.charAt(0) == '1' ? true : false;
					R = raw.charAt(0) == '1' ? true : false;

				}


				long gropid = Long.parseLong(message.getSwitch("gropid"));

				File dumpFile = Paths.get(FOLDER_LOGS.getAbsolutePath(), LoggerX.formatTime("yyyy_MM_dd_HH_mm_ss") + "_dump.txt").toFile();
				dumpFile.createNewFile();

				FileWriter fileWriter = new FileWriter(dumpFile);

				fileWriter.write("## RANK\n\n\n\n");

				String[] result = generateMemberRank(gropid, -1, -1, -1);

				for (String part : result) {
					fileWriter.append(part);
					fileWriter.append("\n");
				}

				fileWriter.write("\n\n\n## DUMP\n\n\n\n");


				GroupStatus groupStatus = GROUP_STATUS.get(gropid).sum();

				fileWriter.write("创建时间：" + LoggerX.formatTime("yyyy-MM-dd HH:mm:ss", new Date(groupStatus.initdt)) + "(" + groupStatus.initdt + ")\n\n");
				fileWriter.write("总消息数：" + groupStatus.GROP_MESSAGES + "\n");
				fileWriter.write("发言条数：" + (groupStatus.GROP_SENTENCE.size() + groupStatus.GROP_PURECCODE) + "\n");
				fileWriter.write("发言字数：" + groupStatus.GROP_CHARACTER + "\n");
				fileWriter.write("命令次数：" + groupStatus.GROP_COMMANDS.size() + "\n");
				fileWriter.write("发言图数：" + groupStatus.GROP_PICTURES.size() + "\n");
				fileWriter.write("闪照图数：" + groupStatus.GROP_SNAPSHOT + "\n");
				fileWriter.write("视频个数：" + groupStatus.GROP_TAPVIDEO + "\n");
				fileWriter.write("红包个数：" + groupStatus.GROP_HONGBAOS + "\n\n");

				for (Entry<Long, UserStatus> userStatusEntry : groupStatus.USER_STATUS.entrySet()) {

					Long userid = userStatusEntry.getKey();

					UserStatus userStatus = userStatusEntry.getValue();

					fileWriter.write("# =============================================================================\n");
					fileWriter.write("# 用户：" + entry.getGropnick(gropid, userid) + "(" + userid + ")\n");
					fileWriter.write("# 发言条数：" + (userStatus.USER_SENTENCE.size() + userStatus.USER_PURECCODE) + "\n");
					fileWriter.write("# 发言字数：" + userStatus.USER_CHARACTER + "\n");
					fileWriter.write("# 命令次数：" + userStatus.USER_COMMANDS.size() + "\n");
					fileWriter.write("# 发言图数：" + userStatus.USER_PICTURES.size() + "\n");
					fileWriter.write("# 闪照图数：" + userStatus.USER_SNAPSHOT + "\n");
					fileWriter.write("# 视频个数：" + userStatus.USER_TAPVIDEO + "\n");
					fileWriter.write("# 红包个数：" + userStatus.USER_HONGBAOS + "\n\n");
					fileWriter.write("# =============================================================================\n");

					for (Message element : userStatus.MESSAGES) {

						if (element.isHongbao()) continue;
						if (element.isQQVideo()) continue;
						if (element.isSnappic()) continue;
						if (P && element.isPureCQC()) {
							fileWriter.write(element.getRawMessage());
							continue;
						}
						if (C && element.isCommand()) {
							fileWriter.write(element.getRawMessage());
							continue;
						}
						if (R && element.hasPicture()) {
							fileWriter.write(element.getRawMessage() + "\n");
						} else {
							fileWriter.write(element.getResMessage() + "\n");
						}
					}

					fileWriter.write("\n\n");
				}

				fileWriter.flush();
				fileWriter.close();

				return new String[] {
						"已将结果保存至文件 " + dumpFile.getName()
				};

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


		switch (mode) {

			case 10:
				String[] result = generateMemberRank(gropid, limitRank, limitRepeat, limitPicture);
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
	 */
	private String[] generateMemberRank(long gropid, int limitRank, int limitRepeat, int limitPicture) {

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

		String valueRank = limitRank > 0 ? String.valueOf(limitRank) : limitRank == 0 ? "无限" : "关";
		String valueRepeat = limitRepeat > 0 ? String.valueOf(limitRepeat) : limitRepeat == 0 ? "无限" : "关";
		String valuePicture = limitPicture > 0 ? String.valueOf(limitPicture) : limitPicture == 0 ? "无限" : "关";

		builder.append("（1/4）水群统计 " + valueRank + "/" + valueRepeat + "/" + valuePicture + "\r\n");
		builder.append("自" + LoggerX.formatTime("yyyy-MM-dd HH", new Date(groupStatus.initdt)) + ":00 以来" + "\r\n");
		builder.append("总消息数：" + groupStatus.GROP_MESSAGES + "\r\n");
		builder.append("发言条数：" + (groupStatus.GROP_SENTENCE.size() + groupStatus.GROP_PURECCODE) + "\r\n");
		builder.append("发言字数：" + groupStatus.GROP_CHARACTER + "\r\n");
		builder.append("命令次数：" + groupStatus.GROP_COMMANDS.size() + "\r\n");
		builder.append("发言图数：" + groupStatus.GROP_PICTURES.size() + "\r\n");
		builder.append("闪照图数：" + groupStatus.GROP_SNAPSHOT + "\r\n");
		builder.append("视频个数：" + groupStatus.GROP_TAPVIDEO + "\r\n");
		builder.append("红包个数：" + groupStatus.GROP_HONGBAOS);

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

				long userid = groupStatusEntry.getKey();
				UserStatus userStatus = groupStatusEntry.getValue();

				int userCharacter = userStatus.USER_SENTENCE.size() + userStatus.USER_PURECCODE;

				if (userCharacter < 1) continue;

				if (!allMemberRank.containsKey(userCharacter)) allMemberRank.put(userCharacter, new HashSet<>());

				allMemberRank.get(userCharacter).add(userid);
			}


			// step 2 输出报告


			if (allMemberRank.size() > 0) {

				builder = new StringBuilder();
				builder.append("（2/4）成员排行：" + "\r\n");

				int order = 1;
				int limit = 0;
				int slice = 0;

				loop: for (Entry<Integer, HashSet<Long>> allMemberRankEntry : allMemberRank.entrySet()) {

					HashSet<Long> membersWithSameRank = allMemberRankEntry.getValue();

					for (long userid : membersWithSameRank) {

						UserStatus userStatus = groupStatus.USER_STATUS.get(userid);
						builder.append("No." + order + " - " + entry.getGropnick(gropid, userid) + "(" + userid + ") " + (userStatus.USER_SENTENCE.size() + userStatus.USER_PURECCODE) + "句/" + userStatus.USER_CHARACTER + "字");
						if (userStatus.USER_PICTURES.size() > 0) { builder.append("/" + userStatus.USER_PICTURES.size() + "图"); }
						if (userStatus.USER_SNAPSHOT > 0) { builder.append("/" + userStatus.USER_SNAPSHOT + "闪"); }
						if (userStatus.USER_TAPVIDEO > 0) { builder.append("/" + userStatus.USER_TAPVIDEO + "片"); }
						if (userStatus.USER_HONGBAOS > 0) { builder.append("/" + userStatus.USER_HONGBAOS + "包"); }
						builder.append("\r\n");

						if (limitRank != -1 && ++limit > limitRank) break loop; // limit = -1 表示无限

						if (++slice == 10) {
							report.add(builder.substring(0, builder.length() - 2));
							builder = new StringBuilder();
							slice = 0;
						}

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

				if (messageContent.length() == 0) {
					continue;
				} else if (messageContent.matches("\\s+")) {
					continue;
				} else if (messageContent.equals("¿")) {
					messageContent = "？";
				} else if (messageContent.equals("?")) {
					messageContent = "？";
				} else if (messageContent.equals("??")) {
					messageContent = "？？";
				} else if (messageContent.equals("???")) {
					messageContent = "？？？";
				} else if (messageContent.equals("????")) {
					messageContent = "？？？？";
				} else if (messageContent.equals("wky")) {
					messageContent = "我可以";
				} else if (messageContent.equals("whl")) {
					messageContent = "我好了";
				} else if (messageContent.equals("hso")) {
					messageContent = "好骚哦";
				} else if (messageContent.equals("tql")) {
					messageContent = "太强了";
				} else if (messageContent.equals("tfl")) {
					messageContent = "太富了";
				} else if (messageContent.equals("tcl")) {
					messageContent = "太草了";
				} else if (messageContent.equals("ghs")) {
					messageContent = "搞黄色";
				} else if (messageContent.equals("草")) {
					messageContent = "草";
				} else if (messageContent.equals("操")) {
					messageContent = "草";
				} else if (messageContent.equals("艹")) {
					messageContent = "草";
				} else if (messageContent.equals("好色哦")) {
					messageContent = "好骚哦";
				} else {
					// SAM IS RAGE
					// SAM IS RAGE
				}

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


			// step 3 输出报告

			if (allMessageRank.size() > 0) {

				builder = new StringBuilder();
				builder.append("（3/4）整句排行：" + "\r\n");

				int order = 1;
				int limit = 0;
				int slice = 0;

				loop: for (Entry<Integer, HashSet<String>> allMessageRankEntry : allMessageRank.entrySet()) {

					int messageRepeatCount = allMessageRankEntry.getKey();

					HashSet<String> messageRepeat = allMessageRankEntry.getValue();

					for (String sentence : messageRepeat) {
						builder.append("No." + order + " - " + messageRepeatCount + "次：" + sentence + "\r\n");

						if (limitRepeat != -1 && ++limit > limitRepeat) break loop; // limit = -1 表示无限

						if (++slice == 10) {
							report.add(builder.substring(0, builder.length() - 2));
							builder = new StringBuilder();
							slice = 0;
						}
					}
					order = order + messageRepeat.size();
				}
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


			// step 3 输出报告

			if (allPictureRank.size() > 0) {

				int order = 1;
				int limit = 0;

				loop: for (Entry<Integer, HashSet<String>> allPictureRankEntry : allPictureRank.entrySet()) {

					int pictureRepeatCount = allPictureRankEntry.getKey();
					HashSet<String> pictures = allPictureRankEntry.getValue();

					for (String pictureCode : pictures) {
						String imageURL = CQCode.getInstance().getCQImage(pictureCode).getUrl();
						report.add("No." + order + " - " + pictureRepeatCount + "次：" + imageURL);

						if (limitRepeat != -1 && ++limit > limitPicture) break loop; // limit = -1 表示无限
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

			logger.info("保存数据");

			GROUP_STATUS.forEach((key, value) -> value.clean()); // 清理掉所有不需要保存的数据

			logger.info("整理数据");

			saver.writeObject(GROUP_STATUS);
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

						if (entry.DEBUG()) {
							Listener_TopSpeak.this.logger.full("工作线程休眠：" + time);
						}

						Thread.sleep(time);

						if (entry.DEBUG()) {
							Listener_TopSpeak.this.logger.full("工作线程执行");
						}

						File DAILY_BACKUP = Paths.get(Listener_TopSpeak.this.FOLDER_DATA.getAbsolutePath(), LoggerX.formatTime("yyyy_MM_dd_HH_mm_ss") + ".bak").toFile();

						saveData(DAILY_BACKUP);

						for (long temp : GROUP_REPORT) {
							entry.gropInfo(temp, generateMemberRank(temp, 10, 10, 3));
						}

						Listener_TopSpeak.this.logger.seek("定时备份", DAILY_BACKUP.getAbsolutePath());

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

	public int GROP_SNAPSHOT = 0;
	public int GROP_HONGBAOS = 0;
	public int GROP_TAPVIDEO = 0;
	public int GROP_MESSAGES = 0;
	public int GROP_CHARACTER = 0;
	public int GROP_PURECCODE = 0;


	public GroupStatus(long gropid) {


		initdt = System.currentTimeMillis();
		this.gropid = gropid;

		entry.getCQ().logDebug("创新的组存档", initdt + " " + gropid);

		for (Member member : entry.getCQ().getGroupMemberList(gropid)) {
			USER_STATUS.put(member.getQQId(), new UserStatus(member.getQQId()));
		}

	}

	public void clean() {
		entry.getCQ().logDebug("FurryBlackDebug", "grop " + gropid);
		USER_STATUS.forEach((key, value) -> value.clean());
		GROP_SENTENCE = null;
		GROP_COMMANDS = null;
		GROP_PICTURES = null;
	}

	public void say(long userid, MessageGrop message) {
		USER_STATUS.get(userid).say(message);
	}

	public GroupStatus sum() {
		GROP_SENTENCE = new LinkedList<>();
		GROP_COMMANDS = new LinkedList<>();
		GROP_PICTURES = new LinkedList<>();

		GROP_SNAPSHOT = 0;
		GROP_HONGBAOS = 0;
		GROP_TAPVIDEO = 0;
		GROP_MESSAGES = 0;
		GROP_CHARACTER = 0;
		GROP_PURECCODE = 0;

		for (long userid : USER_STATUS.keySet()) {

			UserStatus userStauts = USER_STATUS.get(userid).sum();
			GROP_SENTENCE.addAll(userStauts.USER_SENTENCE);
			GROP_COMMANDS.addAll(userStauts.USER_COMMANDS);
			GROP_PICTURES.addAll(userStauts.USER_PICTURES);

			GROP_SNAPSHOT = GROP_SNAPSHOT + userStauts.USER_SNAPSHOT;
			GROP_HONGBAOS = GROP_HONGBAOS + userStauts.USER_HONGBAOS;
			GROP_TAPVIDEO = GROP_TAPVIDEO + userStauts.USER_TAPVIDEO;

			GROP_MESSAGES = GROP_MESSAGES + userStauts.MESSAGES.size();

			GROP_CHARACTER = GROP_CHARACTER + userStauts.USER_CHARACTER;
			GROP_PURECCODE = GROP_PURECCODE + userStauts.USER_PURECCODE;

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

	public int USER_SNAPSHOT = 0;
	public int USER_HONGBAOS = 0;
	public int USER_TAPVIDEO = 0;
	public int USER_CHARACTER = 0;
	public int USER_PURECCODE = 0;


	public UserStatus(long userid) {
		this.userid = userid;
	}

	public void clean() {
		entry.getCQ().logDebug("FurryBlackDexebug", "   user " + userid);
		USER_COMMANDS = null;
		USER_SENTENCE = null;
		USER_PICTURES = null;
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
		USER_SNAPSHOT = 0;
		USER_TAPVIDEO = 0;
		USER_HONGBAOS = 0;

		for (MessageGrop temp : MESSAGES) {
			if (temp.isCommand()) {
				USER_COMMANDS.add(temp.getCommand());
			} else if (temp.isSnappic()) {
				USER_SNAPSHOT++;
			} else if (temp.isQQVideo()) {
				USER_TAPVIDEO++;
			} else if (temp.isHongbao()) {
				USER_HONGBAOS++;
			} else if (temp.hasPicture()) {
				for (String image : temp.getPicture()) {
					USER_PICTURES.add(image);
				}
			} else if (temp.isPureCQC()) {

				USER_PURECCODE++;
				USER_CHARACTER++;

			} else {

				if (temp.getResLength() == 0) {
					continue;
				}
				USER_SENTENCE.add(temp.getResMessage());
				USER_CHARACTER = USER_CHARACTER + temp.getResLength();
			}
		}
		return this;
	}

}
