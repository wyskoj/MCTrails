/*
 * MIT License
 *
 * Copyright (c) 2020 Jacob Wysko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.wysko.mctrails;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.StringJoiner;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ParticleDisplayer {
	
	/**
	 * The {@link PlayerParticleSettings} related to this ParticleDisplayer
	 */
	PlayerParticleSettings settings;
	boolean enabledOnQuit = false;
	private ScheduledExecutorService executorService;
	private boolean looping = false;
	private Future<?> future;
	final Runnable loopParticles = () -> {
		Random random = new Random();
		Player player = Bukkit.getPlayer(settings.playerUUID);
		assert player != null;
		if (!Bukkit.getOnlinePlayers().contains(player)) {
			stopLoop();
			return;
		}
		Location location = player.getLocation();
		player.spawnParticle(settings.particle,
				location.getX() + (random.nextDouble() - 0.5),
				location.getY() + (random.nextDouble() - 0.5),
				location.getZ() + (random.nextDouble() - 0.5), 1);
	};
	
	public ParticleDisplayer(PlayerParticleSettings settings) {
		this.settings = settings;
		executorService = Executors.newScheduledThreadPool(4);
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
	public @NotNull String toString() {
		return new StringJoiner(", ", ParticleDisplayer.class.getSimpleName() + "[", "]")
				.add("settings=" + settings)
				.add("looping=" + looping)
				.add("loopParticles=" + loopParticles)
				.toString();
	}
	
	
	public boolean wasEnabledOnQuit() {
		return enabledOnQuit;
	}
	
}
