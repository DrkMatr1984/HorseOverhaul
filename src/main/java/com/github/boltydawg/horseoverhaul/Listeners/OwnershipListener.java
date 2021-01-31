package com.github.boltydawg.horseoverhaul.Listeners;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Horse;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.boltydawg.horseoverhaul.CustomLang;
import com.github.boltydawg.horseoverhaul.Main;
import com.github.boltydawg.horseoverhaul.NamePrompt;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class OwnershipListener implements Listener {
		
	public static ItemStack blankDeed;
	
	public static boolean ownership, coloredNames, craftDeed;
	
	
	private static ItemStack getDeed(UUID horseID, String horsey, UUID pID, String pname) {
		
		ItemStack deed = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta met = (BookMeta)deed.getItemMeta();
		
		met.setDisplayName(ChatColor.GREEN.toString() + "Deed to " + horsey);
		met.setAuthor(null);
		met.setGeneration(Generation.ORIGINAL);
		
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.BLUE + "Property of");
		lore.add(ChatColor.BLUE + pname);
		met.setLore(lore);
		
		
		met.setPages(horseID.toString(), pID.toString());
		
		deed.setItemMeta(met);
		
		return deed;
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		
		if(event.getEntity() instanceof AbstractHorse) {
			
			AbstractHorse abHorse = (AbstractHorse) event.getEntity();
			
			if(abHorse.getOwner() != null && abHorse.getScoreboardTags().contains("ho.isOwned")) {
				
				if(event.getEntity() instanceof Horse && ((Horse)event.getEntity()).getInventory().getArmor() != null) {
					if(event.getDamager() instanceof Player) {

						Player player = (Player) event.getDamager();
						
						if(abHorse.getOwner().getUniqueId().equals(player.getUniqueId())) {
							
							event.setCancelled(true);
							abHorse.getWorld().playSound(abHorse.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5F, 1.3F);
							
						}
					}
	
					else if(event.getDamager() instanceof Arrow) {
						
						Arrow arrow = (Arrow) event.getDamager();
						
						if(arrow.getShooter() != null && arrow.getShooter() instanceof Player) {
							
							Player player = (Player) arrow.getShooter();
							
							if(abHorse.getOwner().getUniqueId().equals(player.getUniqueId())) {
								
								event.setCancelled(true);
								abHorse.getWorld().playSound(abHorse.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5F, 1.3F);
								
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onTame(EntityTameEvent event) {
		
		if(event.getEntity() instanceof AbstractHorse) {
			
			AbstractHorse abHorse = (AbstractHorse)event.getEntity();
			
			new BukkitRunnable() {
				@Override
				public void run() {
					abHorse.setOwner(null);
					abHorse.setTamed(true);
				}
			}.runTaskLater(Main.instance, 1L);
			
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onClickHorse(PlayerInteractEntityEvent event) {
		
		if(event.getRightClicked() instanceof AbstractHorse) {
			
			Player player = event.getPlayer();
			AbstractHorse abHorse = (AbstractHorse)event.getRightClicked();
			ItemStack main = player.getInventory().getItemInMainHand();
			ItemStack off = player.getInventory().getItemInOffHand();
			
			if(Material.NAME_TAG.equals(main.getType()) || Material.NAME_TAG.equals(off.getType())) {
				
				
				if(abHorse.getOwner() != null && abHorse.getScoreboardTags().contains("ho.isOwned") && abHorse.getOwner().getUniqueId().equals(player.getUniqueId()) ) {
					
					if(main != null && off != null && off.equals(getDeed(abHorse.getUniqueId(), abHorse.getCustomName(), player.getUniqueId(), player.getName())) 
							&& main.getItemMeta().hasDisplayName()) {
						
						player.getInventory().setItemInOffHand(getDeed(abHorse.getUniqueId(), main.getItemMeta().getDisplayName(), player.getUniqueId(), player.getName()) );
						
					}
					else {
						
						event.setCancelled(true);
						player.sendMessage(ChatColor.RED + CustomLang.getConfig().getString("msg.renameWarning"));
						
					}
				}
				else {
					
					event.setCancelled(true);
					player.sendMessage(CustomLang.getConfig().getString("msg.renameBlocked"));
					
				}	
			}
			
			else if(event.getHand().equals(EquipmentSlot.HAND)) {
				
				if(abHorse.getOwner() != null && abHorse.getScoreboardTags().contains("ho.isOwned")) {
					
					if( !abHorse.getOwner().getUniqueId().equals(player.getUniqueId()) ) {
						
						if( main != null && main.hasItemMeta() && main.getItemMeta().hasDisplayName() 
								&& main.getItemMeta().getDisplayName().contains("Deed to")
								&& main.getItemMeta().hasLore()
								&& main.getItemMeta().getLore().get(0).contains("Property of")
								&& main.getType().equals(Material.WRITTEN_BOOK) ) {
							
							BookMeta met = (BookMeta)main.getItemMeta();
							
							UUID horseId = UUID.fromString(met.getPage(1));
						
							if(abHorse.getUniqueId().equals(horseId)) {
								
								if(Generation.ORIGINAL.equals(met.getGeneration())) {
									
									if(player.hasPermission("horseo.claimOwned")) {
										player.getInventory().setItemInMainHand(null);
										claimHorse(abHorse, player, abHorse.getCustomName());
									}
									else {
										player.sendMessage(ChatColor.RED + "You don't have permission to claim other people's horses");
									}

								}
								else {
							
									TextComponent msg = new TextComponent();
									msg.setText("You need the original copy!");
									msg.setColor(ChatColor.RED);
									player.spigot().sendMessage(ChatMessageType.ACTION_BAR,msg);
									
									abHorse.getWorld().playSound(abHorse.getLocation(), Sound.ENTITY_HORSE_ANGRY, 0.8F, 1.0F);
							
								}
								
								event.setCancelled(true);
								return;
							}		
						}
						
						if(player.hasPermission("horseo.interactAll")) {
							return;
						}
						
						abHorse.getWorld().playSound(abHorse.getLocation(), Sound.ENTITY_HORSE_ANGRY, 0.8F, 1.0F);
						event.setCancelled(true);
				
						TextComponent msg = new TextComponent();
						msg.setText(player.getServer().getOfflinePlayer(abHorse.getOwner().getUniqueId()).getName() + " owns this horse!");
						msg.setColor(ChatColor.RED);
						player.spigot().sendMessage(ChatMessageType.ACTION_BAR,msg);
						
					}
					
					else if( !abHorse.isAdult() && Material.SHEARS.equals(main.getType())) {
						
						if(abHorse.getScoreboardTags().contains("ho.isNeutered")) {
							
							player.sendMessage(ChatColor.RED + ("You've already neutered "+ abHorse.getCustomName() + "!"));
						}
						
						else {
							
							abHorse.getScoreboardTags().add("ho.isNeutered");
							
							abHorse.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 10));
							abHorse.getWorld().playSound(abHorse.getLocation(), Sound.ENTITY_HORSE_DEATH, 1.2f, 1.5f);
							
							new BukkitRunnable() {
								@Override
								public void run() {
									abHorse.getWorld().playSound(abHorse.getLocation(), Sound.ENTITY_CHICKEN_EGG, 0.9f, 1.3f);
									player.sendMessage(ChatColor.RED + ("You have successfully neutered "+ abHorse.getCustomName() + ". He/she will never breed."));
								}
							}.runTaskLater(Main.instance, 20L);
							
						}
					}
					
				}
				else if(main != null && main.isSimilar(OwnershipListener.blankDeed)) {
						
					if( abHorse.isTamed() ) {
						
						event.setCancelled(true);
						
						if(player.isConversing()) {
							
							player.sendRawMessage(ChatColor.RED + "You must finish naming the current horse before claiming another!");
							return;
							
						}
						
						if(!player.hasPermission("horseo.claimWild")) {
							player.sendMessage(ChatColor.RED + "You do not have permission to claim wild horses");
							return;
						}
						
						player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
						
						ConversationFactory cf = new ConversationFactory(Main.instance);
						Conversation conv = cf.withFirstPrompt(new NamePrompt(player,abHorse)).withLocalEcho(true).buildConversation(player);
						conv.begin();
						
					}
					else {
						player.sendMessage(ChatColor.RED + "You must tame this horse before claiming it!");
					}
					
				}
			}
		}
	}
	
	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {
		
		if(event.getItem() != null && Material.WRITTEN_BOOK.equals(event.getItem().getType())
				&& event.getItem().hasItemMeta()
				&& event.getItem().getItemMeta().hasDisplayName() 
				&& event.getItem().getItemMeta().getDisplayName().contains("Deed to ")
				&& event.getItem().getItemMeta().hasLore()
				&& event.getItem().getItemMeta().getLore().get(0).contains("Property of")) {
			
			event.setUseItemInHand(Result.DENY);
			
		}
	}
	
	/**
	 * Handles the claiming of a horse
	 * @param abHorse
	 * @param player
	 * @param horseName
	 */
	public static void claimHorse(AbstractHorse abHorse, Player player, String horseName) {
		
		String previousOwner = null;
		
		if(abHorse.getOwner() != null && abHorse.getScoreboardTags().contains("ho.isOwned")) {
			previousOwner = abHorse.getOwner().getName();
		}
		
		abHorse.setOwner(player);
		abHorse.getScoreboardTags().add("ho.isOwned");
		abHorse.setCustomName(horseName);
		
		abHorse.getWorld().playSound(abHorse.getLocation(), Sound.ENTITY_HORSE_ARMOR, 0.9f, 2.0f);
		player.sendRawMessage(ChatColor.GREEN + ("You are now the proud owner of " + horseName + "!"));
		
		player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 0.5f);
		
		String info = player.getName() + " claims horse " + horseName;
		if( previousOwner != null) {
			info += " from previous owner: " + previousOwner + ", ";
		}
		else {
			info += " from nature, ";
		}
		info += "in world: " + abHorse.getWorld().getName() 
				+ ", at coords: x=" + (int)abHorse.getLocation().getX() + " y=" + (int)abHorse.getLocation().getY() + " z=" + (int)abHorse.getLocation().getZ();
		
		Main.instance.getLogger().info(info);
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				
				player.getInventory().addItem(getDeed(abHorse.getUniqueId(), abHorse.getCustomName(), player.getUniqueId(), player.getName()));
				
			}
		}.runTaskLater(Main.instance, 4);
		
		
	}
}