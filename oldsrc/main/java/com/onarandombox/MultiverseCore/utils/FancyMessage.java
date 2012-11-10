/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.utils;

import com.onarandombox.MultiverseCore.api.FancyText;

/**
 * A colored text-message.
 */
public class FancyMessage implements FancyText {
    private String title;
    private String message;
    private boolean main = true;
    private FancyColorScheme colors;

    /**
     * Allows easy creation of an alternating colored list.
     * TODO: Documentation! Why does CheckStyle just ignore this?
     *
     * @param title
     * @param message
     * @param scheme
     */
    public FancyMessage(String title, String message, FancyColorScheme scheme) {
        this.title = title;
        this.message = message;
        this.colors = scheme;
    }

    /**
     * Makes this {@link FancyMessage} use the main-color.
     */
    public void setColorMain() {
        this.main = true;
    }

    /**
     * Makes this {@link FancyMessage} use the alt-color.
     */
    public void setColorAlt() {
        this.main = false;
    }

    @Override
    public String getFancyText() {
        return this.colors.getMain(this.main) + this.title + this.colors.getDefault() + message;
    }

    /**
     * Specifies whether this {@link FancyMessage} should use the alt-color.
     * @param altColor Whether this {@link FancyMessage} should use the alt-color.
     */
    public void setAltColor(boolean altColor) {
        this.main = !altColor;
    }

    /**
     * Specifies whether this {@link FancyMessage} should use the main-color.
     * @param mainColor Whether this {@link FancyMessage} should use the main-color.
     */
    public void setMainColor(boolean mainColor) {
        this.main = mainColor;
    }
}
