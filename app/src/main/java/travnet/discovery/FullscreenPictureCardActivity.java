package travnet.discovery;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class FullscreenPictureCardActivity extends AppCompatActivity {

    DataPictureCard dataPictureCard;
    int position;

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
        CardPictureViewHolder cardPictureViewHolder = new CardPictureViewHolder(view);
        cardPictureViewHolder.poplulatePictureCard(dataPictureCard, 0);
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
