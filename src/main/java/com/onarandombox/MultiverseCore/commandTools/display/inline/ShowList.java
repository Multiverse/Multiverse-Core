package com.onarandombox.MultiverseCore.commandTools.display.inline;

import java.util.List;

public class ShowList extends ShowInline<ListDisplay, List<String>> {

    public ShowList(ListDisplay display) {
        super(display);
    }

    @Override
    protected void calculateContent() {
        boolean isFirst = true;
        for (String element : this.contents) {
            if (!this.display.getFilter().checkMatch(element)) {
                continue;
            }
            if (isFirst) {
                isFirst = false;
            }
            else {
                contentBuilder.append(", ");
            }
            contentBuilder.append(this.display.getColours().get())
                    .append(element);
        }
    }
}
