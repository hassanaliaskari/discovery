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

        //Stub


        ArrayList<String> links1 = new ArrayList<>();
        links1.add("http://science-all.com/images/wallpapers/nature/nature-25.jpg");
        links1.add("http://www.amaraholisticwellbeing.com/wp-content/uploads/2015/01/Fall-beautiful-nature-22666764-900-562.jpg");
        ArrayList<String> headers1 = new ArrayList<>();
        headers1.add("test1");
        headers1.add("test2");
        DataBucketListCard data1 = new DataBucketListCard("Bali, Indonesia", links1, headers1);

        ArrayList<String> links2 = new ArrayList<>();
        links2.add("https://capfor.files.wordpress.com/2012/07/beautiful-forest-beautiful-day-forests-grass-green-light-nature-sunshine-trees.jpg");
        DataBucketListCard data2 = new DataBucketListCard("Lombok, Indonesia", links2, headers1);

        ArrayList<String> links3 = new ArrayList<>();
        links3.add("http://hdwpro.com/wp-content/uploads/2015/12/nature-thailand.jpg");
        links3.add("http://webneel.com/wallpaper/sites/default/files/images/04-2013/mediterranean-beach-wallpaper.jpg");
        links3.add("https://i.ytimg.com/vi/9Nwn-TZfFUI/maxresdefault.jpg");
        links3.add("http://tedytravel.com/wp-content/uploads/2016/04/Krabi-8.jpg");
        DataBucketListCard data3 = new DataBucketListCard("Krabi, Thailand", links3, headers1);
        userBucketList.add(data1);
        userBucketList.add(data2);
        userBucketList.add(data3);


        emptyStateLayout = (LinearLayout) findViewById(R.id.empty_state_layout);
        emptyStateLayout.setVisibility(View.GONE);
        if (userBucketList.size() == 0) {
            emptyStateLayout.setVisibility(View.VISIBLE);
        }


        RecyclerView userBucketList = (RecyclerView) findViewById(R.id.user_bucket_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        userBucketList.setLayoutManager(linearLayoutManager);
        userBucketList.setAdapter(bucketListItemAdapter);

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
            BucketListBlogAdapter bucketListBlogAdapter = new BucketListBlogAdapter(userBucketList.get(position).headers, userBucketList.get(position).pictures);
            cardBucketListItemViewHolder.blogs.setAdapter(bucketListBlogAdapter);

        }
    }


    private class BucketListPictureAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder> {
        List<String> pictures;

        public BucketListPictureAdapter(List<String> pictures){
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
            ImageLoader.getInstance().displayImage(pictures.get(position), cardBucketListPictureViewHolder.image);
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
        List<String> pictures;
        List<String> headers;

        public BucketListBlogAdapter(List<String> headers, List<String> pictures){
            super();
            this.headers = headers;
            this.pictures = pictures;
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
            ImageLoader.getInstance().displayImage(pictures.get(position), cardBucketListBlogViewHolder.thumbnail);
        }

        @Override
        public int getItemCount() {
            return pictures.size();
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
