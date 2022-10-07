package com.github.boltydawg.horseoverhaul;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;

import com.github.boltydawg.horseoverhaul.Listeners.OwnershipListener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class NamePrompt extends StringPrompt {
	
	private Player player;
	private AbstractHorse abHorse;

	public NamePrompt(Player player, AbstractHorse horse) {
		this.player = player;
		this.abHorse = horse;
	}

	@Override
	public String getPromptText(ConversationContext context) {
		
		TextComponent tc = new TextComponent("What would you like to name your new steed?");
		tc.setColor(ChatColor.BLUE);
		
		if(HorseOverhaul.instance.config.coloredNames) {
			TextComponent tc1 = new TextComponent("\nClick ");
			tc1.setColor(ChatColor.BLUE);
			
			TextComponent tc2 = new TextComponent("here");
			tc2.setColor(ChatColor.GOLD);
			tc2.setUnderlined(true);
			tc2.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/attachments/example2-png.188806"));
			
			TextComponent tc3 = new TextComponent(" to see a list of available color codes");
			tc3.setColor(ChatColor.BLUE);
			
			tc.addExtra(tc1);
			tc.addExtra(tc2);
			tc.addExtra(tc3);
		}
		
		if(context.getForWhom() instanceof Player) {
			((Player)context.getForWhom()).spigot().sendMessage(tc);
		}
		
		return ChatColor.GRAY + "(type your answer in chat)";
	}

	@Override
	public Prompt acceptInput(ConversationContext context, String input) {
		
		String name = input;
		
		if(HorseOverhaul.instance.config.coloredNames) {
			
			while(name.contains("&")) {
				
				int ind = name.indexOf('&');
				
				if(ChatColor.getByChar( name.charAt(ind+1) ) != null ) {
					
					name = name.substring(0, ind) + ChatColor.getByChar( name.charAt(ind+1) ) + name.substring(ind+2);

				}
			}
		}
		
		if(ChatColor.stripColor(name).length() > 16) {
			
			context.getForWhom().sendRawMessage(ChatColor.RED + "Name too long! Must be at most 16 characters");
			return new NamePrompt(player,abHorse);
			
		}
		
		OwnershipListener.claimHorse(abHorse, player, name);
		
		return StringPrompt.END_OF_CONVERSATION;
		
	}
}