package com.github.boltydawg.horseoverhaul;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomConfig {
	
	private static File file;
	private static FileConfiguration customFile;
	
	private static final String NAME = "settings.yml";
	
	
	/**
	 * Set up our {@link FileConfiguration} and locally stored {@link File}
	 */
	public static void setup() {
		file = fetchConfigFile(Main.instance);
		customFile = YamlConfiguration.loadConfiguration(file);
		
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
	public static FileConfiguration getConfig() {
		return customFile;
	}
	
	/**
	 * save the current configuration to settings.yml
	 */
	public static void save() {
		try {
			customFile.save(file);
		} 
		catch(IOException e) {
			Main.instance.getLogger().warning("Error saving config, please report this to BoltyDawg:\n" + e.toString());
		}
	}
	
	/**
	 * reloads the configuration
	 */
	public static void reload() {
		customFile = YamlConfiguration.loadConfiguration(file);
		Main.instance.readConfig(customFile);
	}
	
	/**
	 * returns a file representing our custom configuration.
	 * imports from our plugin's resource folder if none exists
	 * @param plugin
	 * @return File
	 */
	private static File fetchConfigFile(JavaPlugin plugin) {
		File file = new File(plugin.getDataFolder(), NAME);
		
		if(!file.exists()) {
			try {
				file.createNewFile();
				plugin.getLogger().info("creating " + NAME);
			} 
			catch (IOException e) {
				Main.instance.getLogger().warning("Error creating config, please report this to BoltyDawg:\n" + e.toString());
			}	
		}
		
		return file;
	}
}
