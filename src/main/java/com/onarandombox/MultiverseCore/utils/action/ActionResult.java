package com.onarandombox.MultiverseCore.utils.action;

public interface ActionResult {
    String getName();

    boolean isSuccessful();

    boolean hasResult(ActionResult result);
}
