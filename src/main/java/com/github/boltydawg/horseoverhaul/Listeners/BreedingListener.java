package com.github.boltydawg.horseoverhaul.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

import com.github.boltydawg.horseoverhaul.CustomLang;
import com.github.boltydawg.horseoverhaul.StatHorse;

public class BreedingListener implements Listener{
	
	public static boolean betterBreeding, foodEffects;

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
					player.sendMessage( r + CustomLang.getConfig().getString("msg.breedFailAlert2"));
				}
				else if( fneut ) {
					player.sendMessage( r + father.getName() + r + CustomLang.getConfig().getString("msg.breedFailAlert"));
				}
				else {
					player.sendMessage( r + mother.getName() + r + CustomLang.getConfig().getString("msg.breedFailAlert"));
				}
				
				father.setLoveModeTicks(0);
				mother.setLoveModeTicks(0);
				event.setCancelled(true);
				return;
				
			}
			
			// set the stats based off what what breeding food was used
			StatHorse foal;
			if(event.getBredWith().getType().equals(Material.ENCHANTED_GOLDEN_APPLE)) {
				
				foal = new StatHorse((AbstractHorse)event.getEntity(), (byte)2);
				event.getEntity().getScoreboardTags().add("ho.isNeutered");
				
			}

			else if(event.getBredWith().getType().equals(Material.GOLDEN_APPLE))
				foal = new StatHorse((AbstractHorse)event.getEntity(), (byte)1);
			else
				foal = new StatHorse((AbstractHorse)event.getEntity());
			
			
			foal.calculateBirth((AbstractHorse)event.getMother(),(AbstractHorse)event.getFather());
			foal.roach.setTamed(true);
			
		}
	}
}