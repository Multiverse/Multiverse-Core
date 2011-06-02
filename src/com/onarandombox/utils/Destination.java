package com.onarandombox.utils;

enum DestinationType {
    World, Portal, Invalid
}

public class Destination {
    private String name;
    private DestinationType type;
    
    public Destination(String name, DestinationType type) {
        this.name = name;
        this.type = type;
    }
    
    public String getName() {
        return name;
    }
    
    public DestinationType getType() {
        return type;
    }
    
    private static Destination getBadDestination() {
        return new Destination("", DestinationType.Invalid);
    }
    
    public static Destination parseDestination(String dest) {
        if (dest == null) {
            return getBadDestination();
        }
        
        String[] items = dest.split(":");
        if (items.length != 2) {
            return getBadDestination();
        }
        if (items[0].equalsIgnoreCase("w")) {
            return new Destination(items[1], DestinationType.World);
        } else if (items[0].equalsIgnoreCase("p")) {
            return new Destination(items[1], DestinationType.Portal);
        }
        return getBadDestination();
    }
}
