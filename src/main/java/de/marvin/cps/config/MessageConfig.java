package de.marvin.cps.config;

import de.marvin.cps.message.Message;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class MessageConfig extends AbstractConfig {

    public MessageConfig(
            @NotNull final JavaPlugin plugin
    ) {
        super(plugin, "messages.yml");
    }

    /**
     * Gets given {@link Message} from the config file.
     * If the message is not found, it returns {@link Message#defaultMessage()}.
     *
     * @param message {@link Message} to retrieve
     * @return {@link Message} from {@link MessageConfig} if found,
     * otherwise returns {@link Message#defaultMessage()}.
     */
    public String message(
            @NotNull final Message message
    ) {
        return this.file().getString(message.path(), message.defaultMessage());
    }

    /**
     * Loads all default messages into the config file
     * if they are not already present.
     */
    @Override
    public void loadDefaultValues() {
        for (Message message : Message.values()) this.file().addDefault(message.path(), message.defaultMessage());
        this.file().options().copyDefaults(true);
    }

}
