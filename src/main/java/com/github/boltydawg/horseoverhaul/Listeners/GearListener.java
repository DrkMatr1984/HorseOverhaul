package com.github.boltydawg.horseoverhaul.Listeners;

import org.bukkit.Material;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Horse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.github.boltydawg.horseoverhaul.HorseOverhaul;

public class GearListener implements Listener {
	
	@EventHandler
	public void clickEntity(PlayerInteractEntityEvent event) {
		
		if(event.getRightClicked() instanceof AbstractHorse) {
			
			AbstractHorse abHorse = (AbstractHorse)event.getRightClicked();
			
			if( abHorse.isTamed() ) {
				
				if(!event.getPlayer().isSneaking()) {
					
					ItemStack hand = event.getPlayer().getInventory().getItemInMainHand();
					if(HorseOverhaul.instance.config.gearSaddles)
					    if( hand.getType().equals(Material.SADDLE) && abHorse.getInventory().getSaddle() == null ) {
						
						    abHorse.getInventory().setSaddle(hand);
						    event.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
						    event.setCancelled(true);
						
					    }
					if(HorseOverhaul.instance.config.gearArmor)
					    if(hand.getType().name().contains("HORSE_ARMOR") && event.getRightClicked() instanceof Horse) {
						    Horse horse = (Horse)event.getRightClicked();
						
						    if(horse.getInventory().getArmor() == null) {
							    horse.getInventory().setArmor(hand);
							    event.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
							    event.setCancelled(true);
						    }
					    }
		        }
			}
		}//add carpets for llama?
	}
}