package de.marvin.cps.plugin.command.subcommands;

import de.marvin.cps.core.click.ClickSession;
import de.marvin.cps.core.message.Message;
import de.marvin.cps.core.message.Messages;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Sub-command displays the pattern explanation page.
 */
public class HelpSubCommand extends CPSSubCommand {

    /**
     * {@inheritDoc}
     *
     * @return The name of the sub-command
     */
    @Override
    public @NotNull String name() {
        return "help";
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
        var displaySize = ClickSession.displaySize();
        Messages.send(player, Message.PATTERN_HELP, Map.of(
                "seconds", displaySize / 20,
                "ticks", displaySize
        ));
        return false;
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
