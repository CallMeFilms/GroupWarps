package me.callmefilms.GroupWarps;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class GWFront extends JavaPlugin {
	
	private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;

    @Override
    public void onDisable() {
        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    @Override
    public void onEnable() {
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();
        setupChat();
        if(this.getDataFolder().exists()) {
        	log.info("Successfully found data folder...");
        }
        else {
        	log.info("Data folder does not exist...");
        	log.info("Creating a data folder...");
        	this.getDataFolder().mkdir();
        	log.info("Created data folder...");
        }
        File config = new File(this.getDataFolder().getPath() + File.separator + "config.yml");
        if(config.exists()) {
        	log.info("Found config file...");
        } else {
        	log.info("Config file does not exist...");
        	log.info("Creating config file...");
        	try {
				config.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	log.info("Created config file...");
        	}
        YamlConfiguration configYAML = YamlConfiguration.loadConfiguration(config);
        configYAML.createSection("Warp Limits");
        configYAML.set("Warp Limits.Example", 3);
        try {
			configYAML.save(config);
			} catch (IOException e) {
				e.printStackTrace();
				}
        File warps = new File(this.getDataFolder().getPath() + File.separator + "warps.yml");
        if(warps.exists()) {
        	log.info("Found warps file...");
        } else {
        	log.info("Warps file does not exist...");
        	log.info("Creating warps file...");
        	try {
				warps.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	log.info("Created warps file...");
        }
        YamlConfiguration warpsYAML = YamlConfiguration.loadConfiguration(warps);
        if(!warpsYAML.contains("Warps")) {
        	warpsYAML.createSection("Warps");
        }
        Map<String, Object> exWarpInfo = new HashMap<String, Object>();
        exWarpInfo.put("Group", "Example");
        exWarpInfo.put("X", 1);
        exWarpInfo.put("Y", 2);
        exWarpInfo.put("Z", 3);
        exWarpInfo.put("Yaw", 0);
        exWarpInfo.put("Pitch", 0);
        warpsYAML.set("Warps.ExampleWarp", exWarpInfo);
        try {
			warpsYAML.save(warps);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        Bukkit.getServer().getPluginCommand("warp").setExecutor(new Commands());
        Bukkit.getServer().getPluginCommand("setwarp").setExecutor(new Commands());
        Bukkit.getServer().getPluginCommand("delwarp").setExecutor(new Commands());
        }
    
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    
    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }
    
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
    
    public static Economy getEcononomy() {
        return econ;
    }
    
    public static Permission getPermissions() {
        return perms;
    }
    
    public static Chat getChat() {
        return chat;
    }
    
    public static String putWithPrefix(String message) {
    	String prefix = ChatColor.GRAY + "[" + ChatColor.GOLD + "Warp" + ChatColor.GRAY + "] " + ChatColor.WHITE + ChatColor.BOLD + ">> " + ChatColor.RESET + ChatColor.YELLOW + message;
    	return prefix;
    }
    
    public static List<String> setToList(Set<String> set) {
    	Iterator<String> setIt = set.iterator();
    	List<String> list = new ArrayList<String>();
    	while(setIt.hasNext()) {
    		list.add(setIt.next());
    	}
		return list;
    }
}
