package de.marvin.cps.plugin.command.subcommands;

import com.google.inject.Inject;
import de.marvin.cps.api.monitor.MonitorHandler;
import de.marvin.cps.core.message.Message;
import de.marvin.cps.core.message.Messages;
import de.marvin.cps.core.monitor.MonitorResult;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Sub-command stops monitoring for the player who executed the command. If no player is being monitored, a
 * message is sent indicating that monitor is not active.
 */
public class OffSubCommand extends CPSSubCommand {

    private final @NotNull MonitorHandler monitorHandler;

    @Inject
    public OffSubCommand(
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
        return "off";
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
        var result = this.monitorHandler.stop(player);
        if (result == MonitorResult.NOT_MONITORING) {
            Messages.send(player, Message.NOT_MONITORING);
            return false;
        }
        Messages.send(player, Message.MONITORING_OFF);
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

}
