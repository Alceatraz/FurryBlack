package studio.blacktech.coolqbot.furryblack.plugins;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.module.Message;
import studio.blacktech.coolqbot.furryblack.module.ModuleListener;

public class Listener_TopSpeak extends ModuleListener {

	private int TOTAL_GLOBAL = 0;
	private int LENGTH_GLOBAL = 0;
	private HashMap<Long, GroupStatus> STORAGE = new HashMap<Long, GroupStatus>();

	public Listener_TopSpeak() {
		this.MODULE_DISPLAYNAME = "水群统计";
		this.MODULE_PACKAGENAME = "shui";
		this.MODULE_DESCRIPTION = "水群统计";
		this.MODULE_VERSION = "3.2.2";
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
		message.calculateLength();
		this.TOTAL_GLOBAL++;
		this.LENGTH_GLOBAL = this.LENGTH_GLOBAL + message.length;
		if (!this.STORAGE.containsKey(gropid)) {
			this.STORAGE.put(gropid, new GroupStatus());
		}
		this.STORAGE.get(gropid).speak(userid, message);
		return true;
	}

	@Override
	public String generateReport(boolean fullreport, int loglevel, Object[] parameters) {

		StringBuilder builder = new StringBuilder();
		TreeMap<Integer, Long> groupRank = new TreeMap<Integer, Long>((a, b) -> a - b);

		if (fullreport) {

			int i = 0;
			int j = 0;

			switch (loglevel) {

			case 1:
				// 二参数私聊调用 所有群排行和所有成员排行
				for (long gropid : this.STORAGE.keySet()) {
					groupRank.put(this.STORAGE.get(gropid).TOTAL_GROUP, gropid);
				}
				builder.append("总发言数：");
				builder.append(this.TOTAL_GLOBAL);
				builder.append("\r\n总发字节：");
				builder.append(this.LENGTH_GLOBAL);
				builder.append("\r\n群排行：");
				for (int count : groupRank.keySet()) {
					i++;
					builder.append("\r\n");
					builder.append("No.");
					builder.append(i);
					builder.append("：");
					builder.append(groupRank.get(count));
					builder.append(" - ");
					builder.append(count);
					builder.append("条/");
					builder.append(this.STORAGE.get(groupRank.get(count)).LENGTH_GROUP);
					builder.append("字\r\n");
					GroupStatus status = this.STORAGE.get(groupRank.get(count));
					for (long temp : status.userStatus.keySet()) {
						groupRank.put(status.userStatus.get(temp).TOTAL_MEMBER, temp);
					}
					for (int temp : groupRank.keySet()) {
						j++;
						builder.append("\r\n");
						builder.append("No.");
						builder.append(j);
						builder.append(" - ");
						builder.append(JcqApp.CQ.getGroupMemberInfoV2((long) parameters[0], groupRank.get(temp)).getNick());
						builder.append("(");
						groupRank.get(temp);
						builder.append(")");
						builder.append(status.userStatus.get(groupRank.get(temp)).TOTAL_MEMBER);
						builder.append(" 句");
						builder.append(status.userStatus.get(groupRank.get(temp)).LENGTH_MEMBER);
						builder.append(" 字");
					}
				}
				break;

			// 三参数私聊调用
			case 2:
				break;

			case 3:
				// 在群内调用 统计本群最能水的人
				GroupStatus status = this.STORAGE.get((long) parameters[0]);
				builder.append("总发言数：");
				builder.append(status.TOTAL_GROUP);
				builder.append("\r\n总发字节：");
				builder.append(status.LENGTH_GROUP);
				builder.append("\r\n群排行：");
				for (long temp : status.userStatus.keySet()) {
					groupRank.put(status.userStatus.get(temp).TOTAL_MEMBER, temp);
				}
				for (int temp : groupRank.keySet()) {
					i++;
					builder.append("\r\n");
					builder.append("No.");
					builder.append(i);
					builder.append(" - ");
					builder.append(JcqApp.CQ.getGroupMemberInfoV2((long) parameters[0], groupRank.get(temp)).getNick());
					builder.append("(");
					groupRank.get(temp);
					builder.append(")");
					builder.append(status.userStatus.get(groupRank.get(temp)).TOTAL_MEMBER);
					builder.append(" 句");
					builder.append(status.userStatus.get(groupRank.get(temp)).LENGTH_MEMBER);
					builder.append(" 字");
				}
				break;
			}
		} else {
			// 每日报告 - 所有群排行

			for (long gropid : this.STORAGE.keySet()) {
				groupRank.put(this.STORAGE.get(gropid).TOTAL_GROUP, gropid);
			}
			builder.append("总发言数：");
			builder.append(this.TOTAL_GLOBAL);
			builder.append("\r\n总发字节：");
			builder.append(this.LENGTH_GLOBAL);
			builder.append("\r\n群排行：");
			int i = 0;
			for (int count : groupRank.keySet()) {
				i++;
				builder.append("\r\n");
				builder.append("No.");
				builder.append(i);
				builder.append("：");
				builder.append(groupRank.get(count));
				builder.append(" - ");
				builder.append(count);
				builder.append("条/");
				builder.append(this.STORAGE.get(groupRank.get(count)).LENGTH_GROUP);
				builder.append("字");
			}
		}
		return builder.toString();
	}

	private class GroupStatus {

		public int TOTAL_GROUP = 0;
		public int LENGTH_GROUP = 0;
		public LinkedList<Message> gropMessages = new LinkedList<Message>();
		public HashMap<Long, UserStatus> userStatus = new HashMap<Long, UserStatus>();

		public void speak(long userid, Message message) {
			this.TOTAL_GROUP++;
			this.LENGTH_GROUP = this.LENGTH_GROUP + message.length;
			if (!this.userStatus.containsKey(userid)) {
				this.userStatus.put(userid, new UserStatus());
			}
			this.gropMessages.add(message);
			this.userStatus.get(userid).speak(message);
		}

	}

	private class UserStatus {

		public int TOTAL_MEMBER = 0;
		public int LENGTH_MEMBER = 0;
		public LinkedList<Message> userMessages = new LinkedList<Message>();

		public void speak(Message message) {
			this.TOTAL_MEMBER++;
			this.LENGTH_MEMBER = this.LENGTH_MEMBER + message.length;
			this.userMessages.add(message);
		}
	}
}
