package studio.blacktech.coolqbot.furryblack.modules.Executor;


import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.LoggerX.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.annotation.ModuleExecutorComponent;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;


@ModuleExecutorComponent
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
	public static String[] MODULE_PRIVACY_STORED = new String[] {};
	public static String[] MODULE_PRIVACY_CACHED = new String[] {};
	public static String[] MODULE_PRIVACY_OBTAIN = new String[] {};

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================

	private static final TimeZone zone_W8 = TimeZone.getTimeZone("America/Los_Angeles");
	private static final TimeZone zone_W4 = TimeZone.getTimeZone("America/New_York");
	private static final TimeZone zone_00 = TimeZone.getTimeZone("UTC");
	private static final TimeZone zone_E0 = TimeZone.getTimeZone("Europe/London");
	// private static final TimeZone zone_E1 = TimeZone.getTimeZone("Europe/Stockholm");
	private static final TimeZone zone_E8 = TimeZone.getTimeZone("Asia/Shanghai");

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
	public boolean doUserMessage(int typeid, long userid, MessageUser message, int messageid, int messagefont) throws Exception {

		entry.userInfo(userid, getTime());
		return true;

	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, MessageDisz message, int messageid, int messagefont) throws Exception {

		entry.diszInfo(diszid, getTime());
		return true;

	}

	@Override
	public boolean doGropMessage(long gropid, long userid, MessageGrop message, int messageid, int messagefont) throws Exception {

		entry.gropInfo(gropid, getTime());
		return true;

	}

	private String getTime() {

		// @formatter:off

		return

            "世界协调时(UTC) " + LoggerX.formatTime("yyyy-MM-dd HH:mm", Executor_time.zone_00) + "\r\n" +
            "美国西部(UTC-8) " + LoggerX.formatTime("HH:mm", Executor_time.zone_W8) + format(Executor_time.zone_W8) + "\r\n" +
            "美国东部(UTC-4) " + LoggerX.formatTime("HH:mm", Executor_time.zone_W4) + format(Executor_time.zone_W4) + "\r\n" +
            "欧洲英国(UTC+0) " + LoggerX.formatTime("HH:mm", Executor_time.zone_E0) + format(Executor_time.zone_E0) + "\r\n" +
            //"欧洲瑞典(UTC+1) " + LoggerX.formatTime("HH:mm", zone_E1) + this.format(zone_E1) + "\r\n" +
            "亚洲中国(UTC+8) " + LoggerX.formatTime("HH:mm", Executor_time.zone_E8)
		;

		// @formatter:on
	}

	/**
	 * 这个算法非常牛逼 而且我不打算解释
	 */
	@SuppressWarnings("deprecation")
	private String format(TimeZone timezone) {

		// @formatter:off

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

        if (isEnableDST ^ isDisableDST) {
			builder.append(" 夏令时");
		}

        int TZ_DATE = Integer.parseInt(LoggerX.formatTime("dd", timezone));
        int E8_DATE = Integer.parseInt(LoggerX.formatTime("dd", Executor_time.zone_E8));

        if (E8_DATE - TZ_DATE > 0) {
			builder.append(" 昨天," + TZ_DATE + "日");
		} else if (E8_DATE - TZ_DATE < 0) {
			builder.append(" 明天," + TZ_DATE + "日");
		}

        // @formatter:on

		return builder.toString();

	}
	// ==========================================================================================================================================================
	//
	// 工具函数
	//
	// ==========================================================================================================================================================

	@Override
	public String[] generateReport(int mode, Message message, Object... parameters) {

		return new String[0];

	}

}
