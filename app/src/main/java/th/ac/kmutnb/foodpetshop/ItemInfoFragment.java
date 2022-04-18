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
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class ItemInfoFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private static final String TAG = "my_app";
    public static final String REQUEST_TAG = "myrequest";
    ProgressDialog pDialog;
    private RequestQueue mQueue;

    private View view;

    private String name;
    private String img;
    private String category;
    private double price;
    private int amount;
    private int selled;
    private String detail;
    private int popular;

    private String userID;

    public ItemInfoFragment() {
    }

    public static ItemInfoFragment newInstance(String param1, String param2) {
        ItemInfoFragment fragment = new ItemInfoFragment();
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
        view = inflater.inflate(R.layout.fragment_item_info, container, false);
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
                Intent itnHome = new Intent(getActivity(), MainActivity.class);
                startActivity(itnHome);
            }
        });

        loadItem("http://192.168.0.105:4990/api/items/getitem/" + mParam1);
        authToken("http://192.168.0.105:4990/api/users/auth_token", Token);

        ImageButton addtocart = view.findViewById(R.id.buybutton2);
        addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                EditText inputamount = view.findViewById(R.id.inputamountitem);
                String input_amount = inputamount.getText().toString();
                Log.i(TAG, input_amount);
                if(input_amount.isEmpty()){
                    Toast.makeText(getActivity(),"คุณจำเป็นต้องกรอกจำนวนที่ต้องการจะซื้อ", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    int itemamount = Integer.valueOf(input_amount);

                    if(itemamount > amount){
                        Toast.makeText(getActivity(),"จำนวนสินค้าที่ระบุไม่ถูกต้อง!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    addItemCart(itemamount);
                }
            }
        });

        ImageButton buyitem = view.findViewById(R.id.buybutton);
        buyitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                EditText inputamount = view.findViewById(R.id.inputamountitem);
                String input_amount = inputamount.getText().toString();
                if(input_amount.isEmpty()){
                    Toast.makeText(getActivity(),"คุณจำเป็นต้องกรอกจำนวนที่ต้องการจะซื้อ", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    int amount = Integer.valueOf(input_amount);
                }
            }
        });
    }

    public void addItemCart(int amount){
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading..");
        pDialog.show();

        String url = "http://192.168.0.105:4990/api/users/additemcart";
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

                        if(type.matches("item_exist")){
                            Toast.makeText(getActivity(),"รายการสินค้านี้อยู้ในตะกร้าของคุณอยู่แล้ว!", Toast.LENGTH_SHORT).show();
                        } else if(type.matches("success")){
                            pDialog.setMessage("สินค้าได้ถูกเพิ่มไปยังตะกร้าสินค้าของคุณเรียบร้อยแล้ว!");
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
                params.put("itemownerid", userID);
                params.put("itemname", name);
                params.put("itemamount", String.valueOf(amount));
                double totalprice = amount * price;
                params.put("itemtotalprice", String.valueOf(totalprice));
                params.put("itemimg", img);
                Log.i(TAG, "Object: " + params);
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

                        String img = null;
                        String name = null;
                        String category = null;
                        double price = 0;
                        int amount = 0;
                        int selled = 0;
                        String detail = null;
                        int popular = 0;

                        try {
                            JSONObject jsonobject = new JSONObject(response);
                            name = jsonobject.getString("itemname");
                            img = jsonobject.getString("itemimg");
                            category = jsonobject.getString("itemcategory");
                            price = jsonobject.getDouble("itemprice");
                            amount = jsonobject.getInt("itemamount");
                            selled = jsonobject.getInt("itemselled");
                            detail = jsonobject.getString("itemdetail");
                            popular = jsonobject.getInt("itempopular");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(response.length() > 0){
                            SetItemData(name, img, category, price, amount, selled, detail, popular);
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

    public void SetItemData(String name, String img, String category, double price, int amount, int selled, String detail, int popular){

        this.name = name;
        this.img = img;
        this.category = category;
        this.price = price;
        this.amount = amount;
        this.selled = selled;
        this.detail = detail;
        this.popular = popular;

        String baseURL = "http://154.202.2.5/foodpetshop/img/";
        ImageView itemimg = view.findViewById(R.id.itemInfoImage2);
        String imgUrl = baseURL + img;
        Picasso.get()
                .load(imgUrl)
                .memoryPolicy(MemoryPolicy.NO_CACHE )
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .placeholder(R.mipmap.ic_launcher).fit()
                .error(R.mipmap.ic_launcher)
                .into(itemimg);

        TextView itemname = view.findViewById(R.id.itemName);
        itemname.setText(name);

        TextView itemcategory = view.findViewById(R.id.itemCategory);
        itemcategory.setText("ประเภทอาหาร: " + category);

        TextView itemprice = view.findViewById(R.id.itemPrice);
        String formattedPrice = String.format("%,.2f", price);
        itemprice.setText("฿" + formattedPrice);

        TextView itemamount = view.findViewById(R.id.itemStorage);
        String formattedAmount = String.format("%,d", amount);
        itemamount.setText("จำนวนคงเหลือ: " + formattedAmount);

        TextView itemselled = view.findViewById(R.id.itemSelled);
        String formattedSelled = String.format("%,d", selled);
        itemselled.setText("ขายแล้ว: " + formattedSelled);

        TextView itemdetail = view.findViewById(R.id.itemDetail);
        itemdetail.setText(detail);

        ImageView star1 = view.findViewById(R.id.iteminfostar1);
        ImageView star2 = view.findViewById(R.id.iteminfostar2);
        ImageView star3 = view.findViewById(R.id.iteminfostar3);
        ImageView star4 = view.findViewById(R.id.iteminfostar4);
        ImageView star5 = view.findViewById(R.id.iteminfostar5);

        if(popular >= 1) star1.setVisibility(View.VISIBLE);
        if(popular >= 2) star2.setVisibility(View.VISIBLE);
        if(popular >= 3) star3.setVisibility(View.VISIBLE);
        if(popular >= 4) star4.setVisibility(View.VISIBLE);
        if(popular >= 5) star5.setVisibility(View.VISIBLE);
    }

    public void authToken(String url, String Token){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, response);

                        String type = null;
                        String userName = null;
                        try {
                            JSONObject jsonobject = new JSONObject(response);
                            type = jsonobject.getString("type");
                            userName = jsonobject.getString("username");
                            userID = jsonobject.getString("_id");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(type.matches("success") && userName != null){
                            EditText inputAmount = view.findViewById(R.id.inputamountitem);
                            inputAmount.setVisibility(View.VISIBLE);

                            ImageButton buyitem = view.findViewById(R.id.buybutton);
                            buyitem.setVisibility(View.VISIBLE);

                            ImageButton addcart = view.findViewById(R.id.buybutton2);
                            addcart.setVisibility(View.VISIBLE);
                        } else if(type.matches("exp")){
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