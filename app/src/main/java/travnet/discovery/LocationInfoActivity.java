package travnet.discovery;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationInfoActivity extends FragmentActivity implements OnMapReadyCallback  {

    DataPictureCard dataPictureCard;
    double curLat;
    double curLng;
    LinearLayout scoreBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_info);

        TextView name = (TextView) findViewById(R.id.name);
        TextView summary = (TextView) findViewById(R.id.summary);
        TextView link = (TextView) findViewById(R.id.link);
        View scrim = findViewById(R.id.scrim);
        scoreBar = (LinearLayout) findViewById(R.id.score_bar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_view);
        mapFragment.getMapAsync(this);

        dataPictureCard = (DataPictureCard) getIntent().getParcelableExtra("data_picture_card");
        curLat = getIntent().getDoubleExtra("cur_lat", 0);
        curLng = getIntent().getDoubleExtra("cur_lng", 0);



        /*scrim.setClickable(true);
        scrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrim.setClickable(false);
                infoView.setVisibility(View.GONE);

            }
        });*/

        name.setText(dataPictureCard.locationInfoName);
        summary.setText(dataPictureCard.locationInfoSummary);
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browseLink();
            }
        });

        drawScoreBar();


    }


    public void browseLink () {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dataPictureCard.locationInfoLink));
        this.startActivity(browserIntent);
    }



    private void drawScoreBar() {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        if(dataPictureCard.locationScore.size() != 0) {
            for (int i = 0; i < 12; i++) {
                TextView month = new TextView(this);
                month.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));
                month.setGravity(Gravity.CENTER | Gravity.CENTER);
                month.setTextColor(Color.parseColor("#000000"));
                month.setText(months[i]);

                int red = Color.argb(0x80, 0xb3, 0x24, 0x24);
                int yellow = Color.argb(0x80, 0xb2, 0xb3, 0x24);
                int green = Color.argb(0x80, 0x24, 0xb3, 0x24);

                GradientDrawable greenToYellow = new GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[] {green,yellow});
                GradientDrawable yellowToGreen = new GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[] {yellow,green});
                GradientDrawable yellowToRed = new GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[] {yellow,red});
                GradientDrawable redToYellow = new GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[] {red,yellow});
                GradientDrawable greenToRed = new GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[] {green,red});
                GradientDrawable redToGreen = new GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[] {red,green});

                /*greenToYellow.setGradientCenter(0.9f, 0.5f);
                greenToRed.setGradientCenter(0.9f, 0.5f);
                redToYellow.setGradientCenter(0.9f, 0.5f);
                redToGreen.setGradientCenter(0.9f, 0.5f);
                yellowToGreen.setGradientCenter(0.9f, 0.5f);
                yellowToRed.setGradientCenter(0.9f, 0.5f);*/


                int score = (int) Math.floor(dataPictureCard.locationScore.get(i));
                if (score == 3)
                    score = 2;
                int nxtScore = -1;
                if (i<11)
                    nxtScore = (int) Math.floor(dataPictureCard.locationScore.get(i+1));
                else nxtScore = (int) Math.floor(dataPictureCard.locationScore.get(0));
                if(nxtScore == 3)
                    nxtScore = 2;

                Log.v("score", String.valueOf(score) + String.valueOf(nxtScore));

                if(score == nxtScore || i==11) {
                    if (score == 0) {
                        month.setBackgroundColor(red);
                    } else if (score == 1) {
                        month.setBackgroundColor(yellow);
                    } else {
                        month.setBackgroundColor(green);
                    }
                }

                if(score == 0 && nxtScore == 1) {
                    month.setBackground(redToYellow);
                } else if (score == 1 && nxtScore == 0) {
                    month.setBackground(yellowToRed);
                } else if (score == 1 && nxtScore == 2) {
                    month.setBackground(yellowToGreen);
                } else if (score == 2 && nxtScore == 1) {
                    month.setBackground(greenToYellow);
                } else if (score == 0 && nxtScore == 2) {
                    month.setBackground(redToGreen);
                } else if (score == 2 && nxtScore == 0) {
                    month.setBackground(greenToRed);
                }


                /*double score = dataPictureCard.locationScore.get(i);
                if (score > 0.0 && score < 1.0) {
                    month.setBackgroundColor(red);
                } else if (score > 1.0 && score < 2.0) {
                    month.setBackgroundColor(yellow);
                } else {
                    month.setBackgroundColor(green);
                }*/
                scoreBar.addView(month);
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.clear();
        LatLng cardPos = new LatLng(dataPictureCard.latitude, dataPictureCard.longitude);
        LatLng userPos = new LatLng(curLat, curLng);

        googleMap.addMarker(new MarkerOptions().position(cardPos));
        //googleMap.addMarker(new MarkerOptions().position(userPos));

        /*LatLngBounds.Builder b = new LatLngBounds.Builder();
        b.include(userPos);
        b.include(cardPos);
        LatLngBounds bounds = b.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;*/

        /*Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(cardPos.latitude, cardPos.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }*/




        //CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, 5);
        //googleMap.moveCamera(cu);


        //Log.v("min", String.valueOf(googleMap.getMinZoomLevel()));
        //Log.v("cur" , String.valueOf(googleMap.getCameraPosition().zoom));

        //googleMap.animateCamera(cu);

        // For zooming automatically to the location of the marker
        CameraPosition cameraPosition = new CameraPosition.Builder().target(cardPos).zoom(5).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
