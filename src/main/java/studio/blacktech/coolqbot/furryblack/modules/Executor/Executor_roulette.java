package studio.blacktech.coolqbot.furryblack.modules.Executor;

import org.meowy.cqp.jcq.entity.Member;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;
import studio.blacktech.coolqbot.furryblack.entry;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Executor_roulette extends ModuleExecutor {

    private static final long serialVersionUID = 1L;

    // ==========================================================================================================================================================
    //
    // 模块基本配置
    //
    // ==========================================================================================================================================================

    private static String MODULE_PACKAGENAME = "Executor_Roulette";
    private static String MODULE_COMMANDNAME = "roulette";
    private static String MODULE_DISPLAYNAME = "俄罗斯轮盘赌";
    private static String MODULE_DESCRIPTION = "你看这子弹又尖又长，这名单又大又宽";
    private static String MODULE_VERSION = "1.0";
    private static String[] MODULE_USAGE = new String[]{
            "/roulette 筹码 - 加入或者发起一局俄罗斯轮盘赌，十分钟仍未满员则自动解散对局"
    };
    private static String[] MODULE_PRIVACY_STORED = new String[]{};
    private static String[] MODULE_PRIVACY_CACHED = new String[]{
            "按照\"群-成员-回合\"的层级关系存储 - 回合结束或超时后下一次第一名玩家加入时释放"
    };
    private static String[] MODULE_PRIVACY_OBTAIN = new String[]{
            "获取命令发送人"
    };

    // ==========================================================================================================================================================
    //
    // 成员变量
    //
    // ==========================================================================================================================================================

    private HashMap<Long, RouletteRound> ROULETTE_ROUNDS;
    private ArrayList<Integer> ROULETTE_FREQ;
    private int ROUND_EXPIRED = 0;
    private int ROUND_SUCCESS = 0;

    // ==========================================================================================================================================================
    //
    // 生命周期函数
    //
    // ==========================================================================================================================================================

    public Executor_roulette() throws Exception {

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

        ROULETTE_ROUNDS = new HashMap<>();
        ROULETTE_FREQ = new ArrayList<>();

        ROULETTE_FREQ.add(0);
        ROULETTE_FREQ.add(0);
        ROULETTE_FREQ.add(0);
        ROULETTE_FREQ.add(0);
        ROULETTE_FREQ.add(0);
        ROULETTE_FREQ.add(0);

        ENABLE_USER = false;
        ENABLE_DISZ = false;
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
        return new String[]{
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
        return true;
    }

    @Override
    public boolean doDiszMessage(long diszid, long userid, MessageDisz message, int messageid, int messagefont) throws Exception {
        return true;
    }

    @Override
    public boolean doGropMessage(long gropid, long userid, MessageGrop message, int messageid, int messagefont) throws Exception {

        // 只有命令 没下注
        if (message.getSection() == 0) {
            entry.gropInfo(gropid, userid, "不下注是8koi的");
            return true;
        }

        // 对局不存在 创建一个
        if (!ROULETTE_ROUNDS.containsKey(gropid)) {
            ROULETTE_ROUNDS.put(gropid, new RouletteRound());
        }

        // 获取本群对局
        RouletteRound round = ROULETTE_ROUNDS.get(gropid);

        if (round.lock) {
            // 本来是有锁的，但是我觉得没人能正好在100ms内再加入所以删了
            // SX found this BUG
            // Module.gropInfo(gropid, "你是最佳第七人，你妈妈不爱你，你甚至不配拥有名字。");
            return false;
        }

        // 对局超时就新建一个
        if ((round.time.getTime() + 600000) < new Date().getTime()) {
            ROUND_EXPIRED++;
            round = new RouletteRound();
            ROULETTE_ROUNDS.remove(gropid);
            ROULETTE_ROUNDS.put(gropid, round);
        }

        if (round.join(gropid, userid, message)) {

            entry.gropInfo(gropid, "名单已凑齐 装填子弹中");
            int bullet = new SecureRandom().nextInt(6);
            Member member;
            for (int i = 0; i < 6; i++) {
                member = entry.getCQ().getGroupMemberInfo(gropid, round.player.get(i));
                if (i == bullet) {
                    ROULETTE_FREQ.set(i, ROULETTE_FREQ.get(i) + 1);
                    entry.gropInfo(gropid, entry.getGropnick(gropid, member.getQQId()) + " (" + round.player.get(i) + "): [CQ:face,id=169][CQ:emoji,id=10060]");
                } else {
                    entry.gropInfo(gropid, entry.getGropnick(gropid, member.getQQId()) + " (" + round.player.get(i) + "): [CQ:face,id=169][CQ:emoji,id=11093]");
                }

            }
            entry.gropInfo(gropid, "@平安中国 目标已击毙:  [CQ:at,qq=" + round.player.get(bullet) + "]\r\n" + round.chip.get(bullet));
            ROULETTE_ROUNDS.remove(gropid);
            ROUND_SUCCESS++;
        }

        return true;
    }

    @Override
    public String[] generateReport(int mode, Message message, Object... parameters) {
        if ((COUNT_USER + COUNT_DISZ + COUNT_GROP) == 0) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("成功回合 : ");
        builder.append(ROUND_SUCCESS);
        builder.append("\r\n失败回合 : ");
        builder.append(ROUND_EXPIRED);
        if (ROUND_SUCCESS > 0) {
            builder.append("\r\n第一发 : ");
            builder.append(ROULETTE_FREQ.get(0));
            builder.append(" - ");
            builder.append((ROULETTE_FREQ.get(0) * 100) / ROUND_SUCCESS);
            builder.append("%\r\n第二发 : ");
            builder.append(ROULETTE_FREQ.get(1));
            builder.append(" - ");
            builder.append((ROULETTE_FREQ.get(1) * 100) / ROUND_SUCCESS);
            builder.append("%\r\n第三发 : ");
            builder.append(ROULETTE_FREQ.get(2));
            builder.append(" - ");
            builder.append((ROULETTE_FREQ.get(2) * 100) / ROUND_SUCCESS);
            builder.append("%\r\n第四发 : ");
            builder.append(ROULETTE_FREQ.get(3));
            builder.append(" - ");
            builder.append((ROULETTE_FREQ.get(3) * 100) / ROUND_SUCCESS);
            builder.append("%\r\n第五发 : ");
            builder.append(ROULETTE_FREQ.get(4));
            builder.append(" - ");
            builder.append((ROULETTE_FREQ.get(4) * 100) / ROUND_SUCCESS);
            builder.append("%\r\n第六发 : ");
            builder.append(ROULETTE_FREQ.get(5));
            builder.append(" - ");
            builder.append((ROULETTE_FREQ.get(5) * 100) / ROUND_SUCCESS);
            builder.append("%");
        }
        String[] res = new String[]{
                builder.toString()
        };
        return res;
    }

    private class RouletteRound {

        public Date time;

        ArrayList<String> chip = new ArrayList<>();
        ArrayList<Long> player = new ArrayList<>();

        int players = 0;
        boolean lock = false;

        RouletteRound() {
            time = new Date();
        }

        public boolean join(long gropid, long userid, Message message) {
            if (player.contains(userid)) {
                entry.gropInfo(gropid, "你8koi离开，不准放过");
            } else {
                time = new Date();
                players++;
                player.add(userid);
                chip.add(message.getOptions());
                StringBuilder buffer = new StringBuilder();
                buffer.append("俄罗斯轮盘 - 当前人数 (");
                buffer.append(players);
                buffer.append("/6)");
                int i = 0;
                for (; i < players; i++) {
                    buffer.append("\r\n");
                    buffer.append(i + 1);
                    buffer.append(" - ");
                    buffer.append(player.get(i));
                    buffer.append(" : ");
                    buffer.append(chip.get(i));
                }
                for (; i < 6; i++) {
                    buffer.append("\r\n");
                    buffer.append(i + 1);
                    buffer.append(" - 等待加入");
                }
                entry.gropInfo(gropid, buffer.toString());
            }
            lock = players > 5;
            return lock;
        }
    }
}
