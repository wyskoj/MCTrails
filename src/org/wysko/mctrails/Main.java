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
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

public class Main extends JavaPlugin implements Listener, Serializable, InventoryHolder {
	
	final String GUI_DISABLE = ChatColor.RED + "Disable";
	final String GUI_ENABLE = ChatColor.GREEN + "Enable";
	final String GUI_TRAILS_CONFIGURATION = "Trails Configuration";
	final transient ArrayList<ParticleDisplayer> particleDisplayers = new ArrayList<>();
	
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
	}
	
	@EventHandler
	public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
		final UUID uuid = event.getPlayer().getUniqueId();
		ParticleDisplayer displayer = displayerByUUID(uuid);
		if (Objects.nonNull(displayer)) {
			displayer.reset();
			if (displayer.wasEnabledOnQuit()) {
				displayer.startLoop();
			}
		}
		if (displayerByUUID(uuid) == null) {
			particleDisplayers.add(defaultDisplayer(uuid));
		}
	}
	
	@EventHandler
	public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
		ParticleDisplayer displayer = displayerByUUID(event.getPlayer().getUniqueId());
		if (Objects.nonNull(displayer)) {
			displayer.enabledOnQuit = displayer.isLooping();
//			displayer.stopLoop();
		}
	}
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		Player player = Bukkit.getPlayer(sender.getName());
		assert player != null;
		switch (command.getName().toLowerCase()) {
			case "trails":
			case "trail":
			case "trailoreenos":
				player.openInventory(getUserTrailGUI(player));
				break;
		}
		return true;
	}
	
	public Inventory getUserTrailGUI(HumanEntity entity) {
		ParticleDisplayer displayer = displayerByUUID(entity.getUniqueId());
		if (Objects.isNull(displayer)) {
			final ParticleDisplayer e = new ParticleDisplayer(new PlayerParticleSettings(entity.getUniqueId(), Particle.HEART));
			particleDisplayers.add(e);
			displayer = e;
		}
		
		Inventory newGUI = Bukkit.createInventory(this, 9, GUI_TRAILS_CONFIGURATION);
		ItemStack enableDisable = new ItemStack(
				displayer.isLooping() ? Material.EMERALD : Material.BARRIER
				, 1);
		ItemMeta meta = enableDisable.getItemMeta();
		
		meta.setDisplayName(displayer.isLooping() ? GUI_DISABLE : GUI_ENABLE);
		meta.setLore(Collections.singletonList(displayer.isLooping() ? "Your trails are currently enabled." : "Your trails are currently disabled."));
		enableDisable.setItemMeta(meta);
		newGUI.addItem(enableDisable);
		return newGUI;
	}
	
	@Override
	public @NotNull Inventory getInventory() {
		System.out.println("oof dont know what to do");
		return null;
	}
	
	private ParticleDisplayer defaultDisplayer(UUID playerUUID) {
		return new ParticleDisplayer(new PlayerParticleSettings(playerUUID, Particle.HEART));
	}
	
	@EventHandler
	public void onInventoryClick(final InventoryClickEvent event) {
		HumanEntity player = event.getWhoClicked();
		ItemStack clickedStack = event.getCurrentItem();
		if (clickedStack != null) {
			if (event.getView().getTitle().equals(GUI_TRAILS_CONFIGURATION)) {
				ParticleDisplayer displayer = displayerByUUID(player.getUniqueId());
				if (displayer == null) {
					final ParticleDisplayer e = defaultDisplayer(player.getUniqueId());
					particleDisplayers.add(e);
					displayer = e;
				}
				player.closeInventory();
				final String clickedItemName = clickedStack.getItemMeta().getDisplayName();
				
				if (clickedItemName.equals(GUI_ENABLE)) {
					displayer.startLoop();
					player.sendMessage("Your trails have been " + GUI_ENABLE + "d");
				} else if (clickedItemName.equals(GUI_DISABLE)) {
					displayer.stopLoop();
					player.sendMessage("Your trails have been " + GUI_DISABLE + "d");
				}
			}
			
		}
	}
}
