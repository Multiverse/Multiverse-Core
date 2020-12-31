package com.onarandombox.MultiverseCore.commandTools.display.kvpair;

import com.onarandombox.MultiverseCore.commandTools.display.ColourAlternator;
import com.onarandombox.MultiverseCore.commandTools.display.ContentCreator;
import com.onarandombox.MultiverseCore.commandTools.display.ContentDisplay;
import com.onarandombox.MultiverseCore.commandTools.display.ContentFilter;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Used to display config/property values pair, each separated with a comma.
 */
public class KeyValueDisplay extends ContentDisplay<Map<String, Object>> {

    private final String operator;

    public KeyValueDisplay(@NotNull Plugin plugin,
                           @NotNull CommandSender sender,
                           @Nullable String header,
                           @NotNull ContentCreator<Map<String, Object>> creator,
                           @NotNull ContentFilter filter,
                           @Nullable ColourAlternator colours,
                           @NotNull String operator) {

        super(plugin, sender, header, creator, filter, colours);
        this.operator = operator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShowKeyValue getShowRunnable() {
        return new ShowKeyValue(this);
    }

    public String getOperator() {
        return operator;
    }
}
