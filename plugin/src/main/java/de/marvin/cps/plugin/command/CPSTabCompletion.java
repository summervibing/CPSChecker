package de.marvin.cps.plugin.command;

import com.google.inject.Inject;
import de.marvin.cps.api.monitor.MonitorHandler;
import de.marvin.cps.core.pattern.PatternType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Handles tab completion for the {@link CPSCommand}.
 * Provides completions based on the command context.
 */
public class CPSTabCompletion implements TabCompleter {

    private final JavaPlugin plugin;
    private final MonitorHandler monitorHandler;

    @Inject
    public CPSTabCompletion(
            @NotNull final JavaPlugin plugin,
            @NotNull final MonitorHandler monitorHandler
    ) {
        this.plugin = plugin;
        this.monitorHandler = monitorHandler;
    }

    @Override
    public List<String> onTabComplete(
            @NotNull final CommandSender sender,
            @NotNull final Command command,
            @NotNull final String label,
            @NotNull final String[] strings
    ) {
        // Suggestions for the first argument
        if (strings.length == 1) {
            var completions = new ArrayList<>(List.of("off", "help"));
            if (sender.hasPermission("cps.use.admin")) completions.addAll(List.of("list", "start", "stop"));
            completions.addAll(this.plugin.getServer().getOnlinePlayers().stream().map(Player::getName).toList());
            return completions;
        }

        // Suggestions for the second argument
        if (strings.length == 2) {
            if (strings[0].equalsIgnoreCase("stop") && sender.hasPermission("cps.use.admin"))
                return this.monitorHandler.monitors().keySet().stream().map(Player::getName).toList();
            if (strings[0].equalsIgnoreCase("start") && sender.hasPermission("cps.use.admin"))
                return this.plugin.getServer().getOnlinePlayers().stream().map(Player::getName).toList();
            if ((this.isUniqueId(strings[0]) && this.plugin.getServer().getPlayer(UUID.fromString(strings[0])) != null)
                    || this.plugin.getServer().getPlayer(strings[0]) != null)
                return Stream.of(PatternType.values()).map(mode -> mode.name().toLowerCase()).toList();
        }

        // Suggestions for the third argument
        if (strings.length == 3) {
            if (strings[0].equalsIgnoreCase("start") && sender.hasPermission("cps.use.admin"))
                return this.plugin.getServer().getOnlinePlayers().stream().map(Player::getName).toList();
            if ((this.isUniqueId(strings[0]) && this.plugin.getServer().getPlayer(UUID.fromString(strings[0])) != null)
                    || this.plugin.getServer().getPlayer(strings[0]) != null)
                return List.of("left", "right");
        }

        // Suggestions for the fourth argument
        if (strings.length == 4) {
            if (strings[0].equalsIgnoreCase("start") && sender.hasPermission("cps.use.admin"))
                return Stream.of(PatternType.values()).map(mode -> mode.name().toLowerCase()).toList();
        }

        // Suggestions for the fifth argument
        if (strings.length == 5) {
            if (strings[0].equalsIgnoreCase("start") && sender.hasPermission("cps.use.admin"))
                return List.of("left", "right");
        }

        return null;
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
