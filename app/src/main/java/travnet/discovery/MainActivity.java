package travnet.discovery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.FacebookSdk;
import com.facebook.login.*;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements SignInFragment.OnFragmentInteractionListener, SignInFragment.OnLoginListener,
        HomeFragment.OnFragmentInteractionListener,
        GoogleApiClient.OnConnectionFailedListener,
        NavigationView.OnNavigationItemSelectedListener {


    private static final int REQUEST_ADD_INTEREST = 1;

    HomeFragment homeFragment;
    SignInFragment signInFragment;
    Backend backend;
    View navDrawerHeader;
    SharedPreferences myPrefs;

    FloatingActionMenu fabmenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(this);
        backend = Backend.getInstance();
        homeFragment = new HomeFragment();

        myPrefs = this.getSharedPreferences("login", MODE_PRIVATE);

        //Check for previous login
        boolean isLogged = getIntent().getExtras().getBoolean("isLogged");

        if (isLogged) {
            setupHomeScreen();
        } else {
            //Set Login fragment
            signInFragment = new SignInFragment();
            signInFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, signInFragment).commit();
        }

    }



    public void onLoginSuccessful(){
        //Save login
        myPrefs.edit().putBoolean("isLogged", true).apply();
        Log.i("login", User.getInstance().getUserID());

        //Remove Login Fragment
        getSupportFragmentManager().beginTransaction()
            .remove(signInFragment).commitAllowingStateLoss();
        setupHomeScreen();
    }


    public void onLoginFailed(){
        LoginManager.getInstance().logOut();
        Toast.makeText(getApplicationContext(), R.string.error_login_failed, Toast.LENGTH_LONG).show();
    }


    void setupHomeScreen() {
        homeFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, homeFragment).commitAllowingStateLoss();

        //Check if user has selected intgerests
        int userState = myPrefs.getInt("user_state", 0);
        User.getInstance().setUserState(userState);
        if (userState == 0){
            requestUserInterests();
        }


        //Initialize navigation drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navDrawerHeader = navigationView.getHeaderView(0);

        //Floating action buttons
        fabmenu = (FloatingActionMenu) findViewById(R.id.fab_add);
        fabmenu.setClosedOnTouchOutside(true);
        fabmenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if (opened) {
                    dimBackground();
                } else {
                    restoreBackground();
                }
            }
        });

        FloatingActionButton fabAddBlog = (FloatingActionButton) findViewById(R.id.fab_add_blog);
        fabAddBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabmenu.close(true);
                addBlogCard();
            }
        });
        FloatingActionButton fabAddPictureCard = (FloatingActionButton) findViewById(R.id.fab_add_picture_card);
        fabAddPictureCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabmenu.close(true);
                addPictureCard();
            }
        });

        //Get User Info from Server
        backend.getUserInfo(backend.new GetUserInfoListener() {
            @Override
            public void onUserInfoFetched() {
                Log.i("init", "user info fetched");
                updateNavDrawerHeader();
            }
            @Override
            public void onGetUserInfoFailed() {
            }
        });

    }


    void addBlogCard() {
        Intent intent = new Intent(this, AddBlogCardActivity.class);
        this.startActivity(intent);
    }

    void addPictureCard() {
        Intent intent = new Intent(this, AddPictureCardActivity.class);
        this.startActivity(intent);
    }


    void updateNavDrawerHeader() {
        ImageView profilePic = (ImageView) navDrawerHeader.findViewById(R.id.profile_pic);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(User.getInstance().getProfilePicURL(), profilePic);
        //profilePic.setImageBitmap(User.getInstance().getProfilePic());
        TextView profileName = (TextView) navDrawerHeader.findViewById(R.id.profile_name);
        profileName.setText(User.getInstance().getName());
        TextView profileHome = (TextView) navDrawerHeader.findViewById(R.id.profile_home);
        profileHome.setText(User.getInstance().getHometown());
    }


    private void requestUserInterests() {
        Intent intent = new Intent(this, AddInterestActivity.class);
        intent.putExtra("minimum_required", 3);
        this.startActivityForResult(intent, REQUEST_ADD_INTEREST);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_INTEREST) {
            User.getInstance().setUserState(1);
            myPrefs.edit().putInt("user_state", 1).apply();
            backend.updateUserInterests(Backend.getInstance().new UpdateUserInterestsListener() {
                @Override
                public void onInterestsUpdated() {
                }

                @Override
                public void onInterestsUpdateFailed() {
                }
            });
        }

    }



    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_profile_photos) {
            Intent intent = new Intent(this, PicturesActivity.class);
            this.startActivity(intent);
        } else if (id == R.id.nav_profile_interests) {
            Intent intent = new Intent(this, InterestActivity.class);
            this.startActivity(intent);
        } else if (id == R.id.nav_profile_bucket_list) {
            Intent intent = new Intent(this, BucketListActivity.class);
            this.startActivity(intent);
        } else if (id == R.id.logout) {
            LoginManager.getInstance().logOut();
            //Clear SharedPreferences
            SharedPreferences.Editor prefsEditor = myPrefs.edit();
            prefsEditor.putBoolean("isLogged", false);
            prefsEditor.commit();
            //Restart activity
            Intent intent = getIntent();
            intent.putExtra("isLogged", false);
            finish();
            startActivity(intent);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void dimBackground() {
        findViewById(R.id.detailsDimView).setVisibility(View.VISIBLE);
        findViewById(R.id.detailsDimView).setClickable(true);
    }

    private void restoreBackground() {
        findViewById(R.id.detailsDimView).setVisibility(View.GONE);
        findViewById(R.id.detailsDimView).setClickable(false);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        fabmenu.close(true);

        return super.dispatchTouchEvent(event);
    }



    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    public void onFragmentInteraction(Uri uri){
    }

    public void onListViewScrollStart(){
    }

    public void onListViewScrollStop(){
    }

}
