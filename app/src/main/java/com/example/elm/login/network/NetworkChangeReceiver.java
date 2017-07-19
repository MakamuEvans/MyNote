package com.example.elm.login.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.elm.login.services.note.SyncUpload;

/**
 * Created by elm on 7/10/17.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String status = NetworkUtil.getConnectivityStatusString(context);

        if (status!="Not connected to Internet"){
            Intent uploadintent = new Intent(context, SyncUpload.class);
            context.startService(uploadintent);
        }
    }
}
