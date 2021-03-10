package com.github.boltydawg.horseoverhaul.Listeners;

import org.bukkit.Material;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class CaptureListener implements Listener{
	
	ItemStack captureEgg;
	
	public static void test(Horse horse, Player player) {
		Material mat = Material.getMaterial("HORSE_SPAWN_EGG");
		ItemStack egg = new ItemStack(mat, 1);
		player.sendMessage(horse.toString());
	}
	
	@EventHandler
	public void clickEntity(PlayerInteractEntityEvent event) {
		
		if(event.getRightClicked() instanceof AbstractHorse) {
			test((Horse)event.getRightClicked(), event.getPlayer());
		}
	}
}
