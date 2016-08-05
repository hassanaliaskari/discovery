package travnet.discovery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class HomeFragment extends Fragment {
    public static final int LOADING_THRESHOLD = 2;
    public static final int NO_OF_CARDS = 5;

    private static final int TYPE_PICTURE = 0;
    private static final int TYPE_BLOG = 1;
    private static final int FULLSCREEN_PICTURE = 55;

    public static class CardsRef {
        int type;
        int index;
    }

    static ArrayList<DataPictureCard> dataPictureCards;
    static ArrayList<DataBlogCard> dataBlogCards;
    static ArrayList<CardsRef> cardsRef;

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
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        initializeListView();
        requestCards();

        return view;
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



    // Function to make http request for cards. The received cards are added to the data arrays
    private void requestCards () {
        Backend backend = Backend.getInstance();
        backend.getCards(cardsRef.size(), backend.new GetCardsListener() {
            @Override
            public void onCardsFetched(ArrayList<DataPictureCard> dataPictureCards, ArrayList<DataBlogCard> dataBlogCards, ArrayList<CardsRef> cardsRef) {
                copyCards(dataPictureCards, dataBlogCards, cardsRef);
                cardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onGetCardsFailed() {
                Toast.makeText(getActivity().getApplicationContext(), R.string.error_connect_server_failed, Toast.LENGTH_LONG).show();
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
                    CardPictureViewHolder cardPictureViewHolder = new CardPictureViewHolder(view);
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
                    CardPictureViewHolder cardPictureViewHolder = (CardPictureViewHolder) holder;
                    final DataPictureCard dataPictureCard = dataPictureCards.get(cardsRef.get(position).index);
                    cardPictureViewHolder.poplulatePictureCard(dataPictureCard, position);
                    cardPictureViewHolder.image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openFullScreenPicture(dataPictureCard, position);
                        }
                    });
                    break;

                case TYPE_BLOG:
                    CardBlogViewHolder cardBlogViewHolder = (CardBlogViewHolder) holder;
                    final DataBlogCard dataBlogCard = dataBlogCards.get(cardsRef.get(position).index);
                    cardBlogViewHolder.poplulateBlogCard(dataBlogCard, position);
                    cardBlogViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
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
