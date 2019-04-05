package studio.blacktech.coolqbot.furryblack.core;

import java.util.ArrayList;
import java.util.TreeMap;

import studio.blacktech.coolqbot.furryblack.module.ModuleLogic;

public class ExtensionManager {

	private static TreeMap<String, ModuleLogic> PLUGINS = new TreeMap<String, ModuleLogic	>();

	private static ArrayList<ModuleLogic> PLUGINS_LISTEN = new ArrayList<ModuleLogic>();
	private static ArrayList<ModuleLogic> PLUGINS_EVENTS = new ArrayList<ModuleLogic>();
	private static ArrayList<ModuleLogic> PLUGINS_STORAD = new ArrayList<ModuleLogic>();

}
