package travnet.discovery;

import android.content.Intent;
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
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_info);

        TextView name = (TextView) findViewById(R.id.name);
        TextView summary = (TextView) findViewById(R.id.summary);
        TextView link = (TextView) findViewById(R.id.link);
        View scrim = findViewById(R.id.scrim);
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


    }


    public void browseLink () {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dataPictureCard.locationInfoLink));
        this.startActivity(browserIntent);
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
