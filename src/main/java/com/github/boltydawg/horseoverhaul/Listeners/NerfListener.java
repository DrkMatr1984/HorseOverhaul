package com.github.boltydawg.horseoverhaul.Listeners;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.world.ChunkLoadEvent;

import com.github.boltydawg.horseoverhaul.HorseOverhaul;

public class NerfListener implements Listener{
	
	@EventHandler
	public void onSpawn(CreatureSpawnEvent event){
		
		if(event.getEntity() instanceof AbstractHorse) {
			
			if(event.getSpawnReason().equals(SpawnReason.NATURAL)) {
					
				nerf((AbstractHorse)event.getEntity());
			}
			
			else if(HorseOverhaul.instance.config.override) {
				
				event.getEntity().addScoreboardTag("ho.isNerfed");
				
			}
		}
	}
	
	
	@EventHandler
	public void onNewChunk(ChunkLoadEvent event) {
		
		if(event.isNewChunk()) {
			
			for(Entity e : event.getChunk().getEntities()) {
				
				if(e instanceof AbstractHorse) {
					
					nerf((AbstractHorse)e);
				}
			}
		}
		else if(HorseOverhaul.instance.config.override) {
			
			for(Entity e : event.getChunk().getEntities()) {
				
				if(e instanceof AbstractHorse) {
					
					if(!e.getScoreboardTags().contains("ho.isNerfed"))
						nerf((AbstractHorse)e);
					
				}
			}
		}
	}
	
	public static void nerf(AbstractHorse horse) {
		
		if(HorseOverhaul.instance.config.override) {
			
			horse.addScoreboardTag("ho.isNerfed");
			
		}
		
		horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue( horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() / HorseOverhaul.instance.config.nerfDivisor );
		horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue( horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() / HorseOverhaul.instance.config.nerfDivisor );
		
		horse.setJumpStrength( horse.getJumpStrength() / HorseOverhaul.instance.config.nerfDivisor );
		
	}
}
