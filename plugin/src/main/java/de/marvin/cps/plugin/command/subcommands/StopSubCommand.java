package de.marvin.cps.plugin.command.subcommands;

import com.google.inject.Inject;
import de.marvin.cps.api.monitor.MonitorHandler;
import de.marvin.cps.core.message.Message;
import de.marvin.cps.core.message.Messages;
import de.marvin.cps.core.monitor.MonitorResult;
import de.marvin.cps.core.util.UniqueIdUtil;
import de.marvin.cps.core.permission.Permission;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Sub-command stops the monitor for the specified player. If the player is not monitoring anyone, a message
 * is sent indicating that the monitor is not active.
 */
public class StopSubCommand extends CPSSubCommand {

    private final @NotNull MonitorHandler monitorHandler;

    @Inject
    public StopSubCommand(
            @NotNull MonitorHandler monitorHandler
    ) {
        this.monitorHandler = monitorHandler;
    }

    /**
     * {@inheritDoc}
     *
     * @return The name of the sub-command
     */
    @Override
    public @NotNull String name() {
        return "stop";
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
        if (args.length < 1) {
            Messages.sendUsageMessage(player);
            return false;
        }

        var controlledPlayer = UniqueIdUtil.isUniqueId(args[0])
                ? player.getServer().getPlayer(UUID.fromString(args[0]))
                : player.getServer().getPlayer(args[0]);
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
        if (args.length != 1) return List.of();
        return this.monitorHandler.monitors().keySet().stream().map(Player::getName).toList();
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

}
