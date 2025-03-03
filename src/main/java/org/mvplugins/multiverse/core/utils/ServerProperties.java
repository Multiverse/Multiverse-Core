package org.mvplugins.multiverse.core.utils;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Option;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@Service
public final class ServerProperties {

    private final Map<String, String> properties;
    private final FileUtils fileUtils;

    @Inject
    public ServerProperties(@NotNull FileUtils fileUtils) {
        this.fileUtils = fileUtils;
        properties = new HashMap<>();
        parseServerPropertiesFile();
    }

    private void parseServerPropertiesFile() {
        if (fileUtils.getServerProperties() == null) {
            return;
        }
        try {
            Files.readAllLines(fileUtils.getServerProperties().toPath()).stream()
                    .map(String::strip)
                    .filter(line -> !line.startsWith("#"))
                    .map(line -> line.split("=", 2))
                    .filter(line -> line.length == 2)
                    .forEach(line -> properties.put(line[0], line[1]));
        } catch (IOException e) {
            Logging.warning("Could not read server.properties file! Some features such as default world may not work as expected.");
        }
    }

    public Option<String> getLevelName() {
        return getProperty("level-name");
    }

    public boolean getAllowNether() {
        return getProperty("allow-nether").map(Boolean::parseBoolean).getOrElse(true);
    }

    public Option<String> getProperty(String key) {
        return Option.of(properties.get(key));
    }
}
