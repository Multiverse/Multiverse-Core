package com.onarandombox.MultiverseCore.commandTools.display.kvpair;

import com.onarandombox.MultiverseCore.commandTools.display.ContentFilter;
import com.onarandombox.MultiverseCore.commandTools.display.ShowRunnable;
import org.bukkit.ChatColor;

import java.util.Map;

public class ShowKeyValue extends ShowRunnable<KeyValueDisplay, Map<String, Object>> {

    private final StringBuilder contentBuilder;

    public ShowKeyValue(KeyValueDisplay display) {
        super(display);
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
            contentBuilder.append(this.display.getColours().getThis())
                    .append(entry.getKey())
                    .append(ChatColor.WHITE)
                    .append(this.display.getOperator())
                    .append(this.display.getColours().getThat())
                    .append(entry.getValue())
                    .append(ChatColor.WHITE);
        }
    }

    @Override
    public boolean hasContentToShow() {
        return contentBuilder.length() == 0;
    }

    @Override
    public boolean validateContent() {
        return true;
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
