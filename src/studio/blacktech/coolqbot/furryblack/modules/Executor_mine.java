package studio.blacktech.coolqbot.furryblack.modules;

import studio.blacktech.coolqbot.furryblack.common.Message;
import studio.blacktech.coolqbot.furryblack.common.ModuleExecutor;

//
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.InetSocketAddress;
//import java.net.Socket;
//
//import studio.blacktech.coolqbot.furryblack.signal.Workflow;
//
public class Executor_mine extends ModuleExecutor {
	
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

		return false;
	}

	@Override
	public boolean doDiszMessage(final long diszid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {

		return false;
	}

	@Override
	public boolean doGropMessage(final long gropid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {

		return false;
	}

}
//
//	public Executor_mine() {
//		this.MODULE_NAME = "�ҵ���������";
//		this.MODULE_HELP = "//mine status �鿴����������״̬\r\n//mine online �г��������������";
//		this.MODULE_COMMAND = "mine";
//		this.MODULE_VERSION = "1.5.1";
//		this.MODULE_DESCRIPTION = "�ҵ�����๦������";
//		this.MODULE_PRIVACY = "�洢 : ��\r\n���� : ��\r\n��ȡ : 1\r\n1: �����������@";
//	}
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
