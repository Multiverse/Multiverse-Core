package org.mvplugins.multiverse.core.locale.message;

import co.aikar.commands.CommandIssuer;
import org.jetbrains.annotations.Nullable;
import org.mvplugins.multiverse.core.locale.PluginLocales;

/**
 * Contains a localizable {@link Message}.
 */
public interface LocalizableMessage {
    /**
     * Gets the localizable message for this object.
     * <br/>
     * This can be null if the object does not have a localizable message.
     * <br/>
     * The returned message will be localized with the {@link PluginLocales} provided by the {@link CommandIssuer}
     * that called the method which returned this object.
     *
     * @return The localizable message, or null if none exists.
     */
    @Nullable Message getLocalizableMessage();
}
