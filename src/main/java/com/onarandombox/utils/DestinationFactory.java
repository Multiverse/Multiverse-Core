package com.onarandombox.utils;

import java.util.HashMap;
import java.util.Map;

import com.onarandombox.MultiverseCore.MultiverseCore;

public class DestinationFactory {
    private MultiverseCore plugin;
    private Map<String, Class<? extends Destination>> destList;

    public DestinationFactory(MultiverseCore plugin) {
        this.plugin = plugin;
        this.destList = new HashMap<String, Class<? extends Destination>>();
    }

    public Destination getDestination(String dest) {
        String idenChar = "";
        if(dest.split(":").length > 1) {
            idenChar = dest.substring(0, 1);
        }
        
        if (this.destList.containsKey(idenChar)) {
            Class<? extends Destination> myClass = this.destList.get(idenChar);
            try {
                Destination mydest = myClass.newInstance();
                mydest.setDestination(this.plugin, dest);
                return mydest;
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            }
        }
        return new InvalidDestination();
    }

    public boolean registerDestinationType(Class<? extends Destination> c, String identifier) {
        if (this.destList.containsKey(identifier)) {
            return false;
        }
        this.destList.put(identifier, c);
        return true;
    }
}
