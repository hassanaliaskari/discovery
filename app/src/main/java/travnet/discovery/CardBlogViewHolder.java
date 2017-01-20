package travnet.discovery;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.liangfeizc.avatarview.AvatarView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

//View Holder for picture cards
public class CardBlogViewHolder extends RecyclerView.ViewHolder {
    View cardView;

    TextView title;
    ImageView thumbnail;
    TextView extract;
    ImageButton likeButton;
    TextView noOfLikes;
    ImageButton addToBlButton;
    TextView noOfBucketList;
    TextView activity;
    TextView location;
    TextView distance;
    TextView visaInfo;
    AvatarView uploaderPic;
    TextView uploaderName;

    public CardBlogViewHolder(View itemView) {
        super(itemView);
        cardView = itemView;

        title = (TextView) itemView.findViewById(R.id.title);
        thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
        extract = (TextView) itemView.findViewById(R.id.extract);
        likeButton = (ImageButton) itemView.findViewById(R.id.like_button);
        noOfLikes = (TextView) itemView.findViewById(R.id.no_of_likes);
        addToBlButton = (ImageButton) itemView.findViewById(R.id.add_to_bl_button);
        noOfBucketList = (TextView) itemView.findViewById(R.id.no_of_bl);
        activity = (TextView) itemView.findViewById(R.id.activity);
        location = (TextView) itemView.findViewById(R.id.location);
        distance = (TextView) itemView.findViewById(R.id.distance);
        visaInfo = (TextView) itemView.findViewById((R.id.visa_info));
        uploaderPic = (AvatarView) itemView.findViewById(R.id.pp);
        uploaderName = (TextView) itemView.findViewById(R.id.uploader_name);

    }


    public void poplulateBlogCard (final DataBlogCard dataBlogCard, int position) {
        DisplayImageOptions options= new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        this.thumbnail.setImageDrawable(null);
        this.title.setText(dataBlogCard.title);
        ImageLoader.getInstance().displayImage(dataBlogCard.thumbnail_url, this.thumbnail, options, new Animations.AnimateFirstDisplayListener());
        this.extract.setText(dataBlogCard.extract);
        this.noOfLikes.setText(String.valueOf(dataBlogCard.likes));
        this.noOfBucketList.setText(String.valueOf(dataBlogCard.noBlucketListed));
        this.activity.setText(android.text.TextUtils.join(", ", dataBlogCard.interests));
        this.location.setText(dataBlogCard.location);
        this.distance.setText("About " + String.valueOf(dataBlogCard.distance) + " km from you.");
        ImageLoader.getInstance().displayImage(dataBlogCard.dataUploaderBar.uploader_pp, this.uploaderPic, options, null);
        this.uploaderName.setText(dataBlogCard.dataUploaderBar.uploader_name);

        this.visaInfo.setText(dataBlogCard.visaInfo);
        if (dataBlogCard.visaInfo.equals("Nationality needed") || dataBlogCard.visaInfo.equals("Not available") || dataBlogCard.visaInfo.equals("Unknown") )
            this.visaInfo.setVisibility(View.GONE);


        if (dataBlogCard.isLiked == false) {
            likeButton.setImageResource(R.drawable.ic_like);
            this.addLikeCallback(dataBlogCard);
        } else {
            this.likeButton.setImageResource(R.drawable.ic_liked);
            this.likeButton.setClickable(false);
        }

        if (dataBlogCard.isAddedToBl == false) {
            this.addToBlButton.setImageResource(R.drawable.ic_add_to_bl);
            this.addBucketCallback(dataBlogCard);
        } else {
            this.addToBlButton.setImageResource(R.drawable.ic_added_to_bl);
            this.addToBlButton.setClickable(false);
        }

    }


     public void addLikeCallback(final DataBlogCard dataBlogCard) {
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Backend.getInstance().registerLikeCard(dataBlogCard.id);
                dataBlogCard.likes++;
                noOfLikes.setText(String.valueOf(dataBlogCard.likes));
                dataBlogCard.isLiked = true;

                likeButton.setImageResource(R.drawable.ic_liked);
                likeButton.setClickable(false);
            }
        });
    }


    public void addBucketCallback(final DataBlogCard dataBlogCard) {
        addToBlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Backend.getInstance().registerBucketCard(dataBlogCard.id);
                dataBlogCard.noBlucketListed++;
                noOfBucketList.setText(String.valueOf(dataBlogCard.noBlucketListed));

                dataBlogCard.isAddedToBl = true;

                addToBlButton.setImageResource(R.drawable.ic_added_to_bl);
                addToBlButton.setClickable(false);
            }
        });
    }


    public abstract class AddlocationCallbackListener {
        public AddlocationCallbackListener() {
        }

        public abstract void onLocationLinkClicked(String uri);
    }

    public void addlocationCallback(final DataBlogCard dataBlogCard, final View infoView, final double curLat, final double curLng, final AddlocationCallbackListener listener) {
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

                name.setText(dataBlogCard.locationInfoName);
                summary.setText(dataBlogCard.locationInfoSummary);
                link.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onLocationLinkClicked(dataBlogCard.locationInfoLink);
                    }
                });
                (mapView).getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        googleMap.clear();
                        LatLng cardPos = new LatLng(dataBlogCard.latitude, dataBlogCard.longitude);
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
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, 5);
                        googleMap.animateCamera(cu);

                        // For zooming automatically to the location of the marker
                        //CameraPosition cameraPosition = new CameraPosition.Builder().target(cardPos).zoom(0).build();
                        //googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                });

            }
        };

        //title.setOnClickListener(locationListener);
        location.setOnClickListener(locationListener);
    }


}
