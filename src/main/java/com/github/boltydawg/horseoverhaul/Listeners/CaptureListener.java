package com.github.boltydawg.horseoverhaul.Listeners;

import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import com.github.boltydawg.horseoverhaul.Main;
import com.github.boltydawg.horseoverhaul.StatHorse;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class CaptureListener implements Listener{
	
	//fields
	private ItemStack captureBall;
	
	
	//constructor
	public CaptureListener() {
		
		super();
		
		//TODO get the captureBall from recipes.yml
		this.captureBall = new ItemStack(Material.FIREWORK_STAR);
		ItemMeta met = captureBall.getItemMeta();
		met.setDisplayName("Capture Ball (empty)");
		ArrayList<String> lore = new ArrayList<String>();
		//TODO pull this from config. 0 = don't put this here
		lore.add(ChatColor.RED + "Uses: 10");
		met.setLore(lore);
		
		captureBall.setItemMeta(met);
	}
	
	
	//get methods
	public ItemStack getCaptureBall() {
		return this.captureBall;
	}
	

	//TODO finish all of the conditions
	@EventHandler
	public void clickEntity(PlayerInteractEntityEvent event) {
		
		if(event.getRightClicked() instanceof AbstractHorse) {
			
			Player player = event.getPlayer();
			ItemStack item = event.getHand().name().equalsIgnoreCase("HAND") ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInOffHand();
			
			if(item != null && isCaptureBall(item) ) { 
				
				AbstractHorse abHorse = (AbstractHorse)event.getRightClicked();
				
				if(abHorse.getOwner() != null && abHorse.getOwner().equals(player)) {
					
					item.setItemMeta(captureHorse(abHorse, player, item));
					
					//TODO test on: chested mules, horse armor, llamas, and saddles of course
					for(ItemStack invItem : abHorse.getInventory().getContents()) {
						
						if(invItem != null) {
							
							abHorse.getWorld().dropItemNaturally(abHorse.getLocation(), invItem);
							
						}
					}
				}
			}
		}
	}
	
	//TODO finish this
	private boolean isCaptureBall(ItemStack item) {
		ItemMeta met = item.getItemMeta();
		
		if(met == null) return false;
		
		return item.getType().equals(captureBall.getType()); 
	}

	@EventHandler
	public void spawnInHorseFromCapture(PlayerInteractEvent event) {
		
		//check if a block was clicked with a firework star
		if(event.getItem() != null && event.getItem().hasItemMeta() && 
				Action.RIGHT_CLICK_BLOCK.equals(event.getAction()) && Material.FIREWORK_STAR.equals(event.getItem().getType())) {
			
			ItemStack item = event.getItem();
			ItemMeta met = item.getItemMeta();
			Player player = event.getPlayer();
			Block clickedBlock = event.getClickedBlock();
			//fetch the data that could be stored in inside the item entity
			String dataString = met.getPersistentDataContainer().getOrDefault(new NamespacedKey(Main.instance, "ho.capture-data"), PersistentDataType.STRING, "");
			
			
			//check if it's a capture ball with data stored inside it
			if(dataString != "" && clickedBlock != null) {
				
				String[] data = dataString.split(",");
				
				if(!player.getName().equals(data[0])) {
					
					TextComponent tc = new TextComponent();
					tc.setText("Only the horse's owner can spawn it back in");
					tc.setColor(ChatColor.RED);
					player.spigot().sendMessage(ChatMessageType.ACTION_BAR, tc);
					
					return;
					
				}
			
				AbstractHorse abHorse = (AbstractHorse)(clickedBlock.getWorld().spawnEntity(clickedBlock.getLocation().add(0, 1.0, 0), EntityType.valueOf(data[1])));
				
				setAttributesFromData(abHorse, data);
				
				abHorse.setOwner(player);
				
				//TODO replace this with the met of our stored itemstack
				ItemMeta clearedMet = new ItemStack(Material.FIREWORK_STAR).getItemMeta();
				clearedMet.setDisplayName("Empty Capture Ball");
				
				item.setItemMeta(clearedMet);
			}
		}	
	}
	
	//0: owner, 1: type, 2: name, 3: age, 4: maxHealth, 5: curHealth, 6: speed, 7: jump, 8: neutered, 9: color, 10: style/strength 
	private void setAttributesFromData(AbstractHorse abHorse, String[] data) {
		
		abHorse.setCustomName(data[2]);
		
		int age = Integer.parseInt(data[3]);
		abHorse.setAge(age);
		
		double maxHealth = Double.parseDouble(data[4]);
		abHorse.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
		
		double curHealth = Double.parseDouble(data[5]);
		abHorse.setHealth(curHealth);
		
		double speed = Double.parseDouble(data[6]);
		abHorse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
		
		double jump = Double.parseDouble(data[7]);
		abHorse.getAttribute(Attribute.HORSE_JUMP_STRENGTH).setBaseValue(jump);
		
		if(data[8].equals("t")) {
			abHorse.getScoreboardTags().add("ho.isNeutered");
		}
		
		if (abHorse instanceof Horse) {
			Horse horse = (Horse)abHorse;
			horse.setColor(Horse.Color.valueOf(data[9]));
			horse.setStyle(Style.valueOf(data[10]));
		}
		else if (abHorse instanceof Llama) {
			Llama llama = (Llama)abHorse;
			llama.setColor(org.bukkit.entity.Llama.Color.valueOf(data[9]));
			llama.setStrength(Integer.parseInt(data[10]));
		}
	}

	private  static ItemMeta captureHorse(AbstractHorse abHorse, Player player, ItemStack item) {
		
		FireworkEffectMeta met = (FireworkEffectMeta)(item.getItemMeta());
		
		met.setDisplayName("Filled Capture Orb");
		
		met.setLore(getLore(abHorse));
		
		String data = createDataString(abHorse, player.getName());
		
		met.getPersistentDataContainer().set(new NamespacedKey(Main.instance, "ho.capture-data"), PersistentDataType.STRING, data);
		
		abHorse.remove();
		
		FireworkEffect.Builder builder = FireworkEffect.builder();
		builder.withColor(Color.YELLOW, Color.GREEN);
		met.setEffect(builder.build());
		
		//TODO find an itemflag that works, this does not
		met.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		
		//TODO remove this
		player.sendMessage(data);
		
		return met;
	}
	
	//0: owner, 1: type, 2: name, 3: age, 4: curHealth, 5: maxHealth, 6: speed, 7: jump, 8: neutered, 9: color, 10: style/strength 
	private static String createDataString(AbstractHorse abHorse, String pname) {
		
		String data = pname + ",";
		data += abHorse.getType().name() + ",";
		
		data += abHorse.getCustomName() + ",";
		data += abHorse.getAge() + ",";
		data += Main.df.format(abHorse.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()) + ",";
		data += Main.df.format(abHorse.getHealth()) + ",";
		data += Main.df.format(abHorse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue()) + ",";
		data += Main.df.format(abHorse.getAttribute(Attribute.HORSE_JUMP_STRENGTH).getBaseValue()) + ",";
		data += abHorse.getScoreboardTags().contains("ho.isNeutered") ? "t," : "f,";
		
		if (abHorse instanceof Horse) {
			Horse horse = (Horse)abHorse;
			data += horse.getColor().name() + ",";
			data += horse.getStyle().name();
		}
		else if (abHorse instanceof Llama) {
			Llama llama = (Llama)abHorse;
			data += llama.getColor().name() + ",";
			data += llama.getStrength();
		}
		
		return data;
	}
	
	private static ArrayList<String> getLore(AbstractHorse abHorse) {
		
		StatHorse statHorse = new StatHorse(abHorse);
		
		ArrayList<String> lore = new ArrayList<String>();
		
		lore.add("Contains " + statHorse.getName());
//		lore.add("Health: " + Main.df.format(statHorse.getHealth()));
//		lore.add("Speed: " + Main.df.format(statHorse.getSpeed()));
//		lore.add("Jump: " + Main.df.format(statHorse.getJumpHeight()));
		lore.add("Owned by " + abHorse.getOwner().getName());
		
		return lore;
	}
}
