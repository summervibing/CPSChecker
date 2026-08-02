package de.marvin.cps.plugin.command.subcommands;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Class representing a sub-command for the {@link CPSSubCommand}.
 */
public abstract class CPSSubCommand {

    /**
     * Returns the name of the sub-command.
     *
     * @return The name of the sub-command
     */
    public abstract @NotNull String name();

    /**
     * Executes the sub-command with the given sender and arguments.
     *
     * @param player The executing {@link Player} of the sub-command
     * @param args   The arguments passed to the sub-command
     * @return {@code true} if the command was executed successfully, {@code false} if any error occurred
     */
    public abstract boolean onCommand(@NotNull Player player, @NotNull String[] args);

    /**
     * Returns a {@link List} of possible completions for the sub-command.
     *
     * @param player The executing {@link Player} of the sub-command
     * @param args   The arguments passed to the sub-command
     * @return A {@link List} of possible completions for the sub-command
     */
    public abstract List<String> onTabComplete(@NotNull Player player, @NotNull String[] args);

    /**
     * Returns the permissions needed for execution of the sub-command.
     * <p>
     * <b>Note:</b> If no additional permissions are required besides {@code cps.use}, just return
     * {@code null}.
     *
     * @return The permissions needed for execution of the sub-command
     */
    public @Nullable String permission() {
        return null;
    }

}
