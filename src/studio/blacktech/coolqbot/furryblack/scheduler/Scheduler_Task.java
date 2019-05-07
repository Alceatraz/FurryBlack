package studio.blacktech.coolqbot.furryblack.scheduler;

import java.util.Date;
import java.util.Properties;

import studio.blacktech.coolqbot.furryblack.SystemHandler;
import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.module.Message;
import studio.blacktech.coolqbot.furryblack.module.Module;
import studio.blacktech.coolqbot.furryblack.module.ModuleScheduler;
import studio.blacktech.coolqbot.furryblack.plugins.Executor_jrjp;
import studio.blacktech.coolqbot.furryblack.plugins.Executor_jrrp;

@SuppressWarnings("deprecation")
public class Scheduler_TASK extends ModuleScheduler {

	private static Date date;
	private static boolean INITIALIZATIONLOCK = false;

	public Scheduler_TASK(StringBuilder initBuilder, Properties config) {
		if (Scheduler_TASK.INITIALIZATIONLOCK) {
			return;
		}
		Scheduler_TASK.INITIALIZATIONLOCK = true;

		this.MODULE_DISPLAYNAME = "每日任务";
		this.MODULE_PACKAGENAME = "task";
		this.MODULE_DESCRIPTION = "每日任务";
		this.MODULE_VERSION = "1.0.0";
		this.MODULE_USAGE = new String[] {};
		this.MODULE_PRIVACY_TRIGER = new String[] {};
		this.MODULE_PRIVACY_LISTEN = new String[] {};
		this.MODULE_PRIVACY_STORED = new String[] {};
		this.MODULE_PRIVACY_CACHED = new String[] {};
		this.MODULE_PRIVACY_OBTAIN = new String[] {};
	}

	@Override
	public void run() {
		Scheduler_TASK.date = new Date();
		long time = 86400L;
		time = time - Scheduler_TASK.date.getSeconds();
		time = time - (Scheduler_TASK.date.getMinutes() * 60);
		time = time - (Scheduler_TASK.date.getHours() * 3600);
		System.out.println("TASK模块启动 - " + time);
		try {
			Thread.sleep(time * 1000);
			while (true) {
				Module.userInfo(entry.OPERATOR(), SystemHandler.generateFullReport(0, 0, null, null));
				((Executor_jrjp) SystemHandler.getExecutor("jrjp")).flush();
				((Executor_jrrp) SystemHandler.getExecutor("jrrp")).flush();
				Thread.sleep(86400000L);
			}
		} catch (final InterruptedException exception) {
			exception.printStackTrace();
			return;
		}
	}

	@Override
	public String generateReport(int logLevel, int logMode, Message message, Object[] parameters) {
		return null;
	}
}
