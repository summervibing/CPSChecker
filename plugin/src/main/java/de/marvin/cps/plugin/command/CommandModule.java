package de.marvin.cps.plugin.command;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import de.marvin.cps.plugin.command.subcommands.*;

/**
 * Binds all {@link CPSSubCommand CPSSubCommands} for injection.
 */
public class CommandModule extends AbstractModule {

    @Override
    protected void configure() {
        var subCommands = MapBinder.newMapBinder(
                binder(),
                String.class,
                CPSSubCommand.class
        );

        subCommands.addBinding("help").to(HelpSubCommand.class);
        subCommands.addBinding("list").to(ListSubCommand.class);
        subCommands.addBinding("off").to(OffSubCommand.class);
        subCommands.addBinding("start").to(StartSubCommand.class);
        subCommands.addBinding("stop").to(StopSubCommand.class);
    }

}
