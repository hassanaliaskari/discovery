package travnet.discovery;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


//Data Structure for blog cards
public class DataBlogCard {
    String id;
    String url;
    String thumbnail_url;
    String title;
    String extract;
    int likes;
    int noBlucketListed;
    String location;
    String locationInfoName;
    String locationInfoSummary;
    String locationInfoLink;
    double latitude;
    double longitude;
    int distance;
    String visaInfo;
    ArrayList<String> interests;
    DataUploaderBar dataUploaderBar;

    boolean isLiked;
    boolean isAddedToBl;

    public DataBlogCard() {
    }

    public DataBlogCard(String id, boolean isLiked, boolean isAddedToBl, String url, String thumbnail_url, String title, String extract, int likes, int noBlucketListed,
                        String location, String locationInfoName, String locationInfoSummary, String locationInfoLink, double latitude, double longitude, int distance,
                        String visaInfo, ArrayList<String> interests, String uploader_name, String uploader_pp) {
        dataUploaderBar = new DataUploaderBar();
        this.interests = new ArrayList<String>();
        this.id = id;
        this.url = url;
        this.thumbnail_url = thumbnail_url;
        this.title = title;
        this.extract = extract;
        this.likes = likes;
        this.noBlucketListed = noBlucketListed;
        this.location = location;
        this.locationInfoName = locationInfoName;
        this.locationInfoSummary = locationInfoSummary;
        this.locationInfoLink = locationInfoLink;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.visaInfo = visaInfo;
        this.interests.addAll(interests);
        this.dataUploaderBar.uploader_name = uploader_name;
        this.dataUploaderBar.uploader_pp = uploader_pp;

        this.isLiked = isLiked;
        this.isAddedToBl = isAddedToBl;
    }

}



