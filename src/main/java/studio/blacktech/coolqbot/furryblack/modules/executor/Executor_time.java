package studio.blacktech.coolqbot.furryblack.modules.executor;


import studio.blacktech.coolqbot.furryblack.common.annotation.ModuleExecutorComponent;
import studio.blacktech.coolqbot.furryblack.common.loggerx.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;
import studio.blacktech.coolqbot.furryblack.entry;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


@ModuleExecutorComponent
public class Executor_time extends ModuleExecutor {

	private static final long serialVersionUID = 1L;

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	private static final String MODULE_PACKAGENAME = "Executor_Time";
	private static final String MODULE_COMMANDNAME = "time";
	private static final String MODULE_DISPLAYNAME = "环球时间";
	private static final String MODULE_DESCRIPTION = "那谁睡觉了吗";
	private static final String MODULE_VERSION = "1.0.4";
	private static final String[] MODULE_USAGE = new String[] {
			"/time 看看谁该睡觉了"
	};
	public static String[] MODULE_PRIVACY_STORED = new String[] {};
	public static String[] MODULE_PRIVACY_CACHED = new String[] {};
	public static String[] MODULE_PRIVACY_OBTAIN = new String[] {};

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================

	private static final TimeZone zone_USW = TimeZone.getTimeZone("America/Los_Angeles");
	private static final TimeZone zone_USE = TimeZone.getTimeZone("America/New_York");
	private static final TimeZone zone_00 = TimeZone.getTimeZone("UTC");
	private static final TimeZone zone_UK = TimeZone.getTimeZone("Europe/London");
	private static final TimeZone zone_SE = TimeZone.getTimeZone("Europe/Stockholm");
	private static final TimeZone zone_FR = TimeZone.getTimeZone("Europe/Paris");
	private static final TimeZone zone_CN = TimeZone.getTimeZone("Asia/Shanghai");

	private long cachets;
	private String cache;

	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================

	public Executor_time() throws Exception {
		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}


	@Override
	public boolean init() throws Exception {

		cachets = 0;
		cache = getTime();

		ENABLE_USER = true;
		ENABLE_DISZ = true;
		ENABLE_GROP = true;
		return true;
	}


	@Override
	public boolean boot() throws Exception {
		return true;
	}


	@Override
	public boolean save() throws Exception {
		return true;
	}


	@Override
	public boolean shut() throws Exception {
		return true;
	}


	@Override
	public String[] exec(Message message) throws Exception {
		return new String[] {
				"此模块无可用命令"
		};
	}


	@Override
	public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) {

	}

	@Override
	public void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) {

	}

	@Override
	public boolean doUserMessage(MessageUser message) throws Exception {
		long userid = message.getUserID();
		entry.userInfo(userid, getTime());
		return true;

	}

	@Override
	public boolean doDiszMessage(MessageDisz message) throws Exception {
		long diszid = message.getDiszID();
		entry.diszInfo(diszid, getTime());
		return true;

	}

	@Override
	public boolean doGropMessage(MessageGrop message) throws Exception {
		long gropid = message.getGropID();
		entry.gropInfo(gropid, getTime());
		return true;

	}

	private String getTime() {

		long now = System.currentTimeMillis();

		if (now - cachets > 3600000) {

			cachets = now;

			cache = "世界协调时(UTC) " + LoggerX.formatTime("yyyy-MM-dd HH:mm", zone_00) + "\r\n" +
					"美国西部(UTC-8) " + LoggerX.formatTime("HH:mm", zone_USW) + format(zone_USW) + "\r\n" +
					"美国东部(UTC-4) " + LoggerX.formatTime("HH:mm", zone_USE) + format(zone_USE) + "\r\n" +
					"欧洲英国(UTC+0) " + LoggerX.formatTime("HH:mm", zone_UK) + format(zone_UK) + "\r\n" +
					"欧洲瑞典(UTC+1) " + LoggerX.formatTime("HH:mm", zone_SE) + format(zone_SE) + "\r\n" +
					"欧洲法国(UTC+1) " + LoggerX.formatTime("HH:mm", zone_FR) + format(zone_FR) + "\r\n" +
					"亚洲中国(UTC+8) " + LoggerX.formatTime("HH:mm", zone_CN);

		}

		return cache;

	}


	/**
	 * 这个算法非常牛逼 而且我不打算解释
	 */
	@SuppressWarnings("deprecation")
	private String format(TimeZone timezone) {


		boolean isEnableDST = false;
		boolean isDisableDST = false;

		StringBuilder builder = new StringBuilder();

		Calendar today = Calendar.getInstance(timezone);

		long current = today.getTimeInMillis();

		Date begin = new Date(current);

		begin.setMonth(1);
		begin.setDate(1);
		begin.setHours(0);
		begin.setMinutes(0);
		begin.setSeconds(0);

		Calendar temp = Calendar.getInstance(timezone);

		temp.setTime(new Date(begin.getTime() / 1000 * 1000));

		for (long i = temp.getTimeInMillis(); i < current; i = temp.getTimeInMillis()) {

			temp.add(Calendar.DATE, 1);

			long t = temp.getTimeInMillis();

			if (t - i < 86400000) {
				isEnableDST = true;
			} else if (t - i > 86400000) {
				isDisableDST = true;
			}
		}

		if (isEnableDST ^ isDisableDST) builder.append(" 夏令时");

		int TZ_DATE = Integer.parseInt(LoggerX.formatTime("dd", timezone));
		int E8_DATE = Integer.parseInt(LoggerX.formatTime("dd", Executor_time.zone_CN));

		if (E8_DATE - TZ_DATE > 0) {
			builder.append(" 昨天," + TZ_DATE + "日");
		} else if (E8_DATE - TZ_DATE < 0) {
			builder.append(" 明天," + TZ_DATE + "日");
		}

		return builder.toString();

	}


	// ==========================================================================================================================================================
	//
	// 工具函数
	//
	// ==========================================================================================================================================================

	@Override
	public String[] generateReport(Message message) {

		return new String[0];

	}

}
