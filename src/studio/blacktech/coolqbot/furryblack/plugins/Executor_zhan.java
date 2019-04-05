package studio.blacktech.coolqbot.furryblack.plugins;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.TreeMap;

import studio.blacktech.coolqbot.furryblack.module.Message;
import studio.blacktech.coolqbot.furryblack.module.Module;
import studio.blacktech.coolqbot.furryblack.module.ModuleExecutor;

public class Executor_zhan extends ModuleExecutor {

	private static TreeMap<Integer, String> CARD = new TreeMap<Integer, String>();
	private static ArrayList<Integer> FREQ = new ArrayList<Integer>();

	public Executor_zhan() {

		this.MODULE_DISPLAYNAME = "塔罗牌占卜";
		this.MODULE_PACKAGENAME = "zhan";
		this.MODULE_DESCRIPTION = "大阿卡那塔罗牌占卜";
		this.MODULE_VERSION = "2.2.3";
		this.MODULE_USAGE = new String[] {
				"//zhan 理由"
		};
		this.MODULE_PRIVACY_TRIGER = new String[] {};
		this.MODULE_PRIVACY_LISTEN = new String[] {};
		this.MODULE_PRIVACY_STORED = new String[] {};
		this.MODULE_PRIVACY_CACHED = new String[] {};
		this.MODULE_PRIVACY_OBTAIN = new String[] {
				"获取命令发送人"
		};

		Executor_zhan.CARD.put(1, "O. THE FOOL 愚者正位\r\n愚蠢 狂躁 挥霍无度 神志不清");
		Executor_zhan.CARD.put(2, "O. THE FOOL 愚者逆位\r\n疏忽 缺乏 暮气 无效 虚荣");
		Executor_zhan.CARD.put(3, "I. THE MAGICIAN 魔术师正位\r\n手段 灾难 痛苦 损失");
		Executor_zhan.CARD.put(4, "I. THE MAGICIAN 魔术师逆位\r\n羞辱 忧虑 精神疾病");
		Executor_zhan.CARD.put(5, "II. THE HIGH PRIESTESS 女祭司正位\r\n秘密 神秘 未来不明朗 英明");
		Executor_zhan.CARD.put(6, "II. THE HIGH PRIESTESS 女祭司逆位\r\n冲动 狂热 自负 浮于表面");
		Executor_zhan.CARD.put(7, "III. THE EMPRESS 皇后正位\r\n丰收 倡议 隐秘 困难 无知");
		Executor_zhan.CARD.put(8, "III. THE EMPRESS 皇后逆位\r\n光明 真相 喜悦");
		Executor_zhan.CARD.put(9, "IV. THE EMPEROR 皇帝正位\r\n稳定 力量 帮助 保护 信念");
		Executor_zhan.CARD.put(10, "IV. THE EMPEROR 皇帝逆位\r\n仁慈 同情 赞许 阻碍 不成熟");
		Executor_zhan.CARD.put(11, "V. THE HIEROPHANT 教皇正位\r\n宽恕 束缚 奴役 灵感");
		Executor_zhan.CARD.put(12, "V. THE HIEROPHANT 教皇逆位\r\n善解人意 和睦 过度善良 软弱");
		Executor_zhan.CARD.put(13, "VI. THE LOVERS 恋人正位\r\n吸引 爱 美丽 通过试炼");
		Executor_zhan.CARD.put(14, "VI. THE LOVERS 恋人逆位\r\n失败 愚蠢的设计");
		Executor_zhan.CARD.put(15, "VII. THE CHARIOT 战车正位\r\n救助 天意 胜利 复仇");
		Executor_zhan.CARD.put(16, "VII. THE CHARIOT 战车逆位\r\n打败 狂暴 吵架 诉讼");
		Executor_zhan.CARD.put(17, "VIII. THE STRENGTH 力量正位\r\n能量 行动 勇气 海量");
		Executor_zhan.CARD.put(18, "VIII. THE STRENGTH 力量逆位\r\n专断 弱点 滥用力量 不和");
		Executor_zhan.CARD.put(19, "IX. THE HERMIT 隐者正位\r\n慎重 叛徒 掩饰 堕落 恶事");
		Executor_zhan.CARD.put(20, "IX. THE HERMIT 隐者逆位\r\n隐蔽 害怕 伪装 过分小心");
		Executor_zhan.CARD.put(21, "X. THE WHEEL OF FORTUNE 命运之轮正位\r\n命运 好运 成功 幸福");
		Executor_zhan.CARD.put(22, "X. THE WHEEL OF FORTUNE 命运之轮逆位\r\n增加 丰富 多余");
		Executor_zhan.CARD.put(23, "XI. THE JUSTICE 正义正位\r\n公平 正义 廉洁 行政");
		Executor_zhan.CARD.put(24, "XI. THE JUSTICE 正义逆位\r\n偏执 不公 过度俭朴");
		Executor_zhan.CARD.put(25, "XII. THE HANGED MAN 吊人正位\r\n智慧 牺牲 审判 细心 眼光");
		Executor_zhan.CARD.put(26, "XII. THE HANGED MAN 吊人逆位\r\n自私 群众 人民");
		Executor_zhan.CARD.put(27, "XIII. DEATH 死亡正位\r\n终结 死亡 毁灭 腐朽");
		Executor_zhan.CARD.put(28, "XIII. DEATH 死亡逆位\r\n惯性 昏睡 石化 梦游");
		Executor_zhan.CARD.put(29, "XIV. TEMPERANCE 节制正位\r\n经济 适度 节俭 管理 住所");
		Executor_zhan.CARD.put(30, "XIV. TEMPERANCE 节制逆位\r\n教会 分离 不幸的组合 冲突的利益");
		Executor_zhan.CARD.put(31, "XV. THE DEVIL 恶魔正位\r\n毁坏 暴力 强迫 愤怒 额外努力 死亡");
		Executor_zhan.CARD.put(32, "XV. THE DEVIL 恶魔逆位\r\n死亡 弱点 盲目 琐事");
		Executor_zhan.CARD.put(33, "XVI. THE TOWER 高塔正位\r\n苦难 废墟 贫乏 耻辱 灾害 逆境 骗局");
		Executor_zhan.CARD.put(34, "XVI. THE TOWER 高塔逆位\r\n专断 监禁 受苦 损害");
		Executor_zhan.CARD.put(35, "XVII. THE STAR 星星正位\r\n丢失 窃贼 匮乏 放弃 未来的希望");
		Executor_zhan.CARD.put(36, "XVII. THE STAR 星星逆位\r\n傲慢 无能 傲气");
		Executor_zhan.CARD.put(37, "XVIII. THE MOON 月亮正位\r\n隐藏的敌人 诽谤 危险 黑暗 恐怖 错误");
		Executor_zhan.CARD.put(38, "XVIII. THE MOON 月亮逆位\r\n不稳定 易变 骗局 错误");
		Executor_zhan.CARD.put(39, "XIX. THE SUN 太阳正位\r\n喜悦 结婚 满意");
		Executor_zhan.CARD.put(40, "XIX. THE SUN 太阳逆位\r\n开心 满意");
		Executor_zhan.CARD.put(41, "XX. THE LAST JUDGMENT 审判正位\r\n变位 复兴 结果");
		Executor_zhan.CARD.put(42, "XX. THE LAST JUDGMENT 审判逆位\r\n弱点 胆怯 天真 决定 熟虑");
		Executor_zhan.CARD.put(43, "XXI. THE WORLD 世界正位\r\n成功 道路 航程 换位");
		Executor_zhan.CARD.put(44, "XXI. THE WORLD 世界逆位\r\n惯性 固执 停滞 持久");
		// 为什么不写循环？ 因为运行快
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
		Executor_zhan.FREQ.add(0);
	}

	@Override
	public boolean doUserMessage(int typeid, long userid, Message message, int messageid, int messagefont) throws Exception {
		Module.userInfo(userid, this.zhan(message));
		return true;
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, Message message, int messageid, int messagefont) throws Exception {
		Module.diszInfo(diszid, userid, this.zhan(message));
		return true;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, Message message, int messageid, int messagefont) throws Exception {
		Module.gropInfo(gropid, userid, this.zhan(message));
		return true;
	}

	public String zhan(Message message) {
		if (message.length == 1) {
			return "你不能占卜空气";
		} else {
			SecureRandom random = new SecureRandom();
			int urandom = random.nextInt(43) + 1;
			StringBuilder builder = new StringBuilder();
			builder.append("你因为 ");
			builder.append(message.join(1));
			builder.append(" 抽到了：\r\n");
			builder.append(Executor_zhan.CARD.get(urandom));
			Executor_zhan.FREQ.set(urandom, Executor_zhan.FREQ.get(urandom) + 1);
			return builder.toString();
		}
	}

	@Override
	public String getReport() {
		if (this.COUNT == 0) {
			return null;
		}
		final StringBuilder builder = new StringBuilder();
		int coverage = 0;
		for (int i = 0; i < 44; i++) {
			if (Executor_zhan.FREQ.get(i) == 0) {
				coverage++;
			}
		}
		coverage = 44 - coverage;
		for (int i = 0; i < 44; i++) {
			if (Executor_zhan.FREQ.get(i) == 0) {
				continue;
			}
			builder.append("\r\n第 ");
			builder.append(i + 1);
			builder.append(" 张 : ");
			builder.append(Executor_zhan.FREQ.get(i));
			builder.append(" - ");
			builder.append((Executor_zhan.FREQ.get(i) * 100) / coverage);
			builder.append("%");
		}
		return builder.toString();
	}
}
