/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.configuration;

import com.onarandombox.MultiverseCore.enums.EnglishChatColor;
import org.bukkit.configuration.ConfigurationSection;

/**
 * A {@link EnglishChatColor} config-property.
 */
public class ColorConfigProperty implements MVConfigProperty<EnglishChatColor> {
    private String name;
    private EnglishChatColor value;
    private String configNode;
    private ConfigurationSection section;
    private String help;

    public ColorConfigProperty(ConfigurationSection section, String name, EnglishChatColor defaultValue, String help) {
        this(section, name, defaultValue, name, help);
    }

    public ColorConfigProperty(ConfigurationSection section, String name, EnglishChatColor defaultValue, String configNode, String help) {
        this.name = name;
        this.configNode = configNode;
        this.section = section;
        this.help = help;
        this.value = defaultValue;
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
        this.setValue(color);
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

    @Override
    public String getHelp() {
        return this.help;
    }
}
