package studio.blacktech.coolqbot.furryblack.scheduler;

import java.util.Date;

import studio.blacktech.common.ConfigureX;
import studio.blacktech.coolqbot.furryblack.SystemHandler;
import studio.blacktech.coolqbot.furryblack.module.Module;
import studio.blacktech.coolqbot.furryblack.module.ModuleScheduler;
import studio.blacktech.coolqbot.furryblack.plugins.Executor_jrjp;
import studio.blacktech.coolqbot.furryblack.plugins.Executor_jrrp;

@SuppressWarnings("deprecation")
public class Scheduler_Task extends ModuleScheduler {

	private static Date date;

	@Override
	public void run() {
		Scheduler_Task.date = new Date();
		long time = 86400L;
		time = time - Scheduler_Task.date.getSeconds();
		time = time - (Scheduler_Task.date.getMinutes() * 60);
		time = time - (Scheduler_Task.date.getHours() * 3600);
		try {
			Thread.sleep(time * 1000);
			while (true) {
				Module.userInfo(ConfigureX.OPERATOR(), SystemHandler.genReport());
				Executor_jrrp.flush();
				Executor_jrjp.flush();
				Thread.sleep(86400000L);
			}
		} catch (final InterruptedException exception) {
			exception.printStackTrace();
		}
	}

	@Override
	public String generateReport() {
		return null;
	}
}
