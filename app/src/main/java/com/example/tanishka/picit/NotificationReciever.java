package com.example.tanishka.picit;

import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;

/**
 * Created by Tanishka on 12-06-2016.
 */
public class NotificationReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      if(getResultCode()!= Activity.RESULT_OK)
          return;
        int requestCode=intent.getIntExtra(PollService.REQUEST_CODE,0);
        Notification notification= intent.getParcelableExtra(PollService.NOTIFICATION);
        NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(requestCode,notification);
    }
}
