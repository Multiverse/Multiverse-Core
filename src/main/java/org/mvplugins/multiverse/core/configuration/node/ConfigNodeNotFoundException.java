package org.mvplugins.multiverse.core.configuration.node;

import static org.mvplugins.multiverse.core.utils.MVCorei18n.CONFIG_NODE_NOTFOUND;
import static org.mvplugins.multiverse.core.utils.message.MessageReplacement.replace;

import org.jetbrains.annotations.Nullable;
import org.mvplugins.multiverse.core.exceptions.MultiverseException;

public class ConfigNodeNotFoundException extends MultiverseException {

    public ConfigNodeNotFoundException(@Nullable String nodeName) {
        super(CONFIG_NODE_NOTFOUND.bundle("Config node not found: {node}", replace("{node}").with(nodeName)), null);
    }
}
