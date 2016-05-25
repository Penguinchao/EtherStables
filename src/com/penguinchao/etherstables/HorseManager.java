package com.penguinchao.etherstables;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.Player;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class HorseManager {
	private EtherStables main;
	public HorseManager(EtherStables passedMain){
		main = passedMain;
		main.debugTrace("Created Horse Manager");
	}
	protected ItemStack scoopHorse(Horse horse, boolean dropInventory){ //Despawns the horse, then returns the spawn egg for that horse
		//Check Tamed
		if(horse.isTamed()){
			main.debugTrace("[scoopHorse] Horse is Tamed");
		}else{
			main.debugTrace("[scoopHorse] Horse is not Tamed - not scooping");
			return null;
		}
		//Gather Horse Info
		main.debugTrace("[scoopHorse] Gathering Horse Info");
		Variant variant = horse.getVariant();
		org.bukkit.entity.Horse.Color color = horse.getColor();
		Style style = horse.getStyle();
		double jumpStrength = horse.getJumpStrength();
		//boolean carryingChest = horse.isCarryingChest();
		int age = horse.getAge();
		String name = horse.getCustomName();		
		//Drop Inventory
		main.debugTrace("[scoopHorse] Get Inventory");
		HorseInventory horseItems = horse.getInventory();
		if(horseItems != null){
			/*
			 * Commented out, because it is in otherItems too
			if(horseItems.getSaddle() != null){
				horse.getWorld().dropItem(horse.getLocation(), horseItems.getSaddle() );
			}
			if(horseItems.getArmor() != null){
				horse.getWorld().dropItem(horse.getLocation(), horseItems.getArmor() );
			}
			*/
			if(dropInventory){
				main.debugTrace("[scoopHorse] Dropping Inventory");
				ItemStack[] otherItems = horseItems.getContents();
				if(otherItems != null){
					//Drop all chest items
					for(int i = 0; i < otherItems.length; i++){
						if(otherItems[i] != null){
							horse.getWorld().dropItem(horse.getLocation(), otherItems[i] );
						}
					}
				}
			}
			/*
			 * Commented out, because it will store chest state
			if(horse.isCarryingChest()){
				//Drop Chest
				ItemStack chest = new ItemStack(Material.CHEST, 1);
				horse.getWorld().dropItem(horse.getLocation(), chest );
			}
			*/
		}
		//Store Horse
		main.debugTrace("[scoopHorse] Storing Horse");
		main.debugTrace("[scoopHorse] Creating Item Stack");
		ItemStack returnMe = new ItemStack(Material.MONSTER_EGG , 1, (short) 100);
		main.debugTrace("[scoopHorse] Getting Meta");
		ItemMeta meta = returnMe.getItemMeta();
		if(name != null){
			//Horse Name is Set
			main.debugTrace("[scoopHorse] Horse name is set");
			meta.setDisplayName(name);
		}
		main.debugTrace("[scoopHorse] Creating Lore");
		List<String> lore = new ArrayList<String>();
		main.debugTrace("[scoopHorse] Adding Variant");
		if(variant == null){
			lore.add("Horse");
		}else{
			if(variant == Horse.Variant.DONKEY){
				lore.add("Donkey");
			}else if(variant == Horse.Variant.HORSE){
				lore.add("Horse");
			}else if(variant == Horse.Variant.MULE){
				lore.add("Mule");
			}else if(variant == Horse.Variant.SKELETON_HORSE){
				lore.add("Skeleton");
			}else if(variant == Horse.Variant.UNDEAD_HORSE){
				lore.add("Undead");
			}else {
				main.debugTrace("Horse Variant was not set. Storing a horse.");
				lore.add("Horse");
			}
		}
		main.debugTrace("[scoopHorse] Adding Color");
		if(color == null){
			//Color is null
			//lore.add("Snow White");
		}else{
			if(color == Horse.Color.BLACK){
				//Black
				lore.add("Black");
			}else if(color == Horse.Color.BROWN){
				//Brown
				lore.add("Brown");
			}else if(color == Horse.Color.CHESTNUT){
				//Chestnut
				lore.add("Chestnut");
			}else if(color == Horse.Color.CREAMY){
				//Creamy
				lore.add("Creamy");
			}else if(color == Horse.Color.DARK_BROWN){
				//Dark Brown
				lore.add("Dark Brown");
			}else if(color == Horse.Color.GRAY){
				//Gray
				lore.add("Gray");
			}else if(color == Horse.Color.WHITE){
				//White
				lore.add("Snow White");
			}else {
				//TODO Add something?
			}
		}
		main.debugTrace("[scoopHorse] Adding Style");
		if(style == null){
			lore.add("No Markings");
		}else{
			if(style == Horse.Style.NONE){
				lore.add("No Markings");
			} else if(style == Horse.Style.BLACK_DOTS){
				lore.add("Black Dots");
			} else if(style == Horse.Style.WHITE){
				lore.add("White Markings");
			} else if(style == Horse.Style.WHITE_DOTS){
				lore.add("White Dots");
			} else if(style == Horse.Style.WHITEFIELD){
				lore.add("Milky Splotches");
			} else {
				main.debugTrace("Horse Style was not set. Storing no markings");
				lore.add("No Markings");
			}
		}
		main.debugTrace("[scoopHorse] Adding Age");
		lore.add("Age: "+age);
		main.debugTrace("[scoopHorse] Adding Strength");
		if(main.getConfig().getBoolean("truncate-stats")){
			//Stats will be truncated
			lore.add("Strength: "+HorseUtilities.truncateDouble(jumpStrength, main.getConfig().getInt("truncate-precision")));
		}else{
			//Stats will not be truncated
			lore.add("Strength: "+jumpStrength);
		}
		meta.setLore(lore);
		returnMe.setItemMeta(meta);
		//Remove entity
		horse.remove();
		//Return Egg
		main.debugTrace("[scoopHorse] Returning Egg");
		return returnMe;
	}
	protected Horse spawnHorseFromItem(ItemStack item, Player player){
		main.debugTrace("[spawnHorseFromItem] Spawning Entity");
		Horse horse = (Horse) player.getWorld().spawnEntity(player.getLocation(), EntityType.HORSE);
		main.debugTrace("[spawnHorseFromItem] Getting Item Meta");
		ItemMeta meta = item.getItemMeta();
		if(meta == null){
			main.debugTrace("Meta is null. Returning Random");
			return horse;
		}
		//Lookup horse entity from egg - return random horse if not found
		main.debugTrace("[spawnHorseFromItem] Getting display name");
		if(meta.getDisplayName() == null){
			main.debugTrace("[spawnHorseFromItem] No display name");
		}else{
			horse.setCustomName(meta.getDisplayName());
		}
		main.debugTrace("[spawnHorseFromItem] Getting Lore");
		List<String> lore = meta.getLore();
		if(lore != null){
			for(String entry : lore){
				//Copy Text to Horse Attributes
				if(entry.equals("Horse")){
					horse.setVariant(Horse.Variant.HORSE);
				}else if(entry.equals("Donkey")){
					horse.setVariant(Horse.Variant.DONKEY);
				}else if(entry.equals("Mule")){
					horse.setVariant(Horse.Variant.MULE);
				}else if(entry.equals("Skeleton")){
					horse.setVariant(Horse.Variant.SKELETON_HORSE);
				}else if(entry.equals("Undead")){
					horse.setVariant(Horse.Variant.UNDEAD_HORSE);
				}else if(entry.equals("Black")){
					horse.setColor(Horse.Color.BLACK);
				}else if(entry.equals("Brown")){
					horse.setColor(Horse.Color.BROWN);
				}else if(entry.equals("Chestnut")){
					horse.setColor(Horse.Color.CHESTNUT);
				}else if(entry.equals("Creamy")){
					horse.setColor(Horse.Color.CREAMY);
				}else if(entry.equals("Dark Brown")){
					horse.setColor(Horse.Color.DARK_BROWN);
				}else if(entry.equals("Gray")){
					horse.setColor(Horse.Color.GRAY);
				}else if(entry.equals("Snow White")){
					horse.setColor(Horse.Color.WHITE);
				}else if(entry.equals("No Markings")){
					horse.setStyle(Horse.Style.NONE);
				}else if(entry.equals("Black Dots")){
					horse.setStyle(Horse.Style.BLACK_DOTS);
				}else if(entry.equals("White Markings")){
					horse.setStyle(Horse.Style.WHITE);
				}else if(entry.equals("White Dots")){
					horse.setStyle(Horse.Style.WHITE_DOTS);
				}else if(entry.equals("Milky Splotches")){
					horse.setStyle(Horse.Style.WHITEFIELD);
				}else{
					String[] rowArray = entry.split(" ");
					if(rowArray[0].equals("Age:")){
						Integer age = new Integer(rowArray[1]);
						horse.setAge(age);
					}else if(rowArray[0].equals("Strength:")){
						Double strength = new Double(rowArray[1]);
						horse.setJumpStrength(strength);
					}else {
						main.debugTrace("[spawnHorseFromItem] "+entry+" is not a horse attribute");
					}
				}
			}
		}
		//Set Ownership
		main.debugTrace("[spawnHorseFromItem] Setting ownership");
		horse.setTamed(true);
		AnimalTamer tamer = (AnimalTamer) player;
		horse.setOwner(tamer);
		return horse;
	}
	public void scoopOtherHorses(Player player){
		List<LivingEntity> allEntities = player.getWorld().getLivingEntities();
		for(Entity entity : allEntities){
			if(entity instanceof Horse){
				//Is Horse, Continue pro
				Horse horse = (Horse) entity;
				if(horse.isTamed()){
					//Horse is tamed, Continue
					if(HorseUtilities.getHorseOwner(horse) == player.getUniqueId()){
						//Player owns horse
						//Get Inventory 
						ItemStack[] items = horse.getInventory().getContents();
						//Give Egg
						HorseUtilities.softAddItem(scoopHorse(horse, false), player);
						//Give Items
						for(int i = 0; i < items.length; i++){
							if(items[i] != null){
								HorseUtilities.softAddItem(items[i], player);
							}
						}
					}
				}
			}
		}
	}
}
