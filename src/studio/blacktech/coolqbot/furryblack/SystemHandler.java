package studio.blacktech.coolqbot.furryblack;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import com.sobte.cqp.jcq.entity.IMsg;
import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.common.ConfigureX;
import studio.blacktech.common.LoggerX;
import studio.blacktech.common.exception.ReInitializationException;
import studio.blacktech.coolqbot.furryblack.module.Message;
import studio.blacktech.coolqbot.furryblack.module.Module;
import studio.blacktech.coolqbot.furryblack.module.ModuleExecutor;
import studio.blacktech.coolqbot.furryblack.module.ModuleListener;
import studio.blacktech.coolqbot.furryblack.module.ModuleTrigger;
import studio.blacktech.coolqbot.furryblack.plugins.Executor_chou;
import studio.blacktech.coolqbot.furryblack.plugins.Executor_dice;
import studio.blacktech.coolqbot.furryblack.plugins.Executor_echo;
import studio.blacktech.coolqbot.furryblack.plugins.Executor_gamb;
import studio.blacktech.coolqbot.furryblack.plugins.Executor_jrjp;
import studio.blacktech.coolqbot.furryblack.plugins.Executor_jrrp;
import studio.blacktech.coolqbot.furryblack.plugins.Executor_kong;
import studio.blacktech.coolqbot.furryblack.plugins.Executor_roll;
import studio.blacktech.coolqbot.furryblack.plugins.Executor_zhan;
import studio.blacktech.coolqbot.furryblack.scheduler.Scheduler_DDNS;
import studio.blacktech.coolqbot.furryblack.scheduler.Scheduler_Task;

@SuppressWarnings("unused")
public class SystemHandler extends Module {

	private static int COUNT_USER_MESSAGE = 0;
	private static int COUNT_DISZ_MESSAGE = 0;
	private static int COUNT_GROP_MESSAGE = 0;

	// @formatter:off
	private static String MESSAGE_LIST_USER = "";
	private static String MESSAGE_LIST_DISZ = "";
	private static String MESSAGE_LIST_GROP = "";
	private static String MESSAGE_HELP =
			"FurryBlack - һ��С����������˹�����\r\n" +
			"\r\n" +
			"ʹ�ü�����ͬ�������û����Э��\r\n" +
			"\r\n" +
			"������Ӻ���Ҳ��ʹ��\r\n" +
			"��Ӻ��ѽ��Զ�ͬ��\r\n" +
			"������Ⱥ���Զ�ͬ��\r\n" +
			"\r\n" +
			"�������˫б��//��ͷ\r\n" +
			"//eula - �鿴�û�ʹ��Э��\r\n" +
			"//info - �汾��Ȩ��Ϣ\r\n" +
			"//list - �г�����ģ��\r\n" +
			"//help <����> - ��ʾָ��ģ�����";
	private static String MESSAGE_INFO =
			"FurryBlack - һ��С����������˹�����\r\n" +
			"\r\n" +
			"ʹ�ü�����ͬ�������û����Э��(//eula�鿴)\r\n" +
			"\r\n" +
			"�汾��Ϣ: REPLACE_VERSION\r\n" +
			"\r\n" +
			"��˽����(��ܼ���˽):\r\n" +
			"˽��ģʽ - �û���Ϣ �� ������ �� ������ �� ������(//��ͷ����Ϣ) �� ������\r\n" +
			"Ⱥ��ģʽ - �û���Ϣ �� ������ �� ������ �� ������(//��ͷ����Ϣ) �� ������\r\n" +
			"\r\n" +
			"��˽����(ģ�鼶��˽):\r\n" +
			"ÿ��ģ�鶼�������Լ�����˽���𣬿���//help <name>�鿴\r\n" +
			"���� - ָ��ֱ�ӻ�ȡ�û�������Ϣ���ҿ����ڽ�������ǰ���ص������޸��κ����ݵĲ��\r\n" +
			"���� - ָ��ֱ�ӻ�ȡ�û�������Ϣ������������ϢҲ�����޸��κ����ݵĵĲ��\r\n" +
			"��ͨ - ��δע���������߼�����Ϊ����//��ͷ����Ϣ��������ֻ�����ӦPACKNAME�Ĳ��\r\n" +
			"\r\n" +
			"�洢 - ָ�������û���Ϣ�����ݴ洢���־û�/���ݿ�/�ļ���\r\n" +
			"���� - ָ�������û���Ϣ�����ݴ洢���ڴ����ݽṹ/�����У����ض�������ͷ�\r\n" +
			"��ȡ - ָ�������û���Ϣ��������ȡ�������ڴ�����ڵ��δ���������ɺ������ͷ�\r\n" +
			"\r\n" +
			"��Ȩ��Ϣ(�߼�����): ��Ȩ����BlackTechStudio\r\n" +
			"�� Team BTSNUVO ����\r\n" +
			"�� Team BTSNODE ��Ӫ\r\n" +
			"\r\n" +
			"��Ȩ��Ϣ(��������): ��Ȩ����FPDG,��Ȩʹ��\r\n" +
			"https://twitter.com/flappydoggy/status/877582553762283520\r\n" +
			"https://twitter.com/flappydoggy/status/875026125038080000\r\n" +
			"".replaceAll("REPLACE_VERSION", entry.PRODUCT_VERSION);
	private static String MESSAGE_EULA =
			"FurryBlack - һ��С����������˹�����\r\n" +
			"\r\n" +
			"�����û����Э�飨���¼��EULA����\r\n" +
			"\r\n" +
			"�׷���Blacktechstudio�����¼��BTS��\r\n" +
			"�ҷ�������\r\n" +
			"\r\n" +
			"1����//help //eula //info(�޲���)֮�⣬ʹ�ñ��˸��κι��ܼ���ʾ�ҷ�ͬ�ⱾEULA��\r\n" +
			"2���׷������ҷ����κ���Ϊ��ʾ����Ĭʾ���κ�����򷴶ԣ�\r\n" +
			"3���ҷ���Υ����EULA���׷���Ȩ��ȡ���ҷ���ʹ��Ȩ��\r\n" +
			"4���ҷ��������κ���ʽɢ��Υ��Υ�ܻ���ɿ����޵Ȳ�����Ϣ��\r\n" +
			"5���ҷ�������δ��Ȩ������½����˸����κ���ʽ�����κ���ʽ����ҵ��;��\r\n" +
			"6���׷��Լ������漰�Ŀ���ά��������Ա���е������ҷ�ʹ�õ��µ��κ���ʧ��\r\n" +
			"7���׷��Լ������漰�Ŀ���ά��������Ա���е����ڳ����������������κ���ʧ��\r\n" +
			"\r\n" +
			"\r\n" +
			"BTS��2019-02-22 ����";
	// @formatter:on

	private static boolean INITIALIZATIONLOCK = false;

	private static boolean ENABLE_TRIGGER_USER = false;
	private static boolean ENABLE_TRIGGER_DISZ = false;
	private static boolean ENABLE_TRIGGER_GROP = false;

	private static boolean ENABLE_LISENTER_USER = false;
	private static boolean ENABLE_LISENTER_DISZ = false;
	private static boolean ENABLE_LISENTER_GROP = false;

	private static boolean ENABLE_BLACKLIST = false;

	private static boolean ENABLE_USER_IGNORE = false;
	private static boolean ENABLE_DISZ_IGNORE = false;
	private static boolean ENABLE_GROP_IGNORE = false;

	private static HashMap<String, Thread> SCHEDULER = new HashMap<String, Thread>();

	private static ArrayList<CharSequence> BLACKLIST = new ArrayList<CharSequence>(100);

	private static ArrayList<Long> USER_IGNORE = new ArrayList<Long>(100);
	private static ArrayList<Long> DISZ_IGNORE = new ArrayList<Long>(100);
	private static ArrayList<Long> GROP_IGNORE = new ArrayList<Long>(100);

	private static ArrayList<ModuleTrigger> TRIGGER_USER = new ArrayList<ModuleTrigger>(100);
	private static ArrayList<ModuleTrigger> TRIGGER_DISZ = new ArrayList<ModuleTrigger>(100);
	private static ArrayList<ModuleTrigger> TRIGGER_GROP = new ArrayList<ModuleTrigger>(100);

	private static ArrayList<ModuleListener> LISTENER_USER = new ArrayList<ModuleListener>(100);
	private static ArrayList<ModuleListener> LISTENER_DISZ = new ArrayList<ModuleListener>(100);
	private static ArrayList<ModuleListener> LISTENER_GROP = new ArrayList<ModuleListener>(100);

	private static TreeMap<String, ModuleExecutor> EXECUTOR_USER = new TreeMap<String, ModuleExecutor>();
	private static TreeMap<String, ModuleExecutor> EXECUTOR_DISZ = new TreeMap<String, ModuleExecutor>();
	private static TreeMap<String, ModuleExecutor> EXECUTOR_GROP = new TreeMap<String, ModuleExecutor>();

	protected static boolean init() throws ReInitializationException, NumberFormatException, IOException {

		if (SystemHandler.INITIALIZATIONLOCK) {
			throw new ReInitializationException();
		}

		SystemHandler.INITIALIZATIONLOCK = true;

		ENABLE_TRIGGER_USER = ConfigureX.ENABLE_TRIGGER_USER();
		ENABLE_TRIGGER_DISZ = ConfigureX.ENABLE_TRIGGER_DISZ();
		ENABLE_TRIGGER_GROP = ConfigureX.ENABLE_TRIGGER_GROP();

		ENABLE_LISENTER_USER = ConfigureX.ENABLE_LISENTER_USER();
		ENABLE_LISENTER_DISZ = ConfigureX.ENABLE_LISENTER_DISZ();
		ENABLE_LISENTER_GROP = ConfigureX.ENABLE_LISENTER_GROP();

		ENABLE_BLACKLIST = ConfigureX.ENABLE_BLACKLIST();

		ENABLE_USER_IGNORE = ConfigureX.ENABLE_USERIGNORE();
		ENABLE_DISZ_IGNORE = ConfigureX.ENABLE_DISZIGNORE();
		ENABLE_GROP_IGNORE = ConfigureX.ENABLE_GROPIGNORE();

		String line;
		BufferedReader reader;
		StringBuilder builder;

		reader = new BufferedReader(new FileReader(ConfigureX.FILE_BLACKLIST));
		while ((line = reader.readLine()) != null) {
			SystemHandler.BLACKLIST.add(line);
		}
		reader.close();

		reader = new BufferedReader(new FileReader(ConfigureX.FILE_USERIGNORE));
		while ((line = reader.readLine()) != null) {
			SystemHandler.USER_IGNORE.add(Long.parseLong(line));
		}
		reader.close();

		reader = new BufferedReader(new FileReader(ConfigureX.FILE_DISZIGNORE));
		while ((line = reader.readLine()) != null) {
			SystemHandler.DISZ_IGNORE.add(Long.parseLong(line));
		}
		reader.close();

		reader = new BufferedReader(new FileReader(ConfigureX.FILE_GROPIGNORE));
		while ((line = reader.readLine()) != null) {
			SystemHandler.GROP_IGNORE.add(Long.parseLong(line));
		}
		reader.close();

		ModuleExecutor chou = new Executor_chou();
		ModuleExecutor dice = new Executor_dice();
		ModuleExecutor echo = new Executor_echo();
		ModuleExecutor gamb = new Executor_gamb();
		ModuleExecutor jrjp = new Executor_jrjp();
		ModuleExecutor jrrp = new Executor_jrrp();
		ModuleExecutor kong = new Executor_kong();
		ModuleExecutor roll = new Executor_roll();
		ModuleExecutor zhan = new Executor_zhan();

		SystemHandler.registerUserExecutor(dice);
		SystemHandler.registerUserExecutor(echo);
		SystemHandler.registerUserExecutor(jrrp);
		SystemHandler.registerUserExecutor(kong);
		SystemHandler.registerUserExecutor(roll);
		SystemHandler.registerUserExecutor(zhan);

		SystemHandler.registerDiszExecutor(dice);
		SystemHandler.registerDiszExecutor(echo);
		SystemHandler.registerDiszExecutor(jrrp);
		SystemHandler.registerDiszExecutor(jrjp);
		SystemHandler.registerDiszExecutor(roll);
		SystemHandler.registerDiszExecutor(zhan);

		SystemHandler.registerGropExecutor(chou);
		SystemHandler.registerGropExecutor(dice);
		SystemHandler.registerGropExecutor(echo);
		SystemHandler.registerGropExecutor(gamb);
		SystemHandler.registerGropExecutor(jrjp);
		SystemHandler.registerGropExecutor(jrrp);
		SystemHandler.registerGropExecutor(kong);
		SystemHandler.registerGropExecutor(roll);
		SystemHandler.registerGropExecutor(zhan);

		builder = new StringBuilder("�Ѿ���װ�Ĳ�� - ˽�Ŀ���: ");
		builder.append(SystemHandler.EXECUTOR_USER.size());
		for (final String temp : SystemHandler.EXECUTOR_USER.keySet()) {
			final ModuleExecutor module = SystemHandler.EXECUTOR_USER.get(temp);
			module.genFullHelp();
			builder.append("\r\n//");
			builder.append(module.MODULE_PACKAGENAME);
			builder.append(" > ");
			builder.append(module.MODULE_DISPLAYNAME);
			builder.append(" : ");
			builder.append(module.MODULE_DESCRIPTION);
		}
		SystemHandler.MESSAGE_LIST_USER = builder.toString();

		builder = new StringBuilder("�Ѿ���װ�Ĳ�� - ���������: ");
		builder.append(SystemHandler.EXECUTOR_USER.size());
		for (final String temp : SystemHandler.EXECUTOR_USER.keySet()) {
			final ModuleExecutor module = SystemHandler.EXECUTOR_USER.get(temp);
			module.genFullHelp();
			builder.append("\r\n//");
			builder.append(module.MODULE_PACKAGENAME);
			builder.append(" > ");
			builder.append(module.MODULE_DISPLAYNAME);
			builder.append(" : ");
			builder.append(module.MODULE_DESCRIPTION);
		}
		SystemHandler.MESSAGE_LIST_DISZ = builder.toString();

		builder = new StringBuilder("�Ѿ���װ�Ĳ�� - Ⱥ�Ŀ���: ");
		builder.append(SystemHandler.EXECUTOR_USER.size());
		for (final String temp : SystemHandler.EXECUTOR_USER.keySet()) {
			final ModuleExecutor module = SystemHandler.EXECUTOR_USER.get(temp);
			module.genFullHelp();
			builder.append("\r\n//");
			builder.append(module.MODULE_PACKAGENAME);
			builder.append(" > ");
			builder.append(module.MODULE_DISPLAYNAME);
			builder.append(" : ");
			builder.append(module.MODULE_DESCRIPTION);
		}
		SystemHandler.MESSAGE_LIST_GROP = builder.toString();

		SystemHandler.SCHEDULER.put("TASK", new Thread(new Scheduler_Task()));
		SystemHandler.SCHEDULER.put("DDNS", new Thread(new Scheduler_DDNS()));

		SystemHandler.SCHEDULER.get("TASK").start();
		SystemHandler.SCHEDULER.get("DDNS").start();

		return true;
	}

	protected static int doUserMessage(final int typeid, final long userid, final String message, final int messageid, final int messagefont) throws Exception {
		SystemHandler.COUNT_USER_MESSAGE++;
		// �Ƿ������û�������
		if (SystemHandler.ENABLE_USER_IGNORE && SystemHandler.USER_IGNORE.contains(userid)) {
			return IMsg.MSG_IGNORE;
		}
		// �Ƿ����ü�����
		if (SystemHandler.ENABLE_LISENTER_USER) {
			for (final ModuleListener temp : SystemHandler.LISTENER_USER) {
				temp.excuteUserMessage(typeid, userid, new Message(message), messageid, messagefont);
			}
		}
		// �Ƿ����ô�����
		if (SystemHandler.ENABLE_TRIGGER_USER) {
			boolean intercept = false;
			for (final ModuleTrigger temp : SystemHandler.TRIGGER_USER) {
				intercept = intercept || temp.excuteUserMessage(typeid, userid, new Message(message), messageid, messagefont);
			}
			if (intercept) {
				return IMsg.MSG_IGNORE;
			}
		}
		// �Ƿ����ð����ʹ���
		if (SystemHandler.ENABLE_BLACKLIST) {
			for (final CharSequence temp : SystemHandler.BLACKLIST) {
				if (message.contains(temp)) {
					return IMsg.MSG_IGNORE;
				}
			}
		}
		// ���������
		if (message.startsWith("//") && (message.length() > 2)) {
			final Message command = new Message(message);
			switch (command.prase()) {
			case "info":
				JcqApp.CQ.sendPrivateMsg(userid, SystemHandler.MESSAGE_INFO);
				break;
			case "eula":
				JcqApp.CQ.sendPrivateMsg(userid, SystemHandler.MESSAGE_EULA);
				break;
			case "list":
				JcqApp.CQ.sendPrivateMsg(userid, SystemHandler.MESSAGE_LIST_USER);
				break;
			case "help":
				if (command.length == 1) {
					JcqApp.CQ.sendPrivateMsg(userid, SystemHandler.MESSAGE_HELP);
				} else if (command.length > 1) {
					if (SystemHandler.EXECUTOR_USER.containsKey(command.cmd[1])) {
						Module.userInfo(userid, SystemHandler.EXECUTOR_USER.get(command.cmd[1]).MODULE_FULLHELP);
					} else {
						Module.userInfo(userid, "û�д˲��\r\n" + SystemHandler.MESSAGE_LIST_USER);
					}
				}
				break;
			case "admin":
				if (userid == ConfigureX.OPERATOR()) {
					Zwischenspiel.doUserAdmin(command);
				}
				break;
			default:
				if (SystemHandler.EXECUTOR_USER.containsKey(command.cmd[0])) {
					try {
						SystemHandler.EXECUTOR_USER.get(command.cmd[0]).excuteUserMessage(typeid, userid, command, messageid, messagefont);
					} catch (final Exception exception) {
						exception.printStackTrace();
					}
				} else {
					Module.userInfo(userid, "û�д˲��\r\n" + SystemHandler.MESSAGE_LIST_USER);
				}
			}
			return IMsg.MSG_IGNORE;
		}
		JcqApp.CQ.sendPrivateMsg(userid, "��ʹ��//help�鿴����");
		return IMsg.MSG_IGNORE;
	}

	protected static int doDiszMessage(final long diszid, final long userid, final String message, final int messageid, final int messagefont) throws Exception {
		SystemHandler.COUNT_USER_MESSAGE++;
		// �Ƿ������û�������
		if (SystemHandler.ENABLE_DISZ_IGNORE && SystemHandler.DISZ_IGNORE.contains(userid)) {
			return IMsg.MSG_IGNORE;
		}
		// �Ƿ����ü�����
		if (SystemHandler.ENABLE_LISENTER_DISZ) {
			for (final ModuleListener temp : SystemHandler.LISTENER_DISZ) {
				temp.excutDiszMessage(diszid, userid, new Message(message), messageid, messagefont);
			}
		}
		// �Ƿ����ô�����
		if (SystemHandler.ENABLE_TRIGGER_DISZ) {
			boolean intercept = false;
			for (final ModuleTrigger temp : SystemHandler.TRIGGER_DISZ) {
				intercept = intercept || temp.excutDiszMessage(diszid, userid, new Message(message), messageid, messagefont);
			}
			if (intercept) {
				return IMsg.MSG_IGNORE;
			}
		}
		// �Ƿ����ð����ʹ���
		if (SystemHandler.ENABLE_BLACKLIST) {
			for (final CharSequence temp : SystemHandler.BLACKLIST) {
				if (message.contains(temp)) {
					return IMsg.MSG_IGNORE;
				}
			}
		}
		// ���������
		if (message.startsWith("//") && (message.length() > 2)) {
			final Message command = new Message(message);
			switch (command.prase()) {
			case "info":
				JcqApp.CQ.sendPrivateMsg(userid, SystemHandler.MESSAGE_INFO);
				break;
			case "eula":
				JcqApp.CQ.sendPrivateMsg(userid, SystemHandler.MESSAGE_EULA);
				break;
			case "list":
				JcqApp.CQ.sendPrivateMsg(userid, SystemHandler.MESSAGE_LIST_DISZ);
				break;
			case "help":
				if (command.length == 1) {
					JcqApp.CQ.sendPrivateMsg(userid, SystemHandler.MESSAGE_HELP);
				} else if (command.length > 1) {
					if (SystemHandler.EXECUTOR_DISZ.containsKey(command.cmd[1])) {
						Module.userInfo(userid, SystemHandler.EXECUTOR_DISZ.get(command.cmd[1]).MODULE_FULLHELP);
					} else {
						Module.userInfo(userid, "û�д˲��\r\n" + SystemHandler.MESSAGE_LIST_DISZ);
					}
				}
				break;
			case "admin":
				if (userid == ConfigureX.OPERATOR()) {
					Zwischenspiel.doDiszAdmin(command);
				}
				break;
			default:
				if (SystemHandler.EXECUTOR_DISZ.containsKey(command.cmd[0])) {
					try {
						SystemHandler.EXECUTOR_DISZ.get(command.cmd[0]).excutDiszMessage(diszid, userid, command, messageid, messagefont);
					} catch (final Exception exception) {
						exception.printStackTrace();
					}
				} else {
					Module.userInfo(userid, "û�д˲��\r\n" + SystemHandler.MESSAGE_LIST_DISZ);
				}
			}
			return IMsg.MSG_IGNORE;
		}
		return IMsg.MSG_IGNORE;
	}

	protected static int doGropMessage(final long gropid, final long userid, final String message, final int messageid, final int messagefont) throws Exception {
		SystemHandler.COUNT_GROP_MESSAGE++;
		// �Ƿ������û�������
		if (SystemHandler.ENABLE_GROP_IGNORE && SystemHandler.GROP_IGNORE.contains(userid)) {
			return IMsg.MSG_IGNORE;
		}
		// �Ƿ����ü�����
		if (SystemHandler.ENABLE_LISENTER_GROP) {
			for (final ModuleListener temp : SystemHandler.LISTENER_GROP) {
				temp.excuteGropMessage(gropid, userid, new Message(message), messageid, messagefont);
			}
		}
		// �Ƿ����ô�����
		if (SystemHandler.ENABLE_TRIGGER_GROP) {
			boolean intercept = false;
			for (final ModuleTrigger temp : SystemHandler.TRIGGER_GROP) {
				intercept = intercept || temp.excuteGropMessage(gropid, userid, new Message(message), messageid, messagefont);
			}
			if (intercept) {
				return IMsg.MSG_IGNORE;
			}
		}
		// �Ƿ����ð����ʹ���
		if (SystemHandler.ENABLE_BLACKLIST) {
			for (final CharSequence temp : SystemHandler.BLACKLIST) {
				if (message.contains(temp)) {
					return IMsg.MSG_IGNORE;
				}
			}
		}
		// ���������
		if (message.startsWith("//") && (message.length() > 2)) {
			final Message command = new Message(message);
			switch (command.prase()) {
			case "info":
				JcqApp.CQ.sendPrivateMsg(userid, SystemHandler.MESSAGE_INFO);
				break;
			case "eula":
				JcqApp.CQ.sendPrivateMsg(userid, SystemHandler.MESSAGE_EULA);
				break;
			case "list":
				JcqApp.CQ.sendPrivateMsg(userid, SystemHandler.MESSAGE_LIST_GROP);
				break;
			case "help":
				if (command.length == 1) {
					JcqApp.CQ.sendPrivateMsg(userid, SystemHandler.MESSAGE_HELP);
				} else if (command.length > 1) {
					if (SystemHandler.EXECUTOR_USER.containsKey(command.cmd[1])) {
						Module.userInfo(userid, SystemHandler.EXECUTOR_USER.get(command.cmd[1]).MODULE_FULLHELP);
					} else {
						Module.userInfo(userid, "û�д˲��\r\n" + SystemHandler.MESSAGE_LIST_GROP);
					}
				}
				break;
			case "admin":
				if (userid == ConfigureX.OPERATOR()) {
					Zwischenspiel.doGropAdmin(command);
				}
				break;
			default:
				if (SystemHandler.EXECUTOR_GROP.containsKey(command.cmd[0])) {
					try {
						SystemHandler.EXECUTOR_GROP.get(command.cmd[0]).excuteGropMessage(gropid, userid, command, messageid, messagefont);
					} catch (final Exception exception) {
						exception.printStackTrace();
					}
				} else {
					Module.userInfo(userid, "û�д˲��\r\n" + SystemHandler.MESSAGE_LIST_GROP);
				}
			}
			return IMsg.MSG_IGNORE;
		}
		return IMsg.MSG_IGNORE;
	}

	@Override
	public String getReport() {
		return SystemHandler.genReport();
	}

	public static String genReport() {
		String report;
		StringBuilder builder = new StringBuilder();
		builder.append(LoggerX.time());
		builder.append(" - ״̬��");
		builder.append("\r\n");

		builder.append("ϵͳ״̬��");
		builder.append("\r\n");
		builder.append("����-˽�ģ� ");
		builder.append(SystemHandler.COUNT_USER_MESSAGE);
		builder.append("\r\n");
		builder.append("����-���ģ� ");
		builder.append(SystemHandler.COUNT_DISZ_MESSAGE);
		builder.append("\r\n");
		builder.append("����-Ⱥ�ģ� ");
		builder.append(SystemHandler.COUNT_GROP_MESSAGE);
		builder.append("\r\n");

		builder.append("ģ��״̬:");
		builder.append("\r\n");
		builder.append("ģ��-˽��:");
		for (final String temp : SystemHandler.EXECUTOR_USER.keySet()) {
			builder.append("\r\n");
			builder.append("ģ�� ");
			builder.append(temp);
			builder.append(": ");
			builder.append(SystemHandler.EXECUTOR_USER.get(temp).COUNT);
			builder.append(" �ε���");
			report = SystemHandler.EXECUTOR_USER.get(temp).getReport();
			if (report != null) {
				builder.append(report);
			}
		}
		builder.append("\r\n");
		builder.append("ģ��-����:");
		for (final String temp : SystemHandler.EXECUTOR_DISZ.keySet()) {
			builder.append("\r\n");
			builder.append("ģ�� ");
			builder.append(temp);
			builder.append(": ");
			builder.append(SystemHandler.EXECUTOR_DISZ.get(temp).COUNT);
			builder.append(" �ε���");
			report = SystemHandler.EXECUTOR_DISZ.get(temp).getReport();
			if (report != null) {
				builder.append(report);
			}
		}
		builder.append("\r\n");
		builder.append("ģ��-Ⱥ��:");
		for (final String temp : SystemHandler.EXECUTOR_GROP.keySet()) {
			builder.append("\r\n");
			builder.append("ģ�� ");
			builder.append(temp);
			builder.append(": ");
			builder.append(SystemHandler.EXECUTOR_GROP.get(temp).COUNT);
			builder.append(" �ε���");
			report = SystemHandler.EXECUTOR_GROP.get(temp).getReport();
			if (report != null) {
				builder.append(report);
			}
		}
		return builder.toString();
	}

	private static void registerUserTrigger(ModuleTrigger trigger) {
		SystemHandler.TRIGGER_USER.add(trigger);
	}

	private static void registerDiszTrigger(ModuleTrigger trigger) {
		SystemHandler.TRIGGER_DISZ.add(trigger);
	}

	private static void registerGropTrigger(ModuleTrigger trigger) {
		SystemHandler.TRIGGER_GROP.add(trigger);
	}

	private static void registerUserListener(ModuleListener listener) {
		SystemHandler.LISTENER_USER.add(listener);
	}

	private static void registerDiszListener(ModuleListener listener) {
		SystemHandler.LISTENER_DISZ.add(listener);
	}

	private static void registerGropListener(ModuleListener listener) {
		SystemHandler.LISTENER_GROP.add(listener);
	}

	private static void registerUserExecutor(ModuleExecutor executor) {
		SystemHandler.EXECUTOR_USER.put(executor.MODULE_PACKAGENAME, executor);
	}

	private static void registerDiszExecutor(ModuleExecutor executor) {
		SystemHandler.EXECUTOR_DISZ.put(executor.MODULE_PACKAGENAME, executor);
	}

	private static void registerGropExecutor(ModuleExecutor executor) {
		SystemHandler.EXECUTOR_GROP.put(executor.MODULE_PACKAGENAME, executor);
	}

}
