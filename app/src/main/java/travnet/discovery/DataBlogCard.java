package travnet.discovery;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


//Data Structure for blog cards
public class DataBlogCard {
    String url;
    String thumbnail_url;
    String title;
    String extract;
    int likes;
    int noBlucketListed;
    String location;
    DataUploaderBar dataUploaderBar;

    boolean isLiked;
    boolean isAddedToBl;

    public DataBlogCard() {
    }

    public DataBlogCard(String url, String thumbnail_url, String title, String extract, int likes, String location, String uploader_name, String uploader_pp) {
        dataUploaderBar = new DataUploaderBar();
        this.url = url;
        this.thumbnail_url = thumbnail_url;
        this.title = title;
        this.extract = extract;
        this.likes = likes;
        this.location = location;
        this.dataUploaderBar.uploader_name = uploader_name;
        this.dataUploaderBar.uploader_pp = uploader_pp;

        this.isLiked = false;
    }

}



