/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore;

public interface MVPlugin extends LoggablePlugin {
    public String dumpVersionInfo(String buffer);

    public MultiverseCore getCore();

    public void setCore(MultiverseCore core);
}
