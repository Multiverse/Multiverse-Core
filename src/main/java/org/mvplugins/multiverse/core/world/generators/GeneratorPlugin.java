package org.mvplugins.multiverse.core.world.generators;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>A generator API for Multiverse.</p>
 *
 * <p>Any generator plugin can register themselves to Multiverse. This will provide Multiverse with addition
 * information about your generator plugin such as generator id suggestions, example usages and link to more
 * info on your generator plugin.</p>
 */
public interface GeneratorPlugin {
    /**
     * <p>Suggest possible generator ids. To be used in command tab-completion.</p>
     *
     * <p>These suggestions can be static without relying on currentIdInput, or dynamically changed based
     * on the currentIdInput.</p>
     *
     * @param currentIdInput    The current state user input. This may be null or empty if user has not started
     *                          any input for generator id.
     * @return Collection of suggested generator ids.
     */
    @NotNull Collection<String> suggestIds(@Nullable String currentIdInput);

    /**
     * <p>Gets command usages that users can try to generate a world with your generator plugin. Returning null means
     * you do not wish to show any usage examples for your generator plugin.</p>
     *
     * <p>An example command: '/mv create myworld normal -g CoolGen:FunWorld'</p>
     *
     * <p>Notes on usage of this method:</p>
     * <ul>
     *     <li>Feel free to have colors in your command usage, but not Multiverse won't parse color codes for you.</li>
     *     <li>Please include the starting slash '/' in your usage examples.</li>
     *     <li>We suggest keeping the usage to at most 5 examples.</li>
     *     <li>This should not be a full explanation on all your generator plugin, just basics usages to get people
     *     started. For full guide, you can use {@link #getInfoLink()} to direct users.</li>
     * </ul>
     *
     * @return A collection of command usage examples.
     */
    @Nullable Collection<String> getExampleUsages();

    /**
     * <p>Gets a link with more information on your generator plugin. Returning null means you do not wish to link
     * users to any website related to your generator plugin.</p>
     *
     * <p>An example info: 'Click on https://www.amazinggenerator.io ;)'</p>
     *
     * <p>Some suggested places you can link to are: spigot resource page, github repo or your own plugin site.</p>
     *
     * @return Link to more info on your generator plugin.
     */
    @Nullable String getInfoLink();

    /**
     * Gets the java plugin for this generator. In short, return your own generator plugin instance.
     *
     * @return The associated plugin for this generator.
     */
    @NotNull String getPluginName();
}
