package studio.blacktech.coolqbot.furryblack.module;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.sobte.cqp.jcq.entity.Member;
import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.signal.Workflow;

public class Executor_roulette extends FunctionExecutor {

	private static HashMap<Long, RouletteRound> rounds = new HashMap<Long, RouletteRound>();

	private static ArrayList<Integer> roulette = new ArrayList<Integer>();
	private static int ROUND_EXPIRED = 0;
	private static int ROUND_SUCCESS = 0;

	public Executor_roulette() {
		this.MODULE_NAME = "俄罗斯轮盘赌";
		this.MODULE_HELP = "//roulette 筹码 - 加入或者发起一局俄罗斯轮盘赌，玩家必须下注，且不可退出。十分钟仍未满员则自动解散对局。";
		this.MODULE_COMMAND = "roulette";
		this.MODULE_VERSION = "2.3.10";
		this.MODULE_DESCRIPTION = "你看这子弹又尖又长，这名单又大又宽";
		this.MODULE_PRIVACY = "存储 : 无\r\n缓存 : 2\r\n1: 群号-回合对应表 - 回合结束后删除\r\n2: 回合-QQ号对应表 - 回合就结束后删除\r\n获取 : 2\r\n1: 命令发送人用于@\r\n2: 用户的昵称和群昵称";
		Executor_roulette.roulette.add(0);
		Executor_roulette.roulette.add(0);
		Executor_roulette.roulette.add(0);
		Executor_roulette.roulette.add(0);
		Executor_roulette.roulette.add(0);
		Executor_roulette.roulette.add(0);
	}

	@Override
	public void executor(final Workflow flow) throws InterruptedException {
		this.counter++;
		if (flow.from < 3) {
			FunctionExecutor.priWarn(flow, "俄罗斯轮盘赌仅支持群");
			return;
		}
		if (flow.length < 2) {
			FunctionExecutor.grpInfo(flow, "不下注是8koi的");
			return;
		}
		if (!Executor_roulette.rounds.containsKey(flow.gpid)) {
			Executor_roulette.rounds.put(flow.gpid, new RouletteRound());
		}
		final RouletteRound round = Executor_roulette.rounds.get(flow.gpid);
		if (round.lock) {
			FunctionExecutor.grpInfo(flow, "本局还miu结束");
			return;
		}
		if ((round.time.getTime() + 600000) < new Date().getTime()) {
			Executor_roulette.ROUND_EXPIRED++;
			Executor_roulette.rounds.remove(flow.gpid);
			Executor_roulette.rounds.put(flow.gpid, new RouletteRound());
		}
		if (round.join(flow)) {
			Executor_roulette.ROUND_SUCCESS++;
			round.lock = true;
			final SecureRandom random = new SecureRandom();
			final int bullet = random.nextInt(6);
			FunctionExecutor.grpAnno(flow.gpid, "名单已凑齐 装填子弹中 ");
			Member member;
			String nickname;
			for (int i = 0; i < 6; i++) {
				member = JcqApp.CQ.getGroupMemberInfoV2(flow.gpid, round.player.get(i));
				nickname = member.getCard();
				if (nickname.length() == 0) {
					nickname = member.getNick();
				}
				if (i == bullet) {
					Executor_roulette.roulette.set(i, Executor_roulette.roulette.get(i) + 1);
					FunctionExecutor.grpAnno(flow.gpid, nickname + " (" + round.player.get(i) + "): [CQ:face,id=169][CQ:emoji,id=10060]");
				} else {
					FunctionExecutor.grpAnno(flow.gpid, nickname + " (" + round.player.get(i) + "): [CQ:face,id=169][CQ:emoji,id=11093]");
				}
			}
			FunctionExecutor.grpAnno(flow.gpid, "目标已击毙:  [CQ:at,qq=" + round.player.get(bullet) + "]\r\n" + round.chip.get(bullet));
			Executor_roulette.rounds.remove(flow.gpid);
		}
	}

	private class RouletteRound {
		public ArrayList<String> chip = new ArrayList<String>();
		public boolean lock = false;
		public ArrayList<Long> player = new ArrayList<Long>();
		public int players = 0;
		public Date time;

		public RouletteRound() {
			this.time = new Date();
		}

		public boolean join(final Workflow flow) {
			if (this.player.contains(flow.qqid)) {
				FunctionExecutor.grpInfo(flow, "你8koi离开，不准放过");
			} else {
				this.players++;
				this.player.add(flow.qqid);
				this.chip.add(flow.join(1));
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
				FunctionExecutor.grpAnno(flow.gpid, buffer.toString());
			}
			return this.players == 6 ? true : false;
		}
	}

	@Override
	public String genReport() {
		if (this.counter == 0) {
			return null;
		}
		final StringBuilder builder = new StringBuilder();
		builder.append("成功回合 : ");
		builder.append(Executor_roulette.ROUND_SUCCESS);
		builder.append("\r\n失败回合 : ");
		builder.append(Executor_roulette.ROUND_EXPIRED);
		if (Executor_roulette.ROUND_SUCCESS > 0) {
			builder.append("\r\n第一发 : ");
			builder.append(Executor_roulette.roulette.get(0));
			builder.append(" - ");
			builder.append((Executor_roulette.roulette.get(0) * 100) / Executor_roulette.ROUND_SUCCESS);
			builder.append("%\r\n第二发 : ");
			builder.append(Executor_roulette.roulette.get(1));
			builder.append(" - ");
			builder.append((Executor_roulette.roulette.get(1) * 100) / Executor_roulette.ROUND_SUCCESS);
			builder.append("%\r\n第三发 : ");
			builder.append(Executor_roulette.roulette.get(2));
			builder.append(" - ");
			builder.append((Executor_roulette.roulette.get(2) * 100) / Executor_roulette.ROUND_SUCCESS);
			builder.append("%\r\n第四发 : ");
			builder.append(Executor_roulette.roulette.get(3));
			builder.append(" - ");
			builder.append((Executor_roulette.roulette.get(3) * 100) / Executor_roulette.ROUND_SUCCESS);
			builder.append("%\r\n第五发 : ");
			builder.append(Executor_roulette.roulette.get(4));
			builder.append(" - ");
			builder.append((Executor_roulette.roulette.get(4) * 100) / Executor_roulette.ROUND_SUCCESS);
			builder.append("%\r\n第六发 : ");
			builder.append(Executor_roulette.roulette.get(5));
			builder.append(" - ");
			builder.append((Executor_roulette.roulette.get(5) * 100) / Executor_roulette.ROUND_SUCCESS);
			builder.append("%");
		}
		return builder.toString();
	}

}
