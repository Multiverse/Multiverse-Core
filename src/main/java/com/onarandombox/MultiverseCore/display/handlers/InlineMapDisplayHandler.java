package com.onarandombox.MultiverseCore.display.handlers;

import com.onarandombox.MultiverseCore.display.ContentDisplay;
import com.onarandombox.MultiverseCore.display.DisplayFormatException;
import com.onarandombox.MultiverseCore.display.DisplayHandler;
import com.onarandombox.MultiverseCore.display.DisplaySettings;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

public class InlineMapDisplayHandler implements DisplayHandler<Map<String, Object>> {

    @Override
    public Collection<String> format(@NotNull CommandSender sender,
                                     @NotNull ContentDisplay<Map<String, Object>> display)
            throws DisplayFormatException {
        StringBuilder builder = new StringBuilder();
        String separator = display.getSetting(DisplaySettings.SEPARATOR);
        String operator = display.getSetting(DisplaySettings.OPERATOR);

        for (Iterator<Map.Entry<String, Object>> iterator = display.getContents().entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Object> entry = iterator.next();
            if (!display.getFilter().checkMatch(entry.getKey()) && !display.getFilter().checkMatch(entry.getValue())) {
                continue;
            }
            builder.append(display.getColorTool().get())
                    .append(entry.getKey())
                    .append(operator)
                    .append(display.getColorTool().get())
                    .append(entry.getValue());
            if (iterator.hasNext()) {
                builder.append(separator);
            }
        }
        return (builder.length() == 0)
                ? Collections.singletonList(display.getEmptyMessage())
                : Collections.singleton(builder.toString());
    }
}
