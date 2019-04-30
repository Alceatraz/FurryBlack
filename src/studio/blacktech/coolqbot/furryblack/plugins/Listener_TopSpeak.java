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
		this.MODULE_DISPLAYNAME = "ˮȺͳ��";
		this.MODULE_PACKAGENAME = "shui";
		this.MODULE_DESCRIPTION = "ˮȺͳ��";
		this.MODULE_VERSION = "3.2.2";
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
					// ������˽�ĵ���
				case 2:
					// ������˽�ĵ���
					status = this.STORAGE.get((long) parameters[0]);
					builder.append("ȫ����������");
					builder.append(status.TOTAL_GROUP);
					builder.append("\r\nȫ����������");
					builder.append(status.LENGTH_GROUP);
					builder.append("\r\nȫ��Ⱥ���У�");
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
						builder.append("��/");
						builder.append(status.userStatus.get(memberRank.get(temp)).LENGTH_MEMBER);
						builder.append(" ��");
					}
					break;
				case 3:
					// ��Ⱥ�ڵ��� ͳ�Ʊ�Ⱥ����ˮ����
					status = this.STORAGE.get((long) parameters[0]);
					builder.append("�ܷ���������");
					builder.append(status.TOTAL_GROUP);
					builder.append("\r\n�ܷ���������");
					builder.append(status.LENGTH_GROUP);
					builder.append("\r\n��Ա���У�");
					for (long temp : status.userStatus.keySet()) {
						allGroupRank.put(status.userStatus.get(temp).TOTAL_MEMBER, temp);
					}
					for (int temp : allGroupRank.keySet()) {
						i++;
						builder.append("\r\n");
						builder.append("No.");
						builder.append(i);
						builder.append(" - ");

						// δ֪Runtime BUG ====================================================
						long temp_gpid = (long) parameters[0];
						long temp_qqid = allGroupRank.get(temp);
						Member temp_info = JcqApp.CQ.getGroupMemberInfoV2(temp_gpid, temp_qqid);
						String temp_nick = temp_info.getNick();
						builder.append(temp_nick);

						// δ֪Runtime BUG ====================================================

						/*
						 * builder.append(JcqApp.CQ.getGroupMemberInfoV2((long)
						 * parameters[0],allGroupRank.get(temp)).getNick());
						 */

						builder.append("(");
						builder.append(allGroupRank.get(temp));
						builder.append(") ");
						builder.append(status.userStatus.get(allGroupRank.get(temp)).TOTAL_MEMBER);
						builder.append("��/");
						builder.append(status.userStatus.get(allGroupRank.get(temp)).LENGTH_MEMBER);
						builder.append(" ��");
					}
					break;
				case 10:
					// ˽�ĵ��ã�dump

					String dumpFileName = "Dump." + LoggerX.time("yyyy_MM_dd.HH_mm_ss") + ".yml";

					File dumpFile = Paths.get(entry.FOLDER_DATA().getAbsolutePath(), dumpFileName).toFile();
					dumpFile.createNewFile();
					FileWriter writer = new FileWriter(dumpFile);

					writer.write("ȫ���ܷ�������");
					writer.write(this.TOTAL_GLOBAL);
					writer.write("\r\nȫ���ܷ��ֽڣ�");
					writer.write(this.LENGTH_GLOBAL);
					writer.write("\r\n\r\n\r\n");

					i = 0;

					for (long gropid : this.STORAGE.keySet()) {

						GroupStatus tempGroup = this.STORAGE.get(gropid);

						writer.write("\r\n\r\n\r\n");
						writer.write("\r\n=================================================================================================================================================");
						writer.write("\r\n=================================================================================================================================================");

						writer.write("\r\nȺ��" + gropid);
						writer.write("\r\n����������" + tempGroup.TOTAL_GROUP);
						writer.write("\r\n����������" + tempGroup.TOTAL_GROUP);
						for (Long qqid : tempGroup.userStatus.keySet()) {
							UserStatus tempUser = tempGroup.userStatus.get(qqid);
							writer.write("\r\n    -----------------------------------------------------------------------------------------------------------------------------------------");
							writer.write("\r\n    �û���  " + qqid);
							writer.write("\r\n      ����������  " + tempUser.TOTAL_MEMBER);
							writer.write("\r\n      ����������  " + tempUser.LENGTH_MEMBER);
							for (Message tempMessage : tempUser.userMessages) {
								writer.write("\r\n        " + tempMessage.rawMessage);
							}
							writer.flush();
						}
						writer.flush();
					}
					writer.flush();
					writer.close();
					builder.append("�����ڴ澵���ѱ�������");
					builder.append(dumpFileName);
					break;
				}
			} else {
				// ÿ�ձ��� - ����Ⱥ����
				for (long gropid : this.STORAGE.keySet()) {
					allGroupRank.put(this.STORAGE.get(gropid).TOTAL_GROUP, gropid);
				}
				builder.append("ȫ���ܷ���������");
				builder.append(this.TOTAL_GLOBAL);
				builder.append("\r\nȫ���ܷ���������");
				builder.append(this.LENGTH_GLOBAL);
				builder.append("\r\nȫ��Ⱥ���У�");
				for (int count : allGroupRank.keySet()) {
					i++;
					builder.append("\r\n");
					builder.append("No.");
					builder.append(i);
					builder.append("��");
					builder.append(allGroupRank.get(count));
					builder.append(" - ");
					builder.append(count);
					builder.append("��/");
					builder.append(this.STORAGE.get(allGroupRank.get(count)).LENGTH_GROUP);
					builder.append("��");
				}
			}

		} catch (IOException exce) {
			exce.printStackTrace();
			builder.append("�������ɳ���");
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
		builder.append("\r\n\r\n���ɱ��濪���� ");
		builder.append(b - aaa);
		builder.append("ms");

		return builder.toString();
	}

}
