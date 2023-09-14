package org.mvplugins.multiverse.core.configuration.functions;

import java.util.Collection;

@FunctionalInterface
public interface NodeSuggester {
    Collection<String> suggest(String input);
}
