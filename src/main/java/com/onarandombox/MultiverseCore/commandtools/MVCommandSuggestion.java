package com.onarandombox.MultiverseCore.commandtools;

import co.aikar.commands.PaperCommandCompletions;

/**
 * Class to handle tab-complete suggestions.
 */
public class MVCommandSuggestion extends PaperCommandCompletions {

    public MVCommandSuggestion(MVCommandManager manager) {
        super(manager);
    }
}
