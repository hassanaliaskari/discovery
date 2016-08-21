package travnet.discovery;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class BucketListActivity extends BaseNavDrawerActivity {

    List<DataBucketListCard> userBucketList;
    BucketListItemAdapter bucketListItemAdapter;
    LinearLayout emptyStateLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_bucket_list, frameLayout);
        updateNavDrawerHeader();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setToolbarToggle(toolbar);


        userBucketList = new ArrayList<>();
        bucketListItemAdapter = new BucketListItemAdapter(this);

        emptyStateLayout = (LinearLayout) findViewById(R.id.empty_state_layout);
        emptyStateLayout.setVisibility(View.GONE);

        RecyclerView userBucketListView = (RecyclerView) findViewById(R.id.user_bucket_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        userBucketListView.setLayoutManager(linearLayoutManager);
        userBucketListView.setAdapter(bucketListItemAdapter);

        Backend.getInstance().getBucketList(Backend.getInstance().new GetBucketListListener() {
            @Override
            public void onBucketListFetched(List<DataBucketListCard> bucketList) {
                if (bucketList.size() == 0) {
                    emptyStateLayout.setVisibility(View.VISIBLE);
                }
                userBucketList.addAll(bucketList);
                bucketListItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onGetBucketListFailed() {

            }
        });

    }

    @Override
    protected void onResume() {
        navigationView.getMenu().findItem(R.id.nav_profile_bucket_list).setChecked(true);
        super.onResume();
    }


    private class BucketListItemAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder> {

        private LayoutInflater inflater;
        private DisplayImageOptions options;


        BucketListItemAdapter(Context context) {
            inflater = LayoutInflater.from(context);

            options = new DisplayImageOptions.Builder()
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
            return userBucketList.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_bucket_list_item, parent, false);
            CardBucketListItemViewHolder cardBucketListItemViewHolder = new CardBucketListItemViewHolder(view);
            return cardBucketListItemViewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            final CardBucketListItemViewHolder cardBucketListItemViewHolder = (CardBucketListItemViewHolder) holder;
            cardBucketListItemViewHolder.location.setText(userBucketList.get(position).location);
            cardBucketListItemViewHolder.setListener();

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            cardBucketListItemViewHolder.pictures.setVisibility(View.GONE);
            cardBucketListItemViewHolder.pictures.setLayoutManager(linearLayoutManager);
            BucketListPictureAdapter bucketListPictureAdapter = new BucketListPictureAdapter(userBucketList.get(position).pictures);
            cardBucketListItemViewHolder.pictures.setAdapter(bucketListPictureAdapter);

            LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getApplicationContext());
            linearLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
            cardBucketListItemViewHolder.blogs.setVisibility(View.GONE);
            cardBucketListItemViewHolder.blogs.setLayoutManager(linearLayoutManager2);
            BucketListBlogAdapter bucketListBlogAdapter = new BucketListBlogAdapter(userBucketList.get(position).blogs);
            cardBucketListItemViewHolder.blogs.setAdapter(bucketListBlogAdapter);

        }
    }


    private class BucketListPictureAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder> {
        List<DataPictureCard> pictures;

        public BucketListPictureAdapter(List<DataPictureCard> pictures){
            super();
            this.pictures = pictures;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_bucket_list_picture, parent, false);
            CardBucketListPictureViewHolder cardBucketListPictureViewHolder = new CardBucketListPictureViewHolder(view);
            return cardBucketListPictureViewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            CardBucketListPictureViewHolder cardBucketListPictureViewHolder = (CardBucketListPictureViewHolder) holder;
            ImageLoader.getInstance().displayImage(pictures.get(position).link, cardBucketListPictureViewHolder.image);
        }

        @Override
        public int getItemCount() {
            return pictures.size();
        }
    }



    public class CardBucketListPictureViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public CardBucketListPictureViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }





    private class BucketListBlogAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder> {
        List<DataBlogCard> blogs;

        public BucketListBlogAdapter(List<DataBlogCard> blogs){
            super();
            this.blogs = blogs;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_bucket_list_blog, parent, false);
            CardBucketListBlogViewHolder cardBucketListBlogViewHolder = new CardBucketListBlogViewHolder(view);
            return cardBucketListBlogViewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            CardBucketListBlogViewHolder cardBucketListBlogViewHolder = (CardBucketListBlogViewHolder) holder;
            ImageLoader.getInstance().displayImage(blogs.get(position).thumbnail_url, cardBucketListBlogViewHolder.thumbnail);
            cardBucketListBlogViewHolder.title.setText(blogs.get(position).title);
        }

        @Override
        public int getItemCount() {
            return blogs.size();
        }
    }

    public class CardBucketListBlogViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView title;

        public CardBucketListBlogViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }





    private class CardBucketListItemViewHolder extends RecyclerView.ViewHolder {
        View cardView;
        RecyclerView pictures;
        RecyclerView blogs;
        TextView location;
        View divider;

        public CardBucketListItemViewHolder(View itemView) {
            super(itemView);
            cardView = itemView;
            pictures  = (RecyclerView) itemView.findViewById(R.id.user_bucket_list_pictures);
            blogs= (RecyclerView) itemView.findViewById(R.id.user_bucket_list_blogs);
            location = (TextView) itemView.findViewById(R.id.location);
            divider = itemView.findViewById(R.id.divider);

        }

        public void setListener() {
            this.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pictures.getVisibility() == View.GONE) {
                        pictures.setVisibility(View.VISIBLE);
                        blogs.setVisibility(View.VISIBLE);
                        divider.setVisibility(View.GONE);
                    } else {
                        pictures.setVisibility(View.GONE);
                        blogs.setVisibility(View.GONE);
                        divider.setVisibility(View.VISIBLE);
                    }
                }
            });
        }


    }



}
