package travnet.discovery;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InterestActivity extends BaseNavDrawerActivity {

    private static final int REQUEST_ADD_INTEREST = 1;

    List<String> userInterests;
    SwipeMenuListView listViewInterests;
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_interest, frameLayout);
        updateNavDrawerHeader();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userInterests = new ArrayList<String>();
        listViewInterests = (SwipeMenuListView) findViewById(R.id.list_interests);

        initializeListView();
        userInterests.addAll(User.getInstance().getInterests());

        //Handle case where user interests have not been updated

        FloatingActionButton fabAddInterest = (FloatingActionButton) findViewById(R.id.add_interest);
        fabAddInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddInterestActivity();
            }
        });

    }

    @Override
    protected void onResume() {
        navigationView.getMenu().findItem(R.id.nav_profile_interests).setChecked(true);
        super.onResume();
    }



    private void initializeListView() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // Create delete button
                SwipeMenuItem deleteMenuItem = new SwipeMenuItem(
                        getApplicationContext());
                deleteMenuItem.setBackground(new ColorDrawable(Color.rgb(0x00,0x00, 0x00)));
                deleteMenuItem.setWidth(200);
                deleteMenuItem.setIcon(R.drawable.ic_delete);
                menu.addMenuItem(deleteMenuItem);
            }
        };

        listViewInterests.setMenuCreator(creator);
        listViewInterests.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        listViewInterests.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        deleteInterest(position);
                        break;
                }
                return false;
            }
        });

        adapter = new ArrayAdapter<String>(this, R.layout.card_interest, R.id.interest_name, userInterests);
        listViewInterests.setAdapter(adapter);
    }



    private void startAddInterestActivity() {
        Intent intent = new Intent(this, AddInterestActivity.class);
        intent.putExtra("interests", (Serializable) userInterests);
        this.startActivityForResult(intent, REQUEST_ADD_INTEREST);
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_INTEREST && resultCode == RESULT_OK) {
            ArrayList<String> interestsToAdd = (ArrayList<String>) data.getSerializableExtra("interests");
            updateInterests(interestsToAdd);
        }

    }

    private void deleteInterest(int position) {
        userInterests.remove(position);
        adapter.notifyDataSetChanged();
    }


    private void updateInterests(ArrayList<String> interestsToAdd) {
        userInterests.clear();
        userInterests.addAll(interestsToAdd);
        adapter.notifyDataSetChanged();
    }

    private void updateBackend() {
        Backend backend = Backend.getInstance();
        backend.updateUserInterests(backend.new UpdateUserInterestsListener() {
            @Override
            public void onInterestsUpdated() {
            }

            @Override
            public void onInterestsUpdateFailed() {
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_complete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_done) {
            User.getInstance().setInterests(userInterests);
            updateBackend();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
