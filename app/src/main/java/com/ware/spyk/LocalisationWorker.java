package com.ware.spyk;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class LocalisationWorker extends Worker {

    public LocalisationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Lire une seule fois la localisation
        GetLocalisation.captureOnce(getApplicationContext());
        return Result.success();
    }
}

