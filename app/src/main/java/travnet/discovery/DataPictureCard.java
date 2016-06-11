package travnet.discovery;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;



//Data Structure for picture cards
public class DataPictureCard implements Parcelable{
    String description;
    String link;
    int likes;
    String location;
    String activity;
    DataUploaderBar dataUploaderBar;

    boolean isLiked;

    public DataPictureCard() {
    }

    public DataPictureCard(String description, String link, int likes, String location, String activity, String uploader_name, String uploader_pp) {
        dataUploaderBar = new DataUploaderBar();
        this.description = description;
        this.link = link;
        this.likes = likes;
        this.location = location;
        this.activity = activity;
        this.dataUploaderBar.uploader_name = uploader_name;
        this.dataUploaderBar.uploader_pp = uploader_pp;

        this.isLiked = false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(description);
        out.writeString(link);
        out.writeInt(likes);
        out.writeString(location);
        out.writeString(activity);
        out.writeString(dataUploaderBar.uploader_name);
        out.writeString(dataUploaderBar.uploader_pp);
        out.writeValue(isLiked);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<DataPictureCard> CREATOR = new Parcelable.Creator<DataPictureCard>() {
        public DataPictureCard createFromParcel(Parcel in) {
            return new DataPictureCard(in);
        }

        public DataPictureCard[] newArray(int size) {
            return new DataPictureCard[size];
        }
    };

    private DataPictureCard(Parcel in) {
        dataUploaderBar = new DataUploaderBar();
        description = in.readString();
        link = in.readString();
        likes = in.readInt();
        location = in.readString();
        activity = in.readString();
        dataUploaderBar.uploader_name = in.readString();
        dataUploaderBar.uploader_pp = in.readString();
        isLiked = (Boolean) in.readValue(null);
    }

}








