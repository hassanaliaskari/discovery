package travnet.discovery;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.wooplr.spotlight.SpotlightView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class HomeFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final int LOADING_THRESHOLD = 2;
    public static final int NO_OF_CARDS = 5;

    private static final int TYPE_PICTURE = 0;
    private static final int TYPE_BLOG = 1;
    private static final int FULLSCREEN_PICTURE = 55;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10;


    public static class CardsRef {
        int type;
        int index;
    }

    static ArrayList<DataPictureCard> dataPictureCards;
    static ArrayList<DataBlogCard> dataBlogCards;
    static ArrayList<CardsRef> cardsRef;

    GoogleApiClient googleApiClient;
    double curLat;
    double curLng;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    CardAdapter cardAdapter;
    ImageLoader imageLoader;

    private OnFragmentInteractionListener mListener;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        dataPictureCards = new ArrayList<>();
        dataBlogCards = new ArrayList<>();
        cardsRef = new ArrayList<>();
        imageLoader = ImageLoader.getInstance();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        initializeListView();
        initializeMap();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                    @Override

                                                    public void onRefresh() {
                                                        cardsRef.clear();
                                                        dataBlogCards.clear();
                                                        dataPictureCards.clear();
                                                        requestCards();
                                                    }
                                                }
        );

        return view;
    }


    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {
            getLocationData();
        }
    }

    private void getLocationData() {
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastLocation != null) {
            curLat = lastLocation.getLatitude();
            curLng = lastLocation.getLongitude();
            requestCards();
        }
}


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocationData();
                } else {
                }
                return;
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }



    public void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    public void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


   // Interface with Activity
    public interface OnFragmentInteractionListener {
        void  onListViewScrollStart();
        void  onListViewScrollStop();
        void  onFragmentInteraction(Uri uri);
    }


    private void initializeMap() {
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Function to make http request for cards. The received cards are added to the data arrays
    private void requestCards () {
        Backend backend = Backend.getInstance();
        backend.getCards(cardsRef.size(), curLat, curLng, backend.new GetCardsListener() {
            @Override
            public void onCardsFetched(ArrayList<DataPictureCard> dataPictureCards, ArrayList<DataBlogCard> dataBlogCards, ArrayList<CardsRef> cardsRef) {
                copyCards(dataPictureCards, dataBlogCards, cardsRef);
                cardAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onGetCardsFailed() {
                Toast.makeText(getActivity().getApplicationContext(), R.string.error_connect_server_failed, Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    private void copyCards(ArrayList<DataPictureCard> dataPictureCards, ArrayList<DataBlogCard> dataBlogCards, ArrayList<CardsRef> cardsRef) {
        this.dataPictureCards.addAll(dataPictureCards);
        this.dataBlogCards.addAll(dataBlogCards);
        this.cardsRef.addAll(cardsRef);
    }



    private void initializeListView () {

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);

        cardAdapter = new CardAdapter(getActivity());
        recyclerView.setAdapter(cardAdapter);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                requestCards();
                int curSize = cardAdapter.getItemCount();
                cardAdapter.notifyItemRangeInserted(curSize, cardsRef.size() - 1);
            }

            @Override
            public void onScrollStart() {
                mListener.onListViewScrollStart();
            }

            @Override
            public void onScrollStop() {
                mListener.onListViewScrollStop();
            }

        });

    }



    //Adapter to populate listView with picture and blog cards
    public class CardAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder> {

        private LayoutInflater inflater;
        private DisplayImageOptions options;

        CardAdapter(Context context) {
            inflater = LayoutInflater.from(context);

            options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .build();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return cardsRef.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch(viewType) {
                case TYPE_PICTURE:
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.card_picture, parent, false);
                    CardPictureViewHolder cardPictureViewHolder = new CardPictureViewHolder(view, getActivity().getApplicationContext());
                    return cardPictureViewHolder;

                case TYPE_BLOG:
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.card_blog, parent, false);
                    CardBlogViewHolder cardBlogViewHolder = new CardBlogViewHolder(view);
                    return cardBlogViewHolder;

                default:
                    return null;
            }

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            switch (holder.getItemViewType()) {

                case TYPE_PICTURE:
                    Backend.getInstance().registerSeenCard((dataPictureCards.get(cardsRef.get(position).index)).id);

                    CardPictureViewHolder cardPictureViewHolder = (CardPictureViewHolder) holder;
                    if (position == 0)
                        showLikeHint(cardPictureViewHolder);
                    final DataPictureCard dataPictureCard = dataPictureCards.get(cardsRef.get(position).index);
                    cardPictureViewHolder.poplulatePictureCard(dataPictureCard, position);
                    cardPictureViewHolder.image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openFullScreenPicture(dataPictureCard, position);
                        }
                    });
                    cardPictureViewHolder.location.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), LocationInfoActivity.class);
                            intent.putExtra("data_picture_card", dataPictureCard);
                            intent.putExtra("cur_lat", curLat);
                            intent.putExtra("cur_lng", curLng);

                            getActivity().startActivity(intent);
                        }
                    });

                    break;

                case TYPE_BLOG:
                    Backend.getInstance().registerSeenCard((dataBlogCards.get(cardsRef.get(position).index)).id);

                    CardBlogViewHolder cardBlogViewHolder = (CardBlogViewHolder) holder;
                    final DataBlogCard dataBlogCard = dataBlogCards.get(cardsRef.get(position).index);
                    cardBlogViewHolder.poplulateBlogCard(dataBlogCard, position);
                    cardBlogViewHolder.thumbnail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dataBlogCard.url));
                            getActivity().startActivity(browserIntent);
                        }
                    });

                    break;
            }
        }


        @Override
        public int getItemViewType(int position) {
            return cardsRef.get(position).type;
        }

        private void showLikeHint(CardPictureViewHolder cardPictureViewHolder) {
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (User.getInstance().getUserState() < 2) {
                    new SpotlightView.Builder(getActivity())
                            .introAnimationDuration(400)
                            .performClick(true)
                            .fadeinTextDuration(400)
                            .headingTvColor(Color.parseColor("#CE1A1A"))
                            .headingTvSize(24)
                            .headingTvText("Like To Reveal")
                            .subHeadingTvSize(16)
                            .maskColor(Color.parseColor("#dc000000"))
                            .lineAndArcColor(Color.parseColor("#CE1A1A"))
                            .subHeadingTvText("Like the picture to reveal its location")
                            .target(cardPictureViewHolder.likeButton)
                            .lineAnimDuration(400)
                            .dismissOnTouch(true)
                            .dismissOnBackPress(true)
                            .enableDismissAfterShown(true)
                            .show();
                    User.getInstance().setUserState(2);
                } else {

                }
            }
        }

    }

    private void openFullScreenPicture(DataPictureCard dataPictureCard, int position) {
        Intent intent = new Intent(getContext(), FullscreenPictureCardActivity.class);
        intent.putExtra("card_src", "feed");
        intent.putExtra("card_data", dataPictureCard);
        intent.putExtra("position", position);
        startActivityForResult(intent, FULLSCREEN_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FULLSCREEN_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                DataPictureCard returnedCard = data.getParcelableExtra("card_data");
                int returnedPosition = data.getIntExtra("position", -1);
                if (returnedPosition != -1) {
                    int index = cardsRef.get(returnedPosition).index;
                    dataPictureCards.set(index, returnedCard);
                    cardAdapter.notifyDataSetChanged();
                }
            }
        }
    }


}
