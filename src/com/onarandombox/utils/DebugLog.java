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

    public DebugLog(String logger, String file) {
        log = Logger.getLogger(logger);

        try {
            fh = new FileHandler(file, true);
            log.setUseParentHandlers(false);
            for (Handler handler : log.getHandlers()) {
                log.removeHandler(handler);
            }
            log.addHandler(fh);
            log.setLevel(Level.ALL);
            fh.setFormatter(new LogFormatter());
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(Level level, String msg) {
        log.log(level, msg);
    }

    private class LogFormatter extends Formatter {
        private final SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        @Override
        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder();
            Throwable ex = record.getThrown();

            builder.append(date.format(record.getMillis()));
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
        fh.close();
    }
}
