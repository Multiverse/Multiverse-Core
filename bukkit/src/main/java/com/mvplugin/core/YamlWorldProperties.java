package com.mvplugin.core;

import com.dumptruckman.minecraft.pluginbase.properties.PropertySerializer;
import com.dumptruckman.minecraft.pluginbase.properties.YamlProperties;
import com.mvplugin.core.api.WorldProperties;
import com.mvplugin.core.minecraft.PlayerPosition;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * YAML implementation of {@link WorldProperties} which will store all the world properties in a
 * YAML file named after the world.
 */
class YamlWorldProperties extends YamlProperties implements WorldProperties {

    YamlWorldProperties(File configFile) throws IOException {
        super(false, true, configFile, WorldProperties.class);
    }

    @Override
    protected void registerSerializers() {
        super.registerSerializers();
        setPropertySerializer(PlayerPosition.class, new PlayerPositionSerializer());
    }

    private static class PlayerPositionSerializer implements PropertySerializer<PlayerPosition> {
        @Override
        public PlayerPosition deserialize(Object o) {
            String world = "";
            double x = 0D;
            double y = 0D;
            double z = 0D;
            float pitch = 0F;
            float yaw = 0F;
            if (o instanceof ConfigurationSection) {
                o = ((ConfigurationSection) o).getValues(false);
            }
            if (o instanceof Map) {
                Map map = (Map) o;
                world = map.get("world").toString();
                try {
                    x = Double.valueOf(map.get("x").toString());
                } catch (Exception ignore) { }
                try {
                    y = Double.valueOf(map.get("y").toString());
                } catch (Exception ignore) { }
                try {
                    z = Double.valueOf(map.get("z").toString());
                } catch (Exception ignore) { }
                try {
                    pitch = Float.valueOf(map.get("pitch").toString());
                } catch (Exception ignore) { }
                try {
                    yaw = Float.valueOf(map.get("yaw").toString());
                } catch (Exception ignore) { }
            }
            return new PlayerPosition(world, x, y, z, pitch, yaw);
        }

        @Override
        public Object serialize(PlayerPosition playerPosition) {
            Map<String, Object> result = new LinkedHashMap<String, Object>(6);
            result.put("world", playerPosition.getWorld() != null ? playerPosition.getWorld() : "");
            result.put("x", playerPosition.getX());
            result.put("y", playerPosition.getY());
            result.put("z", playerPosition.getZ());
            result.put("pitch", playerPosition.getPitch());
            result.put("yaw", playerPosition.getYaw());
            return result;
        }
    }
}
