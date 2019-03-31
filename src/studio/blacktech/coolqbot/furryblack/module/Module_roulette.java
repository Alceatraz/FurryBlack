package studio.blacktech.coolqbot.furryblack.module;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.sobte.cqp.jcq.entity.Member;
import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.signal.Workflow;

public class Module_roulette extends FunctionModuel {

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
				FunctionModuel.grpInfo(flow, "��8koi�뿪����׼�Ź�");
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
				FunctionModuel.grpAnno(flow.gpid, buffer.toString());
			}
			return this.players == 6 ? true : false;
		}
	}

	private static ArrayList<Integer> roulette = new ArrayList<Integer>();
	private static int ROUND_EXPIRED = 0;
	private static int ROUND_SUCCESS = 0;

	private static HashMap<Long, RouletteRound> rounds = new HashMap<Long, RouletteRound>();

	public Module_roulette() {
		this.MODULE_NAME = "����˹���̶�";
		this.MODULE_HELP = "//roulette ���� - ������߷���һ�ֶ���˹���̶ģ���ұ�����ע���Ҳ����˳���ʮ������δ��Ա���Զ���ɢ�Ծ֡�";
		this.MODULE_COMMAND = "roulette";
		this.MODULE_VERSION = "2.3.10";
		this.MODULE_DESCRIPTION = "�㿴���ӵ��ּ��ֳ����������ִ��ֿ�";
		this.MODULE_PRIVACY = "�洢 : ��\r\n���� : 2\r\n1: Ⱥ��-�غ϶�Ӧ�� - �غϽ�����ɾ��\r\n2: �غ�-QQ�Ŷ�Ӧ�� - �غϾͽ�����ɾ��\r\n��ȡ : 2\r\n1: �����������@\r\n2: �û����ǳƺ�Ⱥ�ǳ�";
		Module_roulette.roulette.add(0);
		Module_roulette.roulette.add(0);
		Module_roulette.roulette.add(0);
		Module_roulette.roulette.add(0);
		Module_roulette.roulette.add(0);
		Module_roulette.roulette.add(0);
	}

	@Override
	public void excute(final Workflow flow) throws InterruptedException {
		this.counter++;
		if (flow.from < 3) {
			FunctionModuel.priWarn(flow, "����˹���̶Ľ�֧��Ⱥ");
			return;
		}
		if (flow.length < 2) {
			FunctionModuel.grpInfo(flow, "����ע��8koi��");
			return;
		}
		if (!Module_roulette.rounds.containsKey(flow.gpid)) {
			Module_roulette.rounds.put(flow.gpid, new RouletteRound());
		}
		final RouletteRound round = Module_roulette.rounds.get(flow.gpid);
		if (round.lock) {
			FunctionModuel.grpInfo(flow, "���ֻ�miu����");
			return;
		}
		if ((round.time.getTime() + 600000) < new Date().getTime()) {
			Module_roulette.ROUND_EXPIRED++;
			Module_roulette.rounds.remove(flow.gpid);
			Module_roulette.rounds.put(flow.gpid, new RouletteRound());
		}
		if (round.join(flow)) {
			Module_roulette.ROUND_SUCCESS++;
			round.lock = true;
			final SecureRandom random = new SecureRandom();
			final int bullet = random.nextInt(6);
			FunctionModuel.grpAnno(flow.gpid, "�����Ѵ��� װ���ӵ��� ");
			Member member;
			String nickname;
			for (int i = 0; i < 6; i++) {
				member = JcqApp.CQ.getGroupMemberInfoV2(flow.gpid, round.player.get(i));
				nickname = member.getCard();
				if (nickname.length() == 0) {
					nickname = member.getNick();
				}
				if (i == bullet) {
					Module_roulette.roulette.set(i, Module_roulette.roulette.get(i) + 1);
					FunctionModuel.grpAnno(flow.gpid, nickname + " (" + round.player.get(i) + "): [CQ:face,id=169][CQ:emoji,id=10060]");
				} else {
					FunctionModuel.grpAnno(flow.gpid, nickname + " (" + round.player.get(i) + "): [CQ:face,id=169][CQ:emoji,id=11093]");
				}
			}
			FunctionModuel.grpAnno(flow.gpid, "Ŀ���ѻ���:  [CQ:at,qq=" + round.player.get(bullet) + "]\r\n" + round.chip.get(bullet));
			Module_roulette.rounds.remove(flow.gpid);
		}
	}

	@Override
	public String genReport() {
		if (this.counter == 0) {
			return null;
		}
		final StringBuilder builder = new StringBuilder();
		builder.append("�ɹ��غ� : ");
		builder.append(Module_roulette.ROUND_SUCCESS);
		builder.append("\r\nʧ�ܻغ� : ");
		builder.append(Module_roulette.ROUND_EXPIRED);
		if (Module_roulette.ROUND_SUCCESS > 0) {
			builder.append("\r\n��һ�� : ");
			builder.append(Module_roulette.roulette.get(0));
			builder.append(" - ");
			builder.append((Module_roulette.roulette.get(0) * 100) / Module_roulette.ROUND_SUCCESS);
			builder.append("%\r\n�ڶ��� : ");
			builder.append(Module_roulette.roulette.get(1));
			builder.append(" - ");
			builder.append((Module_roulette.roulette.get(1) * 100) / Module_roulette.ROUND_SUCCESS);
			builder.append("%\r\n������ : ");
			builder.append(Module_roulette.roulette.get(2));
			builder.append(" - ");
			builder.append((Module_roulette.roulette.get(2) * 100) / Module_roulette.ROUND_SUCCESS);
			builder.append("%\r\n���ķ� : ");
			builder.append(Module_roulette.roulette.get(3));
			builder.append(" - ");
			builder.append((Module_roulette.roulette.get(3) * 100) / Module_roulette.ROUND_SUCCESS);
			builder.append("%\r\n���巢 : ");
			builder.append(Module_roulette.roulette.get(4));
			builder.append(" - ");
			builder.append((Module_roulette.roulette.get(4) * 100) / Module_roulette.ROUND_SUCCESS);
			builder.append("%\r\n������ : ");
			builder.append(Module_roulette.roulette.get(5));
			builder.append(" - ");
			builder.append((Module_roulette.roulette.get(5) * 100) / Module_roulette.ROUND_SUCCESS);
			builder.append("%");
		}
		return builder.toString();
	}
}
