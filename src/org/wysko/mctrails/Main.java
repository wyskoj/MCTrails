package org.wysko.mctrails;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.block.data.type.Bed;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;

public class Main extends JavaPlugin implements Listener, Serializable {
	
	transient ArrayList<ParticleDisplayer> particleDisplayers = new ArrayList<>();
	
	
	/**
	 * Finds and returns the ParticleDisplayer object based on Player;
	 *
	 * @param player the player the ParticleDisplayer should be related to
	 * @return the ParticleDisplayer related to the player
	 */
	public ParticleDisplayer displayerByPlayer(@NotNull Player player) {
		for (ParticleDisplayer particleDisplayer : particleDisplayers) {
			if (particleDisplayer.settings.player.equals(player)) {
				return particleDisplayer;
			}
		}
		return null;
	}
	
	@Override
	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("[MCTrails] Has been enabled!");
	}
	
	@EventHandler
	public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
		// TODO Start player loop if it exists and is enabled.
	}
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		Player player = Bukkit.getPlayer(sender.getName());
		assert player != null;
		ParticleDisplayer displayer = displayerByPlayer(player);
		switch (command.getName().toLowerCase()) {
			case "toggletrail":
				if (displayer == null) {
					sender.sendMessage("You do not have an active trail. Type /defaulttrail to set your trail to the default.");
				} else {
					if (displayer.isLooping()) {
						displayer.stopLoop();
					} else {
						displayer.startLoop();
					}
				}
				break;
			case "defaulttrail":
				if (displayer == null) {
					particleDisplayers.add(new ParticleDisplayer(
							new PlayerParticleSettings(player, Particle.HEART)
					));
				} else {
					displayer.settings.particle = Particle.HEART;
				}
				break;
		}
		
		return true;
	}
	
	
}
