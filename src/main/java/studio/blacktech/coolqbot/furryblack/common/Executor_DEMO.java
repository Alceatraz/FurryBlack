package studio.blacktech.coolqbot.furryblack.common;


import java.io.File;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;


public class Executor_DEMO extends ModuleExecutor {

	private static final long serialVersionUID = 1L;
	// ==========================================================================================================================================================
	//
	// 此模块为模板模块
	// 用于教学示例用
	//
	// ==========================================================================================================================================================
	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================
	// PACKAGENAME 名称为完整名称
	// 命名规则应为 类型_名称
	private static String MODULE_PACKAGENAME = "Executor_Demo";
	// COMMANDNAME 名称为命令调用的名称
	// 应和PACKAGE名称一致
	private static String MODULE_COMMANDNAME = "demo";
	// DISPLAYNAME 名称为人类可读的友好名称 应该在8个字以内
	private static String MODULE_DISPLAYNAME = "示范模块";
	// DESCRIPTON 为模块功能简介
	private static String MODULE_DESCRIPTION = "示范如何编写模块";
	// 版本号
	// 推荐两段式版本
	// bug修复加个位
	// 任何功能更新都加十位
	// 仅为后个位归零
	private static String MODULE_VERSION = "1.0";
	// 命令用法，数组的每个元素应为一个参数组合用法及其说明
	// 减号左右各一个空格
	private static String[] MODULE_USAGE = new String[] {
			"命令1 - 命令用法1", "命令2 - 命令用法2", "命令3 - 命令用法3", "命令4 - 命令用法4",
	};
	// 如果需要将数据存储为文件 则应写明存储的内容及其用途 有效时限
	public static String[] MODULE_PRIVACY_STORED = new String[] {
			"隐私级别 - 用途"
	};
	// 如果需要将数据存储在内存 则应写明存储的内容及其用途 有效时限
	public static String[] MODULE_PRIVACY_CACHED = new String[] {
			"隐私级别 - 用途"
	};
	// 如果需要获取用户相关的信息 则应写明内容及其用途 且获取的信息不应该储存 如果需要存储则将此功能写入MODULE_PRIVACY_CACHED
	public static String[] MODULE_PRIVACY_OBTAIN = new String[] {
			"隐私级别 - 用途"
	};
	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================
	// 非原子量请勿在声明变量的时候赋值
	// 推荐使用全大写命名
	// 错误：HashMap<String,String> MAP = new HashMap<>();
	// 正确：HashMap<String,String> MAP ;
	private HashMap<String, String> MAP;
	private File FILE_CUSTOM;
	private Thread thread;
	private boolean ENABLE_DEMO = false;
	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================

	/***
	 * 调用模块实例化方法 此处不应执行任何代码
	 *
	 * @throws Exception 发生任何错误应扔出
	 */
	public Executor_DEMO() throws Exception {
		// @formatter:off

        super(
                MODULE_PACKAGENAME,
                MODULE_COMMANDNAME,
                MODULE_DISPLAYNAME,
                MODULE_DESCRIPTION,
                MODULE_VERSION,
                MODULE_USAGE,
                MODULE_PRIVACY_STORED,
                MODULE_PRIVACY_CACHED,
                MODULE_PRIVACY_OBTAIN
        );

        // @formatter:on
	}

	/**
	 * 生命周期函数 初始化阶段
	 * <p>
	 * 1：初始化配置及数据文件 2：生成所有内存结构 3：读取配置并应用 4：分析 ENABLE_MODE
	 */
	@Override
	public boolean init() throws Exception {

		// ==================================================================================
		// 1：初始化配置及数据文件
		initAppFolder();
		initConfFolder();
		initDataFolder();
		initLogsFolder();
		initPropertiesConfigurtion();
		// ==================================================================================
		// 2：生成所有内存结构
		// 应在此处实例化成员变量
		MAP = new HashMap<>();
		// 关于文件路径：应使用Paths工具类以及内置的 FOLDER_CONF FOLDER_DATA FOLDER_LOGS来表示文件
		FILE_CUSTOM = Paths.get(FOLDER_CONF.getAbsolutePath(), "custom.txt").toFile();
		if (!FILE_CUSTOM.exists()) { FILE_CUSTOM.createNewFile(); }
		// ==================================================================================
		// 3：读取配置
		// NEW_CONFIG=true 为初始化过程中发现配置不存在 创建了新的配置
		if (NEW_CONFIG) {
			// CONFIG对象为Java property对象
			CONFIG.setProperty("enable", "true");
			CONFIG.setProperty("config1", "none");
			CONFIG.setProperty("config2", "none");
			CONFIG.setProperty("config3", "none");
			CONFIG.setProperty("config4", "none");
			// 不要忘记保存
			saveConfig();
		} else {
			loadConfig();
		}
		// 按需分析配置文件
		ENABLE_DEMO = Boolean.parseBoolean(CONFIG.getProperty("enable"));
		// 按需初始化内存结构
		MAP.put("1", "1");
		// 如果需要包含需要获取所有群成员的功能，不应该在doMessage的时候获取 应通过初始化和增减成员函数来维护一个容器
		// ==================================================================================
		// 4：分析 ENABLE_MODE
		// ENABLE_MODE = false 时，由systemd注册插件时将不会注册
		// 此设计的目的是比如模块被配置禁用
		// 则直接跳注册阶段
		// 模块不需要每次doMessage时都判断 if ( enable )
		if (ENABLE_DEMO) {
			ENABLE_USER = true;
			ENABLE_DISZ = true;
			ENABLE_GROP = true;
		}
		return true;

	}

	/**
	 * 如果有 应在此处初始化工作线程并运行 如果ENABLE_MODE=false则不会注册 则不会执行boot的内容
	 */
	@Override
	public boolean boot() throws Exception {

		thread = new Thread(new Worker());
		thread.start();
		return true;

	}

	/**
	 * 如果需要保存数据 则应该在此处保存数据 注意 这个函数不意味着结束
	 */
	@Override
	public boolean save() throws Exception {

		return true;

	}

	/**
	 * 如果有 应在此处打断工作线程 和剩余的关闭逻辑
	 * <p>
	 * 正常关闭情况下执行shut之前将会执行save
	 * <p>
	 * 有可能会使用 /admin init X 强制执行生命周期函数 但是此命令不属于正常使用范畴 可以不考虑此情况
	 */
	@Override
	public boolean shut() throws Exception {

		// 如果包含子线程 应在此时中断
		thread.interrupt();
		thread.join();
		return true;

	}

	/**
	 * 用于管理和debug /admin exec --module=demo xxx xxxx xxx xxxx xxxxx xxxxxxx
	 */
	@Override
	public String[] exec(Message message) throws Exception {

		return new String[] {
				"此模块无可用命令"
		};

	}

	/**
	 * 群成员增加时执行
	 */
	@Override
	public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) {

		// QQ系统通知为
		if (userid == 1000000) { entry.adminInfo("系统消息 - （" + gropid + "）"); }

	}

	/**
	 * 群成员减少时执行
	 */
	@Override
	public void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) {

	}

	/**
	 * 用户发送私聊时执行
	 */
	@Override
	public boolean doUserMessage(int typeid, long userid, MessageUser message, int messageid, int messagefont)
			throws Exception {

		return true;

	}

	/**
	 * 讨论组消息时执行
	 */
	@Override
	public boolean doDiszMessage(long diszid, long userid, MessageDisz message, int messageid, int messagefont)
			throws Exception {

		return true;

	}

	/***
	 * 群聊消息时执行
	 */
	@Override
	public boolean doGropMessage(long gropid, long userid, MessageGrop message, int messageid, int messagefont)
			throws Exception {

		// 不要使用JcpApp.CQ发送消息
		entry.gropInfo(gropid, userid, "MESSAGE");
		return true;

	}
	// ==========================================================================================================================================================
	//
	// 工具函数
	//
	// ==========================================================================================================================================================

	/***
	 * 生成模块报告 数组每个元素就会产生一条消息 避免消息过长
	 */
	@Override
	public String[] generateReport(int mode, Message message, Object... parameters) {

		return null;

	}

	class Worker implements Runnable {

		/**
		 * 模块应当自己负责计划任务 框架不提供统一的计划任务 必须按照此格式写Worker
		 */
		@SuppressWarnings("deprecation")
		@Override
		public void run() {

			// 成员变量
			long time;
			Date date;
			// 最外层循环用于处理发生异常时是否继续运行
			// 休眠被打断会产生InterruptedException
			do {
				try {
					// 实际工作循环
					while (true) {
						// 这是一个比较简约的延时计算
						date = new Date();
						// 假设00:00:00(24:00:00)运行
						// 从00:00:00多少秒后运行
						time = 86400L;
						// 减去当前秒数 以对齐秒 使其能在 xx:xx:00 执行
						time = time - date.getSeconds();
						// 减去当前分钟 以对齐分 使其能在 xx:00:00 执行
						time = time - date.getMinutes() * 60;
						// 减去当前分钟 以对齐时 使其能在 00:00:00 执行
						time = time - date.getHours() * 3600;
						// 转换为毫秒
						time = time * 1000;
						// 应当输出log以便于观察定时任务的状况
						entry.getCQ().logInfo(MODULE_PACKAGENAME, "休眠：" + time);
						Thread.sleep(time);
						// 应当输出log以便于观察定时任务的状况
						entry.getCQ().logInfo(MODULE_PACKAGENAME, "执行");
						// 此处执行实际任务
						// 应当输出log以便于观察定时任务的状况
						entry.getCQ().logInfo(MODULE_PACKAGENAME, "结果");
					}
				} catch (Exception exception) {
					// shut时 应打断休眠此时会产生异常
					if (entry.isEnable()) {
						// 如果框架运行中，则遇到了真正意义上的异常，应观察发生了什么
						entry.getCQ().logWarning(MODULE_PACKAGENAME, "异常");
						exception.printStackTrace();
					} else {
						entry.getCQ().logInfo(MODULE_PACKAGENAME, "关闭");
					}
				}
			} while (entry.isEnable());

		}

	}

}
