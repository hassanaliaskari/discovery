package travnet.discovery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.liangfeizc.avatarview.AvatarView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;



//View Holder for picture cards
public class CardPictureViewHolder extends RecyclerView.ViewHolder {
    View cardView;
    Context context;

    ImageView image;
    View scrim;
    TextView title;
    TextView location;
    ImageButton likeButton;
    TextView noOfLikes;
    ImageButton addToBlButton;
    TextView noOfBucketList;
    TextView activity;
    TextView distance;
    TextView visaInfo;
    TextView description;
    AvatarView uploaderPic;
    TextView uploaderName;

    public CardPictureViewHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
        cardView = itemView;

        image = (ImageView) itemView.findViewById(R.id.image);
        scrim = (View) itemView.findViewById(R.id.scrim);
        title= (TextView) itemView.findViewById(R.id.title);
        location = (TextView) itemView.findViewById(R.id.location);
        likeButton = (ImageButton) itemView.findViewById(R.id.like_button);
        noOfLikes = (TextView) itemView.findViewById(R.id.no_of_likes);
        addToBlButton = (ImageButton) itemView.findViewById(R.id.add_to_bl_button);
        noOfBucketList = (TextView) itemView.findViewById(R.id.no_of_bl);
        activity = (TextView) itemView.findViewById(R.id.activity);
        distance = (TextView) itemView.findViewById(R.id.distance);
        visaInfo = (TextView) itemView.findViewById(R.id.visa_info);
        description = (TextView) itemView.findViewById(R.id.description);
        uploaderName = (TextView) itemView.findViewById(R.id.uploader_name);
        uploaderPic = (AvatarView) itemView.findViewById(R.id.uploader_pp);
    }



    public void poplulatePictureCard (final DataPictureCard dataPictureCard, int position) {
        DisplayImageOptions options= new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        this.image.setScaleType(ImageView.ScaleType.CENTER);
        this.image.setImageDrawable(context.getResources().getDrawable(R.drawable.placeholder_loading));
        ImageLoader.getInstance().displayImage(dataPictureCard.link, this.image, options, new Animations.AnimateFirstDisplayListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                int w = image.getWidth();
                int check = loadedImage.getWidth();
                int check2 = loadedImage.getHeight();
                double ratio = (double)w/(double)loadedImage.getWidth();
                double temp = loadedImage.getHeight() * ratio;
                int h = (int) temp;
                scrim.getLayoutParams().height = h;
                scrim.invalidate();
            }
        });
        /*this.image.post(new Runnable() {
            @Override
            public void run() {
                scrim.getLayoutParams().height = image.getHeight();
            }
        });*/
        Typeface font = Typeface.createFromAsset(context.getAssets(), "EchinosParkScript.ttf");
        this.title.setTypeface(font);
        this.title.setText(dataPictureCard.title);
        this.location.setText(dataPictureCard.location);
        this.noOfLikes.setText(String.valueOf(dataPictureCard.likes));
        this.noOfBucketList.setText(String.valueOf(dataPictureCard.noBlucketListed));
        this.activity.setText(dataPictureCard.activity);
        this.distance.setText("About " + String.valueOf(dataPictureCard.distance) + " km from you.");
        this.description.setText(dataPictureCard.description);
        this.uploaderName.setText(dataPictureCard.dataUploaderBar.uploader_name);
        ImageLoader.getInstance().displayImage(dataPictureCard.dataUploaderBar.uploader_pp, this.uploaderPic, options, null);

        this.visaInfo.setText(dataPictureCard.visaInfo);
        if (dataPictureCard.visaInfo.equals("Nationality needed") || dataPictureCard.visaInfo.equals("Not available") || dataPictureCard.visaInfo.equals("Unknown") )
            this.visaInfo.setVisibility(View.GONE);


        if (dataPictureCard.isLiked == false) {
            this.scrim.setVisibility(View.GONE);
            this.title.setVisibility(View.GONE);
            this.location.setVisibility(View.GONE);
            this.description.setVisibility(View.GONE);
            likeButton.setImageResource(R.drawable.ic_like);
            this.addLikeCallback(dataPictureCard);
        } else {
            this.scrim.setVisibility(View.VISIBLE);
            this.title.setVisibility(View.VISIBLE);
            this.location.setVisibility(View.VISIBLE);
            this.description.setVisibility(View.VISIBLE);
            this.likeButton.setImageResource(R.drawable.ic_liked);
            this.likeButton.setClickable(false);
        }

        if (dataPictureCard.isAddedToBl == false) {
            this.addToBlButton.setImageResource(R.drawable.ic_add_to_bl);
            this.addBucketCallback(dataPictureCard);
        } else {
            this.addToBlButton.setImageResource(R.drawable.ic_added_to_bl);
            this.addToBlButton.setClickable(false);
        }

    }


    public void addLikeCallback(final DataPictureCard dataPictureCard) {
            likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Backend.getInstance().registerLikeCard(dataPictureCard.id);
                dataPictureCard.likes++;
                noOfLikes.setText(String.valueOf(dataPictureCard.likes));
                dataPictureCard.isLiked = true;

                likeButton.setImageResource(R.drawable.ic_liked);
                likeButton.setClickable(false);

                scrim.setVisibility(View.VISIBLE);
                title.setVisibility(View.VISIBLE);
                location.setVisibility(View.VISIBLE);
                description.setVisibility(View.VISIBLE);
                AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
                fadeIn.setDuration(1200);
                fadeIn.setFillAfter(true);
                AlphaAnimation scrimFadeIn = new AlphaAnimation(0.0f, 0.7f);
                scrimFadeIn.setDuration(1200);
                scrimFadeIn.setFillAfter(true);
                title.startAnimation(fadeIn);
                location.startAnimation(fadeIn);
                scrim.startAnimation(scrimFadeIn);
            }
        });
    }


    public abstract class AddlocationCallbackListener {
        public AddlocationCallbackListener() {
        }

        public abstract void onLocationLinkClicked(String uri);
    }

    public void addlocationCallback(final DataPictureCard dataPictureCard, final View infoView, final double curLat, final double curLng, final AddlocationCallbackListener listener) {
        View.OnClickListener locationListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                TextView name = (TextView) infoView.findViewById(R.id.name);
                TextView summary = (TextView) infoView.findViewById(R.id.summary);
                TextView link = (TextView) infoView.findViewById(R.id.link);
                final View scrim = infoView.findViewById(R.id.scrim);
                final MapView mapView = (MapView) infoView.findViewById(R.id.map_view);

                infoView.setVisibility(View.VISIBLE);
                name.setVisibility(View.VISIBLE);
                summary.setVisibility(View.VISIBLE);
                link.setVisibility(View.VISIBLE);
                mapView.setVisibility(View.VISIBLE);

                scrim.setClickable(true);
                scrim.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        scrim.setClickable(false);
                        infoView.setVisibility(View.GONE);

                    }
                });

                name.setText(dataPictureCard.locationInfoName);
                summary.setText(dataPictureCard.locationInfoSummary);
                link.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onLocationLinkClicked(dataPictureCard.locationInfoLink);
                    }
                });
                (mapView).getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        googleMap.clear();
                        LatLng cardPos = new LatLng(dataPictureCard.latitude, dataPictureCard.longitude);
                        LatLng userPos = new LatLng(curLat, curLng);

                        googleMap.addMarker(new MarkerOptions().position(cardPos));
                        googleMap.addMarker(new MarkerOptions().position(userPos));

                        LatLngBounds.Builder b = new LatLngBounds.Builder();
                        b.include(userPos);
                        b.include(cardPos);
                        LatLngBounds bounds = b.build();
                        int width = (int) (0.7 * infoView.getWidth());
                        int height = (int) (0.7 * mapView.getHeight());
                        //Log.v(String.valueOf(width), String.valueOf(height));
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 5);
                        googleMap.moveCamera(cu);
                        //googleMap.animateCamera(cu);

                        // For zooming automatically to the location of the marker
                        //CameraPosition cameraPosition = new CameraPosition.Builder().target(cardPos).zoom(0).build();
                        //googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                });

            }
        };

        title.setOnClickListener(locationListener);
        location.setOnClickListener(locationListener);
    }





    public void addBucketCallback(final DataPictureCard dataPictureCard) {
        addToBlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Backend.getInstance().registerBucketCard(dataPictureCard.id);
                dataPictureCard.noBlucketListed++;
                noOfBucketList.setText(String.valueOf(dataPictureCard.noBlucketListed));
                dataPictureCard.isAddedToBl = true;

                addToBlButton.setImageResource(R.drawable.ic_added_to_bl);
                addToBlButton.setClickable(false);
            }
        });
    }


}
