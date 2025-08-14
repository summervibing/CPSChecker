package de.marvin.cps.protocol.v1_8_8;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.marvin.cps.core.CPSChecker;
import de.marvin.cps.api.click.ClickHandler;
import de.marvin.cps.api.protocol.ClickListener;
import de.marvin.cps.core.util.MaterialUtil;
import de.marvin.cps.core.util.SchedulerUtil;
import org.bukkit.GameMode;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Singleton
public class ClickListenerImpl implements ClickListener {

    private final ClickHandler clickHandler;

    private final Set<UUID> isDigging = new HashSet<>();
    private final Set<UUID> lastClickInvalid = new HashSet<>();

    @Inject
    public ClickListenerImpl(
            @NotNull final ClickHandler clickHandler
    ) {
        this.clickHandler = clickHandler;
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        var type = event.getPacketType();

        if (type.equals(PacketType.Play.Client.BLOCK_DIG)) handleInvalidClick(event);
        if (type.equals(PacketType.Play.Client.ARM_ANIMATION)) handleClick(event);
        if (type.equals(PacketType.Play.Client.USE_ENTITY)) handleAttack(event);
    }

    /**
     * Tracks a player's current digging state.
     *
     * @param event packet event of type {@link PacketType.Play.Client#BLOCK_DIG}
     *              that has been received
     */
    private void handleInvalidClick(PacketEvent event) {
        var player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;

        var uniqueId = player.getUniqueId();
        var packet = event.getPacket();
        var digType = packet.getPlayerDigTypes().read(0);
        var position = packet.getBlockPositionModifier().read(0);
        var block = player.getWorld().getBlockAt(position.getX(), position.getY(), position.getZ());

        switch (digType) {
            case START_DESTROY_BLOCK -> {
                // Ignore blocks that can be broken instantly
                if (MaterialUtil.isInstantBreakable(block.getType())) {
                    this.lastClickInvalid.add(uniqueId);
                    return;
                }
                this.isDigging.add(uniqueId);
            }

            case STOP_DESTROY_BLOCK -> {
                if (!MaterialUtil.isInstantBreakable(block.getType()))
                    SchedulerUtil.delayAsync(() -> this.isDigging.remove(uniqueId), 6L);
            }
            case ABORT_DESTROY_BLOCK -> this.isDigging.remove(uniqueId);
            default -> {}
        }
    }

    /**
     * Handles click registration.
     *
     * @param event packet event of type {@link PacketType.Play.Client#ARM_ANIMATION}
     *              that has been received
     */
    private void handleClick(PacketEvent event) {
        var uniqueId = event.getPlayer().getUniqueId();
        this.clickHandler.registerClick(
                uniqueId,
                this.lastClickInvalid.contains(uniqueId)
                        || this.isDigging.contains(uniqueId)
        );
        this.lastClickInvalid.remove(uniqueId);
    }

    /**
     * Handles attack registration.
     *
     * @param event packet event of type {@link PacketType.Play.Client#USE_ENTITY}
     *              that has been received
     */
    private void handleAttack(PacketEvent event) {
        var action = event.getPacket().getEntityUseActions().read(0);
        if (action != EnumWrappers.EntityUseAction.ATTACK) return;

        var uniqueId = event.getPlayer().getUniqueId();
        this.clickHandler.registerAttack(uniqueId);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        // no implementation needed
    }

    @Override
    public ListeningWhitelist getSendingWhitelist() {
        return ListeningWhitelist.EMPTY_WHITELIST;
    }

    @Override
    public ListeningWhitelist getReceivingWhitelist() {
        return ListeningWhitelist.newBuilder().priority(ListenerPriority.NORMAL)
                .types(
                        PacketType.Play.Client.BLOCK_DIG,
                        PacketType.Play.Client.ARM_ANIMATION,
                        PacketType.Play.Client.USE_ENTITY
                )
                .build();
    }

    @Override
    public Plugin getPlugin() {
        return CPSChecker.instance().javaPlugin();
    }

}
