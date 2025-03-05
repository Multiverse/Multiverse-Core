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
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.commandtools.MVCommandIssuer;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.commandtools.flag.CommandValueFlag;
import org.mvplugins.multiverse.core.commandtools.flag.ParsedCommandFlags;
import org.mvplugins.multiverse.core.commandtools.flags.FilterCommandFlag;
import org.mvplugins.multiverse.core.commandtools.flags.PageCommandFlag;
import org.mvplugins.multiverse.core.display.ContentDisplay;
import org.mvplugins.multiverse.core.display.filters.ContentFilter;
import org.mvplugins.multiverse.core.display.filters.DefaultContentFilter;
import org.mvplugins.multiverse.core.display.handlers.PagedSendHandler;
import org.mvplugins.multiverse.core.display.parsers.MapContentProvider;
import org.mvplugins.multiverse.core.economy.MVEconomist;
import org.mvplugins.multiverse.core.teleportation.LocationManipulation;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.MultiverseWorld;

@Service
@CommandAlias("mv")
final class InfoCommand extends CoreCommand {

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
    @CommandAlias("mvinfo|mvi")
    @Subcommand("info")
    @CommandPermission("multiverse.core.info")
    @CommandCompletion("@mvworlds:scope=both|@flags:groupName=mvinfocommand @flags:groupName=mvinfocommand")
    @Syntax("[world] [--page <page>] [--filter <filter>]")
    @Description("{@@mv-core.info.description")
    public void onInfoCommand(
            MVCommandIssuer issuer,

            @Flags("resolve=issuerAware")
            @Syntax("<world>")
            @Description("{@@mv-core.info.description.world}")
            LoadedMultiverseWorld world,

            @Optional
            @Syntax("[--page <page>]")
            @Description("{@@mv-core.info.description.page}")
            String[] flags) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);

        ContentDisplay.create()
                .addContent(MapContentProvider.forContent(getInfo(world))
                        .withKeyColor(ChatColor.AQUA)
                        .withValueColor(ChatColor.WHITE))
                .withSendHandler(PagedSendHandler.create()
                        .withHeader(getTitle(world))
                        .doPagination(true)
                        .withTargetPage(parsedFlags.flagValue(pageFlag, 1))
                        .withFilter(parsedFlags.flagValue(filterFlag, DefaultContentFilter.get())))
                .send(issuer);
    }

    private String getTitle(MultiverseWorld world) {
        return "&a&l---- World Info: &f&l%s&a&l ----".formatted(world.getName());
    }

    private Map<String, String> getInfo(LoadedMultiverseWorld world) {
        Map<String, String> outMap = new LinkedHashMap<>();

        outMap.put("World Name", world.getName());
        outMap.put("World Alias", world.getAlias());
        outMap.put("World UID", world.getUID().toString());
        outMap.put("Game Mode", world.getGameMode().toString());
        outMap.put("Difficulty", world.getDifficulty().toString());
        outMap.put("Spawn Location", locationManipulation.strCoords(world.getSpawnLocation()));
        outMap.put("Seed", String.valueOf(world.getSeed()));
        getEntryFeeInfo(outMap, world);
        outMap.put("Respawn World", world.getRespawnWorldName());
        outMap.put("World Type", world.getWorldType().get().toString());
        outMap.put("Biome", world.getBiome());
        outMap.put("Generator", world.getGenerator());
        outMap.put("Generate Structures", world.canGenerateStructures().get().toString());
        outMap.put("World Scale", String.valueOf(world.getScale()));
        outMap.put("Weather Enabled", String.valueOf(world.getAllowWeather()));
        outMap.put("Hunger Depletes", String.valueOf(world.getHunger()));
        outMap.put("Keep Spawn In Memory", String.valueOf(world.getKeepSpawnInMemory()));
        outMap.put("PVP Enabled", String.valueOf(world.getPvp()));
        getAnimalSpawningInfo(outMap, world);
        getMonsterSpawningInfo(outMap, world);

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
}
