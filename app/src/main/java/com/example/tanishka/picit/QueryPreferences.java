package com.example.tanishka.picit;

import android.app.DownloadManager;
import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Tanishka on 09-06-2016.
 */
public class QueryPreferences {
    private static final String PREF_SEARCH_QUERY="searchQuery";
    private static final String  LastResultId="lastResultId";
    private static final String PREF_ALARM="isAlarmOn";
    public static String getStoredQuery(Context context){
       return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_SEARCH_QUERY,null);
   }
   public static void setStoredQuery(Context context, String Query){
       PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_SEARCH_QUERY,Query).apply();
   }
  public static String getLastResultId(Context context) {
      return PreferenceManager.getDefaultSharedPreferences(context).getString(LastResultId,null);
  }
  public static void setStoredId(Context context,String lastResultId){
      PreferenceManager.getDefaultSharedPreferences(context).edit().putString(LastResultId,lastResultId).apply();
  }
    public static boolean isAlarmOn(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_ALARM,false);
    }
    public static void setAlarmOn(Context context,boolean isOn){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PREF_ALARM,isOn).apply();
    }
}
