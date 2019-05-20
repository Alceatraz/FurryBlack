package studio.blacktech.coolqbot.furryblack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.TreeMap;

import com.sobte.cqp.jcq.entity.IMsg;
import com.sobte.cqp.jcq.event.JcqApp;

import studio.blacktech.coolqbot.furryblack.module.Message;
import studio.blacktech.coolqbot.furryblack.module.Module;
import studio.blacktech.coolqbot.furryblack.module.ModuleExecutor;
import studio.blacktech.coolqbot.furryblack.module.ModuleListener;
import studio.blacktech.coolqbot.furryblack.module.ModuleScheduler;
import studio.blacktech.coolqbot.furryblack.module.ModuleTrigger;
import studio.blacktech.coolqbot.furryblack.plugins.Executor_admin;
import studio.blacktech.coolqbot.furryblack.plugins.Executor_chou;
import studio.blacktech.coolqbot.furryblack.plugins.Executor_dice;
import studio.blacktech.coolqbot.furryblack.plugins.Executor_echo;
import studio.blacktech.coolqbot.furryblack.plugins.Executor_gamb;
import studio.blacktech.coolqbot.furryblack.plugins.Executor_jrjp;
import studio.blacktech.coolqbot.furryblack.plugins.Executor_jrrp;
import studio.blacktech.coolqbot.furryblack.plugins.Executor_kong;
import studio.blacktech.coolqbot.furryblack.plugins.Executor_roll;
import studio.blacktech.coolqbot.furryblack.plugins.Executor_zhan;
import studio.blacktech.coolqbot.furryblack.plugins.Listener_TopSpeak;
import studio.blacktech.coolqbot.furryblack.plugins.Trigger_SuidDeny;
import studio.blacktech.coolqbot.furryblack.plugins.Trigger_WordDeny;
import studio.blacktech.coolqbot.furryblack.scheduler.Scheduler_DDNS;
import studio.blacktech.coolqbot.furryblack.scheduler.Scheduler_TASK;

public class SystemHandler extends Module {

	private static final long BOOTTIME = System.currentTimeMillis();

	private static boolean INITIALIZATIONLOCK = false;

	private static int COUNT_USER_MESSAGE = 0;
	private static int COUNT_DISZ_MESSAGE = 0;
	private static int COUNT_GROP_MESSAGE = 0;

	private static String MESSAGE_LIST_USER = "";
	private static String MESSAGE_LIST_DISZ = "";
	private static String MESSAGE_LIST_GROP = "";

	// @formatter:off

	private static String MESSAGE_HELP =

			"FurryBlack - һ��С����������˹�����\r\n" +
			"\r\n" +
			"ʹ�ü�����ͬ�������û����Э��\r\n" +
			"\r\n" + "������Ӻ���Ҳ��ʹ��\r\n" +
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
			"ÿ��ģ�鶼�������Լ�����˽���𣬿���//help <name>�鿴\r\n"	+
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
			"��Ŀ��ַ https://git.blacktech.studio/blacktechstudio/furryblack\r\n" +
			"\r\n" +
			"��Ȩ��Ϣ(��������): ��Ȩ����FPDG,��Ȩʹ��\r\n" +
			"https://twitter.com/flappydoggy/status/877582553762283520\r\n" +
			"https://twitter.com/flappydoggy/status/875026125038080000\r\n"
			.replaceAll("REPLACE_VERSION", entry.PRODUCT_VERSION);

	private static String MESSAGE_EULA =

			"FurryBlack - һ��С����������˹�����\r\n" +
			"\r\n" +
			"�����û����Э�飨���¼��EULA����\r\n" +
			"\r\n" +
			"�׷���Blacktechstudio�����¼��BTS��\r\n" +
			"�ҷ�������\r\n" +
			"\r\n" +
			"1����//help //eula //info֮�⣬ʹ�ñ��˸��κι��ܼ���ʾ�ҷ�ͬ�ⱾEULA��\r\n" +
			"2���׷������ҷ����κ���Ϊ��ʾ����Ĭʾ���κ�����򷴶ԣ�\r\n" +
			"3���ҷ���Υ����EULA���׷���Ȩ��ȡ���ҷ���ʹ��Ȩ��\r\n" +
			"4���ҷ��������κ���ʽɢ�����뷨�ɻ���³�Υ���Ĳ�����Ϣ��\r\n" +
			"5���ҷ�������δ��Ȩ������½����˸����κ���ʽ�����κ���ʽ����ҵ��;��\r\n"	+
			"6���׷��Լ������漰�Ŀ���ά��������Ա���е������ҷ�ʹ�õ��µ��κ���ʧ��\r\n" +
			"7���׷��Լ������漰�Ŀ���ά��������Ա���е����ڳ����������������κ���ʧ��\r\n" +
			"���ս���Ȩ��BTS����" +
			"\r\n" +
			"\r\n" +
			"BTS��2019-02-22 ����";

	// @formatter:on

	private static Executor_chou executor_chou;
	private static Executor_dice executor_dice;
	private static Executor_echo executor_echo;
	private static Executor_gamb executor_gamb;
	private static Executor_jrjp executor_jrjp;
	private static Executor_jrrp executor_jrrp;
	private static Executor_kong executor_kong;
	private static Executor_roll executor_roll;
	private static Executor_zhan executor_zhan;
	private static Executor_admin executor_admin;
	private static Listener_TopSpeak listener_topspeak;
	private static Trigger_SuidDeny trigger_suiddeny;
	private static Trigger_WordDeny trigger_worddeny;
	private static Scheduler_TASK scheduler_task;
	private static Scheduler_DDNS scheduler_ddns;

	private static boolean ENABLE_LISENTER_USER = false;
	private static boolean ENABLE_LISENTER_DISZ = false;
	private static boolean ENABLE_LISENTER_GROP = false;
	private static boolean ENABLE_TRIGGER_USER = false;
	private static boolean ENABLE_TRIGGER_DISZ = false;
	private static boolean ENABLE_TRIGGER_GROP = false;
	private static boolean ENABLE_BLACKLIST = false;
	private static boolean ENABLE_USER_IGNORE = false;
	private static boolean ENABLE_DISZ_IGNORE = false;
	private static boolean ENABLE_GROP_IGNORE = false;

	private static TreeMap<String, ModuleListener> LISTENER_INSTANCE = new TreeMap<String, ModuleListener>();
	private static TreeMap<String, ModuleTrigger> TRIGGER_INSTANCE = new TreeMap<String, ModuleTrigger>();
	private static TreeMap<String, ModuleExecutor> EXECUTOR_INSTANCE = new TreeMap<String, ModuleExecutor>();

	private static ArrayList<ModuleTrigger> TRIGGER_USER = new ArrayList<ModuleTrigger>(100);
	private static ArrayList<ModuleTrigger> TRIGGER_DISZ = new ArrayList<ModuleTrigger>(100);
	private static ArrayList<ModuleTrigger> TRIGGER_GROP = new ArrayList<ModuleTrigger>(100);

	private static ArrayList<ModuleListener> LISTENER_USER = new ArrayList<ModuleListener>(100);
	private static ArrayList<ModuleListener> LISTENER_DISZ = new ArrayList<ModuleListener>(100);
	private static ArrayList<ModuleListener> LISTENER_GROP = new ArrayList<ModuleListener>(100);

	private static TreeMap<String, ModuleExecutor> EXECUTOR_USER = new TreeMap<String, ModuleExecutor>();
	private static TreeMap<String, ModuleExecutor> EXECUTOR_DISZ = new TreeMap<String, ModuleExecutor>();
	private static TreeMap<String, ModuleExecutor> EXECUTOR_GROP = new TreeMap<String, ModuleExecutor>();

	private static HashMap<String, ModuleScheduler> SCHEDULER = new HashMap<String, ModuleScheduler>();
	private static HashMap<String, Thread> SCHEDULER_INSTANCE = new HashMap<String, Thread>();

	protected static boolean init(StringBuilder initBuilder, Properties config) throws ReInitializationException, NumberFormatException, IOException {

		if (SystemHandler.INITIALIZATIONLOCK) {
			throw new ReInitializationException();
		}

		SystemHandler.INITIALIZATIONLOCK = true;

		// ==========================================================================================================================

		if (entry.INIT_VERBOSE) {
			initBuilder.append("\r\n[System] ��ȡ����");
		}

		SystemHandler.ENABLE_TRIGGER_USER = Boolean.parseBoolean(config.getProperty("enable_trigger_user", "false"));
		SystemHandler.ENABLE_TRIGGER_DISZ = Boolean.parseBoolean(config.getProperty("enable_trigger_disz", "false"));
		SystemHandler.ENABLE_TRIGGER_GROP = Boolean.parseBoolean(config.getProperty("enable_trigger_grop", "false"));
		SystemHandler.ENABLE_LISENTER_USER = Boolean.parseBoolean(config.getProperty("enable_listener_user", "false"));
		SystemHandler.ENABLE_LISENTER_DISZ = Boolean.parseBoolean(config.getProperty("enable_listener_disz", "false"));
		SystemHandler.ENABLE_LISENTER_GROP = Boolean.parseBoolean(config.getProperty("enable_listener_grop", "false"));
		SystemHandler.ENABLE_BLACKLIST = Boolean.parseBoolean(config.getProperty("enable_blacklist", "false"));
		SystemHandler.ENABLE_USER_IGNORE = Boolean.parseBoolean(config.getProperty("enable_userignore", "false"));
		SystemHandler.ENABLE_DISZ_IGNORE = Boolean.parseBoolean(config.getProperty("enable_diszignore", "false"));
		SystemHandler.ENABLE_GROP_IGNORE = Boolean.parseBoolean(config.getProperty("enable_gropignore", "false"));

		// ==========================================================================================================================

		if (entry.INIT_VERBOSE) {
			initBuilder.append("\r\n[Module] ʵ����������");
		}
		SystemHandler.listener_topspeak = new Listener_TopSpeak();

		if (entry.INIT_VERBOSE) {
			initBuilder.append("\r\n[Module] ע��˽�ļ�����");
		}
		SystemHandler.registerUserListener(SystemHandler.listener_topspeak);

		if (entry.INIT_VERBOSE) {
			initBuilder.append("\r\n[Module] ע�����ļ�����");
		}
		SystemHandler.registerDiszListener(SystemHandler.listener_topspeak);

		if (entry.INIT_VERBOSE) {
			initBuilder.append("\r\n[Module] ע��Ⱥ�ļ�����");
		}
		SystemHandler.registerGropListener(SystemHandler.listener_topspeak);

		if (entry.INIT_VERBOSE) {
			initBuilder.append("\r\n[Module] ͳ�Ƽ�����");
		}
		SystemHandler.ENABLE_LISENTER_USER = SystemHandler.ENABLE_LISENTER_USER && (SystemHandler.LISTENER_USER.size() > 0);
		SystemHandler.ENABLE_LISENTER_DISZ = SystemHandler.ENABLE_LISENTER_DISZ && (SystemHandler.LISTENER_DISZ.size() > 0);
		SystemHandler.ENABLE_LISENTER_GROP = SystemHandler.ENABLE_LISENTER_GROP && (SystemHandler.LISTENER_GROP.size() > 0);

		// ==========================================================================================================================

		if (entry.INIT_VERBOSE) {
			initBuilder.append("\r\n[Module] ʵ����������");
		}
		SystemHandler.trigger_suiddeny = new Trigger_SuidDeny();
		SystemHandler.trigger_worddeny = new Trigger_WordDeny();

		if (entry.INIT_VERBOSE) {
			initBuilder.append("\r\n[Module] ע��˽�Ĵ�����");
		}
		if (SystemHandler.ENABLE_USER_IGNORE) {
			SystemHandler.registerUserTrigger(SystemHandler.trigger_suiddeny);
		}

		if (SystemHandler.ENABLE_BLACKLIST) {
			SystemHandler.registerUserTrigger(SystemHandler.trigger_worddeny);
		}

		if (entry.INIT_VERBOSE) {
			initBuilder.append("\r\n[Module] ע�����Ĵ�����");
		}
		if (SystemHandler.ENABLE_DISZ_IGNORE) {
			SystemHandler.registerDiszTrigger(SystemHandler.trigger_suiddeny);
		}

		if (SystemHandler.ENABLE_BLACKLIST) {
			SystemHandler.registerDiszTrigger(SystemHandler.trigger_worddeny);
		}

		if (entry.INIT_VERBOSE) {
			initBuilder.append("\r\n[Module] ע��Ⱥ�Ĵ�����");
		}
		if (SystemHandler.ENABLE_GROP_IGNORE) {
			SystemHandler.registerGropTrigger(SystemHandler.trigger_suiddeny);
		}

		if (SystemHandler.ENABLE_BLACKLIST) {
			SystemHandler.registerGropTrigger(SystemHandler.trigger_worddeny);
		}

		if (entry.INIT_VERBOSE) {
			initBuilder.append("\r\n[Module] ͳ�ƴ�����");
		}
		SystemHandler.ENABLE_TRIGGER_USER = SystemHandler.ENABLE_TRIGGER_USER && (SystemHandler.TRIGGER_USER.size() > 0);
		SystemHandler.ENABLE_TRIGGER_DISZ = SystemHandler.ENABLE_TRIGGER_DISZ && (SystemHandler.TRIGGER_DISZ.size() > 0);
		SystemHandler.ENABLE_TRIGGER_GROP = SystemHandler.ENABLE_TRIGGER_GROP && (SystemHandler.TRIGGER_GROP.size() > 0);

		// ==========================================================================================================================

		if (entry.INIT_VERBOSE) {
			initBuilder.append("\r\n[Module] ʵ����ִ����");
		}
		SystemHandler.executor_chou = new Executor_chou();
		SystemHandler.executor_dice = new Executor_dice();
		SystemHandler.executor_echo = new Executor_echo();
		SystemHandler.executor_gamb = new Executor_gamb();
		SystemHandler.executor_jrjp = new Executor_jrjp();
		SystemHandler.executor_jrrp = new Executor_jrrp();
		SystemHandler.executor_kong = new Executor_kong();
		SystemHandler.executor_roll = new Executor_roll();
		SystemHandler.executor_zhan = new Executor_zhan();
		SystemHandler.executor_admin = new Executor_admin();

		if (entry.INIT_VERBOSE) {
			initBuilder.append("\r\n[Module] ע��˽��ִ����");
		}
		SystemHandler.registerUserExecutor(SystemHandler.executor_dice);
		SystemHandler.registerUserExecutor(SystemHandler.executor_echo);
		SystemHandler.registerUserExecutor(SystemHandler.executor_jrrp);
		SystemHandler.registerUserExecutor(SystemHandler.executor_kong);
		SystemHandler.registerUserExecutor(SystemHandler.executor_roll);
		SystemHandler.registerUserExecutor(SystemHandler.executor_zhan);
		SystemHandler.registerUserExecutor(SystemHandler.executor_admin);

		if (entry.INIT_VERBOSE) {
			initBuilder.append("\r\n[Module] ע������ִ����");
		}
		SystemHandler.registerDiszExecutor(SystemHandler.executor_dice);
		SystemHandler.registerDiszExecutor(SystemHandler.executor_echo);
		SystemHandler.registerDiszExecutor(SystemHandler.executor_jrrp);
		SystemHandler.registerDiszExecutor(SystemHandler.executor_jrjp);
		SystemHandler.registerDiszExecutor(SystemHandler.executor_roll);
		SystemHandler.registerDiszExecutor(SystemHandler.executor_zhan);

		if (entry.INIT_VERBOSE) {
			initBuilder.append("\r\n[Module] ע��Ⱥ��ִ����");
		}
		SystemHandler.registerGropExecutor(SystemHandler.executor_chou);
		SystemHandler.registerGropExecutor(SystemHandler.executor_dice);
		SystemHandler.registerGropExecutor(SystemHandler.executor_echo);
		SystemHandler.registerGropExecutor(SystemHandler.executor_gamb);
		SystemHandler.registerGropExecutor(SystemHandler.executor_jrjp);
		SystemHandler.registerGropExecutor(SystemHandler.executor_jrrp);
		SystemHandler.registerGropExecutor(SystemHandler.executor_kong);
		SystemHandler.registerGropExecutor(SystemHandler.executor_roll);
		SystemHandler.registerGropExecutor(SystemHandler.executor_zhan);
		SystemHandler.registerGropExecutor(SystemHandler.executor_admin);

		// ==========================================================================================================================

		if (entry.INIT_VERBOSE) {
			initBuilder.append("\r\n[Module] �������� \r\n  ˽�ģ�");
			initBuilder.append(SystemHandler.ENABLE_TRIGGER_USER ? "���� - " + SystemHandler.TRIGGER_USER.size() + "��" : "����");
			initBuilder.append("\r\n  ���ģ�");
			initBuilder.append(SystemHandler.ENABLE_TRIGGER_DISZ ? "���� - " + SystemHandler.TRIGGER_DISZ.size() + "��" : "����");
			initBuilder.append("\r\n  Ⱥ�ģ�");
			initBuilder.append(SystemHandler.ENABLE_TRIGGER_GROP ? "���� - " + SystemHandler.TRIGGER_GROP.size() + "��" : "����");
			initBuilder.append("\r\n[Module] �������� \r\n  ˽�ģ�");
			initBuilder.append(SystemHandler.ENABLE_LISENTER_USER ? "���� - " + SystemHandler.LISTENER_USER.size() + "��" : "����");
			initBuilder.append("\r\n  ���ģ�");
			initBuilder.append(SystemHandler.ENABLE_LISENTER_DISZ ? "���� - " + SystemHandler.LISTENER_DISZ.size() + "��" : "����");
			initBuilder.append("\r\n  Ⱥ�ģ�");
			initBuilder.append(SystemHandler.ENABLE_LISENTER_GROP ? "���� - " + SystemHandler.LISTENER_GROP.size() + "��" : "����");
		}

		// ==========================================================================================================================

		StringBuilder preBuilder = new StringBuilder();
		preBuilder.append("�Ѿ���װ��ִ����: ");
		preBuilder.append(SystemHandler.EXECUTOR_USER.size());
		for (final String temp : SystemHandler.EXECUTOR_USER.keySet()) {
			final ModuleExecutor module = SystemHandler.EXECUTOR_USER.get(temp);
			module.genFullHelp();
			preBuilder.append("\r\n");
			preBuilder.append(module.MODULE_PACKAGENAME);
			preBuilder.append(" > ");
			preBuilder.append(module.MODULE_DISPLAYNAME);
			preBuilder.append(" : ");
			preBuilder.append(module.MODULE_DESCRIPTION);
		}
		preBuilder.append("\r\n�Ѿ���װ�Ĵ�����: ");
		preBuilder.append(SystemHandler.TRIGGER_USER.size());
		for (final ModuleTrigger temp : SystemHandler.TRIGGER_USER) {
			preBuilder.append("\r\n");
			preBuilder.append(temp.MODULE_PACKAGENAME);
			preBuilder.append(" > ");
			preBuilder.append(temp.MODULE_DISPLAYNAME);
			preBuilder.append(" : ");
			preBuilder.append(temp.MODULE_DESCRIPTION);
		}
		preBuilder.append("\r\n�Ѿ���װ�ļ�����: ");
		preBuilder.append(SystemHandler.LISTENER_USER.size());
		for (final ModuleListener temp : SystemHandler.LISTENER_USER) {
			preBuilder.append("\r\n");
			preBuilder.append(temp.MODULE_PACKAGENAME);
			preBuilder.append(" > ");
			preBuilder.append(temp.MODULE_DISPLAYNAME);
			preBuilder.append(" : ");
			preBuilder.append(temp.MODULE_DESCRIPTION);
		}
		SystemHandler.MESSAGE_LIST_USER = preBuilder.toString();
		if (entry.INIT_VERBOSE) {
			initBuilder.append("\r\n[Message] list ˽��\r\n");
			initBuilder.append(SystemHandler.MESSAGE_LIST_USER);
		}

		// ==========================================================================================================================

		preBuilder = new StringBuilder();
		preBuilder.append("�Ѿ���װ��ִ������");
		preBuilder.append(SystemHandler.EXECUTOR_DISZ.size());
		for (final String temp : SystemHandler.EXECUTOR_DISZ.keySet()) {
			final ModuleExecutor module = SystemHandler.EXECUTOR_DISZ.get(temp);
			module.genFullHelp();
			preBuilder.append("\r\n");
			preBuilder.append(module.MODULE_PACKAGENAME);
			preBuilder.append(" > ");
			preBuilder.append(module.MODULE_DISPLAYNAME);
			preBuilder.append(" : ");
			preBuilder.append(module.MODULE_DESCRIPTION);
		}
		preBuilder.append("\r\n�Ѿ���װ�Ĵ�������");
		preBuilder.append(SystemHandler.TRIGGER_DISZ.size());
		for (final ModuleTrigger temp : SystemHandler.TRIGGER_DISZ) {
			preBuilder.append("\r\n");
			preBuilder.append(temp.MODULE_PACKAGENAME);
			preBuilder.append(" > ");
			preBuilder.append(temp.MODULE_DISPLAYNAME);
			preBuilder.append(" : ");
			preBuilder.append(temp.MODULE_DESCRIPTION);
		}
		preBuilder.append("\r\n�Ѿ���װ�ļ�����: ");
		preBuilder.append(SystemHandler.LISTENER_DISZ.size());
		for (final ModuleListener temp : SystemHandler.LISTENER_DISZ) {
			preBuilder.append("\r\n");
			preBuilder.append(temp.MODULE_PACKAGENAME);
			preBuilder.append(" > ");
			preBuilder.append(temp.MODULE_DISPLAYNAME);
			preBuilder.append(" : ");
			preBuilder.append(temp.MODULE_DESCRIPTION);
		}
		SystemHandler.MESSAGE_LIST_DISZ = preBuilder.toString();
		if (entry.INIT_VERBOSE) {
			initBuilder.append("\r\n[Message] list ����\r\n");
			initBuilder.append(SystemHandler.MESSAGE_LIST_DISZ);
		}

		// ==========================================================================================================================

		preBuilder = new StringBuilder();
		preBuilder.append("�Ѿ���װ��ִ������");
		preBuilder.append(SystemHandler.EXECUTOR_GROP.size());
		for (final String temp : SystemHandler.EXECUTOR_GROP.keySet()) {
			final ModuleExecutor module = SystemHandler.EXECUTOR_GROP.get(temp);
			module.genFullHelp();
			preBuilder.append("\r\n");
			preBuilder.append(module.MODULE_PACKAGENAME);
			preBuilder.append(" > ");
			preBuilder.append(module.MODULE_DISPLAYNAME);
			preBuilder.append(" : ");
			preBuilder.append(module.MODULE_DESCRIPTION);
		}
		preBuilder.append("\r\n�Ѿ���װ�Ĵ�������");
		preBuilder.append(SystemHandler.TRIGGER_GROP.size());
		for (final ModuleTrigger temp : SystemHandler.TRIGGER_GROP) {
			preBuilder.append("\r\n");
			preBuilder.append(temp.MODULE_PACKAGENAME);
			preBuilder.append(" > ");
			preBuilder.append(temp.MODULE_DISPLAYNAME);
			preBuilder.append(" : ");
			preBuilder.append(temp.MODULE_DESCRIPTION);
		}
		preBuilder.append("\r\n�Ѿ���װ�ļ�������");
		preBuilder.append(SystemHandler.LISTENER_GROP.size());
		for (final ModuleListener temp : SystemHandler.LISTENER_GROP) {
			preBuilder.append("\r\n");
			preBuilder.append(temp.MODULE_PACKAGENAME);
			preBuilder.append(" > ");
			preBuilder.append(temp.MODULE_DISPLAYNAME);
			preBuilder.append(" : ");
			preBuilder.append(temp.MODULE_DESCRIPTION);
		}
		SystemHandler.MESSAGE_LIST_GROP = preBuilder.toString();
		if (entry.INIT_VERBOSE) {
			initBuilder.append("\r\n[Message] list Ⱥ��\r\n");
			initBuilder.append(SystemHandler.MESSAGE_LIST_GROP);
		}

		// ==========================================================================================================================

		if (entry.INIT_VERBOSE) {
			initBuilder.append("\r\n[Module] ʵ�����ƻ�����");
		}
		SystemHandler.scheduler_task = new Scheduler_TASK(initBuilder, config);
		SystemHandler.scheduler_ddns = new Scheduler_DDNS(initBuilder, config);

		if (entry.INIT_VERBOSE) {
			initBuilder.append("\r\n[Module] ע��ƻ�����");
		}
		SystemHandler.registerSchedular(SystemHandler.scheduler_task);
		SystemHandler.registerSchedular(SystemHandler.scheduler_ddns);

		if (entry.INIT_VERBOSE) {
			initBuilder.append("\r\n[Module] �����ƻ�����");
		}
		SystemHandler.getSchedulerThread("task").start();
		SystemHandler.getSchedulerThread("ddns").start();

		return true;
	}
	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	public static void doGroupMemberIncrease(int subtype, int sendTime, long fromGroup, long fromQQ, long beingOperateQQ) {
		JcqApp.CQ.sendPrivateMsg(entry.OPERATOR(), LoggerX.time() + " - [��Ⱥ] " + ((subtype == 1) ? "��������" : "��������") + "\r\nȺ�ţ�" + fromGroup + "\r\n����" + fromQQ + "\r\n��Ա��" + beingOperateQQ);

	}

	public static void doGroupMemberDecrease(int subtype, int sendTime, long fromGroup, long fromQQ, long beingOperateQQ) {
		JcqApp.CQ.sendPrivateMsg(entry.OPERATOR(), LoggerX.time() + " - [��Ⱥ] " + (subtype == 1 ? "������Ⱥ" : "�����߳�") + "\r\nȺ�ţ�" + fromGroup + "\r\n����" + fromQQ + "\r\n��Ա��" + beingOperateQQ);
		listener_topspeak.memberExit(fromGroup, beingOperateQQ);
	}

	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	protected static int doUserMessage(final int typeid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		SystemHandler.COUNT_USER_MESSAGE++;
		if (message.isCommand) {
			switch (message.messages[0]) {
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
				if (message.segment == 2) {
					if (SystemHandler.EXECUTOR_USER.containsKey(message.messages[1])) {
						Module.userInfo(userid, SystemHandler.EXECUTOR_USER.get(message.messages[1]).MODULE_FULLHELP);
					} else {
						Module.userInfo(userid, "û�д˲��\r\n" + SystemHandler.MESSAGE_LIST_USER);
					}
				} else {
					JcqApp.CQ.sendPrivateMsg(userid, SystemHandler.MESSAGE_HELP);
				}
				break;

			default:
				if (SystemHandler.EXECUTOR_USER.containsKey(message.messages[0])) {
					try {
						SystemHandler.EXECUTOR_USER.get(message.messages[0]).excuteUserMessage(typeid, userid, message, messageid, messagefont);
					} catch (final Exception exception) {
						exception.printStackTrace();
					}
				} else {
					Module.userInfo(userid, "û�д˲��\r\n" + SystemHandler.MESSAGE_LIST_USER);
				}
			}
		} else {
			if (SystemHandler.ENABLE_LISENTER_USER) {
				for (final ModuleListener temp : SystemHandler.LISTENER_USER) {
					temp.excuteUserMessage(typeid, userid, message, messageid, messagefont);
				}
			}
			if (SystemHandler.ENABLE_TRIGGER_USER) {
				boolean intercept = false;
				for (final ModuleTrigger temp : SystemHandler.TRIGGER_USER) {
					intercept = intercept || temp.excuteUserMessage(typeid, userid, message, messageid, messagefont);
				}
				if (intercept) {
					return IMsg.MSG_IGNORE;
				}
			}
			JcqApp.CQ.sendPrivateMsg(userid, "��ʹ��//help�鿴����");
		}
		return IMsg.MSG_IGNORE;
	}

	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	protected static int doDiszMessage(final long diszid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		SystemHandler.COUNT_DISZ_MESSAGE++;
		if (SystemHandler.ENABLE_LISENTER_DISZ) {
			for (final ModuleListener temp : SystemHandler.LISTENER_DISZ) {
				temp.executeDiszMessage(diszid, userid, message, messageid, messagefont);
			}
		}
		if (SystemHandler.ENABLE_TRIGGER_DISZ) {
			boolean intercept = false;
			for (final ModuleTrigger temp : SystemHandler.TRIGGER_DISZ) {
				intercept = intercept || temp.executeDiszMessage(diszid, userid, message, messageid, messagefont);
			}
			if (intercept) {
				return IMsg.MSG_IGNORE;
			}
		}
		if (message.isCommand) {
			switch (message.messages[0]) {
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
				if (message.segment == 2) {
					if (SystemHandler.EXECUTOR_USER.containsKey(message.messages[1])) {
						Module.userInfo(userid, SystemHandler.EXECUTOR_USER.get(message.messages[1]).MODULE_FULLHELP);
					} else {
						Module.userInfo(userid, "û�д˲��\r\n" + SystemHandler.MESSAGE_LIST_USER);
					}
				} else {
					JcqApp.CQ.sendPrivateMsg(userid, SystemHandler.MESSAGE_HELP);
				}
				break;
			default:
				if (SystemHandler.EXECUTOR_DISZ.containsKey(message.messages[0])) {
					try {
						SystemHandler.EXECUTOR_DISZ.get(message.messages[0]).executeDiszMessage(diszid, userid, message, messageid, messagefont);
					} catch (final Exception exception) {
						exception.printStackTrace();
					}
				} else {
					Module.userInfo(userid, "û�д˲��\r\n" + SystemHandler.MESSAGE_LIST_DISZ);
				}
			}
		}
		return IMsg.MSG_IGNORE;
	}

	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	protected static int doGropMessage(final long gropid, final long userid, final Message message, final int messageid, final int messagefont) throws Exception {
		SystemHandler.COUNT_GROP_MESSAGE++;
		if (SystemHandler.ENABLE_LISENTER_GROP) {
			for (final ModuleListener temp : SystemHandler.LISTENER_GROP) {
				temp.executeGropMessage(gropid, userid, message, messageid, messagefont);
			}
		}
		if (SystemHandler.ENABLE_TRIGGER_GROP) {
			boolean intercept = false;
			for (final ModuleTrigger temp : SystemHandler.TRIGGER_GROP) {
				intercept = intercept || temp.executeDiszMessage(gropid, userid, message, messageid, messagefont);
			}
			if (intercept) {
				return IMsg.MSG_IGNORE;
			}
		}
		if (message.isCommand) {
			switch (message.messages[0]) {
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
				if (message.segment == 2) {
					if (SystemHandler.EXECUTOR_USER.containsKey(message.messages[1])) {
						Module.userInfo(userid, SystemHandler.EXECUTOR_USER.get(message.messages[1]).MODULE_FULLHELP);
					} else {
						Module.userInfo(userid, "û�д˲��\r\n" + SystemHandler.MESSAGE_LIST_USER);
					}
				} else {
					JcqApp.CQ.sendPrivateMsg(userid, SystemHandler.MESSAGE_HELP);
				}
				break;
			default:
				if (SystemHandler.EXECUTOR_GROP.containsKey(message.messages[0])) {
					try {
						SystemHandler.EXECUTOR_GROP.get(message.messages[0]).excuteGropMessage(gropid, userid, message, messageid, messagefont);
					} catch (final Exception exception) {
						exception.printStackTrace();
					}
				} else {
					Module.userInfo(userid, "û�д˲��\r\n" + SystemHandler.MESSAGE_LIST_GROP);
				}
			}
		}
		return IMsg.MSG_IGNORE;
	}

	// ==========================================================================================================================================================
	//
	//
	//
	// ==========================================================================================================================================================

	@Override
	public String generateReport(int logLevel, int logMode, Message message, Object[] parameters) {
		return null;
	}

	public static String generateFullReport(int logLevel, int logMode, Message message, Object[] parameters) {

		long uptime = System.currentTimeMillis() - SystemHandler.BOOTTIME;
		long uptimedd = uptime / 86400000;
		uptime = uptime % 86400000;
		long uptimehh = uptime / 3600000;
		uptime = uptime % 3600000;
		long uptimemm = uptime / 60000;
		uptime = uptime % 60000;
		long uptimess = uptime / 1000;

		long totalMemory = Runtime.getRuntime().totalMemory() / 1024;
		long freeMemory = Runtime.getRuntime().freeMemory() / 1024;
		long maxMemory = Runtime.getRuntime().maxMemory() / 1024;

		String report;
		StringBuilder builder = new StringBuilder();

		builder.append(LoggerX.time());
		builder.append(" - ״̬��\r\n\r\nϵͳ״̬��\r\n����ʱ��: ");
		builder.append(uptimedd);
		builder.append(" - ");
		builder.append(uptimehh);
		builder.append(":");
		builder.append(uptimemm);
		builder.append(":");
		builder.append(uptimess);
		builder.append("\r\n�ڴ�����: ");
		builder.append(totalMemory - freeMemory);
		builder.append("KB /");
		builder.append(totalMemory);
		builder.append("KB\r\n�����ڴ�: ");
		builder.append(totalMemory);
		builder.append("KB /");
		builder.append(maxMemory);
		builder.append("MB\r\n\r\n����-˽�ģ� ");
		builder.append(SystemHandler.COUNT_USER_MESSAGE);
		builder.append("��\r\n����-���ģ� ");
		builder.append(SystemHandler.COUNT_DISZ_MESSAGE);
		builder.append("��\r\n����-Ⱥ�ģ� ");
		builder.append(SystemHandler.COUNT_GROP_MESSAGE);
		builder.append("��\r\n\r\n������״̬: ");
		builder.append(SystemHandler.TRIGGER_INSTANCE.size());
		builder.append("��");

		for (final String name : SystemHandler.TRIGGER_INSTANCE.keySet()) {
			ModuleTrigger temp = SystemHandler.TRIGGER_INSTANCE.get(name);
			builder.append("\r\nģ�� ");
			builder.append(temp.MODULE_PACKAGENAME);
			builder.append(": ");
			builder.append(temp.COUNT);
			builder.append("��");
			report = temp.generateReport(0, 0, null, null);
			if (report != null) {
				builder.append("\r\n");
				builder.append(report);
			}
		}

		builder.append("\r\n\r\n������״̬:");
		builder.append(SystemHandler.LISTENER_INSTANCE.size());
		builder.append("��");

		for (final String name : SystemHandler.LISTENER_INSTANCE.keySet()) {
			ModuleListener temp = SystemHandler.LISTENER_INSTANCE.get(name);
			builder.append("\r\nģ�� ");
			builder.append(temp.MODULE_PACKAGENAME);
			builder.append(": ");
			builder.append(temp.COUNT);
			builder.append("��");
			report = temp.generateReport(0, 0, null, null);
			if (report != null) {
				builder.append("\r\n");
				builder.append(report);
			}
		}

		builder.append("\r\n\r\nִ����״̬: ");
		builder.append(SystemHandler.EXECUTOR_INSTANCE.size());
		builder.append("��");

		for (final String name : SystemHandler.EXECUTOR_INSTANCE.keySet()) {
			ModuleExecutor temp = SystemHandler.EXECUTOR_INSTANCE.get(name);
			builder.append("\r\nģ�� ");
			builder.append(temp.MODULE_PACKAGENAME);
			builder.append(": ");
			builder.append(temp.COUNT);
			builder.append("��");
			report = temp.generateReport(0, 0, null, null);
			if (report != null) {
				builder.append("\r\n");
				builder.append(report);
			}
		}

		return builder.toString();
	}

	public static void registerUserTrigger(ModuleTrigger trigger) {
		SystemHandler.TRIGGER_USER.add(trigger);
		SystemHandler.TRIGGER_INSTANCE.put(trigger.MODULE_PACKAGENAME, trigger);
	}

	public static void registerDiszTrigger(ModuleTrigger trigger) {
		SystemHandler.TRIGGER_DISZ.add(trigger);
		SystemHandler.TRIGGER_INSTANCE.put(trigger.MODULE_PACKAGENAME, trigger);
	}

	private static void registerGropTrigger(ModuleTrigger trigger) {
		SystemHandler.TRIGGER_GROP.add(trigger);
		SystemHandler.TRIGGER_INSTANCE.put(trigger.MODULE_PACKAGENAME, trigger);
	}

	public static void registerUserListener(ModuleListener listener) {
		SystemHandler.LISTENER_USER.add(listener);
		SystemHandler.LISTENER_INSTANCE.put(listener.MODULE_PACKAGENAME, listener);
	}

	public static void registerDiszListener(ModuleListener listener) {
		SystemHandler.LISTENER_DISZ.add(listener);
		SystemHandler.LISTENER_INSTANCE.put(listener.MODULE_PACKAGENAME, listener);
	}

	public static void registerGropListener(ModuleListener listener) {
		SystemHandler.LISTENER_GROP.add(listener);
		SystemHandler.LISTENER_INSTANCE.put(listener.MODULE_PACKAGENAME, listener);
	}

	public static void registerUserExecutor(ModuleExecutor executor) {
		SystemHandler.EXECUTOR_USER.put(executor.MODULE_PACKAGENAME, executor);
		SystemHandler.EXECUTOR_INSTANCE.put(executor.MODULE_PACKAGENAME, executor);
	}

	public static void registerDiszExecutor(ModuleExecutor executor) {
		SystemHandler.EXECUTOR_DISZ.put(executor.MODULE_PACKAGENAME, executor);
		SystemHandler.EXECUTOR_INSTANCE.put(executor.MODULE_PACKAGENAME, executor);
	}

	public static void registerGropExecutor(ModuleExecutor executor) {
		SystemHandler.EXECUTOR_GROP.put(executor.MODULE_PACKAGENAME, executor);
		SystemHandler.EXECUTOR_INSTANCE.put(executor.MODULE_PACKAGENAME, executor);
	}

	public static void registerSchedular(ModuleScheduler schulder) {
		SystemHandler.SCHEDULER.put(schulder.MODULE_PACKAGENAME, schulder);
		SystemHandler.SCHEDULER_INSTANCE.put(schulder.MODULE_PACKAGENAME, new Thread(schulder));
	}

	public static ModuleTrigger getTrigger(String name) {
		if (SystemHandler.TRIGGER_INSTANCE.containsKey(name)) {
			return SystemHandler.TRIGGER_INSTANCE.get(name);
		} else {
			return null;
		}
	}

	public static ModuleListener getListener(String name) {
		if (SystemHandler.LISTENER_INSTANCE.containsKey(name)) {
			return SystemHandler.LISTENER_INSTANCE.get(name);
		} else {
			return null;
		}
	}

	public static ModuleExecutor getExecutor(String name) {
		if (SystemHandler.EXECUTOR_INSTANCE.containsKey(name)) {
			return SystemHandler.EXECUTOR_INSTANCE.get(name);
		} else {
			return null;
		}
	}

	public static ModuleScheduler getScheduler(String name) {
		if (SystemHandler.SCHEDULER.containsKey(name)) {
			return SystemHandler.SCHEDULER.get(name);
		} else {
			return null;
		}
	}

	public static Thread getSchedulerThread(String name) {
		if (SystemHandler.SCHEDULER_INSTANCE.containsKey(name)) {
			return SystemHandler.SCHEDULER_INSTANCE.get(name);
		} else {
			return null;
		}
	}

}
