package com.onarandombox.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class DebugLog {

    private FileHandler fh;
    private Logger log;

    /**
     * Creates a new debug logger.
     *
     * @param logger The name of the logger.
     * @param file   The file to log to.
     */
    public DebugLog(String logger, String file) {
        this.log = Logger.getLogger(logger);

        try {
            this.fh = new FileHandler(file, true);
            this.log.setUseParentHandlers(false);
            for (Handler handler : this.log.getHandlers()) {
                this.log.removeHandler(handler);
            }
            this.log.addHandler(this.fh);
            this.log.setLevel(Level.ALL);
            this.fh.setFormatter(new LogFormatter());
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Log a message at a certain level
     *
     * @param level
     * @param msg
     */
    public void log(Level level, String msg) {
        this.log.log(level, msg);
    }

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

    public void close() {
        this.fh.close();
    }
}
