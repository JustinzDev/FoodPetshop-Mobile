package th.ac.kmutnb.foodpetshop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CompleteOrderFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public static final String REQUEST_TAG = "myrequest";
    private static final String TAG = "my_app";
    ProgressDialog pDialog;
    private RequestQueue mQueue;

    private View view;

    private String KeyOrder = null;

    private double totalpriceallitem = 0.00;
    private int coutingitem = 0;
    private String itemImgPreview = null;

    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";

    private static String getRandomString(final int sizeOfRandomString){
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    public CompleteOrderFragment() {
        // Required empty public constructor
    }

    public static CompleteOrderFragment newInstance(String param1, String param2) {
        CompleteOrderFragment fragment = new CompleteOrderFragment();
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

        view = inflater.inflate(R.layout.fragment_complete_order, container, false);
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

        getItems("http://192.168.0.105:4990/api/users/cartitems/" + mParam1);

        ImageButton nextmyorder = view.findViewById(R.id.confirmProfileButton);
        nextmyorder.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.navHostFragment, MyOrdersFragment.newInstance(null, null));
                transaction.commit();
            }
        });


        KeyOrder = getRandomString(6);
    }

    public void getItems(String url){
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading..");
        pDialog.show();

        JsonArrayRequest jsRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Gson gson = new Gson();
                        Log.i(TAG, response.toString());
                        JSONObject jsObj;   // = null;

                        String cartID;
                        String itemName;
                        String itemImg;
                        String itemOwnerID;
                        String itemID;
                        int itemAmount;
                        double itemTotalPrice;
                        for (int i=0; i < response.length(); i++ ) {
                            try {
                                jsObj = response.getJSONObject(i);
                                cartID = jsObj.getString("_id");
                                itemOwnerID = jsObj.getString("itemownerid");
                                itemID = jsObj.getString("itemid");
                                itemAmount = jsObj.getInt("itemamount");
                                itemTotalPrice = jsObj.getDouble("itemtotalprice");
                                itemName = jsObj.getString("itemname");
                                itemImg = jsObj.getString("itemimg");
                                totalpriceallitem += itemTotalPrice;
                                coutingitem++;
                                if(itemImgPreview == null) itemImgPreview = itemImg;
                                createOrderItem("http://192.168.0.105:4990/api/users/createorderitem", cartID, itemName, itemOwnerID, itemID, itemAmount, itemTotalPrice, itemImg);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        createKeyItem("http://192.168.0.105:4990/api/users/createkeyorderlist");

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

    public void createOrderItem(String url, String cartID, String itemName, String itemOwnerID, String itemID, int itemAmount, double itemTotalPrice, String itemImg){
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
                            Log.i(TAG, "createItemSuccess");
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
                params.put("carid", cartID);
                params.put("itemname", itemName);
                params.put("itemownerid", itemOwnerID);
                params.put("itemid", itemID);
                params.put("itemamount", String.valueOf(itemAmount));
                params.put("itemtotalprice", String.valueOf(itemTotalPrice));
                params.put("itemkey", KeyOrder);
                params.put("itemimg", itemImg);
                return params;
            }
        };

        mQueue = Volley.newRequestQueue(getActivity());
        mQueue.add(stringRequest);
    }

    public void createKeyItem(String url){
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
                params.put("itemownerid", mParam1);
                params.put("itemkey", KeyOrder);
                params.put("itemtotalprice", String.valueOf(totalpriceallitem));
                params.put("itempayment", mParam2);
                params.put("itemstate", "wait");
                params.put("itemcount", String.valueOf(coutingitem));
                params.put("itemimgpreview", itemImgPreview);
                return params;
            }
        };

        mQueue = Volley.newRequestQueue(getActivity());
        mQueue.add(stringRequest);
    }
}