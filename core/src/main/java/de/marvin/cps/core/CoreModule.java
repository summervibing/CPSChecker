package de.marvin.cps.core;

import com.google.inject.AbstractModule;
import de.marvin.cps.api.click.ClickHandler;
import de.marvin.cps.api.monitor.MonitorHandler;
import de.marvin.cps.api.user.UserHandler;
import de.marvin.cps.core.click.ClickHandlerImpl;
import de.marvin.cps.core.monitor.MonitorHandlerImpl;
import de.marvin.cps.core.user.UserHandlerImpl;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class CoreModule extends AbstractModule {

    private final JavaPlugin plugin;

    public CoreModule(
            @NotNull final JavaPlugin plugin
    ) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        bind(UserHandler.class).to(UserHandlerImpl.class);
        bind(ClickHandler.class).to(ClickHandlerImpl.class);
        bind(MonitorHandler.class).to(MonitorHandlerImpl.class);
        bind(JavaPlugin.class).toInstance(this.plugin);
    }

}
