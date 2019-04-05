package studio.blacktech.coolqbot.furryblack.scheduler;

import java.util.Date;

import studio.blacktech.common.ConfigureX;
import studio.blacktech.coolqbot.furryblack.MessageHandler;
import studio.blacktech.coolqbot.furryblack.module.ModuleScheduler;

@SuppressWarnings("deprecation")
public class Scheduler_DailyTask extends ModuleScheduler {

	private static Date date;

	@Override
	public void run() {
		Scheduler_DailyTask.date = new Date();
		long time = 86400L;
		time = time - Scheduler_DailyTask.date.getSeconds();
		time = time - (Scheduler_DailyTask.date.getMinutes() * 60);
		time = time - (Scheduler_DailyTask.date.getHours() * 3600);
		try {
			Thread.sleep(time * 1000);
			while (true) {
				userInfo(ConfigureX.OPERATOR(), MessageHandler.genReport());
//				Executor_jrrp.flush();
//				Executor_jrjp.flush();
				Thread.sleep(86400000L);
			}
		} catch (final InterruptedException exception) {
			exception.printStackTrace();
		}
	}

	@Override
	public String getReport() {
		return null;
	}
}
