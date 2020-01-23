package studio.blacktech.coolqbot.furryblack.common.module;


public abstract class ModuleScheduler extends Module {

	private static final long serialVersionUID = 1L;

	// @formatter:off
    public ModuleScheduler(
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

	protected int COUNT = 0;
	protected boolean ENABLE = false;

	public boolean ENABLE() {

		return ENABLE;

	}

	public int COUNT() {

		return COUNT;

	}

}
