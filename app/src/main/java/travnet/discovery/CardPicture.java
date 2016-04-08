package travnet.discovery;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;



public class CardPicture {

    //Data Structure for picture cards and blog cards
    public static class DataPictureCard {
        String description;
        String link;
        String likes;
        String location;
        String activity;
        String uploader_name;
        String uploader_pp;

        public static DataPictureCard newInstance(String description, String link, String likes, String location, String activity, String uploader_name, String uploader_pp) {
            DataPictureCard dataPictureCard = new DataPictureCard();
            dataPictureCard.description = description;
            dataPictureCard.link = link;
            dataPictureCard.likes = likes;
            dataPictureCard.location = location;
            dataPictureCard.activity = activity;
            dataPictureCard.uploader_name =uploader_name;
            dataPictureCard.uploader_pp = uploader_pp;
            return dataPictureCard;
        }

    }


    public static class CardPictureViewHolder extends RecyclerView.ViewHolder {
        TextView description;
        ImageView image;
        Button like_button;
        Button add_to_bl_button;
        TextView likes;
        TextView activity;
        TextView location;
        BarUploaderViewHolder uploader;

        public CardPictureViewHolder(View itemView) {
            super(itemView);
            uploader = new BarUploaderViewHolder ();
            description = (TextView) itemView.findViewById(R.id.description);
            image = (ImageView) itemView.findViewById(R.id.image);
            like_button = (Button) itemView.findViewById(R.id.like_button);
            add_to_bl_button = (Button) itemView.findViewById(R.id.add_to_bl_button);
            likes = (TextView) itemView.findViewById(R.id.likes);
            activity = (TextView) itemView.findViewById(R.id.activity);
            location = (TextView) itemView.findViewById(R.id.location);
            uploader.name = (TextView) itemView.findViewById(R.id.name);
            uploader.pp = (ImageView) itemView.findViewById(R.id.pp);
            addCallbacks();
        }

        private void addCallbacks() {
            like_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    like_button.setText("Liked");
                    location.setVisibility(View.VISIBLE);
                }
            });
        }


    }




}







class BarUploaderViewHolder {
    TextView name;
    ImageView pp;
}
