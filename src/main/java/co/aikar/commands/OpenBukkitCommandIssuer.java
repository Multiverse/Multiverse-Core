package co.aikar.commands;

import org.bukkit.command.CommandSender;

/**
 * Exists just so we can extend BukkitCommandIssuer since it has a package-private constructor.
 */
public abstract class OpenBukkitCommandIssuer extends BukkitCommandIssuer {

    protected OpenBukkitCommandIssuer(BukkitCommandManager manager, CommandSender sender) {
        super(manager, sender);
    }
}
