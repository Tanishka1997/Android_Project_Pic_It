package com.example.tanishka.picit;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tanishka on 31-05-2016.
 */
public class FlikrFetchr {



   List<PicItem> items;
    public static final String KEY="your api key";
    private static final  String Fetch_Recents_Method="flickr.photos.getRecent";
    private static final String Search_Method="flickr.photos.search";
    public static String page_no;
    private static final Uri ENDPOINT=Uri.parse( "https://api.flickr.com/services/rest/").buildUpon().appendQueryParameter("api_key",KEY)
            .appendQueryParameter("format","json").appendQueryParameter("nojsoncallback","1").appendQueryParameter("extras","url_s").build();

    public byte[] getUrlBytes(String urlSpec) throws IOException{

        URL url=new URL(urlSpec);
        HttpURLConnection connection=(HttpURLConnection) url.openConnection();
        try {
            InputStream in=connection.getInputStream();
            ByteArrayOutputStream out=new ByteArrayOutputStream();
            if(connection.getResponseCode()!=HttpURLConnection.HTTP_OK)
            {
                throw new IOException(connection.getResponseMessage()+"with:"+urlSpec);
            }
         int bytesRead=0;
            byte[] buffer=new byte[1024];
            while ((bytesRead=in.read(buffer))>0){
                out.write(buffer,0,bytesRead);
            }
         out.close();
            return out.toByteArray();
        }
        finally {
          connection.disconnect();
        }

    }
   public String getUrlString(String urlSpec) throws IOException{
       return new String(getUrlBytes(urlSpec));
   }

   private String BuildUrl(String method,String query){
       Uri.Builder builder=ENDPOINT.buildUpon().appendQueryParameter("method",method).appendQueryParameter("page",page_no);
       if (method.equals(Search_Method)){
           builder.appendQueryParameter("text",query);
       }
    return builder.build().toString();
   }
  public List<PicItem> fetchPhotos(){
      String url=BuildUrl(Fetch_Recents_Method,null);
      return downloadGalleryItems(url);
  }
 public List<PicItem> searchPhotos(String query){
     String url=BuildUrl(Search_Method,query);
     return downloadGalleryItems(url);
 }


    private List<PicItem> downloadGalleryItems(String url)
  {items=new ArrayList<>();
      try {

          String jsonString = getUrlString(url);
          JSONObject jsonBody=new JSONObject(jsonString);
          parseItems(jsonBody,items);
      }
      catch (JSONException e) {
          e.printStackTrace();
          Log.i("Error","Error");
      }
      catch (IOException e) {
          e.printStackTrace();
          Log.i("Error","Error");
      }
     return items;
  }
 public void parseItems(JSONObject jsonBody,List<PicItem> items) throws IOException,JSONException{
     JSONObject object=jsonBody.getJSONObject("photos");
     JSONArray array=object.getJSONArray("photo");
     for(int i=0;i<array.length();i++){
         PicItem item=new PicItem();
         item.setmCaption(array.getJSONObject(i).getString("title"));
         item.setmId(array.getJSONObject(i).getString("id"));
         if (!array.getJSONObject(i).has("url_s"))
             continue;
         item.setmUrl(array.getJSONObject(i).getString("url_s"));
         item.setmOwner(array.getJSONObject(i).getString("owner"));
         items.add(item);
     }
 }
}
