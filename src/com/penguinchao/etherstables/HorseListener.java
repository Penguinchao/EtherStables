package com.penguinchao.etherstables;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class HorseListener implements Listener {
	private EtherStables main;
	public HorseListener(EtherStables passedMain){
		main = passedMain;
		main.debugTrace("[HorseListener] Registering for Events");
		main.getServer().getPluginManager().registerEvents(this, main);
		main.debugTrace("[HorseListener] Registered for Events");
		main.debugTrace("Created HorseListener");
	}
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event){
		//Prevent Theft
		if(event.getInventory() != null){
			if(event.getInventory() instanceof HorseInventory){
				//A horse inventory was opened
				HorseInventory inv = (HorseInventory) event.getInventory();
				InventoryHolder holder = inv.getHolder();
				if(holder != null){
					if(holder instanceof Horse){
						Horse horse = (Horse) holder;
						if(event.getPlayer().getUniqueId().equals(HorseUtilities.getHorseOwner(horse))){
							//Player Owns the horse
						}else if(HorseUtilities.getHorseOwner(horse) == null){
							//Horse is not owned
						}else{
							//Horse is owned
							Player player = (Player) event.getPlayer();
							player.sendMessage(ChatColor.RED + main.getConfig().getString("messages.no-inv-access"));
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}
	@EventHandler
	public void onPlayerTeleport(EntityTeleportEvent event){
		//Scoop horses on tp
		if(event.getEntity() instanceof Player){
			//Player teleported
			main.debugTrace("[onPlayerTeleport] A player teleported - Scooping all horses");
			Player player = (Player) event.getEntity();
			main.horseManager.scoopOtherHorses(player);
		}
	}
	//@EventHandler
	//public void onHorseDespawn(){
	//	//Scoop instead of despawn
	//}
	@EventHandler
	public void onHorseDeath(EntityDeathEvent event){
		//Prevent horse death
		if(event.getEntity() instanceof Horse){
			Horse horse = (Horse) event.getEntity();
			if(HorseUtilities.getHorseOwner(horse) != null){
				main.debugTrace("[onHorseDeath] An owned horse died. Dropping it as an egg");
				horse.getWorld().dropItem(horse.getLocation(), main.horseManager.scoopHorse(horse, true));
			}
		}
	}
	@EventHandler
	public void onHorseHarm(EntityDamageEvent event){
		//Prevent horse damage
		if(event.getEntity() instanceof Horse){
			Horse horse = (Horse) event.getEntity();
			if(HorseUtilities.getHorseOwner(horse) != null){
				event.setCancelled(true);
			}
		}
	}@EventHandler (priority = EventPriority.MONITOR)
	public void onHorseCancel(CreatureSpawnEvent event){
		main.debugTrace("[onHorseCancel]");
		if(event.getEntity() instanceof Horse){
			//Entity is a Horse
			main.debugTrace("[onHorseCancel] Entity is a horse");
		}else{
			//Not a horse
			main.debugTrace("[onHorseCancel] Entity is not a horse. Done");
			return;
		}
		if(event.getSpawnReason() == SpawnReason.CUSTOM){
			//Horse was spawned by plugin
			main.debugTrace("[onHorseCancel] Horse was spawned by a plugin");
		}else{
			//Not spawned by this plugin
			main.debugTrace("[onHorseCancel] Horse was spawned by a non-plugin source. Done");
			return;
		}
		if(event.isCancelled()){
			//Event is Cancelled
			main.debugTrace("[onHorseCancel] Event was cancelled");
		}else{
			//Event was not cancelled
			main.debugTrace("[onHorseCancel] Event was not cancelled. Done");
			return;
		}
		Horse horse = (Horse) event.getEntity();
		if(HorseUtilities.getHorseOwner(horse) != null){
			//Horse is owned
			main.debugTrace("[onHorseCancel] Horse is owned");
		}else{
			//Horse is not owned
			main.debugTrace("[onHorseCancel] Horse was not owned. Done");
			return;
		}
		main.debugTrace("[onHorseCancel] Getting player");
		Player player = Bukkit.getPlayer(HorseUtilities.getHorseOwner(horse));
		main.debugTrace("[onHorseCancel] Getting ItemStack");
		ItemStack item = main.horseManager.scoopHorse(horse, false);
		main.debugTrace("[onHorseCancel] Adding item to player");
		if(player == null){
			//Player is null
			main.debugTrace("[onHorseCancel] Player is null. Dropping instead.");
			horse.getWorld().dropItem(horse.getLocation(), item);
		}else{
			//Player is not null
			HorseUtilities.softAddItem(item, player);
			main.debugTrace("[onHorseCancel] Done");
		}
		
	}
	@EventHandler (priority = EventPriority.HIGH)
	public void onEggUse(CreatureSpawnEvent event){ //Egg Listener to spawn
		//Check if spawned entity is a horse
		if(event.getEntity() instanceof Horse){
			main.debugTrace("[CreatureSpawnEvent] A horse was spawned");
			//Entity is a horse
		}else{
			//Not a horse
			return;
		}
		//Check if spawned from an egg
		if(event.getSpawnReason() == SpawnReason.SPAWNER_EGG ){
			//Entity was spawned from an egg
			main.debugTrace("[CreatureSpawnEvent] A horse was spawned through an egg - preventing");
			event.getEntity().remove();
			event.setCancelled(true);
			return;
		}else{
			//Not spawned from an egg
			main.debugTrace("[CreatureSpawnEvent] A horse was spawned through a non egg - allowing");
			return;
		}
	}
	@EventHandler
	public void onHorsePlace(PlayerInteractEvent event){
		//Check if is a horse egg
		if(event.getPlayer().getItemInHand() == null){
			//Nothing in hand
			main.debugTrace("[PlayerInteractEvent] Nothing in hand");
			return;
		}else if(event.getPlayer().getItemInHand().getType() != Material.MONSTER_EGG){
			//Not a spawner egg
			main.debugTrace("[PlayerInteractEvent] Not a spawner egg");
			return;
		}else if(event.getPlayer().getItemInHand().getDurability() != 100){
			//Not a horse egg
			main.debugTrace("[PlayerInteractEvent] Not a horse egg");
			return;
		}else{
			main.debugTrace("[PlayerInteractEvent] Item is a horse egg");
		}
		//Check action
		if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			main.debugTrace("[PlayerInteractEvent] "+event.getAction().toString());
		}else{
			main.debugTrace("[PlayerInteractEvent] "+event.getAction().toString());
			return;
		}
		//Check for other horses
		if(main.getConfig().getBoolean("single-horse-mode")){
			event.getPlayer().sendMessage(ChatColor.GREEN+main.getConfig().getString("messages.all-horses-scooped"));
			main.horseManager.scoopOtherHorses(event.getPlayer());
		}
		//Create Horse
		main.horseManager.spawnHorseFromItem(event.getPlayer().getItemInHand(), event.getPlayer());
	}
	@EventHandler
	public void onHorseCollect(PlayerLeashEntityEvent event){//Scoop up horses when leashed
		//Check if horse
		if(event.getEntity() instanceof Horse){
			//A horse is being leashed
		}else{
			//A horse is not being leashed - Done here
			return;
		}
		//Check if horse is owned
		Horse horse = (Horse) event.getEntity();
		if(event.getPlayer().getUniqueId() == HorseUtilities.getHorseOwner(horse)){
			//Player owns the horse
		}else if(null == HorseUtilities.getHorseOwner(horse)){
			//Horse does not have owner
		}else{
			//Horse is owned by someone else
			//Tell player the horse is owned
			event.getPlayer().sendMessage(ChatColor.RED + main.getConfig().getString("messages.horse-owned"));
			event.setCancelled(true);
			return;
		}
		//Check for collect permission
		if(event.getPlayer().hasPermission("etherstables.collect")){
			//Player has permission
		}else{
			//Player does not have permission. Leash like normal
			return;
		}
		//Remove and store entity
		ItemStack[] items = horse.getInventory().getContents();
		ItemStack storedEgg = main.horseManager.scoopHorse(horse, false);
		
		if(storedEgg == null){
			//Unsuccessful Catch -- Untamed?
			return;
		}else{
			event.setCancelled(true);
		}
		//Give Player the egg for that horse
		HorseUtilities.softAddItem(storedEgg, event.getPlayer());
		for(int i = 0; i < items.length; i++){
			if(items[i] != null){
				HorseUtilities.softAddItem(items[i], event.getPlayer());
			}
		}
	}
}
