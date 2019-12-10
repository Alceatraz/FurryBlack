package studio.blacktech.coolqbot.furryblack.modules.Executor;

import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;
import studio.blacktech.coolqbot.furryblack.entry;

import java.math.BigInteger;
import java.util.HashMap;

public class Executor_acon extends ModuleExecutor {

    private static final long serialVersionUID = 1L;

    // ==========================================================================================================================================================
    //
    // 模块基本配置
    //
    // ==========================================================================================================================================================
    public static String[] MODULE_PRIVACY_OBTAIN = new String[]{};
    private static String MODULE_PACKAGENAME = "Executor_Acon";
    private static String MODULE_COMMANDNAME = "acon";
    private static String MODULE_DISPLAYNAME = "空调";
    private static String MODULE_DESCRIPTION = "本群冷气开放";
    private static String MODULE_VERSION = "3.1";
    private static String[] MODULE_USAGE = new String[]{
            "/acon cost - 耗电量",
            "/acon off - 关机",
            "/acon wet - 加湿",
            "/acon dry - 除湿",
            "/acon cold - 制冰模式",
            "/acon cool - 制冷模式",
            "/acon warm - 制热模式",
            "/acon bake - 烘烤模式",
            "/acon burn - 烧烤模式",
            "/acon fire - 焚化模式",
            "/acon c2h2 - 乙炔炬模式",
            "/acon argon - 氩气引弧模式",
            "/acon plasma - 等离子模式",
            "/acon nova - 点亮一颗新星",
            "/acon cfnuke - 点燃一颗冷核武器",
            "/acon trnuke - 点燃一颗热核武器",
            "/acon tpnuke - 点燃一颗三相热核弹",
            "/acon ianova - Ia级超新星吸积引燃",
            "/acon ibnova - Ib级超新星吸积引燃",
            "/acon icnova - Ic级超新星吸积引燃",
            "/acon iinova - II级超新星吸积引燃",
            "/acon ~!C??? - Fy:????",
            "/acon ~!R[?? - FT//s??"
    };
    private static String[] MODULE_PRIVACY_STORED = new String[]{};
    private static String[] MODULE_PRIVACY_CACHED = new String[]{
            "按群存储耗电量 - JCQ停止时释放",
            "按群存储耗工作模式 - JCQ停止时释放",
            "按群存储上次更改模式的时间戳 - JCQ停止时释放",
    };

    // ==========================================================================================================================================================
    //
    // 成员变量
    //
    // ==========================================================================================================================================================
    private HashMap<Long, BigInteger> CONSUMPTION;
    private HashMap<Long, Long> LASTCHANGED;
    private HashMap<Long, Long> WORKINGMODE;

    // ==========================================================================================================================================================
    //
    // 生命周期函数
    //
    // ==========================================================================================================================================================

    public Executor_acon() throws Exception {


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


    }

    @Override
    public boolean init() throws Exception {

        CONSUMPTION = new HashMap<>();
        LASTCHANGED = new HashMap<>();
        WORKINGMODE = new HashMap<>();

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
    public boolean shut() throws Exception {
        return true;
    }

    @Override
    public boolean save() throws Exception {
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

        long currentTime = System.currentTimeMillis() / 1000;
        long elapseTime = 0L;

        if (!CONSUMPTION.containsKey(gropid)) {
            CONSUMPTION.put(gropid, BigInteger.ZERO);
            LASTCHANGED.put(gropid, currentTime);
            WORKINGMODE.put(gropid, 0L);
        }

        if (message.getSection() > 0) {

            BigInteger powerConsumption = CONSUMPTION.get(gropid);
            long lastChangModeTime = LASTCHANGED.get(gropid);
            long workingmode = WORKINGMODE.get(gropid);

            elapseTime = currentTime - lastChangModeTime;

            boolean isChangeMode = true;

            switch (message.getSegment()[0]) {

                case "off":
                    entry.gropInfo(gropid, "空调已关闭");
                    WORKINGMODE.put(gropid, 1L);
                    break;

                case "dry":
                    entry.gropInfo(gropid, "切换至除湿模式");
                    WORKINGMODE.put(gropid, 5880L);
                    break;

                case "wet":
                    entry.gropInfo(gropid, "切换至加湿模式");
                    WORKINGMODE.put(gropid, 5880L);
                    break;

                case "cold":
                    entry.gropInfo(gropid, "切换至制冰模式 -20°");
                    WORKINGMODE.put(gropid, 14700L);
                    break;

                case "cool":
                    entry.gropInfo(gropid, "切换至制冷模式 26.5°");
                    WORKINGMODE.put(gropid, 7350L);
                    break;

                case "warm":
                    entry.gropInfo(gropid, "切换至制热模式 25.5°");
                    WORKINGMODE.put(gropid, 7350L);
                    break;

                case "bake":
                    entry.gropInfo(gropid, "切换至烘烤模式 285°");
                    WORKINGMODE.put(gropid, 14700L);
                    break;

                case "burn":
                    entry.gropInfo(gropid, "切换至烧烤模式 960°");
                    WORKINGMODE.put(gropid, 22050L);
                    break;

                case "fire":
                    entry.gropInfo(gropid, "切换至焚化模式 1,200°");
                    WORKINGMODE.put(gropid, 29400L);
                    break;

                case "c2h2":
                    entry.gropInfo(gropid, "切换至乙炔炬模式 3,300°");
                    WORKINGMODE.put(gropid, 33075L);
                    break;

                case "argon":
                    entry.gropInfo(gropid, "切换至氩气弧模式 7,550°");
                    WORKINGMODE.put(gropid, 36750L);
                    break;

                case "plasma":
                    entry.gropInfo(gropid, "切换至等离子模式 23,500°");
                    WORKINGMODE.put(gropid, 44100L);
                    break;

                case "nova":
                    entry.gropInfo(gropid, "切换至新星模式 1,000,000°");
                    WORKINGMODE.put(gropid, 7350000L);
                    break;

                case "cfnuke":
                    entry.gropInfo(gropid, "切换至冷核模式 100,000,000°");
                    WORKINGMODE.put(gropid, 29400000L);
                    break;

                case "trnuke":
                    entry.gropInfo(gropid, "切换至热核模式 120,000,000°");
                    WORKINGMODE.put(gropid, 33075000L);
                    break;

                case "tfnuke":
                    entry.gropInfo(gropid, "切换至三相热核模式 150,000,000°");
                    WORKINGMODE.put(gropid, 44100000L);
                    break;

                case "ianova":
                    entry.gropInfo(gropid, "切换至Ia星爆发模式 800,000,000°");
                    WORKINGMODE.put(gropid, 294000000L);
                    break;

                case "ibnova":
                    entry.gropInfo(gropid, "切换至Ib新星爆发模式 2,600,000,000°");
                    WORKINGMODE.put(gropid, 330750000L);
                    break;

                case "icnova":
                    entry.gropInfo(gropid, "切换至Ic新星爆发模式 2,800,000,000°");
                    WORKINGMODE.put(gropid, 441000000L);
                    break;

                case "iinova":
                    entry.gropInfo(gropid, "切换至II新星爆发模式 3,000,000,000°");
                    WORKINGMODE.put(gropid, 514500000L);
                    break;

                case "samrage":
                    entry.gropInfo(gropid, "父王之怒 10,000,000,000,000,000,000,000,000,000°");
                    WORKINGMODE.put(gropid, 73500000000L);
                    break;

                case "samrape":
                    entry.gropInfo(gropid, "父王之怒 -273.16°");
                    WORKINGMODE.put(gropid, 73500000000L);
                    break;

                case "cost":

                    powerConsumption = powerConsumption.add(BigInteger.valueOf(elapseTime * workingmode));

                    isChangeMode = false;


                    entry.gropInfo(gropid, String.format("累计共耗电：%skW(%s)度\r\n群主须支付：%s元",
                            powerConsumption.divide(BigInteger.valueOf(1000)).toString(),
                            powerConsumption.divide(BigInteger.valueOf(3600000L)).toString(),
                            powerConsumption.divide(BigInteger.valueOf(1936800L)).toString()
                    ));


                    break;

                default:
                    break;

            }

            if (isChangeMode) {
                powerConsumption = powerConsumption.add(BigInteger.valueOf(elapseTime * workingmode));
            }

            CONSUMPTION.put(gropid, powerConsumption);
            LASTCHANGED.put(gropid, currentTime);

        }

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

}