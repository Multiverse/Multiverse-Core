package com.onarandombox.MultiverseCore.displaytools;

import java.util.Collection;

public interface DisplayHandler<T> {

    Collection<String> format(ContentDisplay<T> display);

}
