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

	private static HashMap<Long, RouletteRound> rounds = new HashMap<Long, RouletteRound>();
	private static ArrayList<Integer> roulette = new ArrayList<Integer>();
	private static int ROUND_EXPIRED = 0;
	private static int ROUND_SUCCESS = 0;

	public Executor_gamb() {

		this.MODULE_DISPLAYNAME = "����˹���̶�";
		this.MODULE_PACKAGENAME = "gamb";
		this.MODULE_DESCRIPTION = "�㿴���ӵ��ּ��ֳ����������ִ��ֿ�";
		this.MODULE_VERSION = "2.12.4";
		this.MODULE_USAGE = new String[] {
				"//gamb ���� - ������߷���һ�ֶ���˹���̶ģ���ұ�����ע���Ҳ����˳���ʮ������δ��Ա���Զ���ɢ�Ծ֡�"
		};
		this.MODULE_PRIVACY_TRIGER = new String[] {};
		this.MODULE_PRIVACY_LISTEN = new String[] {};
		this.MODULE_PRIVACY_STORED = new String[] {};
		this.MODULE_PRIVACY_CACHED = new String[] {
				"Ⱥ��-�غ϶�Ӧ�� - �غϽ�����ɾ��", "�غ�-QQ�Ŷ�Ӧ�� - �غϾͽ�����ɾ��"
		};
		this.MODULE_PRIVACY_OBTAIN = new String[] {
				"��ȡ�������", "�û����ǳƺ�Ⱥ�ǳ�"
		};

		Executor_gamb.roulette.add(0);
		Executor_gamb.roulette.add(0);
		Executor_gamb.roulette.add(0);
		Executor_gamb.roulette.add(0);
		Executor_gamb.roulette.add(0);
		Executor_gamb.roulette.add(0);
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
		if (message.length < 2) {
			Module.gropInfo(gropid, userid, "����ע��8koi��");
			return true;
		}
		if (!Executor_gamb.rounds.containsKey(gropid)) {
			Executor_gamb.rounds.put(gropid, new RouletteRound());
		}
		final RouletteRound round = Executor_gamb.rounds.get(gropid);
		if ((round.time.getTime() + 600000) < new Date().getTime()) {
			Executor_gamb.ROUND_EXPIRED++;
			Executor_gamb.rounds.remove(gropid);
			Executor_gamb.rounds.put(gropid, new RouletteRound());
		}
		if (round.join(gropid, userid, message)) {
			Executor_gamb.ROUND_SUCCESS++;
			final SecureRandom random = new SecureRandom();
			final int bullet = random.nextInt(6);
			Module.gropInfo(gropid, "�����Ѵ��� װ���ӵ���");
			Member member;
			for (int i = 0; i < 6; i++) {
				member = JcqApp.CQ.getGroupMemberInfoV2(gropid, round.player.get(i));
				if (i == bullet) {
					Executor_gamb.roulette.set(i, Executor_gamb.roulette.get(i) + 1);
					Module.gropInfo(gropid, (member.getCard().length() == 0 ? member.getCard() : member.getNick()) + " (" + round.player.get(i) + "): [CQ:face,id=169][CQ:emoji,id=10060]");
				} else {
					Module.gropInfo(gropid, (member.getCard().length() == 0 ? member.getCard() : member.getNick()) + " (" + round.player.get(i) + "): [CQ:face,id=169][CQ:emoji,id=11093]");
				}
			}
			Module.gropInfo(gropid, "Ŀ���ѻ���:  [CQ:at,qq=" + round.player.get(bullet) + "]\r\n" + round.chip.get(bullet));
			Executor_gamb.rounds.remove(gropid);
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
				Module.gropInfo(gropid, "��8koi�뿪����׼�Ź�");
			} else {
				this.time = new Date();
				this.players++;
				this.player.add(userid);
				this.chip.add(message.join(1));
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
				Module.gropInfo(gropid, buffer.toString());
			}
			return this.players == 6 ? true : false;
		}

	}

	@Override
	public String generateReport() {
		if (this.COUNT == 0) {
			return null;
		}
		final StringBuilder builder = new StringBuilder();
		builder.append("�ɹ��غ� : ");
		builder.append(Executor_gamb.ROUND_SUCCESS);
		builder.append("\r\nʧ�ܻغ� : ");
		builder.append(Executor_gamb.ROUND_EXPIRED);
		if (Executor_gamb.ROUND_SUCCESS > 0) {
			builder.append("\r\n��һ�� : ");
			builder.append(Executor_gamb.roulette.get(0));
			builder.append(" - ");
			builder.append((Executor_gamb.roulette.get(0) * 100) / Executor_gamb.ROUND_SUCCESS);
			builder.append("%\r\n�ڶ��� : ");
			builder.append(Executor_gamb.roulette.get(1));
			builder.append(" - ");
			builder.append((Executor_gamb.roulette.get(1) * 100) / Executor_gamb.ROUND_SUCCESS);
			builder.append("%\r\n������ : ");
			builder.append(Executor_gamb.roulette.get(2));
			builder.append(" - ");
			builder.append((Executor_gamb.roulette.get(2) * 100) / Executor_gamb.ROUND_SUCCESS);
			builder.append("%\r\n���ķ� : ");
			builder.append(Executor_gamb.roulette.get(3));
			builder.append(" - ");
			builder.append((Executor_gamb.roulette.get(3) * 100) / Executor_gamb.ROUND_SUCCESS);
			builder.append("%\r\n���巢 : ");
			builder.append(Executor_gamb.roulette.get(4));
			builder.append(" - ");
			builder.append((Executor_gamb.roulette.get(4) * 100) / Executor_gamb.ROUND_SUCCESS);
			builder.append("%\r\n������ : ");
			builder.append(Executor_gamb.roulette.get(5));
			builder.append(" - ");
			builder.append((Executor_gamb.roulette.get(5) * 100) / Executor_gamb.ROUND_SUCCESS);
			builder.append("%");
		}
		return builder.toString();
	}

}
