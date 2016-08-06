package travnet.discovery;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;

import java.io.File;

public class CropPictureActivity extends AppCompatActivity {

    private static final int LANDSCAPE_RATIO = 1;
    private static final int SQUARE_RATIO = 2;
    private static final int POTRAIT_RATIO = 3;

    CropImageView cropImageView;
    Button buttonDone;
    Menu menu;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_picture);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progress = new ProgressDialog(this);
        cropImageView = (CropImageView) findViewById(R.id.crop_image_view);

        Uri imageUri = (Uri) getIntent().getExtras().getParcelable("path");
        setImageForCropping(imageUri);

        ImageButton buttonRotateLeft = (ImageButton) findViewById(R.id.button_rotate_left);
        buttonRotateLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
            }
        });

        ImageButton buttonRotateRight = (ImageButton) findViewById(R.id.button_rotate_right);
        buttonRotateRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
            }
        });

        Button buttonLandscape = (Button) findViewById(R.id.button_landscape);
        buttonLandscape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAspectRatio(LANDSCAPE_RATIO);
            }
        });

        Button buttonSquare = (Button) findViewById(R.id.button_square);
        buttonSquare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAspectRatio(SQUARE_RATIO);
            }
        });

        Button buttonPotrait = (Button) findViewById(R.id.button_potrait);
        buttonPotrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAspectRatio(POTRAIT_RATIO);
            }
        });

    }


    public Uri createSaveUri() {
        return Uri.fromFile(new File(this.getCacheDir(), "croppedImage"));
    }

    public void setImageForCropping(Uri uri) {
        cropImageView.startLoad(uri, new LoadCallback() {
            @Override
            public void onError() {
                Toast.makeText(getApplicationContext(), R.string.error_get_picture_failed, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess() {
                //MenuItem buttonDone = menu.findItem(R.id.action_done);
                //buttonDone.setVisible(true);
                //setDoneButton();
            }

        });
    }


    private void setAspectRatio(int aspectRatio) {
        if (aspectRatio == LANDSCAPE_RATIO) {
            cropImageView.setCropMode(CropImageView.CropMode.RATIO_4_3);
        } else if (aspectRatio == SQUARE_RATIO) {
            cropImageView.setCropMode(CropImageView.CropMode.SQUARE);
        }
        if (aspectRatio == POTRAIT_RATIO) {
            cropImageView.setCropMode(CropImageView.CropMode.RATIO_3_4);
        }

    }



    private void returnCroppedImage() {
        cropImageView.startCrop(createSaveUri(),
                new CropCallback() {
                    @Override
                    public void onSuccess(Bitmap croppedImage) {

                    }

                    @Override
                    public void onError() {
                    }
                },

                new SaveCallback() {
                    @Override
                    public void onSuccess(Uri outputUri) {
                        //Process cropped image
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("uri", outputUri);
                        setResult(Activity.RESULT_OK, returnIntent);
                        progress.dismiss();
                        finish();
                    }

                    @Override
                    public void onError() {
                    }
                }
        );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_activity_complete, menu);
        //MenuItem item = menu.findItem(R.id.action_done);
        //item.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_done) {
            showDialog();
            returnCroppedImage();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void showDialog() {
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setTitle("Cropping Image");
        progress.show();
    }


}
