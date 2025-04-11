package co.aikar.commands;

import org.bukkit.plugin.Plugin;

//TODO: Remove once https://github.com/aikar/commands/pull/429 is merged
@Deprecated
public class MVPaperCommandManager extends PaperCommandManager {

    protected final MVAnnotations annotations;

    public MVPaperCommandManager(Plugin plugin) {
        super(plugin);
        this.annotations = new MVAnnotations(this);
    }

    @Override
    Annotations getAnnotations() {
        return this.annotations;
    }
}
