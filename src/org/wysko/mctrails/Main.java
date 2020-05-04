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
import org.bukkit.command.TabCompleter;
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

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class Main extends JavaPlugin implements Listener, Serializable, InventoryHolder, TabCompleter {
	
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
		int argNum = args.length;
		switch (command.getName().toLowerCase()) {
			case "createtrail":
				switch (argNum) {
					case 2:
						String typed = args[1];
						List<String> particles = new ArrayList<>();
						for (Particle particle : Particle.values()) {
							if (Pattern.compile("^" + typed.toUpperCase()).matcher(particle.name()).find())
								particles.add(particle.name());
						}
						return particles;
					case 3:
						typed = args[2];
						List<String> materials = new ArrayList<>();
						for (Material material : Material.values()) {
							if (Pattern.compile("^" + typed.toUpperCase()).matcher(material.name()).find())
								materials.add(material.name());
						}
						return materials;
					default:
						return new ArrayList<>();
				}
			case "deletetrail":
				if (argNum == 1) {
					String typed = args[0];
					List<String> trailNames = new ArrayList<>();
					for (Trail trail : allTrails) {
						if (Pattern.compile("^" + typed.toUpperCase()).matcher(trail.trailName.toUpperCase()).find()
								&& !trail.trailName.equalsIgnoreCase("default"))
							trailNames.add(trail.trailName);
					}
					return trailNames;
				}
			case "permittrail":
			case "revoketrail":
				switch (argNum) {
					case 1:
						List<String> trailNames = new ArrayList<>();
						for (Trail trail : allTrails) {
							String typed = args[0];
							if (Pattern.compile("^" + typed.toUpperCase()).matcher(trail.trailName.toUpperCase()).find()
									&& !trail.trailName.equalsIgnoreCase("default"))
								trailNames.add(trail.trailName);
						}
						return trailNames;
					case 2:
						List<String> playerNames = new ArrayList<>();
						for (Player player : Bukkit.getOnlinePlayers()) {
							playerNames.add(player.getDisplayName());
						}
						return playerNames;
				}
			case "settrailrate":
			case "settrailamount":
				if (argNum == 1) {
					List<String> trailNames = new ArrayList<>();
					for (Trail trail : allTrails) {
						if (!trail.trailName.equalsIgnoreCase("default"))
							trailNames.add(trail.trailName);
					}
					return trailNames;
				}
				return new ArrayList<>();
			default:
				return new ArrayList<>();
		}
	}
	
	final String GUI_DISABLE = ChatColor.RED + "Disable";
	final String GUI_ENABLE = ChatColor.GREEN + "Enable";
	final String GUI_LIST = "Change trail";
	final File folder = new File("plugins/MCTrails");
	final File playerAvailableTrailsFile = new File("plugins/MCTrails/player_perms.db");
	final File allTrailsFile = new File("plugins/MCTrails/trails.db");
	final File uuidUsernameFile = new File("plugins/MCTrails/users.db");
	
	HashMap<UUID, ParticleDisplayer> particleDisplayers = new HashMap<>();
	HashMap<UUID, ArrayList<Trail>> playerAvailableTrails = new HashMap<>();
	HashMap<UUID, Inventory> lastOpenMenu = new HashMap<>();
	HashMap<UUID, ConfigScreen> lastOpenMenuType = new HashMap<>();
	List<Trail> allTrails = new ArrayList<>();
	HashMap<String, UUID> uuidUsername = new HashMap<>();
	
	void writeAllVarsToFile() throws IOException {
		if (!folder.exists()) folder.mkdir();
		ObjectOutputStream stream2 = new ObjectOutputStream(new FileOutputStream(playerAvailableTrailsFile));
		stream2.writeObject(playerAvailableTrails);
		stream2.close();
		ObjectOutputStream stream3 = new ObjectOutputStream(new FileOutputStream(allTrailsFile));
		stream3.writeObject(allTrails);
		stream3.close();
		ObjectOutputStream stream4 = new ObjectOutputStream(new FileOutputStream(uuidUsernameFile));
		stream4.writeObject(uuidUsername);
		stream4.close();
	}
	
	@SuppressWarnings("unchecked")
	void readAllFiles() throws IOException, ClassNotFoundException {
		ObjectInputStream stream2 = new ObjectInputStream(new FileInputStream(playerAvailableTrailsFile));
		playerAvailableTrails = (HashMap<UUID, ArrayList<Trail>>) stream2.readObject();
		ObjectInputStream stream3 = new ObjectInputStream(new FileInputStream(allTrailsFile));
		allTrails = (List<Trail>) stream3.readObject();
		ObjectInputStream stream4 = new ObjectInputStream(new FileInputStream(uuidUsernameFile));
		uuidUsername = (HashMap<String, UUID>) stream4.readObject();
	}
	
	@Override
	public void onEnable() {
		try {
			readAllFiles();
		} catch (Exception e) {
			System.err.println("[MCTrails] Error loading db files. This is not an issue if this is your first time starting the server" +
					"with this plugin, or you have deleted the db files.");
		}
		
		if (!allTrails.contains(Trail.DEFAULT_TRAIL)) {
			allTrails.add(Trail.DEFAULT_TRAIL);
		}
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
	}
	
	@Override
	public void onDisable() {
		try {
			writeAllVarsToFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void onPlayerJoin(@NotNull PlayerJoinEvent event) throws IOException {
		final UUID uuid = event.getPlayer().getUniqueId();
		ParticleDisplayer displayer = particleDisplayers.get(uuid);
		if (Objects.nonNull(displayer)) {
			displayer.reset();
			if (displayer.wasEnabledOnQuit()) {
				displayer.startLoop();
			}
		}
		if (particleDisplayers.get(uuid) == null) {
			particleDisplayers.put(uuid, defaultDisplayer(uuid));
		}
		playerAvailableTrails.computeIfAbsent(uuid, k -> new ArrayList<>());
		boolean found = false;
		for (Trail trail : playerAvailableTrails.get(uuid)) {
			if (trail.trailName.equalsIgnoreCase("default")) {
				found = true;
				break;
			}
		}
		if (!found) {
			playerAvailableTrails.get(uuid).add(Trail.DEFAULT_TRAIL);
			System.out.println("apparently it doesnt have default so fuck it it's goin in there");
		}
		if (!uuidUsername.containsValue(uuid)) {
			uuidUsername.put(event.getPlayer().getDisplayName().toUpperCase(), uuid);
		}
		writeAllVarsToFile();
	}
	
	@EventHandler
	public void onPlayerQuit(@NotNull PlayerQuitEvent event) throws IOException {
		ParticleDisplayer displayer = particleDisplayers.get(event.getPlayer().getUniqueId());
		if (Objects.nonNull(displayer)) {
			displayer.enabledOnQuit = displayer.isLooping();
		}
		writeAllVarsToFile();
	}
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		Player player = Bukkit.getPlayer(sender.getName());
		assert player != null;
		String commandText = command.getName().toLowerCase();
		if ("trails".equals(commandText)) {
			try {
				player.openInventory(getUserTrailGUI(player, ConfigScreen.MAIN));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if ("createtrail".equals(commandText)) {
			if (args.length != 5) {
				return false;
			}
			String trailname = args[0];
			Material icon;
			Particle particle;
			try {
				icon = Material.valueOf(args[2].toUpperCase());
			} catch (IllegalArgumentException e) {
				player.sendMessage("§c\"" + args[2] + "\" is not a valid material for an icon.");
				return true;
			}
			try {
				particle = Particle.valueOf(args[1].toUpperCase());
			} catch (IllegalArgumentException e) {
				player.sendMessage("§c\"" + args[1] + "\" is not a valid particle.");
				return true;
			}
			
			for (Trail trail : allTrails) {
				if (trail.trailName.equalsIgnoreCase(trailname)) {
					sender.sendMessage("§c\"" + trailname + "\" already exists.");
					return true;
				}
			}
			if (Pattern.compile("[^a-zA-Z0-9]").matcher(trailname).find()) {
				sender.sendMessage("§c\"" + trailname + "\" must be alphanumeric.");
				return true;
			}
			int rate;
			int amount;
			try {
				rate = Integer.parseInt(args[3]);
			} catch (NumberFormatException e) {
				sender.sendMessage("§c" + args[3] + " is not a valid number.");
				return true;
			}
			try {
				amount = Integer.parseInt(args[4]);
			} catch (NumberFormatException e) {
				sender.sendMessage("§c" + args[4] + " is not a valid number.");
				return true;
			}
			if (amount <= 0) {
				sender.sendMessage("§c" + args[3] + " is not a valid number.");
				return true;
			}
			if (rate <= 0) {
				sender.sendMessage("§c" + args[4] + " is not a valid number.");
				return true;
			}
			allTrails.add(new Trail(trailname, particle, icon, rate, amount));
			sender.sendMessage("§aTrail \"" + trailname + "\" has been created.");
			try {
				writeAllVarsToFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if ("deletetrail".equals(commandText)) {
			if (args.length != 1) {
				return false;
			}
			String trailName = args[0];
			boolean exists = doesTrailExist(trailName);
			if (!exists) {
				sender.sendMessage("§c\"" + trailName + "\" does not exist.");
				return true;
			}
			if (trailName.equalsIgnoreCase("default")) {
				sender.sendMessage("§cYou cannot delete the default trail.");
				return true;
			}
			
			for (Map.Entry<UUID, ParticleDisplayer> displayer : particleDisplayers.entrySet()) {
				if (displayer.getValue().settings.trail.trailName.equalsIgnoreCase(trailName)) {
					displayer.getValue().settings.trail = Trail.DEFAULT_TRAIL;
					displayer.getValue().safeRestart();
				}
			}
			for (int i = allTrails.size() - 1; i >= 0; i--) {
				Trail trail = allTrails.get(i);
				if (trail.trailName.equalsIgnoreCase(trailName)) {
					allTrails.remove(i);
				}
			}
			for (Map.Entry<UUID, ArrayList<Trail>> entry : playerAvailableTrails.entrySet()) {
				for (int i = entry.getValue().size() - 1; i >= 0; i--) {
					if (entry.getValue().get(i).trailName.equalsIgnoreCase(trailName)) {
						entry.getValue().remove(i);
					}
				}
			}
			sender.sendMessage("§aTrail \"" + trailName + "\" has been removed.");
			try {
				writeAllVarsToFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if ("alltrails".equals(commandText)) {
			if (args.length != 0) {
				return false;
			}
			if (allTrails.isEmpty()) {
				sender.sendMessage("§eThere are no trails!");
			}
			for (int i = 0; i < allTrails.size(); i++) {
				Trail trail = allTrails.get(i);
				sender.sendMessage(String.format(
						"Trail #%d: %s, %s, %s",
						i, trail.trailName,
						trail.particle,
						trail.guiIcon
				));
			}
			try {
				writeAllVarsToFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if ("permittrail".equals(commandText)) {
			if (args.length != 2) {
				return false;
			}
			String trailName = args[0];
			String playerName = args[1];
			
			if (!doesTrailExist(trailName)) {
				sender.sendMessage("§cTrail \"" + trailName + "\" does not exist.");
				return true;
			}
			Trail trailToAdd = null;
			for (Trail trail : allTrails) {
				if (trail.trailName.equalsIgnoreCase(trailName)) {
					trailToAdd = trail;
					break;
				}
			}
			UUID playerUUID = uuidUsername.get(playerName.toUpperCase());
			for (Trail trail : playerAvailableTrails.get(playerUUID)) {
				if (trail.trailName.equalsIgnoreCase(trailName)) {
					sender.sendMessage("§ePlayer already can use this trail.");
					return true;
				}
			}
			playerAvailableTrails.get(playerUUID).add(trailToAdd);
			sender.sendMessage(String.format("§a%s can now use %s.", playerName, trailName));
			try {
				writeAllVarsToFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if ("revoketrail".equals(commandText)) {
			if (args.length != 2) {
				return false;
			}
			String trailName = args[0];
			String playerName = args[1];
			
			if (!doesTrailExist(trailName)) {
				sender.sendMessage("§c\"" + trailName + "\" does not exist");
				return true;
			}
			if (trailName.equalsIgnoreCase("default")) {
				sender.sendMessage("§cYou cannot revoke access to the default trail.");
			}
			UUID playerUUID = uuidUsername.get(playerName.toUpperCase());
			if (Objects.isNull(playerUUID)) {
				sender.sendMessage("§c\"" + playerName + "\" does not exist or has not played on this server.");
				return true;
			}
			boolean hadAccess = false;
			final ArrayList<Trail> playerAvailableTrails = this.playerAvailableTrails.get(playerUUID);
			for (Trail trail : playerAvailableTrails) {
				if (trail.trailName.equalsIgnoreCase(trailName)) {
					hadAccess = true;
					break;
				}
			}
			if (!hadAccess) {
				sender.sendMessage("§ePlayer already didn't have permission to use that trail.");
				return true;
			}
			for (int i = playerAvailableTrails.size() - 1; i >= 0; i--) {
				if (playerAvailableTrails.get(i).trailName.equalsIgnoreCase(trailName)) {
					playerAvailableTrails.remove(i);
					break;
				}
			}
			for (Map.Entry<UUID, ParticleDisplayer> displayer : particleDisplayers.entrySet()) {
				if (displayer.getValue().settings.trail.trailName.equalsIgnoreCase(trailName)) {
					displayer.getValue().settings.trail = Trail.DEFAULT_TRAIL;
					displayer.getValue().safeRestart();
				}
			}
			sender.sendMessage(String.format("§a%s can no longer use %s.", playerName, trailName));
			try {
				writeAllVarsToFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (commandText.equalsIgnoreCase("settrailamount")) {
			if (args.length != 2) {
				return false;
			}
			String trailName = args[0];
			if (!doesTrailExist(trailName)) {
				sender.sendMessage("§c\"" + trailName + "\" does not exist");
				return true;
			}
			int amount;
			try {
				amount = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				sender.sendMessage("§c" + args[1] + " is not a valid number.");
				return true;
			}
			if (amount <= 0) {
				sender.sendMessage("§c" + args[1] + " is not a valid number.");
				return true;
			}
			for (Trail trail : allTrails) {
				if (trail.trailName.equalsIgnoreCase(trailName)) {
					trail.amount = amount;
				}
			}
			for (Map.Entry<UUID, ParticleDisplayer> displayer : particleDisplayers.entrySet()) {
				if (displayer.getValue().settings.trail.trailName.equalsIgnoreCase(trailName)) {
					displayer.getValue().settings.trail.amount = amount;
					
					displayer.getValue().safeRestart();
				}
			}
			sender.sendMessage("§a" + trailName + "'s amount is now " + amount + ".");
			try {
				writeAllVarsToFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (commandText.equalsIgnoreCase("settrailrate")) {
			if (args.length != 2) {
				return false;
			}
			String trailName = args[0];
			if (!doesTrailExist(trailName)) {
				sender.sendMessage("§c\"" + trailName + "\" does not exist");
				return true;
			}
			int rate;
			try {
				rate = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				sender.sendMessage("§c" + args[1] + " is not a valid number.");
				return true;
			}
			if (rate <= 0) {
				sender.sendMessage("§c" + args[1] + " is not a valid number.");
				return true;
			}
			for (Trail trail : allTrails) {
				if (trail.trailName.equalsIgnoreCase(trailName)) {
					trail.rate = rate;
				}
			}
			for (Map.Entry<UUID, ParticleDisplayer> displayer : particleDisplayers.entrySet()) {
				if (displayer.getValue().settings.trail.trailName.equalsIgnoreCase(trailName)) {
					displayer.getValue().settings.trail.rate = rate;
					displayer.getValue().safeRestart();
				}
			}
			sender.sendMessage("§a" + trailName + "'s rate is now " + rate + " ms.");
			try {
				writeAllVarsToFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	private boolean doesTrailExist(String trailName) {
		boolean found = false;
		for (Trail trail : allTrails) {
			if (trail.trailName.equalsIgnoreCase(trailName)) {
				found = true;
				break;
			}
		}
		return found;
	}
	
	/**
	 * Returns the inventory menu that this user will see on {@code /trails}. Depending on their permissions, they may
	 * see different options.
	 *
	 * @param entity the player related to this GUI
	 * @return the trails configuration menu
	 */
	public Inventory getUserTrailGUI(@NotNull HumanEntity entity, @NotNull Main.ConfigScreen screen) throws IOException {
		final UUID uuid = entity.getUniqueId();
		ParticleDisplayer displayer = particleDisplayers.get(uuid);
		if (Objects.isNull(displayer)) {
			final ParticleDisplayer e = new ParticleDisplayer(new PlayerParticleSettings(uuid, Trail.DEFAULT_TRAIL));
			particleDisplayers.put(uuid, e);
			displayer = e;
		}
		switch (screen) {
			case MAIN:
				Inventory mainMenuGUI = Bukkit.createInventory(this, 9, ConfigScreen.MAIN.title);
				/* Enable/Disable */
				ItemStack enableDisable = new ItemStack(
						displayer.isLooping() ? Material.EMERALD : Material.BARRIER
						, 1);
				ItemMeta meta = enableDisable.getItemMeta();
				
				meta.setDisplayName(displayer.isLooping() ? GUI_DISABLE : GUI_ENABLE);
				meta.setLore(Collections.singletonList(displayer.isLooping() ? "Your trails are currently enabled." : "Your trails are currently disabled."));
				enableDisable.setItemMeta(meta);
				mainMenuGUI.addItem(enableDisable);
				
				/* Available trails */
				ItemStack changeTrail = new ItemStack(Material.BOOK, 1);
				ItemMeta meta1 = changeTrail.getItemMeta();
				meta1.setDisplayName(GUI_LIST);
				meta1.setLore(Collections.singletonList("Change your active trail."));
				changeTrail.setItemMeta(meta1);
				mainMenuGUI.addItem(changeTrail);
				if (lastOpenMenu.get(uuid) == null) {
					lastOpenMenu.put(uuid, mainMenuGUI);
					lastOpenMenuType.put(uuid, ConfigScreen.MAIN);
				} else {
					lastOpenMenu.replace(uuid, mainMenuGUI);
					lastOpenMenuType.replace(uuid, ConfigScreen.MAIN);
				}
				writeAllVarsToFile();
				return mainMenuGUI;
			case LIST:
				Inventory listGUI = Bukkit.createInventory(this, (int) Math.ceil((double) playerAvailableTrails.get(uuid).size() / 9) * 9, ConfigScreen.LIST.title);
				List<Trail> usersAvailableTrails = playerAvailableTrails.get(uuid);
				if (Objects.isNull(usersAvailableTrails)) {
					playerAvailableTrails.put(uuid, new ArrayList<Trail>() {{
						add(Trail.DEFAULT_TRAIL);
					}});
				}
				usersAvailableTrails = playerAvailableTrails.get(uuid);
				for (Trail trail : usersAvailableTrails) {
					ItemStack stack = new ItemStack(trail.guiIcon, 1);
					ItemMeta meta2 = stack.getItemMeta();
					meta2.setDisplayName(trail.trailName);
					if (trail == displayer.settings.trail) {
						// TODO figure out how to make something glow
					}
					stack.setItemMeta(meta2);
					listGUI.addItem(stack);
				}
				if (lastOpenMenu.get(uuid) == null) {
					lastOpenMenu.put(uuid, listGUI);
					lastOpenMenuType.put(uuid, ConfigScreen.LIST);
				} else {
					lastOpenMenu.replace(uuid, listGUI);
					lastOpenMenuType.replace(uuid, ConfigScreen.LIST);
				}
				writeAllVarsToFile();
				return listGUI;
			default:
				writeAllVarsToFile();
				return null;
		}
	}
	
	
	@Override
	public @NotNull Inventory getInventory() {
		System.out.println("oof dont know what to do");
		return null;
	}
	
	private ParticleDisplayer defaultDisplayer(UUID playerUUID) {
		return new ParticleDisplayer(new PlayerParticleSettings(playerUUID, Trail.DEFAULT_TRAIL));
	}
	
	@EventHandler
	public void onInventoryClick(final InventoryClickEvent event) throws IOException {
		HumanEntity player = event.getWhoClicked();
		final UUID uuid = player.getUniqueId();
		final Inventory openMenu = lastOpenMenu.get(uuid);
		final ConfigScreen openMenuType = lastOpenMenuType.get(uuid);
		if (!event.getClickedInventory().equals(openMenu)) {
			return;
		}
		ItemStack clickedStack = event.getCurrentItem();
		if (clickedStack != null) {
			ParticleDisplayer displayer = particleDisplayers.get(uuid);
			if (displayer == null) {
				final ParticleDisplayer e = defaultDisplayer(uuid);
				particleDisplayers.putIfAbsent(uuid, e);
				displayer = e;
			}
			
			final String clickedItemName = clickedStack.getItemMeta().getDisplayName();
			switch (openMenuType) {
				case MAIN:
					if (clickedItemName.equals(GUI_ENABLE)) {
						displayer.startLoop();
						player.sendMessage("Your trails have been " + GUI_ENABLE + "d");
						player.closeInventory();
					} else if (clickedItemName.equals(GUI_DISABLE)) {
						displayer.stopLoop();
						player.sendMessage("Your trails have been " + GUI_DISABLE + "d");
						player.closeInventory();
					} else if (clickedItemName.equals(GUI_LIST)) {
						player.openInventory(getUserTrailGUI(player, ConfigScreen.LIST));
					}
					writeAllVarsToFile();
					break;
				case LIST:
					for (Trail trail : allTrails) {
						if (clickedItemName.equals(trail.trailName)) {
							displayer.settings.trail = trail;
							displayer.safeRestart();
							player.sendMessage("Your trail has been set to §l" + trail.trailName);
							player.openInventory(getUserTrailGUI(player, ConfigScreen.MAIN));
							break;
						}
					}
					writeAllVarsToFile();
					break;
			}
		}
	}
	
	enum ConfigScreen {
		/**
		 * The main menu.
		 */
		MAIN("Trails Configuration"),
		/**
		 * The list of all available trails this user can select.
		 */
		LIST("Available Trails");
		String title;
		
		ConfigScreen(String title) {
			this.title = title;
		}
	}
}
