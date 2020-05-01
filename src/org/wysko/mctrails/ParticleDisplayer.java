package org.wysko.mctrails;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Random;
import java.util.StringJoiner;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ParticleDisplayer {
	
	PlayerParticleSettings settings;
	final Runnable loopParticles = () -> {
		Random random = new Random();
		Player player = Bukkit.getPlayer(settings.playerUUID);
		assert player != null;
		Location location = player.getLocation();
		player.spawnParticle(settings.particle,
				location.getX() + (random.nextDouble() - 0.5),
				location.getY() + (random.nextDouble() - 0.5),
				location.getZ() + (random.nextDouble() - 0.5), 1);
		
		System.out.println("spawning a particle!");
	};
	boolean enabledOnQuit = false;
	private ScheduledExecutorService executorService;
	private boolean looping = false;
	private Future<?> future;
	
	public ParticleDisplayer(PlayerParticleSettings settings) {
		this.settings = settings;
		executorService = Executors.newScheduledThreadPool(1024);
	}
	
	void startLoop() {
		future = executorService.scheduleAtFixedRate(loopParticles, 0, 200, TimeUnit.MILLISECONDS);
		looping = true;
	}
	
	void stopLoop() {
		future.cancel(true);
		looping = false;
	}
	
	boolean isLooping() {
		return looping;
	}
	
	void reset() {
		executorService = Executors.newScheduledThreadPool(1024);
	}
	
	@Override
	public String toString() {
		return new StringJoiner(", ", ParticleDisplayer.class.getSimpleName() + "[", "]")
				.add("settings=" + settings)
				.add("looping=" + looping)
				.add("loopParticles=" + loopParticles)
				.toString();
	}
	
}
