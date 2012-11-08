package com.onarandombox.multiverse.core.api;

import com.dumptruckman.minecraft.pluginbase.locale.Message;
import com.dumptruckman.minecraft.pluginbase.properties.Properties;
import com.dumptruckman.minecraft.pluginbase.properties.PropertyFactory;
import com.dumptruckman.minecraft.pluginbase.properties.SimpleProperty;

/**
 * Houses all of the properties for a Multiverse world.
 */
public interface WorldProperties extends Properties {

    SimpleProperty<String> ALIAS = PropertyFactory.newProperty(String.class, "alias", "")
            .comment("World aliases allow you to name a world differently than what the folder name is.")
            .comment("This lets you choose fancy names for your worlds while keeping the folders nice and neat.")
            .description(Descriptions.ALIAS)
            .build();

    SimpleProperty<Boolean> HIDDEN = PropertyFactory.newProperty(Boolean.class, "hidden", false)
            .comment("The hidden property allows you to have a world that exists but does not show up in lists.")
            .description(Descriptions.HIDDEN)
            .build();

    SimpleProperty<Boolean> PREFIX_CHAT = PropertyFactory.newProperty(Boolean.class, "prefixChat", true)
            .comment("The prefixChat property adds the world's name (or alias) as a prefix to chat messages.")
            .comment("Please note, this property can be disabled globally in the configuration.")
            .description(Descriptions.PREFIX_CHAT)
            .build();

    /**
     * Houses localized (english) descriptions of the Multiverse world properties.
     */
    public static class Descriptions {

        public static final Message ALIAS = new Message("world_properties.descriptions.alias",
                "World aliases allow you to name a world differently than what the folder name is.",
                "This lets you choose fancy names for your worlds while keeping the folders nice and neat.");

        public static final Message HIDDEN = new Message("world_properties.descriptions.hidden",
                "The hidden property allows you to have a world that exists but does not show up in lists.");

        public static final Message PREFIX_CHAT = new Message("world_properties.descriptions.prefixChat",
                "The prefixChat property adds the world's name (or alias) as a prefix to chat messages.");
    }
}
