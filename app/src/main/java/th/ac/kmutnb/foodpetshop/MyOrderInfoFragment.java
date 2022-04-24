package th.ac.kmutnb.foodpetshop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
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
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyOrderInfoFragment extends Fragment {
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
    private MyOrderInfoAdapter listitemAdapter;
    private ArrayList<MyOrderInfoModel> listitem = new ArrayList<>();

    public MyOrderInfoFragment() {
        // Required empty public constructor
    }

    public static MyOrderInfoFragment newInstance(String param1, String param2, String param3, String param4, double param5) {
        MyOrderInfoFragment fragment = new MyOrderInfoFragment();
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
        view = inflater.inflate(R.layout.fragment_my_order_info, container, false);
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
                transaction.replace(R.id.navHostFragment, MyOrdersFragment.newInstance(null, null));
                transaction.commit();
            }
        });

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

        getMyOrderList("http://154.202.2.5:4990/api/users/myorderlist/" + mParam2 + "/" + mParam1);
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
        recyclerViewListitem = (RecyclerView) getView().findViewById(R.id.rv_myorderinfolist);
        listitemAdapter = new MyOrderInfoAdapter(listitem);
        recyclerViewListitem.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false));
        recyclerViewListitem.setAdapter(listitemAdapter);
    }
}