/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.configuration;

import com.onarandombox.MultiverseCore.enums.EnglishChatColor;
import org.bukkit.configuration.ConfigurationSection;

public class ColorConfigProperty implements MVConfigProperty<EnglishChatColor> {
    private String name;
    private EnglishChatColor value;
    private String configNode;
    private ConfigurationSection section;

    public ColorConfigProperty(ConfigurationSection section, String name, EnglishChatColor defaultValue) {
        this.name = name;
        this.configNode = name;
        this.section = section;
        this.parseValue(this.section.getString(this.configNode, defaultValue.toString()));
    }

    public ColorConfigProperty(ConfigurationSection section, String name, EnglishChatColor defaultValue, String configNode) {
        this.name = name;
        this.configNode = configNode;
        this.section = section;
        this.parseValue(this.section.getString(this.configNode, defaultValue.toString()));
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public EnglishChatColor getValue() {
        return this.value;
    }

    @Override
    public boolean setValue(EnglishChatColor value) {
        if (value == null) {
            return false;
        }
        this.value = value;
        this.section.set(configNode, this.value.getText());
        return true;
    }

    @Override
    public boolean parseValue(String value) {
        EnglishChatColor color = EnglishChatColor.fromString(value);
        if (color == null) {
            return false;
        }
        this.value = color;
        return true;
    }

    @Override
    public String getConfigNode() {
        return this.configNode;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
