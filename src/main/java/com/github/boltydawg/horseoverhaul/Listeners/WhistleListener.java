package com.github.boltydawg.horseoverhaul.Listeners;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.boltydawg.horseoverhaul.HorseOverhaul;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class WhistleListener implements Listener {
	
	private static HashMap<UUID,Integer> whistleBlowers = new HashMap<UUID,Integer>();
	
	public static ItemStack blankWhistle;
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		
		if( event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK) ) {
			
			ItemStack item = event.getItem();
			
			//player right clicks while holding a whistle
			if( item != null && item.getType().equals(Material.IRON_NUGGET) && item.hasItemMeta() && 
					item.getItemMeta().getItemFlags() != null && item.getItemMeta().getItemFlags().contains(ItemFlag.HIDE_UNBREAKABLE) ) {
				
				Player player = event.getPlayer();
				
				//double check that the horse's uuid is stored in the item
				String horseId = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(HorseOverhaul.instance, "whistle"), PersistentDataType.STRING);
				if(horseId == null) return;
				
				event.setCancelled(true);
				event.setUseInteractedBlock(Result.ALLOW);
				
				if( !whistleBlowers.containsKey(player.getUniqueId()) ) { //checks if the player is on cool-down
					
					//play a whistle sound (or several, to be precise)
					new BukkitRunnable() {
						
						private int plays = 0;

						@Override
						public void run() {
							
							if(plays < 6) {
								player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, SoundCategory.NEUTRAL, 2.0f, 1.15f);
								plays ++;
							}
							else {
								this.cancel();
							}
							
						}
					}.runTaskTimer(HorseOverhaul.instance, 0L, 2L);
					
					
					boolean found = false;	//boolean for whether or not the horse is detected
					
					for(Entity e : player.getNearbyEntities(100, 30, 100)) { //search a 100x30x100 radius		
						if(e instanceof AbstractHorse) {						
							LivingEntity le = (LivingEntity) e;
							
							if( le.getUniqueId().toString().equals(horseId) ) {
								
								found = true;
								
								if( HorseOverhaul.instance.config.whistleTeleport ) {
									le.teleport(player);
								}
								else {
									le.addPotionEffect( new PotionEffect( PotionEffectType.GLOWING, 200, 1, false, false ) );
								}
								
								le.getWorld().playSound(le.getLocation(), Sound.ENTITY_HORSE_ANGRY, 1.0f, 1.0f);
								break;
								
								}
						}
					}
					
					if(found) {
						TextComponent txt = new TextComponent(ChatColor.GREEN + "Horse located!");
						player.spigot().sendMessage(ChatMessageType.ACTION_BAR, txt);
					}
					else {
						TextComponent txt = new TextComponent(ChatColor.RED + "No response...");
						player.spigot().sendMessage(ChatMessageType.ACTION_BAR, txt);
					}
					
					//put the player on cool-down, as to not overload the server
					whistleBlowers.put(player.getUniqueId(), 10);
					
					new BukkitRunnable() {

						@Override
						public void run() {
							
							int time = whistleBlowers.get(player.getUniqueId());
							
							if(time <= 1) {
								whistleBlowers.remove(player.getUniqueId());
								this.cancel();
							}
							else {
								whistleBlowers.put(player.getUniqueId(), time - 1);
							}
						}
					}.runTaskTimer(HorseOverhaul.instance, 20L, 20L);
					
				}
				
				else {
					
					TextComponent txt = new TextComponent(ChatColor.YELLOW + "You must wait " + whistleBlowers.get(player.getUniqueId()) + " more seconds");
					player.spigot().sendMessage(ChatMessageType.ACTION_BAR, txt);
					
				}
				
			}
		}
	}
	/**
	 * Handles when a player right clicks a horse with a blank whistle.
	 * Runs after the ownership's eventhandler, that way if the player clicked
	 * 	a horse that they don't own, this will already be canceled
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onClickEntity (PlayerInteractEntityEvent event) {
		
		if(!event.isCancelled() && event.getRightClicked() instanceof AbstractHorse) {
			
			AbstractHorse abHorse = (AbstractHorse)event.getRightClicked();
			if(abHorse.isTamed()) {
				
				Player player = event.getPlayer();
				
				ItemStack item;
				if(event.getHand().equals(EquipmentSlot.HAND)) {
					item = player.getInventory().getItemInMainHand();
				}
				else {
					item = player.getInventory().getItemInOffHand();
				}
				
				if(item.isSimilar(WhistleListener.blankWhistle)) {
					AbstractHorse horse = (AbstractHorse) event.getRightClicked();
								
					ItemMeta met = item.getItemMeta();
					met.getPersistentDataContainer().set(new NamespacedKey(HorseOverhaul.instance, "whistle"), PersistentDataType.STRING, horse.getUniqueId().toString());
					
					if(horse.getCustomName() == null && horse instanceof Horse) {
						String color = ((Horse)horse).getColor().name();
						color = color.toCharArray()[0] + color.substring(1).toLowerCase();
						met.setDisplayName(ChatColor.YELLOW + color + " Horse's Whistle");
					}
					else if (horse.getCustomName() != null){
						met.setDisplayName(ChatColor.YELLOW + horse.getName() + "'s Whistle");
					}else {
						String type = horse.getType().name();
						type = type.toCharArray()[0] + type.substring(1).toLowerCase();
						met.setDisplayName(ChatColor.YELLOW + type + "'s Whistle");
					}
					
					
					if(item.getAmount() == 1) {
						item.setItemMeta(met);
					}
					else {
						item.setAmount(item.getAmount() - 1);
						ItemStack t = new ItemStack(Material.IRON_NUGGET);
						t.setItemMeta(met);
						player.getInventory().addItem(t);
					}
									
					player.sendMessage(ChatColor.YELLOW + "Whistle Carved!");
					player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 2.0f);
					event.setCancelled(true);
				}
				
			}
			
		}
	}
}