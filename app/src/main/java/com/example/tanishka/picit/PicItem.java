package com.example.tanishka.picit;

import android.net.Uri;

/**
 * Created by Tanishka on 31-05-2016.
 */
public class PicItem {

    public String getmOwner() {
        return mOwner;
    }

    public void setmOwner(String mOwner) {
        this.mOwner = mOwner;
    }

    private String mOwner;

    public Uri getPhotoUri(){
        return Uri.parse("http://www.flickr.com/photos/")
                .buildUpon()
                .appendPath(mOwner)
                .appendPath(mId)
                .build();

    }
    public String getmCaption() {
        return mCaption;
    }

    private String mCaption;

    public String getmId() {
        return mId;
    }

    private String mId;

    public String getmUrl() {
        return mUrl;
    }

      private String mUrl;

    @Override
    public String toString() {
        return  mCaption;
    }

    public void setmCaption(String mCaption) {
        this.mCaption = mCaption;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }


}
