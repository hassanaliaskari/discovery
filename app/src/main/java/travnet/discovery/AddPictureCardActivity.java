package travnet.discovery;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.isseiaoki.simplecropview.CropImageView;

import java.io.File;
import java.util.ArrayList;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class AddPictureCardActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    private static final int REQUEST_CROP_IMAGE = 5;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 3;

    Uri imageUri;
    Place location;

    EditText add_location;
    AutoCompleteTextView interestAutoCompleteView;
    ImageView previewImage;
    ImageButton buttonGallery;
    ImageButton buttonCamera;
    ProgressDialog progress;
    ArrayList<String> interestlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_picture_card);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageUri = null;
        location = null;
        progress = new ProgressDialog(this);

        interestlist = new ArrayList<String>();
        getInterests();


        buttonGallery = (ImageButton) findViewById(R.id.button_browse_gallery);
        buttonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browsePhoneGallery();
            }
        });

        buttonCamera = (ImageButton) findViewById(R.id.button_camera);
        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });


        previewImage = (ImageView) findViewById(R.id.previewImage);

        add_location = (EditText) findViewById(R.id.add_location);
        add_location.setOnClickListener(getLocation);

        interestAutoCompleteView = (AutoCompleteTextView) findViewById(R.id.add_activity);
        ArrayAdapter<String> activityAdapter = new ArrayAdapter<String>
                (this,android.R.layout.select_dialog_item, interestlist);
        interestAutoCompleteView.setAdapter(activityAdapter);


    }


    private void getInterests() {
        Backend backend = Backend.getInstance();
        backend.getIntersets(backend.new GetInterestsListener() {
            @Override
            public void onInterestsFetched(ArrayList<String> listInterests) {
                interestlist.addAll(listInterests);
            }

            @Override
            public void onGetInterestsFailed() {

            }
        });
    }


    public void openCamera() {
        EasyImage.openCamera(this, 2);
    }

    public void browsePhoneGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }

        EasyImage.openGallery(this, 1);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CROP_IMAGE) {
            previewImage.setImageURI(null);
            Parcelable parcelable = data.getParcelableExtra("uri");
            imageUri = (Uri) parcelable;
            previewImage.setImageURI(imageUri);
            buttonCamera.setVisibility(View.GONE);
            buttonGallery.setVisibility(View.GONE);
        }

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


        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                Toast.makeText(getApplicationContext(), R.string.error_get_picture_failed, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                Uri uri = Uri.fromFile(imageFile);
                cropPicture(uri);
            }
        });
    }

    void cropPicture(Uri uri) {
        Intent intent = new Intent(this, CropPictureActivity.class);
        intent.putExtra("path", uri);
        this.startActivityForResult(intent, REQUEST_CROP_IMAGE);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_complete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_done) {
            postPictureCard();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }






    void postPictureCard() {

        //Check if enough data is present
        if (imageUri == null) {
            Toast.makeText(getApplicationContext(), R.string.error_picture_not_selected, Toast.LENGTH_LONG).show();
            return;
        }
        if (location == null) {
            Toast.makeText(getApplicationContext(), R.string.error_location_not_selected, Toast.LENGTH_LONG).show();
            return;
        }
        String interest = interestAutoCompleteView.getText().toString().trim();
        if (interestlist.contains(interest) == false) {
            Toast.makeText(getApplicationContext(), R.string.error_interest_not_selected, Toast.LENGTH_LONG).show();
            return;
        }

        //Post picture card
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setTitle("Uploading picture");
        progress.show();

        Backend backend = Backend.getInstance();
        backend.uploadPicture(imageUri, backend.new UploadPicureListener() {
            @Override
            public void onUploadPictureSuccess() {
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                progress.dismiss();
                finish();
            }

            @Override
            public void onUploadPictureFailed() {
                progress.dismiss();
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
            }
        });

    }

}
