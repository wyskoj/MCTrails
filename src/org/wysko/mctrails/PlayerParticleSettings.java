package org.wysko.mctrails;

import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.StringJoiner;
import java.util.UUID;

public class PlayerParticleSettings {
	/**
	 * The UUID of the Player that these settings apply to.
	 */
	UUID playerUUID;
	
	/**
	 * The Particle that should generate around the player.
	 */
	Particle particle;
	
	public PlayerParticleSettings(UUID playerUUID, Particle particle) {
		this.playerUUID = playerUUID;
		this.particle = particle;
	}
	
	@Override
	public String toString() {
		return new StringJoiner(", ", PlayerParticleSettings.class.getSimpleName() + "[", "]")
				.add("playerUUID=" + playerUUID)
				.add("particle=" + particle)
				.toString();
	}
}
