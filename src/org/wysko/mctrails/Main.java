package org.wysko.mctrails;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class Main extends JavaPlugin implements Listener, Serializable {
	
	transient ArrayList<ParticleDisplayer> particleDisplayers = new ArrayList<>();
	
	
	/**
	 * Finds and returns the ParticleDisplayer object based on Player;
	 *
	 * @param UUID the UUID of the player the ParticleDisplayer should be related to
	 * @return the ParticleDisplayer related to the player, or null if it does not exist
	 */
	@Nullable
	public ParticleDisplayer displayerByUUID(@NotNull UUID UUID) {
		for (ParticleDisplayer particleDisplayer : particleDisplayers) {
			if (particleDisplayer.settings.playerUUID.equals(UUID)) {
				return particleDisplayer;
			}
		}
		return null;
	}
	
	@Override
	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("[MCTrails] Has been enabled!");
		for (ParticleDisplayer displayer : particleDisplayers) {
			displayer.stopLoop();
		}
	}
	
	@EventHandler
	public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
		ParticleDisplayer displayer = displayerByUUID(event.getPlayer().getUniqueId());
		if (Objects.nonNull(displayer)) {
			displayer.reset();
			if (displayer.enabledOnQuit) {
				displayer.startLoop();
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
		ParticleDisplayer displayer = displayerByUUID(event.getPlayer().getUniqueId());
		if (Objects.nonNull(displayer)) {
			displayer.enabledOnQuit = displayer.isLooping();
		}
	}
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		Player player = Bukkit.getPlayer(sender.getName());
		assert player != null;
		ParticleDisplayer displayer = displayerByUUID(player.getUniqueId());
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
				if (Objects.nonNull(displayer)) {
					System.out.println(String.format(
							"Toggled %s's trail of %s %s.",
							player.getDisplayName(),
							displayer.settings.particle,
							displayer.isLooping() ? "on" : "off"
					));
				}
				break;
			case "defaulttrail":
				if (displayer == null) {
					particleDisplayers.add(new ParticleDisplayer(
							new PlayerParticleSettings(player.getUniqueId(), Particle.HEART)
					));
				} else {
					displayer.settings.particle = Particle.HEART;
				}
				break;
		}
		
		return true;
	}
	
	
}
