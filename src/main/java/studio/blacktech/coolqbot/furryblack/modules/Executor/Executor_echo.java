package studio.blacktech.coolqbot.furryblack.modules.Executor;

import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;
import studio.blacktech.coolqbot.furryblack.entry;

public class Executor_echo extends ModuleExecutor {

    private static final long serialVersionUID = 1L;

    // ==========================================================================================================================================================
    //
    // 模块基本配置
    //
    // ==========================================================================================================================================================

    private static String MODULE_PACKAGENAME = "Executor_Echo";
    private static String MODULE_COMMANDNAME = "echo";
    private static String MODULE_DISPLAYNAME = "回显";
    private static String MODULE_DESCRIPTION = "&0>1";
    private static String MODULE_VERSION = "1.1";
    private static String[] MODULE_USAGE = new String[]{
            "/echo 内容 - &0>1"
    };
    private static String[] MODULE_PRIVACY_STORED = new String[]{};
    private static String[] MODULE_PRIVACY_CACHED = new String[]{};
    private static String[] MODULE_PRIVACY_OBTAIN = new String[]{
            "获取命令发送人"
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

    public Executor_echo() throws Exception {

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
        entry.userInfo(userid, message.getSection() == 0 ? "echo null" : message.getOptions());
        return true;
    }

    @Override
    public boolean doDiszMessage(long diszid, long userid, MessageDisz message, int messageid, int messagefont) throws Exception {
        entry.diszInfo(diszid, userid, message.getSection() == 0 ? "echo null" : message.getOptions());
        return true;
    }

    @Override
    public boolean doGropMessage(long gropid, long userid, MessageGrop message, int messageid, int messagefont) throws Exception {
        entry.gropInfo(gropid, userid, message.getSection() == 0 ? "echo null" : message.getOptions());
        return true;
    }

    @Override
    public String[] generateReport(int mode, Message message, Object... parameters) {
        return new String[0];
    }

}
