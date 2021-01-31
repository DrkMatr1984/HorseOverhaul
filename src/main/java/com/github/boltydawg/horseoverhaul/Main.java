package com.github.boltydawg.horseoverhaul;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.boltydawg.horseoverhaul.Listeners.BreedingListener;
import com.github.boltydawg.horseoverhaul.Listeners.GearListener;
import com.github.boltydawg.horseoverhaul.Listeners.NerfListener;
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
	
	public static Main instance;
	
	private BreedingListener breeding;
	private StatsListener stats;
	private OwnershipListener ownership;
	private NerfListener nerf;
	private WhistleListener whistle;
	
	@Override
	public void onEnable() {
		
		instance = this;
		
		//set constant listeners
		this.getServer().getPluginManager().registerEvents(new GearListener(), this);
		
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
		
		if(config.getBoolean("betterBreeding.enabled")) {
			//initialize 
			this.breeding = new BreedingListener();
			
			//register listener
			this.getServer().getPluginManager().registerEvents(breeding, this);
			
			//set other fields
			BreedingListener.betterBreeding = true;
				
			BreedingListener.foodEffects = config.getBoolean("betterBreeding.foodEffects");
		}
		
		if(config.getBoolean("checkStats.enabled")) {
			//initialize 
			this.stats = new StatsListener();
			
			//register listener
			this.getServer().getPluginManager().registerEvents(stats, this);
			
			//set other fields
			StatsListener.checkStats = true;
			
			StatsListener.untamed = config.getBoolean("checkStats.requireTamed");
		}
		
		if(config.getBoolean("ownership.enabled")) {
			//initialize
			this.ownership = new OwnershipListener();
			
			//register the listener
			this.getServer().getPluginManager().registerEvents(ownership, this);
			
			//set other fields
			OwnershipListener.ownership = true;
			
			OwnershipListener.blankDeed = new ItemStack(Material.PAPER);
			ItemMeta met = OwnershipListener.blankDeed.getItemMeta();
			met.setDisplayName("Blank Deed");
			ArrayList<String> lore = new ArrayList<String>();
			lore.add(ChatColor.GRAY + "Right click an unclaimed");
			lore.add(ChatColor.GRAY + "horse to make it yours");
			met.setLore(lore);
			OwnershipListener.blankDeed.setItemMeta(met);
			
			if(config.getBoolean("ownership.craftingRecipe")) {
				
				ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(this, "blankDeed"),OwnershipListener.blankDeed);
				recipe.addIngredient(1, Material.WRITABLE_BOOK);
				recipe.addIngredient(1, Material.GOLDEN_CARROT);
				
				this.getServer().addRecipe(recipe);
				
				OwnershipListener.craftDeed = true;
				
			}
			
			OwnershipListener.coloredNames = config.getBoolean("ownership.coloredNames");
				
		}
		
		if(config.getBoolean("nerfWildSpawns.enabled")) {
			//initialize
			this.nerf = new NerfListener();
			
			//register the listener
			this.getServer().getPluginManager().registerEvents(nerf, this);
			
			//set other fields
			NerfListener.divisor = config.getDouble("nerfWildSpawns.divisor", 1.5);
			
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
			//initialize
			this.whistle = new WhistleListener();
			
			//register the listener
			this.getServer().getPluginManager().registerEvents(whistle, this);
			
			//set other fields
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
			else
				WhistleListener.craftWhistle = false;
				
			WhistleListener.whistleTP = config.getBoolean("whistles.teleport");
		}
	}
	
	public void removeListeners() {
		// Unregister and unload the all listeners, for the case of usage of /horseo reload
		if (this.breeding != null) {
			
			HandlerList.unregisterAll(this.breeding);
			
			this.breeding = null;
			
			BreedingListener.betterBreeding = false;
		}
		
		if (this.stats != null) {
			HandlerList.unregisterAll(stats);
			
			this.stats = null;
			
			StatsListener.checkStats = false;
		}
		
		if (this.ownership != null) {
			HandlerList.unregisterAll(ownership);
			
			this.ownership = null;
			
			OwnershipListener.ownership = true;
			OwnershipListener.blankDeed = null;
			OwnershipListener.craftDeed = false;
			OwnershipListener.coloredNames = false;
		}
		
		if (this.nerf != null) {
			HandlerList.unregisterAll(nerf);
			
			this.nerf = null;
		}
		
		if (this.whistle != null){
			HandlerList.unregisterAll(whistle);
			
			this.whistle = null;
			
			WhistleListener.whistle = false;
		}

		Iterator<Recipe> it = this.getServer().recipeIterator();
		int found = 0;
		while(it.hasNext() && found < 2) {
			ItemStack n = it.next().getResult();
			if (n.equals(OwnershipListener.blankDeed) ||
					n.equals(WhistleListener.blankWhistle) ) {
				it.remove();
				found++;
			}
		}
	}
	
}