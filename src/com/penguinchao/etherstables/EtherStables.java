package com.penguinchao.etherstables;

import org.bukkit.plugin.java.JavaPlugin;

public class EtherStables extends JavaPlugin{
	protected HorseListener  horseListener;
	protected HorseManager   horseManager;
	protected HorseUtilities horseUtilities;
	private   boolean		 debugEnabled;
	@Override
	public void onEnable(){
		saveDefaultConfig();
		debugEnabled = getConfig().getBoolean("debug-mode");
		debugTrace("Debug Mode Enabled");
		debugTrace("Creating HorseManager");
		horseManager = new HorseManager(this);
		debugTrace("Creating HorseListener");
		horseListener = new HorseListener(this);
	}
	@Override
	public void onDisable(){
		//TODO Save horses
	}
	protected void debugTrace(String message){
		if(debugEnabled){
			getLogger().info("[DEBUG] "+message);
		}
	}
}
