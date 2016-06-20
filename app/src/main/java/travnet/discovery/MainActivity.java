package travnet.discovery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.*;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends BaseNavDrawerActivity
        implements SignInFragment.OnFragmentInteractionListener, SignInFragment.OnLoginListener,
        HomeFragment.OnFragmentInteractionListener,
        GoogleApiClient.OnConnectionFailedListener {


    private static final int REQUEST_ADD_INTEREST = 1;

    HomeFragment homeFragment;
    SignInFragment signInFragment;
    Backend backend;
    SharedPreferences myPrefs;

    FloatingActionMenu fabmenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_main, frameLayout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setToolbarToggle(toolbar);

        FacebookSdk.sdkInitialize(this);
        backend = Backend.getInstance();
        homeFragment = new HomeFragment();

        fabmenu = (FloatingActionMenu) findViewById(R.id.fab_add);
        fabmenu.setVisibility(View.GONE);

        myPrefs = this.getSharedPreferences("login", MODE_PRIVATE);

        //Check for previous login
        SharedPreferences myPrefs = this.getSharedPreferences("login", MODE_PRIVATE);
        boolean prev_login = myPrefs.getBoolean("prev_login", false);
        String userID = myPrefs.getString("user_id", "");
        boolean isLogged = (prev_login && !userID.equals(""));

        if (isLogged) {
            User.getInstance().setUserID(userID);
            setupHomeScreen();
        } else {
            //Set Login fragment
            signInFragment = new SignInFragment();
            signInFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, signInFragment).commit();
        }

    }

    @Override
    protected void onResume() {
        navigationView.getMenu().findItem(R.id.nav_profile_home).setChecked(true);
        super.onResume();
    }


    public void onLoginSuccessful(){
        //Save login
        myPrefs.edit().putBoolean("prev_login", true).apply();
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

        //Floating action buttons
        fabmenu.setVisibility(View.VISIBLE);
        fabmenu.setClosedOnTouchOutside(true);
        fabmenu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!fabmenu.isOpened()) {
                    dimBackground();
                    fabmenu.open(true);
                } else {
                    restoreBackground();
                    fabmenu.close(true);
                }
            }
        });

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



    private void requestUserInterests() {
        Intent intent = new Intent(this, AddInterestActivity.class);
        intent.putExtra("minimum_required", 3);
        this.startActivityForResult(intent, REQUEST_ADD_INTEREST);
    }

    @Override
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


    private void dimBackground() {
        View overlay = findViewById(R.id.detailsDimView);

        if (overlay.getVisibility() == View.VISIBLE)
            return;

        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_overlay_fade_in);
        overlay.startAnimation(fadeInAnimation);

        overlay.setVisibility(View.VISIBLE);
        overlay.setClickable(true);

    }

    private void restoreBackground() {
        findViewById(R.id.detailsDimView).setVisibility(View.GONE);
        findViewById(R.id.detailsDimView).setClickable(false);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        if (fabmenu.isOpened()) {
            fabmenu.close(true);
        }

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
