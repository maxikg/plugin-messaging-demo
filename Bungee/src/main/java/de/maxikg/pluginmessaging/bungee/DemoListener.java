package de.maxikg.pluginmessaging.bungee;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Logger;

public class DemoListener implements Listener {

    private static final Logger LOGGER = Logger.getLogger(DemoListener.class.getName());

    private final Plugin plugin;

    public DemoListener(Plugin plugin) {
        this.plugin = Preconditions.checkNotNull(plugin, "plugin must not be null.");
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent e) {
        if (DemoPlugin.CHANNEL.equals(e.getTag())) {
            // Connection comes from server. Safe to route. Otherwise it was sent by client which is unsafe.
            if (e.getSender() instanceof Server) {
                ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());

                int id = in.readByte();
                switch (id) {
                    case 0x00:
                        handleEffect((Server) e.getSender(), (ProxiedPlayer) e.getReceiver(), in);
                        break;
                    case 0x01:
                        handleEffectState(in);
                        break;
                    default:
                        LOGGER.warning("Unknown id: " + id);
                }
            } else if (e.getSender() instanceof ProxiedPlayer) {
                LOGGER.warning("Potential security error: " + ((ProxiedPlayer) e.getSender()).getName() + "tried to sent a packet through the plugin channel " + DemoPlugin.CHANNEL + ".");
            }
            e.setCancelled(true);
        }
    }

    private void handleEffect(Server server, ProxiedPlayer player, ByteArrayDataInput in) {
        ProxiedPlayer victim = plugin.getProxy().getPlayer(in.readUTF());
        if (victim == null) {
            // Victim isn't online or doesn't exists
            writeState(server, false);
            return;
        }

        int effect = in.readInt();

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.write(0x00);
        UUID uuid = player.getUniqueId();
        out.writeLong(uuid.getMostSignificantBits());
        out.writeLong(uuid.getLeastSignificantBits());
        out.writeInt(effect);
        victim.getServer().sendData(DemoPlugin.CHANNEL, out.toByteArray());
    }

    private void handleEffectState(ByteArrayDataInput in) {
        UUID uuid = new UUID(in.readLong(), in.readLong());
        ProxiedPlayer player = plugin.getProxy().getPlayer(uuid);
        // Origin isn't only. So just drop this.
        if (player == null) {
            LOGGER.warning("Sending player " + uuid + " isn't online right now.");
            return;
        }
        boolean success = in.readBoolean();
        System.out.println(success);
        writeState(player.getServer(), success);
    }

    private void writeState(Server server, boolean success) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.write(0x01);
        out.writeBoolean(success);
        server.sendData(DemoPlugin.CHANNEL, out.toByteArray());
    }
}
