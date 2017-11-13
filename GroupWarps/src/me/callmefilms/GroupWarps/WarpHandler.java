package me.callmefilms.GroupWarps;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class WarpHandler {
	
	public static void createWarp(String name, String group, Location loc) {
		File warps = new File(Bukkit.getServer().getPluginManager().getPlugin("GroupWarps").getDataFolder() + File.separator + "warps.yml");
		YamlConfiguration warpsYAML = YamlConfiguration.loadConfiguration(warps);
		ConfigurationSection warpList = warpsYAML.getConfigurationSection("Warps");
		ConfigurationSection newWarp = warpList.createSection(name);
		newWarp.set("Group", group);
		newWarp.set("X", loc.getX());
		newWarp.set("Y", loc.getY());
		newWarp.set("Z", loc.getZ());
		newWarp.set("Yaw", loc.getYaw());
		newWarp.set("Pitch", loc.getPitch());
		try {
			warpsYAML.save(warps);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void setExistWarp(String name, String group, Location loc) {
		File warps = new File(Bukkit.getServer().getPluginManager().getPlugin("GroupWarps").getDataFolder() + File.separator + "warps.yml");
		YamlConfiguration warpsYAML = YamlConfiguration.loadConfiguration(warps);
		ConfigurationSection warpList = warpsYAML.getConfigurationSection("Warps");
		ConfigurationSection existWarp = warpList.getConfigurationSection(name);
		existWarp.set("Group", group);
		existWarp.set("X", loc.getX());
		existWarp.set("Y", loc.getY());
		existWarp.set("Z", loc.getZ());
		existWarp.set("Yaw", loc.getYaw());
		existWarp.set("Pitch", loc.getPitch());
		try {
			warpsYAML.save(warps);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void delWarp(String name, String group) {
		File warps = new File(Bukkit.getServer().getPluginManager().getPlugin("GroupWarps").getDataFolder() + File.separator + "warps.yml");
		YamlConfiguration warpsYAML = YamlConfiguration.loadConfiguration(warps);
		ConfigurationSection warpList = warpsYAML.getConfigurationSection("Warps");
		warpList.set(name, null);
		try {
			warpsYAML.save(warps);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
