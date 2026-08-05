package de.marvin.cps.plugin.command;

import com.google.inject.Inject;
import de.marvin.cps.core.click.ClickType;
import de.marvin.cps.core.message.Message;
import de.marvin.cps.core.message.Messages;
import de.marvin.cps.core.pattern.PatternType;
import de.marvin.cps.core.util.UniqueIdUtil;
import de.marvin.cps.core.permission.Permission;
import de.marvin.cps.plugin.command.subcommands.CPSSubCommand;
import de.marvin.cps.plugin.command.subcommands.StartSubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Handles the main command of the plugin, allowing players to monitor other players' cps and click patterns,
 * stop monitors, list active monitors and display the pattern explanation.
 */
public class CPSCommand implements CommandExecutor, TabCompleter {

    private final @NotNull JavaPlugin plugin;
    private final @NotNull Map<String, CPSSubCommand> subCommands;

    @Inject
    public CPSCommand(
            @NotNull JavaPlugin plugin,
            @NotNull Map<String, CPSSubCommand> subCommands
    ) {
        this.plugin = plugin;
        this.subCommands = subCommands;
    }

    /**
     * Main command of the plugin. Handles monitoring players, stopping monitors, listing currently active
     * monitors, and displaying the pattern explanation.
     * <p>
     * Requires the <code>cps.use</code> permission to execute the command, and the <code>cps.use.admin</code>
     * permission to access administrative features such as listing current monitors as well as starting and
     * stopping others' monitors.
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
     * @param args    Passed command arguments
     * @return {@code true} if the command was executed successfully, {@code false} if an error occurred or
     * usage message is being sent to the player.
     */
    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return false;
        }

        if (args.length == 0) {
            Messages.sendUsageMessage(player);
            return false;
        }

        var subCommand = this.subCommands.get(args[0].toLowerCase());
        if (subCommand == null) {
            var possibleTargetPlayer = UniqueIdUtil.isUniqueId(args[0])
                    ? player.getServer().getPlayer(UUID.fromString(args[0]))
                    : player.getServer().getPlayer(args[0]);

            if (possibleTargetPlayer == null) {
                Messages.sendUsageMessage(player);
                return false;
            }

            return monitor(
                    player,
                    possibleTargetPlayer,
                    args.length > 1 ? args[1] : null,
                    args.length > 2 ? args[2] : null
            );
        }

        var permission = subCommand.permission();
        if (permission != null && !player.hasPermission(permission)) {
            Messages.sendUsageMessage(player);
            return false;
        }

        return subCommand.onCommand(
                player,
                Arrays.copyOfRange(args, 1, args.length)
        );
    }

    /**
     * Handles tab completion for the command.
     *
     * @param sender  The {@link CommandSender} of the command
     * @param command The {@link Command} object representing the command
     * @param label   The label of the command
     * @param args    The arguments passed to the command
     * @return A {@link List} of possible completions for the command
     */
    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (!(sender instanceof Player player)) return List.of();

        if (args.length == 1) {
            var completions = new ArrayList<>(List.of("off", "help"));
            if (sender.hasPermission(Permission.ADMIN_COMMAND_USE.toString())) {
                completions.addAll(List.of("list", "start", "stop"));
            }
            completions.addAll(this.plugin.getServer().getOnlinePlayers().stream().map(Player::getName).toList());
            return completions;
        }

        var subCommand = this.subCommands.get(args[0].toLowerCase());

        // It is assumed that the player wants to toggle the monitor for themselves if the first argument
        // does not refer to a sub-command which is why the respective completions are set
        if (subCommand == null) return monitorCompletions(args.length, false);

        return subCommand.onTabComplete(
                player,
                Arrays.copyOfRange(args, 1, args.length)
        );
    }

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
    private boolean monitor(
            @NotNull Player executingPlayer,
            @NotNull Player targetPlayer,
            @Nullable String patternInput,
            @Nullable String clickInput
    ) {
        var startSubCommand = (StartSubCommand) this.subCommands.get("start");
        if (startSubCommand == null) {
            Messages.send(executingPlayer, Message.ERROR);
            return false;
        }

        return startSubCommand.monitor(
                executingPlayer,
                targetPlayer,
                patternInput,
                clickInput
        );
    }

    /**
     * Returns a {@link List} of tab completions based on the arguments position.
     *
     * @param argumentPosition Position of the argument
     * @param adminCommand     {@code true} if the administrative monitor start command is to be executed,
     *                         {@code false} if the executor wants to toggle the monitor for themselves
     * @return A {@link List} of tab completions based on the arguments position, or an empty {@link List} if
     * the {@link StartSubCommand} class is not found
     */
    private @NotNull List<String> monitorCompletions(
            int argumentPosition,
            boolean adminCommand
    ) {
        var startSubCommand = (StartSubCommand) this.subCommands.get("start");
        if (startSubCommand == null) return List.of();
        return startSubCommand.monitorCompletions(argumentPosition, adminCommand);
    }

}
