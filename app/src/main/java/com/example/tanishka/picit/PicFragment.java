package com.example.tanishka.picit;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PicFragment extends VisibleClass {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private ThumbnailDownloader<Holder> mThumbnailDownloader;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters


   int a,b;
    private String mParam1;
    private String mParam2;
    List<PicItem> mPicItems=new ArrayList<>();
    RecyclerView recyclerView;
    SearchView searchView;


    public PicFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment PicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PicFragment newInstance() {
        PicFragment fragment = new PicFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState!=null)
            a=savedInstanceState.getInt("a");
        else
        a=1;
        FlikrFetchr.page_no="1";
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        setHasOptionsMenu(true);
        setRetainInstance(true);
        update();
        Handler handler=new Handler();
        mThumbnailDownloader=new ThumbnailDownloader<>(handler);

       mThumbnailDownloader.setThumbnailDownlodListener(new ThumbnailDownloader.ThumbnailDownloadListener<Holder>() {
           @Override
           public void onThumbnailDownloaded(Holder target, Bitmap bitmap) {
               Drawable drawable=new BitmapDrawable(getResources(),bitmap);
               target.bind(drawable);
           }
       });
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();


    }

    public void update(){
        String query=QueryPreferences.getStoredQuery(getActivity());
        new FetchItemsTask(query).execute();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_implement,menu);
        final MenuItem menuItem=menu.findItem(R.id.menu_item_search);
        searchView=(SearchView) menuItem.getActionView();
        searchView.setOnSearchClickListener(new SearchView.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query=QueryPreferences.getStoredQuery(getActivity());
                searchView.setQuery(query,false);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public boolean onQueryTextSubmit(String query) {
                QueryPreferences.setStoredQuery(getActivity(),query);
                a=1;
                FlikrFetchr.page_no=1+"";
                update();
                View v=getActivity().getCurrentFocus();
                if(v!=null)
                {
                    InputMethodManager inputMethodManager=(InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),0);
                }

                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
       MenuItem item=menu.findItem(R.id.start_polling);
        if (PollService.isServiceAlarmOn(getActivity()))
            item.setTitle("Stop Polling");
        else
            item.setTitle("Start Polling");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_clear:
                QueryPreferences.setStoredQuery(getActivity(), null);
                a=1;
                FlikrFetchr.page_no="1";
                update();
                searchView.setQuery(null,false);
                getActivity().invalidateOptionsMenu();
                return true;
            case R.id.start_polling:
                boolean sholdStartAlarm=!PollService.isServiceAlarmOn(getActivity());
                PollService.setSysytemService(getActivity(),sholdStartAlarm);
                getActivity().invalidateOptionsMenu();
                return true;
            case R.id.refresh:
                QueryPreferences.setStoredQuery(getActivity(), null);
                update();
                return true;

            default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_pic, container, false);
        recyclerView=(RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy>0){
                    super.onScrolled(recyclerView, dx, dy);

                 if (!recyclerView.canScrollVertically(dy)){
                     a=Integer.parseInt(FlikrFetchr.page_no);
                     a=a+1;
                     FlikrFetchr.page_no=""+a;
                     update();
                 }
                }
              if (dy<0){
                  super.onScrolled(recyclerView, dx, dy);

                  if (!recyclerView.canScrollVertically(dy)){

                      a=Integer.parseInt(FlikrFetchr.page_no);
                      if (a!=1){
                          a=a-1;
                          FlikrFetchr.page_no=""+a;
                      update();}
                  }
              }
            }
        });
        return view;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.clearQueue();
        mThumbnailDownloader.quit();
    }

    public class Holder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        ImageView imageView;
        private PicItem mpicItem;
        public Holder(View itemView) {
            super(itemView);
            imageView=(ImageView) itemView.findViewById(R.id.no_image);
            itemView.setOnClickListener(this);

        }

        public void bind(Drawable drawable) {
        imageView.setImageDrawable(drawable);
        }

        private void bindGalleryItem(PicItem item){
            mpicItem=item;
        }
        @Override
        public void onClick(View v) {
         Intent intent=PhotoPageActivity.newIntent(getActivity(),mpicItem.getPhotoUri());
            startActivity(intent);
        }
    }
        private class picAdapter extends RecyclerView.Adapter<Holder>{
            List<PicItem> picItems;

        public picAdapter(List<PicItem> mpicItems) {
            this.picItems = mpicItems;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater=LayoutInflater.from(getActivity());
           View view= layoutInflater.inflate(R.layout.gallery_item,parent,false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            PicItem item=picItems.get(position);
            holder.bindGalleryItem(item);
            Drawable placeholder=getResources().getDrawable(R.drawable.no_image);
            mThumbnailDownloader.queueThumbnail(holder,item.getmUrl(),item.getmId());
            holder.bind(placeholder);
        }

        @Override
        public int getItemCount() {
            return picItems.size();
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("a",a);
    }

    private class FetchItemsTask extends AsyncTask<Void,Void,List<PicItem>> {

    private String query;
    private ProgressDialog mProgress;
    public FetchItemsTask(String mQuery)
    {
     query=mQuery;
    }

    @Override
    protected void onPreExecute() {
      if (query==null){
        if (a!=1){
        mProgress=new ProgressDialog(getActivity());
        mProgress.setIndeterminate(true);
        mProgress.setCancelable(false);
        mProgress.setMessage("Loading..........Page "+a);
        mProgress.show();}
        else
        {
            mProgress=new ProgressDialog(getActivity());
            mProgress.setIndeterminate(true);
            mProgress.setCancelable(false);
            mProgress.setMessage("Loading....");
            mProgress.show();}}
        else{
          if (a!=1){
              mProgress=new ProgressDialog(getActivity());
              mProgress.setIndeterminate(true);
              mProgress.setCancelable(false);
              mProgress.setMessage("Loading Search results....Page "+a);
              mProgress.show();}
          else
          {
              mProgress=new ProgressDialog(getActivity());
              mProgress.setIndeterminate(true);
              mProgress.setCancelable(false);
              mProgress.setMessage("Loading Search Results....");
              mProgress.show();}

      }

        super.onPreExecute();
    }

    @Override
        protected List<PicItem> doInBackground(Void... params) {

            FlikrFetchr flikrFetchr=new FlikrFetchr();

             if (query==null)
             return    flikrFetchr.fetchPhotos();
            else
             return    flikrFetchr.searchPhotos(query);
        }

    @Override
    protected void onPostExecute(List<PicItem> list) {
        mProgress.dismiss();
        mPicItems=list;
      setupAdapter();
    }
    public void setupAdapter(){
        if (isAdded())
        {recyclerView.setAdapter(new picAdapter(mPicItems));
           }
    }
}

}
