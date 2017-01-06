package travnet.discovery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
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
    ImageButton mapButton;
    ImageButton likeButton;
    TextView noOfLikes;
    ImageButton addToBlButton;
    TextView noOfBucketList;
    TextView activity;
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
        mapButton = (ImageButton) itemView.findViewById(R.id.map_button);
        likeButton = (ImageButton) itemView.findViewById(R.id.like_button);
        noOfLikes = (TextView) itemView.findViewById(R.id.no_of_likes);
        addToBlButton = (ImageButton) itemView.findViewById(R.id.add_to_bl_button);
        noOfBucketList = (TextView) itemView.findViewById(R.id.no_of_bl);
        activity = (TextView) itemView.findViewById(R.id.activity);
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
        this.description.setText(dataPictureCard.description);
        this.uploaderName.setText(dataPictureCard.dataUploaderBar.uploader_name);
        ImageLoader.getInstance().displayImage(dataPictureCard.dataUploaderBar.uploader_pp, this.uploaderPic, options, null);

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


    public void addlocationCallback(final DataPictureCard dataPictureCard, final View infoView) {
        View.OnClickListener locationListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                TextView name = (TextView) infoView.findViewById(R.id.name);
                TextView summary = (TextView) infoView.findViewById(R.id.summary);
                final View scrim = infoView.findViewById(R.id.scrim);
                MapView mapView = (MapView) infoView.findViewById(R.id.map_view);

                infoView.setVisibility(View.VISIBLE);
                name.setVisibility(View.VISIBLE);
                summary.setVisibility(View.VISIBLE);
                mapView.setVisibility(View.GONE);

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
            }
        };

        title.setOnClickListener(locationListener);
        location.setOnClickListener(locationListener);
    }

    public void addMapCallback (final DataPictureCard dataPictureCard, final View infoView) {
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView name = (TextView) infoView.findViewById(R.id.name);
                TextView summary = (TextView) infoView.findViewById(R.id.summary);
                final View scrim = infoView.findViewById(R.id.scrim);
                MapView mapView = (MapView) infoView.findViewById(R.id.map_view);

                infoView.setVisibility(View.VISIBLE);
                name.setVisibility(View.GONE);
                summary.setVisibility(View.GONE);
                mapView.setVisibility(View.VISIBLE);

                scrim.setClickable(true);
                scrim.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        scrim.setClickable(false);
                        infoView.setVisibility(View.GONE);
                    }
                });


                (mapView).getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        LatLng position = new LatLng(dataPictureCard.latitude, dataPictureCard.longitude);
                        googleMap.addMarker(new MarkerOptions().position(position).title("Marker Title").snippet("Marker Description"));

                        // For zooming automatically to the location of the marker
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(position).zoom(12).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                });

            }

        });
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
