package de.marvin.cps.protocol.v1_9.packetlistener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.marvin.cps.api.click.ClickHandler;
import de.marvin.cps.api.protocol.packetlistener.ClickListener;
import de.marvin.cps.core.CPSChecker;
import de.marvin.cps.core.click.ClickType;
import de.marvin.cps.core.util.MaterialUtil;
import de.marvin.cps.core.util.SchedulerUtil;
import de.marvin.cps.protocol.v1_9.VMaterialUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Handles registration of clicks to the {@link ClickHandler}.
 */
@Singleton
public class ClickListenerImpl implements ClickListener, Listener {

    /**
     * The threshold in milliseconds for considering a right click on the ground as valid.
     */
    private static final long RIGHT_CLICK_GROUND_THRESHOLD = 15L;

    private final @NotNull ClickHandler clickHandler;

    /**
     * Tracks the {@link UUID UUIDs} of players that are currently digging.
     */
    private final @NotNull Set<UUID> isDigging = Sets.newConcurrentHashSet();
    /**
     * Tracks the {@link UUID UUIDs} of players that have right-clicked on the ground and the system time
     * in milliseconds of the last right click.
     */
    private final @NotNull Map<UUID, Long> rightClickedGround = Maps.newConcurrentMap();

    @Inject
    public ClickListenerImpl(
            @NotNull ClickHandler clickHandler
    ) {
        this.clickHandler = clickHandler;
    }

    /**
     * Filters incoming packets and delegates them accordingly.
     *
     * @param event {@link PacketEvent} that has been received
     */
    @Override
    public void onPacketReceiving(
            PacketEvent event
    ) {
        var type = event.getPacketType();

        if (type.equals(PacketType.Play.Client.BLOCK_DIG)) this.handleInvalidClick(event);
        if (type.equals(PacketType.Play.Client.ARM_ANIMATION)) this.handleLeftClick(event);
        if (type.equals(PacketType.Play.Client.USE_ENTITY)) this.handleAttack(event);
        if (type.equals(PacketType.Play.Client.USE_ITEM)) this.handleUseItem(event);
        if (type.equals(PacketType.Play.Client.USE_ITEM_ON)) this.handleUseItemOn(event);
    }

    /**
     * Tracks a player's current digging state.
     *
     * @param event {@link PacketEvent} of type {@link PacketType.Play.Client#BLOCK_DIG} that has been
     *              received
     */
    private void handleInvalidClick(
            @NotNull PacketEvent event
    ) {
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
                    this.clickHandler.registerClick(
                            uniqueId,
                            ClickType.INVALID_LEFT_CLICK
                    );
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
     * @param event {@link PacketEvent} of type {@link PacketType.Play.Client#ARM_ANIMATION} that has been
     *              received
     */
    private void handleLeftClick(
            @NotNull PacketEvent event
    ) {
        var uniqueId = event.getPlayer().getUniqueId();
        this.clickHandler.registerClick(
                uniqueId,
                ClickType.LEFT_CLICK
        );
        if (this.isDigging.contains(uniqueId))
            this.clickHandler.registerClick(
                    uniqueId,
                    ClickType.INVALID_LEFT_CLICK
            );
    }

    /**
     * Handles attack registration.
     *
     * @param event {@link PacketEvent} of type {@link PacketType.Play.Client#USE_ENTITY} that has been
     *              received
     */
    private void handleAttack(
            @NotNull PacketEvent event
    ) {
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
     * @param event {@link PacketEvent} of type {@link PacketType.Play.Client#USE_ITEM} that has been
     *              received
     */
    private void handleUseItem(
            @NotNull PacketEvent event
    ) {
        var player = event.getPlayer();
        var uniqueId = player.getUniqueId();
        var hand = event.getPacket().getHands().read(0);

        var itemInMainHand = player.getInventory().getItemInMainHand();
        if (itemInMainHand == null) itemInMainHand = new ItemStack(Material.AIR);

        var itemInOffHand = player.getInventory().getItemInOffHand();
        if (itemInOffHand == null) itemInOffHand = new ItemStack(Material.AIR);

        var current = hand == EnumWrappers.Hand.MAIN_HAND ? itemInMainHand : itemInOffHand;

        var pos = event.getPacket().getBlockPositionModifier().read(0);
        var world = player.getWorld();
        var block = pos.getY() >= 0 && pos.getY() < world.getMaxHeight()
                && world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)
                ? world.getBlockAt(pos.getX(), pos.getY(), pos.getZ()) // not thread safe
                : null;

        // Clicked block is an interactable block
        if (block != null && VMaterialUtil.isInteractable(block.getType())) {
            if (player.isSneaking() && !VMaterialUtil.isPlaceable(current.getType())) return;

            if (!(block.getType().equals(Material.FLOWER_POT) && !VMaterialUtil.canBePotted(current))
                    && !(player.isSneaking() && VMaterialUtil.isPlaceable(current.getType()))) {
                this.clickHandler.registerClick(uniqueId, ClickType.RIGHT_CLICK);
                this.clickHandler.registerClick(uniqueId, ClickType.INVALID_LEFT_CLICK);
                return;
            }
        }

        // No item in either hand
        if (itemInMainHand.getType().equals(Material.AIR) && itemInOffHand.getType().equals(Material.AIR)) {
            // Do not register click twice
            if (hand == EnumWrappers.Hand.OFF_HAND) return;
            this.clickHandler.registerClick(uniqueId, ClickType.RIGHT_CLICK);
            return;
        }

        // Do not register click twice
        if (current.getType().equals(Material.AIR)) return;

        // Track click on ground to prevent double registration in use item on packet
        var systemTime = System.currentTimeMillis();
        this.rightClickedGround.put(uniqueId, systemTime);

        // Item is not placeable -> process in use item on packet
        if (!VMaterialUtil.isPlaceable(current.getType())) {
            // Item cannot be placed but can spawn entity when aiming at a valid position
            if (!VMaterialUtil.isMinecart(current.getType())
                    && !VMaterialUtil.isBoneMeal(current)
                    && !current.getType().equals(Material.MONSTER_EGG)) return;
            this.clickHandler.registerClick(uniqueId, ClickType.INVALID_LEFT_CLICK);
            return;
        }

        // Item is placeable
        this.clickHandler.registerClick(uniqueId, ClickType.RIGHT_CLICK);

        // If clicked item is placeable non-block material, packet is called twice, once with
        // valid position and once without one, if player cannot place a block with the item.
        // As combining these two packets is the only way to determine if a block actually can
        // be placed, a right click in this case is, for reasons of inaccuracy, never registered.
        if (VMaterialUtil.isPlaceableNonBlock(current.getType())) {
            this.clickHandler.registerClick(uniqueId, ClickType.INVALID_LEFT_CLICK);
            return;
        }

        // If player actually places a block
        if (!current.getType().isBlock()) return;
        this.clickHandler.registerClick(uniqueId, ClickType.PLACEMENT);
        this.clickHandler.registerClick(uniqueId, ClickType.INVALID_LEFT_CLICK);
    }

    /**
     * Handles right click registration.
     *
     * @param event {@link PacketEvent} of type {@link PacketType.Play.Client#USE_ITEM_ON} that has been
     *              received
     */
    private void handleUseItemOn(
            @NotNull PacketEvent event
    ) {
        var player = event.getPlayer();
        var uniqueId = player.getUniqueId();

        var hand = event.getPacket().getHands().read(0);

        var itemInMainHand = player.getInventory().getItemInMainHand();
        if (itemInMainHand == null) itemInMainHand = new ItemStack(Material.AIR);

        var itemInOffHand = player.getInventory().getItemInOffHand();
        if (itemInOffHand == null) itemInOffHand = new ItemStack(Material.AIR);

        // Do not register click twice
        if (!itemInMainHand.getType().equals(Material.AIR) && !itemInOffHand.getType().equals(Material.AIR)
                && hand == EnumWrappers.Hand.OFF_HAND)
            return;

        var current = hand == EnumWrappers.Hand.MAIN_HAND ? itemInMainHand : itemInOffHand;

        // Player right-clicked with non-placeable item in hand
        if (!VMaterialUtil.isPlaceable(current.getType())) {
            if (current.getType().equals(Material.FISHING_ROD))
                this.clickHandler.registerClick(uniqueId, ClickType.INVALID_LEFT_CLICK);
            this.clickHandler.registerClick(uniqueId, ClickType.RIGHT_CLICK);
            return;
        }

        // Prevent double registration if right click was already processed in use item packet
        var systemTime = System.currentTimeMillis();
        var rightClickTime = this.rightClickedGround.get(uniqueId);
        if (rightClickTime != null) {
            this.rightClickedGround.remove(uniqueId);
            if ((systemTime - rightClickTime) <= RIGHT_CLICK_GROUND_THRESHOLD) return;
        }

        // Right-click with placeable item in hand
        this.clickHandler.registerClick(
                uniqueId,
                ClickType.RIGHT_CLICK
        );
    }

    @Override
    public void onPacketSending(
            PacketEvent event
    ) {
        // No implementation needed
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
                        PacketType.Play.Client.USE_ITEM,
                        PacketType.Play.Client.USE_ITEM_ON
                )
                .build();
    }

    @Override
    public Plugin getPlugin() {
        return CPSChecker.instance().javaPlugin();
    }

    /**
     * Returns the {@link Set} of {@link UUID UUIDs} that are currently digging.
     *
     * @return {@link Set} of {@link UUID UUIDs} that are currently digging
     */
    public @NotNull Set<UUID> isDigging() {
        return this.isDigging;
    }

    /**
     * Gets the {@link Map} of {@link UUID UUIDs} and system time in milliseconds of last right click on
     * ground.
     *
     * @return {@link Map} of {@link UUID UUIDs} and system time as {@link Long}
     */
    public @NotNull Map<UUID, Long> rightClickedGround() {
        return this.rightClickedGround;
    }

}
