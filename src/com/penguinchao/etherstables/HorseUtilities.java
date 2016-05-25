package com.penguinchao.etherstables;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HorseUtilities {
	@SuppressWarnings("deprecation")
	public static void softAddItem(ItemStack item, Player player){
		HashMap<Integer, ItemStack> leftovers = player.getInventory().addItem(item);
		for( Map.Entry<Integer, ItemStack> me : leftovers.entrySet() ){
			player.getWorld().dropItem(player.getLocation(), me.getValue() );
		}
		player.updateInventory();
	}
	public static double truncateDouble(double currentNum, int decimalPlaces){
		BigDecimal bd = new BigDecimal(currentNum);
		bd = bd.setScale(decimalPlaces, BigDecimal.ROUND_FLOOR);
		return bd.doubleValue();
	}
	public static UUID getHorseOwner(Horse horse){ //Get the owner of a placed horse
		if(horse.isTamed()){
			//Horse is tamed -- Continuing
		}else{
			return null;
		}
		return horse.getOwner().getUniqueId();
	}
}
