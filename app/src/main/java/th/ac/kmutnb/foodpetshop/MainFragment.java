package th.ac.kmutnb.foodpetshop;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private View view;

    ImageSlider imageSlider;

    public static final String REQUEST_TAG = "myrequest";
    private static final String TAG = "my_app";
    ProgressDialog pDialog;
    private RequestQueue mQueue;

    private RecyclerView recyclerViewListitem;
    private ListItemAdapter listitemAdapter;

    private ArrayList<ListItemModel> listitem = new ArrayList<>();

    private RecyclerView recyclerView;
    private StaticRvAdapter staticRvAdapter;
    SharedPreferences sp;
    public MainFragment() {

    }

    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
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
        view = inflater.inflate(R.layout.fragment_main, container, false);
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

        //SlideImage
        imageSlider = (ImageSlider) view.findViewById(R.id.image_slider);
        ArrayList<SlideModel> images = new ArrayList<>();
        images.add(new SlideModel(R.drawable.picslide_1, null));
        images.add(new SlideModel(R.drawable.picslide_2, null));
        images.add(new SlideModel(R.drawable.picslide_3, null));
        images.add(new SlideModel(R.drawable.picslide_4, null));
        imageSlider.setImageList(images, ScaleTypes.CENTER_CROP);

        //Categorys
        ArrayList<StatisRvModel> item = new ArrayList<>();
        item.add(new StatisRvModel(R.drawable.cat, "อาหารแมว", "Cat"));
        item.add(new StatisRvModel(R.drawable.dog, "อาหารสุนัข", "Dog"));
        item.add(new StatisRvModel(R.drawable.bird, "อาหารนก", "Bird"));
        item.add(new StatisRvModel(R.drawable.fish, "อาหารปลา", "Fish"));
        item.add(new StatisRvModel(R.drawable.hamster, "อาหารหนูแฮมเตอร์", "Hamster"));
        item.add(new StatisRvModel(R.drawable.hedgehog, "อาหารเม่น", "Hedgehog"));
        item.add(new StatisRvModel(R.drawable.sugar, "อาหารชูก้าร์ไกรเดอร์", "Sugar"));

        staticRvAdapter = new StaticRvAdapter(item);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_listitemcategory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(staticRvAdapter);

        //itemlist
        getItems("http://192.168.0.105:4990/api/items/getitems");
        authToken("http://192.168.0.105:4990/api/users/auth_token", Token);
    }

    public void authToken(String url, String Token){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, response);

                        String userName = null;
                        String userEmail = null;
                        String userPhone = null;
                        boolean userAdmin = false;

                        try {
                            JSONObject jsonobject = new JSONObject(response);
                            userName = jsonobject.getString("username");
                            userEmail = jsonobject.getString("email");
                            userPhone = jsonobject.getString("phone");
                            userAdmin = jsonobject.getBoolean("admin");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(userName != null && userEmail != null && userPhone != null){
                            TextView tvUsername = getActivity().findViewById(R.id.txt_Username);
                            tvUsername.setText(userName);

                            TextView tvSlideNavigation = getActivity().findViewById(R.id.txUserNavi);
                            tvSlideNavigation.setText(userName);

                            ImageButton cartbutton = getActivity().findViewById(R.id.itemcart);
                            cartbutton.setVisibility(View.VISIBLE);

                            NavigationView navigationView = getActivity().findViewById(R.id.navigationView);
                            Menu nav_Menu = navigationView.getMenu();
                            nav_Menu.findItem(R.id.logout).setVisible(true);
                            nav_Menu.findItem(R.id.loginFragment).setVisible(false);
                            nav_Menu.findItem(R.id.myOrdersFragment).setVisible(true);
                            nav_Menu.findItem(R.id.editProfileFragment).setVisible(true);
                            if(userAdmin){
                                nav_Menu.findItem(R.id.adminFragment).setVisible(true);
                            }
                        } else{
                            SharedPreferences preferences = getContext().getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
                            preferences.edit().remove("Token").commit();
                            Intent itnHome = new Intent(getActivity(), MainActivity.class);
                            startActivity(itnHome);
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

    public void getItems(String url){
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading..");
        pDialog.show();

        listitem.clear();

        JsonArrayRequest jsRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Gson gson = new Gson();

                        JSONObject jsObj;   // = null;
                        for (int i=0; i < response.length(); i++ ) {
                            try {
                                jsObj = response.getJSONObject(i);
                                ListItemModel dataitem = gson.fromJson(String.valueOf(jsObj), ListItemModel.class);
                                listitem.add(dataitem);
                                Log.d(TAG,"gson "+ dataitem.getItemname());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if (listitem.size() > 0){
                            displayListview();
                        }

                        pDialog.hide();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG,error.toString());
                        Toast.makeText(getActivity(),error.toString(), Toast.LENGTH_SHORT).show();
                        pDialog.hide();
                    }
                }); // Request

        mQueue = Volley.newRequestQueue(getActivity());
        jsRequest.setTag(REQUEST_TAG);
        mQueue.add(jsRequest);
    }

    public void displayListview(){
        recyclerViewListitem = (RecyclerView) getView().findViewById(R.id.rv_listitem);
        listitemAdapter = new ListItemAdapter(listitem);
        recyclerViewListitem.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false));
        recyclerViewListitem.setAdapter(listitemAdapter);
    }
}