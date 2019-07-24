package studio.blacktech.coolqbot.furryblack.modules;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.sobte.cqp.jcq.entity.Member;
import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;

public class Executor_roulette extends ModuleExecutor {

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "executor_roulette";
	private static String MODULE_COMMANDNAME = "roulette";
	private static String MODULE_DISPLAYNAME = "俄罗斯轮盘赌";
	private static String MODULE_DESCRIPTION = "你看这子弹又尖又长，这名单又大又宽";
	private static String MODULE_VERSION = "1.0";
	private static String[] MODULE_USAGE = new String[] {
			"/roulette 筹码 - 加入或者发起一局俄罗斯轮盘赌，十分钟仍未满员则自动解散对局"
	};
	private static String[] MODULE_PRIVACY_TRIGER = new String[] {};
	private static String[] MODULE_PRIVACY_LISTEN = new String[] {};
	private static String[] MODULE_PRIVACY_STORED = new String[] {};
	private static String[] MODULE_PRIVACY_CACHED = new String[] {};
	private static String[] MODULE_PRIVACY_OBTAIN = new String[] {
			"获取命令发送人"
	};

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================

	private HashMap<Long, RouletteRound> rounds = new HashMap<>();
	private ArrayList<Integer> roulette = new ArrayList<>();
	private int ROUND_EXPIRED = 0;
	private int ROUND_SUCCESS = 0;

	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================

	public Executor_roulette() throws Exception {
		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_TRIGER, MODULE_PRIVACY_LISTEN, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	@Override
	public void init(LoggerX logger) throws Exception {

		this.roulette.add(0);
		this.roulette.add(0);
		this.roulette.add(0);
		this.roulette.add(0);
		this.roulette.add(0);
		this.roulette.add(0);

		this.ENABLE_USER = true;
		this.ENABLE_DISZ = true;
		this.ENABLE_GROP = true;
	}

	@Override
	public void boot(LoggerX logger) throws Exception {
	}

	@Override
	public void shut(LoggerX logger) throws Exception {
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
		return true;
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, MessageDisz message, int messageid, int messagefont) throws Exception {
		return true;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, MessageGrop message, int messageid, int messagefont) throws Exception {
		if (message.getSection() < 2) {
			entry.getMessage().gropInfo(gropid, userid, "不下注是8koi的");
			return true;
		}
		if (!this.rounds.containsKey(gropid)) { this.rounds.put(gropid, new RouletteRound()); }
		RouletteRound round = this.rounds.get(gropid);

		if (round.lock) {
			// 本来是有锁的，但是我觉得没人能正好在100ms内再加入所以删了
			// SX found this BUG
			// Module.gropInfo(gropid, "你是最佳第七人，你妈妈不爱你，你甚至不配拥有名字。");
			return false;
		} else {
			if (round.time.getTime() + 600000 < new Date().getTime()) {
				this.ROUND_EXPIRED++;
				this.rounds.remove(gropid);
				this.rounds.put(gropid, new RouletteRound());
			}
			if (round.join(gropid, userid, message)) {
				this.ROUND_SUCCESS++;
				SecureRandom random = new SecureRandom();
				int bullet = random.nextInt(6);
				entry.getMessage().gropInfo(gropid, "名单已凑齐 装填子弹中");
				Member member;
				for (int i = 0; i < 6; i++) {
					member = JcqApp.CQ.getGroupMemberInfoV2(gropid, round.player.get(i));
					if (i == bullet) {
						this.roulette.set(i, this.roulette.get(i) + 1);
						entry.getMessage().gropInfo(gropid, entry.getNickmap().getNickname(member.getQqId()) + " (" + round.player.get(i) + "): [CQ:face,id=169][CQ:emoji,id=10060]");
					} else {
						entry.getMessage().gropInfo(gropid, entry.getNickmap().getNickname(member.getQqId()) + " (" + round.player.get(i) + "): [CQ:face,id=169][CQ:emoji,id=11093]");
					}

				}
				entry.getMessage().gropInfo(gropid, "@平安中国 目标已击毙:  [CQ:at,qq=" + round.player.get(bullet) + "]\r\n" + round.chip.get(bullet));
				this.rounds.remove(gropid);
			}
		}
		return true;
	}

	private class RouletteRound {

		public ArrayList<String> chip = new ArrayList<>();
		public ArrayList<Long> player = new ArrayList<>();
		public int players = 0;
		public Date time;
		public boolean lock = false;

		public RouletteRound() {
			this.time = new Date();
		}

		public boolean join(long gropid, long userid, Message message) {
			if (this.player.contains(userid)) {
				entry.getMessage().gropInfo(gropid, "你8koi离开，不准放过");
			} else {
				this.time = new Date();
				this.players++;
				this.player.add(userid);
				this.chip.add(message.join(1));
				StringBuilder buffer = new StringBuilder();
				buffer.append("俄罗斯轮盘 - 当前人数 (");
				buffer.append(this.players);
				buffer.append("/6)");
				int i = 0;
				for (; i < this.players; i++) {
					buffer.append("\r\n");
					buffer.append(i + 1);
					buffer.append(" - ");
					buffer.append(this.player.get(i));
					buffer.append(" : ");
					buffer.append(this.chip.get(i));
				}
				for (; i < 6; i++) {
					buffer.append("\r\n");
					buffer.append(i + 1);
					buffer.append(" - 等待加入");
				}
				entry.getMessage().gropInfo(gropid, buffer.toString());
			}
			this.lock = this.players > 5 ? true : false;
			return this.lock;
		}
	}

	@Override
	public String[] generateReport(int mode, Message message, Object... parameters) {
		if (this.COUNT_USER + this.COUNT_DISZ + this.COUNT_GROP == 0) { return null; }
		StringBuilder builder = new StringBuilder();
		builder.append("成功回合 : ");
		builder.append(this.ROUND_SUCCESS);
		builder.append("\r\n失败回合 : ");
		builder.append(this.ROUND_EXPIRED);
		if (this.ROUND_SUCCESS > 0) {
			builder.append("\r\n第一发 : ");
			builder.append(this.roulette.get(0));
			builder.append(" - ");
			builder.append(this.roulette.get(0) * 100 / this.ROUND_SUCCESS);
			builder.append("%\r\n第二发 : ");
			builder.append(this.roulette.get(1));
			builder.append(" - ");
			builder.append(this.roulette.get(1) * 100 / this.ROUND_SUCCESS);
			builder.append("%\r\n第三发 : ");
			builder.append(this.roulette.get(2));
			builder.append(" - ");
			builder.append(this.roulette.get(2) * 100 / this.ROUND_SUCCESS);
			builder.append("%\r\n第四发 : ");
			builder.append(this.roulette.get(3));
			builder.append(" - ");
			builder.append(this.roulette.get(3) * 100 / this.ROUND_SUCCESS);
			builder.append("%\r\n第五发 : ");
			builder.append(this.roulette.get(4));
			builder.append(" - ");
			builder.append(this.roulette.get(4) * 100 / this.ROUND_SUCCESS);
			builder.append("%\r\n第六发 : ");
			builder.append(this.roulette.get(5));
			builder.append(" - ");
			builder.append(this.roulette.get(5) * 100 / this.ROUND_SUCCESS);
			builder.append("%");
		}
		String res[] = new String[1];
		res[0] = builder.toString();
		return res;
	}
}
