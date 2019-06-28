package studio.blacktech.coolqbot.furryblack.modules;

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
import studio.blacktech.coolqbot.furryblack.common.Message;
import studio.blacktech.coolqbot.furryblack.common.Module;
import studio.blacktech.coolqbot.furryblack.common.ModuleExecutor;

public class Module_Nick extends ModuleExecutor {

	public Module_Nick() {
		this.MODULE_PACKAGENAME = "nick";
		this.MODULE_DISPLAYNAME = "昵称映射";
		this.MODULE_DESCRIPTION = "将复杂的昵称替换为简单的";
		this.MODULE_VERSION = "1.0.0";
		this.MODULE_USAGE = new String[] {
				"//nick set <短昵称> - 设置你的短昵称",
		};
		this.MODULE_PRIVACY_TRIGER = new String[] {};
		this.MODULE_PRIVACY_LISTEN = new String[] {};
		this.MODULE_PRIVACY_STORED = new String[] {
				"按人存储昵称映射"
		};
		this.MODULE_PRIVACY_CACHED = new String[] {
				"按人存储昵称映射"
		};
		this.MODULE_PRIVACY_OBTAIN = new String[] {
				"获取命令发送人"
		};

	}

	private static boolean ENABLE = true;

	private static TreeMap<Long, String> MEMBER = new TreeMap<>();

	public static void init(final StringBuilder builder, final Properties config) {
		Module_Nick.ENABLE = Boolean.parseBoolean(config.getProperty("enable_nickname_map", "true"));
		for (final Group group : JcqApp.CQ.getGroupList()) {
			for (final Member member : JcqApp.CQ.getGroupMemberList(group.getId())) {
				Module_Nick.MEMBER.put(member.getQqId(), member.getNick());
			}
		}
		if (Module_Nick.ENABLE) { Module_Nick.loadMember(); }
	}

	public static void loadMember() {
		String line;
		String[] temp;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(entry.MODULE_NICK_NICKNAMES()), "UTF-8"));) {
			while ((line = reader.readLine()) != null) {
				if (line.charAt(1) == '#' || line.indexOf(":") < 0) {
					continue;
				} else {
					temp = line.split(":");
					Module_Nick.MEMBER.put(Long.parseLong(temp[0]), temp[1]);
				}
			}
			reader.close();
		} catch (final Exception exception) {
			exception.printStackTrace();
		}
		Module.userInfo(entry.OPERATOR(), "已加载");
	}

	public static void dumpMember() {
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(entry.MODULE_NICK_NICKNAMES(), false), "UTF-8"));) {
			for (final Long userid : Module_Nick.MEMBER.keySet()) {
				writer.write(Long.toString(userid));
				writer.write(":");
				writer.write(JcqApp.CQ.getStrangerInfo(userid).getNick());
				writer.write("\r\n");
			}
			writer.flush();
			writer.close();
		} catch (final IOException exception) {
			exception.printStackTrace();
		}
		Module.userInfo(entry.OPERATOR(), "已保存");
	}

	public static void addMember(final String userid, final String nickname) {
		Module_Nick.MEMBER.put(Long.parseLong(userid), nickname);
		try {
			final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(entry.MODULE_NICK_NICKNAMES(), true), "UTF-8"));
			writer.write(userid);
			writer.write(":");
			writer.write(nickname);
			writer.flush();
			writer.close();
		} catch (final IOException exception) {
			exception.printStackTrace();
		}
		Module.userInfo(entry.OPERATOR(), "已添加");
	}

	public static void addMember(final Long userid) {
		if (!Module_Nick.MEMBER.containsKey(userid)) { Module_Nick.MEMBER.put(userid, JcqApp.CQ.getStrangerInfo(userid).getNick()); }
	}

	public static boolean hasMember(final Long userid) {
		return Module_Nick.MEMBER.containsKey(userid);
	}

	public static String getNickname(final Long userid) {
		return Module_Nick.MEMBER.get(userid);
	}

	@Override
	public boolean doUserMessage(final int typeid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		return false;
	}

	@Override
	public boolean doDiszMessage(final long diszid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		return false;
	}

	@Override
	public boolean doGropMessage(final long gropid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		return false;
	}

	@Override
	public void memberExit(long gropid, long userid) {
	}

	@Override
	public void memberJoin(long gropid, long userid) {
	}

	@Override
	public String[] generateReport(int logLevel, int logMode, int typeid, long userid, long diszid, long gropid, Message message, Object... parameters) {
		return null;
	}

}
