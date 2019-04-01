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
		this.MODULE_NAME = "����˹���̶�";
		this.MODULE_HELP = "//roulette ���� - ������߷���һ�ֶ���˹���̶ģ���ұ�����ע���Ҳ����˳���ʮ������δ��Ա���Զ���ɢ�Ծ֡�";
		this.MODULE_COMMAND = "roulette";
		this.MODULE_VERSION = "2.3.10";
		this.MODULE_DESCRIPTION = "�㿴���ӵ��ּ��ֳ����������ִ��ֿ�";
		this.MODULE_PRIVACY = "�洢 : ��\r\n���� : 2\r\n1: Ⱥ��-�غ϶�Ӧ�� - �غϽ�����ɾ��\r\n2: �غ�-QQ�Ŷ�Ӧ�� - �غϾͽ�����ɾ��\r\n��ȡ : 2\r\n1: �����������@\r\n2: �û����ǳƺ�Ⱥ�ǳ�";
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
			FunctionExecutor.priWarn(flow, "����˹���̶Ľ�֧��Ⱥ");
			return;
		}
		if (flow.length < 2) {
			FunctionExecutor.grpInfo(flow, "����ע��8koi��");
			return;
		}
		if (!Executor_roulette.rounds.containsKey(flow.gpid)) {
			Executor_roulette.rounds.put(flow.gpid, new RouletteRound());
		}
		final RouletteRound round = Executor_roulette.rounds.get(flow.gpid);
		if (round.lock) {
			FunctionExecutor.grpInfo(flow, "���ֻ�miu����");
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
			FunctionExecutor.grpAnno(flow.gpid, "�����Ѵ��� װ���ӵ��� ");
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
			FunctionExecutor.grpAnno(flow.gpid, "Ŀ���ѻ���:  [CQ:at,qq=" + round.player.get(bullet) + "]\r\n" + round.chip.get(bullet));
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
				FunctionExecutor.grpInfo(flow, "��8koi�뿪����׼�Ź�");
			} else {
				this.players++;
				this.player.add(flow.qqid);
				this.chip.add(flow.join(1));
				final StringBuilder buffer = new StringBuilder();
				buffer.append("����˹���� - ��ǰ���� (");
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
					buffer.append(" - �ȴ�����");
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
		builder.append("�ɹ��غ� : ");
		builder.append(Executor_roulette.ROUND_SUCCESS);
		builder.append("\r\nʧ�ܻغ� : ");
		builder.append(Executor_roulette.ROUND_EXPIRED);
		if (Executor_roulette.ROUND_SUCCESS > 0) {
			builder.append("\r\n��һ�� : ");
			builder.append(Executor_roulette.roulette.get(0));
			builder.append(" - ");
			builder.append((Executor_roulette.roulette.get(0) * 100) / Executor_roulette.ROUND_SUCCESS);
			builder.append("%\r\n�ڶ��� : ");
			builder.append(Executor_roulette.roulette.get(1));
			builder.append(" - ");
			builder.append((Executor_roulette.roulette.get(1) * 100) / Executor_roulette.ROUND_SUCCESS);
			builder.append("%\r\n������ : ");
			builder.append(Executor_roulette.roulette.get(2));
			builder.append(" - ");
			builder.append((Executor_roulette.roulette.get(2) * 100) / Executor_roulette.ROUND_SUCCESS);
			builder.append("%\r\n���ķ� : ");
			builder.append(Executor_roulette.roulette.get(3));
			builder.append(" - ");
			builder.append((Executor_roulette.roulette.get(3) * 100) / Executor_roulette.ROUND_SUCCESS);
			builder.append("%\r\n���巢 : ");
			builder.append(Executor_roulette.roulette.get(4));
			builder.append(" - ");
			builder.append((Executor_roulette.roulette.get(4) * 100) / Executor_roulette.ROUND_SUCCESS);
			builder.append("%\r\n������ : ");
			builder.append(Executor_roulette.roulette.get(5));
			builder.append(" - ");
			builder.append((Executor_roulette.roulette.get(5) * 100) / Executor_roulette.ROUND_SUCCESS);
			builder.append("%");
		}
		return builder.toString();
	}

}
