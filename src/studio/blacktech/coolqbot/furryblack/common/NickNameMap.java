package studio.blacktech.coolqbot.furryblack.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Properties;
import java.util.TreeMap;

import com.sobte.cqp.jcq.entity.Group;
import com.sobte.cqp.jcq.entity.Member;
import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.entry;

public class NickNameMap extends Module {

	private static boolean ENABLE = true;

	private static TreeMap<Long, String> MEMBER = new TreeMap<>();

	public static void init(StringBuilder builder, Properties config) {
		ENABLE = Boolean.parseBoolean(config.getProperty("enable_nickname_map", "true"));
		for (Group group : JcqApp.CQ.getGroupList()) {
			for (Member member : JcqApp.CQ.getGroupMemberList(group.getId())) {
				MEMBER.put(member.getQqId(), member.getNick());
			}
		}
		if (ENABLE) { loadMember(); }
	}

	public static void loadMember() {
		String line;
		String[] temp;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(entry.FILE_NICKNAMES()), "UTF-8"));) {
			while ((line = reader.readLine()) != null) {
				if (line.charAt(1) == '#' || line.indexOf(":") < 0) {
					continue;
				} else {
					temp = line.split(":");
					MEMBER.put(Long.parseLong(temp[0]), temp[1]);
				}
			}
			reader.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		userInfo(entry.OPERATOR(), "已加载");
	}

	public static void dumpMember() {
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(entry.FILE_NICKNAMES(), false), "UTF-8"));
			for (Long userid : MEMBER.keySet()) {
				writer.write(Long.toString(userid));
				writer.write(":");
				writer.write(JcqApp.CQ.getStrangerInfo(userid).getNick());
				writer.write("\r\n");
			}
			writer.flush();
			writer.close();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		userInfo(entry.OPERATOR(), "已保存");
	}

	public static void addMember(String userid, String nickname) {
		MEMBER.put(Long.parseLong(userid), nickname);
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(entry.FILE_NICKNAMES(), true), "UTF-8"));
			writer.write(userid);
			writer.write(":");
			writer.write(nickname);
			writer.flush();
			writer.close();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		userInfo(entry.OPERATOR(), "已添加");
	}

	public static void addMember(Long userid) {
		if (!MEMBER.containsKey(userid)) { MEMBER.put(userid, JcqApp.CQ.getStrangerInfo(userid).getNick()); }
	}

	public static boolean hasMember(Long userid) {
		return MEMBER.containsKey(userid);
	}

	public static String getNickname(Long userid) {
		return MEMBER.get(userid);
	}

}
