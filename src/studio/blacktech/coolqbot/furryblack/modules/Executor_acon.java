package studio.blacktech.coolqbot.furryblack.modules;

import java.util.HashMap;

import studio.blacktech.coolqbot.furryblack.common.Message;
import studio.blacktech.coolqbot.furryblack.common.Module;
import studio.blacktech.coolqbot.furryblack.common.ModuleExecutor;

public class Executor_acon extends ModuleExecutor {

	private final HashMap<Long, Long> CONSUMPTION;
	private final HashMap<Long, Long> WORKINGMODE;
	private final HashMap<Long, Long> LASTCHANGED;

	public Executor_acon() {
		this.MODULE_PACKAGENAME = "acon";
		this.MODULE_DISPLAYNAME = "空调";
		this.MODULE_DESCRIPTION = "本群冷气开放";
		this.MODULE_VERSION = "1.0.0";
		this.MODULE_USAGE = new String[] {
				"//acon off - 关机",
				"//acon dry - 除湿",
				"//acon cool - 制冷",
				"//acon cost - 耗电量",
		};
		this.MODULE_PRIVACY_TRIGER = new String[] {};
		this.MODULE_PRIVACY_LISTEN = new String[] {};
		this.MODULE_PRIVACY_STORED = new String[] {};
		this.MODULE_PRIVACY_CACHED = new String[] {
				"按群存储耗电量",
				"按群存储耗工作模式",
				"按群存储上次更改模式的时间",
		};
		this.MODULE_PRIVACY_OBTAIN = new String[] {
				"获取命令发送人"
		};

		this.CONSUMPTION = new HashMap<>();
		this.WORKINGMODE = new HashMap<>();
		this.LASTCHANGED = new HashMap<>();
	}

	@Override
	public void memberExit(long gropid, long userid) {
	}

	@Override
	public void memberJoin(long gropid, long userid) {
	}

	@Override
	public String[] generateReport(final int logLevel, final int logMode, final int typeid, final long userid, final long diszid, final long gropid, final Message message, final Object... parameters) {
		return null;
	}

	@Override
	public boolean doUserMessage(final int typeid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		return true;
	}

	@Override
	public boolean doDiszMessage(final long diszid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		return true;
	}

	@Override
	public boolean doGropMessage(final long gropid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {

		final long current = System.currentTimeMillis() / 1000;
		long elapse = 0L;

		if (!this.CONSUMPTION.containsKey(gropid)) {
			this.CONSUMPTION.put(gropid, 0L);
			this.WORKINGMODE.put(gropid, 0L);
			this.LASTCHANGED.put(gropid, current);
			elapse = 0;
		}

		if (message.segment == 1) {

			Module.gropInfo(gropid, userid, "请勿触摸以防触电");

		} else {

			long consumption = this.CONSUMPTION.get(gropid);
			final long workingmode = this.WORKINGMODE.get(gropid);
			final long lastchanged = this.LASTCHANGED.get(gropid);

			elapse = current - lastchanged;

			switch (message.messages[1]) {

			case "off":
				if (workingmode == 0L) {
					Module.gropInfo(gropid, "空调没开");
				} else {
					Module.gropInfo(gropid, "空调已关闭");
					consumption = consumption + (elapse * workingmode);
					this.CONSUMPTION.put(gropid, consumption);
					this.LASTCHANGED.put(gropid, current);
					this.WORKINGMODE.put(gropid, 0L);
				}
				break;

			case "dry":
				if (workingmode == 5880L) {
					Module.gropInfo(gropid, "已经处于除湿模式");
				} else {
					Module.gropInfo(gropid, "切换至除湿模式");
					consumption = consumption + (elapse * workingmode);
					this.CONSUMPTION.put(gropid, consumption);
					this.LASTCHANGED.put(gropid, current);
					this.WORKINGMODE.put(gropid, 5880L);
				}
				break;

			case "wet":
				if (workingmode == 5880L) {
					Module.gropInfo(gropid, "已经处于加湿模式");
				} else {
					Module.gropInfo(gropid, "切换至加湿模式");
					consumption = consumption + (elapse * workingmode);
					this.CONSUMPTION.put(gropid, consumption);
					this.LASTCHANGED.put(gropid, current);
					this.WORKINGMODE.put(gropid, 5880L);
				}
				break;

			case "bake":
				if (workingmode == 114700L) {
					Module.gropInfo(gropid, "已经处于烧烤模式");
				} else {
					Module.gropInfo(gropid, "切换至烧烤模式");
					consumption = consumption + (elapse * workingmode);
					this.CONSUMPTION.put(gropid, consumption);
					this.LASTCHANGED.put(gropid, current);
					this.WORKINGMODE.put(gropid, 7350L);
				}
				break;

			case "cold":
				if (workingmode == 14700L) {
					Module.gropInfo(gropid, "已经处于制冰模式");
				} else {
					Module.gropInfo(gropid, "切换至制冰模式");
					consumption = consumption + (elapse * workingmode);
					this.CONSUMPTION.put(gropid, consumption);
					this.LASTCHANGED.put(gropid, current);
					this.WORKINGMODE.put(gropid, 7350L);
				}
				break;

			case "warn":
				if (workingmode == 7350L) {
					Module.gropInfo(gropid, "已经处于制热模式");
				} else {
					Module.gropInfo(gropid, "切换至制热模式");
					consumption = consumption + (elapse * workingmode);
					this.CONSUMPTION.put(gropid, consumption);
					this.LASTCHANGED.put(gropid, current);
					this.WORKINGMODE.put(gropid, 7350L);
				}
				break;

			case "cool":
				if (workingmode == 7350L) {
					Module.gropInfo(gropid, "已经处于制冷模式");
				} else {
					Module.gropInfo(gropid, "切换至制冷模式");
					consumption = consumption + (elapse * workingmode);
					this.CONSUMPTION.put(gropid, consumption);
					this.LASTCHANGED.put(gropid, current);
					this.WORKINGMODE.put(gropid, 7350L);
				}
				break;

			case "cost":
				consumption = consumption + (elapse * workingmode);
				this.CONSUMPTION.put(gropid, consumption);
				this.LASTCHANGED.put(gropid, current);
				Module.gropInfo(gropid, "累计共耗电：" + String.format("%.2f", consumption / 1000D) + "kW(" + String.format("%.2f", consumption / 3600000D) + ")度\r\n群主须支付：" + String.format("%.2f", consumption / 1936800D) + "元");
				break;

			default:
				Module.gropInfo(gropid, userid, "请勿触摸以防触电");
				break;
			}

		}

		return true;
	}

}