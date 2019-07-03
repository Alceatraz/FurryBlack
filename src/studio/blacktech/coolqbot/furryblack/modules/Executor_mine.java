package studio.blacktech.coolqbot.furryblack.modules;

import studio.blacktech.coolqbot.furryblack.common.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;

//
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.InetSocketAddress;
//import java.net.Socket;
//
//import studio.blacktech.coolqbot.furryblack.signal.Workflow;
//
public class Executor_mine extends ModuleExecutor {

	// ==========================================================================================================================================================
	//
	// ģ���������
	//
	// ==========================================================================================================================================================

	public static String MODULE_PACKAGENAME = "mine";
	public static String MODULE_DISPLAYNAME = "�ҵ���������";
	public static String MODULE_DESCRIPTION = "�ҵ���������";
	public static String MODULE_VERSION = "2.0";
	public static String[] MODULE_USAGE = new String[] {
			"/mine status �鿴����������״̬",
			"/mine online �г��������������"
	};
	public static String[] MODULE_PRIVACY_TRIGER = new String[] {};
	public static String[] MODULE_PRIVACY_LISTEN = new String[] {};
	public static String[] MODULE_PRIVACY_STORED = new String[] {};
	public static String[] MODULE_PRIVACY_CACHED = new String[] {};
	public static String[] MODULE_PRIVACY_OBTAIN = new String[] {
			"��ȡ�������"
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

	public Executor_mine() throws Exception {
		super(MODULE_DISPLAYNAME, MODULE_PACKAGENAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_TRIGER, MODULE_PRIVACY_LISTEN, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	@Override
	public void init(LoggerX logger) throws Exception {
		this.ENABLE_USER = false;
		this.ENABLE_DISZ = false;
		this.ENABLE_GROP = false;
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
		return false;
	}

	@Override
	public boolean doDiszMessage(final long diszid, final long userid, final MessageDisz message, final int messageid, final int messagefont) throws Exception {
		return false;
	}

	@Override
	public boolean doGropMessage(final long gropid, final long userid, final MessageGrop message, final int messageid, final int messagefont) throws Exception {
		return false;
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
//

//
//	@Override
//	public void executor(final Workflow flow) throws Exception {
//		this.counter++;
//		String res;
//		String temp;
//		byte[] buffer;
//		if (flow.length == 1) {
//			res = "������� - ������Ҫһ������";
//		} else {
//			final Socket socket = new Socket();
//			socket.connect(new InetSocketAddress("192.168.1.10", 44505), 2000);
//			final InputStream rx = socket.getInputStream();
//			final OutputStream tx = socket.getOutputStream();
//			Thread.sleep(50L);
//			switch (flow.command[1]) {
//			case "online":
//				tx.write("REQ_ONLINE".getBytes("UTF-8"));
//				tx.flush();
//				buffer = new byte[131072];
//				rx.read(buffer);
//				temp = new String(buffer, "UTF-8");
//				temp = temp.trim();
//				res = temp;
//				break;
//			case "status":
//			default:
//				tx.write("REQ_STATUS".getBytes("UTF-8"));
//				tx.flush();
//				buffer = new byte[131072];
//				rx.read(buffer);
//				temp = new String(buffer, "UTF-8");
//				temp = temp.trim();
//				res = "���������� - " + temp + "������";
//				break;
//			}
//			rx.close();
//			tx.close();
//			socket.close();
//		}
//		switch (flow.from) {
//		case 1:
//			FunctionExecutor.priInfo(flow, res);
//			break;
//		case 2:
//			FunctionExecutor.disInfo(flow, res);
//			break;
//		case 3:
//			FunctionExecutor.grpInfo(flow, res);
//			break;
//		}
//	}
//
//	@Override
//	public String genReport() {
//		return null;
//	}
//}
