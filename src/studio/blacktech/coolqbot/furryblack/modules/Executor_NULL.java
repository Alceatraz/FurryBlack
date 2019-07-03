package studio.blacktech.coolqbot.furryblack.modules;

import studio.blacktech.coolqbot.furryblack.common.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;

public class Executor_NULL extends ModuleExecutor {

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	public static String MODULE_PACKAGENAME = "null";
	public static String MODULE_DISPLAYNAME = "实例模块";
	public static String MODULE_DESCRIPTION = "实例模块";
	public static String MODULE_VERSION = "1.0.0";
	public static String[] MODULE_USAGE = new String[] {
			"命令1 - 命令用法1",
			"命令2 - 命令用法2",
			"命令3 - 命令用法3",
			"命令4 - 命令用法4",
	};
	public static String[] MODULE_PRIVACY_TRIGER = new String[] {
			"触发器 - 功能"
	};
	public static String[] MODULE_PRIVACY_LISTEN = new String[] {
			"监听器 - 功能"
	};
	public static String[] MODULE_PRIVACY_STORED = new String[] {
			"隐私级别 - 用途"
	};
	public static String[] MODULE_PRIVACY_CACHED = new String[] {
			"隐私级别 - 用途"
	};
	public static String[] MODULE_PRIVACY_OBTAIN = new String[] {
			"隐私级别 - 用途"
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

	/***
	 * 调用模块实例化方法 此处不应执行任何代码
	 *
	 * @throws Exception
	 */
	public Executor_NULL() throws Exception {
		super(MODULE_DISPLAYNAME, MODULE_PACKAGENAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_TRIGER, MODULE_PRIVACY_LISTEN, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	/***
	 * 初始化阶段 应
	 *
	 * 1：如果没有生成默认配置 2：生成所有内存结构 3：读取配置 4：分析 ENABLE_MODE
	 */
	@Override
	public void init(LoggerX logger) throws Exception {

		if (this.NEW_CONFIG) {
			this.CONFIG.setProperty("config1", "none");
			this.CONFIG.setProperty("config2", "none");
			this.CONFIG.setProperty("config3", "none");
			this.CONFIG.setProperty("config4", "none");
			this.saveConfig();
		} else {
			this.loadConfig();
		}

		this.ENABLE_USER = false;
		this.ENABLE_DISZ = false;
		this.ENABLE_GROP = false;
	}

	/***
	 * 如果ENABLE_MODE=false则不会注册 则不会执行boot的内容
	 */
	@Override
	public void boot(LoggerX logger) throws Exception {

	}

	/***
	 * 应在此处运行关闭逻辑
	 */
	@Override
	public void shut(LoggerX logger) throws Exception {
	}

	/***
	 * 重载配置使用
	 */
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
		return true;
	}

	@Override
	public boolean doDiszMessage(final long diszid, final long userid, final MessageDisz message, final int messageid, final int messagefont) throws Exception {
		return true;
	}

	@Override
	public boolean doGropMessage(final long gropid, final long userid, final MessageGrop message, final int messageid, final int messagefont) throws Exception {
		return true;
	}

	// ==========================================================================================================================================================
	//
	// 工具函数
	//
	// ==========================================================================================================================================================

	@Override
	public String[] generateReport(int mode, final Message message, final Object... parameters) {
		return null;
	}

}
