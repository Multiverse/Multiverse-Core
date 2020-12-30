package com.onarandombox.MultiverseCore.commandTools.display;

@FunctionalInterface
public interface ContentCreator<T> {
    T generateContent();
}
