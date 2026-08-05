package de.marvin.cps.core.config;

import de.marvin.cps.core.message.Message;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * This class is responsible for loading and managing messages from the "messages.yml" configuration file.
 */
public class MessageConfig extends AbstractConfig {

    public MessageConfig(
            @NotNull JavaPlugin plugin
    ) {
        super(plugin, "messages.yml");
    }

    /**
     * Returns given {@link Message} from the config file. If the message is not found, it returns
     * {@link Message#defaultMessage()}.
     *
     * @param message {@link Message} to retrieve
     * @return {@link Message} from {@link MessageConfig} if found, otherwise returns
     * {@link Message#defaultMessage()}
     */
    public String message(
            @NotNull Message message
    ) {
        return this.file().getString(message.path(), message.defaultMessage());
    }

    /**
     * Loads all default messages into the config file if they are not already present.
     */
    @Override
    public void loadDefaultValues() {
        for (var message : Message.values()) this.file().addDefault(message.path(), message.defaultMessage());
        this.file().options().copyDefaults(true);
    }

}
