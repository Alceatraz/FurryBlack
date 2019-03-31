package studio.blacktech.coolqbot.furryblack.module;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.TreeMap;

import studio.blacktech.coolqbot.furryblack.signal.Workflow;

public class Module_zhan extends FunctionModuel {

	private static TreeMap<Integer, String> CARD = new TreeMap<Integer, String>();
	private static ArrayList<Integer> FREQ = new ArrayList<Integer>();

	public Module_zhan() {
		this.MODULE_NAME = "������ռ��";
		this.MODULE_HELP = "//zhan - ��ȡһ�Ŵ󰢿�����\r\n//zhan Ŀ�� - Ϊĳ��ռ��";
		this.MODULE_COMMAND = "zhan";
		this.MODULE_VERSION = "1.2.2";
		this.MODULE_DESCRIPTION = "�󰢿���������ռ��";
		this.MODULE_PRIVACY = "�洢 : ��\r\n���� : ��\r\n��ȡ : 1\r\n1: �����������@";

		Module_zhan.CARD.put(1, "O. THE FOOL ������λ\r\n�޴� ���� �ӻ��޶� ��־����");
		Module_zhan.CARD.put(2, "O. THE FOOL ������λ\r\n��� ȱ�� ĺ�� ��Ч ����");
		Module_zhan.CARD.put(3, "I. THE MAGICIAN ħ��ʦ��λ\r\n�ֶ� ���� ʹ�� ��ʧ");
		Module_zhan.CARD.put(4, "I. THE MAGICIAN ħ��ʦ��λ\r\n���� ���� ���񼲲�");
		Module_zhan.CARD.put(5, "II. THE HIGH PRIESTESS Ů��˾��λ\r\n���� ���� δ�������� Ӣ��");
		Module_zhan.CARD.put(6, "II. THE HIGH PRIESTESS Ů��˾��λ\r\n�嶯 ���� �Ը� ���ڱ���");
		Module_zhan.CARD.put(7, "III. THE EMPRESS �ʺ���λ\r\n���� ���� ���� ���� ��֪");
		Module_zhan.CARD.put(8, "III. THE EMPRESS �ʺ���λ\r\n���� ���� ϲ��");
		Module_zhan.CARD.put(9, "IV. THE EMPEROR �ʵ���λ\r\n�ȶ� ���� ���� ���� ����");
		Module_zhan.CARD.put(10, "IV. THE EMPEROR �ʵ���λ\r\n�ʴ� ͬ�� ���� �谭 ������");
		Module_zhan.CARD.put(11, "V. THE HIEROPHANT �̻���λ\r\n��ˡ ���� ū�� ���");
		Module_zhan.CARD.put(12, "V. THE HIEROPHANT �̻���λ\r\n�ƽ����� ���� �������� ����");
		Module_zhan.CARD.put(13, "VI. THE LOVERS ������λ\r\n���� �� ���� ͨ������");
		Module_zhan.CARD.put(14, "VI. THE LOVERS ������λ\r\nʧ�� �޴������");
		Module_zhan.CARD.put(15, "VII. THE CHARIOT ս����λ\r\n���� ���� ʤ�� ����");
		Module_zhan.CARD.put(16, "VII. THE CHARIOT ս����λ\r\n��� �� ���� ����");
		Module_zhan.CARD.put(17, "VIII. THE STRENGTH ������λ\r\n���� �ж� ���� ����");
		Module_zhan.CARD.put(18, "VIII. THE STRENGTH ������λ\r\nר�� ���� �������� ����");
		Module_zhan.CARD.put(19, "IX. THE HERMIT ������λ\r\n���� ��ͽ ���� ���� ����");
		Module_zhan.CARD.put(20, "IX. THE HERMIT ������λ\r\n���� ���� αװ ����С��");
		Module_zhan.CARD.put(21, "X. THE WHEEL OF FORTUNE ����֮����λ\r\n���� ���� �ɹ� �Ҹ�");
		Module_zhan.CARD.put(22, "X. THE WHEEL OF FORTUNE ����֮����λ\r\n���� �ḻ ����");
		Module_zhan.CARD.put(23, "XI. THE JUSTICE ������λ\r\n��ƽ ���� ���� ����");
		Module_zhan.CARD.put(24, "XI. THE JUSTICE ������λ\r\nƫִ ���� ���ȼ���");
		Module_zhan.CARD.put(25, "XII. THE HANGED MAN ������λ\r\n�ǻ� ���� ���� ϸ�� �۹�");
		Module_zhan.CARD.put(26, "XII. THE HANGED MAN ������λ\r\n��˽ Ⱥ�� ����");
		Module_zhan.CARD.put(27, "XIII. DEATH ������λ\r\n�ս� ���� ���� ����");
		Module_zhan.CARD.put(28, "XIII. DEATH ������λ\r\n���� ��˯ ʯ�� ����");
		Module_zhan.CARD.put(29, "XIV. TEMPERANCE ������λ\r\n���� �ʶ� �ڼ� ���� ס��");
		Module_zhan.CARD.put(30, "XIV. TEMPERANCE ������λ\r\n�̻� ���� ���ҵ���� ��ͻ������");
		Module_zhan.CARD.put(31, "XV. THE DEVIL ��ħ��λ\r\n�ٻ� ���� ǿ�� ��ŭ ����Ŭ�� ����");
		Module_zhan.CARD.put(32, "XV. THE DEVIL ��ħ��λ\r\n���� ���� äĿ ����");
		Module_zhan.CARD.put(33, "XVI. THE TOWER ������λ\r\n���� ���� ƶ�� ���� �ֺ� �澳 ƭ��");
		Module_zhan.CARD.put(34, "XVI. THE TOWER ������λ\r\nר�� ��� �ܿ� ��");
		Module_zhan.CARD.put(35, "XVII. THE STAR ������λ\r\n��ʧ ���� �ѷ� ���� δ����ϣ��");
		Module_zhan.CARD.put(36, "XVII. THE STAR ������λ\r\n���� ���� ����");
		Module_zhan.CARD.put(37, "XVIII. THE MOON ������λ\r\n���صĵ��� �̰� Σ�� �ڰ� �ֲ� ����");
		Module_zhan.CARD.put(38, "XVIII. THE MOON ������λ\r\n���ȶ� �ױ� ƭ�� ����");
		Module_zhan.CARD.put(39, "XIX. THE SUN ̫����λ\r\nϲ�� ��� ����");
		Module_zhan.CARD.put(40, "XIX. THE SUN ̫����λ\r\n���� ����");
		Module_zhan.CARD.put(41, "XX. THE LAST JUDGMENT ������λ\r\n��λ ���� ���");
		Module_zhan.CARD.put(42, "XX. THE LAST JUDGMENT ������λ\r\n���� ���� ���� ���� ����");
		Module_zhan.CARD.put(43, "XXI. THE WORLD ������λ\r\n�ɹ� ��· ���� ��λ");
		Module_zhan.CARD.put(44, "XXI. THE WORLD ������λ\r\n���� ��ִ ͣ�� �־�");
		// Ϊʲô��дѭ���� ��Ϊ���п�
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
		Module_zhan.FREQ.add(0);
	}

	@Override
	public void excute(final Workflow flow) {
		this.counter++;
		final SecureRandom random = new SecureRandom();
		final int urandom = random.nextInt(43) + 1;
		final StringBuilder builder = new StringBuilder();
		builder.append("����Ϊ");
		builder.append(flow.join(1));
		builder.append(" �鵽��\r\n");
		builder.append(Module_zhan.CARD.get(urandom));
		Module_zhan.FREQ.set(urandom, Module_zhan.FREQ.get(urandom) + 1);
		switch (flow.from) {
		case 1:
			FunctionModuel.priInfo(flow, builder.toString());
			break;
		case 2:
			FunctionModuel.disInfo(flow, builder.toString());
			break;
		case 3:
			FunctionModuel.grpInfo(flow, builder.toString());
			break;
		}
	}

	@Override
	public String genReport() {
		if (this.counter == 0) {
			return null;
		}
		final StringBuilder builder = new StringBuilder();
		int coverage = 0;
		for (int i = 0; i < 44; i++) {
			if (Module_zhan.FREQ.get(i) == 0) {
				coverage++;
			}
		}
		coverage = 44 - coverage;
		for (int i = 0; i < 44; i++) {
			if (Module_zhan.FREQ.get(i) == 0) {
				continue;
			}
			builder.append("\r\n�� ");
			builder.append(i + 1);
			builder.append(" �� : ");
			builder.append(Module_zhan.FREQ.get(i));
			builder.append(" - ");
			builder.append((Module_zhan.FREQ.get(i) * 100) / coverage);
			builder.append("%");
		}
		return builder.toString();
	}
}
