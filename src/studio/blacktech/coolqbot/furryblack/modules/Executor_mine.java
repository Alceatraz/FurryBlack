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
	public boolean doUserMessage(int typeid, long userid, Message message, int messageid, int messagefont) throws Exception {

		return false;
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, Message message, int messageid, int messagefont) throws Exception {

		return false;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, Message message, int messageid, int messagefont) throws Exception {

		return false;
	}

	@Override
	public String generateReport(int logLevel, int logMode, int typeid, long userid, long diszid, long gropid, Message message, Object[] parameters) {

		return null;
	}

}
//
//	public Executor_mine() {
//		this.MODULE_NAME = "我的世界助手";
//		this.MODULE_HELP = "//mine status 查看服务器在线状态\r\n//mine online 列出服务器在线玩家";
//		this.MODULE_COMMAND = "mine";
//		this.MODULE_VERSION = "1.5.1";
//		this.MODULE_DESCRIPTION = "我的世界多功能助手";
//		this.MODULE_PRIVACY = "存储 : 无\r\n缓存 : 无\r\n获取 : 1\r\n1: 命令发送人用于@";
//	}
//
//	@Override
//	public void executor(final Workflow flow) throws Exception {
//		this.counter++;
//		String res;
//		String temp;
//		byte[] buffer;
//		if (flow.length == 1) {
//			res = "命令错误 - 至少需要一个参数";
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
//				res = "服务器正常 - " + temp + "人在线";
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
