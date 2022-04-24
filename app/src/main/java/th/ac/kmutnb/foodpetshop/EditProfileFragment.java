package th.ac.kmutnb.foodpetshop;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditProfileFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private View view;

    public static final String REQUEST_TAG = "myrequest";
    private static final String TAG = "my_app";
    ProgressDialog pDialog;
    private RequestQueue mQueue;

    private String realuserID;
    private String inputPhone;
    private String inputFirstname;
    private String inputLastname;
    private String inputAddress;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    public static EditProfileFragment newInstance(String param1, String param2) {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();

        SharedPreferences sp = getContext().getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        String Token = sp.getString("Token", "");
        Log.i(TAG, "Homepage: " + Token);

        ImageButton backbutton = view.findViewById(R.id.imagebuttonbackcart);
        backbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent itnHome = new Intent(getActivity(), MainActivity.class);
                startActivity(itnHome);
            }
        });

        ImageButton confirmButton = view.findViewById(R.id.confirmProfileButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                EditText edittextPhone = view.findViewById(R.id.inputPhone);
                EditText edittextFirstname = view.findViewById(R.id.inputFristname);
                EditText edittextLasname = view.findViewById(R.id.inputLastname);
                EditText edittextAddress = view.findViewById(R.id.inputaddress);

                inputPhone = edittextPhone.getText().toString();
                inputFirstname = edittextFirstname.getText().toString();
                inputLastname = edittextLasname.getText().toString();
                inputAddress = edittextAddress.getText().toString();

                updateprofileAPI("http://192.168.0.105:4990/api/users/update_profile");
            }
        });

        authToken("http://192.168.0.105:4990/api/users/auth_token", Token);
    }

    public void getProfileData(String url){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, response);

                        String userName = null;
                        String userEmail = null;
                        String userPhone = null;
                        String userFirstname = null;
                        String userLastname = null;
                        String userAddress = null;

                        try {
                            JSONObject jsonobject = new JSONObject(response);
                            userName = jsonobject.getString("username");
                            userEmail = jsonobject.getString("email");
                            userPhone = jsonobject.getString("telephone");
                            userFirstname = jsonobject.getString("firstname");
                            userLastname = jsonobject.getString("lastname");
                            userAddress = jsonobject.getString("address");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(userName != null && userEmail != null && userPhone != null){
                            EditText username = view.findViewById(R.id.inputUsername);
                            username.setText(userName);
                            username.setFocusableInTouchMode(false);

                            EditText email = view.findViewById(R.id.inputEmail);
                            email.setText(userEmail);
                            email.setFocusableInTouchMode(false);

                            EditText phone = view.findViewById(R.id.inputPhone);
                            phone.setText(userPhone);

                            EditText firstname = view.findViewById(R.id.inputFristname);
                            firstname.setText(userFirstname);

                            EditText lastname = view.findViewById(R.id.inputLastname);
                            lastname.setText(userLastname);

                            EditText address = view.findViewById(R.id.inputaddress);
                            address.setText(userAddress);
                        } else{
                            SharedPreferences preferences = getContext().getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
                            preferences.edit().remove("Token").commit();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Error handling
                        Log.i(TAG, "onErrorResponse(): "+
                                error.getMessage());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("_id", realuserID);
                return params;
            }
        };

        mQueue = Volley.newRequestQueue(getActivity());
        mQueue.add(stringRequest);
    }

    public void authToken(String url, String Token){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, response);

                        String userID = null;

                        try {
                            JSONObject jsonobject = new JSONObject(response);
                            userID = jsonobject.getString("_id");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(userID != null){
                            realuserID = userID;
                            getProfileData("http://192.168.0.105:4990/api/users/get_profiledata");

                        } else{
                            SharedPreferences preferences = getContext().getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
                            preferences.edit().remove("Token").commit();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Error handling
                        Log.i(TAG, "onErrorResponse(): "+
                                error.getMessage());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", Token);
                return params;
            }
        };

        mQueue = Volley.newRequestQueue(getActivity());
        mQueue.add(stringRequest);
    }

    public void updateprofileAPI(String url){
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading..");
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, response);

                        String type = null;

                        try {
                            JSONObject jsonobject = new JSONObject(response);
                            type = jsonobject.getString("type");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(type.matches("success")){
                            pDialog.setMessage("คุณได้อัพเดทโปรไฟล์ส่วนตัวเรียบร้อยแล้ว");
                            new CountDownTimer(3000, 1000) {
                                public void onTick(long millisUntilFinished) { }
                                public void onFinish() {
                                    pDialog.hide();
                                    FragmentManager manager = getFragmentManager();
                                    FragmentTransaction transaction = manager.beginTransaction();
                                    transaction.replace(R.id.navHostFragment, EditProfileFragment.newInstance(null, null));
                                    transaction.commit();
                                }
                            }.start();
                        } else Log.i(TAG, "Wrong!");
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Error handling
                        Log.i(TAG, "onErrorResponse(): "+
                                error.getMessage());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("_id", realuserID);
                params.put("telephone", inputPhone);
                params.put("firstname", inputFirstname);
                params.put("lastname", inputLastname);
                params.put("address", inputAddress);
                return params;
            }
        };

        mQueue = Volley.newRequestQueue(getActivity());
        mQueue.add(stringRequest);
    }
}