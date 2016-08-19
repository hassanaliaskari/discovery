package travnet.discovery;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.GoogleMap;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class AddCardInfoActivity extends AppCompatActivity {
    private static final int REQUEST_ADD_INTEREST = 5;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 6;
    private static final int TYPE_PICTURE = 0;
    private static final int TYPE_BLOG = 1;

    Place location;
    ArrayList<String> selectedInterests;
    int cardType;
    Backend backend;
    int pictureCount;
    Uri imageUri;

    EditText inputHeading;
    EditText add_location;
    EditText inputInterest;
    EditText inputDescription;
    ProgressDialog progress;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        selectedInterests = new ArrayList<>();
        location = null;
        progress = new ProgressDialog(this);
        backend = Backend.getInstance();

        imageUri = null;
        Parcelable parcelable = getIntent().getParcelableExtra("image_uri");
        imageUri = (Uri) parcelable;


        inputInterest = (EditText) findViewById(R.id.add_activity);
        inputInterest.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startAddInterestActivity();
                }
                return true;
            }
        });

        add_location = (EditText) findViewById(R.id.add_location);
        add_location.setOnClickListener(getLocation);

        inputDescription = (EditText) findViewById(R.id.description);
        inputHeading = (EditText) findViewById(R.id.heading);

        if (getIntent().getStringExtra("type").equals("blog")) {
            cardType = TYPE_BLOG;
            inputDescription.setVisibility(View.GONE);
            inputHeading.setVisibility(View.GONE);
        } else {
            cardType = TYPE_PICTURE;
            inputDescription.setVisibility(View.VISIBLE);
            inputHeading.setVisibility(View.VISIBLE);
        }


    }


    private void startAddInterestActivity() {
        Intent intent = new Intent(this, AddInterestActivity.class);
        intent.putExtra("interests", (Serializable) selectedInterests);
        intent.putExtra("minimum_required", 1);
        if (cardType == TYPE_BLOG) {
            intent.putExtra("maximum_allowed", 3);
        } else {
            intent.putExtra("maximum_allowed", 1);
        }
        this.startActivityForResult(intent, REQUEST_ADD_INTEREST);
    }


    EditText.OnClickListener getLocation = new EditText.OnClickListener() {
        @Override
        public void onClick(View v) {
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            //progress.setTitle("Loading Preview");
            progress.show();
            startGooglePlaces();
        }
    };


    void startGooglePlaces() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            Toast.makeText(getApplicationContext(), R.string.error_get_location_failed, Toast.LENGTH_LONG).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(getApplicationContext(), R.string.error_get_location_failed, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            progress.dismiss();
            if (resultCode == Activity.RESULT_OK) {
                location = PlaceAutocomplete.getPlace(this, data);
                add_location.setText(location.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }


        if (requestCode == REQUEST_ADD_INTEREST && resultCode == RESULT_OK) {
            ArrayList<String> interestsToAdd = (ArrayList<String>) data.getSerializableExtra("interests");
            selectedInterests.clear();
            selectedInterests.addAll(interestsToAdd);
            inputInterest.setText(android.text.TextUtils.join(", ", selectedInterests));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_activity_complete, menu);
        MenuItem item = menu.findItem(R.id.action_done);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_done) {
            if (cardType == TYPE_BLOG) {
                postBlog();
            } else {
                checkInfo();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void postBlog() {

        String blogURL = getIntent().getStringExtra("url");
        String blogTitle = getIntent().getStringExtra("title");
        String blogExtract = getIntent().getStringExtra("extract");
        String thumbnailURL = getIntent().getStringExtra("thumbnail");

        //String interests = inputInterest.getText().toString().trim();
        if (selectedInterests.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.error_interest_not_selected, Toast.LENGTH_LONG).show();
            return;
        }

        if (location == null) {
            Toast.makeText(getApplicationContext(), R.string.error_location_not_selected, Toast.LENGTH_LONG).show();
            return;
        }

        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setTitle("Sharing Blog");
        progress.show();

        Backend backend = Backend.getInstance();
        backend.postBlog(blogURL, blogTitle, blogExtract, thumbnailURL, selectedInterests, location.getName().toString(), backend.new PostBlogListener() {
            @Override
            public void onBlogPostSuccess() {
                progress.dismiss();
                returnFromActivity();
            }

            @Override
            public void onBlogPostFailed() {
                progress.dismiss();
                Toast.makeText(getApplicationContext(), R.string.error_blog_post_failed, Toast.LENGTH_LONG).show();
            }
        });

    }


    void checkInfo() {
        //Check if enough data is present
        String interest = inputInterest.getText().toString().trim();
        if (interest.isEmpty() == true) {
            Toast.makeText(getApplicationContext(), R.string.error_interest_not_selected, Toast.LENGTH_LONG).show();
            return;
        }

        if (location == null) {
            Toast.makeText(getApplicationContext(), R.string.error_location_not_selected, Toast.LENGTH_LONG).show();
            return;
        }

        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setTitle("Uploading Picture");
        progress.show();
        getUserPictureCount();
    }



    void getUserPictureCount() {
        backend.getUserPictureCount(backend.new GetUserPictureCountListener() {
            @Override
            public void onSuccess(int count) {
                pictureCount = count;
                uploadToS3();
            }

            @Override
            public void onFailed() {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
            }
        });
    }



    void uploadToS3() {
        String userID = User.getInstance().getUserID();
        String key = userID + "-" + pictureCount + ".jpg";

        backend.uploadPicture(imageUri, key, backend.new UploadPicureListener() {
            @Override
            public void onUploadPictureSuccess() {
                postPictureCard();
            }

            @Override
            public void onUploadPictureFailed() {
                progress.dismiss();
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
            }
        });

    }

    void postPictureCard() {
        String interest = inputInterest.getText().toString().trim();
        String description = inputDescription.getText().toString().trim();
        String heading = inputHeading.getText().toString().trim();
        String userID = User.getInstance().getUserID();
        String pictureUrl = "https://travnet.s3.amazonaws.com/" + userID + "-" + pictureCount + ".jpg";


        backend.postPictureCard(pictureUrl, heading, location.getName().toString(), interest, description, backend.new PostPictureCardListener() {
            @Override
            public void onPostPictureCardSuccess() {
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                progress.dismiss();
                returnFromActivity();
            }

            @Override
            public void onPostPictureCardFailed() {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
            }
        });



    }




    private void returnFromActivity() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }


}
