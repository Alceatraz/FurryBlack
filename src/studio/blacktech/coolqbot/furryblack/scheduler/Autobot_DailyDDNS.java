package studio.blacktech.coolqbot.furryblack.scheduler;

import java.util.Date;

import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.common.LoggerX;
import studio.blacktech.coolqbot.furryblack.entry;
import studio.blacktech.coolqbot.furryblack.core.Zwischenspiel;

public class Autobot_DailyDDNS implements Runnable {

	private String ADDRESS = "127.0.0.1";
	private Date date;
	private String resp;
	private String temp;
	private long time;
	private long a;
	private long b;
	private long c;

	@Override
	@SuppressWarnings("deprecation")
	public void run() {
		this.date = new Date();
		this.time = 3605;
		this.time = this.time - this.date.getSeconds();
		this.time = this.time - (this.date.getMinutes() * 60);
		if (this.time < 60) {
			this.time = this.time + 3600;
		}
		try {
			this.temp = Zwischenspiel.ddnsGetIP();
			if (this.temp == null) {
				this.ADDRESS = "127.0.0.1";
				JcqApp.CQ.sendPrivateMsg(entry.OPERATOR, LoggerX.time(this.date) + " [DDNS] ʧ��");
			} else {
				this.ADDRESS = this.temp;
				JcqApp.CQ.sendPrivateMsg(entry.OPERATOR, LoggerX.time(this.date) + " [DDNS] " + this.ADDRESS);
			}
			Thread.sleep(this.time * 1000);
			while (true) {
				this.date = new Date();
				this.a = System.nanoTime();
				this.temp = Zwischenspiel.ddnsGetIP();
				this.b = System.nanoTime();
				if (this.temp == null) {
					this.resp = Zwischenspiel.ddnsSetIP();
					this.c = System.nanoTime();
					JcqApp.CQ.sendPrivateMsg(entry.OPERATOR, LoggerX.time(this.date) + " [DDNS] ��ַˢ��ʧ��\r\n�ϴ�ˢ�µ�ַ: " + this.ADDRESS + "\r\nǿ���л�: " + this.resp + "\r\nִ��ʱ��: " + ((this.b - this.a) / 1000000) + " / " + ((this.c - this.b) / 1000000));
				} else {
					if (!this.ADDRESS.equals(this.temp)) {
						JcqApp.CQ.sendPrivateMsg(entry.OPERATOR, LoggerX.time(this.date) + " [DDNS] ��⵽��ַ���\r\n�ɵ�ַ : " + this.ADDRESS + "\r\n�µ�ַ : " + this.temp + "\r\n��Ӧ : " + this.resp + "\r\nִ��ʱ��: " + ((this.b - this.a) / 1000000) + " / " + ((this.c - this.b) / 1000000));
						this.ADDRESS = this.temp;
					}
				}
				Thread.sleep(3600000L);
			}
		} catch (final InterruptedException exception) {
			exception.printStackTrace();
		}
	}
}
