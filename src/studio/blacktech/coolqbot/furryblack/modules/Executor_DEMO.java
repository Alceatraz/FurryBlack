package studio.blacktech.coolqbot.furryblack.modules;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;

public class Executor_DEMO extends ModuleExecutor {

	// ==========================================================================================================================================================
	//
	// 此模块为模板模块
	// 用于新建模块时快速复制以及教学示例用
	//
	// ==========================================================================================================================================================

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	// PACKAGENAME 名称为完整名称
	// 命名规则应为 类型_名称
	private static String MODULE_PACKAGENAME = "executor_null";

	// COMMANDNAME 名称为命令调用的名称
	// 应和PACKAGE名称一致
	private static String MODULE_COMMANDNAME = "null";

	// DISPLAYNAME 名称为人类可读的友好名称 应该在8个字以内
	private static String MODULE_DISPLAYNAME = "实例模块";

	// DESCRIPTON 为模块功能简介
	private static String MODULE_DESCRIPTION = "实例模块";

	// 版本号
	private static String MODULE_VERSION = "1.0.0";

	// 命令用法，数组的每个元素应为一个参数组合用法及其说明
	// 减号左右各一个空格
	private static String[] MODULE_USAGE = new String[] {
			"命令1 - 命令用法1",
			"命令2 - 命令用法2",
			"命令3 - 命令用法3",
			"命令4 - 命令用法4",
	};

	// 如果注册为触发器 则应写明此模块功能 且必须全部列出
	public static String[] MODULE_PRIVACY_TRIGER = new String[] {
			"触发器 - 功能"
	};

	// 如果注册为监听器 则应写明此模块功能 且必须全部列出
	public static String[] MODULE_PRIVACY_LISTEN = new String[] {
			"监听器 - 功能"
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

	HashMap<String, String> MAP;

	private File FILE_CUSTOM;

	private boolean ENABLE_DEMO = false;

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
	public Executor_DEMO() throws Exception {
		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_TRIGER, MODULE_PRIVACY_LISTEN, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);
	}

	/***
	 * 初始化阶段
	 *
	 * 1：初始化配置及数据文件 2：生成所有内存结构 3：读取配置 4：分析 ENABLE_MODE
	 */
	@Override
	public void init(LoggerX logger) throws Exception {

		// ==================================================================================
		// 1：初始化配置及数据文件

		this.initConfFolder();
		this.initDataFolder();

		this.initCofigurtion();

		// ==================================================================================
		// 2：生成所有内存结构
		// 应在此处实例化成员变量

		this.MAP = new HashMap<>();

		// 关于文件路径：应使用Paths以及内置的 FOLDER_CONF FOLDER_DATA 来表示文件
		this.FILE_CUSTOM = Paths.get(this.FOLDER_CONF.getAbsolutePath(), "custom.txt").toFile();

		if (!this.FILE_CUSTOM.exists()) { this.FILE_CUSTOM.createNewFile(); }

		// ==================================================================================
		// 3：读取配置
		// NEW_CONFIG=true 为初始化过程中发现配置不存在 创建了新的配置

		if (this.NEW_CONFIG) {
			// CONFIG对象为标准Java property对象
			this.CONFIG.setProperty("enable", "true");
			this.CONFIG.setProperty("config1", "none");
			this.CONFIG.setProperty("config2", "none");
			this.CONFIG.setProperty("config3", "none");
			this.CONFIG.setProperty("config4", "none");
			this.saveConfig();
		} else {
			this.loadConfig();
		}

		this.ENABLE_DEMO = Boolean.parseBoolean(this.CONFIG.getProperty("enable"));

		// ==================================================================================
		// 4：分析 ENABLE_MODE
		// ENABLE_MODE = false 时，由systemd注册插件时将不会注册
		// 此设计的目的是比如被配置禁用 则直接跳注册阶段，比每次都if判断效率高

		if (this.ENABLE_DEMO) {
			this.ENABLE_USER = true;
			this.ENABLE_DISZ = true;
			this.ENABLE_GROP = true;
		}

	}

	/***
	 * 如果ENABLE_MODE=false则不会注册 则不会执行boot的内容
	 */
	@Override
	public void boot(LoggerX logger) throws Exception {
		// 如果包含子线程 应在此时启动
	}

	/***
	 * 应在此处运行关闭逻辑
	 */
	@Override
	public void shut(LoggerX logger) throws Exception {
		// 如果包含子线程 应在此时中断
	}

	/***
	 * 重载配置使用
	 */
	@Override
	public void reload(LoggerX logger) throws Exception {
		// 暂时不会被调用
	}

	/***
	 * 群成员增加时执行
	 */
	@Override
	public void groupMemberIncrease(int typeid, int sendtime, long gropid, long operid, long userid) {
		// 为了提升性能 不应该每次执行event都获取成员 应启动时先读取群成员列表 生成相关的内容 使用时直接获取
		// QQ系统通知为
		if (userid == 1000000) { entry.getMessage().adminInfo("系统消息 - （" + gropid + "）"); }
	}

	/***
	 * 群成员减少时执行
	 */
	@Override
	public void groupMemberDecrease(int typeid, int sendtime, long gropid, long operid, long userid) {
	}

	/***
	 * 用户发送私聊时执行
	 */
	@Override
	public boolean doUserMessage(int typeid, long userid, MessageUser message, int messageid, int messagefont) throws Exception {
		return true;
	}

	/***
	 * 讨论组消息时执行
	 */
	@Override
	public boolean doDiszMessage(long diszid, long userid, MessageDisz message, int messageid, int messagefont) throws Exception {
		return true;
	}

	/***
	 * 群聊消息时执行
	 */
	@Override
	public boolean doGropMessage(long gropid, long userid, MessageGrop message, int messageid, int messagefont) throws Exception {
		// 不要使用JcpApp.CQ发送消息，Message实现了撤回功能（仅限Pro版，Air版会失败并提示付费）
		entry.getMessage().gropInfo(gropid, userid, "MESSAGE");
		entry.getMessage().revokeMessage(gropid);
		return true;
	}

	// ==========================================================================================================================================================
	//
	// 工具函数
	//
	// ==========================================================================================================================================================

	/***
	 * 生成模块报告
	 */
	@Override
	public String[] generateReport(int mode, Message message, Object... parameters) {
		return null;
	}

}
