package studio.blacktech.coolqbot.furryblack.modules.Executor;


import java.security.SecureRandom;
import java.util.TreeMap;

import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.common.annotation.ModuleExecutorComponent;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;


/**
 * 这个模块是应邀添加的
 * <p>
 * “那你不怕随机出来鸡蛋壳炒西瓜皮？”
 * <p>
 * “草”
 *
 * @author netuser
 */
@ModuleExecutorComponent
public class Executor_food extends ModuleExecutor {

	private static final long serialVersionUID = 1L;

	// ==========================================================================================================================================================
	//
	// 模块基本配置
	//
	// ==========================================================================================================================================================

	private static String MODULE_PACKAGENAME = "Executor_Food";
	private static String MODULE_COMMANDNAME = "food";
	private static String MODULE_DISPLAYNAME = "今天吃什么";
	private static String MODULE_DESCRIPTION = "随机生成看起来可以吃的东西";
	private static String MODULE_VERSION = "1.1";
	private static String[] MODULE_USAGE = new String[] {};
	public static String[] MODULE_PRIVACY_STORED = new String[] {};
	public static String[] MODULE_PRIVACY_CACHED = new String[] {};
	public static String[] MODULE_PRIVACY_OBTAIN = new String[] {};

	// ==========================================================================================================================================================
	//
	// 成员变量
	//
	// ==========================================================================================================================================================

	private TreeMap<Integer, TreeMap<Integer, String>> MENU;

	// ==========================================================================================================================================================
	//
	// 生命周期函数
	//
	// ==========================================================================================================================================================

	public Executor_food() throws Exception {

		super(MODULE_PACKAGENAME, MODULE_COMMANDNAME, MODULE_DISPLAYNAME, MODULE_DESCRIPTION, MODULE_VERSION, MODULE_USAGE, MODULE_PRIVACY_STORED, MODULE_PRIVACY_CACHED, MODULE_PRIVACY_OBTAIN);

	}

	@Override
	public boolean init() throws Exception {

		MENU = new TreeMap<>();

		// @formatter:off

        String[][] menu = new String[][]{

                {"快餐小吃", "自选快餐", "炒饭", "盖饭", "日式咖喱", "泰式咖喱", "印度咖喱", "麻辣烫", "肉夹馍", "黄焖鸡", "鸡公煲", "糯米鸡", "手抓饼", "臭豆腐", "水煮鱼", "酸菜鱼", "串串香"},
                {"米线粉丝汤", "泡面", "炒粉", "米线", "馄饨", "拉面", "烧饼", "土豆粉", "汆丸子", "云吞面", "热干面", "油泼面", "臊子面", "酸辣粉", "包子粥", "馄饨", "饺子", "肠粉"},
                {"西式", "汉堡", "披萨", "炸鸡", "牛排", "猪排", "意面", "沙拉", "千层面"},
                {"硬菜", "锅包肉", "清蒸白鱼", "猪肉炖粉条", "烤鸭", "锅巴菜", "驴肉火烧", "烩面", "九转大肠", "羊杂割", "葫芦鸡", "河西羊羔", "羊杂碎汤", "大盘鸡", "氽灌肠", "松鼠桂鱼", "八宝鸭", "西湖醋鱼", "麻婆豆腐", "辣子鸡", "沔阳三蒸", "剁椒鱼头", "佛跳墙", "臭鳜鱼", "螺蛳粉", "盐焗鸡", "汽锅鸡", "三杯鸡", "酸汤鱼", "清蒸羊羔肉", "烤全羊", "白切文昌鸡", "撒尿牛丸", "葡国鸡", "卤肉饭"},
                {"今天群主请客", "蒸羊羔", "蒸熊掌", "蒸鹿尾儿", "烧花鸭", "烧雏鸡", "烧子鹅", "卤猪", "卤鸭", "酱鸡", "腊肉", "松花", "小肚儿", "晾肉", "香肠儿", "什锦苏盘儿", "熏鸡白肚儿", "清蒸八宝猪", "江米酿鸭子", "罐儿野鸡", "罐儿鹌鹑", "卤什件儿", "卤子鹅", "山鸡", "兔脯", "菜蟒", "银鱼", "清蒸哈士蟆", "烩腰丝", "烩鸭腰", "烩鸭条", "清拌鸭丝儿", "黄心管儿", "焖白鳝", "焖黄鳝", "豆豉鲶鱼", "锅烧鲤鱼", "锅烧鲶鱼", "清蒸甲鱼", "抓炒鲤鱼", "抓炒对虾", "软炸里脊", "软炸鸡", "什锦套肠儿", "麻酥油卷儿", "卤煮寒鸦儿", "熘鲜蘑", "熘鱼脯", "熘鱼肚", "熘鱼骨", "熘鱼片儿", "醋熘肉片儿", "烩三鲜儿", "烩白蘑", "烩全饤儿", "烩鸽子蛋", "炒虾仁儿", "烩虾仁儿", "烩腰花儿", "烩海参", "炒蹄筋儿", "锅烧海参", "锅烧白菜", "炸开耳", "炒田鸡", "桂花翅子", "清蒸翅子", "炒飞禽", "炸什件儿", "清蒸江瑶柱", "糖熘芡实米", "拌鸡丝", "拌肚丝", "什锦豆腐", "什锦丁儿", "糟鸭", "糟蟹", "糟鱼", "糟熘鱼片", "熘蟹肉", "炒蟹肉", "清拌蟹肉", "蒸南瓜", "酿倭瓜", "炒丝瓜", "酿冬瓜", "焖鸡掌儿", "焖鸭掌儿", "焖笋", "烩茭白", "茄干晒炉肉", "鸭羹", "蟹肉羹", "三鲜木樨汤", "红丸子", "白丸子", "熘丸子", "炸丸子", "南煎丸子", "苜蓿丸子", "三鲜丸子", "四喜丸子", "鲜虾丸子", "鱼脯丸子", "饹炸丸子", "豆腐丸子", "氽丸子", "一品肉", "樱桃肉", "马牙肉", "红焖肉", "黄焖肉", "坛子肉", "烀肉", "扣肉", "松肉", "罐儿肉", "烧肉", "烤肉", "大肉", "白肉", "酱豆腐肉", "红肘子", "白肘子", "水晶肘子", "蜜蜡肘子", "酱豆腐肘子", "扒肘子", "炖羊肉", "烧羊肉", "烤羊肉", "煨羊肉", "涮羊肉", "五香羊肉", "爆羊肉", "氽三样儿", "爆三样儿", "烩银丝", "烩散丹", "熘白杂碎", "三鲜鱼翅", "栗子鸡", "煎氽活鲤鱼", "板鸭", "筒子鸡"},
                {"黑暗料理", "黑木耳炒土豆粉丝", "鸡蛋壳烩牛腩炒西瓜皮", "橙子炖三文鱼", "月饼炒西红柿", "鹌鹑蛋炒圣女果", "五花肉爆炒雪莲", "油爆枇杷", "巴黎水炒生姜", "香蕉皮炒猪皮", "神农小土豆炒葡萄皮", "瓜子皮拌秋葵", "菠菜炖蚕豆", "蛇果焗鸡胸", "西瓜汁配泥鳅", "烟台梨焖豆角"}

        };

        // @formatter:on

		for (int i = 1; i < (menu.length + 1); i++) {
			String[] tempmenu = menu[i - 1];
			TreeMap<Integer, String> tempmap = new TreeMap<>();
			for (int j = 0; j < tempmenu.length; j++) {
				tempmap.put(j, tempmenu[j]);
			}
			MENU.put(i, tempmap);
		}

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

		entry.userInfo(userid, chooseFood(message));
		return true;

	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, MessageDisz message, int messageid, int messagefont) throws Exception {

		entry.diszInfo(diszid, userid, chooseFood(message));
		return true;

	}

	@Override
	public boolean doGropMessage(long gropid, long userid, MessageGrop message, int messageid, int messagefont) throws Exception {

		entry.gropInfo(gropid, userid, chooseFood(message));
		return true;

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

	public String chooseFood(Message message) {


		StringBuilder builder = new StringBuilder();

		if (message.getSection() == 0) {
			for (int mode : MENU.keySet()) {
				TreeMap<Integer, String> temp = MENU.get(mode);
				builder.append("类别 " + mode + "：" + temp.get(0) + " " + (temp.size() - 1) + "种" + "\r\n");
			}
			builder.setLength(builder.length() - 2);

		} else {

			int mode = Integer.parseInt(message.getSegment(0));

			if (MENU.containsKey(mode)) {
				TreeMap<Integer, String> temp = MENU.get(mode);
				SecureRandom random = new SecureRandom();
				int res = random.nextInt(temp.size() - 1) + 1;
				builder.append("从 " + temp.get(0) + " 抽到了：" + temp.get(res));
			} else {
				builder.append("没有这个种类，你在想Peach。");
			}
		}

		return builder.toString();

	}

}
