package de.marvin.cps.protocol.v1_9;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.inject.Singleton;
import de.marvin.cps.api.protocol.ProtocolAdapter;
import de.marvin.cps.core.protocol.MinecraftVersion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class ProtocolAdapterImpl implements ProtocolAdapter {

    private final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

    @Override
    public void sendActionBarMessage(
            @NotNull final Player bukkitPlayer,
            @NotNull final String message
    ) {
        var rawMessage = "{\"text\": \"" + message + "\"}";
        var packet = this.protocolManager.createPacket(PacketType.Play.Server.CHAT);
        packet.getChatComponents().write(0, WrappedChatComponent.fromJson(rawMessage));
        packet.getChatTypes().writeSafely(0, EnumWrappers.ChatType.GAME_INFO);
        packet.getBytes().writeSafely(0, (byte) 2);
        this.protocolManager.sendServerPacket(bukkitPlayer, packet, false);
    }

    /**
     * {@inheritDoc}
     *
     * @return The {@link MinecraftVersion} of the current implementation.
     */
    @Override
    public MinecraftVersion version() {
        return MinecraftVersion.v1_9;
    }

}
