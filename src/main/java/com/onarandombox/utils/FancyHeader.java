/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.utils;

public class FancyHeader implements FancyText {

    private FancyColorScheme colors;
    private String text;

    public FancyHeader(String text, FancyColorScheme scheme) {
        this.colors = scheme;
        this.text = text;
    }

    @Override
    public String getFancyText() {
        return colors.getHeader() + "--- " + text + colors.getHeader() + " ---";
    }

    public void appendText(String string) {
        this.text += string;
    }

}
