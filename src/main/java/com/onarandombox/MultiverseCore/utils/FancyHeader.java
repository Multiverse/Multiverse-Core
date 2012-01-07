/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.utils;

import com.onarandombox.MultiverseCore.api.FancyText;

/**
 * A colored text-header.
 */
public class FancyHeader implements FancyText {

    private FancyColorScheme colors;
    private StringBuilder text;

    public FancyHeader(String text, FancyColorScheme scheme) {
        this.colors = scheme;
        this.text = new StringBuilder(text);
    }

    @Override
    public String getFancyText() {
        return String.format("%s--- %s%s ---", colors.getHeader(), text.toString(), colors.getHeader());
    }

    /**
     * Appends text to this {@link FancyHeader}.
     * @param string The text to append.
     */
    public void appendText(String string) {
        this.text.append(string);
    }

}
