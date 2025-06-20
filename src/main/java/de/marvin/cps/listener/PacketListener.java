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
import de.marvin.cps.util.MaterialUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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

        this.handleMonitorSwitch();
        this.preventOtherActionbars();

        this.handleInvalidClicks();
        this.handleClicks();
        this.handleAttacks();
    }

    /**
     * Listens to item drop packet to switch the current
     * {@link de.marvin.cps.monitor.MonitorMode}.
     */
    private void handleMonitorSwitch() {
        this.protocolManager.addPacketListener(new PacketAdapter(
                this.plugin,
                ListenerPriority.NORMAL,
                PacketType.Play.Client.BLOCK_DIG
        ) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                var player = event.getPlayer();
                var digType = event.getPacket().getPlayerDigTypes().read(0);

                if (digType != EnumWrappers.PlayerDigType.DROP_ITEM) return;
                if (player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR) return;
                monitorHandler.nextMode(player);
            }
        });
    }

    /**
     * Prevents other action bars from being displayed
     * while monitoring a player.
     */
    private void preventOtherActionbars() {
        this.protocolManager.addPacketListener(new PacketAdapter(
                this.plugin,
                ListenerPriority.NORMAL,
                PacketType.Play.Server.CHAT,                    // 1.8 – 1.19.3
                PacketType.Play.Server.SYSTEM_CHAT,             // 1.19.4+
                PacketType.Play.Server.SET_ACTION_BAR_TEXT,     // 1.19.4+
                PacketType.Play.Server.TITLE                    // title api in newer versions
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (!monitorHandler.isMonitoring(event.getPlayer())) return;

                var packetContainer = event.getPacket();
                var type = packetContainer.getType();

                // ===== 1.8 – 1.11 =====
                if (packetContainer.getBytes().size() > 0 && packetContainer.getBytes().read(0) == (byte) 2) {
                    event.setCancelled(true);
                    return;
                }

                // ===== 1.12 – 1.19.3 =====
                if (packetContainer.getChatTypes().size() > 0
                        && packetContainer.getChatTypes().read(0) == EnumWrappers.ChatType.GAME_INFO) {
                    event.setCancelled(true);
                    return;
                }

                // ===== 1.19.4 + =====
                // bool 0 = overlay?, action bars are true
                if (type == PacketType.Play.Server.SYSTEM_CHAT && packetContainer.getBooleans().read(0)) {
                    event.setCancelled(true);
                    return;
                }

                if (type == PacketType.Play.Server.SET_ACTION_BAR_TEXT) {
                    event.setCancelled(true);
                    return;
                }

                // ===== Titel-API (all newer versions) =====
                if (type == PacketType.Play.Server.TITLE &&
                        packetContainer.getTitleActions().read(0) == EnumWrappers.TitleAction.ACTIONBAR) {
                    event.setCancelled(true);
                }
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
                        if (MaterialUtil.isInstantBreakable(block.getType())) {
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

}
