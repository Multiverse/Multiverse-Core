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
import org.mvplugins.multiverse.core.command.MVCommandManager;
import org.mvplugins.multiverse.core.command.flag.CommandValueFlag;
import org.mvplugins.multiverse.core.command.flag.ParsedCommandFlags;
import org.mvplugins.multiverse.core.command.flags.FilterCommandFlag;
import org.mvplugins.multiverse.core.command.flags.PageCommandFlag;
import org.mvplugins.multiverse.core.display.ContentDisplay;
import org.mvplugins.multiverse.core.display.filters.ContentFilter;
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

    private final CommandValueFlag<Integer> pageFlag = flag(PageCommandFlag.create());

    private final CommandValueFlag<ContentFilter> filterFlag = flag(FilterCommandFlag.create());

    private final LocationManipulation locationManipulation;
    private final MVEconomist economist;

    @Inject
    InfoCommand(
            @NotNull MVCommandManager commandManager,
            @NotNull LocationManipulation locationManipulation,
            @NotNull MVEconomist economist) {
        super(commandManager);
        this.locationManipulation = locationManipulation;
        this.economist = economist;
    }

    // TODO: support info for unloaded worlds
    @Subcommand("info")
    @CommandPermission("multiverse.core.info")
    @CommandCompletion("@mvworlds:scope=both|@flags:groupName=mvinfocommand @flags:groupName=mvinfocommand")
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
            String[] flags) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);

        ContentDisplay.create()
                .addContent(MapContentProvider.forContent(getInfo(world))
                        .withKeyColor(ChatColor.AQUA)
                        .withValueColor(ChatColor.WHITE))
                .withSendHandler(PagedSendHandler.create()
                        .withHeader(Message.of(MVCorei18n.INFO_HEADER, replace("{world}").with(world.getName())))
                        .noContentMessage(Message.of(MVCorei18n.INFO_NOCONTENT))
                        .doPagination(true)
                        .withTargetPage(parsedFlags.flagValue(pageFlag, 1))
                        .withFilter(parsedFlags.flagValue(filterFlag, DefaultContentFilter.get())))
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

        outMap.put("Auto Load", String.valueOf(world.getAutoLoad()));
        outMap.put("Keep Spawn In Memory", String.valueOf(world.getKeepSpawnInMemory()));

        getEntryFeeInfo(outMap, world);
        outMap.put("World Scale", String.valueOf(world.getScale()));
        outMap.put("Weather Enabled", String.valueOf(world.getAllowWeather()));
        outMap.put("Allow Flight", String.valueOf(world.getAllowFlight()));
        outMap.put("Hunger Depletes", String.valueOf(world.getHunger()));
        outMap.put("Keep Spawn In Memory", String.valueOf(world.getKeepSpawnInMemory()));
        outMap.put("PVP Enabled", String.valueOf(world.getPvp()));
        outMap.put("Portal Form", String.valueOf(world.getPortalForm()));
        outMap.put("Player Limit", String.valueOf(world.getPlayerLimit()));
        getAnimalSpawningInfo(outMap, world);
        getMonsterSpawningInfo(outMap, world);
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

    private void getAnimalSpawningInfo(Map<String, String> outMap, MultiverseWorld world) {
        if (world.getSpawningAnimals()) {
            outMap.put("Spawning Animals", "ALL");
        } else {
            if (!world.getSpawningAnimalsExceptions().isEmpty()) {
                outMap.put("Spawning Animals", world.getSpawningAnimalsExceptions().toString());
            } else {
                outMap.put("Spawning Animals", "NONE");
            }
        }
    }

    private void getMonsterSpawningInfo(Map<String, String> outMap, MultiverseWorld world) {
        if (world.getSpawningMonsters()) {
            outMap.put("Spawning Monsters", "ALL");
        } else {
            if (!world.getSpawningMonstersExceptions().isEmpty()) {
                outMap.put("Spawning Monsters", world.getSpawningMonstersExceptions().toString());
            } else {
                outMap.put("Spawning Monsters", "NONE");
            }
        }
    }

    @Service
    private static final class LegacyAlias extends InfoCommand implements LegacyAliasCommand {
        @Inject
        LegacyAlias(@NotNull MVCommandManager commandManager, @NotNull LocationManipulation locationManipulation, @NotNull MVEconomist economist) {
            super(commandManager, locationManipulation, economist);
        }

        @Override
        @CommandAlias("mvinfo|mvi")
        public void onInfoCommand(MVCommandIssuer issuer, LoadedMultiverseWorld world, String[] flags) {
            super.onInfoCommand(issuer, world, flags);
        }

        @Override
        public boolean doFlagRegistration() {
            return false;
        }
    }
}
