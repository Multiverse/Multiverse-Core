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
