package de.marvin.cps.protocol.v1_8_8;

import com.google.inject.AbstractModule;
import de.marvin.cps.api.protocol.ProtocolAdapter;
import de.marvin.cps.api.protocol.ClickListener;
import de.marvin.cps.api.protocol.MonitorListener;

public class ProtocolModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ProtocolAdapter.class).to(ProtocolAdapterImpl.class);
        bind(ClickListener.class).to(ClickListenerImpl.class);
        bind(MonitorListener.class).to(MonitorListenerImpl.class);
    }
}
