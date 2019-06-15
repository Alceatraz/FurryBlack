package studio.blacktech.coolqbot.furryblack.modules;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import com.sobte.cqp.jcq.entity.Group;
import com.sobte.cqp.jcq.entity.Member;
import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.Message;
import studio.blacktech.coolqbot.furryblack.common.Module;
import studio.blacktech.coolqbot.furryblack.common.ModuleListener;
import studio.blacktech.coolqbot.furryblack.common.NickNameMap;

@SuppressWarnings("unused")
public class Listener_TopSpeak extends ModuleListener {

	private int GLOBAL_PICTURE = 0;
	private int GLOBAL_SENTENCE = 0;
	private int GLOBAL_CHARACTER = 0;

	public LinkedList<Message> RAW_MESSAGE = new LinkedList<>();
	private HashMap<Long, GroupStatus> GROUP_STATUS = new HashMap<>();

	public Listener_TopSpeak() {
		this.MODULE_PACKAGENAME = "shui";
		this.MODULE_DISPLAYNAME = "ˮȺͳ��";
		this.MODULE_DESCRIPTION = "ˮȺͳ��";
		this.MODULE_VERSION = "14.2.2";
		this.MODULE_USAGE = new String[] {};
		this.MODULE_PRIVACY_TRIGER = new String[] {};
		this.MODULE_PRIVACY_LISTEN = new String[] {
				"��������Ⱥ����Ϣ"
		};
		this.MODULE_PRIVACY_STORED = new String[] {
				"�������������������ļ���������"
		};
		this.MODULE_PRIVACY_CACHED = new String[] {
				"��ȡ��Ϣ����Ⱥ",
				"��ȡ��Ϣ������",
				"��ȡ��Ϣ��������"
		};
		this.MODULE_PRIVACY_OBTAIN = new String[] {};
		List<Group> groups = JcqApp.CQ.getGroupList();
		for (Group group : groups) {
			GroupStatus groupStatus = new GroupStatus();
			for (Member member : JcqApp.CQ.getGroupMemberList(group.getId())) {
				groupStatus.USER_STATUS.put(member.getQqId(), new UserStatus());
			}
			this.GROUP_STATUS.put(group.getId(), groupStatus);
		}
	}

	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	@Override
	public boolean doUserMessage(int typeid, long userid, Message message, int messageid, int messagefont) throws Exception {
		return false;
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, Message message, int messageid, int messagefont) throws Exception {
		return false;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, Message message, int messageid, int messagefont) throws Exception {
		if (message.isCommand) { return false; }
		if (message.isPicture) {
			this.GLOBAL_PICTURE++;
			this.GROUP_STATUS.get(gropid).pic(userid, message);
		} else {
			this.GLOBAL_SENTENCE++;
			this.GLOBAL_CHARACTER = this.GLOBAL_CHARACTER + message.length;
			this.GROUP_STATUS.get(gropid).say(userid, message);
		}
		return true;
	}

	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	private class GroupStatus {

		public int GROUP_PICTURE = 0;
		public int GROUP_SENTENCE = 0;
		public int GROUP_CHARACTER = 0;

		public LinkedList<Long> USER_MESSAGES = new LinkedList<>();
		public LinkedList<Long> USER_PURETEXT = new LinkedList<>();
		public LinkedList<Long> USER_PICTURES = new LinkedList<>();
		public LinkedList<Message> MESG_MESSAGES = new LinkedList<>();
		public LinkedList<Message> MESG_PURETEXT = new LinkedList<>();
		public LinkedList<Message> MESG_PICTURES = new LinkedList<>();

		public HashMap<Long, UserStatus> USER_STATUS = new HashMap<>();

		public void pic(long userid, Message message) {
			this.GROUP_PICTURE++;
			this.USER_STATUS.get(userid).pic(userid, message);
			this.USER_MESSAGES.add(userid);
			this.USER_PICTURES.add(userid);
			this.MESG_MESSAGES.add(message);
			this.MESG_PICTURES.add(message);
		}

		public void say(long userid, Message message) {
			this.GROUP_SENTENCE++;
			this.GROUP_CHARACTER += message.length;
			this.USER_STATUS.get(userid).say(userid, message);
			this.USER_MESSAGES.add(userid);
			this.USER_PURETEXT.add(userid);
			this.MESG_PURETEXT.add(message);
			this.MESG_PURETEXT.add(message);
		}
	}

	// ==========================================================================================================================

	private class UserStatus {

		public int USER_PICTURE = 0;
		public int USER_SENTENCE = 0;
		public int USER_CHARACTER = 0;

		public LinkedList<Message> USER_MESSAGES = new LinkedList<>();
		public LinkedList<Message> USER_PCITURES = new LinkedList<>();

		public void pic(long userid, Message message) {
			this.USER_PICTURE++;
			this.USER_PCITURES.add(message);
		}

		public void say(long userid, Message message) {
			this.USER_SENTENCE++;
			this.USER_CHARACTER += message.length;
			this.USER_MESSAGES.add(message);
		}

	}

	// ==========================================================================================================================

	public void memberExit(long gropid, long userid) {
		this.GROUP_STATUS.get(gropid).USER_STATUS.remove(userid);
	}

	public void memberJoin(long gropid, long userid) {
		this.GROUP_STATUS.get(gropid).USER_STATUS.put(userid, new UserStatus());
	}

	// ==========================================================================================================================

	/***
	 * logLevel ѡ��ģʽ logMode ���͵�Ⱥ�Ļ���˽�ģ�����Ա��
	 */
	@Override
	public String generateReport(int logLevel, int logMode, int typeid, long userid, long diszid, long gropid, Message message, Object[] parameters) {

		StringBuilder builder = new StringBuilder();

		try {
			switch (logLevel) {
			case 0:
				// 0 = ȫ�ְ���Ⱥ�ܷ���������
				this.generateGroupsRank();
				break;
			case 1:
				// 1 = Ⱥ�ڰ��ճ�Ա����������
				this.generateMembersRank(logMode, gropid);
				break;
			case 100:
				// 100 = ����dump
				this.generateDumpFile();
				break;
			}
		} catch (Exception exce) {
			exce.printStackTrace();
			builder.append("�������ɳ���");
			for (StackTraceElement stack : exce.getStackTrace()) {
				builder.append("\r\n");
				builder.append(stack.getClassName());
				builder.append(".");
				builder.append(stack.getMethodName());
				builder.append("(");
				builder.append(stack.getClass().getSimpleName());
				builder.append(".");
				builder.append(stack.getMethodName());
				builder.append(":");
				builder.append(stack.getLineNumber());
				builder.append(")");
			}
			return builder.toString();
		}
		return null;
	}

	// ==========================================================================================================================

	private void generateGroupsRank() {
		StringBuilder builder = new StringBuilder();
		TreeMap<Integer, Long> allGroupRank = new TreeMap<>((a, b) -> b - a);
		for (long gropid : this.GROUP_STATUS.keySet()) {
			int tempSentence = this.GROUP_STATUS.get(gropid).GROUP_SENTENCE;
			if (tempSentence > 0) { allGroupRank.put(this.GROUP_STATUS.get(gropid).GROUP_SENTENCE, gropid); }
		}
		builder.append("����������");
		builder.append(this.GLOBAL_SENTENCE);
		builder.append("\r\n����������");
		builder.append(this.GLOBAL_CHARACTER);
		builder.append("\r\n����ͼ����");
		builder.append(this.GLOBAL_PICTURE);
		builder.append("\r\n�����У�");
		int i = 1;
		for (int count : allGroupRank.keySet()) {
			String groupID = String.valueOf(allGroupRank.get(count));
			builder.append("\r\n");
			builder.append("No.");
			builder.append(i);
			builder.append("��");
			builder.append(groupID.substring(0, 4) + "**" + groupID.substring(6));
			builder.append(" - ");
			builder.append(count);
			builder.append("��/");
			builder.append(this.GROUP_STATUS.get(allGroupRank.get(count)).GROUP_CHARACTER);
			builder.append("��/");
			builder.append(this.GROUP_STATUS.get(allGroupRank.get(count)).GROUP_PICTURE);
			builder.append("ͼ");
			i++;
		}
		Module.userInfo(entry.OPERATOR(), builder.toString());
	}

	// ==========================================================================================================================

	private void generateMembersRank(int logMode, long gropid) {

		StringBuilder builder1 = new StringBuilder();
		StringBuilder builder2 = new StringBuilder();
		StringBuilder builder3 = new StringBuilder();
		StringBuilder builder4 = new StringBuilder();

		GroupStatus groupStatus = this.GROUP_STATUS.get(gropid);

		int REDPACK = 0;
		int SNAPPIC = 0;

		// ================================================================================

		builder1.append("��1/1��ˮȺͳ��\r\n����������");
		builder1.append(groupStatus.GROUP_SENTENCE);
		builder1.append("\r\n����������");
		builder1.append(groupStatus.GROUP_CHARACTER);
		builder1.append("\r\n����ͼ����");
		builder1.append(groupStatus.GROUP_PICTURE);

		// ================================================================================

		builder2.append("��2/4����Ա���У�");
		TreeMap<Integer, HashSet<Long>> allMemberRank = new TreeMap<>((a, b) -> b - a);
		for (long userid : groupStatus.USER_STATUS.keySet()) {
			int userSentence = groupStatus.USER_STATUS.get(userid).USER_SENTENCE;
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
		UserStatus userStatus;
		for (int userRank : allMemberRank.keySet()) {
			HashSet<Long> tempSet = allMemberRank.get(userRank);
			for (Long userid : tempSet) {
				userStatus = groupStatus.USER_STATUS.get(userid);
				builder2.append("\r\nNo.");
				builder2.append(i);
				builder2.append(" - ");
				builder2.append(NickNameMap.getNickname(userid));
				builder2.append("(");
				builder2.append(userid);
				builder2.append(") ");
				builder2.append(userStatus.USER_SENTENCE);
				builder2.append("��/");
				builder2.append(userStatus.USER_CHARACTER);
				builder2.append("��/");
				builder2.append(userStatus.USER_PICTURE);
				builder2.append("ͼ");
			}
			i = i + tempSet.size();
		}
		// ================================================================================

		builder3.append("(3/4)���˵�Ļ���");
		HashMap<String, Integer> allMessageRankTemp = new HashMap<>();
		for (Message message : groupStatus.MESG_PURETEXT) {
			String raw = message.rawMessage();
			if (raw.startsWith("[����]")) {
				REDPACK++;
			} else if (raw.startsWith("[QQ���]")) {
				SNAPPIC++;
			} else {
				if (allMessageRankTemp.containsKey(raw)) {
					allMessageRankTemp.put(raw, allMessageRankTemp.get(raw) + 1);
				} else {
					allMessageRankTemp.put(raw, 1);
				}
			}
		}
		TreeMap<Integer, HashSet<String>> allMessageRank = new TreeMap<>((a, b) -> b - a);
		for (String raw : allMessageRankTemp.keySet()) {
			int tempCount = allMessageRankTemp.get(raw);
			if (allMessageRank.containsKey(tempCount)) {
				allMessageRank.get(tempCount).add(raw);
			} else {
				HashSet<String> tempSet = new HashSet<>();
				tempSet.add(raw);
				allMessageRank.put(tempCount, tempSet);
			}
		}
		i = 1;
		for (int messageRank : allMessageRank.keySet()) {
			HashSet<String> tempSet = allMessageRank.get(messageRank);
			for (String message : tempSet) {
				builder3.append("\r\nNo.");
				builder3.append(i);
				builder3.append(" - ");
				builder3.append(messageRank);
				builder3.append("�Σ�");
				builder3.append(message);
				if (i++ > 25) { break; }
			}
			if (i++ > 25) { break; }
		}

		// ================================================================================

		builder1.append("\r\n��������");
		builder1.append(SNAPPIC);
		builder1.append("\r\n�������");
		builder1.append(REDPACK);

		// ================================================================================

		builder4.append("��4/4����෢��ͼ��");
		HashMap<String, Integer> allPictureRankTemp = new HashMap<>();
		for (Message message : groupStatus.MESG_PICTURES) {
			String raw = message.rawMessage();
			if (allPictureRankTemp.containsKey(raw)) {
				allPictureRankTemp.put(raw, allPictureRankTemp.get(raw) + 1);
			} else {
				allPictureRankTemp.put(raw, 1);
			}
		}
		TreeMap<Integer, HashSet<String>> allPictureRank = new TreeMap<>((a, b) -> b - a);
		for (String raw : allPictureRankTemp.keySet()) {
			int tempCount = allPictureRankTemp.get(raw);
			if (allPictureRank.containsKey(tempCount)) {
				allPictureRank.get(tempCount).add(raw);
			} else {
				HashSet<String> tempSet = new HashSet<>();
				tempSet.add(raw);
				allPictureRank.put(tempCount, tempSet);
			}
		}
		i = 1;
		for (int pictureRank : allPictureRank.keySet()) {
			HashSet<String> tempSet = allPictureRank.get(pictureRank);
			for (String picture : tempSet) {
				builder4.append("\r\nNo.");
				builder4.append(i);
				builder4.append(" - ");
				builder4.append(pictureRank);
				builder4.append("�Σ�");
				builder4.append(JcqApp.CC.getCQImage(picture).getUrl());
				if (i++ > 3) { break; }
			}
			if (i++ > 3) { break; }
		}

		if (logMode == 0) {
			Module.userInfo(entry.OPERATOR(), builder1.toString());
			Module.userInfo(entry.OPERATOR(), builder2.toString());
			Module.userInfo(entry.OPERATOR(), builder3.toString());
			Module.userInfo(entry.OPERATOR(), builder4.toString());
		} else {
			Module.gropInfo(gropid, builder1.toString());
			Module.gropInfo(gropid, builder2.toString());
			Module.gropInfo(gropid, builder3.toString());
			Module.gropInfo(gropid, builder4.toString());
		}

	}

	// ==========================================================================================================================

	private void generateDumpFile() throws IOException {
		String dumpTime = LoggerX.time("yyyy.MM.dd-HH.mm.ss");
		File dumpFolder = Paths.get(entry.FOLDER_DATA().getAbsolutePath(), "TopSpeakDump_" + dumpTime).toFile();
		dumpFolder.mkdirs();
		for (long tempGropid : this.GROUP_STATUS.keySet()) {
			File groupContainerFolder = Paths.get(dumpFolder.getAbsolutePath(), "Group_" + tempGropid).toFile();
			groupContainerFolder.mkdir();
			File dumpByTimeline = Paths.get(groupContainerFolder.getAbsolutePath(), "Main.txt").toFile();
			dumpByTimeline.createNewFile();
			BufferedWriter groupTimelineWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dumpByTimeline), "UTF-8"));
			GroupStatus tempGroupStatus = this.GROUP_STATUS.get(tempGropid);
			groupTimelineWriter.write("Ⱥ�ţ�" + tempGropid);
			groupTimelineWriter.write("\r\n����������" + tempGroupStatus.GROUP_SENTENCE);
			groupTimelineWriter.write("\r\n����������" + tempGroupStatus.GROUP_CHARACTER);
			groupTimelineWriter.write("\r\n����ͼ����" + tempGroupStatus.GROUP_PICTURE);
			groupTimelineWriter.write("\r\n");
			for (int i = 0; i < tempGroupStatus.GROUP_SENTENCE; i++) {
				long tempSender = tempGroupStatus.USER_MESSAGES.get(i);
				Message tempMessage = tempGroupStatus.MESG_MESSAGES.get(i);
				groupTimelineWriter.write("\r\n[" + LoggerX.time(new Date(tempMessage.sendTime)) + "]" + JcqApp.CQ.getStrangerInfo(tempSender).getNick() + "(" + tempSender + "): " + tempMessage.rawMessage());
			}
			groupTimelineWriter.flush();
			groupTimelineWriter.close();
			for (long tempUserid : tempGroupStatus.USER_STATUS.keySet()) {
				File dumpByUseid = Paths.get(groupContainerFolder.getAbsolutePath(), "User_" + tempUserid + ".txt").toFile();
				dumpByUseid.createNewFile();
				BufferedWriter useridWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dumpByUseid), "UTF-8"));
				UserStatus tempUserStatus = tempGroupStatus.USER_STATUS.get(tempUserid);
				useridWriter.write("��Ա��" + tempUserid);
				useridWriter.write("\r\n�ǳƣ�" + JcqApp.CQ.getStrangerInfo(tempUserid).getNick());
				useridWriter.write("\r\n����������" + tempUserStatus.USER_SENTENCE);
				useridWriter.write("\r\n����������" + tempUserStatus.USER_CHARACTER);
				useridWriter.write("\r\n����ͼ����" + tempGroupStatus.USER_PICTURES);
				useridWriter.write("\r\n");
				for (Message tempMessage : tempUserStatus.USER_MESSAGES) {
					useridWriter.write("\r\n[" + LoggerX.time(new Date(tempMessage.sendTime)) + "]: " + tempMessage.rawMessage());
				}
				useridWriter.flush();
				useridWriter.close();
			}
		}
	}
}
