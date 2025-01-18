package org.mvplugins.multiverse.core.configuration.node;

import org.jetbrains.annotations.Nullable;

import org.mvplugins.multiverse.core.api.exceptions.MultiverseException;

import static org.mvplugins.multiverse.core.api.locale.MVCorei18n.CONFIG_NODE_NOTFOUND;
import static org.mvplugins.multiverse.core.api.locale.message.MessageReplacement.replace;

public class ConfigNodeNotFoundException extends MultiverseException {

    public ConfigNodeNotFoundException(@Nullable String nodeName) {
        super(CONFIG_NODE_NOTFOUND.bundle("Config node not found: {node}", replace("{node}").with(nodeName)), null);
    }
}
