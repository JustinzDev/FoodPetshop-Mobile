package th.ac.kmutnb.foodpetshop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
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

public class UserCartFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private View view;

    public static final String REQUEST_TAG = "myrequest";
    private static final String TAG = "my_app";
    ProgressDialog pDialog;
    private RequestQueue mQueue;

    private RecyclerView recyclerViewListitem;
    private CartItemAdapter listitemAdapter;
    private ArrayList<CartListItemModel> listitem = new ArrayList<>();

    private double totalpriceitemall = 0;

    public UserCartFragment() {
        // Required empty public constructor
    }

    public static UserCartFragment newInstance(String param1, String param2) {
        UserCartFragment fragment = new UserCartFragment();
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

        view = inflater.inflate(R.layout.fragment_user_cart, container, false);
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();

        getItems("http://192.168.0.105:4990/api/users/cartitems/" + mParam1);

        ImageButton backbutton = view.findViewById(R.id.imagebuttonbackcart);
        backbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent itnHome = new Intent(getActivity(), MainActivity.class);
                startActivity(itnHome);
            }
        });
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
                        Log.i(TAG, response.toString());
                        JSONObject jsObj;   // = null;
                        for (int i=0; i < response.length(); i++ ) {
                            try {
                                jsObj = response.getJSONObject(i);
                                CartListItemModel dataitem = gson.fromJson(String.valueOf(jsObj), CartListItemModel.class);
                                listitem.add(dataitem);
                                totalpriceitemall += dataitem.getItemprice();
//                                Log.d(TAG,"cart "+ dataitem.getItemname());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if (listitem.size() > 0){
                            displayListview(totalpriceitemall);
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

    public void displayListview(double totalpriceitemall){

        TextView totalpricetxt = view.findViewById(R.id.totalPrice);
        String totalpriceallformatter = String.format("%,.2f", totalpriceitemall);
        totalpricetxt.setText("ยอดรวม: ฿" + totalpriceallformatter);

        recyclerViewListitem = (RecyclerView) getView().findViewById(R.id.rv_cartlistitem);
        listitemAdapter = new CartItemAdapter(listitem);
        recyclerViewListitem.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false));
        recyclerViewListitem.setAdapter(listitemAdapter);
    }
}