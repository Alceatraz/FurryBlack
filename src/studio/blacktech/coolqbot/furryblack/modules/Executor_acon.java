package studio.blacktech.coolqbot.furryblack.modules;

import java.util.HashMap;

import studio.blacktech.coolqbot.furryblack.common.Message;
import studio.blacktech.coolqbot.furryblack.common.Module;
import studio.blacktech.coolqbot.furryblack.common.ModuleExecutor;

public class Executor_acon extends ModuleExecutor {

	private HashMap<Long, Long> CONSUMPTION;
	private HashMap<Long, Long> WORKINGMODE;
	private HashMap<Long, Long> LASTCHANGED;

	public Executor_acon() {
		this.MODULE_DISPLAYNAME = "�յ�";
		this.MODULE_PACKAGENAME = "acon";
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

		CONSUMPTION = new HashMap<Long, Long>();
		WORKINGMODE = new HashMap<Long, Long>();
		LASTCHANGED = new HashMap<Long, Long>();
	}

	@Override
	public boolean doUserMessage(int typeid, long userid, Message message, int messageid, int messagefont) throws Exception {
		return true;
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, Message message, int messageid, int messagefont) throws Exception {
		return true;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, Message message, int messageid, int messagefont) throws Exception {

		long current = System.currentTimeMillis() / 1000;
		long elapse = 0L;

		if (!CONSUMPTION.containsKey(gropid)) {
			CONSUMPTION.put(gropid, 0L);
			WORKINGMODE.put(gropid, 0L);
			LASTCHANGED.put(gropid, current);
			elapse = 0;
		}

		if (message.segment == 1) {

			Module.gropInfo(gropid, userid, "�������Է�����");

		} else {

			long consumption = CONSUMPTION.get(gropid);
			long workingmode = WORKINGMODE.get(gropid);
			long lastchanged = LASTCHANGED.get(gropid);

			elapse = current - lastchanged;

			switch (message.messages[1]) {

			case "off":
				if (workingmode == 0L) {
					Module.gropInfo(gropid, "�յ�û��");
				} else {
					Module.gropInfo(gropid, "�յ��ѹر�");
					consumption = consumption + elapse * workingmode;
					CONSUMPTION.put(gropid, consumption);
					LASTCHANGED.put(gropid, current);
					WORKINGMODE.put(gropid, 0L);
				}
				break;

			case "dry":
				if (workingmode == 5880L) {
					Module.gropInfo(gropid, "�Ѿ����ڳ�ʪģʽ");
				} else {
					Module.gropInfo(gropid, "��Ⱥ�����ѿ��ţ���ʪģʽ");
					consumption = consumption + elapse * workingmode;
					CONSUMPTION.put(gropid, consumption);
					LASTCHANGED.put(gropid, current);
					WORKINGMODE.put(gropid, 5880L);
				}
				break;

			case "cool":
				if (workingmode == 7350L) {
					Module.gropInfo(gropid, "�Ѿ���������ģʽ");
				} else {
					Module.gropInfo(gropid, "��Ⱥ�����ѿ��ţ�����ģʽ");
					consumption = consumption + elapse * workingmode;
					CONSUMPTION.put(gropid, consumption);
					LASTCHANGED.put(gropid, current);
					WORKINGMODE.put(gropid, 7350L);
				}
				break;

			case "cost":
				consumption = consumption + elapse * workingmode;
				CONSUMPTION.put(gropid, consumption);
				LASTCHANGED.put(gropid, current);
				Module.gropInfo(gropid, "�ۼƹ��ĵ磺" + String.format("%.2f", consumption / 1000D) + "kW(" + String.format("%.2f", consumption / 3600000D) + ")��\r\nȺ����֧����" + String.format("%.2f", consumption / 1936800D) + "Ԫ");
				break;

			default:
				Module.gropInfo(gropid, userid, "�������Է�����");
				break;
			}

		}

		return true;
	}

	@Override
	public String generateReport(int logLevel, int logMode, Message message, Object[] parameters) {

		return null;
	}
}
