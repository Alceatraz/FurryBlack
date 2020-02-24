package studio.blacktech.coolqbot.furryblack.common.module;


import studio.blacktech.coolqbot.furryblack.common.message.MessageDisz;
import studio.blacktech.coolqbot.furryblack.common.message.MessageGrop;
import studio.blacktech.coolqbot.furryblack.common.message.MessageUser;


public abstract class ModuleExecutor extends Module {

	private static final long serialVersionUID = 1L;

	// @formatter:off

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

    // @formatter:on

	protected int COUNT_USER = 0;
	protected int COUNT_DISZ = 0;
	protected int COUNT_GROP = 0;

	protected boolean ENABLE_USER = false;
	protected boolean ENABLE_DISZ = false;
	protected boolean ENABLE_GROP = false;

	public abstract boolean doUserMessage(MessageUser message) throws Exception;

	public abstract boolean doDiszMessage(MessageDisz message) throws Exception;

	public abstract boolean doGropMessage(MessageGrop message) throws Exception;


	public boolean executeUserMessage(MessageUser message) throws Exception {
		COUNT_USER++;
		return doUserMessage(message);
	}

	public boolean executeDiszMessage(MessageDisz message) throws Exception {
		COUNT_DISZ++;
		return doDiszMessage(message);
	}

	public boolean executeGropMessage(MessageGrop message) throws Exception {
		COUNT_GROP++;
		return doGropMessage(message);
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
