package org.mvplugins.multiverse.core.listeners;

import org.bukkit.event.Listener;
import org.jvnet.hk2.annotations.Contract;

@Contract
public sealed interface CoreListener extends Listener permits MVChatListener, MVEntityListener, MVLocalesListener,
        MVPlayerListener, MVPortalListener, MVWeatherListener, MVWorldListener {
}
