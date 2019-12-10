package studio.blacktech.coolqbot.furryblack.common.module;

import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;

public abstract class ModuleExecutor extends Module {

    private static final long serialVersionUID = 1L;
    protected int COUNT_USER = 0;

    protected int COUNT_DISZ = 0;
    protected int COUNT_GROP = 0;
    protected boolean ENABLE_USER = false;
    protected boolean ENABLE_DISZ = false;
    protected boolean ENABLE_GROP = false;


    public ModuleExecutor(
            String MODULE_PACKAGENAME,
            String MODULE_COMMANDNAME,
            String MODULE_DISPLAYNAME,
            String MODULE_DESCRIPTION,
            String MODULE_VERSION,
            String[] MODULE_USAGE,
            String[] MODULE_PRIVACY_STORED,
            String[] MODULE_PRIVACY_CACHED,
            String[] MODULE_PRIVACY_OBTAIN
    ) throws Exception {
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

    public abstract boolean doUserMessage(int typeid, long userid, MessageUser message, int messageid,
                                          int messagefont) throws Exception;

    public abstract boolean doDiszMessage(long diszid, long userid, MessageDisz message, int messageid,
                                          int messagefont) throws Exception;

    public abstract boolean doGropMessage(long gropid, long userid, MessageGrop message, int messageid,
                                          int messagefont) throws Exception;

    public boolean executeUserMessage(int typeid, long userid, MessageUser message, int messageid, int messagefont) throws Exception {
        COUNT_USER++;
        return doUserMessage(typeid, userid, message, messageid, messagefont);
    }

    public boolean executeDiszMessage(long diszid, long userid, MessageDisz message, int messageid, int messagefont) throws Exception {
        COUNT_DISZ++;
        return doDiszMessage(diszid, userid, message, messageid, messagefont);
    }

    public boolean executeGropMessage(long gropid, long userid, MessageGrop message, int messageid, int messagefont) throws Exception {
        COUNT_GROP++;
        return doGropMessage(gropid, userid, message, messageid, messagefont);
    }

    public int COUNT_USER() {
        return COUNT_USER;
    }

    public int COUNT_DISZ() {
        return COUNT_DISZ;
    }

    public int COUNT_GROP() {
        return COUNT_GROP;
    }

    public boolean ENABLE_USER() {
        return ENABLE_USER;
    }

    public boolean ENABLE_DISZ() {
        return ENABLE_DISZ;
    }

    public boolean ENABLE_GROP() {
        return ENABLE_GROP;
    }
}
