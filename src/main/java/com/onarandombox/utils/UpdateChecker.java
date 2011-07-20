package com.onarandombox.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class UpdateChecker {

    public static final Logger log = Logger.getLogger("Minecraft");

    public Timer timer = new Timer(); // Create a new Timer.

    private String name; // Hold the Plugins Name.
    private String cversion; // Hold the Plugins Current Version.

    public UpdateChecker(String name, String version) {
        this.name = name;
        this.cversion = version;

        int delay = 0; // No Delay, fire the first check instantly.
        int period = 1800; // Delay 30 Minutes

        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkUpdate();
            }
        }, delay * 1000, period * 1000);
    }

    public void checkUpdate() {
        try {
            URL url = new URL("http://bukkit.onarandombox.com/multiverse/version.php?n=" + URLEncoder.encode(this.name, "UTF-8") + "&v=" + this.cversion);
            URLConnection conn = url.openConnection();
            conn.setReadTimeout(2000); // 2000 = 2 Seconds.

            int code = ((HttpURLConnection) conn).getResponseCode();

            if (code != 200) {
                return;
            }

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String version = null;

            while ((line = rd.readLine()) != null) {
                if (version == null) {
                    version = line;
                }
            }

            if (version == null) {
                rd.close();
                return;
            }

            String v1 = normalisedVersion(version);
            String v2 = normalisedVersion(this.cversion);

            int compare = v1.compareTo(v2);

            if (compare > 0) {
                log.info("[" + this.name + "]" + " - Update Available (" + version + ")");
            }

            rd.close();
        } catch (Exception e) {
            // No need to alert the user of any error here... it's not important.
        }
    }

    /**
     * Convert the given Version String to a Normalised Version String so we can compare it.
     *
     * @param version
     * @return
     */
    public static String normalisedVersion(String version) {
        return normalisedVersion(version, ".", 4);
    }

    public static String normalisedVersion(String version, String sep, int maxWidth) {
        String[] split = Pattern.compile(sep, Pattern.LITERAL).split(version);
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(String.format("%" + maxWidth + 's', s));
        }
        return sb.toString();
    }

}
