package com.github.boltydawg.horseoverhaul;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Horse;


/**
 * @see https://minecraft.gamepedia.com/Attribute
 * @see https://minecraft.gamepedia.com/Horse
 */
public class StatHorse{
	
	/**
	 * fields
	 */
	
	public AbstractHorse roach;
	byte food;
	
	
	/**
	 * constructors
	 */
	
	public StatHorse(AbstractHorse horse) {
		roach = horse;
		food = 0;
	}
	
	public StatHorse(AbstractHorse horse, byte f) {
		roach = (AbstractHorse)horse;
		food = HorseOverhaul.instance.config.foodEffects ? f : 0;
	}
	
	
	/**
	 * get methods
	 */
	
	public double getJumpHeight() {
		double x = roach.getJumpStrength();
		
		return -0.1817584952 * Math.pow(x, 3) + 3.689713992 * Math.pow(x, 2) + 2.128599134 * x - 0.343930367;
	}
	
	public int getHealth() {
		return (int)(roach.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()/2);
	}
	
	public double getSpeed() {
		double x = roach.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
		return 43.178 * x - 0.02141;
	}
	
	
	public void calculateBirth(AbstractHorse mother, AbstractHorse father) {
		if(food == (byte)2) {
			roach.setJumpStrength(1.0);
			roach.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30);
			roach.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3375);
			return;
		}
		double fj, fh, fs, mj, mh, ms;
		
		fj = father.getJumpStrength();
		fh = father.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
		fs = father.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
		
		mj = mother.getJumpStrength();
		mh = mother.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
		ms = mother.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
		
		roach.setJumpStrength(calcJump(fj, mj));
		roach.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(calcHealth(fh, mh));
		roach.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(calcSpeed(fs, ms));
	}
	
	
	/**
	 * Breeding calculations
	 */
	
	/**
	 * @param f: mother's stat, on scale of 0-1.0
	 * @param m: father's stat, on scale of 0-1.0
	 * @return double on scale of 0-1.0 that will determine the child's attribute
	 */
	private double randomizer(double f, double m) {
		double min = f < m ? f : m;
		double max = f > m ? f : m;
		double nmax, nmin;
		
		double offset = Math.pow(max-min, 3) * 3.5;
		nmax = max + ( (1-max-offset) / 4 );
		nmin = min - ( (1-min) /5.5);
		
		double foal = Math.random() * (nmax-nmin) + nmin;
		
		if(food!=0 && foal<min) foal = min;
		
		return Math.min(foal, 1.0);
		
	}
	
	/**
	 * 	Horse Jump height can range from 0.4-1.0, average is 0.7
	 */
	private double calcJump(double f, double m) {
		
		double mc = (m - .4)  / 0.6;
		double fc = (f - .4) / 0.6;
		
		double child = randomizer(mc, fc);
		return child * 0.6 + 0.4;
	}
	
	/**
	 * Health can range from 15-30, average is 22-23
	 */
	private double calcHealth(double f, double m) {
		 
		double mc = (m - 15) / 15;
		double fc = (f - 15) / 15;
		
		double child = randomizer(mc, fc);
		return child * 15 + 15;
	}
	
	/**
	 * Speed ranges from 0.1125 - 0.3375, average is 0.225
	 */
	private double calcSpeed(double f, double m) {
		
		double mc = (m - 0.1125) / 0.225;
		double fc = (f - 0.1125) / 0.225;
		
		double child = randomizer(mc, fc);
		return child * 0.225 + 0.1125;
	}
	
	
	/**
	 * Methods for printing/display a horse's stats
	 */
	
	public String printStats(boolean border) {
		String msg = "";
		
		if(roach.getCustomName()!=null) {
			
			String stripped = ChatColor.stripColor(roach.getCustomName());
			
			String line = ChatColor.GRAY.toString();
			for(int i = 0; i < stripped.length() + 6; i++) {
				line += "-";
			}
			line += "\n" + ChatColor.RESET;
			
			msg+= line;
			
			if( roach.getCustomName().equals(stripped) )
				msg += ChatColor.DARK_AQUA;
			
			msg += (roach.getCustomName() + "'s Stats" + ChatColor.RESET + "\n");
			
			msg += line;
				
		}
		
		else if (roach instanceof Horse) {
			String color = ((Horse)this.roach).getColor().name();
			color = color.toCharArray()[0] + color.substring(1).toLowerCase();
			msg += (ChatColor.DARK_AQUA.toString() + ChatColor.UNDERLINE + color + " Horse's Stats") + ChatColor.RESET + "\n \n";
		}
		
		else {
			String type = roach.getType().name();
			type = type.toCharArray()[0] + type.substring(1).toLowerCase();
			msg += (ChatColor.DARK_AQUA.toString() + ChatColor.UNDERLINE + type + "'s Stats") + ChatColor.RESET + "\n \n";
		}
			
		
		msg += ChatColor.RED + "Health:\n" + printHearts(getHealth()) + " " + ChatColor.RED + HorseOverhaul.df.format(getHealth()) + "h\n";
		msg += ChatColor.GREEN + "Speed:\n" + printSpeed(getSpeed()) + " " + ChatColor.GREEN + HorseOverhaul.df.format(getSpeed()) + "m/s\n";
		msg += ChatColor.BLUE + "Jump Height:\n" + printJump(getJumpHeight()) + " " + ChatColor.BLUE + HorseOverhaul.df.format(getJumpHeight()) + "m\n";
		
		
		if(border) {
			String bord = ChatColor.LIGHT_PURPLE + "-----------------------------------------------------";
			return bord + "\n" + msg + bord;
		}
		else
			return msg + ChatColor.YELLOW + "Can Breed:\n" + (roach.getScoreboardTags().contains("ho.isNeutered") ? ChatColor.LIGHT_PURPLE + "False" : ChatColor.LIGHT_PURPLE + "True") + "\n";
	}
		
	private String printJump(double jh) {
		
		String msg = "";
		double b = 0;
		String blocks = "";
		
		while(jh - b >= 0.2625) {
			
			blocks += "⬛";
			b += 0.525;
			
		}
		
		msg += ChatColor.DARK_BLUE + blocks;
		
		blocks = "";
		
		while(b < 5.25) {
			
			blocks += "⬛";
			b += 0.525;
			
		}
		
		msg += ChatColor.GRAY + blocks;
		return msg;
		
	}
	private String printSpeed(double sp) {
		
		String msg = "";
		int b = 0;
		String rate = "";
		
		while(sp - b >= 0.5) {
			
			rate += "⬤";
			b++;
		}
		
		msg += ChatColor.DARK_GREEN + rate;
		
		rate = "";
		
		while(b < 14.5125) {
			
			rate += "⬤";
			b++;
			
		}
		
		msg += ChatColor.GRAY + rate;
		return msg;
		
	}
	
	private String printHearts(int hp) {
		
		String msg = "";
		int s = 0;
		String hearts = "";
		
		while(s<hp) {
			
			hearts += "❤";
			s++;
			
		}
		
		msg += ChatColor.DARK_RED + hearts;
		
		hearts = "";
		
		while(s < 15) {
			
			hearts += "❤";
			s++;
			
		}	
		
		msg += ChatColor.GRAY + hearts;
		return msg;
		
	}
	
}
