package studio.blacktech.coolqbot.furryblack.plugins;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeMap;

import studio.blacktech.coolqbot.furryblack.module.Message;
import studio.blacktech.coolqbot.furryblack.module.ModuleListener;

public class Listener_WaterCount extends ModuleListener {

	private static HashMap<Long, LinkedList<Message>> messages = new HashMap<Long, LinkedList<Message>>(70);

	public Listener_WaterCount() {
		this.MODULE_DISPLAYNAME = "水群统计";
		this.MODULE_PACKAGENAME = "watercount";
		this.MODULE_DESCRIPTION = "水群统计 - 仅开放于某内测群";
		this.MODULE_VERSION = "2.0.2";
		this.MODULE_USAGE = new String[] {};
		this.MODULE_PRIVACY_TRIGER = new String[] {};
		this.MODULE_PRIVACY_LISTEN = new String[] {
				"获取消息发送人", "获取消息信息"
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
		long a = System.nanoTime();
		StringBuilder builder = new StringBuilder();

		int totalmessages = 0;

		LinkedList<String> allmessage = new LinkedList<String>();

		TreeMap<Long, Integer> fayanliang = new TreeMap<Long, Integer>();

		for (long userid : Listener_WaterCount.messages.keySet()) {

			LinkedList<Message> temp = Listener_WaterCount.messages.get(userid);

			totalmessages = totalmessages + temp.size();

			fayanliang.put(userid, temp.size());

			for (Message message : temp) {
				allmessage.add(message.raw);
			}
		}

		TreeMap<String, Integer> messagefreq = new TreeMap<String, Integer>();
		for (String temp : allmessage) {
			if (messagefreq.containsKey(temp)) {
				messagefreq.put(temp, messagefreq.get(temp) + 1);
			} else {
				messagefreq.put(temp, 1);
			}
		}

		TreeMap<Integer, HashSet<String>> freqres = new TreeMap<Integer, HashSet<String>>((o1, o2) -> o2 - o1);
		for (String temp : messagefreq.keySet()) {
			int i = messagefreq.get(temp);
			if (!freqres.containsKey(i)) {
				freqres.put(i, new HashSet<String>());
			}
			freqres.get(i).add(temp);
		}

		TreeMap<Integer, Long> jiatelin = new TreeMap<Integer, Long>((o1, o2) -> o2 - o1);

		for (long userid : fayanliang.keySet()) {
			jiatelin.put(fayanliang.get(userid), userid);
		}

		builder.append("发言总数： ");
		builder.append(totalmessages);

		builder.append("\r\n\r\n发言数量排名：");
		for (int temp : jiatelin.keySet()) {
			builder.append("\r\n");
			builder.append(temp);
			builder.append(": ");
			builder.append(jiatelin.get(temp));
		}

		builder.append("\r\n\r\n整句频度排名：");
		for (int temp : freqres.keySet()) {
			HashSet<String> i = freqres.get(temp);
			for (String t : i) {
				builder.append("\r\n");
				builder.append(temp);
				builder.append(": ");
				builder.append(t);
			}
		}

		long b = System.nanoTime();

		builder.append("报告生成开销:\r\n");
		builder.append(b - a);
		builder.append("ns\r\n");
		builder.append((b - a) / 1000000);
		builder.append("ms");
		return builder.toString();
	}
}
