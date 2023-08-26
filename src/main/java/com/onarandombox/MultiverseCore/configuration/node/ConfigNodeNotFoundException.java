package com.onarandombox.MultiverseCore.configuration.node;

import com.onarandombox.MultiverseCore.exceptions.MultiverseException;
import org.jetbrains.annotations.Nullable;

import static com.onarandombox.MultiverseCore.utils.MVCorei18n.CONFIG_NODE_NOTFOUND;
import static com.onarandombox.MultiverseCore.utils.message.MessageReplacement.replace;

public class ConfigNodeNotFoundException extends MultiverseException {

    public ConfigNodeNotFoundException(@Nullable String nodeName) {
        super(CONFIG_NODE_NOTFOUND.bundle("Config node not found: {node}", replace("{node}").with(nodeName)), null);
    }
}