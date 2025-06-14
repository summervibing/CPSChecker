package de.marvin.cps.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import de.marvin.cps.CPSChecker;
import de.marvin.cps.click.ClickHandler;
import de.marvin.cps.monitor.MonitorHandler;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Handles registering of clicks and switching
 * the current monitor mode.
 * <p>
 *     {@link PacketListener#handleMonitorSwitch()}: Listens to
 *     item drop packets to switch the current
 *     {@link de.marvin.cps.monitor.MonitorMode MonitorMode}.
 * </p>
 * <p>
 *     {@link PacketListener#handleInvalidClicks()}: Listens to
 *     block dig packets to register to {@link PacketListener#isDigging}
 *     when a player starts and when they stop digging a block in order
 *     to count these clicks as invalid clicks.
 * </p>
 * <p>
 *     {@link PacketListener#handleClicks()}: Listens to
 *     arm swing packets to register clicks.
 * </p>
 */
public class PacketListener {

    private final CPSChecker plugin;

    private final ClickHandler clickHandler;
    private final MonitorHandler monitorHandler;

    private final ProtocolManager protocolManager;

    private final Set<UUID> isDigging = new HashSet<>();
    private final Set<UUID> lastClickInvalid = new HashSet<>();

    public PacketListener(
            @NotNull final CPSChecker plugin,
            @NotNull final ClickHandler clickHandler,
            @NotNull final MonitorHandler monitorHandler
    ) {
        this.plugin = plugin;
        this.clickHandler = clickHandler;
        this.monitorHandler = monitorHandler;
        this.protocolManager = ProtocolLibrary.getProtocolManager();

        handleMonitorSwitch();
        handleInvalidClicks();
        handleClicks();
        handleAttacks();
    }

    /**
     * Listens to item drop packet to switch the current
     * {@link de.marvin.cps.monitor.MonitorMode}.
     */
    private void handleMonitorSwitch() {
        this.protocolManager.addPacketListener(new PacketAdapter(this.plugin, ListenerPriority.NORMAL, PacketType.Play.Client.BLOCK_DIG) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                var player = event.getPlayer();
                var digType = event.getPacket().getPlayerDigTypes().read(0);

                if (digType != EnumWrappers.PlayerDigType.DROP_ITEM) return;
                var monitor = monitorHandler.access(player);
                if (monitor == null) return;
                monitor.nextMode();
            }
        });
    }

    /**
     * Listens to the block dig packet to track the
     * current digging state.
     */
    private void handleInvalidClicks() {
        this.protocolManager.addPacketListener(new PacketAdapter(
                this.plugin,
                ListenerPriority.NORMAL,
                PacketType.Play.Client.BLOCK_DIG
        ) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                var player = event.getPlayer();
                if (player.getGameMode().equals(GameMode.CREATIVE)) return;

                var uniqueId = player.getUniqueId();
                var packet = event.getPacket();
                var digType = packet.getPlayerDigTypes().read(0);

                switch (digType) {
                    case START_DESTROY_BLOCK -> {
                        var position = packet.getBlockPositionModifier().read(0);
                        var block = player.getWorld().getBlockAt(position.getX(), position.getY(), position.getZ());

                        // Ignore blocks that can be broken instantly
                        if (
                                block == null
                                || block.getType() == null
                                || INSTANT_BREAKABLE.contains(block.getType())
                        ) {
                            lastClickInvalid.add(uniqueId);
                            return;
                        }
                        isDigging.add(uniqueId);
                    }

                    case STOP_DESTROY_BLOCK, ABORT_DESTROY_BLOCK -> isDigging.remove(uniqueId);
                    default -> {}
                }
            }
        });
    }

    /**
     * Listens to the arm swing packet to register clicks.
     * <p>
     * <b>Note:</b> Counts clicks while digging as invalid clicks.
     */
    private void handleClicks() {
        this.protocolManager.addPacketListener(new PacketAdapter(
                this.plugin,
                ListenerPriority.NORMAL,
                PacketType.Play.Client.ARM_ANIMATION
        ) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                var uniqueId = event.getPlayer().getUniqueId();
                clickHandler.registerClick(
                        uniqueId,
                        lastClickInvalid.contains(uniqueId) || isDigging.contains(uniqueId)
                );
                lastClickInvalid.remove(uniqueId);
            }
        });
    }

    /**
     * Listens to the use entity packet to register attacks.
     * <p>
     * <b>Note:</b> This is used to track attacks separately
     * from regular clicks.
     */
    private void handleAttacks() {
        this.protocolManager.addPacketListener(new PacketAdapter(
                this.plugin,
                ListenerPriority.NORMAL,
                PacketType.Play.Client.USE_ENTITY
        ) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                var action = event.getPacket().getEntityUseActions().read(0);
                if (action != EnumWrappers.EntityUseAction.ATTACK) return;

                var uniqueId = event.getPlayer().getUniqueId();
                clickHandler.registerAttack(uniqueId);
            }
        });
    }

    /**
     * {@link Set} of {@link Material Materials} that can be broken instantly.
     */
    private static final Set<Material> INSTANT_BREAKABLE = Set.of(
            Material.SAPLING,
            Material.LONG_GRASS,
            Material.DEAD_BUSH,
            Material.YELLOW_FLOWER,
            Material.RED_ROSE,
            Material.BROWN_MUSHROOM,
            Material.RED_MUSHROOM,
            Material.TORCH,
            Material.WATER_LILY,
            Material.DOUBLE_PLANT,
            Material.LEVER,
            Material.REDSTONE,
            Material.REDSTONE_TORCH_OFF,
            Material.REDSTONE_TORCH_ON,
            Material.TRIPWIRE_HOOK,
            Material.TRIPWIRE,
            Material.DIODE_BLOCK_OFF,
            Material.DIODE_BLOCK_ON,
            Material.DIODE,
            Material.REDSTONE_COMPARATOR_OFF,
            Material.REDSTONE_COMPARATOR_ON,
            Material.REDSTONE_COMPARATOR
    );

}
