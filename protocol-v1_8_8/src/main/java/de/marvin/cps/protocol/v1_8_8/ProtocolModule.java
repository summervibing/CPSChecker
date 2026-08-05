package de.marvin.cps.protocol.v1_8_8;

import com.google.inject.AbstractModule;
import de.marvin.cps.api.protocol.BukkitListenerRegistry;
import de.marvin.cps.api.protocol.ProtocolAdapter;
import de.marvin.cps.api.protocol.packetlistener.ClickListener;
import de.marvin.cps.api.protocol.packetlistener.MonitorListener;
import de.marvin.cps.protocol.v1_8_8.packetlistener.ClickListenerImpl;
import de.marvin.cps.protocol.v1_8_8.packetlistener.MonitorListenerImpl;

/**
 * Binds the protocol adapter and packet listeners for the 1.8.8 version of the protocol.
 */
public class ProtocolModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ProtocolAdapter.class).to(ProtocolAdapterImpl.class);
        bind(ClickListener.class).to(ClickListenerImpl.class);
        bind(MonitorListener.class).to(MonitorListenerImpl.class);
        bind(BukkitListenerRegistry.class).to(BukkitListenerRegistryImpl.class);
    }

}
