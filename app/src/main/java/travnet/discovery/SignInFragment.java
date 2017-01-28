package travnet.discovery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignInFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SignInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignInFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    private static final int GOOGLE_SIGN_IN = 1;
    private View myFragmentView;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    GoogleApiClient googleApiClient;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private OnLoginListener loginListener;

    public SignInFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignInFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignInFragment newInstance(String param1, String param2) {
        SignInFragment fragment = new SignInFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .requestProfile()
                .build();
        googleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragmentView = inflater.inflate(R.layout.fragment_login, container, false);

        SignInButton loginButtonGoogle = (SignInButton) myFragmentView.findViewById(R.id.login_button_google);
        loginButtonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });


        loginButton = (LoginButton) myFragmentView.findViewById(R.id.login_button_fb);
        loginButton.setFragment(this);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                //Request user info
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                // Application code
                                try {
                                    String id = object.getString("id");
                                    String email = object.getString("email");
                                    String name = object.getString("name");
                                    JSONObject picture = object.getJSONObject("picture");
                                    String ppURL = picture.getJSONObject("data").getString("url");

                                    User.getInstance().updateUser(name, email, "", "", ppURL);

                                    registerUser("fb", id, name, email, ppURL);
                                    /*Backend.getInstance().registerNewUser(id, name, email, ppURL, Backend.getInstance().new RegisterNewUserListener() {
                                        @Override
                                        public void registerNewUserCompleted() {
                                            loginListener.onLoginSuccessful();
                                        }

                                        @Override
                                        public void registerNewUserFailed() {
                                            loginListener.onLoginFailed();
                                        }
                                    });*/

                                } catch (JSONException e) {
                                    loginListener.onLoginFailed();
                                    e.printStackTrace();
                                }
                            }

                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,email,name,hometown,location,picture.width(300).height(300)");
                //        parameters.putString("fields", "email, name");
                request.setParameters(parameters);
                request.executeAsync();


            }

            @Override
            public void onCancel() {
                loginListener.onLoginFailed();
            }

            @Override
            public void onError(FacebookException e) {
                loginListener.onLoginFailed();
                e.printStackTrace();
            }
        });

        return myFragmentView;
    }


    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

        if (context instanceof OnLoginListener) {
            loginListener = (OnLoginListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLoginListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            String name = acct.getDisplayName();
            String email = acct.getEmail();
            Uri photoURI = acct.getPhotoUrl();
            String ppURL = null;
            if (photoURI != null)
                ppURL = photoURI.toString();

            String id = acct.getId();

            User.getInstance().updateUser(name, email, "", "", ppURL);

            registerUser("g+", id, name, email, ppURL);

        } else {
            loginListener.onLoginFailed();
        }
    }


    private void registerUser(String type, String id, String name, String email, String ppURL) {
        Backend.getInstance().registerNewUser(type, id, name, email, ppURL, Backend.getInstance().new RegisterNewUserListener() {
            @Override
            public void registerNewUserCompleted() {
                loginListener.onLoginSuccessful();
            }

            @Override
            public void registerNewUserFailed() {
                loginListener.onLoginFailed();
            }
        });
    }



    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public interface OnLoginListener {
        void onLoginSuccessful();
        void onLoginFailed();
    }



}
