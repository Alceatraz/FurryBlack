package studio.blacktech.coolqbot.furryblack.modules;

import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;

import com.sobte.cqp.jcq.event.JcqApp;
import com.sobte.cqp.jcq.event.JcqAppAbstract;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;

public class Executor_jrrp extends ModuleExecutor {

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "executor_jrrp";
	private static String MODULE_COMMANDNAME = "jrrp";
	private static String MODULE_DISPLAYNAME = "今日运气";
	private static String MODULE_DESCRIPTION = "查看今天的运气值";
	private static String MODULE_VERSION = "1.0";
	private static String[] MODULE_USAGE = new String[] {
			"/jrrp - 查看今日运气"
	};
	private static String[] MODULE_PRIVACY_TRIGER = new String[] {};
	private static String[] MODULE_PRIVACY_LISTEN = new String[] {};
	private static String[] MODULE_PRIVACY_STORED = new String[] {};
	private static String[] MODULE_PRIVACY_CACHED = new String[] {
			"用户与运气对应表 - 每日UTC+8 00:00 清空"
	};
	private static String[] MODULE_PRIVACY_OBTAIN = new String[] {
			"获取命令发送人"
	};

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================

	private HashMap<Long, Integer> JRRP = new HashMap<>();

	private Thread flush;

	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================

	public Executor_jrrp() throws Exception {
		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_TRIGER, MODULE_PRIVACY_LISTEN, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	@Override
	public void init(LoggerX logger) throws Exception {
		this.ENABLE_USER = true;
		this.ENABLE_DISZ = true;
		this.ENABLE_GROP = true;
	}

	@Override
	public void boot(LoggerX logger) throws Exception {
		this.flush = new Thread(new Worker());
		this.flush.start();
	}

	@Override
	public void shut(LoggerX logger) throws Exception {
		this.flush.interrupt();
	}

	@Override
	public void reload(LoggerX logger) throws Exception {
	}

	@Override
	public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) {
	}

	@Override
	public void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) {
	}

	@Override
	public boolean doUserMessage(int typeid, long userid, MessageUser message, int messageid, int messagefont) throws Exception {
		if (!this.JRRP.containsKey(userid)) {
			SecureRandom random = new SecureRandom();
			this.JRRP.put(userid, random.nextInt(100));
		}
		entry.getMessage().userInfo(userid, "今天的运气是" + this.JRRP.get(userid) + "%!!!");
		return true;
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, MessageDisz message, int messageid, int messagefont) throws Exception {
		if (!this.JRRP.containsKey(userid)) {
			SecureRandom random = new SecureRandom();
			this.JRRP.put(userid, random.nextInt(100));
		}
		entry.getMessage().diszInfo(diszid, userid, "今天的运气是" + this.JRRP.get(userid) + "%!!!");
		return true;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, MessageGrop message, int messageid, int messagefont) throws Exception {
		if (!this.JRRP.containsKey(userid)) {
			SecureRandom random = new SecureRandom();
			this.JRRP.put(userid, random.nextInt(100));
		}
		entry.getMessage().gropInfo(gropid, userid, "今天的运气是" + this.JRRP.get(userid) + "%!!!");
		return true;
	}

	@Override
	public String[] generateReport(int mode, Message message, Object... parameters) {
		return null;
		/*
		 * if (this.COUNT_USER + this.COUNT_DISZ + this.COUNT_GROP == 0) { return null;
		 * } TreeMap<Integer, Integer> frequency = new TreeMap<>(); for (long temp :
		 * this.JRRP.keySet()) { int luck = this.JRRP.get(temp); frequency.put(luck,
		 * frequency.containsKey(luck) ? frequency.get(luck) + 1 : 1); } int size =
		 * this.JRRP.size(); StringBuilder builder = new StringBuilder();
		 * builder.append("共生成了 "); builder.append(size); builder.append("次"); for
		 * (Entry<Integer, Integer> temp : frequency.entrySet()) {
		 * builder.append("\r\n"); builder.append(temp.getKey()); builder.append(" : ");
		 * builder.append(temp.getValue() * 100 / size); builder.append("%"); } String
		 * res[] = new String[1]; res[0] = builder.toString(); return res;
		 */
	}

	@SuppressWarnings("deprecation")
	class Worker implements Runnable {
		@Override
		public void run() {
			JcqApp.CQ.logInfo("FurryBlack", "jrrp - Worker 已启动");
			while (JcqAppAbstract.enable) {
				try {
					long time;
					Date date;
					while (true) {
						// =======================================================
						time = 86400L;
						date = new Date();
						time = time - date.getSeconds();
						time = time - date.getMinutes() * 65;
						time = time - date.getHours() * 3600;
						time = time * 1000;
						time = time - 5;
						Thread.sleep(time);
						// =======================================================
						Executor_jrrp.this.JRRP.clear();
						// =======================================================
						SecureRandom random = new SecureRandom();
						Thread.sleep(random.nextInt(3600000));
						// =======================================================
					}
				} catch (InterruptedException exception) {
				}
			}
		}
	}
}
