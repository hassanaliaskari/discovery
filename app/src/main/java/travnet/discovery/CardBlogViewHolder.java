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

import com.liangfeizc.avatarview.AvatarView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

//View Holder for picture cards
public class CardBlogViewHolder extends RecyclerView.ViewHolder {
    View cardView;

    ImageView thumbnail;
    TextView title;
    TextView extract;
    ImageButton like_button;
    TextView likes;
    TextView activity;
    TextView location;
    AvatarView uploaderPic;
    BarUploaderViewHolder uploader;

    public CardBlogViewHolder(View itemView) {
        super(itemView);
        cardView = itemView;
        uploader = new BarUploaderViewHolder ();
        thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
        title = (TextView) itemView.findViewById(R.id.title);
        extract = (TextView) itemView.findViewById(R.id.extract);
        like_button = (ImageButton) itemView.findViewById(R.id.like_button);
        likes = (TextView) itemView.findViewById(R.id.likes);
        activity = (TextView) itemView.findViewById(R.id.activity);
        location = (TextView) itemView.findViewById(R.id.location);
        //uploader.name = (TextView) itemView.findViewById(R.id.name);
        //uploader.pp = (ImageView) itemView.findViewById(R.id.pp);
        uploaderPic = (AvatarView) itemView.findViewById(R.id.pp);

    }


    public void poplulateBlogCard (final DataBlogCard dataBlogCard, int position) {
        DisplayImageOptions options= new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_loading)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        ImageLoader.getInstance().displayImage(dataBlogCard.thumbnail_url, this.thumbnail, options, new Animations.AnimateFirstDisplayListener());
        this.title.setText(dataBlogCard.title);
        this.extract.setText(dataBlogCard.extract);
        //this.like_button.setText("Like");
        //this.likes.setText(dataBlogCard.likes + " People Likes this");
        this.activity.setText("Diving");
        this.location.setText(dataBlogCard.location);
        //this.uploader.name.setText(dataBlogCard.dataUploaderBar.uploader_name);
        //ImageLoader.getInstance().displayImage(dataBlogCard.dataUploaderBar.uploader_pp, this.uploader.pp, options, null);
        ImageLoader.getInstance().displayImage(dataBlogCard.dataUploaderBar.uploader_pp, this.uploaderPic, options, null);

        if (dataBlogCard.isLiked == false) {
            this.addLikeCallback(dataBlogCard);
        }


    }




    public void addLikeCallback(final DataBlogCard dataBlogCard) {
        like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataBlogCard.likes++;
                dataBlogCard.isLiked = true;

                like_button.setOnClickListener(null);
                //likes.setText(dataBlogCard.likes + " People Likes this");
            }
        });
    }


}
