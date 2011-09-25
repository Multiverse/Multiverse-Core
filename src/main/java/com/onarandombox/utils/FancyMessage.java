/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.utils;

import com.onarandombox.MultiverseCore.api.FancyText;

public class FancyMessage implements FancyText {
    private String title;
    private String message;
    private boolean main = true;
    private FancyColorScheme colors;
    /**
     * Allows easy creation of an alternating colored list
     * @param title
     * @param message
     */
    public FancyMessage(String title, String message, FancyColorScheme scheme) {
        this.title = title;
        this.message = message;
        this.colors = scheme;
    }
    public void setColorMain() {
        this.main = true;
    }
    public void setColorAlt() {
        this.main = false;
    }
    @Override
    public String getFancyText() {
        return this.colors.getMain(this.main) + this.title + this.colors.getDefault() + message;
    }
    public void setAltColor(boolean altColor) {
        this.main = !altColor;
    }
    public void setMainColor(boolean mainColor) {
        this.main = mainColor;
    }
}
