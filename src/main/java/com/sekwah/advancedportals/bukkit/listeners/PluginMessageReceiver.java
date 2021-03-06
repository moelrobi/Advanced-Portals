package com.sekwah.advancedportals.bukkit.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.sekwah.advancedportals.bukkit.AdvancedPortalsPlugin;
import com.sekwah.advancedportals.bukkit.destinations.Destination;
import com.sekwah.advancedportals.bungee.BungeeMessages;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.UUID;

public class PluginMessageReceiver implements PluginMessageListener {

    private AdvancedPortalsPlugin plugin;

    public PluginMessageReceiver(AdvancedPortalsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {

        if (!channel.equals(plugin.channelName)) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();

        if (subchannel.equals(BungeeMessages.SERVER_DESTI)) {
            String targetDestination = in.readUTF();
            UUID bungeeUUID = UUID.fromString(in.readUTF());
            UUID offlineUUID = UUID.fromString(in.readUTF());

            Player targetPlayer = this.plugin.getServer().getPlayer(bungeeUUID);

            if(targetPlayer == null) {
                targetPlayer = this.plugin.getServer().getPlayer(offlineUUID);
                this.plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + BungeeMessages.WARNING_MESSAGE
                        + "\n\nThis server is the offending server.");
            }

            if (targetPlayer != null) {
                Player finalTargetPlayer = targetPlayer;
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
                        () -> Destination.warp(finalTargetPlayer, targetDestination, false, true),
                        20L
                );
            }
        }
    }

    /**
     * Example forward packet.
     *
     * Construct like the forge packets.
     *
     * out.writeUTF("Forward"); // So BungeeCord knows to forward it
     out.writeUTF("ALL");
     out.writeUTF("MyChannel"); // The channel name to check if this your data

     ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
     DataOutputStream msgout = new DataOutputStream(msgbytes);
     msgout.writeUTF("Some kind of data here"); // You can do anything you want with msgout
     msgout.writeShort(123);

     out.writeShort(msgbytes.toByteArray().length);
     out.write(msgbytes.toByteArray());
     *
     */
}
