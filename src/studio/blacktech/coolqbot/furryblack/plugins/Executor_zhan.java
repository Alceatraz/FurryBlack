package studio.blacktech.coolqbot.furryblack.plugins;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.TreeMap;

import studio.blacktech.coolqbot.furryblack.module.Message;
import studio.blacktech.coolqbot.furryblack.module.Module;
import studio.blacktech.coolqbot.furryblack.module.ModuleExecutor;

public class Executor_zhan extends ModuleExecutor {

	private static TreeMap<Integer, String> CARD = new TreeMap<Integer, String>();
	private static ArrayList<Integer> FREQ = new ArrayList<Integer>();

	public Executor_zhan() {

		this.MODULE_DISPLAYNAME = "������ռ��";
		this.MODULE_PACKAGENAME = "zhan";
		this.MODULE_DESCRIPTION = "�󰢿���������ռ��";
		this.MODULE_VERSION = "2.2.3";
		this.MODULE_USAGE = new String[] {
				"//zhan ����"
		};
		this.MODULE_PRIVACY_TRIGER = new String[] {};
		this.MODULE_PRIVACY_LISTEN = new String[] {};
		this.MODULE_PRIVACY_STORED = new String[] {};
		this.MODULE_PRIVACY_CACHED = new String[] {};
		this.MODULE_PRIVACY_OBTAIN = new String[] {
				"��ȡ�������"
		};

		Executor_zhan.CARD.put(1, "O. THE FOOL ������λ\r\n�޴� ���� �ӻ��޶� ��־����");
		Executor_zhan.CARD.put(2, "O. THE FOOL ������λ\r\n��� ȱ�� ĺ�� ��Ч ����");
		Executor_zhan.CARD.put(3, "I. THE MAGICIAN ħ��ʦ��λ\r\n�ֶ� ���� ʹ�� ��ʧ");
		Executor_zhan.CARD.put(4, "I. THE MAGICIAN ħ��ʦ��λ\r\n���� ���� ���񼲲�");
		Executor_zhan.CARD.put(5, "II. THE HIGH PRIESTESS Ů��˾��λ\r\n���� ���� δ�������� Ӣ��");
		Executor_zhan.CARD.put(6, "II. THE HIGH PRIESTESS Ů��˾��λ\r\n�嶯 ���� �Ը� ���ڱ���");
		Executor_zhan.CARD.put(7, "III. THE EMPRESS �ʺ���λ\r\n���� ���� ���� ���� ��֪");
		Executor_zhan.CARD.put(8, "III. THE EMPRESS �ʺ���λ\r\n���� ���� ϲ��");
		Executor_zhan.CARD.put(9, "IV. THE EMPEROR �ʵ���λ\r\n�ȶ� ���� ���� ���� ����");
		Executor_zhan.CARD.put(10, "IV. THE EMPEROR �ʵ���λ\r\n�ʴ� ͬ�� ���� �谭 ������");
		Executor_zhan.CARD.put(11, "V. THE HIEROPHANT �̻���λ\r\n��ˡ ���� ū�� ���");
		Executor_zhan.CARD.put(12, "V. THE HIEROPHANT �̻���λ\r\n�ƽ����� ���� �������� ����");
		Executor_zhan.CARD.put(13, "VI. THE LOVERS ������λ\r\n���� �� ���� ͨ������");
		Executor_zhan.CARD.put(14, "VI. THE LOVERS ������λ\r\nʧ�� �޴������");
		Executor_zhan.CARD.put(15, "VII. THE CHARIOT ս����λ\r\n���� ���� ʤ�� ����");
		Executor_zhan.CARD.put(16, "VII. THE CHARIOT ս����λ\r\n��� �� ���� ����");
		Executor_zhan.CARD.put(17, "VIII. THE STRENGTH ������λ\r\n���� �ж� ���� ����");
		Executor_zhan.CARD.put(18, "VIII. THE STRENGTH ������λ\r\nר�� ���� �������� ����");
		Executor_zhan.CARD.put(19, "IX. THE HERMIT ������λ\r\n���� ��ͽ ���� ���� ����");
		Executor_zhan.CARD.put(20, "IX. THE HERMIT ������λ\r\n���� ���� αװ ����С��");
		Executor_zhan.CARD.put(21, "X. THE WHEEL OF FORTUNE ����֮����λ\r\n���� ���� �ɹ� �Ҹ�");
		Executor_zhan.CARD.put(22, "X. THE WHEEL OF FORTUNE ����֮����λ\r\n���� �ḻ ����");
		Executor_zhan.CARD.put(23, "XI. THE JUSTICE ������λ\r\n��ƽ ���� ���� ����");
		Executor_zhan.CARD.put(24, "XI. THE JUSTICE ������λ\r\nƫִ ���� ���ȼ���");
		Executor_zhan.CARD.put(25, "XII. THE HANGED MAN ������λ\r\n�ǻ� ���� ���� ϸ�� �۹�");
		Executor_zhan.CARD.put(26, "XII. THE HANGED MAN ������λ\r\n��˽ Ⱥ�� ����");
		Executor_zhan.CARD.put(27, "XIII. DEATH ������λ\r\n�ս� ���� ���� ����");
		Executor_zhan.CARD.put(28, "XIII. DEATH ������λ\r\n���� ��˯ ʯ�� ����");
		Executor_zhan.CARD.put(29, "XIV. TEMPERANCE ������λ\r\n���� �ʶ� �ڼ� ���� ס��");
		Executor_zhan.CARD.put(30, "XIV. TEMPERANCE ������λ\r\n�̻� ���� ���ҵ���� ��ͻ������");
		Executor_zhan.CARD.put(31, "XV. THE DEVIL ��ħ��λ\r\n�ٻ� ���� ǿ�� ��ŭ ����Ŭ�� ����");
		Executor_zhan.CARD.put(32, "XV. THE DEVIL ��ħ��λ\r\n���� ���� äĿ ����");
		Executor_zhan.CARD.put(33, "XVI. THE TOWER ������λ\r\n���� ���� ƶ�� ���� �ֺ� �澳 ƭ��");
		Executor_zhan.CARD.put(34, "XVI. THE TOWER ������λ\r\nר�� ��� �ܿ� ��");
		Executor_zhan.CARD.put(35, "XVII. THE STAR ������λ\r\n��ʧ ���� �ѷ� ���� δ����ϣ��");
		Executor_zhan.CARD.put(36, "XVII. THE STAR ������λ\r\n���� ���� ����");
		Executor_zhan.CARD.put(37, "XVIII. THE MOON ������λ\r\n���صĵ��� �̰� Σ�� �ڰ� �ֲ� ����");
		Executor_zhan.CARD.put(38, "XVIII. THE MOON ������λ\r\n���ȶ� �ױ� ƭ�� ����");
		Executor_zhan.CARD.put(39, "XIX. THE SUN ̫����λ\r\nϲ�� ��� ����");
		Executor_zhan.CARD.put(40, "XIX. THE SUN ̫����λ\r\n���� ����");
		Executor_zhan.CARD.put(41, "XX. THE LAST JUDGMENT ������λ\r\n��λ ���� ���");
		Executor_zhan.CARD.put(42, "XX. THE LAST JUDGMENT ������λ\r\n���� ���� ���� ���� ����");
		Executor_zhan.CARD.put(43, "XXI. THE WORLD ������λ\r\n�ɹ� ��· ���� ��λ");
		Executor_zhan.CARD.put(44, "XXI. THE WORLD ������λ\r\n���� ��ִ ͣ�� �־�");
		// Ϊʲô��дѭ���� ��Ϊ���п�
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
	}

	@Override
	public boolean doUserMessage(int typeid, long userid, Message message, int messageid, int messagefont) throws Exception {
		Module.userInfo(userid, this.zhan(message));
		return true;
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, Message message, int messageid, int messagefont) throws Exception {
		Module.diszInfo(diszid, userid, this.zhan(message));
		return true;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, Message message, int messageid, int messagefont) throws Exception {
		Module.gropInfo(gropid, userid, this.zhan(message));
		return true;
	}

	public String zhan(Message message) {
		if (message.length == 1) {
			return "�㲻��ռ������";
		} else {
			SecureRandom random = new SecureRandom();
			int urandom = random.nextInt(43) + 1;
			StringBuilder builder = new StringBuilder();
			builder.append("����Ϊ ");
			builder.append(message.join(1));
			builder.append(" �鵽�ˣ�\r\n");
			builder.append(Executor_zhan.CARD.get(urandom));
			Executor_zhan.FREQ.set(urandom, Executor_zhan.FREQ.get(urandom) + 1);
			return builder.toString();
		}
	}

	@Override
	public String getReport() {
		if (this.COUNT == 0) {
			return null;
		}
		final StringBuilder builder = new StringBuilder();
		int coverage = 0;
		for (int i = 0; i < 44; i++) {
			if (Executor_zhan.FREQ.get(i) == 0) {
				coverage++;
			}
		}
		coverage = 44 - coverage;
		for (int i = 0; i < 44; i++) {
			if (Executor_zhan.FREQ.get(i) == 0) {
				continue;
			}
			builder.append("\r\n�� ");
			builder.append(i + 1);
			builder.append(" �� : ");
			builder.append(Executor_zhan.FREQ.get(i));
			builder.append(" - ");
			builder.append((Executor_zhan.FREQ.get(i) * 100) / coverage);
			builder.append("%");
		}
		return builder.toString();
	}
}
