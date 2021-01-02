package com.onarandombox.MultiverseCore.commandTools.display.inline;

import com.onarandombox.MultiverseCore.commandTools.display.ColourAlternator;
import com.onarandombox.MultiverseCore.commandTools.display.ContentCreator;
import com.onarandombox.MultiverseCore.commandTools.display.ContentDisplay;
import com.onarandombox.MultiverseCore.commandTools.display.ContentFilter;
import com.onarandombox.MultiverseCore.commandTools.display.ShowRunnable;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ListDisplay extends ContentDisplay<List<String>> {
    public ListDisplay(@NotNull Plugin plugin,
                       @NotNull CommandSender sender,
                       @Nullable String header,
                       @NotNull ContentCreator<List<String>> creator,
                       @NotNull ContentFilter filter,
                       @Nullable ColourAlternator colours) {

        super(plugin, sender, header, creator, filter, colours);
    }

    @Override
    public @NotNull ShowRunnable<ListDisplay, List<String>> getShowRunnable() {
        return new ShowList(this);
    }
}
