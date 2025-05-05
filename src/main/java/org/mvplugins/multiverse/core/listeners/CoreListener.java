package org.mvplugins.multiverse.core.listeners;

import org.jvnet.hk2.annotations.Contract;
import org.mvplugins.multiverse.core.dynamiclistener.DynamicListener;

@Contract
public sealed interface CoreListener extends DynamicListener permits
        MVChatListener,
        MVEntityListener,
        MVPlayerListener,
        MVPortalListener,
        MVWeatherListener,
        MVWorldListener
{ }
