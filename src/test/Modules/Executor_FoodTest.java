package test.Modules;

import org.junit.jupiter.api.Test;

import studio.blacktech.coolqbot.furryblack.common.LoggerX.LoggerX;
import studio.blacktech.coolqbot.furryblack.common.message.Message;
import studio.blacktech.coolqbot.furryblack.modules.Executor.Executor_food;

public class Executor_FoodTest {

	@Test
	void test() throws Exception {

		Executor_food instance = new Executor_food();

		instance.init(new LoggerX());
		instance.boot(new LoggerX());

		System.out.println(instance.chooseFood(new Message("/food", 1, 1).parse()));
		System.out.println(instance.chooseFood(new Message("/food 1", 1, 1).parse()));
		System.out.println(instance.chooseFood(new Message("/food 2", 1, 1).parse()));
		System.out.println(instance.chooseFood(new Message("/food 3", 1, 1).parse()));
		System.out.println(instance.chooseFood(new Message("/food 4", 1, 1).parse()));
		System.out.println(instance.chooseFood(new Message("/food 5 ", 1, 1).parse()));
		System.out.println(instance.chooseFood(new Message("/food 6", 1, 1).parse()));
		System.out.println(instance.chooseFood(new Message("/food 7", 1, 1).parse()));
	}

}
