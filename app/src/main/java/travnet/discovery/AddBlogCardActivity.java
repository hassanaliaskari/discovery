package travnet.discovery;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class AddBlogCardActivity extends AppCompatActivity {

    private static final int REQUEST_CROP_IMAGE = 2;
    private static final int REQUEST_POST_BLOG = 5;
    String blogURL;
    String blogTitle;
    String blogExtract;
    String thumbnailURL;
    Uri localImageUri;

    Menu menu;
    ProgressDialog progress;

    ImageView previewThumbnail;
    ImageButton buttonCrop;
    TextView previewTitle;
    TextView previewExtract;
    EditText inputBlogURL;
    Button buttonPreviewBlog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_blog_card);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        previewThumbnail = (ImageView) findViewById(R.id.thumbnail);
        buttonCrop = (ImageButton) findViewById(R.id.button_crop_image);
        previewTitle = (TextView) findViewById(R.id.title);
        previewExtract = (TextView) findViewById(R.id.extract);
        inputBlogURL = (EditText) findViewById(R.id.blog_link);
        buttonPreviewBlog = (Button) findViewById(R.id.button_preview_blog);


        buttonPreviewBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blogURL = inputBlogURL.getText().toString();
                TextCrawler textCrawler = new TextCrawler();
                textCrawler.makePreview(linkPreviewCallback, blogURL);
            }
        });


        buttonCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropPicture(thumbnailURL);
            }
        });
        buttonCrop.setVisibility(View.GONE);

    }

    private LinkPreviewCallback linkPreviewCallback = new LinkPreviewCallback() {

        @Override
        public void onPre() {
            showProgressDialog();
        }

        @Override
        public void onPos(SourceContent sourceContent, boolean isNull) {
            progress.dismiss();

            if (isNull || sourceContent.getFinalUrl().equals("")) {
                Toast.makeText(getApplicationContext(), R.string.error_blog_preview_failed, Toast.LENGTH_LONG).show();
                return;
            }

            MenuItem buttonPostBlog = menu.findItem(R.id.action_done);
            buttonPostBlog.setVisible(true);
            buttonCrop.setVisibility(View.VISIBLE);

            inputBlogURL.setVisibility(View.GONE);
            buttonPreviewBlog.setVisibility(View.GONE);

            blogTitle = sourceContent.getTitle();
            blogExtract = sourceContent.getDescription();
            previewTitle.setText(blogTitle);
            previewExtract.setText(blogExtract);
            thumbnailURL = sourceContent.getImages().get(0);

            loadImage();
        }
    };



    private void loadImage() {
        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .build();
        ImageLoader.getInstance().displayImage(thumbnailURL, previewThumbnail, options, null);
        imageLoader.loadImage(thumbnailURL, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                localImageUri = getImageUri(getApplicationContext(), loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    void cropPicture(String url) {
        Intent intent = new Intent(this, CropPictureActivity.class);
        Uri imageUri = Uri.parse(url);
        intent.putExtra("path", localImageUri);
        this.startActivityForResult(intent, REQUEST_CROP_IMAGE);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CROP_IMAGE && resultCode == RESULT_OK) {
            previewThumbnail.setImageURI(null);
            Parcelable parcelable = data.getParcelableExtra("uri");
            Uri newImageUri = (Uri) parcelable;
            previewThumbnail.setImageURI(newImageUri);

            new File(localImageUri.getPath()).delete();
            localImageUri = newImageUri;
            buttonCrop.setVisibility(View.GONE);
        }

        if (requestCode == REQUEST_POST_BLOG && resultCode == RESULT_OK) {
            finish();
        }


    }


    private void startCardInfoActivity () {
        Intent intent = new Intent(this, AddCardInfoActivity.class);
        intent.putExtra("type", "blog");
        intent.putExtra("url", blogURL);
        intent.putExtra("title", blogTitle);
        intent.putExtra("extract", blogExtract);
        intent.putExtra("thumbnail", thumbnailURL);
        this.startActivityForResult(intent, REQUEST_POST_BLOG);
    }


    private void showProgressDialog() {
        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //progress.setTitle("Loading Preview");
        progress.show();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_activity_complete, menu);
        MenuItem item = menu.findItem(R.id.action_done);
        item.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_done) {
            startCardInfoActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void returnFromActivity() {
        finish();
    }

}
