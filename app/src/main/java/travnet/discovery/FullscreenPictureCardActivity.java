package travnet.discovery;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.liangfeizc.avatarview.AvatarView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FullscreenPictureCardActivity extends AppCompatActivity {

    DataPictureCard dataPictureCard;
    int position;

    AvatarView uploaderPic;
    TextView uploaderName;
    TextView title;
    TextView location;
    ImageView image;
    ImageButton likeButton;
    ImageButton addToBlButton;
    TextView likes;
    TextView activity;
    TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_fullscreen_picture_card);

        View view = findViewById(R.id.card_holder);
        dataPictureCard = (DataPictureCard) getIntent().getParcelableExtra("card_data");
        position = getIntent().getIntExtra("position", -1);


        uploaderName = (TextView) findViewById(R.id.uploader_name);
        uploaderPic = (AvatarView) findViewById(R.id.uploader_pp);
        title = (TextView) findViewById(R.id.title);
        location = (TextView) findViewById(R.id.location);
        image = (ImageView) findViewById(R.id.image);
        likeButton = (ImageButton) findViewById(R.id.like_button);
        addToBlButton = (ImageButton) findViewById(R.id.add_to_bl_button);
        likes = (TextView) findViewById(R.id.likes);
        activity = (TextView) findViewById(R.id.activity);
        description = (TextView) findViewById(R.id.description);


        ImageLoader.getInstance().displayImage(dataPictureCard.dataUploaderBar.uploader_pp, this.uploaderPic);
        uploaderName.setText(dataPictureCard.dataUploaderBar.uploader_name);
        title.setText(dataPictureCard.title);
        location.setText(dataPictureCard.location);
        ImageLoader.getInstance().displayImage(dataPictureCard.link, image);
        activity.setText(dataPictureCard.activity);
        description.setText(dataPictureCard.description);
        addLikeCallback();
        addBucketCallback();

        if(dataPictureCard.isLiked == false) {
            title.setVisibility(View.GONE);
            location.setVisibility(View.GONE);
            description.setVisibility(View.GONE);
        }
        if (dataPictureCard.isLiked == true) {
            likeButton.setImageResource(R.drawable.ic_liked);
            likeButton.setClickable(false);
        }
        if(dataPictureCard.isAddedToBl == true) {
            addToBlButton.setImageResource(R.drawable.ic_added_to_bl);
            addToBlButton.setClickable(false);
        }

        //Check if the picture is the user's own
        if (getIntent().getStringExtra("card_src").equals("user")) {
            likeButton.setVisibility(View.GONE);
            addToBlButton.setVisibility(View.GONE);
        }

    }


    public void addLikeCallback() {
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataPictureCard.likes++;
                dataPictureCard.isLiked = true;

                likeButton.setImageResource(R.drawable.ic_liked);
                likeButton.setClickable(false);

                title.setVisibility(View.VISIBLE);
                location.setVisibility(View.VISIBLE);
                description.setVisibility(View.VISIBLE);
            }
        });
    }


    public void addBucketCallback() {
        addToBlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataPictureCard.noBlucketListed++;
                dataPictureCard.isAddedToBl = true;

                addToBlButton.setImageResource(R.drawable.ic_added_to_bl);
                addToBlButton.setClickable(false);
            }
        });
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("card_data", dataPictureCard);
        if (position != -1) {
            intent.putExtra("position", position);
        }
        setResult(RESULT_OK, intent);
        finish();
    }

}
