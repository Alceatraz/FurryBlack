package studio.blacktech.coolqbot.furryblack.modules;

import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import com.sobte.cqp.jcq.entity.Group;
import com.sobte.cqp.jcq.entity.Member;
import com.sobte.cqp.jcq.event.JcqApp;
import com.sobte.cqp.jcq.event.JcqAppAbstract;

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
	// ģ���������
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "listener_topspeak";
	private static String MODULE_COMMANDNAME = "shui";
	private static String MODULE_DISPLAYNAME = "ˮȺͳ��";
	private static String MODULE_DESCRIPTION = "ˮȺͳ��";
	private static String MODULE_VERSION = "18.0";
	private static String[] MODULE_USAGE = new String[] {};
	private static String[] MODULE_PRIVACY_TRIGER = new String[] {};
	private static String[] MODULE_PRIVACY_LISTEN = new String[] {
			"��¼������Ϣ"
	};
	private static String[] MODULE_PRIVACY_STORED = new String[] {};
	private static String[] MODULE_PRIVACY_CACHED = new String[] {
			"��¼������Ϣ"
	};
	private static String[] MODULE_PRIVACY_OBTAIN = new String[] {
			"��ȡ��Ϣ����Ⱥ",
			"��ȡ��Ϣ������",
	};

	// ==========================================================================================================================================================
	//
	// ��Ա����
	//
	// ==========================================================================================================================================================

	private HashMap<Long, GroupStatus> GROUP_STATUS = new HashMap<>();

	private Thread worker;

	// ==========================================================================================================================================================
	//
	// �������ں���
	//
	// ==========================================================================================================================================================

	public Listener_TopSpeak() throws Exception {
		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_TRIGER, MODULE_PRIVACY_LISTEN, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	@Override
	public void init(LoggerX logger) throws Exception {

		List<Group> groups = JcqApp.CQ.getGroupList();
		for (Group group : groups) {
			GroupStatus groupStatus = new GroupStatus(group.getId());
			for (Member member : JcqApp.CQ.getGroupMemberList(group.getId())) {
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
		this.worker = new Thread(new Worker());
		this.worker.start();
	}

	@Override
	public void shut(LoggerX logger) throws Exception {
		this.worker.interrupt();
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

		public int GROP_SNAPSHOT = 0;
		public int GROP_HONGBAOS = 0;
		public int GROP_TAPVIDEO = 0;

		public int GROP_MESSAGES = 0;

		public int GROP_CHARACTER = 0;
		public int GROP_PURECCODE = 0;

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

			this.GROP_SNAPSHOT = 0;
			this.GROP_HONGBAOS = 0;
			this.GROP_TAPVIDEO = 0;

			this.GROP_MESSAGES = 0;

			this.GROP_CHARACTER = 0;
			this.GROP_PURECCODE = 0;

			for (long userid : this.USER_STATUS.keySet()) {

				UserStatus userStauts = this.USER_STATUS.get(userid).sum();

				this.GROP_SENTENCE.addAll(userStauts.USER_SENTENCE);
				this.GROP_COMMANDS.addAll(userStauts.USER_COMMANDS);
				this.GROP_PICTURES.addAll(userStauts.USER_PICTURES);

				this.GROP_SNAPSHOT = this.GROP_SNAPSHOT + userStauts.USER_SNAPSHOT;
				this.GROP_HONGBAOS = this.GROP_HONGBAOS + userStauts.USER_HONGBAOS;
				this.GROP_TAPVIDEO = this.GROP_TAPVIDEO + userStauts.USER_TAPVIDEO;

				this.GROP_MESSAGES = this.GROP_MESSAGES + userStauts.MESSAGES.size();

				this.GROP_CHARACTER = this.GROP_CHARACTER + userStauts.USER_CHARACTER;
				this.GROP_PURECCODE = this.GROP_PURECCODE + userStauts.USER_PURECCODE;

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

		public int USER_SNAPSHOT = 0;
		public int USER_HONGBAOS = 0;
		public int USER_TAPVIDEO = 0;

		public int USER_CHARACTER = 0;
		public int USER_PURECCODE = 0;

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
			this.USER_SNAPSHOT = 0;
			this.USER_HONGBAOS = 0;
			this.USER_TAPVIDEO = 0;
			this.USER_CHARACTER = 0;
			this.USER_PURECCODE = 0;

			for (MessageGrop temp : this.MESSAGES) {

				if (temp.isCommand()) {
					this.USER_COMMANDS.add(temp.getCommand());
					continue;
				}

				temp.anaylysMessage();

				if (temp.isSnappic()) {
					this.USER_SNAPSHOT++;
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

				// ��CQ Code����Ϣ�ᱻ�滻Ϊ "" �� 1��0�ּ���
				// ����ֱ�Ӱ���һ����ӽ� SENTENCE
				// �������ִ���ռλ�� ""
				// ��������洢һ������
				if (temp.isPureCQC()) {
					this.USER_PURECCODE++;
					this.USER_CHARACTER++;
				} else {
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
		case 10:
			return this.generateMemberRank(Long.parseLong(message.getFlag("gropid")));
		}
		return null;
	}

	// ==========================================================================================================================

	public String[] generateMemberRank(long gropid) {

		StringBuilder builder;
		LinkedList<String> report = new LinkedList<>();
		GroupStatus groupStatus = this.GROUP_STATUS.get(gropid).sum();

		// ===========================================================

		builder = new StringBuilder();

		// ===========================================================

		builder.append("��1/4��ˮȺͳ��");

		builder.append("\r\n����Ϣ����");
		builder.append(groupStatus.GROP_MESSAGES);
		builder.append("\r\n����������");
		builder.append(groupStatus.GROP_SENTENCE.size() + groupStatus.GROP_PURECCODE);
		builder.append("\r\n����������");
		builder.append(groupStatus.GROP_CHARACTER);
		builder.append("\r\n���������");
		builder.append(groupStatus.GROP_COMMANDS.size());
		builder.append("\r\n����ͼ����");
		builder.append(groupStatus.GROP_PICTURES.size());
		builder.append("\r\n����ͼ����");
		builder.append(groupStatus.GROP_SNAPSHOT);
		builder.append("\r\n��Ƶ������");
		builder.append(groupStatus.GROP_TAPVIDEO);
		builder.append("\r\n���������");
		builder.append(groupStatus.GROP_HONGBAOS);

		report.add(builder.toString());

		// ===========================================================

		builder = new StringBuilder();

		UserStatus userStatus;
		TreeMap<Integer, HashSet<Long>> allMemberRank = new TreeMap<>((a, b) -> b - a);

		for (long userid : groupStatus.USER_STATUS.keySet()) {
			userStatus = groupStatus.USER_STATUS.get(userid);
			int userCharacter = userStatus.USER_SENTENCE.size() + userStatus.USER_PURECCODE;
			if (userCharacter > 0) {
				if (allMemberRank.containsKey(userCharacter)) {
					allMemberRank.get(userCharacter).add(userid);
				} else {
					HashSet<Long> tempSet = new HashSet<>();
					tempSet.add(userid);
					allMemberRank.put(userCharacter, tempSet);
				}
			}
		}

		if (allMemberRank.size() > 0) {
			builder.append("��2/4����Ա���У�");
			int i = 1;
			for (int userRank : allMemberRank.keySet()) {
				HashSet<Long> tempSet = allMemberRank.get(userRank);
				for (Long userid : tempSet) {
					userStatus = groupStatus.USER_STATUS.get(userid);
					builder.append("\r\nNo.");
					builder.append(i);
					builder.append(" - ");
					builder.append(entry.getNickmap().getNickname(userid));
					builder.append("(");
					builder.append(userid);
					builder.append(") ");
					builder.append(userStatus.USER_SENTENCE.size() + userStatus.USER_PURECCODE);
					builder.append("��/");
					builder.append(userStatus.USER_CHARACTER);
					builder.append("��/");
					builder.append(userStatus.USER_PICTURES.size());
					builder.append("ͼ/");
					builder.append(userStatus.USER_SNAPSHOT);
					builder.append("��/");
					builder.append(userStatus.USER_TAPVIDEO);
					builder.append("Ƭ/");
					builder.append(userStatus.USER_HONGBAOS);
					builder.append("��");
				}
				i = i + tempSet.size();
			}
			report.add(builder.toString());
		}

		// ===========================================================

		builder = new StringBuilder();

		HashMap<String, Integer> allMessageRankTemp = new HashMap<>();

		for (String message : groupStatus.GROP_SENTENCE) {

			// �ϲ���������
			if (message.equals("?")) { message = "��"; }
			if (message.equals("wky")) { message = "�ҿ���"; }
			if (message.equals("whl")) { message = "�Һ���"; }
			if (message.equals("hso")) { message = "��ɧŶ"; }
			if (message.equals("tql")) { message = "̫ǿ��"; }
			if (message.equals("tfl")) { message = "̫����"; }
			// �ϲ���������

			if (allMessageRankTemp.containsKey(message)) {
				allMessageRankTemp.put(message, allMessageRankTemp.get(message) + 1);
			} else {
				allMessageRankTemp.put(message, 1);
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

		allMessageRank.remove(1);

		if (allMessageRank.size() > 0) {
			builder.append("��3/4���������У�");
			int i = 1;
			for (int messageRank : allMessageRank.keySet()) {

				HashSet<String> tempSet = allMessageRank.get(messageRank);
				for (String message : tempSet) {
					builder.append("\r\nNo.");
					builder.append(i);
					builder.append(" - ");
					builder.append(messageRank);
					builder.append("�Σ�");
					builder.append(message);
					if (i++ > 25) { break; }
				}
			}
			report.add(builder.toString());
		} else {
			builder.append("��3/4���������У�û���ظ���������");
		}

		// ===========================================================

		HashMap<String, Integer> allPictureRankTemp = new HashMap<>();
		for (String message : groupStatus.GROP_PICTURES) {
			if (allPictureRankTemp.containsKey(message)) {
				allPictureRankTemp.put(message, allPictureRankTemp.get(message) + 1);
			} else {
				allPictureRankTemp.put(message, 1);
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

		allPictureRank.remove(1);

		if (allPictureRank.size() > 0) {
			report.add("��4/4��ͼƬ���У�");
			int i = 1;
			for (int pictureRank : allPictureRank.keySet()) {
				HashSet<String> tempSet = allPictureRank.get(pictureRank);
				for (String picture : tempSet) {
					builder = new StringBuilder();
					builder.append("No.");
					builder.append(i);
					builder.append(" - ");
					builder.append(pictureRank);
					builder.append("�Σ�\r\n");
					builder.append(JcqApp.CC.getCQImage(picture).getUrl());
					report.add(builder.toString());
					if (i++ > 5) { break; }
				}
			}
		} else {
			builder.append("��4/4��ͼƬ���У�û���ظ�����ͼƬ");
		}

		// ===========================================================

		return report.toArray(new String[report.size()]);
	}

	@SuppressWarnings("deprecation")
	class Worker implements Runnable {
		@Override
		public void run() {
			JcqApp.CQ.logInfo("FurryBlack", "TopSpeak - Worker ������");
			while (JcqAppAbstract.enable) {
				try {
					long time;
					Date date;
					while (true) {
						time = 86405L;
						date = new Date();
						time = time - date.getSeconds();
						time = time - date.getMinutes() * 65;
						time = time - date.getHours() * 3600;
						time = time * 1000;
						time = time - 5;
						Thread.sleep(time);

						for (long temp : Listener_TopSpeak.this.GROUP_STATUS.keySet()) {
							entry.getMessage().gropInfo(temp, Listener_TopSpeak.this.generateMemberRank(temp));
						}

						SecureRandom random = new SecureRandom();
						Thread.sleep(random.nextInt(3600000));
					}
				} catch (InterruptedException exception) {
				}
			}
		}
	}

}
