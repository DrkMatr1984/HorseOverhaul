package com.github.boltydawg.horseoverhaul;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.boltydawg.horseoverhaul.Listeners.BreedingListener;
import com.github.boltydawg.horseoverhaul.Listeners.OwnershipListener;
import com.github.boltydawg.horseoverhaul.Listeners.StatsListener;
import com.github.boltydawg.horseoverhaul.Listeners.WhistleListener;

import net.md_5.bungee.api.ChatColor;

/**
 * Handles the execution of the /horse command
 * @author Jason
 *
 */
public class CommandHorseo implements CommandExecutor {
	/**
	 * This method fires everytime someone uses the /horseo command.
	 * Expects only 1 argument: either what they'd like more information about, or an admin using "reload"
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		//checks if the player didn't enter any arguments following /horseo
		if(args == null || args.length != 1){
			return false;
		}
		
		//checks if the player entered a valid string after /horseo, and sends them the proper message
		else {
			String param = args[0];
			
			//check if sender is a player
			if(!(sender instanceof Player)) {
				//no need to check permissions
				if (param.equalsIgnoreCase("reload")) {
					sender.sendMessage("Reloading HorseOverhaul...");
					
					//unitialize any existing listeners
					Main.instance.removeListeners();
					
					//reload the config and necessary listeners
					CustomConfig.reload();
					
					sender.sendMessage("Done.");
					return true;
				}
				return false;
			}
			
			Player player = ((Player) sender);
			
			
			if(param.equalsIgnoreCase("breed")) {
				player.sendMessage(helpBreed());
			}
			else if(param.equalsIgnoreCase("own")) {
				player.sendMessage(helpOwn());
			}
			else if(param.equalsIgnoreCase("stats")) {
				player.sendMessage(helpStats());
			}
			else if(param.equalsIgnoreCase("whistle")) {
				player.sendMessage(helpWhistle());
			}
			else if(param.equalsIgnoreCase("pizza")) {
				player.sendMessage("Your gamemode has been updated to Creative Mode");
			}
			else if (param.equalsIgnoreCase("reload")) {
				if (player.hasPermission("horseo.reload")) {
					player.sendMessage("Reloading HorseOverhaul...");
					
					//unitialize any existing listeners
					Main.instance.removeListeners();
					
					//reload the config and necessary listeners
					CustomConfig.reload();
					
					player.sendMessage("Done.");
					
					return true;
				}
				else {
					player.sendMessage(ChatColor.DARK_AQUA + "[Horse Overhaul]" + ChatColor.RESET + ChatColor.RED + "You do not have permission to use this command");
				}
			}
			else {
				return false;
			}
				
			return true;
		}
			
	}
	
	/**
	 * Strings that are printed out for the help command
	 */
	
	private String helpBreed() {
		String msg = ChatColor.LIGHT_PURPLE + ChatColor.UNDERLINE.toString() + "HORSE OVERHAUL HELP: Breeding" + ChatColor.RESET + "\n\n";
		
		if( BreedingListener.betterBreeding ) {
			
			msg += ChatColor.GREEN + "The breeding algorithm has been reworked in a way such that there's more of a focus on generational improvements rather than random luck.\n" + ChatColor.RESET;
			
			if( BreedingListener.foodEffects ) {
				msg += ChatColor.YELLOW + "You can get better results from your breeding by using better foods: golden apples will prevent the foal from having stats less than the lesser "
						+ "of the two parents; using enchanted golden apples will give you a foal with max stats, however, it will be sterile and unable to breed in the future.";
			}
		}
		else {
			
			msg += ChatColor.RED + "This feature is currently disabled by your server administrator.";
			
		}
		return msg;
	}
	
	private String helpOwn() {
		String msg = ChatColor.LIGHT_PURPLE + ChatColor.UNDERLINE.toString() + "HORSE OVERHAUL HELP: Ownership" + ChatColor.RESET + '\n';
		
		if( OwnershipListener.ownership ) {
			
			if(OwnershipListener.craftDeed) {
				msg += ChatColor.GREEN + "Craft a blank deed by combining a golden carrot with a book and quill.\n" + ChatColor.RESET;
			}
			
			msg += ChatColor.YELLOW + "Claim a horse as yours by right clicking it with a blank deed, or if its already claimed, the original copy of its deed!\n" + ChatColor.RESET;
			msg += ChatColor.GREEN + "Other players cannot interact with horses that you own, and equipping your owned horses with armor prevents you from accidentally damaging them :)\n" + ChatColor.RESET;
			msg += ChatColor.YELLOW + "If you're in the business of selling horses, you can right click a baby foal that you own with shears to neuter it and prevent it from ever breeding.\n" + ChatColor.RESET;
			msg += ChatColor.GREEN + "If trying to use a nametag on one of your horses, you must be holding its deed in your off hand.\n" + ChatColor.RESET;
			if(OwnershipListener.coloredNames) {
				msg += ChatColor.YELLOW + "You can use Color Codes when naming your horses! See "+ ChatColor.UNDERLINE + "https://www.spigotmc.org/attachments/example2-png.188806/";
			}
		}
		else {
			
			msg += ChatColor.RED + "This feature is currently disabled by your server administrator.";
			
		}
		return msg;
	}
	
	private String helpStats() {
		
		String msg = ChatColor.LIGHT_PURPLE + ChatColor.UNDERLINE.toString() + "HORSE OVERHAUL HELP: Checking Stats" + ChatColor.RESET + '\n';
		
		if( StatsListener.checkStats ) {
	
			msg += ChatColor.GREEN + "Right click a horse while holding a carrot on a stick to see its stats!\n" + ChatColor.RESET;
			msg += ChatColor.YELLOW + "After viewing a horse's stats, you can display them on a sign by right clicking the sign with your carrot on a stick.\n";
			if( OwnershipListener.ownership ) {
				msg += ChatColor.GREEN + "You can only check the stats of tamed horses that are not owned by other players";
			}
			else {
				msg += ChatColor.GREEN + "You can only check the stats of tamed horses";
			}
		}
		else {
			
			msg += ChatColor.RED + "This feature is currently disabled by your server administrator.";
			
		}
		return msg;
	}
	
	private String helpWhistle() {
		String msg = ChatColor.LIGHT_PURPLE + ChatColor.UNDERLINE.toString() + "HORSE OVERHAUL HELP: Whistles" + ChatColor.RESET + '\n';
		
		if( WhistleListener.whistle ) {
			
			if( WhistleListener.craftWhistle ) {
				
				msg += ChatColor.GREEN + "You can craft a blank whistle by combining a golden carrot and iron ingot\n" + ChatColor.RESET;
				
			}
			msg += ChatColor.YELLOW + "Once you've obtained a blank whistle, all you need to do is right click the horse you want to link it to, and you'll have yourself a functioning whistle!\n" + ChatColor.RESET;
			if ( WhistleListener.whistleTP ) {
				msg += ChatColor.GREEN + "Right click while holding a whistle to search in a 100x30x100 radius for your horse. If found, it will be teleported to your location!\n" + ChatColor.RESET + ChatColor.YELLOW;
			}
			else {
				msg += ChatColor.GREEN + "Right click while holding a whistle to search in a 100x30x100 radius for your horse. If found, you'll be able to see a highlight of it (even through walls) for 10 seconds\n" + ChatColor.RESET;
				msg += ChatColor.YELLOW + "Be mindful, however, because other players will be able to see your horse's outline too!\n" + ChatColor.RESET + ChatColor.GREEN;
			}
			
			if ( OwnershipListener.ownership ) {
				
				msg += "Note: you can only link your whistle to a horse that is not owned by another player. It's recommended that if you intend on claiming a horse, you should do so before carving the whistle.\n" + ChatColor.RESET;
				
			}
		}
		else {
			
			msg += ChatColor.RED + "This feature is currently disabled by your server administrator.";
			
		}
		
		return msg;
	}
}