package com.github.boltydawg.horseoverhaul.Listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.github.boltydawg.horseoverhaul.StatHorse;
import com.github.boltydawg.horseoverhaul.HorseOverhaul;

public class StatsListener implements Listener {
	
	/**
	 * This class holds the listener that triggers when a player right clicks a horse
	 * 	with the intention of checking its stats, and the necessary listeners to
	 *  allow players to store the stats of the last horse they inspected to a sign.
	*/
	 
	public static HashMap<UUID, ArrayList<String>> signStats = new HashMap<UUID, ArrayList<String>>();
	
	/**
	 * @param event - event triggered when a player right clicks a horse
	 * 
	 * The priority is set to low because this same event is handled by the ownership
	 * 	listener, and if the ownership listener wants to cancel the event, it needs to
	 * 	do so before this event runs
	 */
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onClick(PlayerInteractEntityEvent event) {
		
		if(event.getRightClicked() instanceof AbstractHorse) {
			
			Player player = event.getPlayer();
			AbstractHorse abHorse = (AbstractHorse)event.getRightClicked();
			if(event.isCancelled() || (!abHorse.isTamed() && !HorseOverhaul.instance.config.requireTamed) ) {
				
				return;
			}

			else if(event.getHand().equals(EquipmentSlot.HAND) && 
					event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.CARROT_ON_A_STICK)) {
				
				event.setCancelled(true);
				player.sendMessage(new StatHorse(abHorse).printStats(!HorseOverhaul.instance.config.ownershipEnabled));
				
				
				ArrayList<String> stats = new ArrayList<String>();
				StatHorse roach = new StatHorse(abHorse);
				if(abHorse.getCustomName() != null)
					stats.add(abHorse.getCustomName() + ChatColor.RESET + ":");
				else if (event.getRightClicked() instanceof Horse) {
					Horse horse = (Horse)event.getRightClicked();
					String color = horse.getColor().name();
					color = color.toCharArray()[0] + color.substring(1).toLowerCase();
					if(horse.isAdult())
						stats.add(color + " Horse: ");
					else
						stats.add(color + " Foal:");
				}
				else {
					String type = abHorse.getType().name();
					type = type.toCharArray()[0] + type.substring(1).toLowerCase();
					stats.add(type + " Horse: ");
				}
					
				stats.add("Health: " + roach.getHealth());
				stats.add("Speed: " + HorseOverhaul.df.format(roach.getSpeed()));
				stats.add("Jump: " + HorseOverhaul.df.format(roach.getJumpHeight()));
				
				signStats.put(player.getUniqueId(), stats);
				
			}
		}
	}
	
	/**
	 * Detects when a player right clicks a sign while holding a carrot on a stick
	 * @param event
	 */
	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getClickedBlock().getType().name().contains("_SIGN")
				&& event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.CARROT_ON_A_STICK) && signStats.containsKey(event.getPlayer().getUniqueId())) {
			
			Sign sign = (Sign)(event.getClickedBlock().getState());
			
			ArrayList<String> msg = signStats.get(event.getPlayer().getUniqueId());
			
			for(int i=0; i<4; i++) {
				
				sign.setLine(i, msg.get(i));

			}
			
			sign.update();
			
			signStats.remove(event.getPlayer().getUniqueId());
			
		}	
	}
	
	
	/**
	 * Removes a player's storage when they leave
	 * @param event
	 */
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		
		signStats.remove(event.getPlayer().getUniqueId());
		
	}
}