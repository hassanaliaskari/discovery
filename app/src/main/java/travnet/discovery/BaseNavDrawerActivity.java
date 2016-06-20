package travnet.discovery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.nostra13.universalimageloader.core.ImageLoader;

public class BaseNavDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    protected FrameLayout frameLayout;
    protected NavigationView navigationView;
    protected DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_nav_drawer);
        frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

    }



    protected void updateNavDrawerHeader() {
        View navDrawerHeader;
        navDrawerHeader = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0);

        ImageView profilePic = (ImageView) navDrawerHeader.findViewById(R.id.profile_pic);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(User.getInstance().getProfilePicURL(), profilePic);
        //profilePic.setImageBitmap(User.getInstance().getProfilePic());
        TextView profileName = (TextView) navDrawerHeader.findViewById(R.id.profile_name);
        profileName.setText(User.getInstance().getName());
        TextView profileHome = (TextView) navDrawerHeader.findViewById(R.id.profile_home);
        String name = User.getInstance().getName();
        profileHome.setText(User.getInstance().getHometown());
    }

    protected void setToolbarToggle(Toolbar toolbar) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_profile_home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            this.startActivity(intent);
        } else if (id == R.id.nav_profile_photos) {
            if (!(this instanceof PicturesActivity)) {
                Intent intent = new Intent(this, PicturesActivity.class);
                this.startActivity(intent);
            }
        } else if (id == R.id.nav_profile_interests) {
            if (!(this instanceof InterestActivity)) {
                Intent intent = new Intent(this, InterestActivity.class);
                this.startActivity(intent);
            }
        } else if (id == R.id.nav_profile_bucket_list) {
            if (!(this instanceof BucketListActivity)) {
                Intent intent = new Intent(this, BucketListActivity.class);
                this.startActivity(intent);
            }
        } else if (id == R.id.logout) {
            LoginManager.getInstance().logOut();

            //Clear SharedPreferences
            SharedPreferences myPrefs;
            myPrefs = this.getSharedPreferences("login", MODE_PRIVATE);
            SharedPreferences.Editor prefsEditor = myPrefs.edit();
            prefsEditor.putBoolean("isLogged", false);
            prefsEditor.commit();

            //Restart activity
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("isLogged", false);
            finish();
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}

