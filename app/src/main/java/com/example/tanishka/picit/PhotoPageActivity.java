package com.example.tanishka.picit;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class PhotoPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_page);
        FragmentManager fm=getSupportFragmentManager();
        Fragment fragment=fm.findFragmentByTag("PhotoPage");
        if (fragment==null)
        {
            fragment=PhotoPage.newInstance(getIntent().getData());
            fm.beginTransaction().replace(R.id.photo_page,fragment,"PhotoPage").commit();
        }
    }
    public static Intent newIntent(Context context, Uri uri)
    {
        Intent intent= new Intent(context,PhotoPageActivity.class);
        intent.setData(uri);
        return intent;

    }

    @Override
    public void onBackPressed() {
    WebView webview=(WebView) findViewById(R.id.fragment_photo_page_web_view);
        assert webview != null;
        if (webview.canGoBack())
            webview.goBack();
        else
            super.onBackPressed();
    }
}
