package th.ac.kmutnb.foodpetshop;

import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ManageStorageItemFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private View view;

    private static final String TAG = "my_app";
    public static final String REQUEST_TAG = "myrequest";
    ProgressDialog pDialog;
    private RequestQueue mQueue;


    private String name;
    private String img;
    private String category;
    private double price;
    private int amount;
    private int selled;
    private String detail;
    private int popular;

    public ManageStorageItemFragment() {
        // Required empty public constructor
    }

    public static ManageStorageItemFragment newInstance(String param1, String param2) {
        ManageStorageItemFragment fragment = new ManageStorageItemFragment();
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
        view =  inflater.inflate(R.layout.fragment_manage_storage_item, container, false);
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
        Button button1 = view.findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.navHostFragment, AdminControlFragment.newInstance(null, null));
                transaction.commit();
            }
        });

        Button button2 = view.findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.navHostFragment, ManageOrderFragment.newInstance(null, null));
                transaction.commit();
            }
        });

        Button buttonEdit = view.findViewById(R.id.buttonEdit);
        buttonEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                EditText edittextitemname = view.findViewById(R.id.storageItemName);
                String itemName = edittextitemname.getText().toString();

                EditText edittextitemamount = view.findViewById(R.id.storageItemAmount);
                String itemAmount = edittextitemamount.getText().toString();
                int itemAmountReal = Integer.valueOf(itemAmount);

                EditText edittextitemprice = view.findViewById(R.id.storageItemPrice);
                String itemPrice = edittextitemprice.getText().toString();
                double itemPriceReal = Double.parseDouble(itemPrice);

                EditText edittextitemcategory = view.findViewById(R.id.storageItemCategory);
                String itemCategory = edittextitemcategory.getText().toString();

                EditText edittextitemdetail = view.findViewById(R.id.storageItemDetail);
                String itemDetail = edittextitemdetail.getText().toString();

                editItem("http://154.202.2.5:4990/api/items/edititem", itemName, itemAmountReal, itemPriceReal, itemCategory, itemDetail);
            }
        });

        Button buttonDelete = view.findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                deleteItem("http://154.202.2.5:4990/api/items/deleteitem/" + mParam1);
            }
        });

        Log.i(TAG, mParam1);
        loadItem("http://154.202.2.5:4990/api/items/getitem/" + mParam1);
    }

    public void editItem(String url, String itemName, int itemAmountReal, double itemPriceReal, String itemCategory, String itemDetail){
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading..");
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, response);

                        String message = null;

                        try {
                            JSONObject jsonobject = new JSONObject(response);
                            message = jsonobject.getString("message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(message != null){
                            pDialog.setMessage(message);
                            new CountDownTimer(3000, 1000) {
                                public void onTick(long millisUntilFinished) { }
                                public void onFinish() {
                                    pDialog.hide();
                                    FragmentManager manager = getFragmentManager();
                                    FragmentTransaction transaction = manager.beginTransaction();
                                    transaction.replace(R.id.navHostFragment, AdminControlFragment.newInstance(null, null));
                                    transaction.commit();
                                }
                            }.start();
                        } else pDialog.hide();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Error handling
                        Log.i(TAG, "onErrorResponse(): "+
                                error.getMessage());

                        pDialog.hide();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("itemid", mParam1);
                params.put("itemname", itemName);
                params.put("itemamount", String.valueOf(itemAmountReal));
                params.put("itemprice", String.valueOf(itemPriceReal));
                params.put("itemcategory", String.valueOf(itemCategory));
                params.put("itemdetail", String.valueOf(itemDetail));
                return params;
            }
        };

        mQueue = Volley.newRequestQueue(getActivity());
        mQueue.add(stringRequest);
    }

    public void deleteItem(String url){
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading..");
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, response);

                        String message = null;

                        try {
                            JSONObject jsonobject = new JSONObject(response);
                            message = jsonobject.getString("message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(message != null){
                            pDialog.setMessage(message);
                            new CountDownTimer(3000, 1000) {
                                public void onTick(long millisUntilFinished) { }
                                public void onFinish() {
                                    pDialog.hide();
                                    FragmentManager manager = getFragmentManager();
                                    FragmentTransaction transaction = manager.beginTransaction();
                                    transaction.replace(R.id.navHostFragment, AdminControlFragment.newInstance(null, null));
                                    transaction.commit();
                                }
                            }.start();
                        } else pDialog.hide();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Error handling
                        Log.i(TAG, "onErrorResponse(): "+
                                error.getMessage());

                        pDialog.hide();
                    }
                });
//        {
//            @Override
//            protected Map<String, String> getParams(){
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("username", usernameText);
//                return params;
//            }
//        };

        mQueue = Volley.newRequestQueue(getActivity());
        mQueue.add(stringRequest);
    }

    public void loadItem(String url){
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading..");
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, response);

                        String img = null;
                        String name = null;
                        String category = null;
                        double price = 0;
                        int amount = 0;
                        String detail = null;

                        try {
                            JSONObject jsonobject = new JSONObject(response);
                            name = jsonobject.getString("itemname");
                            img = jsonobject.getString("itemimg");
                            category = jsonobject.getString("itemcategory");
                            price = jsonobject.getDouble("itemprice");
                            amount = jsonobject.getInt("itemamount");
                            detail = jsonobject.getString("itemdetail");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(response.length() > 0){
                            SetItemData(name, img, category, price, amount, detail);
                        }
                        pDialog.hide();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Error handling
                        Log.i(TAG, "onErrorResponse(): "+
                                error.getMessage());

                        pDialog.hide();
                    }
                });
//        {
//            @Override
//            protected Map<String, String> getParams(){
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("username", usernameText);
//                return params;
//            }
//        };

        mQueue = Volley.newRequestQueue(getActivity());
        mQueue.add(stringRequest);
    }

    public void SetItemData(String name, String img, String category, double price, int amount, String detail){
        this.name = name;
        this.img = img;
        this.category = category;
        this.price = price;
        this.amount = amount;
        this.detail = detail;

        String baseURL = "http://154.202.2.5/foodpetshop/img/";
        ImageView itemimg = view.findViewById(R.id.stroageItemImg);
        String imgUrl = baseURL + img;
        Picasso.get()
                .load(imgUrl)
                .memoryPolicy(MemoryPolicy.NO_CACHE )
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .placeholder(R.mipmap.ic_launcher).fit()
                .error(R.mipmap.ic_launcher)
                .into(itemimg);

        EditText edittextitemname = view.findViewById(R.id.storageItemName);
        edittextitemname.setText(name);

        EditText edittextitemamount = view.findViewById(R.id.storageItemAmount);
        String formattedAmount = String.format("%,d", amount);
        edittextitemamount.setText("" + formattedAmount);

        EditText edittextitemprice = view.findViewById(R.id.storageItemPrice);
        edittextitemprice.setText("" + price);

        EditText edittextitemcategory = view.findViewById(R.id.storageItemCategory);
        edittextitemcategory.setText(category);

        EditText edittextitemdetail = view.findViewById(R.id.storageItemDetail);
        edittextitemdetail.setText(detail);
    }
}