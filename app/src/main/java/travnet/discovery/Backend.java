package travnet.discovery;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by root on 5/20/16.
 */
public class Backend {
    private static Backend ourInstance = new Backend();
    private Context context;

    //private String baseUrl = "http://192.168.1.25:8080/api/";
    private String baseUrl = "http://54.86.18.174/api/";

    public static final int NO_OF_CARDS = 5;
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

    public void registerNewUser(final String id, final String name, final String email, final String location, final String hometown, final String ppURL, final RegisterNewUserListener listener) {

        class RegisterNewUserTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {
                User.getInstance().setProfilePicURL(ppURL);


                RequestQueue queue = Volley.newRequestQueue(context);
                String url = baseUrl + "registerUser";

                JSONObject user = new JSONObject();
                try {
                    user.put("email", email);
                    user.put("name", name);
                    user.put("living_in", location);
                    user.put("home", hometown);
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

                RequestQueue queue = Volley.newRequestQueue(context);
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
                                    String hometown = response.getString("home");
                                    String location = response.getString("living_in");
                                    String ppURL = response.getString("profile_pic");
                                    JSONArray interestJSON = response.getJSONArray("interests");
                                    String temp = interestJSON.getString(0);
                                    List<String> interests = Arrays.asList(temp.substring(1,temp.length()-1).split("\\s*,\\s*"));
                                    User.getInstance().updateUser(name, email, location, hometown, ppURL);
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

                RequestQueue queue = Volley.newRequestQueue(context);
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

                RequestQueue queue = Volley.newRequestQueue(context);
                String url = baseUrl + "getCards";

                /*JSONObject userID = new JSONObject();
                try {
                    userID.put("user_id", User.getInstance().getUserID());

                } catch (JSONException e) {
                    e.printStackTrace();
                }*/

                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET, url, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    ArrayList<DataPictureCard> dataPictureCards = new ArrayList<DataPictureCard>();
                                    JSONArray arrayJson = response.getJSONArray("cards");
                                    for (int i=0; i< NO_OF_CARDS; i++) {
                                        JSONObject card = arrayJson.getJSONObject(i);

                                        String check = card.getString(("card-type"));
                                        if (card.getString("card-type").equals("image")) {
                                            JSONObject content = card.getJSONObject("content");
                                            DataPictureCard temp = new DataPictureCard(content.getString("description"), content.getString("url"),
                                                    card.getInt("likes"), card.getString("location"), "Place holder", card.getString("user-name"),
                                                    card.getString("user-img"));
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
                //Stub
                /*String imageLink = "http://www.planwallpaper.com/static/images/4-Nature-Wallpapers-2014-1_ukaavUI.jpg";

                ArrayList userPictures = new ArrayList<DataPictureCard>();
                DataPictureCard data1 = new DataPictureCard("A", imageLink, 10, "D", "E", "F", "G");
                DataPictureCard data2 = new DataPictureCard("A", imageLink, 10, "D", "E", "F", "G");
                DataPictureCard data3 = new DataPictureCard("A", imageLink, 10, "D", "E", "F", "G");
                DataPictureCard data4 = new DataPictureCard("A", imageLink, 10, "D", "E", "F", "G");
                userPictures.add(data1);
                userPictures.add(data2);
                userPictures.add(data3);
                userPictures.add(data4);
                listener.onUserPicturesFetched(userPictures);*/

            }



        }

        new getUserPicturesTask().execute();

    }



    public abstract class GetInterestsListener {
        public GetInterestsListener() {
        }

        public abstract void onInterestsFetched(ArrayList<String> listInterests);
    }

    public void getIntersets(final GetInterestsListener listener) {
        class getIntersetsTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {

                RequestQueue queue = Volley.newRequestQueue(context);
                String url = baseUrl + "getInterests";

                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET, url, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                Log.v("fd", "df");
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
                                }

                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.v("fd", "df");
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



    public void postPictureCard(final String title, final String interest) {
        class postPictureCard extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {

                RequestQueue queue = Volley.newRequestQueue(context);
                String url = baseUrl + "registerCard";

                JSONObject pictureCard = new JSONObject();
                try {
                    pictureCard.put("user_id", User.getInstance().getUserID());
                    pictureCard.put("card_type", "photo");
                    pictureCard.put("interest", interest);
                    pictureCard.put("title", title);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.POST, url, pictureCard, new Response.Listener<JSONObject>() {

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



    public abstract class GetCardsListener {
        public GetCardsListener() {
        }

        public abstract void onCardsFetched(ArrayList<DataPictureCard> dataPictureCards, ArrayList<DataBlogCard> dataBlogCards, ArrayList<HomeFragment.CardsRef> cardsRef);
        public abstract void onGetCardsFailed();
    }
    public void getCards(int currNoOfCards, final GetCardsListener listener) {

        if (cardsRef.size() > currNoOfCards) {
            listener.onCardsFetched(dataPictureCards, dataBlogCards, cardsRef);
            return;
        } else {
            dataPictureCards.clear();
            dataBlogCards.clear();
            cardsRef.clear();
        }


        class getCardsTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {
                RequestQueue queue = Volley.newRequestQueue(context);
                String url = baseUrl + "getCards";

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObj = new JSONObject(response);
                                    JSONArray arrayJson = jsonObj.getJSONArray("cards");
                                    for (int i=0; i< NO_OF_CARDS; i++) {
                                        JSONObject card = arrayJson.getJSONObject(i);
                                        HomeFragment.CardsRef cardRef = new HomeFragment.CardsRef();

                                        String check = card.getString(("card-type"));
                                        if (card.getString("card-type").equals("image")) {
                                            cardRef.type = TYPE_PICTURE;
                                            cardRef.index = dataPictureCards.size();
                                            cardsRef.add(cardRef);
                                            JSONObject content = card.getJSONObject("content");
                                            DataPictureCard temp = new DataPictureCard(content.getString("description"), content.getString("url"),
                                                    card.getInt("likes"), card.getString("location"), "Sight-Seeing", card.getString("user-name"),
                                                    card.getString("user-img"));
                                            dataPictureCards.add(temp);
                                        }
                                        else if (card.getString("card-type").equals("blog")) {
                                            cardRef.type = TYPE_BLOG;
                                            cardRef.index = dataBlogCards.size();
                                            cardsRef.add(cardRef);
                                            JSONObject content = card.getJSONObject("content");
                                            DataBlogCard temp = new DataBlogCard(content.getString("url"), content.getString("thumbnail"), content.getString("title"),
                                                    content.getString("abstract"), card.getInt("likes"), card.getString("location"), card.getString("user-name"),
                                                    card.getString("user-img"));
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
                queue.add(stringRequest);

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

                RequestQueue queue = Volley.newRequestQueue(context);
                String url = baseUrl + "updateInterests";

                JSONObject userInterest = new JSONObject();
                try {
                    userInterest.put("user_id", User.getInstance().getUserID());
                    ArrayList<String> listInterest = new ArrayList<String>(User.getInstance().getInterests());
                    userInterest.put("interests", listInterest);

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
    public void postBlog(final String blogURL, final String blogTitle, final String blogExtract, final String thumbnailURL, final PostBlogListener listener) {
        class updateUserInterestsTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {

                RequestQueue queue = Volley.newRequestQueue(context);
                String url = baseUrl + "registerCard";

                JSONObject blog = new JSONObject();
                try {
                    blog.put("user_id", User.getInstance().getUserID());
                    blog.put("card_type", "blog");
                    blog.put("url", blogURL);
                    blog.put("title", blogTitle);
                    blog.put("extract", blogExtract);
                    blog.put("thumbnail", thumbnailURL);

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






}
