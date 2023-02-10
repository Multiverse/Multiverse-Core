package com.onarandombox.MultiverseCore.api.action;

public interface ActionResult {
    String getName();

    boolean isSuccessful();

    boolean hasResult(ActionResult result);
}
