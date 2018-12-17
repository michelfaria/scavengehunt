package io.github.michelfaria.scavengehunt;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StartCommandExecutor implements CommandExecutor {

    private final ScavengeHunt plugin;

    public StartCommandExecutor(ScavengeHunt plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (plugin.isGameRunning()) {
            commandSender.sendMessage("Game already running!");
            return true;
        }

        if (args.length != 1) {
            return false;
        }

        long time;
        try {
            time = Long.parseLong(args[0]);
        } catch (NumberFormatException ex) {
            return false;
        }

        try {
            plugin.startGame(time);
        } catch (ScavengeException ex) {
            commandSender.sendMessage(ex.getMessage());
            return true;
        }
        return true;
    }
}
