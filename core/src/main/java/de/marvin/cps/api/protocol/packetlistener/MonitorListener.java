package de.marvin.cps.api.protocol.packetlistener;

import com.comphenix.protocol.events.PacketListener;

/**
 * Handles monitor switching by listening to
 * item drop packets and preventing other plugins
 * from sending action bar messages to a player
 * who currently is monitoring another player.
 */
public interface MonitorListener extends PacketListener {

}
