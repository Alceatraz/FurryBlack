package studio.blacktech.coolqbot.furryblack.modules;

import studio.blacktech.coolqbot.furryblack.common.Message;
import studio.blacktech.coolqbot.furryblack.common.Module;
import studio.blacktech.coolqbot.furryblack.common.ModuleExecutor;

public class Executor_kong extends ModuleExecutor {

	public Executor_kong() {

		this.MODULE_DISPLAYNAME = "变臭";
		this.MODULE_PACKAGENAME = "kong";
		this.MODULE_DESCRIPTION = "给 文 字 加 空 格";
		this.MODULE_VERSION = "2.0.0";
		this.MODULE_USAGE = new String[] {
				"//kong 原句 - 给原句添加空格"
		};
		this.MODULE_PRIVACY_TRIGER = new String[] {};
		this.MODULE_PRIVACY_LISTEN = new String[] {};
		this.MODULE_PRIVACY_STORED = new String[] {};
		this.MODULE_PRIVACY_CACHED = new String[] {};
		this.MODULE_PRIVACY_OBTAIN = new String[] {
				"获取命令发送人"
		};
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
		Module.userInfo(userid, message.length == 1 ? "你 想 把 空 气 变 臭 吗" : Executor_kong.kong(message));
		return true;
	}

	@Override
	public boolean doDiszMessage(final long diszid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		Module.diszInfo(diszid, userid, message.length == 1 ? "你 想 把 空 气 变 臭 吗" : Executor_kong.kong(message));
		return true;
	}

	@Override
	public boolean doGropMessage(final long gropid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		Module.gropInfo(gropid, userid, message.length == 1 ? "你 想 把 空 气 变 臭 吗" : Executor_kong.kong(message));
		return true;
	}

	private static String kong(final Message message) {
		String temp;
		temp = message.join(1);
		temp = temp.replaceAll(" ", "");
		temp = temp.trim();
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < temp.length(); i++) {
			builder.append(temp.charAt(i));
			builder.append(" ");
		}
		builder.setLength(builder.length() - 1);
		return builder.toString();
	}

}
