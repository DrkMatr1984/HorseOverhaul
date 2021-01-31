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
		
		TextComponent tc = new TextComponent(CustomLang.getConfig().getString("msg.nameSteedPrompt"));
		tc.setColor(ChatColor.BLUE);
		
		if(OwnershipListener.coloredNames) {
			
			TextComponent tc2 = new TextComponent("Color Codes");
			tc2.setColor(ChatColor.GOLD);
			tc2.setUnderlined(true);
			tc2.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/attachments/example2-png.188806"));
			
			tc.addExtra(tc2);
		}
		
		if(context.getForWhom() instanceof Player) {
			((Player)context.getForWhom()).spigot().sendMessage(tc);
		}
		
		return ChatColor.GRAY + CustomLang.getConfig().getString("msg.nameSteedPrompt2");
	}

	@Override
	public Prompt acceptInput(ConversationContext context, String input) {
		
		String name = input;
		
		if(OwnershipListener.coloredNames) {
			
			while(name.contains("&")) {
				
				int ind = name.indexOf('&');
				
				if(ChatColor.getByChar( name.charAt(ind+1) ) != null ) {
					
					name = name.substring(0, ind) + ChatColor.getByChar( name.charAt(ind+1) ) + name.substring(ind+2);

				}
			}
		}
		
		if(ChatColor.stripColor(name).length() > 16) {
			
			context.getForWhom().sendRawMessage(ChatColor.RED + CustomLang.getConfig().getString("msg.nameTooLongWarning"));
			return new NamePrompt(player,abHorse);
			
		}
		
		OwnershipListener.claimHorse(abHorse, player, name);
		
		return StringPrompt.END_OF_CONVERSATION;
		
	}
}