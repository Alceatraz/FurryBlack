package studio.blacktech.coolqbot.furryblack.modules;

import java.math.BigInteger;
import java.util.HashMap;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;

public class Executor_acon extends ModuleExecutor {

	// ==========================================================================================================================================================
	//
	// ģ���������
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "acon";
	private static String MODULE_DISPLAYNAME = "�յ�";
	private static String MODULE_DESCRIPTION = "��Ⱥ��������";
	private static String MODULE_VERSION = "3.0";
	private static String[] MODULE_USAGE = new String[] {
			"/acon cost - �ĵ���",
			"/acon off - �ػ�",
			"/acon wet - ��ʪ",
			"/acon dry - ��ʪ",
			"/acon cold - �Ʊ�ģʽ",
			"/acon cool - ����ģʽ",
			"/acon warn - ����ģʽ",
			"/acon bake - �濾ģʽ",
			"/acon burn - �տ�ģʽ",
			"/acon fire - �ٻ�ģʽ",
			"/acon c2h2 - ��Ȳ��ģʽ",
			"/acon argon - �������ģʽ",
			"/acon plasma - ������ģʽ",
			"/acon nova - ����һ������",
			"/acon cfnuke - ��ȼһ���������",
			"/acon trnuke - ��ȼһ���Ⱥ�����",
			"/acon tpnuke - ��ȼһ�������Ⱥ˵�",
			"/acon ianova - Ia��������������ȼ",
			"/acon ibnova - Ib��������������ȼ",
			"/acon icnova - Ic��������������ȼ",
			"/acon iinova - II��������������ȼ",
			"/acon ~!C??? - Fy:????"
	};
	private static String[] MODULE_PRIVACY_TRIGER = new String[] {};
	private static String[] MODULE_PRIVACY_LISTEN = new String[] {};
	private static String[] MODULE_PRIVACY_STORED = new String[] {};
	private static String[] MODULE_PRIVACY_CACHED = new String[] {
			"��Ⱥ�洢�ĵ���",
			"��Ⱥ�洢�Ĺ���ģʽ",
			"��Ⱥ�洢�ϴθ���ģʽ��ʱ���",
	};
	public static String[] MODULE_PRIVACY_OBTAIN = new String[] {};

	// ==========================================================================================================================================================
	//
	// ��Ա����
	//
	// ==========================================================================================================================================================

	private HashMap<Long, BigInteger> CONSUMPTION;
	private HashMap<Long, Long> WORKINGMODE;
	private HashMap<Long, Long> LASTCHANGED;

	// ==========================================================================================================================================================
	//
	// �������ں���
	//
	// ==========================================================================================================================================================

	public Executor_acon() throws Exception {
		super(MODULE_DISPLAYNAME, MODULE_PACKAGENAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_TRIGER, MODULE_PRIVACY_LISTEN, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	@Override
	public void init(LoggerX logger) throws Exception {

		this.CONSUMPTION = new HashMap<>();
		this.WORKINGMODE = new HashMap<>();
		this.LASTCHANGED = new HashMap<>();

		this.ENABLE_USER = false;
		this.ENABLE_DISZ = false;
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

		final long current = System.currentTimeMillis() / 1000;
		long elapse = 0L;

		if (!this.CONSUMPTION.containsKey(gropid)) {
			this.CONSUMPTION.put(gropid, BigInteger.ZERO);
			this.WORKINGMODE.put(gropid, 0L);
			this.LASTCHANGED.put(gropid, current);
			elapse = 0;
		}

		if (message.getSection() == 2) {

			message.parseOption();

			BigInteger consumption = this.CONSUMPTION.get(gropid);
			final long workingmode = this.WORKINGMODE.get(gropid);
			final long lastchanged = this.LASTCHANGED.get(gropid);

			elapse = current - lastchanged;

			switch (message.getSegment()[0]) {

			case "off":
				entry.getMessage().gropInfo(gropid, "�յ��ѹر�");
				this.WORKINGMODE.put(gropid, 1L);
				break;

			case "dry":
				entry.getMessage().gropInfo(gropid, "�л�����ʪģʽ");
				this.WORKINGMODE.put(gropid, 5880L);
				break;

			case "wet":
				entry.getMessage().gropInfo(gropid, "�л�����ʪģʽ");
				this.WORKINGMODE.put(gropid, 5880L);
				break;

			case "cold":
				entry.getMessage().gropInfo(gropid, "�л����Ʊ�ģʽ -20��");
				this.WORKINGMODE.put(gropid, 14700L);
				break;

			case "cool":
				entry.getMessage().gropInfo(gropid, "�л�������ģʽ 20��");
				this.WORKINGMODE.put(gropid, 7350L);
				break;

			case "warn":
				entry.getMessage().gropInfo(gropid, "�л�������ģʽ 23��");
				this.WORKINGMODE.put(gropid, 7350L);
				break;

			case "bake":
				entry.getMessage().gropInfo(gropid, "�л����濾ģʽ 285��");
				this.WORKINGMODE.put(gropid, 14700L);
				break;

			case "burn":
				entry.getMessage().gropInfo(gropid, "�л����տ�ģʽ 960��");
				this.WORKINGMODE.put(gropid, 22050L);
				break;

			case "fire":
				entry.getMessage().gropInfo(gropid, "�л����ٻ�ģʽ 1,200��");
				this.WORKINGMODE.put(gropid, 29400L);
				break;

			case "c2h2":
				entry.getMessage().gropInfo(gropid, "�л�����Ȳ��ģʽ 3,300��");
				this.WORKINGMODE.put(gropid, 33075L);
				break;

			case "argon":
				entry.getMessage().gropInfo(gropid, "�л��������ģʽ 7,550��");
				this.WORKINGMODE.put(gropid, 36750L);
				break;

			case "plasma":
				entry.getMessage().gropInfo(gropid, "�л���������ģʽ 23,500��");
				this.WORKINGMODE.put(gropid, 44100L);
				break;

			case "nova":
				entry.getMessage().gropInfo(gropid, "�л�������ģʽ 1,000,000��");
				this.WORKINGMODE.put(gropid, 7350000L);
				break;

			case "cfnuke":
				entry.getMessage().gropInfo(gropid, "�л������ģʽ 100,000,000��");
				this.WORKINGMODE.put(gropid, 29400000L);
				break;

			case "trnuke":
				entry.getMessage().gropInfo(gropid, "�л����Ⱥ�ģʽ 120,000,000��");
				this.WORKINGMODE.put(gropid, 33075000L);
				break;

			case "tfnuke":
				entry.getMessage().gropInfo(gropid, "�л��������Ⱥ�ģʽ 150,000,000��");
				this.WORKINGMODE.put(gropid, 44100000L);
				break;

			case "ianova":
				entry.getMessage().gropInfo(gropid, "�л���Ia�Ǳ���ģʽ 800,000,000��");
				this.WORKINGMODE.put(gropid, 294000000L);
				break;

			case "ibnova":
				entry.getMessage().gropInfo(gropid, "�л���Ib���Ǳ���ģʽ 2,600,000,000��");
				this.WORKINGMODE.put(gropid, 330750000L);
				break;

			case "icnova":
				entry.getMessage().gropInfo(gropid, "�л���Ic���Ǳ���ģʽ 2,800,000,000��");
				this.WORKINGMODE.put(gropid, 441000000L);
				break;

			case "iinova":
				entry.getMessage().gropInfo(gropid, "�л���II���Ǳ���ģʽ 3,000,000,000��");
				this.WORKINGMODE.put(gropid, 514500000L);
				break;

			case "samrage":
				entry.getMessage().gropInfo(gropid, "����֮ŭ 10,000,000,000,000,000,000,000,000,000��");
				this.WORKINGMODE.put(gropid, 73500000000L);
				break;

			case "cost":
				// @formatter:off
				entry.getMessage().gropInfo(gropid,
						"�ۼƹ��ĵ磺" +
						String.format("%.2f", consumption.divide(BigInteger.valueOf(1000))) + "kW(" +
						String.format("%.2f", consumption.divide(BigInteger.valueOf(3600000L))) + ")��\r\nȺ����֧����" +
						String.format("%.2f", consumption.divide(BigInteger.valueOf(1936800L))) + "Ԫ"
				);
				// @formatter:on
				break;

			default:
				entry.getMessage().gropInfo(gropid, userid, "�������Է�����");
				break;
			}

			consumption = consumption.add(BigInteger.valueOf(elapse * workingmode));

			this.CONSUMPTION.put(gropid, consumption);
			this.LASTCHANGED.put(gropid, current);

		}

		return true;
	}

	// ==========================================================================================================================================================
	//
	// ���ߺ���
	//
	// ==========================================================================================================================================================

	@Override
	public String[] generateReport(int mode, final Message message, final Object... parameters) {
		return null;
	}

}