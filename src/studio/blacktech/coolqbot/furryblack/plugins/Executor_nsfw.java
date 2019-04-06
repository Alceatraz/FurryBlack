package studio.blacktech.coolqbot.furryblack.plugins;

import studio.blacktech.coolqbot.furryblack.module.Message;
import studio.blacktech.coolqbot.furryblack.module.ModuleExecutor;

//
//import studio.blacktech.coolqbot.furryblack.signal.Workflow;
//
public class Executor_nsfw extends ModuleExecutor {

	@Override
	public boolean doUserMessage(int typeid, long userid, Message message, int messageid, int messagefont) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, Message message, int messageid, int messagefont) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, Message message, int messageid, int messagefont) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String generateReport() {
		// TODO Auto-generated method stub
		return null;
	}

}
//
//	public Executor_nsfw() {
//		this.MODULE_NAME = "刷屏助手";
//		this.MODULE_HELP = "//nsfw";
//		this.MODULE_COMMAND = "nsfw";
//		this.MODULE_VERSION = "1.0.0";
//		this.MODULE_DESCRIPTION = "输入命令启用一次性的刷屏功能";
//		this.MODULE_PRIVACY = "特权 : 1\r\n1: 监听群用户发言事件而不经过//过滤，不解析内容\r\n存储 : 无\r\n缓存 : 无\r\n获取 : 1\r\n1: 命令发送人用于@";
//
//	}
//
//	@Override
//	public void executor(final Workflow flow) {
//		this.counter++;
//		final String res = "NSFW !\r\n++++++\r\n| 01 |\r\n| 02 |\r\n| 03 |\r\n| 04 |\r\n| 05 |\r\n| 06 |\r\n| 07 |\r\n| 08 |\r\n| 09 |\r\n| 10 |\r\n| 11 |\r\n| 12 |\r\n| 13 |\r\n| 14 |\r\n| 15 |\r\n| 16 |\r\n| 17 |\r\n| 18 |\r\n| 19 |\r\n| 20 |\r\n| 21 |\r\n| 22 |\r\n| 23 |\r\n| 24 |\r\n| 25 |\r\n| 26 |\r\n| 27 |\r\n| 28 |\r\n| 29 |\r\n| 30 |\r\n| 41 |\r\n| 42 |\r\n| 43 |\r\n| 44 |\r\n| 45 |\r\n| 46 |\r\n| 47 |\r\n| 48 |\r\n| 49 |\r\n| 40 |\r\n| 41 |\r\n| 42 |\r\n| 43 |\r\n| 44 |\r\n| 45 |\r\n| 46 |\r\n| 47 |\r\n| 48 |\r\n| 49 |\r\n| 50 |\r\n| 51 |\r\n| 52 |\r\n| 53 |\r\n| 54 |\r\n| 55 |\r\n| 56 |\r\n| 57 |\r\n| 58 |\r\n| 59 |\r\n| 60 |\r\n++++++\r\nNSFW !";
//		switch (flow.from) {
//		case 1:
//			FunctionExecutor.priInfo(flow, res);
//			break;
//		case 2:
//			FunctionExecutor.disAnno(flow.dzid, res);
//			break;
//		case 3:
//			FunctionExecutor.grpAnno(flow.gpid, res);
//			break;
//		}
//	}
//
//	@Override
//	public String genReport() {
//		return null;
//	}
//}
