package com.onarandombox.MultiverseCore;

public interface MVPlugin extends LoggablePlugin {
    public void dumpVersionInfo();
    public MultiverseCore getCore();
    public void setCore(MultiverseCore core);
}
