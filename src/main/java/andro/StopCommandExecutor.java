package andro;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StopCommandExecutor implements CommandExecutor {

    private final ScavengeHunt plugin;

    public StopCommandExecutor(ScavengeHunt plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        try {
            plugin.endGame();
        } catch (ScavengeException ex) {
            commandSender.sendMessage(ex.getMessage());
        }
        return true;
    }
}
