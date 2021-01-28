package com.github.boltydawg.horseoverhaul;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.boltydawg.horseoverhaul.Listeners.BreedingListener;
import com.github.boltydawg.horseoverhaul.Listeners.GearListener;
import com.github.boltydawg.horseoverhaul.Listeners.NerfListener;
import com.github.boltydawg.horseoverhaul.Listeners.DeathListener;
import com.github.boltydawg.horseoverhaul.Listeners.OwnershipListener;
import com.github.boltydawg.horseoverhaul.Listeners.StatsListener;
import com.github.boltydawg.horseoverhaul.Listeners.WhistleListener;


/**
 * A plugin that improves many aspects of owning/breeding horses
 * 
 * @author BoltyDawg
 */

//TODO horse capturing system like pokemon?
//TODO test breeding algorithm some more?
//TODO look into changing method of storing horse data?
//TODO add way to "unlock" a horse?


public class Main extends JavaPlugin{
	
	public static DecimalFormat df = new DecimalFormat("0.00");
	
	public static JavaPlugin instance;
	
	@Override
	public void onEnable() {
		
		instance = this;
		
		// setup config
		CustomConfig.setup();
		this.readConfig(CustomConfig.getConfig());
		// setup lang
		CustomLang.setup();
		
		// commands
		this.getCommand("horseo").setExecutor(new CommandHorseo());
	}
	
	@Override
	public void onDisable() {
		this.getLogger().info("Saving config");
		CustomConfig.save();
	}
	
	/**
	 * read and set the config's values
	 * @param config
	 */
	public void readConfig(FileConfiguration config) {
		if(config.getBoolean("autoGearEquip.enabled")) {

			this.getServer().getPluginManager().registerEvents(new GearListener(), this);
			
		}
		if(config.getBoolean("betterBreeding.enabled")) {
			
			this.getServer().getPluginManager().registerEvents(new BreedingListener(), this);
			
			BreedingListener.betterBreeding = true;
			
			if(config.getBoolean("betterBreeding.foodEffects")) {
				
				BreedingListener.foodEffects = true;
				
			}
			
		}
		if(config.getBoolean("checkStats.enabled")) {
			
			this.getServer().getPluginManager().registerEvents(new StatsListener(), this);
			
			StatsListener.checkStats = true;
			
			if(config.getBoolean("checkStats.requireTamed")) {
				
				StatsListener.untamed = true;
				
			}
			else
				StatsListener.untamed = false;
		}
		
		if(config.getBoolean("dropGear.enabled")) {
			
			this.getServer().getPluginManager().registerEvents(new DeathListener(), this);
			
		}
		if(config.getBoolean("ownership.enabled")) {
			
			this.getServer().getPluginManager().registerEvents(new OwnershipListener(), this);
			
			OwnershipListener.ownership = true;
			
			OwnershipListener.blankDeed = new ItemStack(Material.PAPER);
			ItemMeta met = OwnershipListener.blankDeed.getItemMeta();
			met.setDisplayName("Blank Deed");
			ArrayList<String> lore = new ArrayList<String>();
			lore.add(ChatColor.GRAY + "Right click an unclaimed");
			lore.add(ChatColor.GRAY + "horse to make it yours");
			met.setLore(lore);
			OwnershipListener.blankDeed.setItemMeta(met);
			
			if(config.getBoolean("ownership.craftingRecipes")) {
				
				ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(this, "blankDeed"),OwnershipListener.blankDeed);
				recipe.addIngredient(1, Material.WRITABLE_BOOK);
				recipe.addIngredient(1, Material.GOLDEN_CARROT);
				
				this.getServer().addRecipe(recipe);
				
				OwnershipListener.craftDeed = true;
				
			}
			
			OwnershipListener.coloredNames = config.getBoolean("ownership.coloredNames");
				
		}
		if(config.getBoolean("nerfWildSpawns.enabled")) {
			
			this.getServer().getPluginManager().registerEvents(new NerfListener(), this);
			
			NerfListener.nerf = config.getDouble("nerfWildSpawns.factor", 1.5);
			
			if(config.getBoolean("nerfWildSpawns.override")) {
				
				NerfListener.override = true;
				
				for (World w: instance.getServer().getWorlds()){
					for(LivingEntity e: w.getLivingEntities()) {
						if(e.isValid() && e instanceof AbstractHorse && !e.getScoreboardTags().contains("ho.isNerfed")) {
							NerfListener.nerf((AbstractHorse)e);
						}
					}
				}
			}
			else
				NerfListener.override = false;
		}
		if(config.getBoolean("whistles.enabled")) {
			
			this.getServer().getPluginManager().registerEvents(new WhistleListener(), this);
			
			WhistleListener.whistle = true;
			
			WhistleListener.blankWhistle = new ItemStack(Material.IRON_NUGGET);
			ItemMeta met = WhistleListener.blankWhistle.getItemMeta();
			met.setDisplayName("Blank Whistle");
			met.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			WhistleListener.blankWhistle.setItemMeta(met);
			
			if(config.getBoolean("whistles.craftingRecipe")) {
				
				ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(this, "whistle"), WhistleListener.blankWhistle);
				recipe.addIngredient(1, Material.IRON_INGOT);
				recipe.addIngredient(1, Material.GOLDEN_CARROT);
				
				this.getServer().addRecipe(recipe);
				
				WhistleListener.craftWhistle = true;
				
			}
			if ( config.getBoolean("whistles.teleport") ) {
				
				WhistleListener.whistleTP = true;
				
			}
		}
	}
	
}