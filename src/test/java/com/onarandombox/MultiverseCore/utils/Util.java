/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.utils;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogRecord;

public class Util {
    private Util() {}

    public static final Logger logger = Logger.getLogger("MV-Test");

    static {
        logger.setUseParentHandlers(false);

        Handler handler = new ConsoleHandler();
        handler.setFormatter(new MVTestLogFormatter());
        Handler[] handlers = logger.getHandlers();

        for (Handler h : handlers)
            logger.removeHandler(h);

        logger.addHandler(handler);
    }

    public static void log(Throwable t) {
        log(Level.WARNING, t.getLocalizedMessage(), t);
    }

    public static void log(Level level, Throwable t) {
        log(level, t.getLocalizedMessage(), t);
    }

    public static void log(String message, Throwable t) {
        log(Level.WARNING, message, t);
    }

    public static void log(Level level, String message, Throwable t) {
        LogRecord record = new LogRecord(level, message);
        record.setThrown(t);
        logger.log(record);
    }

    public static void log(String message) {
        log(Level.INFO, message);
    }

    public static void log(Level level, String message) {
        logger.log(level, message);
    }
}
