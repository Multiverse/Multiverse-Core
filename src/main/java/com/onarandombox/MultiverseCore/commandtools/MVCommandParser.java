package com.onarandombox.MultiverseCore.commandtools;

import co.aikar.commands.PaperCommandContexts;

/**
 * Class to parse command arguments into its object and validate them.
 */
public class MVCommandParser extends PaperCommandContexts {

    public MVCommandParser(MVCommandManager manager) {
        super(manager);
    }
}
