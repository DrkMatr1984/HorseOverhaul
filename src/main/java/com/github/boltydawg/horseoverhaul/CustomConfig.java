package com.github.boltydawg.horseoverhaul;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class CustomConfig {
	
	private FileConfiguration customFile;
	private File file;
	
	private static final String NAME = "settings.yml";
	
	private Main plugin;
	
	public CustomConfig(Main plugin) {
		this.plugin = plugin;
		this.file = fetchConfigFile();
		this.customFile = YamlConfiguration.loadConfiguration(file);
		
		addDefaults();
	}
	
	private void addDefaults() {
		customFile.addDefault("autoGear.enabled", true);
		customFile.addDefault("autoGear.saddles", true);
		customFile.addDefault("autoGear.horseArmor", true);
		customFile.addDefault("betterBreeding.enabled", true);
		customFile.addDefault("betterBreeding.foodEffects",true);
		customFile.addDefault("checkStats.enabled", true);
		customFile.addDefault("checkStats.requireTamed", true);
		customFile.addDefault("ownership.enabled", true);
		customFile.addDefault("ownership.craftingRecipe", true);
		customFile.addDefault("ownership.coloredNames", false);
		customFile.addDefault("nerfWildSpawns.enabled", false);
		customFile.addDefault("nerfWildSpawns.divisor", 1.5);
		customFile.addDefault("nerfWildSpawns.override", false);
		customFile.addDefault("whistles.enabled", true);
		customFile.addDefault("whistles.craftingRecipe", true);
		customFile.addDefault("whistles.teleport", false);
		
		customFile.options().copyDefaults(true);
		customFile.options().header("HorseOverhaul Configuration\n\nAnytime you change an option here be sure to run the command \"/horseo reload\"\n\nSee https://www.spigotmc.org/resources/horse-overhaul.75448/ for more information about each option\n\n");
		
		save();
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
			this.plugin.getLogger().warning("Error saving config, please report this to BoltyDawg:\n" + e.toString());
		}
	}
	
	/**
	 * reloads the configuration
	 */
	public void reload() {
		if(!this.file.exists()) {
			file = fetchConfigFile();
			customFile = YamlConfiguration.loadConfiguration(file);
			addDefaults();
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
		File file = new File(this.plugin.getDataFolder(), NAME);
		
		if(!file.exists()) {
			try {
				file.createNewFile();
				this.plugin.getLogger().info("creating " + NAME);
			} 
			catch (IOException e) {
				this.plugin.getLogger().warning("Error creating config, please report this to BoltyDawg:\n" + e.toString());
			}	
		}
		
		return file;
	}
}
