package com.example.tanishka.picit;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class Pic extends AppCompatActivity {

    private PicFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic);
        FragmentManager manager = getSupportFragmentManager();
        fragment=(PicFragment) manager.findFragmentByTag("PicFragment");
        if (fragment==null) {
            fragment = new PicFragment();
        }
        manager.beginTransaction().replace(R.id.frame, fragment, "PicFragment").commit();

    }



    public static Intent newIntent(Context context){
      return new Intent(context,Pic.class);
  }
}