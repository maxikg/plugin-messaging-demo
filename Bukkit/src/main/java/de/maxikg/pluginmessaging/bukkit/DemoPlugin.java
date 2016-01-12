package de.maxikg.pluginmessaging.bukkit;

import com.google.common.base.Joiner;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class DemoPlugin extends JavaPlugin {

    private DemoMessenger messenger;

    @Override
    public void onEnable() {
        messenger = new DemoMessenger(this);
        messenger.register(new DemoMessageListener(this, messenger));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only usable for players.");
            return true;
        }

        if (args.length != 2)
            return false;

        String target = args[0];
        Effect effect = Effect.match(args[1]);
        if (effect == null) {
            sender.sendMessage(ChatColor.RED + "Unknown effect. Available effects are " + Joiner.on(", ").join(Effect.values()));
            return true;
        }

        messenger.playEffect((Player) sender, target, effect);
        return true;
    }
}
