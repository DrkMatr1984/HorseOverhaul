package com.github.boltydawg.horseoverhaul.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

import com.github.boltydawg.horseoverhaul.HorseOverhaul;
import com.github.boltydawg.horseoverhaul.StatHorse;

public class BreedingListener implements Listener{

	@EventHandler
	public void onBreed(EntityBreedEvent event) {
		
		if(event.getEntity() instanceof AbstractHorse && event.getBreeder() instanceof Player) {
			
			// ensures that the two parents are horses
			if(!( event.getFather() instanceof AbstractHorse && event.getMother() instanceof AbstractHorse )) return;
			
			Player player = (Player)event.getBreeder();
			
			// handle if either of the parents are neutered
			boolean fneut = event.getFather().getScoreboardTags().contains("ho.isNeutered");
			boolean mneut = event.getMother().getScoreboardTags().contains("ho.isNeutered");
			if( fneut || mneut ) {
				
				AbstractHorse father = (AbstractHorse)event.getFather();
				AbstractHorse mother = (AbstractHorse)event.getMother();
				
				String r = ChatColor.RESET + ChatColor.RED.toString();
				if ( fneut && mneut ) {
					player.sendMessage( r + "Both " + father.getName() + r + " and " + mother.getName() + r + " are neutered! The breed attempt fails");
				}
				else if( fneut ) {
					player.sendMessage( r + father.getName() + r + " is neutered! The breed attempt fails");
				}
				else {
					player.sendMessage( r + mother.getName() + r + " is neutered! The breed attempt fails");
				}
				
				father.setLoveModeTicks(0);
				mother.setLoveModeTicks(0);
				event.setCancelled(true);
				return;
				
			}
			
			// set the stats based off what what breeding food was used
			StatHorse foal = new StatHorse((AbstractHorse)event.getEntity());;
			if(HorseOverhaul.instance.config.foodEffects) {
				if(event.getBredWith().getType().equals(Material.ENCHANTED_GOLDEN_APPLE)) {
					foal = new StatHorse((AbstractHorse)event.getEntity(), (byte)2);
					event.getEntity().getScoreboardTags().add("ho.isNeutered");
				}
				if(event.getBredWith().getType().equals(Material.GOLDEN_APPLE))
					foal = new StatHorse((AbstractHorse)event.getEntity(), (byte)1);
			}

			foal.calculateBirth((AbstractHorse)event.getMother(),(AbstractHorse)event.getFather());
			foal.roach.setTamed(true);
		}
	}
}