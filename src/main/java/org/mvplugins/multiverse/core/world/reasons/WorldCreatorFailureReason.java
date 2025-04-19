package org.mvplugins.multiverse.core.world.reasons;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.utils.result.FailureReason;

/**
 * Result of bukkit's world creator failure.
 */
public enum WorldCreatorFailureReason implements FailureReason {

    /**
     * Biome provider parsed is invalid and thrown an exception.
     */
    INVALID_BIOME_PROVIDER(MVCorei18n.WORLDCREATOR_INVALIDBIOMEPROVIDER),

    /**
     * Chunk generator parsed is invalid and thrown an exception.
     */
    INVALID_CHUNK_GENERATOR(MVCorei18n.WORLDCREATOR_INVALIDCHUNKGENERATOR),

    /**
     * Chunk generator parsed is invalid and thrown an exception.
     */
    BUKKIT_CREATION_FAILED(MVCorei18n.WORLDCREATOR_BUKKITCREATIONFAILED),
    ;

    private final MessageKeyProvider message;

    WorldCreatorFailureReason(MessageKeyProvider message) {
        this.message = message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageKey getMessageKey() {
        return message.getMessageKey();
    }
}
