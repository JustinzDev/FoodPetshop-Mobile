package th.ac.kmutnb.foodpetshop;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

public class CartItemInfoFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private static final String TAG = "my_app";
    public static final String REQUEST_TAG = "myrequest";
    ProgressDialog pDialog;
    private RequestQueue mQueue;

    private View view;

    private String userID;

    private String itemid;
    private String itemname;
    private int itemamount;
    private int itemamountStorage;
    private double itemtotalprice;
    private String itemdetail;
    private String itemimg;
    private String itemcategory;

    public CartItemInfoFragment() { }

    public static CartItemInfoFragment newInstance(String param1, String param2) {
        CartItemInfoFragment fragment = new CartItemInfoFragment();
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
        view = inflater.inflate(R.layout.fragment_cart_item_info, container, false);

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

        ImageButton backbutton = view.findViewById(R.id.imagebuttonbackiteminfo);
        backbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.navHostFragment, UserCartFragment.newInstance(userID, null));
                transaction.commit();
            }
        });

        Log.i(TAG, mParam1);
        loadItem("http://154.202.2.5:4990/api/users/getitemcart/" + mParam1);
        authToken("http://154.202.2.5:4990/api/users/auth_token", Token);

        ImageButton addtocart = view.findViewById(R.id.buybutton2);
        addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                EditText inputamount = view.findViewById(R.id.storageItemName);
                String input_amount = inputamount.getText().toString();
                Log.i(TAG, input_amount);
                if(input_amount.isEmpty()){
                    Toast.makeText(getActivity(),"คุณจำเป็นต้องกรอกจำนวนที่ต้องการจะซื้อ", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(getContext());
                    builder.setMessage("คุณต้องการที่จะลบสินค้า '" + itemname + "' ออกจากตะกร้าสินค้าของคุณหรือไม่?");
                    builder.setPositiveButton("ลบ", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            deleteItemCart("http://154.202.2.5:4990/api/users/deleteitemcard");
                        }
                    });
                    builder.setNegativeButton("ไม่เลย", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Not Event
                        }
                    });
                    builder.show();

                    Log.i(TAG, "Delete");
                }
            }
        });

        ImageButton editItem = view.findViewById(R.id.buybutton);
        editItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                EditText inputamount = view.findViewById(R.id.storageItemName);
                String input_amount = inputamount.getText().toString();
                if(input_amount.isEmpty()){
                    Toast.makeText(getActivity(),"คุณจำเป็นต้องกรอกจำนวนที่ต้องการจะซื้อ", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    int itemamountEx = Integer.valueOf(input_amount);
                    if(itemamountEx > itemamountStorage){
                        Toast.makeText(getActivity(),"สินค้าในคลังมีไม่เพียงพอกับจำนวนที่คุณต้องการ!", Toast.LENGTH_SHORT).show();
                    }
                    else updateItemAPI("http://154.202.2.5:4990/api/users/updateitemcard", itemamountEx);
                }
            }
        });
    }

    public void deleteItemCart(String url){
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading..");
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, response);

                        String type = null;
                        String message = null;

                        try {
                            JSONObject jsonobject = new JSONObject(response);
                            type = jsonobject.getString("type");
                            message = jsonobject.getString("message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        pDialog.setMessage(message);
                        new CountDownTimer(3000, 1000) {
                            public void onTick(long millisUntilFinished) { }
                            public void onFinish() {
                                pDialog.hide();
                                FragmentManager manager = getFragmentManager();
                                FragmentTransaction transaction = manager.beginTransaction();
                                transaction.replace(R.id.navHostFragment, UserCartFragment.newInstance(userID, null));
                                transaction.commit();
                            }
                        }.start();
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
                params.put("_id", mParam1);
                return params;
            }
        };

        mQueue = Volley.newRequestQueue(getActivity());
        mQueue.add(stringRequest);
    }

    public void updateItemAPI(String url, int amount){
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading..");
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, response);

                        String type = null;
                        String message = null;

                        try {
                            JSONObject jsonobject = new JSONObject(response);
                            type = jsonobject.getString("type");
                            message = jsonobject.getString("message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        pDialog.setMessage(message);
                        new CountDownTimer(3000, 1000) {
                            public void onTick(long millisUntilFinished) { }
                            public void onFinish() {
                                pDialog.hide();
                                FragmentManager manager = getFragmentManager();
                                FragmentTransaction transaction = manager.beginTransaction();
                                transaction.replace(R.id.navHostFragment, UserCartFragment.newInstance(userID, null));
                                transaction.commit();
                            }
                        }.start();
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
                params.put("_id", mParam1);
                params.put("itemamount", String.valueOf(amount));
                return params;
            }
        };

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

                        try {
                            JSONObject jsonobject = new JSONObject(response);
                            itemid = jsonobject.getString("itemid");
                            itemamount = jsonobject.getInt("itemamount");
                            itemtotalprice = jsonobject.getDouble("itemtotalprice");
                            itemimg = jsonobject.getString("itemimg");

                            pDialog.hide();
                            loadItemInfo("http://154.202.2.5:4990/api/items/getitem/" + itemid);
                        } catch (JSONException e) {
                            e.printStackTrace();
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

    public void loadItemInfo(String url){
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading..");
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, response);

                        try {
                            JSONObject jsonobject = new JSONObject(response);
                            itemname = jsonobject.getString("itemname");
                            itemdetail = jsonobject.getString("itemdetail");
                            itemamountStorage = jsonobject.getInt("itemamount");
                            itemcategory = jsonobject.getString("itemcategory");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(response.length() > 0){
                            SetItemData();
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

    public void SetItemData(){
        String baseURL = "http://154.202.2.5/foodpetshop/img/";
        ImageView itemimgview = view.findViewById(R.id.stroageItemImg);
        String imgUrl = baseURL + itemimg;
        Picasso.get()
                .load(imgUrl)
                .memoryPolicy(MemoryPolicy.NO_CACHE )
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .placeholder(R.mipmap.ic_launcher).fit()
                .error(R.mipmap.ic_launcher)
                .into(itemimgview);

        TextView itemnametxt = view.findViewById(R.id.itemName);
        itemnametxt.setText(itemname);

        TextView itemcategorytxt = view.findViewById(R.id.itemCategory);
        itemcategorytxt.setText("ประเภทอาหาร: " + itemcategory);

        TextView itempricetxt = view.findViewById(R.id.itemPrice);
        String formattedPrice = String.format("%,.2f", itemtotalprice);
        itempricetxt.setText("ยอดรวม: ฿" + formattedPrice);

        TextView itemamounttxt = view.findViewById(R.id.itemStorage);
        String formattedAmount = String.format("%,d", itemamountStorage);
        itemamounttxt.setText("จำนวนสินค้าคงเหลือในคลัง: " + formattedAmount);

        TextView itemdetailtxt = view.findViewById(R.id.itemDetail);
        itemdetailtxt.setText(itemdetail);

        EditText inputamountEx = view.findViewById(R.id.storageItemName);
        inputamountEx.setText("" + itemamount);
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

                        try {
                            JSONObject jsonobject = new JSONObject(response);
                            userName = jsonobject.getString("username");
                            userEmail = jsonobject.getString("email");
                            userPhone = jsonobject.getString("phone");
                            userID = jsonobject.getString("_id");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(userName != null && userEmail != null && userPhone != null){
                            EditText inputAmount = view.findViewById(R.id.storageItemName);
                            inputAmount.setVisibility(View.VISIBLE);

                            ImageButton buyitem = view.findViewById(R.id.buybutton);
                            buyitem.setVisibility(View.VISIBLE);

                            ImageButton addcart = view.findViewById(R.id.buybutton2);
                            addcart.setVisibility(View.VISIBLE);
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
}