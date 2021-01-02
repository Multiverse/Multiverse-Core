package com.onarandombox.MultiverseCore.commandTools.display.inline;

import com.onarandombox.MultiverseCore.commandTools.display.ContentDisplay;
import com.onarandombox.MultiverseCore.commandTools.display.ShowRunnable;

public abstract class ShowInline<D extends ContentDisplay<?, T>, T> extends ShowRunnable<D, T> {

    protected final StringBuilder contentBuilder;

    public ShowInline(D display) {
        super(display);
        this.contentBuilder = new StringBuilder();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean hasContentToShow() {
        return contentBuilder.length() > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean validateContent() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void showHeader() {
        if (this.display.getHeader() == null) {
            return;
        }
        this.display.getSender().sendMessage(this.display.getHeader());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void showContent() {
        this.display.getSender().sendMessage(contentBuilder.toString());
    }
}
