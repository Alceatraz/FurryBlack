package studio.blacktech.coolqbot.furryblack.plugins;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

import com.sobte.cqp.jcq.entity.Member;
import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.common.LoggerX;
import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.module.Message;
import studio.blacktech.coolqbot.furryblack.module.ModuleListener;

@SuppressWarnings("unused")
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
		this.TOTAL_GLOBAL++;
		this.LENGTH_GLOBAL = this.LENGTH_GLOBAL + message.length;
		if (!this.STORAGE.containsKey(gropid)) {
			this.STORAGE.put(gropid, new GroupStatus());
		}
		this.STORAGE.get(gropid).speak(userid, message);
		return true;
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

	/***
	 *
	 */
	@Override
	public String generateReport(boolean fullreport, int loglevel, Object[] parameters) {

		long aaa = System.currentTimeMillis();

		StringBuilder builder = new StringBuilder();
		TreeMap<Integer, Long> allGroupRank = new TreeMap<Integer, Long>((a, b) -> b - a);
		TreeMap<Integer, Long> memberRank = new TreeMap<Integer, Long>((a, b) -> b - a);
		GroupStatus status;
		int i = 0;
		try {
			if (fullreport) {
				switch (loglevel) {
				case 1:
					// 二参数私聊调用
				case 2:
					// 三参数私聊调用
					status = this.STORAGE.get((long) parameters[0]);
					builder.append("全球发言条数：");
					builder.append(status.TOTAL_GROUP);
					builder.append("\r\n全球发言字数：");
					builder.append(status.LENGTH_GROUP);
					builder.append("\r\n全球群排行：");
					for (long temp : status.userStatus.keySet()) {
						memberRank.put(status.userStatus.get(temp).TOTAL_MEMBER, temp);
					}
					for (int temp : memberRank.keySet()) {
						i++;
						builder.append("\r\n");
						builder.append("No.");
						builder.append(i);
						builder.append(" - ");
						builder.append(JcqApp.CQ.getGroupMemberInfoV2((long) parameters[0], memberRank.get(temp)).getNick());
						builder.append("(");
						builder.append(memberRank.get(temp));
						builder.append(") ");
						builder.append(status.userStatus.get(memberRank.get(temp)).TOTAL_MEMBER);
						builder.append("句/");
						builder.append(status.userStatus.get(memberRank.get(temp)).LENGTH_MEMBER);
						builder.append(" 字");
					}
					break;
				case 3:
					// 在群内调用 统计本群最能水的人
					status = this.STORAGE.get((long) parameters[0]);
					builder.append("总发言条数：");
					builder.append(status.TOTAL_GROUP);
					builder.append("\r\n总发言字数：");
					builder.append(status.LENGTH_GROUP);
					builder.append("\r\n成员排行：");
					for (long temp : status.userStatus.keySet()) {
						allGroupRank.put(status.userStatus.get(temp).TOTAL_MEMBER, temp);
					}
					for (int temp : allGroupRank.keySet()) {
						i++;
						builder.append("\r\n");
						builder.append("No.");
						builder.append(i);
						builder.append(" - ");

						// 未知Runtime BUG ====================================================
						long temp_gpid = (long) parameters[0];
						long temp_qqid = allGroupRank.get(temp);
						Member temp_info = JcqApp.CQ.getGroupMemberInfoV2(temp_gpid, temp_qqid);
						String temp_nick = temp_info.getNick();
						builder.append(temp_nick);

						// 未知Runtime BUG ====================================================

						/*
						 * builder.append(JcqApp.CQ.getGroupMemberInfoV2((long)
						 * parameters[0],allGroupRank.get(temp)).getNick());
						 */

						builder.append("(");
						builder.append(allGroupRank.get(temp));
						builder.append(") ");
						builder.append(status.userStatus.get(allGroupRank.get(temp)).TOTAL_MEMBER);
						builder.append("句/");
						builder.append(status.userStatus.get(allGroupRank.get(temp)).LENGTH_MEMBER);
						builder.append(" 字");
					}
					break;
				case 10:
					// 私聊调用，dump

					String dumpFileName = "Dump." + LoggerX.time("yyyy_MM_dd.HH_mm_ss") + ".yml";

					File dumpFile = Paths.get(entry.FOLDER_DATA().getAbsolutePath(), dumpFileName).toFile();
					dumpFile.createNewFile();
					FileWriter writer = new FileWriter(dumpFile);

					writer.write("全球总发言数：");
					writer.write(this.TOTAL_GLOBAL);
					writer.write("\r\n全球总发字节：");
					writer.write(this.LENGTH_GLOBAL);
					writer.write("\r\n\r\n\r\n");

					i = 0;

					for (long gropid : this.STORAGE.keySet()) {

						GroupStatus tempGroup = this.STORAGE.get(gropid);

						writer.write("\r\n\r\n\r\n");
						writer.write("\r\n=================================================================================================================================================");
						writer.write("\r\n=================================================================================================================================================");

						writer.write("\r\n群：" + gropid);
						writer.write("\r\n发言条数：" + tempGroup.TOTAL_GROUP);
						writer.write("\r\n发言字数：" + tempGroup.TOTAL_GROUP);
						for (Long qqid : tempGroup.userStatus.keySet()) {
							UserStatus tempUser = tempGroup.userStatus.get(qqid);
							writer.write("\r\n    -----------------------------------------------------------------------------------------------------------------------------------------");
							writer.write("\r\n    用户：  " + qqid);
							writer.write("\r\n      发言条数：  " + tempUser.TOTAL_MEMBER);
							writer.write("\r\n      发言条数：  " + tempUser.LENGTH_MEMBER);
							for (Message tempMessage : tempUser.userMessages) {
								writer.write("\r\n        " + tempMessage.rawMessage);
							}
							writer.flush();
						}
						writer.flush();
					}
					writer.flush();
					writer.close();
					builder.append("完整内存镜像已保存至：");
					builder.append(dumpFileName);
					break;
				}
			} else {
				// 每日报告 - 所有群排行
				for (long gropid : this.STORAGE.keySet()) {
					allGroupRank.put(this.STORAGE.get(gropid).TOTAL_GROUP, gropid);
				}
				builder.append("全球总发言条数：");
				builder.append(this.TOTAL_GLOBAL);
				builder.append("\r\n全球总发言字数：");
				builder.append(this.LENGTH_GLOBAL);
				builder.append("\r\n全球群排行：");
				for (int count : allGroupRank.keySet()) {
					i++;
					builder.append("\r\n");
					builder.append("No.");
					builder.append(i);
					builder.append("：");
					builder.append(allGroupRank.get(count));
					builder.append(" - ");
					builder.append(count);
					builder.append("条/");
					builder.append(this.STORAGE.get(allGroupRank.get(count)).LENGTH_GROUP);
					builder.append("字");
				}
			}

		} catch (IOException exce) {
			exce.printStackTrace();
			builder.append("报告生成出错");
			for (StackTraceElement stack : exce.getStackTrace()) {
				builder.append("Line: ");
				builder.append(stack.getLineNumber());
				builder.append(" In - ");
				builder.append(stack.getClassName());
				builder.append(" >> ");
				builder.append(stack.getMethodName());
				builder.append("\r\n");
			}
		}

		long b = System.currentTimeMillis();
		builder.append("\r\n\r\n生成报告开销： ");
		builder.append(b - aaa);
		builder.append("ms");

		return builder.toString();
	}

}
