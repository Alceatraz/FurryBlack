package studio.blacktech.coolqbot.furryblack.modules;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import com.sobte.cqp.jcq.entity.Group;
import com.sobte.cqp.jcq.entity.Member;
import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.Module_Nickmap;
import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleListener;

@SuppressWarnings("unused")
public class Listener_TopSpeak extends ModuleListener {

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "shui";
	private static String MODULE_DISPLAYNAME = "水群统计";
	private static String MODULE_DESCRIPTION = "水群统计";
	private static String MODULE_VERSION = "18.0";
	private static String[] MODULE_USAGE = new String[] {};
	private static String[] MODULE_PRIVACY_TRIGER = new String[] {};
	private static String[] MODULE_PRIVACY_LISTEN = new String[] {
			"记录所有消息"
	};
	private static String[] MODULE_PRIVACY_STORED = new String[] {};
	private static String[] MODULE_PRIVACY_CACHED = new String[] {
			"记录所有消息"
	};
	private static String[] MODULE_PRIVACY_OBTAIN = new String[] {
			"获取消息发送群",
			"获取消息发送人",
	};

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================

	private final HashMap<Long, GroupStatus> GROUP_STATUS = new HashMap<>();

	private ShuiDelegate delegate = new ShuiDelegate();

	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================

	public Listener_TopSpeak() throws Exception {
		super(MODULE_DISPLAYNAME, MODULE_PACKAGENAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_TRIGER, MODULE_PRIVACY_LISTEN, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	@Override
	public void init(LoggerX logger) throws Exception {

		final List<Group> groups = JcqApp.CQ.getGroupList();
		for (final Group group : groups) {
			final GroupStatus groupStatus = new GroupStatus(group.getId());
			for (final Member member : JcqApp.CQ.getGroupMemberList(group.getId())) {
				groupStatus.USER_STATUS.put(member.getQqId(), new UserStatus(member.getQqId()));
			}
			this.GROUP_STATUS.put(group.getId(), groupStatus);
		}

		this.ENABLE_USER = false;
		this.ENABLE_DISZ = false;
		this.ENABLE_GROP = true;
	}

	@Override
	public void boot(LoggerX logger) throws Exception {
	}

	@Override
	public void shut(LoggerX logger) throws Exception {
	}

	@Override
	public void reload(LoggerX logger) throws Exception {
	}

	@Override
	public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) {
		this.GROUP_STATUS.get(gropid).USER_STATUS.put(userid, new UserStatus(userid));
	}

	@Override
	public void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) {
		this.GROUP_STATUS.get(gropid).USER_STATUS.remove(userid);
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
		this.GROUP_STATUS.get(gropid).say(userid, message);
		return true;
	}

	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	private class GroupStatus {

		public long gropid = 0;

		public HashMap<Long, UserStatus> USER_STATUS = new HashMap<>();

		public LinkedList<String> GROP_SENTENCE;
		public LinkedList<String> GROP_COMMANDS;
		public LinkedList<String> GROP_PICTURES;

		public int GROP_SNAPSHOP = 0;
		public int GROP_HONGBAOS = 0;
		public int GROP_TAPVIDEO = 0;

		public int GROP_MESSAGES = 0;

		public int GROP_CHARACTER = 0;

		public GroupStatus(long gropid) {
			this.gropid = gropid;
		}

		public void say(long userid, MessageGrop message) {
			this.USER_STATUS.get(userid).say(message);
		}

		public GroupStatus sum() {

			this.GROP_SENTENCE = new LinkedList<>();
			this.GROP_COMMANDS = new LinkedList<>();
			this.GROP_PICTURES = new LinkedList<>();

			this.GROP_SNAPSHOP = 0;
			this.GROP_HONGBAOS = 0;
			this.GROP_TAPVIDEO = 0;

			this.GROP_MESSAGES = 0;

			this.GROP_CHARACTER = 0;

			for (long userid : this.USER_STATUS.keySet()) {

				UserStatus userStauts = this.USER_STATUS.get(userid).sum();

				this.GROP_SENTENCE.addAll(userStauts.USER_SENTENCE);
				this.GROP_COMMANDS.addAll(userStauts.USER_COMMANDS);
				this.GROP_PICTURES.addAll(userStauts.USER_PICTURES);

				this.GROP_SNAPSHOP = this.GROP_SNAPSHOP + userStauts.USER_SNAPSHOP;
				this.GROP_HONGBAOS = this.GROP_HONGBAOS + userStauts.USER_HONGBAOS;
				this.GROP_TAPVIDEO = this.GROP_TAPVIDEO + userStauts.USER_TAPVIDEO;

				this.GROP_MESSAGES = this.GROP_MESSAGES + userStauts.MESSAGES.size();

				this.GROP_CHARACTER = this.GROP_CHARACTER + userStauts.USER_CHARACTER;

			}

			return this;
		}
	}

	// ==========================================================================================================================

	private class UserStatus {

		public long userid = 0;

		public LinkedList<MessageGrop> MESSAGES = new LinkedList<>();

		public LinkedList<String> USER_COMMANDS;
		public LinkedList<String> USER_SENTENCE;
		public LinkedList<String> USER_PICTURES;

		public int USER_SNAPSHOP = 0;
		public int USER_HONGBAOS = 0;
		public int USER_TAPVIDEO = 0;

		public int USER_CHARACTER = 0;

		public UserStatus(long userid) {
			this.userid = userid;
		}

		public void say(MessageGrop message) {
			this.MESSAGES.add(message);
		}

		public UserStatus sum() {

			this.USER_SENTENCE = new LinkedList<>();
			this.USER_COMMANDS = new LinkedList<>();
			this.USER_PICTURES = new LinkedList<>();
			this.USER_SNAPSHOP = 0;
			this.USER_HONGBAOS = 0;
			this.USER_TAPVIDEO = 0;

			for (MessageGrop temp : this.MESSAGES) {
				if (temp.isCommand()) {
					this.USER_COMMANDS.add(temp.getCommand());
					continue;
				}
				temp.anaylysMessage();

				if (temp.isSnappic()) {
					this.USER_SNAPSHOP++;
					continue;
				}
				if (temp.isHongbao()) {
					this.USER_HONGBAOS++;
					continue;
				}
				if (temp.isQQVideo()) {
					this.USER_TAPVIDEO++;
					continue;
				}

				if (temp.hasPicture()) {
					for (String image : temp.getPicture()) {
						this.USER_PICTURES.add(image);
					}
				}

				if (temp.getResLength() > 0) {
					this.USER_SENTENCE.add(temp.getResMessage());
					this.USER_CHARACTER = this.USER_CHARACTER + temp.getResLength();
				}

			}

			return this;
		}
	}

	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	@Override
	public String[] generateReport(int mode, Message message, Object... parameters) {

		switch (mode) {

		case 0:

			break;

		case 10:
			this.generateMemberRank(Long.parseLong(message.getSegment()[3]));
			break;

		case 20:
			break;
		}
		return null;
	}

	// ==========================================================================================================================

	private LinkedList<String> generateMemberRank(long gropid) {
		LinkedList<String> report = new LinkedList<>();

		GroupStatus groupStatus = this.GROUP_STATUS.get(gropid).sum();

		StringBuilder builder;

		builder = new StringBuilder();

		// ===========================================================

		builder.append("（1/4）水群统计");

		builder.append("\r\n总消息数：");
		builder.append(groupStatus.GROP_MESSAGES);
		builder.append("\r\n发言条数：");
		builder.append(groupStatus.GROP_SENTENCE);
		builder.append("\r\n发言字数：");
		builder.append(groupStatus.GROP_CHARACTER);
		builder.append("\r\n命令次数：");
		builder.append(groupStatus.GROP_COMMANDS.size());
		builder.append("\r\n发言图数：");
		builder.append(groupStatus.GROP_PICTURES.size());
		builder.append("\r\n闪照图数：");
		builder.append(groupStatus.GROP_PICTURES);
		builder.append("\r\n视频个数：");
		builder.append(groupStatus.GROP_TAPVIDEO);
		builder.append("\r\n红包个数：");
		builder.append(groupStatus.GROP_HONGBAOS);

		report.add(builder.toString());
		builder = new StringBuilder();

		// ===========================================================

		builder.append("（2/4）成员排行：");

		UserStatus userStatus;

		TreeMap<Integer, HashSet<Long>> allMemberRank = new TreeMap<>((a, b) -> b - a);

		for (long userid : groupStatus.USER_STATUS.keySet()) {
			int userSentence = groupStatus.USER_STATUS.get(userid).USER_SENTENCE.size();
			if (userSentence > 0) {
				if (allMemberRank.containsKey(userSentence)) {
					allMemberRank.get(userSentence).add(userid);
				} else {
					HashSet<Long> tempSet = new HashSet<>();
					tempSet.add(userid);
					allMemberRank.put(userSentence, tempSet);
				}
			}
		}

		int i = 1;

		for (int userRank : allMemberRank.keySet()) {
			HashSet<Long> tempSet = allMemberRank.get(userRank);
			for (Long userid : tempSet) {
				userStatus = groupStatus.USER_STATUS.get(userid);
				builder.append("\r\nNo.");
				builder.append(i);
				builder.append(" - ");
				builder.append(entry.getNickmap().getNickname(userid).substring(0, 8));
				builder.append("(");
				builder.append(userid);
				builder.append(") ");
				builder.append(userStatus.USER_SENTENCE);
				builder.append("句/");
				builder.append(userStatus.USER_CHARACTER);
				builder.append("字/");
				builder.append(userStatus.USER_PICTURES.size());
				builder.append("图/");
				builder.append(userStatus.USER_PICTURES.size());
				builder.append("闪");
				builder.append(userStatus.USER_TAPVIDEO);
				builder.append("片");
				builder.append(userStatus.USER_HONGBAOS);
				builder.append("包");
			}
			i = i + tempSet.size();
		}

		report.add(builder.toString());
		builder = new StringBuilder();

		// ===========================================================

		builder.append("(3/4)最多说的话：");

		HashMap<String, Integer> allMessageRankTemp = new HashMap<>();

		for (String message : groupStatus.GROP_SENTENCE) {

			// 替换表
			if (message.equals("?")) { message = "？"; }

			if (allMessageRankTemp.containsKey(message)) {
				allMessageRankTemp.put(message, allMessageRankTemp.get(message) + 1);
			} else {
				allMessageRankTemp.put(message, 1);
			}
		}

		TreeMap<Integer, HashSet<String>> allMessageRank = new TreeMap<>((a, b) -> b - a);

		for (final String raw : allMessageRankTemp.keySet()) {
			final int tempCount = allMessageRankTemp.get(raw);
			if (allMessageRank.containsKey(tempCount)) {
				allMessageRank.get(tempCount).add(raw);
			} else {
				final HashSet<String> tempSet = new HashSet<>();
				tempSet.add(raw);
				allMessageRank.put(tempCount, tempSet);
			}
		}

		i = 1;
		for (int messageRank : allMessageRank.keySet()) {
			HashSet<String> tempSet = allMessageRank.get(messageRank);
			for (String message : tempSet) {
				builder.append("\r\nNo.");
				builder.append(i);
				builder.append(" - ");
				builder.append(messageRank);
				builder.append("次：");
				builder.append(message);
				if (i++ > 25) { break; }
			}
		}

		report.add(builder.toString());
		builder = new StringBuilder();

		// ===========================================================

		builder.append("（4/4）最多发的图：");

		HashMap<String, Integer> allPictureRankTemp = new HashMap<>();
		for (String message : groupStatus.GROP_PICTURES) {
			if (allPictureRankTemp.containsKey(message)) {
				allPictureRankTemp.put(message, allPictureRankTemp.get(message) + 1);
			} else {
				allPictureRankTemp.put(message, 1);
			}
		}

		TreeMap<Integer, HashSet<String>> allPictureRank = new TreeMap<>((a, b) -> b - a);
		for (final String raw : allPictureRankTemp.keySet()) {
			final int tempCount = allPictureRankTemp.get(raw);
			if (allPictureRank.containsKey(tempCount)) {
				allPictureRank.get(tempCount).add(raw);
			} else {
				final HashSet<String> tempSet = new HashSet<>();
				tempSet.add(raw);
				allPictureRank.put(tempCount, tempSet);
			}
		}

		i = 1;
		for (final int pictureRank : allPictureRank.keySet()) {
			final HashSet<String> tempSet = allPictureRank.get(pictureRank);
			for (final String picture : tempSet) {
				report.add(builder.toString());
				builder = new StringBuilder();
				builder.append("\r\nNo.");
				builder.append(i);
				builder.append(" - ");
				builder.append(pictureRank);
				builder.append("次：");
				builder.append(JcqApp.CC.getCQImage(picture).getUrl());
				if (i++ > 5) { break; }
			}
		}

		// ===========================================================

		return report;
	}

	public ShuiDelegate getDelegate() {
		return this.delegate;
	}

	public class ShuiDelegate {

		public LinkedList<String> generateMemberRank(long gropid) {
			return generateMemberRank(gropid);
		}
	}

}
