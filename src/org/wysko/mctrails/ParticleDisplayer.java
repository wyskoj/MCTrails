package org.wysko.mctrails;

import org.bukkit.Location;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ParticleDisplayer {
	
	private ScheduledExecutorService executorService;
	PlayerParticleSettings settings;
	private boolean looping = false;
	
	final Runnable loopParticles = () -> {
		Random random = new Random();
		
		Location location = settings.player.getLocation();
		settings.player.spawnParticle(settings.particle,
				location.getX() + (random.nextDouble() - 0.5),
				location.getY() + (random.nextDouble() - 0.5),
				location.getZ() + (random.nextDouble() - 0.5), 1);
	};
	
	public ParticleDisplayer(PlayerParticleSettings settings) {
		this.settings = settings;
		executorService = Executors.newScheduledThreadPool(1);
	}
	
	void startLoop() {
		executorService = Executors.newScheduledThreadPool(1);
		executorService.scheduleAtFixedRate(loopParticles, 0, 200, TimeUnit.MILLISECONDS);
		looping = true;
	}
	
	void stopLoop() {
		executorService.shutdown();
		looping = false;
	}
	
	boolean isLooping() {
		return looping;
	}
}
