package studio.blacktech.coolqbot.furryblack.common;

public abstract class ModuleScheduler extends Module implements Runnable {

	public String MODULE_DISPLAYNAME = null;
	public String MODULE_PACKAGENAME = null;
	public String MODULE_DESCRIPTION = null;
	public String MODULE_VERSION = null;
	public String[] MODULE_USAGE = null;
	public String[] MODULE_PRIVACY_TRIGER = null;
	public String[] MODULE_PRIVACY_LISTEN = null;
	public String[] MODULE_PRIVACY_STORED = null;
	public String[] MODULE_PRIVACY_CACHED = null;
	public String[] MODULE_PRIVACY_OBTAIN = null;

	public String MODULE_FULLHELP = null;

}
