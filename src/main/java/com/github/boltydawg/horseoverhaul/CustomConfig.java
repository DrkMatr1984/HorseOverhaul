package com.github.boltydawg.horseoverhaul;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class CustomConfig {
	
	private FileConfiguration customFile;
	private File file;
	
	private HorseOverhaul plugin;
	public boolean gearEnabled;
	public boolean gearSaddles;
	public boolean gearArmor;
	public boolean betterBreedingEnabled;
	public boolean foodEffects;
	public boolean checkStatsEnabled;
	public boolean requireTamed;
	public boolean ownershipEnabled;
	public boolean deedCraftingRecipe;
	public boolean coloredNames;
	public boolean nerfWildSpawns;
	public double nerfDivisor;
	public boolean override;
	public boolean whistlesEnabled;
	public boolean whistleCraftingRecipe;
	public boolean whistleTeleport;
	public boolean horseCombat;
	public boolean horseMeleeCombat;
	public double stationaryMeleeDamageMultiplier;
	public double speedMeleeDamageMultiplier;
	public boolean horseRangedCombat;
	public boolean onlyArrows;
	public double stationaryRangedDamageMultiplier;
	public double speedRangedDamageMultiplier;
	public String notifyMessage;
	public boolean debug;
	
	
	public CustomConfig(HorseOverhaul plugin) {
		this.plugin = plugin;
		plugin.saveDefaultConfig();
		this.file = fetchConfigFile();
		this.customFile = YamlConfiguration.loadConfiguration(file);
	}
	
	public void loadConfig() {
		//auto gears equip
		gearEnabled = customFile.getBoolean("autoGear.enabled");
		gearSaddles = customFile.getBoolean("autoGear.saddles");
		gearArmor = customFile.getBoolean("autoGear.horseArmor");
		
		//better breeding
		betterBreedingEnabled = customFile.getBoolean("betterBreeding.enabled");
		foodEffects = customFile.getBoolean("betterBreeding.foodEffects");
		
		//check stats
		checkStatsEnabled = customFile.getBoolean("checkStats.enabled");
		requireTamed = customFile.getBoolean("checkStats.requireTamed");
		
		//ownership
		ownershipEnabled = customFile.getBoolean("ownership.enabled");
		deedCraftingRecipe = customFile.getBoolean("ownership.craftingRecipe");
		coloredNames = customFile.getBoolean("ownership.coloredNames");
		
		//nerf wild spawns
		nerfWildSpawns = customFile.getBoolean("nerfWildSpawns.enabled");
		nerfDivisor = customFile.getDouble("nerfWildSpawns.divisor");
		override = customFile.getBoolean("nerfWildSpawns.override");
		
		//whistles
		whistlesEnabled = customFile.getBoolean("whistles.enabled");
		whistleCraftingRecipe = customFile.getBoolean("whistles.craftingRecipe");
		whistleTeleport = customFile.getBoolean("whistles.teleport");
		
		//horse combat
		horseCombat = customFile.getBoolean("horseCombat.horseCombat");
		horseMeleeCombat = customFile.getBoolean("horseCombat.horseMeleeCombat.enabled");
		stationaryMeleeDamageMultiplier = customFile.getDouble("horseCombat.horseMeleeCombat.stationaryMeleeDamageMultiplier");
		speedMeleeDamageMultiplier = customFile.getDouble("horseCombat.horseMeleeCombat.speedMeleeDamageMultiplier");
		horseRangedCombat = customFile.getBoolean("horseCombat.rangedHorseCombat.enabled");
		onlyArrows = customFile.getBoolean("horseCombat.rangedHorseCombat.onlyArrows");
		stationaryRangedDamageMultiplier = customFile.getDouble("horseCombat.rangedHorseCombat.stationaryRangedDamageMultiplier");
		speedRangedDamageMultiplier = customFile.getDouble("horseCombat.rangedHorseCombat.speedRangedDamageMultiplier");
		notifyMessage = customFile.getString("horseCombat.other.notifyMessage");
		debug = customFile.getBoolean("horseCombat.other.debug");
	}
  		
	
	/**
	 * getter for our custom {@link FileConfiguration}
	 * @return FileConfiguration
	 */
	public FileConfiguration getConfig() {
		return customFile;
	}
	
	/**
	 * save the current configuration to settings.yml
	 */
	public void save() {
		try {
			customFile.save(fetchConfigFile());
		} 
		catch(IOException e) {
			this.plugin.getLogger().warning("Error saving config, please report this to DrkMatr1984:\n" + e.toString());
		}
	}
	
	/**
	 * reloads the configuration
	 */
	public void reload() {
		if(!this.file.exists()) {
			this.plugin.saveDefaultConfig();
			file = fetchConfigFile();
			customFile = YamlConfiguration.loadConfiguration(file);
		}
		else
			customFile = YamlConfiguration.loadConfiguration(file);
	}
	
	/**
	 * returns a file representing our custom configuration.
	 * imports from our plugin's resource folder if none exists
	 * @param plugin
	 * @return File
	 */
	private  File fetchConfigFile() {
		File file = new File(this.plugin.getDataFolder(), "config.yml");
		
		if(!file.exists()) {
			try {
				file.createNewFile();
				this.plugin.getLogger().info("creating " + "config.yml");
			} 
			catch (IOException e) {
				this.plugin.getLogger().warning("Error creating config, please report this to DrkMatr1984:\n" + e.toString());
			}	
		}
		
		return file;
	}
	
}
