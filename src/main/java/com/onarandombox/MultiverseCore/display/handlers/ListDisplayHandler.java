package com.onarandombox.MultiverseCore.display.handlers;

import com.onarandombox.MultiverseCore.display.ContentDisplay;
import com.onarandombox.MultiverseCore.display.DisplayFormatException;
import com.onarandombox.MultiverseCore.display.DisplayHandler;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.stream.Collectors;

public class ListDisplayHandler implements DisplayHandler<Collection<String>> {

    @Override
    public Collection<String> format(@NotNull CommandSender sender, @NotNull ContentDisplay<Collection<String>> display)
            throws DisplayFormatException {
        return display.getContents().stream()
                .filter(display.getFilter()::checkMatch)
                .map(s -> (ContentDisplay.LINE_BREAK.equals(s)) ? "" : display.getColorTool().get() + s)
                .collect(Collectors.toList());
    }
}
