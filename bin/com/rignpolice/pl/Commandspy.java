package com.rignpolice.pl;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Commandspy extends JavaPlugin implements Listener {
	Config pca = new Config(this);

	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("STARTED");
		getLogger().info("By DaRignio");
		loadConfig();
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
	}

	public void loadConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	public void onDisable() {
		getLogger().info("Disabled!");
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String cmd = command.getName();
		if (cmd.equalsIgnoreCase("csreload")) {
			reloadConfig();
			saveConfig();
			sender.sendMessage(color("&cReloading &econfig.yml"));
			sender.sendMessage(color("&aReloaded!"));
			return true;
		}
		if (cmd.equalsIgnoreCase("commandspy")) {
			if ((sender instanceof Player)) {
				Player player = (Player) sender;
				if (player.hasPermission("ic.spy") || player.isOp()) {
					if (args.length == 0) {
						if (pca.getBoolean(player, "enabled")) {
							player.sendMessage(color("&3Commandspy disabled!"));
							pca.set(player, "enabled", Boolean.valueOf(false));
						} else {
							player.sendMessage(color("&3Commandspy enabled!"));
							pca.set(player, "enabled", Boolean.valueOf(true));
						}
						return true;
					}
					if (args.length == 1) {
						Player playerargs = Bukkit.getPlayer(args[0]);
						if (pca.getBoolean(playerargs, "enabled")) {
							sender.sendMessage(color("&3Commandspy disabled for " + playerargs.getName() + "!"));
							pca.set(playerargs, "enabled", Boolean.valueOf(false));
						} else {
							sender.sendMessage(color("&3Commandspy enabled for " + playerargs.getName() + "!"));
							pca.set(playerargs, "enabled", Boolean.valueOf(true));
						}
						return true;
					}
				} else {
					sender.sendMessage(color("&4No permission!"));
				}
			} else {
				sender.sendMessage(color("Only players can use commandspy!"));
			}
		}
		return true;
	}

	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		String n = p.getName();
		String m = e.getMessage();
		String f = getConfig().getString("format");
		String wf = getConfig().getString("worldedit-format");
		for (Player g : Bukkit.getOnlinePlayers()) {
			if (((g.hasPermission("ic.spy")) || (g.isOp())) && (pca.getBoolean(g, "enabled"))) {
				for (String blacklisted : getConfig().getStringList("blacklisted")) {
					if (!blacklisted.contains(n)) {
						if (m.startsWith("//")) {
							g.sendMessage(color(wf).replace("{player}", n).replace("{command}", m));
						}
						if (!m.startsWith("//")) {
							g.sendMessage(color(f).replace("{player}", n).replace("{command}", m));
						}
					} else {

					}
				}
			}
		}

	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (!pca.exists(p)) {

			pca.createConfig(e.getPlayer());
			getLogger().info("Config created for " + p.getName());
			pca.set(p, "enabled", Boolean.valueOf(false));
			pca.set(p, "important", Boolean.valueOf(false));
		} else {
			getLogger().info("Config found for " + p.getName());
			pca.set(p, "name", p.getName());
			pca.set(p, "uuid", p.getUniqueId().toString());
			pca.set(p, "ip", p.getAddress().toString());
			if (p.hasPermission("ic.spy") || (p.isOp())) {
				if (!pca.getBoolean(p, "enabled")) {
					p.sendMessage(color("&3Commandspy disabled!"));
					pca.set(p, "enabled", Boolean.valueOf(false));
				} else {
					p.sendMessage(color("&3Commandspy enabled!"));
					pca.set(p, "enabled", Boolean.valueOf(true));
				}
			}
		}
	}

	public static String color(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}
}
