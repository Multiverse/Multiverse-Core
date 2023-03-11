package com.onarandombox.MultiverseCore.inject;

import org.jvnet.hk2.annotations.Contract;

/**
 * Marker interface for services belonging to a plugin that should be eagerly initialized when service injection is
 * set up for the owning plugin.
 */
@Contract
public interface AutoLoadedService { }
