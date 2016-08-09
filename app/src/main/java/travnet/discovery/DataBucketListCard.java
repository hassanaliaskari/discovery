package travnet.discovery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 5/27/16.
 */

public class DataBucketListCard {

    String location;
    List<String> pictures;
    List<String> headers;

    public DataBucketListCard() {
        pictures = new ArrayList<>();
    }

    public DataBucketListCard(String location, List<String> pictures, List<String> headers) {
        this.pictures = new ArrayList<>();
        this.headers = new ArrayList<>();
        this.location = location;
        this.pictures.addAll(pictures);
        this.headers.addAll(headers);

    }


}





