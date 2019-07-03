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
	// ģ���������
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "roulette";
	private static String MODULE_DISPLAYNAME = "����˹���̶�";
	private static String MODULE_DESCRIPTION = "�㿴���ӵ��ּ��ֳ����������ִ��ֿ�";
	private static String MODULE_VERSION = "1.0";
	private static String[] MODULE_USAGE = new String[] {
			"/roulette ���� - ������߷���һ�ֶ���˹���̶ģ�ʮ������δ��Ա���Զ���ɢ�Ծ�"
	};
	private static String[] MODULE_PRIVACY_TRIGER = new String[] {};
	private static String[] MODULE_PRIVACY_LISTEN = new String[] {};
	private static String[] MODULE_PRIVACY_STORED = new String[] {};
	private static String[] MODULE_PRIVACY_CACHED = new String[] {};
	private static String[] MODULE_PRIVACY_OBTAIN = new String[] {
			"��ȡ�������"
	};

	// ==========================================================================================================================================================
	//
	// ��Ա����
	//
	// ==========================================================================================================================================================

	private final HashMap<Long, RouletteRound> rounds = new HashMap<>();
	private final ArrayList<Integer> roulette = new ArrayList<>();
	private int ROUND_EXPIRED = 0;
	private int ROUND_SUCCESS = 0;

	// ==========================================================================================================================================================
	//
	// �������ں���
	//
	// ==========================================================================================================================================================

	public Executor_roulette() throws Exception {
		super(MODULE_DISPLAYNAME, MODULE_PACKAGENAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_TRIGER, MODULE_PRIVACY_LISTEN, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
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
	public boolean doUserMessage(final int typeid, final long userid, final MessageUser message, final int messageid, final int messagefont) throws Exception {
		return true;
	}

	@Override
	public boolean doDiszMessage(final long diszid, final long userid, final MessageDisz message, final int messageid, final int messagefont) throws Exception {
		return true;
	}

	@Override
	public boolean doGropMessage(final long gropid, final long userid, final MessageGrop message, final int messageid, final int messagefont) throws Exception {
		if (message.getSection() < 2) {
			entry.getMessage().gropInfo(gropid, userid, "����ע��8koi��");
			return true;
		}
		if (!this.rounds.containsKey(gropid)) { this.rounds.put(gropid, new RouletteRound()); }
		final RouletteRound round = this.rounds.get(gropid);

		if (round.lock) {
			// �����������ģ������Ҿ���û����������100ms���ټ�������ɾ��
			// SX found this BUG
			// Module.gropInfo(gropid, "������ѵ����ˣ������費���㣬����������ӵ�����֡�");
			return false;
		} else {
			if (round.time.getTime() + 600000 < new Date().getTime()) {
				this.ROUND_EXPIRED++;
				this.rounds.remove(gropid);
				this.rounds.put(gropid, new RouletteRound());
			}
			if (round.join(gropid, userid, message)) {
				this.ROUND_SUCCESS++;
				final SecureRandom random = new SecureRandom();
				final int bullet = random.nextInt(6);
				entry.getMessage().gropInfo(gropid, "�����Ѵ��� װ���ӵ���");
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
				entry.getMessage().gropInfo(gropid, "@ƽ���й� Ŀ���ѻ���:  [CQ:at,qq=" + round.player.get(bullet) + "]\r\n" + round.chip.get(bullet));
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

		public boolean join(final long gropid, final long userid, final Message message) {
			if (this.player.contains(userid)) {
				entry.getMessage().gropInfo(gropid, "��8koi�뿪����׼�Ź�");
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
				entry.getMessage().gropInfo(gropid, buffer.toString());
			}
			this.lock = this.players > 5 ? true : false;
			return this.lock;
		}
	}

	@Override
	public String[] generateReport(int mode, final Message message, final Object... parameters) {
		if (this.COUNT == 0) { return null; }
		final StringBuilder builder = new StringBuilder();
		builder.append("�ɹ��غ� : ");
		builder.append(this.ROUND_SUCCESS);
		builder.append("\r\nʧ�ܻغ� : ");
		builder.append(this.ROUND_EXPIRED);
		if (this.ROUND_SUCCESS > 0) {
			builder.append("\r\n��һ�� : ");
			builder.append(this.roulette.get(0));
			builder.append(" - ");
			builder.append(this.roulette.get(0) * 100 / this.ROUND_SUCCESS);
			builder.append("%\r\n�ڶ��� : ");
			builder.append(this.roulette.get(1));
			builder.append(" - ");
			builder.append(this.roulette.get(1) * 100 / this.ROUND_SUCCESS);
			builder.append("%\r\n������ : ");
			builder.append(this.roulette.get(2));
			builder.append(" - ");
			builder.append(this.roulette.get(2) * 100 / this.ROUND_SUCCESS);
			builder.append("%\r\n���ķ� : ");
			builder.append(this.roulette.get(3));
			builder.append(" - ");
			builder.append(this.roulette.get(3) * 100 / this.ROUND_SUCCESS);
			builder.append("%\r\n���巢 : ");
			builder.append(this.roulette.get(4));
			builder.append(" - ");
			builder.append(this.roulette.get(4) * 100 / this.ROUND_SUCCESS);
			builder.append("%\r\n������ : ");
			builder.append(this.roulette.get(5));
			builder.append(" - ");
			builder.append(this.roulette.get(5) * 100 / this.ROUND_SUCCESS);
			builder.append("%");
		}
		final String res[] = new String[1];
		res[0] = builder.toString();
		return res;
	}
}
