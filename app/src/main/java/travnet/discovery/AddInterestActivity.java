package travnet.discovery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddInterestActivity extends AppCompatActivity {

    ArrayList<String> interests;
    ArrayList<String> selectedIntersets;
    AddInterestItemAdapter addInterestItemAdapter;
    RecyclerView listInterests;

    int minInterestRequired;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_interest);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        interests = new ArrayList<>();
        selectedIntersets = new ArrayList<>();
        addInterestItemAdapter = new AddInterestItemAdapter(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            minInterestRequired = extras.getInt("minimum_required", 0);
            if (extras.containsKey("interests")) {
                ArrayList<String> userInterests = (ArrayList <String>) extras.getSerializable("interests");
                selectedIntersets.addAll(userInterests);
            }

        } else {
            minInterestRequired = 0;
        }


        Backend.getInstance().getIntersets(Backend.getInstance().new GetInterestsListener() {
            @Override
            public void onInterestsFetched(ArrayList<String> listInterests) {
                interests.addAll(listInterests);
                addInterestItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onGetInterestsFailed() {
            }
        });



        //Stub
        //interests.addAll(Arrays.asList("Surfing", "Diving", "Biking", "Yoga", "Kite Surfing", "Sightseeing"));


        listInterests = (RecyclerView) findViewById(R.id.list_interests);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listInterests.setLayoutManager(linearLayoutManager);
        listInterests.setAdapter(addInterestItemAdapter);

        FloatingActionButton buttonAdd = (FloatingActionButton) findViewById(R.id.add);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnInterests();
            }
        });

    }

    private class AddInterestItemAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder> {

        private LayoutInflater inflater;
        private DisplayImageOptions options;


        AddInterestItemAdapter(Context context) {
            inflater = LayoutInflater.from(context);

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
            return interests.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_add_interest, parent, false);
            CardAddInterestItemViewHolder cardAddInterestItemViewHolder = new CardAddInterestItemViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CheckBox)v.findViewById(R.id.interest_select)).toggle();
                }
            });
            return cardAddInterestItemViewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            CardAddInterestItemViewHolder cardBucketListItemViewHolder = (CardAddInterestItemViewHolder) holder;
            cardBucketListItemViewHolder.interestName.setText(interests.get(position));
            if (selectedIntersets.contains(interests.get(position))) {
                cardBucketListItemViewHolder.isSelected.setChecked(true);
            }


        }
    }


    private class CardAddInterestItemViewHolder extends RecyclerView.ViewHolder {
        View cardView;
        TextView interestName;
        CheckBox isSelected;

        public CardAddInterestItemViewHolder(View itemView) {
            super(itemView);
            cardView = itemView;

            interestName = (TextView) itemView.findViewById(R.id.interest_name);
            isSelected = (CheckBox) itemView.findViewById(R.id.interest_select);

        }
    }



    private void returnInterests() {
        //Browse through list to find selected interests
        selectedIntersets.clear();

        for (int i=0; i<listInterests.getChildCount(); i++){
            View view = listInterests.getChildAt(i);
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.interest_select);
            if (checkBox.isChecked() == true) {
                String interestName = ((TextView)view.findViewById(R.id.interest_name)).getText().toString();
                selectedIntersets.add(interestName);
            }
        }

        if (selectedIntersets.size() >= minInterestRequired) {
            User.getInstance().setInterests(selectedIntersets);

            Intent returnIntent = new Intent();
            returnIntent.putExtra("interests", selectedIntersets);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        } else {
            String msg = "Please select " + minInterestRequired + "interests";
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        }

    }

}
