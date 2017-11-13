package me.callmefilms.GroupWarps;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.chat.plugins.Chat_DroxPerms.PermissionServerListener;
import net.milkbowl.vault.permission.Permission;

public class Commands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sndr, Command cmd, String label, String[] args) {
		if(!(sndr instanceof Player)) {
			sndr.sendMessage("That command can not be executed by console personnel. Please try again in-game.");
		} else {
			Player player = (Player) sndr;
			Permission perms = GWFront.getPermissions();
			String[] playerGroups = perms.getPlayerGroups(player);
			File config = new File(Bukkit.getServer().getPluginManager().getPlugin("GroupWarps").getDataFolder() + File.separator + "config.yml");
			YamlConfiguration configYAML = new YamlConfiguration();
			try {
				configYAML.load(config);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
			Map<String, Object> groups = configYAML.getConfigurationSection("Warp Limits").getValues(false);
			File warpFile = new File(Bukkit.getServer().getPluginManager().getPlugin("GroupWarps").getDataFolder() + File.separator + "warps.yml");
			YamlConfiguration warpsYAML = new YamlConfiguration();
			try {
				warpsYAML.load(warpFile);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
			switch(cmd.getName()) {
			case "warp":
				if(args.length < 1) {
//					DISPLAY WARP LIST
				} else {
					if(warpsYAML.getConfigurationSection("Warps").contains(args[0])) {
						Map<String, Object> warpInfo = warpsYAML.getConfigurationSection("Warps").getConfigurationSection(args[0]).getValues(false);
						Location warpLoc = new Location(player.getWorld(), (double) warpInfo.get("X"), (double) warpInfo.get("Y"), (double) warpInfo.get("Z"));
						warpLoc.setYaw(Math.round((double) warpInfo.get("Yaw")));
						warpLoc.setPitch(Math.round((double) warpInfo.get("Pitch")));
						player.teleport(warpLoc);
						player.sendMessage(GWFront.putWithPrefix("Warped to " + ChatColor.GOLD + args[0] + ChatColor.YELLOW + "."));
					}
				}
				break;
			case "setwarp":
				if(args.length < 1) {
					player.sendMessage(GWFront.putWithPrefix(ChatColor.GOLD + "Correct usage: " + ChatColor.YELLOW + "/setwarp <name>"));
				} else {
					String highest = getHighestGroup(player);
					ConfigurationSection warps = warpsYAML.getConfigurationSection("Warps");
					Map<String, Object> warpInfo = new HashMap<String, Object>();
					Location warpLoc = player.getLocation();
					warpInfo.put("Group", highest);
					warpInfo.put("X", warpLoc.getX());
					warpInfo.put("Y", warpLoc.getY());
					warpInfo.put("Z", warpLoc.getZ());
					warpInfo.put("Yaw", warpLoc.getYaw());
					warpInfo.put("Pitch", warpLoc.getPitch());
					if(warps.contains(args[0])) {
						WarpHandler.setExistWarp(args[0], highest, player.getLocation());
						player.sendMessage(GWFront.putWithPrefix("Set warp " + ChatColor.GOLD + args[0] + ChatColor.YELLOW + " at " + warpLoc.getX() + ", " + warpLoc.getY() + ", " + warpLoc.getZ()));
					} else {
						int max = (int) groups.get(highest);
						if(warps.getKeys(false).size() == max) {
							player.sendMessage(GWFront.putWithPrefix("Your group can not add any more warps."));
						} else {
							warps.set(args[0], warpInfo);
							WarpHandler.createWarp(args[0], highest, warpLoc);
							player.sendMessage(GWFront.putWithPrefix("Set warp " + ChatColor.GOLD + args[0] + ChatColor.YELLOW + " at " + warpLoc.getX() + ", " + warpLoc.getY() + ", " + warpLoc.getZ()));
						}
					}
				}
				break;
			case "delwarp":
				if(args.length < 0) {
					player.sendMessage(GWFront.putWithPrefix(ChatColor.GOLD + "Correct usage: " + ChatColor.YELLOW + "/delwarp <name>"));
				} else {
					ConfigurationSection warps = warpsYAML.getConfigurationSection("Warps");
					if(warps.contains(args[0])) {
						for(String targPlayGroup : GWFront.getPermissions().getPlayerGroups(player)) {
							if(((String) warps.getConfigurationSection(args[0]).get("Group")).equalsIgnoreCase(targPlayGroup)) {
								WarpHandler.delWarp(args[0], targPlayGroup);
								player.sendMessage(GWFront.putWithPrefix("Warp " + ChatColor.GOLD + args[0] + ChatColor.YELLOW + " deleted"));
								return true;
							}
						}
						player.sendMessage(GWFront.putWithPrefix("This warp belongs to the group " + ChatColor.GOLD + warps.getConfigurationSection(args[0]).get("Group") + ChatColor.YELLOW + ", which you are not apart of."));
					} else {
						player.sendMessage(GWFront.putWithPrefix("Warp \"" + ChatColor.GOLD + args[0] + ChatColor.YELLOW + "\" does not exist."));
					}
				}
				break;
			}
		}
		return true;
	}
	
	static public String getHighestGroup(Player player) {
		Permission perms = GWFront.getPermissions();
		String[] playerGroups = perms.getPlayerGroups(player);
		File config = new File(Bukkit.getServer().getPluginManager().getPlugin("GroupWarps").getDataFolder() + File.separator + "config.yml");
		YamlConfiguration configYAML = new YamlConfiguration();
		try {
			configYAML.load(config);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		Map<String, Object> groups = configYAML.getConfigurationSection("Warp Limits").getValues(false);
		List<Integer> limits = new ArrayList<Integer>();
		for(String targGroup : playerGroups) {
			if(groups.containsKey(targGroup)) {
				limits.add((Integer) groups.get(targGroup));
			}
		}
		int max = Collections.max(limits);
		for(String targGroup : groups.keySet()) {
			if(groups.get(targGroup) == (Object) max) {
				return targGroup;
			}
		}
		return null;
	}
	
}
