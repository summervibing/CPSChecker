package de.marvin.cps.plugin.command.subcommands;

import com.google.inject.Inject;
import de.marvin.cps.api.monitor.MonitorHandler;
import de.marvin.cps.core.message.Message;
import de.marvin.cps.core.message.Messages;
import de.marvin.cps.permission.Permission;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Sub-command lists all currently monitoring players, who they are monitoring and which mode they are using
 * to do so. If no players are being monitored, a message is sent indicating that no monitors are active.
 */
public class ListSubCommand extends CPSSubCommand {

    private final MonitorHandler monitorHandler;

    @Inject
    public ListSubCommand(
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
        return "list";
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
        var monitors = this.monitorHandler.monitors();
        if (monitors.isEmpty()) {
            Messages.send(player, Message.ADMIN_NO_CURRENT_MONITORS);
            return false;
        }

        Messages.send(player, Message.ADMIN_CURRENT_MONITORS_HEADER, Map.of("count", monitors.size()));
        monitors.forEach((p, monitor) -> Messages.send(player, Message.ADMIN_CURRENT_MONITOR,
                Map.of(
                        "player", p.getName(),
                        "monitored", monitor.user().name()
                )
        ));
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
        return List.of();
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
