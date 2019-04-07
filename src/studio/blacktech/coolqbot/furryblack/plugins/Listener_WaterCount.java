package studio.blacktech.coolqbot.furryblack.plugins;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeMap;

import com.sobte.cqp.jcq.entity.Member;
import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.module.Message;
import studio.blacktech.coolqbot.furryblack.module.ModuleListener;

public class Listener_WaterCount extends ModuleListener {

	// ���洢 �û�-����
	private static HashMap<Long, LinkedList<Message>> messages = new HashMap<Long, LinkedList<Message>>(70);

	public Listener_WaterCount() {
		this.MODULE_DISPLAYNAME = "ˮȺͳ��";
		this.MODULE_PACKAGENAME = "watercount";
		this.MODULE_DESCRIPTION = "ˮȺͳ�� - ��������ĳ�ڲ�Ⱥ";
		this.MODULE_VERSION = "2.0.2";
		this.MODULE_USAGE = new String[] {};
		this.MODULE_PRIVACY_TRIGER = new String[] {};
		this.MODULE_PRIVACY_LISTEN = new String[] {
				"��ȡ��Ϣ������", "��ȡ��Ϣ��Ϣ"
		};
		this.MODULE_PRIVACY_STORED = new String[] {};
		this.MODULE_PRIVACY_CACHED = new String[] {};
		this.MODULE_PRIVACY_OBTAIN = new String[] {};
	}

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
		if (gropid == 805795515) {
//			message.time = System.currentTimeMillis();
			if (!Listener_WaterCount.messages.containsKey(userid)) {
				Listener_WaterCount.messages.put(userid, new LinkedList<Message>());
			}
			Listener_WaterCount.messages.get(userid).add(message);
		}
		return true;
	}

	@Override
	public String generateReport() {
		return Listener_WaterCount.genReport(false);
	}

	public static String genReport(boolean fullreport) {
		// ���洢
		// private static HashMap<Long, LinkedList<Message>> messages = new HashMap<Long, LinkedList<Message>>(70);
		long a = System.nanoTime();
		int totalMessageCount = 0;
		// ������Ϣ
		LinkedList<String> allMessageFlat = new LinkedList<String>();
		// �û�ID - ��������
		TreeMap<Long, Integer> userSpeakCount = new TreeMap<Long, Integer>();
		for (long userid : Listener_WaterCount.messages.keySet()) {
			// �����洢��������List
			LinkedList<Message> temp = Listener_WaterCount.messages.get(userid);
			// ��������Ϣ����
			totalMessageCount = totalMessageCount + temp.size();
			// ��¼ÿ���û��ķ�������
			userSpeakCount.put(userid, temp.size());
			for (Message message : temp) {
				// ����List�� ȡ��������Ϣ����List
				allMessageFlat.add(message.raw);
			}
		}
		TreeMap<String, Integer> messageCount = new TreeMap<String, Integer>();
		// ͳ����Ϣ���ִ��� ����Ϊ ��Ϣ - ����
		for (String temp : allMessageFlat) {
			if (messageCount.containsKey(temp)) {
				messageCount.put(temp, messageCount.get(temp) + 1);
			} else {
				messageCount.put(temp, 1);
			}
		}
		TreeMap<Integer, HashSet<String>> messageRepeatCountResult = new TreeMap<Integer, HashSet<String>>((o1, o2) -> o2 - o1);
		// ת��Ϊ ���� - ��Ϣ ����TreeMap�Զ�����
		for (String temp : messageCount.keySet()) {
			int i = messageCount.get(temp);
			if (!messageRepeatCountResult.containsKey(i)) {
				messageRepeatCountResult.put(i, new HashSet<String>());
			}
			messageRepeatCountResult.get(i).add(temp);
		}
		TreeMap<Integer, Long> userSpeakCountInDesOrder = new TreeMap<Integer, Long>((o1, o2) -> o2 - o1);
		// �û� - ���� ת��Ϊ ���� - �û� ����TreeMap�Զ���������
		for (long userid : userSpeakCount.keySet()) {
			userSpeakCountInDesOrder.put(userSpeakCount.get(userid), userid);
		}
		// =============================================================
		int limit = 0;
		Member member;
		StringBuilder builder = new StringBuilder();
		builder.append("���������� ");
		builder.append(totalMessageCount);
		builder.append("\r\n\r\n��������������");
		long useruid = 0;
		for (int userSpeakCountValue : userSpeakCountInDesOrder.keySet()) {
			limit++;
			useruid = userSpeakCountInDesOrder.get(userSpeakCountValue);
			member = JcqApp.CQ.getGroupMemberInfoV2(805795515, useruid);
			builder.append("\r\n");
			builder.append(userSpeakCountValue);
			builder.append(": ");
			builder.append(member.getCard().length() == 0 ? member.getCard() : member.getNick());
			builder.append("(");
			builder.append(useruid);
			builder.append(")");
			if (!fullreport && (limit > 4)) {
				limit = 0;
				break;
			}
		}
		builder.append("\r\n\r\n����Ƶ��������");
		for (int messageRepeatCountValue : messageRepeatCountResult.keySet()) {
			if (messageRepeatCountValue == 1) {
				continue;
			}
			HashSet<String> messageRepeatCountSentences = messageRepeatCountResult.get(messageRepeatCountValue);
			for (String sentence : messageRepeatCountSentences) {
				limit++;
				builder.append("\r\n");
				builder.append(messageRepeatCountValue);
				builder.append(": ");
				builder.append(sentence);
			}
			if (!fullreport && (limit > 9)) {
				break;
			}
		}
		long b = System.nanoTime();
		builder.append("\r\n\r\n�������ɿ���:\r\n");
		builder.append(b - a);
		builder.append("ns\r\n");
		builder.append((b - a) / 1000000);
		builder.append("ms");
		return builder.toString();
	}
}
