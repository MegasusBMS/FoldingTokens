package Main;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	public final Logger logger = Logger.getLogger("Minecraft");
	public static Main plugin;
	Connection c = new Connection();
	TokenCalc t = new TokenCalc();
	String pref = ChatColor.AQUA + "[FoldingTokens]: ";
	boolean whitelist = true;
	PlayerPoints playerPoints;

	@Override
	public void onEnable() {
		PluginDescriptionFile pdffile = this.getDescription();
		this.logger.info(pdffile.getName() + " version " + pdffile.getVersion() + " A fost activat");
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		getCommand("foldingt").setExecutor(this);
		c.start();
		hookPlayerPoints();
	}

	@Override
	public void onDisable() {
		PluginDescriptionFile pdffile = this.getDescription();
		this.logger.info(pdffile.getName() + " A fost dezactivat");
	}
	
	private boolean hookPlayerPoints() {
	    final Plugin plugin = this.getServer().getPluginManager().getPlugin("PlayerPoints");
	    playerPoints = PlayerPoints.class.cast(plugin);
	    return playerPoints != null; 
	}

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		String name = p.getName();
		if (getConfig().get("Whitelist.Status:") != null)
			whitelist = (boolean) getConfig().get("Whitelist.Status:");
		if (whitelist) {
			List<String> list = new ArrayList<String>();
			list = (List<String>) getConfig().get("Whitelist.List:");
			if (list == null)
				return;
			if (list.size() == 0)
				return;
			if (!list.contains(name))
				return;
		}
		if(c.getPoints(name)==null)
			return;
		List<String> list = c.getPoints(name);
		if (list.get(0) == null || list.get(1) == null) {
			return;
		}
		String uuid = p.getUniqueId().toString();
		long credite = Long.parseLong(list.get(0));
		long wuuri = Long.parseLong(list.get(1));
		BigDecimal credits = BigDecimal.valueOf(credite);
		BigDecimal wu = BigDecimal.valueOf(wuuri);
		BigDecimal tokensPerWU = t.tokens(credits, wu);
		int tokens = wu.multiply(tokensPerWU).intValue();
		if (!getConfig().contains("Players." + uuid)) {
			if(playerPoints.getAPI().give(p.getUniqueId(), tokens)) {
			p.sendMessage(ChatColor.DARK_RED + "You recived " + tokens + " tokens from folding");
			getConfig().set("Players." + uuid + ".Tokens:", tokens);
			saveConfig();
			}
		} else {
			int ti = getConfig().getInt("Players." + uuid + ".Tokens:");
			int tf = tokens - ti;
			if (tf < 1)
				return;
			if(playerPoints.getAPI().give(p.getUniqueId(), tf)) {
			getConfig().set("Players." + uuid + ".Tokens:", tokens);
			saveConfig();
			p.sendMessage(ChatColor.DARK_RED + "You recived " + tf + " tokens from folding");
			}
		}
	}

	@SuppressWarnings("unchecked")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			if (label.equalsIgnoreCase("foldingt")) {
				if (args.length == 2) {
					if (args[0].equalsIgnoreCase("whitelist")) {
						if (args[1].equals("on")) {
							whitelist = true;
							getConfig().set("Whitelist.Status:", whitelist);
							sender.sendMessage(pref + ChatColor.DARK_RED + "Whitelist ON");
							saveConfig();
							return true;
						}
						if (args[1].equals("off")) {
							whitelist = false;
							getConfig().set("Whitelist.Status:", whitelist);
							sender.sendMessage(pref + ChatColor.DARK_RED + "Whitelist OFF");
							saveConfig();
							return true;
						}
						Player tp = Bukkit.getServer().getPlayer(args[1]);
						if (tp == null) {
							sender.sendMessage(pref + "/foldingt whitelist (on/off/name)");
							saveConfig();
							return false;
						}
						List<String> s = new ArrayList<String>();
						s = (List<String>) getConfig().get("Whitelist.List:");
						if (s == null) {
							s = new ArrayList<String>();
						}
						if (s.contains(tp.getName())) {
							s.remove(tp.getName());
							sender.sendMessage(
									pref + ChatColor.DARK_RED + "Player: " + tp.getName() + " removed from whitelist");
						} else {
							s.add(tp.getName());
							sender.sendMessage(
									pref + ChatColor.DARK_RED + "Player: " + tp.getName() + " added to whitelist");
						}
						getConfig().set("Whitelist.List:", s);
						saveConfig();
						return true;

					} else {
						sender.sendMessage(pref + "/foldingt whitelist (on/off/name)");
					}
				} else
					sender.sendMessage(pref + "/foldingt whitelist (on/off/name)");
			}
		}
		return false;
	}

}
