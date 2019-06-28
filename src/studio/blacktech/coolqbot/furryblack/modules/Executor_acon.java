package studio.blacktech.coolqbot.furryblack.modules;

import java.util.HashMap;

import studio.blacktech.coolqbot.furryblack.common.Message;
import studio.blacktech.coolqbot.furryblack.common.Module;
import studio.blacktech.coolqbot.furryblack.common.ModuleExecutor;

public class Executor_acon extends ModuleExecutor {

	private final HashMap<Long, Long> CONSUMPTION;
	private final HashMap<Long, Long> WORKINGMODE;
	private final HashMap<Long, Long> LASTCHANGED;

	public Executor_acon() {
		this.MODULE_PACKAGENAME = "acon";
		this.MODULE_DISPLAYNAME = "�յ�";
		this.MODULE_DESCRIPTION = "��Ⱥ��������";
		this.MODULE_VERSION = "1.0.0";
		this.MODULE_USAGE = new String[] {
				"//acon off - �ػ�",
				"//acon dry - ��ʪ",
				"//acon cool - ����",
				"//acon cost - �ĵ���",
		};
		this.MODULE_PRIVACY_TRIGER = new String[] {};
		this.MODULE_PRIVACY_LISTEN = new String[] {};
		this.MODULE_PRIVACY_STORED = new String[] {};
		this.MODULE_PRIVACY_CACHED = new String[] {
				"��Ⱥ�洢�ĵ���",
				"��Ⱥ�洢�Ĺ���ģʽ",
				"��Ⱥ�洢�ϴθ���ģʽ��ʱ��",
		};
		this.MODULE_PRIVACY_OBTAIN = new String[] {
				"��ȡ�������"
		};

		this.CONSUMPTION = new HashMap<>();
		this.WORKINGMODE = new HashMap<>();
		this.LASTCHANGED = new HashMap<>();
	}

	@Override
	public void memberExit(long gropid, long userid) {
	}

	@Override
	public void memberJoin(long gropid, long userid) {
	}

	@Override
	public String[] generateReport(final int logLevel, final int logMode, final int typeid, final long userid, final long diszid, final long gropid, final Message message, final Object... parameters) {
		return null;
	}

	@Override
	public boolean doUserMessage(final int typeid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		return true;
	}

	@Override
	public boolean doDiszMessage(final long diszid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		return true;
	}

	@Override
	public boolean doGropMessage(final long gropid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {

		final long current = System.currentTimeMillis() / 1000;
		long elapse = 0L;

		if (!this.CONSUMPTION.containsKey(gropid)) {
			this.CONSUMPTION.put(gropid, 0L);
			this.WORKINGMODE.put(gropid, 0L);
			this.LASTCHANGED.put(gropid, current);
			elapse = 0;
		}

		if (message.segment == 1) {

			Module.gropInfo(gropid, userid, "�������Է�����");

		} else {

			long consumption = this.CONSUMPTION.get(gropid);
			final long workingmode = this.WORKINGMODE.get(gropid);
			final long lastchanged = this.LASTCHANGED.get(gropid);

			elapse = current - lastchanged;

			switch (message.messages[1]) {

			case "off":
				if (workingmode == 0L) {
					Module.gropInfo(gropid, "�յ�û��");
				} else {
					Module.gropInfo(gropid, "�յ��ѹر�");
					consumption = consumption + (elapse * workingmode);
					this.CONSUMPTION.put(gropid, consumption);
					this.LASTCHANGED.put(gropid, current);
					this.WORKINGMODE.put(gropid, 0L);
				}
				break;

			case "dry":
				if (workingmode == 5880L) {
					Module.gropInfo(gropid, "�Ѿ����ڳ�ʪģʽ");
				} else {
					Module.gropInfo(gropid, "�л�����ʪģʽ");
					consumption = consumption + (elapse * workingmode);
					this.CONSUMPTION.put(gropid, consumption);
					this.LASTCHANGED.put(gropid, current);
					this.WORKINGMODE.put(gropid, 5880L);
				}
				break;

			case "wet":
				if (workingmode == 5880L) {
					Module.gropInfo(gropid, "�Ѿ����ڼ�ʪģʽ");
				} else {
					Module.gropInfo(gropid, "�л�����ʪģʽ");
					consumption = consumption + (elapse * workingmode);
					this.CONSUMPTION.put(gropid, consumption);
					this.LASTCHANGED.put(gropid, current);
					this.WORKINGMODE.put(gropid, 5880L);
				}
				break;

			case "bake":
				if (workingmode == 114700L) {
					Module.gropInfo(gropid, "�Ѿ������տ�ģʽ");
				} else {
					Module.gropInfo(gropid, "�л����տ�ģʽ");
					consumption = consumption + (elapse * workingmode);
					this.CONSUMPTION.put(gropid, consumption);
					this.LASTCHANGED.put(gropid, current);
					this.WORKINGMODE.put(gropid, 7350L);
				}
				break;

			case "cold":
				if (workingmode == 14700L) {
					Module.gropInfo(gropid, "�Ѿ������Ʊ�ģʽ");
				} else {
					Module.gropInfo(gropid, "�л����Ʊ�ģʽ");
					consumption = consumption + (elapse * workingmode);
					this.CONSUMPTION.put(gropid, consumption);
					this.LASTCHANGED.put(gropid, current);
					this.WORKINGMODE.put(gropid, 7350L);
				}
				break;

			case "warn":
				if (workingmode == 7350L) {
					Module.gropInfo(gropid, "�Ѿ���������ģʽ");
				} else {
					Module.gropInfo(gropid, "�л�������ģʽ");
					consumption = consumption + (elapse * workingmode);
					this.CONSUMPTION.put(gropid, consumption);
					this.LASTCHANGED.put(gropid, current);
					this.WORKINGMODE.put(gropid, 7350L);
				}
				break;

			case "cool":
				if (workingmode == 7350L) {
					Module.gropInfo(gropid, "�Ѿ���������ģʽ");
				} else {
					Module.gropInfo(gropid, "�л�������ģʽ");
					consumption = consumption + (elapse * workingmode);
					this.CONSUMPTION.put(gropid, consumption);
					this.LASTCHANGED.put(gropid, current);
					this.WORKINGMODE.put(gropid, 7350L);
				}
				break;

			case "cost":
				consumption = consumption + (elapse * workingmode);
				this.CONSUMPTION.put(gropid, consumption);
				this.LASTCHANGED.put(gropid, current);
				Module.gropInfo(gropid, "�ۼƹ��ĵ磺" + String.format("%.2f", consumption / 1000D) + "kW(" + String.format("%.2f", consumption / 3600000D) + ")��\r\nȺ����֧����" + String.format("%.2f", consumption / 1936800D) + "Ԫ");
				break;

			default:
				Module.gropInfo(gropid, userid, "�������Է�����");
				break;
			}

		}

		return true;
	}

}