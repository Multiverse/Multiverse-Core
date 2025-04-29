package org.mvplugins.multiverse.core.commands;

import java.util.LinkedHashMap;
import java.util.Map;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import jakarta.inject.Inject;
import org.bukkit.ChatColor;
import org.bukkit.WorldType;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.command.LegacyAliasCommand;
import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.command.flag.ParsedCommandFlags;
import org.mvplugins.multiverse.core.command.flags.PageFilterFlags;
import org.mvplugins.multiverse.core.display.ContentDisplay;
import org.mvplugins.multiverse.core.display.filters.DefaultContentFilter;
import org.mvplugins.multiverse.core.display.handlers.PagedSendHandler;
import org.mvplugins.multiverse.core.display.parsers.MapContentProvider;
import org.mvplugins.multiverse.core.economy.MVEconomist;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.Message;
import org.mvplugins.multiverse.core.teleportation.LocationManipulation;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.MultiverseWorld;

import static org.mvplugins.multiverse.core.locale.message.MessageReplacement.replace;

@Service
class InfoCommand extends CoreCommand {

    private final LocationManipulation locationManipulation;
    private final MVEconomist economist;
    private final PageFilterFlags flags;

    @Inject
    InfoCommand(
            @NotNull LocationManipulation locationManipulation,
            @NotNull MVEconomist economist,
            @NotNull PageFilterFlags flags
    ) {
        this.locationManipulation = locationManipulation;
        this.economist = economist;
        this.flags = flags;
    }

    // TODO: support info for unloaded worlds
    @Subcommand("info")
    @CommandPermission("multiverse.core.info")
    @CommandCompletion("@mvworlds:scope=both|@flags:resolveUntil=arg1,groupName=" + PageFilterFlags.NAME + " @flags:groupName=" + PageFilterFlags.NAME)
    @Syntax("[world] [--page <page>] [--filter <filter>]")
    @Description("{@@mv-core.info.description}")
    public void onInfoCommand(
            MVCommandIssuer issuer,

            @Flags("resolve=issuerAware")
            @Syntax("<world>")
            @Description("{@@mv-core.info.description.world}")
            LoadedMultiverseWorld world,

            @Optional
            @Syntax("[--page <page>] [--filter <filter>]")
            String[] flagArray) {
        ParsedCommandFlags parsedFlags = flags.parse(flagArray);

        ContentDisplay.create()
                .addContent(MapContentProvider.forContent(getInfo(world))
                        .withKeyColor(ChatColor.AQUA)
                        .withValueColor(ChatColor.WHITE))
                .withSendHandler(PagedSendHandler.create()
                        .withHeader(Message.of(MVCorei18n.INFO_HEADER, replace("{world}").with(world.getName())))
                        .noContentMessage(Message.of(MVCorei18n.INFO_NOCONTENT))
                        .doPagination(true)
                        .withTargetPage(parsedFlags.flagValue(flags.page, 1))
                        .withFilter(parsedFlags.flagValue(flags.filter, DefaultContentFilter.get())))
                .send(issuer);
    }

    private Map<String, String> getInfo(LoadedMultiverseWorld world) {
        Map<String, String> outMap = new LinkedHashMap<>();

        outMap.put("World Name", world.getName());
        outMap.put("World Alias", world.getAlias());
        outMap.put("World UID", world.getUID().toString());
        outMap.put("Game Mode", world.getGameMode().toString());
        outMap.put("Difficulty", world.getDifficulty().toString());

        outMap.put("Spawn Location", locationManipulation.strCoords(world.getSpawnLocation()));
        outMap.put("Respawn World", world.getRespawnWorldName());
        outMap.put("Bed Respawn", String.valueOf(world.getBedRespawn()));
        outMap.put("Anchor Respawn", String.valueOf(world.getAnchorRespawn()));

        outMap.put("Seed", String.valueOf(world.getSeed()));
        outMap.put("Environment", String.valueOf(world.getEnvironment()));
        outMap.put("World Type", world.getWorldType().map(WorldType::getName).getOrNull());
        outMap.put("Biome", world.getBiome());
        outMap.put("Generator", world.getGenerator());
        outMap.put("Generate Structures", world.canGenerateStructures().map(String::valueOf).getOrNull());

        outMap.put("Auto Load", String.valueOf(world.isAutoLoad()));
        outMap.put("Keep Spawn In Memory", String.valueOf(world.isKeepSpawnInMemory()));

        getEntryFeeInfo(outMap, world);
        outMap.put("World Scale", String.valueOf(world.getScale()));
        outMap.put("Weather Enabled", String.valueOf(world.isAllowWeather()));
        outMap.put("Allow Flight", String.valueOf(world.isAllowFlight()));
        outMap.put("Hunger Depletes", String.valueOf(world.isHunger()));
        outMap.put("Keep Spawn In Memory", String.valueOf(world.isKeepSpawnInMemory()));
        outMap.put("PVP Enabled", String.valueOf(world.getPvp()));
        outMap.put("Portal Form", String.valueOf(world.getPortalForm()));
        outMap.put("Player Limit", String.valueOf(world.getPlayerLimit()));
//        getAnimalSpawningInfo(outMap, world);
//        getMonsterSpawningInfo(outMap, world);
        outMap.put("World Blacklist", String.join(", ", world.getWorldBlacklist()));

        return outMap;
    }

    private void getEntryFeeInfo(Map<String, String> outMap, MultiverseWorld world) {
        double price = world.getPrice();
        if (price == 0) {
            outMap.put("Entry Fee", "FREE!");
        } else if (price > 0) {
            outMap.put("Entry Fee", economist.formatPrice(-price, world.getCurrency()));
        } else if (price < 0) {
            outMap.put("Entry Reward", economist.formatPrice(price, world.getCurrency()));
        }
    }

//    private void getAnimalSpawningInfo(Map<String, String> outMap, MultiverseWorld world) {
//        if (world.isSpawningAnimals()) {
//            outMap.put("Spawning Animals", "ALL");
//        } else {
//            if (!world.getSpawningAnimalsExceptions().isEmpty()) {
//                outMap.put("Spawning Animals", world.getSpawningAnimalsExceptions().toString());
//            } else {
//                outMap.put("Spawning Animals", "NONE");
//            }
//        }
//    }
//
//    private void getMonsterSpawningInfo(Map<String, String> outMap, MultiverseWorld world) {
//        if (world.isSpawningMonsters()) {
//            outMap.put("Spawning Monsters", "ALL");
//        } else {
//            if (!world.getSpawningMonstersExceptions().isEmpty()) {
//                outMap.put("Spawning Monsters", world.getSpawningMonstersExceptions().toString());
//            } else {
//                outMap.put("Spawning Monsters", "NONE");
//            }
//        }
//    }

    @Service
    private static final class LegacyAlias extends InfoCommand implements LegacyAliasCommand {
        @Inject
        LegacyAlias(
                @NotNull LocationManipulation locationManipulation,
                @NotNull MVEconomist economist,
                @NotNull PageFilterFlags flags
        ) {
            super(locationManipulation, economist, flags);
        }

        @Override
        @CommandAlias("mvinfo|mvi")
        public void onInfoCommand(MVCommandIssuer issuer, LoadedMultiverseWorld world, String[] flags) {
            super.onInfoCommand(issuer, world, flags);
        }
    }
}
