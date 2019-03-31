package studio.blacktech.coolqbot.furryblack.module;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import studio.blacktech.coolqbot.furryblack.signal.Workflow;

public class Module_mine extends FunctionModuel {

	public Module_mine() {
		this.MODULE_NAME = "�ҵ���������";
		this.MODULE_HELP = "//mine status �鿴����������״̬\r\n//mine online �г��������������";
		this.MODULE_COMMAND = "mine";
		this.MODULE_VERSION = "1.5.1";
		this.MODULE_DESCRIPTION = "�ҵ�����๦������";
		this.MODULE_PRIVACY = "�洢 : ��\r\n���� : ��\r\n��ȡ : 1\r\n1: �����������@";
	}

	@Override
	public void excute(final Workflow flow) throws Exception {
		this.counter++;
		String res;
		String temp;
		byte[] buffer;
		if (flow.length == 1) {
			res = "������� - ������Ҫһ������";
		} else {
			final Socket socket = new Socket();
			socket.connect(new InetSocketAddress("192.168.1.10", 44505), 2000);
			final InputStream rx = socket.getInputStream();
			final OutputStream tx = socket.getOutputStream();
			Thread.sleep(50L);
			switch (flow.command[1]) {
			case "online":
				tx.write("REQ_ONLINE".getBytes("UTF-8"));
				tx.flush();
				buffer = new byte[131072];
				rx.read(buffer);
				temp = new String(buffer, "UTF-8");
				temp = temp.trim();
				res = temp;
				break;
			case "status":
			default:
				tx.write("REQ_STATUS".getBytes("UTF-8"));
				tx.flush();
				buffer = new byte[131072];
				rx.read(buffer);
				temp = new String(buffer, "UTF-8");
				temp = temp.trim();
				res = "���������� - " + temp + "������";
				break;
			}
			rx.close();
			tx.close();
			socket.close();
		}
		switch (flow.from) {
		case 1:
			FunctionModuel.priInfo(flow, res);
			break;
		case 2:
			FunctionModuel.disInfo(flow, res);
			break;
		case 3:
			FunctionModuel.grpInfo(flow, res);
			break;
		}
	}

	@Override
	public String genReport() {
		return null;
	}
}
