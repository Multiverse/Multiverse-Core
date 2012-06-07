/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * The Multiverse debug-logger.
 */
public class DebugLog extends Logger {

    private FileHandler fh;

    /**
     * Creates a new debug logger.
     *
     * @param logger The name of the logger.
     * @param file   The file to log to.
     */
    public DebugLog(String logger, String file) {
        super(logger, null);

        try {
            this.fh = new FileHandler(file, true);
            this.setUseParentHandlers(false);
            List<Handler> toRemove = Arrays.asList(this.getHandlers());
            for (Handler handler : toRemove) {
                this.removeHandler(handler);
            }
            this.addHandler(this.fh);
            this.setLevel(Level.ALL);
            this.fh.setFormatter(new LogFormatter());
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Our log-{@link Formatter}.
     */
    private class LogFormatter extends Formatter {
        private final SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        @Override
        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder();
            Throwable ex = record.getThrown();

            builder.append(this.date.format(record.getMillis()));
            builder.append(" [");
            builder.append(record.getLevel().getLocalizedName().toUpperCase());
            builder.append("] ");
            builder.append(record.getMessage());
            builder.append('\n');

            if (ex != null) {
                StringWriter writer = new StringWriter();
                ex.printStackTrace(new PrintWriter(writer));
                builder.append(writer);
            }

            return builder.toString();
        }
    }

    /**
     * Closes this {@link DebugLog}.
     */
    public void close() {
        this.fh.close();
    }
}
