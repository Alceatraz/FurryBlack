package studio.blacktech.coolqbot.furryblack;


import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class PlayGround {

	@Test
	void test() {

		TimeZone zone_0 = TimeZone.getTimeZone("Europe/France");
		TimeZone zone_1 = TimeZone.getTimeZone("Europe/Paris");

		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		formater.setTimeZone(zone_0);
		System.out.println(formater.format(new Date()));

		formater.setTimeZone(zone_1);
		System.out.println(formater.format(new Date()));

	}

}
