package studio.blacktech.coolqbot.furryblack;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.sobte.cqp.jcq.entity.IMsg;
import com.sobte.cqp.jcq.event.JcqApp;
import com.sobte.cqp.jcq.event.JcqAppAbstract;

import studio.blacktech.common.exception.ReInitializationException;
import studio.blacktech.coolqbot.furryblack.signal.Workflow;

public class EventHandler {

	private static boolean INITIALIZATIONLOCK = false;

	private static ArrayList<String> BLACKLIST = new ArrayList<String>(100);
	private static ArrayList<Long> USER_IGNORE = new ArrayList<Long>(100);
	private static ArrayList<Long> DISC_IGNORE = new ArrayList<Long>(100);
	private static ArrayList<Long> GROP_IGNORE = new ArrayList<Long>(100);

	public boolean init() throws ReInitializationException, NumberFormatException, IOException {

		if (INITIALIZATIONLOCK) {
			throw new ReInitializationException();
		}

		INITIALIZATIONLOCK = true;

		String line;
		BufferedReader reader;

		reader = new BufferedReader(new FileReader(Paths.get(JcqAppAbstract.appDirectory, "USER_IGNORE.txt").toFile()));
		while ((line = reader.readLine()) != null) {
			USER_IGNORE.add(Long.parseLong(line));
		}
		reader.close();

		reader = new BufferedReader(new FileReader(Paths.get(JcqAppAbstract.appDirectory, "DISC_IGNORE.txt").toFile()));
		while ((line = reader.readLine()) != null) {
			DISC_IGNORE.add(Long.parseLong(line));
		}
		reader.close();

		reader = new BufferedReader(new FileReader(Paths.get(JcqAppAbstract.appDirectory, "GROP_IGNORE.txt").toFile()));
		while ((line = reader.readLine()) != null) {
			GROP_IGNORE.add(Long.parseLong(line));
		}
		reader.close();

		reader = new BufferedReader(new FileReader(Paths.get(JcqAppAbstract.appDirectory, "BLACKLIST.txt").toFile()));
		while ((line = reader.readLine()) != null) {
			BLACKLIST.add(line);
		}
		reader.close();

		return true;
	}

	public static int doPrivateMessage(final int mesg_type, final int mesg_id, final long user_id, final String message, final int mesg_font) {

		if (USER_IGNORE.contains(user_id)) {
			return IMsg.MSG_IGNORE;
		}

		if (message.startsWith("//") && (message.length() > 2)) {

			LogicX.executor(new Workflow(mesg_type, mesg_id, user_id, message, mesg_font));

		} else {
			JcqApp.CQ.sendPrivateMsg(user_id, "为了保障用户隐私\r\n任何非//开头的内容都将被忽略\r\n请使用//help查看帮助");
		}
		return IMsg.MSG_IGNORE;
	}

	public static int doDiscuzMessage(final int mesg_type, final int mesg_id, final long discuss_id, final long user_id, final String message, final int mesg_font) {

		if (USER_IGNORE.contains(user_id) || DISC_IGNORE.contains(discuss_id)) {
			return IMsg.MSG_IGNORE;
		}

		if (message.startsWith("//") && (message.length() > 2)) {
			LogicX.executor(new Workflow(mesg_type, mesg_id, user_id, discuss_id, message, mesg_font));
//		} else if (ENABLE_LISTENER_GROP && LISTENER_GROP.containsKey(discuss_id) && LISTENER_GROP.get(discuss_id).containsKey(user_id)) {
//			LogicX.listener(new Workflow(mesg_type, mesg_id, user_id, discuss_id, message, mesg_font));
		}
		return IMsg.MSG_IGNORE;
	}

	public static int doGroupMessage(final int mesg_type, final int mesg_id, final long group_id, final long user_id, final String anonmessage, final String message, final int mesg_font) {
		if (GROP_IGNORE.contains(group_id) || USER_IGNORE.contains(user_id)) {
			return IMsg.MSG_IGNORE;
		}
		if (message.startsWith("//") && (message.length() > 2)) {
			LogicX.executor(new Workflow(mesg_type, mesg_id, user_id, group_id, message, anonmessage, mesg_font));
//		} else if (ENABLE_LISTENER_GROP && LISTENER_GROP.containsKey(group_id) && LISTENER_GROP.get(group_id).containsKey(user_id)) {
//			LogicX.listener(new Workflow(mesg_type, mesg_id, user_id, group_id, message, anonmessage, mesg_font));
		}
		return IMsg.MSG_IGNORE;
	}
}
