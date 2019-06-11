package studio.blacktech.coolqbot.furryblack.modules;

import java.util.HashMap;

import studio.blacktech.coolqbot.furryblack.common.Message;
import studio.blacktech.coolqbot.furryblack.common.Module;
import studio.blacktech.coolqbot.furryblack.common.ModuleExecutor;

public class Executor_acon extends ModuleExecutor {

	private HashMap<Long, Long> CONSUMPTION;
	private HashMap<Long, Long> WORKINGMODE;
	private HashMap<Long, Long> LASTCHANGED;

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
	public boolean doUserMessage(int typeid, long userid, Message message, int messageid, int messagefont) throws Exception {
		return true;
	}

	@Override
	public boolean doDiszMessage(long diszid, long userid, Message message, int messageid, int messagefont) throws Exception {
		return true;
	}

	@Override
	public boolean doGropMessage(long gropid, long userid, Message message, int messageid, int messagefont) throws Exception {

		long current = System.currentTimeMillis() / 1000;
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
			long workingmode = this.WORKINGMODE.get(gropid);
			long lastchanged = this.LASTCHANGED.get(gropid);

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
					Module.gropInfo(gropid, "本群冷气已开放：除湿模式");
					consumption = consumption + (elapse * workingmode);
					this.CONSUMPTION.put(gropid, consumption);
					this.LASTCHANGED.put(gropid, current);
					this.WORKINGMODE.put(gropid, 5880L);
				}
				break;

			case "cool":
				if (workingmode == 7350L) {
					Module.gropInfo(gropid, "已经处于制冷模式");
				} else {
					Module.gropInfo(gropid, "本群冷气已开放：制冷模式");
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