package org.mvplugins.multiverse.core.listeners;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.dynamiclistener.annotations.EventClass;
import org.mvplugins.multiverse.core.dynamiclistener.EventRunnable;
import org.mvplugins.multiverse.core.world.WorldManager;

@Service
final class MVAdvancementListener implements CoreListener {

    private final WorldManager worldManager;

    @Inject
    MVAdvancementListener(@NotNull WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    @EventClass("com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent")
    EventRunnable playerAdvancementCriterionGrant() {
        return new EventRunnable<PlayerAdvancementCriterionGrantEvent>() {
            @Override
            public void onEvent(PlayerAdvancementCriterionGrantEvent event) {
                worldManager.getLoadedWorld(event.getPlayer().getWorld()).peek(mvWorld -> {
                    if (!mvWorld.isAllowAdvancementGrant() && !event.getCriterion().equals("unlock_right_away")) {
                        Logging.finest("Advancement criterion cancelled: %s", event.getCriterion());
                        event.setCancelled(true);
                    }
                });
            }
        };
    }
}
