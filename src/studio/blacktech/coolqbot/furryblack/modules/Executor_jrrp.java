package studio.blacktech.coolqbot.furryblack.modules;

import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

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

	private static String MODULE_PACKAGENAME = "jrrp";
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

	private final HashMap<Long, Integer> JRRP = new HashMap<>();

	private Thread flush;

	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================

	public Executor_jrrp() throws Exception {
		super(MODULE_DISPLAYNAME, MODULE_PACKAGENAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_TRIGER, MODULE_PRIVACY_LISTEN, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
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
	public boolean doUserMessage(final int typeid, final long userid, final MessageUser message, final int messageid, final int messagefont) throws Exception {
		if (!this.JRRP.containsKey(userid)) {
			final SecureRandom random = new SecureRandom();
			this.JRRP.put(userid, random.nextInt(100));
		}
		entry.getMessage().userInfo(userid, "今天的运气是" + this.JRRP.get(userid) + "%!!!");
		return true;
	}

	@Override
	public boolean doDiszMessage(final long diszid, final long userid, final MessageDisz message, final int messageid, final int messagefont) throws Exception {
		if (!this.JRRP.containsKey(userid)) {
			final SecureRandom random = new SecureRandom();
			this.JRRP.put(userid, random.nextInt(100));
		}
		entry.getMessage().diszInfo(diszid, userid, "今天的运气是" + this.JRRP.get(userid) + "%!!!");
		return true;
	}

	@Override
	public boolean doGropMessage(final long gropid, final long userid, final MessageGrop message, final int messageid, final int messagefont) throws Exception {
		if (!this.JRRP.containsKey(userid)) {
			final SecureRandom random = new SecureRandom();
			this.JRRP.put(userid, random.nextInt(100));
		}
		entry.getMessage().gropInfo(gropid, userid, "今天的运气是" + this.JRRP.get(userid) + "%!!!");
		return true;
	}

	@Override
	public String[] generateReport(int mode, final Message message, final Object... parameters) {
		if (this.COUNT == 0) { return null; }
		final TreeMap<Integer, Integer> frequency = new TreeMap<>();
		for (final long temp : this.JRRP.keySet()) {
			final int luck = this.JRRP.get(temp);
			frequency.put(luck, frequency.containsKey(luck) ? frequency.get(luck) + 1 : 1);
		}
		final int size = this.JRRP.size();
		final StringBuilder builder = new StringBuilder();
		builder.append("共生成了 ");
		builder.append(size);
		builder.append("次");
		for (final Entry<Integer, Integer> temp : frequency.entrySet()) {
			builder.append("\r\n");
			builder.append(temp.getKey());
			builder.append(" : ");
			builder.append(temp.getValue() * 100 / size);
			builder.append("%");
		}
		final String res[] = new String[1];
		res[0] = builder.toString();
		return res;
	}

	@SuppressWarnings("deprecation")
	class Worker implements Runnable {
		@Override
		public void run() {
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
				} catch (final InterruptedException exception) {
				}
			}
		}
	}
}
