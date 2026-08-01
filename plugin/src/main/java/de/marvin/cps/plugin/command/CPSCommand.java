package de.marvin.cps.plugin.command;

import com.google.inject.Inject;
import de.marvin.cps.api.monitor.MonitorHandler;
import de.marvin.cps.core.click.ClickSession;
import de.marvin.cps.core.click.ClickType;
import de.marvin.cps.core.message.Message;
import de.marvin.cps.core.message.Messages;
import de.marvin.cps.core.monitor.MonitorResult;
import de.marvin.cps.core.pattern.PatternType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

/**
 * Handles the main command of the plugin, allowing players
 * to monitor other players' cps and click patterns,
 * stop monitors, list active monitors and display the
 * pattern explanation.
 */
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
     * Requires the <code>cps.use</code> permission to execute
     * the command, and the <code>cps.use.admin</code> permission
     * to access administrative features such as listing current
     * monitors as well as starting and stopping others' monitors.
     * </p>
     * <p>
     * Command usage:
     * <pre>
     *         <strong>/cps help</strong> - Displays the pattern explanation page.
     *         <strong>/cps list</strong> - Lists all currently monitoring players, who they are
     *         monitoring and which mode they are using to do so. If no players are
     *         being monitored, a message is sent indicating that no monitors are
     *         active.
     *         <strong>/cps start &lt;username/uuid&gt; &lt;username/uuid&gt; [&lt;mode&gt;]
     *         [&lt;left/right&gt;]</strong> - Starts monitor for the specified player.
     *         <strong>/cps stop &lt;player&gt;</strong> - Stops monitor for the specified player. If the
     *         player is not monitoring anyone, a message is sent indicating that
     *         the monitor is not active.
     *         <strong>/cps off</strong> - Stops monitoring for the player who executed the command.
     *         If no player is being monitored, a message is sent indicating that
     *         monitor is not active.
     *         <strong>/cps &lt;player&gt; [&lt;mode&gt;] [&lt;left/right&gt;]</strong> - Starts monitoring the
     *         specified player. If no mode is specified, defaults to BASIC mode.
     *     </pre>
     * </p>
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param strings Passed command arguments
     * @return {@code true} if the command was executed successfully,
     * {@code false} if an error or usage message is being sent to
     * the player.
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

        if (strings[0].equalsIgnoreCase("start") && player.hasPermission("cps.use.admin")) {
            if (strings.length < 3) {
                sendUsageMessage(player);
                return false;
            }

            var controlledPlayer = isUniqueId(strings[1])
                    ? player.getServer().getPlayer(UUID.fromString(strings[1]))
                    : player.getServer().getPlayer(strings[1]);
            if (controlledPlayer == null) {
                Messages.send(player, Message.ADMIN_MONITOR_PLAYER_NOT_FOUND);
                return false;
            }

            var targetPlayer = isUniqueId(strings[2])
                    ? player.getServer().getPlayer(UUID.fromString(strings[2]))
                    : player.getServer().getPlayer(strings[2]);
            if (targetPlayer == null) {
                Messages.send(player, Message.ADMIN_MONITORED_PLAYER_NOT_FOUND);
                return false;
            }

            return monitor(
                    player,
                    controlledPlayer,
                    targetPlayer,
                    strings.length > 3 ? strings[3] : null,
                    strings.length > 4 ? strings[4] : null
            );
        }

        if (strings[0].equalsIgnoreCase("stop") && player.hasPermission("cps.use.admin")) {
            if (strings.length < 2) {
                sendUsageMessage(player);
                return false;
            }

            var controlledPlayer = isUniqueId(strings[1])
                    ? player.getServer().getPlayer(UUID.fromString(strings[1]))
                    : player.getServer().getPlayer(strings[1]);
            if (controlledPlayer == null) {
                Messages.send(player, Message.PLAYER_NOT_FOUND);
                return false;
            }

            var result = this.monitorHandler.stop(controlledPlayer);
            if (result == MonitorResult.NOT_MONITORING) {
                Messages.send(player, Message.ADMIN_NOT_MONITORING,
                        Map.of("controlled", controlledPlayer.getName())
                );
                return false;
            }

            Messages.send(player, Message.ADMIN_STOPPED_MONITOR,
                    Map.of("controlled", controlledPlayer.getName())
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

        return monitor(
                player,
                targetPlayer,
                strings.length > 1 ? strings[1] : null,
                strings.length > 2 ? strings[2] : null
        );
    }

    /**
     * Starts the monitor for the executing {@link Player} which
     * then is monitoring the target {@link Player} with the
     * provided {@link PatternType} and {@link ClickType}.
     *
     * @param executingPlayer Executing {@link Player}
     * @param targetPlayer    Monitored {@link Player}
     * @param patternInput    Used {@link PatternType}
     * @param clickInput      Showed {@link ClickType}
     * @return {@code true} if the monitor was started successfully,
     * {@code false} otherwise.
     */
    private boolean monitor(
            @NotNull Player executingPlayer,
            @NotNull Player targetPlayer,
            @Nullable String patternInput,
            @Nullable String clickInput
    ) {
        return monitor(executingPlayer, null, targetPlayer, patternInput, clickInput);
    }

    /**
     * Starts the monitor for the controlled {@link Player} which
     * then is monitoring the target {@link Player} with the
     * provided {@link PatternType} and {@link ClickType}.
     * <p>
     * <b>Note:</b> If no controlled {@link Player} is provided the
     * monitor is started for the executing {@link Player}.
     *
     * @param executingPlayer Executing {@link Player}
     * @param targetPlayer    Monitored {@link Player}
     * @param patternInput    Used {@link PatternType}
     * @param clickInput      Showed {@link ClickType}
     * @return {@code true} if the monitor was started successfully,
     * {@code false} otherwise.
     */
    private boolean monitor(
            @NotNull Player executingPlayer,
            @Nullable Player controlledPlayer,
            @NotNull Player targetPlayer,
            @Nullable String patternInput,
            @Nullable String clickInput
    ) {
        var monitoringPlayer = controlledPlayer != null ? controlledPlayer : executingPlayer;
        var monitor = this.monitorHandler.access(monitoringPlayer);

        var patternType = PatternType.BASIC;
        if (patternInput != null) {
            patternType = PatternType.fromString(patternInput);
            if (patternType == null) {
                sendUsageMessage(executingPlayer);
                return false;
            }
        } else {
            if (monitor != null) patternType = monitor.patternType();
        }

        var clickType = ClickType.LEFT_CLICK;
        if (clickInput != null) {
            clickType = ClickType.fromString(clickInput);
            if (clickType == null) {
                sendUsageMessage(executingPlayer);
                return false;
            }
        } else {
            if (monitor != null) clickType = monitor.clickType();
        }

        var result = this.monitorHandler.monitor(
                monitoringPlayer,
                targetPlayer.getUniqueId(),
                patternType,
                clickType
        );

        if (result == MonitorResult.USER_NOT_FOUND) {
            Messages.send(executingPlayer, Message.PLAYER_NOT_FOUND);
            return false;
        }

        if (result == MonitorResult.ALREADY_MONITORING) {
            Messages.send(
                    executingPlayer,
                    controlledPlayer == null ? Message.ALREADY_MONITORING : Message.ADMIN_ALREADY_MONITORING,
                    Map.of(
                            "controlled", monitoringPlayer.getName(),
                            "player", targetPlayer.getName(),
                            "pattern", patternType.name().toLowerCase(),
                            "click", clickType.name().toLowerCase().split("_")[0]
                    )
            );
            return false;
        }

        Messages.send(
                executingPlayer,
                controlledPlayer == null ? Message.MONITORING_PLAYER : Message.ADMIN_STARTED_MONITOR,
                Map.of(
                        "controlled", monitoringPlayer.getName(),
                        "player", targetPlayer.getName(),
                        "mode", patternType.name().toLowerCase(),
                        "click", clickType.name().toLowerCase().split("_")[0]
                )
        );
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
                : Message.COMMAND_USAGE
        );
    }

    /**
     * Checks if the given input is a valid {@link UUID}.
     *
     * @param input Input to check
     * @return {@code true} if the input is a valid {@link UUID},
     * {@code false} otherwise.
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
