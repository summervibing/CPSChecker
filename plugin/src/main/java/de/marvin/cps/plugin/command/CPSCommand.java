package de.marvin.cps.plugin.command;

import com.google.inject.Inject;
import de.marvin.cps.core.click.ClickSession;
import de.marvin.cps.core.click.ClickType;
import de.marvin.cps.core.message.Message;
import de.marvin.cps.core.message.Messages;
import de.marvin.cps.api.monitor.MonitorHandler;
import de.marvin.cps.core.pattern.PatternType;
import de.marvin.cps.core.monitor.MonitorResult;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

/**
 * Handles the main command of the plugin, allowing players
 * to monitor other players' cps and click patterns,
 * stop monitors, list active monitors and display the
 * pattern explanation.
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class CPSCommand implements CommandExecutor {

    private final MonitorHandler monitorHandler;

    @Inject
    public CPSCommand(MonitorHandler monitorHandler) {
        this.monitorHandler = monitorHandler;
    }

    /**
     * Main command of the plugin. Handles monitoring players,
     * stopping monitors, listing currently active monitors,
     * and displaying the pattern explanation.
     * <p>
     *     Requires the <code>cps.use</code> permission to execute
     *     the command, and the <code>cps.use.admin</code> permission
     *     to access administrative features such as
     *     listing current monitors and stopping others' monitors.
     * </p>
     * <p>
     *     Command usage:
     *     <pre>
     *         <strong>/cps &lt;player&gt; [mode]</strong> - Starts monitoring the specified
     *         player. If no mode is specified, defaults to BASIC mode.
     *         <strong>/cps off</strong> - Stops monitoring for the player who executed
     *         the command. If no player is being monitored, a message is sent
     *         indicating that monitor is not active.
     *         <strong>/cps stop &lt;player&gt;</strong> - Stops monitor for the specified
     *         player. If the player is not monitoring anyone, a message
     *         is sent indicating that the monitor is not active.
     *         <strong>/cps list</strong> - Lists all currently monitoring players,
     *         who they are monitoring and which mode they are using to do so. If
     *         no players are being monitored, a message is sent indicating that
     *         no monitors are active.
     *         <strong>/cps help</strong> - Displays the pattern explanation page.
     *     </pre>
     * </p>
     *
     * @param sender Source of the command
     * @param command Command which was executed
     * @param label Alias of the command which was used
     * @param strings Passed command arguments
     * @return {@code true} if the command was executed successfully,
     *         {@code false} if an error or usage message is being
     *         sent to the player.
     */
    @Override
    public boolean onCommand(
            @NotNull final CommandSender sender,
            @NotNull final Command command,
            @NotNull final String label,
            @NotNull final String[] strings
    ) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return false;
        }

        if (strings.length == 0) {
            sendUsageMessage(player);
            return false;
        }

        if (strings[0].equalsIgnoreCase("help")) {
            var displaySize = ClickSession.displaySize();
            Messages.send(player, Message.PATTERN_HELP, Map.of(
                    "seconds", displaySize / 20,
                    "ticks", displaySize
            ));
            return true;
        }

        if (strings[0].equalsIgnoreCase("list") && player.hasPermission("cps.use.admin")) {
            var monitors = this.monitorHandler.monitors();
            if (monitors.isEmpty()) {
                Messages.send(player, Message.ADMIN_NO_CURRENT_MONITORS);
                return false;
            }

            Messages.send(player, Message.ADMIN_CURRENT_MONITORS_HEADER, Map.of("count", monitors.size()));
            monitors.forEach((p, monitor) -> Messages.send(player, Message.ADMIN_CURRENT_MONITOR,
                    Map.of("player", p.getName(), "monitored", monitor.user().name())
            ));
            return true;
        }

        if (strings[0].equalsIgnoreCase("stop") && player.hasPermission("cps.use.admin")) {
            if (strings.length < 2) {
                Messages.send(player, Message.ADMIN_USAGE);
                return false;
            }

            var targetPlayer = isUniqueId(strings[1])
                    ? player.getServer().getPlayer(UUID.fromString(strings[1]))
                    : player.getServer().getPlayer(strings[1]);
            if (targetPlayer == null) {
                Messages.send(player, Message.PLAYER_NOT_FOUND);
                return false;
            }

            var result = this.monitorHandler.stop(targetPlayer);
            if (result == MonitorResult.NOT_MONITORING) {
                Messages.send(player, Message.NOT_MONITORING);
                return false;
            }

            Messages.send(player, Message.ADMIN_STOPPED_MONITOR,
                    Map.of("player", targetPlayer.getName())
            );
            return true;
        }

        if (strings[0].equalsIgnoreCase("off")) {
            var result = this.monitorHandler.stop(player);
            if (result == MonitorResult.NOT_MONITORING) {
                Messages.send(player, Message.NOT_MONITORING);
                return false;
            }
            Messages.send(player, Message.MONITORING_OFF);
            return true;
        }

        var targetPlayer = isUniqueId(strings[0])
                ? player.getServer().getPlayer(UUID.fromString(strings[0]))
                : player.getServer().getPlayer(strings[0]);
        if (targetPlayer == null) {
            Messages.send(player, Message.PLAYER_NOT_FOUND);
            return false;
        }

        var monitor = this.monitorHandler.access(player);

        var patternType = PatternType.BASIC;
        if (strings.length > 1) {
            patternType = PatternType.fromString(strings[1]);
            if (patternType == null) {
                sendUsageMessage(player);
                return false;
            }
        } else {
            if (monitor != null) patternType = monitor.patternType();
        }

        var clickType = ClickType.LEFT_CLICK;
        if (strings.length > 2) {
            clickType = ClickType.fromString(strings[2]);
            if (clickType == null) {
                sendUsageMessage(player);
                return false;
            }
        } else {
            if (monitor != null) clickType = monitor.clickType();
        }

        var result = this.monitorHandler.monitor(player, targetPlayer.getUniqueId(), patternType, clickType);
        if (result == MonitorResult.USER_NOT_FOUND) {
            Messages.send(player, Message.PLAYER_NOT_FOUND);
            return false;
        }

        if (result == MonitorResult.ALREADY_MONITORING) {
            Messages.send(player, Message.ALREADY_MONITORING, Map.of(
                    "player", targetPlayer.getName(),
                    "pattern", patternType.name().toLowerCase(),
                    "click", clickType.name().toLowerCase().split("_")[0]
            ));
            return false;
        }

        Messages.send(player, Message.MONITORING_PLAYER, Map.of(
                "player", targetPlayer.getName(),
                "mode", patternType.name().toLowerCase(),
                "click", clickType.name().toLowerCase().split("_")[0]
        ));
        return true;
    }

    // Helper methods

    /**
     * Sends a usage message to the {@link Player} based on
     * their permissions.
     *
     * @param player {@link Player} to send the usage message to
     */
    private void sendUsageMessage(
            @NotNull final Player player
    ) {
        Messages.send(player, player.hasPermission("cps.use.admin")
                ? Message.ADMIN_USAGE
                : Message.COMMAND_USAGE);
    }

    /**
     * Checks if the given input is a valid {@link UUID}.
     *
     * @param input Input to check
     * @return {@code true} if the input is a valid {@link UUID},
     *         {@code false} otherwise.
     */
    private boolean isUniqueId(
            @NotNull final String input
    ) {
        try {
            UUID.fromString(input);
            return true;
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

}
