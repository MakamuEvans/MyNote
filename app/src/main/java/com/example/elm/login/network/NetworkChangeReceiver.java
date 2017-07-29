package com.example.elm.login.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.elm.login.services.note.SyncDelete;
import com.example.elm.login.services.note.SyncFavourite;
import com.example.elm.login.services.note.SyncUpload;

/**
 * Created by elm on 7/10/17.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String status = NetworkUtil.getConnectivityStatusString(context);

        if (!status.equals("Not connected to Internet")){
            Log.e("internet", "Intaneti");
            Log.e("action", intent.getAction());
            if (intent.getAction().equals("newUpload")){
                Intent uploadintent = new Intent(context, SyncUpload.class);
                context.startService(uploadintent);
            }
            if (intent.getAction().equals("favourite")){
                Intent fav = new Intent(context, SyncFavourite.class);
                context.startService(fav);
            }
            if (intent.getAction().equals("delete")){
                Intent fav = new Intent(context, SyncDelete.class);
                context.startService(fav);
            }
        }else {
            Log.e("internet", "Intaneti imelosti");
        }
    }
}
