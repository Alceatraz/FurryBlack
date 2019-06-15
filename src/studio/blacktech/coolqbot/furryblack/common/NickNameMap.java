package studio.blacktech.coolqbot.furryblack.common;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Properties;

import com.sobte.cqp.jcq.entity.Group;
import com.sobte.cqp.jcq.entity.Member;
import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.entry;

public class NickNameMap {

	private static HashMap<Long, String> MEMBER = new HashMap<>();

	public static boolean hasMember(Long userid) {
		return MEMBER.containsKey(userid);
	}

	public static String addMember(Long userid, String nickname) {
		return MEMBER.put(userid, nickname);
	}

	public static String addMember(Long userid) {
		return MEMBER.put(userid, JcqApp.CQ.getStrangerInfo(userid).getNick());
	}

	public static String getNickname(Long userid) {
		return MEMBER.get(userid);
	}

	public static void init(StringBuilder builder, Properties config) throws NumberFormatException, IOException {

		String line;
		String[] temp;
		BufferedReader reader;

		if (Boolean.parseBoolean(config.getProperty("enable_nickname_map", "true"))) {

			for (Group group : JcqApp.CQ.getGroupList()) {
				for (Member member : JcqApp.CQ.getGroupMemberList(group.getId())) {
					MEMBER.put(member.getQqId(), member.getNick());
				}
			}

			reader = new BufferedReader(new InputStreamReader(new FileInputStream(entry.FILE_NICKNAMES()), "UTF-8"));
			while ((line = reader.readLine()) != null) {
				temp = line.split(":");
				MEMBER.put(Long.parseLong(temp[0]), temp[1]);
			}
			reader.close();
		}

	}
}
