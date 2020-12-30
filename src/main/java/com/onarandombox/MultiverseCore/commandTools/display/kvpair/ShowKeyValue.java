package com.onarandombox.MultiverseCore.commandTools.display.kvpair;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.commandTools.display.ContentFilter;
import com.onarandombox.MultiverseCore.commandTools.display.ShowRunnable;
import org.bukkit.ChatColor;

import java.util.Map;

public class ShowKeyValue extends ShowRunnable<Map<String, Object>> {

    private final StringBuilder contentBuilder;
    private final KeyValueDisplay display;

    public ShowKeyValue(KeyValueDisplay display) {
        super(display);
        this.display = display;
        this.contentBuilder = new StringBuilder();
    }

    @Override
    public void calculateContent() {
        ContentFilter filter = this.display.getFilter();
        boolean isFirst = true;
        for (Map.Entry<String, Object> entry : this.contents.entrySet()) {
            if (!filter.checkMatch(entry.getKey()) && !filter.checkMatch(entry.getValue().toString())) {
                continue;
            }
            if (isFirst) {
                isFirst = false;
            }
            else {
                contentBuilder.append(", ");
            }
            contentBuilder.append(this.display.getColours().getColorThis())
                    .append(entry.getKey())
                    .append(ChatColor.WHITE)
                    .append(this.display.getOperator())
                    .append(this.display.getColours().getColorThat())
                    .append(entry.getValue());
        }
    }

    @Override
    public boolean validateContent() {
        return false;
    }

    @Override
    public void showHeader() {
        if (this.display.getHeader() == null) {
            return;
        }

        this.display.getSender().sendMessage(this.display.getHeader());
    }

    @Override
    public void showContent() {
        this.display.getSender().sendMessage(contentBuilder.toString());
    }
}
