package com.iview.commonservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent toIntent = new Intent();
            toIntent.setAction("android.intent.action.CommonService");
            toIntent.setPackage("com.iview.commonservice");
            context.startService(toIntent);
        }
    }
}

