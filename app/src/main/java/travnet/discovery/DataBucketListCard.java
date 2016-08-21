package travnet.discovery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 5/27/16.
 */

public class DataBucketListCard {

    String location;
    List<DataPictureCard> pictures;
    List<DataBlogCard> blogs;

    public DataBucketListCard() {
        pictures = new ArrayList<>();
        blogs = new ArrayList<>();
    }

    public DataBucketListCard(String location, List<DataPictureCard> pictures, List<DataBlogCard> blogs) {
        this.pictures = new ArrayList<>();
        this.blogs = new ArrayList<>();
        this.location = location;
        this.pictures.addAll(pictures);
        this.blogs.addAll(blogs);
    }


}





