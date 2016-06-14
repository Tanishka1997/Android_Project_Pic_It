package com.example.tanishka.picit;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.GpsStatus;
import android.os.Build;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Handler;

/**
 * Created by Tanishka on 02-06-2016.
 */
public class ThumbnailDownloader<T> extends HandlerThread {

    private static final int MESSAGE_WHAT=0;
    private static final String name="ThumbnailDownloader";
    private android.os.Handler mRequestHandler;
    private ConcurrentHashMap<T,String> hashMap=new ConcurrentHashMap<>();
    private ConcurrentHashMap<T,String> hashMap2=new ConcurrentHashMap<>();
     private ThumbnailDownloadListener<T> mthumbnaildownloadlistener;
    private android.os.Handler mResponseHandler;
    private LruCache<String,Bitmap> mMemeoryCache;


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public ThumbnailDownloader(android.os.Handler handler) {
        super(name);
        mResponseHandler=handler;
        final int maxmemory=(int) Runtime.getRuntime().maxMemory();
        int cacheSize=maxmemory/8;
        mMemeoryCache=new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return  value.getByteCount();
            }
        };


    }
   @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
   public void add(String key, Bitmap value){
       if(getBitmap(key)==null)
           mMemeoryCache.put(key,value);
   }
   @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
   public Bitmap getBitmap(String key){
       return mMemeoryCache.get(key);
   }
    public  Bitmap loadBitmap(String Id){
        return getBitmap(Id);
    }
    public void queueThumbnail(T target,String url,String id){
         if(url==null)
         {hashMap.remove(target);hashMap2.remove(target);}
       else
         {
             hashMap.put(target,url);
             hashMap2.put(target,id);
             mRequestHandler.obtainMessage(MESSAGE_WHAT,target).sendToTarget();

         }
   }

    public  interface ThumbnailDownloadListener<T>{
         void onThumbnailDownloaded(T target,Bitmap bitmap);
    }

    public void setThumbnailDownlodListener(ThumbnailDownloadListener<T> listener){
         mthumbnaildownloadlistener=listener;
    }
    @Override
    protected void onLooperPrepared() {
         mRequestHandler=new android.os.Handler(){
             @Override
             public void handleMessage(Message msg) {
                 if(msg.what==MESSAGE_WHAT){
                     Log.i(name,"1234");
                     T target= (T) msg.obj;
                 handler(target);
                 }
             }
         };

    }
  public void  handler(final T target){

      try {
          final String url=hashMap.get(target);
          final String Id=hashMap2.get(target);
          final Bitmap bitmap;
              if (url == null)
                  return;
              if (getBitmap(Id)==null)
              { byte[] BitmapBytes = new FlikrFetchr().getUrlBytes(url);
               bitmap = BitmapFactory.decodeByteArray(BitmapBytes, 0, BitmapBytes.length);}

          else
          {  bitmap= loadBitmap(Id);}
                 mResponseHandler.post(new Runnable() {
                  @Override
                  public void run() {
                      if (hashMap.get(target) != url) {
                          return;
                      }
                      hashMap.remove(target);
                      hashMap2.remove(target);
                      add(Id, bitmap);
                      mthumbnaildownloadlistener.onThumbnailDownloaded(target, bitmap);

                  }
                 });



      } catch (IOException e) {
          e.printStackTrace();
      }

      }
 public void clearQueue(){
     mRequestHandler.removeMessages(MESSAGE_WHAT);
 }
}
