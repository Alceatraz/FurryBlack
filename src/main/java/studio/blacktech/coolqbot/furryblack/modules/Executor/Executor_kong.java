package studio.blacktech.coolqbot.furryblack.modules.Executor;

import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;
import studio.blacktech.coolqbot.furryblack.common.module.ModuleExecutor;
import studio.blacktech.coolqbot.furryblack.entry;

public class Executor_kong extends ModuleExecutor {

    private static final long serialVersionUID = 1L;

    // ==========================================================================================================================================================
    //
    // 模块基本配置
    //
    // ==========================================================================================================================================================

    private static String MODULE_PACKAGENAME = "Executor_Kong";
    private static String MODULE_COMMANDNAME = "kong";
    private static String MODULE_DISPLAYNAME = "变臭";
    private static String MODULE_DESCRIPTION = "给文字添加空格";
    private static String MODULE_VERSION = "1.0";
    private static String[] MODULE_USAGE = new String[]{
            "/kong 原句 - 给原句添加空格"
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

    public Executor_kong() throws Exception {

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

    private static String kong(Message message) {
        String temp;
        temp = message.getOptions();
        temp = temp.replaceAll(" ", "");
        temp = temp.replaceAll("\\[CQ:.+\\]", "");
        temp = temp.trim();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < temp.length(); i++) {
            builder.append(temp.charAt(i));
            builder.append(" ");
        }
        builder.setLength(builder.length() - 1);
        return builder.toString();
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
        entry.userInfo(userid, message.getSection() == 0 ? "你 想 把 空 气 变 臭 吗" : Executor_kong.kong(message));
        return true;
    }

    @Override
    public boolean doDiszMessage(long diszid, long userid, MessageDisz message, int messageid, int messagefont) throws Exception {
        entry.diszInfo(diszid, userid, message.getSection() == 0 ? "你 想 把 空 气 变 臭 吗" : Executor_kong.kong(message));
        return true;
    }

    @Override
    public boolean doGropMessage(long gropid, long userid, MessageGrop message, int messageid, int messagefont) throws Exception {
        entry.gropInfo(gropid, userid, message.getSection() == 0 ? "你 想 把 空 气 变 臭 吗" : Executor_kong.kong(message));
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
