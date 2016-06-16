package travnet.discovery;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.liangfeizc.avatarview.AvatarView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

//View Holder for picture cards
public class CardPictureViewHolder extends RecyclerView.ViewHolder {
    View cardView;

    TextView description;
    ImageView image;
    ImageButton like_button;
    ImageButton add_to_bl_button;
    TextView likes;
    TextView activity;
    View scrim;
    TextView location;
    AvatarView uploaderPic;
    BarUploaderViewHolder uploader;

    public CardPictureViewHolder(View itemView) {
        super(itemView);
        cardView = itemView;
        uploader = new BarUploaderViewHolder ();
        description = (TextView) itemView.findViewById(R.id.description);
        image = (ImageView) itemView.findViewById(R.id.image);
        like_button = (ImageButton) itemView.findViewById(R.id.like_button);
        add_to_bl_button = (ImageButton) itemView.findViewById(R.id.add_to_bl_button);
        likes = (TextView) itemView.findViewById(R.id.likes);
        activity = (TextView) itemView.findViewById(R.id.activity);
        scrim = (View) itemView.findViewById(R.id.scrim);
        location = (TextView) itemView.findViewById(R.id.location);
        //uploader.name = (TextView) itemView.findViewById(R.id.name);
        //uploader.pp = (ImageView) itemView.findViewById(R.id.pp);
        uploaderPic = (AvatarView) itemView.findViewById(R.id.pp);
    }



    public void poplulatePictureCard (final DataPictureCard dataPictureCard, int position) {
        DisplayImageOptions options= new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_loading)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        this.description.setText(dataPictureCard.description);
        //this.likes.setText(String.valueOf(dataPictureCard.likes));
        this.activity.setText(dataPictureCard.activity);
        this.location.setText(dataPictureCard.location);
        ImageLoader.getInstance().displayImage(dataPictureCard.link, this.image, options, new Animations.AnimateFirstDisplayListener());
        //this.uploader.name.setText(dataPictureCard.dataUploaderBar.uploader_name);
        //ImageLoader.getInstance().displayImage(dataPictureCard.dataUploaderBar.uploader_pp, this.uploader.pp, options, null);
        ImageLoader.getInstance().displayImage(dataPictureCard.dataUploaderBar.uploader_pp, this.uploaderPic, options, null);


        if (dataPictureCard.isLiked == false) {
            this.like_button.setVisibility(View.VISIBLE);
            this.scrim.setVisibility(View.GONE);
            this.location.setVisibility(View.GONE);
            this.addLikeCallback(dataPictureCard);
        } else {
            this.like_button.setImageResource(R.drawable.ic_liked);
            this.add_to_bl_button.setVisibility(View.VISIBLE);
            this.scrim.setVisibility(View.VISIBLE);
            this.location.setVisibility(View.VISIBLE);
        }

    }


        public void addLikeCallback(final DataPictureCard dataPictureCard) {
        like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataPictureCard.likes++;
                dataPictureCard.isLiked = true;

                //like_button.setVisibility(View.GONE);
                like_button.setImageResource(R.drawable.ic_liked);
                add_to_bl_button.setVisibility(View.VISIBLE);
                scrim.setVisibility(View.VISIBLE);
                location.setVisibility(View.VISIBLE);
                AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f );
                fadeIn.setDuration(1200);
                fadeIn.setFillAfter(true);
                AlphaAnimation scrimFadeIn = new AlphaAnimation(0.0f , 0.7f );
                scrimFadeIn.setDuration(1200);
                scrimFadeIn.setFillAfter(true);
                location.startAnimation(fadeIn);
                scrim.startAnimation(scrimFadeIn);
                //likes.setText(dataPictureCard.likes + " People Likes this");
            }
        });
    }


}
