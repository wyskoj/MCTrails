package org.wysko.mctrails;

import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class PlayerParticleSettings {
	/**
	 * The Player that these settings apply to.
	 */
	Player player;
	
	/**
	 * The Particle that should generate around the player.
	 */
	Particle particle;
	
	public PlayerParticleSettings(Player player, Particle particle) {
		this.player = player;
		this.particle = particle;
	}
}
