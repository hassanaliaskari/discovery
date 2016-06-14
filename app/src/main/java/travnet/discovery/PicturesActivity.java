package travnet.discovery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

public class PicturesActivity extends BaseNavDrawerActivity {

    ArrayList<DataPictureCard> userPictures;
    ImageAdapter imageadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_pictures, frameLayout);
        updateNavDrawerHeader();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userPictures = new ArrayList<DataPictureCard>();
        imageadapter = new ImageAdapter();
        RecyclerView gridUserPictures = (RecyclerView) findViewById(R.id.grid_user_pictures);

        Backend.getInstance().getUserPictures(Backend.getInstance().new GetUserPicturesListener() {
            @Override
            public void onUserPicturesFetched(ArrayList<DataPictureCard> userPictures) {
                addUserPictures(userPictures);
            }

            @Override
            public void onGetUserPicturesFailed() {
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        gridUserPictures.setLayoutManager(gridLayoutManager);
        gridUserPictures.setAdapter(imageadapter);
    }

    @Override
    protected void onResume() {
        navigationView.getMenu().findItem(R.id.nav_profile_photos).setChecked(true);
        super.onResume();
    }


    private void addUserPictures(ArrayList<DataPictureCard> userPictures) {
        this.userPictures.clear();
        this.userPictures.addAll(userPictures);
        imageadapter.notifyDataSetChanged();
    }


    private class ImageAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder> {

        private DisplayImageOptions options;

        ImageAdapter() {
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.mipmap.ic_loading)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .build();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return userPictures.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_user_picture, parent, false);
            CardUserPictureViewHolder cardUserPictureViewHolder = new CardUserPictureViewHolder(view);
            return cardUserPictureViewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            CardUserPictureViewHolder cardPictureViewHolder = (CardUserPictureViewHolder) holder;
            ImageLoader.getInstance().displayImage(userPictures.get(position).link, cardPictureViewHolder.image, options, null);
        }
    }


    private class CardUserPictureViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public CardUserPictureViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }





}
