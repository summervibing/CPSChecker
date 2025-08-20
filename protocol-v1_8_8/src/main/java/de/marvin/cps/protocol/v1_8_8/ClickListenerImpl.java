package de.marvin.cps.protocol.v1_8_8;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.marvin.cps.api.click.ClickHandler;
import de.marvin.cps.api.protocol.ClickListener;
import de.marvin.cps.core.CPSChecker;
import de.marvin.cps.core.click.ClickType;
import de.marvin.cps.core.util.MaterialUtil;
import de.marvin.cps.core.util.SchedulerUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
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
        if (type.equals(PacketType.Play.Client.ARM_ANIMATION)) handleLeftClick(event);
        if (type.equals(PacketType.Play.Client.USE_ENTITY)) handleAttack(event);
        if (type.equals(PacketType.Play.Client.USE_ITEM_ON)) handleRightClick(event);
    }

    /**
     * Tracks a player's current digging state.
     *
     * @param event packet event of type {@link PacketType.Play.Client#BLOCK_DIG}
     *              that has been received
     */
    private void handleInvalidClick(PacketEvent event) {
        var player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) return;

        var uniqueId = player.getUniqueId();
        var packet = event.getPacket();
        var digType = packet.getPlayerDigTypes().read(0);
        var position = packet.getBlockPositionModifier().read(0);

        switch (digType) {
            case START_DESTROY_BLOCK -> {
                var block = player.getWorld().getBlockAt(position.getX(), position.getY(), position.getZ());
                // Ignore blocks that can be broken instantly
                if (MaterialUtil.isInstantBreakable(block.getType())) {
                    this.lastClickInvalid.add(uniqueId);
                    return;
                }
                this.isDigging.add(uniqueId);
            }

            case STOP_DESTROY_BLOCK -> {
                var block = player.getWorld().getBlockAt(position.getX(), position.getY(), position.getZ());
                if (!MaterialUtil.isInstantBreakable(block.getType()))
                    SchedulerUtil.delayAsync(() -> this.isDigging.remove(uniqueId), 6L);
            }
            case ABORT_DESTROY_BLOCK -> this.isDigging.remove(uniqueId);
            default -> {
            }
        }
    }

    /**
     * Handles left click registration.
     *
     * @param event packet event of type {@link PacketType.Play.Client#ARM_ANIMATION}
     *              that has been received
     */
    private void handleLeftClick(PacketEvent event) {
        var uniqueId = event.getPlayer().getUniqueId();
        this.clickHandler.registerClick(
                uniqueId,
                ClickType.LEFT_CLICK
        );
        if (this.lastClickInvalid.contains(uniqueId) || this.isDigging.contains(uniqueId))
            this.clickHandler.registerClick(
                    uniqueId,
                    ClickType.INVALID_LEFT_CLICK
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
        this.clickHandler.registerClick(
                uniqueId,
                ClickType.ATTACK
        );
    }

    /**
     * Handles right click registration.
     *
     * @param event packet event of type {@link PacketType.Play.Client#USE_ITEM_ON}
     *              that has been received
     */
    private void handleRightClick(PacketEvent event) {
        var player = event.getPlayer();
        var uniqueId = player.getUniqueId();
        var packet = event.getPacket();

        var pos = packet.getBlockPositionModifier().read(0);

        var item = packet.getItemModifier().read(0);
        if (item == null) item = new ItemStack(Material.AIR);
        var itemType = item.getType();

        // Check if aiming at a position where a block could be placed
        var isValidPosition = !(pos.getX() == -1 && pos.getY() == -1 && pos.getZ() == -1);

        var world = player.getWorld();
        var block = isValidPosition
                && pos.getY() >= 0 && pos.getY() < world.getMaxHeight()
                && world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)
                ? player.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()) // not thread safe
                : null;

        // Clicked block is an interactable block
        if (block != null && VMaterialUtil.isInteractable(block.getType())) {
            if (player.isSneaking() && !VMaterialUtil.isPlaceable(itemType)) return;

            if (!(block.getType().equals(Material.FLOWER_POT) && !VMaterialUtil.canBePotted(item))
                    && !(player.isSneaking() && VMaterialUtil.isPlaceable(itemType))) {
                this.clickHandler.registerClick(uniqueId, ClickType.RIGHT_CLICK);
                this.clickHandler.registerClick(uniqueId, ClickType.INVALID_LEFT_CLICK);
                return;
            }
        }

        // No item in hand
        if (itemType.equals(Material.AIR)) {
            // If pottable item is placed in a flower pot, minecraft calls packet too late
            // causing item type being Material#AIR when only having one item in the hand
            // which leads to an invalid left click
            if (block != null && block.getType().equals(Material.FLOWER_POT))
                this.clickHandler.registerClick(uniqueId, ClickType.INVALID_LEFT_CLICK);
            this.clickHandler.registerClick(uniqueId, ClickType.RIGHT_CLICK);
            return;
        }

        // Item is not placeable
        if (!VMaterialUtil.isPlaceable(itemType)) {
            // To prevent double registration, as the packet in this case is called two times,
            // with and without a valid block position
            if (isValidPosition) {
                // Item cannot be placed but can spawn entity when aiming at a valid position
                if (!VMaterialUtil.isMinecart(itemType)
                        && !VMaterialUtil.isBoneMeal(item)
                        && !itemType.equals(Material.MONSTER_EGG)) return;
                this.clickHandler.registerClick(uniqueId, ClickType.INVALID_LEFT_CLICK);
                return;
            }
            if (itemType.equals(Material.FISHING_ROD))
                this.clickHandler.registerClick(uniqueId, ClickType.INVALID_LEFT_CLICK);
            this.clickHandler.registerClick(uniqueId, ClickType.RIGHT_CLICK);
            return;
        }

        // If player in adventure mode, only register a right click if position is invalid,
        // as packet is called twice if they could potentially place one, once with a valid
        // position and once without one
        if (isValidPosition && player.getGameMode() == GameMode.ADVENTURE) return;

        // If clicked item is placeable non-block material, packet is called twice, once with
        // valid position and once without one, if player cannot place a block with the item.
        // As combining these two packets is the only way to determine if a block actually can
        // be placed, a right click in this case is, for reasons of inaccuracy, never registered.
        if (VMaterialUtil.isPlaceableNonBlock(itemType) && isValidPosition) {
            this.clickHandler.registerClick(uniqueId, ClickType.INVALID_LEFT_CLICK);
            return;
        }

        // Block is placeable
        this.clickHandler.registerClick(uniqueId, ClickType.RIGHT_CLICK);

        // If player actually places a block
        if (!isValidPosition) return;
        this.clickHandler.registerClick(uniqueId, ClickType.PLACEMENT);
        this.clickHandler.registerClick(uniqueId, ClickType.INVALID_LEFT_CLICK);
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
                        PacketType.Play.Client.USE_ENTITY,
                        PacketType.Play.Client.USE_ITEM_ON
                )
                .build();
    }

    @Override
    public Plugin getPlugin() {
        return CPSChecker.instance().javaPlugin();
    }

}
