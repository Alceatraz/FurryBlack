package studio.blacktech.coolqbot.furryblack.plugins;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.util.Date;
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
	public String generateReport(int logLevel, int logMode, Message message, Object[] parameters) {

		long timeStart = System.currentTimeMillis();

		StringBuilder builder = new StringBuilder();

		try {
			switch (logLevel) {
			case 0:
				// 0 = 按照群总发言数对群排序
				this.generateGroupsRank(builder);
				break;
			case 1:
				// 1 = 群内按照成员发言数排序
				this.generateMembersRank(builder, parameters);
				break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				break;
			case 100:
				// 100 = 生成dump文件
				this.generateDumpFile(builder);
				break;

			}

		} catch (Exception exce) {
			exce.printStackTrace();
			builder.append("报告生成出错");
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

		long timeFinsh = System.currentTimeMillis();

		builder.append("\r\n\r\n生成报告开销： ");
		builder.append(timeFinsh - timeStart);
		builder.append("ms");

		return builder.toString();
	}

	/***
	 * 所有群按总发言数排名
	 *
	 * @param builder
	 */
	private void generateGroupsRank(StringBuilder builder) {

		int i = 0;

		TreeMap<Integer, Long> allGroupRank = new TreeMap<Integer, Long>((a, b) -> b - a);

		// 利用红黑树按群的总发言条数降序
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

	/***
	 * 指定群按发言数成员排名
	 *
	 * @param builder
	 */
	private void generateMembersRank(StringBuilder builder, Object[] parameters) {

		long gropid = (long) parameters[0];
		GroupStatus groupStatus = this.STORAGE.get(gropid);
		Member member;
		int i = 0;

		TreeMap<Integer, Long> allMemberRank = new TreeMap<Integer, Long>((a, b) -> b - a);

		builder.append("总发言条数：");
		builder.append(groupStatus.TOTAL_GROUP);
		builder.append("\r\n总发言字数：");
		builder.append(groupStatus.LENGTH_GROUP);
		builder.append("\r\n成员排行：");

		// 利用红黑树按成员的发言条数降序
		for (long userid : groupStatus.userStatus.keySet()) {
			allMemberRank.put(groupStatus.userStatus.get(userid).TOTAL_MEMBER, userid);
		}

		Long userid;
		UserStatus userStatus;
		for (int userRank : allMemberRank.keySet()) {
			i++;
			userid = allMemberRank.get(userRank);
			userStatus = groupStatus.userStatus.get(userid);
			builder.append("\r\nNo.");
			builder.append(i);
			builder.append(" - ");
			builder.append(JcqApp.CQ.getGroupMemberInfoV2(gropid, userid).getNick());
			builder.append("(");
			builder.append(userid);
			builder.append(") ");
			builder.append(userStatus.TOTAL_MEMBER);
			builder.append("句/");
			builder.append(userStatus.LENGTH_MEMBER);
			builder.append("字");
		}
	}

	private void generateDumpFile(StringBuilder builder) throws IOException {

		String dumpFileName = LoggerX.time("yyyy.MM.dd-HH.mm.ss");
		File dumpFile = Paths.get(entry.FOLDER_DATA().getAbsolutePath(), "TopSpeak_Dump_" + dumpFileName + ".txt").toFile();
		dumpFile.createNewFile();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dumpFile), "UTF-8"));

		writer.write("完整镜像 - " + dumpFileName);

		writer.write("\r\n\r\n\r\n");
		writer.write("\r\n================================================================================");
		writer.write("\r\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		writer.write("\r\n================================================================================");
		writer.write("按群时间线");

		for (long gropid : this.STORAGE.keySet()) {

			GroupStatus tempGroup = this.STORAGE.get(gropid);

			writer.write("\r\n\r\n\r\n");
			writer.write("\r\n================================================================================");
			writer.write("\r\n群号：" + gropid);
			writer.write("\r\n发言条数：" + tempGroup.TOTAL_GROUP);
			writer.write("\r\n发言字数：" + tempGroup.TOTAL_GROUP);

			writer.flush();

			for (Message tempMessage : tempGroup.gropMessages) {
				writer.write("\r\n  " + LoggerX.time(new Date(tempMessage.sendTime)) + "：" + tempMessage.rawMessage);
			}

		}

		writer.write("\r\n\r\n\r\n");
		writer.write("\r\n================================================================================");
		writer.write("\r\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		writer.write("\r\n================================================================================");
		writer.write("按群员分组");

		Member member;

		for (long gropid : this.STORAGE.keySet()) {

			GroupStatus tempGroup = this.STORAGE.get(gropid);

			writer.write("\r\n\r\n\r\n");
			writer.write("\r\n================================================================================");
			writer.write("\r\n群号：" + gropid);
			writer.write("\r\n发言条数：" + tempGroup.TOTAL_GROUP);
			writer.write("\r\n发言字数：" + tempGroup.TOTAL_GROUP);
			writer.flush();

			for (Long qqid : tempGroup.userStatus.keySet()) {

				UserStatus tempUser = tempGroup.userStatus.get(qqid);

				writer.write("\r\n    ----------------------------------------------------------------------------");

				member = JcqApp.CQ.getGroupMemberInfoV2(gropid, qqid);

				writer.write("\r\n    用户： " + member.getQqId());
				writer.write("\r\n    昵称： " + member.getNick());
				writer.write("\r\n    名片： " + member.getCard());
				writer.write("\r\n    发言条数： " + tempUser.TOTAL_MEMBER);
				writer.write("\r\n    发言条数： " + tempUser.LENGTH_MEMBER);

				for (Message tempMessage : tempUser.userMessages) {
					writer.write("\r\n      " + LoggerX.time(new Date(tempMessage.sendTime)) + "：" + tempMessage.rawMessage);
				}
				writer.flush();
			}

		}
		writer.close();
		builder.append("完整内存镜像已保存至：");
		builder.append(dumpFileName);
	}

}
