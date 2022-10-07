package com.github.boltydawg.horseoverhaul.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import com.github.boltydawg.horseoverhaul.HorseOverhaul;

public class CombatListener implements Listener {

    @EventHandler(priority=EventPriority.LOW)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        double bondam;
        Player player;
        Projectile p;
        
        double orig = event.getOriginalDamage(EntityDamageEvent.DamageModifier.BASE);
        if (HorseOverhaul.instance.config.horseRangedCombat && event.getDamager() instanceof Projectile && 
        		((Projectile)event.getDamager()).getShooter() instanceof Player && event.getEntity() instanceof LivingEntity && 
        		((player = (Player)(p = (Projectile)event.getDamager()).getShooter()).hasPermission("horseo.horsecombat.ranged") || 
        				player.hasPermission("horseo.horsecombat.*")) && player.getVehicle() instanceof AbstractHorse && (!HorseOverhaul.instance.config.onlyArrows || 
        						event.getDamager() instanceof Arrow)) {
            double bondam2;
            double fbondam = bondam2 = (double)Math.round((player.getVehicle().getVelocity().length() * 10.0 - 0.7) * 
            		HorseOverhaul.instance.config.speedRangedDamageMultiplier);
            double finaldamage = orig + HorseOverhaul.instance.config.stationaryRangedDamageMultiplier + fbondam;
            event.setDamage(finaldamage);
            if (HorseOverhaul.instance.config.debug) {
            	String projectile = p.getType().name();
            	projectile = projectile.toCharArray()[0] + projectile.substring(1).toLowerCase();
            	player.sendMessage((Object)ChatColor.YELLOW + "The Projectile you fired was " + projectile);
                player.sendMessage((Object)ChatColor.YELLOW + "Your Base Ranged Damage was " + orig);
                player.sendMessage((Object)ChatColor.YELLOW + "Your Stationary Horseback Ranged Damage Boost was " + HorseOverhaul.instance.config.stationaryRangedDamageMultiplier);
                player.sendMessage((Object)ChatColor.YELLOW + "Your Horse Speed-boosted Ranged Damage was " + bondam2);
                player.sendMessage((Object)ChatColor.RED + "Your Total Final Ranged Damage was " + finaldamage);
            }
        }
        if (!HorseOverhaul.instance.config.horseMeleeCombat) return;
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;
        Player player2 = (Player)event.getDamager();
        if (!player2.hasPermission("horseo.horsecombat.melee")) {
            if (!player2.hasPermission("horseo.horsecombat.*")) return;
        }
        if (!(player2.getVehicle() instanceof Horse)) return;
        double fbondam = bondam = (double)Math.round((player2.getVehicle().getVelocity().length() * 10.0 - 0.7) * HorseOverhaul.instance.config.speedMeleeDamageMultiplier);
        double finaldamage = orig + HorseOverhaul.instance.config.stationaryMeleeDamageMultiplier + fbondam;
        event.setDamage(finaldamage);
        if (HorseOverhaul.instance.config.debug) {
        	player2.sendMessage((Object)ChatColor.YELLOW + "Your Base Melee Damage was " + orig);
        	player2.sendMessage((Object)ChatColor.YELLOW + "Your Stationary Horseback Melee Damage Boost was " + HorseOverhaul.instance.config.stationaryMeleeDamageMultiplier);
        	player2.sendMessage((Object)ChatColor.YELLOW + "Your Horse Speed-boosted Melee Damage was " + bondam);
        	player2.sendMessage((Object)ChatColor.RED + "Your Total Final Melee Damage was " + finaldamage);
        }
    }

    @EventHandler(priority=EventPriority.LOW)
    public void onHorse(VehicleEnterEvent e) {
        Vehicle ride = e.getVehicle();
        if (HorseOverhaul.instance.config.notifyMessage == null || HorseOverhaul.instance.config.notifyMessage == "") return;
        if (!(ride instanceof Horse)) return;
        if (!(e.getEntered() instanceof Player)) return;
        Player p = (Player)e.getEntered();
        if (!((Tameable)ride).isTamed()) return;
        if (!p.hasPermission("horsecombat.allow.*") && !p.hasPermission("horsecombat.allow.melee")) {
            if (!p.hasPermission("horsecombat.allow.ranged")) return;
        }
        e.getEntered().sendMessage(ChatColor.translateAlternateColorCodes((char)'&', HorseOverhaul.instance.config.notifyMessage));
    }
}

