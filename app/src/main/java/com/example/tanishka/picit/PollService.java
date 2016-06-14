package com.example.tanishka.picit;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.List;

/**
 * Created by Tanishka on 10-06-2016.
 */
public class PollService extends IntentService{
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param context Used to name the worker thread, important only for debugging.
     */
    private static final String name="PollService";
    private static final long POLL_INTERVAL= AlarmManager.INTERVAL_FIFTEEN_MINUTES;
    public static final String ACTION_SHOW_NOTIFICATION="com.example.tanishka.picit.SHOW_NOTIFICATION";
    public static final String PERM_PERMISSION="com.example.tanishka.picit.PRIVATE";
    public static final String REQUEST_CODE="REQUEST_CODE";
    public static final String NOTIFICATION="NOTIFICATION";
    public static Intent newIntent(Context context){
        return  new Intent(context,PollService.class);
    }
    public PollService() {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
         if (!isNetworkAvailableAndConnected())
             return;
     String query=QueryPreferences.getStoredQuery(this);
     String lastResultId=QueryPreferences.getLastResultId(this);
        List<PicItem> items;

        if (query==null)
                   items=new FlikrFetchr().fetchPhotos();
        else
                   items= new FlikrFetchr().searchPhotos(query);
        if (items.size()==0)
            return;

        String res_id=items.get(0).getmId();
        if (res_id.equals(lastResultId)){
            Log.i("PollService","Got old result"+res_id);

        }
        else {
            Log.i("Pollservie", "Got new Result" + res_id);

            Intent i = Pic.newIntent(this);
            PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker("New Picture")
                    .setSmallIcon(R.drawable.image)
                    .setContentTitle("New Picture")
                    .setContentText("You have new Pictures")
                    .setContentIntent(pi)
                    .setAutoCancel(true).build();
             showBackgroundNotification(0,notification);
        }

        QueryPreferences.setStoredId(this,res_id);

    }
 public void showBackgroundNotification(int requestCode,Notification notification){
     Intent i=new Intent(ACTION_SHOW_NOTIFICATION);
     i.putExtra(REQUEST_CODE,requestCode);
     i.putExtra(NOTIFICATION,notification);
     sendOrderedBroadcast(i,PERM_PERMISSION,null,null, Activity.RESULT_OK,null,null);
 }

    public boolean isNetworkAvailableAndConnected(){
     ConnectivityManager cm=(ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
     boolean isNetworkAvailable=cm.getActiveNetworkInfo()!=null;
     boolean isNetworkConnected=isNetworkAvailable&&cm.getActiveNetworkInfo().isAvailable();
     return isNetworkConnected;
 }
public static void setSysytemService(Context context,boolean isOn){
    Intent i=PollService.newIntent(context);
    PendingIntent pendingIntent=PendingIntent.getService(context,0,i,0);
    AlarmManager alarmManager=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    if(isOn)
    {
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),POLL_INTERVAL,pendingIntent);
    }
    else
    {
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }
  QueryPreferences.setAlarmOn(context,isOn);
}
public static boolean isServiceAlarmOn(Context context){
    Intent intent=PollService.newIntent(context);
    PendingIntent pm=PendingIntent.getService(context,0,intent,PendingIntent.FLAG_NO_CREATE);
    return pm!=null;
 }

}
