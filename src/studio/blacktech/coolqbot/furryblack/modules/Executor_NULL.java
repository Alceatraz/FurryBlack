package studio.blacktech.coolqbot.furryblack.modules;

import studio.blacktech.coolqbot.furryblack.common.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;

public class Executor_NULL extends ModuleExecutor {

	// ==========================================================================================================================================================
	//
	// ģ���������
	//
	// ==========================================================================================================================================================

	public static String MODULE_PACKAGENAME = "null";
	public static String MODULE_DISPLAYNAME = "ʵ��ģ��";
	public static String MODULE_DESCRIPTION = "ʵ��ģ��";
	public static String MODULE_VERSION = "1.0.0";
	public static String[] MODULE_USAGE = new String[] {
			"����1 - �����÷�1",
			"����2 - �����÷�2",
			"����3 - �����÷�3",
			"����4 - �����÷�4",
	};
	public static String[] MODULE_PRIVACY_TRIGER = new String[] {
			"������ - ����"
	};
	public static String[] MODULE_PRIVACY_LISTEN = new String[] {
			"������ - ����"
	};
	public static String[] MODULE_PRIVACY_STORED = new String[] {
			"��˽���� - ��;"
	};
	public static String[] MODULE_PRIVACY_CACHED = new String[] {
			"��˽���� - ��;"
	};
	public static String[] MODULE_PRIVACY_OBTAIN = new String[] {
			"��˽���� - ��;"
	};

	// ==========================================================================================================================================================
	//
	// ��Ա����
	//
	// ==========================================================================================================================================================

	// ==========================================================================================================================================================
	//
	// �������ں���
	//
	// ==========================================================================================================================================================

	/***
	 * ����ģ��ʵ�������� �˴���Ӧִ���κδ���
	 *
	 * @throws Exception
	 */
	public Executor_NULL() throws Exception {
		super(MODULE_DISPLAYNAME, MODULE_PACKAGENAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_TRIGER, MODULE_PRIVACY_LISTEN, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	/***
	 * ��ʼ���׶� Ӧ
	 *
	 * 1�����û������Ĭ������ 2�����������ڴ�ṹ 3����ȡ���� 4������ ENABLE_MODE
	 */
	@Override
	public void init(LoggerX logger) throws Exception {

		if (this.NEW_CONFIG) {
			this.CONFIG.setProperty("config1", "none");
			this.CONFIG.setProperty("config2", "none");
			this.CONFIG.setProperty("config3", "none");
			this.CONFIG.setProperty("config4", "none");
			this.saveConfig();
		} else {
			this.loadConfig();
		}

		this.ENABLE_USER = false;
		this.ENABLE_DISZ = false;
		this.ENABLE_GROP = false;
	}

	/***
	 * ���ENABLE_MODE=false�򲻻�ע�� �򲻻�ִ��boot������
	 */
	@Override
	public void boot(LoggerX logger) throws Exception {

	}

	/***
	 * Ӧ�ڴ˴����йر��߼�
	 */
	@Override
	public void shut(LoggerX logger) throws Exception {
	}

	/***
	 * ��������ʹ��
	 */
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
