package de.marvin.cps.plugin.command.subcommands;

import com.google.inject.Inject;
import de.marvin.cps.api.monitor.MonitorHandler;
import de.marvin.cps.core.click.ClickType;
import de.marvin.cps.core.message.Message;
import de.marvin.cps.core.message.Messages;
import de.marvin.cps.core.monitor.MonitorResult;
import de.marvin.cps.core.pattern.PatternType;
import de.marvin.cps.core.util.UniqueIdUtil;
import de.marvin.cps.core.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Sub-command starts monitor for the specified player.
 */
public class StartSubCommand extends CPSSubCommand {

    private final @NotNull JavaPlugin plugin;
    private final @NotNull MonitorHandler monitorHandler;

    @Inject
    public StartSubCommand(
            @NotNull JavaPlugin plugin,
            @NotNull MonitorHandler monitorHandler
    ) {
        this.plugin = plugin;
        this.monitorHandler = monitorHandler;
    }

    /**
     * {@inheritDoc}
     *
     * @return The name of the sub-command
     */
    @Override
    public @NotNull String name() {
        return "start";
    }

    /**
     * {@inheritDoc}
     *
     * @param player The executing {@link Player} of the sub-command
     * @param args   The arguments passed to the sub-command
     * @return {@code true} if the command was executed successfully, {@code false} if any error occurred
     */
    @Override
    public boolean onCommand(
            @NotNull Player player,
            @NotNull String[] args
    ) {
        if (args.length < 2) {
            Messages.sendUsageMessage(player);
            return false;
        }

        var controlledPlayer = UniqueIdUtil.isUniqueId(args[0])
                ? player.getServer().getPlayer(UUID.fromString(args[0]))
                : player.getServer().getPlayer(args[0]);
        if (controlledPlayer == null) {
            Messages.send(player, Message.ADMIN_MONITOR_PLAYER_NOT_FOUND);
            return false;
        }

        var targetPlayer = UniqueIdUtil.isUniqueId(args[1])
                ? player.getServer().getPlayer(UUID.fromString(args[1]))
                : player.getServer().getPlayer(args[1]);
        if (targetPlayer == null) {
            Messages.send(player, Message.ADMIN_MONITORED_PLAYER_NOT_FOUND);
            return false;
        }

        return monitor(
                player,
                controlledPlayer,
                targetPlayer,
                args.length > 2 ? args[2] : null,
                args.length > 3 ? args[3] : null
        );
    }

    /**
     * {@inheritDoc}
     *
     * @param player The executing {@link Player} of the sub-command
     * @param args   The arguments passed to the sub-command
     * @return A {@link List} of possible completions for the sub-command
     */
    @Override
    public List<String> onTabComplete(
            @NotNull Player player,
            @NotNull String[] args
    ) {
        return monitorCompletions(args.length, true);
    }

    /**
     * {@inheritDoc}
     *
     * @return The permissions needed for execution of the sub-command
     */
    @Override
    public @Nullable String permission() {
        return Permission.ADMIN_COMMAND_USE.toString();
    }

    // Helper methods

    /**
     * Starts the monitor for the executing {@link Player} which then is monitoring the target {@link Player}
     * with the provided {@link PatternType} and {@link ClickType}.
     *
     * @param executingPlayer Executing {@link Player}
     * @param targetPlayer    Monitored {@link Player}
     * @param patternInput    Used {@link PatternType}
     * @param clickInput      Showed {@link ClickType}
     * @return {@code true} if the monitor was started successfully, {@code false} otherwise
     */
    public boolean monitor(
            @NotNull Player executingPlayer,
            @NotNull Player targetPlayer,
            @Nullable String patternInput,
            @Nullable String clickInput
    ) {
        return monitor(executingPlayer, null, targetPlayer, patternInput, clickInput);
    }

    /**
     * Starts the monitor for the controlled {@link Player} which then is monitoring the target {@link Player}
     * with the provided {@link PatternType} and {@link ClickType}.
     * <p>
     * <b>Note:</b> If no controlled {@link Player} is provided the monitor is started for the executing
     * {@link Player}.
     *
     * @param executingPlayer Executing {@link Player}
     * @param targetPlayer    Monitored {@link Player}
     * @param patternInput    Used {@link PatternType}
     * @param clickInput      Showed {@link ClickType}
     * @return {@code true} if the monitor was started successfully, {@code false} otherwise
     */
    public boolean monitor(
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
                Messages.sendUsageMessage(executingPlayer);
                return false;
            }
        } else {
            if (monitor != null) patternType = monitor.patternType();
        }

        var clickType = ClickType.LEFT_CLICK;
        if (clickInput != null) {
            clickType = ClickType.fromString(clickInput);
            if (clickType == null) {
                Messages.sendUsageMessage(executingPlayer);
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

    /**
     * Returns a {@link List} of tab completions based on the arguments position.
     *
     * @param argumentPosition Position of the argument
     * @param adminCommand     {@code true} if the administrative monitor start command is to be executed,
     *                         {@code false} if the executor wants to toggle the monitor for themselves
     * @return A {@link List} of tab completions based on the arguments position
     */
    public List<String> monitorCompletions(
            int argumentPosition,
            boolean adminCommand
    ) {
        if (argumentPosition <= 0) return List.of();
        if (argumentPosition == 1) return this.plugin.getServer().getOnlinePlayers()
                .stream()
                .map(Player::getName)
                .toList();


        if (adminCommand) {
            if (argumentPosition == 2) return this.plugin.getServer().getOnlinePlayers()
                    .stream()
                    .map(Player::getName)
                    .toList();
            if (argumentPosition == 3) return Stream.of(PatternType.values())
                    .map(mode -> mode.name().toLowerCase())
                    .toList();
            if (argumentPosition == 4) return List.of("left", "right");
            return List.of();
        }

        if (argumentPosition == 2) return Stream.of(PatternType.values())
                .map(mode -> mode.name().toLowerCase())
                .toList();
        if (argumentPosition == 3) return List.of("left", "right");
        return List.of();
    }

}
