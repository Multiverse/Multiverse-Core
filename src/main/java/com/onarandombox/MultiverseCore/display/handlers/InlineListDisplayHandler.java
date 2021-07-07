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

public class InlineListDisplayHandler implements DisplayHandler<Collection<String>> {

    @Override
    public Collection<String> format(@NotNull CommandSender sender, @NotNull ContentDisplay<Collection<String>> display)
            throws DisplayFormatException {
        StringBuilder builder = new StringBuilder();
        String separator = display.getSetting(DisplaySettings.SEPARATOR);

        for (Iterator<String> iterator = display.getContents().iterator(); iterator.hasNext(); ) {
            String content = iterator.next();
            if (!display.getFilter().checkMatch(content)) {
                continue;
            }
            builder.append(display.getColorTool().get()).append(content);
            if (iterator.hasNext()) {
                builder.append(separator);
            }
        }
        return (builder.length() == 0)
                ? Collections.singletonList(display.getEmptyMessage())
                : Collections.singleton(builder.toString());
    }
}
