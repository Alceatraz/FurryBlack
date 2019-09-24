package studio.blacktech.coolqbot.furryblack.modules.Executor;

import java.util.TimeZone;

import org.junit.jupiter.api.Test;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.LoggerX.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;

public class Executor_time extends ModuleExecutor {

	private static final long serialVersionUID = 1L;

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "Executor_Time";
	private static String MODULE_COMMANDNAME = "time";
	private static String MODULE_DISPLAYNAME = "环球时间";
	private static String MODULE_DESCRIPTION = "那谁睡觉了吗";
	private static String MODULE_VERSION = "1.0";
	private static String[] MODULE_USAGE = new String[] {
			"/time 看看谁该睡觉了"
	};
	public static String[] MODULE_PRIVACY_TRIGER = new String[] {};
	public static String[] MODULE_PRIVACY_LISTEN = new String[] {};
	public static String[] MODULE_PRIVACY_STORED = new String[] {};
	public static String[] MODULE_PRIVACY_CACHED = new String[] {};
	public static String[] MODULE_PRIVACY_OBTAIN = new String[] {};

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

	public Executor_time() throws Exception {
		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_TRIGER, MODULE_PRIVACY_LISTEN, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	@Override
	public void init(LoggerX logger) throws Exception {

		this.ENABLE_USER = true;
		this.ENABLE_DISZ = true;
		this.ENABLE_GROP = true;
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
		entry.getMessage().userInfo(userid, this.getTime());
		return true;
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, MessageDisz message, int messageid, int messagefont) throws Exception {
		entry.getMessage().diszInfo(diszid, this.getTime());
		return true;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, MessageGrop message, int messageid, int messagefont) throws Exception {
		entry.getMessage().gropInfo(gropid, this.getTime());
		return true;
	}

	private String getTime() {
		TimeZone zone_W7 = TimeZone.getTimeZone("MST");
		TimeZone zone_W4 = TimeZone.getTimeZone("PRT");
		TimeZone zone_00 = TimeZone.getTimeZone("UTC");
		TimeZone zone_E2 = TimeZone.getTimeZone("EET");
		TimeZone zone_E8 = TimeZone.getTimeZone("CTT");

		int E8_DATE = Integer.parseInt(LoggerX.formatTime("dd", zone_E8));

		boolean yestday_W7 = Integer.parseInt(LoggerX.formatTime("dd", zone_W7)) < E8_DATE;
		boolean yestday_W4 = Integer.parseInt(LoggerX.formatTime("dd", zone_W4)) < E8_DATE;
		boolean yestday_E2 = Integer.parseInt(LoggerX.formatTime("dd", zone_E2)) < E8_DATE;

		return
		// @formatter:off
				"世界协调时(UTC) " + LoggerX.formatTime("yyyy-MM-dd HH:mm", zone_00) +
				"\r\n美国西部(UTC-7) " + (yestday_W7 ? "昨天 " : "") + LoggerX.formatTime("HH:mm", zone_W7) +
				"\r\n美国东部(UTC-4) " + (yestday_W4 ? "昨天 " : "") + LoggerX.formatTime("HH:mm", zone_W4) +
				"\r\n欧洲英国(UTC+4) " + (yestday_E2 ? "昨天 " : "") + LoggerX.formatTime("HH:mm", zone_E2) +
				"\r\n亚洲中国(UTC+8) " + LoggerX.formatTime("HH:mm", zone_E8)
		// @formatter:on
		;
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

	@Test
	void test() {
		System.out.println(this.getTime());
	}
}
