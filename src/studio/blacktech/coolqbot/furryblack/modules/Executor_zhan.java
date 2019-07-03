package studio.blacktech.coolqbot.furryblack.modules;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.TreeMap;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;

public class Executor_zhan extends ModuleExecutor {

	// ==========================================================================================================================================================
	//
	// ģ���������
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "zhan";
	private static String MODULE_DISPLAYNAME = "ռ��";
	private static String MODULE_DESCRIPTION = "�󰢿���������ռ��";
	private static String MODULE_VERSION = "1.0";
	private static String[] MODULE_USAGE = new String[] {
			"/zhan ���� - Ϊĳ��ռ��"
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

	private final TreeMap<Integer, String> CARD = new TreeMap<>();
	private final ArrayList<Integer> FREQ = new ArrayList<>();

	// ==========================================================================================================================================================
	//
	// �������ں���
	//
	// ==========================================================================================================================================================

	public Executor_zhan() throws Exception {
		super(MODULE_DISPLAYNAME, MODULE_PACKAGENAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_TRIGER, MODULE_PRIVACY_LISTEN, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	@Override
	public void init(LoggerX logger) throws Exception {

		this.CARD.put(1, "O. THE FOOL ������λ\r\n�޴� ���� �ӻ��޶� ��־����");
		this.CARD.put(2, "O. THE FOOL ������λ\r\n��� ȱ�� ĺ�� ��Ч ����");
		this.CARD.put(3, "I. THE MAGICIAN ħ��ʦ��λ\r\n�ֶ� ���� ʹ�� ��ʧ");
		this.CARD.put(4, "I. THE MAGICIAN ħ��ʦ��λ\r\n���� ���� ���񼲲�");
		this.CARD.put(5, "II. THE HIGH PRIESTESS Ů��˾��λ\r\n���� ���� δ�������� Ӣ��");
		this.CARD.put(6, "II. THE HIGH PRIESTESS Ů��˾��λ\r\n�嶯 ���� �Ը� ���ڱ���");
		this.CARD.put(7, "III. THE EMPRESS �ʺ���λ\r\n���� ���� ���� ���� ��֪");
		this.CARD.put(8, "III. THE EMPRESS �ʺ���λ\r\n���� ���� ϲ��");
		this.CARD.put(9, "IV. THE EMPEROR �ʵ���λ\r\n�ȶ� ���� ���� ���� ����");
		this.CARD.put(10, "IV. THE EMPEROR �ʵ���λ\r\n�ʴ� ͬ�� ���� �谭 ������");
		this.CARD.put(11, "V. THE HIEROPHANT �̻���λ\r\n��ˡ ���� ū�� ���");
		this.CARD.put(12, "V. THE HIEROPHANT �̻���λ\r\n�ƽ����� ���� �������� ����");
		this.CARD.put(13, "VI. THE LOVERS ������λ\r\n���� �� ���� ͨ������");
		this.CARD.put(14, "VI. THE LOVERS ������λ\r\nʧ�� �޴������");
		this.CARD.put(15, "VII. THE CHARIOT ս����λ\r\n���� ���� ʤ�� ����");
		this.CARD.put(16, "VII. THE CHARIOT ս����λ\r\n��� �� ���� ����");
		this.CARD.put(17, "VIII. THE STRENGTH ������λ\r\n���� �ж� ���� ����");
		this.CARD.put(18, "VIII. THE STRENGTH ������λ\r\nר�� ���� �������� ����");
		this.CARD.put(19, "IX. THE HERMIT ������λ\r\n���� ��ͽ ���� ���� ����");
		this.CARD.put(20, "IX. THE HERMIT ������λ\r\n���� ���� αװ ����С��");
		this.CARD.put(21, "X. THE WHEEL OF FORTUNE ����֮����λ\r\n���� ���� �ɹ� �Ҹ�");
		this.CARD.put(22, "X. THE WHEEL OF FORTUNE ����֮����λ\r\n���� �ḻ ����");
		this.CARD.put(23, "XI. THE JUSTICE ������λ\r\n��ƽ ���� ���� ����");
		this.CARD.put(24, "XI. THE JUSTICE ������λ\r\nƫִ ���� ���ȼ���");
		this.CARD.put(25, "XII. THE HANGED MAN ������λ\r\n�ǻ� ���� ���� ϸ�� �۹�");
		this.CARD.put(26, "XII. THE HANGED MAN ������λ\r\n��˽ Ⱥ�� ����");
		this.CARD.put(27, "XIII. DEATH ������λ\r\n�ս� ���� ���� ����");
		this.CARD.put(28, "XIII. DEATH ������λ\r\n���� ʯ�� ���� �� ˯");
		this.CARD.put(29, "XIV. TEMPERANCE ������λ\r\n���� �ʶ� �ڼ� ���� ס��");
		this.CARD.put(30, "XIV. TEMPERANCE ������λ\r\n�̻� ���� ���ҵ���� ��ͻ������");
		this.CARD.put(31, "XV. THE DEVIL ��ħ��λ\r\n�ٻ� ���� ǿ�� ��ŭ ����Ŭ�� ����");
		this.CARD.put(32, "XV. THE DEVIL ��ħ��λ\r\n���� ���� äĿ ����");
		this.CARD.put(33, "XVI. THE TOWER ������λ\r\n���� ���� ƶ�� ���� �ֺ� �澳 ƭ��");
		this.CARD.put(34, "XVI. THE TOWER ������λ\r\nר�� ��� �ܿ� ��");
		this.CARD.put(35, "XVII. THE STAR ������λ\r\n��ʧ ���� �ѷ� ���� δ����ϣ��");
		this.CARD.put(36, "XVII. THE STAR ������λ\r\n���� ���� ����");
		this.CARD.put(37, "XVIII. THE MOON ������λ\r\n���صĵ��� �̰� Σ�� �ڰ� �ֲ� ����");
		this.CARD.put(38, "XVIII. THE MOON ������λ\r\n���ȶ� �ױ� ƭ�� ����");
		this.CARD.put(39, "XIX. THE SUN ̫����λ\r\nϲ�� ��� ����");
		this.CARD.put(40, "XIX. THE SUN ̫����λ\r\n���� ����");
		this.CARD.put(41, "XX. THE LAST JUDGMENT ������λ\r\n��λ ���� ���");
		this.CARD.put(42, "XX. THE LAST JUDGMENT ������λ\r\n���� ���� ���� ���� ����");
		this.CARD.put(43, "XXI. THE WORLD ������λ\r\n�ɹ� ��· ���� ��λ");
		this.CARD.put(44, "XXI. THE WORLD ������λ\r\n���� ��ִ ͣ�� �־�");

		// Ϊʲô��дѭ���� ��Ϊ���п�
		// @formatter:off
		this.FREQ.add(0);this.FREQ.add(0);this.FREQ.add(0);
		this.FREQ.add(0);this.FREQ.add(0);this.FREQ.add(0);
		this.FREQ.add(0);this.FREQ.add(0);this.FREQ.add(0);
		this.FREQ.add(0);this.FREQ.add(0);this.FREQ.add(0);
		this.FREQ.add(0);this.FREQ.add(0);this.FREQ.add(0);
		this.FREQ.add(0);this.FREQ.add(0);this.FREQ.add(0);
		this.FREQ.add(0);this.FREQ.add(0);this.FREQ.add(0);
		this.FREQ.add(0);this.FREQ.add(0);this.FREQ.add(0);
		this.FREQ.add(0);this.FREQ.add(0);this.FREQ.add(0);
		this.FREQ.add(0);this.FREQ.add(0);this.FREQ.add(0);
		this.FREQ.add(0);this.FREQ.add(0);this.FREQ.add(0);
		this.FREQ.add(0);this.FREQ.add(0);this.FREQ.add(0);
		this.FREQ.add(0);this.FREQ.add(0);this.FREQ.add(0);
		this.FREQ.add(0);this.FREQ.add(0);this.FREQ.add(0);
		this.FREQ.add(0);this.FREQ.add(0);
		// @formatter:on

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
		entry.getMessage().userInfo(userid, this.zhan(message));
		return true;
	}

	@Override
	public boolean doDiszMessage(final long diszid, final long userid, final MessageDisz message, final int messageid, final int messagefont) throws Exception {
		entry.getMessage().diszInfo(diszid, userid, this.zhan(message));
		return true;
	}

	@Override
	public boolean doGropMessage(final long gropid, final long userid, final MessageGrop message, final int messageid, final int messagefont) throws Exception {
		entry.getMessage().gropInfo(gropid, userid, this.zhan(message));
		return true;
	}

	public String zhan(final Message message) {
		if (message.getSection() == 1) {
			return "�㲻��ռ������";
		} else {
			final SecureRandom random = new SecureRandom();
			final int urandom = random.nextInt(43) + 1;
			final StringBuilder builder = new StringBuilder();
			builder.append("����Ϊ ");
			builder.append(message.getOptions());
			builder.append(" �鵽�ˣ�\r\n");
			builder.append(this.CARD.get(urandom));
			this.FREQ.set(urandom, this.FREQ.get(urandom) + 1);
			return builder.toString();
		}
	}

	@Override
	public String[] generateReport(int mode, final Message message, final Object... parameters) {
		if (this.COUNT == 0) { return null; }
		final StringBuilder builder = new StringBuilder();
		int coverage = 0;
		for (int i = 0; i < 44; i++) {
			if (this.FREQ.get(i) == 0) { coverage++; }
		}
		coverage = 44 - coverage;
		for (int i = 0; i < 44; i++) {
			if (this.FREQ.get(i) == 0) { continue; }
			builder.append("\r\n�� ");
			builder.append(i + 1);
			builder.append(" �� : ");
			builder.append(this.FREQ.get(i));
			builder.append(" - ");
			builder.append(this.FREQ.get(i) * 100 / coverage);
			builder.append("%");
		}
		String res[] = new String[1];
		res[0] = builder.toString();
		return res;
	}

}
