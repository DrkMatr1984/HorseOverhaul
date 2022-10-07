package com.github.boltydawg.horseoverhaul;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
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
import com.github.boltydawg.horseoverhaul.Listeners.CombatListener;
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


public class HorseOverhaul extends JavaPlugin{
	
	public static DecimalFormat df = new DecimalFormat("0.00");
	
	public static HorseOverhaul instance;
	
	public CustomConfig config;
	
	private ShapelessRecipe whistleRecipe;
	private ShapelessRecipe deedRecipe;
	
	private GearListener gear;
	private BreedingListener breeding;
	private StatsListener stats;
	private OwnershipListener ownership;
	private NerfListener nerf;
	private WhistleListener whistle;
	private CombatListener combat;
	
	@Override
	public void onEnable() {
		
		instance = this;
		
		// setup config
		this.config = new CustomConfig(this);
		this.config.loadConfig();
		
		// load listeners
		this.loadListeners();
		
		// commands
		this.getCommand("horseo").setExecutor(new CommandHorseo());
	}
	
	@Override
	public void onDisable() {
		this.getLogger().info("Saving config");
		config.save();
		removeListeners();
		removeRecipes();
	}
	
	public CustomConfig getCustomConfig() {
		return this.config;
	}
	
	public void removeRecipes() {
		if(whistleRecipe!=null)
		    if(this.getServer().getRecipe(whistleRecipe.getKey())!=null)
			    this.getServer().removeRecipe(whistleRecipe.getKey());
		if(deedRecipe!=null)
		    if(this.getServer().getRecipe(deedRecipe.getKey())!=null)
			    this.getServer().removeRecipe(deedRecipe.getKey());
	}
	
	/**
	 * load the required listeners based on config options
	 */
	public void loadListeners() {
		
		if(config.gearEnabled) {
			//initialize 
			this.gear = new GearListener();
			
			//register listener
			this.getServer().getPluginManager().registerEvents(gear, this);
		}
		
		if(config.betterBreedingEnabled) {
			//initialize 
			this.breeding = new BreedingListener();
			
			//register listener
			this.getServer().getPluginManager().registerEvents(breeding, this);
			
		}
		
		if(config.checkStatsEnabled) {
			//initialize 
			this.stats = new StatsListener();
			
			//register listener
			this.getServer().getPluginManager().registerEvents(stats, this);
			
		}
		
		if(config.ownershipEnabled) {
			//initialize
			this.ownership = new OwnershipListener();
			
			//register the listener
			this.getServer().getPluginManager().registerEvents(ownership, this);
			
			OwnershipListener.blankDeed = new ItemStack(Material.PAPER);
			ItemMeta met = OwnershipListener.blankDeed.getItemMeta();
			met.setDisplayName("Blank Deed");
			ArrayList<String> lore = new ArrayList<String>();
			lore.add(ChatColor.GRAY + "Right click an unclaimed");
			lore.add(ChatColor.GRAY + "horse to make it yours");
			met.setLore(lore);
			OwnershipListener.blankDeed.setItemMeta(met);
			
			if(config.deedCraftingRecipe) {
				
				deedRecipe = new ShapelessRecipe(new NamespacedKey(this, "blankDeed"),OwnershipListener.blankDeed);
				deedRecipe.addIngredient(1, Material.WRITABLE_BOOK);
				deedRecipe.addIngredient(1, Material.GOLDEN_CARROT);
				if(this.getServer().getRecipe(deedRecipe.getKey()) == null)
					this.getServer().addRecipe(deedRecipe);
				
			}
				
		}
		
		if(config.nerfWildSpawns) {
			//initialize
			this.nerf = new NerfListener();
			
			//register the listener
			this.getServer().getPluginManager().registerEvents(nerf, this);
			
			if(config.override) {
				
				for (World w: instance.getServer().getWorlds()){
					for(LivingEntity e: w.getLivingEntities()) {
						if(e.isValid() && e instanceof AbstractHorse && !e.getScoreboardTags().contains("ho.isNerfed")) {
							NerfListener.nerf((AbstractHorse)e);
						}
					}
				}
			}
			
		}
		
		if(config.whistlesEnabled) {
			//initialize
			this.whistle = new WhistleListener();
			
			//register the listener
			this.getServer().getPluginManager().registerEvents(whistle, this);
			
			WhistleListener.blankWhistle = new ItemStack(Material.IRON_NUGGET);
			ItemMeta met = WhistleListener.blankWhistle.getItemMeta();
			met.setDisplayName("Blank Whistle");
			met.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			WhistleListener.blankWhistle.setItemMeta(met);
			
			if(config.whistleCraftingRecipe) {				
				whistleRecipe = new ShapelessRecipe(new NamespacedKey(this, "whistle"), WhistleListener.blankWhistle);
				whistleRecipe.addIngredient(1, Material.IRON_INGOT);
				whistleRecipe.addIngredient(1, Material.GOLDEN_CARROT);
				if(this.getServer().getRecipe(whistleRecipe.getKey()) == null)
				    this.getServer().addRecipe(whistleRecipe);
			}
		}
		
		if(config.horseCombat) {
			//initialize 
			this.combat = new CombatListener();
			
			//register listener
			this.getServer().getPluginManager().registerEvents(combat, this);
			
		}
	}
	
	public void removeListeners() {
		// Unregister and unload the all listeners, for the case of usage of /horseo reload
		if (this.gear != null){
			HandlerList.unregisterAll(gear);
			
			this.gear = null;
			
			config.gearEnabled = false;
		}
		if (this.breeding != null) {
			
			HandlerList.unregisterAll(this.breeding);
			
			this.breeding = null;
			
			config.betterBreedingEnabled = false;
		}
		
		if (this.stats != null) {
			HandlerList.unregisterAll(stats);
			
			this.stats = null;
			
			config.checkStatsEnabled = false;
		}
		
		if (this.ownership != null) {
			HandlerList.unregisterAll(ownership);
			
			this.ownership = null;
			
			config.ownershipEnabled = true;
			config.deedCraftingRecipe = false;
			config.coloredNames = false;
		}
		
		if (this.nerf != null) {
			HandlerList.unregisterAll(nerf);
			
			this.nerf = null;
		}
		
		if (this.whistle != null){
			HandlerList.unregisterAll(whistle);
			
			this.whistle = null;
			
			config.whistlesEnabled = false;
		}
		
		if (this.combat != null){
			HandlerList.unregisterAll(combat);
			
			this.combat = null;
			
			config.horseCombat = false;
		}

		Iterator<Recipe> it = this.getServer().recipeIterator();
		int found = 0;
		while(it.hasNext() && found < 2) {
			ItemStack n = it.next().getResult();
			if (n.isSimilar(OwnershipListener.blankDeed) ||
					n.isSimilar(WhistleListener.blankWhistle) ) {
				it.remove();
				found++;
			}
		}
	}
}