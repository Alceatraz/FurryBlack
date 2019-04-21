package studio.blacktech.coolqbot.furryblack.plugins;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.sobte.cqp.jcq.entity.Member;
import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.module.Message;
import studio.blacktech.coolqbot.furryblack.module.Module;
import studio.blacktech.coolqbot.furryblack.module.ModuleExecutor;

public class Executor_gamb extends ModuleExecutor {

	private HashMap<Long, RouletteRound> rounds = new HashMap<Long, RouletteRound>();
	private ArrayList<Integer> roulette = new ArrayList<Integer>();
	private int ROUND_EXPIRED = 0;
	private int ROUND_SUCCESS = 0;

	public Executor_gamb() {

		this.MODULE_DISPLAYNAME = "俄罗斯轮盘赌";
		this.MODULE_PACKAGENAME = "gamb";
		this.MODULE_DESCRIPTION = "你看这子弹又尖又长，这名单又大又宽";
		this.MODULE_VERSION = "2.12.4";
		this.MODULE_USAGE = new String[] {
				"//gamb 筹码 - 加入或者发起一局俄罗斯轮盘赌，玩家必须下注，且不可退出。十分钟仍未满员则自动解散对局。"
		};
		this.MODULE_PRIVACY_TRIGER = new String[] {};
		this.MODULE_PRIVACY_LISTEN = new String[] {};
		this.MODULE_PRIVACY_STORED = new String[] {};
		this.MODULE_PRIVACY_CACHED = new String[] {
				"群号-回合对应表 - 回合结束后删除", "回合-QQ号对应表 - 回合结束后删除"
		};
		this.MODULE_PRIVACY_OBTAIN = new String[] {
				"获取命令发送人", "用户的昵称和群昵称"
		};

		this.roulette.add(0);
		this.roulette.add(0);
		this.roulette.add(0);
		this.roulette.add(0);
		this.roulette.add(0);
		this.roulette.add(0);
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
		if (message.segment < 2) {
			Module.gropInfo(gropid, userid, "不下注是8koi的");
			return true;
		}
		if (!this.rounds.containsKey(gropid)) {
			this.rounds.put(gropid, new RouletteRound());
		}
		final RouletteRound round = this.rounds.get(gropid);
		if ((round.time.getTime() + 600000) < new Date().getTime()) {
			this.ROUND_EXPIRED++;
			this.rounds.remove(gropid);
			this.rounds.put(gropid, new RouletteRound());
		}
		if (round.join(gropid, userid, message)) {
			this.ROUND_SUCCESS++;
			final SecureRandom random = new SecureRandom();
			final int bullet = random.nextInt(6);
			Module.gropInfo(gropid, "名单已凑齐 装填子弹中");
			Member member;
			for (int i = 0; i < 6; i++) {
				member = JcqApp.CQ.getGroupMemberInfoV2(gropid, round.player.get(i));
				if (i == bullet) {
					this.roulette.set(i, this.roulette.get(i) + 1);
					Module.gropInfo(gropid, member.getNick() + " (" + round.player.get(i) + "): [CQ:face,id=169][CQ:emoji,id=10060]");
				} else {
					Module.gropInfo(gropid, member.getNick() + " (" + round.player.get(i) + "): [CQ:face,id=169][CQ:emoji,id=11093]");
				}
			}
			Module.gropInfo(gropid, "目标已击毙:  [CQ:at,qq=" + round.player.get(bullet) + "]\r\n" + round.chip.get(bullet));
			this.rounds.remove(gropid);
		}
		return true;
	}

	private class RouletteRound {

		public ArrayList<String> chip = new ArrayList<String>();
		public ArrayList<Long> player = new ArrayList<Long>();
		public int players = 0;
		public Date time;

		public RouletteRound() {
			this.time = new Date();
		}

		public boolean join(long gropid, long userid, Message message) {
			if (this.player.contains(userid)) {
				Module.gropInfo(gropid, "你8koi离开，不准放过");
			} else {
				this.time = new Date();
				this.players++;
				this.player.add(userid);
				this.chip.add(message.join(1));
				final StringBuilder buffer = new StringBuilder();
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
				Module.gropInfo(gropid, buffer.toString());
			}
			return this.players == 6 ? true : false;
		}

	}

	@Override
	public String generateReport(boolean fullreport, int loglevel, Object[] parameters) {
		if (this.COUNT == 0) {
			return null;
		}
		final StringBuilder builder = new StringBuilder();
		builder.append("成功回合 : ");
		builder.append(this.ROUND_SUCCESS);
		builder.append("\r\n失败回合 : ");
		builder.append(this.ROUND_EXPIRED);
		if (this.ROUND_SUCCESS > 0) {
			builder.append("\r\n第一发 : ");
			builder.append(this.roulette.get(0));
			builder.append(" - ");
			builder.append((this.roulette.get(0) * 100) / this.ROUND_SUCCESS);
			builder.append("%\r\n第二发 : ");
			builder.append(this.roulette.get(1));
			builder.append(" - ");
			builder.append((this.roulette.get(1) * 100) / this.ROUND_SUCCESS);
			builder.append("%\r\n第三发 : ");
			builder.append(this.roulette.get(2));
			builder.append(" - ");
			builder.append((this.roulette.get(2) * 100) / this.ROUND_SUCCESS);
			builder.append("%\r\n第四发 : ");
			builder.append(this.roulette.get(3));
			builder.append(" - ");
			builder.append((this.roulette.get(3) * 100) / this.ROUND_SUCCESS);
			builder.append("%\r\n第五发 : ");
			builder.append(this.roulette.get(4));
			builder.append(" - ");
			builder.append((this.roulette.get(4) * 100) / this.ROUND_SUCCESS);
			builder.append("%\r\n第六发 : ");
			builder.append(this.roulette.get(5));
			builder.append(" - ");
			builder.append((this.roulette.get(5) * 100) / this.ROUND_SUCCESS);
			builder.append("%");
		}
		return builder.toString();
	}

}
