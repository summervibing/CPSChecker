package de.marvin.cps.command;

import de.marvin.cps.monitor.MonitorHandler;
import de.marvin.cps.monitor.MonitorMode;
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
@SuppressWarnings("ResultOfMethodCallIgnored")
public class CPSTabCompletion implements TabCompleter {

    private final JavaPlugin plugin;
    private final MonitorHandler monitorHandler;

    public CPSTabCompletion(
            @NotNull final JavaPlugin plugin,
            @NotNull final MonitorHandler monitorHandler
    ) {
        this.plugin = plugin;
        this.monitorHandler = monitorHandler;
    }

    @Override
    public List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String label,
            String[] strings
    ) {

        if (strings.length == 1) {
            var completions = new ArrayList<>(List.of("off", "help"));
            if (sender.hasPermission("cps.use.admin")) completions.addAll(List.of("list", "stop"));
            completions.addAll(this.plugin.getServer().getOnlinePlayers().stream().map(Player::getName).toList());
            return completions;
        }

        if (strings.length == 2) {
            if (strings[0].equalsIgnoreCase("stop") && sender.hasPermission("cps.use.admin"))
                return this.monitorHandler.monitors().keySet().stream().map(Player::getName).toList();
            if ((isUniqueId(strings[0]) && this.plugin.getServer().getPlayer(UUID.fromString(strings[0])) != null)
                    || this.plugin.getServer().getPlayer(strings[0]) != null)
                return Stream.of(MonitorMode.values()).map(mode -> mode.name().toLowerCase()).toList();
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
    private boolean isUniqueId(@NotNull final String input) {
        try {
            UUID.fromString(input);
            return true;
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

}
