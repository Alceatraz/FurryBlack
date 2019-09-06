package studio.blacktech.coolqbot.furryblack.modules.Executor;

import studio.blacktech.coolqbot.furryblack.common.LoggerX.LoggerX;
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

	private static final long serialVersionUID = 1L;

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "Executor_Mine";
	private static String MODULE_COMMANDNAME = "mine";
	private static String MODULE_DISPLAYNAME = "我的世界助手";
	private static String MODULE_DESCRIPTION = "我的世界助手";
	private static String MODULE_VERSION = "2.0";
	private static String[] MODULE_USAGE = new String[] {
			"/mine status 查看服务器在线状态",
			"/mine online 列出服务器在线玩家"
	};
	private static String[] MODULE_PRIVACY_TRIGER = new String[] {};
	private static String[] MODULE_PRIVACY_LISTEN = new String[] {};
	private static String[] MODULE_PRIVACY_STORED = new String[] {};
	private static String[] MODULE_PRIVACY_CACHED = new String[] {};
	private static String[] MODULE_PRIVACY_OBTAIN = new String[] {
			"获取命令发送人"
	};

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================

	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================

	public Executor_mine() throws Exception {
		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_TRIGER, MODULE_PRIVACY_LISTEN, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
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
	public void save(LoggerX logger) throws Exception {
	}

	@Override
	public void reload(LoggerX logger) throws Exception {
	}

	@Override
	public void exec(LoggerX logger, Message message) throws Exception {
	}

	@Override
	public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) {
	}

	@Override
	public void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) {
	}

	@Override
	public boolean doUserMessage(int typeid, long userid, MessageUser message, int messageid, int messagefont) throws Exception {
		return false;
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, MessageDisz message, int messageid, int messagefont) throws Exception {
		return false;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, MessageGrop message, int messageid, int messagefont) throws Exception {
		return false;
	}

	// ==========================================================================================================================================================
	//
	// 工具函数
	//
	// ==========================================================================================================================================================

	@Override
	public String[] generateReport(int mode, Message message, Object... parameters) {
		return null;
	}

}
//

//
//	@Override
//	public void executor( Workflow flow) throws Exception {
//		this.counter++;
//		String res;
//		String temp;
//		byte[] buffer;
//		if (flow.length == 1) {
//			res = "命令错误 - 至少需要一个参数";
//		} else {
//			 Socket socket = new Socket();
//			socket.connect(new InetSocketAddress("192.168.1.10", 44505), 2000);
//			 InputStream rx = socket.getInputStream();
//			 OutputStream tx = socket.getOutputStream();
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
