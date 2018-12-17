package andro;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ListZonesCommandExecutor implements CommandExecutor {

    private final ScavengeHunt plugin;

    public ListZonesCommandExecutor(ScavengeHunt plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (plugin.zones == null) {
            commandSender.sendMessage("Zones: [" + String.join(", ", plugin.listZones()) + "]");
        } else {
            commandSender.sendMessage(ChatColor.YELLOW + "Zones:");
            for (Zone zone : plugin.zones) {
                commandSender.sendMessage("-> " + zone.getName() + ChatColor.YELLOW + " [Eggs left: " + ChatColor.LIGHT_PURPLE + zone.getEggs().size() + ChatColor.YELLOW + "]");
            }
        }
        return true;
    }
}
