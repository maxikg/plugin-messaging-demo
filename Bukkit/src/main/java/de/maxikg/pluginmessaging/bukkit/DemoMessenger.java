package de.maxikg.pluginmessaging.bukkit;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.UUID;

public class DemoMessenger {

    public static String CHANNEL = "demo-plugin";

    private final Plugin plugin;

    public DemoMessenger(Plugin plugin) {
        this.plugin = Preconditions.checkNotNull(plugin, "plugin must not be null.");
    }

    public void register(PluginMessageListener listener) {
        Messenger messenger = plugin.getServer().getMessenger();
        messenger.registerOutgoingPluginChannel(plugin, CHANNEL);
        messenger.registerIncomingPluginChannel(plugin, CHANNEL, listener);
    }

    public void playEffect(Player sender, String target, Effect effect) {
        // Creates a new ByteArrayDataOutput
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        // Write packet identifier (0x00 outgoing = play effect packet)
        out.write(0x00);

        // Write the name of the target
        out.writeUTF(target);

        // Write the ordinal of the effect
        out.writeInt(effect.ordinal());

        // Writes the content of out to the plugin channel
        sender.sendPluginMessage(plugin, CHANNEL, out.toByteArray());
    }

    public void indicateEffectSuccess(UUID senderUuid, Player player, boolean success) {
        // Creates a new ByteArrayDataOutput
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        // Write packet identifier (0x00 outgoing = effect success packet)
        out.write(0x01);

        // Writes the sender
        out.writeLong(senderUuid.getMostSignificantBits());
        out.writeLong(senderUuid.getLeastSignificantBits());

        // Write the success state
        out.writeBoolean(success);

        // Writes the content of out to the plugin channel
        player.sendPluginMessage(plugin, CHANNEL, out.toByteArray());
    }
}
