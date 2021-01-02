package com.onarandombox.MultiverseCore.commandTools.display.inline;

import com.onarandombox.MultiverseCore.commandTools.display.ContentDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ListDisplay extends ContentDisplay<ListDisplay, List<String>> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected @NotNull ShowList getShowRunnable() {
        return new ShowList(this);
    }
}