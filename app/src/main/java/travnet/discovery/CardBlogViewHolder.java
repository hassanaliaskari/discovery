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

    TextView title;
    ImageView thumbnail;
    TextView extract;
    ImageButton like_button;
    TextView noOfLikes;
    ImageButton add_to_bl_button;
    TextView noOfBucketList;
    TextView activity;
    TextView location;
    AvatarView uploaderPic;
    TextView uploaderName;

    public CardBlogViewHolder(View itemView) {
        super(itemView);
        cardView = itemView;

        title = (TextView) itemView.findViewById(R.id.title);
        thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
        extract = (TextView) itemView.findViewById(R.id.extract);
        like_button = (ImageButton) itemView.findViewById(R.id.like_button);
        noOfLikes = (TextView) itemView.findViewById(R.id.no_of_likes);
        add_to_bl_button = (ImageButton) itemView.findViewById(R.id.add_to_bl_button);
        noOfBucketList = (TextView) itemView.findViewById(R.id.no_of_bl);
        activity = (TextView) itemView.findViewById(R.id.activity);
        location = (TextView) itemView.findViewById(R.id.location);
        uploaderPic = (AvatarView) itemView.findViewById(R.id.pp);
        uploaderName = (TextView) itemView.findViewById(R.id.uploader_name);

    }


    public void poplulateBlogCard (final DataBlogCard dataBlogCard, int position) {
        DisplayImageOptions options= new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        this.title.setText(dataBlogCard.title);
        ImageLoader.getInstance().displayImage(dataBlogCard.thumbnail_url, this.thumbnail, options, new Animations.AnimateFirstDisplayListener());
        this.extract.setText(dataBlogCard.extract);
        this.noOfLikes.setText(String.valueOf(dataBlogCard.likes));
        this.noOfBucketList.setText(String.valueOf(dataBlogCard.noBlucketListed));
        this.activity.setText(android.text.TextUtils.join(", ", dataBlogCard.interests));
        this.location.setText(dataBlogCard.location);
        ImageLoader.getInstance().displayImage(dataBlogCard.dataUploaderBar.uploader_pp, this.uploaderPic, options, null);
        this.uploaderName.setText(dataBlogCard.dataUploaderBar.uploader_name);

        if (dataBlogCard.isLiked == false) {
            this.addLikeCallback(dataBlogCard);
        } else {
            this.like_button.setImageResource(R.drawable.ic_liked);
            this.like_button.setClickable(false);
        }

        if (dataBlogCard.isAddedToBl == false) {
            this.addBucketCallback(dataBlogCard);
        } else {
            this.add_to_bl_button.setImageResource(R.drawable.ic_added_to_bl);
            this.add_to_bl_button.setClickable(false);
        }

    }


     public void addLikeCallback(final DataBlogCard dataBlogCard) {
        like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataBlogCard.likes++;
                noOfLikes.setText(String.valueOf(dataBlogCard.likes));
                dataBlogCard.isLiked = true;

                like_button.setImageResource(R.drawable.ic_liked);
                like_button.setClickable(false);
            }
        });
    }


    public void addBucketCallback(final DataBlogCard dataBlogCard) {
        add_to_bl_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataBlogCard.noBlucketListed++;
                noOfBucketList.setText(String.valueOf(dataBlogCard.noBlucketListed));

                dataBlogCard.isAddedToBl = true;

                add_to_bl_button.setImageResource(R.drawable.ic_added_to_bl);
                add_to_bl_button.setClickable(false);
            }
        });
    }

}
