package com.onarandombox.MultiverseCore.inject.registration;

import org.jvnet.hk2.annotations.Contract;

/**
 * The base contract for automatic registration filters.
 * <br/>
 * This is primarily for internal use only. See {@link DoNotRegister} for a practical application.
 */
@Contract
public interface RegistrationFilter {

    boolean shouldRegister(Object object);
}
