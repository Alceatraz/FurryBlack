package studio.blacktech.coolqbot.furryblack.scheduler;

import java.util.Date;

import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.Zwischenspiel;
import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.module.Executor_jrjp;
import studio.blacktech.coolqbot.furryblack.module.Executor_jrrp;

public class Autobot_DailyTask implements Runnable {

	private static Date date;

	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		Autobot_DailyTask.date = new Date();
		long time = 86400L;
		time = time - Autobot_DailyTask.date.getSeconds();
		time = time - (Autobot_DailyTask.date.getMinutes() * 60);
		time = time - (Autobot_DailyTask.date.getHours() * 3600);
		try {
			Thread.sleep(time * 1000);
			while (true) {
				Autobot_DailyTask.date = new Date();
				Executor_jrrp.flush();
				Executor_jrjp.flush();
				JcqApp.CQ.sendPrivateMsg(entry.OPERATOR, Zwischenspiel.genReport(Autobot_DailyTask.date));
				Thread.sleep(86400000L);
			}
		} catch (final InterruptedException exception) {
			exception.printStackTrace();
		}
	}
}
