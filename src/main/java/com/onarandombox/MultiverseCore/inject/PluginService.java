package com.onarandombox.MultiverseCore.inject;

import org.jvnet.hk2.annotations.Contract;

/**
 * Marker interface for plugin services.
 * <br/>
 * Implementations of this interface will be eagerly loaded when injection is initialized for the owning plugin.
 */
@Contract
public interface PluginService { }
