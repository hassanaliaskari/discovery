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
    String id;
    String description;
    String link;
    int likes;
    int noBlucketListed;
    String title;
    String location;
    String locationInfoName;
    String locationInfoSummary;
    String activity;
    DataUploaderBar dataUploaderBar;

    boolean isLiked;
    boolean isAddedToBl;

    public DataPictureCard() {
    }

    public DataPictureCard(String id, boolean isLiked, boolean isAddedToBl, String description, String link, int likes, int noBlucketListed, String title, String location, String locationInfoName, String locationInfoSummary, String activity, String uploader_name, String uploader_pp) {
        dataUploaderBar = new DataUploaderBar();
        this.id = id;
        this.description = description;
        this.link = link;
        this.likes = likes;
        this.noBlucketListed = noBlucketListed;
        this.title = title;
        this.location = location;
        this.locationInfoName = locationInfoName;
        this.locationInfoSummary = locationInfoSummary;
        this.activity = activity;
        this.dataUploaderBar.uploader_name = uploader_name;
        this.dataUploaderBar.uploader_pp = uploader_pp;

        this.isLiked = isLiked;
        this.isAddedToBl = isAddedToBl;
    }

    @Override
    public int describeContents() {
        return 0;
    }



    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(description);
        out.writeString(link);
        out.writeInt(likes);
        out.writeInt(noBlucketListed);
        out.writeString(title);
        out.writeString(location);
        out.writeString(locationInfoName);
        out.writeString(locationInfoSummary);
        out.writeString(activity);
        out.writeString(dataUploaderBar.uploader_name);
        out.writeString(dataUploaderBar.uploader_pp);
        out.writeValue(isLiked);
        out.writeValue(isAddedToBl);
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
        id = in.readString();
        description = in.readString();
        link = in.readString();
        likes = in.readInt();
        noBlucketListed = in.readInt();
        title = in.readString();
        location = in.readString();
        locationInfoName = in.readString();
        locationInfoSummary = in.readString();
        activity = in.readString();
        dataUploaderBar.uploader_name = in.readString();
        dataUploaderBar.uploader_pp = in.readString();
        isLiked = (Boolean) in.readValue(null);
        isAddedToBl = (Boolean) in.readValue(null);
    }

}








