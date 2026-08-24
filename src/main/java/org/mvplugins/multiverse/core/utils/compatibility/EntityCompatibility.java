package org.mvplugins.multiverse.core.utils.compatibility;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mvplugins.multiverse.core.utils.ReflectHelper;

/**
 * Compatibility class used to handle API changes in {@link Entity} class.
 */
// TODO: Consider making this part of the public API in v5.9.
@ApiStatus.Internal
public final class EntityCompatibility {

    private static final boolean HAS_GET_ENTITY_SPAWN_REASON_METHOD;

    static {
        HAS_GET_ENTITY_SPAWN_REASON_METHOD = ReflectHelper.hasMethod(Entity.class, "getEntitySpawnReason");
    }

    /**
     * Gets the reason that initially spawned the entity when supported by the server.
     *
     * @param entity The entity to query.
     * @return The entity's spawn reason, or null when the API is unavailable.
     */
    @Nullable
    public static SpawnReason getEntitySpawnReason(@NotNull Entity entity) {
        if (HAS_GET_ENTITY_SPAWN_REASON_METHOD) {
            return entity.getEntitySpawnReason();
        }
        return null;
    }

    private EntityCompatibility() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
