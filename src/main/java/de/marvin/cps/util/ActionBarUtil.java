package de.marvin.cps.util;

import de.marvin.cps.CPSChecker;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ActionBarUtil {

    private static final Map<Player, BukkitTask> PENDING_MESSAGES = new HashMap<>();

    /**
     * Sends a message to the player's action bar.
     * <p>
     * The message will appear above the player's hot bar
     * for 2 seconds and then fade away over 1 second.
     *
     * @param bukkitPlayer the player to send the message to.
     * @param message the message to send.
     */
    public static void sendActionBarMessage(
            @NotNull Player bukkitPlayer,
            @NotNull String message
    ) {
        sendRawActionBarMessage(bukkitPlayer, "{\"text\": \"" + message + "\"}");
    }

    /**
     * Sends a raw message (JSON format) to the player's action
     * bar.
     * <p>
     * <b>Note:</b> While the action bar accepts raw messages
     * it is currently only capable of displaying text.
     * <p>
     * The message will appear above the player's hot bar for
     * 2 seconds and then fade away over 1 second.
     *
     * @param bukkitPlayer the player to send the message to.
     * @param rawMessage the json format message to send.
     */
    public static void sendRawActionBarMessage(
            @NotNull Player bukkitPlayer,
            @NotNull String rawMessage
    ) {
        var player = (CraftPlayer) bukkitPlayer;
        var chatBaseComponent = ChatSerializer.a(rawMessage);
        var packetPlayOutChat = new PacketPlayOutChat(chatBaseComponent, (byte) 2);
        player.getHandle().playerConnection.sendPacket(packetPlayOutChat);
    }

    /**
     * Sends a message to the player's action bar that
     * lasts for an extended duration.
     * <p>
     * The message will appear above the player's hot bar
     * for the specified duration and fade away during the
     * last second of the duration.
     * <p>
     * Only one long duration message can be sent at a time
     * per player. If a new message is sent via this message
     * any previous messages still being displayed will be
     * replaced.
     *
     * @param bukkitPlayer the player to send the message to.
     * @param message the message to send.
     * @param duration the duration the message should be visible for in seconds.
     */
    public static void sendActionBarMessage(
            @NotNull final Player bukkitPlayer,
            @NotNull final String message,
            final int duration
    ) {
        cancelPendingMessages(bukkitPlayer);
        final var messageTask = new BukkitRunnable() {
            private int count = 0;
            @Override
            public void run() {
                if (count >= (duration - 3)) {
                    this.cancel();
                }
                sendActionBarMessage(bukkitPlayer, message);
                count++;
            }
        }.runTaskTimer(CPSChecker.instance(), 0L, 20L);
        PENDING_MESSAGES.put(bukkitPlayer, messageTask);
    }

    /**
     * Cancels any pending action bar messages for the specified player.
     *
     * @param bukkitPlayer the player whose pending messages should be cancelled.
     */
    private static void cancelPendingMessages(@NotNull Player bukkitPlayer) {
        var task = PENDING_MESSAGES.remove(bukkitPlayer);
        if (task != null) task.cancel();
    }

}
