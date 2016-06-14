package com.example.tanishka.picit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Tanishka on 11-06-2016.
 */
public class StartupReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isOn=QueryPreferences.isAlarmOn(context);
        PollService.setSysytemService(context,isOn);
    }
}
