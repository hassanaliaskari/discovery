package travnet.discovery;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.EventLogTags;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Created by root on 5/20/16.
 */
public class Backend {
    private static Backend ourInstance = new Backend();
    private RequestQueue queue;
    private Context context;

    //private String baseUrl = "http://54.169.51.25/api/";
    private String baseUrl = "http://10.0.2.2:8080/api/";

    private static final int TYPE_PICTURE = 0;
    private static final int TYPE_BLOG = 1;

    static ArrayList<DataPictureCard> dataPictureCards;
    static ArrayList<DataBlogCard> dataBlogCards;
    static ArrayList<HomeFragment.CardsRef> cardsRef;


    public static Backend getInstance() {
        return ourInstance;
    }

    private Backend() {
    }

    public void initialize(Context context) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
        queue.getCache().clear();


        dataPictureCards = new ArrayList<>();
        dataBlogCards = new ArrayList<>();
        cardsRef = new ArrayList<>();
    }


    public abstract class RegisterNewUserListener {
        public RegisterNewUserListener() {
        }

        public abstract void registerNewUserCompleted();
        public abstract void registerNewUserFailed();
    }

    public void registerNewUser(final String id, final String name, final String email, final String ppURL, final RegisterNewUserListener listener) {

        class RegisterNewUserTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {
                User.getInstance().setProfilePicURL(ppURL);

                String url = baseUrl + "registerUser";

                JSONObject user = new JSONObject();
                try {
                    user.put("email", email);
                    user.put("name", name);
                    user.put("facebook_id", id);
                    user.put("date_of_birth", "25");
                    user.put("profile_pic", ppURL);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.POST, url, user, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                String userID = null;
                                try {
                                    userID = response.getString("user_id");

                                } catch (JSONException e) {
                                    listener.registerNewUserFailed();
                                    e.printStackTrace();
                                }
                                User.getInstance().setUserID(userID);
                                SharedPreferences myPrefs = context.getSharedPreferences("login", Context.MODE_PRIVATE);
                                SharedPreferences.Editor prefsEditor = myPrefs.edit();
                                prefsEditor.putString("user_id", User.getInstance().getUserID());
                                prefsEditor.commit();
                                listener.registerNewUserCompleted();
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                listener.registerNewUserFailed();
                            }
                        });


                queue.add(jsObjRequest);

                return null;
            }

            protected Void onPostExecute() {
                return null;
            }
        }

        new RegisterNewUserTask().execute();

    }


    public abstract class GetUserInfoListener {
        public GetUserInfoListener() {
        }

        public abstract void onUserInfoFetched();
        public abstract void onGetUserInfoFailed();
    }


    public void getUserInfo(final GetUserInfoListener listener) {

        class getUserInfoTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {

                String url = baseUrl + "getUserInfo";

                JSONObject userID = new JSONObject();
                try {
                    userID.put("user_id", User.getInstance().getUserID());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.POST, url, userID, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String name = response.getString("name");
                                    String email = response.getString("email");
                                    String ppURL = response.getString("profile_pic");
                                    JSONArray jsonInterests = response.getJSONArray("interests");
                                    //String temp = response.getString("interests");
                                    //List<String> interests = Arrays.asList(temp.substring(1,temp.length()-1).split("\\s*,\\s*"));
                                    ArrayList<String> interests = new ArrayList<String>();
                                    for (int i=0; i<jsonInterests.length(); i++){
                                        interests.add(jsonInterests.getString(i));
                                    }
                                    User.getInstance().updateUser(name, email, "", "", ppURL);
                                    User.getInstance().setInterests(interests);
                                    listener.onUserInfoFetched();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    listener.onGetUserInfoFailed();
                                }

                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                listener.onGetUserInfoFailed();
                            }
                        });

                queue.add(jsObjRequest);

                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
            }



        }

        new getUserInfoTask().execute();

    }




    public abstract class GetUserInterestsListener {
        public GetUserInterestsListener() {
        }

        public abstract void onUserInterestsFetched();
    }

    public void getUserIntersets(final GetUserInterestsListener listener) {
        class getUserInterestsTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {

                String url = baseUrl + "getUserInterests";

                JSONObject userID = new JSONObject();
                try {
                    userID.put("user_id", User.getInstance().getUserID());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.POST, url, userID, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {

                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        });

                queue.add(jsObjRequest);

                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
                //Stub
                //User.getInstance().setInterests(Arrays.asList("Surfing"));
                listener.onUserInterestsFetched();

            }



        }

        new getUserInterestsTask().execute();

    }




    public abstract class GetUserPicturesListener {
        public GetUserPicturesListener() {
        }

        public abstract void onUserPicturesFetched(ArrayList<DataPictureCard> userPictures);
        public abstract void onGetUserPicturesFailed();
    }

    public void getUserPictures(final GetUserPicturesListener listener) {
        class getUserPicturesTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {

                String url = baseUrl + "getUserCards";

                JSONObject userID = new JSONObject();
                try {
                    userID.put("user_id", User.getInstance().getUserID());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.POST, url, userID, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    ArrayList<DataPictureCard> dataPictureCards = new ArrayList<>();
                                    JSONArray arrayJson = response.getJSONArray("cards");
                                    for (int i=0; i< arrayJson.length(); i++) {
                                        JSONObject card = arrayJson.getJSONObject(i);

                                        boolean isLiked = false;
                                        JSONArray likeList = card.getJSONArray("like_list");
                                        for (int j=0;j<likeList.length();j++) {
                                            String userID = User.getInstance().getUserID();
                                            if (likeList.getString(j).equals(userID))
                                                isLiked = true;
                                        }

                                        boolean isBucketListed = false;
                                        JSONArray bucketUsers = card.getJSONArray("bucket_users");
                                        for (int j=0;j<bucketUsers.length();j++) {
                                            String userID = User.getInstance().getUserID();
                                            if (bucketUsers.getString(j).equals(userID))
                                                isBucketListed = true;
                                        }

                                        if (card.getString("card_type").equals("photo")) {
                                            JSONArray interests = card.getJSONArray("interests");
                                            DataPictureCard temp = new DataPictureCard(card.getString("_id"), isLiked, isBucketListed, card.getString("description"), card.getString("url"),
                                                    card.getInt("likes"), card.getInt("bucket_count"), card.getString("title"), card.getString("location"), card.getString("location_info_name"),
                                                    card.getString("location_info_summary"), card.getDouble("latitude"), card.getDouble("longitude"),
                                                    interests.getString(0), card.getString("user_name"), card.getString("user_profile_pic"));
                                            dataPictureCards.add(temp);
                                        }
                                    }
                                    listener.onUserPicturesFetched(dataPictureCards);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    listener.onGetUserPicturesFailed();
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                listener.onGetUserPicturesFailed();
                            }
                        });

                queue.add(jsObjRequest);

                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
            }



        }

        new getUserPicturesTask().execute();

    }



    public abstract class GetInterestsListener {
        public GetInterestsListener() {
        }

        public abstract void onInterestsFetched(ArrayList<String> listInterests);
        public abstract void onGetInterestsFailed();
    }

    public void getIntersets(final GetInterestsListener listener) {
        class getIntersetsTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {

                String url = baseUrl + "getInterests";

                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET, url, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                ArrayList<String> listInterest = new ArrayList<String>();
                                try {
                                    JSONArray arrayJson = response.getJSONArray("interests");
                                    for (int i=0; i<arrayJson.length(); i++) {
                                        String interestName = arrayJson.getJSONObject(i).getString("interest");
                                        listInterest.add(interestName);
                                    }
                                    listener.onInterestsFetched(listInterest);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    listener.onGetInterestsFailed();
                                }

                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                listener.onGetInterestsFailed();
                            }
                        });

                queue.add(jsObjRequest);

                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
            }



        }

        new getIntersetsTask().execute();

    }





    public abstract class GetCardsListener {
        public GetCardsListener() {
        }

        public abstract void onCardsFetched(ArrayList<DataPictureCard> dataPictureCards, ArrayList<DataBlogCard> dataBlogCards, ArrayList<HomeFragment.CardsRef> cardsRef);
        public abstract void onGetCardsFailed();
    }
    public void getCards(int currNoOfCards, final double lat, final double lng, final GetCardsListener listener) {

        /*if (cardsRef.size() > currNoOfCards) {
            listener.onCardsFetched(dataPictureCards, dataBlogCards, cardsRef);
            return;
        } else {
            dataPictureCards.clear();
            dataBlogCards.clear();
            cardsRef.clear();

        }*/

        dataPictureCards.clear();
        dataBlogCards.clear();
        cardsRef.clear();

        class getCardsTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {
                String url = baseUrl + "getCards";
                JSONObject param = new JSONObject();
                try {
                    param.put("user_id", User.getInstance().getUserID());
                    param.put("latitude", lat);
                    param.put("longitude", lng);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    //JSONObject jsonObj = new JSONObject(response);
                                    JSONArray arrayJson = response.getJSONArray("cards");
                                    int noOfCards = arrayJson.length();
                                    for (int i=0; i< noOfCards; i++) {
                                        JSONObject card = arrayJson.getJSONObject(i);
                                        HomeFragment.CardsRef cardRef = new HomeFragment.CardsRef();

                                        boolean isLiked = card.getBoolean("is_liked");
                                        /*JSONArray likeList = card.getJSONArray("like_list");
                                        for (int j=0;j<likeList.length();j++) {
                                            String userID = User.getInstance().getUserID();
                                            if (likeList.getString(j).equals(userID))
                                                isLiked = true;
                                        }*/

                                        boolean isBucketListed = card.getBoolean("is_bucket_listed");
                                        /*JSONArray bucketUsers = card.getJSONArray("bucket_users");
                                        for (int j=0;j<bucketUsers.length();j++) {
                                            String userID = User.getInstance().getUserID();
                                            if (bucketUsers.getString(j).equals(userID))
                                                isBucketListed = true;
                                        }*/

                                        if (card.getString("card_type").equals("photo")) {
                                            cardRef.type = TYPE_PICTURE;
                                            cardRef.index = dataPictureCards.size();
                                            cardsRef.add(cardRef);
                                            JSONArray interests = card.getJSONArray("interests");
                                            DataPictureCard temp = new DataPictureCard(card.getString("_id"), isLiked, isBucketListed, card.getString("description"), card.getString("url"),
                                                    card.getInt("likes"), card.getInt("bucket_count"), card.getString("title"), card.getString("location"), card.getString("location_info_name"),
                                                    card.getString("location_info_summary"), card.getDouble("latitude"), card.getDouble("longitude"),
                                                    interests.getString(0), card.getString("user_name"), card.getString("user_profile_pic"));
                                            dataPictureCards.add(temp);
                                        }
                                        else if (card.getString("card_type").equals("blog")) {
                                            cardRef.type = TYPE_BLOG;
                                            cardRef.index = dataBlogCards.size();
                                            cardsRef.add(cardRef);
                                            JSONArray interests = card.getJSONArray("interests");
                                            ArrayList<String> interestList = new ArrayList<>();
                                            for (int j=0;j<interests.length();j++)
                                                interestList.add(interests.getString(j));
                                            DataBlogCard temp = new DataBlogCard(card.getString("_id"), isLiked, isBucketListed, card.getString("url"), card.getString("thumbnail"), card.getString("title"),
                                                    card.getString("description"), card.getInt("likes"), card.getInt("bucket_count"), card.getString("location"), interestList, card.getString("user_name"),
                                                    card.getString("user_profile_pic"));
                                            dataBlogCards.add(temp);

                                        }
                                    }
                                    listener.onCardsFetched(dataPictureCards, dataBlogCards, cardsRef);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onGetCardsFailed();
                    }
                });

                // Add the request to the RequestQueue.
                jsonObjectRequest.setShouldCache(false);
                queue.getCache().invalidate(url, true);
                queue.getCache().remove(url);
                queue.getCache().clear();
                queue.add(jsonObjectRequest);

                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
            }



        }

        new getCardsTask().execute();

    }



    public abstract class UpdateUserInterestsListener {
        public UpdateUserInterestsListener() {
        }

        public abstract void onInterestsUpdated();
        public abstract void onInterestsUpdateFailed();
    }
    public void updateUserInterests(final UpdateUserInterestsListener listener) {
        class updateUserInterestsTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {

                String url = baseUrl + "registerInterests";

                JSONObject userInterest = new JSONObject();
                try {
                    userInterest.put("user_id", User.getInstance().getUserID());
                    ArrayList<String> listInterest = new ArrayList<String>(User.getInstance().getInterests());
                    //JSONArray jsonInterests = new JSONArray(Arrays.asList(listInterest));
                    JSONArray jsonInterests = new JSONArray();
                    for (int i=0;i<listInterest.size();i++)
                        jsonInterests.put(listInterest.get(i));
                    userInterest.put("interests", jsonInterests);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.POST, url, userInterest, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                listener.onInterestsUpdated();
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                listener.onInterestsUpdateFailed();
                            }
                        });

                queue.add(jsObjRequest);

                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
            }

        }

        new updateUserInterestsTask().execute();

    }



    public abstract class PostBlogListener {
        public PostBlogListener() {
        }

        public abstract void onBlogPostSuccess();
        public abstract void onBlogPostFailed();
    }
    public void postBlog(final String blogURL, final String blogTitle, final String blogExtract, final String thumbnailURL, final ArrayList<String> interestList, final String locationID, final String location, final PostBlogListener listener) {
        class updateUserInterestsTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {

                String url = baseUrl + "registerCard";

                JSONObject blog = new JSONObject();
                try {
                    blog.put("user_id", User.getInstance().getUserID());
                    blog.put("card_type", "blog");
                    blog.put("url", blogURL);
                    blog.put("title", blogTitle);
                    blog.put("thumbnail", thumbnailURL);
                    blog.put("description", blogExtract);
                    JSONArray interests = new JSONArray();
                    for (int i=0;i<interestList.size();i++)
                        interests.put(interestList.get(i));
                    blog.put("interests", interests);
                    blog.put("location_id", locationID);
                    blog.put("location", location);

                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onBlogPostFailed();
                }

                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.POST, url, blog, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                listener.onBlogPostSuccess();
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                listener.onBlogPostFailed();
                            }
                        });

                queue.add(jsObjRequest);

                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
            }

        }

        new updateUserInterestsTask().execute();

    }




    public abstract class GetUserPictureCountListener {
        public GetUserPictureCountListener() {
        }

        public abstract void onSuccess(int count);
        public abstract void onFailed();
    }

    public void getUserPictureCount(final GetUserPictureCountListener listener) {
        class getUserPictureCountTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {

                String url = baseUrl + "getUserPhotoCount";

                JSONObject userID = new JSONObject();
                try {
                    userID.put("user_id", User.getInstance().getUserID());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.POST, url, userID, new Response.Listener<JSONObject>() {


                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int count = response.getInt("count");
                                    listener.onSuccess(count);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    listener.onFailed();
                                }

                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                listener.onFailed();
                            }
                        });

                queue.add(jsObjRequest);

                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
            }
        }
        new getUserPictureCountTask().execute();
    }


    public abstract class GetS3KeyListener {
        public GetS3KeyListener() {
        }

        public abstract void onGetS3KeySuccess(String keyID, String secretKey);
        public abstract void onGetS3KeyFailed();
    }

    public void getS3Key(final GetS3KeyListener listener) {
        class getS3KeyTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {

                String url = baseUrl + "getSecretKey";

                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET, url, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                ArrayList<String> listInterest = new ArrayList<String>();
                                try {
                                    String keyID = response.getString("key");
                                    String secretKey = response.getString("secret");
                                    listener.onGetS3KeySuccess(keyID, secretKey);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    listener.onGetS3KeyFailed();
                                }

                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                listener.onGetS3KeyFailed();
                            }
                        });

                queue.add(jsObjRequest);

                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
            }



        }

        new getS3KeyTask().execute();

    }



    public abstract class UploadPicureListener {
        public UploadPicureListener() {
        }

        public abstract void onUploadPictureSuccess();
        public abstract void onUploadPictureFailed();
    }

    public void uploadPicture(Uri uri, final String key, final UploadPicureListener listener) {
        File file = new File(uri.getPath());

        AmazonS3 s3 = new AmazonS3Client(new AWSCredentials() {
            @Override
            public String getAWSAccessKeyId() {
                return S3Key.getInstance().getKeyID();
            }

            @Override
            public String getAWSSecretKey() {
                return S3Key.getInstance().getSecretKey();
            }
        });
        TransferUtility transferUtility = new TransferUtility(s3, context);
        TransferObserver observer = transferUtility.upload(
                "travnet",     /* The bucket to upload to */
                key,    /* The key for the uploaded object */
                file        /* The file where the data to upload exists */
        );
        observer.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.COMPLETED) {
                    listener.onUploadPictureSuccess();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int percentage = (int) (bytesCurrent / bytesTotal * 100);
                //Display percentage transfered to user
            }


            @Override
            public void onError(int id, Exception ex) {
                listener.onUploadPictureFailed();
            }

        });

    }



    public abstract class PostPictureCardListener {
        public PostPictureCardListener() {
        }

        public abstract void onPostPictureCardSuccess();
        public abstract void onPostPictureCardFailed();
    }

    public void postPictureCard(final String pictureUrl, final String heading, final String locationID, final String location, final double latitude, final double longitude, final String interest, final String description, final PostPictureCardListener listener) {
        class postPictureCard extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {

                String url = baseUrl + "registerCard";

                JSONObject pictureCard = new JSONObject();
                try {
                    pictureCard.put("user_id", User.getInstance().getUserID());
                    pictureCard.put("card_type", "photo");
                    pictureCard.put("url", pictureUrl);
                    pictureCard.put("title", heading);
                    pictureCard.put("location_id", locationID);
                    pictureCard.put("location", location);
                    pictureCard.put("latitude", latitude);
                    pictureCard.put("longitude", longitude);
                    JSONArray interests = new JSONArray();
                    interests.put(interest);
                    pictureCard.put("interests", interests);
                    pictureCard.put("description", description);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.POST, url, pictureCard, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                listener.onPostPictureCardSuccess();
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                listener.onPostPictureCardFailed();
                            }
                        });

                queue.add(jsObjRequest);

                return null;
            }

            @Override
            protected void onPostExecute(Void v) {

            }
        }

        new postPictureCard().execute();

    }


    public void registerSeenCard(final String cardID) {
        class postPictureCard extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {

                String url = baseUrl + "seenCard";

                JSONObject likeInfo = new JSONObject();
                try {
                    likeInfo.put("user_id", User.getInstance().getUserID());
                    likeInfo.put("card_id", cardID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.POST, url, likeInfo, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {

                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });

                queue.add(jsObjRequest);

                return null;
            }

            @Override
            protected void onPostExecute(Void v) {

            }
        }

        new postPictureCard().execute();

    }

    public void registerLikeCard(final String cardID) {
        class postPictureCard extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {

                String url = baseUrl + "likeCard";

                JSONObject likeInfo = new JSONObject();
                try {
                    likeInfo.put("user_id", User.getInstance().getUserID());
                    likeInfo.put("card_id", cardID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.POST, url, likeInfo, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {

                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });

                queue.add(jsObjRequest);

                return null;
            }

            @Override
            protected void onPostExecute(Void v) {

            }
        }

        new postPictureCard().execute();

    }

    public void registerBucketCard(final String cardID) {
        class postPictureCard extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {

                String url = baseUrl + "addToBucket";

                JSONObject likeInfo = new JSONObject();
                try {
                    likeInfo.put("user_id", User.getInstance().getUserID());
                    likeInfo.put("card_id", cardID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.POST, url, likeInfo, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                Log.v("bl", "response");
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.v("bl", "error");
                            }
                        });

                queue.add(jsObjRequest);

                return null;
            }

            @Override
            protected void onPostExecute(Void v) {

            }
        }

        new postPictureCard().execute();

    }



    public abstract class GetBucketListListener {
        public GetBucketListListener() {
        }

        public abstract void onBucketListFetched(List<DataBucketListCard> userBucketList);
        public abstract void onGetBucketListFailed();
    }
    public void getBucketList(final GetBucketListListener listener) {

        class getBucketListTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {
                String url = baseUrl + "getBucketList";

                JSONObject userID = new JSONObject();
                try {
                    userID.put("user_id", User.getInstance().getUserID());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.POST, url, userID, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    ArrayList<DataBucketListCard> userBucketList = new ArrayList();
                                    ArrayList<String> countries = new ArrayList<>();
                                    JSONArray arrayJson = response.getJSONArray("bucket_list");
                                    int noOfCards = arrayJson.length();
                                    for (int i=0; i< noOfCards; i++) {
                                        JSONObject card = arrayJson.getJSONObject(i);
                                        String location = card.getString("location");
                                        List<String> temp = Arrays.asList(location.split(","));
                                        String country = temp.get(temp.size() - 1);

                                        int idx;
                                        for (idx=0;idx<userBucketList.size();idx++) {
                                            if (userBucketList.get(idx).location.equals(country))
                                                break;
                                        }
                                        if (idx == userBucketList.size()) {
                                            DataBucketListCard dataBucketListCard = new DataBucketListCard();
                                            dataBucketListCard.location = country;
                                            userBucketList.add(dataBucketListCard);
                                        }
                                        if (card.getString("card_type").equals("photo")){
                                            DataPictureCard dataPictureCard = createPictureCardFromJSON(card);
                                            userBucketList.get(idx).pictures.add(dataPictureCard);
                                        }
                                        else if (card.getString("card_type").equals("blog")){
                                            DataBlogCard dataBlogCard = createBlogCardFromJSON(card);
                                            userBucketList.get(idx).blogs.add(dataBlogCard);
                                        }

                                    }
                                    listener.onBucketListFetched(userBucketList);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                listener.onGetBucketListFailed();
                            }
                        });

                // Add the request to the RequestQueue.
                jsObjRequest.setShouldCache(false);
                queue.add(jsObjRequest);

                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
            }



        }

        new getBucketListTask().execute();
    }



    DataPictureCard createPictureCardFromJSON (JSONObject card) {
        DataPictureCard dataPictureCard = null;
        try {
            boolean isLiked = false;
            JSONArray likeList = card.getJSONArray("like_list");
            for (int j = 0; j < likeList.length(); j++) {
                String userID = User.getInstance().getUserID();
                if (likeList.getString(j).equals(userID))
                    isLiked = true;
            }

            boolean isBucketListed = false;
            JSONArray bucketUsers = card.getJSONArray("bucket_users");
            for (int j = 0; j < bucketUsers.length(); j++) {
                String userID = User.getInstance().getUserID();
                if (bucketUsers.getString(j).equals(userID))
                    isBucketListed = true;
            }

            JSONArray interests = card.getJSONArray("interests");
            dataPictureCard = new DataPictureCard(card.getString("_id"), isLiked, isBucketListed, card.getString("description"), card.getString("url"),
                    card.getInt("likes"), card.getInt("bucket_count"), card.getString("title"), card.getString("location"), card.getString("location_info_name"),
                    card.getString("location_info_summary"), card.getDouble("latitude"), card.getDouble("longitude"),
                    interests.getString(0), card.getString("user_name"), card.getString("user_profile_pic"));

        }  catch (JSONException e) {
            e.printStackTrace();
        }

        return dataPictureCard;
    }


    DataBlogCard createBlogCardFromJSON (JSONObject card) {
        DataBlogCard dataBlogCard = null;
        try {
            boolean isLiked = false;
            JSONArray likeList = card.getJSONArray("like_list");
            for (int j = 0; j < likeList.length(); j++) {
                String userID = User.getInstance().getUserID();
                if (likeList.getString(j).equals(userID))
                    isLiked = true;
            }

            boolean isBucketListed = false;
            JSONArray bucketUsers = card.getJSONArray("bucket_users");
            for (int j = 0; j < bucketUsers.length(); j++) {
                String userID = User.getInstance().getUserID();
                if (bucketUsers.getString(j).equals(userID))
                    isBucketListed = true;
            }

            JSONArray interests = card.getJSONArray("interests");
            ArrayList<String> interestList = new ArrayList<>();
            for (int j=0;j<interests.length();j++)
                interestList.add(interests.getString(j));
            dataBlogCard = new DataBlogCard(card.getString("_id"), isLiked, isBucketListed, card.getString("url"), card.getString("thumbnail"), card.getString("title"),
                    card.getString("description"), card.getInt("likes"), card.getInt("bucket_count"), card.getString("location"), interestList, card.getString("user_name"),
                    card.getString("user_profile_pic"));

        }  catch (JSONException e) {
            e.printStackTrace();
        }

        return dataBlogCard;
    }


}




