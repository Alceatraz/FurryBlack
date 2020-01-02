package studio.blacktech.coolqbot.furryblack.modules.Executor;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.TreeMap;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.annotation.ModuleExecutorCompment;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;

@ModuleExecutorCompment(name = "Executor_zhan")
public class Executor_zhan extends ModuleExecutor {

	private static final long serialVersionUID = 1L;

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "Executor_Zhan";
	private static String MODULE_COMMANDNAME = "zhan";
	private static String MODULE_DISPLAYNAME = "占卜";
	private static String MODULE_DESCRIPTION = "大阿卡那塔罗牌占卜";
	private static String MODULE_VERSION = "1.0";
	private static String[] MODULE_USAGE = new String[] {
			"/zhan 理由 - 为某事占卜"
	};

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

	private TreeMap<Integer, String> CARD;
	private ArrayList<Integer> FREQ;

	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================

	public Executor_zhan() throws Exception {

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

	@Override
	public boolean init() throws Exception {

		this.CARD = new TreeMap<>();
		this.FREQ = new ArrayList<>();

		// =======================================
		//
		// 为什么不读配置文件？
		// 我有理由相信，塔罗牌会保持44张不会变
		//
		// =======================================

		this.CARD.put(1, "O. THE FOOL 愚者正位\r\n愚蠢 狂躁 挥霍无度 神志不清");
		this.CARD.put(2, "O. THE FOOL 愚者逆位\r\n疏忽 缺乏 暮气 无效 虚荣");
		this.CARD.put(3, "I. THE MAGICIAN 魔术师正位\r\n手段 灾难 痛苦 损失");
		this.CARD.put(4, "I. THE MAGICIAN 魔术师逆位\r\n羞辱 忧虑 精神疾病");
		this.CARD.put(5, "II. THE HIGH PRIESTESS 女祭司正位\r\n秘密 神秘 未来不明朗 英明");
		this.CARD.put(6, "II. THE HIGH PRIESTESS 女祭司逆位\r\n冲动 狂热 自负 浮于表面");
		this.CARD.put(7, "III. THE EMPRESS 皇后正位\r\n丰收 倡议 隐秘 困难 无知");
		this.CARD.put(8, "III. THE EMPRESS 皇后逆位\r\n光明 真相 喜悦");
		this.CARD.put(9, "IV. THE EMPEROR 皇帝正位\r\n稳定 力量 帮助 保护 信念");
		this.CARD.put(10, "IV. THE EMPEROR 皇帝逆位\r\n仁慈 同情 赞许 阻碍 不成熟");
		this.CARD.put(11, "V. THE HIEROPHANT 教皇正位\r\n宽恕 束缚 奴役 灵感");
		this.CARD.put(12, "V. THE HIEROPHANT 教皇逆位\r\n善解人意 和睦 过度善良 软弱");
		this.CARD.put(13, "VI. THE LOVERS 恋人正位\r\n吸引 爱 美丽 通过试炼");
		this.CARD.put(14, "VI. THE LOVERS 恋人逆位\r\n失败 愚蠢的设计");
		this.CARD.put(15, "VII. THE CHARIOT 战车正位\r\n救助 天意 胜利 复仇");
		this.CARD.put(16, "VII. THE CHARIOT 战车逆位\r\n打败 狂暴 吵架 诉讼");
		this.CARD.put(17, "VIII. THE STRENGTH 力量正位\r\n能量 行动 勇气 海量");
		this.CARD.put(18, "VIII. THE STRENGTH 力量逆位\r\n专断 弱点 滥用力量 不和");
		this.CARD.put(19, "IX. THE HERMIT 隐者正位\r\n慎重 叛徒 掩饰 堕落 恶事");
		this.CARD.put(20, "IX. THE HERMIT 隐者逆位\r\n隐蔽 害怕 伪装 过分小心");
		this.CARD.put(21, "X. THE WHEEL OF FORTUNE 命运之轮正位\r\n命运 好运 成功 幸福");
		this.CARD.put(22, "X. THE WHEEL OF FORTUNE 命运之轮逆位\r\n增加 丰富 多余");
		this.CARD.put(23, "XI. THE JUSTICE 正义正位\r\n公平 正义 廉洁 行政");
		this.CARD.put(24, "XI. THE JUSTICE 正义逆位\r\n偏执 不公 过度俭朴");
		this.CARD.put(25, "XII. THE HANGED MAN 吊人正位\r\n智慧 牺牲 审判 细心 眼光");
		this.CARD.put(26, "XII. THE HANGED MAN 吊人逆位\r\n自私 群众 人民");
		this.CARD.put(27, "XIII. DEATH 死亡正位\r\n终结 死亡 毁灭 腐朽");
		this.CARD.put(28, "XIII. DEATH 死亡逆位\r\n惯性 石化 梦游 昏 睡");
		this.CARD.put(29, "XIV. TEMPERANCE 节制正位\r\n经济 适度 节俭 管理 住所");
		this.CARD.put(30, "XIV. TEMPERANCE 节制逆位\r\n教会 分离 不幸的组合 冲突的利益");
		this.CARD.put(31, "XV. THE DEVIL 恶魔正位\r\n毁坏 暴力 强迫 愤怒 额外努力 死亡");
		this.CARD.put(32, "XV. THE DEVIL 恶魔逆位\r\n死亡 弱点 盲目 琐事");
		this.CARD.put(33, "XVI. THE TOWER 高塔正位\r\n苦难 废墟 贫乏 耻辱 灾害 逆境 骗局");
		this.CARD.put(34, "XVI. THE TOWER 高塔逆位\r\n专断 监禁 受苦 损害");
		this.CARD.put(35, "XVII. THE STAR 星星正位\r\n丢失 窃贼 匮乏 放弃 未来的希望");
		this.CARD.put(36, "XVII. THE STAR 星星逆位\r\n傲慢 无能 傲气");
		this.CARD.put(37, "XVIII. THE MOON 月亮正位\r\n隐藏的敌人 诽谤 危险 黑暗 恐怖 错误");
		this.CARD.put(38, "XVIII. THE MOON 月亮逆位\r\n不稳定 易变 骗局 错误");
		this.CARD.put(39, "XIX. THE SUN 太阳正位\r\n喜悦 结婚 满意");
		this.CARD.put(40, "XIX. THE SUN 太阳逆位\r\n开心 满意");
		this.CARD.put(41, "XX. THE LAST JUDGMENT 审判正位\r\n变位 复兴 结果");
		this.CARD.put(42, "XX. THE LAST JUDGMENT 审判逆位\r\n弱点 胆怯 天真 决定 熟虑");
		this.CARD.put(43, "XXI. THE WORLD 世界正位\r\n成功 道路 航程 换位");
		this.CARD.put(44, "XXI. THE WORLD 世界逆位\r\n惯性 固执 停滞 持久");

		// @formatter:off
		this.FREQ.add(0);        this.FREQ.add(0);        this.FREQ.add(0);        this.FREQ.add(0);        this.FREQ.add(0);
		this.FREQ.add(0);        this.FREQ.add(0);        this.FREQ.add(0);        this.FREQ.add(0);        this.FREQ.add(0);
		this.FREQ.add(0);        this.FREQ.add(0);        this.FREQ.add(0);        this.FREQ.add(0);        this.FREQ.add(0);
		this.FREQ.add(0);        this.FREQ.add(0);        this.FREQ.add(0);        this.FREQ.add(0);        this.FREQ.add(0);
		this.FREQ.add(0);        this.FREQ.add(0);        this.FREQ.add(0);        this.FREQ.add(0);        this.FREQ.add(0);
		this.FREQ.add(0);        this.FREQ.add(0);        this.FREQ.add(0);        this.FREQ.add(0);        this.FREQ.add(0);
		this.FREQ.add(0);        this.FREQ.add(0);        this.FREQ.add(0);        this.FREQ.add(0);        this.FREQ.add(0);
		this.FREQ.add(0);        this.FREQ.add(0);        this.FREQ.add(0);        this.FREQ.add(0);        this.FREQ.add(0);
		this.FREQ.add(0);        this.FREQ.add(0);        this.FREQ.add(0);        this.FREQ.add(0);        // @formatter:on

		this.ENABLE_USER = true;
		this.ENABLE_DISZ = true;
		this.ENABLE_GROP = true;
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
		if (message.getSection() == 0) {
			entry.userInfo(userid, "你不能占卜空气");
		} else {
			SecureRandom random = new SecureRandom();
			int urandom = random.nextInt(43) + 1;
			StringBuilder builder = new StringBuilder();
			builder.append("你因为 ");
			builder.append(message.getOptions());
			builder.append(" 抽到了：\r\n");
			builder.append(this.CARD.get(urandom));
			this.FREQ.set(urandom, this.FREQ.get(urandom) + 1);
			entry.userInfo(userid, builder.toString());
		}
		return true;
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, MessageDisz message, int messageid, int messagefont) throws Exception {
		if (message.getSection() == 0) {
			entry.diszInfo(diszid, userid, "你不能占卜空气");
		} else {
			SecureRandom random = new SecureRandom();
			int urandom = random.nextInt(43) + 1;
			StringBuilder builder = new StringBuilder();
			builder.append("你因为 ");
			builder.append(message.getOptions());
			builder.append(" 抽到了：\r\n");
			builder.append(this.CARD.get(urandom));
			this.FREQ.set(urandom, this.FREQ.get(urandom) + 1);
			entry.diszInfo(diszid, userid, builder.toString());
		}
		return true;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, MessageGrop message, int messageid, int messagefont) throws Exception {
		if (message.getSection() == 0) {
			entry.gropInfo(gropid, userid, "你不能占卜空气");
		} else {
			SecureRandom random = new SecureRandom();
			int urandom = random.nextInt(43) + 1;
			StringBuilder builder = new StringBuilder();
			builder.append("你因为 ");
			builder.append(message.getOptions());
			builder.append(" 抽到了：\r\n");
			builder.append(this.CARD.get(urandom));
			this.FREQ.set(urandom, this.FREQ.get(urandom) + 1);
			entry.gropInfo(gropid, userid, builder.toString());
		}
		return true;
	}

	@Override
	public String[] generateReport(int mode, Message message, Object... parameters) {
		if ((this.COUNT_USER + this.COUNT_DISZ + this.COUNT_GROP) == 0) { return null; }
		StringBuilder builder = new StringBuilder();
		int coverage = 0;
		for (int i = 0; i < 44; i++) {
			if (this.FREQ.get(i) == 0) { coverage++; }
		}
		coverage = 44 - coverage;
		for (int i = 0; i < 44; i++) {
			if (this.FREQ.get(i) == 0) { continue; }
			builder.append("\r\n第 ");
			builder.append(i + 1);
			builder.append(" 张 : ");
			builder.append(this.FREQ.get(i));
			builder.append(" - ");
			builder.append((this.FREQ.get(i) * 100) / coverage);
			builder.append("%");
		}
		String[] res = new String[] {
				builder.toString()
		};
		return res;
	}

}
