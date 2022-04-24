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
import android.widget.Button;
import android.widget.ImageButton;
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

public class AdminControlFragment extends Fragment {

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
    private StorageItemListAdapter listitemAdapter;

    private ArrayList<ListItemModel> listitem = new ArrayList<>();

    public AdminControlFragment() {
        // Required empty public constructor
    }

    public static AdminControlFragment newInstance(String param1, String param2) {
        AdminControlFragment fragment = new AdminControlFragment();
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
        view =  inflater.inflate(R.layout.fragment_admin_control, container, false);

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

        Button button3 = view.findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(), AddItemActivity.class);
                startActivity(intent);
            }
        });

        getItems("http://154.202.2.5:4990/api/items/getitems");
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
        recyclerViewListitem = (RecyclerView) getView().findViewById(R.id.rv_storagelistitem);
        listitemAdapter = new StorageItemListAdapter(listitem);
        recyclerViewListitem.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false));
        recyclerViewListitem.setAdapter(listitemAdapter);
    }
}