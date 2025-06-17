package de.marvin.cps.command;

import de.marvin.cps.message.Message;
import de.marvin.cps.message.Messages;
import de.marvin.cps.monitor.MonitorHandler;
import de.marvin.cps.monitor.MonitorMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class CPSCommand implements CommandExecutor {

    private final MonitorHandler monitorHandler;

    public CPSCommand(
            @NotNull final MonitorHandler monitorHandler
    ) {
        this.monitorHandler = monitorHandler;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] strings
    ) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return false;
        }

        if (!player.hasPermission("cps.use")) {
            Messages.send(player, Message.NO_PERMISSION);
            return false;
        }

        if (strings.length == 0) {
            if (player.hasPermission("cps.use.admin")) {
                Messages.send(player, Message.ADMIN_USAGE);
                return false;
            }
            Messages.send(player, Message.COMMAND_USAGE);
            return false;
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

        if (strings[0].equalsIgnoreCase("stop") && player.hasPermission("cps.use.admin")) {
            if (strings.length < 2) {
                Messages.send(player, Message.ADMIN_USAGE);
                return false;
            }

            var targetPlayer = isUniqueId(strings[1])
                    ? player.getServer().getPlayer(UUID.fromString(strings[1]))
                    : player.getServer().getPlayer(strings[1]);
            if (targetPlayer == null) {
                Messages.send(player, Message.PLAYER_NOT_FOUND);
                return false;
            }

            var result = this.monitorHandler.stop(targetPlayer);
            if (result == MonitorHandler.Result.NOT_MONITORING) {
                Messages.send(player, Message.NOT_MONITORING);
                return false;
            }

            Messages.send(player, Message.ADMIN_STOPPED_MONITOR,
                    Map.of("player", targetPlayer.getName())
            );
            return true;
        }

        if (strings[0].equalsIgnoreCase("off")) {
            var result = this.monitorHandler.stop(player);
            if (result == MonitorHandler.Result.NOT_MONITORING) {
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

        var mode = MonitorMode.BASIC;
        if (strings.length > 1) {
            mode = MonitorMode.fromString(strings[1]);
            if (mode == null) {
                Messages.send(player, player.hasPermission("cps.use.admin")
                        ? Message.ADMIN_USAGE
                        : Message.COMMAND_USAGE);
                return false;
            }
        }

        var result = this.monitorHandler.monitor(player, targetPlayer.getUniqueId(), mode);
        if (result == MonitorHandler.Result.USER_NOT_FOUND) {
            Messages.send(player, Message.PLAYER_NOT_FOUND);
            return false;
        }

        if (result == MonitorHandler.Result.ALREADY_MONITORING) {
            Messages.send(player, Message.ALREADY_MONITORING, Map.of(
                    "player", targetPlayer.getName(),
                    "mode", mode.name()
            ));
            return false;
        }

        Messages.send(player, Message.MONITORING_PLAYER, Map.of(
                "player", targetPlayer.getName(),
                "mode", mode.name()
        ));
        return true;
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
