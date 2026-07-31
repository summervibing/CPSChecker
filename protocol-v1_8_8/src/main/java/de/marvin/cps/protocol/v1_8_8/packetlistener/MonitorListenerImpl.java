package de.marvin.cps.protocol.v1_8_8.packetlistener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.marvin.cps.core.CPSChecker;
import de.marvin.cps.api.monitor.MonitorHandler;
import de.marvin.cps.api.protocol.packetlistener.MonitorListener;
import de.marvin.cps.core.pattern.PatternType;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

@Singleton
public class MonitorListenerImpl implements MonitorListener {

    private final MonitorHandler monitorHandler;

    @Inject
    public MonitorListenerImpl(MonitorHandler monitorHandler) {
        this.monitorHandler = monitorHandler;
    }

    /**
     * Prevents other action bars from being displayed
     * while monitoring a player.
     *
     * @param event packet that should be sent
     */
    @Override
    public void onPacketSending(PacketEvent event) {
        if (!event.getPacketType().equals(PacketType.Play.Server.CHAT)) return;
        if (!this.monitorHandler.isMonitoring(event.getPlayer())) return;

        var packetContainer = event.getPacket();
        if (packetContainer.getBytes().size() <= 0 || packetContainer.getBytes().read(0) != (byte) 2) return;
        event.setCancelled(true);
    }

    /**
     * Listens to item drop packet to switch the current
     * {@link PatternType}.
     *
     * @param event packet that has been received
     */
    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (!event.getPacketType().equals(PacketType.Play.Client.BLOCK_DIG)) return;
        var player = event.getPlayer();
        var digType = event.getPacket().getPlayerDigTypes().read(0);

        if (digType != EnumWrappers.PlayerDigType.DROP_ITEM) return;
        if (player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR) return;
        if (player.isSneaking()) {
            this.monitorHandler.nextPatternType(player);
            return;
        }
        this.monitorHandler.nextClickType(player);
    }

    @Override
    public ListeningWhitelist getSendingWhitelist() {
        return ListeningWhitelist.newBuilder()
                .priority(ListenerPriority.HIGHEST)
                .types(PacketType.Play.Server.CHAT)
                .build();
    }

    @Override
    public ListeningWhitelist getReceivingWhitelist() {
        return ListeningWhitelist.newBuilder()
                .priority(ListenerPriority.NORMAL)
                .types(PacketType.Play.Client.BLOCK_DIG)
                .build();
    }

    @Override
    public Plugin getPlugin() {
        return CPSChecker.instance().javaPlugin();
    }

}
