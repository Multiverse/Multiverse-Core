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
            System.out.print("Found the dest key!");
            Class<? extends Destination> myClass = this.destList.get(idenChar);
            try {
                Destination mydest = myClass.newInstance();
                System.out.print(idenChar);
                if(!mydest.isThisType(plugin, dest)) {
                    System.out.print("Invalid A!");
                    return new InvalidDestination();
                }
                mydest.setDestination(this.plugin, dest);
                System.out.print("Valid!");
                return mydest;
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            }
        }
        System.out.print("Invalid C!");
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
