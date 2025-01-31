open module multiverse.core {
    requires co.aikar.commands;
    requires com.dumptruckman.minecraft.util;
    requires de.themoep.idconverter;
    requires io.github.townyadvanced.commentedconfiguration;
    requires io.papermc.lib;
    requires io.vavr;
    requires me.clip.placeholderapi;
    requires net.milkbowl.vault;
    requires net.minidev.json;
    requires org.bstats.bukkit;
    requires org.bukkit;
    requires org.jetbrains.annotations;
    requires org.jvnet.hk2;

    exports org.mvplugins.multiverse.core;
    exports org.mvplugins.multiverse.core.commands;
    exports org.mvplugins.multiverse.core.listeners;
    exports org.mvplugins.multiverse.core.configuration.handle;
    exports org.mvplugins.multiverse.core.commandtools;
    exports org.mvplugins.multiverse.core.utils;
    exports org.mvplugins.multiverse.core.utils.result;
    exports org.mvplugins.multiverse.core.locale;
    exports org.mvplugins.multiverse.core.locale.message;
    exports org.mvplugins.multiverse.core.config;
    exports org.mvplugins.multiverse.core.inject;
    exports org.mvplugins.multiverse.core.anchor;
    exports org.mvplugins.multiverse.core.economy;
    exports org.mvplugins.multiverse.core.destination;
    exports org.mvplugins.multiverse.core.destination.core;
    exports org.mvplugins.multiverse.core.world;
    exports org.mvplugins.multiverse.core.world.helpers;
    exports org.mvplugins.multiverse.core.world.reasons;
    exports org.mvplugins.multiverse.core.world.location;
    exports org.mvplugins.multiverse.core.world.options;
    exports org.mvplugins.multiverse.core.world.generators;
    exports org.mvplugins.multiverse.core.teleportation;
}
