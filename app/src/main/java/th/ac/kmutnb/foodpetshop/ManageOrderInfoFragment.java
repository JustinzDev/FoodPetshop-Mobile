package th.ac.kmutnb.foodpetshop;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManageOrderInfoFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";

    private String mParam1;
    private String mParam2;
    private String mParam3;
    private String mParam4;
    private double mParam5;

    private View view;

    public static final String REQUEST_TAG = "myrequest";
    private static final String TAG = "my_app";
    ProgressDialog pDialog;
    private RequestQueue mQueue;

    private RecyclerView recyclerViewListitem;
    private ManageOrderInfoAdapter listitemAdapter;
    private ArrayList<MyOrderInfoModel> listitem = new ArrayList<>();

    public ManageOrderInfoFragment() {
        // Required empty public constructor
    }

    public static ManageOrderInfoFragment newInstance(String param1, String param2, String param3, String param4, double param5) {
        ManageOrderInfoFragment fragment = new ManageOrderInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        args.putDouble(ARG_PARAM5, param5);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
            mParam4 = getArguments().getString(ARG_PARAM4);
            mParam5 = getArguments().getDouble(ARG_PARAM5);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_manage_order_info, container, false);
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
        ImageButton backbutton = view.findViewById(R.id.imagebuttonbackcart);
        backbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.navHostFragment, ManageOrderFragment.newInstance(null, null));
                transaction.commit();
            }
        });

        Button button1 = view.findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ManageStateThisOrder("http://192.168.0.105:4990/api/items/managestatethisorder");
            }
        });

        Button button2 = view.findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(mParam4.matches("wait")) {
                    deleteThisOrder("http://192.168.0.105:4990/api/items/deletethisorder");
                }
            }
        });

        if(mParam4.matches("finish")){
            button1.setVisibility(View.INVISIBLE);
            button2.setVisibility(View.INVISIBLE);
        }

        TextView title = view.findViewById(R.id.orderID);
        title.setText("orderID: " + mParam1);

        TextView payment = view.findViewById(R.id.orderPayment);
        if(mParam3.matches("transfer")) payment.setText("ช่องทางการชำระเงิน: โอนบัญชีธนาคาร");
        else if(mParam3.matches("keep_destination")) payment.setText("ช่องทางการชำระเงิน: เก็บปลายทาง");

        TextView state = view.findViewById(R.id.OrderState);
        if(mParam4.matches("wait")) state.setText("สถานะ: รอดำเนินการ");
        else if(mParam4.matches("process")) state.setText("สถานะ: กำลังจัดส่ง");
        else if(mParam4.matches("finish")) state.setText("สถานะ: จัดส่งสำเร็จ");

        TextView totalprice = view.findViewById(R.id.orderTotalPrice);
        String priceformatter = String.format("%,.2f", mParam5);
        totalprice.setText("รวม: " + priceformatter + " บาท");

        getOwnerOrder("http://192.168.0.105:4990/api/users/getusername/" + mParam2);
        getMyOrderList("http://192.168.0.105:4990/api/items/manageorderlist/" + mParam1);
    }

    public void ManageStateThisOrder(String url){
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
                            TextView state = view.findViewById(R.id.OrderState);
                            Log.i(TAG, mParam4);
                            if(mParam4.matches("wait")) {
                                state.setText("สถานะ: กำลังจัดส่ง");
                                mParam4 = "process";
                            }
                            else if(mParam4.matches("process")) {
                                state.setText("สถานะ: จัดส่งสำเร็จ");
                                mParam4 = "finish";
                            }
                            new CountDownTimer(3000, 1000) {
                                public void onTick(long millisUntilFinished) { }
                                public void onFinish() {
                                    pDialog.hide();
                                    FragmentManager manager = getFragmentManager();
                                    FragmentTransaction transaction = manager.beginTransaction();
                                    transaction.replace(R.id.navHostFragment, ManageOrderInfoFragment.newInstance(mParam1, mParam2, mParam3, mParam4, mParam5));
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
                params.put("itemkey", mParam1);
                params.put("itemownerid", mParam2);
                return params;
            }
        };

        mQueue = Volley.newRequestQueue(getActivity());
        mQueue.add(stringRequest);
    }

    public void deleteThisOrder(String url){
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
                                    transaction.replace(R.id.navHostFragment, ManageOrderFragment.newInstance(null, null));
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
                params.put("itemkey", mParam1);
                params.put("itemownerid", mParam2);
                return params;
            }
        };

        mQueue = Volley.newRequestQueue(getActivity());
        mQueue.add(stringRequest);
    }

    public void getOwnerOrder(String url){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, response);

                        String username = null;

                        try {
                            JSONObject jsonobject = new JSONObject(response);
                            username = jsonobject.getString("username");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(username != null){
                            TextView usernameText = view.findViewById(R.id.orderUserName);
                            usernameText.setText("สั่งซื้อโดย: " + username);
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
                });
//        {
//            @Override
//            protected Map<String, String> getParams(){
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("_id", mParam1);
//                return params;
//            }
//        };

        mQueue = Volley.newRequestQueue(getActivity());
        mQueue.add(stringRequest);
    }

    public void getMyOrderList(String url){
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading..");
        pDialog.show();

        listitem.clear();

        JsonArrayRequest jsRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Gson gson = new Gson();
                        Log.i(TAG, response.toString());
                        JSONObject jsObj;   // = null;
                        for (int i=0; i < response.length(); i++ ) {
                            try {
                                jsObj = response.getJSONObject(i);
                                MyOrderInfoModel dataitem = gson.fromJson(String.valueOf(jsObj), MyOrderInfoModel.class);
                                listitem.add(dataitem);
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
        recyclerViewListitem = (RecyclerView) getView().findViewById(R.id.rv_manageorderinfolist);
        listitemAdapter = new ManageOrderInfoAdapter(listitem);
        recyclerViewListitem.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false));
        recyclerViewListitem.setAdapter(listitemAdapter);
    }
}