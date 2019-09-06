package studio.blacktech.coolqbot.furryblack.modules;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.util.TreeMap;

import com.sobte.cqp.jcq.entity.Group;
import com.sobte.cqp.jcq.entity.Member;
import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.common.LoggerX.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.module.Module;

public class Module_Nickmap extends Module {

	private static final long serialVersionUID = 1L;

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "Core_Nickmap";
	private static String MODULE_COMMANDNAME = "nickmap";
	private static String MODULE_DISPLAYNAME = "昵称映射";
	private static String MODULE_DESCRIPTION = "将复杂的昵称映射为朋友间的简短称呼";
	private static String MODULE_VERSION = "1.0";
	private static String[] MODULE_USAGE = new String[] {};
	private static String[] MODULE_PRIVACY_TRIGER = new String[] {};
	private static String[] MODULE_PRIVACY_LISTEN = new String[] {};
	private static String[] MODULE_PRIVACY_STORED = new String[] {
			"按照\"群号:QQ号:昵称\"的形式存储简短昵称"
	};
	private static String[] MODULE_PRIVACY_CACHED = new String[] {};
	private static String[] MODULE_PRIVACY_OBTAIN = new String[] {};

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================

	private static TreeMap<Long, TreeMap<Long, String>> NICKNAME;

	private File FILE_NICKNAME;

	private NicknameDelegate delegate = new NicknameDelegate();

	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================

	public Module_Nickmap() throws Exception {
		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_TRIGER, MODULE_PRIVACY_LISTEN, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	@Override
	public void init(LoggerX logger) throws Exception {

		this.initDataFolder();

		NICKNAME = new TreeMap<>();

		for (Group group : JcqApp.CQ.getGroupList()) {
			NICKNAME.put(group.getId(), new TreeMap<>());
		}

		this.FILE_NICKNAME = Paths.get(this.FOLDER_DATA.getAbsolutePath(), "user_nickname.txt").toFile();
		if (!this.FILE_NICKNAME.exists()) { this.FILE_NICKNAME.createNewFile(); }

		String line;
		String temp[];
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.FILE_NICKNAME), "UTF-8"));
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("#")) { continue; }
			if (line.indexOf(":") < 0) { continue; }
			temp = line.split(":");
			if (temp.length != 3) {
				logger.mini(MODULE_PACKAGENAME, "配置错误", line);
				continue;
			}
			NICKNAME.get(Long.parseLong(temp[0])).put(Long.parseLong(temp[1]), temp[2]);

			logger.seek(MODULE_PACKAGENAME, "读取昵称表", NICKNAME.size());
			for (long nickmap : NICKNAME.keySet()) {
				logger.seek(MODULE_PACKAGENAME, "群" + nickmap, NICKNAME.get(nickmap).size() + "个");
			}
		}
		reader.close();
	}

	@Override
	public void boot(LoggerX logger) throws Exception {
	}

	@Override
	public void shut(LoggerX logger) throws Exception {
	}

	@Override
	public void save(LoggerX logger) throws Exception {
	}

	@Override
	public void reload(LoggerX logger) throws Exception {
	}

	@Override
	public void exec(LoggerX logger, Message message) throws Exception {

		if (message.getSection() < 2) {
			logger.info(MODULE_PACKAGENAME, "参数不足");
			return;
		}

		String command = message.getSegment()[1];

		switch (command) {

		case "save":

			// 这并不是保存功能，读取所有群，列出所有成员 保存到文件
			// 为什么叫save？ 你想每次输命令都写
			// /admin exec --module=nickmap dumpAllGropMemberToFileByGroup
			// 吗，还是想
			// /admin exec --module=nickmap --hexcommnad=00,22,F1,FF,C4,F2,00,DA,C1,43,F0,90

			StringBuilder builder = new StringBuilder();

			builder.append("\r\n# Save ");
			builder.append(LoggerX.datetime());
			builder.append("\r\n");

			for (Group group : JcqApp.CQ.getGroupList()) {
				long groupid = group.getId();
				for (Member member : JcqApp.CQ.getGroupMemberList(groupid)) {
					builder.append(groupid);
					builder.append(":");
					builder.append(member.getQqId());
					builder.append(":");
					builder.append(member.getNick());
					builder.append("\r\n");
				}
			}

			// FileWriter writer = new FileWriter(this.FILE_NICKNAME, true);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.FILE_NICKNAME, true), "UTF-8"));
			writer.append(builder.toString().substring(0, builder.length() - 2));
			writer.flush();
			writer.close();

			return;

		case "load":

			return;
		}
	}

	@Override
	public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) throws Exception {

		StringBuilder builder = new StringBuilder();

		builder.append("\r\n# Member Increase ");
		builder.append(LoggerX.datetime());
		builder.append("\r\n");
		builder.append(gropid);
		builder.append(":");
		builder.append(userid);
		builder.append(":");
		builder.append(JcqApp.CQ.getStrangerInfo(userid));

		FileWriter writer = new FileWriter(this.FILE_NICKNAME, true);
		writer.append(builder.toString());
		writer.flush();
		writer.close();

	}

	@Override
	public void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) throws Exception {
		if (NICKNAME.containsKey(gropid)) {
			TreeMap<Long, String> temp = NICKNAME.get(gropid);
			if (temp.containsKey(userid)) {
				temp.remove(userid);
				//
			}
		}
	}

	// ==========================================================================================================================================================
	//
	// 工具函数
	//
	// ==========================================================================================================================================================

	@Override
	public String[] generateReport(int mode, Message message, Object... parameters) {
		return null;
	}

	public String doGetGropnick(long gropid, long userid) {

		if (NICKNAME.containsKey(gropid)) {
			TreeMap<Long, String> temp = NICKNAME.get(gropid);
			if (temp.containsKey(userid)) {
				return temp.get(userid);
				//
			}
		}
		return JcqApp.CQ.getStrangerInfo(userid).getNick();
	}

	// ==========================================================================================================================================================
	//
	// 代理对象
	//
	// ==========================================================================================================================================================

	public NicknameDelegate getDelegate() {
		return this.delegate;
	}

	public class NicknameDelegate {
		public String getGropnick(long gropid, long userid) {
			return Module_Nickmap.this.doGetGropnick(gropid, userid);
		}

		public String getNickname(long userid) {
			return JcqApp.CQ.getStrangerInfo(userid).getNick();
		}
	}
}
