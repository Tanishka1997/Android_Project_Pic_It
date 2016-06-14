package com.example.tanishka.picit;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.widget.Toast;

/**
 * Created by Tanishka on 11-06-2016.
 */
public abstract class VisibleClass extends Fragment{
    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter=new IntentFilter(PollService.ACTION_SHOW_NOTIFICATION);
        getActivity().registerReceiver(mNotification,filter,PollService.PERM_PERMISSION,null);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mNotification);
    }
 private BroadcastReceiver mNotification=new BroadcastReceiver() {
     @Override
     public void onReceive(Context context, Intent intent) {
         setResultCode(Activity.RESULT_CANCELED);
     }
 };
}
