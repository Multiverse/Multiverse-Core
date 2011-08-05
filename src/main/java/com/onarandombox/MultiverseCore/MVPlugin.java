package com.onarandombox.MultiverseCore;

public interface MVPlugin extends LoggablePlugin {
    public String dumpVersionInfo(String buffer);
    public MultiverseCore getCore();
    public void setCore(MultiverseCore core);
}
