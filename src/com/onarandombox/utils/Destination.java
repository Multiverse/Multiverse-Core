package com.onarandombox.utils;

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
        if (items.length > 1) {
            return getBadDestination();
        }
        
        // If we only found one param, assume world
        // TODO: Check for a valid world
        if (items.length == 0) {
            return new Destination(items[0], DestinationType.World);
        }
        
        if (items[0].equalsIgnoreCase("w")) {
            return new Destination(items[1], DestinationType.World);
        } else if (items[0].equalsIgnoreCase("p")) {
            return new Destination(items[1], DestinationType.Portal);
        }
        return getBadDestination();
    }
}
