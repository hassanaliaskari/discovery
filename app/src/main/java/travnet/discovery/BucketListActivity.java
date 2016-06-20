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

import java.util.ArrayList;
import java.util.List;

public class BucketListActivity extends BaseNavDrawerActivity {

    List<DataBucketListCard> userBucketList;
    BucketListItemAdapter bucketListItemAdapter;

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
        DataBucketListCard data1 = new DataBucketListCard("Bali, Indonesia", links1);

        ArrayList<String> links2 = new ArrayList<>();
        links2.add("https://capfor.files.wordpress.com/2012/07/beautiful-forest-beautiful-day-forests-grass-green-light-nature-sunshine-trees.jpg");
        DataBucketListCard data2 = new DataBucketListCard("Lombok, Indonesia", links2);

        ArrayList<String> links3 = new ArrayList<>();
        links3.add("http://hdwpro.com/wp-content/uploads/2015/12/nature-thailand.jpg");
        links3.add("http://webneel.com/wallpaper/sites/default/files/images/04-2013/mediterranean-beach-wallpaper.jpg");
        links3.add("https://i.ytimg.com/vi/9Nwn-TZfFUI/maxresdefault.jpg");
        links3.add("http://tedytravel.com/wp-content/uploads/2016/04/Krabi-8.jpg");
        DataBucketListCard data3 = new DataBucketListCard("Krabi, Thailand", links3);
        userBucketList.add(data1);
        userBucketList.add(data2);
        userBucketList.add(data3);


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
            cardBucketListItemViewHolder.scrollView.setVisibility(View.GONE);
            populateScrollView(cardBucketListItemViewHolder.locationPictures, userBucketList.get(position).pictures);
            cardBucketListItemViewHolder.setListener();
        }
    }


    private void populateScrollView(LinearLayout layout, List<String> pictures) {
        for (int i=0; i<pictures.size(); i++) {
            ImageView imageView = new ImageView(BucketListActivity.this);
            imageView.setBackgroundResource(R.drawable.placeholder_loading);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(100), dpToPx(100));
            if (i==0) {
                params.setMargins(dpToPx(16),0,0,0);
            } else {
                params.setMargins(dpToPx(4), 0, 0, 0);
            }
            imageView.setLayoutParams(params);
            layout.addView(imageView);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImageLoader.getInstance().displayImage(pictures.get(i), imageView);
        }
    }

    private int dpToPx(int dp){
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private class CardBucketListItemViewHolder extends RecyclerView.ViewHolder {
        View cardView;
        HorizontalScrollView scrollView;
        LinearLayout locationPictures;
        TextView location;
        View divider;

        public CardBucketListItemViewHolder(View itemView) {
            super(itemView);
            cardView = itemView;
            scrollView = (HorizontalScrollView) itemView.findViewById(R.id.scrollview);
            locationPictures = (LinearLayout) scrollView.findViewById(R.id.location_pictures);
            location = (TextView) itemView.findViewById(R.id.location);
            divider = itemView.findViewById(R.id.divider);

        }

        public void setListener() {
            this.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (scrollView.getVisibility() == View.GONE) {
                        scrollView.setVisibility(View.VISIBLE);
                        divider.setVisibility(View.GONE);
                    } else {
                        scrollView.setVisibility(View.GONE);
                        divider.setVisibility(View.VISIBLE);
                    }
                }
            });
        }


    }



}
