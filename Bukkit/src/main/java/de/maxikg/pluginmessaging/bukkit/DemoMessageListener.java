package de.maxikg.pluginmessaging.bukkit;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.UUID;

public class DemoMessageListener implements PluginMessageListener {

    private final Plugin plugin;
    private final DemoMessenger messenger;

    public DemoMessageListener(Plugin plugin, DemoMessenger messenger) {
        this.plugin = Preconditions.checkNotNull(plugin, "plugin must not be null.");
        this.messenger = Preconditions.checkNotNull(messenger, "messenger must not be null.");
    }

    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);

        int id = in.readByte();
        switch (id) {
            case 0x00:
                handleEffect(player, in);
                break;
            case 0x01:
                handleEffectState(player, in);
                break;
            default:
                plugin.getLogger().warning("Packet received with unknown id: " + id);
        }
    }

    private void handleEffect(Player victim, ByteArrayDataInput in) {
        UUID sender = new UUID(in.readLong(), in.readLong());
        Effect effect = Effect.values()[in.readInt()];

        if (effect == null) {
            messenger.indicateEffectSuccess(sender, victim, false);
            return;
        }

        effect.play(victim);
        messenger.indicateEffectSuccess(sender, victim, true);
    }

    private void handleEffectState(Player player, ByteArrayDataInput in) {
        boolean success = in.readBoolean();

        if (success)
            player.sendMessage(ChatColor.GREEN + "The effect was successfully executed.");
        else
            player.sendMessage(ChatColor.RED + "The effect wasn't executed. Maybe the target isn't online or the effect unavailable.");
    }
}
