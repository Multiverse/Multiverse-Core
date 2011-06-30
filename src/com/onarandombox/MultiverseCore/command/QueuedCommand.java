package com.onarandombox.MultiverseCore.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;

import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MultiverseCore;

public class QueuedCommand {
    
    private String name;
    private Object[] args;
    private Class<?> paramTypes[];
    private CommandSender sender;
    private MultiverseCore plugin;
    private Calendar timeRequested;
    private String success;
    private String fail;
    
    public QueuedCommand(String commandName, Object[] args, Class<?> partypes[], CommandSender sender, Calendar instance, MultiverseCore plugin, String success, String fail) {
        this.plugin = plugin;
        this.name = commandName;
        this.args = args;
        this.sender = sender;
        this.timeRequested = instance;
        this.paramTypes = partypes;
        this.setSuccess(success);
        this.setFail(fail);
    }
    
    public CommandSender getSender() {
        return this.sender;
    }
    
    public boolean execute() {
        this.timeRequested.add(Calendar.SECOND, 10);
        if (this.timeRequested.after(Calendar.getInstance())) {
            try {
                Method method = this.plugin.getClass().getMethod(this.name, this.paramTypes);
                try {
                    method.invoke(this.plugin, this.args);
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return false;
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return false;
                } catch (InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return false;
                }
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            } catch (NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
            return true;
        } else {
            this.sender.sendMessage("This command has expried. Please type the original command again.");
        }
        return false;
    }
    
    private void setSuccess(String success) {
        this.success = success;
    }
    
    public String getSuccess() {
        return this.success;
    }
    
    private void setFail(String fail) {
        this.fail = fail;
    }
    
    public String getFail() {
        return this.fail;
    }
}
