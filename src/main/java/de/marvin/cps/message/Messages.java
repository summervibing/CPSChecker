package de.marvin.cps.message;

import de.marvin.cps.CPSChecker;
import de.marvin.cps.config.MessageConfig;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Messages {

    private static Map<Message, String> cached;

    /**
     * Caches all messages from {@link MessageConfig}.
     *
     * @param plugin {@link org.bukkit.plugin.java.JavaPlugin} instance
     */
    public static void initialize(
            @NotNull final CPSChecker plugin
    ) {
        var config = new MessageConfig(plugin);
        var map = new HashMap<Message, String>();
        for (var message : Message.values()) map.put(message, config.message(message));
        Messages.cached = Map.copyOf(map);
    }

    /**
     * Gets the specified message out of {@link Messages#cached}.
     *
     * @param message {@link Message} to return
     * @return Specified {@link Message} out of {@link Messages#cached}.
     */
    private static String get(
            @NotNull final Message message
    ) {
        if (Messages.cached == null) {
            CPSChecker.instance().getLogger().severe("Messages not initialized. Call Messages#initialize() first.");
            return ChatColor.RED + "CPSChecker is not working properly. Please contact an administrator.";
        }
        return Messages.cached.getOrDefault(message, "Message not found: " + message.name());
    }

    /**
     * Gets specified message and returns it formatted.
     *
     * @param message {@link Message} to return
     * @return Formatted {@link Message}.
     */
    public static String message(
            @NotNull final Message message
    ) {
        return formatted(message, null);
    }

    /**
     * Gets specified message and returns it formatted.
     *
     * @param message      {@link Message} to return
     * @param placeholders Placeholders to replace in message
     * @return Formatted {@link Message}.
     */
    public static String formatted(
            @NotNull final Message message,
            @Nullable final Map<String, Object> placeholders
    ) {
        var raw = get(message);
        if (placeholders == null || placeholders.isEmpty())
            return ChatColor.translateAlternateColorCodes('&', raw);

        for (var entry : placeholders.entrySet())
            raw = raw.replace("%" + entry.getKey() + "%", String.valueOf(entry.getValue()));

        return ChatColor.translateAlternateColorCodes('&', raw);
    }

    /**
     * Sends a formatted message to the player.
     *
     * @param player  {@link Player} to send the message to
     * @param message {@link Message} to send
     */
    public static void send(
            @NotNull final Player player,
            @NotNull final Message message
    ) {
        send(player, message, null);
    }

    /**
     * Sends a formatted message to the player.
     *
     * @param player       {@link Player} to send the message to
     * @param message      {@link Message} to send
     * @param placeholders Placeholders to replace in message
     */
    public static void send(
            @NotNull final Player player,
            @NotNull final Message message,
            @Nullable final Map<String, Object> placeholders
    ) {
        if (!player.isOnline()) return;
        player.sendMessage(formatted(message, placeholders));
    }

}
