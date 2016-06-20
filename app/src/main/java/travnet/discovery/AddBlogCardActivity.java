package travnet.discovery;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class AddBlogCardActivity extends AppCompatActivity {

    private static final int REQUEST_ADD_INTEREST = 1;
    String blogURL;
    String blogTitle;
    String blogExtract;
    String thumbnailURL;
    ArrayList<String> selectedInterests;

    Menu menu;
    ProgressDialog progress;

    ImageView previewThumbnail;
    TextView previewTitle;
    TextView previewExtract;
    EditText inputBlogURL;
    Button buttonPreviewBlog;
    EditText inputInterest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_blog_card);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        selectedInterests = new ArrayList<>();

        previewThumbnail = (ImageView) findViewById(R.id.thumbnail);
        previewTitle = (TextView) findViewById(R.id.title);
        previewExtract = (TextView) findViewById(R.id.extract);
        inputBlogURL = (EditText) findViewById(R.id.blog_link);
        buttonPreviewBlog = (Button) findViewById(R.id.button_preview_blog);
        inputInterest = (EditText) findViewById(R.id.add_activity);

        buttonPreviewBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blogURL = inputBlogURL.getText().toString();
                TextCrawler textCrawler = new TextCrawler();
                textCrawler.makePreview(linkPreviewCallback, blogURL);
            }
        });

        inputInterest.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startAddInterestActivity();
                }
                return true;
            }
        });
        inputInterest.setVisibility(View.GONE);

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

            inputBlogURL.setVisibility(View.GONE);
            buttonPreviewBlog.setVisibility(View.GONE);
            inputInterest.setVisibility(View.VISIBLE);

            blogTitle = sourceContent.getTitle();
            blogExtract = sourceContent.getDescription();
            previewTitle.setText(blogTitle);
            previewExtract.setText(blogExtract);

            thumbnailURL = sourceContent.getImages().get(0);
            ImageLoader imageLoader = ImageLoader.getInstance();
            ImageLoader.getInstance().displayImage(thumbnailURL, previewThumbnail);

        }
    };


    private void startAddInterestActivity() {
        Intent intent = new Intent(this, AddInterestActivity.class);
        intent.putExtra("interests", (Serializable) selectedInterests);
        intent.putExtra("minimum_required", 1);
        intent.putExtra("maximum_allowed", 3);
        this.startActivityForResult(intent, REQUEST_ADD_INTEREST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_INTEREST && resultCode == RESULT_OK) {
            ArrayList<String> interestsToAdd = (ArrayList<String>) data.getSerializableExtra("interests");
            selectedInterests.clear();
            selectedInterests.addAll(interestsToAdd);
            inputInterest.setText(android.text.TextUtils.join(", ", selectedInterests));
        }

    }



    public void postBlog() {
        if (selectedInterests.size() == 0) {
            Toast.makeText(getApplicationContext(), "Please select interest", Toast.LENGTH_LONG).show();
            return;
        }


        Backend backend = Backend.getInstance();
        backend.postBlog(blogURL, blogTitle, blogExtract, thumbnailURL, backend.new PostBlogListener() {
            @Override
            public void onBlogPostSuccess() {
                returnFromActivity();
            }

            @Override
            public void onBlogPostFailed() {
                Toast.makeText(getApplicationContext(), R.string.error_blog_post_failed, Toast.LENGTH_LONG).show();
            }
        });

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
            postBlog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void returnFromActivity() {
        finish();
    }

}
