package com.github.boltydawg.horseoverhaul;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomLang {
	
	private static File file;
	private static FileConfiguration customFile;
	
	private static final String NAME = "lang.yml";
	
	
	/**
	 * Set up our {@link FileConfiguration} and locally stored {@link File}
	 */
	public static void setup() {
		file = fetchConfigFile(Main.instance);
		customFile = YamlConfiguration.loadConfiguration(file);
		
		customFile.addDefault("msg.nameSteedPrompt", "What would you like to name your new steed?");
		customFile.addDefault("msg.nameTooLongWarning", "Name too long! Must be at most 16 characters");
		customFile.addDefault("msg.breedFailAlert", " is neutered! The breed attempt fails");
		customFile.addDefault("msg.breedFailAlert2", "Both parents are neutered! The breed attempt fails");
		customFile.addDefault("msg.renameWarning", "You must be holding this horse's deed in your off hand in order to rename it!");
		customFile.addDefault("msg.renameBlocked", "You can only rename a horse that you own!");
		
		customFile.addDefault("item.deed", "Deed to");
		customFile.addDefault("item.deedDesc", "Property of");
		
		customFile.options().copyDefaults(true);
		customFile.options().header("HorseOverhaul Language Configuration");
		
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
