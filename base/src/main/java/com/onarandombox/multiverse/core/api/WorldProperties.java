package com.onarandombox.multiverse.core.api;

import com.dumptruckman.minecraft.pluginbase.config.Properties;
import com.dumptruckman.minecraft.pluginbase.config.PropertyBuilder;
import com.dumptruckman.minecraft.pluginbase.config.SimpleProperty;
import com.dumptruckman.minecraft.pluginbase.locale.Message;

/**
 * Houses all of the properties for a Multiverse world.
 */
public interface WorldProperties extends Properties {

    SimpleProperty<Boolean> HIDDEN = new PropertyBuilder<Boolean>(Boolean.class, "hidden")
            .def(false)
            .comment("This property allows you to have a world that exists but does not show up in lists.")
            .description(Descriptions.HIDDEN)
            .build();

    SimpleProperty<Boolean> PREFIX_CHAT = new PropertyBuilder<Boolean>(Boolean.class, "prefixChat")
            .def(true)
            .comment("This property adds the world's name (or alias) as a prefix to chat messages.")
            .comment("Please note, this property can be disabled globally in the configuration.")
            .description(Descriptions.PREFIX_CHAT)
            .build();

    /**
     * Houses localized (english) descriptions of the Multiverse world properties.
     */
    public static class Descriptions {

        public static final Message HIDDEN = new Message("world_properties.descriptions.hidden",
                "The hidden property allows you to have a world that exists but does not show up in lists.");

        public static final Message PREFIX_CHAT = new Message("world_properties.descriptions.prefixChat",
                "The prefixChat property adds the world's name (or alias) as a prefix to chat messages.");
    }
}
